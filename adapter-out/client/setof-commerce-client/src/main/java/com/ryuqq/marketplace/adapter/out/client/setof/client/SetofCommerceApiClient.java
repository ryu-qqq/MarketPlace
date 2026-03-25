package com.ryuqq.marketplace.adapter.out.client.setof.client;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofDescriptionRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImageVariantSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofImagesRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofNoticeRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupBasicInfoUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupRegistrationResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductPriceUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductStockUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductsUpdateRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofRefundPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerAddressSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSellerSyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofShippingPolicySyncRequest;
import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofSyncApiResponse;
import com.ryuqq.marketplace.adapter.out.client.setof.support.SetofCommerceApiExecutor;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 세토프 커머스 HTTP 호출 담당 클라이언트.
 *
 * <p>모든 외부 HTTP 호출을 이 클래스에서 처리합니다. {@link SetofCommerceApiExecutor}를 통해 CB + Retry 보호 하에 실행합니다.
 *
 * <p>Adapter는 비즈니스 로직(매핑 등)을 담당하고, HTTP 호출 자체는 이 클래스에 위임합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofCommerceApiClient {

    private static final Logger log = LoggerFactory.getLogger(SetofCommerceApiClient.class);
    private static final String SERVICE_TOKEN_HEADER = "X-Service-Token";

    private final RestClient restClient;
    private final SetofCommerceApiExecutor executor;

    public SetofCommerceApiClient(
            RestClient setofCommerceRestClient, SetofCommerceApiExecutor executor) {
        this.restClient = setofCommerceRestClient;
        this.executor = executor;
    }

    // ===== 상품 그룹 =====

    /**
     * 상품 그룹 등록.
     *
     * <p>POST /api/v2/admin/product-groups
     */
    public SetofProductGroupRegistrationResponse registerProduct(
            String shopSecret, SetofProductGroupRegistrationRequest request) {
        log.info("세토프 커머스 상품 그룹 등록 요청");
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/admin/product-groups")
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SetofProductGroupRegistrationResponse.class));
    }

    /**
     * 상품 그룹 전체 수정.
     *
     * <p>PUT /api/v2/admin/product-groups/{productGroupId}
     */
    public void updateProduct(
            String shopSecret, String externalProductId, SetofProductGroupUpdateRequest request) {
        log.info("세토프 커머스 상품 그룹 전체 수정 요청: externalProductId={}", externalProductId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}",
                                        externalProductId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 상품 그룹 조회.
     *
     * <p>GET /api/v2/admin/product-groups/{productGroupId}
     */
    public SetofProductGroupDetailResponse getProduct(
            String shopSecret, String externalProductId) {
        log.info("세토프 커머스 상품 그룹 조회 요청: externalProductId={}", externalProductId);
        return executor.execute(
                () ->
                        restClient
                                .get()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}",
                                        externalProductId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .retrieve()
                                .body(SetofProductGroupDetailResponse.class));
    }

    /**
     * 상품 그룹 기본정보 수정.
     *
     * <p>PATCH /api/v2/admin/product-groups/{productGroupId}/basic-info
     */
    public void updateBasicInfo(
            String shopSecret,
            String externalProductGroupId,
            SetofProductGroupBasicInfoUpdateRequest request) {
        log.info("세토프 커머스 기본정보 수정 요청: externalProductGroupId={}", externalProductGroupId);
        executor.execute(
                () ->
                        restClient
                                .patch()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/basic-info",
                                        externalProductGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 상품 가격 수정.
     *
     * <p>PATCH /api/v2/admin/products/{productId}/price
     */
    public void updatePrice(
            String shopSecret, Long productId, SetofProductPriceUpdateRequest request) {
        log.info("세토프 커머스 상품 가격 수정 요청: productId={}", productId);
        executor.execute(
                () ->
                        restClient
                                .patch()
                                .uri("/api/v2/admin/products/{productId}/price", productId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 상품 재고 수정.
     *
     * <p>PATCH /api/v2/admin/products/{productId}/stock
     */
    public void updateStock(
            String shopSecret, Long productId, SetofProductStockUpdateRequest request) {
        log.info("세토프 커머스 상품 재고 수정 요청: productId={}", productId);
        executor.execute(
                () ->
                        restClient
                                .patch()
                                .uri("/api/v2/admin/products/{productId}/stock", productId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 상품+옵션 일괄 수정.
     *
     * <p>PATCH /api/v2/admin/products/product-groups/{productGroupId}
     */
    public void updateProducts(
            String shopSecret, Long productGroupId, SetofProductsUpdateRequest request) {
        log.info("세토프 커머스 상품+옵션 일괄 수정 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .patch()
                                .uri(
                                        "/api/v2/admin/products/product-groups/{productGroupId}",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 이미지 =====

    /**
     * 이미지 등록.
     *
     * <p>POST /api/v2/admin/product-groups/{productGroupId}/images
     */
    public void registerImages(
            String shopSecret, Long productGroupId, SetofImagesRequest request) {
        log.info("세토프 커머스 이미지 등록 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/images",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 이미지 수정.
     *
     * <p>PUT /api/v2/admin/product-groups/{productGroupId}/images
     */
    public void updateImages(
            String shopSecret, Long productGroupId, SetofImagesRequest request) {
        log.info("세토프 커머스 이미지 수정 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/images",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 상세설명 =====

    /**
     * 상세설명 등록.
     *
     * <p>POST /api/v2/admin/product-groups/{productGroupId}/description
     */
    public void registerDescription(
            String shopSecret, Long productGroupId, SetofDescriptionRequest request) {
        log.info("세토프 커머스 상세설명 등록 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/description",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 상세설명 수정.
     *
     * <p>PUT /api/v2/admin/product-groups/{productGroupId}/description
     */
    public void updateDescription(
            String shopSecret, Long productGroupId, SetofDescriptionRequest request) {
        log.info("세토프 커머스 상세설명 수정 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/description",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 고시정보 =====

    /**
     * 고시정보 등록.
     *
     * <p>POST /api/v2/admin/product-groups/{productGroupId}/notice
     */
    public void registerNotice(
            String shopSecret, Long productGroupId, SetofNoticeRequest request) {
        log.info("세토프 커머스 고시정보 등록 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/notice",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 고시정보 수정.
     *
     * <p>PUT /api/v2/admin/product-groups/{productGroupId}/notice
     */
    public void updateNotice(
            String shopSecret, Long productGroupId, SetofNoticeRequest request) {
        log.info("세토프 커머스 고시정보 수정 요청: productGroupId={}", productGroupId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/product-groups/{productGroupId}/notice",
                                        productGroupId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 이미지 Variant =====

    /**
     * 이미지 Variant 동기화.
     *
     * <p>PUT /api/v2/admin/image-variants/sync
     */
    public void syncImageVariants(String shopSecret, SetofImageVariantSyncRequest request) {
        log.info("세토프 커머스 이미지 Variant 동기화 요청: sourceImageId={}", request.sourceImageId());
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri("/api/v2/admin/image-variants/sync")
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 셀러 =====

    /**
     * 셀러 등록.
     *
     * <p>POST /api/v2/admin/sellers
     */
    public SetofSyncApiResponse createSeller(String shopSecret, SetofSellerSyncRequest request) {
        log.info("세토프 커머스 셀러 등록 요청");
        return executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/admin/sellers")
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .body(SetofSyncApiResponse.class));
    }

    /**
     * 셀러 수정.
     *
     * <p>PUT /api/v2/admin/sellers/{sellerId}
     */
    public void updateSeller(
            String shopSecret, Long sellerId, SetofSellerSyncRequest request) {
        log.info("세토프 커머스 셀러 수정 요청: sellerId={}", sellerId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri("/api/v2/admin/sellers/{sellerId}", sellerId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 셀러 주소 =====

    /**
     * 셀러 주소 등록.
     *
     * <p>POST /api/v2/admin/seller-addresses/sellers/{sellerId}
     */
    public void createSellerAddress(
            String shopSecret, Long sellerId, SetofSellerAddressSyncRequest request) {
        log.info("세토프 커머스 셀러주소 등록 요청: sellerId={}", sellerId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/admin/seller-addresses/sellers/{sellerId}", sellerId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 셀러 주소 수정.
     *
     * <p>PUT /api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}
     */
    public void updateSellerAddress(
            String shopSecret,
            Long sellerId,
            Long addressId,
            SetofSellerAddressSyncRequest request) {
        log.info("세토프 커머스 셀러주소 수정 요청: sellerId={}, addressId={}", sellerId, addressId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}",
                                        sellerId,
                                        addressId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 셀러 주소 삭제.
     *
     * <p>DELETE /api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}
     */
    public void deleteSellerAddress(String shopSecret, Long sellerId, Long addressId) {
        log.info("세토프 커머스 셀러주소 삭제 요청: sellerId={}, addressId={}", sellerId, addressId);
        executor.execute(
                () ->
                        restClient
                                .delete()
                                .uri(
                                        "/api/v2/admin/seller-addresses/sellers/{sellerId}/{addressId}",
                                        sellerId,
                                        addressId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 환불 정책 =====

    /**
     * 환불 정책 등록.
     *
     * <p>POST /api/v2/admin/sellers/{sellerId}/refund-policies
     */
    public void createRefundPolicy(
            String shopSecret, Long sellerId, SetofRefundPolicySyncRequest request) {
        log.info("세토프 커머스 환불정책 등록 요청: sellerId={}", sellerId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/admin/sellers/{sellerId}/refund-policies", sellerId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 환불 정책 수정.
     *
     * <p>PUT /api/v2/admin/sellers/{sellerId}/refund-policies/{policyId}
     */
    public void updateRefundPolicy(
            String shopSecret,
            Long sellerId,
            Long policyId,
            SetofRefundPolicySyncRequest request) {
        log.info("세토프 커머스 환불정책 수정 요청: sellerId={}, policyId={}", sellerId, policyId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/sellers/{sellerId}/refund-policies/{policyId}",
                                        sellerId,
                                        policyId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 배송 정책 =====

    /**
     * 배송 정책 등록.
     *
     * <p>POST /api/v2/admin/sellers/{sellerId}/shipping-policies
     */
    public void createShippingPolicy(
            String shopSecret, Long sellerId, SetofShippingPolicySyncRequest request) {
        log.info("세토프 커머스 배송정책 등록 요청: sellerId={}", sellerId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/admin/sellers/{sellerId}/shipping-policies", sellerId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 배송 정책 수정.
     *
     * <p>PUT /api/v2/admin/sellers/{sellerId}/shipping-policies/{policyId}
     */
    public void updateShippingPolicy(
            String shopSecret,
            Long sellerId,
            Long policyId,
            SetofShippingPolicySyncRequest request) {
        log.info("세토프 커머스 배송정책 수정 요청: sellerId={}, policyId={}", sellerId, policyId);
        executor.execute(
                () ->
                        restClient
                                .put()
                                .uri(
                                        "/api/v2/admin/sellers/{sellerId}/shipping-policies/{policyId}",
                                        sellerId,
                                        policyId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(request)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 주문 =====

    /**
     * 주문 확인.
     *
     * <p>POST /api/v2/orders/{orderItemId}/confirm
     */
    public void confirmOrder(String shopSecret, String orderItemId) {
        log.info("세토프 커머스 주문 확인 요청: orderItemId={}", orderItemId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/orders/{orderItemId}/confirm", orderItemId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 배송 준비 완료.
     *
     * <p>POST /api/v2/orders/{orderItemId}/ready-to-ship
     */
    public void readyToShip(String shopSecret, String orderItemId) {
        log.info("세토프 커머스 배송 준비 완료 요청: orderItemId={}", orderItemId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/orders/{orderItemId}/ready-to-ship", orderItemId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 취소 =====

    /**
     * 취소 승인.
     *
     * <p>POST /api/v2/cancels/{cancelId}/approve
     */
    public void approveCancel(String shopSecret, String cancelId) {
        log.info("세토프 커머스 취소 승인 요청: cancelId={}", cancelId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/cancels/{cancelId}/approve", cancelId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 취소 거부.
     *
     * <p>POST /api/v2/cancels/{cancelId}/reject
     */
    public void rejectCancel(String shopSecret, String cancelId, String rejectReason) {
        log.info("세토프 커머스 취소 거부 요청: cancelId={}", cancelId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/cancels/{cancelId}/reject", cancelId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .body(Map.of("rejectReason", rejectReason))
                                .retrieve()
                                .toBodilessEntity());
    }

    // ===== 반품 =====

    /**
     * 반품 완료.
     *
     * <p>POST /api/v2/refunds/{refundId}/complete
     */
    public void completeRefund(String shopSecret, String refundId) {
        log.info("세토프 커머스 반품 완료 요청: refundId={}", refundId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/refunds/{refundId}/complete", refundId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .retrieve()
                                .toBodilessEntity());
    }

    /**
     * 반품 거부.
     *
     * <p>POST /api/v2/refunds/{refundId}/reject
     */
    public void rejectRefund(String shopSecret, String refundId, String rejectReason) {
        log.info("세토프 커머스 반품 거부 요청: refundId={}", refundId);
        executor.execute(
                () ->
                        restClient
                                .post()
                                .uri("/api/v2/refunds/{refundId}/reject", refundId)
                                .header(SERVICE_TOKEN_HEADER, shopSecret)
                                .body(Map.of("rejectReason", rejectReason))
                                .retrieve()
                                .toBodilessEntity());
    }
}
