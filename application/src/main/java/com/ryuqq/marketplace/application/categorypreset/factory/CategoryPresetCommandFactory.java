package com.ryuqq.marketplace.application.categorypreset.factory;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * CategoryPreset Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class CategoryPresetCommandFactory {

    private final TimeProvider timeProvider;

    public CategoryPresetCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 현재 시간 반환 (상태 변경 시 사용). */
    public Instant now() {
        return timeProvider.now();
    }
}
