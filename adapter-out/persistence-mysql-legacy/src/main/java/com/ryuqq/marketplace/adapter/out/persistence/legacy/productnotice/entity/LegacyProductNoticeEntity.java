package com.ryuqq.marketplace.adapter.out.persistence.legacy.productnotice.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyProductNoticeEntity - 레거시 상품 고시정보 엔티티.
 *
 * <p>레거시 DB의 product_notice 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "product_notice")
public class LegacyProductNoticeEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "product_group_id")
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

    protected LegacyProductNoticeEntity() {}

    private LegacyProductNoticeEntity(
            Long productGroupId,
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {
        this.productGroupId = productGroupId;
        this.material = material;
        this.color = color;
        this.size = size;
        this.maker = maker;
        this.origin = origin;
        this.washingMethod = washingMethod;
        this.yearMonthDay = yearMonthDay;
        this.assuranceStandard = assuranceStandard;
        this.asPhone = asPhone;
        this.deleteYn = "N";
    }

    public static LegacyProductNoticeEntity create(
            long productGroupId,
            String material,
            String color,
            String size,
            String maker,
            String origin,
            String washingMethod,
            String yearMonthDay,
            String assuranceStandard,
            String asPhone) {
        return new LegacyProductNoticeEntity(
                productGroupId,
                material,
                color,
                size,
                maker,
                origin,
                washingMethod,
                yearMonthDay,
                assuranceStandard,
                asPhone);
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
