package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** OrderItem JPA 엔티티. */
@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "product_group_id", nullable = false)
    private long productGroupId;

    @Column(name = "product_id", nullable = false)
    private long productId;

    @Column(name = "seller_id", nullable = false)
    private long sellerId;

    @Column(name = "brand_id", nullable = false)
    private long brandId;

    @Column(name = "sku_code", length = 50)
    private String skuCode;

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
    private String receiverZipcode;

    @Column(name = "receiver_address", length = 500)
    private String receiverAddress;

    @Column(name = "receiver_address_detail", length = 500)
    private String receiverAddressDetail;

    @Column(name = "delivery_request", length = 500)
    private String deliveryRequest;

    protected OrderItemJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private OrderItemJpaEntity(
            Long id,
            String orderId,
            long productGroupId,
            long productId,
            long sellerId,
            long brandId,
            String skuCode,
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
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.orderId = orderId;
        this.productGroupId = productGroupId;
        this.productId = productId;
        this.sellerId = sellerId;
        this.brandId = brandId;
        this.skuCode = skuCode;
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
        this.receiverZipcode = receiverZipcode;
        this.receiverAddress = receiverAddress;
        this.receiverAddressDetail = receiverAddressDetail;
        this.deliveryRequest = deliveryRequest;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static OrderItemJpaEntity create(
            Long id,
            String orderId,
            long productGroupId,
            long productId,
            long sellerId,
            long brandId,
            String skuCode,
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
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            Instant createdAt,
            Instant updatedAt) {
        return new OrderItemJpaEntity(
                id,
                orderId,
                productGroupId,
                productId,
                sellerId,
                brandId,
                skuCode,
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
                receiverZipcode,
                receiverAddress,
                receiverAddressDetail,
                deliveryRequest,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getProductGroupId() {
        return productGroupId;
    }

    public long getProductId() {
        return productId;
    }

    public long getSellerId() {
        return sellerId;
    }

    public long getBrandId() {
        return brandId;
    }

    public String getSkuCode() {
        return skuCode;
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

    public String getReceiverZipcode() {
        return receiverZipcode;
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
}
