package com.ryuqq.marketplace.adapter.in.scheduler.inboundorder;

import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties.InboundOrderPollingEntry;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollExternalOrdersUseCase;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 외부 판매채널의 주문을 주기적으로 폴링하여 InboundOrder로 수신하는 스케줄러.
 *
 * <p>SalesChannel별 독립 CronTask를 등록하여 각 채널의 Shop 단위로 폴링합니다.
 */
@Component
@ConditionalOnProperty(
        prefix = "scheduler.jobs.inbound-order-polling",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = false)
public class InboundOrderPollingScheduler implements SchedulingConfigurer {

    private static final Logger log = LoggerFactory.getLogger(InboundOrderPollingScheduler.class);

    private final PollExternalOrdersUseCase pollExternalOrdersUseCase;
    private final SchedulerProperties.InboundOrderPolling config;
    private final TaskScheduler taskScheduler;

    public InboundOrderPollingScheduler(
            PollExternalOrdersUseCase pollExternalOrdersUseCase,
            SchedulerProperties schedulerProperties,
            TaskScheduler taskScheduler) {
        this.pollExternalOrdersUseCase = pollExternalOrdersUseCase;
        this.config = schedulerProperties.jobs().inboundOrderPolling();
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(taskScheduler);

        if (config.entries() == null || config.entries().isEmpty()) {
            log.info("InboundOrder 폴링 엔트리 없음 — 스케줄러 미등록");
            return;
        }

        for (InboundOrderPollingEntry entry : config.entries()) {
            if (!entry.enabled()) {
                log.info("InboundOrder 폴링 비활성: salesChannelId={}", entry.salesChannelId());
                continue;
            }

            CronTrigger trigger =
                    new CronTrigger(entry.cron(), TimeZone.getTimeZone(entry.timezone()));

            Runnable task = createPollingTask(entry);
            registrar.addTriggerTask(task, trigger);

            log.info(
                    "InboundOrder 폴링 등록: salesChannelId={}, cron={}, batchSize={}",
                    entry.salesChannelId(),
                    entry.cron(),
                    entry.batchSize());
        }
    }

    private Runnable createPollingTask(InboundOrderPollingEntry entry) {
        return () -> {
            try {
                var result =
                        pollExternalOrdersUseCase.execute(
                                entry.salesChannelId(), entry.batchSize());
                if (result.total() > 0) {
                    log.info(
                            "InboundOrder 폴링 완료: salesChannelId={}, total={}, created={},"
                                    + " failed={}",
                            entry.salesChannelId(),
                            result.total(),
                            result.created(),
                            result.failed());
                }
            } catch (Exception e) {
                log.error("InboundOrder 폴링 실패: salesChannelId={}", entry.salesChannelId(), e);
            }
        };
    }
}
