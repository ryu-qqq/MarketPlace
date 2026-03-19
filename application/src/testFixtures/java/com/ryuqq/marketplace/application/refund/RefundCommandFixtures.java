package com.ryuqq.marketplace.application.refund;

import com.ryuqq.marketplace.application.refund.dto.command.ApproveRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.CollectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.ExecuteRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.dto.command.HoldRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.ProcessPendingRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RecoverTimeoutRefundOutboxCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RejectRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand;
import com.ryuqq.marketplace.application.refund.dto.command.RequestRefundBatchCommand.RefundRequestItem;
import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import java.util.List;

/**
 * Refund Application Command 테스트 Fixtures.
 *
 * <p>Refund 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class RefundCommandFixtures {

    private RefundCommandFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_REFUND_CLAIM_ID = "01900000-0000-7000-8000-000000000010";
    private static final String DEFAULT_ORDER_ITEM_ID = "01940001-0000-7000-8000-000000000001";
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    private static final String DEFAULT_REQUESTED_BY = "customer@marketplace.com";
    private static final long DEFAULT_SELLER_ID = 10L;
    private static final Long DEFAULT_SELLER_ID_BOXED = 10L;

    // ===== RequestRefundBatchCommand =====

    public static RequestRefundBatchCommand requestBatchCommand() {
        return new RequestRefundBatchCommand(
                List.of(defaultRefundRequestItem()), DEFAULT_REQUESTED_BY, DEFAULT_SELLER_ID);
    }

    public static RequestRefundBatchCommand requestBatchCommand(List<RefundRequestItem> items) {
        return new RequestRefundBatchCommand(items, DEFAULT_REQUESTED_BY, DEFAULT_SELLER_ID);
    }

    public static RefundRequestItem defaultRefundRequestItem() {
        return new RefundRequestItem(
                DEFAULT_ORDER_ITEM_ID, 1, RefundReasonType.CHANGE_OF_MIND, "단순 변심입니다.");
    }

    public static RefundRequestItem refundRequestItem(String orderItemId, int refundQty) {
        return new RefundRequestItem(orderItemId, refundQty, RefundReasonType.DEFECTIVE, null);
    }

    // ===== ApproveRefundBatchCommand =====

    public static ApproveRefundBatchCommand approveBatchCommand() {
        return new ApproveRefundBatchCommand(
                List.of(DEFAULT_REFUND_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static ApproveRefundBatchCommand approveBatchCommand(List<String> refundClaimIds) {
        return new ApproveRefundBatchCommand(
                refundClaimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static ApproveRefundBatchCommand approveBatchCommandForSuperAdmin(
            List<String> refundClaimIds) {
        return new ApproveRefundBatchCommand(refundClaimIds, DEFAULT_PROCESSED_BY, null);
    }

    // ===== RejectRefundBatchCommand =====

    public static RejectRefundBatchCommand rejectBatchCommand() {
        return new RejectRefundBatchCommand(
                List.of(DEFAULT_REFUND_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static RejectRefundBatchCommand rejectBatchCommand(List<String> refundClaimIds) {
        return new RejectRefundBatchCommand(
                refundClaimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static RejectRefundBatchCommand rejectBatchCommandForSuperAdmin(
            List<String> refundClaimIds) {
        return new RejectRefundBatchCommand(refundClaimIds, DEFAULT_PROCESSED_BY, null);
    }

    // ===== CollectRefundBatchCommand =====

    public static CollectRefundBatchCommand collectBatchCommand() {
        return new CollectRefundBatchCommand(
                List.of(DEFAULT_REFUND_CLAIM_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static CollectRefundBatchCommand collectBatchCommand(List<String> refundClaimIds) {
        return new CollectRefundBatchCommand(
                refundClaimIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    // ===== HoldRefundBatchCommand =====

    public static HoldRefundBatchCommand holdBatchCommand() {
        return new HoldRefundBatchCommand(
                List.of(DEFAULT_REFUND_CLAIM_ID),
                true,
                "추가 확인이 필요합니다.",
                DEFAULT_PROCESSED_BY,
                DEFAULT_SELLER_ID_BOXED);
    }

    public static HoldRefundBatchCommand releaseHoldBatchCommand() {
        return new HoldRefundBatchCommand(
                List.of(DEFAULT_REFUND_CLAIM_ID),
                false,
                null,
                DEFAULT_PROCESSED_BY,
                DEFAULT_SELLER_ID_BOXED);
    }

    public static HoldRefundBatchCommand holdBatchCommand(
            List<String> refundClaimIds, boolean isHold) {
        return new HoldRefundBatchCommand(
                refundClaimIds, isHold, null, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    // ===== ProcessPendingRefundOutboxCommand =====

    public static ProcessPendingRefundOutboxCommand processPendingOutboxCommand() {
        return new ProcessPendingRefundOutboxCommand(100, 5);
    }

    public static ProcessPendingRefundOutboxCommand processPendingOutboxCommand(
            int batchSize, int delaySeconds) {
        return new ProcessPendingRefundOutboxCommand(batchSize, delaySeconds);
    }

    // ===== RecoverTimeoutRefundOutboxCommand =====

    public static RecoverTimeoutRefundOutboxCommand recoverTimeoutOutboxCommand() {
        return new RecoverTimeoutRefundOutboxCommand(50, 300L);
    }

    public static RecoverTimeoutRefundOutboxCommand recoverTimeoutOutboxCommand(
            int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutRefundOutboxCommand(batchSize, timeoutSeconds);
    }

    // ===== ExecuteRefundOutboxCommand =====

    public static ExecuteRefundOutboxCommand executeRefundOutboxCommand() {
        return ExecuteRefundOutboxCommand.of(1L, DEFAULT_ORDER_ITEM_ID, "REQUEST");
    }

    public static ExecuteRefundOutboxCommand executeRefundOutboxCommand(
            Long outboxId, String outboxType) {
        return ExecuteRefundOutboxCommand.of(outboxId, DEFAULT_ORDER_ITEM_ID, outboxType);
    }
}
