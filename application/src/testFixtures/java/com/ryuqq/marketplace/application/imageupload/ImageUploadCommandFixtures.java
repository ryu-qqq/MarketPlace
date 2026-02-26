package com.ryuqq.marketplace.application.imageupload;

import com.ryuqq.marketplace.application.imageupload.dto.command.ProcessPendingImageUploadCommand;
import com.ryuqq.marketplace.application.imageupload.dto.command.RecoverTimeoutImageUploadCommand;

/**
 * ImageUpload Application Command 테스트 Fixtures.
 *
 * <p>ImageUpload 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class ImageUploadCommandFixtures {

    private ImageUploadCommandFixtures() {}

    // ===== 기본 상수 =====
    public static final int DEFAULT_BATCH_SIZE = 10;
    public static final int DEFAULT_DELAY_SECONDS = 5;
    public static final long DEFAULT_TIMEOUT_SECONDS = 300L;

    // ===== ProcessPendingImageUploadCommand =====

    public static ProcessPendingImageUploadCommand processPendingCommand() {
        return ProcessPendingImageUploadCommand.of(DEFAULT_BATCH_SIZE, DEFAULT_DELAY_SECONDS);
    }

    public static ProcessPendingImageUploadCommand processPendingCommand(
            int batchSize, int delaySeconds) {
        return ProcessPendingImageUploadCommand.of(batchSize, delaySeconds);
    }

    // ===== RecoverTimeoutImageUploadCommand =====

    public static RecoverTimeoutImageUploadCommand recoverTimeoutCommand() {
        return RecoverTimeoutImageUploadCommand.of(DEFAULT_BATCH_SIZE, DEFAULT_TIMEOUT_SECONDS);
    }

    public static RecoverTimeoutImageUploadCommand recoverTimeoutCommand(
            int batchSize, long timeoutSeconds) {
        return RecoverTimeoutImageUploadCommand.of(batchSize, timeoutSeconds);
    }
}
