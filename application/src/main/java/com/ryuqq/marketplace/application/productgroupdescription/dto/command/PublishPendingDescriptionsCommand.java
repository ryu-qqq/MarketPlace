package com.ryuqq.marketplace.application.productgroupdescription.dto.command;

/**
 * PUBLISH_READY 상태의 상세설명 CDN 퍼블리시 Command.
 *
 * @param batchSize 한 번에 처리할 최대 개수
 */
public record PublishPendingDescriptionsCommand(int batchSize) {

    public static PublishPendingDescriptionsCommand of(int batchSize) {
        return new PublishPendingDescriptionsCommand(batchSize);
    }
}
