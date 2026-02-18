package com.ryuqq.marketplace.bootstrap.config;

import java.time.Clock;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Core Configuration.
 *
 * <p>애플리케이션 핵심 빈들을 등록합니다.
 */
@Configuration
public class CoreConfiguration {

    /**
     * Clock 빈 등록.
     *
     * <p>시스템 기본 타임존 Clock을 제공합니다. 테스트 시 Mock Clock으로 교체 가능합니다.
     *
     * @return Clock 인스턴스
     */
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }

    /**
     * 배치 처리용 Virtual Thread Executor 빈 등록.
     *
     * <p>Java 21 Virtual Thread를 사용하여 I/O 바운드 배치 작업을 병렬 처리합니다. Spring이 종료 시 AutoCloseable.close()를
     * 호출하여 안전하게 shutdown합니다.
     *
     * @return Virtual Thread 기반 ExecutorService
     */
    @Bean("batchExecutor")
    public ExecutorService batchExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
