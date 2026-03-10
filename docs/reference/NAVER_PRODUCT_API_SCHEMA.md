# 네이버 커머스 API - 상품 관련 API 구조체

> **출처**: https://apicenter.commerce.naver.com/docs/commerce-api/current/
> **API 버전**: 2.73.0
> **작성일**: 2026-03-10
> **목적**: OMS 상품 동기화 개발 시 참조용

---

## 1. API 엔드포인트 목록

| # | 메서드 | 경로 | 설명 | API 버전 |
|---|--------|------|------|----------|
| 1 | `POST` | `/v2/products` | 상품 등록 | v2 |
| 2 | `PUT` | `/v2/products/origin-products/:originProductNo` | 원상품 수정 (전체) | v2 |
| 3 | `PUT` | `/v2/products/channel-products/:channelProductNo` | 채널 상품 수정 (전체) | v2 |
| 4 | `PATCH` | `/v1/products/origin-products/multi-update` | 멀티 상품 변경 (선택적) | v1 |
| 5 | `PUT` | `/v1/products/origin-products/:originProductNo/change-status` | 판매 상태 변경 | v1 |
| 6 | `PUT` | `/v1/products/origin-products/:originProductNo/option-stock` | 상품 옵션 재고 변경 | v1 |
| 7 | `PUT` | `/v1/products/origin-products/bulk-update` | 상품 벌크 업데이트 | v1 |
| 8 | `GET` | `/v2/products/origin-products/:originProductNo` | 원상품 조회 | v2 |

> **참고**: v2 API의 등록/수정은 `원상품 정보 구조체` + `채널상품 정보 구조체`를 공유합니다.
> v1 부분 수정 API들은 경량화된 별도 스키마를 사용합니다.

---

## 2. 상품 등록 (POST /v2/products) - Request Body

### 2.1 최상위 구조

```
{
  "originProduct": { ... },                    // 원상품 (object) - required
  "smartstoreChannelProduct": { ... },         // 스마트스토어 채널상품 (object)
  "windowChannelProduct": { ... }              // 쇼핑윈도 채널상품 (object)
}
```

### 2.2 originProduct (원상품) 필드

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `statusType` | string | **Y** | 판매 상태 코드. 등록 시 `SALE`만 가능 |
| `saleType` | string | N | 판매 유형 (`NEW`, `OLD`). 미입력 시 `NEW` |
| `leafCategoryId` | string | **등록 시 Y** | 리프 카테고리 ID |
| `name` | string | **Y** | 상품명 |
| `detailContent` | string | **Y** | 상품 상세 정보 (HTML) |
| `images` | object | **Y** | 상품 이미지 |
| `saleStartDate` | string\<date-time\> | N | 판매 시작 일시 |
| `saleEndDate` | string\<date-time\> | N | 판매 종료 일시 |
| `salePrice` | integer\<int64\> | **Y** | 판매 가격 (최대 999,999,990) |
| `stockQuantity` | integer\<int32\> | N | 재고 수량 (최대 99,999,999) |
| `deliveryInfo` | object | **Y** | 배송 정보 |
| `productLogistics` | object[] | N | 물류사 정보 |
| `detailAttribute` | object | **Y** | 원상품 상세 속성 |
| `customerBenefit` | object | N | 고객 혜택 정보 |

#### statusType 가능한 값
- `WAIT` (판매 대기), `SALE` (판매 중), `OUTOFSTOCK` (품절)
- `UNADMISSION` (승인 대기), `REJECTION` (승인 거부)
- `SUSPENSION` (판매 중지), `CLOSE` (판매 종료), `PROHIBITION` (판매 금지)

### 2.3 originProduct.images (상품 이미지)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `representativeImage` | object | **Y** | 대표 이미지 |
| `representativeImage.url` | string | **Y** | 이미지 URL |
| `optionalImages` | object[] | N | 추가 이미지 (최대 9장) |
| `optionalImages[].url` | string | **Y** | 이미지 URL |

### 2.4 originProduct.deliveryInfo (배송 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `deliveryType` | string | **Y** | 배송 방법 (`DELIVERY`, `DIRECT` 등) |
| `deliveryAttributeType` | string | **Y** | 배송 속성 (`NORMAL`, `TODAY` 등) |
| `deliveryCompany` | string | N | 택배사 |
| `outboundLocationId` | string | N | 판매자 창고 ID |
| `deliveryBundleGroupUsable` | boolean | N | 묶음배송 가능 여부 |
| `deliveryBundleGroupId` | integer\<int64\> | N | 묶음배송 그룹 코드 |
| `quickServiceAreas` | string[] | N | 퀵서비스 지역 |
| `visitAddressId` | integer\<int64\> | N | 방문 수령 주소록 ID |
| `deliveryFee` | object | **Y** | 배송비 정보 |
| `claimDeliveryInfo` | object | **Y** | 반품/교환 정보 |
| `installation` | boolean | N | 설치 여부 |
| `installationFee` | boolean | N | 별도 설치비 유무 |
| `expectedDeliveryPeriodType` | string | N | 발송 예정일 타입 |
| `expectedDeliveryPeriodDirectInput` | string | N | 발송 예정일 직접 입력 |
| `todayStockQuantity` | integer\<int32\> | N | 오늘출발 재고 수량 |
| `customProductAfterOrderYn` | boolean | N | 주문 확인 후 제작 여부 |
| `hopeDeliveryGroupId` | integer\<int64\> | N | 희망일배송 그룹 번호 |
| `businessCustomsClearanceSaleYn` | boolean | N | 사업자 통관 판매 여부 |

#### deliveryFee (배송비 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `deliveryFeeType` | string | N | 배송비 타입 (`FREE`, `CONDITIONAL_FREE`, `PAID` 등) |
| `baseFee` | integer\<int32\> | N | 기본 배송비 |
| `freeConditionalAmount` | integer\<int32\> | N | 무료 조건 금액 |
| `repeatQuantity` | integer\<int32\> | N | 기본 배송비 반복 부과 수량 |
| `secondBaseQuantity` | integer\<int32\> | N | 2구간 수량 |
| `secondExtraFee` | integer\<int32\> | N | 2구간 추가 배송비 |
| `thirdBaseQuantity` | integer\<int32\> | N | 3구간 수량 |
| `thirdExtraFee` | integer\<int32\> | N | 3구간 추가 배송비 |
| `deliveryFeePayType` | string | N | 배송비 결제 방식 (`COLLECT`, `PREPAID` 등) |
| `deliveryFeeByArea` | object | N | 지역별 추가 배송비 |
| `differentialFeeByArea` | string | N | 지역별 차등 배송비 정보 |

#### claimDeliveryInfo (반품/교환 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `returnDeliveryCompanyPriorityType` | string | N | 반품 택배사 우선순위 (`PRIMARY`) |
| `returnDeliveryFee` | integer\<int32\> | **Y** | 반품 배송비 |
| `exchangeDeliveryFee` | integer\<int32\> | **Y** | 교환 배송비 |
| `shippingAddressId` | integer\<int64\> | N | 출고지 주소록 번호 |
| `returnAddressId` | integer\<int64\> | N | 반품/교환지 주소록 번호 |
| `freeReturnInsuranceYn` | boolean | N | 반품안심케어 설정 |

### 2.5 originProduct.detailAttribute (상세 속성)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `naverShoppingSearchInfo` | object | N | 네이버 쇼핑 검색 정보 |
| `manufactureDefineNo` | string | N | 품번 |
| `afterServiceInfo` | object | **Y** | A/S 정보 |
| `purchaseQuantityInfo` | object | N | 구매 수량 설정 |
| `originAreaInfo` | object | **Y** | 원산지 정보 |
| `sellerCodeInfo` | object | N | 판매자 코드 정보 |
| `skuYn` | boolean | N | SKU 생성 여부 |
| `optionInfo` | object | **Y** | 옵션 정보 |
| `supplementProductInfo` | object | N | 추가 상품 |
| `purchaseReviewInfo` | object | N | 구매평 정보 |
| `isbnInfo` | object | N | ISBN 정보 |
| `bookInfo` | object | N | 도서 정보 |
| `eventPhraseCont` | string | N | 이벤트 문구 (홍보 문구) |
| `manufactureDate` | string\<date\> | N | 제조일자 |
| `releaseDate` | string\<date\> | N | 출시일자 |
| `validDate` | string\<date\> | N | 유효일자 |
| `taxType` | string | N | 부가가치세 타입 (`TAX`, `DUTYFREE`, `SMALL`) |
| `customsTaxType` | string | N | 관부가세 타입 |
| `productCertificationInfos` | object[] | N | 인증 정보 목록 |
| `certificationTargetExcludeContent` | object | N | 인증 대상 제외 정보 |
| `sellerCommentContent` | string | N | 판매자 특이 사항 |
| `sellerCommentUsable` | boolean | N | 판매자 특이 사항 사용 여부 |
| `minorPurchasable` | boolean | **Y** | 미성년자 구매 가능 여부 |
| `ecoupon` | object | N | E쿠폰 |
| `productInfoProvidedNotice` | object | **Y** | 상품정보제공고시 |
| `productAttributes` | object[] | N | 상품 속성 목록 |
| `cultureCostIncomeDeductionYn` | boolean | N | 문화비 소득공제 여부 |
| `customProductYn` | boolean | N | 맞춤 제작 상품 여부 |
| `itselfProductionProductYn` | boolean | N | 자체 제작 상품 여부 |
| `brandCertificationYn` | boolean | N | 브랜드 인증 여부 |
| `seoInfo` | object | N | SEO 정보 |
| `productSize` | object | N | 상품 사이즈 |
| `unitCapacity` | object | N | 단위 용량 |

#### naverShoppingSearchInfo (네이버 쇼핑 검색 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `modelId` | integer\<int64\> | N | 상품 모델 ID |
| `modelName` | string | N | 상품 모델명 |
| `manufacturerName` | string | N | 제조사명 |
| `brandId` | integer\<int64\> | N | 브랜드 ID |
| `brandName` | string | N | 브랜드명 |
| `catalogMatchingYn` | boolean | N | 카탈로그 연결 여부 |
| `matchedCatalogId` | integer\<int64\> | N | 연결된 카탈로그 ID |

#### afterServiceInfo (A/S 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `afterServiceTelephoneNumber` | string | **Y** | A/S 전화번호 |
| `afterServiceGuideContent` | string | **Y** | A/S 안내 |

#### originAreaInfo (원산지 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `originAreaCode` | string | **Y** | 원산지 상세 지역 코드 |
| `importer` | string | N | 수입사명 |
| `content` | string | N | 원산지 표시 내용 |
| `plural` | boolean | N | 복수 원산지 여부 |

#### sellerCodeInfo (판매자 코드 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `sellerManagementCode` | string | N | 판매자 관리 코드 |
| `sellerBarcode` | string | N | 판매자 바코드 |
| `sellerCustomCode1` | string | N | 판매자 내부 코드 1 |
| `sellerCustomCode2` | string | N | 판매자 내부 코드 2 |

#### optionInfo (옵션 정보)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `simpleOptionSortType` | string | N | 단독형 옵션 정렬 (`CREATE`, `ABC`, `LOW_PRICE` 등) |
| `optionSimple` | object[] | N | 단독형 옵션 목록 |
| `optionCustom` | object[] | N | 직접입력형 옵션 목록 |
| `optionCombinationSortType` | string | N | 조합형 옵션 정렬 |
| `optionCombinationGroupNames` | object | N | 조합형 옵션 그룹명 (최대 4개) |
| `optionCombinations` | object[] | N | 조합형 옵션 목록 |
| `standardOptionGroups` | object[] | N | 표준형 옵션 그룹 |
| `optionStandards` | object[] | N | 표준형 옵션 목록 |
| `useStockManagement` | boolean | N | 옵션 재고 수량 관리 사용 여부 |
| `optionDeliveryAttributes` | string[] | N | 옵션별 배송 속성 목록 |

#### optionCombinations (조합형 옵션) 항목

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `id` | integer\<int64\> | N | 옵션 ID (수정 시 사용) |
| `stockQuantity` | integer\<int32\> | N | 재고 수량 |
| `price` | integer\<int32\> | N | 옵션가 |
| `usable` | boolean | N | 사용 여부 |
| `optionName1` | string | N | 옵션값 1 |
| `optionName2` | string | N | 옵션값 2 |
| `optionName3` | string | N | 옵션값 3 |
| `optionName4` | string | N | 옵션값 4 |
| `sellerManagerCode` | string | N | 판매자 관리 코드 |
| `skuYn` | boolean | N | SKU 생성 여부 |

#### productInfoProvidedNotice (상품정보제공고시)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `productInfoProvidedNoticeType` | string | **Y** | 상품군 유형 코드 |
| `wear` | object | 조건부 | 의류 |
| `shoes` | object | 조건부 | 구두/신발 |
| `bag` | object | 조건부 | 가방 |
| `fashionItems` | object | 조건부 | 패션잡화 |
| `sleepingGear` | object | 조건부 | 침구류/커튼 |
| `furniture` | object | 조건부 | 가구 |
| `imageAppliances` | object | 조건부 | 영상가전 |
| `homeAppliances` | object | 조건부 | 가정용 전기제품 |
| `seasonAppliances` | object | 조건부 | 계절가전 |
| `officeAppliances` | object | 조건부 | 사무용기기 |
| `opticsAppliances` | object | 조건부 | 광학기기 |
| `microElectronics` | object | 조건부 | 소형전자 |
| `navigation` | object | 조건부 | 내비게이션 |
| `carArticles` | object | 조건부 | 자동차용품 |
| `medicalAppliances` | object | 조건부 | 의료기기 |
| `kitchenUtensils` | object | 조건부 | 주방용품 |
| `cosmetic` | object | 조건부 | 화장품 |
| `jewellery` | object | 조건부 | 귀금속/보석/시계 |
| `food` | object | 조건부 | 식품 |
| `generalFood` | object | 조건부 | 가공식품 |
| `dietFood` | object | 조건부 | 건강기능식품 |
| `kids` | object | 조건부 | 영유아용품 |
| `musicalInstrument` | object | 조건부 | 악기 |
| `sportsEquipment` | object | 조건부 | 스포츠용품 |
| `books` | object | 조건부 | 서적 |
| `rentalEtc` | object | 조건부 | 물품대여(서적/유아용품 등) |
| `rentalHa` | object | 조건부 | 물품대여(정수기/비데 등) |
| `digitalContents` | object | 조건부 | 디지털 콘텐츠 |
| `giftCard` | object | 조건부 | 상품권/쿠폰 |
| `mobileCoupon` | object | 조건부 | 모바일 쿠폰 |
| `movieShow` | object | 조건부 | 영화/공연 |
| `etcService` | object | 조건부 | 기타 용역 |
| `biochemistry` | object | 조건부 | 생활화학제품 |
| `biocidal` | object | 조건부 | 살생물제품 |
| `cellPhone` | object | 조건부 | 휴대폰 |
| `etc` | object | 조건부 | 기타 재화 |

> 각 고시 타입별 공통 필드: `returnCostReason`, `noRefundReason`, `qualityAssuranceStandard`, `compensationProcedure`, `troubleShootingContents` + 타입별 고유 필드

### 2.6 originProduct.customerBenefit (고객 혜택)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `immediateDiscountPolicy` | object | N | 판매자 기본 할인 정책 |
| `purchasePointPolicy` | object | N | 상품 구매 포인트 정책 |
| `reviewPointPolicy` | object | N | 구매평 포인트 정책 |
| `freeInterestPolicy` | object | N | 무이자 할부 정책 |
| `giftPolicy` | object | N | 사은품 정책 |
| `multiPurchaseDiscountPolicy` | object | N | 복수 구매 할인 정책 |
| `reservedDiscountPolicy` | object | N | 예약 할인 정책 |

### 2.7 smartstoreChannelProduct (스마트스토어 채널상품)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `channelProductName` | string | N | 채널 전용 상품명 (미입력 시 원상품명) |
| `bbsSeq` | integer\<int64\> | N | 콘텐츠 게시글 일련번호 (공지사항) |
| `storeKeepExclusiveProduct` | boolean | N | 알림받기 회원 전용 상품 여부 |
| `naverShoppingRegistration` | boolean | **Y** | 네이버 쇼핑 등록 여부 |
| `channelProductDisplayStatusType` | string | **Y** | 전시 상태 코드 (`WAIT`, `ON`, `SUSPENSION`) |

### 2.8 windowChannelProduct (쇼핑윈도 채널상품)

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `channelProductName` | string | N | 채널 전용 상품명 |
| `bbsSeq` | integer\<int64\> | N | 콘텐츠 게시글 일련번호 |
| `storeKeepExclusiveProduct` | boolean | N | 알림받기 회원 전용 상품 여부 |
| `naverShoppingRegistration` | boolean | **Y** | 네이버 쇼핑 등록 여부 |
| `channelNo` | integer\<int64\> | **Y** | 윈도 채널 번호 |
| `best` | boolean | N | 베스트 여부 |

---

## 3. 원상품 수정 (PUT /v2/products/origin-products/:originProductNo) - Request Body

> **중요**: 원상품 수정의 Request Body 구조는 상품 등록(POST)과 **동일**합니다.

### 등록과의 차이점

| 항목 | 등록 (POST) | 수정 (PUT) |
|------|-------------|------------|
| `statusType` | `SALE`만 가능 | `SALE`, `SUSPENSION` 가능 |
| `leafCategoryId` | 필수 | 카탈로그 변경 시에만 필수 |
| `detailContent` | 필수 | 생략 가능 (기존값 유지) |
| `salePrice` | 필수 | 필수 |
| Path Parameter | 없음 | `originProductNo` (필수) |

> **핵심**: 수정 API는 전체 대치(full replacement) 방식입니다.
> 보내지 않은 필드는 null/기본값으로 초기화될 수 있으므로, **반드시 조회 후 전체 데이터를 포함하여 수정 요청**해야 합니다.

---

## 4. 채널 상품 수정 (PUT /v2/products/channel-products/:channelProductNo) - Request Body

> 채널 상품 수정도 등록과 동일한 구조체를 사용합니다.
> Path Parameter로 `channelProductNo`를 받습니다.

---

## 5. 부분 수정 엔드포인트

### 5.1 멀티 상품 변경 (PATCH /v1/products/origin-products/multi-update)

**여러 상품의 판매가, 재고, 할인, 판매 상태를 각각 다르게 변경할 수 있는 핵심 API입니다.**

#### Request Body

```json
{
  "multiProductUpdateRequestVos": [
    {
      "originProductNo": 12345,
      "multiUpdateTypes": ["SALE_PRICE", "STOCK"],
      "productSalePrice": {
        "salePrice": 29900
      },
      "immediateDiscountPolicy": {
        "discountMethod": { ... }
      },
      "stockQuantity": 100
    }
  ]
}
```

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `multiProductUpdateRequestVos` | object[] | **Y** | 수정 요청 목록 |
| `[].originProductNo` | integer\<int64\> | **Y** | 원상품 번호 |
| `[].multiUpdateTypes` | string[] | N | 수정할 항목 지정 |
| `[].productSalePrice` | object | N | 판매가 정보 |
| `[].productSalePrice.salePrice` | integer\<int32\> | **Y** | 판매 가격 |
| `[].immediateDiscountPolicy` | object | N | 판매자 기본 할인 정책 |
| `[].immediateDiscountPolicy.discountMethod` | object | N | 할인 혜택 |
| `[].stockQuantity` | integer\<int32\> | N | 재고 수량 |

#### multiUpdateTypes 가능한 값

| 값 | 설명 |
|----|------|
| `SALE_PRICE` | 판매가 변경 |
| `IMMEDIATE_DISCOUNT` | 기본 할인 변경 |
| `STOCK` | 재고 수량 변경 |
| `PRODUCT_STATUS_SALE` | 판매 중으로 상태 변경 |
| `PRODUCT_STATUS_SUSPENSION` | 판매 중지로 상태 변경 |

### 5.2 판매 상태 변경 (PUT /v1/products/origin-products/:originProductNo/change-status)

**단일 상품의 판매 상태만 변경하는 경량 API입니다.**

#### Request Body

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `statusType` | string | **Y** | 변경할 판매 상태 |
| `saleStartDate` | string\<date-time\> | N | 판매 시작 일시 |
| `saleEndDate` | string\<date-time\> | N | 판매 종료 일시 |
| `stockQuantity` | integer\<int64\> | N | 재고 수량 (최대 99,999,999) |

#### 상태 전이 규칙
- `SALE` → `OUTOFSTOCK` (재고 0으로 변경됨)
- `SUSPENSION`, `OUTOFSTOCK` → `SALE` (품절→판매 중 변경 시 재고 수량 필수)
- `SALE`, `OUTOFSTOCK`, `WAIT` → `SUSPENSION`
- 재고 수량 0이면 `OUTOFSTOCK` 유지 (단, `SUSPENSION`이면 유지)

### 5.3 상품 옵션 재고 변경 (PUT /v1/products/origin-products/:originProductNo/option-stock)

**상품 옵션의 재고, 가격, 할인가를 변경하는 API입니다.**

#### Request Body

| 필드명 | 타입 | 필수 | 설명 |
|--------|------|------|------|
| `productSalePrice` | object | N | 판매가 정보 |
| `productSalePrice.salePrice` | integer\<int32\> | **Y** | 판매 가격 |
| `immediateDiscountPolicy` | object | N | 판매자 기본 할인 정책 |
| `optionInfo` | object | **Y** | 옵션 정보 |
| `optionInfo.optionCombinations` | object[] | N | 조합형 옵션 목록 |
| `optionInfo.optionStandards` | object[] | N | 표준형 옵션 목록 |
| `optionInfo.useStockManagement` | boolean | N | 재고 관리 사용 여부 (false → 수량 9,999) |

> **참고**: 옵션 타입, 옵션명, 옵션값 변경이 필요하면 상품 수정 API(PUT /v2)를 사용해야 합니다.

### 5.4 상품 벌크 업데이트 (PUT /v1/products/origin-products/bulk-update)

> 여러 상품을 동시에 수정합니다. 멀티 상품 변경(5.1)과 유사하나 PUT 메서드를 사용합니다.
> 상세 스키마는 멀티 상품 변경과 거의 동일합니다.

---

## 6. 원상품 조회 (GET /v2/products/origin-products/:originProductNo) - Response Body

### 응답 최상위 구조

```json
{
  "originProductNo": 12345,
  "smartstoreChannelProductNo": 67890,
  "windowChannelProductNo": null,
  "originProduct": { ... },
  "smartstoreChannelProduct": { ... },
  "windowChannelProduct": { ... }
}
```

| 필드명 | 타입 | 설명 |
|--------|------|------|
| `originProductNo` | integer\<int64\> | 원상품 번호 |
| `smartstoreChannelProductNo` | integer\<int64\> | 스마트스토어 채널 상품번호 |
| `windowChannelProductNo` | integer\<int64\> | 윈도 채널 상품번호 |
| `originProduct` | object | 원상품 정보 (등록 시와 동일 구조 + 읽기 전용 필드) |
| `smartstoreChannelProduct` | object | 스마트스토어 채널상품 |
| `windowChannelProduct` | object | 윈도 채널상품 |

> **응답의 `originProduct`는 요청 구조와 거의 동일**하며, 추가로 읽기 전용 필드가 포함됩니다.
> 예: `detailAttribute`가 `조회용 원상품 상세 속성 (object)`으로 명시되며,
> 등록 시에는 사용하지 않는 계산된 값들(카탈로그 매칭 결과 등)이 포함됩니다.

---

## 7. 등록 vs 수정 vs 부분수정 비교표

| 기능 | POST /v2/products | PUT /v2/.../origin-products/:id | PUT /v2/.../channel-products/:id | PATCH /v1/.../multi-update | PUT /v1/.../change-status | PUT /v1/.../option-stock |
|------|------|------|------|------|------|------|
| **방식** | 전체 등록 | 전체 대치 | 전체 대치 | **선택적 부분 수정** | 상태만 변경 | 옵션 재고만 변경 |
| 상품명 | Y | Y | - | - | - | - |
| 상세정보 | Y | Y (생략 가능) | - | - | - | - |
| 판매가 | Y | Y | - | **Y** (SALE_PRICE) | - | Y |
| 재고 | Y | Y | - | **Y** (STOCK) | Y | Y (옵션별) |
| 판매상태 | Y | Y | - | **Y** (STATUS_*) | **Y** | - |
| 할인정책 | Y | Y | - | **Y** (IMMEDIATE_DISCOUNT) | - | Y |
| 이미지 | Y | Y | - | - | - | - |
| 배송정보 | Y | Y | - | - | - | - |
| 옵션정보 | Y | Y | - | - | - | Y (재고/가격만) |
| 고시정보 | Y | Y | - | - | - | - |
| 채널설정 | Y | - | Y | - | - | - |
| **다건 지원** | 1건 | 1건 | 1건 | **복수건** | 1건 | 1건 |

---

## 8. 우리 시스템과의 매핑 분석

### 8.1 내부 필드 → 네이버 API 필드 매핑

| 내부 시스템 (ProductGroup/OutboundProduct) | 네이버 API 필드 | 비고 |
|-------------------------------------------|----------------|------|
| `productGroupName` | `originProduct.name` | 상품명 |
| `detailDescription` (HTML) | `originProduct.detailContent` | 상세 설명 |
| `regularPrice` | - | 네이버에 정가 개념 없음 |
| `currentPrice` / `sellingPrice` | `originProduct.salePrice` | 판매가 |
| `mainImageUrl` | `originProduct.images.representativeImage.url` | 대표 이미지 |
| `additionalImageUrls` | `originProduct.images.optionalImages[].url` | 추가 이미지 |
| `brandName` / `brandId` | `detailAttribute.naverShoppingSearchInfo.brandId/brandName` | 브랜드 |
| `categoryId` | `originProduct.leafCategoryId` | 네이버 카테고리 ID로 매핑 필요 |
| `sellerManagementCode` | `detailAttribute.sellerCodeInfo.sellerManagementCode` | 판매자 관리코드 |
| 옵션 (size/color) | `detailAttribute.optionInfo.optionCombinations[]` | 조합형 옵션 |
| 옵션별 재고 | `optionCombinations[].stockQuantity` | |
| 옵션별 추가가격 | `optionCombinations[].price` | |
| 배송비 정보 | `originProduct.deliveryInfo` | 사전 설정 필요 |
| A/S 정보 | `detailAttribute.afterServiceInfo` | 셀러별 사전 설정 |
| 원산지 | `detailAttribute.originAreaInfo` | 원산지 코드 매핑 필요 |
| 고시정보 | `detailAttribute.productInfoProvidedNotice` | 카테고리별 타입 매핑 필요 |

### 8.2 네이버 Partial Update 지원 여부 (핵심 질문 답변)

> **Q: 네이버는 Setof처럼 스마트/선택적 부분 수정을 지원하는가?**

**A: 제한적으로 지원합니다.**

네이버 커머스 API는 두 가지 수정 전략을 제공합니다:

#### 1) 전체 대치 (Full Replacement) - v2 API
- `PUT /v2/products/origin-products/:id` - 원상품 전체 수정
- `PUT /v2/products/channel-products/:id` - 채널상품 전체 수정
- 모든 필드를 보내야 하며, 누락된 필드는 null/기본값으로 덮어씌워질 위험
- **조회 → 수정할 필드만 변경 → 전체 재전송** 패턴 필요

#### 2) 선택적 부분 수정 - v1 API (제한된 필드만)
- **`PATCH /v1/.../multi-update`**: 가장 유연한 부분 수정. `multiUpdateTypes`로 수정할 항목을 명시적으로 선택:
  - `SALE_PRICE` - 판매가만 변경
  - `IMMEDIATE_DISCOUNT` - 할인만 변경
  - `STOCK` - 재고만 변경
  - `PRODUCT_STATUS_SALE` / `PRODUCT_STATUS_SUSPENSION` - 상태만 변경
  - **복수 상품 동시 처리 가능**
- **`PUT /v1/.../change-status`**: 판매 상태 + 재고만
- **`PUT /v1/.../option-stock`**: 옵션별 재고/가격만

#### 결론: 우리 시스템 적용 전략

```
[빈번한 변경] → PATCH multi-update (가격, 재고, 상태, 할인)
[옵션 재고]   → PUT option-stock (옵션별 세밀한 재고/가격 조정)
[전체 수정]   → PUT v2 origin-products (상품 정보 전체 변경 시)
[상태만]      → PUT change-status (품절/판매중지 전환)
```

**Setof와의 차이점**: Setof는 SetOf(수정할_필드_목록) 패턴으로 모든 필드에 대해 선택적 수정이 가능하지만, 네이버는 **v1 PATCH로 가격/재고/상태/할인만 선택적 수정**이 가능하고, 나머지(상품명, 이미지, 배송정보, 고시정보 등)는 **v2 PUT 전체 대치만 가능**합니다.

---

## 부록 A. 원상품 정보 구조체 - JSON 샘플 (축약)

```json
{
  "statusType": "SALE",
  "saleType": "NEW",
  "leafCategoryId": "50000803",
  "name": "테스트 상품",
  "detailContent": "<html>...</html>",
  "images": {
    "representativeImage": { "url": "https://..." },
    "optionalImages": [{ "url": "https://..." }]
  },
  "salePrice": 29900,
  "stockQuantity": 100,
  "deliveryInfo": {
    "deliveryType": "DELIVERY",
    "deliveryAttributeType": "NORMAL",
    "deliveryCompany": "CJGLS",
    "deliveryFee": {
      "deliveryFeeType": "FREE",
      "deliveryFeePayType": "PREPAID",
      "deliveryFeeByArea": {
        "deliveryAreaType": "AREA_2",
        "area2extraFee": 3000,
        "area3extraFee": 5000
      }
    },
    "claimDeliveryInfo": {
      "returnDeliveryFee": 2500,
      "exchangeDeliveryFee": 5000,
      "shippingAddressId": 12345,
      "returnAddressId": 12345
    }
  },
  "detailAttribute": {
    "naverShoppingSearchInfo": {
      "brandId": 12345,
      "manufacturerName": "제조사"
    },
    "afterServiceInfo": {
      "afterServiceTelephoneNumber": "02-1234-5678",
      "afterServiceGuideContent": "상품 수령 후 7일 이내"
    },
    "originAreaInfo": {
      "originAreaCode": "0200037",
      "importer": ""
    },
    "sellerCodeInfo": {
      "sellerManagementCode": "SELLER-001"
    },
    "optionInfo": {
      "optionCombinationGroupNames": {
        "optionGroupName1": "색상",
        "optionGroupName2": "사이즈"
      },
      "optionCombinations": [
        {
          "stockQuantity": 50,
          "price": 0,
          "usable": true,
          "optionName1": "블랙",
          "optionName2": "M"
        }
      ],
      "useStockManagement": true
    },
    "minorPurchasable": true,
    "productInfoProvidedNotice": {
      "productInfoProvidedNoticeType": "WEAR",
      "wear": {
        "material": "면 100%",
        "color": "블랙",
        "size": "S/M/L/XL",
        "manufacturer": "제조사",
        "caution": "세탁 시 주의",
        "warrantyPolicy": "상품 수령 후 7일",
        "afterServiceDirector": "02-1234-5678",
        "returnCostReason": "단순변심: 편도 2,500원",
        "noRefundReason": "상품 훼손 시 불가",
        "qualityAssuranceStandard": "관련법 준수",
        "compensationProcedure": "전화/온라인 접수",
        "troubleShootingContents": "A/S 문의"
      }
    },
    "taxType": "TAX"
  }
}
```

---

## 부록 B. 자주 사용하는 Enum 값 정리

### statusType (판매 상태)
| 코드 | 설명 | 등록 | 수정 |
|------|------|------|------|
| `WAIT` | 판매 대기 | - | - |
| `SALE` | 판매 중 | Y | Y |
| `OUTOFSTOCK` | 품절 | - | - (재고 0이면 자동) |
| `UNADMISSION` | 승인 대기 | - | - |
| `REJECTION` | 승인 거부 | - | - |
| `SUSPENSION` | 판매 중지 | - | Y |
| `CLOSE` | 판매 종료 | - | - |
| `PROHIBITION` | 판매 금지 | - | - |

### deliveryFeeType (배송비 타입)
| 코드 | 설명 |
|------|------|
| `FREE` | 무료 배송 |
| `CONDITIONAL_FREE` | 조건부 무료 |
| `PAID` | 유료 |
| `UNIT_QUANTITY_PAID` | 수량별 부과 |

### channelProductDisplayStatusType (전시 상태)
| 코드 | 설명 |
|------|------|
| `WAIT` | 전시 대기 |
| `ON` | 전시 중 |
| `SUSPENSION` | 전시 중지 |

### taxType (부가세 타입)
| 코드 | 설명 |
|------|------|
| `TAX` | 과세 |
| `DUTYFREE` | 면세 |
| `SMALL` | 영세 |
