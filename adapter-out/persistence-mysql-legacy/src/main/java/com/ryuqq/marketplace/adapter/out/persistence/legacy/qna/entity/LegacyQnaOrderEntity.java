package com.ryuqq.marketplace.adapter.out.persistence.legacy.qna.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyQnaOrderEntity - 레거시 QnA-주문 연결 엔티티.
 *
 * <p>레거시 DB의 qna_order 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "qna_order")
public class LegacyQnaOrderEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_order_id")
    private Long id;

    @Column(name = "QNA_ID")
    private Long qnaId;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyQnaOrderEntity() {}

    public Long getId() {
        return id;
    }

    public Long getQnaId() {
        return qnaId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
