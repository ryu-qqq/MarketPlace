package com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity;

import com.ryuqq.marketplace.adapter.out.persistence.common.entity.BaseAuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * ProductNotice JPA 엔티티.
 *
 * <p>상품 고시정보 Aggregate Root의 영속성 엔티티입니다.
 *
 * <p>PER-ENT-001: JPA 관계 어노테이션 미사용, Long FK 전략 적용.
 */
@Entity
@Table(name = "product_notices")
public class ProductNoticeJpaEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_group_id", nullable = false)
    private Long productGroupId;

    @Column(name = "notice_category_id", nullable = false)
    private Long noticeCategoryId;

    protected ProductNoticeJpaEntity() {
        super();
    }

    private ProductNoticeJpaEntity(
            Long id,
            Long productGroupId,
            Long noticeCategoryId,
            Instant createdAt,
            Instant updatedAt) {
        super(createdAt, updatedAt);
        this.id = id;
        this.productGroupId = productGroupId;
        this.noticeCategoryId = noticeCategoryId;
    }

    public static ProductNoticeJpaEntity create(
            Long id,
            Long productGroupId,
            Long noticeCategoryId,
            Instant createdAt,
            Instant updatedAt) {
        return new ProductNoticeJpaEntity(
                id, productGroupId, noticeCategoryId, createdAt, updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public Long getNoticeCategoryId() {
        return noticeCategoryId;
    }
}
