package com.ryuqq.marketplace.adapter.in.sqs.inspection.listener;

import com.ryuqq.marketplace.adapter.in.sqs.inspection.dto.InspectionSqsMessage;
import com.ryuqq.marketplace.application.productgroupinspection.dto.command.ExecuteScoringCommand;
import com.ryuqq.marketplace.application.productgroupinspection.port.in.command.ExecuteScoringUseCase;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Scoring 단계 SQS Listener.
 *
 * <p>Scoring 큐의 메시지를 수신하여 AI 기반 점수 계산을 실행합니다. 점수 결과에 따라 Enhancement 또는 Verification 큐로 메시지를 발행합니다.
 *
 * <p>메시지 처리 성공 시 자동으로 acknowledge (삭제)되며, 실패 시 visibility timeout 후 재처리됩니다. maxReceiveCount 초과 시
 * DLQ로 이동합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "sqs.consumer.inspection-scoring",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true)
public class ScoringListener {

    private static final Logger log = LoggerFactory.getLogger(ScoringListener.class);

    private final ExecuteScoringUseCase executeScoringUseCase;

    public ScoringListener(ExecuteScoringUseCase executeScoringUseCase) {
        this.executeScoringUseCase = executeScoringUseCase;
    }

    @SqsListener("${sqs.queues.inspection-scoring}")
    public void onMessage(InspectionSqsMessage message) {
        log.debug(
                "Scoring 메시지 수신: outboxId={}, productGroupId={}",
                message.outboxId(),
                message.productGroupId());

        executeScoringUseCase.execute(
                ExecuteScoringCommand.of(message.outboxId(), message.productGroupId()));

        log.info(
                "Scoring 메시지 처리 완료: outboxId={}, productGroupId={}",
                message.outboxId(),
                message.productGroupId());
    }
}
