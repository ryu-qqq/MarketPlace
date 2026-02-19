package com.ryuqq.marketplace.application.productgroupdescription.port.in.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.PublishPendingDescriptionsCommand;

/**
 * PUBLISH_READY 상태의 상세설명을 CDN에 퍼블리시하는 UseCase.
 *
 * <p>이미지 URL을 CDN URL로 치환한 HTML을 CDN에 업로드하고 cdnPath를 업데이트합니다.
 */
public interface PublishPendingDescriptionsUseCase {

    SchedulerBatchProcessingResult execute(PublishPendingDescriptionsCommand command);
}
