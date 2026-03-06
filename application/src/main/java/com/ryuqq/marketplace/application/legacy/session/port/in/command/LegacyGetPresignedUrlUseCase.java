package com.ryuqq.marketplace.application.legacy.session.port.in.command;

import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;

/** 레거시 Presigned URL 발급 UseCase. */
public interface LegacyGetPresignedUrlUseCase {

    /**
     * Presigned URL을 발급합니다.
     *
     * @param command 발급 요청 커맨드
     * @return Presigned URL 결과
     */
    LegacyPresignedUrlResult execute(LegacyGetPresignedUrlCommand command);
}
