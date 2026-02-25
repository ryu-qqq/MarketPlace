package com.ryuqq.marketplace.application.legacyproduct.facade;

import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductDeliveryCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductDescriptionCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacyproduct.manager.LegacyProductNoticeCommandManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import org.springframework.stereotype.Component;

/**
 * 레거시 상품그룹 등록 Facade.
 *
 * <p>상품그룹 + 고시정보 + 배송정보 + 상세설명을 일괄 저장합니다.
 */
@Component
public class LegacyProductGroupRegistrationFacade {

    private final LegacyProductGroupCommandManager productGroupCommandManager;
    private final LegacyProductNoticeCommandManager noticeCommandManager;
    private final LegacyProductDeliveryCommandManager deliveryCommandManager;
    private final LegacyProductDescriptionCommandManager descriptionCommandManager;

    public LegacyProductGroupRegistrationFacade(
            LegacyProductGroupCommandManager productGroupCommandManager,
            LegacyProductNoticeCommandManager noticeCommandManager,
            LegacyProductDeliveryCommandManager deliveryCommandManager,
            LegacyProductDescriptionCommandManager descriptionCommandManager) {
        this.productGroupCommandManager = productGroupCommandManager;
        this.noticeCommandManager = noticeCommandManager;
        this.deliveryCommandManager = deliveryCommandManager;
        this.descriptionCommandManager = descriptionCommandManager;
    }

    /** 상품그룹 + 고시정보 + 배송정보 + 상세설명 일괄 저장 후 productGroupId 반환. */
    public Long register(LegacyProductGroup productGroup) {
        Long productGroupId = productGroupCommandManager.persist(productGroup);
        LegacyProductGroupId groupId = LegacyProductGroupId.of(productGroupId);

        noticeCommandManager.persist(groupId, productGroup.notice());
        deliveryCommandManager.persist(groupId, productGroup.delivery());
        descriptionCommandManager.persist(groupId, productGroup.description());

        return productGroupId;
    }
}
