package com.ryuqq.marketplace.application.productgroup.dto.response;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerCsSyncResult;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 상품 그룹 외부 채널 동기화용 DTO.
 *
 * <p>Port-Out을 통해 adapter-out으로 전달되는 데이터입니다. Domain Aggregate 대신 Application DTO만 포함합니다.
 *
 * @param queryResult 기본 정보 + 정책 (Composition 쿼리 결과)
 * @param images 이미지 목록
 * @param optionGroups 옵션 그룹 목록
 * @param status 상품 그룹 상태
 * @param soldout 품절 여부
 * @param products 상품(SKU) 목록
 * @param descriptionContent 상세설명 HTML 콘텐츠
 * @param notice 고시정보
 * @param noticeCategory 고시정보 카테고리 (필드명 매핑용)
 * @param sellerCs 셀러 CS 연락처
 * @param variantsByImageId 이미지 ID별 Variant 목록
 */
public record ProductGroupSyncData(
        ProductGroupDetailCompositeQueryResult queryResult,
        List<ProductGroupImageResult> images,
        List<SellerOptionGroupResult> optionGroups,
        String status,
        boolean soldout,
        List<ProductResult> products,
        Optional<String> descriptionContent,
        Optional<ProductNoticeResult> notice,
        Optional<NoticeCategoryResult> noticeCategory,
        Optional<SellerCsSyncResult> sellerCs,
        Map<Long, List<ImageVariantResult>> variantsByImageId) {

    public ProductGroupSyncData {
        images = images != null ? List.copyOf(images) : List.of();
        optionGroups = optionGroups != null ? List.copyOf(optionGroups) : List.of();
        products = products != null ? List.copyOf(products) : List.of();
        variantsByImageId = variantsByImageId != null ? Map.copyOf(variantsByImageId) : Map.of();
    }

    /**
     * Bundle에서 SyncData로 변환합니다.
     *
     * @param bundle 내부 조립용 번들 (Domain Aggregate 포함)
     * @return 외부 전달용 SyncData (Application DTO만 포함)
     */
    public static ProductGroupSyncData from(ProductGroupDetailBundle bundle) {
        List<ProductGroupImageResult> imageResults =
                bundle.group().images().stream()
                        .map(
                                image -> {
                                    ProductGroupImageResult base =
                                            ProductGroupImageResult.from(image);
                                    Long imageId = image.idValue();
                                    List<ImageVariantResult> variants =
                                            imageId != null
                                                    ? bundle.variantsByImageId()
                                                            .getOrDefault(imageId, List.of())
                                                    : List.of();
                                    return variants.isEmpty() ? base : base.withVariants(variants);
                                })
                        .toList();

        List<SellerOptionGroupResult> optionGroupResults =
                bundle.group().sellerOptionGroups().stream()
                        .map(SellerOptionGroupResult::from)
                        .toList();

        List<ProductResult> productResults =
                bundle.products().stream().map(ProductResult::from).toList();

        Optional<ProductNoticeResult> noticeResult = bundle.notice().map(ProductNoticeResult::from);

        Optional<NoticeCategoryResult> noticeCategoryResult =
                bundle.noticeCategory()
                        .map(
                                nc ->
                                        new NoticeCategoryResult(
                                                nc.idValue(),
                                                nc.codeValue(),
                                                nc.nameKo(),
                                                nc.nameEn(),
                                                nc.targetCategoryGroup().name(),
                                                nc.isActive(),
                                                nc.fields().stream()
                                                        .map(
                                                                f ->
                                                                        new NoticeFieldResult(
                                                                                f.idValue(),
                                                                                f.fieldCodeValue(),
                                                                                f.fieldNameValue(),
                                                                                f.isRequired(),
                                                                                f.sortOrder()))
                                                        .toList(),
                                                nc.createdAt()));

        Optional<SellerCsSyncResult> sellerCsResult =
                bundle.sellerCs().map(SellerCsSyncResult::from);

        return new ProductGroupSyncData(
                bundle.queryResult(),
                imageResults,
                optionGroupResults,
                bundle.group().status().name(),
                bundle.group().status().isSoldout(),
                productResults,
                bundle.description().map(desc -> desc.contentValue()),
                noticeResult,
                noticeCategoryResult,
                sellerCsResult,
                bundle.variantsByImageId());
    }
}
