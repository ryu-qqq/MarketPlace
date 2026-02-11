package com.ryuqq.marketplace.domain.notice.exception;

/** 고시정보 카테고리를 찾을 수 없을 때 예외. */
public class NoticeCategoryNotFoundException extends NoticeException {

    private static final NoticeErrorCode ERROR_CODE = NoticeErrorCode.NOTICE_CATEGORY_NOT_FOUND;

    public NoticeCategoryNotFoundException() {
        super(ERROR_CODE);
    }

    public NoticeCategoryNotFoundException(Long noticeCategoryId) {
        super(ERROR_CODE, String.format("고시정보 카테고리를 찾을 수 없습니다 (id: %d)", noticeCategoryId));
    }
}
