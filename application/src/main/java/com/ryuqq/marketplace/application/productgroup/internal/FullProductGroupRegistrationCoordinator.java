package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 전체 Aggregate 등록 Coordinator.
 *
 * <p>ProductGroup 기본 정보 -> per-package Coordinator 위임 순서로 전체 등록을 조율합니다. 각 per-package Coordinator의
 * Command 기반 register 메서드를 사용하여, 도메인 객체 생성은 각 Coordinator 내부에서 처리됩니다.
 *
 * <p>수정 플로우는 {@link FullProductGroupUpdateCoordinator}를 사용합니다.
 */
@Component
public class FullProductGroupRegistrationCoordinator {

    private final ProductGroupCommandCoordinator productGroupCommandCoordinator;
    private final ImageCommandCoordinator imageCommandCoordinator;
    private final SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    private final DescriptionCommandCoordinator descriptionCommandCoordinator;
    private final ProductNoticeCommandCoordinator noticeCommandCoordinator;
    private final ProductCommandCoordinator productCommandCoordinator;
    private final IntelligenceOutboxCommandManager intelligenceOutboxCommandManager;

    public FullProductGroupRegistrationCoordinator(
            ProductGroupCommandCoordinator productGroupCommandCoordinator,
            ImageCommandCoordinator imageCommandCoordinator,
            SellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            DescriptionCommandCoordinator descriptionCommandCoordinator,
            ProductNoticeCommandCoordinator noticeCommandCoordinator,
            ProductCommandCoordinator productCommandCoordinator,
            IntelligenceOutboxCommandManager intelligenceOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
        this.intelligenceOutboxCommandManager = intelligenceOutboxCommandManager;
    }

    /**
     * 상품 그룹 전체 등록을 조율합니다.
     *
     * <p>번들에 포함된 등록 데이터로 per-package Register Command를 생성하고 각 Coordinator에 위임합니다.
     *
     * @param bundle 등록 번들 (ProductGroup + per-package 등록 데이터)
     * @return 생성된 상품 그룹 ID + 상품 ID 목록
     */
    @Transactional
    public ProductGroupRegistrationResult register(ProductGroupRegistrationBundle bundle) {
        // 1. ProductGroup 기본 정보 (검증 + persist) → Coordinator
        Long productGroupId = productGroupCommandCoordinator.register(bundle.productGroup());

        // 2. Images → Command 기반 등록
        imageCommandCoordinator.register(
                new RegisterProductGroupImagesCommand(productGroupId, bundle.images()));

        // 3. OptionGroups → Command 기반 등록
        List<SellerOptionValueId> allOptionValueIds =
                sellerOptionCommandCoordinator.register(
                        new RegisterSellerOptionGroupsCommand(
                                productGroupId, bundle.optionType(), bundle.optionGroups()));

        // 4. Description → Command 기반 등록
        descriptionCommandCoordinator.register(
                new RegisterProductGroupDescriptionCommand(
                        productGroupId, bundle.descriptionContent()));

        // 5. Notice → Command 기반 등록
        noticeCommandCoordinator.register(
                new RegisterProductNoticeCommand(
                        productGroupId, bundle.noticeCategoryId(), bundle.noticeEntries()));

        // 6. Products → 이름 기반 옵션 resolve 후 등록
        List<Long> productIds =
                productCommandCoordinator.registerWithOptionResolve(
                        productGroupId,
                        bundle.products(),
                        bundle.optionGroups(),
                        allOptionValueIds,
                        bundle.createdAt());

        // 7. Intelligence Outbox 저장 (PENDING)
        IntelligenceOutbox intelligenceOutbox =
                IntelligenceOutbox.forNew(productGroupId, bundle.createdAt());
        intelligenceOutboxCommandManager.persist(intelligenceOutbox);

        return new ProductGroupRegistrationResult(productGroupId, productIds);
    }
}
