package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.setofsync.manager.SetofSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.ShippingPolicyCommandFixtures;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.validator.ShippingPolicyValidator;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
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
@DisplayName("ChangeShippingPolicyStatusService 단위 테스트")
class ChangeShippingPolicyStatusServiceTest {

    @InjectMocks private ChangeShippingPolicyStatusService sut;

    @Mock private ShippingPolicyCommandFactory commandFactory;
    @Mock private ShippingPolicyCommandManager commandManager;
    @Mock private ShippingPolicyValidator validator;
    @Mock private SetofSyncOutboxCommandManager setofSyncOutboxCommandManager;

    @Nested
    @DisplayName("execute() - 배송 정책 상태 변경")
    class ExecuteTest {

        @Test
        @DisplayName("배송 정책을 활성화한다")
        void execute_Activate_ActivatesPolicies() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            ChangeShippingPolicyStatusCommand command =
                    ShippingPolicyCommandFixtures.activateCommand(sellerId, policyId);

            Instant changedAt = Instant.now();
            List<StatusChangeContext<ShippingPolicyId>> contexts =
                    List.of(new StatusChangeContext<>(ShippingPolicyId.of(policyId), changedAt));

            ShippingPolicy policy = ShippingPolicyFixtures.inactiveShippingPolicy();
            List<ShippingPolicy> policies = List.of(policy);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(List.of(ShippingPolicyId.of(policyId))))
                    .willReturn(policies);

            // when
            sut.execute(command);

            // then
            then(commandFactory).should().createStatusChangeContexts(command);
            then(validator).should().findAllExistingOrThrow(List.of(ShippingPolicyId.of(policyId)));
            then(commandManager).should().persistAll(policies);
        }

        @Test
        @DisplayName("배송 정책을 비활성화한다")
        void execute_Deactivate_DeactivatesPolicies() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            ChangeShippingPolicyStatusCommand command =
                    ShippingPolicyCommandFixtures.deactivateCommand(sellerId, policyId);

            Instant changedAt = Instant.now();
            List<StatusChangeContext<ShippingPolicyId>> contexts =
                    List.of(new StatusChangeContext<>(ShippingPolicyId.of(policyId), changedAt));

            ShippingPolicy policy =
                    ShippingPolicyFixtures.activeNonDefaultShippingPolicy(
                            policyId, SellerId.of(sellerId));
            List<ShippingPolicy> policies = List.of(policy);

            given(commandFactory.createStatusChangeContexts(command)).willReturn(contexts);
            given(validator.findAllExistingOrThrow(List.of(ShippingPolicyId.of(policyId))))
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
