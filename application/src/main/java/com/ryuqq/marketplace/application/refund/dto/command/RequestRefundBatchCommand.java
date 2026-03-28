package com.ryuqq.marketplace.application.refund.dto.command;

import com.ryuqq.marketplace.domain.refund.vo.RefundReasonType;
import java.util.List;

/** 환불 요청 일괄 처리 명령. */
public record RequestRefundBatchCommand(
        List<RefundRequestItem> items, String requestedBy, long sellerId) {

    public record RefundRequestItem(
            Long orderItemId, int refundQty, RefundReasonType reasonType, String reasonDetail) {}
}
