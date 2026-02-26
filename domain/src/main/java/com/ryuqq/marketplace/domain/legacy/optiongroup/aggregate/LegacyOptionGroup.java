package com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate;

import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.vo.LegacyOptionName;

/**
 * 레거시(세토프) 옵션 그룹 Aggregate Root.
 *
 * <p>세토프 DB의 option_group 테이블에 대응합니다.
 */
public class LegacyOptionGroup {

    private final LegacyOptionGroupId id;
    private LegacyOptionName optionName;

    private LegacyOptionGroup(LegacyOptionGroupId id, LegacyOptionName optionName) {
        this.id = id;
        this.optionName = optionName;
    }

    /** 신규 레거시 옵션 그룹 생성. */
    public static LegacyOptionGroup forNew(LegacyOptionName optionName) {
        return new LegacyOptionGroup(LegacyOptionGroupId.forNew(), optionName);
    }

    /** DB에서 복원. */
    public static LegacyOptionGroup reconstitute(Long id, LegacyOptionName optionName) {
        return new LegacyOptionGroup(LegacyOptionGroupId.of(id), optionName);
    }

    public LegacyOptionGroupId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public LegacyOptionName optionName() {
        return optionName;
    }
}
