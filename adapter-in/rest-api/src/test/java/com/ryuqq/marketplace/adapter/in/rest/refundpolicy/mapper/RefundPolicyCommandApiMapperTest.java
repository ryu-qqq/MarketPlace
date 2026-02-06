package com.ryuqq.marketplace.adapter.in.rest.refundpolicy.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.RefundPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.ChangeRefundPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.RegisterRefundPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refundpolicy.dto.command.UpdateRefundPolicyApiRequest;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicyCommandApiMapper 단위 테스트")
class RefundPolicyCommandApiMapperTest {

    private RefundPolicyCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RefundPolicyCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(sellerId, RegisterRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterRefundPolicyApiRequest를 RegisterRefundPolicyCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            RegisterRefundPolicyApiRequest request = RefundPolicyApiFixtures.registerRequest();

            // when
            RegisterRefundPolicyCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyName()).isEqualTo(RefundPolicyApiFixtures.DEFAULT_POLICY_NAME);
            assertThat(command.defaultPolicy()).isTrue();
            assertThat(command.returnPeriodDays())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_RETURN_PERIOD_DAYS);
            assertThat(command.exchangePeriodDays())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_EXCHANGE_PERIOD_DAYS);
            assertThat(command.partialRefundEnabled()).isTrue();
            assertThat(command.inspectionRequired()).isTrue();
            assertThat(command.inspectionPeriodDays())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_INSPECTION_PERIOD_DAYS);
            assertThat(command.additionalInfo())
                    .isEqualTo(RefundPolicyApiFixtures.DEFAULT_ADDITIONAL_INFO);
        }

        @Test
        @DisplayName("반품 불가 조건을 변환한다")
        void toCommand_ConvertsConditions_ReturnsCommandWithConditions() {
            // given
            Long sellerId = 1L;
            RegisterRefundPolicyApiRequest request = RefundPolicyApiFixtures.registerRequest();

            // when
            RegisterRefundPolicyCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.nonReturnableConditions())
                    .containsExactly("OPENED_PACKAGING", "USED_PRODUCT", "MISSING_TAG");
        }

        @Test
        @DisplayName("conditions가 null이면 빈 리스트로 변환한다")
        void toCommand_NullConditions_ReturnsEmptyList() {
            // given
            Long sellerId = 1L;
            RegisterRefundPolicyApiRequest request =
                    RefundPolicyApiFixtures.registerRequestWithNullConditions();

            // when
            RegisterRefundPolicyCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.nonReturnableConditions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(sellerId, policyId, UpdateRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateRefundPolicyApiRequest를 UpdateRefundPolicyCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            Long policyId = 10L;
            UpdateRefundPolicyApiRequest request = RefundPolicyApiFixtures.updateRequest();

            // when
            UpdateRefundPolicyCommand command = mapper.toCommand(sellerId, policyId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyId()).isEqualTo(10L);
            assertThat(command.policyName()).isEqualTo("수정된 환불정책");
            assertThat(command.defaultPolicy()).isFalse();
            assertThat(command.returnPeriodDays()).isEqualTo(14);
            assertThat(command.exchangePeriodDays()).isEqualTo(14);
            assertThat(command.nonReturnableConditions())
                    .containsExactly("OPENED_PACKAGING", "TIME_EXPIRED");
            assertThat(command.partialRefundEnabled()).isFalse();
            assertThat(command.inspectionRequired()).isFalse();
            assertThat(command.inspectionPeriodDays()).isZero();
            assertThat(command.additionalInfo()).isEqualTo("수정된 안내 문구입니다.");
        }

        @Test
        @DisplayName("conditions가 null이면 빈 리스트로 변환한다")
        void toCommand_NullConditions_ReturnsEmptyList() {
            // given
            Long sellerId = 1L;
            Long policyId = 10L;
            UpdateRefundPolicyApiRequest request =
                    RefundPolicyApiFixtures.updateRequestWithNullConditions();

            // when
            UpdateRefundPolicyCommand command = mapper.toCommand(sellerId, policyId, request);

            // then
            assertThat(command.nonReturnableConditions()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toCommand(sellerId, ChangeStatusRequest) - 상태 변경 요청 변환")
    class ToChangeStatusCommandTest {

        @Test
        @DisplayName("ChangeRefundPolicyStatusApiRequest를 ChangeRefundPolicyStatusCommand로 변환한다")
        void toCommand_ConvertsChangeStatusRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            ChangeRefundPolicyStatusApiRequest request =
                    RefundPolicyApiFixtures.changeStatusRequest();

            // when
            ChangeRefundPolicyStatusCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyIds()).containsExactly(1L, 2L, 3L);
            assertThat(command.active()).isFalse();
        }
    }
}
