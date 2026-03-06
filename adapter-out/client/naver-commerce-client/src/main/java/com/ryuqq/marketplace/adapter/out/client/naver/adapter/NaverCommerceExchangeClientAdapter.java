package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeReDeliveryRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeRejectRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 교환 처리 클라이언트 어댑터.
 *
 * <p>교환 수거완료 승인/재배송/거부/보류/보류해제 API 5개 엔드포인트를 제공합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceExchangeClientAdapter {

    private static final String CLAIM_EXCHANGE_BASE =
            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange";

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;

    public NaverCommerceExchangeClientAdapter(
            RestClient naverCommerceRestClient, NaverCommerceTokenManager tokenManager) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
    }

    /** 교환 수거완료를 승인합니다. */
    public NaverClaimResponse approveCollectedExchange(String productOrderId) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_EXCHANGE_BASE + "/approve-collected", productOrderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 교환 재배송을 처리합니다. */
    public NaverClaimResponse reDeliverExchange(
            String productOrderId, NaverExchangeReDeliveryRequest request) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_EXCHANGE_BASE + "/dispatch", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 교환 요청을 거부(철회)합니다. */
    public NaverClaimResponse rejectExchange(
            String productOrderId, NaverExchangeRejectRequest request) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_EXCHANGE_BASE + "/reject", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 교환을 보류합니다. */
    public void holdbackExchange(String productOrderId) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri(CLAIM_EXCHANGE_BASE + "/holdback", productOrderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    /** 교환 보류를 해제합니다. */
    public void releaseExchangeHoldback(String productOrderId) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri(CLAIM_EXCHANGE_BASE + "/holdback/release", productOrderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
}
