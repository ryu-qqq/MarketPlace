package com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** SellerOptionGroup JPA 엔티티. */
@Entity
@Table(name = "seller_option_groups")
public class SellerOptionGroupJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "option_group_name", nullable = false, length = 100)
    private String optionGroupName;

    @Column(name = "canonical_option_group_id")
    private Long canonicalOptionGroupId;

    @Column(name = "input_type", nullable = false)
    private String inputType;

    @Column(name = "sort_order")
    private int sortOrder;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deleted_at")
    private Instant deletedAt;

    protected SellerOptionGroupJpaEntity() {}

    private SellerOptionGroupJpaEntity(
            Long id,
            Long productGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            String inputType,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.optionGroupName = optionGroupName;
        this.canonicalOptionGroupId = canonicalOptionGroupId;
        this.inputType = inputType;
        this.sortOrder = sortOrder;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public static SellerOptionGroupJpaEntity create(
            Long id,
            Long productGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            String inputType,
            int sortOrder,
            boolean deleted,
            Instant deletedAt) {
        return new SellerOptionGroupJpaEntity(
                id,
                productGroupId,
                optionGroupName,
                canonicalOptionGroupId,
                inputType,
                sortOrder,
                deleted,
                deletedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getOptionGroupName() {
        return optionGroupName;
    }

    public Long getCanonicalOptionGroupId() {
        return canonicalOptionGroupId;
    }

    public String getInputType() {
        return inputType;
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
