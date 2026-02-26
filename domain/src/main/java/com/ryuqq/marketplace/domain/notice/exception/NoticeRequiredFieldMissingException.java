package com.ryuqq.marketplace.domain.notice.exception;

import java.util.List;

/** 필수 고시정보 필드가 누락된 경우 예외. */
public class NoticeRequiredFieldMissingException extends NoticeException {

    public NoticeRequiredFieldMissingException(List<Long> missingFieldIds) {
        super(
                NoticeErrorCode.NOTICE_REQUIRED_FIELD_MISSING,
                String.format("필수 고시정보 필드가 누락되었습니다: %s", missingFieldIds));
    }
}
