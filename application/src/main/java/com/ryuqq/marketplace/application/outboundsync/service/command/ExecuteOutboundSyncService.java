package com.ryuqq.marketplace.application.outboundsync.service.command;

import com.ryuqq.marketplace.application.outboundsync.dto.command.ExecuteOutboundSyncCommand;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.outboundsync.port.in.command.ExecuteOutboundSyncUseCase;
import com.ryuqq.marketplace.domain.outboundsync.aggregate.OutboundSyncOutbox;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 외부 채널 연동 실행 서비스.
 *
 * <p>SQS 컨슈머에서 수신한 메시지를 처리하여 Outbox 상태를 COMPLETED로 전환합니다.
 *
 * <p>향후 외부 채널별 클라이언트(네이버커머스 등) 연동 시, 외부 API 호출은 반드시 이 트랜잭션 밖에서 수행해야 합니다 (이벤트 발행 또는 별도 서비스 분리).
 */
@Service
public class ExecuteOutboundSyncService implements ExecuteOutboundSyncUseCase {

    private static final Logger log = LoggerFactory.getLogger(ExecuteOutboundSyncService.class);

    private final OutboundSyncOutboxReadManager outboxReadManager;
    private final OutboundSyncOutboxCommandManager outboxCommandManager;

    public ExecuteOutboundSyncService(
            OutboundSyncOutboxReadManager outboxReadManager,
            OutboundSyncOutboxCommandManager outboxCommandManager) {
        this.outboxReadManager = outboxReadManager;
        this.outboxCommandManager = outboxCommandManager;
    }

    @Override
    @Transactional
    public void execute(ExecuteOutboundSyncCommand command) {
        OutboundSyncOutbox outbox = outboxReadManager.getById(command.outboxId());
        Instant now = Instant.now();

        try {
            outbox.complete(now);
            outboxCommandManager.persist(outbox);

            log.info(
                    "OutboundSync 완료: outboxId={}, productGroupId={}, salesChannelId={},"
                            + " syncType={}",
                    command.outboxId(),
                    command.productGroupId(),
                    command.salesChannelId(),
                    command.syncType());
        } catch (Exception e) {
            log.error(
                    "OutboundSync 실행 실패: outboxId={}, error={}",
                    command.outboxId(),
                    e.getMessage(),
                    e);
            outbox.recordFailure(true, "실행 실패: " + e.getMessage(), Instant.now());
            outboxCommandManager.persist(outbox);
            throw new IllegalStateException(
                    "OutboundSync 실행 실패: outboxId=" + command.outboxId(), e);
        }
    }
}
