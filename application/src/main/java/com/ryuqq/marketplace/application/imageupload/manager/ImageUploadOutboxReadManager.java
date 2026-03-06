package com.ryuqq.marketplace.application.imageupload.manager;

import com.ryuqq.marketplace.application.imageupload.port.out.query.ImageUploadOutboxQueryPort;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * ImageUploadOutbox Read Manager.
 *
 * <p>이미지 업로드 Outbox 조회를 위한 매니저입니다.
 */
@Component
public class ImageUploadOutboxReadManager {

    private final ImageUploadOutboxQueryPort queryPort;

    public ImageUploadOutboxReadManager(ImageUploadOutboxQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional
    public List<ImageUploadOutbox> findPendingOutboxesForRetry(Instant beforeTime, int limit) {
        return queryPort.findPendingOutboxesForRetry(beforeTime, limit);
    }

    @Transactional
    public List<ImageUploadOutbox> findProcessingTimeoutOutboxes(
            Instant timeoutThreshold, int limit) {
        return queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit);
    }

    @Transactional(readOnly = true)
    public List<ImageUploadOutbox> findBySourceIdsAndSourceType(
            List<Long> sourceIds, ImageSourceType sourceType) {
        if (sourceIds == null || sourceIds.isEmpty()) {
            return List.of();
        }
        return queryPort.findBySourceIdsAndSourceType(sourceIds, sourceType);
    }

    @Transactional
    public List<ImageUploadOutbox> findProcessingOutboxes(int limit) {
        return queryPort.findProcessingOutboxes(limit);
    }

    @Transactional
    public List<ImageUploadOutbox> findRecoverableFailedOutboxes(Instant failedBefore, int limit) {
        return queryPort.findRecoverableFailedOutboxes(failedBefore, limit);
    }

    @Transactional(readOnly = true)
    public Optional<ImageUploadOutbox> findProcessingByDownloadTaskId(String downloadTaskId) {
        return queryPort.findProcessingByDownloadTaskId(downloadTaskId);
    }
}
