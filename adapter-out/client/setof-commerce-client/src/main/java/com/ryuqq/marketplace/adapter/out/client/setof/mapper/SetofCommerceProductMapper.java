package com.ryuqq.marketplace.adapter.out.client.setof.mapper;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.DescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.ImageRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.NoticeEntryRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.NoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.OptionGroupRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.OptionValueRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.ProductRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest.SelectedOptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDetailBundle → Setof Commerce 요청 DTO 변환 매퍼.
 *
 * <p>세토프 커머스는 마켓플레이스와 스키마가 거의 동일하므로 변환이 단순합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceProductMapper {

    /**
     * 상품 등록 요청 변환.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @return 세토프 상품 등록 요청 DTO
     */
    public SetofProductGroupRegistrationRequest toRegistrationRequest(
            ProductGroupDetailBundle bundle, Long externalCategoryId, Long externalBrandId) {

        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();
        ProductGroup group = bundle.group();

        return new SetofProductGroupRegistrationRequest(
                queryResult.sellerId(),
                externalBrandId,
                externalCategoryId,
                queryResult.shippingPolicy() != null
                        ? queryResult.shippingPolicy().policyId()
                        : null,
                queryResult.refundPolicy() != null ? queryResult.refundPolicy().policyId() : null,
                queryResult.productGroupName(),
                queryResult.optionType(),
                mapImages(group.images()),
                mapOptionGroups(group.sellerOptionGroups()),
                mapProducts(bundle.products(), group.sellerOptionGroups()),
                mapDescription(bundle),
                mapNotice(bundle));
    }

    /**
     * 상품 수정 요청 변환.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @return 세토프 상품 수정 요청 DTO
     */
    public SetofProductGroupUpdateRequest toUpdateRequest(
            ProductGroupDetailBundle bundle, Long externalCategoryId, Long externalBrandId) {

        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();
        ProductGroup group = bundle.group();

        return new SetofProductGroupUpdateRequest(
                queryResult.productGroupName(),
                externalBrandId,
                externalCategoryId,
                queryResult.shippingPolicy() != null
                        ? queryResult.shippingPolicy().policyId()
                        : null,
                queryResult.refundPolicy() != null ? queryResult.refundPolicy().policyId() : null,
                queryResult.optionType(),
                null,
                mapUpdateImages(group.images()),
                mapUpdateOptionGroups(group.sellerOptionGroups()),
                mapUpdateProducts(bundle.products(), group.sellerOptionGroups()),
                mapUpdateDescription(bundle),
                mapUpdateNotice(bundle));
    }

    /**
     * 상품 삭제(판매중지) 요청 변환.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @return status=DISCONTINUED인 수정 요청 DTO
     */
    public SetofProductGroupUpdateRequest toDeleteRequest(
            ProductGroupDetailBundle bundle, Long externalCategoryId, Long externalBrandId) {

        SetofProductGroupUpdateRequest updateRequest =
                toUpdateRequest(bundle, externalCategoryId, externalBrandId);

        return new SetofProductGroupUpdateRequest(
                updateRequest.productGroupName(),
                updateRequest.brandId(),
                updateRequest.categoryId(),
                updateRequest.shippingPolicyId(),
                updateRequest.refundPolicyId(),
                updateRequest.optionType(),
                "DISCONTINUED",
                updateRequest.images(),
                updateRequest.optionGroups(),
                updateRequest.products(),
                updateRequest.description(),
                updateRequest.notice());
    }

    private List<ImageRequest> mapImages(List<ProductGroupImage> groupImages) {
        return groupImages.stream()
                .map(
                        image ->
                                new ImageRequest(
                                        image.imageTypeName(),
                                        resolveImageUrl(image),
                                        image.sortOrder()))
                .toList();
    }

    private List<SetofProductGroupUpdateRequest.ImageRequest> mapUpdateImages(
            List<ProductGroupImage> groupImages) {
        return groupImages.stream()
                .map(
                        image ->
                                new SetofProductGroupUpdateRequest.ImageRequest(
                                        image.imageTypeName(),
                                        resolveImageUrl(image),
                                        image.sortOrder()))
                .toList();
    }

    private String resolveImageUrl(ProductGroupImage image) {
        String uploaded = image.uploadedUrlValue();
        return uploaded != null ? uploaded : image.originUrlValue();
    }

    private List<OptionGroupRequest> mapOptionGroups(List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .map(
                        og ->
                                new OptionGroupRequest(
                                        og.optionGroupNameValue(),
                                        og.canonicalOptionGroupId() != null
                                                ? og.canonicalOptionGroupId().value()
                                                : null,
                                        og.inputType() != null ? og.inputType().name() : null,
                                        og.optionValues().stream()
                                                .map(
                                                        ov ->
                                                                new OptionValueRequest(
                                                                        ov.optionValueNameValue(),
                                                                        ov.canonicalOptionValueId()
                                                                                        != null
                                                                                ? ov.canonicalOptionValueId()
                                                                                        .value()
                                                                                : null,
                                                                        ov.sortOrder()))
                                                .toList()))
                .toList();
    }

    private List<SetofProductGroupUpdateRequest.OptionGroupRequest> mapUpdateOptionGroups(
            List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .map(
                        og ->
                                new SetofProductGroupUpdateRequest.OptionGroupRequest(
                                        og.idValue(),
                                        og.optionGroupNameValue(),
                                        og.canonicalOptionGroupId() != null
                                                ? og.canonicalOptionGroupId().value()
                                                : null,
                                        og.inputType() != null ? og.inputType().name() : null,
                                        og.optionValues().stream()
                                                .map(
                                                        ov ->
                                                                new SetofProductGroupUpdateRequest
                                                                        .OptionValueRequest(
                                                                        ov.idValue(),
                                                                        ov.optionValueNameValue(),
                                                                        ov.canonicalOptionValueId()
                                                                                        != null
                                                                                ? ov.canonicalOptionValueId()
                                                                                        .value()
                                                                                : null,
                                                                        ov.sortOrder()))
                                                .toList()))
                .toList();
    }

    private List<ProductRequest> mapProducts(
            List<Product> products, List<SellerOptionGroup> optionGroups) {

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(optionGroups);

        return products.stream()
                .map(
                        product ->
                                new ProductRequest(
                                        product.skuCodeValue(),
                                        product.regularPriceValue(),
                                        product.currentPriceValue(),
                                        product.stockQuantity(),
                                        product.sortOrder(),
                                        mapSelectedOptions(product, optionGroups, optionValueMap)))
                .toList();
    }

    private List<SetofProductGroupUpdateRequest.ProductRequest> mapUpdateProducts(
            List<Product> products, List<SellerOptionGroup> optionGroups) {

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(optionGroups);

        return products.stream()
                .map(
                        product ->
                                new SetofProductGroupUpdateRequest.ProductRequest(
                                        product.idValue(),
                                        product.skuCodeValue(),
                                        product.regularPriceValue(),
                                        product.currentPriceValue(),
                                        product.stockQuantity(),
                                        product.sortOrder(),
                                        mapUpdateSelectedOptions(
                                                product, optionGroups, optionValueMap)))
                .toList();
    }

    private List<SelectedOptionRequest> mapSelectedOptions(
            Product product,
            List<SellerOptionGroup> optionGroups,
            Map<Long, SellerOptionValue> optionValueMap) {

        Map<Long, String> mappingByGroupId = buildMappingByGroupId(product, optionValueMap);

        List<SelectedOptionRequest> selectedOptions = new ArrayList<>();
        for (SellerOptionGroup group : optionGroups) {
            String valueName = mappingByGroupId.get(group.idValue());
            if (valueName != null) {
                selectedOptions.add(
                        new SelectedOptionRequest(group.optionGroupNameValue(), valueName));
            }
        }
        return selectedOptions;
    }

    private List<SetofProductGroupUpdateRequest.SelectedOptionRequest> mapUpdateSelectedOptions(
            Product product,
            List<SellerOptionGroup> optionGroups,
            Map<Long, SellerOptionValue> optionValueMap) {

        Map<Long, String> mappingByGroupId = buildMappingByGroupId(product, optionValueMap);

        List<SetofProductGroupUpdateRequest.SelectedOptionRequest> selectedOptions =
                new ArrayList<>();
        for (SellerOptionGroup group : optionGroups) {
            String valueName = mappingByGroupId.get(group.idValue());
            if (valueName != null) {
                selectedOptions.add(
                        new SetofProductGroupUpdateRequest.SelectedOptionRequest(
                                group.optionGroupNameValue(), valueName));
            }
        }
        return selectedOptions;
    }

    private Map<Long, String> buildMappingByGroupId(
            Product product, Map<Long, SellerOptionValue> optionValueMap) {
        Map<Long, String> mappingByGroupId = new HashMap<>();
        for (ProductOptionMapping mapping : product.optionMappings()) {
            SellerOptionValue value = optionValueMap.get(mapping.sellerOptionValueIdValue());
            if (value != null) {
                mappingByGroupId.put(
                        value.sellerOptionGroupIdValue(), value.optionValueNameValue());
            }
        }
        return mappingByGroupId;
    }

    private Map<Long, SellerOptionValue> buildOptionValueMap(List<SellerOptionGroup> optionGroups) {
        Map<Long, SellerOptionValue> map = new HashMap<>();
        for (SellerOptionGroup group : optionGroups) {
            for (SellerOptionValue value : group.optionValues()) {
                map.put(value.idValue(), value);
            }
        }
        return map;
    }

    private DescriptionRequest mapDescription(ProductGroupDetailBundle bundle) {
        return bundle.description()
                .map(desc -> new DescriptionRequest(desc.contentValue()))
                .orElse(new DescriptionRequest(""));
    }

    private SetofProductGroupUpdateRequest.DescriptionRequest mapUpdateDescription(
            ProductGroupDetailBundle bundle) {
        return bundle.description()
                .map(
                        desc ->
                                new SetofProductGroupUpdateRequest.DescriptionRequest(
                                        desc.contentValue()))
                .orElse(new SetofProductGroupUpdateRequest.DescriptionRequest(""));
    }

    private NoticeRequest mapNotice(ProductGroupDetailBundle bundle) {
        return bundle.notice().map(this::convertNotice).orElse(null);
    }

    private SetofProductGroupUpdateRequest.NoticeRequest mapUpdateNotice(
            ProductGroupDetailBundle bundle) {
        return bundle.notice().map(this::convertUpdateNotice).orElse(null);
    }

    private NoticeRequest convertNotice(ProductNotice notice) {
        List<ProductNoticeEntry> entries = notice.entries();
        if (entries.isEmpty()) {
            return null;
        }

        List<NoticeEntryRequest> entryRequests =
                entries.stream()
                        .map(
                                entry ->
                                        new NoticeEntryRequest(
                                                entry.noticeFieldIdValue(),
                                                entry.fieldValueValue()))
                        .toList();

        return new NoticeRequest(notice.noticeCategoryIdValue(), entryRequests);
    }

    private SetofProductGroupUpdateRequest.NoticeRequest convertUpdateNotice(ProductNotice notice) {
        List<ProductNoticeEntry> entries = notice.entries();
        if (entries.isEmpty()) {
            return null;
        }

        List<SetofProductGroupUpdateRequest.NoticeEntryRequest> entryRequests =
                entries.stream()
                        .map(
                                entry ->
                                        new SetofProductGroupUpdateRequest.NoticeEntryRequest(
                                                entry.noticeFieldIdValue(),
                                                entry.fieldValueValue()))
                        .toList();

        return new SetofProductGroupUpdateRequest.NoticeRequest(
                notice.noticeCategoryIdValue(), entryRequests);
    }
}
