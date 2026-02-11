package com.ryuqq.marketplace.application.categorypreset;

import com.ryuqq.marketplace.application.categorypreset.dto.bundle.RegisterCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.UpdateCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.domain.categorymapping.CategoryMappingFixtures;
import com.ryuqq.marketplace.domain.categorymapping.aggregate.CategoryMapping;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * CategoryPreset Command 테스트 Fixtures.
 *
 * <p>CategoryPreset 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class CategoryPresetCommandFixtures {

    private CategoryPresetCommandFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final String DEFAULT_CATEGORY_CODE = "TEST_CATEGORY_CODE";
    public static final String DEFAULT_PRESET_NAME = "테스트 카테고리 프리셋";
    public static final List<Long> DEFAULT_INTERNAL_CATEGORY_IDS = List.of(1L, 2L, 3L);

    // ===== RegisterCategoryPresetCommand =====

    public static RegisterCategoryPresetCommand registerCommand() {
        return new RegisterCategoryPresetCommand(
                DEFAULT_SHOP_ID,
                DEFAULT_PRESET_NAME,
                DEFAULT_CATEGORY_CODE,
                DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static RegisterCategoryPresetCommand registerCommand(Long shopId) {
        return new RegisterCategoryPresetCommand(
                shopId, DEFAULT_PRESET_NAME, DEFAULT_CATEGORY_CODE, DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static RegisterCategoryPresetCommand registerCommand(
            Long shopId, String categoryCode, String presetName) {
        return new RegisterCategoryPresetCommand(
                shopId, presetName, categoryCode, DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static RegisterCategoryPresetCommand registerCommandWithCategories(
            List<Long> internalCategoryIds) {
        return new RegisterCategoryPresetCommand(
                DEFAULT_SHOP_ID, DEFAULT_PRESET_NAME, DEFAULT_CATEGORY_CODE, internalCategoryIds);
    }

    // ===== UpdateCategoryPresetCommand =====

    public static UpdateCategoryPresetCommand updateCommand(Long categoryPresetId) {
        return new UpdateCategoryPresetCommand(
                categoryPresetId,
                "수정된 프리셋명",
                DEFAULT_CATEGORY_CODE,
                DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static UpdateCategoryPresetCommand updateCommand(
            Long categoryPresetId, String presetName) {
        return new UpdateCategoryPresetCommand(
                categoryPresetId, presetName, DEFAULT_CATEGORY_CODE, DEFAULT_INTERNAL_CATEGORY_IDS);
    }

    public static UpdateCategoryPresetCommand updateCommandWithCategories(
            Long categoryPresetId, List<Long> internalCategoryIds) {
        return new UpdateCategoryPresetCommand(
                categoryPresetId, "수정된 프리셋명", DEFAULT_CATEGORY_CODE, internalCategoryIds);
    }

    // ===== DeleteCategoryPresetsCommand =====

    public static DeleteCategoryPresetsCommand deleteCommand() {
        return new DeleteCategoryPresetsCommand(List.of(1L, 2L, 3L));
    }

    public static DeleteCategoryPresetsCommand deleteCommand(List<Long> ids) {
        return new DeleteCategoryPresetsCommand(ids);
    }

    // ===== RegisterCategoryPresetBundle =====

    public static RegisterCategoryPresetBundle registerBundle() {
        CategoryPreset categoryPreset = CategoryPresetFixtures.newCategoryPreset();
        Long salesChannelCategoryId = CategoryPresetFixtures.DEFAULT_SALES_CHANNEL_CATEGORY_ID;
        Instant now = CommonVoFixtures.now();
        return new RegisterCategoryPresetBundle(
                categoryPreset, salesChannelCategoryId, DEFAULT_INTERNAL_CATEGORY_IDS, now);
    }

    public static RegisterCategoryPresetBundle registerBundle(
            CategoryPreset categoryPreset, List<Long> internalCategoryIds) {
        Long salesChannelCategoryId = CategoryPresetFixtures.DEFAULT_SALES_CHANNEL_CATEGORY_ID;
        return new RegisterCategoryPresetBundle(
                categoryPreset, salesChannelCategoryId, internalCategoryIds, CommonVoFixtures.now());
    }

    // ===== UpdateCategoryPresetBundle =====

    public static UpdateCategoryPresetBundle updateBundle() {
        CategoryPreset existing = CategoryPresetFixtures.activeCategoryPreset();
        Long salesChannelCategoryId = CategoryPresetFixtures.DEFAULT_SALES_CHANNEL_CATEGORY_ID;
        List<CategoryMapping> mappings =
                List.of(
                        CategoryMappingFixtures.newCategoryMapping(
                                existing.idValue(), salesChannelCategoryId, 1L),
                        CategoryMappingFixtures.newCategoryMapping(
                                existing.idValue(), salesChannelCategoryId, 2L));
        Instant now = CommonVoFixtures.now();
        return new UpdateCategoryPresetBundle(
                existing, "수정된 프리셋명", salesChannelCategoryId, mappings, now);
    }

    public static UpdateCategoryPresetBundle updateBundle(
            CategoryPreset existing, String presetName) {
        Long salesChannelCategoryId = CategoryPresetFixtures.DEFAULT_SALES_CHANNEL_CATEGORY_ID;
        List<CategoryMapping> mappings =
                List.of(
                        CategoryMappingFixtures.newCategoryMapping(
                                existing.idValue(), salesChannelCategoryId, 1L),
                        CategoryMappingFixtures.newCategoryMapping(
                                existing.idValue(), salesChannelCategoryId, 2L));
        return new UpdateCategoryPresetBundle(
                existing, presetName, salesChannelCategoryId, mappings, CommonVoFixtures.now());
    }
}
