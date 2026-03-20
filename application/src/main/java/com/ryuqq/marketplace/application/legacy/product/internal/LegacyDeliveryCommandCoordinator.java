package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductDeliveryCommandManager;
import com.ryuqq.marketplace.domain.legacy.productdelivery.aggregate.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import org.springframework.stereotype.Component;

/**
 * 레거시 배송정보 Coordinator.
 *
 * <p>배송정보 등록 및 수정 라이프사이클을 관리합니다.
 * LegacyProductDelivery는 독립 aggregate로 분리되었으므로 직접 저장합니다.
 */
@Component
public class LegacyDeliveryCommandCoordinator {

    private final LegacyProductDeliveryCommandManager deliveryCommandManager;

    public LegacyDeliveryCommandCoordinator(
            LegacyProductDeliveryCommandManager deliveryCommandManager) {
        this.deliveryCommandManager = deliveryCommandManager;
    }

    /** 배송정보 등록 (상품그룹 등록 시 사용). */
    public void register(LegacyProductGroupId groupId, LegacyProductDelivery delivery) {
        deliveryCommandManager.persist(groupId, delivery);
    }

    /** 배송정보 수정. */
    public void update(LegacyProductGroupId groupId, LegacyProductDelivery delivery) {
        deliveryCommandManager.persist(groupId, delivery);
    }
}
