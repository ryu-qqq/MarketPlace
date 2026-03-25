package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofShippingPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceUnauthorizedException;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.support.SetofSellerTokenProvider;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundShippingPolicySyncClient;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceShippingPolicySyncAdapter implements OutboundShippingPolicySyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceShippingPolicySyncAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final ShippingPolicyReadManager policyReadManager;
    private final SetofCommerceSellerSyncMapper mapper;
    private final SetofCommerceProperties properties;
    private final SetofSellerTokenProvider tokenProvider;

    public SetofCommerceShippingPolicySyncAdapter(
            SetofCommerceApiClient apiClient,
            ShippingPolicyReadManager policyReadManager,
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
    public OutboundSellerSyncResult createShippingPolicy(Shop shop, Long sellerId, Long policyId) {
        try {
            ShippingPolicy policy =
                    policyReadManager.getBySellerIdAndId(
                            SellerId.of(sellerId), ShippingPolicyId.of(policyId));
            SetofShippingPolicySyncRequest request = mapper.toShippingPolicyRequest(policy);

            log.info("세토프 커머스 배송정책 등록 요청: sellerId={}, policyId={}", sellerId, policyId);

            executeWithTokenRefresh(shop, token -> {
                apiClient.createShippingPolicy(token, request);
                return null;
            });
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 배송정책 등록 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateShippingPolicy(Shop shop, Long sellerId, Long policyId) {
        try {
            ShippingPolicy policy =
                    policyReadManager.getBySellerIdAndId(
                            SellerId.of(sellerId), ShippingPolicyId.of(policyId));
            SetofShippingPolicySyncRequest request = mapper.toShippingPolicyRequest(policy);

            log.info("세토프 커머스 배송정책 수정 요청: sellerId={}, policyId={}", sellerId, policyId);

            executeWithTokenRefresh(shop, token -> {
                apiClient.updateShippingPolicy(token, policyId, request);
                return null;
            });
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 배송정책 수정 실패: sellerId={}, policyId={}", sellerId, policyId, e);
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
