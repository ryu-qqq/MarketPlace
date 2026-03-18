package com.ryuqq.marketplace.adapter.in.rest.refund.dto.request;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/** 환불 보류/보류 해제 일괄 처리 요청 DTO. */
public record HoldRefundBatchApiRequest(
        @NotEmpty List<String> refundClaimIds,
        boolean isHold,
        String memo) {}
