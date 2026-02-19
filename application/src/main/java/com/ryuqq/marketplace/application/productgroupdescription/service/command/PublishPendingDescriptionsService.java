package com.ryuqq.marketplace.application.productgroupdescription.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.PublishPendingDescriptionsCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionPublishCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionReadManager;
import com.ryuqq.marketplace.application.productgroupdescription.port.in.command.PublishPendingDescriptionsUseCase;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * PUBLISH_READY 상태의 상세설명을 CDN에 퍼블리시하는 서비스.
 *
 * <p>이미지 원본 URL → CDN URL 치환 후 HTML을 CDN에 업로드합니다.
 */
@Service
public class PublishPendingDescriptionsService implements PublishPendingDescriptionsUseCase {

    private final ProductGroupDescriptionReadManager descriptionReadManager;
    private final DescriptionPublishCoordinator descriptionPublishCoordinator;

    public PublishPendingDescriptionsService(
            ProductGroupDescriptionReadManager descriptionReadManager,
            DescriptionPublishCoordinator descriptionPublishCoordinator) {
        this.descriptionReadManager = descriptionReadManager;
        this.descriptionPublishCoordinator = descriptionPublishCoordinator;
    }

    @Override
    public SchedulerBatchProcessingResult execute(PublishPendingDescriptionsCommand command) {
        List<ProductGroupDescription> descriptions =
                descriptionReadManager.findPublishReady(command.batchSize());

        int total = descriptions.size();
        int successCount = 0;
        int failedCount = 0;

        for (ProductGroupDescription description : descriptions) {
            if (descriptionPublishCoordinator.publish(description)) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
