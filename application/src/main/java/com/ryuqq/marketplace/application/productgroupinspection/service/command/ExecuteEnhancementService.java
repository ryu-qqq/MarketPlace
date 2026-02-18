package com.ryuqq.marketplace.application.productgroupinspection.service.command;

import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteEnhancementCommand;
import com.ryuqq.marketplace.application.productgroupinspection.factory.InspectionMessageFactory;
import com.ryuqq.marketplace.application.productgroupinspection.internal.InspectionEnhancementProcessor;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxReadManager;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ExecuteEnhancementUseCase;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Enhancement 단계 실행 서비스.
 *
 * <p>Outbox를 조회·검증하고 메시지 바디를 생성한 뒤 Processor로 위임합니다.
 */
@Service
public class ExecuteEnhancementService implements ExecuteEnhancementUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteEnhancementService.class);

    private final ProductGroupInspectionOutboxReadManager outboxReadManager;
    private final InspectionMessageFactory messageFactory;
    private final InspectionEnhancementProcessor enhancementProcessor;

    public ExecuteEnhancementService(
            ProductGroupInspectionOutboxReadManager outboxReadManager,
            InspectionMessageFactory messageFactory,
            InspectionEnhancementProcessor enhancementProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.messageFactory = messageFactory;
        this.enhancementProcessor = enhancementProcessor;
    }

    @Override
    public void execute(ExecuteEnhancementCommand command) {
        ProductGroupInspectionOutbox outbox =
                outboxReadManager
                        .findById(command.outboxId())
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Outbox를 찾을 수 없습니다: " + command.outboxId()));

        if (!outbox.hasExpectedStatus(InspectionOutboxStatus.ENHANCING)) {
            log.info(
                    "Enhancement 스킵 (상태 불일치): outboxId={}, currentStatus={}",
                    command.outboxId(),
                    outbox.status());
            return;
        }

        String messageBody =
                messageFactory.createMessageBody(command.outboxId(), command.productGroupId());
        enhancementProcessor.process(outbox, messageBody);
    }
}
