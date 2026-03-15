package com.ryuqq.marketplace.application.imagetransform.service.command;

import com.ryuqq.marketplace.application.imagetransform.dto.command.CompleteImageTransformCallbackCommand;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformCompletionBundle;
import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformCompletionFactory;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformCompletionCoordinator;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.CompleteImageTransformCallbackUseCase;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxReadManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 이미지 변환 콜백 완료 서비스.
 *
 * <p>FileFlow에서 변환 완료 시 콜백으로 호출됩니다. 조회 + Factory + Coordinator 위임만 담당합니다.
 */
@Service
public class CompleteImageTransformCallbackService
        implements CompleteImageTransformCallbackUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(CompleteImageTransformCallbackService.class);

    private final ImageTransformOutboxReadManager outboxReadManager;
    private final ImageTransformOutboxCommandManager outboxCommandManager;
    private final ImageTransformCompletionFactory completionFactory;
    private final ImageTransformCompletionCoordinator completionCoordinator;
    private final ImageTransformManager transformManager;
    private final ImageVariantSyncOutboxReadManager syncOutboxReadManager;

    public CompleteImageTransformCallbackService(
            ImageTransformOutboxReadManager outboxReadManager,
            ImageTransformOutboxCommandManager outboxCommandManager,
            ImageTransformCompletionFactory completionFactory,
            ImageTransformCompletionCoordinator completionCoordinator,
            ImageTransformManager transformManager,
            ImageVariantSyncOutboxReadManager syncOutboxReadManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.completionFactory = completionFactory;
        this.completionCoordinator = completionCoordinator;
        this.transformManager = transformManager;
        this.syncOutboxReadManager = syncOutboxReadManager;
    }

    @Override
    public void execute(CompleteImageTransformCallbackCommand command) {
        Optional<ImageTransformOutbox> optionalOutbox =
                outboxReadManager.findProcessingByTransformRequestId(command.transformRequestId());

        if (optionalOutbox.isEmpty()) {
            log.warn(
                    "콜백 수신했으나 PROCESSING 상태의 Outbox를 찾을 수 없음: transformRequestId={}",
                    command.transformRequestId());
            return;
        }

        ImageTransformOutbox outbox = optionalOutbox.get();
        Instant now = Instant.now();

        String resultCdnUrl =
                command.resultAssetId() != null
                        ? transformManager.resolveAssetCdnUrl(command.resultAssetId())
                        : null;

        boolean needsSyncOutbox =
                !syncOutboxReadManager.existsPendingBySourceImageId(outbox.sourceImageId());

        ImageTransformCompletionBundle bundle =
                completionFactory.create(outbox, command, resultCdnUrl, needsSyncOutbox, now);

        if (bundle.completed()) {
            log.info(
                    "이미지 변환 콜백 완료: sourceType={}, sourceImageId={}, variantType={}",
                    outbox.sourceType(),
                    outbox.sourceImageId(),
                    outbox.variantType());
            completionCoordinator.complete(outbox, bundle, now);
        } else {
            log.warn(
                    "이미지 변환 콜백 실패: sourceType={}, sourceImageId={}, variantType={},"
                            + " retryable={}, error={}",
                    outbox.sourceType(),
                    outbox.sourceImageId(),
                    outbox.variantType(),
                    bundle.retryable(),
                    bundle.errorMessage());
            outbox.recordFailure(bundle.retryable(), bundle.errorMessage(), now);
            outboxCommandManager.persist(outbox);
        }
    }
}
