package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 옵션 그룹에 옵션 값이 없을 때 발생하는 예외. */
public class OptionGroupEmptyValuesException extends DomainException {

    public OptionGroupEmptyValuesException(String optionGroupName) {
        super(
                ProductGroupErrorCode.OPTION_GROUP_EMPTY_VALUES,
                String.format("옵션 그룹 '%s'에 옵션 값이 없습니다", optionGroupName),
                Map.of("optionGroupName", optionGroupName));
    }
}
