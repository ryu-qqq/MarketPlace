package com.ryuqq.marketplace.application.brandpreset.factory;

import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.time.Instant;
import org.springframework.stereotype.Component;

/**
 * BrandPreset Command Factory.
 *
 * <p>APP-TIM-001: TimeProvider.now()는 Factory에서만 호출합니다.
 */
@Component
public class BrandPresetCommandFactory {

    private final TimeProvider timeProvider;

    public BrandPresetCommandFactory(TimeProvider timeProvider) {
        this.timeProvider = timeProvider;
    }

    /** 등록 Command로부터 BrandPreset 도메인 객체 생성. */
    public BrandPreset create(RegisterBrandPresetCommand command) {
        Instant now = timeProvider.now();
        return BrandPreset.forNew(
                command.shopId(), command.salesChannelBrandId(), command.presetName(), now);
    }

    /** 현재 시간 반환. */
    public Instant now() {
        return timeProvider.now();
    }
}
