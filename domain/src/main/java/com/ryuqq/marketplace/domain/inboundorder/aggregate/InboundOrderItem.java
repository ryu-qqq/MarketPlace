package com.ryuqq.marketplace.domain.inboundorder.aggregate;

import com.ryuqq.marketplace.domain.inboundorder.id.InboundOrderItemId;

/** InboundOrder 내부 Entity — 외부몰 주문 상품 아이템. */
public class InboundOrderItem {

    private final InboundOrderItemId id;

    private final String externalProductOrderId;
    private final String externalProductId;
    private final String externalOptionId;
    private final String externalProductName;
    private final String externalOptionName;
    private final String externalImageUrl;

    private final int unitPrice;
    private final int quantity;
    private final int totalAmount;
    private final int discountAmount;
    private final int sellerBurdenDiscountAmount;
    private final int paymentAmount;

    private final String receiverName;
    private final String receiverPhone;
    private final String receiverZipCode;
    private final String receiverAddress;
    private final String receiverAddressDetail;
    private final String deliveryRequest;

    private Long resolvedProductGroupId;
    private Long resolvedProductId;
    private Long resolvedSellerId;
    private Long resolvedBrandId;
    private String resolvedSkuCode;
    private String resolvedProductGroupName;
    private boolean mapped;

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundOrderItem(
            InboundOrderItemId id,
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
            int sellerBurdenDiscountAmount,
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
            boolean mapped) {
        this.id = id;
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
        this.sellerBurdenDiscountAmount = sellerBurdenDiscountAmount;
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
    public static InboundOrderItem forNew(
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
            int sellerBurdenDiscountAmount,
            int paymentAmount,
            String receiverName,
            String receiverPhone,
            String receiverZipCode,
            String receiverAddress,
            String receiverAddressDetail,
            String deliveryRequest) {
        return new InboundOrderItem(
                InboundOrderItemId.forNew(),
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
                sellerBurdenDiscountAmount,
                paymentAmount,
                receiverName,
                receiverPhone,
                receiverZipCode,
                receiverAddress,
                receiverAddressDetail,
                deliveryRequest,
                null,
                null,
                null,
                null,
                null,
                null,
                false);
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundOrderItem reconstitute(
            InboundOrderItemId id,
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
            int sellerBurdenDiscountAmount,
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
            boolean mapped) {
        return new InboundOrderItem(
                id,
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
                sellerBurdenDiscountAmount,
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
                mapped);
    }

    public void applyMapping(
            Long productGroupId,
            Long productId,
            Long sellerId,
            Long brandId,
            String skuCode,
            String productGroupName) {
        this.resolvedProductGroupId = productGroupId;
        this.resolvedProductId = productId;
        this.resolvedSellerId = sellerId;
        this.resolvedBrandId = brandId;
        this.resolvedSkuCode = skuCode;
        this.resolvedProductGroupName = productGroupName;
        this.mapped = true;
    }

    public boolean isMapped() {
        return mapped;
    }

    public Long idValue() {
        return id != null ? id.value() : null;
    }

    public InboundOrderItemId id() {
        return id;
    }

    public String externalProductOrderId() {
        return externalProductOrderId;
    }

    public String externalProductId() {
        return externalProductId;
    }

    public String externalOptionId() {
        return externalOptionId;
    }

    public String externalProductName() {
        return externalProductName;
    }

    public String externalOptionName() {
        return externalOptionName;
    }

    public String externalImageUrl() {
        return externalImageUrl;
    }

    public int unitPrice() {
        return unitPrice;
    }

    public int quantity() {
        return quantity;
    }

    public int totalAmount() {
        return totalAmount;
    }

    public int discountAmount() {
        return discountAmount;
    }

    public int sellerBurdenDiscountAmount() {
        return sellerBurdenDiscountAmount;
    }

    public int paymentAmount() {
        return paymentAmount;
    }

    public String receiverName() {
        return receiverName;
    }

    public String receiverPhone() {
        return receiverPhone;
    }

    public String receiverZipCode() {
        return receiverZipCode;
    }

    public String receiverAddress() {
        return receiverAddress;
    }

    public String receiverAddressDetail() {
        return receiverAddressDetail;
    }

    public String deliveryRequest() {
        return deliveryRequest;
    }

    public Long resolvedProductGroupId() {
        return resolvedProductGroupId;
    }

    public Long resolvedProductId() {
        return resolvedProductId;
    }

    public Long resolvedSellerId() {
        return resolvedSellerId;
    }

    public Long resolvedBrandId() {
        return resolvedBrandId;
    }

    public String resolvedSkuCode() {
        return resolvedSkuCode;
    }

    public String resolvedProductGroupName() {
        return resolvedProductGroupName;
    }
}
