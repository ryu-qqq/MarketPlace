package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderHistoryQueryDto;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderHistoryEntry;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 복합 조회 Mapper.
 *
 * <p>flat QueryDto + optionValues + histories → {@link LegacyOrderCompositeResult} 변환. nullable 필드에
 * 대한 null-safe 처리를 담당합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyOrderCompositeMapper {

    /**
     * flat DTO, 옵션값 목록, 이력 목록을 Application Result로 변환합니다.
     *
     * @param dto flat projection DTO
     * @param optionValues 옵션값 목록 (별도 쿼리 결과)
     * @param histories 주문 이력 목록 (별도 쿼리 결과)
     * @return LegacyOrderCompositeResult
     */
    public LegacyOrderCompositeResult toResult(
            LegacyOrderCompositeQueryDto dto,
            List<String> optionValues,
            List<LegacyOrderHistoryQueryDto> histories) {
        Instant orderDate =
                dto.orderDate() != null ? dto.orderDate().toInstant(ZoneOffset.UTC) : null;
        Instant shipmentCreatedAt =
                dto.shipmentCreatedAt() != null
                        ? dto.shipmentCreatedAt().toInstant(ZoneOffset.UTC)
                        : null;

        List<LegacyOrderHistoryEntry> historyEntries =
                histories.stream()
                        .map(
                                h ->
                                        new LegacyOrderHistoryEntry(
                                                h.orderStatus(),
                                                h.changeReason(),
                                                h.changeDetailReason(),
                                                h.insertDate() != null
                                                        ? h.insertDate().toInstant(ZoneOffset.UTC)
                                                        : null))
                        .toList();

        return new LegacyOrderCompositeResult(
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
                dto.externalOrderPkId(),
                dto.externalSiteId(),
                dto.interlockingSiteName(),
                dto.receiverName(),
                dto.receiverPhone(),
                dto.receiverZipCode(),
                dto.receiverAddress(),
                dto.receiverAddressDetail(),
                dto.deliveryRequest(),
                dto.invoiceNo(),
                dto.companyCode(),
                shipmentCreatedAt,
                historyEntries);
    }
}
