# 네이버 커머스 API - 상품 주문 정보 구조체

> **출처**: https://apicenter.commerce.naver.com/docs/commerce-api/current/schemas/상품-주문-정보-구조체
> **API 버전**: 2.73.0
> **작성일**: 2026-03-09
> **목적**: NaverProductOrderDetail DTO 확장 및 VO 바인딩 개발 시 참조용

---

## 구조 개요

```
data: ProductOrdersInfo[] (Array)
├── order: OrderResponseContent (주문 정보)
├── productOrder: ProductOrderResponseContent (상품 주문 정보)
│   ├── shippingAddress (배송지 주소)
│   ├── takingAddress (판매자 출고지 주소)
│   ├── skuMappings[] (물류 재고 정보)
│   ├── hopeDelivery (배송 희망 정보)
│   ├── appliedCoupons[] (쿠폰 사용 정보)
│   ├── appliedCardPromotion (카드 프로모션 정보)
│   ├── cancel (취소) ⚠️ 2025년 제거 예정
│   ├── return (반품) ⚠️ 2025년 제거 예정
│   ├── exchange (교환) ⚠️ 2025년 제거 예정
│   ├── beforeClaim (클레임 요청 전 정보)
│   ├── currentClaim (현재 진행 중 클레임)
│   └── completedClaims[] (완료된 클레임 목록)
└── delivery: DeliveryResponseContent (배송 정보)
```

---

## 1. order (주문 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `chargeAmountPaymentAmount` | integer | 충전금 최종 결제 금액 |
| `checkoutAccumulationPaymentAmount` | integer | 네이버페이 적립금 최종 결제 금액 |
| `generalPaymentAmount` | integer | 일반 결제 수단 최종 결제 금액 |
| `naverMileagePaymentAmount` | integer | 네이버페이 포인트 최종 결제 금액 |
| `orderDate` | datetime | 주문 일시 |
| `orderDiscountAmount` | integer | 주문 할인액 |
| `orderId` | string | 주문 번호 (20바이트) | 
| `ordererId` | string | 주문자 ID (20바이트) |
| `ordererName` | string | 주문자 이름 (300바이트, 선물 주문은 마스킹) |
| `ordererTel` | string | 주문자 연락처 (선물 주문은 마스킹) |
| `paymentDate` | datetime | 결제 일시 (최종 결제) |
| `paymentDueDate` | datetime | 결제 기한 |
| `paymentMeans` | string | 결제 수단 (신용카드/간편결제/휴대폰/계좌/무통장입금/포인트·머니/패밀리/후불) |
| `isDeliveryMemoParticularInput` | string | 배송 메모 개별 입력 여부 |
| `payLocationType` | string | 결제 위치 구분 (PC/MOBILE) |
| `ordererNo` | string | 주문자 번호 (20바이트) |
| `payLaterPaymentAmount` | integer | 후불 결제 최종 결제 금액 |
| `isMembershipSubscribed` | boolean | 주문시점 멤버십 여부 |

### 현재 NaverProductOrderOrder.java에 있는 필드
- orderId, orderDate, paymentDate, ordererName, ordererTel, ordererId, ordererNo, payLocationType, paymentMeans

### 추가 필요 필드
- `chargeAmountPaymentAmount`, `checkoutAccumulationPaymentAmount`, `generalPaymentAmount`
- `naverMileagePaymentAmount`, `orderDiscountAmount`, `paymentDueDate`
- `isDeliveryMemoParticularInput`, `payLaterPaymentAmount`, `isMembershipSubscribed`

---

## 2. productOrder (상품 주문 정보)

### 2.1 상태/분류 필드

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `productOrderId` | string | 상품 주문 번호 (20바이트) |
| `productOrderStatus` | enum | 상품 주문 상태 |
| `claimStatus` | enum | 클레임 상태 |
| `claimType` | enum | 클레임 구분 |
| `claimId` | string | 클레임 번호 (20바이트) |
| `placeOrderDate` | datetime | 발주 확인일 |
| `placeOrderStatus` | enum | 발주 상태 (NOT_YET/OK/CANCEL) |
| `decisionDate` | datetime | 구매 확정일 |
| `productClass` | string | 상품 종류 (일반/추가 상품 구분) |

#### productOrderStatus 값
| 코드 | 설명 |
|------|------|
| `PAYMENT_WAITING` | 결제 대기 |
| `PAYED` | 결제 완료 |
| `DELIVERING` | 배송 중 |
| `DELIVERED` | 배송 완료 |
| `PURCHASE_DECIDED` | 구매 확정 |
| `EXCHANGED` | 교환 |
| `CANCELED` | 취소 |
| `RETURNED` | 반품 |
| `CANCELED_BY_NOPAYMENT` | 미결제 취소 |

#### claimStatus 값
| 코드 | 설명 |
|------|------|
| `CANCEL_REQUEST` | 취소 요청 |
| `CANCELING` | 취소 처리 중 |
| `CANCEL_DONE` | 취소 처리 완료 |
| `CANCEL_REJECT` | 취소 철회 |
| `RETURN_REQUEST` | 반품 요청 |
| `EXCHANGE_REQUEST` | 교환 요청 |
| `COLLECTING` | 수거 처리 중 |
| `COLLECT_DONE` | 수거 완료 |
| `EXCHANGE_REDELIVERING` | 교환 재배송 중 |
| `RETURN_DONE` | 반품 완료 |
| `EXCHANGE_DONE` | 교환 완료 |
| `RETURN_REJECT` | 반품 철회 |
| `EXCHANGE_REJECT` | 교환 철회 |
| `PURCHASE_DECISION_HOLDBACK` | 구매 확정 보류 |
| `PURCHASE_DECISION_REQUEST` | 구매 확정 요청 |
| `PURCHASE_DECISION_HOLDBACK_RELEASE` | 구매 확정 보류 해제 |
| `ADMIN_CANCELING` | 직권 취소 중 |
| `ADMIN_CANCEL_DONE` | 직권 취소 완료 |
| `ADMIN_CANCEL_REJECT` | 직권 취소 철회 |

#### claimType 값
| 코드 | 설명 |
|------|------|
| `CANCEL` | 취소 |
| `RETURN` | 반품 |
| `EXCHANGE` | 교환 |
| `PURCHASE_DECISION_HOLDBACK` | 구매 확정 보류 |
| `ADMIN_CANCEL` | 직권 취소 |

### 2.2 상품 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `groupProductId` | number | 그룹 상품 번호 |
| `productId` | string | 채널 상품 번호 (150바이트) |
| `originalProductId` | string | 원상품 번호 (150바이트) |
| `merchantChannelId` | string | 채널 번호 (150바이트) |
| `productName` | string | 상품명 (4000바이트) |
| `productOption` | string | 상품 옵션/옵션명 (4000바이트) |
| `optionCode` | string | 옵션 코드 (1000바이트) |
| `optionPrice` | integer | 옵션 금액 |
| `sellerProductCode` | string | 판매자 상품 코드 (150바이트) |
| `optionManageCode` | string | 옵션 관리 코드 (1000바이트) |
| `itemNo` | string | 아이템 번호 (optionCode와 동일) |
| `mallId` | string | 가맹점 ID (20바이트) |
| `freeGift` | string | 사은품 (1000바이트) |
| `sellerCustomCode1` | string | 판매자 내부 코드 1 (1000바이트) |
| `sellerCustomCode2` | string | 판매자 내부 코드 2 (1000바이트) |
| `individualCustomUniqueCode` | string | 구매자 개인통관고유부호 (거래 종료 시 미노출) |

### 2.3 금액/가격 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `unitPrice` | integer | 상품 가격 |
| `quantity` | integer | 최초 수량 |
| `initialQuantity` | integer | 최초 수량 |
| `remainQuantity` | integer | 잔여 수량 |
| `totalPaymentAmount` | integer | 최초 결제 금액 (할인 적용 후) |
| `initialPaymentAmount` | integer | 최초 결제 금액 (할인 적용 후) |
| `remainPaymentAmount` | integer | 잔여 결제 금액 (할인 적용 후) |
| `totalProductAmount` | integer | 최초 주문 금액 (할인 적용 전) |
| `initialProductAmount` | integer | 최초 주문 금액 (할인 적용 전) |
| `remainProductAmount` | integer | 잔여 주문 금액 (할인 적용 전) |

### 2.4 할인 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `productDiscountAmount` | integer | 최초 상품별 할인액 |
| `initialProductDiscountAmount` | integer | 최초 상품별 할인액 |
| `remainProductDiscountAmount` | integer | 잔여 상품별 할인액 |
| `productImediateDiscountAmount` | integer | 상품별 즉시 할인 금액 |
| `initialProductImmediateDiscountAmount` | integer | 최초 상품별 즉시 할인 금액 |
| `remainProductImmediateDiscountAmount` | integer | 잔여 상품별 즉시 할인 금액 |
| `productProductDiscountAmount` | integer | 상품별 상품 할인 쿠폰 금액 |
| `initialProductProductDiscountAmount` | integer | 최초 상품별 상품 할인 쿠폰 금액 |
| `remainProductProductDiscountAmount` | integer | 잔여 상품별 상품 할인 쿠폰 금액 |
| `productMultiplePurchaseDiscountAmount` | integer | 상품별 복수 구매 할인 금액 |
| `sellerBurdenDiscountAmount` | integer | 판매자 부담 할인액 |
| `sellerBurdenImediateDiscountAmount` | integer | 판매자 부담 즉시 할인 금액 |
| `initialSellerBurdenImmediateDiscountAmount` | integer | 최초 판매자 부담 즉시 할인 금액 |
| `remainSellerBurdenImmediateDiscountAmount` | integer | 잔여 판매자 부담 즉시 할인 금액 |
| `sellerBurdenProductDiscountAmount` | integer | 판매자 부담 상품 할인 쿠폰 금액 |
| `initialSellerBurdenProductDiscountAmount` | integer | 최초 판매자 부담 상품 할인 쿠폰 금액 |
| `remainSellerBurdenProductDiscountAmount` | integer | 잔여 판매자 부담 상품 할인 쿠폰 금액 |
| `sellerBurdenMultiplePurchaseDiscountAmount` | integer | 판매자 부담 복수 구매 할인 금액 |
| `sellerBurdenStoreDiscountAmount` | integer | 판매자 부담 스토어 할인 금액 |
| `sellerBurdenMultiplePurchaseDiscountType` | enum | 판매자 부담 복수 구매 할인 타입 (IGNORE_QUANTITY/QUANTITY) |

### 2.5 배송비 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `deliveryFeeAmount` | integer | 배송비 합계 |
| `deliveryDiscountAmount` | integer | 배송비 최종 할인액 |
| `deliveryPolicyType` | string | 배송비 정책 (조건별 무료 등) |
| `sectionDeliveryFee` | integer | 지역별 추가 배송비 |
| `shippingFeeType` | string | 배송비 형태 (선불/착불/무료) |
| `packageNumber` | string | 묶음배송 번호 (20바이트) |

### 2.6 배송 방법/속성

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `expectedDeliveryMethod` | enum | 배송 방법 코드 |
| `expectedDeliveryCompany` | enum | 택배사 코드 |
| `deliveryAttributeType` | enum | 배송 속성 타입 |
| `arrivalGuaranteeDate` | datetime | 배송 도착 보장 일시 |
| `deliveryTagType` | enum | 배송 태그 타입 |
| `shippingStartDate` | datetime | 발송 시작일 |
| `shippingDueDate` | datetime | 발송 기한 |
| `shippingMemo` | string | 배송 메모 (4000바이트) |

#### expectedDeliveryMethod 값
| 코드 | 설명 |
|------|------|
| `DELIVERY` | 택배, 등기, 소포 |
| `GDFW_ISSUE_SVC` | 굿스플로 송장 출력 |
| `VISIT_RECEIPT` | 방문 수령 |
| `DIRECT_DELIVERY` | 직접 전달 |
| `QUICK_SVC` | 퀵서비스 |
| `NOTHING` | 배송 없음 |
| `RETURN_DESIGNATED` | 지정 반품 택배 |
| `RETURN_DELIVERY` | 일반 반품 택배 |
| `RETURN_INDIVIDUAL` | 직접 반송 |
| `RETURN_MERCHANT` | 판매자 직접 수거 (장보기 전용) |
| `UNKNOWN` | 알 수 없음 |

#### deliveryAttributeType 값
| 코드 | 설명 |
|------|------|
| `NORMAL` | 일반배송 |
| `TODAY` | 오늘출발 |
| `OPTION_TODAY` | 옵션별 오늘출발 |
| `HOPE` | 희망일배송 |
| `TODAY_ARRIVAL` | 당일배송 |
| `DAWN_ARRIVAL` | 새벽배송 |
| `PRE_ORDER` | 예약구매 |
| `ARRIVAL_GUARANTEE` | N배송 |
| `SELLER_GUARANTEE` | N판매자배송 |
| `HOPE_SELLER_GUARANTEE` | N희망일배송 |
| `PICKUP` | 픽업 |
| `QUICK` | 즉시배달 |

#### deliveryTagType 값
| 코드 | 설명 |
|------|------|
| `TODAY` | 오늘배송 |
| `TOMORROW` | 내일배송 |
| `DAWN` | 새벽배송 |
| `SUNDAY` | 일요배송 |
| `STANDARD` | D+2이상배송 |
| `HOPE` | 희망일배송 |

### 2.7 발송 지연

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `delayedDispatchReason` | enum | 발송 지연 사유 코드 |
| `delayedDispatchDetailedReason` | string | 발송 지연 상세 사유 (4000바이트) |

#### delayedDispatchReason 값
| 코드 | 설명 |
|------|------|
| `PRODUCT_PREPARE` | 상품 준비 중 |
| `CUSTOMER_REQUEST` | 고객 요청 |
| `CUSTOM_BUILD` | 주문 제작 |
| `RESERVED_DISPATCH` | 예약 발송 |
| `OVERSEA_DELIVERY` | 해외 배송 |
| `ETC` | 기타 |

### 2.8 수수료/정산 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `commissionRatingType` | string | 수수료 과금 구분 (결제수수료/판매수수료/채널수수료) |
| `commissionPrePayStatus` | enum | 수수료 선결제 상태 |
| `paymentCommission` | integer | 결제 수수료 |
| `saleCommission` | integer | (구)판매 수수료 |
| `channelCommission` | integer | 채널 수수료 |
| `knowledgeShoppingSellingInterlockCommission` | integer | 네이버 쇼핑 매출 연동 수수료 |
| `expectedSettlementAmount` | integer | 정산 예정 금액 |

#### commissionPrePayStatus 값
| 코드 | 설명 |
|------|------|
| `GENERAL_PRD` | 일반 상품 |
| `PRE_PAY_PRD_NO_PAY` | 선차감 (차감 전) |
| `PRE_PAY_PRD_PAYED` | 선차감 (차감 후) |

### 2.9 유입/기타 정보

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `inflowPath` | string | 유입 경로 (검색광고(SA)/공동구매/밴드/네이버쇼핑 등) |
| `inflowPathAdd` | string | 유입 경로 추가 정보 |
| `giftReceivingStatus` | enum | 선물 수락 상태 (WAIT_FOR_RECEIVING/RECEIVED) |
| `taxType` | enum | 상품 과면세 여부 (TAXATION/TAX_EXEMPTION/TAX_FREE) |
| `storageType` | enum | 옵션 보관 유형 (DRY/WET/FROZEN) |
| `logisticsDirectContracted` | boolean | 물류 직계약 여부 |
| `logisticsCompanyId` | string | 물류사 코드 (45바이트) |
| `logisticsCenterId` | string | 물류센터 코드 (45바이트) |

### 2.10 shippingAddress (배송지 주소)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `addressType` | enum | 배송지 타입 (DOMESTIC/FOREIGN) |
| `baseAddress` | string | 기본 주소 (300바이트) |
| `detailedAddress` | string | 상세 주소 (300바이트) |
| `name` | string | 이름 (150바이트) |
| `tel1` | string | 연락처 1 (45바이트) |
| `tel2` | string | 연락처 2 (45바이트) |
| `zipCode` | string | 우편번호 (45바이트) |
| `city` | string | 도시 (국내='') |
| `state` | string | 주 (국내='') |
| `country` | string | 국가 (45바이트) |
| `isRoadNameAddress` | boolean | 도로명 주소 여부 |
| `pickupLocationType` | enum | 수령 위치 (FRONT_OF_DOOR/MANAGEMENT_OFFICE/DIRECT_RECEIVE/OTHER) |
| `pickupLocationContent` | string | 수령 위치 상세 |
| `entryMethod` | enum | 출입 방법 (LOBBY_PW/MANAGEMENT_OFFICE/FREE/OTHER) |
| `entryMethodContent` | string | 출입 방법 상세 |
| `buildingManagementNo` | string | 건물 관리 번호 |
| `longitude` | string | 경도 |
| `latitude` | string | 위도 |

> pickupLocationType, entryMethod 등은 장보기/N배송/N희망일배송/N판매자배송 주문에 한정

### 2.11 takingAddress (판매자 출고지 주소)

shippingAddress와 동일한 구조 (addressType, baseAddress, detailedAddress, name, tel1, tel2, zipCode, city, state, country, isRoadNameAddress)

### 2.12 appliedCoupons[] (쿠폰 사용 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `couponPublishNumber` | string | 쿠폰 발행 번호 |
| `couponClassCode` | enum | 쿠폰 유형 (NMP_PRD_DCNT: 관리자 상품할인 / NMP_PRD_DUP_DCNT: 관리자/판매자 상품중복할인) |
| `couponDiscountAmount` | integer | 쿠폰 할인 금액 |
| `naverBurdenRatio` | integer | 네이버 부담률 |

### 2.13 appliedCardPromotion (카드 프로모션)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `promotionName` | string | 프로모션 이름 (25바이트) |
| `cardCompanyName` | string | 카드사 이름 (250바이트) |
| `promotionApplyAmount` | integer | 프로모션 혜택 금액 |
| `brandCompanyBurdenRatio` | integer | 판매자/가맹점 분담 비율 |

### 2.14 skuMappings[] (물류 재고 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `nsId` | string | 네이버 SKU ID (풀필먼트 상품주문 한정) |
| `nsBarcode` | string | SKU 바코드 (풀필먼트 상품주문 한정) |
| `pickingQuantityPerOrder` | integer | 주문당 피킹수량 (풀필먼트 상품주문 한정) |

### 2.15 hopeDelivery (배송 희망 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `region` | string | 지역 (100바이트) |
| `additionalFee` | integer | 배송 희망 지역 설정 배송비 |
| `hopeDeliveryYmd` | string | 배송 희망일 (yyyymmdd) |
| `hopeDeliveryHm` | string | 배송 희망 시간 (HHmm) |
| `changeReason` | string | 변경 사유 |
| `changer` | string | 변경한 사용자 (구매자/판매자/판매자 API) |

---

## 3. 클레임 정보

### ⚠️ Deprecated (2025년 상반기 제거 예정)

다음 3개 필드는 **2025년 상반기 중 제거 예정**이며, `beforeClaim`/`currentClaim`/`completedClaims` 사용 권장:
- `cancel` → currentClaim/completedClaims 하위 동일 오브젝트
- `return` → currentClaim/completedClaims 하위 동일 오브젝트
- `exchange` → currentClaim/completedClaims 하위 동일 오브젝트

### 3.1 beforeClaim (클레임 요청 전 정보)

클레임이 요청되기 전 상태의 exchange 정보를 포함합니다. `beforeClaim.exchange` 하위 구조.

### 3.2 currentClaim (현재 진행 중 클레임)

현재 처리 중인 클레임 정보. cancel/return/exchange 하위 구조.

### 3.3 completedClaims[] (완료된 클레임 목록)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `claimType` | enum | 클레임 구분 (CANCEL/RETURN/EXCHANGE/PURCHASE_DECISION_HOLDBACK/ADMIN_CANCEL) |
| `claimId` | string | 클레임 번호 |
| `claimStatus` | enum | 클레임 상태 |
| `claimRequestDate` | datetime | 클레임 요청일 |
| `requestChannel` | string | 접수 채널 |
| `claimRequestDetailContent` | string | 클레임 상세 사유 (4000바이트) |
| `claimRequestReason` | enum | 클레임 요청 사유 |

### 3.4 클레임 공통 필드 (cancel/return/exchange/currentClaim 내부)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `claimId` | string | 클레임 번호 (20바이트) |
| `claimStatus` | enum | 클레임 상태 |
| `claimRequestDate` | datetime | 클레임 요청일 |
| `requestChannel` | string | 접수 채널 |
| `requestQuantity` | integer | 요청 수량 |
| `refundExpectedDate` | datetime | 환불 예정일 |
| `refundStandbyReason` | string | 환불 대기 사유 |
| `refundStandbyStatus` | string | 환불 대기 상태 |

#### cancel 전용
| 필드명 | 타입 | 설명 |
|--------|------|------|
| `cancelApprovalDate` | datetime | 취소 승인일 |
| `cancelCompletedDate` | datetime | 취소 완료일 |
| `cancelDetailedReason` | string | 취소 상세 사유 (4000바이트) |
| `cancelReason` | enum | 취소 사유 |

#### return 전용
| 필드명 | 타입 | 설명 |
|--------|------|------|
| `returnDetailedReason` | string | 반품 상세 사유 |
| `returnReason` | enum | 반품 사유 |
| `returnCompletedDate` | datetime | 반품 완료일 |
| `returnReceiveAddress` | object | 판매자 교환/반품 수취 주소 |
| `returnImageUrl` | string[] | 반품 이미지 URL |
| `collectAddress` | object | 구매자 수거지 주소 |
| `collectCompletedDate` | datetime | 수거 완료일 |
| `collectDeliveryCompany` | enum | 수거 택배사 코드 |
| `collectDeliveryMethod` | enum | 수거 배송 방법 |
| `collectStatus` | enum | 수거 상태 |
| `collectTrackingNumber` | string | 수거 송장 번호 |
| `claimDeliveryFeeDemandAmount` | integer | 반품 배송비 청구액 |
| `claimDeliveryFeePayMeans` | string | 반품 배송비 결제 수단 |
| `claimDeliveryFeePayMethod` | string | 반품 배송비 결제 방법 |
| `claimDeliveryFeeProductOrderIds` | string | 반품 배송비 묶음 청구 상품주문번호 (쉼표 구분) |
| `claimDeliveryFeeDiscountAmount` | integer | 반품 배송비 할인액 |
| `remoteAreaCostChargeAmount` | integer | 반품 도서산간 배송비 |
| `etcFeeDemandAmount` | integer | 기타 비용 청구액 |
| `etcFeePayMeans` | string | 기타 비용 결제 수단 |
| `etcFeePayMethod` | string | 기타 비용 결제 방법 |

#### 보류 관련
| 필드명 | 타입 | 설명 |
|--------|------|------|
| `holdbackReason` | enum | 보류 유형 |
| `holdbackDetailedReason` | string | 보류 상세 사유 |
| `holdbackStatus` | enum | 보류 상태 (HOLDBACK/RELEASED) |
| `holdbackConfigDate` | datetime | 보류 설정일 |
| `holdbackConfigurer` | string | 보류 설정자 |
| `holdbackReleaseDate` | datetime | 보류 해제일 |
| `holdbackReleaser` | string | 보류 해제자 |

#### holdbackReason 값
| 코드 | 설명 |
|------|------|
| `RETURN_DELIVERYFEE` | 반품 배송비 청구 |
| `EXTRAFEEE` | 추가 비용 청구 |
| `RETURN_DELIVERYFEE_AND_EXTRAFEEE` | 반품 배송비 + 추가 비용 청구 |
| `RETURN_PRODUCT_NOT_DELIVERED` | 반품 상품 미입고 |
| `ETC` | 기타 사유 |
| `EXCHANGE_DELIVERYFEE` | 교환 배송비 청구 |
| `EXCHANGE_EXTRAFEE` | 추가 교환 비용 청구 |
| `EXCHANGE_PRODUCT_READY` | 교환 상품 준비 중 |
| `EXCHANGE_PRODUCT_NOT_DELIVERED` | 교환 상품 미입고 |
| `EXCHANGE_HOLDBACK` | 교환 구매 확정 보류 |
| `SELLER_CONFIRM_NEED` | 판매자 확인 필요 |
| `PURCHASER_CONFIRM_NEED` | 구매자 확인 필요 |
| `SELLER_REMIT` | 판매자 직접 송금 |
| `ETC2` | 기타 |

#### collectStatus 값
| 코드 | 설명 |
|------|------|
| `NOT_REQUESTED` | 수거 미요청 |
| `COLLECT_REQUEST_TO_AGENT` | 수거 지시 완료 |
| `COLLECT_REQUEST_TO_DELIVERY_COMPANY` | 수거 요청 |
| `COLLECT_WAITING` | 택배사 수거 예정 |
| `DELIVERING` | 수거 진행 중 |
| `DELIVERED` | 수거 완료 |
| `DELIVERY_FAILED` | 배송 실패 |
| `COLLECT_FAILED` | 수거 실패 |
| `WRONG_INVOICE` | 오류 송장 |
| `COLLECT_CANCELED` | 수거 취소 |

#### claimReason 값 (취소/반품/교환 공통)
| 코드 | 설명 |
|------|------|
| `INTENT_CHANGED` | 구매 의사 취소 |
| `COLOR_AND_SIZE` | 색상 및 사이즈 변경 |
| `WRONG_ORDER` | 다른 상품 잘못 주문 |
| `PRODUCT_UNSATISFIED` | 서비스 불만족 |
| `DELAYED_DELIVERY` | 배송 지연 |
| `SOLD_OUT` | 상품 품절 |
| `DROPPED_DELIVERY` | 배송 누락 |
| `NOT_YET_DELIVERY` | 미배송 |
| `BROKEN` | 상품 파손 |
| `INCORRECT_INFO` | 상품 정보 상이 |
| `WRONG_DELIVERY` | 오배송 |
| `WRONG_OPTION` | 색상 등 다른 상품 잘못 배송 |
| `SIMPLE_INTENT_CHANGED` | 단순 변심 |
| `MISTAKE_ORDER` | 주문 실수 |
| `ETC` | 기타 (API에서 지정 불가) |
| `OUT_OF_STOCK` | 재고 부족 |
| `SALE_INTENT_CHANGED` | 판매 의사 변심 |
| `NOT_YET_PAYMENT` | 구매자 미결제 |
| `NOT_YET_RECEIVE` | 상품 미수취 |
| `WRONG_DELAYED_DELIVERY` | 오배송 및 지연 |
| `BROKEN_AND_BAD` | 파손 및 불량 |
| `GIFT_INTENT_CHANGED` | 보내기 취소 |
| `GIFT_REFUSAL` | 선물 거절 |
| `MINOR_RESTRICTED` | 상품 수신 불가 |
| `UNDER_QUANTITY` | 주문 수량 미달 |
| `ASYNC_FAIL_PAYMENT` | 결제 승인 실패 |

### 3.5 클레임 배송비 지원

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `claimDeliveryFeeSupportType` | enum | 클레임 배송비 지원 타입 |
| `claimDeliveryFeeSupportAmount` | integer | 클레임 배송비 지원 금액 |
| `membershipsArrivalGuaranteeClaimSupportingAmount` | integer | 멤버십 N배송 지원 금액 |

#### claimDeliveryFeeSupportType 값
| 코드 | 설명 |
|------|------|
| `MEMBERSHIP_ARRIVAL_GUARANTEE` | 멤버십 도착보장 |
| `MEMBERSHIP_KURLY` | 멤버십 컬리 N마트 |

---

## 4. delivery (배송 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `deliveredDate` | datetime | 배송 완료 일시 |
| `deliveryCompany` | enum | 택배사 코드 |
| `deliveryMethod` | enum | 배송 방법 코드 |
| `deliveryStatus` | enum | 배송 상세 상태 |
| `isWrongTrackingNumber` | boolean | 오류 송장 여부 |
| `pickupDate` | datetime | 집화 일시 |
| `sendDate` | datetime | 발송 일시 |
| `trackingNumber` | string | 송장 번호 (100바이트) |
| `wrongTrackingNumberRegisteredDate` | datetime | 오류 송장 등록 일시 |
| `wrongTrackingNumberType` | string | 오류 사유 |

#### deliveryStatus 값
| 코드 | 설명 |
|------|------|
| `COLLECT_REQUEST` | 수거 요청 |
| `COLLECT_WAIT` | 수거 대기 |
| `COLLECT_CARGO` | 집화 |
| `DELIVERY_COMPLETION` | 배송 완료 |
| `DELIVERING` | 배송중 |
| `DELIVERY_FAIL` | 배송 실패 |
| `WRONG_INVOICE` | 오류 송장 |
| `COLLECT_CARGO_FAIL` | 집화 실패 |
| `COLLECT_CARGO_CANCEL` | 집화 취소 |
| `NOT_TRACKING` | 배송 추적 없음 |

---

## 5. 주요 택배사 코드 (자주 사용)

| 코드 | 설명 |
|------|------|
| `CJGLS` | CJ대한통운 |
| `HYUNDAI` | 롯데택배 |
| `HANJIN` | 한진택배 |
| `KGB` | 로젠택배 |
| `EPOST` | 우체국택배 |
| `CUPARCEL` | CU편의점택배 |
| `DHL` | DHL |
| `FEDEX` | FEDEX |
| `UPS` | UPS |

> 전체 택배사 코드는 API 문서 참조 (약 200개)

---

## 6. 현재 NaverProductOrderDetail과의 GAP 분석

### NaverProductOrderOrder (order 레벨)
**현재 있음**: orderId, orderDate, paymentDate, ordererName, ordererTel, ordererId, ordererNo, payLocationType, paymentMeans
**추가 필요**: chargeAmountPaymentAmount, checkoutAccumulationPaymentAmount, generalPaymentAmount, naverMileagePaymentAmount, orderDiscountAmount, paymentDueDate, isDeliveryMemoParticularInput, payLaterPaymentAmount, isMembershipSubscribed

### ProductOrderInfo (productOrder 레벨)
**현재 있음**: productOrderId, productOrderStatus, productId, productName, productOption, optionCode, sellerProductCode, optionManageCode, quantity, unitPrice, totalProductAmount, productDiscountAmount, totalPaymentAmount, shippingAddress, shippingMemo
**추가 필요 (우선순위 높음)**:
- 상태: claimStatus, claimType, claimId, placeOrderDate, placeOrderStatus, decisionDate
- 상품: groupProductId, originalProductId, productClass, mallId, freeGift, itemNo
- 금액: initialQuantity, remainQuantity, initialPaymentAmount, remainPaymentAmount, initialProductAmount, remainProductAmount, optionPrice
- 할인: sellerBurdenDiscountAmount + 세부 할인 필드들
- 배송: deliveryFeeAmount, deliveryDiscountAmount, deliveryPolicyType, sectionDeliveryFee, shippingFeeType, packageNumber
- 배송속성: expectedDeliveryMethod, expectedDeliveryCompany, deliveryAttributeType, shippingStartDate, shippingDueDate, arrivalGuaranteeDate
- 수수료/정산: commissionRatingType, paymentCommission, saleCommission, channelCommission, expectedSettlementAmount
- 기타: inflowPath, taxType, storageType

### NaverDeliveryInfo (delivery 레벨)
**현재 있음**: deliveryCompany, deliveryMethod, deliveryStatus, trackingNumber, sendDate, deliveredDate, pickupDate, isWrongTrackingNumber
**추가 필요**: wrongTrackingNumberRegisteredDate, wrongTrackingNumberType

### 클레임 관련
**현재 없음** → beforeClaim, currentClaim, completedClaims 구조 전체 신규 추가 필요
