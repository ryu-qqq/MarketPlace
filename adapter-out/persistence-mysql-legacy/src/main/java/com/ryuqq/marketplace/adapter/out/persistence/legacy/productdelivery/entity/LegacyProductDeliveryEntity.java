package com.ryuqq.marketplace.adapter.out.persistence.legacy.productdelivery.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductDeliveryEntity - 레거시 상품 배송 정보 엔티티.
 *
 * <p>레거시 DB의 product_delivery 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_delivery")
public class LegacyProductDeliveryEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "product_group_id")
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

    protected LegacyProductDeliveryEntity() {}

    private LegacyProductDeliveryEntity(
            Long productGroupId,
            String deliveryArea,
            Integer deliveryFee,
            Integer deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            Integer returnChargeDomestic,
            String returnExchangeAreaDomestic) {
        this.productGroupId = productGroupId;
        this.deliveryArea = deliveryArea;
        this.deliveryFee = deliveryFee;
        this.deliveryPeriodAverage = deliveryPeriodAverage;
        this.returnMethodDomestic = returnMethodDomestic;
        this.returnCourierDomestic = returnCourierDomestic;
        this.returnChargeDomestic = returnChargeDomestic;
        this.returnExchangeAreaDomestic = returnExchangeAreaDomestic;
        this.deleteYn = "N";
    }

    public static LegacyProductDeliveryEntity create(
            long productGroupId,
            String deliveryArea,
            long deliveryFee,
            int deliveryPeriodAverage,
            String returnMethodDomestic,
            String returnCourierDomestic,
            int returnChargeDomestic,
            String returnExchangeAreaDomestic) {
        return new LegacyProductDeliveryEntity(
                productGroupId,
                deliveryArea,
                (int) deliveryFee,
                deliveryPeriodAverage,
                returnMethodDomestic,
                returnCourierDomestic,
                returnChargeDomestic,
                returnExchangeAreaDomestic);
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
