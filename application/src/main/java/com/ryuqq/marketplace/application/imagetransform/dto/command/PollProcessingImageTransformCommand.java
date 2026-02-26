package com.ryuqq.marketplace.application.imagetransform.dto.command;

/**
 * PROCESSING 이미지 변환 Outbox 폴링 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 */
public record PollProcessingImageTransformCommand(int batchSize) {

    public static PollProcessingImageTransformCommand of(int batchSize) {
        return new PollProcessingImageTransformCommand(batchSize);
    }
}
