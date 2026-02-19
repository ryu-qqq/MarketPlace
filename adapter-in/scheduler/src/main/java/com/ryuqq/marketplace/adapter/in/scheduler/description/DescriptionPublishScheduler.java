package com.ryuqq.marketplace.adapter.in.scheduler.description;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.PublishPendingDescriptionsCommand;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.PublishPendingDescriptionsUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 상세설명 CDN 퍼블리시 스케줄러.
 *
 * <p>PUBLISH_READY 상태의 상세설명을 CDN에 업로드합니다. 이미지 URL을 CDN URL로 치환한 HTML을 업로드하고 cdnPath를 업데이트합니다.
 *
 * @see SchedulerProperties
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.description-publish",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class DescriptionPublishScheduler {

    private final PublishPendingDescriptionsUseCase publishUseCase;
    private final SchedulerProperties.DescriptionPublish config;

    public DescriptionPublishScheduler(
            PublishPendingDescriptionsUseCase publishUseCase,
            SchedulerProperties schedulerProperties) {
        this.publishUseCase = publishUseCase;
        this.config = schedulerProperties.jobs().descriptionPublish();
    }

    /** PUBLISH_READY 상태의 상세설명을 CDN에 퍼블리시합니다. */
    @Scheduled(
            cron = "${scheduler.jobs.description-publish.cron}",
            zone = "${scheduler.jobs.description-publish.timezone}")
    @SchedulerJob("DescriptionPublish")
    public SchedulerBatchProcessingResult publishDescriptions() {
        PublishPendingDescriptionsCommand command =
                PublishPendingDescriptionsCommand.of(config.batchSize());
        return publishUseCase.execute(command);
    }
}
