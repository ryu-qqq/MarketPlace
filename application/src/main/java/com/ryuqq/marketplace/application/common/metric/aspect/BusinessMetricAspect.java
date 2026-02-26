package com.ryuqq.marketplace.application.common.metric.aspect;

import com.ryuqq.marketplace.application.common.metric.MarketPlaceMetrics;
import com.ryuqq.marketplace.application.common.metric.annotation.BusinessMetric;
import io.micrometer.core.instrument.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * {@link BusinessMetric} 어노테이션이 붙은 메서드의 메트릭을 수집하는 Aspect.
 *
 * <p>실행 시간, 호출 횟수, 에러 횟수를 자동으로 기록합니다.
 */
@Aspect
@Component
public class BusinessMetricAspect {

    private final MarketPlaceMetrics metrics;

    public BusinessMetricAspect(MarketPlaceMetrics metrics) {
        this.metrics = metrics;
    }

    @Around("@annotation(businessMetric)")
    public Object around(ProceedingJoinPoint joinPoint, BusinessMetric businessMetric)
            throws Throwable {
        String metricName = businessMetric.value();
        String operation = businessMetric.operation();

        Timer.Sample sample = metrics.startTimer();
        try {
            Object result = joinPoint.proceed();

            metrics.stopTimer(
                    sample,
                    metricName + "_duration_seconds",
                    "operation",
                    operation,
                    "outcome",
                    "success");
            metrics.incrementCounter(
                    metricName + "_total", "operation", operation, "outcome", "success");

            return result;
        } catch (Exception e) {
            metrics.stopTimer(
                    sample,
                    metricName + "_duration_seconds",
                    "operation",
                    operation,
                    "outcome",
                    "error");
            metrics.incrementCounter(
                    metricName + "_total", "operation", operation, "outcome", "error");
            metrics.incrementCounter(
                    metricName + "_errors_total",
                    "operation",
                    operation,
                    "exception",
                    e.getClass().getSimpleName());
            throw e;
        }
    }
}
