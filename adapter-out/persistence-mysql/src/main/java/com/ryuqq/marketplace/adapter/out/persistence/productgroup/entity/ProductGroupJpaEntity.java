package com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** ProductGroup JPA 엔티티. */
@Entity
@Table(name = "product_groups")
public class ProductGroupJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "shipping_policy_id", nullable = false)
    private Long shippingPolicyId;

    @Column(name = "refund_policy_id", nullable = false)
    private Long refundPolicyId;

    @Column(name = "product_group_name", nullable = false, length = 200)
    private String productGroupName;

    @Column(name = "option_type", nullable = false, length = 50)
    private String optionType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    protected ProductGroupJpaEntity() {
        super();
    }

    private ProductGroupJpaEntity(
            Long id,
            Long sellerId,
            Long brandId,
            Long categoryId,
            Long shippingPolicyId,
            Long refundPolicyId,
            String productGroupName,
            String optionType,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.sellerId = sellerId;
        this.brandId = brandId;
        this.categoryId = categoryId;
        this.shippingPolicyId = shippingPolicyId;
        this.refundPolicyId = refundPolicyId;
        this.productGroupName = productGroupName;
        this.optionType = optionType;
        this.status = status;
    }

    public static ProductGroupJpaEntity create(
            Long id,
            Long sellerId,
            Long brandId,
            Long categoryId,
            Long shippingPolicyId,
            Long refundPolicyId,
            String productGroupName,
            String optionType,
            String status,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductGroupJpaEntity(
                id,
                sellerId,
                brandId,
                categoryId,
                shippingPolicyId,
                refundPolicyId,
                productGroupName,
                optionType,
                status,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getShippingPolicyId() {
        return shippingPolicyId;
    }

    public Long getRefundPolicyId() {
        return refundPolicyId;
    }

    public String getProductGroupName() {
        return productGroupName;
    }

    public String getOptionType() {
        return optionType;
    }

    public String getStatus() {
        return status;
    }
}
