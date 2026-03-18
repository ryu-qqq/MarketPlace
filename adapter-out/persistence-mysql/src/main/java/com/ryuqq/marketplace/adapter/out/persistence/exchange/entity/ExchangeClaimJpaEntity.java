package com.ryuqq.marketplace.adapter.out.persistence.exchange.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** 교환 클레임 JPA 엔티티. */
@Entity
@Table(name = "exchange_claims")
public class ExchangeClaimJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "claim_number", nullable = false, length = 50)
    private String claimNumber;

    @Column(name = "order_item_id", nullable = false, length = 36)
    private String orderItemId;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "exchange_qty", nullable = false)
    private int exchangeQty;

    @Column(name = "exchange_status", nullable = false, length = 20)
    private String exchangeStatus;

    @Column(name = "reason_type", nullable = false, length = 50)
    private String reasonType;

    @Column(name = "reason_detail", length = 500)
    private String reasonDetail;

    @Column(name = "original_product_id")
    private Long originalProductId;

    @Column(name = "original_sku_code", length = 50)
    private String originalSkuCode;

    @Column(name = "target_product_group_id")
    private Long targetProductGroupId;

    @Column(name = "target_product_id")
    private Long targetProductId;

    @Column(name = "target_sku_code", length = 50)
    private String targetSkuCode;

    @Column(name = "target_quantity")
    private Integer targetQuantity;

    @Column(name = "original_price")
    private Integer originalPrice;

    @Column(name = "target_price")
    private Integer targetPrice;

    @Column(name = "price_difference")
    private Integer priceDifference;

    @Column(name = "additional_payment_required")
    private boolean additionalPaymentRequired;

    @Column(name = "partial_refund_required")
    private boolean partialRefundRequired;

    @Column(name = "collect_shipping_fee")
    private Integer collectShippingFee;

    @Column(name = "reship_shipping_fee")
    private Integer reshipShippingFee;

    @Column(name = "total_shipping_fee")
    private Integer totalShippingFee;

    @Column(name = "shipping_fee_payer", length = 10)
    private String shippingFeePayer;

    @Column(name = "claim_shipment_id", length = 36)
    private String claimShipmentId;

    @Column(name = "linked_order_id", length = 36)
    private String linkedOrderId;

    @Column(name = "requested_by", nullable = false, length = 100)
    private String requestedBy;

    @Column(name = "processed_by", length = 100)
    private String processedBy;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

    protected ExchangeClaimJpaEntity() {
        super();
    }

    private ExchangeClaimJpaEntity(
            String id,
            String claimNumber,
            String orderItemId,
            long sellerId,
            int exchangeQty,
            String exchangeStatus,
            String reasonType,
            String reasonDetail,
            Long originalProductId,
            String originalSkuCode,
            Long targetProductGroupId,
            Long targetProductId,
            String targetSkuCode,
            Integer targetQuantity,
            Integer originalPrice,
            Integer targetPrice,
            Integer priceDifference,
            boolean additionalPaymentRequired,
            boolean partialRefundRequired,
            Integer collectShippingFee,
            Integer reshipShippingFee,
            Integer totalShippingFee,
            String shippingFeePayer,
            String claimShipmentId,
            String linkedOrderId,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.claimNumber = claimNumber;
        this.orderItemId = orderItemId;
        this.sellerId = sellerId;
        this.exchangeQty = exchangeQty;
        this.exchangeStatus = exchangeStatus;
        this.reasonType = reasonType;
        this.reasonDetail = reasonDetail;
        this.originalProductId = originalProductId;
        this.originalSkuCode = originalSkuCode;
        this.targetProductGroupId = targetProductGroupId;
        this.targetProductId = targetProductId;
        this.targetSkuCode = targetSkuCode;
        this.targetQuantity = targetQuantity;
        this.originalPrice = originalPrice;
        this.targetPrice = targetPrice;
        this.priceDifference = priceDifference;
        this.additionalPaymentRequired = additionalPaymentRequired;
        this.partialRefundRequired = partialRefundRequired;
        this.collectShippingFee = collectShippingFee;
        this.reshipShippingFee = reshipShippingFee;
        this.totalShippingFee = totalShippingFee;
        this.shippingFeePayer = shippingFeePayer;
        this.claimShipmentId = claimShipmentId;
        this.linkedOrderId = linkedOrderId;
        this.requestedBy = requestedBy;
        this.processedBy = processedBy;
        this.requestedAt = requestedAt;
        this.processedAt = processedAt;
        this.completedAt = completedAt;
    }

    public static ExchangeClaimJpaEntity create(
            String id,
            String claimNumber,
            String orderItemId,
            long sellerId,
            int exchangeQty,
            String exchangeStatus,
            String reasonType,
            String reasonDetail,
            Long originalProductId,
            String originalSkuCode,
            Long targetProductGroupId,
            Long targetProductId,
            String targetSkuCode,
            Integer targetQuantity,
            Integer originalPrice,
            Integer targetPrice,
            Integer priceDifference,
            boolean additionalPaymentRequired,
            boolean partialRefundRequired,
            Integer collectShippingFee,
            Integer reshipShippingFee,
            Integer totalShippingFee,
            String shippingFeePayer,
            String claimShipmentId,
            String linkedOrderId,
            String requestedBy,
            String processedBy,
            Instant requestedAt,
            Instant processedAt,
            Instant completedAt,
            Instant createdAt,
            Instant updatedAt) {
        return new ExchangeClaimJpaEntity(
                id, claimNumber, orderItemId, sellerId, exchangeQty,
                exchangeStatus, reasonType, reasonDetail,
                originalProductId, originalSkuCode,
                targetProductGroupId, targetProductId, targetSkuCode, targetQuantity,
                originalPrice, targetPrice, priceDifference,
                additionalPaymentRequired, partialRefundRequired,
                collectShippingFee, reshipShippingFee, totalShippingFee, shippingFeePayer,
                claimShipmentId, linkedOrderId,
                requestedBy, processedBy, requestedAt, processedAt, completedAt,
                createdAt, updatedAt);
    }

    public String getId() { return id; }
    public String getClaimNumber() { return claimNumber; }
    public String getOrderItemId() { return orderItemId; }
    public long getSellerId() { return sellerId; }
    public int getExchangeQty() { return exchangeQty; }
    public String getExchangeStatus() { return exchangeStatus; }
    public String getReasonType() { return reasonType; }
    public String getReasonDetail() { return reasonDetail; }
    public Long getOriginalProductId() { return originalProductId; }
    public String getOriginalSkuCode() { return originalSkuCode; }
    public Long getTargetProductGroupId() { return targetProductGroupId; }
    public Long getTargetProductId() { return targetProductId; }
    public String getTargetSkuCode() { return targetSkuCode; }
    public Integer getTargetQuantity() { return targetQuantity; }
    public Integer getOriginalPrice() { return originalPrice; }
    public Integer getTargetPrice() { return targetPrice; }
    public Integer getPriceDifference() { return priceDifference; }
    public boolean isAdditionalPaymentRequired() { return additionalPaymentRequired; }
    public boolean isPartialRefundRequired() { return partialRefundRequired; }
    public Integer getCollectShippingFee() { return collectShippingFee; }
    public Integer getReshipShippingFee() { return reshipShippingFee; }
    public Integer getTotalShippingFee() { return totalShippingFee; }
    public String getShippingFeePayer() { return shippingFeePayer; }
    public String getClaimShipmentId() { return claimShipmentId; }
    public String getLinkedOrderId() { return linkedOrderId; }
    public String getRequestedBy() { return requestedBy; }
    public String getProcessedBy() { return processedBy; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getProcessedAt() { return processedAt; }
    public Instant getCompletedAt() { return completedAt; }
}
