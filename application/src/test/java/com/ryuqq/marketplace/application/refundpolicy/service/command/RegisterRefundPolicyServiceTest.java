package com.ryuqq.marketplace.application.refundpolicy.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refundpolicy.RefundPolicyCommandFixtures;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyCommandFactory;
import com.ryuqq.marketplace.application.refundpolicy.internal.DefaultRefundPolicyResolver;
import com.ryuqq.marketplace.application.refundpolicy.internal.RefundPolicyOutboundFacade;
import com.ryuqq.marketplace.domain.outboundseller.vo.OutboundSellerOperationType;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
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
@DisplayName("RegisterRefundPolicyService 단위 테스트")
class RegisterRefundPolicyServiceTest {

    @InjectMocks private RegisterRefundPolicyService sut;

    @Mock private RefundPolicyCommandFactory commandFactory;
    @Mock private DefaultRefundPolicyResolver defaultPolicyResolver;
    @Mock private RefundPolicyOutboundFacade outboundFacade;

    @Nested
    @DisplayName("execute() - 환불 정책 등록")
    class ExecuteTest {

        @Test
        @DisplayName("환불 정책을 등록하고 ID를 반환한다")
        void execute_RegistersPolicy_ReturnsId() {
            // given
            Long sellerId = 1L;
            Long expectedPolicyId = 100L;
            RegisterRefundPolicyCommand command =
                    RefundPolicyCommandFixtures.registerCommand(sellerId);
            RefundPolicy refundPolicy = RefundPolicyFixtures.newRefundPolicy();

            given(commandFactory.create(command)).willReturn(refundPolicy);
            given(
                            outboundFacade.persistWithSync(
                                    eq(refundPolicy),
                                    eq(OutboundSellerOperationType.CREATE),
                                    eq(refundPolicy.createdAt())))
                    .willReturn(expectedPolicyId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedPolicyId);
            then(commandFactory).should().create(command);
            then(defaultPolicyResolver)
                    .should()
                    .resolveForRegistration(
                            refundPolicy.sellerId(), refundPolicy, refundPolicy.createdAt());
            then(outboundFacade)
                    .should()
                    .persistWithSync(
                            eq(refundPolicy),
                            eq(OutboundSellerOperationType.CREATE),
                            eq(refundPolicy.createdAt()));
        }

        @Test
        @DisplayName("간편 환불 정책을 등록한다")
        void execute_SimplePolicy_RegistersSuccessfully() {
            // given
            Long sellerId = 1L;
            Long expectedPolicyId = 101L;
            RegisterRefundPolicyCommand command =
                    RefundPolicyCommandFixtures.simpleRegisterCommand(sellerId);
            RefundPolicy refundPolicy = RefundPolicyFixtures.newRefundPolicy(7, 14);

            given(commandFactory.create(command)).willReturn(refundPolicy);
            given(
                            outboundFacade.persistWithSync(
                                    eq(refundPolicy),
                                    eq(OutboundSellerOperationType.CREATE),
                                    eq(refundPolicy.createdAt())))
                    .willReturn(expectedPolicyId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedPolicyId);
        }
    }
}
