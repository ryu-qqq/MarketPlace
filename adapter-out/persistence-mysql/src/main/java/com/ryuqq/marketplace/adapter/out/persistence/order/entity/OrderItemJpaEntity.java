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

    @Column(name = "delivery_status", nullable = false, length = 20)
    private String deliveryStatus;

    @Column(name = "shipment_company_code", length = 50)
    private String shipmentCompanyCode;

    @Column(name = "invoice", length = 100)
    private String invoice;

    @Column(name = "shipment_completed_date")
    private Instant shipmentCompletedDate;

    @Column(name = "commission_rate", nullable = false)
    private int commissionRate;

    @Column(name = "fee", nullable = false)
    private int fee;

    @Column(name = "expectation_settlement_amount", nullable = false)
    private int expectationSettlementAmount;

    @Column(name = "settlement_amount", nullable = false)
    private int settlementAmount;

    @Column(name = "share_ratio", nullable = false)
    private int shareRatio;

    @Column(name = "expected_settlement_day")
    private Instant expectedSettlementDay;

    @Column(name = "settlement_day")
    private Instant settlementDay;

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
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            String deliveryStatus,
            String shipmentCompanyCode,
            String invoice,
            Instant shipmentCompletedDate,
            int commissionRate,
            int fee,
            int expectationSettlementAmount,
            int settlementAmount,
            int shareRatio,
            Instant expectedSettlementDay,
            Instant settlementDay,
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
        this.paymentAmount = paymentAmount;
        this.receiverName = receiverName;
        this.receiverPhone = receiverPhone;
        this.receiverZipcode = receiverZipcode;
        this.receiverAddress = receiverAddress;
        this.receiverAddressDetail = receiverAddressDetail;
        this.deliveryRequest = deliveryRequest;
        this.deliveryStatus = deliveryStatus;
        this.shipmentCompanyCode = shipmentCompanyCode;
        this.invoice = invoice;
        this.shipmentCompletedDate = shipmentCompletedDate;
        this.commissionRate = commissionRate;
        this.fee = fee;
        this.expectationSettlementAmount = expectationSettlementAmount;
        this.settlementAmount = settlementAmount;
        this.shareRatio = shareRatio;
        this.expectedSettlementDay = expectedSettlementDay;
        this.settlementDay = settlementDay;
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
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipcode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest,
            String deliveryStatus,
            String shipmentCompanyCode,
            String invoice,
            Instant shipmentCompletedDate,
            int commissionRate,
            int fee,
            int expectationSettlementAmount,
            int settlementAmount,
            int shareRatio,
            Instant expectedSettlementDay,
            Instant settlementDay,
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
                paymentAmount,
                receiverName,
                receiverPhone,
                receiverZipcode,
                receiverAddress,
                receiverAddressDetail,
                deliveryRequest,
                deliveryStatus,
                shipmentCompanyCode,
                invoice,
                shipmentCompletedDate,
                commissionRate,
                fee,
                expectationSettlementAmount,
                settlementAmount,
                shareRatio,
                expectedSettlementDay,
                settlementDay,
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

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getShipmentCompanyCode() {
        return shipmentCompanyCode;
    }

    public String getInvoice() {
        return invoice;
    }

    public Instant getShipmentCompletedDate() {
        return shipmentCompletedDate;
    }

    public int getCommissionRate() {
        return commissionRate;
    }

    public int getFee() {
        return fee;
    }

    public int getExpectationSettlementAmount() {
        return expectationSettlementAmount;
    }

    public int getSettlementAmount() {
        return settlementAmount;
    }

    public int getShareRatio() {
        return shareRatio;
    }

    public Instant getExpectedSettlementDay() {
        return expectedSettlementDay;
    }

    public Instant getSettlementDay() {
        return settlementDay;
    }

    public void updateDeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}
