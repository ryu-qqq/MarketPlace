package com.ryuqq.marketplace.application.refund.internal;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateReversalEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.factory.SettlementEntryCommandFactory;
import com.ryuqq.marketplace.application.settlement.entry.internal.SettlementEntryPersistenceFacade;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 환불 도메인 정산 역분개 Entry 생성 프로세서.
 *
 * <p>환불 완료 시 역분개 Entry를 생성합니다. UseCase를 거치지 않고 Factory + PersistenceFacade를 직접 사용합니다.
 */
@Component
public class RefundSettlementProcessor {

    private static final Logger log = LoggerFactory.getLogger(RefundSettlementProcessor.class);

    private final SettlementEntryCommandFactory factory;
    private final SettlementEntryPersistenceFacade persistenceFacade;

    public RefundSettlementProcessor(
            SettlementEntryCommandFactory factory,
            SettlementEntryPersistenceFacade persistenceFacade) {
        this.factory = factory;
        this.persistenceFacade = persistenceFacade;
    }

    /** 환불 역분개 Entry를 생성하고 저장한다. 실패해도 클레임 처리를 막지 않는다. */
    public void createReversalEntry(
            String orderItemId, long sellerId, String refundClaimId, int refundAmount) {
        try {
            CreateReversalEntryCommand command =
                    new CreateReversalEntryCommand(
                            orderItemId, sellerId, refundClaimId, "REFUND", refundAmount, 0);
            SettlementEntry entry = factory.createReversalEntry(command);
            persistenceFacade.persist(entry);
        } catch (Exception e) {
            log.warn(
                    "정산 역분개 Entry 생성 실패: refundClaimId={}, error={}",
                    refundClaimId,
                    e.getMessage());
        }
    }
}
