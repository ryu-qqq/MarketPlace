package com.ryuqq.marketplace.application.uploadsession.service.command;

import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.uploadsession.dto.command.CompleteUploadSessionCommand;
import com.ryuqq.marketplace.application.uploadsession.port.in.command.CompleteUploadSessionUseCase;
import org.springframework.stereotype.Service;

/**
 * 업로드 세션 완료 처리 서비스.
 *
 * <p>FileStorageManager에 위임합니다.
 */
@Service
public class CompleteUploadSessionService implements CompleteUploadSessionUseCase {

    private final FileStorageManager fileStorageManager;

    public CompleteUploadSessionService(FileStorageManager fileStorageManager) {
        this.fileStorageManager = fileStorageManager;
    }

    @Override
    public void execute(CompleteUploadSessionCommand command) {
        fileStorageManager.completeUploadSession(
                command.sessionId(), command.fileSize(), command.etag());
    }
}
