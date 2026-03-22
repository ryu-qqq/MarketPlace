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
 * <p>레거시 DB의 option_group 테이블 매핑. product_group_id 포함.
 */
@Entity
@Table(name = "option_group")
public class LegacyOptionGroupEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_group_id")
    private Long id;

    @Column(name = "PRODUCT_GROUP_ID")
    private Long productGroupId;

    @Column(name = "OPTION_NAME")
    private String optionName;

    @Column(name = "delete_yn")
    private String deleteYn;

    protected LegacyOptionGroupEntity() {}

    private LegacyOptionGroupEntity(Long id, Long productGroupId, String optionName, String deleteYn) {
        this.id = id;
        this.productGroupId = productGroupId;
        this.optionName = optionName;
        this.deleteYn = deleteYn;
    }

    public static LegacyOptionGroupEntity create(long productGroupId, String optionName) {
        return new LegacyOptionGroupEntity(null, productGroupId, optionName, "N");
    }

    public static LegacyOptionGroupEntity create(
            Long id, long productGroupId, String optionName, String deleteYn) {
        return new LegacyOptionGroupEntity(id, productGroupId, optionName, deleteYn);
    }

    public Long getId() {
        return id;
    }

    public Long getProductGroupId() {
        return productGroupId;
    }

    public String getOptionName() {
        return optionName;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
