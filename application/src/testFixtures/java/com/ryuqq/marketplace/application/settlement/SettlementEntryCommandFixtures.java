package com.ryuqq.marketplace.application.settlement;

import com.ryuqq.marketplace.application.settlement.entry.dto.command.CompleteSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateReversalEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.CreateSalesEntryCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.HoldSettlementEntryBatchCommand;
import com.ryuqq.marketplace.application.settlement.entry.dto.command.ReleaseSettlementEntryBatchCommand;
import java.util.List;

/**
 * SettlementEntry Application Command 테스트 Fixtures.
 *
 * <p>SettlementEntry 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class SettlementEntryCommandFixtures {

    private SettlementEntryCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final long DEFAULT_SELLER_ID = 100L;
    public static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    public static final String DEFAULT_CLAIM_ID = "claim-test-001";
    public static final int DEFAULT_SALES_AMOUNT = 50000;
    public static final int DEFAULT_COMMISSION_RATE = 1000; // 10%

    // ===== CreateSalesEntryCommand =====

    public static CreateSalesEntryCommand createSalesEntryCommand() {
        return new CreateSalesEntryCommand(
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE);
    }

    public static CreateSalesEntryCommand createSalesEntryCommand(Long orderItemId, long sellerId) {
        return new CreateSalesEntryCommand(
                orderItemId, sellerId, DEFAULT_SALES_AMOUNT, DEFAULT_COMMISSION_RATE);
    }

    public static CreateSalesEntryCommand createSalesEntryCommand(
            Long orderItemId, long sellerId, int salesAmount, int commissionRate) {
        return new CreateSalesEntryCommand(orderItemId, sellerId, salesAmount, commissionRate);
    }

    // ===== CreateReversalEntryCommand =====

    public static CreateReversalEntryCommand createCancelReversalCommand() {
        return createReversalEntryCommand("CANCEL");
    }

    public static CreateReversalEntryCommand createRefundReversalCommand() {
        return createReversalEntryCommand("REFUND");
    }

    public static CreateReversalEntryCommand createExchangeOutReversalCommand() {
        return createReversalEntryCommand("EXCHANGE_OUT");
    }

    public static CreateReversalEntryCommand createExchangeInReversalCommand() {
        return createReversalEntryCommand("EXCHANGE_IN");
    }

    public static CreateReversalEntryCommand createReversalEntryCommand(String claimType) {
        return new CreateReversalEntryCommand(
                DEFAULT_ORDER_ITEM_ID,
                DEFAULT_SELLER_ID,
                DEFAULT_CLAIM_ID,
                claimType,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE);
    }

    public static CreateReversalEntryCommand createReversalEntryCommand(
            Long orderItemId, long sellerId, String claimId, String claimType) {
        return new CreateReversalEntryCommand(
                orderItemId,
                sellerId,
                claimId,
                claimType,
                DEFAULT_SALES_AMOUNT,
                DEFAULT_COMMISSION_RATE);
    }

    // ===== CompleteSettlementEntryBatchCommand =====

    public static CompleteSettlementEntryBatchCommand completeSettlementEntryBatchCommand() {
        return new CompleteSettlementEntryBatchCommand(
                List.of("entry-001", "entry-002", "entry-003"));
    }

    public static CompleteSettlementEntryBatchCommand completeSettlementEntryBatchCommand(
            List<String> entryIds) {
        return new CompleteSettlementEntryBatchCommand(entryIds);
    }

    // ===== HoldSettlementEntryBatchCommand =====

    public static HoldSettlementEntryBatchCommand holdSettlementEntryBatchCommand() {
        return new HoldSettlementEntryBatchCommand(List.of("entry-001", "entry-002"), "분쟁 발생으로 보류");
    }

    public static HoldSettlementEntryBatchCommand holdSettlementEntryBatchCommand(
            List<String> entryIds, String holdReason) {
        return new HoldSettlementEntryBatchCommand(entryIds, holdReason);
    }

    // ===== ReleaseSettlementEntryBatchCommand =====

    public static ReleaseSettlementEntryBatchCommand releaseSettlementEntryBatchCommand() {
        return new ReleaseSettlementEntryBatchCommand(List.of("entry-001", "entry-002"));
    }

    public static ReleaseSettlementEntryBatchCommand releaseSettlementEntryBatchCommand(
            List<String> entryIds) {
        return new ReleaseSettlementEntryBatchCommand(entryIds);
    }
}
