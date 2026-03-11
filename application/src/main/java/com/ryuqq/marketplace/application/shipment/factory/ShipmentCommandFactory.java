package com.ryuqq.marketplace.application.shipment.factory;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBundle;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentOutboxPayloadBuilder;
import com.ryuqq.marketplace.domain.order.aggregate.OrderItem;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentNumber;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethod;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentMethodType;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentShipData;
import java.time.Instant;
import java.util.ArrayList;
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
    private final IdGeneratorPort idGeneratorPort;

    public ShipmentCommandFactory(TimeProvider timeProvider, IdGeneratorPort idGeneratorPort) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
    }

    /**
     * 발주확인 배치 컨텍스트 생성.
     *
     * @param command 발주확인 배치 Command
     * @return BulkStatusChangeContext (OrderItemId 목록 + changedAt)
     */
    public BulkStatusChangeContext<OrderItemId> createConfirmContexts(
            ConfirmShipmentBatchCommand command) {
        Instant changedAt = timeProvider.now();
        List<OrderItemId> ids = command.orderItemIds().stream().map(OrderItemId::of).toList();
        return new BulkStatusChangeContext<>(ids, changedAt);
    }

    /**
     * 발주확인 번들 생성.
     *
     * <p>OrderItem 상태변경(READY→CONFIRMED) + Shipment 생성 + ShipmentOutbox 생성을 번들로 구성.
     *
     * @param orderItems 발주확인 대상 주문상품 목록 (이미 confirm() 호출됨)
     * @return ConfirmShipmentBundle
     */
    public ConfirmShipmentBundle createConfirmBundle(List<OrderItem> orderItems) {
        Instant now = timeProvider.now();

        List<Shipment> shipments = new ArrayList<>();
        List<ShipmentOutbox> outboxes = new ArrayList<>();

        for (OrderItem item : orderItems) {
            OrderItemId orderItemId = item.id();

            Shipment shipment =
                    Shipment.forNew(
                            ShipmentId.forNew(idGeneratorPort.generate()),
                            ShipmentNumber.generate(),
                            orderItemId,
                            now);
            shipments.add(shipment);

            ShipmentOutbox outbox =
                    ShipmentOutbox.forNew(
                            orderItemId,
                            ShipmentOutboxType.CONFIRM,
                            ShipmentOutboxPayloadBuilder.confirmPayload(),
                            now);
            outboxes.add(outbox);
        }

        return new ConfirmShipmentBundle(shipments, outboxes, orderItems);
    }

    /**
     * 송장등록 배치 컨텍스트 목록 생성.
     *
     * @param command 송장등록 배치 Command
     * @return UpdateContext 목록 (OrderItemId + ShipmentShipData + changedAt)
     */
    public List<UpdateContext<OrderItemId, ShipmentShipData>> createShipContexts(
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
     * @return ShipSingleContext (orderItemId 기반)
     */
    public ShipSingleContext createShipSingleContext(ShipSingleCommand command) {
        Instant changedAt = timeProvider.now();
        ShipmentMethod method =
                createShipmentMethod(
                        command.shipmentMethodType(), command.courierCode(), command.courierName());
        ShipmentShipData shipData = ShipmentShipData.of(command.trackingNumber(), method);
        return new ShipSingleContext(OrderItemId.of(command.orderItemId()), shipData, changedAt);
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
     * <p>orderItemId 기반 조회를 사용합니다.
     *
     * @param orderItemId 상품주문 ID
     * @param shipData 송장 데이터
     * @param changedAt 변경 시간
     */
    public record ShipSingleContext(
            OrderItemId orderItemId, ShipmentShipData shipData, Instant changedAt) {}

    private UpdateContext<OrderItemId, ShipmentShipData> createShipItemContext(
            ShipBatchItem item, Instant changedAt) {
        OrderItemId orderItemId = OrderItemId.of(item.orderItemId());
        ShipmentMethod method =
                createShipmentMethod(
                        item.shipmentMethodType(), item.courierCode(), item.courierName());
        ShipmentShipData shipData = ShipmentShipData.of(item.trackingNumber(), method);
        return new UpdateContext<>(orderItemId, shipData, changedAt);
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
