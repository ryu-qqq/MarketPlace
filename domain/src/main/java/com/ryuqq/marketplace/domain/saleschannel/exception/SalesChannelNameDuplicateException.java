package com.ryuqq.marketplace.domain.saleschannel.exception;

/** 판매채널명 중복 예외. */
public class SalesChannelNameDuplicateException extends SalesChannelException {

    private static final SalesChannelErrorCode ERROR_CODE =
            SalesChannelErrorCode.SALES_CHANNEL_NAME_DUPLICATE;

    public SalesChannelNameDuplicateException() {
        super(ERROR_CODE);
    }

    public SalesChannelNameDuplicateException(String channelName) {
        super(ERROR_CODE, String.format("판매채널명 '%s'은(는) 이미 존재합니다", channelName));
    }
}
