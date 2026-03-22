package com.ryuqq.marketplace.adapter.in.scheduler.qna;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.PollExternalQnasUseCase;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * InboundQna 폴링 스케줄러.
 *
 * <p>외부 판매채널에서 QnA를 주기적으로 수집합니다. config에 설정된 salesChannelIds를 순회하며 폴링합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-qna-polling",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class InboundQnaPollingScheduler {

    private static final Logger log = LoggerFactory.getLogger(InboundQnaPollingScheduler.class);

    private final PollExternalQnasUseCase pollExternalQnasUseCase;
    private final SchedulerProperties.InboundQnaPolling config;

    public InboundQnaPollingScheduler(
            PollExternalQnasUseCase pollExternalQnasUseCase,
            SchedulerProperties schedulerProperties) {
        this.pollExternalQnasUseCase = pollExternalQnasUseCase;
        this.config = schedulerProperties.jobs().inboundQnaPolling();
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-qna-polling.cron}",
            zone = "${scheduler.jobs.inbound-qna-polling.timezone}")
    @SchedulerJob("InboundQna-Polling")
    public void pollQnas() {
        List<Long> salesChannelIds = config.salesChannelIds();
        if (salesChannelIds == null || salesChannelIds.isEmpty()) {
            log.debug("InboundQna 폴링 대상 salesChannelId 없음");
            return;
        }

        int totalReceived = 0;
        for (Long salesChannelId : salesChannelIds) {
            try {
                int received = pollExternalQnasUseCase.execute(salesChannelId, config.batchSize());
                if (received > 0) {
                    log.info(
                            "InboundQna 폴링 완료: salesChannelId={}, 수신 {}건",
                            salesChannelId,
                            received);
                }
                totalReceived += received;
            } catch (Exception e) {
                log.error("InboundQna 폴링 실패: salesChannelId={}", salesChannelId, e);
            }
        }

        if (totalReceived > 0) {
            log.info("InboundQna 폴링 전체 완료: totalReceived={}", totalReceived);
        }
    }
}
