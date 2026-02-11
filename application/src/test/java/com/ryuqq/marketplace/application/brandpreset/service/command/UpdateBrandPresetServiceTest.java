package com.ryuqq.marketplace.application.brandpreset.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetCommandFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.UpdateBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.internal.BrandPresetMappingFacade;
import com.ryuqq.marketplace.application.brandpreset.validator.BrandPresetValidator;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
import com.ryuqq.marketplace.domain.brandpreset.id.BrandPresetId;
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
@DisplayName("UpdateBrandPresetService 단위 테스트")
class UpdateBrandPresetServiceTest {

    @InjectMocks private UpdateBrandPresetService sut;

    @Mock private BrandPresetValidator validator;
    @Mock private BrandPresetCommandFactory commandFactory;
    @Mock private BrandPresetMappingFacade facade;

    @Nested
    @DisplayName("execute() - 브랜드 프리셋 수정")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드로 브랜드 프리셋을 수정한다")
        void execute_ValidCommand_UpdatesBrandPreset() {
            // given
            Long brandPresetId = 1L;
            UpdateBrandPresetCommand command =
                    BrandPresetCommandFixtures.updateCommand(brandPresetId);
            BrandPreset existing = BrandPresetFixtures.activeBrandPreset(brandPresetId);
            UpdateBrandPresetBundle bundle = BrandPresetCommandFixtures.updateBundle();

            given(validator.findExistingOrThrow(BrandPresetId.of(brandPresetId)))
                    .willReturn(existing);
            given(commandFactory.createUpdateBundle(existing, command)).willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(validator).should().findExistingOrThrow(BrandPresetId.of(brandPresetId));
            then(validator)
                    .should()
                    .validateSameChannel(existing.shopId(), command.salesChannelBrandId());
            then(validator).should().validateInternalBrandsExist(command.internalBrandIds());
            then(commandFactory).should().createUpdateBundle(existing, command);
            then(facade).should().updateWithMappings(bundle);
        }

        @Test
        @DisplayName("프리셋명만 변경할 수 있다")
        void execute_UpdatePresetNameOnly_Updates() {
            // given
            Long brandPresetId = 1L;
            String newPresetName = "새 프리셋명";
            UpdateBrandPresetCommand command =
                    BrandPresetCommandFixtures.updateCommand(brandPresetId, newPresetName);
            BrandPreset existing = BrandPresetFixtures.activeBrandPreset(brandPresetId);
            UpdateBrandPresetBundle bundle =
                    BrandPresetCommandFixtures.updateBundle(existing, newPresetName);

            given(validator.findExistingOrThrow(BrandPresetId.of(brandPresetId)))
                    .willReturn(existing);
            given(commandFactory.createUpdateBundle(existing, command)).willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createUpdateBundle(existing, command);
            then(facade).should().updateWithMappings(bundle);
        }
    }
}
