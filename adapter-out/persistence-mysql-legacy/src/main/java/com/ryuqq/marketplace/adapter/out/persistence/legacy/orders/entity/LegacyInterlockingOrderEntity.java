package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyInterlockingOrderEntity - 레거시 연동 주문 엔티티.
 *
 * <p>레거시 DB의 interlocking_order 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "interlocking_order")
public class LegacyInterlockingOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interlocking_order_id")
    private Long interlockingOrderId;

    @Column(name = "EXTERNAL_IDX")
    private long externalIdx;

    @Column(name = "EXTERNAL_ORDER_ID")
    private String externalOrderId;

    @Column(name = "INTERLOCKING_SITE_ID")
    private long interlockingSiteId;

    @Column(name = "SITE_NAME")
    private String siteName;

    @Column(name = "PAYMENT_ID")
    private long paymentId;

    @Column(name = "ORDER_ID")
    private long orderId;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyInterlockingOrderEntity() {}

    public static LegacyInterlockingOrderEntity create(
            long externalIdx,
            String externalOrderId,
            long interlockingSiteId,
            String siteName,
            long paymentId,
            long orderId) {
        LegacyInterlockingOrderEntity entity = new LegacyInterlockingOrderEntity();
        entity.externalIdx = externalIdx;
        entity.externalOrderId = externalOrderId;
        entity.interlockingSiteId = interlockingSiteId;
        entity.siteName = siteName;
        entity.paymentId = paymentId;
        entity.orderId = orderId;
        entity.deleteYn = "N";
        return entity;
    }

    public Long getInterlockingOrderId() {
        return interlockingOrderId;
    }

    public long getExternalIdx() {
        return externalIdx;
    }

    public String getExternalOrderId() {
        return externalOrderId;
    }

    public long getInterlockingSiteId() {
        return interlockingSiteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public long getPaymentId() {
        return paymentId;
    }

    public long getOrderId() {
        return orderId;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
