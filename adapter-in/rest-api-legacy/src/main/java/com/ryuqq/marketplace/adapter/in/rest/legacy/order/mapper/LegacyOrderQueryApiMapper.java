package com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyOrderSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderHistoryResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderListResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.LegacyOrderProductInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.LegacyPaymentInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.LegacyReceiverInfo;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailWithHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 조회 API Mapper.
 *
 * <p>Request→SearchParams, DetailResult→OrderResponse 변환.
 */
@Component
public class LegacyOrderQueryApiMapper {

    /**
     * 요청 DTO → 검색 파라미터 변환.
     *
     * @param request 검색 요청
     * @param effectiveSellerId MASTER이면 null (전체 조회), SELLER이면 본인 ID (강제 필터)
     */
    public LegacyOrderSearchParams toSearchParams(
            LegacyOrderSearchRequest request, Long effectiveSellerId) {
        return new LegacyOrderSearchParams(
                request.orderStatusList(),
                request.lastDomainId(),
                request.startDate(),
                request.endDate(),
                effectiveSellerId != null ? effectiveSellerId : request.sellerId(),
                request.resolvedSize());
    }

    public LegacyOrderResponse toOrderResponse(LegacyOrderDetailResult result) {
        LegacyPaymentInfo payment =
                new LegacyPaymentInfo(
                        result.paymentId(),
                        result.orderAmount(),
                        result.commissionRate(),
                        result.shareRatio());

        LegacyReceiverInfo receiverInfo =
                new LegacyReceiverInfo(
                        nullToEmpty(result.receiverName()),
                        nullToEmpty(result.receiverPhone()),
                        nullToEmpty(result.receiverAddress()),
                        nullToEmpty(result.receiverAddressDetail()),
                        nullToEmpty(result.receiverZipCode()),
                        nullToEmpty(result.deliveryRequest()));

        String optionString =
                result.optionValues() != null ? String.join(" ", result.optionValues()) : "";

        LegacyOrderProductInfo orderProduct =
                new LegacyOrderProductInfo(
                        result.productGroupId(),
                        result.productId(),
                        nullToEmpty(result.productGroupName()),
                        nullToEmpty(result.brandName()),
                        nullToEmpty(result.mainImageUrl()),
                        result.quantity(),
                        nullToEmpty(result.orderStatus()),
                        result.regularPrice(),
                        result.orderAmount(),
                        optionString,
                        result.optionValues() != null ? result.optionValues() : List.of());

        return new LegacyOrderResponse(
                result.orderId(), "", payment, receiverInfo, orderProduct, result.orderDate());
    }

    public List<LegacyOrderListResponse> toOrderListResponses(
            List<LegacyOrderDetailWithHistoryResult> items) {
        return items.stream()
                .map(
                        item ->
                                new LegacyOrderListResponse(
                                        toOrderResponse(item.order()),
                                        toHistoryResponses(item.histories())))
                .toList();
    }

    private List<LegacyOrderHistoryResponse> toHistoryResponses(
            List<LegacyOrderHistoryResult> histories) {
        if (histories == null || histories.isEmpty()) {
            return List.of();
        }
        return histories.stream()
                .map(
                        h ->
                                new LegacyOrderHistoryResponse(
                                        h.orderHistoryId(),
                                        h.orderId(),
                                        h.orderStatus(),
                                        h.changeReason(),
                                        h.changeDetailReason(),
                                        h.createdAt()))
                .toList();
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
