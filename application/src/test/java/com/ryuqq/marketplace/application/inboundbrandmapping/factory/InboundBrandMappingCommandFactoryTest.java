package com.ryuqq.marketplace.application.inboundbrandmapping.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundbrandmapping.InboundBrandMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingUpdateData;
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
@DisplayName("InboundBrandMappingCommandFactory 단위 테스트")
class InboundBrandMappingCommandFactoryTest {

    @InjectMocks private InboundBrandMappingCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - 단건 등록 도메인 객체 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterCommand로 InboundBrandMapping을 생성한다")
        void create_ValidCommand_ReturnsDomainObject() {
            // given
            RegisterInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            InboundBrandMapping result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.inboundSourceId()).isEqualTo(command.inboundSourceId());
            assertThat(result.externalBrandCode()).isEqualTo(command.externalBrandCode());
            assertThat(result.externalBrandName()).isEqualTo(command.externalBrandName());
            assertThat(result.internalBrandId()).isEqualTo(command.internalBrandId());
            assertThat(result.createdAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createAll() - 배치 등록 도메인 객체 목록 생성")
    class CreateAllTest {

        @Test
        @DisplayName("BatchRegisterCommand로 InboundBrandMapping 목록을 생성한다")
        void createAll_ValidBatchCommand_ReturnsDomainObjects() {
            // given
            BatchRegisterInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.batchRegisterCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            List<InboundBrandMapping> result = sut.createAll(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(m -> m.inboundSourceId().equals(command.inboundSourceId()));
            assertThat(result.get(0).externalBrandCode()).isEqualTo("BR001");
            assertThat(result.get(1).externalBrandCode()).isEqualTo("BR002");
            assertThat(result.get(2).externalBrandCode()).isEqualTo("BR003");
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - 수정 UpdateContext 생성")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateCommand로 UpdateContext를 생성한다")
        void createUpdateContext_ValidCommand_ReturnsUpdateContext() {
            // given
            Long mappingId = 1L;
            UpdateInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.updateCommand(mappingId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, InboundBrandMappingUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(mappingId);
            assertThat(result.updateData().externalBrandName())
                    .isEqualTo(command.externalBrandName());
            assertThat(result.updateData().internalBrandId()).isEqualTo(command.internalBrandId());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태의 UpdateCommand로 UpdateContext를 생성한다")
        void createUpdateContext_InactiveStatus_ReturnsUpdateContextWithInactive() {
            // given
            Long mappingId = 1L;
            UpdateInboundBrandMappingCommand command =
                    InboundBrandMappingCommandFixtures.updateCommandWithInactive(mappingId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, InboundBrandMappingUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().status().name()).isEqualTo("INACTIVE");
        }
    }
}
