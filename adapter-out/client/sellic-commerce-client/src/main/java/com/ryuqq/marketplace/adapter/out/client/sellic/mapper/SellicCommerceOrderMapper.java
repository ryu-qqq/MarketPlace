package com.ryuqq.marketplace.adapter.out.client.sellic.mapper;

import com.ryuqq.marketplace.adapter.out.client.sellic.dto.SellicOrderQueryResponse.SellicOrderData;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 셀릭 주문 응답 → ExternalOrderPayload 변환 매퍼.
 *
 * <p>셀릭 API는 주문 아이템 단위로 응답합니다 (1주문 N아이템 → N행). ORDER_ID(쇼핑몰주문번호) 기준으로 그룹핑하여 ExternalOrderPayload로
 * 변환합니다.
 */
@Component
@ConditionalOnProperty(prefix = "sellic-commerce", name = "customer-id")
public class SellicCommerceOrderMapper {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter SELLIC_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 셀릭 주문 데이터 목록 → ExternalOrderPayload 목록으로 변환.
     *
     * <p>ORDER_ID(쇼핑몰주문번호) 기준으로 그룹핑합니다.
     */
    public List<ExternalOrderPayload> toExternalOrderPayloads(List<SellicOrderData> datas) {
        if (datas == null || datas.isEmpty()) {
            return List.of();
        }

        // ORDER_ID 기준으로 그룹핑 (순서 보존)
        Map<String, List<SellicOrderData>> groupedByOrderId = new LinkedHashMap<>();
        for (SellicOrderData data : datas) {
            String key = resolveOrderKey(data);
            groupedByOrderId.computeIfAbsent(key, k -> new ArrayList<>()).add(data);
        }

        return groupedByOrderId.entrySet().stream()
                .map(entry -> toOrderPayload(entry.getKey(), entry.getValue()))
                .toList();
    }

    private ExternalOrderPayload toOrderPayload(String orderKey, List<SellicOrderData> items) {
        SellicOrderData first = items.get(0);

        Instant orderedAt = parseDateTime(first.orderDate());
        Instant createdAt = parseDateTime(first.createdAt());

        int totalPayment =
                items.stream().mapToInt(d -> d.paymentPrice() != null ? d.paymentPrice() : 0).sum();

        List<ExternalOrderItemPayload> itemPayloads =
                items.stream().map(this::toItemPayload).toList();

        return new ExternalOrderPayload(
                orderKey,
                orderedAt != null ? orderedAt : createdAt,
                first.userName(),
                null,
                first.userCel() != null ? first.userCel() : first.userTel(),
                null,
                totalPayment,
                orderedAt != null ? orderedAt : createdAt,
                itemPayloads);
    }

    private ExternalOrderItemPayload toItemPayload(SellicOrderData data) {
        int unitPrice = data.saleCost() != null ? data.saleCost() : 0;
        int quantity = data.saleCnt() != null ? data.saleCnt() : 1;
        int totalAmount = data.totalPrice() != null ? data.totalPrice() : unitPrice * quantity;
        int paymentAmount = data.paymentPrice() != null ? data.paymentPrice() : totalAmount;
        int discountAmount = totalAmount - paymentAmount;

        return new ExternalOrderItemPayload(
                String.valueOf(data.idx()),
                data.ownCode() != null ? data.ownCode() : String.valueOf(data.productId()),
                data.optionCode() != null ? String.valueOf(data.optionCode()) : null,
                data.productName(),
                data.optionName(),
                null,
                unitPrice,
                quantity,
                totalAmount,
                Math.max(discountAmount, 0),
                0,
                paymentAmount,
                data.receiveName(),
                data.receiveCel() != null ? data.receiveCel() : data.receiveTel(),
                data.receiveZipcode(),
                data.receiveAddr(),
                null,
                data.deliveryMessage(),
                null);
    }

    private String resolveOrderKey(SellicOrderData data) {
        // 쇼핑몰 주문번호가 있으면 사용, 없으면 셀릭 주문번호(IDX) 사용
        if (data.orderId() != null && !data.orderId().isBlank()) {
            return data.orderId();
        }
        return String.valueOf(data.idx());
    }

    private Instant parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isBlank()) {
            return null;
        }
        try {
            // ISO 8601 형식 (실제 API 응답: 2026-03-19T11:50:49.000+09:00)
            if (dateTimeStr.contains("T")) {
                return java.time.OffsetDateTime.parse(dateTimeStr).toInstant();
            }
            // 문서 형식 (YYYY-MM-DD HH:mm:ss)
            LocalDateTime ldt = LocalDateTime.parse(dateTimeStr, SELLIC_DATE_FORMAT);
            return ldt.atZone(KST).toInstant();
        } catch (Exception e) {
            return null;
        }
    }
}
