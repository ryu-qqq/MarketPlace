package com.ryuqq.marketplace.adapter.in.scheduler.purchaseconfirmed;

import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties;
import com.ryuqq.marketplace.adapter.in.scheduler.config.SchedulerProperties.InboundOrderPollingEntry;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.PollPurchaseConfirmedOrdersUseCase;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * 외부 판매채널의 구매확정 이벤트를 주기적으로 폴링하는 스케줄러.
 *
 * <p>InboundOrderPolling과 동일한 entries 구조를 재사용하여 SalesChannel별 독립 CronTask를 등록합니다.
 */
@Component
public class PurchaseConfirmedPollingScheduler implements SchedulingConfigurer {

    private static final Logger log =
            LoggerFactory.getLogger(PurchaseConfirmedPollingScheduler.class);

    private final PollPurchaseConfirmedOrdersUseCase pollPurchaseConfirmedOrdersUseCase;
    private final SchedulerProperties.InboundOrderPolling config;
    private final TaskScheduler taskScheduler;

    public PurchaseConfirmedPollingScheduler(
            PollPurchaseConfirmedOrdersUseCase pollPurchaseConfirmedOrdersUseCase,
            SchedulerProperties schedulerProperties,
            TaskScheduler taskScheduler) {
        this.pollPurchaseConfirmedOrdersUseCase = pollPurchaseConfirmedOrdersUseCase;
        this.config = schedulerProperties.jobs().purchaseConfirmedPolling();
        this.taskScheduler = taskScheduler;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        registrar.setScheduler(taskScheduler);

        if (config == null || config.entries() == null || config.entries().isEmpty()) {
            log.info("구매확정 폴링 엔트리 없음 — 스케줄러 미등록");
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
                    "구매확정 폴링 등록: salesChannelId={}, cron={}", entry.salesChannelId(), entry.cron());
        }
    }

    private Runnable createPollingTask(InboundOrderPollingEntry entry) {
        return () -> {
            try {
                pollPurchaseConfirmedOrdersUseCase.execute(entry.salesChannelId());
                log.info("구매확정 폴링 완료: salesChannelId={}", entry.salesChannelId());
            } catch (Exception e) {
                log.error("구매확정 폴링 실패: salesChannelId={}", entry.salesChannelId(), e);
            }
        };
    }
}
