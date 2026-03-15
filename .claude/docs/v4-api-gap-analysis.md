# V4 주문 API 간극 분석

## 배경

레거시 어드민 API 스펙을 그대로 유지하라는 프론트 요청으로 V4 API 응답 구조를 레거시와 동일하게 맞춤.
그러나 OMS 도메인 특성상 제공 불가능한 필드가 다수 존재하여, OrderQueryApiMapper에서 기본값(0, "")으로 채우고 있음.

---

## 1. 제거 확정 — OMS에서 제공 불가능

| 위치 | 필드 | 현재 기본값 | 사유 |
|------|------|------------|------|
| `PaymentDetailApiResponse` | `userId` | `0L` | 사용자/회원 시스템이 아님. 결제 PG 연동에서도 userId를 받지 않음 |
| `PaymentDetailApiResponse` | `usedMileageAmount` | `0` | 마일리지 시스템 미보유. OMS 관할 밖 |
| `OrderProductApiResponse` | `totalExpectedRefundMileageAmount` | `0` | 마일리지 시스템 미보유. `usedMileageAmount`와 동일 맥락. 처리 불가 영역 |

**조치**: 필드 제거 또는 응답에서 null 처리. 프론트에 "OMS 스펙상 제공 불가" 공지.

---

## 2. 제거 확정 — 주문 목록/상세에서 분리

| 위치 | 필드 | 현재 기본값 | 사유 |
|------|------|------------|------|
| `OrderDetailApiResponseV4` | `settlementInfo` 전체 | 0/"" | 정산 정보는 정산 전용 메뉴에서 확인해야 함. 주문 상세에 불필요 |

**조치**: `settlementInfo` nullable 스펙이므로 null로 전송. 정산 전용 API/메뉴로 이동.

SettlementInfo 하위 필드:
- `commissionRate`, `fee`, `expectationSettlementAmount`, `settlementAmount`, `shareRatio`
- `expectedSettlementDay`, `settlementDay`

---

## 3. 조치 완료 — paymentAmount로 대체

| 위치 | 필드 | 변경 전 | 변경 후 | 사유 |
|------|------|--------|--------|------|
| `PaymentDetailApiResponse` | `billAmount` | `0` | `paymentAmount` 값 사용 | OMS에서 청구 금액과 실결제 금액의 구분이 없음. 동일 값으로 채움 |

**조치 완료**: `OrderQueryApiMapper.toPaymentDetailV4()`에서 `billAmount`를 `payment.paymentAmount()`로 매핑.

---

## 4. 유지 가능 — 향후 채울 수 있는 필드

| 위치 | 필드 | 현재 기본값 | 비고 |
|------|------|------------|------|
| `OrderProductApiResponse` | `deliveryArea` | `""` | 배송 지역 정보. 향후 배송 도메인 확장 시 채울 수 있음 |
| `ExternalOrderInfoApiResponse` | `shopOrderStatus` | `""` | 외부몰 주문상태. ShipmentOutbox 연동 완성 시 외부 채널에서 받아올 수 있음 |
| `CancelSummaryV4/LatestCancel` | `type` | `""` | 취소 유형 (전액/부분 등). 도메인에 cancelType 추가 시 채울 수 있음 |
| `CancelItemApiResponse` | `type` | `""` | 위와 동일 |
| `OrderHistoryItemApiResponse` | `changeDetailReason` | `""` | 상태 변경 상세 사유. 도메인에 detailReason 추가 시 채울 수 있음 |

---

## 5. 구조적 차이 — 유지하되 인지 필요

| 위치 | 필드 | 현재 처리 | 비고 |
|------|------|----------|------|
| `PriceApiResponse` | `regularPrice`, `currentPrice`, `salePrice` | 모두 `unitPrice`로 동일값 | OMS는 주문 시점 단가(`unitPrice`)만 보유. 정가/현재가/판매가 구분 없음 |
| `PriceApiResponse` | `directDiscountPrice`, `directDiscountRate`, `discountRate` | `discountAmount` 기반 계산 | 할인 유형 구분 없이 단일 할인액만 존재 |
| `OrderProductApiResponse` | `options` | `List.of()` (빈 리스트) | 레거시는 `[{optionName:"색상", optionValue:"블랙"}]` 구조화된 옵션 제공. OMS는 옵션을 name/value 쌍으로 분리 저장하지 않음. `option` 문자열 필드(`externalOptionName`, 예: "블랙/260")로 옵션 정보 전달 중이나, 구조화된 `options` 리스트는 채울 수 없음 |
| `OrderProductApiResponse` | `productGroupMainImageUrl` vs `mainImageUrl` | `mainImageUrl` 매핑 | 필드명 불일치지만 데이터는 정상 제공 |

---

## 요약 — 액션 아이템

### 즉시 조치 (프론트 협의 후)
1. `userId` → 제거 또는 null (OMS 제공 불가)
2. `usedMileageAmount` → 제거 또는 null (OMS 제공 불가)
3. `totalExpectedRefundMileageAmount` → 0 유지 (OMS 제공 불가, 문서 명시)
4. `settlementInfo` → null 전송 (정산 메뉴로 이동)

### 조치 완료
5. ~~`billAmount`~~ → `paymentAmount` 값으로 대체 (코드 수정 완료)

### 향후 개선 (도메인 확장 시)
6. `cancelType` 도메인 필드 추가 → `type` 채움
7. `shopOrderStatus` 외부 채널 연동 시 채움
8. `deliveryArea` 배송 도메인 확장 시 채움
9. `changeDetailReason` 히스토리 상세 사유 추가 시 채움

### 유지 (현행 유지)
10. Price 3중 복제 (regularPrice=currentPrice=salePrice=unitPrice) — 레거시 호환
11. `options` 빈 리스트 — OMS는 옵션을 name/value로 분리 저장하지 않아 구조화된 데이터 제공 불가. `option`(문자열) + `skuNumber`로 식별 가능
