package com.ryuqq.marketplace.adapter.out.persistence.seller.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.SoftDeletableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/**
 * SellerContractJpaEntity - 셀러 계약 정보 JPA 엔티티.
 *
 * <p>PER-ENT-001: Entity는 @Entity, @Table 어노테이션 필수.
 *
 * <p>PER-ENT-002: JPA 관계 어노테이션 금지 (@OneToMany, @ManyToOne 등).
 *
 * <p>PER-ENT-003: ID 필드는 @GeneratedValue(strategy = IDENTITY).
 *
 * <p>PER-ENT-004: Lombok 사용 금지 - 수동 Getter/생성자.
 */
@Entity
@Table(name = "seller_contracts")
public class SellerContractJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "commission_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(name = "contract_start_date", nullable = false)
    private LocalDate contractStartDate;

    @Column(name = "contract_end_date")
    private LocalDate contractEndDate;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ContractStatusJpaValue status;

    @Column(name = "special_terms", columnDefinition = "TEXT")
    private String specialTerms;

    protected SellerContractJpaEntity() {
        super();
    }

    private SellerContractJpaEntity(
            Long id,
            Long sellerId,
            BigDecimal commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            ContractStatusJpaValue status,
            String specialTerms,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.commissionRate = commissionRate;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.status = status;
        this.specialTerms = specialTerms;
    }

    public static SellerContractJpaEntity create(
            Long id,
            Long sellerId,
            BigDecimal commissionRate,
            LocalDate contractStartDate,
            LocalDate contractEndDate,
            ContractStatusJpaValue status,
            String specialTerms,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerContractJpaEntity(
                id,
                sellerId,
                commissionRate,
                contractStartDate,
                contractEndDate,
                status,
                specialTerms,
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

    public BigDecimal getCommissionRate() {
        return commissionRate;
    }

    public LocalDate getContractStartDate() {
        return contractStartDate;
    }

    public LocalDate getContractEndDate() {
        return contractEndDate;
    }

    public ContractStatusJpaValue getStatus() {
        return status;
    }

    public String getSpecialTerms() {
        return specialTerms;
    }

    /** JPA Enum for ContractStatus. */
    public enum ContractStatusJpaValue {
        ACTIVE,
        EXPIRED,
        TERMINATED
    }
}
