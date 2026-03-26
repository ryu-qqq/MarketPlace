package com.ryuqq.marketplace.application.order.internal;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.factory.SettlementEntryCommandFactory;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 주문 도메인 정산 Entry 생성 프로세서.
 *
 * <p>구매확정 시 판매 Entry를 생성합니다. UseCase를 거치지 않고 Factory + PersistenceFacade를 직접 사용합니다.
 */
@Component
public class OrderSettlementProcessor {

    private static final Logger log = LoggerFactory.getLogger(OrderSettlementProcessor.class);

    private final SettlementEntryCommandFactory factory;
    private final SettlementEntryPersistenceFacade persistenceFacade;

    public OrderSettlementProcessor(
            SettlementEntryCommandFactory factory,
            SettlementEntryPersistenceFacade persistenceFacade) {
        this.factory = factory;
        this.persistenceFacade = persistenceFacade;
    }

    /** 판매 Entry를 생성하고 저장한다. 실패해도 주문 처리를 막지 않는다. */
    public void createSalesEntry(String orderItemId, long sellerId, int salesAmount) {
        try {
            CreateSalesEntryCommand command =
                    new CreateSalesEntryCommand(orderItemId, sellerId, salesAmount, 0);
            SettlementEntry entry = factory.createSalesEntry(command);
            persistenceFacade.persist(entry);
        } catch (Exception e) {
            log.warn("정산 Entry 생성 실패: orderItemId={}, error={}", orderItemId, e.getMessage());
        }
    }
}
