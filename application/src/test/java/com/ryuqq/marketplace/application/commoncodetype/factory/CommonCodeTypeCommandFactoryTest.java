package com.ryuqq.marketplace.application.commoncodetype.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.commoncodetype.CommonCodeTypeCommandFixtures;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeTypeUpdateData;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
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
@DisplayName("CommonCodeTypeCommandFactory 단위 테스트")
class CommonCodeTypeCommandFactoryTest {

    @InjectMocks private CommonCodeTypeCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - CommonCodeType 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterCommonCodeTypeCommand로 CommonCodeType을 생성한다")
        void create_FromCommand_ReturnsCommonCodeType() {
            // given
            RegisterCommonCodeTypeCommand command = CommonCodeTypeCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            CommonCodeType result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.code()).isEqualTo(command.code());
            assertThat(result.name()).isEqualTo(command.name());
            assertThat(result.description()).isEqualTo(command.description());
            assertThat(result.displayOrder()).isEqualTo(command.displayOrder());
            assertThat(result.isNew()).isTrue();
            assertThat(result.isActive()).isTrue();
        }

        @Test
        @DisplayName("생성된 CommonCodeType은 신규 상태이다")
        void create_ReturnsNewCommonCodeType() {
            // given
            RegisterCommonCodeTypeCommand command =
                    CommonCodeTypeCommandFixtures.registerCommand("NEW_CODE");
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            CommonCodeType result = sut.create(command);

            // then
            assertThat(result.isNew()).isTrue();
            assertThat(result.isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - UpdateContext 생성")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateCommonCodeTypeCommand로 UpdateContext를 생성한다")
        void createUpdateContext_FromCommand_ReturnsUpdateContext() {
            // given
            Long targetId = 1L;
            UpdateCommonCodeTypeCommand command =
                    CommonCodeTypeCommandFixtures.updateCommand(targetId);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<CommonCodeTypeId, CommonCodeTypeUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(targetId);
            assertThat(result.updateData().name().value()).isEqualTo(command.name());
            assertThat(result.updateData().description().value()).isEqualTo(command.description());
            assertThat(result.updateData().displayOrder().value())
                    .isEqualTo(command.displayOrder());
            assertThat(result.changedAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createStatusChangeContexts() - StatusChangeContext 목록 생성")
    class CreateStatusChangeContextsTest {

        @Test
        @DisplayName("단일 ID로 StatusChangeContext 목록을 생성한다")
        void createStatusChangeContexts_SingleId_ReturnsSingleContext() {
            // given
            Long targetId = 1L;
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.activateCommand(targetId);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            List<StatusChangeContext<CommonCodeTypeId>> result =
                    sut.createStatusChangeContexts(command);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id().value()).isEqualTo(targetId);
            assertThat(result.get(0).changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("복수 ID로 StatusChangeContext 목록을 생성한다")
        void createStatusChangeContexts_MultipleIds_ReturnsMultipleContexts() {
            // given
            List<Long> targetIds = List.of(1L, 2L, 3L);
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.changeStatusCommand(targetIds, false);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            List<StatusChangeContext<CommonCodeTypeId>> result =
                    sut.createStatusChangeContexts(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.stream().map(ctx -> ctx.id().value()).toList())
                    .containsExactlyElementsOf(targetIds);
            assertThat(result.stream().allMatch(ctx -> ctx.changedAt().equals(now))).isTrue();
        }

        @Test
        @DisplayName("빈 ID 목록이면 빈 리스트를 반환한다")
        void createStatusChangeContexts_EmptyIds_ReturnsEmptyList() {
            // given
            ChangeActiveStatusCommand command =
                    CommonCodeTypeCommandFixtures.changeStatusCommand(List.of(), true);
            Instant now = CommonVoFixtures.now();

            given(timeProvider.now()).willReturn(now);

            // when
            List<StatusChangeContext<CommonCodeTypeId>> result =
                    sut.createStatusChangeContexts(command);

            // then
            assertThat(result).isEmpty();
        }
    }
}
