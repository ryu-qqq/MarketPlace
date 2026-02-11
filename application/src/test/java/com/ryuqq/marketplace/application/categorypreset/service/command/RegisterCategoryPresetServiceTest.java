package com.ryuqq.marketplace.application.categorypreset.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetCommandFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.bundle.RegisterCategoryPresetBundle;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.factory.CategoryPresetCommandFactory;
import com.ryuqq.marketplace.application.categorypreset.internal.CategoryPresetMappingFacade;
import com.ryuqq.marketplace.application.categorypreset.validator.CategoryPresetValidator;
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
@DisplayName("RegisterCategoryPresetService 단위 테스트")
class RegisterCategoryPresetServiceTest {

    @InjectMocks private RegisterCategoryPresetService sut;

    @Mock private CategoryPresetValidator validator;
    @Mock private CategoryPresetCommandFactory commandFactory;
    @Mock private CategoryPresetMappingFacade facade;

    @Nested
    @DisplayName("execute() - 카테고리 프리셋 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 카테고리 프리셋을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsCategoryPresetId() {
            // given
            RegisterCategoryPresetCommand command = CategoryPresetCommandFixtures.registerCommand();
            Long salesChannelCategoryId = 200L;
            RegisterCategoryPresetBundle bundle = CategoryPresetCommandFixtures.registerBundle();
            Long expectedId = 1L;

            given(validator.resolveSalesChannelCategoryId(command.shopId(), command.categoryCode()))
                    .willReturn(salesChannelCategoryId);
            given(commandFactory.createRegisterBundle(command, salesChannelCategoryId))
                    .willReturn(bundle);
            given(facade.registerWithMappings(bundle)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator)
                    .should()
                    .resolveSalesChannelCategoryId(command.shopId(), command.categoryCode());
            then(validator).should().validateInternalCategoriesExist(command.internalCategoryIds());
            then(commandFactory).should().createRegisterBundle(command, salesChannelCategoryId);
            then(facade).should().registerWithMappings(bundle);
        }

        @Test
        @DisplayName("내부 카테고리가 없어도 프리셋을 등록할 수 있다")
        void execute_WithoutInternalCategories_RegistersPreset() {
            // given
            RegisterCategoryPresetCommand command =
                    CategoryPresetCommandFixtures.registerCommandWithCategories(null);
            Long salesChannelCategoryId = 200L;
            RegisterCategoryPresetBundle existingBundle =
                    CategoryPresetCommandFixtures.registerBundle();
            RegisterCategoryPresetBundle bundle =
                    CategoryPresetCommandFixtures.registerBundle(
                            existingBundle.categoryPreset(), null);
            Long expectedId = 1L;

            given(validator.resolveSalesChannelCategoryId(command.shopId(), command.categoryCode()))
                    .willReturn(salesChannelCategoryId);
            given(commandFactory.createRegisterBundle(command, salesChannelCategoryId))
                    .willReturn(bundle);
            given(facade.registerWithMappings(bundle)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }
}
