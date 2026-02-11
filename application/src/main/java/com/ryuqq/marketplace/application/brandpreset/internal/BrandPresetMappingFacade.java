package com.ryuqq.marketplace.application.brandpreset.internal;

import com.ryuqq.marketplace.application.brandmapping.manager.BrandMappingCommandManager;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.RegisterBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.UpdateBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetCommandManager;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** BrandPreset + BrandMapping 동시 저장을 위한 Facade. */
@Component
public class BrandPresetMappingFacade {

    private final BrandPresetCommandManager presetCommandManager;
    private final BrandMappingCommandManager mappingCommandManager;

    public BrandPresetMappingFacade(
            BrandPresetCommandManager presetCommandManager,
            BrandMappingCommandManager mappingCommandManager) {
        this.presetCommandManager = presetCommandManager;
        this.mappingCommandManager = mappingCommandManager;
    }

    /**
     * BrandPreset 저장 + BrandMapping 벌크 저장을 하나의 트랜잭션으로 처리.
     *
     * @param bundle 프리셋 + 매핑 생성에 필요한 데이터 번들
     * @return 저장된 프리셋 ID
     */
    @Transactional
    public Long registerWithMappings(RegisterBrandPresetBundle bundle) {
        Long presetId = presetCommandManager.persist(bundle.brandPreset());

        List<BrandMapping> mappings = bundle.createMappings(presetId);
        if (!mappings.isEmpty()) {
            mappingCommandManager.persistAll(mappings);
        }

        return presetId;
    }

    /**
     * BrandPreset 수정 + BrandMapping 교체를 하나의 트랜잭션으로 처리. 기존 매핑을 모두 삭제(hard delete)하고 새 매핑으로 교체합니다.
     *
     * @param bundle 프리셋 수정 + 매핑 교체에 필요한 데이터 번들
     */
    @Transactional
    public void updateWithMappings(UpdateBrandPresetBundle bundle) {
        BrandPreset brandPreset = bundle.brandPreset();
        brandPreset.update(bundle.presetName(), bundle.salesChannelBrandId(), bundle.now());
        presetCommandManager.persist(brandPreset);

        mappingCommandManager.deleteAllByPresetId(brandPreset.idValue());

        if (!bundle.brandMappings().isEmpty()) {
            mappingCommandManager.persistAll(bundle.brandMappings());
        }
    }

    /**
     * BrandPreset 벌크 비활성화 + 연관 BrandMapping 삭제를 하나의 트랜잭션으로 처리.
     *
     * @param brandPresets 비활성화할 프리셋 목록
     * @param now 상태 변경 시간
     * @return 비활성화된 프리셋 수
     */
    @Transactional
    public int deactivateWithMappings(List<BrandPreset> brandPresets, Instant now) {
        for (BrandPreset brandPreset : brandPresets) {
            brandPreset.deactivate(now);
        }
        presetCommandManager.persistAll(brandPresets);

        List<Long> presetIds = brandPresets.stream().map(BrandPreset::idValue).toList();
        mappingCommandManager.deleteAllByPresetIds(presetIds);

        return brandPresets.size();
    }
}
