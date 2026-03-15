package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofRefundPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundRefundPolicySyncClient;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceRefundPolicySyncAdapter implements OutboundRefundPolicySyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceRefundPolicySyncAdapter.class);

    private final RestClient restClient;
    private final RefundPolicyReadManager policyReadManager;
    private final SetofCommerceSellerSyncMapper mapper;
    private final CircuitBreaker circuitBreaker;

    public SetofCommerceRefundPolicySyncAdapter(
            RestClient setofCommerceRestClient,
            RefundPolicyReadManager policyReadManager,
            SetofCommerceSellerSyncMapper mapper,
            CircuitBreaker setofCommerceCircuitBreaker) {
        this.restClient = setofCommerceRestClient;
        this.policyReadManager = policyReadManager;
        this.mapper = mapper;
        this.circuitBreaker = setofCommerceCircuitBreaker;
    }

    @Override
    public OutboundSellerSyncResult createRefundPolicy(Long sellerId, Long policyId) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        RefundPolicy policy =
                                policyReadManager.getBySellerIdAndId(
                                        SellerId.of(sellerId), RefundPolicyId.of(policyId));
                        SetofRefundPolicySyncRequest request = mapper.toRefundPolicyRequest(policy);

                        log.info(
                                "세토프 커머스 환불정책 등록 요청: sellerId={}, policyId={}", sellerId, policyId);

                        restClient
                                .post()
                                .uri("/api/v2/admin/sellers/{sellerId}/refund-policies", sellerId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();

                        return OutboundSellerSyncResult.ofSuccess();
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        } catch (RestClientException e) {
            log.error("세토프 커머스 환불정책 등록 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateRefundPolicy(Long sellerId, Long policyId) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        RefundPolicy policy =
                                policyReadManager.getBySellerIdAndId(
                                        SellerId.of(sellerId), RefundPolicyId.of(policyId));
                        SetofRefundPolicySyncRequest request = mapper.toRefundPolicyRequest(policy);

                        log.info(
                                "세토프 커머스 환불정책 수정 요청: sellerId={}, policyId={}", sellerId, policyId);

                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/sellers/{sellerId}/refund-policies/{policyId}",
                                        sellerId,
                                        policyId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();

                        return OutboundSellerSyncResult.ofSuccess();
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        } catch (RestClientException e) {
            log.error("세토프 커머스 환불정책 수정 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }
}
