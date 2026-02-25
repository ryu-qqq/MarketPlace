package com.ryuqq.marketplace.domain.legacy.optiongroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 레거시 옵션 그룹을 찾을 수 없을 때 발생하는 예외. */
public class LegacyOptionGroupNotFoundException extends DomainException {

    public LegacyOptionGroupNotFoundException(Long optionGroupId) {
        super(
                LegacyOptionGroupErrorCode.LEGACY_OPTION_GROUP_NOT_FOUND,
                String.format("레거시 옵션 그룹을 찾을 수 없습니다: %d", optionGroupId),
                Map.of("optionGroupId", optionGroupId));
    }
}
