package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerAddressSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerAddressSyncClient;
import com.ryuqq.marketplace.application.selleraddress.manager.SellerAddressReadManager;
import com.ryuqq.marketplace.domain.selleraddress.aggregate.SellerAddress;
import com.ryuqq.marketplace.domain.selleraddress.id.SellerAddressId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceSellerAddressSyncAdapter implements OutboundSellerAddressSyncClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceSellerAddressSyncAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SellerAddressReadManager addressReadManager;
    private final SetofCommerceSellerSyncMapper mapper;
    private final SetofCommerceProperties properties;

    public SetofCommerceSellerAddressSyncAdapter(
            SetofCommerceApiClient apiClient,
            SellerAddressReadManager addressReadManager,
            SetofCommerceSellerSyncMapper mapper,
            SetofCommerceProperties properties) {
        this.apiClient = apiClient;
        this.addressReadManager = addressReadManager;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public OutboundSellerSyncResult createSellerAddress(Shop shop, Long sellerId, Long addressId) {
        try {
            SellerAddress address = addressReadManager.getById(SellerAddressId.of(addressId));
            SetofSellerAddressSyncRequest request = mapper.toSellerAddressRequest(address);

            log.info("세토프 커머스 셀러주소 등록 요청: sellerId={}, addressId={}", sellerId, addressId);

            apiClient.createSellerAddress(properties.getServiceToken(), sellerId, request);
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러주소 등록 실패: sellerId={}, addressId={}", sellerId, addressId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateSellerAddress(Shop shop, Long sellerId, Long addressId) {
        try {
            SellerAddress address = addressReadManager.getById(SellerAddressId.of(addressId));
            SetofSellerAddressSyncRequest request = mapper.toSellerAddressRequest(address);

            log.info("세토프 커머스 셀러주소 수정 요청: sellerId={}, addressId={}", sellerId, addressId);

            apiClient.updateSellerAddress(
                    properties.getServiceToken(), sellerId, addressId, request);
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러주소 수정 실패: sellerId={}, addressId={}", sellerId, addressId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult deleteSellerAddress(Shop shop, Long sellerId, Long addressId) {
        try {
            log.info("세토프 커머스 셀러주소 삭제 요청: sellerId={}, addressId={}", sellerId, addressId);

            apiClient.deleteSellerAddress(properties.getServiceToken(), sellerId, addressId);
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러주소 삭제 실패: sellerId={}, addressId={}", sellerId, addressId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }
}
