package com.ryuqq.marketplace.application.uploadsession.port.in.command;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;

/** Presigned URL 발급 UseCase. */
public interface GenerateUploadUrlUseCase {

    PresignedUrlResponse execute(PresignedUploadUrlRequest request);
}
