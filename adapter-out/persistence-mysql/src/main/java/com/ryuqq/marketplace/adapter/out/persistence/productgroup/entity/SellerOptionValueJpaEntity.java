package com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    protected SellerOptionValueJpaEntity() {}

    private SellerOptionValueJpaEntity(
            Long id,
            Long sellerOptionGroupId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder) {
        this.id = id;
        this.sellerOptionGroupId = sellerOptionGroupId;
        this.optionValueName = optionValueName;
        this.canonicalOptionValueId = canonicalOptionValueId;
        this.sortOrder = sortOrder;
    }

    public static SellerOptionValueJpaEntity create(
            Long id,
            Long sellerOptionGroupId,
            String optionValueName,
            Long canonicalOptionValueId,
            int sortOrder) {
        return new SellerOptionValueJpaEntity(
                id, sellerOptionGroupId, optionValueName, canonicalOptionValueId, sortOrder);
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
}
