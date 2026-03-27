package com.ryuqq.marketplace.adapter.out.client.sellic.client;

import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicApiResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicProductUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicShipmentRequest;
import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicShipmentResponse;
import com.ryuqq.marketplace.adapter.out.client.sellic.support.SellicCommerceApiExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 셀릭 커머스 HTTP 호출 담당 클라이언트.
 *
 * <p>모든 외부 HTTP 호출을 이 클래스에서 처리합니다. {@link SellicCommerceApiExecutor}를 통해 CB + Retry 보호 하에 실행합니다.
 */
@Component
@ConditionalOnProperty(prefix = "sellic-commerce", name = "base-url")
public class SellicCommerceApiClient {

    private static final Logger log = LoggerFactory.getLogger(SellicCommerceApiClient.class);

    private final RestClient restClient;
    private final SellicCommerceApiExecutor executor;

    public SellicCommerceApiClient(
            RestClient sellicCommerceRestClient, SellicCommerceApiExecutor executor) {
        this.restClient = sellicCommerceRestClient;
        this.executor = executor;
    }

    /**
     * 상품 등록.
     *
     * <p>POST /openapi/set_product
     */
    public SellicApiResponse registerProduct(SellicProductRegistrationRequest request) {
        log.info("셀릭 커머스 상품 등록 요청: ownCode={}", request.ownCode());
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/openapi/set_product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SellicApiResponse.class));
    }

    /**
     * 상품 수정.
     *
     * <p>POST /openapi/edit_product
     */
    public SellicApiResponse updateProduct(SellicProductUpdateRequest request) {
        log.info("셀릭 커머스 상품 수정 요청: productId={}", request.productId());
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/openapi/edit_product")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SellicApiResponse.class));
    }

    /**
     * 재고 수정.
     *
     * <p>POST /openapi/edit_stock
     */
    public SellicApiResponse updateStock(SellicProductStockUpdateRequest request) {
        log.info("셀릭 커머스 재고 수정 요청: productId={}", request.productId());
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/openapi/edit_stock")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SellicApiResponse.class));
    }

    // ===== 주문 =====

    /**
     * 주문서 조회.
     *
     * <p>POST /openapi/get_order
     */
    public SellicOrderQueryResponse queryOrders(SellicOrderQueryRequest request) {
        log.info("셀릭 커머스 주문서 조회 요청: {}~{}", request.startDate(), request.endDate());
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/openapi/get_order")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SellicOrderQueryResponse.class));
    }

    // ===== 송장 =====

    /**
     * 송장 등록.
     *
     * <p>POST /openapi/set_ship
     */
    public SellicShipmentResponse registerShipment(SellicShipmentRequest request) {
        log.info("셀릭 커머스 송장 등록 요청: {}건", request.ships().size());
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/openapi/set_ship")
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SellicShipmentResponse.class));
    }
}
