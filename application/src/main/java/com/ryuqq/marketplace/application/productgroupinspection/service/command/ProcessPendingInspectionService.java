package com.ryuqq.marketplace.application.productgroupinspection.service.command;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ProcessPendingInspectionCommand;
import com.ryuqq.marketplace.application.productgroupinspection.factory.InspectionMessageFactory;
import com.ryuqq.marketplace.application.productgroupinspection.internal.InspectionRelayProcessor;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxReadManager;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ProcessPendingInspectionUseCase;
import com.ryuqq.marketplace.application.productgroupinspection.validator.InspectionReadinessValidator;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ProcessPendingInspectionService - Outbox Relay 서비스.
 *
 * <p>PENDING Outbox를 조회하여 선행 조건을 검증하고, 메시지 바디를 생성한 뒤 InspectionRelayProcessor에 relay를 위임합니다.
 */
@Service
public class ProcessPendingInspectionService implements ProcessPendingInspectionUseCase {

    private static final Logger log =
            LoggerFactory.getLogger(ProcessPendingInspectionService.class);

    private final ProductGroupInspectionOutboxReadManager outboxReadManager;
    private final InspectionReadinessValidator readinessValidator;
    private final InspectionMessageFactory messageFactory;
    private final InspectionRelayProcessor relayProcessor;

    public ProcessPendingInspectionService(
            ProductGroupInspectionOutboxReadManager outboxReadManager,
            InspectionReadinessValidator readinessValidator,
            InspectionMessageFactory messageFactory,
            InspectionRelayProcessor relayProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.readinessValidator = readinessValidator;
        this.messageFactory = messageFactory;
        this.relayProcessor = relayProcessor;
    }

    @Override
    public SchedulerBatchProcessingResult execute(ProcessPendingInspectionCommand command) {
        List<ProductGroupInspectionOutbox> outboxes =
                outboxReadManager.findPendingOutboxes(command.beforeTime(), command.batchSize());

        int total = outboxes.size();
        int successCount = 0;
        int failedCount = 0;

        for (ProductGroupInspectionOutbox outbox : outboxes) {
            if (!readinessValidator.isReady(outbox.productGroupId())) {
                log.debug("검수 선행 조건 미충족, 스킵: productGroupId={}", outbox.productGroupId());
                failedCount++;
                continue;
            }

            String messageBody =
                    messageFactory.createMessageBody(outbox.idValue(), outbox.productGroupId());
            boolean success = relayProcessor.relay(outbox, messageBody);
            if (success) {
                successCount++;
            } else {
                failedCount++;
            }
        }

        return SchedulerBatchProcessingResult.of(total, successCount, failedCount);
    }
}
