package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimRejectRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnApproveRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 반품 처리 클라이언트 어댑터.
 *
 * <p>반품 요청/승인/거절/보류/보류해제 API 5개 엔드포인트를 제공합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceReturnClientAdapter {

    private static final String CLAIM_RETURN_BASE =
            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/return";

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;

    public NaverCommerceReturnClientAdapter(
            RestClient naverCommerceRestClient, NaverCommerceTokenManager tokenManager) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
    }

    /** 반품을 요청합니다. */
    public NaverClaimResponse requestReturn(String productOrderId, NaverReturnRequest request) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_RETURN_BASE + "/request", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 반품을 승인합니다. */
    public NaverClaimResponse approveReturn(
            String productOrderId, NaverReturnApproveRequest request) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_RETURN_BASE + "/approve", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 반품을 거절합니다. */
    public NaverClaimResponse rejectReturn(String productOrderId, NaverClaimRejectRequest request) {
        String token = tokenManager.getAccessToken();

        return restClient
                .post()
                .uri(CLAIM_RETURN_BASE + "/reject", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .body(NaverClaimResponse.class);
    }

    /** 반품을 보류합니다. */
    public void holdbackReturn(String productOrderId) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri(CLAIM_RETURN_BASE + "/holdback", productOrderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }

    /** 반품 보류를 해제합니다. */
    public void releaseReturnHoldback(String productOrderId) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri(CLAIM_RETURN_BASE + "/holdback/release", productOrderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();
    }
}
