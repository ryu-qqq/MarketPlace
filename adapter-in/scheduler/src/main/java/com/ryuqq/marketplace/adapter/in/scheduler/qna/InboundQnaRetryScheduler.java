package com.ryuqq.marketplace.adapter.in.scheduler.qna;

import com.ryuqq.marketplace.adapter.in.scheduler.annotation.SchedulerJob;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.RetryReceivedInboundQnasUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * RECEIVED 상태 InboundQna 재변환 스케줄러.
 *
 * <p>즉시 변환에 실패하여 RECEIVED 상태로 남은 InboundQna를 주기적으로 재시도합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-qna-retry",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class InboundQnaRetryScheduler {

    private static final Logger log = LoggerFactory.getLogger(InboundQnaRetryScheduler.class);

    private final RetryReceivedInboundQnasUseCase retryReceivedInboundQnasUseCase;
    private final SchedulerProperties.InboundQnaRetry config;

    public InboundQnaRetryScheduler(
            RetryReceivedInboundQnasUseCase retryReceivedInboundQnasUseCase,
            SchedulerProperties schedulerProperties) {
        this.retryReceivedInboundQnasUseCase = retryReceivedInboundQnasUseCase;
        this.config = schedulerProperties.jobs().inboundQnaRetry();
    }

    @Scheduled(
            cron = "${scheduler.jobs.inbound-qna-retry.cron}",
            zone = "${scheduler.jobs.inbound-qna-retry.timezone}")
    @SchedulerJob("InboundQna-Retry")
    public void retryReceivedQnas() {
        int converted = retryReceivedInboundQnasUseCase.execute(config.batchSize());
        if (converted > 0) {
            log.info("InboundQna 재변환 완료: converted={}", converted);
        }
    }
}
