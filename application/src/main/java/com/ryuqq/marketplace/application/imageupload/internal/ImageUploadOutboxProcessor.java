package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.application.common.dto.response.ExternalDownloadResponse;
import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 이미지 업로드 Outbox 처리기.
 *
 * <p>스케줄러에서 호출됩니다.
 *
 * <p><strong>트랜잭션 전략</strong>:
 *
 * <ul>
 *   <li>PROCESSING 상태 변경: 별도 트랜잭션 (외부 API 호출 전 커밋 필요)
 *   <li>실패 시 상태 변경: 별도 트랜잭션 (실패 상태 즉시 커밋 필요)
 *   <li>성공 시 완료 처리: ImageUploadCompletionFacade를 통해 원자적 처리
 * </ul>
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>PROCESSING 상태로 변경 (다른 프로세스와 충돌 방지)
 *   <li>BundleFactory로 다운로드 요청 Bundle 생성
 *   <li>FileStorageManager를 통해 외부 URL에서 S3 업로드
 *   <li>성공 시: 이미지 uploaded_url 업데이트 + Outbox COMPLETED (Facade를 통해 원자적 처리)
 *   <li>실패 시: 재시도 가능하면 PENDING, 아니면 FAILED
 * </ol>
 */
@Component
public class ImageUploadOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(ImageUploadOutboxProcessor.class);

    private final ImageUploadOutboxCommandManager outboxCommandManager;
    private final ImageUploadCompletionFacade completionFacade;
    private final FileStorageManager fileStorageManager;
    private final ImageUploadProcessBundleFactory bundleFactory;

    public ImageUploadOutboxProcessor(
            ImageUploadOutboxCommandManager outboxCommandManager,
            ImageUploadCompletionFacade completionFacade,
            FileStorageManager fileStorageManager,
            ImageUploadProcessBundleFactory bundleFactory) {
        this.outboxCommandManager = outboxCommandManager;
        this.completionFacade = completionFacade;
        this.fileStorageManager = fileStorageManager;
        this.bundleFactory = bundleFactory;
    }

    /**
     * 단일 Outbox를 처리합니다.
     *
     * @param outbox 처리할 Outbox
     * @return 처리 성공 여부
     */
    public boolean processOutbox(ImageUploadOutbox outbox) {
        Instant now = Instant.now();

        try {
            outbox.startProcessing(now);
            outboxCommandManager.persist(outbox);

            ImageUploadProcessBundle bundle = bundleFactory.create(outbox, now);
            ExternalDownloadResponse response =
                    fileStorageManager.downloadFromExternalUrl(bundle.downloadRequest());

            if (response.success()) {
                return handleSuccess(bundle, response.newCdnUrl());
            } else {
                return handleFailure(bundle, response.errorMessage());
            }

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

    private boolean handleSuccess(ImageUploadProcessBundle bundle, String newCdnUrl) {
        log.info(
                "이미지 업로드 성공: sourceType={}, sourceId={}, newCdnUrl={}",
                bundle.outbox().sourceType(),
                bundle.outbox().sourceId(),
                newCdnUrl);

        completionFacade.complete(bundle.outbox(), newCdnUrl, bundle.processedAt());
        return true;
    }

    private boolean handleFailure(ImageUploadProcessBundle bundle, String errorMessage) {
        log.warn(
                "이미지 업로드 실패 (재시도 예정): sourceType={}, sourceId={}, error={}",
                bundle.outbox().sourceType(),
                bundle.outbox().sourceId(),
                errorMessage);

        bundle.outbox().recordFailure(true, errorMessage, bundle.processedAt());
        outboxCommandManager.persist(bundle.outbox());
        return false;
    }
}
