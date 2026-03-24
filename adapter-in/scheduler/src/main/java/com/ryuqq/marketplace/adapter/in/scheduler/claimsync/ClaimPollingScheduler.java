package com.ryuqq.marketplace.adapter.in.scheduler.claimsync;

import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties.InboundOrderPollingEntry;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.claimsync.port.in.PollExternalClaimsUseCase;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 외부 판매채널의 클레임(취소/반품/교환) 변경 내역을 주기적으로 폴링하는 스케줄러.
 *
 * <p>InboundOrderPolling과 동일한 entries 구조를 사용하여 SalesChannel별 독립 CronTask를 등록합니다.
 */
@Component
public class ClaimPollingScheduler implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(ClaimPollingScheduler.class);

    private final PollExternalClaimsUseCase pollExternalClaimsUseCase;
    private final SchedulerProperties.InboundOrderPolling config;
    private final TaskScheduler taskScheduler;

    public ClaimPollingScheduler(
            PollExternalClaimsUseCase pollExternalClaimsUseCase,
            SchedulerProperties schedulerProperties,
            TaskScheduler taskScheduler) {
        this.pollExternalClaimsUseCase = pollExternalClaimsUseCase;
        this.config = schedulerProperties.jobs().inboundOrderPolling();
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(taskScheduler);

        if (config == null || config.entries() == null || config.entries().isEmpty()) {
            log.info("클레임 폴링 엔트리 없음 — 스케줄러 미등록");
            return;
        }

        for (InboundOrderPollingEntry entry : config.entries()) {
            if (!entry.enabled()) {
                continue;
            }

            CronTrigger trigger =
                    new CronTrigger(entry.cron(), TimeZone.getTimeZone(entry.timezone()));

            Runnable task = createPollingTask(entry);
            registrar.addTriggerTask(task, trigger);

            log.info(
                    "클레임 폴링 등록: salesChannelId={}, cron={}",
                    entry.salesChannelId(),
                    entry.cron());
        }
    }

    private Runnable createPollingTask(InboundOrderPollingEntry entry) {
        return () -> {
            try {
                ClaimSyncResult result =
                        pollExternalClaimsUseCase.execute(entry.salesChannelId());
                if (result.totalProcessed() > 0) {
                    log.info(
                            "클레임 폴링 완료: salesChannelId={}, total={}, cancel={}, refund={},"
                                    + " exchange={}, skipped={}, failed={}",
                            entry.salesChannelId(),
                            result.totalProcessed(),
                            result.cancelSynced(),
                            result.refundSynced(),
                            result.exchangeSynced(),
                            result.skipped(),
                            result.failed());
                }
            } catch (Exception e) {
                log.error("클레임 폴링 실패: salesChannelId={}", entry.salesChannelId(), e);
            }
        };
    }
}
