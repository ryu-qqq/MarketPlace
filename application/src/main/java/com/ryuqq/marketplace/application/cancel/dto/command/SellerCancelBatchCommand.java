package com.ryuqq.marketplace.application.cancel.dto.command;

import com.ryuqq.marketplace.domain.cancel.vo.CancelReasonType;
import java.util.List;

/** 판매자 취소 일괄 처리 명령. sellerId는 요청한 셀러 ID. */
public record SellerCancelBatchCommand(
        List<SellerCancelItem> items, String requestedBy, long sellerId) {

    public record SellerCancelItem(
            String orderItemId, int cancelQty, CancelReasonType reasonType, String reasonDetail) {}
}
