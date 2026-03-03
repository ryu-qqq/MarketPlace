package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverCancelRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 취소 처리 클라이언트 어댑터.
 *
 * <p>주문 취소 요청 및 취소 승인 API 2개 엔드포인트를 제공합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceCancelClientAdapter {

    private static final String CLAIM_CANCEL_BASE =
            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel";

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;

    public NaverCommerceCancelClientAdapter(
            RestClient naverCommerceRestClient, NaverCommerceTokenManager tokenManager) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
    }

    /** 취소를 요청합니다. */
    public NaverClaimResponse requestCancel(String productOrderId, NaverCancelRequest request) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_CANCEL_BASE + "/request", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 취소 요청을 승인합니다. (Request Body 없음) */
    public NaverClaimResponse approveCancel(String productOrderId) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_CANCEL_BASE + "/approve", productOrderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(NaverClaimResponse.class);
    }
}
