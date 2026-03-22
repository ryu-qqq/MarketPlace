# 셀릭 채널 통합 계획

> 작성일: 2026-03-21
> 상태: 계획 단계 (별도 Epic)

---

## 1. 배경

현재 구조:
```
우리(MarketPlace) → 셀릭(OMS) → 네이버/SSF/LF 등 외부몰
```

목표 구조:
```
우리(MarketPlace) → 네이버/SSF/LF 등 외부몰 (직접 연동)
```

무중단 이관을 위해 셀릭을 세일즈 채널로 등록하고, 기존 outbound(상품 동기화) + inbound(주문 수집) 파이프라인을 활용한다.

---

## 2. 셀릭을 세일즈 채널로 등록하는 이유

- 네이버, SSF처럼 동일한 파이프라인으로 처리 가능
- 무중단 이관 — 셀릭 살려두고 MarketPlace로 점진적 전환
- 나중에 셀릭 제거 시 채널 비활성화만 하면 끝
- 기존 인프라(outbound sync, inbound order) 재사용

---

## 3. 셀릭 연동 범위

| 기능 | 방향 | 설명 |
|------|------|------|
| 상품 등록/수정 | MarketPlace → 셀릭 | outbound sync adapter |
| 주문 풀링 | 셀릭 → MarketPlace | inbound order adapter (polling) |
| 운송장 등록 | MarketPlace → 셀릭 | outbound command adapter |

셀릭은 운송장 등록만 지원. 취소/반품 등 클레임 처리는 셀릭 UI에서 직접.

---

## 4. 주문 저장 전략

### 방안: 셀릭 채널로 저장 + 원본 채널 기록

```
셀릭에서 주문 풀링 → 저장 시:
  - salesChannelId = 셀릭 채널 ID
  - originChannelName = "NAVER" / "SSF" / "LF" (externalOrderPkId 패턴으로 파악)
  - externalOrderNo = 원본 외부몰 주문번호
```

### externalOrderPkId 패턴 (이미 LegacyOrderChannelResolver에 구현됨)

| 패턴 | 원본 채널 |
|------|----------|
| "OD" 시작 | SSF |
| 8자리 순수 숫자 | LF |
| 그 외 | NAVER (셀릭 경유 주문의 98%) |

### 셀릭 제거 시

1. `originChannelName` 기반으로 salesChannelId 일괄 UPDATE
2. 미처리 주문(배송 전)은 셀릭에서 마무리
3. 셀릭 채널 비활성화

---

## 5. 셀러 UX 전환 가이드

| 단계 | 셀릭 경유 주문 | 직접 연동 주문 |
|------|-------------|-------------|
| 전환 전 | 셀릭에서 처리 (운송장 등록 등) | - |
| 전환 중 | 셀릭에서 처리 | MarketPlace에서 처리 |
| 전환 후 | 셀릭 비활성화, 남은 주문 마무리 | MarketPlace에서 처리 |

셀러에게 안내: "당분간 셀릭 주문은 셀릭에서, 전환 완료 후 모든 주문을 MarketPlace에서 처리"

---

## 6. 필요한 작업

### Adapter 개발
1. `setof-sellic-client` 모듈 (또는 기존 setof-commerce-client 확장)
   - 상품 등록/수정 API
   - 주문 풀링 API
   - 운송장 등록 API

### 스키마
- `order_items` 또는 `orders` 테이블에 `origin_channel_name` 컬럼 추가 (nullable)
- 셀릭 세일즈 채널 데이터 INSERT

### 설정
- outbound sync: 셀릭 채널용 상품 동기화 전략 등록
- inbound order: 셀릭 주문 풀링 스케줄러 설정
- shipment: 셀릭 운송장 등록 전략 등록

---

## 7. 주의사항

### 주문 중복 방지
셀릭 경유 네이버 주문과 나중에 네이버 직접 연동 주문이 중복될 수 있음.
`externalOrderNo`로 중복 체크 필수.

### 셀릭 제거 타이밍
모든 셀러가 직접 연동으로 전환 완료 + 셀릭 미처리 주문 0건 확인 후 제거.

### LegacyOrderConversion과의 관계
현재 luxurydb → market 주문 컨버전은 별도 파이프라인.
셀릭 채널 통합은 이와 독립적으로 진행 가능.
컨버전 활성화(스케줄러 설정) 후 기존 주문 이관 → 셀릭 채널 통합은 신규 주문부터 적용.

---

## 8. 관련 코드

| 파일 | 역할 |
|------|------|
| `LegacyOrderChannelResolver` | 셀릭 경유 주문 원본 채널 파싱 (재사용 가능) |
| `LegacyOrderStatusMapper` | 레거시 상태 → 내부 도메인 상태 매핑 |
| `LegacyOrderConversionCoordinator` | luxurydb → market 주문 변환 |
| `LegacyOrderConversionScheduler` | 컨버전 스케줄러 (enabled: true 설정 필요) |

---

## 9. 레거시 주문 컨버전 활성화 절차

> 현재 상태 (2026-03-21): Stage/Prod 모두 주문 컨버전 **비활성화**
> - Stage: 테이블 있음, 아웃박스 0건, order_items 0건
> - Prod: `legacy_order_conversion_outboxes`, `legacy_order_id_mappings` 테이블 자체 없음

### 비활성화 원인

스케줄러가 `@ConditionalOnProperty(matchIfMissing = false)` — yml에 `enabled: true` 명시 필요.
현재 scheduler yml 어디에도 설정이 없어서 빈 등록 자체가 안 됨.

관련 스케줄러:
- `LegacyOrderConversionSeederScheduler` — luxurydb 주문 스캔 → 아웃박스 생성
- `LegacyOrderConversionScheduler` — 아웃박스 소비 → market 스키마에 저장

### Stage 활성화 절차

```yaml
# scheduler-stage.yml 에 추가
scheduler:
  jobs:
    legacy-order-conversion-seeder:
      enabled: true
      cron: "0 */5 * * * *"
    legacy-order-conversion:
      enabled: true
      cron: "0 */5 * * * *"
    legacy-order-conversion-timeout:
      enabled: true
      cron: "0 */10 * * * *"
```

1. 위 설정을 `scheduler-stage.yml`에 추가
2. 배포
3. Seeder가 luxurydb 24,015건 주문 스캔 → `legacy_order_conversion_outboxes`에 PENDING 생성
4. Conversion 스케줄러가 PENDING 소비 → `market.order_items`, `market.orders` 등에 저장
5. `legacy_order_id_mappings`에 legacyOrderId ↔ internalOrderId 매핑 생성

### Prod 활성화 절차

Stage와 동일 + 추가 작업:
1. **Flyway 마이그레이션 필요** — `legacy_order_conversion_outboxes`, `legacy_order_id_mappings` 테이블이 Prod에 없음
2. `scheduler.yml` (prod)에 동일 설정 추가
3. 배포

### 주의사항

- 상품 컨버전이 먼저 완료되어야 함 — 주문 컨버전 시 `legacy_product_id_mappings`로 상품 ID를 변환하는데, 매핑이 없으면 레거시 ID를 그대로 사용
- Seeder 첫 실행 시 전체 주문 스캔 → 부하 주의 (분산 락으로 다중 인스턴스 동시 실행 방지됨)
- ORDER_FAILED 상태 주문은 이관 제외 (`LegacyOrderStatusMapper.isEligibleForMigration`)
