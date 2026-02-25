package com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate;

import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;

/**
 * 레거시(세토프) 옵션 상세 Aggregate Root.
 *
 * <p>세토프 DB의 option_detail 테이블에 대응합니다.
 */
public class LegacyOptionDetail {

    private final LegacyOptionDetailId id;
    private final LegacyOptionGroupId optionGroupId;
    private String optionValue;

    private LegacyOptionDetail(
            LegacyOptionDetailId id, LegacyOptionGroupId optionGroupId, String optionValue) {
        this.id = id;
        this.optionGroupId = optionGroupId;
        this.optionValue = optionValue;
    }

    /** 신규 레거시 옵션 상세 생성. */
    public static LegacyOptionDetail forNew(LegacyOptionGroupId optionGroupId, String optionValue) {
        return new LegacyOptionDetail(LegacyOptionDetailId.forNew(), optionGroupId, optionValue);
    }

    /** DB에서 복원. */
    public static LegacyOptionDetail reconstitute(Long id, Long optionGroupId, String optionValue) {
        return new LegacyOptionDetail(
                LegacyOptionDetailId.of(id), LegacyOptionGroupId.of(optionGroupId), optionValue);
    }

    public LegacyOptionDetailId id() {
        return id;
    }

    public Long idValue() {
        return id.value();
    }

    public LegacyOptionGroupId optionGroupId() {
        return optionGroupId;
    }

    public Long optionGroupIdValue() {
        return optionGroupId.value();
    }

    public String optionValue() {
        return optionValue;
    }
}
