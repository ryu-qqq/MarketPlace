package com.ryuqq.marketplace.application.categorypreset.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetCommandFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.internal.CategoryPresetMappingFacade;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetReadManager;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
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
@DisplayName("DeleteCategoryPresetsService 단위 테스트")
class DeleteCategoryPresetsServiceTest {

    @InjectMocks private DeleteCategoryPresetsService sut;

    @Mock private CategoryPresetReadManager readManager;
    @Mock private CategoryPresetCommandFactory commandFactory;
    @Mock private CategoryPresetMappingFacade facade;

    @Nested
    @DisplayName("execute() - 카테고리 프리셋 삭제(비활성화)")
    class ExecuteTest {

        @Test
        @DisplayName("여러 카테고리 프리셋을 비활성화한다")
        void execute_DeactivatesMultiplePresets() {
            // given
            DeleteCategoryPresetsCommand command = CategoryPresetCommandFixtures.deleteCommand();
            Instant now = CommonVoFixtures.now();
            StatusChangeContext<List<Long>> context = new StatusChangeContext<>(command.ids(), now);
            List<CategoryPreset> presets =
                    List.of(
                            CategoryPresetFixtures.activeCategoryPreset(1L),
                            CategoryPresetFixtures.activeCategoryPreset(2L),
                            CategoryPresetFixtures.activeCategoryPreset(3L));
            int expectedDeactivated = 3;

            given(commandFactory.createDeactivateContext(command)).willReturn(context);
            given(readManager.findAllByIds(context.id())).willReturn(presets);
            given(facade.deactivateWithMappings(presets, context.changedAt()))
                    .willReturn(expectedDeactivated);

            // when
            int result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedDeactivated);
            then(commandFactory).should().createDeactivateContext(command);
            then(readManager).should().findAllByIds(context.id());
            then(facade).should().deactivateWithMappings(presets, context.changedAt());
        }

        @Test
        @DisplayName("단일 카테고리 프리셋을 비활성화한다")
        void execute_SinglePreset_Deactivates() {
            // given
            DeleteCategoryPresetsCommand command =
                    CategoryPresetCommandFixtures.deleteCommand(List.of(1L));
            Instant now = CommonVoFixtures.now();
            StatusChangeContext<List<Long>> context = new StatusChangeContext<>(command.ids(), now);
            List<CategoryPreset> presets = List.of(CategoryPresetFixtures.activeCategoryPreset(1L));
            int expectedDeactivated = 1;

            given(commandFactory.createDeactivateContext(command)).willReturn(context);
            given(readManager.findAllByIds(context.id())).willReturn(presets);
            given(facade.deactivateWithMappings(presets, context.changedAt()))
                    .willReturn(expectedDeactivated);

            // when
            int result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedDeactivated);
            then(facade).should().deactivateWithMappings(presets, context.changedAt());
        }
    }
}
