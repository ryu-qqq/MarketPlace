package com.ryuqq.marketplace.application.shipment.factory;

import com.ryuqq.marketplace.application.common.dto.command.BulkStatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ProcessPendingShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.RecoverTimeoutShipmentOutboxCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand.ShipBatchItem;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentOutboxPayloadBuilder;
import com.ryuqq.marketplace.application.shipment.internal.ShipmentPersistenceBundle;
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

@Component
public class ShipmentCommandFactory {

    private final TimeProvider timeProvider;
    private final IdGeneratorPort idGeneratorPort;

    public ShipmentCommandFactory(TimeProvider timeProvider, IdGeneratorPort idGeneratorPort) {
        this.timeProvider = timeProvider;
        this.idGeneratorPort = idGeneratorPort;
    }

    public BulkStatusChangeContext<OrderItemId> createConfirmContexts(
            ConfirmShipmentBatchCommand command) {
        Instant changedAt = timeProvider.now();
        List<OrderItemId> ids = command.orderItemIds().stream().map(OrderItemId::of).toList();
        return new BulkStatusChangeContext<>(ids, changedAt);
    }

    public ShipmentPersistenceBundle createConfirmBundle(
            List<OrderItem> orderItems, Instant changedAt) {
        List<Shipment> shipments = new ArrayList<>();
        List<ShipmentOutbox> outboxes = new ArrayList<>();

        for (OrderItem item : orderItems) {
            OrderItemId orderItemId = item.id();

            Shipment shipment =
                    Shipment.forNew(
                            ShipmentId.forNew(idGeneratorPort.generate()),
                            ShipmentNumber.generate(),
                            orderItemId,
                            changedAt);
            shipment.prepare(changedAt);
            shipments.add(shipment);

            ShipmentOutbox outbox =
                    ShipmentOutbox.forNew(
                            orderItemId,
                            ShipmentOutboxType.CONFIRM,
                            ShipmentOutboxPayloadBuilder.confirmPayload(),
                            changedAt);
            outboxes.add(outbox);
        }

        return ShipmentPersistenceBundle.of(shipments, outboxes, orderItems);
    }

    public List<UpdateContext<OrderItemId, ShipmentShipData>> createShipContexts(
            ShipBatchCommand command) {
        Instant changedAt = timeProvider.now();
        return command.items().stream()
                .map(item -> createShipItemContext(item, changedAt))
                .toList();
    }

    public ShipSingleContext createShipSingleContext(ShipSingleCommand command) {
        Instant changedAt = timeProvider.now();
        ShipmentMethod method =
                ShipmentMethod.of(
                        ShipmentMethodType.fromString(command.shipmentMethodType()),
                        command.courierCode(),
                        command.courierName());
        ShipmentShipData shipData = ShipmentShipData.of(command.trackingNumber(), method);
        return new ShipSingleContext(OrderItemId.of(command.orderItemId()), shipData, changedAt);
    }

    public Instant resolveBeforeTime(ProcessPendingShipmentOutboxCommand command) {
        return timeProvider.now().minusSeconds(command.delaySeconds());
    }

    public Instant resolveTimeoutThreshold(RecoverTimeoutShipmentOutboxCommand command) {
        return timeProvider.now().minusSeconds(command.timeoutSeconds());
    }

    public StatusChangeContext<Long> createOutboxTransitionContext(Long outboxId) {
        return new StatusChangeContext<>(outboxId, timeProvider.now());
    }

    public record ShipSingleContext(
            OrderItemId orderItemId, ShipmentShipData shipData, Instant changedAt) {}

    private UpdateContext<OrderItemId, ShipmentShipData> createShipItemContext(
            ShipBatchItem item, Instant changedAt) {
        OrderItemId orderItemId = OrderItemId.of(item.orderItemId());
        ShipmentMethod method =
                ShipmentMethod.of(
                        ShipmentMethodType.fromString(item.shipmentMethodType()),
                        item.courierCode(),
                        null);
        ShipmentShipData shipData = ShipmentShipData.of(item.trackingNumber(), method);
        return new UpdateContext<>(orderItemId, shipData, changedAt);
    }
}
