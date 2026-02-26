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

    private LegacyCommonCodeEntity(
            Long id,
            Long codeGroupId,
            String codeDetail,
            String codeDetailDisplayName,
            Integer displayOrder,
            String deleteYn) {
        this.id = id;
        this.codeGroupId = codeGroupId;
        this.codeDetail = codeDetail;
        this.codeDetailDisplayName = codeDetailDisplayName;
        this.displayOrder = displayOrder;
        this.deleteYn = deleteYn;
    }

    /** 테스트 및 Mapper에서 Entity 생성 시 사용하는 팩토리 메서드. */
    public static LegacyCommonCodeEntity create(
            Long id,
            Long codeGroupId,
            String codeDetail,
            String codeDetailDisplayName,
            Integer displayOrder,
            String deleteYn) {
        return new LegacyCommonCodeEntity(
                id, codeGroupId, codeDetail, codeDetailDisplayName, displayOrder, deleteYn);
    }

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
