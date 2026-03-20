package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceBasicInfoAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceDescriptionAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceImageAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceNoticeAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceProductAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 부분 수정 실행기.
 *
 * <p>changedAreas에 포함된 영역만 개별 API로 호출합니다.
 *
 * <ul>
 *   <li>BASIC_INFO → PATCH /basic-info
 *   <li>PRICE, STOCK, OPTION → PATCH /products/product-groups/{id} (상품+옵션 일괄)
 *   <li>IMAGE → PUT /images
 *   <li>DESCRIPTION → PUT /description
 *   <li>NOTICE → PUT /notice
 * </ul>
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
@edu.umd.cs.findbugs.annotations.SuppressFBWarnings(
        value = "EI_EXPOSE_REP2",
        justification = "Spring-managed bean injection")
public class SetofPartialProductUpdateExecutor implements SetofProductUpdateExecutor {

    private static final Logger log =
            LoggerFactory.getLogger(SetofPartialProductUpdateExecutor.class);

    private final SetofCommerceBasicInfoAdapter basicInfoAdapter;
    private final SetofCommerceProductAdapter productAdapter;
    private final SetofCommerceImageAdapter imageAdapter;
    private final SetofCommerceDescriptionAdapter descriptionAdapter;
    private final SetofCommerceNoticeAdapter noticeAdapter;
    private final SetofCommerceProductMapper mapper;

    public SetofPartialProductUpdateExecutor(
            SetofCommerceBasicInfoAdapter basicInfoAdapter,
            SetofCommerceProductAdapter productAdapter,
            SetofCommerceImageAdapter imageAdapter,
            SetofCommerceDescriptionAdapter descriptionAdapter,
            SetofCommerceNoticeAdapter noticeAdapter,
            SetofCommerceProductMapper mapper) {
        this.basicInfoAdapter = basicInfoAdapter;
        this.productAdapter = productAdapter;
        this.imageAdapter = imageAdapter;
        this.descriptionAdapter = descriptionAdapter;
        this.noticeAdapter = noticeAdapter;
        this.mapper = mapper;
    }

    @Override
    public boolean supports(Set<ChangedArea> changedAreas) {
        return changedAreas != null && !changedAreas.isEmpty();
    }

    @Override
    public void execute(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas,
            SetofProductGroupDetailResponse existingProduct) {

        Long externalId;
        try {
            externalId = Long.valueOf(externalProductId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "유효하지 않은 externalProductId: " + externalProductId, e);
        }

        log.info(
                "세토프 부분 수정 실행: externalProductId={}, changedAreas={}",
                externalProductId,
                changedAreas);

        if (changedAreas.contains(ChangedArea.BASIC_INFO)) {
            updateBasicInfo(syncData, externalCategoryId, externalBrandId, externalProductId);
        }

        if (containsAny(changedAreas, ChangedArea.PRICE, ChangedArea.STOCK, ChangedArea.OPTION)) {
            updateProducts(syncData, externalId, existingProduct);
        }

        if (changedAreas.contains(ChangedArea.IMAGE)) {
            updateImages(syncData, externalId);
        }

        if (changedAreas.contains(ChangedArea.DESCRIPTION)) {
            updateDescription(syncData, externalId);
        }

        if (changedAreas.contains(ChangedArea.NOTICE)) {
            updateNotice(syncData, externalId);
        }

        log.info("세토프 부분 수정 완료: externalProductId={}", externalProductId);
    }

    private void updateBasicInfo(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId) {
        SetofProductGroupBasicInfoUpdateRequest request =
                mapper.toBasicInfoUpdateRequest(syncData, externalCategoryId, externalBrandId);
        basicInfoAdapter.updateBasicInfo(externalProductId, request);
    }

    private void updateProducts(
            ProductGroupSyncData syncData,
            Long externalId,
            SetofProductGroupDetailResponse existingProduct) {
        SetofProductsUpdateRequest request =
                mapper.toProductsUpdateRequest(
                        syncData.products(), syncData.optionGroups(), existingProduct);
        productAdapter.updateProducts(externalId, request);
    }

    private void updateImages(ProductGroupSyncData syncData, Long externalId) {
        SetofImagesRequest request = mapper.toImagesRequest(syncData.images());
        imageAdapter.updateImages(externalId, request);
    }

    private void updateDescription(ProductGroupSyncData syncData, Long externalId) {
        SetofDescriptionRequest request = mapper.toDescriptionRequest(syncData);
        if (request != null) {
            descriptionAdapter.updateDescription(externalId, request);
        }
    }

    private void updateNotice(ProductGroupSyncData syncData, Long externalId) {
        SetofNoticeRequest request =
                syncData.notice().map(notice -> mapper.toNoticeRequest(notice, null)).orElse(null);
        if (request != null) {
            noticeAdapter.updateNotice(externalId, request);
        }
    }

    private static boolean containsAny(Set<ChangedArea> areas, ChangedArea... targets) {
        for (ChangedArea target : targets) {
            if (areas.contains(target)) {
                return true;
            }
        }
        return false;
    }
}
