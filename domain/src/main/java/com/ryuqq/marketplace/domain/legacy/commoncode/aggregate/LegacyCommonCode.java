package com.ryuqq.marketplace.domain.legacy.commoncode.aggregate;

/**
 * 레거시(세토프) 공통 코드 Value Object.
 *
 * <p>세토프 DB의 common_code 테이블에 대응합니다.
 */
public class LegacyCommonCode {

    private final Long id;
    private final Long codeGroupId;
    private final String codeDetail;
    private final String codeDetailDisplayName;
    private final Integer displayOrder;

    private LegacyCommonCode(
            Long id,
            Long codeGroupId,
            String codeDetail,
            String codeDetailDisplayName,
            Integer displayOrder) {
        this.id = id;
        this.codeGroupId = codeGroupId;
        this.codeDetail = codeDetail;
        this.codeDetailDisplayName = codeDetailDisplayName;
        this.displayOrder = displayOrder;
    }

    /** DB에서 복원. */
    public static LegacyCommonCode reconstitute(
            Long id,
            Long codeGroupId,
            String codeDetail,
            String codeDetailDisplayName,
            Integer displayOrder) {
        return new LegacyCommonCode(
                id, codeGroupId, codeDetail, codeDetailDisplayName, displayOrder);
    }

    public Long id() {
        return id;
    }

    public Long codeGroupId() {
        return codeGroupId;
    }

    public String codeDetail() {
        return codeDetail;
    }

    public String codeDetailDisplayName() {
        return codeDetailDisplayName;
    }

    public Integer displayOrder() {
        return displayOrder;
    }
}
