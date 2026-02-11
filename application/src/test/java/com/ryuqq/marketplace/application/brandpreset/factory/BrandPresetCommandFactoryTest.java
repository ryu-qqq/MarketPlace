package com.ryuqq.marketplace.application.brandpreset.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetCommandFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.RegisterBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.bundle.UpdateBrandPresetBundle;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
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
@DisplayName("BrandPresetCommandFactory 단위 테스트")
class BrandPresetCommandFactoryTest {

    @InjectMocks private BrandPresetCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createRegisterBundle() - 등록 번들 생성")
    class CreateRegisterBundleTest {

        @Test
        @DisplayName("등록 커맨드로부터 RegisterBrandPresetBundle을 생성한다")
        void createRegisterBundle_ValidCommand_ReturnsBundle() {
            // given
            RegisterBrandPresetCommand command = BrandPresetCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            RegisterBrandPresetBundle bundle = sut.createRegisterBundle(command);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.brandPreset()).isNotNull();
            assertThat(bundle.salesChannelBrandId()).isEqualTo(command.salesChannelBrandId());
            assertThat(bundle.internalBrandIds()).isEqualTo(command.internalBrandIds());
            assertThat(bundle.now()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("내부 브랜드 ID가 null이어도 번들을 생성할 수 있다")
        void createRegisterBundle_NullInternalBrandIds_ReturnsBundle() {
            // given
            RegisterBrandPresetCommand command =
                    BrandPresetCommandFixtures.registerCommandWithBrands(null);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            RegisterBrandPresetBundle bundle = sut.createRegisterBundle(command);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.internalBrandIds()).isNull();
        }
    }

    @Nested
    @DisplayName("createUpdateBundle() - 수정 번들 생성")
    class CreateUpdateBundleTest {

        @Test
        @DisplayName("수정 커맨드로부터 UpdateBrandPresetBundle을 생성한다")
        void createUpdateBundle_ValidCommand_ReturnsBundle() {
            // given
            BrandPreset existing = BrandPresetFixtures.activeBrandPreset();
            UpdateBrandPresetCommand command =
                    BrandPresetCommandFixtures.updateCommand(existing.idValue());
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateBrandPresetBundle bundle = sut.createUpdateBundle(existing, command);

            // then
            assertThat(bundle).isNotNull();
            assertThat(bundle.brandPreset()).isEqualTo(existing);
            assertThat(bundle.presetName()).isEqualTo(command.presetName());
            assertThat(bundle.salesChannelBrandId()).isEqualTo(command.salesChannelBrandId());
            assertThat(bundle.brandMappings()).isNotEmpty();
            assertThat(bundle.brandMappings()).hasSize(command.internalBrandIds().size());
            assertThat(bundle.now()).isEqualTo(now);
            then(timeProvider).should().now();
        }

        @Test
        @DisplayName("내부 브랜드 ID가 비어있으면 빈 매핑 목록을 생성한다")
        void createUpdateBundle_EmptyInternalBrandIds_ReturnsEmptyMappings() {
            // given
            BrandPreset existing = BrandPresetFixtures.activeBrandPreset();
            UpdateBrandPresetCommand command =
                    BrandPresetCommandFixtures.updateCommandWithBrands(existing.idValue(), List.of());
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateBrandPresetBundle bundle = sut.createUpdateBundle(existing, command);

            // then
            assertThat(bundle.brandMappings()).isEmpty();
        }
    }

    @Nested
    @DisplayName("createDeactivateContext() - 비활성화 컨텍스트 생성")
    class CreateDeactivateContextTest {

        @Test
        @DisplayName("삭제 커맨드로부터 StatusChangeContext를 생성한다")
        void createDeactivateContext_ValidCommand_ReturnsContext() {
            // given
            DeleteBrandPresetsCommand command = BrandPresetCommandFixtures.deleteCommand();
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            StatusChangeContext<List<Long>> context = sut.createDeactivateContext(command);

            // then
            assertThat(context).isNotNull();
            assertThat(context.id()).isEqualTo(command.ids());
            assertThat(context.changedAt()).isEqualTo(now);
            then(timeProvider).should().now();
        }
    }
}
