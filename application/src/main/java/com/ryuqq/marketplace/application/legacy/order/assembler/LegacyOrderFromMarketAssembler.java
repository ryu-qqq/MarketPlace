package com.ryuqq.marketplace.application.legacy.order.assembler;

import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailWithHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderCancelResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderClaimResult;
import com.ryuqq.marketplace.application.order.dto.response.OrderHistoryResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderDetailResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.CancelSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ClaimSummary;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.OrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ProductOrderInfo;
import com.ryuqq.marketplace.application.order.dto.response.ProductOrderListResult.ReceiverInfo;
import com.ryuqq.marketplace.application.legacyconversion.manager.LegacySellerIdMappingReadManager;
import com.ryuqq.marketplace.domain.cancel.vo.CancelStatus;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderIdMapping;
import com.ryuqq.marketplace.domain.refund.vo.RefundStatus;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Market 스키마 조회 결과 -> 레거시 주문 응답 Assembler.
 *
 * <p>표준 ProductOrderDetailResult를 LegacyOrderDetailResult로 변환합니다. 핵심은 market 상태(OrderItemStatus +
 * CancelStatus/RefundStatus + ShipmentStatus)를 레거시 ORDER_STATUS 문자열로 역매핑하는 것입니다.
 */
@Component
public class LegacyOrderFromMarketAssembler {

    private final LegacySellerIdMappingReadManager sellerIdMappingReadManager;

    public LegacyOrderFromMarketAssembler(
            LegacySellerIdMappingReadManager sellerIdMappingReadManager) {
        this.sellerIdMappingReadManager = sellerIdMappingReadManager;
    }

    /**
     * 표준 주문 상세 -> 레거시 주문 상세 + 히스토리 변환.
     *
     * @param detail 표준 주문 상세 결과
     * @param mapping 레거시-market ID 매핑
     * @param shipmentStatus 배송 상태 (없으면 null)
     * @return 레거시 주문 상세 + 히스토리
     */
    public LegacyOrderDetailWithHistoryResult toDetailWithHistory(
            ProductOrderDetailResult detail,
            LegacyOrderIdMapping mapping,
            ShipmentStatus shipmentStatus) {

        LegacyOrderDetailResult order = toDetailResult(detail, mapping, shipmentStatus);
        List<LegacyOrderHistoryResult> histories =
                toHistoryResults(detail.timeLine(), mapping.legacyOrderId());

        return new LegacyOrderDetailWithHistoryResult(order, histories);
    }

    /**
     * 표준 주문 상세 -> 레거시 주문 상세 변환.
     *
     * @param detail 표준 주문 상세 결과
     * @param mapping 레거시-market ID 매핑
     * @param shipmentStatus 배송 상태 (없으면 null)
     * @return 레거시 주문 상세
     */
    public LegacyOrderDetailResult toDetailResult(
            ProductOrderDetailResult detail,
            LegacyOrderIdMapping mapping,
            ShipmentStatus shipmentStatus) {

        OrderInfo order = detail.order();
        ProductOrderInfo product = detail.productOrder();
        ReceiverInfo receiver = detail.receiver();

        String legacyStatus = resolveOrderStatus(detail, shipmentStatus);

        return new LegacyOrderDetailResult(
                mapping.legacyOrderId(),
                mapping.legacyPaymentId(),
                parseLongSafe(product.externalProductId(), product.productId()),
                resolveLegacySellerId(product.sellerId(), order.shopId()),
                0L,
                product.paymentAmount(),
                legacyStatus,
                product.quantity(),
                order.createdAt(),
                product.productGroupId(),
                safe(product.productGroupName()),
                product.brandId() != null ? product.brandId() : 0L,
                safe(product.brandName()),
                product.categoryId() != null ? product.categoryId() : 0L,
                product.regularPrice(),
                product.unitPrice(),
                12L,
                100L,
                resolveOptionValues(product),
                safe(product.mainImageUrl()),
                safe(receiver.receiverName()),
                safe(receiver.receiverPhone()),
                safe(receiver.receiverZipcode()),
                safe(receiver.receiverAddress()),
                safe(receiver.receiverAddressDetail()),
                safe(receiver.deliveryRequest()));
    }

    /**
     * 표준 주문 목록 항목 -> 레거시 주문 상세 변환 (목록 조회용).
     *
     * @param item 표준 주문 목록 항목
     * @param mapping 레거시-market ID 매핑
     * @param shipmentStatus 배송 상태 (없으면 null)
     * @return 레거시 주문 상세
     */
    public LegacyOrderDetailResult toDetailResultFromListItem(
            ProductOrderListResult item,
            LegacyOrderIdMapping mapping,
            ShipmentStatus shipmentStatus) {

        OrderInfo order = item.order();
        ProductOrderInfo product = item.productOrder();
        ReceiverInfo receiver = item.receiver();

        String legacyStatus = resolveOrderStatusFromList(item, shipmentStatus);

        return new LegacyOrderDetailResult(
                mapping.legacyOrderId(),
                mapping.legacyPaymentId(),
                parseLongSafe(product.externalProductId(), product.productId()),
                resolveLegacySellerId(product.sellerId(), order.shopId()),
                0L,
                product.paymentAmount(),
                legacyStatus,
                product.quantity(),
                order.createdAt(),
                product.productGroupId(),
                safe(product.productGroupName()),
                product.brandId() != null ? product.brandId() : 0L,
                safe(product.brandName()),
                product.categoryId() != null ? product.categoryId() : 0L,
                product.regularPrice(),
                product.unitPrice(),
                12L,
                100L,
                resolveOptionValues(product),
                safe(product.mainImageUrl()),
                safe(receiver.receiverName()),
                safe(receiver.receiverPhone()),
                safe(receiver.receiverZipcode()),
                safe(receiver.receiverAddress()),
                safe(receiver.receiverAddressDetail()),
                safe(receiver.deliveryRequest()));
    }

    /** ProductOrderListResult에서 상태 역매핑 (CancelSummary/ClaimSummary + delivery 기반). */
    private String resolveOrderStatusFromList(
            ProductOrderListResult item, ShipmentStatus shipmentStatus) {
        String orderItemStatus = item.delivery().orderItemStatus();

        String cancelStatus = resolveFromActiveCancel(item.cancel(), List.of());
        if (cancelStatus != null) {
            return cancelStatus;
        }

        String claimStatus = resolveFromActiveClaim(item.claim(), List.of());
        if (claimStatus != null) {
            return claimStatus;
        }

        return switch (orderItemStatus) {
            case "CANCELLED" -> "SALE_CANCELLED_COMPLETED";
            case "RETURNED" -> "RETURN_REQUEST_COMPLETED";
            case "RETURN_REQUESTED" -> "RETURN_REQUEST";
            case "READY" -> "ORDER_PROCESSING";
            case "CONFIRMED" -> resolveFromShipment(shipmentStatus);
            default -> "ORDER_PROCESSING";
        };
    }

    // ==================== 역방향 상태 매핑 ====================

    /**
     * Market 상태 -> 레거시 ORDER_STATUS 역매핑.
     *
     * <p>우선순위: 1) 활성 취소 2) 활성 클레임(반품) 3) 완료 취소/반품 4) 배송 상태 기반
     */
    String resolveOrderStatus(ProductOrderDetailResult detail, ShipmentStatus shipmentStatus) {
        String orderItemStatus = detail.delivery().orderItemStatus();

        // 1. 활성 취소 확인
        String cancelStatus = resolveFromActiveCancel(detail.cancel(), detail.cancels());
        if (cancelStatus != null) {
            return cancelStatus;
        }

        // 2. 활성 클레임(반품) 확인
        String claimStatus = resolveFromActiveClaim(detail.claim(), detail.claims());
        if (claimStatus != null) {
            return claimStatus;
        }

        // 3. 종료 상태 기반 매핑 (OrderItemStatus)
        return switch (orderItemStatus) {
            case "CANCELLED" -> "SALE_CANCELLED_COMPLETED";
            case "RETURNED" -> "RETURN_REQUEST_COMPLETED";
            case "RETURN_REQUESTED" -> "RETURN_REQUEST";
            case "READY" -> "ORDER_PROCESSING";
            case "CONFIRMED" -> resolveFromShipment(shipmentStatus);
            default -> "ORDER_PROCESSING";
        };
    }

    /**
     * 활성 취소 -> 레거시 상태.
     *
     * @return 매핑된 레거시 상태. 활성 취소 없으면 null
     */
    private String resolveFromActiveCancel(CancelSummary summary, List<OrderCancelResult> cancels) {
        if (!summary.hasActiveCancel() || summary.latest() == null) {
            // 활성 취소가 없어도 완료된 취소가 있으면 반영
            if (summary.totalCancelledQty() > 0) {
                return "SALE_CANCELLED_COMPLETED";
            }
            return null;
        }

        return switch (parseCancelStatus(summary.latest().status())) {
            case REQUESTED -> "SALE_CANCELLED";
            case APPROVED -> "CANCEL_REQUEST_CONFIRMED";
            case COMPLETED -> "SALE_CANCELLED_COMPLETED";
            case REJECTED, CANCELLED -> null;
        };
    }

    /**
     * 활성 클레임(반품) -> 레거시 상태.
     *
     * @return 매핑된 레거시 상태. 활성 클레임 없으면 null
     */
    private String resolveFromActiveClaim(ClaimSummary summary, List<OrderClaimResult> claims) {
        if (!summary.hasActiveClaim() || summary.latest() == null) {
            return null;
        }

        // REFUND 타입만 처리 (레거시에는 EXCHANGE 없음)
        if (!"REFUND".equals(summary.latest().type())) {
            return null;
        }

        return switch (parseRefundStatus(summary.latest().status())) {
            case REQUESTED -> "RETURN_REQUEST";
            case COLLECTING, COLLECTED -> "RETURN_REQUEST_CONFIRMED";
            case COMPLETED -> "RETURN_REQUEST_COMPLETED";
            case REJECTED -> "RETURN_REQUEST_REJECTED";
            case CANCELLED -> null;
        };
    }

    /** ShipmentStatus -> 레거시 배송 상태. */
    private String resolveFromShipment(ShipmentStatus shipmentStatus) {
        if (shipmentStatus == null) {
            return "DELIVERY_PENDING";
        }
        return switch (shipmentStatus) {
            case READY, PREPARING -> "DELIVERY_PENDING";
            case SHIPPED, IN_TRANSIT -> "DELIVERY_PROCESSING";
            case DELIVERED -> "DELIVERY_COMPLETED";
            case FAILED -> "DELIVERY_PENDING";
            case CANCELLED -> "SALE_CANCELLED_COMPLETED";
        };
    }

    // ==================== 통합 타임라인 ====================

    /**
     * 주문 상세의 모든 이벤트를 통합 타임라인으로 변환.
     *
     * <p>OrderHistory + Cancel + Refund + Shipment 이벤트를 시간순으로 병합합니다.
     */
    public List<LegacyOrderHistoryResult> toUnifiedTimeline(
            ProductOrderDetailResult detail,
            long legacyOrderId,
            Shipment shipment) {

        List<LegacyOrderHistoryResult> timeline = new ArrayList<>();

        // 1. 주문 상태 변경 이력
        if (detail.timeLine() != null) {
            detail.timeLine().forEach(h -> timeline.add(new LegacyOrderHistoryResult(
                    h.historyId(), legacyOrderId,
                    safe(h.toStatus()), safe(h.changedBy()), safe(h.reason()),
                    h.changedAt())));
        }

        // 2. 취소 이력
        if (detail.cancels() != null) {
            for (OrderCancelResult c : detail.cancels()) {
                if (c.requestedAt() != null) {
                    timeline.add(new LegacyOrderHistoryResult(
                            0L, legacyOrderId, "SALE_CANCELLED",
                            safe(c.reasonType()), safe(c.reasonDetail()),
                            c.requestedAt()));
                }
                if (c.completedAt() != null) {
                    timeline.add(new LegacyOrderHistoryResult(
                            0L, legacyOrderId, "SALE_CANCELLED_COMPLETED",
                            safe(c.reasonType()), safe(c.reasonDetail()),
                            c.completedAt()));
                }
            }
        }

        // 3. 반품 이력
        if (detail.claims() != null) {
            for (OrderClaimResult r : detail.claims()) {
                if (r.requestedAt() != null) {
                    timeline.add(new LegacyOrderHistoryResult(
                            0L, legacyOrderId, "RETURN_REQUEST",
                            safe(r.reasonType()), safe(r.reasonDetail()),
                            r.requestedAt()));
                }
                if (r.completedAt() != null) {
                    timeline.add(new LegacyOrderHistoryResult(
                            0L, legacyOrderId, "RETURN_REQUEST_COMPLETED",
                            safe(r.reasonType()), safe(r.reasonDetail()),
                            r.completedAt()));
                }
                if (r.rejectedAt() != null) {
                    timeline.add(new LegacyOrderHistoryResult(
                            0L, legacyOrderId, "RETURN_REQUEST_REJECTED",
                            safe(r.reasonType()), safe(r.reasonDetail()),
                            r.rejectedAt()));
                }
            }
        }

        // 4. 배송 이력
        if (shipment != null) {
            if (shipment.shippedAt() != null) {
                String invoiceInfo = shipment.trackingNumber() != null
                        ? "송장: " + shipment.trackingNumber() : "";
                timeline.add(new LegacyOrderHistoryResult(
                        0L, legacyOrderId, "DELIVERY_PROCESSING",
                        invoiceInfo, "", shipment.shippedAt()));
            }
            if (shipment.deliveredAt() != null) {
                timeline.add(new LegacyOrderHistoryResult(
                        0L, legacyOrderId, "DELIVERY_COMPLETED",
                        "", "", shipment.deliveredAt()));
            }
        }

        // 시간순 정렬
        timeline.sort(Comparator.comparing(
                LegacyOrderHistoryResult::createdAt,
                Comparator.nullsLast(Comparator.naturalOrder())));

        return timeline;
    }

    // ==================== 히스토리 변환 ====================

    private List<LegacyOrderHistoryResult> toHistoryResults(
            List<OrderHistoryResult> timeLine, long legacyOrderId) {
        if (timeLine == null || timeLine.isEmpty()) {
            return List.of();
        }
        return timeLine.stream().map(h -> toLegacyHistory(h, legacyOrderId)).toList();
    }

    private LegacyOrderHistoryResult toLegacyHistory(
            OrderHistoryResult history, long legacyOrderId) {
        return new LegacyOrderHistoryResult(
                history.historyId(),
                legacyOrderId,
                safe(history.toStatus()),
                safe(history.changedBy()),
                safe(history.reason()),
                history.changedAt());
    }

    // ==================== 유틸리티 ====================

    private List<String> resolveOptionValues(ProductOrderInfo product) {
        String optionName = product.externalOptionName();
        if (optionName == null || optionName.isBlank()) {
            return List.of();
        }
        return List.of(optionName);
    }

    private CancelStatus parseCancelStatus(String status) {
        try {
            return CancelStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return CancelStatus.REQUESTED;
        }
    }

    private RefundStatus parseRefundStatus(String status) {
        try {
            return RefundStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            return RefundStatus.REQUESTED;
        }
    }

    private long resolveLegacySellerId(Long internalSellerId, long fallback) {
        if (internalSellerId == null) {
            return fallback;
        }
        return sellerIdMappingReadManager
                .findLegacySellerIdByInternalSellerId(internalSellerId)
                .orElse(internalSellerId);
    }

    private long parseLongSafe(String value, long fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private String safe(String value) {
        return value != null ? value : "";
    }
}
