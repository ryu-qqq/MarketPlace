package com.ryuqq.marketplace.domain.legacy.optiondetail.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 레거시 옵션 상세를 찾을 수 없을 때 발생하는 예외. */
public class LegacyOptionDetailNotFoundException extends DomainException {

    public LegacyOptionDetailNotFoundException(Long optionDetailId) {
        super(
                LegacyOptionDetailErrorCode.LEGACY_OPTION_DETAIL_NOT_FOUND,
                String.format("레거시 옵션 상세를 찾을 수 없습니다: %d", optionDetailId),
                Map.of("optionDetailId", optionDetailId));
    }
}
