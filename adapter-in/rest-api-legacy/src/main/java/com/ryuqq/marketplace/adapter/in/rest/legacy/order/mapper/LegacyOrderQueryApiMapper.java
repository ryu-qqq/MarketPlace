package com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper;

import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyOrderSearchRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.BrandInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.BuyerInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ClothesDetailInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.OptionInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.OrderHistoryInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.OrderProductInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.PaymentInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.PaymentShipmentInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.PriceInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ProductGroupDetails;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ProductStatusInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.ReceiverInfo;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse.SettlementInfo;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailWithHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 조회 API Mapper.
 *
 * <p>Request→SearchParams, DetailResult→OrderResponse 변환. 세토프 레거시 어드민과 동일한 flat 구조로 매핑.
 */
@Component
public class LegacyOrderQueryApiMapper {

    private static final DateTimeFormatter SETOF_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.of("Asia/Seoul"));

    public LegacyOrderSearchParams toSearchParams(
            LegacyOrderSearchRequest request, Long effectiveSellerId) {
        return new LegacyOrderSearchParams(
                request.orderStatusList(),
                request.lastDomainId(),
                request.startDate(),
                request.endDate(),
                effectiveSellerId != null ? effectiveSellerId : request.sellerId(),
                request.resolvedSize(),
                request.periodType(),
                request.searchKeyword(),
                request.searchWord());
    }

    /** 단건 조회용. */
    public LegacyOrderResponse toOrderResponse(LegacyOrderDetailResult result) {
        return toOrderResponse(result, null);
    }

    /** 목록 조회용. */
    public LegacyOrderResponse toOrderResponse(
            LegacyOrderDetailResult result, List<LegacyOrderHistoryResult> histories) {

        BuyerInfo buyerInfo =
                new BuyerInfo(
                        nullToEmpty(result.buyerName()),
                        nullToEmpty(result.buyerEmail()),
                        nullToEmpty(result.buyerPhone()));

        PaymentInfo payment =
                new PaymentInfo(
                        result.paymentId(),
                        nullToEmpty(result.paymentAgencyId()),
                        nullToEmpty(result.paymentStatus()),
                        nullToEmpty(result.paymentMethod()),
                        formatDate(result.orderDate()),
                        formatDateNullable(result.paymentCanceledDate()),
                        result.userId(),
                        nullToEmpty(result.siteName()),
                        result.billAmount(),
                        result.orderAmount(),
                        result.usedMileageAmount());

        ReceiverInfo receiverInfo =
                new ReceiverInfo(
                        nullToEmpty(result.receiverName()),
                        nullToEmpty(result.receiverPhone()),
                        nullToEmpty(result.receiverAddress()),
                        nullToEmpty(result.receiverAddressDetail()),
                        nullToEmpty(result.receiverZipCode()),
                        "KR",
                        nullToEmpty(result.deliveryRequest()));

        PaymentShipmentInfo paymentShipmentInfo =
                new PaymentShipmentInfo(
                        nullToEmpty(result.deliveryStatus()),
                        nullToEmpty(result.shipmentCompanyCode()),
                        nullToEmpty(result.shipmentInvoiceNo()),
                        formatDateNullable(result.shipmentCompletedDate()));

        double commissionRateDouble = result.commissionRate();
        double fee = result.orderAmount() * commissionRateDouble / 100.0;
        long expectationSettlementAmount = Math.round(result.orderAmount() - fee);
        double shareRatioDouble = result.shareRatio();

        SettlementInfo settlementInfo =
                new SettlementInfo(
                        commissionRateDouble,
                        fee,
                        expectationSettlementAmount,
                        expectationSettlementAmount,
                        shareRatioDouble,
                        null,
                        null);

        String optionString =
                result.optionValues() != null ? String.join(" ", result.optionValues()) : "";

        List<OptionInfo> optionInfos = buildOptionInfos(result.optionValues());

        PriceInfo priceInfo =
                new PriceInfo(
                        result.regularPrice(),
                        result.currentPrice(),
                        result.currentPrice(),
                        result.directDiscountPrice(),
                        0,
                        result.discountRate());

        ProductStatusInfo productStatus = new ProductStatusInfo("N", "Y");

        ClothesDetailInfo clothesDetailInfo =
                new ClothesDetailInfo(
                        nullToEmpty(result.productCondition()),
                        nullToEmpty(result.origin()),
                        nullToEmpty(result.styleCode()));

        ProductGroupDetails productGroupDetails =
                new ProductGroupDetails(
                        nullToEmpty(result.productGroupName()),
                        nullToEmpty(result.optionType()),
                        nullToEmpty(result.managementType()),
                        priceInfo,
                        productStatus,
                        clothesDetailInfo,
                        result.sellerId(),
                        result.categoryId(),
                        result.brandId());

        BrandInfo brand = new BrandInfo(result.brandId(), nullToEmpty(result.brandName()));

        OrderProductInfo orderProduct =
                new OrderProductInfo(
                        result.orderId(),
                        productGroupDetails,
                        brand,
                        result.productGroupId(),
                        result.productId(),
                        nullToEmpty(result.sellerName()),
                        nullToEmpty(result.mainImageUrl()),
                        nullToEmpty(result.deliveryArea()),
                        result.quantity(),
                        nullToEmpty(result.orderStatus()),
                        result.regularPrice(),
                        result.orderAmount(),
                        0,
                        optionString,
                        nullToEmpty(result.skuNumber()),
                        optionInfos);

        List<OrderHistoryInfo> orderHistories = toOrderHistoryInfos(histories);

        return new LegacyOrderResponse(
                result.orderId(),
                buyerInfo,
                payment,
                receiverInfo,
                paymentShipmentInfo,
                settlementInfo,
                orderProduct,
                orderHistories);
    }

    public List<LegacyOrderResponse> toOrderListResponses(
            List<LegacyOrderDetailWithHistoryResult> items) {
        return items.stream().map(item -> toOrderResponse(item.order(), item.histories())).toList();
    }

    /** 단건 이력 → OrderHistoryInfo 변환. */
    public OrderHistoryInfo toOrderHistoryInfo(LegacyOrderHistoryResult h) {
        return new OrderHistoryInfo(
                h.orderId(),
                nullToEmpty(h.changeReason()),
                nullToEmpty(h.changeDetailReason()),
                nullToEmpty(h.orderStatus()),
                "",
                "REFER_DETAIL",
                formatDate(h.createdAt()));
    }

    private List<OrderHistoryInfo> toOrderHistoryInfos(List<LegacyOrderHistoryResult> histories) {
        if (histories == null || histories.isEmpty()) {
            return List.of();
        }
        return histories.stream()
                .map(
                        h ->
                                new OrderHistoryInfo(
                                        h.orderId(),
                                        nullToEmpty(h.changeReason()),
                                        nullToEmpty(h.changeDetailReason()),
                                        nullToEmpty(h.orderStatus()),
                                        "",
                                        "REFER_DETAIL",
                                        formatDate(h.createdAt())))
                .toList();
    }

    private List<OptionInfo> buildOptionInfos(List<String> optionValues) {
        if (optionValues == null || optionValues.isEmpty()) {
            return List.of();
        }
        return optionValues.stream().map(value -> new OptionInfo(0, 0, "", value)).toList();
    }

    private String formatDate(Instant instant) {
        if (instant == null) {
            return "";
        }
        return SETOF_DATE_FORMAT.format(instant);
    }

    /** null이면 null 반환 (setof 호환 — canceledDate, shipmentCompletedDate 등). */
    private String formatDateNullable(Instant instant) {
        if (instant == null) {
            return null;
        }
        return SETOF_DATE_FORMAT.format(instant);
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }
}
