package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 같은 옵션 그룹 내 옵션 값 이름이 중복될 때 발생하는 예외. */
public class OptionValueDuplicateNameException extends DomainException {

    public OptionValueDuplicateNameException(String optionGroupName, String duplicateValueName) {
        super(
                ProductGroupErrorCode.OPTION_VALUE_DUPLICATE_NAME,
                String.format(
                        "옵션 그룹 '%s' 내 옵션 값 '%s'이(가) 중복되었습니다", optionGroupName, duplicateValueName),
                Map.of(
                        "optionGroupName", optionGroupName,
                        "duplicateValueName", duplicateValueName));
    }
}
