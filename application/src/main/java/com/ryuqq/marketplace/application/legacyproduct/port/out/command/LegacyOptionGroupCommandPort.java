package com.ryuqq.marketplace.application.legacyproduct.port.out.command;

import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;

/** 세토프 DB option_group 테이블 커맨드 Port. */
public interface LegacyOptionGroupCommandPort {

    Long persist(LegacyOptionGroup optionGroup);
}
