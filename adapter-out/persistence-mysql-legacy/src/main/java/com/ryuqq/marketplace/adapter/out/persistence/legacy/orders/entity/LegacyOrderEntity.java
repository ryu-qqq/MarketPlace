package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * LegacyOrderEntity - 레거시 주문 엔티티.
 *
 * <p>레거시 DB의 orders 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "orders")
public class LegacyOrderEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @Column(name = "PAYMENT_ID")
    private Long paymentId;

    @Column(name = "PRODUCT_ID")
    private Long productId;

    @Column(name = "SELLER_ID")
    private Long sellerId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ORDER_AMOUNT")
    private Long orderAmount;

    @Column(name = "ORDER_STATUS")
    private String orderStatus;

    @Column(name = "PURCHASE_CONFIRMED_DATE")
    private LocalDateTime purchaseConfirmedDate;

    @Column(name = "SETTLEMENT_DATE")
    private LocalDateTime settlementDate;

    @Column(name = "QUANTITY")
    private Integer quantity;

    @Column(name = "REVIEW_YN")
    private String reviewYn;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderEntity() {}

    public Long getId() {
        return id;
    }

    public Long getPaymentId() {
        return paymentId;
    }

    public Long getProductId() {
        return productId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getOrderAmount() {
        return orderAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public LocalDateTime getPurchaseConfirmedDate() {
        return purchaseConfirmedDate;
    }

    public LocalDateTime getSettlementDate() {
        return settlementDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getReviewYn() {
        return reviewYn;
    }

    public String getDeleteYn() {
        return deleteYn;
    }

    public void updateOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
