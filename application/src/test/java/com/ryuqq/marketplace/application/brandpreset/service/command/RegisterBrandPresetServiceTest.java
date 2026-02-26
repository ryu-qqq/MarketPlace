package com.ryuqq.marketplace.application.brandpreset.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetCommandFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.RegisterBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.internal.BrandPresetMappingFacade;
import com.ryuqq.marketplace.application.brandpreset.validator.BrandPresetValidator;
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
@DisplayName("RegisterBrandPresetService 단위 테스트")
class RegisterBrandPresetServiceTest {

    @InjectMocks private RegisterBrandPresetService sut;

    @Mock private BrandPresetValidator validator;
    @Mock private BrandPresetCommandFactory commandFactory;
    @Mock private BrandPresetMappingFacade facade;

    @Nested
    @DisplayName("execute() - 브랜드 프리셋 등록")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 브랜드 프리셋을 등록하고 ID를 반환한다")
        void execute_ValidCommand_ReturnsBrandPresetId() {
            // given
            RegisterBrandPresetCommand command = BrandPresetCommandFixtures.registerCommand();
            RegisterBrandPresetBundle bundle = BrandPresetCommandFixtures.registerBundle();
            Long expectedId = 1L;

            given(commandFactory.createRegisterBundle(command)).willReturn(bundle);
            given(facade.registerWithMappings(bundle)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(validator)
                    .should()
                    .validateSameChannel(command.shopId(), command.salesChannelBrandId());
            then(validator).should().validateInternalBrandsExist(command.internalBrandIds());
            then(commandFactory).should().createRegisterBundle(command);
            then(facade).should().registerWithMappings(bundle);
        }

        @Test
        @DisplayName("내부 브랜드가 없어도 프리셋을 등록할 수 있다")
        void execute_WithoutInternalBrands_RegistersPreset() {
            // given
            RegisterBrandPresetCommand command =
                    BrandPresetCommandFixtures.registerCommandWithBrands(null);
            RegisterBrandPresetBundle existingBundle = BrandPresetCommandFixtures.registerBundle();
            RegisterBrandPresetBundle bundle =
                    BrandPresetCommandFixtures.registerBundle(existingBundle.brandPreset(), null);
            Long expectedId = 1L;

            given(commandFactory.createRegisterBundle(command)).willReturn(bundle);
            given(facade.registerWithMappings(bundle)).willReturn(expectedId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedId);
        }
    }
}
