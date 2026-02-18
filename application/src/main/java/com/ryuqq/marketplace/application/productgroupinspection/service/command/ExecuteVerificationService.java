package com.ryuqq.marketplace.application.productgroupinspection.service.command;

import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteVerificationCommand;
import com.ryuqq.marketplace.application.productgroupinspection.internal.InspectionVerificationProcessor;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxReadManager;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ExecuteVerificationUseCase;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Verification 단계 실행 서비스.
 *
 * <p>Outbox를 조회·검증한 뒤 Processor로 위임합니다.
 */
@Service
public class ExecuteVerificationService implements ExecuteVerificationUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteVerificationService.class);

    private final ProductGroupInspectionOutboxReadManager outboxReadManager;
    private final InspectionVerificationProcessor verificationProcessor;

    public ExecuteVerificationService(
            ProductGroupInspectionOutboxReadManager outboxReadManager,
            InspectionVerificationProcessor verificationProcessor) {
        this.outboxReadManager = outboxReadManager;
        this.verificationProcessor = verificationProcessor;
    }

    @Override
    public void execute(ExecuteVerificationCommand command) {
        ProductGroupInspectionOutbox outbox =
                outboxReadManager
                        .findById(command.outboxId())
                        .orElseThrow(
                                () ->
                                        new IllegalStateException(
                                                "Outbox를 찾을 수 없습니다: " + command.outboxId()));

        if (!outbox.hasExpectedStatus(InspectionOutboxStatus.VERIFYING)) {
            log.info(
                    "Verification 스킵 (상태 불일치): outboxId={}, currentStatus={}",
                    command.outboxId(),
                    outbox.status());
            return;
        }

        verificationProcessor.process(outbox);
    }
}
