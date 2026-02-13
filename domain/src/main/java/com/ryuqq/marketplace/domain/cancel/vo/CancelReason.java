package com.ryuqq.marketplace.domain.cancel.vo;

import com.ryuqq.marketplace.domain.cancel.exception.CancelErrorCode;
import com.ryuqq.marketplace.domain.cancel.exception.CancelException;

/** 취소 사유 Value Object. */
public record CancelReason(CancelReasonType reasonType, String reasonDetail) {

    public CancelReason {
        if (reasonType == null) {
            throw new CancelException(CancelErrorCode.INVALID_CANCEL_REASON, "취소 사유 유형은 필수입니다");
        }
        if (reasonType == CancelReasonType.OTHER
                && (reasonDetail == null || reasonDetail.isBlank())) {
            throw new CancelException(
                    CancelErrorCode.INVALID_CANCEL_REASON, "기타 사유 선택 시 상세 사유는 필수입니다");
        }
    }
}
