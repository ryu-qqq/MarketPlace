package com.ryuqq.marketplace.adapter.in.rest.selleradmin.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.selleradmin.SellerAdminApplicationApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ApplySellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkApproveSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.BulkRejectSellerAdminApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.command.ChangeSellerAdminPasswordApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleradmin.dto.response.BulkApproveSellerAdminApiResponse;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApplySellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkApproveSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.BulkRejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ChangeSellerAdminPasswordCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.RejectSellerAdminCommand;
import com.ryuqq.marketplace.application.selleradmin.dto.command.ResetSellerAdminPasswordCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminApplicationCommandApiMapper 단위 테스트")
class SellerAdminApplicationCommandApiMapperTest {

    private final SellerAdminApplicationCommandApiMapper sut =
            new SellerAdminApplicationCommandApiMapper();

    @Nested
    @DisplayName("toCommand(ApplySellerAdminApiRequest) - 가입 신청 요청 변환")
    class ToApplyCommandTest {

        @Test
        @DisplayName("ApplySellerAdminApiRequest를 ApplySellerAdminCommand로 변환한다")
        void toCommand_ConvertsApplyRequest_ReturnsCommand() {
            // given
            ApplySellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.applyRequest(
                            1L, "admin@test.com", "김철수", "010-9999-8888", "SecurePass1!");

            // when
            ApplySellerAdminCommand command = sut.toCommand(request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.loginId()).isEqualTo("admin@test.com");
            assertThat(command.name()).isEqualTo("김철수");
            assertThat(command.phoneNumber()).isEqualTo("010-9999-8888");
            assertThat(command.password()).isEqualTo("SecurePass1!");
        }
    }

    @Nested
    @DisplayName("toApproveCommand(String) - 승인 요청 변환")
    class ToApproveCommandTest {

        @Test
        @DisplayName("sellerAdminId를 ApproveSellerAdminCommand로 변환한다")
        void toApproveCommand_ConvertsId_ReturnsCommand() {
            // given
            String sellerAdminId = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

            // when
            ApproveSellerAdminCommand command = sut.toApproveCommand(sellerAdminId);

            // then
            assertThat(command.sellerAdminId()).isEqualTo(sellerAdminId);
        }
    }

    @Nested
    @DisplayName("toRejectCommand(String) - 거절 요청 변환")
    class ToRejectCommandTest {

        @Test
        @DisplayName("sellerAdminId를 RejectSellerAdminCommand로 변환한다")
        void toRejectCommand_ConvertsId_ReturnsCommand() {
            // given
            String sellerAdminId = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

            // when
            RejectSellerAdminCommand command = sut.toRejectCommand(sellerAdminId);

            // then
            assertThat(command.sellerAdminId()).isEqualTo(sellerAdminId);
        }
    }

    @Nested
    @DisplayName("toBulkApproveCommand(BulkApproveSellerAdminApiRequest) - 일괄 승인 요청 변환")
    class ToBulkApproveCommandTest {

        @Test
        @DisplayName("BulkApproveSellerAdminApiRequest를 BulkApproveSellerAdminCommand로 변환한다")
        void toBulkApproveCommand_ConvertsRequest_ReturnsCommand() {
            // given
            BulkApproveSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkApproveRequest("id-1", "id-2", "id-3");

            // when
            BulkApproveSellerAdminCommand command = sut.toBulkApproveCommand(request);

            // then
            assertThat(command.sellerAdminIds()).containsExactly("id-1", "id-2", "id-3");
        }
    }

    @Nested
    @DisplayName("toBulkRejectCommand(BulkRejectSellerAdminApiRequest) - 일괄 거절 요청 변환")
    class ToBulkRejectCommandTest {

        @Test
        @DisplayName("BulkRejectSellerAdminApiRequest를 BulkRejectSellerAdminCommand로 변환한다")
        void toBulkRejectCommand_ConvertsRequest_ReturnsCommand() {
            // given
            BulkRejectSellerAdminApiRequest request =
                    SellerAdminApplicationApiFixtures.bulkRejectRequest("id-4", "id-5");

            // when
            BulkRejectSellerAdminCommand command = sut.toBulkRejectCommand(request);

            // then
            assertThat(command.sellerAdminIds()).containsExactly("id-4", "id-5");
        }
    }

    @Nested
    @DisplayName("toResetPasswordCommand(String) - 비밀번호 초기화 요청 변환")
    class ToResetPasswordCommandTest {

        @Test
        @DisplayName("sellerAdminId를 ResetSellerAdminPasswordCommand로 변환한다")
        void toResetPasswordCommand_ConvertsId_ReturnsCommand() {
            // given
            String sellerAdminId = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";

            // when
            ResetSellerAdminPasswordCommand command = sut.toResetPasswordCommand(sellerAdminId);

            // then
            assertThat(command.sellerAdminId()).isEqualTo(sellerAdminId);
        }
    }

    @Nested
    @DisplayName(
            "toChangePasswordCommand(String, ChangeSellerAdminPasswordApiRequest) - 비밀번호 변경 요청 변환")
    class ToChangePasswordCommandTest {

        @Test
        @DisplayName("sellerAdminId와 request를 ChangeSellerAdminPasswordCommand로 변환한다")
        void toChangePasswordCommand_ConvertsRequest_ReturnsCommand() {
            // given
            String sellerAdminId = "01956f4a-2b3c-7d8e-9f0a-1b2c3d4e5f60";
            ChangeSellerAdminPasswordApiRequest request =
                    SellerAdminApplicationApiFixtures.changePasswordRequest("NewPass1!");

            // when
            ChangeSellerAdminPasswordCommand command =
                    sut.toChangePasswordCommand(sellerAdminId, request);

            // then
            assertThat(command.sellerAdminId()).isEqualTo(sellerAdminId);
            assertThat(command.newPassword()).isEqualTo("NewPass1!");
        }
    }

    @Nested
    @DisplayName("toResponse(BatchProcessingResult) - 일괄 승인 결과 응답 변환")
    class ToResponseTest {

        @Test
        @DisplayName("전체 성공 BatchProcessingResult를 BulkApproveSellerAdminApiResponse로 변환한다")
        void toResponse_AllSuccess_ReturnsResponse() {
            // given
            BatchProcessingResult<String> result =
                    SellerAdminApplicationApiFixtures.allSuccessBatchResult("id-1", "id-2");

            // when
            BulkApproveSellerAdminApiResponse response = sut.toResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isZero();
            assertThat(response.results()).hasSize(2);
            assertThat(response.results().get(0).sellerAdminId()).isEqualTo("id-1");
            assertThat(response.results().get(0).success()).isTrue();
            assertThat(response.results().get(0).errorCode()).isNull();
        }

        @Test
        @DisplayName("부분 실패 BatchProcessingResult를 BulkApproveSellerAdminApiResponse로 변환한다")
        void toResponse_PartialFailure_ReturnsResponse() {
            // given
            BatchProcessingResult<String> result =
                    SellerAdminApplicationApiFixtures.partialFailureBatchResult("id-1", "id-2");

            // when
            BulkApproveSellerAdminApiResponse response = sut.toResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.successCount()).isEqualTo(1);
            assertThat(response.failureCount()).isEqualTo(1);
            assertThat(response.results()).hasSize(2);

            BulkApproveSellerAdminApiResponse.ItemResult failedItem = response.results().get(1);
            assertThat(failedItem.success()).isFalse();
            assertThat(failedItem.errorCode()).isEqualTo("SELADM-003");
            assertThat(failedItem.errorMessage()).isEqualTo("이미 처리된 신청입니다");
        }
    }
}
