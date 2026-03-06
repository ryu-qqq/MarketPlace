package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.common.exception.ExternalServiceUnavailableException;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.imageupload.factory.ImageUploadProcessBundleFactory;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 Outbox 처리기 (논블로킹).
 *
 * <p>스케줄러에서 호출됩니다. FileFlow 다운로드 태스크를 생성만 하고 즉시 반환합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>BundleFactory로 다운로드 요청 Bundle 생성
 *   <li>FileStorageManager.createDownloadTask()로 다운로드 태스크 생성 (ms 단위)
 *   <li>PROCESSING 상태 + downloadTaskId 저장
 *   <li>완료 확인은 ImageUploadPollingProcessor에서 수행
 * </ol>
 */
@Component
public class ImageUploadOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadOutboxProcessor.class);

    private final ImageUploadOutboxCommandManager outboxCommandManager;
    private final FileStorageManager fileStorageManager;
    private final ImageUploadProcessBundleFactory bundleFactory;

    public ImageUploadOutboxProcessor(
            ImageUploadOutboxCommandManager outboxCommandManager,
            FileStorageManager fileStorageManager,
            ImageUploadProcessBundleFactory bundleFactory) {
        this.outboxCommandManager = outboxCommandManager;
        this.fileStorageManager = fileStorageManager;
        this.bundleFactory = bundleFactory;
    }

    /**
     * 단일 Outbox를 처리합니다 (논블로킹).
     *
     * <p>다운로드 태스크를 생성하고 PROCESSING 상태로 변경합니다.
     *
     * @param outbox 처리할 Outbox
     * @return 다운로드 태스크 생성 성공 여부
     */
    public boolean processOutbox(ImageUploadOutbox outbox) {
        Instant now = Instant.now();

        try {
            ImageUploadProcessBundle bundle = bundleFactory.create(outbox, now);
            String downloadTaskId = fileStorageManager.createDownloadTask(bundle.downloadRequest());

            outbox.startProcessing(now, downloadTaskId);
            outboxCommandManager.persist(outbox);

            log.info(
                    "이미지 업로드 다운로드 태스크 생성 완료: outboxId={}, sourceType={}, sourceId={},"
                            + " downloadTaskId={}",
                    outbox.idValue(),
                    outbox.sourceType(),
                    outbox.sourceId(),
                    downloadTaskId);

            return true;

        } catch (ExternalServiceUnavailableException e) {
            log.warn(
                    "FileFlow 서비스 일시 장애 (deferRetry): outboxId={}, error={}",
                    outbox.idValue(),
                    e.getMessage());

            outbox.deferRetry(now);
            outboxCommandManager.persist(outbox);
            return false;

        } catch (Exception e) {
            log.error(
                    "이미지 업로드 Outbox 처리 중 예외 발생: outboxId={}, sourceType={}, sourceId={}, error={}",
                    outbox.idValue(),
                    outbox.sourceType(),
                    outbox.sourceId(),
                    e.getMessage(),
                    e);

            outbox.recordFailure(true, e.getMessage(), now);
            outboxCommandManager.persist(outbox);
            return false;
        }
    }
}
