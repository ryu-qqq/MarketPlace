package com.ryuqq.marketplace.application.inboundcategorymapping.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.inboundcategorymapping.InboundCategoryMappingCommandFixtures;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingUpdateData;
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
@DisplayName("InboundCategoryMappingCommandFactory 단위 테스트")
class InboundCategoryMappingCommandFactoryTest {

    @InjectMocks private InboundCategoryMappingCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - 단건 등록 도메인 객체 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterCommand로 InboundCategoryMapping을 생성한다")
        void create_ValidCommand_ReturnsDomainObject() {
            // given
            RegisterInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.registerCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            InboundCategoryMapping result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.inboundSourceId()).isEqualTo(command.inboundSourceId());
            assertThat(result.externalCategoryCode()).isEqualTo(command.externalCategoryCode());
            assertThat(result.externalCategoryName()).isEqualTo(command.externalCategoryName());
            assertThat(result.internalCategoryId()).isEqualTo(command.internalCategoryId());
            assertThat(result.createdAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("createAll() - 배치 등록 도메인 객체 목록 생성")
    class CreateAllTest {

        @Test
        @DisplayName("BatchRegisterCommand로 InboundCategoryMapping 목록을 생성한다")
        void createAll_ValidBatchCommand_ReturnsDomainObjects() {
            // given
            BatchRegisterInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.batchRegisterCommand();
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            List<InboundCategoryMapping> result = sut.createAll(command);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(m -> m.inboundSourceId().equals(command.inboundSourceId()));
            assertThat(result.get(0).externalCategoryCode()).isEqualTo("CAT_SHOES_001");
            assertThat(result.get(1).externalCategoryCode()).isEqualTo("CAT_BAG_001");
            assertThat(result.get(2).externalCategoryCode()).isEqualTo("CAT_CLOTHES_001");
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
            UpdateInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.updateCommand(mappingId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, InboundCategoryMappingUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(mappingId);
            assertThat(result.updateData().externalCategoryName())
                    .isEqualTo(command.externalCategoryName());
            assertThat(result.updateData().internalCategoryId())
                    .isEqualTo(command.internalCategoryId());
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("INACTIVE 상태의 UpdateCommand로 UpdateContext를 생성한다")
        void createUpdateContext_InactiveStatus_ReturnsUpdateContextWithInactive() {
            // given
            Long mappingId = 1L;
            UpdateInboundCategoryMappingCommand command =
                    InboundCategoryMappingCommandFixtures.updateCommandWithInactive(mappingId);
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<Long, InboundCategoryMappingUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.updateData().status().name()).isEqualTo("INACTIVE");
        }
    }
}
