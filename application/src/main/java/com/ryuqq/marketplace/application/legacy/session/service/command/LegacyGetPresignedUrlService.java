package com.ryuqq.marketplace.application.legacy.session.service.command;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.legacy.session.port.in.command.LegacyGetPresignedUrlUseCase;
import org.springframework.stereotype.Service;

/**
 * 레거시 Presigned URL 발급 서비스.
 *
 * <p>표준 FileStorageManager에 위임합니다.
 */
@Service
public class LegacyGetPresignedUrlService implements LegacyGetPresignedUrlUseCase {

    private final FileStorageManager fileStorageManager;

    public LegacyGetPresignedUrlService(FileStorageManager fileStorageManager) {
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    public PresignedUrlResponse execute(PresignedUploadUrlRequest request) {
        return fileStorageManager.generateUploadUrl(request);
    }
}
