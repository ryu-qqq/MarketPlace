package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyPaymentBillEntity - 레거시 결제 청구서 엔티티.
 *
 * <p>레거시 DB의 payment_bill 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "payment_bill")
public class LegacyPaymentBillEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_bill_id")
    private Long id;

    @Column(name = "PAYMENT_ID")
    private long paymentId;

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "PAYMENT_METHOD_ID")
    private long paymentMethodId;

    @Column(name = "PAYMENT_AMOUNT")
    private long paymentAmount;

    @Column(name = "USED_MILEAGE_AMOUNT")
    private long usedMileageAmount;

    @Column(name = "BUYER_NAME")
    private String buyerName;

    @Column(name = "BUYER_EMAIL")
    private String buyerEmail;

    @Column(name = "BUYER_PHONE_NUMBER")
    private String buyerPhoneNumber;

    @Column(name = "PAYMENT_AGENCY_ID")
    private String paymentAgencyId;

    @Column(name = "PAYMENT_UNIQUE_ID")
    private String paymentUniqueId;

    @Column(name = "RECEIPT_URL")
    private String receiptUrl;

    @Column(name = "PAYMENT_CHANNEL")
    private String paymentChannel;

    @Column(name = "CARD_NAME")
    private String cardName;

    @Column(name = "CARD_NUMBER")
    private String cardNumber;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    protected LegacyPaymentBillEntity() {}

    public static LegacyPaymentBillEntity create(
            long paymentId,
            long userId,
            long paymentAmount,
            String buyerName,
            String buyerEmail,
            String buyerPhoneNumber,
            String paymentUniqueId,
            String paymentChannel,
            String operator) {
        LegacyPaymentBillEntity entity = new LegacyPaymentBillEntity();
        entity.paymentId = paymentId;
        entity.userId = userId;
        entity.paymentMethodId = 0L;
        entity.paymentAmount = paymentAmount;
        entity.usedMileageAmount = 0L;
        entity.buyerName = buyerName;
        entity.buyerEmail = buyerEmail;
        entity.buyerPhoneNumber = buyerPhoneNumber;
        entity.paymentAgencyId = paymentUniqueId;
        entity.paymentUniqueId = paymentUniqueId;
        entity.paymentChannel = paymentChannel;
        entity.deleteYn = "N";
        entity.initAuditFields(operator);
        return entity;
    }

    public Long getId() {
        return id;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public long getUserId() {
        return userId;
    }

    public long getPaymentAmount() {
        return paymentAmount;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public String getBuyerPhoneNumber() {
        return buyerPhoneNumber;
    }

    public String getPaymentUniqueId() {
        return paymentUniqueId;
    }

    public String getPaymentChannel() {
        return paymentChannel;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
