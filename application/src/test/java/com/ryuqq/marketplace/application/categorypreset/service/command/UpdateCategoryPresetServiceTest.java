package com.ryuqq.marketplace.application.categorypreset.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetCommandFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.UpdateCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.internal.CategoryPresetMappingFacade;
import com.ryuqq.marketplace.application.categorypreset.validator.CategoryPresetValidator;
import com.ryuqq.marketplace.domain.categorypreset.CategoryPresetFixtures;
import com.ryuqq.marketplace.domain.categorypreset.aggregate.CategoryPreset;
import com.ryuqq.marketplace.domain.categorypreset.id.CategoryPresetId;
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
@DisplayName("UpdateCategoryPresetService 단위 테스트")
class UpdateCategoryPresetServiceTest {

    @InjectMocks private UpdateCategoryPresetService sut;

    @Mock private CategoryPresetValidator validator;
    @Mock private CategoryPresetCommandFactory commandFactory;
    @Mock private CategoryPresetMappingFacade facade;

    @Nested
    @DisplayName("execute() - 카테고리 프리셋 수정")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 카테고리 프리셋을 수정한다")
        void execute_ValidCommand_UpdatesCategoryPreset() {
            // given
            Long categoryPresetId = 1L;
            UpdateCategoryPresetCommand command =
                    CategoryPresetCommandFixtures.updateCommand(categoryPresetId);
            CategoryPreset existing = CategoryPresetFixtures.activeCategoryPreset(categoryPresetId);
            Long newSalesChannelCategoryId = 300L;
            UpdateCategoryPresetBundle bundle =
                    CategoryPresetCommandFixtures.updateBundle(existing, command.presetName());

            given(validator.findExistingOrThrow(CategoryPresetId.of(categoryPresetId)))
                    .willReturn(existing);
            given(
                            validator.resolveSalesChannelCategoryId(
                                    existing.shopId(), command.categoryCode()))
                    .willReturn(newSalesChannelCategoryId);
            given(commandFactory.createUpdateBundle(existing, command, newSalesChannelCategoryId))
                    .willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(validator).should().findExistingOrThrow(CategoryPresetId.of(categoryPresetId));
            then(validator)
                    .should()
                    .resolveSalesChannelCategoryId(existing.shopId(), command.categoryCode());
            then(validator).should().validateInternalCategoriesExist(command.internalCategoryIds());
            then(commandFactory)
                    .should()
                    .createUpdateBundle(existing, command, newSalesChannelCategoryId);
            then(facade).should().updateWithMappings(bundle);
        }

        @Test
        @DisplayName("카테고리 코드를 변경하여 수정할 수 있다")
        void execute_WithCategoryCodeChange_UpdatesWithNewCategoryId() {
            // given
            Long categoryPresetId = 1L;
            String newCategoryCode = "NEW_CATEGORY_CODE";
            UpdateCategoryPresetCommand command =
                    new UpdateCategoryPresetCommand(
                            categoryPresetId, "수정된 프리셋명", newCategoryCode, List.of(1L, 2L));
            CategoryPreset existing = CategoryPresetFixtures.activeCategoryPreset(categoryPresetId);
            Long newSalesChannelCategoryId = 300L;
            UpdateCategoryPresetBundle bundle =
                    CategoryPresetCommandFixtures.updateBundle(existing, command.presetName());

            given(validator.findExistingOrThrow(CategoryPresetId.of(categoryPresetId)))
                    .willReturn(existing);
            given(validator.resolveSalesChannelCategoryId(existing.shopId(), newCategoryCode))
                    .willReturn(newSalesChannelCategoryId);
            given(commandFactory.createUpdateBundle(existing, command, newSalesChannelCategoryId))
                    .willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(validator)
                    .should()
                    .resolveSalesChannelCategoryId(existing.shopId(), newCategoryCode);
            then(commandFactory)
                    .should()
                    .createUpdateBundle(existing, command, newSalesChannelCategoryId);
        }

        @Test
        @DisplayName("내부 카테고리 목록을 변경하여 수정할 수 있다")
        void execute_WithDifferentCategories_UpdatesMapping() {
            // given
            Long categoryPresetId = 1L;
            UpdateCategoryPresetCommand command =
                    CategoryPresetCommandFixtures.updateCommandWithCategories(
                            categoryPresetId, List.of(4L, 5L, 6L));
            CategoryPreset existing = CategoryPresetFixtures.activeCategoryPreset(categoryPresetId);
            Long newSalesChannelCategoryId = 300L;
            UpdateCategoryPresetBundle bundle =
                    CategoryPresetCommandFixtures.updateBundle(existing, command.presetName());

            given(validator.findExistingOrThrow(CategoryPresetId.of(categoryPresetId)))
                    .willReturn(existing);
            given(
                            validator.resolveSalesChannelCategoryId(
                                    existing.shopId(), command.categoryCode()))
                    .willReturn(newSalesChannelCategoryId);
            given(commandFactory.createUpdateBundle(existing, command, newSalesChannelCategoryId))
                    .willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(validator).should().validateInternalCategoriesExist(command.internalCategoryIds());
            then(facade).should().updateWithMappings(bundle);
        }
    }
}
