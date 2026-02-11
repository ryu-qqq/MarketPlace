package com.ryuqq.marketplace.application.categorypreset.internal;

import com.ryuqq.marketplace.application.categorymapping.manager.CategoryMappingCommandManager;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.RegisterCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.UpdateCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetCommandManager;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** CategoryPreset + CategoryMapping 동시 저장을 위한 Facade. */
@Component
public class CategoryPresetMappingFacade {

    private final CategoryPresetCommandManager presetCommandManager;
    private final CategoryMappingCommandManager mappingCommandManager;

    public CategoryPresetMappingFacade(
            CategoryPresetCommandManager presetCommandManager,
            CategoryMappingCommandManager mappingCommandManager) {
        this.presetCommandManager = presetCommandManager;
        this.mappingCommandManager = mappingCommandManager;
    }

    /**
     * CategoryPreset 저장 + CategoryMapping 벌크 저장을 하나의 트랜잭션으로 처리.
     *
     * @param bundle 프리셋 + 매핑 생성에 필요한 데이터 번들
     * @return 저장된 프리셋 ID
     */
    @Transactional
    public Long registerWithMappings(RegisterCategoryPresetBundle bundle) {
        Long presetId = presetCommandManager.persist(bundle.categoryPreset());

        List<CategoryMapping> mappings = bundle.createMappings(presetId);
        if (!mappings.isEmpty()) {
            mappingCommandManager.persistAll(mappings);
        }

        return presetId;
    }

    /**
     * CategoryPreset 수정 + CategoryMapping 교체를 하나의 트랜잭션으로 처리.
     * 기존 매핑을 모두 삭제(hard delete)하고 새 매핑으로 교체합니다.
     *
     * @param bundle 프리셋 수정 + 매핑 교체에 필요한 데이터 번들
     */
    @Transactional
    public void updateWithMappings(UpdateCategoryPresetBundle bundle) {
        CategoryPreset categoryPreset = bundle.categoryPreset();
        categoryPreset.update(
                bundle.presetName(), bundle.salesChannelCategoryId(), bundle.now());
        presetCommandManager.persist(categoryPreset);

        mappingCommandManager.deleteAllByPresetId(categoryPreset.idValue());

        if (!bundle.categoryMappings().isEmpty()) {
            mappingCommandManager.persistAll(bundle.categoryMappings());
        }
    }

    /**
     * CategoryPreset 벌크 비활성화 + 연관 CategoryMapping 삭제를 하나의 트랜잭션으로 처리.
     *
     * @param categoryPresets 비활성화할 프리셋 목록
     * @param now 상태 변경 시간
     * @return 비활성화된 프리셋 수
     */
    @Transactional
    public int deactivateWithMappings(List<CategoryPreset> categoryPresets, Instant now) {
        for (CategoryPreset categoryPreset : categoryPresets) {
            categoryPreset.deactivate(now);
        }
        presetCommandManager.persistAll(categoryPresets);

        List<Long> presetIds =
                categoryPresets.stream().map(CategoryPreset::idValue).toList();
        mappingCommandManager.deleteAllByPresetIds(presetIds);

        return categoryPresets.size();
    }
}
