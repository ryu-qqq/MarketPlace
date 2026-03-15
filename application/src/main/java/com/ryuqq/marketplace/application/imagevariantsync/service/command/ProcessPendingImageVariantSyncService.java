package com.ryuqq.marketplace.application.imagevariantsync.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantReadManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxReadManager;
import com.ryuqq.marketplace.application.imagevariantsync.port.in.command.ProcessPendingImageVariantSyncUseCase;
import com.ryuqq.marketplace.application.imagevariantsync.port.out.client.ImageVariantSyncClient;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import java.time.Instant;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * PENDING 이미지 Variant Sync Outbox 배치 처리 서비스.
 *
 * <p>PENDING Outbox를 조회하여 각각에 대해:
 *
 * <ol>
 *   <li>sourceImageId로 image_variants 조회
 *   <li>ImageVariantResult로 변환
 *   <li>ImageVariantSyncClient.syncVariants() 호출
 *   <li>성공 시 outbox.complete(), 실패 시 outbox.fail()
 *   <li>persist
 * </ol>
 */
@Service
public class ProcessPendingImageVariantSyncService
        implements ProcessPendingImageVariantSyncUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ProcessPendingImageVariantSyncService.class);

    private final ImageVariantSyncOutboxReadManager outboxReadManager;
    private final ImageVariantSyncOutboxCommandManager outboxCommandManager;
    private final ImageVariantReadManager imageVariantReadManager;
    private final ImageVariantSyncClient imageVariantSyncClient;

    public ProcessPendingImageVariantSyncService(
            ImageVariantSyncOutboxReadManager outboxReadManager,
            ImageVariantSyncOutboxCommandManager outboxCommandManager,
            ImageVariantReadManager imageVariantReadManager,
            ImageVariantSyncClient imageVariantSyncClient) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
        this.imageVariantReadManager = imageVariantReadManager;
        this.imageVariantSyncClient = imageVariantSyncClient;
    }

    @Override
    public SchedulerBatchProcessingResult execute(int batchSize) {
        List<ImageVariantSyncOutbox> outboxes = outboxReadManager.findPendingOutboxes(batchSize);

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ImageVariantSyncOutbox outbox : outboxes) {
            boolean success = processOutbox(outbox);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }

    private boolean processOutbox(ImageVariantSyncOutbox outbox) {
        try {
            List<ImageVariant> variants =
                    imageVariantReadManager.findBySourceImageId(
                            outbox.sourceImageId(), outbox.sourceType());

            List<ImageVariantResult> variantResults =
                    variants.stream().map(ImageVariantResult::from).toList();

            if (variantResults.isEmpty()) {
                log.warn(
                        "이미지 Variant가 없습니다. sourceImageId={}, sourceType={}",
                        outbox.sourceImageId(),
                        outbox.sourceType());
                outbox.fail("이미지 Variant가 존재하지 않습니다", Instant.now());
                outboxCommandManager.persist(outbox);
                return false;
            }

            imageVariantSyncClient.syncVariants(
                    outbox.sourceImageId(), outbox.sourceType().name(), variantResults);

            outbox.complete(Instant.now());
            outboxCommandManager.persist(outbox);
            return true;
        } catch (Exception e) {
            log.error(
                    "이미지 Variant Sync 처리 실패: outboxId={}, sourceImageId={}",
                    outbox.idValue(),
                    outbox.sourceImageId(),
                    e);
            outbox.fail(e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
            return false;
        }
    }
}
