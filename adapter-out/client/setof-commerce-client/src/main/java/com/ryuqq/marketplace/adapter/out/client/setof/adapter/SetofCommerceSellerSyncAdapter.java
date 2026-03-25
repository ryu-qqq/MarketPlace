package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerCreateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSyncApiResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundseller.dto.response.OutboundSellerSyncResult;
import com.ryuqq.marketplace.application.outboundseller.port.out.client.OutboundSellerSyncClient;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceSellerSyncAdapter implements OutboundSellerSyncClient {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceSellerSyncAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SellerReadManager sellerReadManager;
    private final SetofCommerceSellerSyncMapper mapper;
    private final SetofCommerceProperties properties;

    public SetofCommerceSellerSyncAdapter(
            SetofCommerceApiClient apiClient,
            SellerReadManager sellerReadManager,
            SetofCommerceSellerSyncMapper mapper,
            SetofCommerceProperties properties) {
        this.apiClient = apiClient;
        this.sellerReadManager = sellerReadManager;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Override
    public OutboundSellerSyncResult createSeller(Shop shop, Long sellerId) {
        try {
            Seller seller = sellerReadManager.getById(SellerId.of(sellerId));
            SetofSellerCreateRequest request = mapper.toSellerCreateRequest(seller);

            log.info("세토프 커머스 셀러 등록 요청: sellerId={}", sellerId);

            SetofSyncApiResponse response =
                    apiClient.createSeller(properties.getServiceToken(), request);
            return toResult(response);
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러 등록 실패: sellerId={}", sellerId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public OutboundSellerSyncResult updateSeller(Shop shop, Long sellerId) {
        try {
            Seller seller = sellerReadManager.getById(SellerId.of(sellerId));
            SetofSellerSyncRequest request = mapper.toSellerRequest(seller);

            log.info("세토프 커머스 셀러 수정 요청: sellerId={}", sellerId);

            apiClient.updateSeller(properties.getServiceToken(), sellerId, request);
            return OutboundSellerSyncResult.ofSuccess();
        } catch (ExternalServiceUnavailableException e) {
            throw e;
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러 수정 실패: sellerId={}", sellerId, e);
            return OutboundSellerSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    private OutboundSellerSyncResult toResult(SetofSyncApiResponse response) {
        if (response == null || !response.success()) {
            String errorCode = response != null ? response.errorCode() : "NULL_RESPONSE";
            String errorMessage = response != null ? response.errorMessage() : "응답이 null입니다";
            return OutboundSellerSyncResult.retryableFailure(errorCode, errorMessage);
        }
        return OutboundSellerSyncResult.ofSuccess();
    }
}
