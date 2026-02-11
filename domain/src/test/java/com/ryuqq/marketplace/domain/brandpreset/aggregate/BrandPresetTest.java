package com.ryuqq.marketplace.domain.brandpreset.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
import com.ryuqq.marketplace.domain.brandpreset.vo.BrandPresetStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandPreset Aggregate 테스트")
class BrandPresetTest {

    @Nested
    @DisplayName("forNew() - 신규 브랜드 프리셋 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 브랜드 프리셋을 생성한다")
        void createNewBrandPresetWithRequiredFields() {
            // given
            Long shopId = 1L;
            Long salesChannelBrandId = 100L;
            String presetName = "테스트 프리셋";
            Instant now = CommonVoFixtures.now();

            // when
            BrandPreset preset = BrandPreset.forNew(shopId, salesChannelBrandId, presetName, now);

            // then
            assertThat(preset.id().isNew()).isTrue();
            assertThat(preset.shopId()).isEqualTo(shopId);
            assertThat(preset.salesChannelBrandId()).isEqualTo(salesChannelBrandId);
            assertThat(preset.presetName()).isEqualTo(presetName);
            assertThat(preset.status()).isEqualTo(BrandPresetStatus.ACTIVE);
            assertThat(preset.isActive()).isTrue();
            assertThat(preset.createdAt()).isEqualTo(now);
            assertThat(preset.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 브랜드 프리셋을 복원한다")
        void reconstituteActiveBrandPreset() {
            // given
            BrandPresetId id = BrandPresetId.of(1L);
            Long shopId = 1L;
            Long salesChannelBrandId = 100L;
            String presetName = "테스트 프리셋";
            Instant createdAt = CommonVoFixtures.yesterday();
            Instant updatedAt = CommonVoFixtures.yesterday();

            // when
            BrandPreset preset =
                    BrandPreset.reconstitute(
                            id,
                            shopId,
                            salesChannelBrandId,
                            presetName,
                            BrandPresetStatus.ACTIVE,
                            createdAt,
                            updatedAt);

            // then
            assertThat(preset.id()).isEqualTo(id);
            assertThat(preset.idValue()).isEqualTo(1L);
            assertThat(preset.shopId()).isEqualTo(shopId);
            assertThat(preset.isActive()).isTrue();
            assertThat(preset.createdAt()).isEqualTo(createdAt);
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 브랜드 프리셋을 복원한다")
        void reconstituteInactiveBrandPreset() {
            // when
            BrandPreset preset = BrandPresetFixtures.inactiveBrandPreset();

            // then
            assertThat(preset.isActive()).isFalse();
            assertThat(preset.status()).isEqualTo(BrandPresetStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("update() - 프리셋 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("프리셋 이름과 판매채널 브랜드 ID를 수정한다")
        void updatePresetInfo() {
            // given
            BrandPreset preset = BrandPresetFixtures.activeBrandPreset();
            String newName = "수정된 프리셋";
            Long newSalesChannelBrandId = 200L;
            Instant now = CommonVoFixtures.now();

            // when
            preset.update(newName, newSalesChannelBrandId, now);

            // then
            assertThat(preset.presetName()).isEqualTo(newName);
            assertThat(preset.salesChannelBrandId()).isEqualTo(newSalesChannelBrandId);
            assertThat(preset.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("deactivate() - 비활성화")
    class DeactivateTest {

        @Test
        @DisplayName("활성 프리셋을 비활성화한다")
        void deactivateActivePreset() {
            // given
            BrandPreset preset = BrandPresetFixtures.activeBrandPreset();
            Instant now = CommonVoFixtures.now();

            // when
            preset.deactivate(now);

            // then
            assertThat(preset.isActive()).isFalse();
            assertThat(preset.status()).isEqualTo(BrandPresetStatus.INACTIVE);
            assertThat(preset.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            BrandPreset preset = BrandPresetFixtures.activeBrandPreset(100L);

            // when & then
            assertThat(preset.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("shopId()는 Shop ID를 반환한다")
        void shopIdReturnsShopId() {
            // given
            BrandPreset preset = BrandPresetFixtures.activeBrandPreset();

            // when & then
            assertThat(preset.shopId()).isEqualTo(BrandPresetFixtures.DEFAULT_SHOP_ID);
        }
    }
}
