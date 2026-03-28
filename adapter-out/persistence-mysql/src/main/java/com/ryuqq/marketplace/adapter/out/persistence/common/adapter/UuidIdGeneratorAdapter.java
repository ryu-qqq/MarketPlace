package com.ryuqq.marketplace.adapter.out.persistence.common.adapter;

import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Component;

/**
 * UuidIdGeneratorAdapter - UUID 기반 ID 생성 어댑터.
 *
 * <p>IdGeneratorPort를 구현하여 UUID v4 기반의 고유 ID를 생성합니다. generateLong()은 타임스탬프 기반으로 유니크한 Long ID를
 * 생성합니다.
 */
@Component
public class UuidIdGeneratorAdapter implements IdGeneratorPort {

    private static final AtomicLong COUNTER = new AtomicLong(0);

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }

    @Override
    public Long generateLong() {
        long timestamp = System.currentTimeMillis();
        long seq = COUNTER.incrementAndGet() % 1000L;
        return timestamp * 1000L + seq;
    }
}
