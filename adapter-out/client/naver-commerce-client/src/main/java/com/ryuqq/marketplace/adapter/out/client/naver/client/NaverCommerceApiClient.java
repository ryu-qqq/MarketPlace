package com.ryuqq.marketplace.adapter.out.client.naver.client;

import com.ryuqq.marketplace.adapter.out.client.naver.auth.NaverCommerceTokenManager;
import com.ryuqq.marketplace.adapter.out.client.naver.config.NaverCommerceClientConfig;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverImageUploadResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductSearchResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverCancelRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverClaimResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeReDeliveryRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverExchangeRejectRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatusesResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderConfirmRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDelayRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDispatchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderIdsResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderQueryRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRejectRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverReturnRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverWishedDeliveryDateRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverCustomerInquiryPageResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverInquiryAnswerRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverInquiryAnswerResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverProductQnaAnswerRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.qna.NaverProductQnaPageResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.support.NaverCommerceApiExecutor;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

/**
 * 네이버 커머스 HTTP 호출 담당 클라이언트.
 *
 * <p>모든 네이버 커머스 API HTTP 요청을 담당합니다. 토큰 주입, 요청 실행, 응답 파싱 등 HTTP 레벨 관심사만 처리합니다. {@link
 * NaverCommerceApiExecutor}를 통해 CB + Retry가 적용됩니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "naver-commerce", name = "client-id")
@SuppressWarnings("PMD.ExcessiveImports")
public class NaverCommerceApiClient {

    private static final Logger log = LoggerFactory.getLogger(NaverCommerceApiClient.class);

    private final RestClient restClient;
    private final NaverCommerceTokenManager tokenManager;
    private final NaverCommerceApiExecutor executor;

    public NaverCommerceApiClient(
            RestClient naverCommerceRestClient,
            NaverCommerceTokenManager tokenManager,
            NaverCommerceApiExecutor executor) {
        this.restClient = naverCommerceRestClient;
        this.tokenManager = tokenManager;
        this.executor = executor;
    }

    // ===== 상품 API =====

    /** 상품을 등록합니다. */
    public NaverProductRegistrationResponse registerProduct(
            NaverProductRegistrationRequest request) {
        log.info("네이버 커머스 상품 등록 요청");
        return executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        return restClient
                                .post()
                                .uri("/v2/products")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .body(request)
                                .retrieve()
                                .body(NaverProductRegistrationResponse.class);
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 상품 상세를 조회합니다. */
    public NaverProductDetailResponse getProductDetail(String originProductNo) {
        log.info("네이버 커머스 상품 상세 조회: originProductNo={}", originProductNo);
        return executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        return restClient
                                .get()
                                .uri(
                                        "/v2/products/origin-products/{originProductNo}",
                                        originProductNo)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .body(NaverProductDetailResponse.class);
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 상품을 수정합니다. */
    public void updateProduct(NaverProductRegistrationRequest request, String originProductNo) {
        log.info("네이버 커머스 상품 수정 요청: originProductNo={}", originProductNo);
        executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        restClient
                                .put()
                                .uri(
                                        "/v2/products/origin-products/{originProductNo}",
                                        originProductNo)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity();
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 상품을 삭제합니다. */
    public void deleteProduct(String originProductNo) {
        log.info("네이버 커머스 상품 삭제 요청: originProductNo={}", originProductNo);
        executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        restClient
                                .delete()
                                .uri(
                                        "/v2/products/origin-products/{originProductNo}",
                                        originProductNo)
                                .header("Authorization", "Bearer " + token)
                                .retrieve()
                                .toBodilessEntity();
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 상품 목록을 검색합니다 (단일 페이지). */
    public NaverProductSearchResponse searchProducts(NaverProductSearchRequest request) {
        log.info("네이버 커머스 상품 목록 조회: page={}, size={}", request.page(), request.size());
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri("/v1/products/search")
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(NaverProductSearchResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    // ===== 주문 API =====

    /** 주문번호로 상품주문번호 목록을 조회합니다. */
    public NaverProductOrderIdsResponse getProductOrderIds(String orderId) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .get()
                    .uri("/v1/pay-order/seller/orders/{orderId}/product-order-ids", orderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(NaverProductOrderIdsResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 조건형 상품주문 상세를 조회합니다. */
    public NaverProductOrderDetailResponse getProductOrdersConditional(
            String productOrderStatus, String from, String to) {
        String token = tokenManager.getAccessToken();
        try {
            NaverProductOrderDetailResponse response =
                    restClient
                            .get()
                            .uri(
                                    "/v1/pay-order/seller/product-orders"
                                            + "?productOrderStatus={status}"
                                            + "&lastChangedFrom={from}&lastChangedTo={to}",
                                    productOrderStatus,
                                    from,
                                    to)
                            .header("Authorization", "Bearer " + token)
                            .retrieve()
                            .body(NaverProductOrderDetailResponse.class);
            return response != null ? response : new NaverProductOrderDetailResponse(List.of());
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 변경 상품주문 내역을 조회합니다 (폴링용, type 지정). */
    public NaverLastChangedStatusesResponse getLastChangedStatuses(
            String type, String from, String to, int limit, String moreSequence) {
        return executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("type", type);
                        params.put("from", from);
                        params.put("to", to);
                        params.put("limit", limit);

                        String uri =
                                "/v1/pay-order/seller/product-orders/last-changed-statuses"
                                        + "?lastChangedType={type}"
                                        + "&lastChangedFrom={from}&lastChangedTo={to}"
                                        + "&limitCount={limit}";

                        if (moreSequence != null) {
                            params.put("seq", moreSequence);
                            return restClient
                                    .get()
                                    .uri(uri + "&moreSequence={seq}", params)
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
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 변경 상품주문 내역을 조회합니다 (폴링용, 전체 유형). */
    public NaverLastChangedStatusesResponse getLastChangedStatusesAll(
            String from, String to, int limit, String moreSequence) {
        return executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        Map<String, Object> params = new LinkedHashMap<>();
                        params.put("from", from);
                        params.put("to", to);
                        params.put("limit", limit);

                        String uri =
                                "/v1/pay-order/seller/product-orders/last-changed-statuses"
                                        + "?lastChangedFrom={from}&lastChangedTo={to}"
                                        + "&limitCount={limit}";

                        if (moreSequence != null) {
                            params.put("seq", moreSequence);
                            return restClient
                                    .get()
                                    .uri(uri + "&moreSequence={seq}", params)
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
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 상품주문 상세를 일괄 조회합니다. */
    public NaverProductOrderDetailResponse queryProductOrders(List<String> productOrderIds) {
        return executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    NaverProductOrderQueryRequest request =
                            new NaverProductOrderQueryRequest(productOrderIds);
                    try {
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
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    /** 발주를 확인합니다. */
    public void confirmOrders(List<String> productOrderIds) {
        String token = tokenManager.getAccessToken();
        NaverOrderConfirmRequest request = new NaverOrderConfirmRequest(productOrderIds);
        try {
            org.springframework.http.ResponseEntity<String> response =
                    restClient
                            .post()
                            .uri("/v1/pay-order/seller/product-orders/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .body(request)
                            .retrieve()
                            .toEntity(String.class);
            log.info(
                    "네이버 발주 확인 응답: status={}, body={}",
                    response.getStatusCode(),
                    response.getBody());
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
        log.info("네이버 발주 확인 완료: {}건", productOrderIds.size());
    }

    /** 발송을 처리합니다. */
    public void dispatchOrders(NaverOrderDispatchRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            org.springframework.http.ResponseEntity<String> response =
                    restClient
                            .post()
                            .uri("/v1/pay-order/seller/product-orders/dispatch")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("Authorization", "Bearer " + token)
                            .body(request)
                            .retrieve()
                            .toEntity(String.class);
            log.info(
                    "네이버 발송처리 응답: status={}, body={}",
                    response.getStatusCode(),
                    response.getBody());
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 발송 지연을 처리합니다. */
    public void delayDispatch(String productOrderId, NaverOrderDelayRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/delay",
                            productOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 배송 희망일을 변경합니다. */
    public void changeHopeDelivery(String productOrderId, NaverWishedDeliveryDateRequest request) {
        String token = tokenManager.getAccessToken();
        try {
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
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    // ===== 취소 API =====

    /** 취소를 요청합니다. */
    public NaverClaimResponse requestCancel(String productOrderId, NaverCancelRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel/request",
                            productOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 취소 요청을 승인합니다. */
    public NaverClaimResponse approveCancel(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/cancel/approve",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    // ===== 반품 API =====

    /** 반품을 요청합니다. */
    public NaverClaimResponse requestReturn(String productOrderId, NaverReturnRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/request",
                            productOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 반품 요청을 승인합니다. */
    public NaverClaimResponse approveReturn(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/approve",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 반품 요청을 거부합니다. */
    public NaverClaimResponse rejectReturn(
            String productOrderId, NaverReturnRejectRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/reject",
                            productOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 반품을 보류합니다. */
    public void holdbackReturn(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/holdback",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 반품 보류를 해제합니다. */
    public void releaseReturnHoldback(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/return/holdback/release",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    // ===== 교환 API =====

    /** 교환 수거완료를 승인합니다. */
    public NaverClaimResponse approveCollectedExchange(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/approve-collected",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 교환 재배송을 처리합니다. */
    public NaverClaimResponse reDeliverExchange(
            String productOrderId, NaverExchangeReDeliveryRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/dispatch",
                            productOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 교환 요청을 거부합니다. */
    public NaverClaimResponse rejectExchange(
            String productOrderId, NaverExchangeRejectRequest request) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/reject",
                            productOrderId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(NaverClaimResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 교환을 보류합니다. */
    public void holdbackExchange(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/holdback",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 교환 보류를 해제합니다. */
    public void releaseExchangeHoldback(String productOrderId) {
        String token = tokenManager.getAccessToken();
        try {
            restClient
                    .post()
                    .uri(
                            "/v1/pay-order/seller/product-orders/{productOrderId}/claim/exchange/holdback/release",
                            productOrderId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    // ===== QnA API =====

    /** 고객 문의 목록 조회. */
    public NaverCustomerInquiryPageResponse getCustomerInquiries(
            String startSearchDate,
            String endSearchDate,
            boolean answeredOnly,
            int page,
            int size) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .get()
                    .uri(
                            uriBuilder ->
                                    uriBuilder
                                            .path("/v1/pay-user/inquiries")
                                            .queryParam("startSearchDate", startSearchDate)
                                            .queryParam("endSearchDate", endSearchDate)
                                            .queryParam("answered", String.valueOf(answeredOnly))
                                            .queryParam("page", page)
                                            .queryParam("size", size)
                                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(NaverCustomerInquiryPageResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 고객 문의 답변 등록. */
    public NaverInquiryAnswerResponse insertInquiryAnswer(long inquiryNo, String answerComment) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .post()
                    .uri("/v1/pay-merchant/inquiries/{inquiryNo}/answer", inquiryNo)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(NaverInquiryAnswerRequest.of(answerComment))
                    .retrieve()
                    .body(NaverInquiryAnswerResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 고객 문의 답변 수정. */
    public NaverInquiryAnswerResponse updateInquiryAnswer(
            long inquiryNo, long answerContentId, String answerComment) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .put()
                    .uri(
                            "/v1/pay-merchant/inquiries/{inquiryNo}/answer/{answerContentId}",
                            inquiryNo,
                            answerContentId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(NaverInquiryAnswerRequest.of(answerComment))
                    .retrieve()
                    .body(NaverInquiryAnswerResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 상품 문의 목록 조회. */
    public NaverProductQnaPageResponse getProductQnas(
            String fromDate, String toDate, boolean answeredOnly, int page, int size) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .get()
                    .uri(
                            uriBuilder ->
                                    uriBuilder
                                            .path("/v1/contents/qnas")
                                            .queryParam("fromDate", fromDate)
                                            .queryParam("toDate", toDate)
                                            .queryParam("answered", String.valueOf(answeredOnly))
                                            .queryParam("page", page)
                                            .queryParam("size", size)
                                            .build())
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(NaverProductQnaPageResponse.class);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 상품 문의 답변 등록/수정. */
    public void answerProductQna(long questionId, String commentContent) {
        String token = tokenManager.getAccessToken();
        try {
            restClient
                    .put()
                    .uri("/v1/contents/qnas/{questionId}", questionId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + token)
                    .body(new NaverProductQnaAnswerRequest(commentContent))
                    .retrieve()
                    .toBodilessEntity();
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    // ===== 이미지 API =====

    /** 이미지를 업로드합니다. */
    public NaverImageUploadResponse uploadImages(MultiValueMap<String, Object> body) {
        return executor.execute(
                () -> {
                    String token = tokenManager.getAccessToken();
                    try {
                        NaverImageUploadResponse response =
                                restClient
                                        .post()
                                        .uri("/v1/product-images/upload")
                                        .contentType(MediaType.MULTIPART_FORM_DATA)
                                        .header("Authorization", "Bearer " + token)
                                        .body(body)
                                        .retrieve()
                                        .body(NaverImageUploadResponse.class);

                        if (response == null
                                || response.images() == null
                                || response.images().isEmpty()) {
                            throw new IllegalStateException("네이버 이미지 업로드 응답이 비어있습니다");
                        }

                        return response;
                    } catch (ResourceAccessException e) {
                        throw NaverCommerceClientConfig.toNetworkException(e);
                    }
                });
    }

    // ===== 카테고리 / 브랜드 API =====

    /** 전체 카테고리를 조회합니다. */
    public <T> T getCategories(ParameterizedTypeReference<T> responseType) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .get()
                    .uri("/v1/categories")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(responseType);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }

    /** 브랜드를 검색합니다. */
    public <T> T searchBrands(String name, ParameterizedTypeReference<T> responseType) {
        String token = tokenManager.getAccessToken();
        try {
            return restClient
                    .get()
                    .uri("/v1/product-brands?name={name}", name)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(responseType);
        } catch (ResourceAccessException e) {
            throw NaverCommerceClientConfig.toNetworkException(e);
        }
    }
}
