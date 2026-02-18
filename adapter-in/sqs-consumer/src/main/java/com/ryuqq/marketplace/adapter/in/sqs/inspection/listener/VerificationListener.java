package com.ryuqq.marketplace.adapter.in.sqs.inspection.listener;

import com.ryuqq.marketplace.adapter.in.sqs.inspection.dto.InspectionSqsMessage;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteVerificationCommand;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ExecuteVerificationUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Verification 단계 SQS Listener.
 *
 * <p>Verification 큐의 메시지를 수신하여 LLM 기반 최종 품질 검증을 실행합니다. 검증 결과에 따라 ProductGroup을 활성화하거나 거부합니다.
 *
 * <p>메시지 처리 성공 시 자동으로 acknowledge (삭제)되며, 실패 시 visibility timeout 후 재처리됩니다. maxReceiveCount 초과 시
 * DLQ로 이동합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.inspection-verification",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class VerificationListener {

    private static final Logger log = LoggerFactory.getLogger(VerificationListener.class);

    private final ExecuteVerificationUseCase executeVerificationUseCase;

    public VerificationListener(ExecuteVerificationUseCase executeVerificationUseCase) {
        this.executeVerificationUseCase = executeVerificationUseCase;
    }

    @SqsListener("${sqs.queues.inspection-verification}")
    public void onMessage(InspectionSqsMessage message) {
        log.debug(
                "Verification 메시지 수신: outboxId={}, productGroupId={}",
                message.outboxId(),
                message.productGroupId());

        executeVerificationUseCase.execute(
                ExecuteVerificationCommand.of(message.outboxId(), message.productGroupId()));

        log.info(
                "Verification 메시지 처리 완료: outboxId={}, productGroupId={}",
                message.outboxId(),
                message.productGroupId());
    }
}
