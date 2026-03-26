package com.ryuqq.marketplace.application.exchange;

import com.ryuqq.marketplace.application.exchange.dto.command.ApproveExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.CollectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.CompleteExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ConvertToRefundBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ExecuteExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.HoldExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.PrepareExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ProcessPendingExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RecoverTimeoutExchangeOutboxCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RejectExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.RequestExchangeBatchCommand.ExchangeRequestItem;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand;
import com.ryuqq.marketplace.application.exchange.dto.command.ShipExchangeBatchCommand.ShipItem;
import com.ryuqq.marketplace.domain.exchange.vo.ExchangeReasonType;
import java.util.List;

/**
 * Exchange Command 테스트 Fixtures.
 *
 * <p>Exchange 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ExchangeCommandFixtures {

    private ExchangeCommandFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_CLAIM_ID = "01900000-0000-7000-0000-000000000001";
    private static final String DEFAULT_ORDER_ITEM_ID = "01900000-0000-7000-0000-000000000010";
    private static final long DEFAULT_SELLER_ID = 100L;
    private static final String DEFAULT_REQUESTED_BY = "buyer@example.com";
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";

    // ===== RequestExchangeBatchCommand =====

    public static RequestExchangeBatchCommand requestCommand() {
        return new RequestExchangeBatchCommand(
                List.of(exchangeRequestItem()), DEFAULT_REQUESTED_BY, DEFAULT_SELLER_ID);
    }

    public static RequestExchangeBatchCommand requestCommand(List<ExchangeRequestItem> items) {
        return new RequestExchangeBatchCommand(items, DEFAULT_REQUESTED_BY, DEFAULT_SELLER_ID);
    }

    public static ExchangeRequestItem exchangeRequestItem() {
        return new ExchangeRequestItem(
                DEFAULT_ORDER_ITEM_ID,
                1,
                ExchangeReasonType.SIZE_CHANGE,
                "사이즈가 맞지 않아 교환 요청합니다",
                1000L,
                "SKU-RED-M",
                1001L,
                2001L,
                "SKU-RED-XL",
                1);
    }

    public static ExchangeRequestItem exchangeRequestItem(String orderItemId) {
        return new ExchangeRequestItem(
                orderItemId,
                1,
                ExchangeReasonType.SIZE_CHANGE,
                "사이즈가 맞지 않아 교환 요청합니다",
                1000L,
                "SKU-RED-M",
                1001L,
                2001L,
                "SKU-RED-XL",
                1);
    }

    // ===== ApproveExchangeBatchCommand =====

    public static ApproveExchangeBatchCommand approveCommand() {
        return new ApproveExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static ApproveExchangeBatchCommand approveCommand(List<String> claimIds) {
        return new ApproveExchangeBatchCommand(claimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== CollectExchangeBatchCommand =====

    public static CollectExchangeBatchCommand collectCommand() {
        return new CollectExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static CollectExchangeBatchCommand collectCommand(List<String> claimIds) {
        return new CollectExchangeBatchCommand(claimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== PrepareExchangeBatchCommand =====

    public static PrepareExchangeBatchCommand prepareCommand() {
        return new PrepareExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static PrepareExchangeBatchCommand prepareCommand(List<String> claimIds) {
        return new PrepareExchangeBatchCommand(claimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== ShipExchangeBatchCommand =====

    public static ShipExchangeBatchCommand shipCommand() {
        return new ShipExchangeBatchCommand(
                List.of(shipItem()), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static ShipExchangeBatchCommand shipCommand(List<ShipItem> items) {
        return new ShipExchangeBatchCommand(items, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static ShipItem shipItem() {
        return new ShipItem(DEFAULT_CLAIM_ID, "ORDER-20260101-9999", "CJ대한통운", "1234567890");
    }

    public static ShipItem shipItem(String exchangeClaimId) {
        return new ShipItem(exchangeClaimId, "ORDER-20260101-9999", "CJ대한통운", "1234567890");
    }

    // ===== CompleteExchangeBatchCommand =====

    public static CompleteExchangeBatchCommand completeCommand() {
        return new CompleteExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static CompleteExchangeBatchCommand completeCommand(List<String> claimIds) {
        return new CompleteExchangeBatchCommand(claimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== RejectExchangeBatchCommand =====

    public static RejectExchangeBatchCommand rejectCommand() {
        return new RejectExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static RejectExchangeBatchCommand rejectCommand(List<String> claimIds) {
        return new RejectExchangeBatchCommand(claimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== ConvertToRefundBatchCommand =====

    public static ConvertToRefundBatchCommand convertToRefundCommand() {
        return new ConvertToRefundBatchCommand(
                List.of(DEFAULT_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static ConvertToRefundBatchCommand convertToRefundCommand(List<String> claimIds) {
        return new ConvertToRefundBatchCommand(claimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== HoldExchangeBatchCommand =====

    public static HoldExchangeBatchCommand holdCommand() {
        return new HoldExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID),
                true,
                "추가 확인이 필요합니다.",
                DEFAULT_PROCESSED_BY,
                DEFAULT_SELLER_ID);
    }

    public static HoldExchangeBatchCommand releaseHoldCommand() {
        return new HoldExchangeBatchCommand(
                List.of(DEFAULT_CLAIM_ID), false, null, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    public static HoldExchangeBatchCommand holdCommand(List<String> claimIds, boolean isHold) {
        return new HoldExchangeBatchCommand(
                claimIds, isHold, null, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID);
    }

    // ===== ProcessPendingExchangeOutboxCommand =====

    public static ProcessPendingExchangeOutboxCommand processPendingOutboxCommand() {
        return new ProcessPendingExchangeOutboxCommand(100, 5);
    }

    public static ProcessPendingExchangeOutboxCommand processPendingOutboxCommand(
            int batchSize, int delaySeconds) {
        return new ProcessPendingExchangeOutboxCommand(batchSize, delaySeconds);
    }

    // ===== RecoverTimeoutExchangeOutboxCommand =====

    public static RecoverTimeoutExchangeOutboxCommand recoverTimeoutOutboxCommand() {
        return new RecoverTimeoutExchangeOutboxCommand(50, 300L);
    }

    public static RecoverTimeoutExchangeOutboxCommand recoverTimeoutOutboxCommand(
            int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutExchangeOutboxCommand(batchSize, timeoutSeconds);
    }

    // ===== ExecuteExchangeOutboxCommand =====

    public static ExecuteExchangeOutboxCommand executeExchangeOutboxCommand() {
        return ExecuteExchangeOutboxCommand.of(1L, DEFAULT_ORDER_ITEM_ID, "REQUEST");
    }

    public static ExecuteExchangeOutboxCommand executeExchangeOutboxCommand(
            Long outboxId, String outboxType) {
        return ExecuteExchangeOutboxCommand.of(outboxId, DEFAULT_ORDER_ITEM_ID, outboxType);
    }
}
