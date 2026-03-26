package com.ryuqq.marketplace.application.legacy.session.port.in.command;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;

/** 레거시 Presigned URL 발급 UseCase. */
public interface LegacyGetPresignedUrlUseCase {

    /**
     * Presigned URL을 발급합니다.
     *
     * @param request 표준 Presigned URL 요청
     * @return 표준 Presigned URL 결과
     */
    PresignedUrlResponse execute(PresignedUploadUrlRequest request);
}
