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
 * LegacyPaymentEntity - 레거시 결제 엔티티.
 *
 * <p>레거시 DB의 payment 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "payment")
public class LegacyPaymentEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "USER_ID")
    private long userId;

    @Column(name = "PAYMENT_AMOUNT")
    private long paymentAmount;

    @Column(name = "PAYMENT_STATUS")
    private String paymentStatus;

    @Column(name = "SITE_NAME")
    private String siteName;

    @Column(name = "PAYMENT_DATE")
    private LocalDateTime paymentDate;

    @Column(name = "CANCELED_DATE")
    private LocalDateTime canceledDate;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    protected LegacyPaymentEntity() {}

    public static LegacyPaymentEntity create(
            long userId,
            long paymentAmount,
            String paymentStatus,
            String siteName,
            LocalDateTime paymentDate,
            String operator) {
        LegacyPaymentEntity entity = new LegacyPaymentEntity();
        entity.userId = userId;
        entity.paymentAmount = paymentAmount;
        entity.paymentStatus = paymentStatus;
        entity.siteName = siteName;
        entity.paymentDate = paymentDate;
        entity.deleteYn = "N";
        entity.initAuditFields(operator);
        return entity;
    }

    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public long getPaymentAmount() {
        return paymentAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public String getSiteName() {
        return siteName;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public LocalDateTime getCanceledDate() {
        return canceledDate;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
