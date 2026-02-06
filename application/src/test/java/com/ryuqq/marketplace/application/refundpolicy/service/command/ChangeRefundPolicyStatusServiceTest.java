package com.ryuqq.marketplace.application.refundpolicy.service.command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.refundpolicy.RefundPolicyCommandFixtures;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.factory.RefundPolicyCommandFactory;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyCommandManager;
import com.ryuqq.marketplace.application.refundpolicy.validator.RefundPolicyValidator;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
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
@DisplayName("ChangeRefundPolicyStatusService 단위 테스트")
class ChangeRefundPolicyStatusServiceTest {

    @InjectMocks private ChangeRefundPolicyStatusService sut;

    @Mock private RefundPolicyCommandFactory commandFactory;
    @Mock private RefundPolicyCommandManager commandManager;
    @Mock private RefundPolicyValidator validator;

    @Nested
    @DisplayName("execute() - 환불 정책 상태 변경")
    class ExecuteTest {

        @Test
        @DisplayName("환불 정책을 활성화한다")
        void execute_Activate_ActivatesPolicies() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            ChangeRefundPolicyStatusCommand command =
                    RefundPolicyCommandFixtures.activateCommand(sellerId, policyId);

            Instant changedAt = Instant.now();
            List<StatusChangeContext<RefundPolicyId>> contexts =
                    List.of(new StatusChangeContext<>(RefundPolicyId.of(policyId), changedAt));

            RefundPolicy policy = RefundPolicyFixtures.inactiveRefundPolicy();
            List<RefundPolicy> policies = List.of(policy);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(List.of(RefundPolicyId.of(policyId))))
                    .willReturn(policies);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createStatusChangeContexts(command);
            then(validator).should().findAllExistingOrThrow(List.of(RefundPolicyId.of(policyId)));
            then(commandManager).should().persistAll(policies);
        }

        @Test
        @DisplayName("환불 정책을 비활성화한다")
        void execute_Deactivate_DeactivatesPolicies() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            ChangeRefundPolicyStatusCommand command =
                    RefundPolicyCommandFixtures.deactivateCommand(sellerId, policyId);

            Instant changedAt = Instant.now();
            List<StatusChangeContext<RefundPolicyId>> contexts =
                    List.of(new StatusChangeContext<>(RefundPolicyId.of(policyId), changedAt));

            RefundPolicy policy =
                    RefundPolicyFixtures.activeNonDefaultRefundPolicy(
                            policyId, SellerId.of(sellerId));
            List<RefundPolicy> policies = List.of(policy);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(List.of(RefundPolicyId.of(policyId))))
                    .willReturn(policies);

            // when
            sut.execute(command);

            // then
            then(validator)
                    .should()
                    .validateNotLastActivePolicy(eq(SellerId.of(sellerId)), eq(policies));
            then(commandManager).should().persistAll(policies);
        }
    }
}
