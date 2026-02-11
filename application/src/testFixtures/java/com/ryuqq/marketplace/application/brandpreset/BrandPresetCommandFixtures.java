package com.ryuqq.marketplace.application.brandpreset;

import com.ryuqq.marketplace.application.brandpreset.dto.bundle.RegisterBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.UpdateBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.domain.brandmapping.BrandMappingFixtures;
import com.ryuqq.marketplace.domain.brandmapping.aggregate.BrandMapping;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;

/**
 * BrandPreset Command 테스트 Fixtures.
 *
 * <p>BrandPreset 관련 Command 객체들을 생성하는 테스트 유틸리티입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class BrandPresetCommandFixtures {

    private BrandPresetCommandFixtures() {}

    // ===== 기본 값 상수 =====
    public static final Long DEFAULT_SHOP_ID = 1L;
    public static final Long DEFAULT_SALES_CHANNEL_BRAND_ID = 100L;
    public static final String DEFAULT_PRESET_NAME = "테스트 브랜드 프리셋";
    public static final List<Long> DEFAULT_INTERNAL_BRAND_IDS = List.of(10L, 11L, 12L);

    // ===== RegisterBrandPresetCommand =====

    public static RegisterBrandPresetCommand registerCommand() {
        return new RegisterBrandPresetCommand(
                DEFAULT_SHOP_ID,
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_PRESET_NAME,
                DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static RegisterBrandPresetCommand registerCommand(Long shopId) {
        return new RegisterBrandPresetCommand(
                shopId, DEFAULT_SALES_CHANNEL_BRAND_ID, DEFAULT_PRESET_NAME, DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static RegisterBrandPresetCommand registerCommand(
            Long shopId, Long salesChannelBrandId, String presetName) {
        return new RegisterBrandPresetCommand(
                shopId, salesChannelBrandId, presetName, DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static RegisterBrandPresetCommand registerCommandWithBrands(
            List<Long> internalBrandIds) {
        return new RegisterBrandPresetCommand(
                DEFAULT_SHOP_ID, DEFAULT_SALES_CHANNEL_BRAND_ID, DEFAULT_PRESET_NAME, internalBrandIds);
    }

    // ===== UpdateBrandPresetCommand =====

    public static UpdateBrandPresetCommand updateCommand(Long brandPresetId) {
        return new UpdateBrandPresetCommand(
                brandPresetId,
                "수정된 프리셋명",
                DEFAULT_SALES_CHANNEL_BRAND_ID,
                DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static UpdateBrandPresetCommand updateCommand(
            Long brandPresetId, String presetName) {
        return new UpdateBrandPresetCommand(
                brandPresetId, presetName, DEFAULT_SALES_CHANNEL_BRAND_ID, DEFAULT_INTERNAL_BRAND_IDS);
    }

    public static UpdateBrandPresetCommand updateCommandWithBrands(
            Long brandPresetId, List<Long> internalBrandIds) {
        return new UpdateBrandPresetCommand(
                brandPresetId, "수정된 프리셋명", DEFAULT_SALES_CHANNEL_BRAND_ID, internalBrandIds);
    }

    // ===== DeleteBrandPresetsCommand =====

    public static DeleteBrandPresetsCommand deleteCommand() {
        return new DeleteBrandPresetsCommand(List.of(1L, 2L, 3L));
    }

    public static DeleteBrandPresetsCommand deleteCommand(List<Long> ids) {
        return new DeleteBrandPresetsCommand(ids);
    }

    // ===== RegisterBrandPresetBundle =====

    public static RegisterBrandPresetBundle registerBundle() {
        BrandPreset brandPreset = BrandPresetFixtures.newBrandPreset();
        Long salesChannelBrandId = BrandPresetFixtures.DEFAULT_SALES_CHANNEL_BRAND_ID;
        Instant now = CommonVoFixtures.now();
        return new RegisterBrandPresetBundle(
                brandPreset, salesChannelBrandId, DEFAULT_INTERNAL_BRAND_IDS, now);
    }

    public static RegisterBrandPresetBundle registerBundle(
            BrandPreset brandPreset, List<Long> internalBrandIds) {
        Long salesChannelBrandId = BrandPresetFixtures.DEFAULT_SALES_CHANNEL_BRAND_ID;
        return new RegisterBrandPresetBundle(
                brandPreset, salesChannelBrandId, internalBrandIds, CommonVoFixtures.now());
    }

    // ===== UpdateBrandPresetBundle =====

    public static UpdateBrandPresetBundle updateBundle() {
        BrandPreset existing = BrandPresetFixtures.activeBrandPreset();
        Long salesChannelBrandId = BrandPresetFixtures.DEFAULT_SALES_CHANNEL_BRAND_ID;
        List<BrandMapping> mappings =
                List.of(
                        BrandMappingFixtures.newBrandMapping(
                                existing.idValue(), salesChannelBrandId, 10L),
                        BrandMappingFixtures.newBrandMapping(
                                existing.idValue(), salesChannelBrandId, 11L));
        Instant now = CommonVoFixtures.now();
        return new UpdateBrandPresetBundle(
                existing, "수정된 프리셋명", salesChannelBrandId, mappings, now);
    }

    public static UpdateBrandPresetBundle updateBundle(
            BrandPreset existing, String presetName) {
        Long salesChannelBrandId = BrandPresetFixtures.DEFAULT_SALES_CHANNEL_BRAND_ID;
        List<BrandMapping> mappings =
                List.of(
                        BrandMappingFixtures.newBrandMapping(
                                existing.idValue(), salesChannelBrandId, 10L),
                        BrandMappingFixtures.newBrandMapping(
                                existing.idValue(), salesChannelBrandId, 11L));
        return new UpdateBrandPresetBundle(
                existing, presetName, salesChannelBrandId, mappings, CommonVoFixtures.now());
    }
}
