package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderHistoryQueryDto;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 API Mapper.
 *
 * <p>flat QueryDto + optionValues → LegacyOrderDetailResult 변환, LegacyOrderHistoryQueryDto →
 * LegacyOrderHistoryResult 변환.
 */
@Component
public class LegacyOrderApiMapper {

    public LegacyOrderDetailResult toDetailResult(
            LegacyOrderCompositeQueryDto dto, List<String> optionValues) {
        Instant orderDate =
                dto.orderDate() != null ? dto.orderDate().toInstant(ZoneOffset.UTC) : null;

        return new LegacyOrderDetailResult(
                dto.legacyOrderId() != null ? dto.legacyOrderId() : 0L,
                dto.legacyPaymentId() != null ? dto.legacyPaymentId() : 0L,
                dto.legacyProductId() != null ? dto.legacyProductId() : 0L,
                dto.legacySellerId() != null ? dto.legacySellerId() : 0L,
                dto.legacyUserId() != null ? dto.legacyUserId() : 0L,
                dto.orderAmount() != null ? dto.orderAmount() : 0L,
                dto.orderStatus(),
                dto.quantity() != null ? dto.quantity() : 0,
                orderDate,
                dto.productGroupId() != null ? dto.productGroupId() : 0L,
                dto.productGroupName(),
                dto.brandId() != null ? dto.brandId() : 0L,
                dto.brandName(),
                dto.categoryId() != null ? dto.categoryId() : 0L,
                dto.regularPrice() != null ? dto.regularPrice() : 0L,
                dto.currentPrice() != null ? dto.currentPrice() : 0L,
                dto.commissionRate() != null ? dto.commissionRate() : 0L,
                dto.shareRatio() != null ? dto.shareRatio() : 0L,
                List.copyOf(optionValues),
                dto.mainImageUrl(),
                dto.receiverName(),
                dto.receiverPhone(),
                dto.receiverZipCode(),
                dto.receiverAddress(),
                dto.receiverAddressDetail(),
                dto.deliveryRequest());
    }

    public LegacyOrderHistoryResult toHistoryResult(LegacyOrderHistoryQueryDto dto) {
        Instant createdAt =
                dto.insertDate() != null ? dto.insertDate().toInstant(ZoneOffset.UTC) : null;

        return new LegacyOrderHistoryResult(
                dto.orderHistoryId() != null ? dto.orderHistoryId() : 0L,
                dto.orderId() != null ? dto.orderId() : 0L,
                dto.orderStatus(),
                dto.changeReason(),
                dto.changeDetailReason(),
                createdAt);
    }
}
