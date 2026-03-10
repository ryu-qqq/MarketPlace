package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 상품 등록/수정/삭제 클라이언트 어댑터.
 *
 * <p>ProductGroupDetailBundle → Setof 요청 DTO 변환 후 POST/PUT API 호출. 인증은 RestClient
 * defaultHeader(X-Service-Token)로 자동 처리됩니다.
 */
@Component
@Qualifier("setofProductClient")
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceProductClientAdapter implements SalesChannelProductClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceProductClientAdapter.class);

    private final RestClient restClient;
    private final SetofCommerceProductMapper mapper;

    public SetofCommerceProductClientAdapter(
            RestClient setofCommerceRestClient, SetofCommerceProductMapper mapper) {
        this.restClient = setofCommerceRestClient;
        this.mapper = mapper;
    }

    @Override
    public String channelCode() {
        return "SETOF";
    }

    @Override
    public String registerProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel) {

        SetofProductGroupRegistrationRequest request =
                mapper.toRegistrationRequest(bundle, externalCategoryId, externalBrandId);

        log.info(
                "세토프 커머스 상품 등록 요청: productGroupId={}, categoryId={}",
                bundle.group().idValue(),
                externalCategoryId);

        SetofProductGroupRegistrationResponse response =
                restClient
                        .post()
                        .uri("/api/v2/admin/product-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(request)
                        .retrieve()
                        .body(SetofProductGroupRegistrationResponse.class);

        if (response == null || response.productGroupId() == null) {
            throw new IllegalStateException(
                    "세토프 커머스 상품 등록 응답이 null입니다: productGroupId=" + bundle.group().idValue());
        }

        log.info(
                "세토프 커머스 상품 등록 성공: productGroupId={}, externalProductGroupId={}",
                bundle.group().idValue(),
                response.productGroupId());

        return String.valueOf(response.productGroupId());
    }

    @Override
    public void updateProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel) {

        SetofProductGroupUpdateRequest request =
                mapper.toUpdateRequest(bundle, externalCategoryId, externalBrandId);

        log.info(
                "세토프 커머스 상품 수정 요청: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);

        restClient
                .put()
                .uri("/api/v2/admin/product-groups/{productGroupId}", externalProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info(
                "세토프 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        log.info("세토프 커머스 상품 삭제(판매중지) 요청: externalProductId={}", externalProductId);

        SetofProductGroupUpdateRequest deleteRequest = mapper.toDeleteRequest();

        restClient
                .put()
                .uri("/api/v2/admin/product-groups/{productGroupId}", externalProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(deleteRequest)
                .retrieve()
                .toBodilessEntity();

        log.info("세토프 커머스 상품 삭제(판매중지) 성공: externalProductId={}", externalProductId);
    }

    /**
     * 상품 그룹 기본 정보 수정.
     *
     * <p>PATCH /api/v2/admin/product-groups/{productGroupId}/basic-info
     *
     * @param externalProductGroupId 세토프 상품 그룹 ID
     * @param request 기본 정보 수정 요청
     */
    public void updateBasicInfo(
            String externalProductGroupId, SetofProductGroupBasicInfoUpdateRequest request) {
        log.info("세토프 커머스 상품 그룹 기본정보 수정 요청: externalProductGroupId={}", externalProductGroupId);

        restClient
                .patch()
                .uri(
                        "/api/v2/admin/product-groups/{productGroupId}/basic-info",
                        externalProductGroupId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("세토프 커머스 상품 그룹 기본정보 수정 성공: externalProductGroupId={}", externalProductGroupId);
    }
}
