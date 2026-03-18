package com.ryuqq.marketplace.application.legacyconversion.internal;

import com.ryuqq.marketplace.application.cancel.manager.CancelCommandManager;
import com.ryuqq.marketplace.application.legacyconversion.dto.bundle.LegacyOrderConversionBundle;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingCommandManager;
import com.ryuqq.marketplace.application.order.manager.OrderCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundCommandManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 변환 결과 저장 Facade.
 *
 * <p>Order + Cancel/Refund + Mapping을 하나의 트랜잭션으로 저장합니다.
 * 한 건의 주문에 관련된 모든 데이터가 원자적으로 저장되어야 하므로
 * 이 단위로 트랜잭션을 묶습니다.
 */
@Component
public class LegacyOrderPersistenceFacade {

    private final OrderCommandManager orderCommandManager;
    private final CancelCommandManager cancelCommandManager;
    private final RefundCommandManager refundCommandManager;
    private final LegacyOrderIdMappingCommandManager mappingCommandManager;

    public LegacyOrderPersistenceFacade(
            OrderCommandManager orderCommandManager,
            CancelCommandManager cancelCommandManager,
            RefundCommandManager refundCommandManager,
            LegacyOrderIdMappingCommandManager mappingCommandManager) {
        this.orderCommandManager = orderCommandManager;
        this.cancelCommandManager = cancelCommandManager;
        this.refundCommandManager = refundCommandManager;
        this.mappingCommandManager = mappingCommandManager;
    }

    /**
     * 변환 번들을 하나의 트랜잭션으로 저장합니다.
     *
     * @param bundle 변환 결과 번들
     */
    @Transactional
    public void persist(LegacyOrderConversionBundle bundle) {
        orderCommandManager.persist(bundle.order());

        if (bundle.hasCancel()) {
            cancelCommandManager.persist(bundle.cancel());
        }

        if (bundle.hasRefund()) {
            refundCommandManager.persist(bundle.refundClaim());
        }

        mappingCommandManager.persist(bundle.mapping());
    }
}
