package com.ryuqq.marketplace.application.categorypreset.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetCommandFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.RegisterCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.UpdateCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CategoryPresetCommandFactory 단위 테스트")
class CategoryPresetCommandFactoryTest {

    @InjectMocks private CategoryPresetCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createRegisterBundle() - 등록 Bundle 생성")
    class CreateRegisterBundleTest {

        @Test
        @DisplayName("RegisterCategoryPresetCommand로 RegisterCategoryPresetBundle을 생성한다")
        void createRegisterBundle_ReturnsBundle() {
            // given
            RegisterCategoryPresetCommand command = CategoryPresetCommandFixtures.registerCommand();
            Long salesChannelCategoryId = 200L;
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            RegisterCategoryPresetBundle result =
                    sut.createRegisterBundle(command, salesChannelCategoryId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categoryPreset()).isNotNull();
            assertThat(result.categoryPreset().shopId()).isEqualTo(command.shopId());
            assertThat(result.categoryPreset().presetName()).isEqualTo(command.presetName());
            assertThat(result.salesChannelCategoryId()).isEqualTo(salesChannelCategoryId);
            assertThat(result.internalCategoryIds()).isEqualTo(command.internalCategoryIds());
            assertThat(result.now()).isEqualTo(now);
        }

        @Test
        @DisplayName("내부 카테고리 ID가 없어도 Bundle을 생성한다")
        void createRegisterBundle_WithoutCategories_ReturnsBundle() {
            // given
            RegisterCategoryPresetCommand command =
                    CategoryPresetCommandFixtures.registerCommandWithCategories(null);
            Long salesChannelCategoryId = 200L;
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            RegisterCategoryPresetBundle result =
                    sut.createRegisterBundle(command, salesChannelCategoryId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.internalCategoryIds()).isNull();
        }
    }

    @Nested
    @DisplayName("createUpdateBundle() - 수정 Bundle 생성")
    class CreateUpdateBundleTest {

        @Test
        @DisplayName("UpdateCategoryPresetCommand로 UpdateCategoryPresetBundle을 생성한다")
        void createUpdateBundle_ReturnsBundle() {
            // given
            CategoryPreset existing = CategoryPresetFixtures.activeCategoryPreset(1L);
            UpdateCategoryPresetCommand command = CategoryPresetCommandFixtures.updateCommand(1L);
            Long salesChannelCategoryId = CategoryPresetFixtures.DEFAULT_SALES_CHANNEL_CATEGORY_ID;
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateCategoryPresetBundle result =
                    sut.createUpdateBundle(existing, command, salesChannelCategoryId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.categoryPreset()).isEqualTo(existing);
            assertThat(result.presetName()).isEqualTo(command.presetName());
            assertThat(result.salesChannelCategoryId()).isEqualTo(salesChannelCategoryId);
            assertThat(result.categoryMappings()).isNotEmpty();
            assertThat(result.now()).isEqualTo(now);
        }

        @Test
        @DisplayName("내부 카테고리 ID가 비어있으면 빈 매핑 목록을 생성한다")
        void createUpdateBundle_WithEmptyCategories_ReturnsEmptyMappings() {
            // given
            CategoryPreset existing = CategoryPresetFixtures.activeCategoryPreset(1L);
            UpdateCategoryPresetCommand command =
                    CategoryPresetCommandFixtures.updateCommandWithCategories(1L, List.of());
            Long salesChannelCategoryId = CategoryPresetFixtures.DEFAULT_SALES_CHANNEL_CATEGORY_ID;
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateCategoryPresetBundle result =
                    sut.createUpdateBundle(existing, command, salesChannelCategoryId);

            // then
            assertThat(result.categoryMappings()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createDeactivateContext() - 비활성화 Context 생성")
    class CreateDeactivateContextTest {

        @Test
        @DisplayName("DeleteCategoryPresetsCommand로 StatusChangeContext를 생성한다")
        void createDeactivateContext_ReturnsContext() {
            // given
            DeleteCategoryPresetsCommand command = CategoryPresetCommandFixtures.deleteCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            StatusChangeContext<List<Long>> result = sut.createDeactivateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(command.ids());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("단일 ID로도 Context를 생성한다")
        void createDeactivateContext_SingleId_ReturnsContext() {
            // given
            DeleteCategoryPresetsCommand command =
                    CategoryPresetCommandFixtures.deleteCommand(List.of(1L));
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            StatusChangeContext<List<Long>> result = sut.createDeactivateContext(command);

            // then
            assertThat(result.id()).hasSize(1);
            assertThat(result.id()).containsExactly(1L);
        }
    }
}
