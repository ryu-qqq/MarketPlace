package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.config.SetofCommerceProperties;
import com.ryuqq.marketplace.adapter.out.client.setof.exception.SetofCommerceUnauthorizedException;
import com.ryuqq.marketplace.adapter.out.client.setof.support.SetofSellerTokenProvider;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.strategy.SetofProductUpdateExecutorProvider;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.util.Set;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 상품 등록/수정/삭제 클라이언트 어댑터.
 *
 * <p>ProductGroupSyncData -> Setof 요청 DTO 변환 후 ApiClient를 통해 API 호출. HTTP 호출은 {@link
 * SetofCommerceApiClient}에 위임합니다.
 */
@Component
@Qualifier("setofProductClient")
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceProductClientAdapter implements SalesChannelProductClient {

    private static final Logger log =
            LoggerFactory.getLogger(SetofCommerceProductClientAdapter.class);

    private final SetofCommerceApiClient apiClient;
    private final SetofCommerceProductMapper mapper;
    private final SetofProductUpdateExecutorProvider updateExecutorProvider;
    private final SetofCommerceProperties properties;
    private final SetofSellerTokenProvider tokenProvider;

    public SetofCommerceProductClientAdapter(
            SetofCommerceApiClient apiClient,
            SetofCommerceProductMapper mapper,
            SetofProductUpdateExecutorProvider updateExecutorProvider,
            SetofCommerceProperties properties,
            SetofSellerTokenProvider tokenProvider) {
        this.apiClient = apiClient;
        this.mapper = mapper;
        this.updateExecutorProvider = updateExecutorProvider;
        this.properties = properties;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String channelCode() {
        return "SETOF";
    }

    @Override
    public String registerProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel,
            Shop shop) {

        SetofProductGroupRegistrationRequest request =
                mapper.toRegistrationRequest(syncData, externalCategoryId, externalBrandId);

        Long productGroupId = syncData.queryResult().id();

        log.info(
                "세토프 커머스 상품 등록 요청: productGroupId={}, categoryId={}",
                productGroupId,
                externalCategoryId);

        SetofProductGroupRegistrationResponse response =
                executeWithTokenRefresh(
                        shop, token -> apiClient.registerProduct(token, request));

        if (response == null || response.productGroupId() == null) {
            throw new IllegalStateException(
                    "세토프 커머스 상품 등록 응답이 null입니다: productGroupId=" + productGroupId);
        }

        log.info(
                "세토프 커머스 상품 등록 성공: productGroupId={}, externalProductGroupId={}",
                productGroupId,
                response.productGroupId());

        return String.valueOf(response.productGroupId());
    }

    @Override
    public void updateProduct(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas) {

        Long productGroupId = syncData.queryResult().id();

        log.info(
                "세토프 커머스 상품 수정 요청: productGroupId={}, externalProductId={}, changedAreas={}",
                productGroupId,
                externalProductId,
                changedAreas);

        // 기존 세토프 상품 조회 (옵션명 기반 productId 매칭용)
        SetofProductGroupDetailResponse existingProduct = fetchExistingProduct(externalProductId);

        updateExecutorProvider
                .resolve(changedAreas)
                .execute(
                        syncData,
                        externalCategoryId,
                        externalBrandId,
                        externalProductId,
                        channel,
                        changedAreas,
                        existingProduct);

        log.info(
                "세토프 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                productGroupId,
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        log.info("세토프 커머스 상품 삭제(판매중지) 요청: externalProductId={}", externalProductId);

        SetofProductGroupUpdateRequest deleteRequest = mapper.toDeleteRequest();
        apiClient.updateProduct(properties.getServiceToken(), externalProductId, deleteRequest);

        log.info("세토프 커머스 상품 삭제(판매중지) 성공: externalProductId={}", externalProductId);
    }

    /**
     * Shop의 apiKey로 셀러 토큰을 발급받아 사용하고, 실패 시 properties.serviceToken을 fallback으로 사용합니다.
     *
     * @param shop Shop 정보 (nullable)
     * @return 셀러 토큰 또는 서비스 토큰
     */
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

    /**
     * 토큰 인증 실패(401) 시 자동으로 토큰을 재발급하고 재시도합니다.
     *
     * @param shop Shop 정보
     * @param apiCall 토큰을 받아 API를 호출하는 함수
     * @return API 호출 결과
     */
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

    /**
     * 토큰을 재발급합니다. Shop이 없거나 apiKey가 없으면 서비스 토큰을 반환합니다.
     */
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

    /**
     * 기존 세토프 상품 조회.
     *
     * <p>GET /api/v2/admin/product-groups/{productGroupId} 호출하여 기존 상품 정보를 조회합니다. 옵션명 기반 productId
     * 매칭에 사용됩니다. 조회 실패 시 null을 반환하여 수정은 계속 진행됩니다.
     *
     * @param externalProductId 세토프 외부 상품 그룹 ID
     * @return 기존 상품 조회 결과 (실패 시 null)
     */
    private SetofProductGroupDetailResponse fetchExistingProduct(String externalProductId) {
        try {
            log.info("세토프 커머스 기존 상품 조회: externalProductId={}", externalProductId);
            SetofProductGroupDetailResponse response =
                    apiClient.getProduct(properties.getServiceToken(), externalProductId);

            log.info(
                    "세토프 커머스 기존 상품 조회 성공: externalProductId={}, productsCount={}",
                    externalProductId,
                    response != null && response.products() != null
                            ? response.products().size()
                            : 0);

            return response;
        } catch (Exception e) {
            log.warn(
                    "세토프 커머스 기존 상품 조회 실패 (수정은 계속 진행): externalProductId={}, error={}",
                    externalProductId,
                    e.getMessage());
            return null;
        }
    }
}
