package com.ryuqq.marketplace.adapter.out.client.naver.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.naver.adapter.NaverCommerceOrderClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDispatchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDispatchRequest.DispatchProductOrder;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.naver.exception.NaverCommerceServerException;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 배송 상태 동기화 전략.
 *
 * <p>배송 Outbox 유형에 따라 네이버 주문 API를 호출합니다.
 *
 * <ul>
 *   <li>CONFIRM: 발주확인 (POST /v1/pay-order/seller/product-orders/confirm)
 *   <li>SHIP: 발송처리 (POST /v1/pay-order/seller/product-orders/dispatch)
 *   <li>DELIVER: 배송완료 (네이버에서 자동 처리, API 호출 불필요)
 *   <li>CANCEL: 배송 취소 (네이버 취소 API가 담당, 여기서는 성공 처리)
 * </ul>
 */
@Component
@ConditionalOnBean(NaverCommerceOrderClientAdapter.class)
public class NaverShipmentSyncStrategy implements ShipmentSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(NaverShipmentSyncStrategy.class);
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final NaverCommerceOrderClientAdapter orderClient;
    private final ExternalOrderItemMappingQueryPort mappingQueryPort;
    private final ObjectMapper objectMapper;

    public NaverShipmentSyncStrategy(
            NaverCommerceOrderClientAdapter orderClient,
            ExternalOrderItemMappingQueryPort mappingQueryPort,
            ObjectMapper objectMapper) {
        this.orderClient = orderClient;
        this.mappingQueryPort = mappingQueryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public String channelCode() {
        return "NAVER";
    }

    @Override
    public OutboxSyncResult execute(ShipmentOutbox outbox) {
        String externalProductOrderId = resolveExternalProductOrderId(outbox.orderItemIdValue());
        ShipmentOutboxType type = outbox.outboxType();

        try {
            switch (type) {
                case CONFIRM -> {
                    orderClient.confirmOrders(List.of(externalProductOrderId));
                    log.info(
                            "네이버 발주확인 완료: orderItemId={}, externalId={}",
                            outbox.orderItemIdValue(),
                            externalProductOrderId);
                }
                case SHIP -> {
                    DispatchProductOrder dispatchOrder =
                            buildDispatchOrder(externalProductOrderId, outbox.payload());
                    NaverOrderDispatchRequest request =
                            new NaverOrderDispatchRequest(List.of(dispatchOrder));
                    orderClient.dispatchOrders(request);
                    log.info(
                            "네이버 발송처리 완료: orderItemId={}, externalId={}, trackingNumber={}",
                            outbox.orderItemIdValue(),
                            externalProductOrderId,
                            dispatchOrder.trackingNumber());
                }
                case DELIVER -> {
                    // 네이버에서 배송완료는 택배사 연동으로 자동 처리
                    log.info("배송완료 - 네이버 자동 처리: orderItemId={}", outbox.orderItemIdValue());
                }
                case CANCEL -> {
                    // 배송 취소는 Cancel Outbox에서 처리
                    log.info(
                            "배송취소 - Cancel Outbox에서 처리: orderItemId={}", outbox.orderItemIdValue());
                }
            }

            return OutboxSyncResult.success();

        } catch (NaverCommerceBadRequestException | NaverCommerceClientException e) {
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (NaverCommerceServerException | NaverCommerceRateLimitException e) {
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "배송 동기화 중 예외: orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage(),
                    e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }

    private DispatchProductOrder buildDispatchOrder(String externalProductOrderId, String payload) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            String trackingNumber =
                    node.has("trackingNumber") ? node.get("trackingNumber").asText() : null;
            String courierCode = node.has("courierCode") ? node.get("courierCode").asText() : null;
            String dispatchDate = formatForNaver(Instant.now());

            return new DispatchProductOrder(
                    externalProductOrderId, "DELIVERY", courierCode, trackingNumber, dispatchDate);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("배송 Outbox 페이로드 파싱 실패: " + payload, e);
        }
    }

    private String resolveExternalProductOrderId(String orderItemId) {
        return mappingQueryPort
                .findByOrderItemId(orderItemId)
                .map(m -> m.externalProductOrderId())
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "외부 상품주문 매핑을 찾을 수 없습니다. orderItemId=" + orderItemId));
    }

    private String formatForNaver(Instant instant) {
        return instant.atZone(KST).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
