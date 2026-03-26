package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceUnauthorizedException;
import com.ryuqq.marketplace.adapter.out.client.setof.support.SetofSellerTokenProvider;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 클레임(취소/반품) + 주문 상태 변경 Adapter.
 *
 * <p>세토프 자사몰 Admin API v2를 호출합니다. HTTP 호출은 {@link SetofCommerceApiClient}에 위임합니다. 401 응답 시 토큰을
 * 재발급하고 재시도합니다.
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

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceClaimClientAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SetofCommerceProperties properties;
    private final SetofSellerTokenProvider tokenProvider;

    public SetofCommerceClaimClientAdapter(
            SetofCommerceApiClient apiClient,
            SetofCommerceProperties properties,
            SetofSellerTokenProvider tokenProvider) {
        this.apiClient = apiClient;
        this.properties = properties;
        this.tokenProvider = tokenProvider;
    }

    // ===== 주문 =====

    public void confirmOrder(Shop shop, String orderItemId) {
        executeWithTokenRefresh(
                shop,
                token -> {
                    apiClient.confirmOrder(token, orderItemId);
                    return null;
                });
        log.info("세토프 주문 확인 완료: orderItemId={}", orderItemId);
    }

    public void readyToShip(Shop shop, String orderItemId) {
        executeWithTokenRefresh(
                shop,
                token -> {
                    apiClient.readyToShip(token, orderItemId);
                    return null;
                });
        log.info("세토프 배송 준비 완료: orderItemId={}", orderItemId);
    }

    // ===== 취소 =====

    public void approveCancel(Shop shop, String cancelId) {
        executeWithTokenRefresh(
                shop,
                token -> {
                    apiClient.approveCancel(token, cancelId);
                    return null;
                });
        log.info("세토프 취소 승인 완료: cancelId={}", cancelId);
    }

    public void rejectCancel(Shop shop, String cancelId, String rejectReason) {
        executeWithTokenRefresh(
                shop,
                token -> {
                    apiClient.rejectCancel(token, cancelId, rejectReason);
                    return null;
                });
        log.info("세토프 취소 거부 완료: cancelId={}, reason={}", cancelId, rejectReason);
    }

    // ===== 반품 =====

    public void completeRefund(Shop shop, String refundId) {
        executeWithTokenRefresh(
                shop,
                token -> {
                    apiClient.completeRefund(token, refundId);
                    return null;
                });
        log.info("세토프 반품 완료 처리: refundId={}", refundId);
    }

    public void rejectRefund(Shop shop, String refundId, String rejectReason) {
        executeWithTokenRefresh(
                shop,
                token -> {
                    apiClient.rejectRefund(token, refundId, rejectReason);
                    return null;
                });
        log.info("세토프 반품 거부 완료: refundId={}, reason={}", refundId, rejectReason);
    }

    // ===== 토큰 처리 =====

    /** 토큰 인증 실패(401) 시 자동으로 토큰을 재발급하고 재시도합니다. */
    private <T> T executeWithTokenRefresh(Shop shop, Function<String, T> apiCall) {
        String token = resolveSellerToken(shop);
        try {
            return apiCall.apply(token);
        } catch (SetofCommerceUnauthorizedException e) {
            log.warn("세토프 토큰 만료, 재발급 시도: shopId={}", shop != null ? shop.idValue() : "null");
            String refreshedToken = refreshSellerToken(shop);
            return apiCall.apply(refreshedToken);
        }
    }

    private String resolveSellerToken(Shop shop) {
        if (shop != null && shop.apiKey() != null && !shop.apiKey().isBlank()) {
            try {
                return tokenProvider.resolveToken(shop);
            } catch (Exception e) {
                log.warn("세토프 셀러 토큰 발급 실패, 서비스 토큰으로 폴백: shopId={}", shop.idValue());
            }
        }
        return properties.getServiceToken();
    }

    private String refreshSellerToken(Shop shop) {
        if (shop != null && shop.apiKey() != null && !shop.apiKey().isBlank()) {
            try {
                return tokenProvider.refreshToken(shop);
            } catch (Exception e) {
                log.warn("세토프 셀러 토큰 재발급 실패, 서비스 토큰으로 폴백: shopId={}", shop.idValue());
            }
        }
        return properties.getServiceToken();
    }
}
