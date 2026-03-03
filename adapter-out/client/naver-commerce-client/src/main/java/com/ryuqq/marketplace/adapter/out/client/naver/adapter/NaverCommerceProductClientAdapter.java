package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceProductMapper;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 상품 등록/수정 클라이언트 어댑터.
 *
 * <p>ProductGroupDetailBundle → NaverProductRegistrationRequest 변환 후 POST/PUT API 호출.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
public class NaverCommerceProductClientAdapter implements SalesChannelProductClient {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommerceProductClientAdapter.class);

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;
    private final NaverCommerceProductMapper mapper;

    public NaverCommerceProductClientAdapter(
            RestClient naverCommerceRestClient,
            NaverCommerceTokenManager tokenManager,
            NaverCommerceProductMapper mapper) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
        this.mapper = mapper;
    }

    @Override
    public String registerProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel) {

        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(bundle, externalCategoryId, externalBrandId);

        String token = tokenManager.getAccessToken();

        log.info(
                "네이버 커머스 상품 등록 요청: productGroupId={}, categoryId={}",
                bundle.group().idValue(),
                externalCategoryId);

        NaverProductRegistrationResponse response =
                restClient
                        .post()
                        .uri("/v2/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + token)
                        .body(request)
                        .retrieve()
                        .body(NaverProductRegistrationResponse.class);

        if (response == null || response.originProductNo() == null) {
            throw new IllegalStateException(
                    "네이버 커머스 상품 등록 응답이 null입니다: productGroupId=" + bundle.group().idValue());
        }

        log.info(
                "네이버 커머스 상품 등록 성공: productGroupId={}, originProductNo={}",
                bundle.group().idValue(),
                response.originProductNo());

        return String.valueOf(response.originProductNo());
    }

    @Override
    public void updateProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel) {

        NaverProductRegistrationRequest request =
                mapper.toRegistrationRequest(bundle, externalCategoryId, externalBrandId);

        String token = tokenManager.getAccessToken();

        log.info(
                "네이버 커머스 상품 수정 요청: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);

        restClient
                .put()
                .uri("/v2/products/origin-products/{originProductNo}", externalProductId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info(
                "네이버 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        String token = tokenManager.getAccessToken();

        log.info("네이버 커머스 상품 삭제 요청: externalProductId={}", externalProductId);

        restClient
                .delete()
                .uri("/v2/products/origin-products/{originProductNo}", externalProductId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        log.info("네이버 커머스 상품 삭제 성공: externalProductId={}", externalProductId);
    }
}
