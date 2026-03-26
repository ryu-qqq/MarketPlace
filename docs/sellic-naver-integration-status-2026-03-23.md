# 셀릭/네이버 채널 통합 현황 (2026-03-23)

> 작성일: 2026-03-23
> 상태: Step 1 진행 중 (Stage 배포 + 검증)

---

## 1. 전체 구조

### AS-IS (현재 운영)
```
외부몰 주문 (네이버 99%, SSF, LF)
    → 셀릭 OMS가 수집 → luxurydb에 저장
    → 레거시 어드민이 luxurydb 바라봄

자사몰 주문 (세토프)
    → 세토프 웹훅 → MarketPlace → market 스키마
```

### TO-BE (목표)
```
외부몰 주문
    → MarketPlace가 직접 폴링 (네이버/셀릭) → market 스키마
    → OMS에서 직접 처리 (발주확인, 송장등록, 취소, 반품, 교환)

자사몰 주문
    → 세토프 웹훅 → MarketPlace → market 스키마
    → 레거시 어드민은 setof 스키마만 바라봄 (자사몰 주문만)

셀릭 → 채널 비활성화 후 제거
```

---

## 2. 셀릭/네이버 동시 폴링 전략

### 왜 동시에 폴링하는가

셀릭은 네이버/SSF/LF 등의 주문을 중계하는 OMS다. 현재:
- 셀릭이 네이버 주문을 수집 → luxurydb에 저장
- 우리가 셀릭 API로 폴링 → InboundOrder에 저장
- 나중에 네이버 직접 연동으로 전환하면 셀릭 제거

동시 폴링 기간이 필요한 이유:
1. **정합성 검증** — 셀릭 경유 주문과 네이버 직접 주문의 데이터가 일치하는지 비교
2. **무중단 전환** — 셀릭을 끄기 전에 네이버 직접 연동이 정상 동작하는지 확인

### 폴링 스케줄

| 채널 | salesChannelId | 폴링 주기 | 방식 |
|------|---------------|----------|------|
| 세토프 (자사몰) | 1 | 폴링 없음 | 웹훅 수신 |
| 네이버 | 2 | 1분마다 (검증 중, 이후 1시간) | 2-Phase 폴링 (last-changed → 상세조회) |
| 셀릭 | 16 | 하루 5회 (10,13,15,17,19시) | 단일 API 호출 (/get_order) |

### 중복 방지 현황

**현재 중복 체크**: `salesChannelId + externalOrderNo` 기준
- 같은 채널 내 중복: 방지됨
- **크로스채널 중복: 방지 안 됨** ← 위험

**중복 발생 시나리오**:
```
셀릭 폴링 (salesChannelId=16):
  externalOrderNo = "2026031994396341" (쇼핑몰 주문번호)

네이버 폴링 (salesChannelId=2):
  externalOrderNo = "2026031994396341" (동일한 네이버 orderId)

→ salesChannelId가 다르므로 같은 주문이 2건 생성됨
```

**현재 상태에서의 안전성**:
- InboundOrder는 `PENDING_MAPPING`에서 멈춤 (Order 변환 안 됨)
- 정합성 검증 단계이므로 중복 생성 자체는 문제 없음
- Order 변환 활성화 전에 반드시 해결 필요

**계획된 해결 방법**:
1. `origin_channel_name` 컬럼 추가 (orders 또는 inbound_orders)
2. 셀릭 주문 저장 시 `externalOrderNo` 패턴으로 원본 채널 파악 (네이버/SSF/LF)
3. 네이버 폴링 시 이미 셀릭으로 수집된 동일 `externalOrderNo` 존재하면 skip
4. 또는 셀릭 폴링 비활성화 후 네이버만 사용 (가장 깔끔)

---

## 3. 레거시 주문 컨버전과의 관계

### 3가지 주문 파이프라인

```
경로 A: 셀릭 API 폴링 → InboundOrder (salesChannelId=16) → PENDING_MAPPING
경로 B: 네이버 API 폴링 → InboundOrder (salesChannelId=2) → PENDING_MAPPING
경로 C: luxurydb → LegacyOrderConversion → market.orders (salesChannelId=2/14/13 등)
```

**경로 C의 externalOrderNo 패턴 차이**:
- 레거시 컨버전: `"2026032037614391_2026032048573881"` (orderId_productOrderId)
- 네이버 폴링: `"2026032037614391"` (orderId만)
- 값이 다르므로 중복 체크에 걸리지 않음 → 같은 주문이 2건 생성 가능

### 현재 Stage 설정

| 파이프라인 | 상태 |
|-----------|------|
| 셀릭 폴링 | **ON** — 111건 InboundOrder 저장 완료 |
| 네이버 폴링 | **ON** — 1분마다 (스레드풀 증설 필요) |
| 레거시 컨버전 | **OFF** — 버그 수정 후 재활성화 예정 |

### 레거시 컨버전 버그

| 원인 | 건수 | 설명 |
|------|------|------|
| 중복 키 | 12,946 | Seeder cursor=0 리셋 → 이미 있는 orderId 재insert |
| 주문 조회 실패 | 293 | luxurydb에서 해당 주문 복합 정보 못 찾음 |
| seller_id 컬럼 없음 | 10 | V34에서 DROP된 컬럼 참조 |

---

## 4. 구현 완료 사항

### sellic-commerce-client 모듈
```
adapter-out/client/sellic-commerce-client/
  adapter/   → ProductClientAdapter, OrderClientAdapter
  client/    → SellicCommerceApiClient (5개 API)
  config/    → Properties, ClientConfig, CBConfig
  dto/       → 등록/수정/재고/주문조회/송장 요청·응답
  exception/ → 6개 예외 클래스
  mapper/    → ProductMapper, OrderMapper
  strategy/  → ShipmentSyncStrategy, CourierCodeResolver
  support/   → ApiExecutor (CB + Retry)
```

### 셀릭 API 엔드포인트

| 기능 | API | 검증 |
|------|-----|------|
| 상품 등록 | POST /openapi/set_product | ✅ PASS (productId=1697600) |
| 상품 수정 | POST /openapi/edit_product | ✅ PASS |
| 상품 삭제 | POST /openapi/edit_product (sale_status=2004) | ✅ PASS |
| 주문 조회 | POST /openapi/get_order | ✅ PASS (25건 → 20건 변환) |
| 송장 등록 | POST /openapi/set_ship | 코드 완료, 미검증 |
| 재고 수정 | POST /openapi/edit_stock | 코드 완료, 미검증 |

### 전략 패턴 도입

```java
// 주문 폴링
SalesChannelOrderClientManager
  → channelCode 기반 Map<String, SalesChannelOrderClient> O(1) 조회
  → NAVER → NaverCommerceOrderClientAdapter
  → SELLIC → SellicCommerceOrderClientAdapter
  → SETOF → SetofCommerceOrderClientAdapter

// 배송 동기화
ShipmentSyncStrategyProvider
  → channelCode 기반 Map<String, ShipmentSyncStrategy> O(1) 조회
  → NAVER → NaverShipmentSyncStrategy
  → SELLIC → SellicShipmentSyncStrategy
  → SETOF → SetofShipmentSyncStrategy
```

### Stage DB 데이터

| 테이블 | 데이터 |
|--------|--------|
| sales_channel | id=16 SELLIC, id=2 NAVER (수동 변경) |
| shop | id=38 셀릭 공용 (vendor_id=1012, api_key) |
| seller_sales_channels | 39개 셀러 × SELLIC |
| sales_channel_category | 433개 셀릭 카테고리 |
| category_mapping | 345개 내부↔셀릭 매핑 |
| inbound_orders | 셀릭 111건 PENDING_MAPPING |

---

## 5. 네이버 OMS 시나리오 테스트

### 테스트 상품
- 상품명: `[테스트] OMS 시나리오 테스트 상품 - 주문하지마세요`
- 가격: 100원
- smartstoreChannelProductNo: **13287809899**
- URL: `https://brand.naver.com/disney/products/13287809899`

### 주문 5건 (사용자 직접 주문 완료)
- 4건 카드 + 1건 네이버 포인트
- productOrderId: InboundOrder 폴링 후 확인 필요

### 배송완료 운송장 (luxurydb에서 조회)

| 택배사 | 레거시코드 | 네이버코드 | 운송장번호 |
|--------|-----------|-----------|----------|
| CJ대한통운 | SHIP04 | CJGLS | 505836445860 |
| CJ대한통운 | SHIP04 | CJGLS | 589436015912 |
| 롯데택배(구현대) | SHIP146 | HYUNDAI | 316382636301 |
| 롯데택배 | SHIP08 | LOTTEGLOGIS | 410659765305 |
| CJ대한통운 | SHIP04 | CJGLS | 505836440256 |

### OMS 시나리오

| 주문 | 시나리오 | 네이버 API |
|------|---------|-----------|
| 1번 | 정상 배송 | 발주확인 → 발송처리 (송장등록) |
| 2번 | 즉시 취소 | 취소 요청 |
| 3번 | 발주 후 취소 | 발주확인 → 취소 요청 |
| 4번 | 반품 | 발송처리(완료된 운송장) → 반품 승인 |
| 5번 | 교환 | 발송처리(완료된 운송장) → 교환 수거 완료 → 교환 재배송 |

### 테스트 코드 위치

| 파일 | 역할 |
|------|------|
| `NaverOmsScenarioTest.java` | 상품등록 + 폴링 + 발주/송장/취소 시나리오 |
| `NaverOrderPollingExternalTest.java` | 네이버 실제 주문 폴링 + Mapper 변환 |
| `SellicRealProductIntegrationTest.java` | 셀릭 상품 등록/수정/삭제 |
| `SellicOrderPollingExternalTest.java` | 셀릭 주문 25건 폴링 + 변환 |

---

## 6. 인프라 현황

### ECS 스펙

| 서비스 | CPU | Memory | 상태 |
|--------|-----|--------|------|
| web-api | 1024 | 2048 | 정상 |
| scheduler | 1024 | 2048 | 정상 (증설 완료) |
| worker | 512 | 1024 | 정상 |
| legacy-api | 512 | 1024 | 기동 중 (JWT Secret + Shipment fallback 추가) |

### SSM 파라미터

| 이름 | 용도 |
|------|------|
| /marketplace/sellic/api-key | 셀릭 API 키 |
| /marketplace/legacy/jwt-secret | 레거시 JWT 서명 시크릿 |
| /naver-commerce/stage/client-id | 네이버 client ID |
| /naver-commerce/stage/client-secret | 네이버 client secret |

### 알려진 인프라 이슈

1. **스케줄러 스레드 풀**: 기본 1 → 4로 증설 필요 (yml 수정했지만 커밋 전)
2. **legacy-api**: `LegacyShipmentCommandPort` 구현체 없음 → fallback 빈으로 처리
3. **Flyway V31/V32**: Stage DB에 수동 적용 완료 (settlement 테이블)

---

## 7. 무중단 전환 로드맵

### Step 1: Stage 배포 + 검증 (현재)
- [x] 셀릭 폴링 ON → InboundOrder 저장 검증
- [ ] 네이버 폴링 → InboundOrder 저장 검증 (스레드풀 증설 후)
- [ ] 레거시 컨버전 버그 수정 후 재활성화
- [ ] externalOrderNo 기준 셀릭 InboundOrder vs 레거시 Order 정합성 비교

### Step 2: 셀릭 폴링 → Order 변환 활성화
- [ ] origin_channel_name 컬럼 추가
- [ ] 크로스채널 중복 방지 로직 구현
- [ ] InboundOrder → Order 변환 활성화
- [ ] LegacyOrderConversion OFF

### Step 3: rest-api-legacy 주문 엔드포인트 전환
- [ ] 레거시 어드민 주문 조회 → market 스키마
- [ ] luxurydb 주문 조회 → market 주문 조회 결과 일치 검증

### Step 4: Prod 배포
- [ ] Flyway 마이그레이션 (V31~V36)
- [ ] 셀릭/네이버 폴링 활성화
- [ ] 네이버 OMS 시나리오 검증

### Step 5: 셀릭 제거 + 네이버 직접 연동
- [ ] 셀릭 경유 주문 0건 확인
- [ ] 네이버 폴링만 유지
- [ ] 셀릭 채널 비활성화

### Step 6: 정리
- [ ] luxurydb 주문 테이블 의존 완전 제거
- [ ] LegacyOrderConversion 코드 제거
- [ ] 레거시 어드민 → setof 스키마 (자사몰 주문만)
