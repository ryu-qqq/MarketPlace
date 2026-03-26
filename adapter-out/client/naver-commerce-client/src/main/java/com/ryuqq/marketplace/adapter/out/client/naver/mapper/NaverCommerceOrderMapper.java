package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderDetail;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverProductOrderOrder;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.order.NaverShippingAddress;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderItemPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * 네이버 커머스 주문 매퍼.
 *
 * <p>NaverProductOrderDetail → ExternalOrderPayload 변환. orderId 기준으로 그룹핑하여 주문 단위로 변환합니다.
 */
@Component
public class NaverCommerceOrderMapper {

    /**
     * 상품주문 상세 목록을 외부 주문 페이로드 목록으로 변환합니다.
     *
     * <p>동일 orderId를 가진 상품주문을 하나의 ExternalOrderPayload로 그룹핑합니다.
     *
     * @param details 네이버 상품주문 상세 목록
     * @return 주문 단위로 그룹핑된 외부 주문 페이로드 목록
     */
    public List<ExternalOrderPayload> toExternalOrderPayloads(
            List<NaverProductOrderDetail> details) {
        // 배송지 미입력 주문 제외 (선물하기 수락 대기 등)
        List<NaverProductOrderDetail> filtered =
                details.stream().filter(d -> d.productOrder().shippingAddress() != null).toList();

        return filtered.stream()
                .collect(Collectors.groupingBy(d -> d.order().orderId()))
                .entrySet()
                .stream()
                .map(entry -> toExternalOrderPayload(entry.getKey(), entry.getValue()))
                .toList();
    }

    private ExternalOrderPayload toExternalOrderPayload(
            String orderId, List<NaverProductOrderDetail> orderDetails) {
        NaverProductOrderOrder order = orderDetails.getFirst().order();

        List<ExternalOrderItemPayload> items =
                orderDetails.stream().map(this::toItemPayload).toList();

        int totalPayment = items.stream().mapToInt(ExternalOrderItemPayload::paymentAmount).sum();

        return new ExternalOrderPayload(
                orderId,
                parseInstant(order.orderDate()),
                order.ordererName(),
                null,
                order.ordererTel(),
                normalizePaymentMethod(order.paymentMeans()),
                totalPayment,
                parseInstant(order.paymentDate()),
                items);
    }

    private ExternalOrderItemPayload toItemPayload(NaverProductOrderDetail detail) {
        NaverProductOrderDetail.ProductOrderInfo po = detail.productOrder();
        NaverShippingAddress addr = po.shippingAddress();

        return new ExternalOrderItemPayload(
                po.productOrderId(),
                po.originalProductId() != null ? po.originalProductId() : po.productId(),
                po.optionManageCode(),
                po.productName(),
                po.productOption(),
                null,
                po.unitPrice(),
                po.quantity(),
                po.totalProductAmount(),
                po.productDiscountAmount(),
                po.sellerBurdenDiscountAmount() != null ? po.sellerBurdenDiscountAmount() : 0,
                po.totalPaymentAmount(),
                addr != null ? addr.name() : null,
                addr != null ? addr.tel1() : null,
                addr != null ? addr.zipCode() : null,
                addr != null ? addr.baseAddress() : null,
                addr != null ? addr.detailedAddress() : null,
                po.shippingMemo(),
                po.productOrderStatus());
    }

    private static final Map<String, String> PAYMENT_METHOD_MAP =
            Map.ofEntries(
                    Map.entry("신용카드", "CARD"),
                    Map.entry("신용카드 간편결제", "CARD_EASY"),
                    Map.entry("계좌이체", "BANK_TRANSFER"),
                    Map.entry("계좌 간편결제", "BANK_EASY"),
                    Map.entry("무통장입금", "VIRTUAL_ACCOUNT"),
                    Map.entry("포인트/머니결제", "POINT"),
                    Map.entry("네이버페이", "NAVER_PAY"),
                    Map.entry("후불결제", "DEFERRED"),
                    Map.entry("휴대폰결제", "PHONE"));

    private String normalizePaymentMethod(String paymentMeans) {
        if (paymentMeans == null || paymentMeans.isBlank()) {
            return "CARD";
        }
        return PAYMENT_METHOD_MAP.getOrDefault(paymentMeans, paymentMeans);
    }

    private Instant parseInstant(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        return OffsetDateTime.parse(dateStr).toInstant();
    }
}
