package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRejectRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 반품 처리 클라이언트 어댑터.
 *
 * <p>반품 요청/승인/거부/보류/보류해제 API 5개 엔드포인트를 제공합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceReturnClientAdapter {

    private final NaverCommerceApiClient apiClient;

    public NaverCommerceReturnClientAdapter(NaverCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /** 반품을 요청합니다. */
    public NaverClaimResponse requestReturn(String productOrderId, NaverReturnRequest request) {
        return apiClient.requestReturn(productOrderId, request);
    }

    /** 반품 요청을 승인합니다. */
    public NaverClaimResponse approveReturn(String productOrderId) {
        return apiClient.approveReturn(productOrderId);
    }

    /** 반품 요청을 거부(철회)합니다. */
    public NaverClaimResponse rejectReturn(
            String productOrderId, NaverReturnRejectRequest request) {
        return apiClient.rejectReturn(productOrderId, request);
    }

    /** 반품을 보류합니다. */
    public void holdbackReturn(String productOrderId) {
        apiClient.holdbackReturn(productOrderId);
    }

    /** 반품 보류를 해제합니다. */
    public void releaseReturnHoldback(String productOrderId) {
        apiClient.releaseReturnHoldback(productOrderId);
    }
}
