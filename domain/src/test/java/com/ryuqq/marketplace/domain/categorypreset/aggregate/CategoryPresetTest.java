package com.ryuqq.marketplace.domain.categorypreset.aggregate;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
import com.ryuqq.marketplace.domain.categorypreset.vo.CategoryPresetStatus;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPreset Aggregate 테스트")
class CategoryPresetTest {

    @Nested
    @DisplayName("forNew() - 신규 카테고리 프리셋 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 카테고리 프리셋을 생성한다")
        void createNewCategoryPresetWithRequiredFields() {
            // given
            Long shopId = 1L;
            Long salesChannelCategoryId = 200L;
            String presetName = "테스트 프리셋";
            Instant now = CommonVoFixtures.now();

            // when
            CategoryPreset preset =
                    CategoryPreset.forNew(shopId, salesChannelCategoryId, presetName, now);

            // then
            assertThat(preset.id().isNew()).isTrue();
            assertThat(preset.shopId()).isEqualTo(shopId);
            assertThat(preset.salesChannelCategoryId()).isEqualTo(salesChannelCategoryId);
            assertThat(preset.presetName()).isEqualTo(presetName);
            assertThat(preset.status()).isEqualTo(CategoryPresetStatus.ACTIVE);
            assertThat(preset.isActive()).isTrue();
            assertThat(preset.createdAt()).isEqualTo(now);
            assertThat(preset.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 활성 상태의 카테고리 프리셋을 복원한다")
        void reconstituteActiveCategoryPreset() {
            // given
            CategoryPresetId id = CategoryPresetId.of(1L);
            Instant createdAt = CommonVoFixtures.yesterday();

            // when
            CategoryPreset preset =
                    CategoryPreset.reconstitute(
                            id, 1L, 200L, "테스트", CategoryPresetStatus.ACTIVE, createdAt, createdAt);

            // then
            assertThat(preset.id()).isEqualTo(id);
            assertThat(preset.idValue()).isEqualTo(1L);
            assertThat(preset.isActive()).isTrue();
        }

        @Test
        @DisplayName("영속성에서 비활성 상태의 카테고리 프리셋을 복원한다")
        void reconstituteInactiveCategoryPreset() {
            // when
            CategoryPreset preset = CategoryPresetFixtures.inactiveCategoryPreset();

            // then
            assertThat(preset.isActive()).isFalse();
            assertThat(preset.status()).isEqualTo(CategoryPresetStatus.INACTIVE);
        }
    }

    @Nested
    @DisplayName("update() - 프리셋 정보 수정")
    class UpdateTest {

        @Test
        @DisplayName("프리셋 이름과 판매채널 카테고리 ID를 수정한다")
        void updatePresetInfo() {
            // given
            CategoryPreset preset = CategoryPresetFixtures.activeCategoryPreset();
            String newName = "수정된 프리셋";
            Long newSalesChannelCategoryId = 300L;
            Instant now = CommonVoFixtures.now();

            // when
            preset.update(newName, newSalesChannelCategoryId, now);

            // then
            assertThat(preset.presetName()).isEqualTo(newName);
            assertThat(preset.salesChannelCategoryId()).isEqualTo(newSalesChannelCategoryId);
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
            CategoryPreset preset = CategoryPresetFixtures.activeCategoryPreset();
            Instant now = CommonVoFixtures.now();

            // when
            preset.deactivate(now);

            // then
            assertThat(preset.isActive()).isFalse();
            assertThat(preset.status()).isEqualTo(CategoryPresetStatus.INACTIVE);
            assertThat(preset.updatedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            CategoryPreset preset = CategoryPresetFixtures.activeCategoryPreset(100L);
            assertThat(preset.idValue()).isEqualTo(100L);
        }

        @Test
        @DisplayName("shopId()는 Shop ID를 반환한다")
        void shopIdReturnsShopId() {
            CategoryPreset preset = CategoryPresetFixtures.activeCategoryPreset();
            assertThat(preset.shopId()).isEqualTo(CategoryPresetFixtures.DEFAULT_SHOP_ID);
        }
    }
}
