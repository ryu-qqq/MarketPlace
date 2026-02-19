package com.ryuqq.marketplace.application.externalsource.port.in.query;

import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourcePageResult;

/** 외부 소스 검색 UseCase. */
public interface SearchExternalSourceUseCase {

    ExternalSourcePageResult execute(ExternalSourceSearchParams params);
}
