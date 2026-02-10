package com.ryuqq.marketplace.domain.saleschannel.exception;

/** 판매채널 미존재 예외. */
public class SalesChannelNotFoundException extends SalesChannelException {

    private static final SalesChannelErrorCode ERROR_CODE =
            SalesChannelErrorCode.SALES_CHANNEL_NOT_FOUND;

    public SalesChannelNotFoundException() {
        super(ERROR_CODE);
    }

    public SalesChannelNotFoundException(Long salesChannelId) {
        super(ERROR_CODE, String.format("ID가 %d인 판매채널을 찾을 수 없습니다", salesChannelId));
    }
}
