package com.ryuqq.marketplace.application.imagetransform.service.command;

import com.ryuqq.marketplace.application.imagetransform.dto.command.CompleteImageTransformCallbackCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.internal.ImageTransformCompletionCoordinator;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxReadManager;
import com.ryuqq.marketplace.application.imagetransform.port.in.command.CompleteImageTransformCallbackUseCase;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 이미지 변환 콜백 완료 서비스.
 *
 * <p>FileFlow에서 변환 완료 시 콜백으로 호출됩니다. transformRequestId로 Outbox를 조회하여 완료/실패 처리합니다.
 */
@Service
public class CompleteImageTransformCallbackService
        implements CompleteImageTransformCallbackUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(CompleteImageTransformCallbackService.class);
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String HTTP_400 = "400";
    private static final String HTTP_403 = "403";
    private static final String HTTP_404 = "404";
    private static final String HTTP_410 = "410";

    private final ImageTransformOutboxReadManager outboxReadManager;
    private final ImageTransformOutboxCommandManager outboxCommandManager;
    private final ImageTransformCompletionCoordinator completionCoordinator;

    public CompleteImageTransformCallbackService(
            ImageTransformOutboxReadManager outboxReadManager,
            ImageTransformOutboxCommandManager outboxCommandManager,
            ImageTransformCompletionCoordinator completionCoordinator) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.completionCoordinator = completionCoordinator;
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

        if (STATUS_COMPLETED.equals(command.status())) {
            log.info(
                    "이미지 변환 콜백 완료: sourceType={}, sourceImageId={}, variantType={},"
                            + " resultCdnUrl={}",
                    outbox.sourceType(),
                    outbox.sourceImageId(),
                    outbox.variantType(),
                    command.resultCdnUrl());

            ImageTransformResponse response =
                    ImageTransformResponse.completed(
                            command.transformRequestId(),
                            command.resultAssetId(),
                            command.resultCdnUrl(),
                            command.width(),
                            command.height());
            completionCoordinator.complete(outbox, response, now);
        } else {
            boolean canRetry = isRetryableError(command.lastError());
            log.warn(
                    "이미지 변환 콜백 실패: sourceType={}, sourceImageId={}, variantType={},"
                            + " canRetry={}, error={}",
                    outbox.sourceType(),
                    outbox.sourceImageId(),
                    outbox.variantType(),
                    canRetry,
                    command.lastError());
            outbox.recordFailure(canRetry, command.lastError(), now);
            outboxCommandManager.persist(outbox);
        }
    }

    /**
     * 재시도 가능한 에러인지 판단합니다.
     *
     * <p>4xx 클라이언트 에러(400, 403, 404, 410)는 재시도해도 동일하게 실패하므로 즉시 FAILED 처리합니다. 5xx 서버 에러, 타임아웃 등은 재시도
     * 가능합니다.
     */
    private boolean isRetryableError(String errorMessage) {
        if (errorMessage == null) {
            return true;
        }
        return !errorMessage.contains(HTTP_400)
                && !errorMessage.contains(HTTP_403)
                && !errorMessage.contains(HTTP_404)
                && !errorMessage.contains(HTTP_410);
    }
}
