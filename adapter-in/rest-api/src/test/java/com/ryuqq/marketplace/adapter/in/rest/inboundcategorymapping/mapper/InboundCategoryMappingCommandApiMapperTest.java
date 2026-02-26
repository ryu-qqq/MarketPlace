package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.InboundCategoryMappingApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingApiRequest;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.BatchRegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.RegisterInboundCategoryMappingCommand;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.command.UpdateInboundCategoryMappingCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundCategoryMappingCommandApiMapper 단위 테스트")
class InboundCategoryMappingCommandApiMapperTest {

    private InboundCategoryMappingCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundCategoryMappingCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, RegisterInboundCategoryMappingApiRequest) - 단건 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName(
                "RegisterInboundCategoryMappingApiRequest를 RegisterInboundCategoryMappingCommand로"
                        + " 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            RegisterInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.registerRequest();

            // when
            RegisterInboundCategoryMappingCommand command =
                    mapper.toCommand(inboundSourceId, request);

            // then
            assertThat(command.inboundSourceId())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(command.externalCategoryCode())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_CATEGORY_CODE);
            assertThat(command.externalCategoryName())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_CATEGORY_NAME);
            assertThat(command.internalCategoryId())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_INTERNAL_CATEGORY_ID);
        }

        @Test
        @DisplayName("inboundSourceId가 올바르게 주입된다")
        void toCommand_InjectsInboundSourceId_Correctly() {
            // given
            Long inboundSourceId = 42L;
            RegisterInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.registerRequest();

            // when
            RegisterInboundCategoryMappingCommand command =
                    mapper.toCommand(inboundSourceId, request);

            // then
            assertThat(command.inboundSourceId()).isEqualTo(42L);
        }
    }

    @Nested
    @DisplayName(
            "toBatchCommand(Long, BatchRegisterInboundCategoryMappingApiRequest) - 일괄 등록 요청 변환")
    class ToBatchCommandTest {

        @Test
        @DisplayName(
                "BatchRegisterInboundCategoryMappingApiRequest를"
                        + " BatchRegisterInboundCategoryMappingCommand로 변환한다")
        void toBatchCommand_ConvertsBatchRequest_ReturnsCommand() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            BatchRegisterInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.batchRegisterRequest();

            // when
            BatchRegisterInboundCategoryMappingCommand command =
                    mapper.toBatchCommand(inboundSourceId, request);

            // then
            assertThat(command.inboundSourceId())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(command.entries()).hasSize(2);
        }

        @Test
        @DisplayName("일괄 등록의 각 엔트리 필드가 올바르게 변환된다")
        void toBatchCommand_ConvertsEachEntry_CorrectFields() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            BatchRegisterInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.batchRegisterRequest();

            // when
            BatchRegisterInboundCategoryMappingCommand command =
                    mapper.toBatchCommand(inboundSourceId, request);

            // then
            BatchRegisterInboundCategoryMappingCommand.MappingEntry firstEntry =
                    command.entries().get(0);
            assertThat(firstEntry.externalCategoryCode()).isEqualTo("NV_CAT_001");
            assertThat(firstEntry.externalCategoryName()).isEqualTo("남성의류");
            assertThat(firstEntry.internalCategoryId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("일괄 등록의 두 번째 엔트리도 올바르게 변환된다")
        void toBatchCommand_ConvertsSecondEntry_CorrectFields() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            BatchRegisterInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.batchRegisterRequest();

            // when
            BatchRegisterInboundCategoryMappingCommand command =
                    mapper.toBatchCommand(inboundSourceId, request);

            // then
            BatchRegisterInboundCategoryMappingCommand.MappingEntry secondEntry =
                    command.entries().get(1);
            assertThat(secondEntry.externalCategoryCode()).isEqualTo("NV_CAT_002");
            assertThat(secondEntry.externalCategoryName()).isEqualTo("여성의류");
            assertThat(secondEntry.internalCategoryId()).isEqualTo(2L);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateInboundCategoryMappingApiRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName(
                "UpdateInboundCategoryMappingApiRequest를 UpdateInboundCategoryMappingCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long id = 10L;
            UpdateInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.updateRequest();

            // when
            UpdateInboundCategoryMappingCommand command = mapper.toCommand(id, request);

            // then
            assertThat(command.id()).isEqualTo(10L);
            assertThat(command.externalCategoryName())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_CATEGORY_NAME);
            assertThat(command.internalCategoryId())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_INTERNAL_CATEGORY_ID);
            assertThat(command.status())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_STATUS);
        }

        @Test
        @DisplayName("id가 올바르게 주입된다")
        void toCommand_InjectsId_Correctly() {
            // given
            Long id = 99L;
            UpdateInboundCategoryMappingApiRequest request =
                    InboundCategoryMappingApiFixtures.updateRequest();

            // when
            UpdateInboundCategoryMappingCommand command = mapper.toCommand(id, request);

            // then
            assertThat(command.id()).isEqualTo(99L);
        }
    }
}
