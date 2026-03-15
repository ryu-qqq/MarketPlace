package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductPriceUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 상품(SKU) 개별 수정 어댑터.
 *
 * <p>가격, 재고, 상품+옵션 일괄 수정 엔드포인트를 호출합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class SetofCommerceProductAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceProductAdapter.class);

    private final RestClient restClient;
    private final CircuitBreaker circuitBreaker;

    public SetofCommerceProductAdapter(
            RestClient setofCommerceRestClient, CircuitBreaker setofCommerceCircuitBreaker) {
        this.restClient = setofCommerceRestClient;
        this.circuitBreaker = setofCommerceCircuitBreaker;
    }

    /**
     * 상품 가격 수정.
     *
     * <p>PATCH /api/v2/admin/products/{productId}/price
     *
     * @param productId 세토프 상품 ID
     * @param request 가격 수정 요청
     */
    public void updatePrice(Long productId, SetofProductPriceUpdateRequest request) {
        try {
            circuitBreaker.executeRunnable(
                    () -> {
                        log.info("세토프 커머스 상품 가격 수정 요청: productId={}", productId);

                        restClient
                                .patch()
                                .uri("/api/v2/admin/products/{productId}/price", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();

                        log.info("세토프 커머스 상품 가격 수정 성공: productId={}", productId);
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    /**
     * 상품 재고 수정.
     *
     * <p>PATCH /api/v2/admin/products/{productId}/stock
     *
     * @param productId 세토프 상품 ID
     * @param request 재고 수정 요청
     */
    public void updateStock(Long productId, SetofProductStockUpdateRequest request) {
        try {
            circuitBreaker.executeRunnable(
                    () -> {
                        log.info("세토프 커머스 상품 재고 수정 요청: productId={}", productId);

                        restClient
                                .patch()
                                .uri("/api/v2/admin/products/{productId}/stock", productId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();

                        log.info("세토프 커머스 상품 재고 수정 성공: productId={}", productId);
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    /**
     * 상품 + 옵션 일괄 수정.
     *
     * <p>PATCH /api/v2/admin/products/product-groups/{productGroupId}
     *
     * @param productGroupId 세토프 상품 그룹 ID
     * @param request 상품 + 옵션 일괄 수정 요청
     */
    public void updateProducts(Long productGroupId, SetofProductsUpdateRequest request) {
        try {
            circuitBreaker.executeRunnable(
                    () -> {
                        log.info("세토프 커머스 상품+옵션 일괄 수정 요청: productGroupId={}", productGroupId);

                        restClient
                                .patch()
                                .uri(
                                        "/api/v2/admin/products/product-groups/{productGroupId}",
                                        productGroupId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();

                        log.info("세토프 커머스 상품+옵션 일괄 수정 성공: productGroupId={}", productGroupId);
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "세토프 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }
}
