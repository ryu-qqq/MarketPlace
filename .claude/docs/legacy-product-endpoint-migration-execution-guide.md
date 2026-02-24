# 레거시 상품 엔드포인트 전환 실행 가이드

> 작성일: 2026-02-24  
> 상태: Draft  
> 대상: `LegacyProductCommandController` 기반 상품 엔드포인트 11개

---

## 1. 목적

레거시 상품 엔드포인트를 중단 없이 내부 OMS DB로 완전 전환한다.

- 외부 OMS(사방넷/셀릭)가 사용하는 PK는 변경하지 않는다.
- 내부 처리 로직은 점진적으로 Legacy DB fallback에서 OMS 내부 모델로 이동한다.
- 전환 중에도 API 계약(요청/응답 shape)은 유지한다.

---

## 2. 핵심 원칙

1. **Strangler Fig 패턴 적용**
   - Legacy endpoint는 유지
   - 내부 처리 경로만 상태 기반으로 점진 전환

2. **외부 식별자 불변**
   - 외부 OMS가 받은 `productGroupId`/`productId`는 이후에도 동일하게 사용 가능해야 한다.

3. **내부 PK 비노출**
   - 외부 응답에서 internal PK를 직접 반환하지 않는다.

4. **매핑 허브 단일화**
   - `InboundProduct` + `InboundProductItem`을 외부/내부 식별자 매핑의 단일 기준으로 사용한다.

---

## 3. 현재 갭 요약

### 3.1 구현된 것

- `InboundProduct` 기반 productGroupId 매핑
- 레거시 상품 엔드포인트의 command 분리 구조

### 3.2 미구현/보강 필요

- `InboundProductItem`(SKU 레벨 매핑) 도메인/저장소/서비스
- `LEGACY_IMPORTED` 상태 운영 로직
- option/stock/out-stock/PUT update 경로의 external productId 변환/역변환
- 응답 PK 정책 강제(내부 PK 노출 방지)

---

## 4. 전환 아키텍처

```text
Legacy API
  -> LegacyProductCommandController
    -> Legacy Route Coordinator (strangler)
      -> InboundProduct 조회 (group 매핑)
        -> 상태 분기
           - LEGACY_IMPORTED: fallback 경로 (임시)
           - CONVERTED: OMS 내부 경로
      -> InboundProductItem으로 productId external<->internal 변환
      -> 내부 UseCase 실행
      -> 응답 productId/internal 값 역매핑 후 반환
```

---

## 5. 데이터 모델 확장

## 5.1 InboundProductItem (필수)

- 용도: SKU(productId) 레벨 external/internal 매핑
- 핵심 컬럼:
  - `inbound_product_id`
  - `external_product_id`
  - `internal_product_id`
  - `status` (`LEGACY_IMPORTED`, `CONVERTED`)

## 5.2 권장 제약/인덱스

- 인덱스:
  - `(inbound_product_id)`
  - `(external_product_id)`
  - `(status)`
- 유니크 제약(권장):
  - `(inbound_product_id, external_product_id)`
  - `(inbound_product_id, internal_product_id)` where internal not null

---

## 6. 엔드포인트별 전환 규칙

## 6.1 POST `/product/group`

- 등록 성공 시:
  - `InboundProduct` 생성/갱신
  - 생성된 내부 products에 대해 `InboundProductItem` 생성
- 신규 등록 건은 `external_product_id = internal_product_id`로 초기화 가능

## 6.2 PUT `/product/group/{id}` (full update)

- payload 내 `productOptions[].productId`를 external->internal 변환 후 내부 업데이트
- update 결과 응답은 internal->external 역변환하여 반환

## 6.3 PUT `/product/group/{id}/option`

- 요청 option 목록:
  - `productId` external->internal 변환 필수
  - option group/value는 이름 기반 해석 + ID 힌트(있을 경우)로 처리
- 응답 productId는 역매핑하여 external 값 반환

## 6.4 PATCH `/product/group/{id}/stock`

- 요청 `productId` external->internal 변환 후 stock 업데이트
- 응답 productId 역매핑

## 6.5 PATCH `/product/group/{id}/out-stock`

- group 상태 변경은 내부 경로로 수행
- 응답 상품 목록의 productId는 역매핑하여 external 반환

## 6.6 notice/images/description/price/display-yn

- productGroupId 매핑만 정확하면 내부 경로 전환 가능
- 응답에 SKU 목록이 포함될 경우 동일한 역매핑 정책 준수

---

## 7. 상태 기반 라우팅 정책

## 7.1 기준 상태

- `LEGACY_IMPORTED`
- `CONVERTED`

## 7.2 정책

- `CONVERTED`:
  - OMS 내부 경로 강제
  - 매핑 실패 시 명시적 오류 반환(조용한 fallback 금지)

- `LEGACY_IMPORTED`:
  - 임시 fallback 허용
  - 전환률 지표 모니터링 후 단계적 제거

---

## 8. 변환/해석 규칙

## 8.1 Product ID

- 입력: external productId
- 처리 전: `InboundProductItem`으로 internal productId 해석
- 처리 후 응답: internal->external 역해석

## 8.2 Option Group / Option Value

- 기본 판단 기준: 이름(`optionGroupName`, `optionValueName`)
- 보조 힌트: 외부 optionGroupId/optionDetailId (존재 시)
- 충돌 시 정책:
  - 자동 병합 금지
  - 명시적 예외 + 에러코드 반환

---

## 9. 실패 정책

1. external productId 미매핑
   - `CONVERTED` 상태: 4xx(도메인 오류) 반환

2. 부분 매핑 성공(일부 SKU만 해석)
   - 전체 실패(트랜잭션 롤백)

3. 응답 역매핑 실패
   - 내부 PK 노출 대신 명시적 서버 오류 처리

4. 조용한 fallback 금지
   - 상태가 `CONVERTED`인데 fallback 경로를 타지 않도록 방어

---

## 10. 롤아웃 계획 (권장 순서)

1. `InboundProductItem` 도입 + 벌크 적재
2. `option` 경로 전환
3. `stock` 경로 전환
4. `out-stock` 경로 전환
5. `PUT /{id}` full update 전환
6. 응답 PK 검증 자동화
7. `LEGACY_IMPORTED` 트래픽 0 확인 후 fallback 제거

---

## 11. 관측/검증 항목

- endpoint별 매핑 성공률
- unmapped external productId 발생 건수
- `LEGACY_IMPORTED` fallback 호출 비율
- 내부 PK 노출 건수(목표: 0)
- option/stock 업데이트 정합성 샘플링 결과

---

## 12. 완료(Exit) 기준

아래를 모두 만족하면 레거시 상품 엔드포인트 전환 완료로 간주한다.

- `option/stock/out-stock/PUT update`가 OMS 내부 경로에서 동작
- 외부 응답 PK가 항상 external 기준으로 유지
- `InboundProductItem` 기반 양방향 매핑이 운영 데이터에서 안정화
- `LEGACY_IMPORTED` fallback 트래픽 0 달성
- 회귀 테스트 및 운영 지표 이상 없음

---

## 13. 관련 문서

- `.claude/docs/product-integration-migration-plan.md`
- `.claude/docs/legacy-endpoint-traffic-analysis.md`
- `.claude/docs/gateway-routing-migration-spec.md`
