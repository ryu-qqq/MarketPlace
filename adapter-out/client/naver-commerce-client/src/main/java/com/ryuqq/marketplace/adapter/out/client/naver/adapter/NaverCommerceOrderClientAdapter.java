package com.ryuqq.marketplace.adapter.out.client.naver.adapter;

import com.ryuqq.marketplace.adapter.out.client.naver.client.NaverCommerceApiClient;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatus;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverLastChangedStatusesResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDelayRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverOrderDispatchRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetailResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderIdsResponse;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverWishedDeliveryDateRequest;
import com.ryuqq.marketplace.adapter.out.client.naver.mapper.NaverCommerceOrderMapper;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.port.out.client.SalesChannelOrderClient;
import com.ryuqq.marketplace.domain.shop.vo.ShopCredentials;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 주문 조회 및 발주/발송 클라이언트 어댑터.
 *
 * <p>SalesChannelOrderClient를 구현하여 네이버 커머스 주문 API 엔드포인트를 제공합니다. fetchNewOrders는 2-phase
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

    private final NaverCommerceApiClient apiClient;
    private final NaverCommerceOrderMapper mapper;

    public NaverCommerceOrderClientAdapter(
            NaverCommerceApiClient apiClient, NaverCommerceOrderMapper mapper) {
        this.apiClient = apiClient;
        this.mapper = mapper;
    }

    @Override
    public String channelCode() {
        return "NAVER";
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
        return apiClient.getProductOrderIds(orderId);
    }

    /** 조건형 상품주문 상세를 조회합니다. */
    public NaverProductOrderDetailResponse getProductOrdersConditional(
            String productOrderStatus, Instant from, Instant to) {
        return apiClient.getProductOrdersConditional(
                productOrderStatus, formatForNaver(from), formatForNaver(to));
    }

    /** 변경 상품주문 내역을 조회합니다 (폴링용). */
    public NaverLastChangedStatusesResponse getLastChangedStatuses(
            Instant fromTime, Instant toTime, String moreSequence) {
        return apiClient.getLastChangedStatuses(
                LAST_CHANGED_TYPE_PAYED,
                formatForNaver(fromTime),
                formatForNaver(toTime),
                MAX_BATCH_SIZE,
                moreSequence);
    }

    /**
     * 클레임 변경 상품주문 내역을 조회합니다 (lastChangedType 없이 전체 조회).
     *
     * <p>lastChangedType 파라미터를 생략하여 전체 변경 유형을 조회합니다. 클레임 어댑터에서 필터링에 사용합니다.
     */
    public NaverLastChangedStatusesResponse getLastChangedStatusesAll(
            Instant fromTime, Instant toTime, String moreSequence) {
        return apiClient.getLastChangedStatusesAll(
                formatForNaver(fromTime), formatForNaver(toTime), MAX_BATCH_SIZE, moreSequence);
    }

    /** 상품주문 상세를 일괄 조회합니다. */
    public NaverProductOrderDetailResponse queryProductOrders(List<String> productOrderIds) {
        return apiClient.queryProductOrders(productOrderIds);
    }

    // === 발주/발송 ===

    /** 발주를 확인합니다 (최대 30건). */
    public void confirmOrders(List<String> productOrderIds) {
        apiClient.confirmOrders(productOrderIds);
    }

    /** 발송을 처리합니다 (최대 30건). */
    public void dispatchOrders(NaverOrderDispatchRequest request) {
        apiClient.dispatchOrders(request);
    }

    /** 발송 지연을 처리합니다. */
    public void delayDispatch(String productOrderId, NaverOrderDelayRequest request) {
        apiClient.delayDispatch(productOrderId, request);
    }

    /** 배송 희망일을 변경합니다. */
    public void changeHopeDelivery(String productOrderId, NaverWishedDeliveryDateRequest request) {
        apiClient.changeHopeDelivery(productOrderId, request);
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
