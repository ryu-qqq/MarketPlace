package com.ryuqq.marketplace.application.uploadsession.service.command;

import com.ryuqq.marketplace.application.common.dto.command.PresignedUploadUrlRequest;
import com.ryuqq.marketplace.application.common.dto.response.PresignedUrlResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.uploadsession.port.in.command.GenerateUploadUrlUseCase;
import org.springframework.stereotype.Service;

/**
 * Presigned URL 발급 서비스.
 *
 * <p>FileStorageManager에 위임합니다.
 */
@Service
public class GenerateUploadUrlService implements GenerateUploadUrlUseCase {

    private final FileStorageManager fileStorageManager;

    public GenerateUploadUrlService(FileStorageManager fileStorageManager) {
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    public PresignedUrlResponse execute(PresignedUploadUrlRequest request) {
        return fileStorageManager.generateUploadUrl(request);
    }
}
