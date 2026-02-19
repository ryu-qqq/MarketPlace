package com.ryuqq.marketplace.application.imagetransform;

import com.ryuqq.marketplace.application.imagetransform.dto.command.PollProcessingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.command.ProcessPendingImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RecoverTimeoutImageTransformCommand;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;

/**
 * ImageTransform Application Command 테스트 Fixtures.
 *
 * <p>ImageTransform 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageTransformCommandFixtures {

    private ImageTransformCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final int DEFAULT_BATCH_SIZE = 10;
    public static final int DEFAULT_DELAY_SECONDS = 5;
    public static final long DEFAULT_TIMEOUT_SECONDS = 300L;
    public static final long DEFAULT_PRODUCT_GROUP_ID = 1L;

    // ===== ProcessPendingImageTransformCommand =====

    public static ProcessPendingImageTransformCommand processPendingCommand() {
        return ProcessPendingImageTransformCommand.of(DEFAULT_BATCH_SIZE, DEFAULT_DELAY_SECONDS);
    }

    public static ProcessPendingImageTransformCommand processPendingCommand(
            int batchSize, int delaySeconds) {
        return ProcessPendingImageTransformCommand.of(batchSize, delaySeconds);
    }

    // ===== PollProcessingImageTransformCommand =====

    public static PollProcessingImageTransformCommand pollProcessingCommand() {
        return PollProcessingImageTransformCommand.of(DEFAULT_BATCH_SIZE);
    }

    public static PollProcessingImageTransformCommand pollProcessingCommand(int batchSize) {
        return PollProcessingImageTransformCommand.of(batchSize);
    }

    // ===== RecoverTimeoutImageTransformCommand =====

    public static RecoverTimeoutImageTransformCommand recoverTimeoutCommand() {
        return RecoverTimeoutImageTransformCommand.of(DEFAULT_BATCH_SIZE, DEFAULT_TIMEOUT_SECONDS);
    }

    public static RecoverTimeoutImageTransformCommand recoverTimeoutCommand(
            int batchSize, long timeoutSeconds) {
        return RecoverTimeoutImageTransformCommand.of(batchSize, timeoutSeconds);
    }

    // ===== RequestImageTransformCommand =====

    public static RequestImageTransformCommand requestAllVariantsCommand() {
        return RequestImageTransformCommand.allVariants(DEFAULT_PRODUCT_GROUP_ID);
    }

    public static RequestImageTransformCommand requestAllVariantsCommand(Long productGroupId) {
        return RequestImageTransformCommand.allVariants(productGroupId);
    }

    public static RequestImageTransformCommand requestSpecificVariantsCommand() {
        return RequestImageTransformCommand.of(
                DEFAULT_PRODUCT_GROUP_ID,
                List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP));
    }

    public static RequestImageTransformCommand requestSpecificVariantsCommand(
            Long productGroupId, List<ImageVariantType> variantTypes) {
        return RequestImageTransformCommand.of(productGroupId, variantTypes);
    }
}
