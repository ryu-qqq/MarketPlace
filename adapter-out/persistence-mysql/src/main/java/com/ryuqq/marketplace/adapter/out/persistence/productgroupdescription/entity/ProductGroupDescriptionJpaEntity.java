package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * ProductGroupDescriptionJpaEntity - 상품 그룹 상세설명 JPA 엔티티.
 *
 * <p>PER-ENT-001: JPA 관계 어노테이션 미사용, Long FK 전략.
 */
@Entity
@Table(name = "product_group_descriptions")
public class ProductGroupDescriptionJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "cdn_path", length = 500)
    private String cdnPath;

    protected ProductGroupDescriptionJpaEntity() {
        super();
    }

    private ProductGroupDescriptionJpaEntity(
            Long id,
            Long productGroupId,
            String content,
            String cdnPath,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.productGroupId = productGroupId;
        this.content = content;
        this.cdnPath = cdnPath;
    }

    public static ProductGroupDescriptionJpaEntity create(
            Long id,
            Long productGroupId,
            String content,
            String cdnPath,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductGroupDescriptionJpaEntity(
                id, productGroupId, content, cdnPath, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getContent() {
        return content;
    }

    public String getCdnPath() {
        return cdnPath;
    }
}
