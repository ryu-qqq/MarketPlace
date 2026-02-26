package com.ryuqq.marketplace.application.brandpreset.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetCommandFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.factory.BrandPresetCommandFactory;
import com.ryuqq.marketplace.application.brandpreset.internal.BrandPresetMappingFacade;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetReadManager;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.domain.brandpreset.BrandPresetFixtures;
import com.ryuqq.marketplace.domain.brandpreset.aggregate.BrandPreset;
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
@DisplayName("DeleteBrandPresetsService 단위 테스트")
class DeleteBrandPresetsServiceTest {

    @InjectMocks private DeleteBrandPresetsService sut;

    @Mock private BrandPresetReadManager readManager;
    @Mock private BrandPresetCommandFactory commandFactory;
    @Mock private BrandPresetMappingFacade facade;

    @Nested
    @DisplayName("execute() - 브랜드 프리셋 벌크 비활성화")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 ID 목록으로 브랜드 프리셋을 비활성화한다")
        void execute_ValidIds_DeactivatesPresets() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);
            DeleteBrandPresetsCommand command = BrandPresetCommandFixtures.deleteCommand(ids);
            Instant now = CommonVoFixtures.now();
            StatusChangeContext<List<Long>> context = new StatusChangeContext<>(ids, now);
            List<BrandPreset> brandPresets =
                    List.of(
                            BrandPresetFixtures.activeBrandPreset(1L),
                            BrandPresetFixtures.activeBrandPreset(2L),
                            BrandPresetFixtures.activeBrandPreset(3L));
            int expectedCount = 3;

            given(commandFactory.createDeactivateContext(command)).willReturn(context);
            given(readManager.findAllByIds(ids)).willReturn(brandPresets);
            given(facade.deactivateWithMappings(brandPresets, now)).willReturn(expectedCount);

            // when
            int result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedCount);
            then(commandFactory).should().createDeactivateContext(command);
            then(readManager).should().findAllByIds(ids);
            then(facade).should().deactivateWithMappings(brandPresets, now);
        }

        @Test
        @DisplayName("단일 프리셋도 비활성화할 수 있다")
        void execute_SingleId_DeactivatesPreset() {
            // given
            List<Long> ids = List.of(1L);
            DeleteBrandPresetsCommand command = BrandPresetCommandFixtures.deleteCommand(ids);
            Instant now = CommonVoFixtures.now();
            StatusChangeContext<List<Long>> context = new StatusChangeContext<>(ids, now);
            List<BrandPreset> brandPresets = List.of(BrandPresetFixtures.activeBrandPreset(1L));
            int expectedCount = 1;

            given(commandFactory.createDeactivateContext(command)).willReturn(context);
            given(readManager.findAllByIds(ids)).willReturn(brandPresets);
            given(facade.deactivateWithMappings(brandPresets, now)).willReturn(expectedCount);

            // when
            int result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }
}
