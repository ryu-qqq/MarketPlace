package com.ryuqq.marketplace.application.imagetransform.internal;

import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformManager;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 이미지 변환 Outbox 처리기.
 *
 * <p>PENDING 상태의 Outbox를 처리하여 PROCESSING 상태로 변경합니다.
 *
 * <p><strong>처리 흐름</strong>:
 *
 * <ol>
 *   <li>TransformManager를 통해 변환 요청 생성 (외부 API 호출)
 *   <li>성공 시: PROCESSING 상태 + transformRequestId 설정 후 1회 persist
 *   <li>실패 시: 재시도 가능하면 PENDING, 아니면 FAILED
 * </ol>
 */
@Component
public class ImageTransformOutboxProcessor {

    private static final Logger log = LoggerFactory.getLogger(ImageTransformOutboxProcessor.class);

    private static final String CALLBACK_PATH = "/api/v1/market/public/image-transform/callback";

    private final ImageTransformOutboxCommandManager outboxCommandManager;
    private final ImageTransformManager transformManager;
    private final String callbackUrl;

    public ImageTransformOutboxProcessor(
            ImageTransformOutboxCommandManager outboxCommandManager,
            ImageTransformManager transformManager,
            @Value("${fileflow.callback-base-url:}") String callbackBaseUrl) {
        this.outboxCommandManager = outboxCommandManager;
        this.transformManager = transformManager;
        this.callbackUrl =
                (callbackBaseUrl != null && !callbackBaseUrl.isBlank())
                        ? callbackBaseUrl + CALLBACK_PATH
                        : null;
    }

    /**
     * 단일 Outbox를 처리합니다 (PENDING → PROCESSING).
     *
     * <p>API 호출 후 1회만 persist하는 단순 패턴. API 실패 시 DB는 원래 PENDING 상태를 유지하므로 좀비 레코드가 생기지 않습니다.
     *
     * @param outbox 처리할 Outbox
     * @return 처리 성공 여부
     */
    public boolean processOutbox(ImageTransformOutbox outbox) {
        try {
            ImageTransformResponse response =
                    transformManager.createTransformRequest(
                            outbox.uploadedUrlValue(),
                            outbox.variantType(),
                            outbox.fileAssetId(),
                            callbackUrl);

            log.info(
                    "이미지 변환 요청 생성 성공: sourceType={}, sourceImageId={}, variantType={},"
                            + " transformRequestId={}",
                    outbox.sourceType(),
                    outbox.sourceImageId(),
                    outbox.variantType(),
                    response.transformRequestId());

            outbox.startProcessing(Instant.now(), response.transformRequestId());
            outboxCommandManager.persist(outbox);
            return true;

        } catch (Exception e) {
            log.error(
                    "이미지 변환 Outbox 처리 중 예외 발생: outboxId={}, sourceType={}, sourceImageId={},"
                            + " variantType={}, error={}",
                    outbox.idValue(),
                    outbox.sourceType(),
                    outbox.sourceImageId(),
                    outbox.variantType(),
                    e.getMessage(),
                    e);

            persistFailure(outbox, e.getMessage());
            return false;
        }
    }

    private void persistFailure(ImageTransformOutbox outbox, String errorMessage) {
        try {
            outbox.recordFailure(true, errorMessage, Instant.now());
            outboxCommandManager.persist(outbox);
        } catch (Exception persistEx) {
            log.warn(
                    "Outbox 실패 상태 저장 실패: outboxId={}, error={}",
                    outbox.idValue(),
                    persistEx.getMessage());
        }
    }
}
