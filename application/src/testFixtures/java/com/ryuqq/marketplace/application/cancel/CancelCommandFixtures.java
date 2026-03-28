package com.ryuqq.marketplace.application.cancel;

import com.ryuqq.marketplace.application.cancel.dto.command.ApproveCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.ExecuteCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.ProcessPendingCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RecoverTimeoutCancelOutboxCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.RejectCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand;
import com.ryuqq.marketplace.application.cancel.dto.command.SellerCancelBatchCommand.SellerCancelItem;
import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import java.util.List;

/**
 * Cancel Application Command 테스트 Fixtures.
 *
 * <p>Cancel 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CancelCommandFixtures {

    private CancelCommandFixtures() {}

    // ===== 기본 상수 =====
    private static final String DEFAULT_CANCEL_ID = "01900000-0000-7000-8000-000000000001";
    private static final Long DEFAULT_ORDER_ITEM_ID = 1001L;
    private static final String DEFAULT_PROCESSED_BY = "admin@marketplace.com";
    private static final String DEFAULT_REQUESTED_BY = "buyer@marketplace.com";
    private static final long DEFAULT_SELLER_ID = 10L;
    private static final Long DEFAULT_SELLER_ID_BOXED = 10L;

    // ===== ApproveCancelBatchCommand =====

    public static ApproveCancelBatchCommand approveBatchCommand() {
        return new ApproveCancelBatchCommand(
                List.of(DEFAULT_CANCEL_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static ApproveCancelBatchCommand approveBatchCommand(List<String> cancelIds) {
        return new ApproveCancelBatchCommand(
                cancelIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static ApproveCancelBatchCommand approveBatchCommandForSuperAdmin(
            List<String> cancelIds) {
        return new ApproveCancelBatchCommand(cancelIds, DEFAULT_PROCESSED_BY, null);
    }

    // ===== RejectCancelBatchCommand =====

    public static RejectCancelBatchCommand rejectBatchCommand() {
        return new RejectCancelBatchCommand(
                List.of(DEFAULT_CANCEL_ID), DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static RejectCancelBatchCommand rejectBatchCommand(List<String> cancelIds) {
        return new RejectCancelBatchCommand(
                cancelIds, DEFAULT_PROCESSED_BY, DEFAULT_SELLER_ID_BOXED);
    }

    public static RejectCancelBatchCommand rejectBatchCommandForSuperAdmin(List<String> cancelIds) {
        return new RejectCancelBatchCommand(cancelIds, DEFAULT_PROCESSED_BY, null);
    }

    // ===== SellerCancelBatchCommand =====

    public static SellerCancelBatchCommand sellerCancelBatchCommand() {
        return new SellerCancelBatchCommand(
                List.of(defaultSellerCancelItem()), DEFAULT_REQUESTED_BY, DEFAULT_SELLER_ID);
    }

    public static SellerCancelBatchCommand sellerCancelBatchCommand(List<SellerCancelItem> items) {
        return new SellerCancelBatchCommand(items, DEFAULT_REQUESTED_BY, DEFAULT_SELLER_ID);
    }

    public static SellerCancelItem defaultSellerCancelItem() {
        return new SellerCancelItem(
                DEFAULT_ORDER_ITEM_ID, 2, CancelReasonType.OUT_OF_STOCK, "재고 부족으로 취소합니다.");
    }

    public static SellerCancelItem sellerCancelItem(Long orderItemId, int cancelQty) {
        return new SellerCancelItem(orderItemId, cancelQty, CancelReasonType.CHANGE_OF_MIND, null);
    }

    // ===== ProcessPendingCancelOutboxCommand =====

    public static ProcessPendingCancelOutboxCommand processPendingOutboxCommand() {
        return new ProcessPendingCancelOutboxCommand(100, 5);
    }

    public static ProcessPendingCancelOutboxCommand processPendingOutboxCommand(
            int batchSize, int delaySeconds) {
        return new ProcessPendingCancelOutboxCommand(batchSize, delaySeconds);
    }

    // ===== RecoverTimeoutCancelOutboxCommand =====

    public static RecoverTimeoutCancelOutboxCommand recoverTimeoutOutboxCommand() {
        return new RecoverTimeoutCancelOutboxCommand(50, 300L);
    }

    public static RecoverTimeoutCancelOutboxCommand recoverTimeoutOutboxCommand(
            int batchSize, long timeoutSeconds) {
        return new RecoverTimeoutCancelOutboxCommand(batchSize, timeoutSeconds);
    }

    // ===== ExecuteCancelOutboxCommand =====

    public static ExecuteCancelOutboxCommand executeCancelOutboxCommand() {
        return ExecuteCancelOutboxCommand.of(1L, DEFAULT_ORDER_ITEM_ID, "SELLER_CANCEL");
    }

    public static ExecuteCancelOutboxCommand executeCancelOutboxCommand(
            Long outboxId, String outboxType) {
        return ExecuteCancelOutboxCommand.of(outboxId, DEFAULT_ORDER_ITEM_ID, outboxType);
    }
}
