package com.ryuqq.marketplace.domain.shipment.exception;

public class SyncChannelNotSupportedException extends ShipmentException {
    private static final ShipmentErrorCode ERROR_CODE = ShipmentErrorCode.SYNC_CHANNEL_NOT_SUPPORTED;

    public SyncChannelNotSupportedException(String channelCode) {
        super(ERROR_CODE, String.format("지원하지 않는 배송 동기화 채널입니다: %s", channelCode));
    }
}
