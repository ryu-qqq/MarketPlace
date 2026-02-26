package com.ryuqq.marketplace.application.common.metric;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Component;

/**
 * MarketPlace 커스텀 메트릭 관리 클래스.
 *
 * <p>Micrometer의 Timer, Counter를 캐싱하여 성능을 최적화합니다. 모든 메트릭 이름에는 {@code marketplace.} prefix가 자동
 * 부여됩니다.
 *
 * <p>사용 예시:
 *
 * <pre>{@code
 * Timer.Sample sample = metrics.startTimer();
 * // ... 작업 수행 ...
 * metrics.stopTimer(sample, "outbound_client_duration_seconds", "system", "s3", "outcome", "success");
 * metrics.incrementCounter("outbound_client_total", "system", "s3", "outcome", "success");
 * }</pre>
 */
@Component
public class MarketPlaceMetrics {

    private static final String PREFIX = "marketplace.";

    private final MeterRegistry meterRegistry;
    private final ConcurrentMap<String, Counter> counterCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, Timer> timerCache = new ConcurrentHashMap<>();

    public MarketPlaceMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public Timer.Sample startTimer() {
        return Timer.start(meterRegistry);
    }

    public void stopTimer(Timer.Sample sample, String name, String... tags) {
        validateTags(tags);
        Timer timer =
                timerCache.computeIfAbsent(
                        createCacheKey(name, tags),
                        k ->
                                Timer.builder(PREFIX + name)
                                        .tags(tags)
                                        .publishPercentileHistogram()
                                        .register(meterRegistry));
        sample.stop(timer);
    }

    public void incrementCounter(String name, String... tags) {
        validateTags(tags);
        getOrCreateCounter(name, tags).increment();
    }

    public void recordBatchResult(
            String name, String category, SchedulerBatchProcessingResult result) {
        getOrCreateCounter(name + "_items_total", "category", category, "status", "total")
                .increment(result.total());
        getOrCreateCounter(name + "_items_total", "category", category, "status", "success")
                .increment(result.success());
        getOrCreateCounter(name + "_items_total", "category", category, "status", "failed")
                .increment(result.failed());
    }

    public void recordDuration(String name, Duration duration, String... tags) {
        validateTags(tags);
        Timer timer =
                timerCache.computeIfAbsent(
                        createCacheKey(name, tags),
                        k -> Timer.builder(PREFIX + name).tags(tags).register(meterRegistry));
        timer.record(duration);
    }

    private Counter getOrCreateCounter(String name, String... tags) {
        return counterCache.computeIfAbsent(
                createCacheKey(name, tags),
                k -> Counter.builder(PREFIX + name).tags(tags).register(meterRegistry));
    }

    private String createCacheKey(String name, String... tags) {
        return name + Arrays.toString(tags);
    }

    private void validateTags(String... tags) {
        if (tags.length % 2 != 0) {
            throw new IllegalArgumentException(
                    "Tags must be key-value pairs (even count), but got "
                            + tags.length
                            + " elements");
        }
    }
}
