package com.ryuqq.marketplace.adapter.in.rest.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.util.DateTimeFormatUtils;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.query.SearchOrdersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.BuyerInfoApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.OrderItemApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderDetailApiResponse.OrderTimeLineApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.order.dto.response.OrderSummaryApiResponse;
import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.OrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderItemResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderPageResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderSummaryResult;
import java.util.List;
import org.springframework.stereotype.Component;

/** Order Query API Mapper. */
@Component
public class OrderQueryApiMapper {

    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;

    public OrderSearchParams toSearchParams(SearchOrdersApiRequest request) {
        int page = request.page() != null ? request.page() : DEFAULT_PAGE;
        int size = request.size() != null ? request.size() : DEFAULT_SIZE;

        CommonSearchParams searchParams =
                CommonSearchParams.of(
                        null,
                        request.startDate(),
                        request.endDate(),
                        request.sortKey(),
                        request.sortDirection(),
                        page,
                        size);

        return new OrderSearchParams(
                request.status(),
                request.searchField(),
                request.searchWord(),
                request.dateField(),
                searchParams);
    }

    public OrderListApiResponse toListResponse(OrderListResult result) {
        return new OrderListApiResponse(
                result.orderId(),
                result.orderNumber(),
                result.status(),
                result.salesChannelId(),
                result.externalOrderNo(),
                result.buyerName(),
                result.itemCount(),
                DateTimeFormatUtils.formatIso8601(result.orderedAt()),
                DateTimeFormatUtils.formatIso8601(result.createdAt()));
    }

    public PageApiResponse<OrderListApiResponse> toPageResponse(OrderPageResult pageResult) {
        List<OrderListApiResponse> responses =
                pageResult.orders().stream().map(this::toListResponse).toList();
        return PageApiResponse.of(
                responses,
                pageResult.pageMeta().page(),
                pageResult.pageMeta().size(),
                pageResult.pageMeta().totalElements());
    }

    public OrderDetailApiResponse toDetailResponse(OrderDetailResult result) {
        BuyerInfoApiResponse buyerInfo = toBuyerInfoResponse(result.buyerInfo());
        List<OrderItemApiResponse> items =
                result.items().stream().map(this::toItemResponse).toList();
        List<OrderTimeLineApiResponse> timeLine =
                result.histories().stream().map(this::toTimeLineResponse).toList();

        return new OrderDetailApiResponse(
                result.orderId(),
                result.orderNumber(),
                result.status(),
                result.salesChannelId(),
                result.shopId(),
                result.externalOrderNo(),
                DateTimeFormatUtils.formatIso8601(result.externalOrderedAt()),
                buyerInfo,
                items,
                timeLine,
                DateTimeFormatUtils.formatIso8601(result.orderedAt()),
                DateTimeFormatUtils.formatIso8601(result.createdAt()),
                DateTimeFormatUtils.formatIso8601(result.updatedAt()));
    }

    public OrderSummaryApiResponse toSummaryResponse(OrderSummaryResult result) {
        return new OrderSummaryApiResponse(
                result.ordered(),
                result.preparing(),
                result.shipped(),
                result.delivered(),
                result.confirmed(),
                result.cancelled(),
                result.claimInProgress(),
                result.refunded(),
                result.exchanged());
    }

    private BuyerInfoApiResponse toBuyerInfoResponse(OrderDetailResult.BuyerInfoResult buyerInfo) {
        return new BuyerInfoApiResponse(
                buyerInfo.buyerName(), buyerInfo.email(), buyerInfo.phoneNumber());
    }

    private OrderItemApiResponse toItemResponse(OrderItemResult item) {
        return new OrderItemApiResponse(
                item.orderItemId(),
                item.productGroupId(),
                item.productId(),
                item.skuCode(),
                item.externalProductId(),
                item.externalProductName(),
                item.externalOptionName(),
                item.externalImageUrl(),
                item.unitPrice(),
                item.quantity(),
                item.paymentAmount(),
                item.receiverName());
    }

    private OrderTimeLineApiResponse toTimeLineResponse(OrderHistoryResult history) {
        return new OrderTimeLineApiResponse(
                history.historyId(),
                history.fromStatus(),
                history.toStatus(),
                history.changedBy(),
                history.reason(),
                DateTimeFormatUtils.formatIso8601(history.changedAt()));
    }
}
