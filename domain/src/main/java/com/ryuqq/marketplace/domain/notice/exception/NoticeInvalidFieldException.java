package com.ryuqq.marketplace.domain.notice.exception;

import java.util.List;

/** 고시정보 카테고리에 존재하지 않는 필드가 포함된 경우 예외. */
public class NoticeInvalidFieldException extends NoticeException {

    public NoticeInvalidFieldException(List<Long> invalidFieldIds) {
        super(
                NoticeErrorCode.NOTICE_INVALID_FIELD,
                String.format("고시정보 카테고리에 존재하지 않는 필드입니다: %s", invalidFieldIds));
    }
}
