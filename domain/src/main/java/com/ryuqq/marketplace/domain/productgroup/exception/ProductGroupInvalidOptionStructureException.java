package com.ryuqq.marketplace.domain.productgroup.exception;

import com.ryuqq.marketplace.domain.common.exception.DomainException;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import java.util.Map;

/** 옵션 타입과 옵션 그룹 구조가 맞지 않을 때 발생하는 예외. */
public class ProductGroupInvalidOptionStructureException extends DomainException {

    public ProductGroupInvalidOptionStructureException(
            OptionType optionType, int expectedCount, int actualCount) {
        super(
                ProductGroupErrorCode.PRODUCT_GROUP_INVALID_OPTION_STRUCTURE,
                String.format(
                        "옵션 타입 %s는 옵션 그룹 %d개가 필요하지만 %d개가 전달되었습니다",
                        optionType, expectedCount, actualCount),
                Map.of(
                        "optionType",
                        optionType.name(),
                        "expectedCount",
                        expectedCount,
                        "actualCount",
                        actualCount));
    }
}
