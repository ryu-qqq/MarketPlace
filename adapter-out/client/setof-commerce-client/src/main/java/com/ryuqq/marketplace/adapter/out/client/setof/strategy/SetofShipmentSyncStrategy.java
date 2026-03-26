package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.adapter.out.client.setof.adapter.SetofCommerceClaimClientAdapter;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceBadRequestException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceClientException;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceServerException;
import com.ryuqq.marketplace.application.common.dto.result.OutboxSyncResult;
import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 배송 상태 동기화 전략.
 *
 * <p>배송 Outbox 유형에 따라 세토프 Admin API v2를 호출합니다.
 *
 * <ul>
 *   <li>CONFIRM: POST /api/v2/orders/{orderItemId}/confirm → 주문 확인 후 ready-to-ship 순차 호출
 *   <li>SHIP: 운송장 등록 → 세토프 자체 배송 처리 (별도 API 없음, 성공 처리)
 *   <li>DELIVER: 배송완료 → 세토프 자동 처리
 *   <li>CANCEL: 배송취소 → Cancel Outbox에서 처리
 * </ul>
 */
@Component
@ConditionalOnProperty(
        prefix = "setof-commerce.claim-sync",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class SetofShipmentSyncStrategy implements ShipmentSyncStrategy {

    private static final Logger log = LoggerFactory.getLogger(SetofShipmentSyncStrategy.class);

    private final SetofCommerceClaimClientAdapter claimClient;

    public SetofShipmentSyncStrategy(SetofCommerceClaimClientAdapter claimClient) {
        this.claimClient = claimClient;
    }

    @Override
    public String channelCode() {
        return "SETOF";
    }

    @Override
    public OutboxSyncResult execute(ShipmentOutbox outbox, Shop shop) {
        ShipmentOutboxType type = outbox.outboxType();

        try {
            switch (type) {
                case CONFIRM -> {
                    claimClient.confirmOrder(shop, outbox.orderItemIdValue());
                    claimClient.readyToShip(shop, outbox.orderItemIdValue());
                }
                case SHIP -> {
                    log.info(
                            "세토프 운송장 등록 - 세토프 자체 배송 관리: orderItemId={}", outbox.orderItemIdValue());
                }
                case DELIVER -> {
                    log.info("세토프 배송완료 - 자동 처리: orderItemId={}", outbox.orderItemIdValue());
                }
                case CANCEL -> {
                    log.info(
                            "세토프 배송취소 - Cancel Outbox에서 처리: orderItemId={}",
                            outbox.orderItemIdValue());
                }
            }

            return OutboxSyncResult.success();

        } catch (SetofCommerceBadRequestException | SetofCommerceClientException e) {
            log.warn(
                    "세토프 배송 동기화 실패 (재시도 불가): orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage());
            return OutboxSyncResult.failure(false, e.getMessage());
        } catch (SetofCommerceServerException e) {
            log.warn(
                    "세토프 배송 동기화 실패 (재시도 가능): orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage());
            return OutboxSyncResult.failure(true, e.getMessage());
        } catch (Exception e) {
            log.error(
                    "세토프 배송 동기화 중 예외: orderItemId={}, type={}, error={}",
                    outbox.orderItemIdValue(),
                    type,
                    e.getMessage(),
                    e);
            return OutboxSyncResult.failure(true, e.getMessage());
        }
    }
}
