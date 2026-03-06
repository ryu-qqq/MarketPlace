package com.ryuqq.marketplace.application.imageupload.dto.command;

/**
 * FAILED 이미지 업로드 Outbox 복구 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 * @param failedAfterSeconds FAILED 후 최소 경과 시간 (초) - 즉시 복구 방지
 */
public record RecoverFailedImageUploadCommand(int batchSize, long failedAfterSeconds) {

    public static RecoverFailedImageUploadCommand of(int batchSize, long failedAfterSeconds) {
        return new RecoverFailedImageUploadCommand(batchSize, failedAfterSeconds);
    }
}
