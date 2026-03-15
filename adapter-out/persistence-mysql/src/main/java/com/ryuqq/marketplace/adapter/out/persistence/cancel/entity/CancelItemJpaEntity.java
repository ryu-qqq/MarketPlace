package com.ryuqq.marketplace.adapter.out.persistence.cancel.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/**
 * 취소 상품 JPA 엔티티.
 *
 * <p>cancel_items 테이블과 매핑됩니다. cancel_items는 created_at만 존재하므로 BaseAuditEntity를 상속하지 않습니다.
 * CancelItemId.value()가 Long이므로 auto-increment PK를 사용합니다.
 */
@Entity
@Table(name = "cancel_items")
public class CancelItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cancel_id", nullable = false, length = 36)
    private String cancelId;

    @Column(name = "order_item_id", nullable = false)
    private long orderItemId;

    @Column(name = "cancel_qty", nullable = false)
    private int cancelQty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /** JPA 스펙 요구사항 - 기본 생성자. */
    protected CancelItemJpaEntity() {}

    private CancelItemJpaEntity(
            Long id, String cancelId, long orderItemId, int cancelQty, Instant createdAt) {
        this.id = id;
        this.cancelId = cancelId;
        this.orderItemId = orderItemId;
        this.cancelQty = cancelQty;
        this.createdAt = createdAt;
    }

    /**
     * 팩토리 메서드.
     *
     * @param id 취소 상품 ID (null이면 신규 생성)
     * @param cancelId 취소 ID (FK)
     * @param orderItemId 주문 상품 ID
     * @param cancelQty 취소 수량
     * @param createdAt 생성 일시
     * @return CancelItemJpaEntity 인스턴스
     */
    public static CancelItemJpaEntity create(
            Long id, String cancelId, long orderItemId, int cancelQty, Instant createdAt) {
        return new CancelItemJpaEntity(id, cancelId, orderItemId, cancelQty, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getCancelId() {
        return cancelId;
    }

    public long getOrderItemId() {
        return orderItemId;
    }

    public int getCancelQty() {
        return cancelQty;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
