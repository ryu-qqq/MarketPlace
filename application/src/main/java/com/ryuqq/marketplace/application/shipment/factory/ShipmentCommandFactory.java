package com.ryuqq.marketplace.application.shipment.factory;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Shipment Command Factory.
 *
 * <p>Command DTO를 Domain 객체 및 Context로 변환합니다.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 *
 * <p>APP-FAC-001: 상태변경은 StatusChangeContext/BulkStatusChangeContext, 수정은 UpdateContext 사용.
 */
@Component
public class ShipmentCommandFactory {

    private final TimeProvider timeProvider;

    public ShipmentCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /**
     * 발주확인 배치 컨텍스트 생성.
     *
     * @param command 발주확인 배치 Command
     * @return BulkStatusChangeContext (ShipmentId 목록 + changedAt)
     */
    public BulkStatusChangeContext<ShipmentId> createConfirmContexts(
            ConfirmShipmentBatchCommand command) {
        Instant changedAt = timeProvider.now();
        List<ShipmentId> ids = command.shipmentIds().stream().map(ShipmentId::of).toList();
        return new BulkStatusChangeContext<>(ids, changedAt);
    }

    /**
     * 송장등록 배치 컨텍스트 목록 생성.
     *
     * @param command 송장등록 배치 Command
     * @return UpdateContext 목록 (ShipmentId + ShipmentShipData + changedAt)
     */
    public List<UpdateContext<ShipmentId, ShipmentShipData>> createShipContexts(
            ShipBatchCommand command) {
        Instant changedAt = timeProvider.now();
        return command.items().stream()
                .map(item -> createShipItemContext(item, changedAt))
                .toList();
    }

    /**
     * 단건 송장등록 컨텍스트 생성.
     *
     * @param command 단건 송장등록 Command
     * @return UpdateContext (orderId 기반이므로 id는 null, ShipmentShipData + changedAt)
     */
    public ShipSingleContext createShipSingleContext(ShipSingleCommand command) {
        Instant changedAt = timeProvider.now();
        ShipmentMethod method =
                createShipmentMethod(
                        command.shipmentMethodType(), command.courierCode(), command.courierName());
        ShipmentShipData shipData = ShipmentShipData.of(command.trackingNumber(), method);
        return new ShipSingleContext(command.orderId(), shipData, changedAt);
    }

    /**
     * ShipmentMethod 생성.
     *
     * @param shipmentMethodType 배송 방법 유형 문자열
     * @param courierCode 택배사 코드
     * @param courierName 택배사명
     * @return ShipmentMethod
     */
    public ShipmentMethod createShipmentMethod(
            String shipmentMethodType, String courierCode, String courierName) {
        ShipmentMethodType type = resolveMethodType(shipmentMethodType);
        return ShipmentMethod.of(type, courierCode, courierName);
    }

    /**
     * 단건 송장등록 컨텍스트.
     *
     * <p>orderId 기반 조회이므로 ShipmentId 대신 orderId를 사용합니다.
     *
     * @param orderId 주문 ID
     * @param shipData 송장 데이터
     * @param changedAt 변경 시간
     */
    public record ShipSingleContext(String orderId, ShipmentShipData shipData, Instant changedAt) {}

    private UpdateContext<ShipmentId, ShipmentShipData> createShipItemContext(
            ShipBatchItem item, Instant changedAt) {
        ShipmentId shipmentId = ShipmentId.of(item.shipmentId());
        ShipmentMethod method =
                createShipmentMethod(
                        item.shipmentMethodType(), item.courierCode(), item.courierName());
        ShipmentShipData shipData = ShipmentShipData.of(item.trackingNumber(), method);
        return new UpdateContext<>(shipmentId, shipData, changedAt);
    }

    private ShipmentMethodType resolveMethodType(String typeString) {
        if (typeString == null || typeString.isBlank()) {
            return ShipmentMethodType.COURIER;
        }

        for (ShipmentMethodType type : ShipmentMethodType.values()) {
            if (type.name().equalsIgnoreCase(typeString)) {
                return type;
            }
        }

        return ShipmentMethodType.COURIER;
    }
}
