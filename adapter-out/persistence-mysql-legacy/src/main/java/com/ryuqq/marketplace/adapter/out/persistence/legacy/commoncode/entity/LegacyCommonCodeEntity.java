package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.common.entity.LegacyBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * LegacyCommonCodeEntity - 레거시 공통 코드 엔티티.
 *
 * <p>레거시 DB의 common_code 테이블 매핑.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Entity
@Table(name = "common_code")
public class LegacyCommonCodeEntity extends LegacyBaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long id;

    @Column(name = "CODE_GROUP_ID")
    private Long codeGroupId;

    @Column(name = "CODE_DETAIL")
    private String codeDetail;

    @Column(name = "CODE_DETAIL_DISPLAY_NAME")
    private String codeDetailDisplayName;

    @Column(name = "DISPLAY_ORDER")
    private Integer displayOrder;

    @Column(name = "DELETE_YN")
    private String deleteYn;

    protected LegacyCommonCodeEntity() {}

    public Long getId() {
        return id;
    }

    public Long getCodeGroupId() {
        return codeGroupId;
    }

    public String getCodeDetail() {
        return codeDetail;
    }

    public String getCodeDetailDisplayName() {
        return codeDetailDisplayName;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public String getDeleteYn() {
        return deleteYn;
    }
}
