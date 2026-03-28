package com.ryuqq.marketplace.adapter.out.client.sellic.strategy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.out.client.sellic.adapter.SellicCommerceOrderClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.sellic.client.SellicCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.sellic.config.SellicCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicShipmentRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicShipmentRequest.SellicShipEntry;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicShipmentResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceRateLimitException;
import com.ryuqq.marketplace.adapter.out.client.sellic.exception.SellicCommerceServerException;
import com.ryuqq.marketplace.application.claimsync.port.out.query.ExternalOrderItemMappingQueryPort;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

/**
 * 셀릭 커머스 배송 상태 동기화 전략.
 *
 * <p>배송 Outbox 유형에 따라 셀릭 API를 호출합니다.
 *
 * <ul>
 *   <li>CONFIRM: 셀릭에서는 별도 발주확인 API 없음 → 성공 처리
 *   <li>SHIP: 송장등록 (POST /openapi/set_ship)
 *   <li>DELIVER: 셀릭에서 자동 처리 → 성공 처리
 *   <li>CANCEL: 셀릭 UI에서 직접 처리 → 성공 처리
 * </ul>
 */
@Component
@ConditionalOnBean(SellicCommerceOrderClientAdapter.class)
public class SellicShipmentSyncStrategy implements ShipmentSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SellicShipmentSyncStrategy.class);

    private final SellicCommerceApiClient apiClient;
    private final SellicCommerceProperties properties;
    private final ExternalOrderItemMappingQueryPort mappingQueryPort;
    private final ObjectMapper objectMapper;

    public SellicShipmentSyncStrategy(
            SellicCommerceApiClient apiClient,
            SellicCommerceProperties properties,
            ExternalOrderItemMappingQueryPort mappingQueryPort,
            ObjectMapper objectMapper) {
        this.apiClient = apiClient;
        this.properties = properties;
        this.mappingQueryPort = mappingQueryPort;
        this.objectMapper = objectMapper;
    }

    @Override
    public String channelCode() {
        return "SELLIC";
    }

    @Override
    public OutboxSyncResult execute(ShipmentOutbox outbox, Shop shop) {
        ShipmentOutboxType type = outbox.outboxType();

        try {
            switch (type) {
                case CONFIRM -> {
                    // 셀릭에서는 별도 발주확인 API 없음
                    log.info(
                            "셀릭 발주확인 - 별도 API 없음 (자동 성공): orderItemId={}",
                            outbox.orderItemIdValue());
                }
                case SHIP -> {
                    registerShipment(outbox);
                }
                case DELIVER -> {
                    log.info("셀릭 배송완료 - 자동 처리: orderItemId={}", outbox.orderItemIdValue());
                }
                case CANCEL -> {
                    log.info("셀릭 배송취소 - UI에서 직접 처리: orderItemId={}", outbox.orderItemIdValue());
                }
            }

            return OutboxSyncResult.success();

        } catch (SellicCommerceBadRequestException | SellicCommerceClientException e) {
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (SellicCommerceServerException | SellicCommerceRateLimitException e) {
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            log.error(
                    "셀릭 배송 동기화 중 예외: orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage(),
                    e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }

    private void registerShipment(ShipmentOutbox outbox) {
        String externalOrderId = resolveExternalOrderId(outbox.orderItemIdValue());
        int sellicOrderId = Integer.parseInt(externalOrderId);

        String payload = outbox.payload();
        String trackingNumber = extractFromPayload(payload, "trackingNumber");
        String courierCode = extractFromPayload(payload, "courierCode");
        int sellicCourierCode = SellicCourierCodeResolver.resolve(courierCode);

        SellicShipEntry shipEntry =
                new SellicShipEntry(sellicOrderId, sellicCourierCode, trackingNumber, null);

        SellicShipmentRequest request =
                new SellicShipmentRequest(
                        properties.getCustomerId(), properties.getApiKey(), List.of(shipEntry));

        SellicShipmentResponse response = apiClient.registerShipment(request);

        if (!response.isSuccess()) {
            throw new IllegalStateException(
                    "셀릭 송장 등록 실패: orderId=" + sellicOrderId + ", message=" + response.message());
        }

        log.info(
                "셀릭 송장 등록 성공: orderItemId={}, sellicOrderId={}, trackingNumber={}, courier={}",
                outbox.orderItemIdValue(),
                sellicOrderId,
                trackingNumber,
                sellicCourierCode);
    }

    private String resolveExternalOrderId(Long orderItemId) {
        return mappingQueryPort
                .findByOrderItemId(orderItemId)
                .map(m -> m.externalProductOrderId())
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "셀릭 외부 주문 매핑을 찾을 수 없습니다. orderItemId=" + orderItemId));
    }

    private String extractFromPayload(String payload, String field) {
        try {
            JsonNode node = objectMapper.readTree(payload);
            return node.has(field) ? node.get(field).asText() : null;
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            throw new IllegalStateException("셀릭 배송 Outbox 페이로드 파싱 실패: " + payload, e);
        }
    }
}
