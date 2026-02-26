package com.ryuqq.marketplace.application.shippingpolicy.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shippingpolicy.ShippingPolicyCommandFixtures;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.factory.ShippingPolicyCommandFactory;
import com.ryuqq.marketplace.application.shippingpolicy.internal.DefaultShippingPolicyResolver;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
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
@DisplayName("RegisterShippingPolicyService 단위 테스트")
class RegisterShippingPolicyServiceTest {

    @InjectMocks private RegisterShippingPolicyService sut;

    @Mock private ShippingPolicyCommandFactory commandFactory;
    @Mock private ShippingPolicyCommandManager commandManager;
    @Mock private DefaultShippingPolicyResolver defaultPolicyResolver;

    @Nested
    @DisplayName("execute() - 배송 정책 등록")
    class ExecuteTest {

        @Test
        @DisplayName("배송 정책을 등록하고 ID를 반환한다")
        void execute_RegistersPolicy_ReturnsId() {
            // given
            Long sellerId = 1L;
            Long expectedPolicyId = 100L;
            RegisterShippingPolicyCommand command =
                    ShippingPolicyCommandFixtures.registerCommand(sellerId);
            ShippingPolicy shippingPolicy = ShippingPolicyFixtures.newFreeShippingPolicy();

            given(commandFactory.create(command)).willReturn(shippingPolicy);
            given(commandManager.persist(shippingPolicy)).willReturn(expectedPolicyId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedPolicyId);
            then(commandFactory).should().create(command);
            then(defaultPolicyResolver)
                    .should()
                    .resolveForRegistration(
                            shippingPolicy.sellerId(), shippingPolicy, shippingPolicy.createdAt());
            then(commandManager).should().persist(shippingPolicy);
        }

        @Test
        @DisplayName("무료 배송 정책을 등록한다")
        void execute_FreeShippingPolicy_RegistersSuccessfully() {
            // given
            Long sellerId = 1L;
            Long expectedPolicyId = 101L;
            RegisterShippingPolicyCommand command =
                    ShippingPolicyCommandFixtures.freeShippingRegisterCommand(sellerId);
            ShippingPolicy shippingPolicy = ShippingPolicyFixtures.newFreeShippingPolicy();

            given(commandFactory.create(command)).willReturn(shippingPolicy);
            given(commandManager.persist(shippingPolicy)).willReturn(expectedPolicyId);

            // when
            Long result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedPolicyId);
        }
    }
}
