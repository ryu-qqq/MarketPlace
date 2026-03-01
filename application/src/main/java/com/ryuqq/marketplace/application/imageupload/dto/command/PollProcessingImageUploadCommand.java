package com.ryuqq.marketplace.application.imageupload.dto.command;

/**
 * PROCESSING 이미지 업로드 Outbox 폴링 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 */
public record PollProcessingImageUploadCommand(int batchSize) {

    public static PollProcessingImageUploadCommand of(int batchSize) {
        return new PollProcessingImageUploadCommand(batchSize);
    }
}
