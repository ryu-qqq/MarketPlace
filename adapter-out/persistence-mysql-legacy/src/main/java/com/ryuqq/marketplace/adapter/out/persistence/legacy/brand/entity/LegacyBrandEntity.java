package com.ryuqq.marketplace.adapter.out.persistence.legacy.brand.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyBrandEntity - 레거시 브랜드 엔티티.
 *
 * <p>레거시 DB의 brand 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Entity
@Table(name = "brand")
public class LegacyBrandEntity extends LegacyBaseEntity {

    @Id
    @Column(name = "brand_id")
    private Long id;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "brand_icon_image_url")
    private String brandIconImageUrl;

    @Column(name = "display_english_name")
    private String displayEnglishName;

    @Column(name = "display_korean_name")
    private String displayKoreanName;

    @Column(name = "display_order")
    private int displayOrder;

    @Column(name = "display_yn")
    private String displayYn;

    protected LegacyBrandEntity() {}

    public Long getId() {
        return id;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getBrandIconImageUrl() {
        return brandIconImageUrl;
    }

    public String getDisplayEnglishName() {
        return displayEnglishName;
    }

    public String getDisplayKoreanName() {
        return displayKoreanName;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public String getDisplayYn() {
        return displayYn;
    }
}
