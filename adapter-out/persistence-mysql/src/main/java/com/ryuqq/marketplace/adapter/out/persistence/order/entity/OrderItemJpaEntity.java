package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** OrderItem JPA 엔티티. */
@Entity
@Table(name = "order_items")
public class OrderItemJpaEntity extends BaseAuditEntity {

    @Id
    @Column(name = "id", length = 36, nullable = false)
    private String id;

    @Column(name = "order_item_number", nullable = false, length = 50)
    private String orderItemNumber;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "product_group_id", nullable = false)
    private long productGroupId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "sku_code", length = 50)
    private String skuCode;

    @Column(name = "product_group_name", length = 500)
    private String productGroupName;

    @Column(name = "brand_name", length = 200)
    private String brandName;

    @Column(name = "seller_name", length = 200)
    private String sellerName;

    @Column(name = "main_image_url", length = 1000)
    private String mainImageUrl;

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

    @Column(name = "seller_burden_discount_amount", nullable = false)
    private int sellerBurdenDiscountAmount;

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

    @Column(name = "order_item_status", nullable = false, length = 20)
    private String orderItemStatus;

    @Column(name = "external_order_status", length = 50)
    private String externalOrderStatus;

    @Column(name = "cancelled_qty", nullable = false)
    private int cancelledQty;

    @Column(name = "returned_qty", nullable = false)
    private int returnedQty;

    protected OrderItemJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private OrderItemJpaEntity(
            String id,
            String orderItemNumber,
            String orderId,
            long productGroupId,
            Long sellerId,
            Long brandId,
            Long productId,
            String skuCode,
            String productGroupName,
            String brandName,
            String sellerName,
            String mainImageUrl,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int unitPrice,
            int quantity,
            int totalAmount,
            int discountAmount,
            int sellerBurdenDiscountAmount,
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            String orderItemStatus,
            String externalOrderStatus,
            int cancelledQty,
            int returnedQty,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.orderItemNumber = orderItemNumber;
        this.orderId = orderId;
        this.productGroupId = productGroupId;
        this.sellerId = sellerId;
        this.brandId = brandId;
        this.productId = productId;
        this.skuCode = skuCode;
        this.productGroupName = productGroupName;
        this.brandName = brandName;
        this.sellerName = sellerName;
        this.mainImageUrl = mainImageUrl;
        this.externalProductId = externalProductId;
        this.externalOptionId = externalOptionId;
        this.externalProductName = externalProductName;
        this.externalOptionName = externalOptionName;
        this.externalImageUrl = externalImageUrl;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.sellerBurdenDiscountAmount = sellerBurdenDiscountAmount;
        this.paymentAmount = paymentAmount;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverZipcode = receiverZipcode;
        this.receiverAddress = receiverAddress;
        this.receiverAddressDetail = receiverAddressDetail;
        this.deliveryRequest = deliveryRequest;
        this.orderItemStatus = orderItemStatus;
        this.externalOrderStatus = externalOrderStatus;
        this.cancelledQty = cancelledQty;
        this.returnedQty = returnedQty;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static OrderItemJpaEntity create(
            String id,
            String orderItemNumber,
            String orderId,
            long productGroupId,
            Long sellerId,
            Long brandId,
            Long productId,
            String skuCode,
            String productGroupName,
            String brandName,
            String sellerName,
            String mainImageUrl,
            String externalProductId,
            String externalOptionId,
            String externalProductName,
            String externalOptionName,
            String externalImageUrl,
            int unitPrice,
            int quantity,
            int totalAmount,
            int discountAmount,
            int sellerBurdenDiscountAmount,
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            String orderItemStatus,
            String externalOrderStatus,
            int cancelledQty,
            int returnedQty,
            Instant createdAt,
            Instant updatedAt) {
        return new OrderItemJpaEntity(
                id,
                orderItemNumber,
                orderId,
                productGroupId,
                sellerId,
                brandId,
                productId,
                skuCode,
                productGroupName,
                brandName,
                sellerName,
                mainImageUrl,
                externalProductId,
                externalOptionId,
                externalProductName,
                externalOptionName,
                externalImageUrl,
                unitPrice,
                quantity,
                totalAmount,
                discountAmount,
                sellerBurdenDiscountAmount,
                paymentAmount,
                receiverName,
                receiverPhone,
                receiverZipcode,
                receiverAddress,
                receiverAddressDetail,
                deliveryRequest,
                orderItemStatus,
                externalOrderStatus,
                cancelledQty,
                returnedQty,
                createdAt,
                updatedAt);
    }

    public String getId() {
        return id;
    }

    public String getOrderItemNumber() {
        return orderItemNumber;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getProductGroupId() {
        return productGroupId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Long getProductId() {
        return productId;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
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

    public int getSellerBurdenDiscountAmount() {
        return sellerBurdenDiscountAmount;
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

    public String getOrderItemStatus() {
        return orderItemStatus;
    }

    public String getExternalOrderStatus() {
        return externalOrderStatus;
    }

    public void updateOrderItemStatus(String orderItemStatus) {
        this.orderItemStatus = orderItemStatus;
    }

    public void updateExternalOrderStatus(String externalOrderStatus) {
        this.externalOrderStatus = externalOrderStatus;
    }

    public int getCancelledQty() {
        return cancelledQty;
    }

    public int getReturnedQty() {
        return returnedQty;
    }

    public void updateCancelledQty(int cancelledQty) {
        this.cancelledQty = cancelledQty;
    }

    public void updateReturnedQty(int returnedQty) {
        this.returnedQty = returnedQty;
    }
}
