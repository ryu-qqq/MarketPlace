package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyQnaProductEntity - 레거시 QnA-상품 연결 엔티티.
 *
 * <p>레거시 DB의 qna_product 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "qna_product")
public class LegacyQnaProductEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_product_id")
    private Long id;

    @Column(name = "QNA_ID")
    private Long qnaId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyQnaProductEntity() {}

    public Long getId() {
        return id;
    }

    public Long getQnaId() {
        return qnaId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
