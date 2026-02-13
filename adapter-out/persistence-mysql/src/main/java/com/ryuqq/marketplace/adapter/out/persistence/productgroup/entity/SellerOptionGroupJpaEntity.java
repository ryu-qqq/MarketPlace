package com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column(name = "sort_order")
    private int sortOrder;

    protected SellerOptionGroupJpaEntity() {}

    private SellerOptionGroupJpaEntity(
            Long id,
            Long productGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            int sortOrder) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.optionGroupName = optionGroupName;
        this.canonicalOptionGroupId = canonicalOptionGroupId;
        this.sortOrder = sortOrder;
    }

    public static SellerOptionGroupJpaEntity create(
            Long id,
            Long productGroupId,
            String optionGroupName,
            Long canonicalOptionGroupId,
            int sortOrder) {
        return new SellerOptionGroupJpaEntity(
                id, productGroupId, optionGroupName, canonicalOptionGroupId, sortOrder);
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

    public int getSortOrder() {
        return sortOrder;
    }
}
