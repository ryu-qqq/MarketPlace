package com.ryuqq.marketplace.application.imagetransform.internal;

import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformCompletionBundle;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformCompletionFactory;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxReadManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 이미지 변환 Outbox 폴링 처리기.
 *
 * <p>PROCESSING 상태의 Outbox를 폴링하여 변환 결과를 확인합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>TransformClient를 통해 변환 상태 조회
 *   <li>COMPLETED: ImageTransformCompletionCoordinator를 통해 ImageVariant 생성 + Outbox 완료
 *   <li>FAILED: 재시도 가능하면 PENDING, 아니면 FAILED
 *   <li>PROCESSING/PENDING: no-op (다음 폴링 주기에 재확인)
 * </ol>
 */
@Component
public class ImageTransformPollingProcessor {

    private static final Logger log = LoggerFactory.getLogger(ImageTransformPollingProcessor.class);

    private final ImageTransformOutboxCommandManager outboxCommandManager;
    private final ImageTransformManager transformManager;
    private final ImageTransformCompletionFactory completionFactory;
    private final ImageTransformCompletionCoordinator completionCoordinator;
    private final ImageVariantSyncOutboxReadManager syncOutboxReadManager;

    public ImageTransformPollingProcessor(
            ImageTransformOutboxCommandManager outboxCommandManager,
            ImageTransformManager transformManager,
            ImageTransformCompletionFactory completionFactory,
            ImageTransformCompletionCoordinator completionCoordinator,
            ImageVariantSyncOutboxReadManager syncOutboxReadManager) {
        this.outboxCommandManager = outboxCommandManager;
        this.transformManager = transformManager;
        this.completionFactory = completionFactory;
        this.syncOutboxReadManager = syncOutboxReadManager;
        this.completionCoordinator = completionCoordinator;
    }

    /**
     * 단일 Outbox를 폴링합니다 (PROCESSING → COMPLETED/FAILED).
     *
     * @param outbox 폴링할 Outbox
     * @return 처리 완료(COMPLETED/FAILED) 여부. PROCESSING 상태 유지 시 false
     */
    public boolean pollOutbox(ImageTransformOutbox outbox) {
        try {
            ImageTransformResponse response =
                    transformManager.getTransformRequest(outbox.transformRequestId());

            if (response.isCompleted()) {
                return handleCompleted(outbox, response);
            } else if (response.isFailed()) {
                return handleFailed(outbox);
            }

            return false;

        } catch (Exception e) {
            log.error(
                    "이미지 변환 폴링 중 예외 발생: outboxId={}, transformRequestId={}, error={}",
                    outbox.idValue(),
                    outbox.transformRequestId(),
                    e.getMessage(),
                    e);

            outbox.recordFailure(true, e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
            return false;
        }
    }

    private boolean handleCompleted(ImageTransformOutbox outbox, ImageTransformResponse response) {
        log.info(
                "이미지 변환 완료: sourceType={}, sourceImageId={}, variantType={}, resultCdnUrl={}",
                outbox.sourceType(),
                outbox.sourceImageId(),
                outbox.variantType(),
                response.resultCdnUrl());

        Instant now = Instant.now();
        boolean needsSyncOutbox =
                !syncOutboxReadManager.existsPendingBySourceImageId(outbox.sourceImageId());
        ImageTransformCompletionBundle bundle =
                completionFactory.createFromPolling(outbox, response, needsSyncOutbox, now);
        completionCoordinator.complete(outbox, bundle, now);
        return true;
    }

    private boolean handleFailed(ImageTransformOutbox outbox) {
        log.warn(
                "이미지 변환 실패: sourceType={}, sourceImageId={}, variantType={}",
                outbox.sourceType(),
                outbox.sourceImageId(),
                outbox.variantType());

        outbox.recordFailure(true, "FileFlow 변환 실패", Instant.now());
        outboxCommandManager.persist(outbox);
        return false;
    }
}
