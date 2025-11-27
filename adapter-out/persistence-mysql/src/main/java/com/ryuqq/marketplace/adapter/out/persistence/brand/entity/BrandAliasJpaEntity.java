package com.ryuqq.marketplace.adapter.out.persistence.brand.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import com.ryuqq.marketplace.domain.brand.aggregate.brand.BrandAlias;
import com.ryuqq.marketplace.domain.brand.vo.AliasSourceType;
import com.ryuqq.marketplace.domain.brand.vo.AliasStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * BrandAliasJpaEntity - Brand Alias의 JPA Entity
 *
 * <p><strong>Zero-Tolerance 규칙 준수</strong>:</p>
 * <ul>
 *   <li>Lombok 금지 - Plain Java 클래스</li>
 *   <li>Long FK 전략 - JPA 관계 어노테이션 금지, Long brandId 사용</li>
 *   <li>Setter 금지 - Getter Only, 정적 팩토리 메서드 사용</li>
 *   <li>protected 기본 생성자 - JPA 스펙 요구사항</li>
 * </ul>
 *
 * @author ryu-qqq
 * @since 2025-11-27
 */
@Entity
@Table(name = "brand_alias")
public class BrandAliasJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "brand_id", nullable = false)
    private Long brandId;

    @Column(name = "alias_name", nullable = false, length = 255)
    private String aliasName;

    @Column(name = "normalized_alias", nullable = false, length = 255)
    private String normalizedAlias;

    @Column(name = "source_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private AliasSourceType sourceType;

    @Column(name = "seller_id", nullable = false)
    private Long sellerId;

    @Column(name = "mall_code", nullable = false, length = 50)
    private String mallCode;

    @Column(name = "confidence", nullable = false)
    private double confidence;

    @Column(name = "status", nullable = false, length = 30)
    @Enumerated(EnumType.STRING)
    private AliasStatus status;

    protected BrandAliasJpaEntity() {
        super();
    }

    private BrandAliasJpaEntity(
        Long id,
        Long brandId,
        String aliasName,
        String normalizedAlias,
        AliasSourceType sourceType,
        Long sellerId,
        String mallCode,
        double confidence,
        AliasStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        super(createdAt, updatedAt);
        this.id = id;
        this.brandId = brandId;
        this.aliasName = aliasName;
        this.normalizedAlias = normalizedAlias;
        this.sourceType = sourceType;
        this.sellerId = sellerId;
        this.mallCode = mallCode;
        this.confidence = confidence;
        this.status = status;
    }

    public static BrandAliasJpaEntity from(BrandAlias alias) {
        LocalDateTime now = LocalDateTime.now();
        return new BrandAliasJpaEntity(
            alias.id() != null ? alias.id().value() : null,
            alias.brandId(),
            alias.originalAlias(),
            alias.normalizedAlias(),
            alias.source().sourceType(),
            alias.sellerId(),
            alias.mallCode(),
            alias.confidenceValue(),
            alias.status(),
            now,
            now
        );
    }

    public Long getId() {
        return id;
    }

    public Long getBrandId() {
        return brandId;
    }

    public String getAliasName() {
        return aliasName;
    }

    public String getNormalizedAlias() {
        return normalizedAlias;
    }

    public AliasSourceType getSourceType() {
        return sourceType;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public String getMallCode() {
        return mallCode;
    }

    public double getConfidence() {
        return confidence;
    }

    public AliasStatus getStatus() {
        return status;
    }
}
