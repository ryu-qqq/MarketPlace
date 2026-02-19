package com.ryuqq.marketplace.application.externalsource.port.in.query;

import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;

/** 외부 소스 단건 조회 UseCase. */
public interface GetExternalSourceUseCase {

    ExternalSourceResult execute(Long externalSourceId);
}
