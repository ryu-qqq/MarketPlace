package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.AfterServiceInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.CertificationTargetExcludeContent;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DeliveryInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.DetailAttribute;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.Images;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OriginAreaInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OriginProduct;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.ProductInfoProvidedNotice;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.SmartstoreChannelProduct;
import com.ryuqq.marketplace.application.outboundproductimage.dto.ResolvedExternalImages;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeQueryResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.application.seller.dto.response.SellerCsSyncResult;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import java.util.List;
import java.util.Set;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * ProductGroupSyncData -> NaverProductRegistrationRequest 변환 매퍼.
 *
 * <p>각 영역별 변환은 전용 매퍼에 위임하고, 이 클래스는 전체 조립만 담당합니다.
 *
 * @see NaverNoticeMapper
 * @see NaverDeliveryMapper
 * @see NaverOptionMapper
 * @see NaverImageMapper
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceProductMapper {

    private static final String SALE_TYPE_NEW = "NEW";
    private static final String DEFAULT_AS_PHONE = "1660-1126";
    private static final String DEFAULT_AS_GUIDE = "상세페이지 참조";

    /** 상품 등록 요청 변환. */
    public NaverProductRegistrationRequest toRegistrationRequest(
            ProductGroupSyncData syncData, Long externalCategoryId, Long externalBrandId) {
        Images images = NaverImageMapper.mapImages(syncData.images());
        OptionInfo optionInfo =
                NaverOptionMapper.mapOptionInfo(
                        syncData.optionGroups(), syncData.products(), syncData.soldout());
        return buildRequest(syncData, externalCategoryId, externalBrandId, images, optionInfo);
    }

    /** 상품 등록 요청 변환 (외부 채널 이미지 URL 사용). */
    public NaverProductRegistrationRequest toRegistrationRequest(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            ResolvedExternalImages resolvedImages) {
        Images images = resolveImages(syncData, resolvedImages);
        OptionInfo optionInfo =
                NaverOptionMapper.mapOptionInfo(
                        syncData.optionGroups(), syncData.products(), syncData.soldout());
        return buildRequest(syncData, externalCategoryId, externalBrandId, images, optionInfo);
    }

    /**
     * 상품 수정 요청 변환.
     *
     * <p>기존 네이버 상품의 옵션 combination ID를 매칭하여 옵션 구조를 유지합니다.
     */
    public NaverProductRegistrationRequest toUpdateRequest(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            NaverProductDetailResponse existingProduct,
            Set<ChangedArea> changedAreas) {
        Images images = NaverImageMapper.mapImages(syncData.images());
        OptionInfo optionInfo = resolveOptionInfoForUpdate(syncData, existingProduct, changedAreas);
        return buildRequest(syncData, externalCategoryId, externalBrandId, images, optionInfo);
    }

    /** 상품 수정 요청 변환 (외부 채널 이미지 URL 사용). */
    public NaverProductRegistrationRequest toUpdateRequest(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            ResolvedExternalImages resolvedImages,
            NaverProductDetailResponse existingProduct,
            Set<ChangedArea> changedAreas) {
        Images images = resolveImages(syncData, resolvedImages);
        OptionInfo optionInfo = resolveOptionInfoForUpdate(syncData, existingProduct, changedAreas);
        return buildRequest(syncData, externalCategoryId, externalBrandId, images, optionInfo);
    }

    private OptionInfo resolveOptionInfoForUpdate(
            ProductGroupSyncData syncData,
            NaverProductDetailResponse existingProduct,
            Set<ChangedArea> changedAreas) {

        // 기존 상품 정보가 없으면 등록 모드로 폴백
        if (existingProduct == null) {
            return NaverOptionMapper.mapOptionInfo(
                    syncData.optionGroups(), syncData.products(), syncData.soldout());
        }

        // 옵션이 있는 상품: 기존 combination ID 매칭
        return NaverOptionMapper.mapOptionInfoForUpdate(
                syncData.optionGroups(), syncData.products(), existingProduct, syncData.soldout());
    }

    private NaverProductRegistrationRequest buildRequest(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            Images images,
            OptionInfo optionInfo) {

        ProductGroupDetailCompositeQueryResult queryResult = syncData.queryResult();
        List<ProductResult> products = syncData.products();

        DeliveryInfo deliveryInfo =
                NaverDeliveryMapper.mapDeliveryInfo(queryResult.shippingPolicy());

        AfterServiceInfo afterServiceInfo = mapAfterServiceInfo(syncData.sellerCs().orElse(null));
        OriginAreaInfo originAreaInfo = NaverNoticeMapper.mapOriginAreaInfo(syncData);
        ProductInfoProvidedNotice notice = NaverNoticeMapper.mapNotice(syncData);

        String manufacturerName = queryResult.brandName();

        CertificationTargetExcludeContent certExclude =
                CertificationTargetExcludeContent.kcExempt();

        DetailAttribute detailAttribute =
                DetailAttribute.of(
                        externalCategoryId,
                        optionInfo,
                        externalBrandId,
                        manufacturerName,
                        afterServiceInfo,
                        originAreaInfo,
                        true,
                        null,
                        certExclude,
                        notice);

        String detailContent = mapDetailContent(syncData);

        int representativePrice = resolveRepresentativePrice(products);
        int totalStock =
                syncData.soldout()
                        ? 0
                        : products.stream().mapToInt(ProductResult::stockQuantity).sum();
        String naverStatusType = mapNaverStatusType(syncData);

        OriginProduct originProduct =
                new OriginProduct(
                        naverStatusType,
                        SALE_TYPE_NEW,
                        String.valueOf(externalCategoryId),
                        queryResult.productGroupName(),
                        images,
                        detailAttribute,
                        representativePrice,
                        totalStock,
                        detailContent,
                        deliveryInfo);

        SmartstoreChannelProduct channelProduct =
                new SmartstoreChannelProduct(queryResult.productGroupName(), "ON", true, null);

        return new NaverProductRegistrationRequest(originProduct, channelProduct);
    }

    private Images resolveImages(
            ProductGroupSyncData syncData, ResolvedExternalImages resolvedImages) {
        return resolvedImages != null && !resolvedImages.isEmpty()
                ? NaverImageMapper.mapExternalImages(resolvedImages)
                : NaverImageMapper.mapImages(syncData.images());
    }

    private AfterServiceInfo mapAfterServiceInfo(SellerCsSyncResult sellerCs) {
        if (sellerCs == null) {
            return new AfterServiceInfo(DEFAULT_AS_PHONE, DEFAULT_AS_GUIDE);
        }
        String phone = sellerCs.csPhone();
        if (phone == null || phone.isBlank()) {
            phone = sellerCs.csMobile();
        }
        if (phone == null || phone.isBlank()) {
            phone = DEFAULT_AS_PHONE;
        }
        return new AfterServiceInfo(phone, DEFAULT_AS_GUIDE);
    }

    private String mapDetailContent(ProductGroupSyncData syncData) {
        return syncData.descriptionContent()
                .map(content -> (content != null && !content.isBlank()) ? content : "")
                .orElse("");
    }

    private int resolveRepresentativePrice(List<ProductResult> products) {
        return products.stream().mapToInt(ProductResult::currentPrice).min().orElse(0);
    }

    /**
     * 내부 상품 상태 -> 네이버 statusType 매핑.
     *
     * <p>네이버 수정 API에서는 SALE, SUSPENSION만 입력 가능합니다. OUTOFSTOCK(품절)은 stockQuantity=0일 때 네이버가 자동으로
     * 설정하므로, SOLD_OUT은 SALE로 보내고 재고를 0으로 전송합니다.
     */
    private String mapNaverStatusType(ProductGroupSyncData syncData) {
        String status = syncData.status();
        return switch (status) {
            case "ACTIVE", "SOLD_OUT" -> "SALE";
            case "INACTIVE" -> "SUSPENSION";
            default -> "SALE";
        };
    }
}
