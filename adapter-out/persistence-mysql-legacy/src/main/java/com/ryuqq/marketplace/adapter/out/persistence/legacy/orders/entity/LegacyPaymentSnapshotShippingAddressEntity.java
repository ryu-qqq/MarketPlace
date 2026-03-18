package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyPaymentSnapshotShippingAddressEntity - 레거시 결제 스냅샷 배송지 엔티티.
 *
 * <p>레거시 DB의 payment_snapshot_shipping_address 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "payment_snapshot_shipping_address")
public class LegacyPaymentSnapshotShippingAddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_snapshot_shipping_address_id")
    private Long id;

    @Column(name = "PAYMENT_ID")
    private long paymentId;

    @Column(name = "RECEIVER_NAME")
    private String receiverName;

    @Column(name = "SHIPPING_ADDRESS_NAME")
    private String shippingAddressName;

    @Column(name = "ADDRESS_LINE1")
    private String addressLine1;

    @Column(name = "ADDRESS_LINE2")
    private String addressLine2;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "ZIP_CODE")
    private String zipCode;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "DELIVERY_REQUEST")
    private String deliveryRequest;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyPaymentSnapshotShippingAddressEntity() {}

    public Long getId() {
        return id;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public String getShippingAddressName() {
        return shippingAddressName;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getCountry() {
        return country;
    }

    public String getDeliveryRequest() {
        return deliveryRequest;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
