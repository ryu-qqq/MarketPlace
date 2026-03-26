package com.ryuqq.marketplace.application.imagetransform.factory;

import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import java.util.Optional;

/**
 * 이미지 변환 완료 시 생성되는 도메인 객체 번들.
 *
 * <p>성공(completed)과 실패(failed)를 플랫하게 표현합니다.
 *
 * @param completed 성공 여부
 * @param variant 생성할 ImageVariant (성공 시)
 * @param syncOutbox 생성할 Variant Sync Outbox (성공 + PENDING 없을 때)
 * @param errorMessage 에러 메시지 (실패 시)
 * @param retryable 재시도 가능 여부 (실패 시)
 */
public record ImageTransformCompletionBundle(
        boolean completed,
        ImageVariant variant,
        Optional<ImageVariantSyncOutbox> syncOutbox,
        String errorMessage,
        boolean retryable) {

    /** 성공 번들 생성. */
    public static ImageTransformCompletionBundle completed(
            ImageVariant variant, Optional<ImageVariantSyncOutbox> syncOutbox) {
        return new ImageTransformCompletionBundle(true, variant, syncOutbox, null, false);
    }

    /** 실패 번들 생성. */
    public static ImageTransformCompletionBundle failed(String errorMessage, boolean retryable) {
        return new ImageTransformCompletionBundle(
                false, null, Optional.empty(), errorMessage, retryable);
    }
}
