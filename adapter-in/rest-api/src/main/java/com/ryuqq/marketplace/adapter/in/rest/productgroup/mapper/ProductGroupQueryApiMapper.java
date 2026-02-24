package com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.query.SearchProductGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.DescriptionImageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.OptionGroupSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductExcelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupDescriptionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupExcelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupImageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductNoticeApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductNoticeEntryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductOptionMappingApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductOptionMatrixApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ResolvedProductOptionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.SellerOptionGroupApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.SellerOptionValueApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.NonReturnableConditionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.response.RefundPolicyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.response.ShippingPolicyApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.product.dto.response.ProductDetailResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductOptionMappingResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.product.dto.response.ResolvedProductOptionResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.OptionGroupSummaryResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductOptionMatrixResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionGroupResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionValueResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.DescriptionImageResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.response.ProductGroupDescriptionResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import com.ryuqq.marketplace.application.refundpolicy.dto.response.RefundPolicyResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductGroupQueryApiMapper - 상품 그룹 Query API 변환 매퍼.
 *
 * <p>API-MAP-001: Mapper는 @Component로 등록.
 *
 * <p>API-MAP-003: Application Result -> API Response 변환.
 *
 * <p>API-MAP-005: 순수 변환 로직만.
 *
 * <p>API-DTO-005: 날짜 String 변환 필수.
 */
@Component
public class ProductGroupQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public ProductGroupSearchParams toSearchParams(SearchProductGroupsApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null,
                        request.startDate(),
                        request.endDate(),
                        request.sortKey(),
                        request.sortDirection(),
                        page,
                        size);

        return ProductGroupSearchParams.of(
                request.statuses(),
                request.sellerIds(),
                request.brandIds(),
                request.categoryIds(),
                request.productGroupIds(),
                request.searchField(),
                request.searchWord(),
                searchParams);
    }

    // ==================== 목록 변환 ====================

    public PageApiResponse<ProductGroupListApiResponse> toPageResponse(
            ProductGroupPageResult pageResult) {
        List<ProductGroupListApiResponse> responses = toListResponses(pageResult.results());
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    private List<ProductGroupListApiResponse> toListResponses(
            List<ProductGroupListCompositeResult> results) {
        return results.stream().map(this::toListResponse).toList();
    }

    private ProductGroupListApiResponse toListResponse(ProductGroupListCompositeResult result) {
        List<OptionGroupSummaryApiResponse> optionGroups =
                result.optionGroups().stream().map(this::toOptionGroupSummaryResponse).toList();

        return new ProductGroupListApiResponse(
                result.id(),
                result.sellerId(),
                result.sellerName(),
                result.brandId(),
                result.brandName(),
                result.categoryId(),
                result.categoryName(),
                result.categoryDisplayPath(),
                result.categoryIdPath(),
                result.categoryDepth(),
                result.department(),
                result.categoryGroup(),
                result.productGroupName(),
                result.optionType(),
                result.status(),
                result.thumbnailUrl(),
                result.productCount(),
                result.minPrice(),
                result.maxPrice(),
                result.maxDiscountRate(),
                optionGroups,
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    private OptionGroupSummaryApiResponse toOptionGroupSummaryResponse(
            OptionGroupSummaryResult result) {
        return new OptionGroupSummaryApiResponse(
                result.optionGroupName(), result.optionValueNames());
    }

    // ==================== 엑셀 변환 ====================

    public List<ProductGroupExcelApiResponse> toExcelResponses(
            List<ProductGroupExcelCompositeResult> results) {
        return results.stream().map(this::toExcelResponse).toList();
    }

    private ProductGroupExcelApiResponse toExcelResponse(ProductGroupExcelCompositeResult result) {
        ProductGroupListCompositeResult base = result.base();

        List<OptionGroupSummaryApiResponse> optionGroups =
                base.optionGroups().stream().map(this::toOptionGroupSummaryResponse).toList();

        List<ProductGroupImageApiResponse> images =
                result.images().stream().map(this::toImageResponse).toList();

        List<ProductExcelApiResponse> products =
                result.products().stream().map(this::toProductExcelResponse).toList();

        ProductNoticeApiResponse notice =
                result.notice() != null ? toNoticeResponse(result.notice()) : null;

        return new ProductGroupExcelApiResponse(
                base.id(),
                base.sellerId(),
                base.sellerName(),
                base.brandId(),
                base.brandName(),
                base.categoryId(),
                base.categoryName(),
                base.categoryDisplayPath(),
                base.categoryIdPath(),
                base.categoryDepth(),
                base.department(),
                base.categoryGroup(),
                base.productGroupName(),
                base.optionType(),
                base.status(),
                base.thumbnailUrl(),
                base.productCount(),
                base.minPrice(),
                base.maxPrice(),
                base.maxDiscountRate(),
                optionGroups,
                DateTimeFormatUtils.formatIso8601(base.createdAt()),
                DateTimeFormatUtils.formatIso8601(base.updatedAt()),
                images,
                products,
                result.descriptionCdnUrl(),
                notice);
    }

    private ProductExcelApiResponse toProductExcelResponse(ProductResult result) {
        List<ProductOptionMappingApiResponse> mappings =
                result.optionMappings().stream().map(this::toOptionMappingResponse).toList();

        return new ProductExcelApiResponse(
                result.id(),
                result.productGroupId(),
                result.skuCode(),
                result.regularPrice(),
                result.currentPrice(),
                result.salePrice(),
                result.discountRate(),
                result.stockQuantity(),
                result.status(),
                result.sortOrder(),
                mappings,
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    private ProductOptionMappingApiResponse toOptionMappingResponse(
            ProductOptionMappingResult result) {
        return new ProductOptionMappingApiResponse(
                result.id(), result.productId(), result.sellerOptionValueId());
    }

    // ==================== 상세 변환 ====================

    public ProductGroupDetailApiResponse toDetailResponse(
            ProductGroupDetailCompositeResult result) {
        List<ProductGroupImageApiResponse> images =
                result.images().stream().map(this::toImageResponse).toList();

        ProductOptionMatrixApiResponse matrix =
                toOptionMatrixResponse(result.optionProductMatrix());

        ShippingPolicyApiResponse shippingPolicy =
                result.shippingPolicy() != null
                        ? toShippingPolicyResponse(result.shippingPolicy())
                        : null;

        RefundPolicyApiResponse refundPolicy =
                result.refundPolicy() != null
                        ? toRefundPolicyResponse(result.refundPolicy())
                        : null;

        ProductGroupDescriptionApiResponse description =
                result.description() != null ? toDescriptionResponse(result.description()) : null;

        ProductNoticeApiResponse notice =
                result.productNotice() != null ? toNoticeResponse(result.productNotice()) : null;

        return new ProductGroupDetailApiResponse(
                result.id(),
                result.sellerId(),
                result.sellerName(),
                result.brandId(),
                result.brandName(),
                result.categoryId(),
                result.categoryName(),
                result.categoryDisplayPath(),
                result.categoryIdPath(),
                result.productGroupName(),
                result.optionType(),
                result.status(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()),
                images,
                matrix,
                shippingPolicy,
                refundPolicy,
                description,
                notice);
    }

    private ProductGroupImageApiResponse toImageResponse(ProductGroupImageResult result) {
        return new ProductGroupImageApiResponse(
                result.id(),
                result.originUrl(),
                result.uploadedUrl(),
                result.imageType(),
                result.sortOrder());
    }

    private ProductOptionMatrixApiResponse toOptionMatrixResponse(
            ProductOptionMatrixResult result) {
        List<SellerOptionGroupApiResponse> optionGroups =
                result.optionGroups().stream().map(this::toOptionGroupResponse).toList();
        List<ProductDetailApiResponse> products =
                result.products().stream().map(this::toProductDetailResponse).toList();
        return new ProductOptionMatrixApiResponse(optionGroups, products);
    }

    private SellerOptionGroupApiResponse toOptionGroupResponse(SellerOptionGroupResult result) {
        List<SellerOptionValueApiResponse> values =
                result.optionValues().stream().map(this::toOptionValueResponse).toList();
        return new SellerOptionGroupApiResponse(
                result.id(),
                result.optionGroupName(),
                result.canonicalOptionGroupId(),
                result.inputType(),
                result.sortOrder(),
                values);
    }

    private SellerOptionValueApiResponse toOptionValueResponse(SellerOptionValueResult result) {
        return new SellerOptionValueApiResponse(
                result.id(),
                result.sellerOptionGroupId(),
                result.optionValueName(),
                result.canonicalOptionValueId(),
                result.sortOrder());
    }

    private ProductDetailApiResponse toProductDetailResponse(ProductDetailResult result) {
        List<ResolvedProductOptionApiResponse> options =
                result.options().stream().map(this::toResolvedOptionResponse).toList();
        return new ProductDetailApiResponse(
                result.id(),
                result.skuCode(),
                result.regularPrice(),
                result.currentPrice(),
                result.discountRate(),
                result.stockQuantity(),
                result.status(),
                result.sortOrder(),
                options,
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    private ResolvedProductOptionApiResponse toResolvedOptionResponse(
            ResolvedProductOptionResult result) {
        return new ResolvedProductOptionApiResponse(
                result.sellerOptionGroupId(),
                result.optionGroupName(),
                result.sellerOptionValueId(),
                result.optionValueName());
    }

    private ShippingPolicyApiResponse toShippingPolicyResponse(ShippingPolicyResult result) {
        return new ShippingPolicyApiResponse(
                result.policyId(),
                result.sellerId(),
                result.policyName(),
                result.defaultPolicy(),
                result.active(),
                result.shippingFeeType(),
                result.shippingFeeTypeDisplayName(),
                result.baseFee(),
                result.freeThreshold(),
                result.jejuExtraFee(),
                result.islandExtraFee(),
                result.returnFee(),
                result.exchangeFee(),
                result.leadTimeMinDays(),
                result.leadTimeMaxDays(),
                DateTimeFormatUtils.formatTime(result.leadTimeCutoffTime()),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    private RefundPolicyApiResponse toRefundPolicyResponse(RefundPolicyResult result) {
        List<NonReturnableConditionApiResponse> conditions =
                result.nonReturnableConditions() != null
                        ? result.nonReturnableConditions().stream()
                                .map(
                                        c ->
                                                new NonReturnableConditionApiResponse(
                                                        c.code(), c.displayName()))
                                .toList()
                        : List.of();

        return new RefundPolicyApiResponse(
                result.policyId(),
                result.sellerId(),
                result.policyName(),
                result.defaultPolicy(),
                result.active(),
                result.returnPeriodDays(),
                result.exchangePeriodDays(),
                conditions,
                result.partialRefundEnabled(),
                result.inspectionRequired(),
                result.inspectionPeriodDays(),
                result.additionalInfo(),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    private ProductGroupDescriptionApiResponse toDescriptionResponse(
            ProductGroupDescriptionResult result) {
        List<DescriptionImageApiResponse> images =
                result.images().stream().map(this::toDescriptionImageResponse).toList();
        return new ProductGroupDescriptionApiResponse(
                result.id(), result.content(), result.cdnPath(), images);
    }

    private DescriptionImageApiResponse toDescriptionImageResponse(DescriptionImageResult result) {
        return new DescriptionImageApiResponse(
                result.id(), result.originUrl(), result.uploadedUrl(), result.sortOrder());
    }

    private ProductNoticeApiResponse toNoticeResponse(ProductNoticeResult result) {
        List<ProductNoticeEntryApiResponse> entries =
                result.entries().stream().map(this::toNoticeEntryResponse).toList();
        return new ProductNoticeApiResponse(
                result.id(),
                result.noticeCategoryId(),
                entries,
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    private ProductNoticeEntryApiResponse toNoticeEntryResponse(ProductNoticeEntryResult result) {
        return new ProductNoticeEntryApiResponse(
                result.id(), result.noticeFieldId(), result.fieldValue());
    }
}
