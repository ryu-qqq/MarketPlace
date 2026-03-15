package com.ryuqq.marketplace.adapter.out.persistence.exchange.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

/** 교환 대상 상품 JPA 엔티티. */
@Entity
@Table(name = "exchange_items")
public class ExchangeItemJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "exchange_claim_id", nullable = false, length = 36)
    private String exchangeClaimId;

    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "exchange_qty", nullable = false)
    private int exchangeQty;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected ExchangeItemJpaEntity() {}

    private ExchangeItemJpaEntity(
            Long id, String exchangeClaimId, Long orderItemId, int exchangeQty, Instant createdAt) {
        this.id = id;
        this.exchangeClaimId = exchangeClaimId;
        this.orderItemId = orderItemId;
        this.exchangeQty = exchangeQty;
        this.createdAt = createdAt;
    }

    public static ExchangeItemJpaEntity create(
            Long id, String exchangeClaimId, Long orderItemId, int exchangeQty, Instant createdAt) {
        return new ExchangeItemJpaEntity(id, exchangeClaimId, orderItemId, exchangeQty, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getExchangeClaimId() {
        return exchangeClaimId;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public int getExchangeQty() {
        return exchangeQty;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
