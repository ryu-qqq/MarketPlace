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
import java.time.Instant;

/**
 * SellerSettlementJpaEntity - 셀러 정산 정보 JPA 엔티티.
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
@Table(name = "seller_settlements")
public class SellerSettlementJpaEntity extends SoftDeletableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "bank_code", length = 10)
    private String bankCode;

    @Column(name = "bank_name", length = 50)
    private String bankName;

    @Column(name = "account_number", length = 30)
    private String accountNumber;

    @Column(name = "account_holder_name", length = 50)
    private String accountHolderName;

    @Column(name = "settlement_cycle", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private SettlementCycleJpaValue settlementCycle;

    @Column(name = "settlement_day", nullable = false)
    private Integer settlementDay;

    @Column(name = "is_verified", nullable = false)
    private boolean verified;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    protected SellerSettlementJpaEntity() {
        super();
    }

    private SellerSettlementJpaEntity(
            Long id,
            Long sellerId,
            String bankCode,
            String bankName,
            String accountNumber,
            String accountHolderName,
            SettlementCycleJpaValue settlementCycle,
            Integer settlementDay,
            boolean verified,
            Instant verifiedAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        super(createdAt, updatedAt, deletedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.bankCode = bankCode;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountHolderName = accountHolderName;
        this.settlementCycle = settlementCycle;
        this.settlementDay = settlementDay;
        this.verified = verified;
        this.verifiedAt = verifiedAt;
    }

    public static SellerSettlementJpaEntity create(
            Long id,
            Long sellerId,
            String bankCode,
            String bankName,
            String accountNumber,
            String accountHolderName,
            SettlementCycleJpaValue settlementCycle,
            Integer settlementDay,
            boolean verified,
            Instant verifiedAt,
            Instant createdAt,
            Instant updatedAt,
            Instant deletedAt) {
        return new SellerSettlementJpaEntity(
                id,
                sellerId,
                bankCode,
                bankName,
                accountNumber,
                accountHolderName,
                settlementCycle,
                settlementDay,
                verified,
                verifiedAt,
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

    public String getBankCode() {
        return bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public SettlementCycleJpaValue getSettlementCycle() {
        return settlementCycle;
    }

    public Integer getSettlementDay() {
        return settlementDay;
    }

    public boolean isVerified() {
        return verified;
    }

    public Instant getVerifiedAt() {
        return verifiedAt;
    }

    /** JPA Enum for SettlementCycle. */
    public enum SettlementCycleJpaValue {
        WEEKLY,
        BIWEEKLY,
        MONTHLY
    }
}
