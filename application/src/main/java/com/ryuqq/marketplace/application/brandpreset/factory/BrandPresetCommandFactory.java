package com.ryuqq.marketplace.application.brandpreset.factory;

import com.ryuqq.marketplace.application.brandpreset.dto.bundle.RegisterBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.UpdateBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.time.Instant;
import java.util.List;
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

    /** 등록 Command로부터 RegisterBrandPresetBundle 생성. */
    public RegisterBrandPresetBundle createRegisterBundle(RegisterBrandPresetCommand command) {
        Instant now = timeProvider.now();
        BrandPreset brandPreset =
                BrandPreset.forNew(
                        command.shopId(), command.salesChannelBrandId(), command.presetName(), now);
        return new RegisterBrandPresetBundle(
                brandPreset, command.salesChannelBrandId(), command.internalBrandIds(), now);
    }

    /** 수정 Command로부터 UpdateBrandPresetBundle 생성. */
    public UpdateBrandPresetBundle createUpdateBundle(
            BrandPreset existing, UpdateBrandPresetCommand command) {
        Instant now = timeProvider.now();
        List<BrandMapping> brandMappings =
                createBrandMappings(
                        existing.idValue(),
                        command.salesChannelBrandId(),
                        command.internalBrandIds(),
                        now);
        return new UpdateBrandPresetBundle(
                existing, command.presetName(), command.salesChannelBrandId(), brandMappings, now);
    }

    /** 삭제(비활성화) Command로부터 StatusChangeContext 생성. */
    public StatusChangeContext<List<Long>> createDeactivateContext(
            DeleteBrandPresetsCommand command) {
        return new StatusChangeContext<>(command.ids(), timeProvider.now());
    }

    private List<BrandMapping> createBrandMappings(
            Long presetId, Long salesChannelBrandId, List<Long> internalBrandIds, Instant now) {
        if (internalBrandIds == null || internalBrandIds.isEmpty()) {
            return List.of();
        }
        return internalBrandIds.stream()
                .map(
                        internalBrandId ->
                                BrandMapping.forNew(
                                        presetId, salesChannelBrandId, internalBrandId, now))
                .toList();
    }
}
