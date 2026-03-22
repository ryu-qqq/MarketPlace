package com.ryuqq.marketplace.adapter.out.client.setof.adapter;

import com.ryuqq.marketplace.adapter.out.client.setof.client.SetofCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductPriceUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 커머스 상품(SKU) 개별 수정 어댑터.
 *
 * <p>가격, 재고, 상품+옵션 일괄 수정 엔드포인트를 호출합니다. HTTP 호출은 {@link SetofCommerceApiClient}에 위임합니다.
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "base-url")
public class SetofCommerceProductAdapter {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceProductAdapter.class);

    private final SetofCommerceApiClient apiClient;

    public SetofCommerceProductAdapter(SetofCommerceApiClient apiClient) {
        this.apiClient = apiClient;
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
        log.info("세토프 커머스 상품 가격 수정: productId={}", productId);
        apiClient.updatePrice(productId, request);
        log.info("세토프 커머스 상품 가격 수정 성공: productId={}", productId);
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
        log.info("세토프 커머스 상품 재고 수정: productId={}", productId);
        apiClient.updateStock(productId, request);
        log.info("세토프 커머스 상품 재고 수정 성공: productId={}", productId);
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
        log.info("세토프 커머스 상품+옵션 일괄 수정: productGroupId={}", productGroupId);
        apiClient.updateProducts(productGroupId, request);
        log.info("세토프 커머스 상품+옵션 일괄 수정 성공: productGroupId={}", productGroupId);
    }
}
