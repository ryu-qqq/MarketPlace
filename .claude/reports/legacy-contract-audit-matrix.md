# Legacy API 계약 감사 매트릭스

> 생성일: 2026-02-23  
> 목적: setof-commerce 원본 vs MarketPlace legacy 패키지의 요청/응답/에러 계약 차이 분석

## 1. 공통 응답 래퍼 비교

### 성공 응답 구조

| 항목 | setof-commerce (원본) | MarketPlace (현재) | 차이/조치 |
|------|----------------------|-------------------|----------|
| 클래스명 | `ApiResponse<T>` | `LegacyApiResponse<T>` | ✅ 호환 |
| `data` 필드 | `T data` | `T data` | ✅ 동일 |
| `response` 필드 | `Response response` | `LegacyResponse response` | ✅ 호환 |
| `response.status` | `int status` | `int status` | ✅ 동일 |
| `response.message` | `String message` | `String message` | ✅ 동일 |
| success 메서드 | `ApiResponse.success(data)` → `{data: ..., response: {status: 200, message: "success"}}` | 동일 | ✅ 동일 |
| dataNotFound 메서드 | `ApiResponse.dataNotFoundWithErrorMessage(msg)` → `{data: null, response: {status: 404, message: msg}}` | 동일 | ✅ 동일 |

### 에러 응답 구조

| 항목 | setof-commerce (원본) | MarketPlace (현재) | 차이/조치 |
|------|----------------------|-------------------|----------|
| 클래스명 | `ErrorResponse extends Response` | `LegacyErrorResponse` (record) | ⚠️ 구조 확인 필요 |
| `status` 필드 | `int status` (부모에서 상속) | `int status` | ✅ 동일 |
| `message` 필드 | `String message` (부모에서 상속) | `String message` | ✅ 동일 |
| `error` 필드 | `String error` | `String error` | ✅ 동일 |
| `timestamp` 필드 | `String timestamp` (yyyy-MM-dd HH:mm:ss) | `String timestamp` (yyyy-MM-dd HH:mm:ss) | ✅ 동일 |

**결론**: 공통 응답 래퍼는 호환됨

---

## 2. 글로벌 예외 처리 비교

### 예외 핸들러 비교

| 예외 타입 | setof-commerce 처리 | MarketPlace 처리 | 차이/조치 |
|----------|--------------------|--------------------|----------|
| `MethodArgumentNotValidException` | `ErrorResponse` + 400 | `LegacyErrorResponse` + 400 | ⚠️ message 형식 차이: 원본은 defaultMessage만 조인, 현재는 동일 |
| `HttpMessageNotReadableException` | `ErrorResponse` + 400 + "요청 본문(JSON) 형식이 올바르지 않습니다." 상수 사용 | `LegacyErrorResponse` + 400 | ✅ 호환 |
| `IllegalArgumentException` | `ErrorResponse` + 400 + exceptionClassName | `LegacyErrorResponse` + 400 + "INVALID_ARGUMENT" | ⚠️ error 필드 차이 |
| `NullPointerException` | `ErrorResponse` + 400 | 미처리 (Exception 폴백) | ⚠️ 추가 필요 |
| `ApplicationException` (404) | `ApiResponse.dataNotFoundWithErrorMessage()` + 200 | `LegacyApiResponse.dataNotFoundWithErrorMessage()` + 200 | ✅ 동일 |
| `ApplicationException` (non-404) | `ErrorResponse` + status | `LegacyErrorResponse` + status (DomainException 매핑) | ✅ 호환 |
| `HasTitleApplicationException` | 별도 처리 | 미구현 | ⚠️ 필요 시 추가 |
| `UnExpectedException` | `ErrorResponse` + 500 | 미구현 | ⚠️ Exception 폴백으로 대체됨 |
| `Exception` (폴백) | `UnExpectedException`으로 래핑 후 처리 | `LegacyErrorResponse` + 500 | ✅ 호환 |

### 조치 필요 항목

1. **`IllegalArgumentException` error 필드**: 원본은 `IllegalArgumentException` (클래스명), 현재는 `INVALID_ARGUMENT`
   - **결정**: 외부 호환성 위해 원본 패턴 유지 (클래스명 사용)

2. **`NullPointerException` 핸들러**: 원본에 있으나 현재 없음
   - **결정**: 추가 필요

3. **Validation 메시지 포맷**: 
   - 원본: `fieldError.getDefaultMessage()` 조인
   - 현재: 동일
   - ✅ 호환됨

---

## 3. Product API 계약 비교

### 3.1 상품 등록 (POST /api/v1/product/group)

#### 요청 DTO 비교

| 필드 | setof-commerce `CreateProductGroup` | MarketPlace `LegacyCreateProductGroupRequest` | 차이/조치 |
|------|-------------------------------------|----------------------------------------------|----------|
| `productGroupId` | `Long` (nullable) | `long` | ⚠️ null 허용 필요 |
| `productGroupName` | `@Length(max=200)` | 검증 있음 | ✅ 호환 |
| `sellerId` | `@NotNull long` | `long` | ✅ 호환 |
| `optionType` | `@NotNull OptionType` enum | 구현 확인 필요 | ⚠️ 확인 필요 |
| `managementType` | `@NotNull ManagementType` enum | 구현 확인 필요 | ⚠️ 확인 필요 |
| `categoryId` | `@NotNull long` | `long` | ✅ 호환 |
| `brandId` | `@NotNull long` | `long` | ✅ 호환 |
| `productStatus` | `@Valid CreateProductStatus` | 구현됨 | ✅ 확인 |
| `price` | `@Valid @NotNull CreatePrice` | 구현됨 | ✅ 확인 |
| `productNotice` | `@Valid CreateProductNotice` | 구현됨 | ✅ 확인 |
| `clothesDetailInfo` | `@Valid CreateClothesDetail` | 구현됨 | ✅ 확인 |
| `deliveryNotice` | `@Valid CreateDeliveryNotice` | 구현됨 | ✅ 확인 |
| `refundNotice` | `@Valid CreateRefundNotice` | 구현됨 | ✅ 확인 |
| `productImageList` | `@Valid @Size(min=1, max=10)` | 구현됨 | ✅ 확인 |
| `detailDescription` | `@NotNull @Lob` | 구현됨 | ✅ 확인 |
| `productOptions` | `@Size(min=1) @Valid List<CreateOption>` | 구현됨 | ✅ 확인 |

#### 응답 DTO 비교

| 필드 | setof-commerce `CreateProductGroupResponse` | MarketPlace `LegacyCreateProductGroupResponse` | 차이/조치 |
|------|---------------------------------------------|-----------------------------------------------|----------|
| `productGroupId` | `long productGroupId` | `long productGroupId` | ✅ 동일 |
| `sellerId` | `long sellerId` | `long sellerId` | ✅ 동일 |
| `products` | `Set<ProductFetchResponse> products` | **❌ 누락** | 🔴 **추가 필요** |

**`ProductFetchResponse` 필드 (setof-commerce)**:
```java
- productGroupId (JsonIgnore)
- productId: long
- stockQuantity: int
- productStatus: ProductStatus
- option: String
- options: Set<OptionDto> (JsonInclude.NON_EMPTY)
- additionalPrice: BigDecimal
```

### 3.2 상품 조회 (GET /api/v1/product/group/{productGroupId})

| 항목 | setof-commerce | MarketPlace | 차이/조치 |
|------|---------------|-------------|----------|
| 응답 타입 | `ApiResponse<ProductGroupFetchResponse>` | `LegacyApiResponse<LegacyProductDetailApiResponse>` | ⚠️ 필드 매핑 확인 필요 |

### 3.3 기타 상품 API

| 엔드포인트 | 원본 응답 타입 | 현재 응답 타입 | 차이 |
|-----------|--------------|--------------|------|
| PUT /product/group/{id} | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PUT /product/group/{id}/notice | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PUT /product/group/{id}/images | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PUT /product/group/{id}/detailDescription | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PUT /product/group/{id}/option | `ApiResponse<Set<ProductFetchResponse>>` | `LegacyApiResponse<Long>` | 🔴 **응답 불일치** |
| PATCH /product/group/{id}/price | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PATCH /product/group/{id}/display-yn | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PATCH /product/group/{id}/out-stock | `ApiResponse<Set<ProductFetchResponse>>` | `LegacyApiResponse<Long>` | 🔴 **응답 불일치** |
| PATCH /product/group/{id}/stock | `ApiResponse<Set<ProductFetchResponse>>` | `LegacyApiResponse<Long>` | 🔴 **응답 불일치** |

---

## 4. Auth API 계약 비교

| 엔드포인트 | setof-commerce 응답 | MarketPlace 응답 | 상태 |
|-----------|--------------------|--------------------|------|
| POST /auth/authentication | `ApiResponse<AuthTokenResponse>` | `LegacyApiResponse<LegacyAuthTokenResponse>` | ⚠️ DTO 필드 확인 필요 |
| POST /auth | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| PUT /auth/{authId} | `ApiResponse<Long>` | `LegacyApiResponse<Long>` | ✅ 호환 |
| GET /auth | `ApiResponse<CustomPageable<AdministratorResponse>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| GET /auth/admin-validation | `ApiResponse<Boolean>` | `LegacyApiResponse<Boolean>` | ✅ 호환 |
| GET /auth/{sellerId} | `ApiResponse<CustomPageable<AdministratorResponse>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| PUT /auth/approval-status | `ApiResponse<List<Long>>` | `LegacyApiResponse<List<Long>>` | ✅ 호환 |

---

## 5. Brand API 계약 비교

| 엔드포인트 | setof-commerce 응답 | MarketPlace 응답 | 상태 |
|-----------|--------------------|--------------------|------|
| GET /brands | `ApiResponse<CustomPageable<ExtendedBrandContext>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| POST /brand/external/{siteId}/mapping | `ApiResponse<List<BrandMappingInfo>>` | `LegacyApiResponse<List<LegacyBrandMappingInfoRequest>>` | ✅ 호환 |

---

## 6. Category API 계약 비교

| 엔드포인트 | setof-commerce 응답 | MarketPlace 응답 | 상태 |
|-----------|--------------------|--------------------|------|
| GET /category/{categoryId} | `ApiResponse<List<TreeCategoryContext>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| GET /category/parent/{categoryId} | `ApiResponse<List<TreeCategoryContext>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| GET /category/parents | `ApiResponse<List<TreeCategoryContext>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| GET /category | `ApiResponse<List<TreeCategoryContext>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| GET /category/page | `ApiResponse<CustomPageable<ProductCategoryContext>>` | `LegacyApiResponse<Object>` | ⚠️ 타입 지정 필요 |
| POST /category/external/{siteId}/mapping | `ApiResponse<List<CategoryMappingInfo>>` | `LegacyApiResponse<List<LegacyCategoryMappingInfoRequest>>` | ✅ 호환 |

---

## 7. 우선순위별 수정 필요 항목

### 🔴 Critical (즉시 수정 필요)

1. **`LegacyCreateProductGroupResponse`에 `products` 필드 추가**
   - 원본: `Set<ProductFetchResponse> products` 포함
   - 현재: 누락됨
   - 외부 시스템이 이 필드를 기대할 경우 오류 발생 가능

2. **PUT /product/group/{id}/option 응답 타입 변경**
   - 원본: `Set<ProductFetchResponse>`
   - 현재: `Long`

3. **PATCH /product/group/{id}/out-stock 응답 타입 변경**
   - 원본: `Set<ProductFetchResponse>`
   - 현재: `Long`

4. **PATCH /product/group/{id}/stock 응답 타입 변경**
   - 원본: `Set<ProductFetchResponse>`
   - 현재: `Long`

### 🟠 High (권장 수정)

5. **`IllegalArgumentException` 핸들러 error 필드 수정**
   - 원본: 예외 클래스명 (`IllegalArgumentException`)
   - 현재: `INVALID_ARGUMENT`

6. **`NullPointerException` 핸들러 추가**
   - 원본에 존재하나 현재 없음

### 🟡 Medium (점진적 개선)

7. **Auth/Brand/Category API의 응답 타입 구체화**
   - `LegacyApiResponse<Object>` → 구체적 타입으로 변경
   - 미구현 상태이므로 구현 시 적용

---

## 8. 수정 계획

### Phase 1: Product API 응답 정합화 (Critical)

1. `LegacyCreateProductGroupResponse` 수정
   - `products` 필드 추가: `Set<LegacyProductFetchResponse>`
   - `LegacyProductFetchResponse` DTO 생성

2. `LegacyProductCommandController` 응답 타입 수정
   - `updateProductOption`: `Long` → `Set<LegacyProductFetchResponse>`
   - `outOfStock`: `Long` → `Set<LegacyProductFetchResponse>`
   - `updateGroupStock`: `Long` → `Set<LegacyProductFetchResponse>`

### Phase 2: 예외 핸들러 정합화 (High)

1. `LegacyGlobalExceptionHandler` 수정
   - `IllegalArgumentException`: error 필드에 클래스명 사용
   - `NullPointerException` 핸들러 추가

### Phase 3: 나머지 API 구체화 (구현 시)

- Auth/Brand/Category/Order API 구현 시 원본 응답 타입 반영
