package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import java.util.Map;

/** 옵션 그룹명이 중복될 때 발생하는 예외. */
public class OptionGroupDuplicateNameException extends DomainException {

    public OptionGroupDuplicateNameException(String duplicateName) {
        super(
                ProductGroupErrorCode.OPTION_GROUP_DUPLICATE_NAME,
                String.format("옵션 그룹명 '%s'이(가) 중복되었습니다", duplicateName),
                Map.of("duplicateName", duplicateName));
    }
}
