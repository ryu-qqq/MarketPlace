package com.ryuqq.marketplace.application.productgroup.internal;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.BoundCommands;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 그룹 전체 Aggregate 등록 Coordinator.
 *
 * <p>ProductGroup 기본 정보 → per-package Coordinator 위임 순서로 전체 등록을 조율합니다.
 *
 * <p>ProductGroup 기본 정보는 {@link ProductGroupCommandCoordinator}에 위임합니다.
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
    private final ProductGroupInspectionOutboxCommandManager inspectionOutboxCommandManager;

    public FullProductGroupRegistrationCoordinator(
            ProductGroupCommandCoordinator productGroupCommandCoordinator,
            ImageCommandCoordinator imageCommandCoordinator,
            SellerOptionCommandCoordinator sellerOptionCommandCoordinator,
            DescriptionCommandCoordinator descriptionCommandCoordinator,
            ProductNoticeCommandCoordinator noticeCommandCoordinator,
            ProductCommandCoordinator productCommandCoordinator,
            ProductGroupInspectionOutboxCommandManager inspectionOutboxCommandManager) {
        this.productGroupCommandCoordinator = productGroupCommandCoordinator;
        this.imageCommandCoordinator = imageCommandCoordinator;
        this.sellerOptionCommandCoordinator = sellerOptionCommandCoordinator;
        this.descriptionCommandCoordinator = descriptionCommandCoordinator;
        this.noticeCommandCoordinator = noticeCommandCoordinator;
        this.productCommandCoordinator = productCommandCoordinator;
        this.inspectionOutboxCommandManager = inspectionOutboxCommandManager;
    }

    /**
     * 상품 그룹 전체 등록을 조율합니다.
     *
     * <p>번들에 포함된 per-package Command를 사용하여 각 도메인을 독립적으로 persist합니다.
     *
     * @param bundle 등록 번들 (ProductGroup + per-package Commands + ProductCreations)
     * @return 생성된 상품 그룹 ID
     */
    @Transactional
    public Long register(ProductGroupRegistrationBundle bundle) {
        // 1. ProductGroup 기본 정보 (검증 + persist) → Coordinator
        Long productGroupId = productGroupCommandCoordinator.register(bundle.productGroup());

        // 2. per-package Command에 productGroupId 바인딩
        BoundCommands bound = bundle.bindAll(productGroupId);

        // 3. Images → Coordinator (Factory + persist + outbox)
        imageCommandCoordinator.register(bound.imageCommand());

        // 4. OptionGroups → Coordinator (Factory + Validator + persist)
        List<SellerOptionValueId> allOptionValueIds =
                sellerOptionCommandCoordinator.register(bound.optionGroupCommand());

        // 5. Description → Coordinator (Factory + persist + outbox)
        descriptionCommandCoordinator.register(bound.descriptionCommand());

        // 6. Notice → Coordinator (Factory + Validator + persist)
        noticeCommandCoordinator.register(bound.noticeCommand());

        // 7. Products → Coordinator (Factory + persist)
        List<Long> allOptionValueIdValues =
                allOptionValueIds.stream().map(SellerOptionValueId::value).toList();
        RegisterProductsCommand productCommand =
                bundle.bindProductCommand(productGroupId, allOptionValueIdValues);
        productCommandCoordinator.register(productCommand);

        // 8. 검수 Outbox 저장 (PENDING) — 스케줄러가 비동기로 검수 수행
        inspectionOutboxCommandManager.persist(bound.inspectionOutbox());

        return productGroupId;
    }
}
