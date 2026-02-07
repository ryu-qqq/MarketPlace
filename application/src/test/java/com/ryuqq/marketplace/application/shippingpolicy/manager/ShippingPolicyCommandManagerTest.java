package com.ryuqq.marketplace.application.shippingpolicy.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shippingpolicy.port.out.command.ShippingPolicyCommandPort;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
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
@DisplayName("ShippingPolicyCommandManager 단위 테스트")
class ShippingPolicyCommandManagerTest {

    @InjectMocks private ShippingPolicyCommandManager sut;

    @Mock private ShippingPolicyCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 배송 정책 저장")
    class PersistTest {

        @Test
        @DisplayName("배송 정책을 저장하고 ID를 반환한다")
        void persist_SavesPolicy_ReturnsId() {
            // given
            ShippingPolicy policy = ShippingPolicyFixtures.newFreeShippingPolicy();
            Long expectedId = 100L;

            given(commandPort.persist(policy)).willReturn(expectedId);

            // when
            Long result = sut.persist(policy);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(policy);
        }
    }

    @Nested
    @DisplayName("persistAll() - 배송 정책 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("배송 정책 목록을 일괄 저장한다")
        void persistAll_SavesAllPolicies() {
            // given
            List<ShippingPolicy> policies =
                    List.of(
                            ShippingPolicyFixtures.newFreeShippingPolicy(),
                            ShippingPolicyFixtures.newPaidShippingPolicy());

            // when
            sut.persistAll(policies);

            // then
            then(commandPort).should().persistAll(policies);
        }
    }
}
