package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerAddressSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerAddressSyncClient;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
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
public class SetofCommerceSellerAddressSyncAdapter implements OutboundSellerAddressSyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceSellerAddressSyncAdapter.class);

    private final RestClient restClient;
    private final SellerAddressReadManager addressReadManager;
    private final SetofCommerceSellerSyncMapper mapper;
    private final CircuitBreaker circuitBreaker;

    public SetofCommerceSellerAddressSyncAdapter(
            RestClient setofCommerceRestClient,
            SellerAddressReadManager addressReadManager,
            SetofCommerceSellerSyncMapper mapper,
            CircuitBreaker setofCommerceCircuitBreaker) {
        this.restClient = setofCommerceRestClient;
        this.addressReadManager = addressReadManager;
        this.mapper = mapper;
        this.circuitBreaker = setofCommerceCircuitBreaker;
    }

    @Override
    public OutboundSellerSyncResult createSellerAddress(Long sellerId, Long addressId) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        SellerAddress address =
                                addressReadManager.getById(SellerAddressId.of(addressId));
                        SetofSellerAddressSyncRequest request =
                                mapper.toSellerAddressRequest(address);

                        log.info(
                                "세토프 커머스 셀러주소 등록 요청: sellerId={}, addressId={}",
                                sellerId,
                                addressId);

                        restClient
                                .post()
                                .uri("/api/v2/admin/seller-addresses/sellers/{sellerId}", sellerId)
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
            log.error("세토프 커머스 셀러주소 등록 실패: sellerId={}, addressId={}", sellerId, addressId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateSellerAddress(Long sellerId, Long addressId) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        SellerAddress address =
                                addressReadManager.getById(SellerAddressId.of(addressId));
                        SetofSellerAddressSyncRequest request =
                                mapper.toSellerAddressRequest(address);

                        log.info(
                                "세토프 커머스 셀러주소 수정 요청: sellerId={}, addressId={}",
                                sellerId,
                                addressId);

                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}",
                                        sellerId,
                                        addressId)
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
            log.error("세토프 커머스 셀러주소 수정 실패: sellerId={}, addressId={}", sellerId, addressId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult deleteSellerAddress(Long sellerId, Long addressId) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        log.info(
                                "세토프 커머스 셀러주소 삭제 요청: sellerId={}, addressId={}",
                                sellerId,
                                addressId);

                        restClient
                                .delete()
                                .uri(
                                        "/api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}",
                                        sellerId,
                                        addressId)
                                .retrieve()
                                .toBodilessEntity();

                        return OutboundSellerSyncResult.ofSuccess();
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러주소 삭제 실패: sellerId={}, addressId={}", sellerId, addressId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }
}
