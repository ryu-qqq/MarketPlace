package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofShippingPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundShippingPolicySyncClient;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceShippingPolicySyncAdapter implements OutboundShippingPolicySyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceShippingPolicySyncAdapter.class);

    private final RestClient restClient;
    private final ShippingPolicyReadManager policyReadManager;
    private final SetofCommerceSellerSyncMapper mapper;

    public SetofCommerceShippingPolicySyncAdapter(
            RestClient setofCommerceRestClient,
            ShippingPolicyReadManager policyReadManager,
            SetofCommerceSellerSyncMapper mapper) {
        this.restClient = setofCommerceRestClient;
        this.policyReadManager = policyReadManager;
        this.mapper = mapper;
    }

    @Override
    public OutboundSellerSyncResult createShippingPolicy(Long sellerId, Long policyId) {
        try {
            ShippingPolicy policy =
                    policyReadManager.getBySellerIdAndId(
                            SellerId.of(sellerId), ShippingPolicyId.of(policyId));
            SetofShippingPolicySyncRequest request = mapper.toShippingPolicyRequest(policy);

            log.info("세토프 커머스 배송정책 등록 요청: sellerId={}, policyId={}", sellerId, policyId);

            restClient
                    .post()
                    .uri("/api/v2/admin/sellers/{sellerId}/shipping-policies", sellerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            return OutboundSellerSyncResult.ofSuccess();
        } catch (RestClientException e) {
            log.error("세토프 커머스 배송정책 등록 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateShippingPolicy(Long sellerId, Long policyId) {
        try {
            ShippingPolicy policy =
                    policyReadManager.getBySellerIdAndId(
                            SellerId.of(sellerId), ShippingPolicyId.of(policyId));
            SetofShippingPolicySyncRequest request = mapper.toShippingPolicyRequest(policy);

            log.info("세토프 커머스 배송정책 수정 요청: sellerId={}, policyId={}", sellerId, policyId);

            restClient
                    .put()
                    .uri(
                            "/api/v2/admin/sellers/{sellerId}/shipping-policies/{policyId}",
                            sellerId,
                            policyId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            return OutboundSellerSyncResult.ofSuccess();
        } catch (RestClientException e) {
            log.error("세토프 커머스 배송정책 수정 실패: sellerId={}, policyId={}", sellerId, policyId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }
}
