package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo.ClaimDeliveryInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo.DeliveryFeeByArea;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DetailAttribute;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images.OptionalImage;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images.RepresentativeImage;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo.OptionCombination;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo.OptionCombinationGroupNames;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OriginProduct;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.ProductInfoProvidedNotice;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.ProductInfoProvidedNotice.ProductInfoProvidedNoticeContent;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.SmartstoreChannelProduct;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.shippingpolicy.dto.response.ShippingPolicyResult;
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
 * ProductGroupDetailBundle → NaverProductRegistrationRequest 변환 매퍼.
 *
 * <p>내부 상품 데이터를 네이버 커머스 API 등록 요청 형식으로 변환합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceProductMapper {

    private static final String STATUS_SALE = "SALE";
    private static final String SALE_TYPE_NEW = "NEW";
    private static final String DELIVERY_TYPE_DELIVERY = "DELIVERY";
    private static final String DELIVERY_ATTR_NORMAL = "NORMAL";
    private static final String AREA_TYPE_AREA2 = "AREA_2";
    private static final String OPTION_SORT_CREATE = "CREATE";

    /**
     * 상품 등록 요청 변환.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 네이버 카테고리 ID
     * @param externalBrandId 네이버 브랜드 ID (nullable)
     * @return 네이버 상품 등록 요청 DTO
     */
    public NaverProductRegistrationRequest toRegistrationRequest(
            ProductGroupDetailBundle bundle, Long externalCategoryId, Long externalBrandId) {

        ProductGroupDetailCompositeQueryResult queryResult = bundle.queryResult();
        ProductGroup group = bundle.group();
        List<Product> products = bundle.products();

        Images images = mapImages(group.images());
        DeliveryInfo deliveryInfo = mapDeliveryInfo(queryResult.shippingPolicy());
        OptionInfo optionInfo = mapOptionInfo(group, products);
        DetailAttribute detailAttribute =
                DetailAttribute.of(externalCategoryId, optionInfo, externalBrandId);

        String detailContent = mapDetailContent(bundle);
        ProductInfoProvidedNotice notice = mapNotice(bundle);

        int representativePrice = resolveRepresentativePrice(products);
        int totalStock = products.stream().mapToInt(Product::stockQuantity).sum();

        OriginProduct originProduct =
                new OriginProduct(
                        STATUS_SALE,
                        SALE_TYPE_NEW,
                        String.valueOf(externalCategoryId),
                        queryResult.productGroupName(),
                        images,
                        detailAttribute,
                        representativePrice,
                        totalStock,
                        detailContent,
                        deliveryInfo,
                        notice);

        SmartstoreChannelProduct channelProduct =
                new SmartstoreChannelProduct(queryResult.productGroupName(), null);

        return new NaverProductRegistrationRequest(originProduct, channelProduct);
    }

    private Images mapImages(List<ProductGroupImage> groupImages) {
        RepresentativeImage mainImage = null;
        List<OptionalImage> optionalImages = new ArrayList<>();

        for (ProductGroupImage image : groupImages) {
            String url = resolveImageUrl(image);
            if (image.isThumbnail()) {
                mainImage = new RepresentativeImage(url);
            } else {
                optionalImages.add(new OptionalImage(url));
            }
        }

        if (mainImage == null && !groupImages.isEmpty()) {
            mainImage = new RepresentativeImage(resolveImageUrl(groupImages.get(0)));
        }

        return new Images(mainImage, optionalImages.isEmpty() ? null : optionalImages);
    }

    private String resolveImageUrl(ProductGroupImage image) {
        String uploaded = image.uploadedUrlValue();
        return uploaded != null ? uploaded : image.originUrlValue();
    }

    private DeliveryInfo mapDeliveryInfo(ShippingPolicyResult shipping) {
        if (shipping == null) {
            return new DeliveryInfo(
                    DELIVERY_TYPE_DELIVERY, DELIVERY_ATTR_NORMAL, "FREE", null, null);
        }

        String deliveryFee = mapDeliveryFeeType(shipping.shippingFeeType(), shipping.baseFee());

        DeliveryFeeByArea feeByArea = null;
        if (shipping.jejuExtraFee() != null || shipping.islandExtraFee() != null) {
            feeByArea =
                    new DeliveryFeeByArea(
                            shipping.jejuExtraFee(), shipping.islandExtraFee(), AREA_TYPE_AREA2);
        }

        ClaimDeliveryInfo claimInfo = null;
        if (shipping.returnFee() != null || shipping.exchangeFee() != null) {
            claimInfo = new ClaimDeliveryInfo(shipping.returnFee(), shipping.exchangeFee());
        }

        return new DeliveryInfo(
                DELIVERY_TYPE_DELIVERY, DELIVERY_ATTR_NORMAL, deliveryFee, feeByArea, claimInfo);
    }

    private String mapDeliveryFeeType(String shippingFeeType, Long baseFee) {
        if ("FREE".equals(shippingFeeType)) {
            return "FREE";
        }
        if ("CONDITIONAL_FREE".equals(shippingFeeType)) {
            return "CONDITIONAL_FREE";
        }
        if (baseFee != null && baseFee > 0) {
            return "PAID";
        }
        return "FREE";
    }

    private OptionInfo mapOptionInfo(ProductGroup group, List<Product> products) {
        List<SellerOptionGroup> optionGroups = group.sellerOptionGroups();
        if (optionGroups.isEmpty()) {
            return null;
        }

        List<OptionCombinationGroupNames> groupNames =
                optionGroups.stream()
                        .map(og -> new OptionCombinationGroupNames(og.optionGroupNameValue()))
                        .toList();

        Map<Long, SellerOptionValue> optionValueMap = buildOptionValueMap(optionGroups);

        List<OptionCombination> combinations = new ArrayList<>();
        long combinationId = 1;

        for (Product product : products) {
            List<String> optionNames = resolveOptionNames(product, optionGroups, optionValueMap);
            combinations.add(
                    new OptionCombination(
                            combinationId++,
                            optionNames.size() > 0 ? optionNames.get(0) : null,
                            optionNames.size() > 1 ? optionNames.get(1) : null,
                            optionNames.size() > 2 ? optionNames.get(2) : null,
                            product.stockQuantity(),
                            product.currentPriceValue(),
                            product.skuCodeValue(),
                            product.stockQuantity() > 0));
        }

        return new OptionInfo(OPTION_SORT_CREATE, groupNames, combinations);
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

    private List<String> resolveOptionNames(
            Product product,
            List<SellerOptionGroup> optionGroups,
            Map<Long, SellerOptionValue> optionValueMap) {

        Map<Long, String> mappingByGroupId = new HashMap<>();
        for (ProductOptionMapping mapping : product.optionMappings()) {
            SellerOptionValue value = optionValueMap.get(mapping.sellerOptionValueIdValue());
            if (value != null) {
                mappingByGroupId.put(
                        value.sellerOptionGroupIdValue(), value.optionValueNameValue());
            }
        }

        List<String> names = new ArrayList<>();
        for (SellerOptionGroup group : optionGroups) {
            String name = mappingByGroupId.get(group.idValue());
            if (name != null) {
                names.add(name);
            }
        }
        return names;
    }

    private String mapDetailContent(ProductGroupDetailBundle bundle) {
        return bundle.description()
                .map(
                        desc -> {
                            String cdnPath = desc.cdnPathValue();
                            if (cdnPath != null && !cdnPath.isBlank()) {
                                return "<iframe src=\""
                                        + cdnPath
                                        + "\" width=\"100%\" frameborder=\"0\"></iframe>";
                            }
                            return desc.contentValue();
                        })
                .orElse("");
    }

    private ProductInfoProvidedNotice mapNotice(ProductGroupDetailBundle bundle) {
        return bundle.notice().map(this::convertNotice).orElse(null);
    }

    private ProductInfoProvidedNotice convertNotice(ProductNotice notice) {
        List<ProductNoticeEntry> entries = notice.entries();
        if (entries.isEmpty()) {
            return null;
        }

        List<ProductInfoProvidedNoticeContent> contents = new ArrayList<>();
        int order = 1;
        for (ProductNoticeEntry entry : entries) {
            contents.add(
                    new ProductInfoProvidedNoticeContent(
                            order++,
                            String.valueOf(entry.noticeFieldIdValue()),
                            entry.fieldValueValue()));
        }

        return new ProductInfoProvidedNotice("ETC", contents);
    }

    private int resolveRepresentativePrice(List<Product> products) {
        return products.stream().mapToInt(Product::currentPriceValue).min().orElse(0);
    }
}
