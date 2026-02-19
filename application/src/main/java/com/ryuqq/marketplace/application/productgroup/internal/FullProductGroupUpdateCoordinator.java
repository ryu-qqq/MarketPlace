package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 전체 Aggregate 수정 Coordinator.
 *
 * <p>ProductGroup 기본 정보 수정 → per-package Coordinator 위임 순서로 전체 수정을 조율합니다.
 *
 * <p>ProductGroup 기본 정보는 {@link ProductGroupCommandCoordinator}에 위임합니다.
 *
 * <p>등록 플로우는 {@link FullProductGroupRegistrationCoordinator}를 사용합니다.
 */
@Component
public class FullProductGroupUpdateCoordinator {

    private final ProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final ImageCommandCoordinator imageCommandCoordinator;
    private final SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;
    private final ProductCommandCoordinator productCommandCoordinator;

    public FullProductGroupUpdateCoordinator(
            ProductGroupCommandCoordinator productGroupCommandCoordinator,
            ImageCommandCoordinator imageCommandCoordinator,
            SellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            DescriptionCommandCoordinator descriptionCommandCoordinator,
            ProductNoticeCommandCoordinator noticeCommandCoordinator,
            ProductCommandCoordinator productCommandCoordinator) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
    }

    /**
     * 상품 그룹 전체 수정을 조율합니다.
     *
     * <p>번들에 포함된 per-package Update Command를 사용하여 각 도메인을 독립적으로 수정합니다.
     *
     * @param bundle 수정 번들 (ProductGroupUpdateData + per-package Update Commands)
     */
    @Transactional
    public void update(ProductGroupUpdateBundle bundle) {
        // 1. ProductGroup 기본 정보 (검증 + 조회 + update + persist) → Coordinator
        productGroupCommandCoordinator.update(bundle.basicInfoUpdateData());

        // 2. Images → Coordinator
        imageCommandCoordinator.update(bundle.imageCommand());

        // 3. OptionGroups → Coordinator (diff 기반, ID 보존)
        SellerOptionUpdateResult optionResult =
                sellerOptionCommandCoordinator.update(bundle.optionGroupCommand());

        // 4. Description → Coordinator
        descriptionCommandCoordinator.update(bundle.descriptionCommand());

        // 5. Notice → Coordinator
        noticeCommandCoordinator.update(bundle.noticeCommand());

        // 6. Products → Coordinator (productId 기반 diff 매칭)
        productCommandCoordinator.updateWithDiff(
                bundle.basicInfoUpdateData().productGroupId(),
                bundle.productEntries(),
                optionResult);
    }
}
