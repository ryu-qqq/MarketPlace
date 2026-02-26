# InboundProduct 정형화 리팩토링 계획

## 배경

rawPayloadJson(비정형 JSON 문자열) 기반 인바운드 상품 수신 구조를 정형화된 필드 기반으로 전환.
소스별 파서 패턴(Strategy)을 제거하고, 내부 상품 등록 API와 동일한 데이터 구조로 통일.

## 핵심 설계 결정 (확정)

1. **rawPayloadJson 제거** → 이미지/옵션/상품/고시정보를 정형 필드로 수신
2. **인바운드 요청 = 내부 등록(RegisterProductGroupApiRequest)의 확장판**
   - 이미지/옵션/상품/상세설명: 내부 등록과 동일한 구조
   - 인바운드 전용 필드 추가: inboundSourceId, externalProductCode, externalBrandCode, externalCategoryCode
3. **고시정보: fieldCode 기반 매칭**
   - 크롤러: `noticeEntries: [{ fieldCode: "ORIGIN_COUNTRY", fieldValue: "이탈리아" }]` 전송
   - 우리: externalCategoryCode → 내부 카테고리 → CategoryGroup → NoticeCategory → fieldCode 매칭
   - 미제공 필드: `"상세설명 참고"` 기본값 채움
4. **originCountry 필드 삭제** — noticeEntries의 fieldCode로 대체
5. **SKU_PREFIX 삭제** — skuCode는 nullable로 전달
6. **regularPrice/currentPrice** — InboundProduct 레벨에 대표가격으로 유지
7. **shippingPolicyId/refundPolicyId** — 인바운드에서는 셀러 기본정책 자동 할당 (기존 로직 유지)

---

## 변경 대상 파일 목록

### Phase 1: 도메인 VO 생성 (domain)

#### 생성
- `domain/.../inboundproduct/vo/InboundProductPayload.java` — rawPayloadJson 대체 VO
  ```java
  public record InboundProductPayload(
      List<InboundImageData> images,
      List<InboundOptionGroupData> optionGroups,
      List<InboundProductData> products,
      List<InboundNoticeEntry> noticeEntries   // fieldCode 기반
  ) {
      public record InboundImageData(String imageType, String originUrl, int sortOrder) {}

      public record InboundOptionGroupData(
          String optionGroupName, String inputType,
          List<InboundOptionValueData> optionValues) {
          public record InboundOptionValueData(String optionValueName, int sortOrder) {}
      }

      public record InboundProductData(
          String skuCode,              // nullable
          int regularPrice, int currentPrice,
          int stockQuantity, int sortOrder,
          List<InboundSelectedOption> selectedOptions) {
          public record InboundSelectedOption(String optionGroupName, String optionValueName) {}
      }

      public record InboundNoticeEntry(String fieldCode, String fieldValue) {}
  }
  ```

#### 수정
- `domain/.../inboundproduct/aggregate/InboundProduct.java`
  - `String rawPayloadJson` → `InboundProductPayload payload`
  - `forNew()` 파라미터 변경
  - `reconstitute()` 파라미터 변경
  - `detectChanges()` — rawPayloadJson 비교 → payload 비교
  - `applyUpdate()` — rawPayloadJson → payload
- `domain/.../inboundproduct/vo/InboundProductUpdateData.java`
  - `String rawPayloadJson` → `InboundProductPayload payload`
- `domain/.../inboundproduct/vo/InboundProductDiff.java`
  - `rawPayloadChanged` → `payloadChanged`

### Phase 2: Application 레이어 (application)

#### 생성
- `application/.../inboundproduct/internal/InboundBundleAssembler.java` — 범용 번들 변환기
  - `toRegistrationBundle(InboundProduct)` → `ProductGroupRegistrationBundle`
  - `toUpdateBundle(InboundProduct)` → `Optional<ProductGroupUpdateBundle>`
  - 의존성: `ShippingPolicyReadManager`, `RefundPolicyReadManager`, `CategoryNoticeResolver`
  - 고시정보 로직:
    ```java
    private static final String DEFAULT_NOTICE_VALUE = "상세설명 참고";

    // NoticeCategory의 모든 필드 순회
    // 크롤러가 준 fieldCode 매칭 → 값 사용
    // 매칭 안 되면 → "상세설명 참고"
    noticeCategory.fields().stream()
        .map(field -> {
            String value = findByFieldCode(payload.noticeEntries(), field.fieldCodeValue())
                    .orElse(DEFAULT_NOTICE_VALUE);
            return new NoticeEntry(field.idValue(), value);
        }).toList();
    ```

#### 수정
- `application/.../inboundproduct/dto/command/ReceiveInboundProductCommand.java`
  - `String rawPayloadJson` → `InboundProductPayload payload`
- `application/.../inboundproduct/factory/InboundProductCommandFactory.java`
  - `create()`: rawPayloadJson → payload 전달
  - `toUpdateData()`: rawPayloadJson → payload 전달
- `application/.../inboundproduct/internal/InboundProductConversionCoordinator.java`
  - `InboundProductConversionFactory` → `InboundBundleAssembler` 직접 사용
  - sourceCode 조회 불필요 → `inboundSourceReadManager` 제거 가능
- `application/.../inboundproduct/internal/InboundProductUpdateCoordinator.java`
  - `InboundProductUpdateData` 변경에 따른 수정

#### 삭제 (7개)
- `application/.../inboundproduct/internal/MustitPayloadParser.java`
- `application/.../inboundproduct/internal/MustitPayloadResolver.java`
- `application/.../inboundproduct/internal/MustitBundleResolver.java`
- `application/.../inboundproduct/internal/InboundProductPayloadParser.java` (인터페이스)
- `application/.../inboundproduct/internal/InboundProductPayloadParserProvider.java`
- `application/.../inboundproduct/factory/InboundProductConversionFactory.java`
- `application/.../inboundproduct/dto/payload/MustitInboundPayload.java`

### Phase 3: Adapter-In (REST API)

#### 수정
- `adapter-in/.../inboundproduct/dto/command/ReceiveInboundProductApiRequest.java`
  - `String rawPayloadJson` 삭제
  - 정형 필드 추가: images, optionGroups, products, description, noticeEntries
  - 내부 등록(RegisterProductGroupApiRequest)와 동일한 하위 record 구조
  ```java
  public record ReceiveInboundProductApiRequest(
      long inboundSourceId,
      String externalProductCode,
      String productName,
      String externalBrandCode,
      String externalCategoryCode,
      long sellerId,
      int regularPrice,
      int currentPrice,
      String optionType,
      @Valid List<ImageRequest> images,
      @Valid List<OptionGroupRequest> optionGroups,
      @Valid List<ProductRequest> products,
      String descriptionHtml,
      @Valid List<NoticeEntryRequest> noticeEntries   // nullable
  ) {
      public record ImageRequest(String imageType, String originUrl, int sortOrder) {}
      public record OptionGroupRequest(String optionGroupName, String inputType,
                                        List<OptionValueRequest> optionValues) {
          public record OptionValueRequest(String optionValueName, int sortOrder) {}
      }
      public record ProductRequest(String skuCode, int regularPrice, int currentPrice,
                                    int stockQuantity, int sortOrder,
                                    List<SelectedOptionRequest> selectedOptions) {
          public record SelectedOptionRequest(String optionGroupName, String optionValueName) {}
      }
      public record NoticeEntryRequest(String fieldCode, String fieldValue) {}
  }
  ```
- `adapter-in/.../inboundproduct/mapper/InboundProductCommandApiMapper.java`
  - `toCommand()`: 정형 필드 → InboundProductPayload VO → ReceiveInboundProductCommand

### Phase 4: Adapter-Out (Persistence)

#### 수정
- InboundProduct JPA Entity — `rawPayloadJson` 컬럼을 유지하되 InboundProductPayload JSON 직렬화로 저장
  - 또는 `payload` 컬럼으로 rename (DB 마이그레이션 필요 시)
  - JPA AttributeConverter 또는 @Convert로 InboundProductPayload ↔ JSON 변환
- Entity Mapper — rawPayloadJson → payload 변환 로직 수정

### Phase 5: 정리

#### 삭제 가능 여부 검토
- `application/.../notice/resolver/CategoryNoticeResolver.java` — **유지** (InboundBundleAssembler에서 사용)
- `domain/.../inboundsource/vo/KnownInboundSourceCode.java` — 삭제 가능 (소스별 분기 없어짐)
  - 단, 다른 곳에서 참조하는지 확인 필요
- `application/.../inboundproduct/dto/payload/sample.txt` — 삭제 (untracked 파일)
- `domain/.../inboundproduct/exception/InboundPayloadInvalidException.java` — 검토 필요
  - rawPayloadJson 파싱 에러용이었으나 정형화 후에도 유효성 검증 실패 시 사용 가능

---

## 실행 순서

```
1. Phase 1: 도메인 VO (InboundProductPayload) + InboundProduct 수정
2. Phase 2: Application (Command, Factory, Assembler, Coordinator 수정 + 삭제)
3. Phase 3: Adapter-In (Request DTO + Mapper 수정)
4. Phase 4: Adapter-Out (JPA Entity + Mapper 수정)
5. Phase 5: 삭제 파일 정리 + 정적 분석 통과
```

## 변환 흐름 (After)

```
POST /api/admin/inbound-products
  → ReceiveInboundProductApiRequest (정형 필드)
  → InboundProductCommandApiMapper.toCommand()
    → ReceiveInboundProductCommand (with InboundProductPayload)
  → ReceiveInboundProductService.execute()
    → InboundProductRegisterCoordinator.register()
      → InboundProduct.forNew(..., payload)
      → 매핑 (externalBrandCode → brandId, externalCategoryCode → categoryId)
      → persist (payload를 JSON 컬럼으로 저장)

[5분 후 스케줄러]
  → ConvertPendingInboundProductsService
    → InboundProductConversionCoordinator.convert(product)
      → InboundBundleAssembler.toRegistrationBundle(product)
        → product.payload() (이미 정형화된 VO, 파싱 불필요)
        → images: 직접 매핑
        → options: 직접 매핑
        → products: 직접 매핑
        → description: product.descriptionHtml()
        → notice: CategoryNoticeResolver + fieldCode 매칭 + "상세설명 참고" 기본값
      → FullProductGroupRegistrationCoordinator.register(bundle)
      → product.markConverted(productGroupId)
```

## 고시정보 처리 상세

```java
// InboundBundleAssembler 내부
private NoticeRegistrationData resolveNotice(
        InboundProductPayload payload, long internalCategoryId) {

    return categoryNoticeResolver.resolve(internalCategoryId)
        .map(noticeCategory -> {
            List<NoticeEntry> entries = noticeCategory.fields().stream()
                .map(field -> {
                    String value = findByFieldCode(
                            payload.noticeEntries(), field.fieldCodeValue())
                        .orElse(DEFAULT_NOTICE_VALUE);
                    return new NoticeEntry(field.idValue(), value);
                })
                .toList();
            return new NoticeRegistrationData(noticeCategory.idValue(), entries);
        })
        .orElseGet(() -> new NoticeRegistrationData(0L, List.of()));
}

private Optional<String> findByFieldCode(
        List<InboundNoticeEntry> entries, String fieldCode) {
    if (entries == null) return Optional.empty();
    return entries.stream()
        .filter(e -> e.fieldCode().equals(fieldCode))
        .map(InboundNoticeEntry::fieldValue)
        .filter(v -> v != null && !v.isBlank())
        .findFirst();
}
```

## 참고: 내부 등록 Request 구조 (일치시킬 대상)

RegisterProductGroupApiRequest:
- sellerId, brandId, categoryId, shippingPolicyId, refundPolicyId
- productGroupName, optionType
- images: [{ imageType, originUrl, sortOrder }]
- optionGroups: [{ optionGroupName, canonicalOptionGroupId?, inputType?, optionValues }]
- products: [{ skuCode, regularPrice, currentPrice, stockQuantity, sortOrder, selectedOptions }]
- description: { content }
- notice: { noticeCategoryId, entries: [{ noticeFieldId, fieldValue }] }

## 변경량 추정

- 생성: 2개 (InboundProductPayload VO, InboundBundleAssembler)
- 수정: ~10개 (InboundProduct, Command, Factory, Coordinators, Request DTO, Mapper, JPA Entity 등)
- 삭제: 7개 (Mustit 관련 파서 전체 + ConversionFactory)
