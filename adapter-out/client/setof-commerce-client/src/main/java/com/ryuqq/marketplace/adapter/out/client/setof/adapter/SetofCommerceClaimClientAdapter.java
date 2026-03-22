package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 클레임(취소/반품) + 주문 상태 변경 Adapter.
 *
 * <p>세토프 자사몰 Admin API v2를 호출합니다. HTTP 호출은 {@link SetofCommerceApiClient}에 위임합니다.
 *
 * <ul>
 *   <li>POST /api/v2/orders/{orderItemId}/confirm — 주문 확인
 *   <li>POST /api/v2/orders/{orderItemId}/ready-to-ship — 배송 준비 완료
 *   <li>POST /api/v2/cancels/{cancelId}/approve — 취소 승인
 *   <li>POST /api/v2/cancels/{cancelId}/reject — 취소 거부
 *   <li>POST /api/v2/refunds/{refundId}/complete — 반품 완료
 *   <li>POST /api/v2/refunds/{refundId}/reject — 반품 거부
 * </ul>
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceClaimClientAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceClaimClientAdapter.class);

    private final SetofCommerceApiClient apiClient;

    public SetofCommerceClaimClientAdapter(SetofCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // ===== 주문 =====

    public void confirmOrder(String orderItemId) {
        apiClient.confirmOrder(orderItemId);
        log.info("세토프 주문 확인 완료: orderItemId={}", orderItemId);
    }

    public void readyToShip(String orderItemId) {
        apiClient.readyToShip(orderItemId);
        log.info("세토프 배송 준비 완료: orderItemId={}", orderItemId);
    }

    // ===== 취소 =====

    public void approveCancel(String cancelId) {
        apiClient.approveCancel(cancelId);
        log.info("세토프 취소 승인 완료: cancelId={}", cancelId);
    }

    public void rejectCancel(String cancelId, String rejectReason) {
        apiClient.rejectCancel(cancelId, rejectReason);
        log.info("세토프 취소 거부 완료: cancelId={}, reason={}", cancelId, rejectReason);
    }

    // ===== 반품 =====

    public void completeRefund(String refundId) {
        apiClient.completeRefund(refundId);
        log.info("세토프 반품 완료 처리: refundId={}", refundId);
    }

    public void rejectRefund(String refundId, String rejectReason) {
        apiClient.rejectRefund(refundId, rejectReason);
        log.info("세토프 반품 거부 완료: refundId={}, reason={}", refundId, rejectReason);
    }
}
