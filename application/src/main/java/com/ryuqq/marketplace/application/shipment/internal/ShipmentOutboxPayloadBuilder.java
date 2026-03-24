package com.ryuqq.marketplace.application.shipment.internal;

import com.ryuqq.marketplace.application.common.util.OutboxPayloadUtils;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import java.util.Map;

/**
 * 배송 아웃박스 페이로드 빌더.
 *
 * <p>외부 채널 동기화에 필요한 정보를 JSON 페이로드로 구성합니다.
 */
public final class ShipmentOutboxPayloadBuilder {

    private static final String EMPTY_PAYLOAD = "{}";

    private ShipmentOutboxPayloadBuilder() {}

    public static String confirmPayload() {
        return EMPTY_PAYLOAD;
    }

    public static String shipPayload(ShipBatchItem item) {
        return OutboxPayloadUtils.mapToJson(
                Map.of(
                        "trackingNumber", item.trackingNumber(),
                        "courierCode", item.courierCode()));
    }

    public static String shipPayload(ShipSingleCommand command) {
        return OutboxPayloadUtils.mapToJson(
                Map.of(
                        "trackingNumber", command.trackingNumber(),
                        "courierCode", command.courierCode(),
                        "courierName", command.courierName()));
    }
}
