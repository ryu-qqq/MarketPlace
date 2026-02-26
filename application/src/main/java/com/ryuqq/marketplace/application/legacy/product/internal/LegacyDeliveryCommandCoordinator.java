package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductDeliveryCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroup.manager.LegacyProductGroupReadManager;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDelivery;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * 레거시 배송정보 Coordinator.
 *
 * <p>배송정보 등록 및 수정 라이프사이클을 관리합니다.
 */
@Component
public class LegacyDeliveryCommandCoordinator {

    private final LegacyProductGroupReadManager readManager;
    private final LegacyProductGroupCommandManager commandManager;
    private final LegacyProductDeliveryCommandManager deliveryCommandManager;

    public LegacyDeliveryCommandCoordinator(
            LegacyProductGroupReadManager readManager,
            LegacyProductGroupCommandManager commandManager,
            LegacyProductDeliveryCommandManager deliveryCommandManager) {
        this.readManager = readManager;
        this.commandManager = commandManager;
        this.deliveryCommandManager = deliveryCommandManager;
    }

    /** 배송정보 등록 (상품그룹 등록 시 사용). */
    public void register(LegacyProductGroupId groupId, LegacyProductDelivery delivery) {
        deliveryCommandManager.persist(groupId, delivery);
    }

    /** 배송정보 수정. */
    public void update(
            LegacyProductGroupId groupId, LegacyProductDelivery delivery, Instant changedAt) {
        LegacyProductGroup productGroup = readManager.getById(groupId);
        productGroup.updateDelivery(delivery, changedAt);
        deliveryCommandManager.persist(groupId, productGroup.delivery());
        commandManager.persist(productGroup);
    }
}
