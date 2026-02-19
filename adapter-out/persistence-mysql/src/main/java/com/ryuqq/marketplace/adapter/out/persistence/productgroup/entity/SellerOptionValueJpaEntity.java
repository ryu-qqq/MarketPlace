package com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** SellerOptionValue JPA 엔티티. */
@Entity
@Table(name = "seller_option_values")
public class SellerOptionValueJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_option_group_id", nullable = false)
    private Long sellerOptionGroupId;

    @Column(name = "option_value_name", nullable = false, length = 100)
    private String optionValueName;

    @Column(name = "canonical_option_value_id")
    private Long canonicalOptionValueId;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected SellerOptionValueJpaEntity() {}

    private SellerOptionValueJpaEntity(
            Long id,
            Long sellerOptionGroupId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        this.id = id;
        this.sellerOptionGroupId = sellerOptionGroupId;
        this.optionValueName = optionValueName;
        this.canonicalOptionValueId = canonicalOptionValueId;
        this.sortOrder = sortOrder;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static SellerOptionValueJpaEntity create(
            Long id,
            Long sellerOptionGroupId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        return new SellerOptionValueJpaEntity(
                id,
                sellerOptionGroupId,
                optionValueName,
                canonicalOptionValueId,
                sortOrder,
                deleted,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSellerOptionGroupId() {
        return sellerOptionGroupId;
    }

    public String getOptionValueName() {
        return optionValueName;
    }

    public Long getCanonicalOptionValueId() {
        return canonicalOptionValueId;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public Instant getDeletedAt() {
        return deletedAt;
    }
}
