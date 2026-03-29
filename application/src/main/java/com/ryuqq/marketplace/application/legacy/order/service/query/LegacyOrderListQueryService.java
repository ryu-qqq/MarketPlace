package com.ryuqq.marketplace.application.legacy.order.service.query;

import com.ryuqq.marketplace.application.common.dto.query.CommonSearchParams;
import com.ryuqq.marketplace.application.legacy.order.assembler.LegacyOrderFromMarketAssembler;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailWithHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderPageResult;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderListQueryUseCase;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyOrderIdMappingReadManager;
import com.ryuqq.marketplace.application.order.dto.query.OrderSearchParams;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderPageResult;
import com.ryuqq.marketplace.application.order.port.in.query.GetProductOrderListUseCase;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 레거시 주문 목록 조회 서비스.
 *
 * <p>market 스키마에서 주문 목록 조회 후 레거시 응답 형식으로 변환합니다.
 */
@Service
public class LegacyOrderListQueryService implements LegacyOrderListQueryUseCase {

    private final GetProductOrderListUseCase getProductOrderListUseCase;
    private final LegacyOrderIdMappingReadManager mappingReadManager;
    private final ShipmentReadManager shipmentReadManager;
    private final LegacyOrderFromMarketAssembler assembler;

    public LegacyOrderListQueryService(
            GetProductOrderListUseCase getProductOrderListUseCase,
            LegacyOrderIdMappingReadManager mappingReadManager,
            ShipmentReadManager shipmentReadManager,
            LegacyOrderFromMarketAssembler assembler) {
        this.getProductOrderListUseCase = getProductOrderListUseCase;
        this.mappingReadManager = mappingReadManager;
        this.shipmentReadManager = shipmentReadManager;
        this.assembler = assembler;
    }

    @Override
    @Transactional(readOnly = true)
    public LegacyOrderPageResult execute(LegacyOrderSearchParams params) {
        OrderSearchParams marketParams = toMarketSearchParams(params);
        ProductOrderPageResult pageResult = getProductOrderListUseCase.execute(marketParams);

        if (pageResult.productOrders().isEmpty()) {
            return new LegacyOrderPageResult(List.of(), 0, null);
        }

        return convertFromMarket(pageResult);
    }

    private LegacyOrderPageResult convertFromMarket(ProductOrderPageResult pageResult) {
        List<ProductOrderListResult> items = pageResult.productOrders();

        List<Long> orderItemIds = items.stream().map(i -> i.productOrder().orderItemId()).toList();
        Map<Long, LegacyOrderIdMapping> mappingMap =
                mappingReadManager.findByInternalOrderItemIds(orderItemIds).stream()
                        .collect(
                                Collectors.toMap(
                                        LegacyOrderIdMapping::internalOrderItemId,
                                        Function.identity()));

        List<OrderItemId> itemIdList = orderItemIds.stream().map(OrderItemId::of).toList();
        Map<Long, Shipment> shipmentMap =
                shipmentReadManager.findByOrderItemIds(itemIdList).stream()
                        .collect(Collectors.toMap(Shipment::orderItemIdValue, Function.identity()));

        List<LegacyOrderDetailWithHistoryResult> converted = new ArrayList<>();
        for (ProductOrderListResult item : items) {
            Long itemId = item.productOrder().orderItemId();
            LegacyOrderIdMapping mapping = mappingMap.get(itemId);
            if (mapping == null) {
                mapping = LegacyOrderIdMapping.fallback(itemId, itemId, item.order().orderId());
            }
            Shipment shipment = shipmentMap.get(itemId);

            LegacyOrderDetailResult detail =
                    assembler.toDetailResultFromListItem(item, mapping, shipment);
            converted.add(new LegacyOrderDetailWithHistoryResult(detail, List.of()));
        }

        long totalElements = pageResult.pageMeta().totalElements();
        Long lastDomainId = converted.isEmpty() ? null : converted.getLast().order().orderId();

        return new LegacyOrderPageResult(converted, totalElements, lastDomainId);
    }

    private OrderSearchParams toMarketSearchParams(LegacyOrderSearchParams params) {
        CommonSearchParams commonParams =
                CommonSearchParams.of(
                        false,
                        params.startDate() != null ? params.startDate().toLocalDate() : null,
                        params.endDate() != null ? params.endDate().toLocalDate() : null,
                        "createdAt",
                        "DESC",
                        0,
                        params.size());

        List<String> marketStatuses = convertLegacyStatuses(params.orderStatusList());
        String dateField = convertPeriodType(params.periodType());
        String searchField = convertSearchKeyword(params.searchKeyword());

        return new OrderSearchParams(
                marketStatuses,
                searchField,
                params.searchWord(),
                dateField,
                params.sellerId(),
                commonParams);
    }

    /** 레거시 periodType → market dateField 변환. */
    private String convertPeriodType(String periodType) {
        if (periodType == null) {
            return null;
        }
        return switch (periodType) {
            case "PAYMENT" -> "ORDERED";
            case "SETTLEMENT" -> "ORDERED";
            case "ORDER_HISTORY" -> "ORDERED";
            default -> "ORDERED";
        };
    }

    /** 레거시 searchKeyword → market searchField 변환. */
    private String convertSearchKeyword(String searchKeyword) {
        if (searchKeyword == null) {
            return null;
        }
        return switch (searchKeyword) {
            case "ORDER_ID" -> "orderId";
            case "PAYMENT_ID" -> "paymentId";
            case "SELLER_ID" -> "sellerId";
            case "SELLER_NAME" -> "sellerName";
            case "PRODUCT_GROUP_NAME" -> "productGroupName";
            case "PRODUCT_GROUP_ID" -> "productGroupId";
            case "BUYER_NAME" -> "buyerName";
            case "MEMBER_ID" -> "buyerId";
            default -> null;
        };
    }

    private List<String> convertLegacyStatuses(List<String> legacyStatuses) {
        if (legacyStatuses == null || legacyStatuses.isEmpty()) {
            return List.of();
        }
        return legacyStatuses.stream().map(this::toLegacyToMarketStatus).distinct().toList();
    }

    private String toLegacyToMarketStatus(String legacyStatus) {
        return switch (legacyStatus) {
            case "ORDER_PROCESSING", "ORDER_COMPLETED" -> "READY";
            case "DELIVERY_PENDING",
                            "DELIVERY_PROCESSING",
                            "DELIVERY_COMPLETED",
                            "SETTLEMENT_PROCESSING",
                            "SETTLEMENT_COMPLETED" ->
                    "CONFIRMED";
            case "CANCEL_REQUEST_COMPLETED",
                            "CANCEL_REQUEST_CONFIRMED",
                            "SALE_CANCELLED",
                            "SALE_CANCELLED_COMPLETED" ->
                    "CANCELLED";
            case "RETURN_REQUEST", "RETURN_REQUEST_CONFIRMED" -> "RETURN_REQUESTED";
            case "RETURN_REQUEST_COMPLETED", "RETURN_REQUEST_REJECTED" -> "RETURNED";
            default -> "READY";
        };
    }
}
