package com.ryuqq.marketplace.application.legacy.sellicorder.internal;

import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.legacy.sellicorder.dto.command.IssueSellicLegacyOrderCommand;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderPersistencePort;
import com.ryuqq.marketplace.application.legacy.sellicorder.port.out.SellicLegacyOrderQueryPort;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderConversionOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 셀릭 주문 → luxurydb 저장 + Outbox 생성 Coordinator.
 *
 * <p>ExternalOrderPayload를 받아 중복 체크 → OutboundProduct로 상품 매핑 → luxurydb 레거시 형식 저장 →
 * LegacyOrderConversionOutbox 생성을 조율합니다.
 */
@Component
public class SellicLegacyOrderCoordinator {

    private static final Logger log = LoggerFactory.getLogger(SellicLegacyOrderCoordinator.class);
    private static final long SELLIC_SITE_ID = 2L;
    private static final long SYSTEM_USER_ID = 1L;
    private static final String SITE_NAME = "SEWON";
    private static final String PAYMENT_STATUS = "PAYMENT_COMPLETED";
    private static final String ORDER_STATUS = "PAYMENT_COMPLETED";
    private static final String DELIVERY_STATUS = "DELIVERY_PENDING";

    private final SellicLegacyOrderQueryPort queryPort;
    private final SellicLegacyOrderPersistencePort persistencePort;
    private final OutboundProductReadManager outboundProductReadManager;
    private final LegacyOrderConversionOutboxCommandManager outboxCommandManager;

    public SellicLegacyOrderCoordinator(
            SellicLegacyOrderQueryPort queryPort,
            SellicLegacyOrderPersistencePort persistencePort,
            OutboundProductReadManager outboundProductReadManager,
            LegacyOrderConversionOutboxCommandManager outboxCommandManager) {
        this.queryPort = queryPort;
        this.persistencePort = persistencePort;
        this.outboundProductReadManager = outboundProductReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    /**
     * 중복이 아니면 luxurydb에 저장하고 Outbox를 생성합니다.
     *
     * @return true: 저장 완료, false: 중복으로 스킵
     */
    public boolean issueIfNotDuplicate(
            ExternalOrderPayload payload, long salesChannelId, Instant now) {

        // 주문 아이템이 없으면 스킵
        if (payload.items() == null || payload.items().isEmpty()) {
            return false;
        }

        // 첫 번째 아이템의 IDX로 중복 체크
        ExternalOrderItemPayload firstItem = payload.items().get(0);
        long externalIdx = Long.parseLong(firstItem.externalProductOrderId());

        if (queryPort.existsByExternalIdx(SELLIC_SITE_ID, externalIdx)) {
            return false;
        }

        // 상품 매핑 — OWN_CODE로 OutboundProduct 조회
        String ownCode = firstItem.externalProductId();
        List<OutboundProduct> products =
                outboundProductReadManager.findByExternalProductIdsAndSalesChannelId(
                        Set.of(ownCode), salesChannelId);

        if (products.isEmpty()) {
            log.warn(
                    "셀릭 주문 상품 매핑 실패: externalOrderNo={}, ownCode={}",
                    payload.externalOrderNo(),
                    ownCode);
            return false;
        }

        OutboundProduct outboundProduct = products.get(0);
        long productGroupId = outboundProduct.productGroupIdValue();

        // 아이템별로 주문 생성 (셀릭은 아이템 단위로 orders 테이블에 저장)
        long lastOrderId = 0;
        long lastPaymentId = 0;

        for (ExternalOrderItemPayload item : payload.items()) {
            long itemExternalIdx = Long.parseLong(item.externalProductOrderId());

            // 아이템 단위 중복 체크
            if (queryPort.existsByExternalIdx(SELLIC_SITE_ID, itemExternalIdx)) {
                continue;
            }

            IssueSellicLegacyOrderCommand command =
                    buildCommand(payload, item, productGroupId, outboundProduct, now);

            long orderId = persistencePort.persist(command);
            lastOrderId = orderId;

            log.debug("셀릭 주문 luxurydb 저장: externalIdx={}, orderId={}", itemExternalIdx, orderId);
        }

        // Outbox 생성 (마지막 주문 기준)
        if (lastOrderId > 0) {
            // payment_id는 order 테이블에서 조회해야 하지만,
            // persist()가 단일 트랜잭션이므로 payment_id = orderId 직전에 생성된 payment
            // 여기서는 간소화하여 orderId 기준으로 Outbox 생성
            LegacyOrderConversionOutbox outbox =
                    LegacyOrderConversionOutbox.forNew(lastOrderId, 0L, now);
            outboxCommandManager.persist(outbox);
        }

        return true;
    }

    private IssueSellicLegacyOrderCommand buildCommand(
            ExternalOrderPayload payload,
            ExternalOrderItemPayload item,
            long productGroupId,
            OutboundProduct outboundProduct,
            Instant now) {

        long itemExternalIdx = Long.parseLong(item.externalProductOrderId());
        String paymentUniqueId = "SEWON_" + SELLIC_SITE_ID + "_" + payload.externalOrderNo();

        var payment =
                new IssueSellicLegacyOrderCommand.Payment(
                        SYSTEM_USER_ID,
                        item.paymentAmount(),
                        PAYMENT_STATUS,
                        SITE_NAME,
                        payload.orderedAt(),
                        payload.buyerName() != null ? payload.buyerName() : "",
                        "",
                        payload.buyerPhone() != null ? payload.buyerPhone() : "",
                        paymentUniqueId,
                        "PC");

        var order =
                new IssueSellicLegacyOrderCommand.Order(
                        productGroupId, // productId는 productGroupId로 대체 (옵션 매칭은 컨버전에서 처리)
                        outboundProduct.shopId(),
                        SYSTEM_USER_ID,
                        item.paymentAmount(),
                        ORDER_STATUS,
                        item.quantity());

        var shipment = new IssueSellicLegacyOrderCommand.Shipment("", "", "", DELIVERY_STATUS);

        var settlement = new IssueSellicLegacyOrderCommand.Settlement(0L);

        var externalOrder =
                new IssueSellicLegacyOrderCommand.ExternalOrder(
                        SELLIC_SITE_ID, itemExternalIdx, payload.externalOrderNo());

        var interlockingOrder =
                new IssueSellicLegacyOrderCommand.InterlockingOrder(
                        SELLIC_SITE_ID, SITE_NAME, itemExternalIdx, payload.externalOrderNo());

        var shippingAddress =
                new IssueSellicLegacyOrderCommand.ShippingAddress(
                        item.receiverName() != null ? item.receiverName() : "",
                        item.receiverPhone() != null ? item.receiverPhone() : "",
                        item.receiverAddress() != null ? item.receiverAddress() : "",
                        item.receiverZipCode() != null ? item.receiverZipCode() : "",
                        item.deliveryRequest() != null ? item.deliveryRequest() : "");

        return new IssueSellicLegacyOrderCommand(
                payment,
                order,
                shipment,
                settlement,
                externalOrder,
                interlockingOrder,
                shippingAddress);
    }
}
