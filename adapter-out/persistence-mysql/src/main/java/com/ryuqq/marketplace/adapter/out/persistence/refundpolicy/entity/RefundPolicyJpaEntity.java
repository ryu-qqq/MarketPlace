package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * RefundPolicyJpaEntity - 환불 정책 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>PER-ENT-003: ID 필드는 @GeneratedValue(strategy = IDENTITY).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "refund_policies")
public class RefundPolicyJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "policy_name", nullable = false, length = 100)
    private String policyName;

    @Column(name = "is_default_policy", nullable = false)
    private boolean defaultPolicy;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "return_period_days", nullable = false)
    private int returnPeriodDays;

    @Column(name = "exchange_period_days", nullable = false)
    private int exchangePeriodDays;

    @Column(name = "non_returnable_conditions", length = 500)
    private String nonReturnableConditions;

    @Column(name = "is_partial_refund_enabled", nullable = false)
    private boolean partialRefundEnabled;

    @Column(name = "is_inspection_required", nullable = false)
    private boolean inspectionRequired;

    @Column(name = "inspection_period_days")
    private Integer inspectionPeriodDays;

    @Column(name = "additional_info", length = 2000)
    private String additionalInfo;

    protected RefundPolicyJpaEntity() {
        super();
    }

    private RefundPolicyJpaEntity(
            Long id,
            Long sellerId,
            String policyName,
            boolean defaultPolicy,
            boolean active,
            int returnPeriodDays,
            int exchangePeriodDays,
            String nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            Integer inspectionPeriodDays,
            String additionalInfo,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.policyName = policyName;
        this.defaultPolicy = defaultPolicy;
        this.active = active;
        this.returnPeriodDays = returnPeriodDays;
        this.exchangePeriodDays = exchangePeriodDays;
        this.nonReturnableConditions = nonReturnableConditions;
        this.partialRefundEnabled = partialRefundEnabled;
        this.inspectionRequired = inspectionRequired;
        this.inspectionPeriodDays = inspectionPeriodDays;
        this.additionalInfo = additionalInfo;
    }

    public static RefundPolicyJpaEntity create(
            Long id,
            Long sellerId,
            String policyName,
            boolean defaultPolicy,
            boolean active,
            int returnPeriodDays,
            int exchangePeriodDays,
            String nonReturnableConditions,
            boolean partialRefundEnabled,
            boolean inspectionRequired,
            Integer inspectionPeriodDays,
            String additionalInfo,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new RefundPolicyJpaEntity(
                id,
                sellerId,
                policyName,
                defaultPolicy,
                active,
                returnPeriodDays,
                exchangePeriodDays,
                nonReturnableConditions,
                partialRefundEnabled,
                inspectionRequired,
                inspectionPeriodDays,
                additionalInfo,
                createdAt,
                updatedAt,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getPolicyName() {
        return policyName;
    }

    public boolean isDefaultPolicy() {
        return defaultPolicy;
    }

    public boolean isActive() {
        return active;
    }

    public int getReturnPeriodDays() {
        return returnPeriodDays;
    }

    public int getExchangePeriodDays() {
        return exchangePeriodDays;
    }

    public String getNonReturnableConditions() {
        return nonReturnableConditions;
    }

    public boolean isPartialRefundEnabled() {
        return partialRefundEnabled;
    }

    public boolean isInspectionRequired() {
        return inspectionRequired;
    }

    public Integer getInspectionPeriodDays() {
        return inspectionPeriodDays;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }
}
