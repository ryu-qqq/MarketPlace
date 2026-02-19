package com.ryuqq.marketplace.application.externalsource.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.externalsource.ExternalSourceCommandFixtures;
import com.ryuqq.marketplace.application.externalsource.dto.command.RegisterExternalSourceCommand;
import com.ryuqq.marketplace.application.externalsource.dto.command.UpdateExternalSourceCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.externalsource.aggregate.ExternalSource;
import com.ryuqq.marketplace.domain.externalsource.vo.ExternalSourceUpdateData;
import java.time.Instant;
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
@DisplayName("ExternalSourceCommandFactory 단위 테스트")
class ExternalSourceCommandFactoryTest {

    @InjectMocks private ExternalSourceCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - 등록 도메인 객체 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterCommand로 ExternalSource를 생성한다")
        void create_ValidCommand_ReturnsDomainObject() {
            // given
            RegisterExternalSourceCommand command = ExternalSourceCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ExternalSource result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.codeValue()).isEqualTo(command.code());
            assertThat(result.name()).isEqualTo(command.name());
            assertThat(result.type().name()).isEqualTo(command.type());
            assertThat(result.description()).isEqualTo(command.description());
            assertThat(result.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("설명이 없는 RegisterCommand로 ExternalSource를 생성한다")
        void create_WithoutDescription_ReturnsDomainObject() {
            // given
            RegisterExternalSourceCommand command =
                    ExternalSourceCommandFixtures.registerCommandWithoutDescription();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ExternalSource result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.description()).isNull();
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - 수정 UpdateContext 생성")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateCommand로 UpdateContext를 생성한다")
        void createUpdateContext_ValidCommand_ReturnsUpdateContext() {
            // given
            Long externalSourceId = 1L;
            UpdateExternalSourceCommand command =
                    ExternalSourceCommandFixtures.updateCommand(externalSourceId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, ExternalSourceUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(externalSourceId);
            assertThat(result.updateData().name()).isEqualTo(command.name());
            assertThat(result.updateData().description()).isEqualTo(command.description());
            assertThat(result.updateData().status().name()).isEqualTo("ACTIVE");
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태의 UpdateCommand로 UpdateContext를 생성한다")
        void createUpdateContext_InactiveStatus_ReturnsUpdateContextWithInactive() {
            // given
            Long externalSourceId = 1L;
            UpdateExternalSourceCommand command =
                    ExternalSourceCommandFixtures.updateCommandWithInactive(externalSourceId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, ExternalSourceUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().status().name()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("설명이 없는 UpdateCommand로 UpdateContext를 생성한다")
        void createUpdateContext_WithoutDescription_ReturnsUpdateContextWithNullDescription() {
            // given
            Long externalSourceId = 1L;
            UpdateExternalSourceCommand command =
                    ExternalSourceCommandFixtures.updateCommandWithoutDescription(externalSourceId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, ExternalSourceUpdateData> result = sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().description()).isNull();
        }
    }
}
