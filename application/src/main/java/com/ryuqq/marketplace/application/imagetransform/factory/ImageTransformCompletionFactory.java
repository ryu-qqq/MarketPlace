package com.ryuqq.marketplace.application.imagetransform.factory;

import com.ryuqq.marketplace.application.imagetransform.dto.command.CompleteImageTransformCallbackCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformCallbackResult;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 이미지 변환 완료 시 도메인 객체 생성 Factory.
 *
 * <p>콜백 Command → 도메인 VO 변환 + 성공/실패에 따른 번들 생성을 담당합니다. 조회 로직은 포함하지 않습니다.
 */
@Component
public class ImageTransformCompletionFactory {

    /**
     * 콜백 커맨드로부터 도메인 객체 번들을 생성합니다.
     *
     * @param outbox 완료 처리할 이미지 변환 Outbox
     * @param command 콜백 커맨드
     * @param resultCdnUrl CDN URL (Service에서 resolve하여 전달)
     * @param needsSyncOutbox sync 아웃박스 생성 필요 여부
     * @param now 현재 시각
     * @return 성공/실패 구분된 번들
     */
    public ImageTransformCompletionBundle create(
            ImageTransformOutbox outbox,
            CompleteImageTransformCallbackCommand command,
            String resultCdnUrl,
            boolean needsSyncOutbox,
            Instant now) {

        ImageTransformCallbackResult callbackResult =
                ImageTransformCallbackResult.of(
                        command.status(),
                        command.resultAssetId(),
                        resultCdnUrl,
                        command.width(),
                        command.height(),
                        command.lastError());

        if (callbackResult.isCompleted()) {
            return createCompletedBundle(outbox, callbackResult, needsSyncOutbox, now);
        }

        return ImageTransformCompletionBundle.failed(
                callbackResult.errorMessage(), callbackResult.isRetryableFailure());
    }

    /**
     * 폴링 응답으로부터 성공 번들을 생성합니다 (Polling Fallback 용).
     *
     * @param outbox 완료 처리할 이미지 변환 Outbox
     * @param response 폴링 응답
     * @param needsSyncOutbox sync 아웃박스 생성 필요 여부
     * @param now 현재 시각
     * @return 성공 번들
     */
    public ImageTransformCompletionBundle createFromPolling(
            ImageTransformOutbox outbox,
            ImageTransformResponse response,
            boolean needsSyncOutbox,
            Instant now) {

        ImageTransformCallbackResult callbackResult =
                ImageTransformCallbackResult.of(
                        "COMPLETED",
                        response.resultAssetId(),
                        response.resultCdnUrl(),
                        response.width(),
                        response.height(),
                        null);

        return createCompletedBundle(outbox, callbackResult, needsSyncOutbox, now);
    }

    private ImageTransformCompletionBundle createCompletedBundle(
            ImageTransformOutbox outbox,
            ImageTransformCallbackResult result,
            boolean needsSyncOutbox,
            Instant now) {

        ImageVariant variant =
                ImageVariant.forNew(
                        outbox.sourceImageId(),
                        outbox.sourceType(),
                        outbox.variantType(),
                        ResultAssetId.of(result.resultAssetId()),
                        ImageUrl.of(result.resultCdnUrl()),
                        ImageDimension.of(result.width(), result.height()),
                        now);

        Optional<ImageVariantSyncOutbox> syncOutbox =
                needsSyncOutbox
                        ? Optional.of(
                                ImageVariantSyncOutbox.forNew(
                                        outbox.sourceImageId(), outbox.sourceType(), now))
                        : Optional.empty();

        return ImageTransformCompletionBundle.completed(variant, syncOutbox);
    }
}
