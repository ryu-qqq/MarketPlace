package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.InboundBrandMappingApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.RegisterInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.command.UpdateInboundBrandMappingApiRequest;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.BatchRegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.RegisterInboundBrandMappingCommand;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.command.UpdateInboundBrandMappingCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundBrandMappingCommandApiMapper 단위 테스트")
class InboundBrandMappingCommandApiMapperTest {

    private InboundBrandMappingCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundBrandMappingCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, RegisterInboundBrandMappingApiRequest) - 단건 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName(
                "RegisterInboundBrandMappingApiRequest를 RegisterInboundBrandMappingCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            RegisterInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.registerRequest();

            // when
            RegisterInboundBrandMappingCommand command = mapper.toCommand(inboundSourceId, request);

            // then
            assertThat(command.inboundSourceId())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(command.externalBrandCode())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_BRAND_CODE);
            assertThat(command.externalBrandName())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_BRAND_NAME);
            assertThat(command.internalBrandId())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_INTERNAL_BRAND_ID);
        }

        @Test
        @DisplayName("inboundSourceId가 올바르게 주입된다")
        void toCommand_InjectsInboundSourceId_Correctly() {
            // given
            Long inboundSourceId = 42L;
            RegisterInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.registerRequest();

            // when
            RegisterInboundBrandMappingCommand command = mapper.toCommand(inboundSourceId, request);

            // then
            assertThat(command.inboundSourceId()).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName("toBatchCommand(Long, BatchRegisterInboundBrandMappingApiRequest) - 일괄 등록 요청 변환")
    class ToBatchCommandTest {

        @Test
        @DisplayName(
                "BatchRegisterInboundBrandMappingApiRequest를"
                        + " BatchRegisterInboundBrandMappingCommand로 변환한다")
        void toBatchCommand_ConvertsBatchRequest_ReturnsCommand() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            BatchRegisterInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.batchRegisterRequest();

            // when
            BatchRegisterInboundBrandMappingCommand command =
                    mapper.toBatchCommand(inboundSourceId, request);

            // then
            assertThat(command.inboundSourceId())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(command.entries()).hasSize(2);
        }

        @Test
        @DisplayName("일괄 등록의 각 엔트리 필드가 올바르게 변환된다")
        void toBatchCommand_ConvertsEachEntry_CorrectFields() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            BatchRegisterInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.batchRegisterRequest();

            // when
            BatchRegisterInboundBrandMappingCommand command =
                    mapper.toBatchCommand(inboundSourceId, request);

            // then
            BatchRegisterInboundBrandMappingCommand.MappingEntry firstEntry =
                    command.entries().get(0);
            assertThat(firstEntry.externalBrandCode()).isEqualTo("NV_BRAND_001");
            assertThat(firstEntry.externalBrandName()).isEqualTo("나이키");
            assertThat(firstEntry.internalBrandId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("일괄 등록의 두 번째 엔트리도 올바르게 변환된다")
        void toBatchCommand_ConvertsSecondEntry_CorrectFields() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            BatchRegisterInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.batchRegisterRequest();

            // when
            BatchRegisterInboundBrandMappingCommand command =
                    mapper.toBatchCommand(inboundSourceId, request);

            // then
            BatchRegisterInboundBrandMappingCommand.MappingEntry secondEntry =
                    command.entries().get(1);
            assertThat(secondEntry.externalBrandCode()).isEqualTo("NV_BRAND_002");
            assertThat(secondEntry.externalBrandName()).isEqualTo("아디다스");
            assertThat(secondEntry.internalBrandId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateInboundBrandMappingApiRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateInboundBrandMappingApiRequest를 UpdateInboundBrandMappingCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long id = 10L;
            UpdateInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.updateRequest();

            // when
            UpdateInboundBrandMappingCommand command = mapper.toCommand(id, request);

            // then
            assertThat(command.id()).isEqualTo(10L);
            assertThat(command.externalBrandName())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_BRAND_NAME);
            assertThat(command.internalBrandId())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_INTERNAL_BRAND_ID);
            assertThat(command.status()).isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_STATUS);
        }

        @Test
        @DisplayName("id가 올바르게 주입된다")
        void toCommand_InjectsId_Correctly() {
            // given
            Long id = 99L;
            UpdateInboundBrandMappingApiRequest request =
                    InboundBrandMappingApiFixtures.updateRequest();

            // when
            UpdateInboundBrandMappingCommand command = mapper.toCommand(id, request);

            // then
            assertThat(command.id()).isEqualTo(99L);
        }
    }
}
