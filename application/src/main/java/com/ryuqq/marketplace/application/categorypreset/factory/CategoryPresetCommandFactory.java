package com.ryuqq.marketplace.application.categorypreset.factory;

import com.ryuqq.marketplace.application.categorypreset.dto.bundle.RegisterCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.UpdateCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.time.Instant;
import java.util.List;
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

    /** 등록 Command로부터 RegisterCategoryPresetBundle 생성. */
    public RegisterCategoryPresetBundle createRegisterBundle(
            RegisterCategoryPresetCommand command, Long salesChannelCategoryId) {
        Instant now = timeProvider.now();
        CategoryPreset categoryPreset =
                CategoryPreset.forNew(
                        command.shopId(), salesChannelCategoryId, command.presetName(), now);
        return new RegisterCategoryPresetBundle(
                categoryPreset, salesChannelCategoryId, command.internalCategoryIds(), now);
    }

    /** 수정 Command로부터 UpdateCategoryPresetBundle 생성. */
    public UpdateCategoryPresetBundle createUpdateBundle(
            CategoryPreset existing,
            UpdateCategoryPresetCommand command,
            Long salesChannelCategoryId) {
        Instant now = timeProvider.now();
        List<CategoryMapping> categoryMappings =
                createCategoryMappings(
                        existing.idValue(),
                        salesChannelCategoryId,
                        command.internalCategoryIds(),
                        now);
        return new UpdateCategoryPresetBundle(
                existing, command.presetName(), salesChannelCategoryId, categoryMappings, now);
    }

    /** 삭제(비활성화) Command로부터 StatusChangeContext 생성. */
    public StatusChangeContext<List<Long>> createDeactivateContext(
            DeleteCategoryPresetsCommand command) {
        return new StatusChangeContext<>(command.ids(), timeProvider.now());
    }

    private List<CategoryMapping> createCategoryMappings(
            Long presetId,
            Long salesChannelCategoryId,
            List<Long> internalCategoryIds,
            Instant now) {
        if (internalCategoryIds == null || internalCategoryIds.isEmpty()) {
            return List.of();
        }
        return internalCategoryIds.stream()
                .map(
                        internalCategoryId ->
                                CategoryMapping.forNew(
                                        presetId, salesChannelCategoryId, internalCategoryId, now))
                .toList();
    }
}
