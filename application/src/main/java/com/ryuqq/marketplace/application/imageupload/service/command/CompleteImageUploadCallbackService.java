package com.ryuqq.marketplace.application.imageupload.service.command;

import com.ryuqq.marketplace.application.imageupload.dto.command.CompleteImageUploadCallbackCommand;
import com.ryuqq.marketplace.application.imageupload.internal.ImageUploadCompletionCoordinator;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxReadManager;
import com.ryuqq.marketplace.application.imageupload.port.in.command.CompleteImageUploadCallbackUseCase;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import java.time.Instant;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

/**
 * 이미지 업로드 콜백 완료 서비스.
 *
 * <p>FileFlow에서 다운로드 완료 시 콜백으로 호출됩니다. downloadTaskId로 Outbox를 조회하여 완료/실패 처리합니다.
 */
@Service
public class CompleteImageUploadCallbackService implements CompleteImageUploadCallbackUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(CompleteImageUploadCallbackService.class);
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String HTTP_400 = "400";
    private static final String HTTP_403 = "403";
    private static final String HTTP_404 = "404";
    private static final String HTTP_410 = "410";

    private final ImageUploadOutboxReadManager outboxReadManager;
    private final ImageUploadOutboxCommandManager outboxCommandManager;
    private final ImageUploadCompletionCoordinator completionCoordinator;
    private final String cdnDomain;

    public CompleteImageUploadCallbackService(
            ImageUploadOutboxReadManager outboxReadManager,
            ImageUploadOutboxCommandManager outboxCommandManager,
            ImageUploadCompletionCoordinator completionCoordinator,
            @org.springframework.beans.factory.annotation.Value("${fileflow.cdn-domain:}")
                    String cdnDomain) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.completionCoordinator = completionCoordinator;
        this.cdnDomain = cdnDomain;
    }

    @Override
    public void execute(CompleteImageUploadCallbackCommand command) {
        Optional<ImageUploadOutbox> optionalOutbox =
                outboxReadManager.findProcessingByDownloadTaskId(command.downloadTaskId());

        if (optionalOutbox.isEmpty()) {
            log.warn(
                    "콜백 수신했으나 PROCESSING 상태의 Outbox를 찾을 수 없음: downloadTaskId={}",
                    command.downloadTaskId());
            return;
        }

        ImageUploadOutbox outbox = optionalOutbox.get();
        Instant now = Instant.now();

        try {
            if (STATUS_COMPLETED.equals(command.status())) {
                String newCdnUrl = buildCdnUrl(command.s3Key());
                log.info(
                        "이미지 업로드 콜백 완료: sourceType={}, sourceId={}, newCdnUrl={}",
                        outbox.sourceType(),
                        outbox.sourceId(),
                        newCdnUrl);
                completionCoordinator.complete(outbox, newCdnUrl, command.assetId(), now);
            } else {
                boolean canRetry = isRetryableError(command.lastError());
                log.warn(
                        "이미지 업로드 콜백 실패: sourceType={}, sourceId={}, canRetry={}, error={}",
                        outbox.sourceType(),
                        outbox.sourceId(),
                        canRetry,
                        command.lastError());
                outbox.recordFailure(canRetry, command.lastError(), now);
                outboxCommandManager.persist(outbox);
            }
        } catch (OptimisticLockingFailureException e) {
            log.warn(
                    "콜백 처리 중 동시 수정 감지 (이미 처리된 것으로 간주): downloadTaskId={}, sourceType={},"
                            + " sourceId={}",
                    command.downloadTaskId(),
                    outbox.sourceType(),
                    outbox.sourceId());
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

    private String buildCdnUrl(String s3Key) {
        if (s3Key == null || cdnDomain == null || cdnDomain.isBlank()) {
            return null;
        }
        return "https://" + cdnDomain + "/" + s3Key;
    }
}
