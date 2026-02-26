package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOrderSnapshotProductNoticeEntity - 주문 스냅샷 고시정보 엔티티.
 *
 * <p>레거시 DB의 order_snapshot_product_notice 테이블 매핑.
 */
@Entity
@Table(name = "order_snapshot_product_notice")
public class LegacyOrderSnapshotProductNoticeEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_snapshot_product_notice_id")
    private Long id;

    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "MATERIAL")
    private String material;

    @Column(name = "COLOR")
    private String color;

    @Column(name = "SIZE")
    private String size;

    @Column(name = "MAKER")
    private String maker;

    @Column(name = "ORIGIN")
    private String origin;

    @Column(name = "WASHING_METHOD")
    private String washingMethod;

    @Column(name = "YEAR_MONTH_DAY")
    private String yearMonthDay;

    @Column(name = "ASSURANCE_STANDARD")
    private String assuranceStandard;

    @Column(name = "AS_PHONE")
    private String asPhone;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOrderSnapshotProductNoticeEntity() {}

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getMaterial() {
        return material;
    }

    public String getColor() {
        return color;
    }

    public String getSize() {
        return size;
    }

    public String getMaker() {
        return maker;
    }

    public String getOrigin() {
        return origin;
    }

    public String getWashingMethod() {
        return washingMethod;
    }

    public String getYearMonthDay() {
        return yearMonthDay;
    }

    public String getAssuranceStandard() {
        return assuranceStandard;
    }

    public String getAsPhone() {
        return asPhone;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
