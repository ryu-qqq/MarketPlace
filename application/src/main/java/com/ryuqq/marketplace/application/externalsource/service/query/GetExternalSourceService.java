package com.ryuqq.marketplace.application.externalsource.service.query;

import com.ryuqq.marketplace.application.externalsource.dto.response.ExternalSourceResult;
import com.ryuqq.marketplace.application.externalsource.manager.ExternalSourceReadManager;
import com.ryuqq.marketplace.application.externalsource.port.in.query.GetExternalSourceUseCase;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import org.springframework.stereotype.Service;

/** 외부 소스 단건 조회 Service. */
@Service
public class GetExternalSourceService implements GetExternalSourceUseCase {

    private final ExternalSourceReadManager readManager;

    public GetExternalSourceService(ExternalSourceReadManager readManager) {
        this.readManager = readManager;
    }

    @Override
    public ExternalSourceResult execute(Long externalSourceId) {
        ExternalSource externalSource = readManager.getById(externalSourceId);
        return ExternalSourceResult.from(externalSource);
    }
}
