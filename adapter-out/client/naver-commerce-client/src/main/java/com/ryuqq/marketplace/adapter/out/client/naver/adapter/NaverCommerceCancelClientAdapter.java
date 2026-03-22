package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverCancelRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 취소 처리 클라이언트 어댑터.
 *
 * <p>주문 취소 요청 및 취소 승인 API 2개 엔드포인트를 제공합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceCancelClientAdapter {

    private final NaverCommerceApiClient apiClient;

    public NaverCommerceCancelClientAdapter(NaverCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /** 취소를 요청합니다. */
    public NaverClaimResponse requestCancel(String productOrderId, NaverCancelRequest request) {
        return apiClient.requestCancel(productOrderId, request);
    }

    /** 취소 요청을 승인합니다. */
    public NaverClaimResponse approveCancel(String productOrderId) {
        return apiClient.approveCancel(productOrderId);
    }
}
