package com.ryuqq.marketplace.adapter.out.client.setof.mapper;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
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
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
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
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ProductGroupDetailBundle → Setof Commerce 요청 DTO 변환 매퍼.
 *
 * <p>세토프 커머스 v2 Admin API 스펙에 맞춰 변환합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
@SuppressWarnings("PMD.GodClass")
public class SetofCommerceProductMapper {

    /**
     * 상품 등록 요청 변환.
     *
     * <p>등록 시 productGroupId, productId를 보내지 않습니다. 세토프 서버에서 auto_increment로 ID를 생성합니다.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @param externalSellerId 세토프 셀러 ID (shop.accountId)
     * @return 세토프 상품 등록 요청 DTO
     */
    public SetofProductGroupRegistrationRequest toRegistrationRequest(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            long externalSellerId) {

        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();
        ProductGroup group = bundle.group();
        List<Product> products = bundle.products();

        int representativeRegularPrice = computeMinRegularPrice(products);
        int representativeCurrentPrice = computeMinCurrentPrice(products);

        return new SetofProductGroupRegistrationRequest(
                null,
                externalSellerId,
                externalBrandId,
                externalCategoryId,
                queryResult.shippingPolicy() != null
                        ? queryResult.shippingPolicy().policyId()
                        : null,
                queryResult.refundPolicy() != null ? queryResult.refundPolicy().policyId() : null,
                queryResult.productGroupName(),
                queryResult.optionType(),
                representativeRegularPrice,
                representativeCurrentPrice,
                mapImages(group.images()),
                mapOptionGroups(group.sellerOptionGroups()),
                mapProducts(products, group.sellerOptionGroups()),
                mapDescription(bundle),
                mapNotice(bundle));
    }

    /**
     * 상품 수정 요청 변환.
     *
     * <p>기존 세토프 상품 조회 결과를 기반으로 옵션명 매칭하여 productId를 할당합니다.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @param existingProduct 기존 세토프 상품 조회 결과 (nullable)
     * @return 세토프 상품 수정 요청 DTO
     */
    public SetofProductGroupUpdateRequest toUpdateRequest(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SetofProductGroupDetailResponse existingProduct) {

        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();
        ProductGroup group = bundle.group();
        List<Product> products = bundle.products();

        int representativeRegularPrice = computeMinRegularPrice(products);
        int representativeCurrentPrice = computeMinCurrentPrice(products);

        return new SetofProductGroupUpdateRequest(
                queryResult.productGroupName(),
                externalBrandId,
                externalCategoryId,
                queryResult.shippingPolicy() != null
                        ? queryResult.shippingPolicy().policyId()
                        : null,
                queryResult.refundPolicy() != null ? queryResult.refundPolicy().policyId() : null,
                queryResult.optionType(),
                representativeRegularPrice,
                representativeCurrentPrice,
                mapUpdateImages(group.images()),
                mapUpdateOptionGroups(group.sellerOptionGroups()),
                mapUpdateProducts(products, group.sellerOptionGroups(), existingProduct),
                mapUpdateDescription(bundle),
                mapUpdateNotice(bundle));
    }

    /**
     * 상품 삭제(판매중지) 요청 변환.
     *
     * <p>status만 DISCONTINUED로 설정하고 나머지는 null로 처리합니다.
     *
     * @return status=DISCONTINUED인 최소 수정 요청 DTO
     */
    public SetofProductGroupUpdateRequest toDeleteRequest() {
        return new SetofProductGroupUpdateRequest(
                null, null, null, null, null, null, 0, 0, null, null, null, null, null);
    }

    /**
     * 상품 그룹 기본 정보 수정 요청 변환.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID
     * @return 기본 정보 수정 요청 DTO
     */
    public SetofProductGroupBasicInfoUpdateRequest toBasicInfoUpdateRequest(
            ProductGroupDetailBundle bundle, Long externalCategoryId, Long externalBrandId) {

        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();

        return new SetofProductGroupBasicInfoUpdateRequest(
                queryResult.productGroupName(),
                externalBrandId,
                externalCategoryId,
                queryResult.shippingPolicy() != null
                        ? queryResult.shippingPolicy().policyId()
                        : null,
                queryResult.refundPolicy() != null ? queryResult.refundPolicy().policyId() : null);
    }

    /**
     * 상품 + 옵션 일괄 수정 요청 변환.
     *
     * <p>기존 세토프 상품 조회 결과를 기반으로 옵션명 매칭하여 productId를 할당합니다.
     *
     * @param products 상품 목록
     * @param optionGroups 옵션 그룹 목록
     * @param existingProduct 기존 세토프 상품 조회 결과 (nullable)
     * @return 상품 + 옵션 일괄 수정 요청 DTO
     */
    public SetofProductsUpdateRequest toProductsUpdateRequest(
            List<Product> products,
            List<SellerOptionGroup> optionGroups,
            SetofProductGroupDetailResponse existingProduct) {

        List<SetofProductsUpdateRequest.OptionGroupRequest> optionGroupRequests =
                optionGroups.stream()
                        .map(
                                og ->
                                        new SetofProductsUpdateRequest.OptionGroupRequest(
                                                og.idValue(),
                                                og.optionGroupNameValue(),
                                                og.sortOrder(),
                                                og.optionValues().stream()
                                                        .map(
                                                                ov ->
                                                                        new SetofProductsUpdateRequest
                                                                                .OptionValueRequest(
                                                                                ov.idValue(),
                                                                                ov
                                                                                        .optionValueNameValue(),
                                                                                ov.sortOrder()))
                                                        .toList()))
                        .toList();

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(optionGroups);
        Map<Long, Long> productIdMatchMap =
                buildProductIdMatchMap(products, optionGroups, existingProduct);

        List<SetofProductsUpdateRequest.ProductRequest> productRequests =
                products.stream()
                        .map(
                                product -> {
                                    Long productId =
                                            productIdMatchMap.getOrDefault(
                                                    product.idValue(), product.idValue());
                                    Map<Long, String> mappingByGroupId =
                                            buildMappingByGroupId(product, optionValueMap);
                                    List<SetofProductsUpdateRequest.SelectedOptionRequest>
                                            selectedOptions = new ArrayList<>();
                                    for (SellerOptionGroup group : optionGroups) {
                                        String valueName = mappingByGroupId.get(group.idValue());
                                        if (valueName != null) {
                                            selectedOptions.add(
                                                    new SetofProductsUpdateRequest
                                                            .SelectedOptionRequest(
                                                            group.optionGroupNameValue(),
                                                            valueName));
                                        }
                                    }
                                    return new SetofProductsUpdateRequest.ProductRequest(
                                            productId,
                                            product.skuCodeValue(),
                                            product.regularPriceValue(),
                                            product.currentPriceValue(),
                                            product.stockQuantity(),
                                            product.sortOrder(),
                                            selectedOptions);
                                })
                        .toList();

        return new SetofProductsUpdateRequest(optionGroupRequests, productRequests);
    }

    /**
     * 상세설명 요청 변환.
     *
     * @param bundle 상품 그룹 상세 번들
     * @return 상세설명 요청 DTO (없으면 null)
     */
    public SetofDescriptionRequest toDescriptionRequest(ProductGroupDetailBundle bundle) {
        return bundle.description()
                .map(desc -> new SetofDescriptionRequest(desc.contentValue(), null))
                .orElse(null);
    }

    /**
     * 이미지 요청 변환.
     *
     * @param images 이미지 목록
     * @return 이미지 요청 DTO
     */
    public SetofImagesRequest toImagesRequest(List<ProductGroupImage> images) {
        List<SetofImagesRequest.ImageRequest> imageRequests =
                images.stream()
                        .map(
                                image ->
                                        new SetofImagesRequest.ImageRequest(
                                                image.imageTypeName(),
                                                resolveImageUrl(image),
                                                image.sortOrder()))
                        .toList();
        return new SetofImagesRequest(imageRequests);
    }

    /**
     * 고시정보 요청 변환.
     *
     * <p>fieldName은 noticeFieldId → fieldName 매핑이 필요합니다. 매핑이 없으면 빈 문자열로 대체합니다.
     *
     * @param notice 고시정보
     * @param noticeFieldNameMap noticeFieldId → fieldName 매핑 (nullable)
     * @return 고시정보 요청 DTO (없으면 null)
     */
    public SetofNoticeRequest toNoticeRequest(
            ProductNotice notice, Map<Long, String> noticeFieldNameMap) {
        if (notice == null) {
            return null;
        }

        Map<Long, String> fieldNameMap = noticeFieldNameMap != null ? noticeFieldNameMap : Map.of();

        List<SetofNoticeRequest.NoticeEntryRequest> entries =
                notice.entries().stream()
                        .map(
                                entry ->
                                        new SetofNoticeRequest.NoticeEntryRequest(
                                                entry.noticeFieldIdValue(),
                                                fieldNameMap.getOrDefault(
                                                        entry.noticeFieldIdValue(), ""),
                                                entry.fieldValueValue()))
                        .toList();

        return new SetofNoticeRequest(entries);
    }

    // ── Registration 내부 매핑 메서드 ──

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

    private List<OptionGroupRequest> mapOptionGroups(List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .map(
                        og ->
                                new OptionGroupRequest(
                                        og.optionGroupNameValue(),
                                        og.sortOrder(),
                                        og.optionValues().stream()
                                                .map(
                                                        ov ->
                                                                new OptionValueRequest(
                                                                        ov.optionValueNameValue(),
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
                                        null,
                                        product.skuCodeValue(),
                                        product.regularPriceValue(),
                                        product.currentPriceValue(),
                                        product.stockQuantity(),
                                        product.sortOrder(),
                                        mapSelectedOptions(product, optionGroups, optionValueMap)))
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

    private DescriptionRequest mapDescription(ProductGroupDetailBundle bundle) {
        return bundle.description()
                .map(desc -> new DescriptionRequest(desc.contentValue(), null))
                .orElse(null);
    }

    private NoticeRequest mapNotice(ProductGroupDetailBundle bundle) {
        return bundle.notice()
                .map(
                        notice -> {
                            Map<Long, String> fieldNameMap =
                                    bundle.noticeCategory()
                                            .map(
                                                    nc ->
                                                            nc.fields().stream()
                                                                    .collect(
                                                                            java.util.stream
                                                                                    .Collectors
                                                                                    .toMap(
                                                                                            NoticeField
                                                                                                    ::idValue,
                                                                                            NoticeField
                                                                                                    ::fieldNameValue)))
                                            .orElse(Map.of());
                            return convertNotice(notice, fieldNameMap);
                        })
                .orElse(null);
    }

    private NoticeRequest convertNotice(ProductNotice notice, Map<Long, String> fieldNameMap) {
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
                                                fieldNameMap.getOrDefault(
                                                        entry.noticeFieldIdValue(), "기타"),
                                                entry.fieldValueValue()))
                        .toList();

        return new NoticeRequest(entryRequests);
    }

    // ── Update 내부 매핑 메서드 ──

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

    private List<SetofProductGroupUpdateRequest.OptionGroupRequest> mapUpdateOptionGroups(
            List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .map(
                        og ->
                                new SetofProductGroupUpdateRequest.OptionGroupRequest(
                                        og.idValue(),
                                        og.optionGroupNameValue(),
                                        og.sortOrder(),
                                        og.optionValues().stream()
                                                .map(
                                                        ov ->
                                                                new SetofProductGroupUpdateRequest
                                                                        .OptionValueRequest(
                                                                        ov.idValue(),
                                                                        ov.optionValueNameValue(),
                                                                        ov.sortOrder()))
                                                .toList()))
                .toList();
    }

    /**
     * 수정 시 상품 목록 변환.
     *
     * <p>기존 세토프 상품의 옵션명 조합 또는 SKU 코드로 매칭하여 세토프 productId를 할당합니다.
     */
    private List<SetofProductGroupUpdateRequest.ProductRequest> mapUpdateProducts(
            List<Product> products,
            List<SellerOptionGroup> optionGroups,
            SetofProductGroupDetailResponse existingProduct) {

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(optionGroups);
        Map<Long, Long> productIdMatchMap =
                buildProductIdMatchMap(products, optionGroups, existingProduct);

        return products.stream()
                .map(
                        product -> {
                            Long productId =
                                    productIdMatchMap.getOrDefault(
                                            product.idValue(), product.idValue());
                            return new SetofProductGroupUpdateRequest.ProductRequest(
                                    productId,
                                    product.skuCodeValue(),
                                    product.regularPriceValue(),
                                    product.currentPriceValue(),
                                    product.stockQuantity(),
                                    product.sortOrder(),
                                    mapUpdateSelectedOptions(
                                            product, optionGroups, optionValueMap));
                        })
                .toList();
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

    private SetofProductGroupUpdateRequest.DescriptionRequest mapUpdateDescription(
            ProductGroupDetailBundle bundle) {
        return bundle.description()
                .map(
                        desc ->
                                new SetofProductGroupUpdateRequest.DescriptionRequest(
                                        desc.contentValue(), null))
                .orElse(null);
    }

    private SetofProductGroupUpdateRequest.NoticeRequest mapUpdateNotice(
            ProductGroupDetailBundle bundle) {
        return bundle.notice()
                .map(
                        notice -> {
                            Map<Long, String> fieldNameMap =
                                    bundle.noticeCategory()
                                            .map(
                                                    nc ->
                                                            nc.fields().stream()
                                                                    .collect(
                                                                            java.util.stream
                                                                                    .Collectors
                                                                                    .toMap(
                                                                                            NoticeField
                                                                                                    ::idValue,
                                                                                            NoticeField
                                                                                                    ::fieldNameValue)))
                                            .orElse(Map.of());
                            return convertUpdateNotice(notice, fieldNameMap);
                        })
                .orElse(null);
    }

    private SetofProductGroupUpdateRequest.NoticeRequest convertUpdateNotice(
            ProductNotice notice, Map<Long, String> fieldNameMap) {
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
                                                fieldNameMap.getOrDefault(
                                                        entry.noticeFieldIdValue(), "기타"),
                                                entry.fieldValueValue()))
                        .toList();

        return new SetofProductGroupUpdateRequest.NoticeRequest(entryRequests);
    }

    // ── 옵션명 기반 productId 매칭 ──

    /**
     * 내부 product와 기존 세토프 product를 옵션명 조합 또는 SKU 코드로 매칭하여 내부 productId → 세토프 productId 매핑을 생성합니다.
     *
     * <p>매칭 우선순위:
     *
     * <ol>
     *   <li>SKU 코드 일치
     *   <li>옵션명 조합(optionGroupName:optionValueName) 일치
     * </ol>
     *
     * @param products 내부 상품 목록
     * @param optionGroups 옵션 그룹 목록
     * @param existingProduct 기존 세토프 상품 조회 결과 (nullable)
     * @return 내부 productId → 세토프 productId 매핑
     */
    private Map<Long, Long> buildProductIdMatchMap(
            List<Product> products,
            List<SellerOptionGroup> optionGroups,
            SetofProductGroupDetailResponse existingProduct) {

        if (existingProduct == null || existingProduct.products() == null) {
            return Map.of();
        }

        // 기존 세토프 product를 SKU 코드 → setofProductId, 옵션명 조합 → setofProductId로 인덱싱
        Map<String, Long> skuToSetofId = new HashMap<>();
        Map<String, Long> optionKeyToSetofId = new HashMap<>();

        for (var ep : existingProduct.products()) {
            if (ep.skuCode() != null && !ep.skuCode().isBlank()) {
                skuToSetofId.put(ep.skuCode(), ep.productId());
            }
            String key = buildExternalOptionKey(ep.selectedOptions());
            if (!key.isEmpty()) {
                optionKeyToSetofId.put(key, ep.productId());
            }
        }

        // 내부 product를 순회하며 매칭
        Map<Long, Long> matchMap = new HashMap<>();
        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(optionGroups);

        for (Product product : products) {
            // 우선순위 1: SKU 코드
            Long setofId = null;
            if (product.skuCodeValue() != null && !product.skuCodeValue().isBlank()) {
                setofId = skuToSetofId.get(product.skuCodeValue());
            }
            // 우선순위 2: 옵션명 조합
            if (setofId == null) {
                String key = buildInternalOptionKey(product, optionGroups, optionValueMap);
                if (!key.isEmpty()) {
                    setofId = optionKeyToSetofId.get(key);
                }
            }
            if (setofId != null) {
                matchMap.put(product.idValue(), setofId);
            }
        }
        return matchMap;
    }

    /** 기존 세토프 상품의 selectedOptions를 정렬된 옵션 키 문자열로 변환합니다. */
    private String buildExternalOptionKey(
            List<SetofProductGroupDetailResponse.SelectedOptionResponse> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty()) {
            return "";
        }
        return selectedOptions.stream()
                .map(so -> so.optionGroupName() + ":" + so.optionValueName())
                .sorted()
                .collect(Collectors.joining("|"));
    }

    /** 내부 상품의 옵션 매핑을 정렬된 옵션 키 문자열로 변환합니다. */
    private String buildInternalOptionKey(
            Product product,
            List<SellerOptionGroup> optionGroups,
            Map<Long, SellerOptionValue> optionValueMap) {
        Map<Long, String> mappingByGroupId = buildMappingByGroupId(product, optionValueMap);
        return optionGroups.stream()
                .filter(g -> mappingByGroupId.containsKey(g.idValue()))
                .map(g -> g.optionGroupNameValue() + ":" + mappingByGroupId.get(g.idValue()))
                .sorted()
                .collect(Collectors.joining("|"));
    }

    // ── 공통 유틸리티 ──

    private String resolveImageUrl(ProductGroupImage image) {
        String uploaded = image.uploadedUrlValue();
        return uploaded != null ? uploaded : image.originUrlValue();
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

    private int computeMinRegularPrice(List<Product> products) {
        return products.stream().mapToInt(Product::regularPriceValue).min().orElse(0);
    }

    private int computeMinCurrentPrice(List<Product> products) {
        return products.stream().mapToInt(Product::currentPriceValue).min().orElse(0);
    }
}
