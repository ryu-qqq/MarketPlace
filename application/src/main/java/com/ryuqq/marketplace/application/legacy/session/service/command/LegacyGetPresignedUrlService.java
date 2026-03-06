package com.ryuqq.marketplace.application.legacy.session.service.command;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.legacy.session.dto.command.LegacyGetPresignedUrlCommand;
import com.ryuqq.marketplace.application.legacy.session.dto.response.LegacyPresignedUrlResult;
import com.ryuqq.marketplace.application.legacy.session.factory.LegacyPresignedUrlRequestFactory;
import com.ryuqq.marketplace.application.legacy.session.port.in.command.LegacyGetPresignedUrlUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 Presigned URL 발급 서비스.
 *
 * <p>Factory에서 PresignedUploadUrlRequest를 생성하고, FileStorageManager에 위임합니다.
 */
@Service
public class LegacyGetPresignedUrlService implements LegacyGetPresignedUrlUseCase {

    private final LegacyPresignedUrlRequestFactory requestFactory;
    private final FileStorageManager fileStorageManager;

    public LegacyGetPresignedUrlService(
            LegacyPresignedUrlRequestFactory requestFactory,
            FileStorageManager fileStorageManager) {
        this.requestFactory = requestFactory;
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    public LegacyPresignedUrlResult execute(LegacyGetPresignedUrlCommand command) {
        PresignedUploadUrlRequest request = requestFactory.create(command);
        PresignedUrlResponse response = fileStorageManager.generateUploadUrl(request);
        return new LegacyPresignedUrlResult(
                response.sessionId(), response.presignedUrl(), response.fileKey());
    }
}
