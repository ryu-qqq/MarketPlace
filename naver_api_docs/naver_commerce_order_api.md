# 네이버 커머스 API - 주문 조회 관련 API 문서

> 출처: https://apicenter.commerce.naver.com/docs/commerce-api/current
> 버전: 2.73.0
> 수집일: 2026-03-18

---

## 1. 변경 상품 주문 정보 구조체

**URL**: `schemas/변경-상품-주문-정보-구조체`

이 구조체는 주문건의 변경 상품 주문 정보를 표현하는 구조체입니다.
전체 주문건에서 지정된 조회 조건에 해당하는 주문건을 식별할 수 있는 일부 정보를 표현합니다.

- 이 구조체는 API 호출에 대한 응답으로만 사용합니다.
- 구조체의 객체 1개는 상품주문번호 1개를 표현합니다.

### 필드 목록 (Array)

| 필드명 | 타입 | 필수 | 설명 | 기본값 |
|--------|------|------|------|--------|
| orderId | string | - | 주문 ID. 20바이트 내외 | - |
| productOrderId | string | - | 상품 주문 ID. 20바이트 내외 | - |
| lastChangedType | string (enum) | - | 최종 변경 구분. 250바이트 내외 | - |
| paymentDate | string\<date-time\> | - | 결제 일시. 45바이트 내외. Example: `2023-01-16T17:14:51.794+09:00` | - |
| lastChangedDate | string\<date-time\> | - | 최종 변경 일시. 45바이트 내외. Example: `2023-01-16T17:14:51.794+09:00` | - |
| productOrderStatus | string (enum) | - | 상품 주문 상태. 250바이트 내외 | - |
| claimType | string (enum) | - | 클레임 구분. 250바이트 내외 | - |
| claimStatus | string (enum) | - | 클레임 상태. 250바이트 내외 | - |
| receiverAddressChanged | boolean | - | 배송지 정보 변경 여부. 45바이트 내외 | `false` |
| giftReceivingStatus | string (enum) | - | 선물 수락 상태 구분. 250바이트 내외 | - |

### Enum: lastChangedType

| 코드 | 설명 | 비고 |
|------|------|------|
| PAY_WAITING | 결제 대기 | |
| PAYED | 결제 완료 | |
| EXCHANGE_OPTION | 옵션 변경 | 선물하기 |
| DELIVERY_ADDRESS_CHANGED | 배송지 변경 | |
| GIFT_RECEIVED | 선물 수락 | 선물하기 |
| CLAIM_REJECTED | 클레임 철회 | |
| DISPATCHED | 발송 처리 | |
| CLAIM_REQUESTED | 클레임 요청 | |
| COLLECT_DONE | 수거 완료 | |
| CLAIM_COMPLETED | 클레임 완료 | |
| PURCHASE_DECIDED | 구매 확정 | |
| HOPE_DELIVERY_INFO_CHANGED | 배송 희망일 변경 | |
| CLAIM_REDELIVERING | 교환 재배송처리 | |

### Enum: productOrderStatus

| 코드 | 설명 |
|------|------|
| PAYMENT_WAITING | 결제 대기 |
| PAYED | 결제 완료 |
| DELIVERING | 배송 중 |
| DELIVERED | 배송 완료 |
| PURCHASE_DECIDED | 구매 확정 |
| EXCHANGED | 교환 |
| CANCELED | 취소 |
| RETURNED | 반품 |
| CANCELED_BY_NOPAYMENT | 미결제 취소 |

### Enum: claimType

| 코드 | 설명 |
|------|------|
| CANCEL | 취소 |
| RETURN | 반품 |
| EXCHANGE | 교환 |
| PURCHASE_DECISION_HOLDBACK | 구매 확정 보류 |
| ADMIN_CANCEL | 직권 취소 |

### Enum: claimStatus

| 코드 | 설명 |
|------|------|
| CANCEL_REQUEST | 취소 요청 |
| CANCELING | 취소 처리 중 |
| CANCEL_DONE | 취소 처리 완료 |
| CANCEL_REJECT | 취소 철회 |
| RETURN_REQUEST | 반품 요청 |
| EXCHANGE_REQUEST | 교환 요청 |
| COLLECTING | 수거 처리 중 |
| COLLECT_DONE | 수거 완료 |
| EXCHANGE_REDELIVERING | 교환 재배송 중 |
| RETURN_DONE | 반품 완료 |
| EXCHANGE_DONE | 교환 완료 |
| RETURN_REJECT | 반품 철회 |
| EXCHANGE_REJECT | 교환 철회 |
| PURCHASE_DECISION_HOLDBACK | 구매 확정 보류 |
| PURCHASE_DECISION_REQUEST | 구매 확정 요청 |
| PURCHASE_DECISION_HOLDBACK_RELEASE | 구매 확정 보류 해제 |
| ADMIN_CANCELING | 직권 취소 중 |
| ADMIN_CANCEL_DONE | 직권 취소 완료 |
| ADMIN_CANCEL_REJECT | 직권 취소 철회 |

### Enum: giftReceivingStatus

| 코드 | 설명 |
|------|------|
| WAIT_FOR_RECEIVING | 수락 대기(배송지 입력 대기) |
| RECEIVED | 수락 완료 |

### 예시 JSON
```json
[
  {
    "orderId": "string",
    "productOrderId": "string",
    "lastChangedType": "string",
    "paymentDate": "2023-01-16T17:14:51.794+09:00",
    "lastChangedDate": "2023-01-16T17:14:51.794+09:00",
    "productOrderStatus": "string",
    "claimType": "string",
    "claimStatus": "string",
    "receiverAddressChanged": false,
    "giftReceivingStatus": "string"
  }
]
```

---

## 2. 상품 주문 정보 구조체

**URL**: `schemas/상품-주문-정보-구조체`

이 구조체는 주문건의 상세 정보를 표현하는 구조체입니다.

- 이 구조체는 API 호출에 대한 응답으로만 사용합니다.
- 주문건의 특성 및 진행 상황에 따라 하위 노드 필드 구성이 다양하게 제공될 수 있습니다.
- 구조체의 객체 1개는 상품주문번호 1개를 표현합니다.

### 최상위 필드

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| timestamp | string\<date-time\> | - | Example: `2023-01-16T17:14:51.794+09:00` |
| traceId | string | REQUIRED | |
| data | productOrdersInfo.pay-order-seller (object)[] | - | 상품 주문 정보 구조체 배열 |

### data[].order (주문 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| chargeAmountPaymentAmount | integer | 충전금 결제 금액 |
| checkoutAccumulationPaymentAmount | integer | 체크아웃 적립금 결제 금액 |
| generalPaymentAmount | integer | 일반 결제 금액 |
| naverMileagePaymentAmount | integer | 네이버 마일리지 결제 금액 |
| orderDate | string\<date-time\> | 주문 일시 |
| orderDiscountAmount | integer | 주문 할인 금액 |
| orderId | string | 주문 ID |
| ordererId | string | 주문자 ID |
| ordererName | string | 주문자명 |
| ordererTel | string | 주문자 전화번호 |
| paymentDate | string\<date-time\> | 결제 일시 |
| paymentDueDate | string\<date-time\> | 결제 기한 일시 |
| paymentMeans | string | 결제 수단 |
| isDeliveryMemoParticularInput | string | 배송 메모 특이사항 입력 여부 |
| payLocationType | string | 결제 위치 유형 |
| ordererNo | string | 주문자 번호 |
| payLaterPaymentAmount | integer | 후불 결제 금액 |
| isMembershipSubscribed | boolean | 멤버십 가입 여부 |

### data[].productOrder (상품 주문 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| claimStatus | string (enum) | 클레임 상태 |
| claimType | string (enum) | 클레임 구분 |
| decisionDate | string\<date-time\> | 구매 확정 일시 |
| delayedDispatchDetailedReason | string | 발송 지연 상세 사유 |
| delayedDispatchReason | string (enum) | 발송 지연 사유. Example: `PRODUCT_PREPARE` |
| deliveryDiscountAmount | integer | 배송비 할인 금액 |
| deliveryFeeAmount | integer | 배송비 |
| deliveryPolicyType | string | 배송 정책 유형 |
| expectedDeliveryMethod | string (enum) | 예상 배송 방법. Example: `DELIVERY` |
| freeGift | string | 사은품 |
| mallId | string | 몰 ID |
| optionCode | string | 옵션 코드 |
| optionPrice | integer | 옵션 가격 |
| packageNumber | string | 묶음 배송 번호 |
| placeOrderDate | string\<date-time\> | 발주 일시 |
| placeOrderStatus | string (enum) | 발주 상태 |
| productClass | string | 상품 유형 |
| productDiscountAmount | integer | 상품 할인 금액 |
| initialProductDiscountAmount | integer | 초기 상품 할인 금액 |
| remainProductDiscountAmount | integer | 잔여 상품 할인 금액 |
| groupProductId | integer | 그룹 상품 ID |
| productId | string | 상품 ID |
| originalProductId | string | 원상품 ID |
| merchantChannelId | string | 판매자 채널 ID |
| productName | string | 상품명 |
| productOption | string | 상품 옵션 |
| productOrderId | string | 상품 주문 ID |
| productOrderStatus | string (enum) | 상품 주문 상태 |
| quantity | integer | 수량 |
| initialQuantity | integer | 초기 수량 |
| remainQuantity | integer | 잔여 수량 |
| sectionDeliveryFee | integer | 구간 배송비 |
| sellerProductCode | string | 판매자 상품 코드 |
| shippingAddress | object | 배송지 주소 (하위 참조) |
| shippingStartDate | string\<date-time\> | 배송 시작일 |
| shippingDueDate | string\<date-time\> | 배송 기한일 |
| shippingFeeType | string | 배송비 유형 |
| shippingMemo | string | 배송 메모 |
| takingAddress | object | 수취 주소 (하위 참조) |
| totalPaymentAmount | integer | 총 결제 금액 |
| initialPaymentAmount | integer | 초기 결제 금액 |
| remainPaymentAmount | integer | 잔여 결제 금액 |
| totalProductAmount | integer | 총 상품 금액 |
| initialProductAmount | integer | 초기 상품 금액 |
| remainProductAmount | integer | 잔여 상품 금액 |
| unitPrice | integer | 단가 |
| sellerBurdenDiscountAmount | integer | 판매자 부담 할인 금액 |
| commissionRatingType | string | 수수료 등급 유형 |
| commissionPrePayStatus | string | 수수료 선결제 상태 |
| paymentCommission | integer | 결제 수수료 |
| saleCommission | integer | 판매 수수료 |
| expectedSettlementAmount | integer | 예상 정산 금액 |
| inflowPath | string | 유입 경로 |
| inflowPathAdd | string | 유입 경로 추가 |
| itemNo | string | 아이템 번호 |
| optionManageCode | string | 옵션 관리 코드 |
| sellerCustomCode1 | string | 판매자 커스텀 코드 1 |
| sellerCustomCode2 | string | 판매자 커스텀 코드 2 |
| claimId | string | 클레임 ID |
| channelCommission | integer | 채널 수수료 |
| individualCustomUniqueCode | string | 개인통관고유부호 |
| productImediateDiscountAmount | integer | 상품 즉시 할인 금액 |
| initialProductImmediateDiscountAmount | integer | 초기 상품 즉시 할인 금액 |
| remainProductImmediateDiscountAmount | integer | 잔여 상품 즉시 할인 금액 |
| productProductDiscountAmount | integer | 상품 상품할인 금액 |
| initialProductProductDiscountAmount | integer | 초기 상품 상품할인 금액 |
| remainProductProductDiscountAmount | integer | 잔여 상품 상품할인 금액 |
| productMultiplePurchaseDiscountAmount | integer | 상품 복수 구매 할인 금액 |
| sellerBurdenImediateDiscountAmount | integer | 판매자 부담 즉시 할인 금액 |
| initialSellerBurdenImmediateDiscountAmount | integer | 초기 판매자 부담 즉시 할인 금액 |
| remainSellerBurdenImmediateDiscountAmount | integer | 잔여 판매자 부담 즉시 할인 금액 |
| sellerBurdenProductDiscountAmount | integer | 판매자 부담 상품할인 금액 |
| initialSellerBurdenProductDiscountAmount | integer | 초기 판매자 부담 상품할인 금액 |
| remainSellerBurdenProductDiscountAmount | integer | 잔여 판매자 부담 상품할인 금액 |
| sellerBurdenMultiplePurchaseDiscountAmount | integer | 판매자 부담 복수 구매 할인 금액 |
| knowledgeShoppingSellingInterlockCommission | integer | 지식쇼핑 판매 연동 수수료 |
| giftReceivingStatus | string (enum) | 선물 수락 상태 |
| sellerBurdenStoreDiscountAmount | integer | 판매자 부담 스토어 할인 금액 |
| sellerBurdenMultiplePurchaseDiscountType | string (enum) | 판매자 부담 복수 구매 할인 유형. Example: `IGNORE_QUANTITY` |
| logisticsCompanyId | string | 물류사 ID |
| logisticsCenterId | string | 물류센터 ID |
| skuMappings | object[] | SKU 매핑 정보 (하위 참조) |
| hopeDelivery | object | 희망 배송 정보 (하위 참조) |
| deliveryAttributeType | string | 배송 속성 유형 |
| expectedDeliveryCompany | string | 예상 배송 택배사 |
| arrivalGuaranteeDate | string\<date-time\> | 도착 보장일 |
| deliveryTagType | string | 배송 태그 유형 |
| taxType | string | 과세 유형 |
| storageType | string | 보관 유형 |
| logisticsDirectContracted | boolean | 물류 직계약 여부 |
| appliedCoupons | object[] | 적용 쿠폰 목록 (하위 참조) |
| appliedCardPromotion | object | 적용 카드 프로모션 (하위 참조) |

### data[].productOrder.shippingAddress (배송지 주소)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| addressType | string | 주소 유형 |
| baseAddress | string | 기본 주소 |
| city | string | 시/도 |
| country | string | 국가 |
| detailedAddress | string | 상세 주소 |
| name | string | 수령인명 |
| state | string | 주/도 |
| tel1 | string | 전화번호 1 |
| tel2 | string | 전화번호 2 |
| zipCode | string | 우편번호 |
| isRoadNameAddress | boolean | 도로명 주소 여부 |
| pickupLocationType | string (enum) | 수령 위치 유형. Example: `FRONT_OF_DOOR` |
| pickupLocationContent | string | 수령 위치 내용 |
| entryMethod | string (enum) | 출입 방법. Example: `LOBBY_PW` |
| entryMethodContent | string | 출입 방법 내용 |
| buildingManagementNo | string | 건물 관리 번호 |
| longitude | string | 경도 |
| latitude | string | 위도 |

### data[].productOrder.takingAddress (수취 주소)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| addressType | string | 주소 유형 |
| baseAddress | string | 기본 주소 |
| city | string | 시/도 |
| country | string | 국가 |
| detailedAddress | string | 상세 주소 |
| name | string | 수령인명 |
| state | string | 주/도 |
| tel1 | string | 전화번호 1 |
| tel2 | string | 전화번호 2 |
| zipCode | string | 우편번호 |
| isRoadNameAddress | boolean | 도로명 주소 여부 |

### data[].productOrder.skuMappings[] (SKU 매핑)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| nsId | string | NS ID |
| nsBarcode | string | NS 바코드 |
| pickingQuantityPerOrder | integer | 주문당 피킹 수량 |

### data[].productOrder.hopeDelivery (희망 배송 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| region | string | 지역 |
| additionalFee | integer | 추가 비용 |
| hopeDeliveryYmd | string | 희망 배송일 (YYYYMMDD) |
| hopeDeliveryHm | string | 희망 배송 시간 (HHmm) |
| changeReason | string | 변경 사유 |
| changer | string | 변경자 |

### data[].productOrder.appliedCoupons[] (적용 쿠폰)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| couponPublishNumber | string | 쿠폰 발행 번호 |
| couponClassCode | string | 쿠폰 분류 코드 |
| couponDiscountAmount | integer | 쿠폰 할인 금액 |
| naverBurdenRatio | integer | 네이버 부담 비율 |

### data[].productOrder.appliedCardPromotion (적용 카드 프로모션)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| promotionName | string | 프로모션명 |
| cardCompanyName | string | 카드사명 |
| promotionApplyAmount | integer | 프로모션 적용 금액 |
| brandCompanyBurdenRatio | integer | 브랜드사 부담 비율 |

### data[].cancel (취소 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| claimId | string | 클레임 ID |
| cancelApprovalDate | string\<date-time\> | 취소 승인 일시 |
| cancelCompletedDate | string\<date-time\> | 취소 완료 일시 |
| cancelDetailedReason | string | 취소 상세 사유 |
| cancelReason | string | 취소 사유 |
| claimRequestDate | string\<date-time\> | 클레임 요청 일시 |
| claimStatus | string (enum) | 클레임 상태 |
| refundExpectedDate | string\<date-time\> | 환불 예정 일시 |
| refundStandbyReason | string | 환불 대기 사유 |
| refundStandbyStatus | string | 환불 대기 상태 |
| requestChannel | string | 요청 채널 |
| requestQuantity | integer | 요청 수량 |

### data[].return (반품 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| claimId | string | 클레임 ID |
| claimDeliveryFeeDemandAmount | integer | 클레임 배송비 청구 금액 |
| claimDeliveryFeePayMeans | string | 클레임 배송비 결제 수단 |
| claimDeliveryFeePayMethod | string | 클레임 배송비 결제 방법 |
| claimRequestDate | string\<date-time\> | 클레임 요청 일시 |
| claimStatus | string (enum) | 클레임 상태 |
| collectAddress | object | 수거지 주소 (주소 구조체) |
| collectCompletedDate | string\<date-time\> | 수거 완료 일시 |
| collectDeliveryCompany | string | 수거 택배사 |
| collectDeliveryMethod | string (enum) | 수거 배송 방법. Example: `DELIVERY` |
| collectStatus | string (enum) | 수거 상태. Example: `NOT_REQUESTED` |
| collectTrackingNumber | string | 수거 송장 번호 |
| etcFeeDemandAmount | integer | 기타 비용 청구 금액 |
| etcFeePayMeans | string | 기타 비용 결제 수단 |
| etcFeePayMethod | string | 기타 비용 결제 방법 |
| holdbackDetailedReason | string | 보류 상세 사유 |
| holdbackReason | string | 보류 사유 |
| holdbackStatus | string | 보류 상태 |
| refundExpectedDate | string\<date-time\> | 환불 예정 일시 |
| refundStandbyReason | string | 환불 대기 사유 |
| refundStandbyStatus | string | 환불 대기 상태 |
| requestChannel | string | 요청 채널 |
| requestQuantity | integer | 요청 수량 |
| returnDetailedReason | string | 반품 상세 사유 |
| returnReason | string | 반품 사유 |
| returnReceiveAddress | object | 반품 수령 주소 (주소 구조체 + logisticsCenterId) |
| returnCompletedDate | string\<date-time\> | 반품 완료 일시 |
| holdbackConfigDate | string\<date-time\> | 보류 설정 일시 |
| holdbackConfigurer | string | 보류 설정자 |
| holdbackReleaseDate | string\<date-time\> | 보류 해제 일시 |
| holdbackReleaser | string | 보류 해제자 |
| claimDeliveryFeeProductOrderIds | string | 클레임 배송비 상품주문 ID |
| claimDeliveryFeeDiscountAmount | integer | 클레임 배송비 할인 금액 |
| remoteAreaCostChargeAmount | integer | 도서산간 비용 청구 금액 |
| membershipsArrivalGuaranteeClaimSupportingAmount | integer | 멤버십 도착보장 클레임 지원 금액 |
| returnImageUrl | string[] | 반품 이미지 URL 목록 |
| claimDeliveryFeeSupportType | string | 클레임 배송비 지원 유형 |
| claimDeliveryFeeSupportAmount | integer | 클레임 배송비 지원 금액 |

### data[].exchange (교환 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| claimId | string | 클레임 ID |
| claimDeliveryFeeDemandAmount | integer | 클레임 배송비 청구 금액 |
| claimDeliveryFeePayMeans | string | 클레임 배송비 결제 수단 |
| claimDeliveryFeePayMethod | string | 클레임 배송비 결제 방법 |
| claimRequestDate | string\<date-time\> | 클레임 요청 일시 |
| claimStatus | string (enum) | 클레임 상태 |
| collectAddress | object | 수거지 주소 (주소 구조체) |
| collectCompletedDate | string\<date-time\> | 수거 완료 일시 |
| collectDeliveryCompany | string | 수거 택배사 |
| collectDeliveryMethod | string (enum) | 수거 배송 방법. Example: `DELIVERY` |
| collectStatus | string (enum) | 수거 상태. Example: `NOT_REQUESTED` |
| collectTrackingNumber | string | 수거 송장 번호 |
| etcFeeDemandAmount | integer | 기타 비용 청구 금액 |
| etcFeePayMeans | string | 기타 비용 결제 수단 |
| etcFeePayMethod | string | 기타 비용 결제 방법 |
| exchangeDetailedReason | string | 교환 상세 사유 |
| exchangeReason | string | 교환 사유 |
| holdbackDetailedReason | string | 보류 상세 사유 |
| holdbackReason | string | 보류 사유 |
| holdbackStatus | string | 보류 상태 |
| reDeliveryMethod | string (enum) | 재배송 방법. Example: `DELIVERY` |
| reDeliveryStatus | string (enum) | 재배송 상태. Example: `COLLECT_REQUEST` |
| reDeliveryCompany | string | 재배송 택배사 |
| reDeliveryTrackingNumber | string | 재배송 송장 번호 |
| reDeliveryAddress | object | 재배송 주소 (주소 구조체) |
| requestChannel | string | 요청 채널 |
| requestQuantity | integer | 요청 수량 |
| returnReceiveAddress | object | 반품 수령 주소 (주소 구조체 + logisticsCenterId) |
| holdbackConfigDate | string\<date-time\> | 보류 설정 일시 |
| holdbackConfigurer | string | 보류 설정자 |
| holdbackReleaseDate | string\<date-time\> | 보류 해제 일시 |
| holdbackReleaser | string | 보류 해제자 |
| claimDeliveryFeeProductOrderIds | string | 클레임 배송비 상품주문 ID |
| reDeliveryOperationDate | string\<date-time\> | 재배송 처리 일시 |
| claimDeliveryFeeDiscountAmount | integer | 클레임 배송비 할인 금액 |
| remoteAreaCostChargeAmount | integer | 도서산간 비용 청구 금액 |
| membershipsArrivalGuaranteeClaimSupportingAmount | integer | 멤버십 도착보장 클레임 지원 금액 |
| exchangeImageUrl | string[] | 교환 이미지 URL 목록 |
| claimDeliveryFeeSupportType | string | 클레임 배송비 지원 유형 |
| claimDeliveryFeeSupportAmount | integer | 클레임 배송비 지원 금액 |

### data[].beforeClaim (이전 클레임)

`exchange` 필드를 포함하며, 구조는 data[].exchange와 동일합니다.

### data[].currentClaim (현재 클레임)

`cancel`, `return`, `exchange` 필드를 포함하며, 각각의 구조는 위의 data[].cancel, data[].return, data[].exchange와 동일합니다.

### data[].completedClaims[] (완료된 클레임 목록)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| claimType | string | 클레임 유형 |
| claimId | string | 클레임 ID |
| claimStatus | string | 클레임 상태 |
| claimRequestDate | string\<date-time\> | 클레임 요청 일시 |
| requestChannel | string | 요청 채널 |
| claimRequestDetailContent | string | 클레임 요청 상세 내용 |
| claimRequestReason | string | 클레임 요청 사유 |
| refundExpectedDate | string\<date-time\> | 환불 예정 일시 |
| refundStandbyReason | string | 환불 대기 사유 |
| refundStandbyStatus | string | 환불 대기 상태 |
| requestQuantity | integer | 요청 수량 |
| claimDeliveryFeeDemandAmount | integer | 클레임 배송비 청구 금액 |
| claimDeliveryFeePayMeans | string | 클레임 배송비 결제 수단 |
| claimDeliveryFeePayMethod | string | 클레임 배송비 결제 방법 |
| returnReceiveAddress | object | 반품 수령 주소 |
| collectAddress | object | 수거지 주소 |
| collectCompletedDate | string\<date-time\> | 수거 완료 일시 |
| collectDeliveryCompany | string | 수거 택배사 |
| collectDeliveryMethod | string (enum) | 수거 배송 방법 |
| collectStatus | string (enum) | 수거 상태 |
| collectTrackingNumber | string | 수거 송장 번호 |
| etcFeeDemandAmount | integer | 기타 비용 청구 금액 |
| etcFeePayMeans | string | 기타 비용 결제 수단 |
| etcFeePayMethod | string | 기타 비용 결제 방법 |
| holdbackDetailedReason | string | 보류 상세 사유 |
| holdbackReason | string | 보류 사유 |
| holdbackStatus | string | 보류 상태 |
| holdbackConfigDate | string\<date-time\> | 보류 설정 일시 |
| holdbackConfigurer | string | 보류 설정자 |
| holdbackReleaseDate | string\<date-time\> | 보류 해제 일시 |
| holdbackReleaser | string | 보류 해제자 |
| claimDeliveryFeeProductOrderIds | string | 클레임 배송비 상품주문 ID |
| claimDeliveryFeeDiscountAmount | integer | 클레임 배송비 할인 금액 |
| remoteAreaCostChargeAmount | integer | 도서산간 비용 청구 금액 |
| claimCompleteOperationDate | string\<date-time\> | 클레임 완료 처리 일시 |
| claimRequestAdmissionDate | string\<date-time\> | 클레임 요청 승인 일시 |
| collectOperationDate | string | 수거 처리 일시 |
| collectStartTime | string | 수거 시작 시간 |
| collectEndTime | string | 수거 종료 시간 |
| collectSlotId | string | 수거 슬롯 ID |
| reDeliveryAddress | object | 재배송 주소 |
| reDeliveryMethod | string (enum) | 재배송 방법 |
| reDeliveryStatus | string (enum) | 재배송 상태 |
| reDeliveryCompany | string | 재배송 택배사 |
| reDeliveryTrackingNumber | string | 재배송 송장 번호 |
| reDeliveryOperationDate | string\<date-time\> | 재배송 처리 일시 |
| membershipsArrivalGuaranteeClaimSupportingAmount | integer | 멤버십 도착보장 클레임 지원 금액 |
| claimDeliveryFeeSupportType | string | 클레임 배송비 지원 유형 |
| claimDeliveryFeeSupportAmount | integer | 클레임 배송비 지원 금액 |

### data[].delivery (배송 정보)

| 필드명 | 타입 | 설명 |
|--------|------|------|
| deliveredDate | string\<date-time\> | 배송 완료 일시 |
| deliveryCompany | string | 배송 택배사 |
| deliveryMethod | string (enum) | 배송 방법. Example: `DELIVERY` |
| deliveryStatus | string (enum) | 배송 상태. Example: `COLLECT_REQUEST` |
| isWrongTrackingNumber | boolean | 송장 번호 오류 여부 |
| pickupDate | string\<date-time\> | 픽업 일시 |
| sendDate | string\<date-time\> | 발송 일시 |
| trackingNumber | string | 송장 번호 |
| wrongTrackingNumberRegisteredDate | string\<date-time\> | 송장 번호 오류 등록 일시 |
| wrongTrackingNumberType | string | 송장 번호 오류 유형 |

---

## 3. 상품 주문 목록 조회

**HTTP 메서드**: `GET`
**URL 경로**: `/v1/pay-order/seller/orders/:orderId/product-order-ids`

주문에 대한 상품 주문 번호 목록을 조회합니다.

### 요청 파라미터

#### Path Parameters

| 필드명 | 타입 | 필수 | 설명 | 예시 |
|--------|------|------|------|------|
| orderId | string | REQUIRED | 주문 번호 | `2021123115350911` |

### 응답 구조 (200)

(성공) 상품 주문 번호

**Content-Type**: application/json

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| timestamp | string\<date-time\> | - | Example: `2023-01-16T17:14:51.794+09:00` |
| traceId | string | REQUIRED | |
| data | string[] | - | 상품 주문 번호 목록 |

### curl 예시
```bash
curl -L 'https://api.commerce.naver.com/external/v1/pay-order/seller/orders/:orderId/product-order-ids' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer <token>'
```

---

## 4. 조건형 상품 주문 상세 내역 조회

**HTTP 메서드**: `GET`
**URL 경로**: `/v1/pay-order/seller/product-orders`

조건에 맞는 상품 주문에 대한 상세 내역을 조회합니다.

### 요청 파라미터

#### Query Parameters

| 필드명 | 타입 | 필수 | 설명 | 기본값 | 예시 |
|--------|------|------|------|--------|------|
| from | string\<date-time\> | REQUIRED | 조회 기준의 시작 일시(inclusive) | - | `2024-06-07T19:00:00.000+09:00` |
| to | string\<date-time\> | - | 조회 기준의 종료 일시(inclusive). 생략 시 from으로부터 24시간 후로 자동 지정 | - | `2024-06-08T19:00:00.000+09:00` |
| rangeType | string (enum) | - | 조회 기준 유형. 생략 시 PAYED_DATETIME으로 자동 지정. 250바이트 내외 | `PAYED_DATETIME` | `PAYED_DATETIME` |
| productOrderStatuses | string[] (enum) | - | 상품 주문 상태 목록. 250바이트 내외 | - | `[PAYMENT_WAITING, PAYED, CANCELED]` |
| claimStatuses | string[] (enum) | - | 클레임 상태 목록. 250바이트 내외 | - | `[CANCEL_REQUEST, CANCELING]` |
| placeOrderStatusType | string (enum) | - | 발주 상태. 250바이트 내외 | - | `NOT_YET` |
| fulfillment | boolean | - | 풀필먼트 배송여부 | - | `true` |
| pageSize | integer | - | 페이징 사이즈. >= 1 and <= 300 | `300` | `300` |
| page | integer | - | 페이지 번호. >= 1 | `1` | `1` |
| quantityClaimCompatibility | boolean | - | 수량클레임 변경사항 개발 대응 완료 여부 | - | `true` |

### Enum: rangeType

| 코드 | 설명 |
|------|------|
| PAYED_DATETIME | 결제일시 |
| ORDERED_DATETIME | 주문일시 |
| DISPATCHED_DATETIME | 발송처리일시 |
| PURCHASE_DECIDED_DATETIME | 구매확정일시 |
| CLAIM_REQUESTED_DATETIME | 클레임요청일시 |
| CLAIM_COMPLETED_DATETIME | 클레임완료일시 |
| COLLECT_COMPLETED_DATETIME | 수거완료일시 |
| GIFT_RECEIVED_DATETIME | 선물수락일시 |
| HOPE_DELIVERY_INFO_CHANGED_DATETIME | 배송희망일변경일시 |

### Enum: productOrderStatuses

| 코드 | 설명 |
|------|------|
| PAYMENT_WAITING | 결제 대기 |
| PAYED | 결제 완료 |
| DELIVERING | 배송 중 |
| DELIVERED | 배송 완료 |
| PURCHASE_DECIDED | 구매 확정 |
| EXCHANGED | 교환 |
| CANCELED | 취소 |
| RETURNED | 반품 |
| CANCELED_BY_NOPAYMENT | 미결제 취소 |

### Enum: claimStatuses

| 코드 | 설명 |
|------|------|
| CANCEL_REQUEST | 취소 요청 |
| CANCELING | 취소 처리 중 |
| CANCEL_DONE | 취소 처리 완료 |
| CANCEL_REJECT | 취소 철회 |
| RETURN_REQUEST | 반품 요청 |
| EXCHANGE_REQUEST | 교환 요청 |
| COLLECTING | 수거 처리 중 |
| COLLECT_DONE | 수거 완료 |
| EXCHANGE_REDELIVERING | 교환 재배송 중 |
| RETURN_DONE | 반품 완료 |
| EXCHANGE_DONE | 교환 완료 |
| RETURN_REJECT | 반품 철회 |
| EXCHANGE_REJECT | 교환 철회 |
| PURCHASE_DECISION_HOLDBACK | 구매 확정 보류 |
| PURCHASE_DECISION_REQUEST | 구매 확정 요청 |
| PURCHASE_DECISION_HOLDBACK_RELEASE | 구매 확정 보류 해제 |
| ADMIN_CANCELING | 직권 취소 중 |
| ADMIN_CANCEL_DONE | 직권 취소 완료 |
| ADMIN_CANCEL_REJECT | 직권 취소 철회 |

### Enum: placeOrderStatusType

| 코드 | 설명 |
|------|------|
| NOT_YET | 발주 미확인 |
| OK | 발주 확인 |
| CANCEL | 발주 확인 해제 |

### Enum: fulfillment

| 값 | 설명 |
|------|------|
| null | 풀필먼트 설정된 상품 여부를 구분하지 않고 상품주문 상세내역을 조회 |
| false | 풀필먼트 설정이 되지 않은 상품의 상품주문 상세내역을 조회 |
| true | 풀필먼트 설정된 상품의 상품주문 상세내역을 조회 |

### 응답 구조 (200)

(성공) 상품 주문 내역

**Content-Type**: application/json

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| timestamp | string\<date-time\> | - | Example: `2023-01-16T17:14:51.794+09:00` |
| traceId | string | REQUIRED | |
| data | object | - | (하위 필드는 상품 주문 정보 구조체 참조) |

### curl 예시
```bash
curl -L 'https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer <token>'
```

---

## 5. 변경 상품 주문 내역 조회

**HTTP 메서드**: `GET`
**URL 경로**: `/v1/pay-order/seller/product-orders/last-changed-statuses`

조회 요청 범위의 기준은 변경 일시(date-time)입니다.

조회 종료 일시(lastChangedTo) 값을 생략하면 조회 시작 일시(lastChangedFrom)로부터 이후 24시간의 내역을 조회합니다. 조회 결과는 변경 일시 기준 오름차순으로 정렬되며, 일시가 같으면 상품 주문 번호 기준 오름차순으로 정렬됩니다.

조회 결과는 요청 범위 내에서 최대 300개(또는 limitCount)의 변경된 상품 주문 내역을 제공합니다. 예를 들어 조회 요청 범위 내에 345개의 변경된 상품 주문 내역이 있어도 첫 요청의 응답에는 300개만 제공합니다. 이어서 나머지 45개의 정렬된 데이터를 조회하려면 앞 요청의 응답에 포함된 more 객체의 moreFrom과 moreSequence 값을 다음 요청의 lastChangedFrom과 moreSequence에 각각 입력합니다. 만약 조회 요청 범위 내에 변경된 상품 주문 내역이 300개(또는 limitCount) 이하라면 more 객체는 제공되지 않습니다.

### 요청 파라미터

#### Query Parameters

| 필드명 | 타입 | 필수 | 설명 | 예시 |
|--------|------|------|------|------|
| lastChangedFrom | string\<date-time\> | REQUIRED | 조회 시작 일시(inclusive) | `2022-04-11T15:21:44.000+09:00` |
| lastChangedTo | string\<date-time\> | - | 조회 종료 일시(inclusive). 생략 시 lastChangedFrom으로부터 24시간 후로 자동 지정 | - |
| lastChangedType | string (enum) | - | 최종 변경 구분 (변경 상품 주문 정보 구조체의 lastChangedType enum 참조) | - |
| moreSequence | string | - | moreSequence 사용법은 API 설명 참고. 임의의 값 입력 시 예기치 않은 결과가 제공될 수 있음 | - |
| limitCount | integer | - | 조회 응답 개수 제한. 생략하거나 300을 초과하는 값을 입력하면 최대 300개의 내역을 제공 | - |

### 응답 구조 (200)

(성공) 변경 상품 주문 내역

**Content-Type**: application/json

응답 탭: Schema / not_more / has_more

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| timestamp | string\<date-time\> | - | Example: `2023-01-16T17:14:51.794+09:00` |
| traceId | string | REQUIRED | |
| data | object | - | 변경 상품 주문 내역 (변경 상품 주문 정보 구조체 배열 + more 객체) |

#### data 내부 구조

| 필드명 | 타입 | 설명 |
|--------|------|------|
| lastChangeStatuses | Array | 변경 상품 주문 정보 구조체 배열 (1번 구조체 참조) |
| more | object | 추가 데이터 존재 시 제공 |
| more.moreFrom | string\<date-time\> | 다음 요청 시 lastChangedFrom에 입력할 값 |
| more.moreSequence | string | 다음 요청 시 moreSequence에 입력할 값 |

### curl 예시
```bash
curl -L 'https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders/last-changed-statuses' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer <token>'
```

---

## 6. 상품 주문 상세 내역 조회

**HTTP 메서드**: `POST`
**URL 경로**: `/v1/pay-order/seller/product-orders/query`

상품 주문에 대한 상세 상품 주문 내역을 조회합니다. 요청 가능한 상품 주문 번호는 최대 300개입니다.

### 요청 파라미터

#### Body (required, application/json)

string 배열

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| productOrderIds | string[] | REQUIRED | 상품 주문 번호 목록 (최대 300개) |
| quantityClaimCompatibility | boolean | - | 수량클레임 변경사항 개발 대응 완료 여부 (수량클레임 변경사항에 대한 개발 대응 완료 시 true 값으로 호출) |

### 응답 구조 (200)

(성공) 상품 주문 내역

**Content-Type**: application/json

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| timestamp | string\<date-time\> | - | Example: `2023-01-16T17:14:51.794+09:00` |
| traceId | string | REQUIRED | |
| data | productOrdersInfo.pay-order-seller (object)[] | - | 상품 주문 정보 구조체 배열 (2번 구조체 참조) |

### curl 예시
```bash
curl -L -X POST 'https://api.commerce.naver.com/external/v1/pay-order/seller/product-orders/query' \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -H 'Authorization: Bearer <token>'
```

---

## 공통 Enum 참조

### deliveryMethod (배송 방법 코드)

| 코드 | 설명 |
|------|------|
| DELIVERY | 택배, 등기, 소포 |
| GDFW_ISSUE_SVC | 굿스플로 송장 출력 |
| VISIT_RECEIPT | 방문 수령 |
| DIRECT_DELIVERY | 직접 전달 |
| QUICK_SVC | 퀵서비스 |
| NOTHING | 배송 없음 |
| RETURN_DESIGNATED | 지정 반품 택배 |
| RETURN_DELIVERY | 일반 반품 택배 |
| RETURN_INDIVIDUAL | 직접 반송 |
| RETURN_MERCHANT | 판매자 직접 수거(장보기 전용) |
| UNKNOWN | 알 수 없음(예외 처리에 사용) |

### collectStatus / deliveryStatus (수거/배송 상태)

| 코드 | 설명 |
|------|------|
| NOT_REQUESTED | 미요청 |
| COLLECT_REQUEST | 수거 요청 |
| (기타 값은 API 문서 참조) | |

### reDeliveryStatus (재배송 상태)

| 코드 | 설명 |
|------|------|
| COLLECT_REQUEST | 수거 요청 |
| (기타 값은 API 문서 참조) | |

### pickupLocationType (수령 위치 유형)

| 코드 | 설명 |
|------|------|
| FRONT_OF_DOOR | 문 앞 |
| (기타 값은 API 문서 참조) | |

### entryMethod (출입 방법)

| 코드 | 설명 |
|------|------|
| LOBBY_PW | 로비 비밀번호 |
| (기타 값은 API 문서 참조) | |

### delayedDispatchReason (발송 지연 사유)

| 코드 | 설명 |
|------|------|
| PRODUCT_PREPARE | 상품 준비 |
| (기타 값은 API 문서 참조) | |

### sellerBurdenMultiplePurchaseDiscountType (판매자 부담 복수 구매 할인 유형)

| 코드 | 설명 |
|------|------|
| IGNORE_QUANTITY | 수량 무시 |
| (기타 값은 API 문서 참조) | |

---

## 인증

모든 API는 `Authorization: Bearer <token>` 헤더가 필요합니다.
Base URL: `https://api.commerce.naver.com/external`
