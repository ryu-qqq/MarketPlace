package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalTime;

/**
 * ShippingPolicyJpaEntity - 배송 정책 JPA 엔티티.
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
@Table(name = "shipping_policies")
public class ShippingPolicyJpaEntity extends SoftDeletableEntity {

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

    @Column(name = "shipping_fee_type", nullable = false, length = 30)
    private String shippingFeeType;

    @Column(name = "base_fee")
    private Integer baseFee;

    @Column(name = "free_threshold")
    private Integer freeThreshold;

    @Column(name = "jeju_extra_fee")
    private Integer jejuExtraFee;

    @Column(name = "island_extra_fee")
    private Integer islandExtraFee;

    @Column(name = "return_fee")
    private Integer returnFee;

    @Column(name = "exchange_fee")
    private Integer exchangeFee;

    @Column(name = "lead_time_min_days")
    private Integer leadTimeMinDays;

    @Column(name = "lead_time_max_days")
    private Integer leadTimeMaxDays;

    @Column(name = "lead_time_cutoff_time")
    private LocalTime leadTimeCutoffTime;

    protected ShippingPolicyJpaEntity() {
        super();
    }

    private ShippingPolicyJpaEntity(
            Long id,
            Long sellerId,
            String policyName,
            boolean defaultPolicy,
            boolean active,
            String shippingFeeType,
            Integer baseFee,
            Integer freeThreshold,
            Integer jejuExtraFee,
            Integer islandExtraFee,
            Integer returnFee,
            Integer exchangeFee,
            Integer leadTimeMinDays,
            Integer leadTimeMaxDays,
            LocalTime leadTimeCutoffTime,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.policyName = policyName;
        this.defaultPolicy = defaultPolicy;
        this.active = active;
        this.shippingFeeType = shippingFeeType;
        this.baseFee = baseFee;
        this.freeThreshold = freeThreshold;
        this.jejuExtraFee = jejuExtraFee;
        this.islandExtraFee = islandExtraFee;
        this.returnFee = returnFee;
        this.exchangeFee = exchangeFee;
        this.leadTimeMinDays = leadTimeMinDays;
        this.leadTimeMaxDays = leadTimeMaxDays;
        this.leadTimeCutoffTime = leadTimeCutoffTime;
    }

    public static ShippingPolicyJpaEntity create(
            Long id,
            Long sellerId,
            String policyName,
            boolean defaultPolicy,
            boolean active,
            String shippingFeeType,
            Integer baseFee,
            Integer freeThreshold,
            Integer jejuExtraFee,
            Integer islandExtraFee,
            Integer returnFee,
            Integer exchangeFee,
            Integer leadTimeMinDays,
            Integer leadTimeMaxDays,
            LocalTime leadTimeCutoffTime,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new ShippingPolicyJpaEntity(
                id,
                sellerId,
                policyName,
                defaultPolicy,
                active,
                shippingFeeType,
                baseFee,
                freeThreshold,
                jejuExtraFee,
                islandExtraFee,
                returnFee,
                exchangeFee,
                leadTimeMinDays,
                leadTimeMaxDays,
                leadTimeCutoffTime,
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

    public String getShippingFeeType() {
        return shippingFeeType;
    }

    public Integer getBaseFee() {
        return baseFee;
    }

    public Integer getFreeThreshold() {
        return freeThreshold;
    }

    public Integer getJejuExtraFee() {
        return jejuExtraFee;
    }

    public Integer getIslandExtraFee() {
        return islandExtraFee;
    }

    public Integer getReturnFee() {
        return returnFee;
    }

    public Integer getExchangeFee() {
        return exchangeFee;
    }

    public Integer getLeadTimeMinDays() {
        return leadTimeMinDays;
    }

    public Integer getLeadTimeMaxDays() {
        return leadTimeMaxDays;
    }

    public LocalTime getLeadTimeCutoffTime() {
        return leadTimeCutoffTime;
    }
}
