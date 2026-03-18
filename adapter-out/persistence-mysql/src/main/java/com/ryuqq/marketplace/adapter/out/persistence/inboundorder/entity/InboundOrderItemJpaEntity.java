package com.ryuqq.marketplace.adapter.out.persistence.inboundorder.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** InboundOrderItem JPA 엔티티. */
@Entity
@Table(name = "inbound_order_items")
public class InboundOrderItemJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "inbound_order_id", nullable = false)
    private long inboundOrderId;

    @Column(name = "external_product_order_id", length = 100)
    private String externalProductOrderId;

    @Column(name = "external_product_id", nullable = false, length = 100)
    private String externalProductId;

    @Column(name = "external_option_id", length = 100)
    private String externalOptionId;

    @Column(name = "external_product_name", length = 500)
    private String externalProductName;

    @Column(name = "external_option_name", length = 500)
    private String externalOptionName;

    @Column(name = "external_image_url", length = 1000)
    private String externalImageUrl;

    @Column(name = "unit_price", nullable = false)
    private int unitPrice;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    @Column(name = "discount_amount", nullable = false)
    private int discountAmount;

    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;

    @Column(name = "receiver_name", nullable = false, length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    @Column(name = "receiver_zipcode", length = 10)
    private String receiverZipCode;

    @Column(name = "receiver_address", length = 500)
    private String receiverAddress;

    @Column(name = "receiver_address_detail", length = 500)
    private String receiverAddressDetail;

    @Column(name = "delivery_request", length = 500)
    private String deliveryRequest;

    @Column(name = "resolved_product_group_id")
    private Long resolvedProductGroupId;

    @Column(name = "resolved_product_id")
    private Long resolvedProductId;

    @Column(name = "resolved_seller_id")
    private Long resolvedSellerId;

    @Column(name = "resolved_brand_id")
    private Long resolvedBrandId;

    @Column(name = "resolved_sku_code", length = 50)
    private String resolvedSkuCode;

    @Column(name = "resolved_product_group_name", length = 500)
    private String resolvedProductGroupName;

    @Column(name = "mapped", nullable = false)
    private boolean mapped;

    protected InboundOrderItemJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundOrderItemJpaEntity(
            Long id,
            long inboundOrderId,
            String externalProductOrderId,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int unitPrice,
            int quantity,
            int totalAmount,
            int discountAmount,
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipCode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            Long resolvedProductGroupId,
            Long resolvedProductId,
            Long resolvedSellerId,
            Long resolvedBrandId,
            String resolvedSkuCode,
            String resolvedProductGroupName,
            boolean mapped,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.inboundOrderId = inboundOrderId;
        this.externalProductOrderId = externalProductOrderId;
        this.externalProductId = externalProductId;
        this.externalOptionId = externalOptionId;
        this.externalProductName = externalProductName;
        this.externalOptionName = externalOptionName;
        this.externalImageUrl = externalImageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.paymentAmount = paymentAmount;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverZipCode = receiverZipCode;
        this.receiverAddress = receiverAddress;
        this.receiverAddressDetail = receiverAddressDetail;
        this.deliveryRequest = deliveryRequest;
        this.resolvedProductGroupId = resolvedProductGroupId;
        this.resolvedProductId = resolvedProductId;
        this.resolvedSellerId = resolvedSellerId;
        this.resolvedBrandId = resolvedBrandId;
        this.resolvedSkuCode = resolvedSkuCode;
        this.resolvedProductGroupName = resolvedProductGroupName;
        this.mapped = mapped;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundOrderItemJpaEntity create(
            Long id,
            long inboundOrderId,
            String externalProductOrderId,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int unitPrice,
            int quantity,
            int totalAmount,
            int discountAmount,
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipCode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            Long resolvedProductGroupId,
            Long resolvedProductId,
            Long resolvedSellerId,
            Long resolvedBrandId,
            String resolvedSkuCode,
            String resolvedProductGroupName,
            boolean mapped,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundOrderItemJpaEntity(
                id,
                inboundOrderId,
                externalProductOrderId,
                externalProductId,
                externalOptionId,
                externalProductName,
                externalOptionName,
                externalImageUrl,
                unitPrice,
                quantity,
                totalAmount,
                discountAmount,
                paymentAmount,
                receiverName,
                receiverPhone,
                receiverZipCode,
                receiverAddress,
                receiverAddressDetail,
                deliveryRequest,
                resolvedProductGroupId,
                resolvedProductId,
                resolvedSellerId,
                resolvedBrandId,
                resolvedSkuCode,
                resolvedProductGroupName,
                mapped,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public long getInboundOrderId() {
        return inboundOrderId;
    }

    public String getExternalProductOrderId() {
        return externalProductOrderId;
    }

    public String getExternalProductId() {
        return externalProductId;
    }

    public String getExternalOptionId() {
        return externalOptionId;
    }

    public String getExternalProductName() {
        return externalProductName;
    }

    public String getExternalOptionName() {
        return externalOptionName;
    }

    public String getExternalImageUrl() {
        return externalImageUrl;
    }

    public int getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public String getReceiverZipCode() {
        return receiverZipCode;
    }

    public String getReceiverAddress() {
        return receiverAddress;
    }

    public String getReceiverAddressDetail() {
        return receiverAddressDetail;
    }

    public String getDeliveryRequest() {
        return deliveryRequest;
    }

    public Long getResolvedProductGroupId() {
        return resolvedProductGroupId;
    }

    public Long getResolvedProductId() {
        return resolvedProductId;
    }

    public Long getResolvedSellerId() {
        return resolvedSellerId;
    }

    public Long getResolvedBrandId() {
        return resolvedBrandId;
    }

    public String getResolvedSkuCode() {
        return resolvedSkuCode;
    }

    public String getResolvedProductGroupName() {
        return resolvedProductGroupName;
    }

    public boolean isMapped() {
        return mapped;
    }
}
