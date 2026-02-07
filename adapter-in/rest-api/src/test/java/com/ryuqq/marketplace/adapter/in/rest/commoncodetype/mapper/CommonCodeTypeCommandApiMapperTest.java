package com.ryuqq.marketplace.adapter.in.rest.commoncodetype.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.CommonCodeTypeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.RegisterCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncodetype.dto.command.UpdateCommonCodeTypeApiRequest;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.ChangeActiveStatusCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.RegisterCommonCodeTypeCommand;
import com.ryuqq.marketplace.application.commoncodetype.dto.command.UpdateCommonCodeTypeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeCommandApiMapper 단위 테스트")
class CommonCodeTypeCommandApiMapperTest {

    private CommonCodeTypeCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CommonCodeTypeCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(RegisterRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterCommonCodeTypeApiRequest를 RegisterCommonCodeTypeCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            RegisterCommonCodeTypeApiRequest request = CommonCodeTypeApiFixtures.registerRequest();

            // when
            RegisterCommonCodeTypeCommand command = mapper.toCommand(request);

            // then
            assertThat(command.code()).isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_CODE);
            assertThat(command.name()).isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_NAME);
            assertThat(command.description())
                    .isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_DESCRIPTION);
            assertThat(command.displayOrder())
                    .isEqualTo(CommonCodeTypeApiFixtures.DEFAULT_DISPLAY_ORDER);
        }

        @Test
        @DisplayName("커스텀 값으로 등록 요청을 변환한다")
        void toCommand_CustomValues_ReturnsCommandWithCustomValues() {
            // given
            RegisterCommonCodeTypeApiRequest request =
                    CommonCodeTypeApiFixtures.registerRequest(
                            "DELIVERY_TYPE", "배송유형", "배송 유형 목록", 5);

            // when
            RegisterCommonCodeTypeCommand command = mapper.toCommand(request);

            // then
            assertThat(command.code()).isEqualTo("DELIVERY_TYPE");
            assertThat(command.name()).isEqualTo("배송유형");
            assertThat(command.description()).isEqualTo("배송 유형 목록");
            assertThat(command.displayOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("toCommand(id, UpdateRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateCommonCodeTypeApiRequest를 UpdateCommonCodeTypeCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long commonCodeTypeId = 10L;
            UpdateCommonCodeTypeApiRequest request = CommonCodeTypeApiFixtures.updateRequest();

            // when
            UpdateCommonCodeTypeCommand command = mapper.toCommand(commonCodeTypeId, request);

            // then
            assertThat(command.id()).isEqualTo(10L);
            assertThat(command.name()).isEqualTo("수정된 결제수단");
            assertThat(command.description()).isEqualTo("수정된 설명입니다.");
            assertThat(command.displayOrder()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("toCommand(ChangeActiveStatusRequest) - 활성화 상태 변경 요청 변환")
    class ToChangeActiveStatusCommandTest {

        @Test
        @DisplayName("ChangeActiveStatusApiRequest를 ChangeActiveStatusCommand로 변환한다")
        void toCommand_ConvertsChangeActiveStatusRequest_ReturnsCommand() {
            // given
            ChangeActiveStatusApiRequest request =
                    CommonCodeTypeApiFixtures.changeActiveStatusRequest();

            // when
            ChangeActiveStatusCommand command = mapper.toCommand(request);

            // then
            assertThat(command.ids()).containsExactly(1L, 2L, 3L);
            assertThat(command.active()).isFalse();
        }
    }
}
