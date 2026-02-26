package com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * ProductNoticeEntry JPA 엔티티.
 *
 * <p>고시정보 항목의 영속성 엔티티입니다. 감사 필드 없이 독립적으로 관리됩니다.
 *
 * <p>PER-ENT-001: JPA 관계 어노테이션 미사용, Long FK 전략 적용.
 */
@Entity
@Table(name = "product_notice_entries")
public class ProductNoticeEntryJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_notice_id", nullable = false)
    private Long productNoticeId;

    @Column(name = "notice_field_id", nullable = false)
    private Long noticeFieldId;

    @Column(name = "field_value", length = 500)
    private String fieldValue;

    protected ProductNoticeEntryJpaEntity() {}

    private ProductNoticeEntryJpaEntity(
            Long id, Long productNoticeId, Long noticeFieldId, String fieldValue) {
        this.id = id;
        this.productNoticeId = productNoticeId;
        this.noticeFieldId = noticeFieldId;
        this.fieldValue = fieldValue;
    }

    public static ProductNoticeEntryJpaEntity create(
            Long id, Long productNoticeId, Long noticeFieldId, String fieldValue) {
        return new ProductNoticeEntryJpaEntity(id, productNoticeId, noticeFieldId, fieldValue);
    }

    public Long getId() {
        return id;
    }

    public Long getProductNoticeId() {
        return productNoticeId;
    }

    public Long getNoticeFieldId() {
        return noticeFieldId;
    }

    public String getFieldValue() {
        return fieldValue;
    }
}
