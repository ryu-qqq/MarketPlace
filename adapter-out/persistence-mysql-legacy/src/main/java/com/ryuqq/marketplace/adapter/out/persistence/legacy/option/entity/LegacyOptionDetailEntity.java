package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOptionDetailEntity - 레거시 옵션 상세 엔티티.
 *
 * <p>레거시 DB의 option_detail 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "option_detail")
public class LegacyOptionDetailEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_detail_id")
    private Long id;

    @Column(name = "OPTION_GROUP_ID")
    private Long optionGroupId;

    @Column(name = "OPTION_VALUE")
    private String optionValue;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOptionDetailEntity() {}

    private LegacyOptionDetailEntity(Long optionGroupId, String optionValue) {
        this.optionGroupId = optionGroupId;
        this.optionValue = optionValue;
        this.deleteYn = "N";
    }

    public static LegacyOptionDetailEntity create(long optionGroupId, String optionValue) {
        return new LegacyOptionDetailEntity(optionGroupId, optionValue);
    }

    public Long getId() {
        return id;
    }

    public Long getOptionGroupId() {
        return optionGroupId;
    }

    public String getOptionValue() {
        return optionValue;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
