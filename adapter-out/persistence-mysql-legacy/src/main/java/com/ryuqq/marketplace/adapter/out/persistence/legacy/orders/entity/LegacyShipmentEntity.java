package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * LegacyShipmentEntity - 레거시 배송 엔티티.
 *
 * <p>레거시 DB의 shipment 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "shipment")
public class LegacyShipmentEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shipment_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "SHIPMENT_TYPE")
    private String shipmentType;

    @Column(name = "PAYMENT_SNAPSHOT_SHIPPING_ADDRESS_ID")
    private Long paymentSnapshotShippingAddressId;

    @Column(name = "SENDER_NAME")
    private String senderName;

    @Column(name = "SENDER_EMAIL")
    private String senderEmail;

    @Column(name = "SENDER_PHONE_NUMBER")
    private String senderPhoneNumber;

    @Column(name = "INVOICE_NO")
    private String invoiceNo;

    @Column(name = "COMPANY_CODE")
    private String companyCode;

    @Column(name = "DELIVERY_STATUS")
    private String deliveryStatus;

    @Column(name = "DELIVERY_DATE")
    private LocalDateTime deliveryDate;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyShipmentEntity() {}

    public static LegacyShipmentEntity create(
            long orderId,
            long paymentSnapshotShippingAddressId,
            String senderName,
            String senderEmail,
            String senderPhoneNumber,
            String deliveryStatus,
            String operator) {
        LegacyShipmentEntity entity = new LegacyShipmentEntity();
        entity.orderId = orderId;
        entity.paymentSnapshotShippingAddressId = paymentSnapshotShippingAddressId;
        entity.senderName = senderName;
        entity.senderEmail = senderEmail;
        entity.senderPhoneNumber = senderPhoneNumber;
        entity.deliveryStatus = deliveryStatus;
        entity.companyCode = "REFER_DETAIL";
        entity.deleteYn = "N";
        entity.initAuditFields(operator);
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getShipmentType() {
        return shipmentType;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public String getCompanyCode() {
        return companyCode;
    }

    public String getDeliveryStatus() {
        return deliveryStatus;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
