package com.ryuqq.marketplace.application.externalsource.port.out.command;

import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;

/** ExternalSource 저장/수정 포트. */
public interface ExternalSourceCommandPort {

    Long persist(ExternalSource externalSource);

    boolean existsByCode(String code);
}
