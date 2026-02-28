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
 * 외부 채널 연동 실행 서비스 (스텁).
 *
 * <p>현재는 Outbox 상태만 COMPLETED로 변경하는 스텁입니다. 향후 외부 채널별 클라이언트(네이버커머스 등) 연동 시 확장합니다.
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
            if (outbox.isPending()) {
                outbox.startProcessing(now);
                outboxCommandManager.persist(outbox);
            }

            // TODO: 향후 외부 채널별 클라이언트 연동 구현
            // 현재는 스텁으로 바로 COMPLETED 처리
            outbox.complete(Instant.now());
            outboxCommandManager.persist(outbox);

            log.info(
                    "OutboundSync 완료 (스텁): outboxId={}, productGroupId={}, salesChannelId={},"
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
        }
    }
}
