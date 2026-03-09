package com.ryuqq.marketplace.domain.inboundorder.aggregate;

import com.ryuqq.marketplace.domain.inboundorder.id.InboundOrderId;
import com.ryuqq.marketplace.domain.inboundorder.vo.InboundOrderStatus;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * InboundOrder Aggregate Root.
 *
 * <p>외부 판매채널에서 수신한 주문 원본을 관리합니다. 상품 매핑 → 내부 Order 변환의 상태 머신을 제공합니다.
 */
public class InboundOrder {

    private final InboundOrderId id;
    private final long salesChannelId;
    private final long shopId;
    private long sellerId;
    private final String externalOrderNo;
    private final Instant externalOrderedAt;

    private final String buyerName;
    private final String buyerEmail;
    private final String buyerPhone;

    private final String paymentMethod;
    private final int totalPaymentAmount;
    private final Instant paidAt;

    private InboundOrderStatus status;
    private String internalOrderId;
    private String failureReason;

    private final List<InboundOrderItem> items;

    private final Instant createdAt;
    private Instant updatedAt;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundOrder(
            InboundOrderId id,
            long salesChannelId,
            long shopId,
            long sellerId,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String paymentMethod,
            int totalPaymentAmount,
            Instant paidAt,
            InboundOrderStatus status,
            String internalOrderId,
            String failureReason,
            List<InboundOrderItem> items,
            Instant createdAt,
            Instant updatedAt) {
        this.id = id;
        this.salesChannelId = salesChannelId;
        this.shopId = shopId;
        this.sellerId = sellerId;
        this.externalOrderNo = externalOrderNo;
        this.externalOrderedAt = externalOrderedAt;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerPhone = buyerPhone;
        this.paymentMethod = paymentMethod;
        this.totalPaymentAmount = totalPaymentAmount;
        this.paidAt = paidAt;
        this.status = status;
        this.internalOrderId = internalOrderId;
        this.failureReason = failureReason;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundOrder forNew(
            long salesChannelId,
            long shopId,
            long sellerId,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String paymentMethod,
            int totalPaymentAmount,
            Instant paidAt,
            List<InboundOrderItem> items,
            Instant now) {
        return new InboundOrder(
                InboundOrderId.forNew(),
                salesChannelId,
                shopId,
                sellerId,
                externalOrderNo,
                externalOrderedAt,
                buyerName,
                buyerEmail,
                buyerPhone,
                paymentMethod,
                totalPaymentAmount,
                paidAt,
                InboundOrderStatus.RECEIVED,
                null,
                null,
                items,
                now,
                now);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundOrder reconstitute(
            InboundOrderId id,
            long salesChannelId,
            long shopId,
            long sellerId,
            String externalOrderNo,
            Instant externalOrderedAt,
            String buyerName,
            String buyerEmail,
            String buyerPhone,
            String paymentMethod,
            int totalPaymentAmount,
            Instant paidAt,
            InboundOrderStatus status,
            String internalOrderId,
            String failureReason,
            List<InboundOrderItem> items,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundOrder(
                id,
                salesChannelId,
                shopId,
                sellerId,
                externalOrderNo,
                externalOrderedAt,
                buyerName,
                buyerEmail,
                buyerPhone,
                paymentMethod,
                totalPaymentAmount,
                paidAt,
                status,
                internalOrderId,
                failureReason,
                items,
                createdAt,
                updatedAt);
    }

    public void markPendingMapping(Instant now) {
        if (!status.canApplyMapping()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 PENDING_MAPPING으로 전이할 수 없습니다");
        }
        this.status = InboundOrderStatus.PENDING_MAPPING;
        this.updatedAt = now;
    }

    public void applyMapping(Instant now) {
        if (!status.canApplyMapping()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 MAPPED로 전이할 수 없습니다");
        }
        this.status = InboundOrderStatus.MAPPED;
        this.updatedAt = now;
    }

    public void assignSellerId(long sellerId) {
        this.sellerId = sellerId;
    }

    public void markConverted(String orderId, Instant now) {
        if (!status.canConvert()) {
            throw new IllegalStateException("현재 상태(" + status + ")에서 CONVERTED로 전이할 수 없습니다");
        }
        this.internalOrderId = orderId;
        this.status = InboundOrderStatus.CONVERTED;
        this.updatedAt = now;
    }

    public void markFailed(String reason, Instant now) {
        this.failureReason = reason;
        this.status = InboundOrderStatus.FAILED;
        this.updatedAt = now;
    }

    public Long idValue() {
        return id != null ? id.value() : null;
    }

    public InboundOrderId id() {
        return id;
    }

    public long salesChannelId() {
        return salesChannelId;
    }

    public long shopId() {
        return shopId;
    }

    public long sellerId() {
        return sellerId;
    }

    public String externalOrderNo() {
        return externalOrderNo;
    }

    public Instant externalOrderedAt() {
        return externalOrderedAt;
    }

    public String buyerName() {
        return buyerName;
    }

    public String buyerEmail() {
        return buyerEmail;
    }

    public String buyerPhone() {
        return buyerPhone;
    }

    public String paymentMethod() {
        return paymentMethod;
    }

    public int totalPaymentAmount() {
        return totalPaymentAmount;
    }

    public Instant paidAt() {
        return paidAt;
    }

    public InboundOrderStatus status() {
        return status;
    }

    public String internalOrderId() {
        return internalOrderId;
    }

    public String failureReason() {
        return failureReason;
    }

    public List<InboundOrderItem> items() {
        return Collections.unmodifiableList(items);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Instant updatedAt() {
        return updatedAt;
    }
}
