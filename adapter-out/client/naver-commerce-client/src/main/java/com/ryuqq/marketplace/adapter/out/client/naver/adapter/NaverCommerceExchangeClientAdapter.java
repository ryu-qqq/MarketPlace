package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeReDeliveryRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeRejectRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 교환 처리 클라이언트 어댑터.
 *
 * <p>교환 수거완료 승인/재배송/거부/보류/보류해제 API 5개 엔드포인트를 제공합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceExchangeClientAdapter {

    private final NaverCommerceApiClient apiClient;

    public NaverCommerceExchangeClientAdapter(NaverCommerceApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /** 교환 수거완료를 승인합니다. */
    public NaverClaimResponse approveCollectedExchange(String productOrderId) {
        return apiClient.approveCollectedExchange(productOrderId);
    }

    /** 교환 재배송을 처리합니다. */
    public NaverClaimResponse reDeliverExchange(
            String productOrderId, NaverExchangeReDeliveryRequest request) {
        return apiClient.reDeliverExchange(productOrderId, request);
    }

    /** 교환 요청을 거부(철회)합니다. */
    public NaverClaimResponse rejectExchange(
            String productOrderId, NaverExchangeRejectRequest request) {
        return apiClient.rejectExchange(productOrderId, request);
    }

    /** 교환을 보류합니다. */
    public void holdbackExchange(String productOrderId) {
        apiClient.holdbackExchange(productOrderId);
    }

    /** 교환 보류를 해제합니다. */
    public void releaseExchangeHoldback(String productOrderId) {
        apiClient.releaseExchangeHoldback(productOrderId);
    }
}
