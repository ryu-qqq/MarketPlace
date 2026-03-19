package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.mapper.SetofCommerceProductMapper;
import com.ryuqq.marketplace.adapter.out.client.setof.strategy.SetofProductUpdateExecutorProvider;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.outboundsync.port.out.client.SalesChannelProductClient;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.util.Set;
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
    private final SetofProductUpdateExecutorProvider updateExecutorProvider;
    private final CircuitBreaker circuitBreaker;

    public SetofCommerceProductClientAdapter(
            RestClient setofCommerceRestClient,
            SetofCommerceProductMapper mapper,
            SetofProductUpdateExecutorProvider updateExecutorProvider,
            CircuitBreaker setofCommerceCircuitBreaker) {
        this.restClient = setofCommerceRestClient;
        this.mapper = mapper;
        this.updateExecutorProvider = updateExecutorProvider;
        this.circuitBreaker = setofCommerceCircuitBreaker;
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
            SellerSalesChannel channel,
            Shop shop) {

        long externalSellerId = Long.parseLong(shop.accountId());

        SetofProductGroupRegistrationRequest request =
                mapper.toRegistrationRequest(
                        bundle, externalCategoryId, externalBrandId, externalSellerId);

        try {
            return circuitBreaker.executeSupplier(
                    () -> {
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
                                    "세토프 커머스 상품 등록 응답이 null입니다: productGroupId="
                                            + bundle.group().idValue());
                        }

                        log.info(
                                "세토프 커머스 상품 등록 성공: productGroupId={},"
                                        + " externalProductGroupId={}",
                                bundle.group().idValue(),
                                response.productGroupId());

                        return String.valueOf(response.productGroupId());
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    @Override
    public void updateProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas) {

        log.info(
                "세토프 커머스 상품 수정 요청: productGroupId={}, externalProductId={}, changedAreas={}",
                bundle.group().idValue(),
                externalProductId,
                changedAreas);

        // 기존 세토프 상품 조회 (옵션명 기반 productId 매칭용)
        SetofProductGroupDetailResponse existingProduct = fetchExistingProduct(externalProductId);

        updateExecutorProvider
                .resolve(changedAreas)
                .execute(
                        bundle,
                        externalCategoryId,
                        externalBrandId,
                        externalProductId,
                        channel,
                        changedAreas,
                        existingProduct);

        log.info(
                "세토프 커머스 상품 수정 성공: productGroupId={}, externalProductId={}",
                bundle.group().idValue(),
                externalProductId);
    }

    @Override
    public void deleteProduct(String externalProductId, SellerSalesChannel channel) {
        try {
            circuitBreaker.executeRunnable(
                    () -> {
                        log.info("세토프 커머스 상품 삭제(판매중지) 요청: externalProductId={}", externalProductId);

                        SetofProductGroupUpdateRequest deleteRequest = mapper.toDeleteRequest();

                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}",
                                        externalProductId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(deleteRequest)
                                .retrieve()
                                .toBodilessEntity();

                        log.info("세토프 커머스 상품 삭제(판매중지) 성공: externalProductId={}", externalProductId);
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
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
            return circuitBreaker.executeSupplier(
                    () -> {
                        log.info("세토프 커머스 기존 상품 조회: externalProductId={}", externalProductId);

                        SetofProductGroupDetailResponse response =
                                restClient
                                        .get()
                                        .uri(
                                                "/api/v2/admin/product-groups/{productGroupId}",
                                                externalProductId)
                                        .retrieve()
                                        .body(SetofProductGroupDetailResponse.class);

                        log.info(
                                "세토프 커머스 기존 상품 조회 성공: externalProductId={}, productsCount={}",
                                externalProductId,
                                response != null && response.products() != null
                                        ? response.products().size()
                                        : 0);

                        return response;
                    });
        } catch (Exception e) {
            log.warn(
                    "세토프 커머스 기존 상품 조회 실패 (수정은 계속 진행): externalProductId={}, error={}",
                    externalProductId,
                    e.getMessage());
            return null;
        }
    }
}
