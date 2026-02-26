package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "inbound_products")
public class InboundProductJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "inbound_source_id", nullable = false)
    private Long inboundSourceId;

    @Column(name = "external_product_code", nullable = false, length = 255)
    private String externalProductCode;

    @Column(name = "external_brand_code", length = 255)
    private String externalBrandCode;

    @Column(name = "external_category_code", length = 255)
    private String externalCategoryCode;

    @Column(name = "internal_brand_id")
    private Long internalBrandId;

    @Column(name = "internal_category_id")
    private Long internalCategoryId;

    @Column(name = "internal_product_group_id")
    private Long internalProductGroupId;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "resolved_shipping_policy_id")
    private Long resolvedShippingPolicyId;

    @Column(name = "resolved_refund_policy_id")
    private Long resolvedRefundPolicyId;

    @Column(name = "resolved_notice_category_id")
    private Long resolvedNoticeCategoryId;

    protected InboundProductJpaEntity() {
        super();
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    private InboundProductJpaEntity(
            Long id,
            Long inboundSourceId,
            String externalProductCode,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            String status,
            Long resolvedShippingPolicyId,
            Long resolvedRefundPolicyId,
            Long resolvedNoticeCategoryId,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalProductCode = externalProductCode;
        this.externalBrandCode = externalBrandCode;
        this.externalCategoryCode = externalCategoryCode;
        this.internalBrandId = internalBrandId;
        this.internalCategoryId = internalCategoryId;
        this.internalProductGroupId = internalProductGroupId;
        this.sellerId = sellerId;
        this.status = status;
        this.resolvedShippingPolicyId = resolvedShippingPolicyId;
        this.resolvedRefundPolicyId = resolvedRefundPolicyId;
        this.resolvedNoticeCategoryId = resolvedNoticeCategoryId;
    }

    @SuppressWarnings("PMD.ExcessiveParameterList")
    public static InboundProductJpaEntity create(
            Long id,
            Long inboundSourceId,
            String externalProductCode,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            String status,
            Long resolvedShippingPolicyId,
            Long resolvedRefundPolicyId,
            Long resolvedNoticeCategoryId,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundProductJpaEntity(
                id,
                inboundSourceId,
                externalProductCode,
                externalBrandCode,
                externalCategoryCode,
                internalBrandId,
                internalCategoryId,
                internalProductGroupId,
                sellerId,
                status,
                resolvedShippingPolicyId,
                resolvedRefundPolicyId,
                resolvedNoticeCategoryId,
                createdAt,
                updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getInboundSourceId() {
        return inboundSourceId;
    }

    public String getExternalProductCode() {
        return externalProductCode;
    }

    public String getExternalBrandCode() {
        return externalBrandCode;
    }

    public String getExternalCategoryCode() {
        return externalCategoryCode;
    }

    public Long getInternalBrandId() {
        return internalBrandId;
    }

    public Long getInternalCategoryId() {
        return internalCategoryId;
    }

    public Long getInternalProductGroupId() {
        return internalProductGroupId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getStatus() {
        return status;
    }

    public Long getResolvedShippingPolicyId() {
        return resolvedShippingPolicyId;
    }

    public Long getResolvedRefundPolicyId() {
        return resolvedRefundPolicyId;
    }

    public Long getResolvedNoticeCategoryId() {
        return resolvedNoticeCategoryId;
    }
}
