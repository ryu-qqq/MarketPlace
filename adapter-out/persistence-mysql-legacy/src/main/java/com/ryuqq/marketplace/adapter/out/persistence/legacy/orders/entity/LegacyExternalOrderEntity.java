package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyExternalOrderEntity - 레거시 외부 주문 엔티티.
 *
 * <p>레거시 DB의 external_order 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "external_order")
public class LegacyExternalOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "external_order_id")
    private Long externalOrderId;

    @Column(name = "SITE_ID")
    private long siteId;

    @Column(name = "PAYMENT_ID")
    private long paymentId;

    @Column(name = "ORDER_ID")
    private long orderId;

    @Column(name = "EXTERNAL_IDX")
    private long externalIdx;

    @Column(name = "EXTERNAL_ORDER_PK_ID")
    private String externalOrderPkId;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyExternalOrderEntity() {}

    public static LegacyExternalOrderEntity create(
            long siteId, long paymentId, long orderId, long externalIdx, String externalOrderPkId) {
        LegacyExternalOrderEntity entity = new LegacyExternalOrderEntity();
        entity.siteId = siteId;
        entity.paymentId = paymentId;
        entity.orderId = orderId;
        entity.externalIdx = externalIdx;
        entity.externalOrderPkId = externalOrderPkId;
        entity.deleteYn = "N";
        return entity;
    }

    public Long getExternalOrderId() {
        return externalOrderId;
    }

    public long getSiteId() {
        return siteId;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public long getOrderId() {
        return orderId;
    }

    public long getExternalIdx() {
        return externalIdx;
    }

    public String getExternalOrderPkId() {
        return externalOrderPkId;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
