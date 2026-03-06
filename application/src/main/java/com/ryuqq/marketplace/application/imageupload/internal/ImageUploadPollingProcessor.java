package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadStatusResponse;
import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 Outbox 폴링 처리기.
 *
 * <p>PROCESSING 상태의 Outbox를 폴링하여 다운로드 결과를 확인합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>FileStorageManager를 통해 다운로드 태스크 상태 조회
 *   <li>COMPLETED: ImageUploadCompletionCoordinator를 통해 ImageVariant 생성 + Outbox 완료
 *   <li>FAILED: 재시도 가능하면 PENDING, 아니면 FAILED
 *   <li>PROCESSING/PENDING: no-op (다음 폴링 주기에 재확인)
 * </ol>
 */
@Component
public class ImageUploadPollingProcessor {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadPollingProcessor.class);

    private final ImageUploadOutboxCommandManager outboxCommandManager;
    private final FileStorageManager fileStorageManager;
    private final ImageUploadCompletionCoordinator completionCoordinator;

    public ImageUploadPollingProcessor(
            ImageUploadOutboxCommandManager outboxCommandManager,
            FileStorageManager fileStorageManager,
            ImageUploadCompletionCoordinator completionCoordinator) {
        this.outboxCommandManager = outboxCommandManager;
        this.fileStorageManager = fileStorageManager;
        this.completionCoordinator = completionCoordinator;
    }

    /**
     * 단일 Outbox를 폴링합니다 (PROCESSING → COMPLETED/FAILED).
     *
     * @param outbox 폴링할 Outbox
     * @return 처리 완료(COMPLETED/FAILED) 여부. PROCESSING 상태 유지 시 false
     */
    public boolean pollOutbox(ImageUploadOutbox outbox) {
        try {
            ExternalDownloadStatusResponse response =
                    fileStorageManager.getDownloadTaskStatus(outbox.downloadTaskId());

            if (response.isCompleted()) {
                return handleCompleted(outbox, response);
            } else if (response.isFailed()) {
                return handleFailed(outbox, response.errorMessage());
            }

            return false;

        } catch (ExternalServiceUnavailableException e) {
            log.warn(
                    "FileFlow 서비스 일시 장애 (폴링 skip): outboxId={}, downloadTaskId={}, error={}",
                    outbox.idValue(),
                    outbox.downloadTaskId(),
                    e.getMessage());
            return false;

        } catch (Exception e) {
            log.error(
                    "이미지 업로드 폴링 중 예외 발생: outboxId={}, downloadTaskId={}, error={}",
                    outbox.idValue(),
                    outbox.downloadTaskId(),
                    e.getMessage(),
                    e);

            outbox.recordFailure(true, e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
            return false;
        }
    }

    private boolean handleCompleted(
            ImageUploadOutbox outbox, ExternalDownloadStatusResponse response) {
        log.info(
                "이미지 업로드 완료: sourceType={}, sourceId={}, newCdnUrl={}",
                outbox.sourceType(),
                outbox.sourceId(),
                response.newCdnUrl());

        completionCoordinator.complete(
                outbox, response.newCdnUrl(), response.fileAssetId(), Instant.now());
        return true;
    }

    private boolean handleFailed(ImageUploadOutbox outbox, String errorMessage) {
        log.warn(
                "이미지 업로드 실패 (재시도 예정): sourceType={}, sourceId={}, error={}",
                outbox.sourceType(),
                outbox.sourceId(),
                errorMessage);

        outbox.recordFailure(true, errorMessage, Instant.now());
        outboxCommandManager.persist(outbox);
        return false;
    }
}
