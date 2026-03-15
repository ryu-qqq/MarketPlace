package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatusesResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderConfirmRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDelayRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDispatchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderIdsResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderQueryRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverWishedDeliveryDateRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceOrderMapper;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 주문 조회 및 발주/발송 클라이언트 어댑터.
 *
 * <p>SalesChannelOrderClient를 구현하여 네이버 커머스 주문 API 8개 엔드포인트를 제공합니다. fetchNewOrders는 2-phase
 * 폴링(last-changed-statuses → product-orders/query)으로 동작합니다.
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
@SuppressWarnings("PMD.ExcessiveImports")
public class NaverCommerceOrderClientAdapter implements SalesChannelOrderClient {

    private static final Logger log =
            LoggerFactory.getLogger(NaverCommerceOrderClientAdapter.class);
    private static final String LAST_CHANGED_TYPE_PAYED = "PAYED";
    private static final int MAX_BATCH_SIZE = 300;
    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;
    private final NaverCommerceOrderMapper mapper;
    private final CircuitBreaker circuitBreaker;

    public NaverCommerceOrderClientAdapter(
            RestClient naverCommerceRestClient,
            NaverCommerceTokenManager tokenManager,
            NaverCommerceOrderMapper mapper,
            CircuitBreaker naverCommerceCircuitBreaker) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
        this.mapper = mapper;
        this.circuitBreaker = naverCommerceCircuitBreaker;
    }

    @Override
    public boolean supports(String channelCode) {
        return "NAVER".equals(channelCode);
    }

    @Override
    public List<ExternalOrderPayload> fetchNewOrders(
            long salesChannelId,
            long shopId,
            ShopCredentials credentials,
            Instant fromTime,
            Instant toTime) {

        log.info(
                "네이버 주문 폴링 시작: salesChannelId={}, from={}, to={}",
                salesChannelId,
                fromTime,
                toTime);

        List<String> productOrderIds = pollChangedProductOrderIds(fromTime, toTime);
        if (productOrderIds.isEmpty()) {
            log.info("네이버 신규 주문 없음: salesChannelId={}", salesChannelId);
            return List.of();
        }

        log.info("네이버 변경 상품주문 {}건 감지, 상세 조회 시작", productOrderIds.size());

        List<NaverProductOrderDetail> allDetails = new ArrayList<>();
        for (int i = 0; i < productOrderIds.size(); i += MAX_BATCH_SIZE) {
            List<String> batch =
                    productOrderIds.subList(
                            i, Math.min(i + MAX_BATCH_SIZE, productOrderIds.size()));
            NaverProductOrderDetailResponse response = queryProductOrders(batch);
            allDetails.addAll(response.data());
        }

        List<ExternalOrderPayload> result = mapper.toExternalOrderPayloads(allDetails);
        log.info("네이버 주문 {}건 변환 완료: salesChannelId={}", result.size(), salesChannelId);
        return result;
    }

    // === 주문 조회 ===

    /** 주문번호로 상품주문번호 목록을 조회합니다. */
    public NaverProductOrderIdsResponse getProductOrderIds(String orderId) {
        String token = tokenManager.getAccessToken();
        return restClient
                .get()
                .uri("/v1/pay-order/seller/orders/{orderId}/product-order-ids", orderId)
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(NaverProductOrderIdsResponse.class);
    }

    /** 조건형 상품주문 상세를 조회합니다. */
    public NaverProductOrderDetailResponse getProductOrdersConditional(
            String productOrderStatus, Instant from, Instant to) {
        String token = tokenManager.getAccessToken();
        NaverProductOrderDetailResponse response =
                restClient
                        .get()
                        .uri(
                                "/v1/pay-order/seller/product-orders"
                                        + "?productOrderStatus={status}"
                                        + "&lastChangedFrom={from}&lastChangedTo={to}",
                                productOrderStatus,
                                formatForNaver(from),
                                formatForNaver(to))
                        .header("Authorization", "Bearer " + token)
                        .retrieve()
                        .body(NaverProductOrderDetailResponse.class);

        return response != null ? response : new NaverProductOrderDetailResponse(List.of());
    }

    /** 변경 상품주문 내역을 조회합니다 (폴링용). */
    public NaverLastChangedStatusesResponse getLastChangedStatuses(
            Instant fromTime, Instant toTime, String moreSequence) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        String token = tokenManager.getAccessToken();

                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("type", LAST_CHANGED_TYPE_PAYED);
                        params.put("from", formatForNaver(fromTime));
                        params.put("to", formatForNaver(toTime));
                        params.put("limit", MAX_BATCH_SIZE);

                        String uri =
                                "/v1/pay-order/seller/product-orders/last-changed-statuses"
                                        + "?lastChangedType={type}"
                                        + "&lastChangedFrom={from}&lastChangedTo={to}"
                                        + "&limitCount={limit}";

                        if (moreSequence != null) {
                            String uriWithMore = uri + "&moreSequence={seq}";
                            params.put("seq", moreSequence);
                            return restClient
                                    .get()
                                    .uri(uriWithMore, params)
                                    .header("Authorization", "Bearer " + token)
                                    .retrieve()
                                    .body(NaverLastChangedStatusesResponse.class);
                        }

                        return restClient
                                .get()
                                .uri(uri, params)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .body(NaverLastChangedStatusesResponse.class);
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "네이버 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    /** 상품주문 상세를 일괄 조회합니다. */
    public NaverProductOrderDetailResponse queryProductOrders(List<String> productOrderIds) {
        try {
            return circuitBreaker.executeSupplier(
                    () -> {
                        String token = tokenManager.getAccessToken();
                        NaverProductOrderQueryRequest request =
                                new NaverProductOrderQueryRequest(productOrderIds);

                        NaverProductOrderDetailResponse response =
                                restClient
                                        .post()
                                        .uri("/v1/pay-order/seller/product-orders/query")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .header("Authorization", "Bearer " + token)
                                        .body(request)
                                        .retrieve()
                                        .body(NaverProductOrderDetailResponse.class);

                        return response != null
                                ? response
                                : new NaverProductOrderDetailResponse(List.of());
                    });
        } catch (CallNotPermittedException e) {
            throw new ExternalServiceUnavailableException(
                    "네이버 커머스 서비스 일시 중단 (Circuit Breaker OPEN)", e);
        }
    }

    // === 발주/발송 ===

    /** 발주를 확인합니다 (최대 30건). */
    public void confirmOrders(List<String> productOrderIds) {
        String token = tokenManager.getAccessToken();
        NaverOrderConfirmRequest request = new NaverOrderConfirmRequest(productOrderIds);

        restClient
                .post()
                .uri("/v1/pay-order/seller/product-orders/confirm")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("네이버 발주 확인 완료: {}건", productOrderIds.size());
    }

    /** 발송을 처리합니다 (최대 30건). */
    public void dispatchOrders(NaverOrderDispatchRequest request) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri("/v1/pay-order/seller/product-orders/dispatch")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        log.info("네이버 발송 처리 완료");
    }

    /** 발송 지연을 처리합니다. */
    public void delayDispatch(String productOrderId, NaverOrderDelayRequest request) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri("/v1/pay-order/seller/product-orders/{productOrderId}/delay", productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    /** 배송 희망일을 변경합니다. */
    public void changeHopeDelivery(String productOrderId, NaverWishedDeliveryDateRequest request) {
        String token = tokenManager.getAccessToken();

        restClient
                .post()
                .uri(
                        "/v1/pay-order/seller/product-orders/{productOrderId}/hope-delivery/change",
                        productOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();
    }

    // === Private Helpers ===

    private List<String> pollChangedProductOrderIds(Instant fromTime, Instant toTime) {
        List<String> allProductOrderIds = new ArrayList<>();
        String moreSequence = null;

        do {
            NaverLastChangedStatusesResponse response =
                    getLastChangedStatuses(fromTime, toTime, moreSequence);

            if (response == null || response.data() == null) {
                break;
            }

            response.data().lastChangeStatuses().stream()
                    .map(NaverLastChangedStatus::productOrderId)
                    .forEach(allProductOrderIds::add);

            moreSequence =
                    response.data().more() != null ? response.data().more().moreSequence() : null;
        } while (moreSequence != null);

        return allProductOrderIds;
    }

    private String formatForNaver(Instant instant) {
        return instant.atZone(KST).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }
}
