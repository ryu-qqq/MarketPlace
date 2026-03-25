package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofRefundPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceUnauthorizedException;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.support.SetofSellerTokenProvider;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundRefundPolicySyncClient;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceRefundPolicySyncAdapter implements OutboundRefundPolicySyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceRefundPolicySyncAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final RefundPolicyReadManager policyReadManager;
    private final SetofCommerceSellerSyncMapper mapper;
    private final SetofCommerceProperties properties;
    private final SetofSellerTokenProvider tokenProvider;

    public SetofCommerceRefundPolicySyncAdapter(
            SetofCommerceApiClient apiClient,
            RefundPolicyReadManager policyReadManager,
            SetofCommerceSellerSyncMapper mapper,
            SetofCommerceProperties properties,
            SetofSellerTokenProvider tokenProvider) {
        this.apiClient = apiClient;
        this.policyReadManager = policyReadManager;
        this.mapper = mapper;
        this.properties = properties;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public OutboundSellerSyncResult createRefundPolicy(Shop shop, Long sellerId, Long policyId) {
        try {
            RefundPolicy policy =
                    policyReadManager.getBySellerIdAndId(
                            SellerId.of(sellerId), RefundPolicyId.of(policyId));
            SetofRefundPolicySyncRequest request = mapper.toRefundPolicyRequest(policy);

            log.info("세토프 커머스 환불정책 등록 요청: sellerId={}, policyId={}", sellerId, policyId);

            executeWithTokenRefresh(shop, token -> {
                apiClient.createRefundPolicy(token, request);
                return null;
            });
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 환불정책 등록 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateRefundPolicy(Shop shop, Long sellerId, Long policyId) {
        try {
            RefundPolicy policy =
                    policyReadManager.getBySellerIdAndId(
                            SellerId.of(sellerId), RefundPolicyId.of(policyId));
            SetofRefundPolicySyncRequest request = mapper.toRefundPolicyRequest(policy);

            log.info("세토프 커머스 환불정책 수정 요청: sellerId={}, policyId={}", sellerId, policyId);

            executeWithTokenRefresh(shop, token -> {
                apiClient.updateRefundPolicy(token, policyId, request);
                return null;
            });
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 환불정책 수정 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    // ===== 토큰 처리 =====

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
