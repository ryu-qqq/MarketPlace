package com.ryuqq.marketplace.adapter.out.persistence.inboundproduct.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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

    @Column(name = "product_name", nullable = false, length = 500)
    private String productName;

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

    @Column(name = "regular_price", nullable = false)
    private int regularPrice;

    @Column(name = "current_price", nullable = false)
    private int currentPrice;

    @Column(name = "option_type", nullable = false, length = 50)
    private String optionType;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Lob
    @Column(name = "description_html", columnDefinition = "LONGTEXT")
    private String descriptionHtml;

    @Column(name = "raw_payload", columnDefinition = "JSON")
    private String rawPayloadJson;

    @Column(name = "retry_count", nullable = false)
    private int retryCount;

    protected InboundProductJpaEntity() {
        super();
    }

    private InboundProductJpaEntity(
            Long id,
            Long inboundSourceId,
            String externalProductCode,
            String productName,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            int regularPrice,
            int currentPrice,
            String optionType,
            String status,
            String descriptionHtml,
            String rawPayloadJson,
            int retryCount,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.inboundSourceId = inboundSourceId;
        this.externalProductCode = externalProductCode;
        this.productName = productName;
        this.externalBrandCode = externalBrandCode;
        this.externalCategoryCode = externalCategoryCode;
        this.internalBrandId = internalBrandId;
        this.internalCategoryId = internalCategoryId;
        this.internalProductGroupId = internalProductGroupId;
        this.sellerId = sellerId;
        this.regularPrice = regularPrice;
        this.currentPrice = currentPrice;
        this.optionType = optionType;
        this.status = status;
        this.descriptionHtml = descriptionHtml;
        this.rawPayloadJson = rawPayloadJson;
        this.retryCount = retryCount;
    }

    public static InboundProductJpaEntity create(
            Long id,
            Long inboundSourceId,
            String externalProductCode,
            String productName,
            String externalBrandCode,
            String externalCategoryCode,
            Long internalBrandId,
            Long internalCategoryId,
            Long internalProductGroupId,
            Long sellerId,
            int regularPrice,
            int currentPrice,
            String optionType,
            String status,
            String descriptionHtml,
            String rawPayloadJson,
            int retryCount,
            Instant createdAt,
            Instant updatedAt) {
        return new InboundProductJpaEntity(
                id,
                inboundSourceId,
                externalProductCode,
                productName,
                externalBrandCode,
                externalCategoryCode,
                internalBrandId,
                internalCategoryId,
                internalProductGroupId,
                sellerId,
                regularPrice,
                currentPrice,
                optionType,
                status,
                descriptionHtml,
                rawPayloadJson,
                retryCount,
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

    public String getProductName() {
        return productName;
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

    public int getRegularPrice() {
        return regularPrice;
    }

    public int getCurrentPrice() {
        return currentPrice;
    }

    public String getOptionType() {
        return optionType;
    }

    public String getStatus() {
        return status;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public String getRawPayloadJson() {
        return rawPayloadJson;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
