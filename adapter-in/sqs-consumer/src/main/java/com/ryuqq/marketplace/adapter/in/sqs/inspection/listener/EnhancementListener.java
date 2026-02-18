package com.ryuqq.marketplace.adapter.in.sqs.inspection.listener;

import com.ryuqq.marketplace.adapter.in.sqs.inspection.dto.InspectionSqsMessage;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteEnhancementCommand;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ExecuteEnhancementUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Enhancement 단계 SQS Listener.
 *
 * <p>Enhancement 큐의 메시지를 수신하여 LLM 기반 미달 항목 보강을 실행합니다. 보강 완료 후 Verification 큐로 메시지를 발행합니다.
 *
 * <p>메시지 처리 성공 시 자동으로 acknowledge (삭제)되며, 실패 시 visibility timeout 후 재처리됩니다. maxReceiveCount 초과 시
 * DLQ로 이동합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.inspection-enhancement",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class EnhancementListener {

    private static final Logger log = LoggerFactory.getLogger(EnhancementListener.class);

    private final ExecuteEnhancementUseCase executeEnhancementUseCase;

    public EnhancementListener(ExecuteEnhancementUseCase executeEnhancementUseCase) {
        this.executeEnhancementUseCase = executeEnhancementUseCase;
    }

    @SqsListener("${sqs.queues.inspection-enhancement}")
    public void onMessage(InspectionSqsMessage message) {
        log.debug(
                "Enhancement 메시지 수신: outboxId={}, productGroupId={}",
                message.outboxId(),
                message.productGroupId());

        executeEnhancementUseCase.execute(
                ExecuteEnhancementCommand.of(message.outboxId(), message.productGroupId()));

        log.info(
                "Enhancement 메시지 처리 완료: outboxId={}, productGroupId={}",
                message.outboxId(),
                message.productGroupId());
    }
}
