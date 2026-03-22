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
import com.ryuqq.marketplace.application.notice.dto.response.NoticeFieldResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductOptionMappingResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionGroupResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionValueResult;
import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeEntryResult;
import com.ryuqq.marketplace.application.productnotice.dto.response.ProductNoticeResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ProductGroupSyncData -> Setof Commerce 요청 DTO 변환 매퍼.
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
     * @param syncData 상품 그룹 동기화 데이터
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @param externalSellerId 세토프 셀러 ID (shop.accountId)
     * @return 세토프 상품 등록 요청 DTO
     */
    public SetofProductGroupRegistrationRequest toRegistrationRequest(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            long externalSellerId) {

        ProductGroupDetailCompositeQueryResult queryResult = syncData.queryResult();
        List<ProductResult> products = syncData.products();

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
                mapImages(syncData.images()),
                mapOptionGroups(syncData.optionGroups()),
                mapProducts(products, syncData.optionGroups()),
                mapDescription(syncData),
                mapNotice(syncData));
    }

    /**
     * 상품 수정 요청 변환.
     *
     * <p>기존 세토프 상품 조회 결과를 기반으로 옵션명 매칭하여 productId를 할당합니다.
     *
     * @param syncData 상품 그룹 동기화 데이터
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID (nullable)
     * @param existingProduct 기존 세토프 상품 조회 결과 (nullable)
     * @return 세토프 상품 수정 요청 DTO
     */
    public SetofProductGroupUpdateRequest toUpdateRequest(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            SetofProductGroupDetailResponse existingProduct) {

        ProductGroupDetailCompositeQueryResult queryResult = syncData.queryResult();
        List<ProductResult> products = syncData.products();

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
                mapUpdateImages(syncData.images()),
                mapUpdateOptionGroups(syncData.optionGroups()),
                mapUpdateProducts(products, syncData.optionGroups(), existingProduct),
                mapUpdateDescription(syncData),
                mapUpdateNotice(syncData));
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
     * @param syncData 상품 그룹 동기화 데이터
     * @param externalCategoryId 세토프 카테고리 ID
     * @param externalBrandId 세토프 브랜드 ID
     * @return 기본 정보 수정 요청 DTO
     */
    public SetofProductGroupBasicInfoUpdateRequest toBasicInfoUpdateRequest(
            ProductGroupSyncData syncData, Long externalCategoryId, Long externalBrandId) {

        ProductGroupDetailCompositeQueryResult queryResult = syncData.queryResult();

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
            List<ProductResult> products,
            List<SellerOptionGroupResult> optionGroups,
            SetofProductGroupDetailResponse existingProduct) {

        List<SetofProductsUpdateRequest.OptionGroupRequest> optionGroupRequests =
                optionGroups.stream()
                        .map(
                                og ->
                                        new SetofProductsUpdateRequest.OptionGroupRequest(
                                                og.id(),
                                                og.optionGroupName(),
                                                og.sortOrder(),
                                                og.optionValues().stream()
                                                        .map(
                                                                ov ->
                                                                        new SetofProductsUpdateRequest
                                                                                .OptionValueRequest(
                                                                                ov.id(),
                                                                                ov
                                                                                        .optionValueName(),
                                                                                ov.sortOrder()))
                                                        .toList()))
                        .toList();

        Map<Long, SellerOptionValueResult> optionValueMap = buildOptionValueMap(optionGroups);
        Map<Long, Long> productIdMatchMap =
                buildProductIdMatchMap(products, optionGroups, existingProduct);

        List<SetofProductsUpdateRequest.ProductRequest> productRequests =
                products.stream()
                        .map(
                                product -> {
                                    Long productId =
                                            productIdMatchMap.getOrDefault(
                                                    product.id(), product.id());
                                    Map<Long, String> mappingByGroupId =
                                            buildMappingByGroupId(product, optionValueMap);
                                    List<SetofProductsUpdateRequest.SelectedOptionRequest>
                                            selectedOptions = new ArrayList<>();
                                    for (SellerOptionGroupResult group : optionGroups) {
                                        String valueName = mappingByGroupId.get(group.id());
                                        if (valueName != null) {
                                            selectedOptions.add(
                                                    new SetofProductsUpdateRequest
                                                            .SelectedOptionRequest(
                                                            group.optionGroupName(), valueName));
                                        }
                                    }
                                    return new SetofProductsUpdateRequest.ProductRequest(
                                            productId,
                                            product.skuCode(),
                                            product.regularPrice(),
                                            product.currentPrice(),
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
     * @param syncData 상품 그룹 동기화 데이터
     * @return 상세설명 요청 DTO (없으면 null)
     */
    public SetofDescriptionRequest toDescriptionRequest(ProductGroupSyncData syncData) {
        return syncData.descriptionContent()
                .map(content -> new SetofDescriptionRequest(content, null))
                .orElse(null);
    }

    /**
     * 이미지 요청 변환.
     *
     * @param images 이미지 목록
     * @return 이미지 요청 DTO
     */
    public SetofImagesRequest toImagesRequest(List<ProductGroupImageResult> images) {
        List<SetofImagesRequest.ImageRequest> imageRequests =
                images.stream()
                        .map(
                                image ->
                                        new SetofImagesRequest.ImageRequest(
                                                image.imageType(),
                                                resolveImageUrl(image),
                                                image.sortOrder()))
                        .toList();
        return new SetofImagesRequest(imageRequests);
    }

    /**
     * 고시정보 요청 변환.
     *
     * <p>fieldName은 noticeFieldId -> fieldName 매핑이 필요합니다. 매핑이 없으면 빈 문자열로 대체합니다.
     *
     * @param notice 고시정보
     * @param noticeFieldNameMap noticeFieldId -> fieldName 매핑 (nullable)
     * @return 고시정보 요청 DTO (없으면 null)
     */
    public SetofNoticeRequest toNoticeRequest(
            ProductNoticeResult notice, Map<Long, String> noticeFieldNameMap) {
        if (notice == null) {
            return null;
        }

        Map<Long, String> fieldNameMap = noticeFieldNameMap != null ? noticeFieldNameMap : Map.of();

        List<SetofNoticeRequest.NoticeEntryRequest> entries =
                notice.entries().stream()
                        .map(
                                entry ->
                                        new SetofNoticeRequest.NoticeEntryRequest(
                                                entry.noticeFieldId(),
                                                fieldNameMap.getOrDefault(
                                                        entry.noticeFieldId(), ""),
                                                entry.fieldValue()))
                        .toList();

        return new SetofNoticeRequest(entries);
    }

    // -- Registration 내부 매핑 메서드 --

    private List<ImageRequest> mapImages(List<ProductGroupImageResult> groupImages) {
        return groupImages.stream()
                .map(
                        image ->
                                new ImageRequest(
                                        image.imageType(),
                                        resolveImageUrl(image),
                                        image.sortOrder()))
                .toList();
    }

    private List<OptionGroupRequest> mapOptionGroups(List<SellerOptionGroupResult> optionGroups) {
        return optionGroups.stream()
                .map(
                        og ->
                                new OptionGroupRequest(
                                        og.optionGroupName(),
                                        og.sortOrder(),
                                        og.optionValues().stream()
                                                .map(
                                                        ov ->
                                                                new OptionValueRequest(
                                                                        ov.optionValueName(),
                                                                        ov.sortOrder()))
                                                .toList()))
                .toList();
    }

    private List<ProductRequest> mapProducts(
            List<ProductResult> products, List<SellerOptionGroupResult> optionGroups) {

        Map<Long, SellerOptionValueResult> optionValueMap = buildOptionValueMap(optionGroups);

        return products.stream()
                .map(
                        product ->
                                new ProductRequest(
                                        null,
                                        product.skuCode(),
                                        product.regularPrice(),
                                        product.currentPrice(),
                                        product.stockQuantity(),
                                        product.sortOrder(),
                                        mapSelectedOptions(product, optionGroups, optionValueMap)))
                .toList();
    }

    private List<SelectedOptionRequest> mapSelectedOptions(
            ProductResult product,
            List<SellerOptionGroupResult> optionGroups,
            Map<Long, SellerOptionValueResult> optionValueMap) {

        Map<Long, String> mappingByGroupId = buildMappingByGroupId(product, optionValueMap);

        List<SelectedOptionRequest> selectedOptions = new ArrayList<>();
        for (SellerOptionGroupResult group : optionGroups) {
            String valueName = mappingByGroupId.get(group.id());
            if (valueName != null) {
                selectedOptions.add(new SelectedOptionRequest(group.optionGroupName(), valueName));
            }
        }
        return selectedOptions;
    }

    private DescriptionRequest mapDescription(ProductGroupSyncData syncData) {
        return syncData.descriptionContent()
                .map(content -> new DescriptionRequest(content, null))
                .orElse(null);
    }

    private NoticeRequest mapNotice(ProductGroupSyncData syncData) {
        return syncData.notice()
                .map(
                        notice -> {
                            Map<Long, String> fieldNameMap =
                                    syncData.noticeCategory()
                                            .map(
                                                    nc ->
                                                            nc.fields().stream()
                                                                    .collect(
                                                                            java.util.stream
                                                                                    .Collectors
                                                                                    .toMap(
                                                                                            NoticeFieldResult
                                                                                                    ::id,
                                                                                            NoticeFieldResult
                                                                                                    ::fieldName)))
                                            .orElse(Map.of());
                            return convertNotice(notice, fieldNameMap);
                        })
                .orElse(null);
    }

    private NoticeRequest convertNotice(
            ProductNoticeResult notice, Map<Long, String> fieldNameMap) {
        List<ProductNoticeEntryResult> entries = notice.entries();
        if (entries.isEmpty()) {
            return null;
        }

        List<NoticeEntryRequest> entryRequests =
                entries.stream()
                        .map(
                                entry ->
                                        new NoticeEntryRequest(
                                                entry.noticeFieldId(),
                                                fieldNameMap.getOrDefault(
                                                        entry.noticeFieldId(), "기타"),
                                                entry.fieldValue()))
                        .toList();

        return new NoticeRequest(entryRequests);
    }

    // -- Update 내부 매핑 메서드 --

    private List<SetofProductGroupUpdateRequest.ImageRequest> mapUpdateImages(
            List<ProductGroupImageResult> groupImages) {
        return groupImages.stream()
                .map(
                        image ->
                                new SetofProductGroupUpdateRequest.ImageRequest(
                                        image.imageType(),
                                        resolveImageUrl(image),
                                        image.sortOrder()))
                .toList();
    }

    private List<SetofProductGroupUpdateRequest.OptionGroupRequest> mapUpdateOptionGroups(
            List<SellerOptionGroupResult> optionGroups) {
        return optionGroups.stream()
                .map(
                        og ->
                                new SetofProductGroupUpdateRequest.OptionGroupRequest(
                                        og.id(),
                                        og.optionGroupName(),
                                        og.sortOrder(),
                                        og.optionValues().stream()
                                                .map(
                                                        ov ->
                                                                new SetofProductGroupUpdateRequest
                                                                        .OptionValueRequest(
                                                                        ov.id(),
                                                                        ov.optionValueName(),
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
            List<ProductResult> products,
            List<SellerOptionGroupResult> optionGroups,
            SetofProductGroupDetailResponse existingProduct) {

        Map<Long, SellerOptionValueResult> optionValueMap = buildOptionValueMap(optionGroups);
        Map<Long, Long> productIdMatchMap =
                buildProductIdMatchMap(products, optionGroups, existingProduct);

        return products.stream()
                .map(
                        product -> {
                            Long productId =
                                    productIdMatchMap.getOrDefault(product.id(), product.id());
                            return new SetofProductGroupUpdateRequest.ProductRequest(
                                    productId,
                                    product.skuCode(),
                                    product.regularPrice(),
                                    product.currentPrice(),
                                    product.stockQuantity(),
                                    product.sortOrder(),
                                    mapUpdateSelectedOptions(
                                            product, optionGroups, optionValueMap));
                        })
                .toList();
    }

    private List<SetofProductGroupUpdateRequest.SelectedOptionRequest> mapUpdateSelectedOptions(
            ProductResult product,
            List<SellerOptionGroupResult> optionGroups,
            Map<Long, SellerOptionValueResult> optionValueMap) {

        Map<Long, String> mappingByGroupId = buildMappingByGroupId(product, optionValueMap);

        List<SetofProductGroupUpdateRequest.SelectedOptionRequest> selectedOptions =
                new ArrayList<>();
        for (SellerOptionGroupResult group : optionGroups) {
            String valueName = mappingByGroupId.get(group.id());
            if (valueName != null) {
                selectedOptions.add(
                        new SetofProductGroupUpdateRequest.SelectedOptionRequest(
                                group.optionGroupName(), valueName));
            }
        }
        return selectedOptions;
    }

    private SetofProductGroupUpdateRequest.DescriptionRequest mapUpdateDescription(
            ProductGroupSyncData syncData) {
        return syncData.descriptionContent()
                .map(
                        content ->
                                new SetofProductGroupUpdateRequest.DescriptionRequest(
                                        content, null))
                .orElse(null);
    }

    private SetofProductGroupUpdateRequest.NoticeRequest mapUpdateNotice(
            ProductGroupSyncData syncData) {
        return syncData.notice()
                .map(
                        notice -> {
                            Map<Long, String> fieldNameMap =
                                    syncData.noticeCategory()
                                            .map(
                                                    nc ->
                                                            nc.fields().stream()
                                                                    .collect(
                                                                            java.util.stream
                                                                                    .Collectors
                                                                                    .toMap(
                                                                                            NoticeFieldResult
                                                                                                    ::id,
                                                                                            NoticeFieldResult
                                                                                                    ::fieldName)))
                                            .orElse(Map.of());
                            return convertUpdateNotice(notice, fieldNameMap);
                        })
                .orElse(null);
    }

    private SetofProductGroupUpdateRequest.NoticeRequest convertUpdateNotice(
            ProductNoticeResult notice, Map<Long, String> fieldNameMap) {
        List<ProductNoticeEntryResult> entries = notice.entries();
        if (entries.isEmpty()) {
            return null;
        }

        List<SetofProductGroupUpdateRequest.NoticeEntryRequest> entryRequests =
                entries.stream()
                        .map(
                                entry ->
                                        new SetofProductGroupUpdateRequest.NoticeEntryRequest(
                                                entry.noticeFieldId(),
                                                fieldNameMap.getOrDefault(
                                                        entry.noticeFieldId(), "기타"),
                                                entry.fieldValue()))
                        .toList();

        return new SetofProductGroupUpdateRequest.NoticeRequest(entryRequests);
    }

    // -- 옵션명 기반 productId 매칭 --

    /**
     * 내부 product와 기존 세토프 product를 옵션명 조합 또는 SKU 코드로 매칭하여 내부 productId -> 세토프 productId 매핑을 생성합니다.
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
     * @return 내부 productId -> 세토프 productId 매핑
     */
    private Map<Long, Long> buildProductIdMatchMap(
            List<ProductResult> products,
            List<SellerOptionGroupResult> optionGroups,
            SetofProductGroupDetailResponse existingProduct) {

        if (existingProduct == null || existingProduct.products() == null) {
            return Map.of();
        }

        // 기존 세토프 product를 SKU 코드 -> setofProductId, 옵션명 조합 -> setofProductId로 인덱싱
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
        Map<Long, SellerOptionValueResult> optionValueMap = buildOptionValueMap(optionGroups);

        for (ProductResult product : products) {
            // 우선순위 1: SKU 코드
            Long setofId = null;
            if (product.skuCode() != null && !product.skuCode().isBlank()) {
                setofId = skuToSetofId.get(product.skuCode());
            }
            // 우선순위 2: 옵션명 조합
            if (setofId == null) {
                String key = buildInternalOptionKey(product, optionGroups, optionValueMap);
                if (!key.isEmpty()) {
                    setofId = optionKeyToSetofId.get(key);
                }
            }
            if (setofId != null) {
                matchMap.put(product.id(), setofId);
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
            ProductResult product,
            List<SellerOptionGroupResult> optionGroups,
            Map<Long, SellerOptionValueResult> optionValueMap) {
        Map<Long, String> mappingByGroupId = buildMappingByGroupId(product, optionValueMap);
        return optionGroups.stream()
                .filter(g -> mappingByGroupId.containsKey(g.id()))
                .map(g -> g.optionGroupName() + ":" + mappingByGroupId.get(g.id()))
                .sorted()
                .collect(Collectors.joining("|"));
    }

    // -- 공통 유틸리티 --

    private String resolveImageUrl(ProductGroupImageResult image) {
        String uploaded = image.uploadedUrl();
        return uploaded != null ? uploaded : image.originUrl();
    }

    private Map<Long, String> buildMappingByGroupId(
            ProductResult product, Map<Long, SellerOptionValueResult> optionValueMap) {
        Map<Long, String> mappingByGroupId = new HashMap<>();
        for (ProductOptionMappingResult mapping : product.optionMappings()) {
            SellerOptionValueResult value = optionValueMap.get(mapping.sellerOptionValueId());
            if (value != null) {
                mappingByGroupId.put(value.sellerOptionGroupId(), value.optionValueName());
            }
        }
        return mappingByGroupId;
    }

    private Map<Long, SellerOptionValueResult> buildOptionValueMap(
            List<SellerOptionGroupResult> optionGroups) {
        Map<Long, SellerOptionValueResult> map = new HashMap<>();
        for (SellerOptionGroupResult group : optionGroups) {
            for (SellerOptionValueResult value : group.optionValues()) {
                map.put(value.id(), value);
            }
        }
        return map;
    }

    private int computeMinRegularPrice(List<ProductResult> products) {
        return products.stream().mapToInt(ProductResult::regularPrice).min().orElse(0);
    }

    private int computeMinCurrentPrice(List<ProductResult> products) {
        return products.stream().mapToInt(ProductResult::currentPrice).min().orElse(0);
    }
}
