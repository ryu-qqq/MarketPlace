package com.ryuqq.marketplace.application.legacy.product.internal;

import com.ryuqq.marketplace.application.legacy.product.manager.LegacyProductDeliveryCommandManager;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.domain.legacy.productdelivery.aggregate.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ReturnMethod;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ShipmentCompanyCode;
import org.springframework.stereotype.Component;

/**
 * 레거시 배송정보 Coordinator.
 *
 * <p>배송정보 등록 및 수정 라이프사이클을 관리합니다.
 */
@Component
public class LegacyDeliveryCommandCoordinator {

    private final LegacyProductDeliveryCommandManager deliveryCommandManager;

    public LegacyDeliveryCommandCoordinator(
            LegacyProductDeliveryCommandManager deliveryCommandManager) {
        this.deliveryCommandManager = deliveryCommandManager;
    }

    /** 배송정보 등록 (상품그룹 등록 시 사용). DeliveryCommand → 레거시 도메인 변환 후 저장. */
    public void register(
            long productGroupId,
            LegacyRegisterProductGroupCommand.DeliveryCommand command) {
        LegacyProductGroupId groupId = LegacyProductGroupId.of(productGroupId);
        LegacyProductDelivery delivery = new LegacyProductDelivery(
                command.deliveryArea(),
                command.deliveryFee(),
                command.deliveryPeriodAverage(),
                ReturnMethod.valueOf(command.returnMethodDomestic()),
                ShipmentCompanyCode.valueOf(command.returnCourierDomestic()),
                command.returnChargeDomestic(),
                command.returnExchangeAreaDomestic());
        deliveryCommandManager.persist(groupId, delivery);
    }

    /** 배송정보 수정. */
    public void update(LegacyProductGroupId groupId, LegacyProductDelivery delivery) {
        deliveryCommandManager.persist(groupId, delivery);
    }
}
