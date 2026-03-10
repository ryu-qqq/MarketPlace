package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceDescriptionAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceImageAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceNoticeAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceProductAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceProductClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
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
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofPartialProductUpdateExecutor implements SetofProductUpdateExecutor {

    private static final Logger log =
            LoggerFactory.getLogger(SetofPartialProductUpdateExecutor.class);

    private final SetofCommerceProductClientAdapter productClientAdapter;
    private final SetofCommerceProductAdapter productAdapter;
    private final SetofCommerceImageAdapter imageAdapter;
    private final SetofCommerceDescriptionAdapter descriptionAdapter;
    private final SetofCommerceNoticeAdapter noticeAdapter;
    private final SetofCommerceProductMapper mapper;

    public SetofPartialProductUpdateExecutor(
            SetofCommerceProductClientAdapter productClientAdapter,
            SetofCommerceProductAdapter productAdapter,
            SetofCommerceImageAdapter imageAdapter,
            SetofCommerceDescriptionAdapter descriptionAdapter,
            SetofCommerceNoticeAdapter noticeAdapter,
            SetofCommerceProductMapper mapper) {
        this.productClientAdapter = productClientAdapter;
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
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas) {

        Long externalId = Long.valueOf(externalProductId);

        log.info(
                "세토프 부분 수정 실행: externalProductId={}, changedAreas={}",
                externalProductId,
                changedAreas);

        if (changedAreas.contains(ChangedArea.BASIC_INFO)) {
            updateBasicInfo(bundle, externalCategoryId, externalBrandId, externalProductId);
        }

        if (containsAny(changedAreas, ChangedArea.PRICE, ChangedArea.STOCK, ChangedArea.OPTION)) {
            updateProducts(bundle, externalId);
        }

        if (changedAreas.contains(ChangedArea.IMAGE)) {
            updateImages(bundle, externalId);
        }

        if (changedAreas.contains(ChangedArea.DESCRIPTION)) {
            updateDescription(bundle, externalId);
        }

        if (changedAreas.contains(ChangedArea.NOTICE)) {
            updateNotice(bundle, externalId);
        }

        log.info("세토프 부분 수정 완료: externalProductId={}", externalProductId);
    }

    private void updateBasicInfo(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId) {
        SetofProductGroupBasicInfoUpdateRequest request =
                mapper.toBasicInfoUpdateRequest(bundle, externalCategoryId, externalBrandId);
        productClientAdapter.updateBasicInfo(externalProductId, request);
    }

    private void updateProducts(ProductGroupDetailBundle bundle, Long externalId) {
        SetofProductsUpdateRequest request =
                mapper.toProductsUpdateRequest(
                        bundle.products(), bundle.group().sellerOptionGroups());
        productAdapter.updateProducts(externalId, request);
    }

    private void updateImages(ProductGroupDetailBundle bundle, Long externalId) {
        SetofImagesRequest request = mapper.toImagesRequest(bundle.group().images());
        imageAdapter.updateImages(externalId, request);
    }

    private void updateDescription(ProductGroupDetailBundle bundle, Long externalId) {
        SetofDescriptionRequest request = mapper.toDescriptionRequest(bundle);
        if (request != null) {
            descriptionAdapter.updateDescription(externalId, request);
        }
    }

    private void updateNotice(ProductGroupDetailBundle bundle, Long externalId) {
        SetofNoticeRequest request =
                bundle.notice().map(notice -> mapper.toNoticeRequest(notice, null)).orElse(null);
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
