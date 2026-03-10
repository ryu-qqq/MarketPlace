package com.ryuqq.marketplace.adapter.out.persistence.order.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** Payment JPA 엔티티. 주문과 1:1 매핑. */
@Entity
@Table(name = "payments")
public class PaymentJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "order_id", nullable = false, length = 36)
    private String orderId;

    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_agency_id", length = 100)
    private String paymentAgencyId;

    @Column(name = "payment_amount", nullable = false)
    private int paymentAmount;

    @Column(name = "paid_at")
    private Instant paidAt;

    @Column(name = "canceled_at")
    private Instant canceledAt;

    protected PaymentJpaEntity() {
        super();
    }

    private PaymentJpaEntity(
            Long id,
            String orderId,
            String paymentStatus,
            String paymentMethod,
            String paymentAgencyId,
            int paymentAmount,
            Instant paidAt,
            Instant canceledAt,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.orderId = orderId;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.paymentAgencyId = paymentAgencyId;
        this.paymentAmount = paymentAmount;
        this.paidAt = paidAt;
        this.canceledAt = canceledAt;
    }

    public static PaymentJpaEntity create(
            Long id,
            String orderId,
            String paymentStatus,
            String paymentMethod,
            String paymentAgencyId,
            int paymentAmount,
            Instant paidAt,
            Instant canceledAt,
            Instant createdAt,
            Instant updatedAt) {
        return new PaymentJpaEntity(
                id,
                orderId,
                paymentStatus,
                paymentMethod,
                paymentAgencyId,
                paymentAmount,
                paidAt,
                canceledAt,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getPaymentAgencyId() {
        return paymentAgencyId;
    }

    public int getPaymentAmount() {
        return paymentAmount;
    }

    public Instant getPaidAt() {
        return paidAt;
    }

    public Instant getCanceledAt() {
        return canceledAt;
    }
}
