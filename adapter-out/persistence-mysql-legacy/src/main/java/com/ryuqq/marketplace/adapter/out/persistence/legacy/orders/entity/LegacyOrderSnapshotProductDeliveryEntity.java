package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductDeliveryEntity - 주문 스냅샷 배송 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_delivery 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_delivery")
public class LegacyOrderSnapshotProductDeliveryEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_delivery_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "DELIVERY_AREA")
    private String deliveryArea;

    @Column(name = "DELIVERY_FEE")
    private Integer deliveryFee;

    @Column(name = "DELIVERY_PERIOD_AVERAGE")
    private Integer deliveryPeriodAverage;

    @Column(name = "RETURN_METHOD_DOMESTIC")
    private String returnMethodDomestic;

    @Column(name = "RETURN_COURIER_DOMESTIC")
    private String returnCourierDomestic;

    @Column(name = "RETURN_CHARGE_DOMESTIC")
    private Integer returnChargeDomestic;

    @Column(name = "RETURN_EXCHANGE_AREA_DOMESTIC")
    private String returnExchangeAreaDomestic;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductDeliveryEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getDeliveryArea() {
        return deliveryArea;
    }

    public Integer getDeliveryFee() {
        return deliveryFee;
    }

    public Integer getDeliveryPeriodAverage() {
        return deliveryPeriodAverage;
    }

    public String getReturnMethodDomestic() {
        return returnMethodDomestic;
    }

    public String getReturnCourierDomestic() {
        return returnCourierDomestic;
    }

    public Integer getReturnChargeDomestic() {
        return returnChargeDomestic;
    }

    public String getReturnExchangeAreaDomestic() {
        return returnExchangeAreaDomestic;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
