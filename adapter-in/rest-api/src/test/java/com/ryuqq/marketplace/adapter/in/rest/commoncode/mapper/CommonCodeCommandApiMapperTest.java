package com.ryuqq.marketplace.adapter.in.rest.commoncode.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.commoncode.CommonCodeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.ChangeActiveStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.RegisterCommonCodeApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.commoncode.dto.command.UpdateCommonCodeApiRequest;
import com.ryuqq.marketplace.application.commoncode.dto.command.ChangeCommonCodeStatusCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.RegisterCommonCodeCommand;
import com.ryuqq.marketplace.application.commoncode.dto.command.UpdateCommonCodeCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeCommandApiMapper 단위 테스트")
class CommonCodeCommandApiMapperTest {

    private final CommonCodeCommandApiMapper sut = new CommonCodeCommandApiMapper();

    @Nested
    @DisplayName("toCommand(RegisterCommonCodeApiRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterCommonCodeApiRequest를 RegisterCommonCodeCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            RegisterCommonCodeApiRequest request =
                    CommonCodeApiFixtures.registerRequest(1L, "CARD", "신용카드", 1);

            // when
            RegisterCommonCodeCommand command = sut.toCommand(request);

            // then
            assertThat(command.commonCodeTypeId()).isEqualTo(1L);
            assertThat(command.code()).isEqualTo("CARD");
            assertThat(command.displayName()).isEqualTo("신용카드");
            assertThat(command.displayOrder()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateCommonCodeApiRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateCommonCodeApiRequest를 UpdateCommonCodeCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long id = 10L;
            UpdateCommonCodeApiRequest request = CommonCodeApiFixtures.updateRequest("수정된 표시명", 5);

            // when
            UpdateCommonCodeCommand command = sut.toCommand(id, request);

            // then
            assertThat(command.id()).isEqualTo(10L);
            assertThat(command.displayName()).isEqualTo("수정된 표시명");
            assertThat(command.displayOrder()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("toCommand(ChangeActiveStatusApiRequest) - 상태 변경 요청 변환")
    class ToChangeStatusCommandTest {

        @Test
        @DisplayName("활성화 요청을 ChangeCommonCodeStatusCommand로 변환한다")
        void toCommand_ConvertsActivateRequest_ReturnsCommand() {
            // given
            ChangeActiveStatusApiRequest request =
                    CommonCodeApiFixtures.activateRequest(1L, 2L, 3L);

            // when
            ChangeCommonCodeStatusCommand command = sut.toCommand(request);

            // then
            assertThat(command.ids()).containsExactly(1L, 2L, 3L);
            assertThat(command.active()).isTrue();
        }

        @Test
        @DisplayName("비활성화 요청을 ChangeCommonCodeStatusCommand로 변환한다")
        void toCommand_ConvertsDeactivateRequest_ReturnsCommand() {
            // given
            ChangeActiveStatusApiRequest request = CommonCodeApiFixtures.deactivateRequest(4L, 5L);

            // when
            ChangeCommonCodeStatusCommand command = sut.toCommand(request);

            // then
            assertThat(command.ids()).containsExactly(4L, 5L);
            assertThat(command.active()).isFalse();
        }
    }
}
