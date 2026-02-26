package com.ryuqq.marketplace.application.common.metric.aspect;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.common.metric.MarketPlaceMetrics;
import com.ryuqq.marketplace.application.common.metric.annotation.BatchMetric;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * {@link BatchMetric} 어노테이션이 붙은 메서드의 메트릭을 수집하는 Aspect.
 *
 * <p>실행 시간, 호출 횟수, 배치 항목 수, 에러 횟수를 자동으로 기록합니다.
 */
@Aspect
@Component
public class BatchMetricAspect {

    private final MarketPlaceMetrics metrics;

    public BatchMetricAspect(MarketPlaceMetrics metrics) {
        this.metrics = metrics;
    }

    @Around("@annotation(batchMetric)")
    public Object around(ProceedingJoinPoint joinPoint, BatchMetric batchMetric) throws Throwable {
        String metricName = batchMetric.value();
        String category = batchMetric.category();

        Timer.Sample sample = metrics.startTimer();
        try {
            Object result = joinPoint.proceed();

            metrics.stopTimer(
                    sample,
                    metricName + "_duration_seconds",
                    "category",
                    category,
                    "outcome",
                    "success");
            metrics.incrementCounter(
                    metricName + "_total", "category", category, "outcome", "success");

            if (result instanceof SchedulerBatchProcessingResult batchResult) {
                metrics.recordBatchResult(metricName, category, batchResult);
            }

            return result;
        } catch (Exception e) {
            metrics.stopTimer(
                    sample,
                    metricName + "_duration_seconds",
                    "category",
                    category,
                    "outcome",
                    "error");
            metrics.incrementCounter(
                    metricName + "_total", "category", category, "outcome", "error");
            metrics.incrementCounter(
                    metricName + "_errors_total",
                    "category",
                    category,
                    "exception",
                    e.getClass().getSimpleName());
            throw e;
        }
    }
}
