package com.ryuqq.marketplace.adapter.out.persistence.common.adapter;

import com.ryuqq.marketplace.application.common.port.out.IdGeneratorPort;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * UuidIdGeneratorAdapter - UUID 기반 ID 생성 어댑터.
 *
 * <p>IdGeneratorPort를 구현하여 UUID v4 기반의 고유 ID를 생성합니다.
 */
@Component
public class UuidIdGeneratorAdapter implements IdGeneratorPort {

    @Override
    public String generate() {
        return UUID.randomUUID().toString();
    }
}
