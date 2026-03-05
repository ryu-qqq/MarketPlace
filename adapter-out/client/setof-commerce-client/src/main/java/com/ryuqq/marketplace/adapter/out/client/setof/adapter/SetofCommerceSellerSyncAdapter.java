package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSyncApiResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceSellerSyncMapper;
import com.ryuqq.marketplace.application.seller.manager.SellerReadManager;
import com.ryuqq.marketplace.application.setofsync.dto.response.SetofSyncResult;
import com.ryuqq.marketplace.application.setofsync.port.out.client.SetofSellerSyncClient;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceSellerSyncAdapter implements SetofSellerSyncClient {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceSellerSyncAdapter.class);

    private final RestClient restClient;
    private final SellerReadManager sellerReadManager;
    private final SetofCommerceSellerSyncMapper mapper;

    public SetofCommerceSellerSyncAdapter(
            RestClient setofCommerceRestClient,
            SellerReadManager sellerReadManager,
            SetofCommerceSellerSyncMapper mapper) {
        this.restClient = setofCommerceRestClient;
        this.sellerReadManager = sellerReadManager;
        this.mapper = mapper;
    }

    @Override
    public SetofSyncResult createSeller(Long sellerId) {
        try {
            Seller seller = sellerReadManager.getById(SellerId.of(sellerId));
            SetofSellerSyncRequest request = mapper.toSellerRequest(seller);

            log.info("세토프 커머스 셀러 등록 요청: sellerId={}", sellerId);

            SetofSyncApiResponse response =
                    restClient
                            .post()
                            .uri("/api/v2/admin/sellers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(request)
                            .retrieve()
                            .body(SetofSyncApiResponse.class);

            return toResult(response);
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러 등록 실패: sellerId={}", sellerId, e);
            return SetofSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    @Override
    public SetofSyncResult updateSeller(Long sellerId) {
        try {
            Seller seller = sellerReadManager.getById(SellerId.of(sellerId));
            SetofSellerSyncRequest request = mapper.toSellerRequest(seller);

            log.info("세토프 커머스 셀러 수정 요청: sellerId={}", sellerId);

            restClient
                    .put()
                    .uri("/api/v2/admin/sellers/{sellerId}", sellerId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();

            return SetofSyncResult.ofSuccess();
        } catch (RestClientException e) {
            log.error("세토프 커머스 셀러 수정 실패: sellerId={}", sellerId, e);
            return SetofSyncResult.retryableFailure("REST_ERROR", e.getMessage());
        }
    }

    private SetofSyncResult toResult(SetofSyncApiResponse response) {
        if (response == null || !response.success()) {
            String errorCode = response != null ? response.errorCode() : "NULL_RESPONSE";
            String errorMessage = response != null ? response.errorMessage() : "응답이 null입니다";
            return SetofSyncResult.retryableFailure(errorCode, errorMessage);
        }
        return SetofSyncResult.ofSuccess();
    }
}
