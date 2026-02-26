package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyOptionGroupEntity - 레거시 옵션 그룹 엔티티.
 *
 * <p>레거시 DB의 option_group 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "option_group")
public class LegacyOptionGroupEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_id")
    private Long id;

    @Column(name = "OPTION_NAME")
    private String optionName;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOptionGroupEntity() {}

    private LegacyOptionGroupEntity(String optionName) {
        this.optionName = optionName;
        this.deleteYn = "N";
    }

    public static LegacyOptionGroupEntity create(String optionName) {
        return new LegacyOptionGroupEntity(optionName);
    }

    public Long getId() {
        return id;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
