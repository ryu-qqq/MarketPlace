package com.ryuqq.marketplace.application.refundpolicy.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refundpolicy.port.out.command.RefundPolicyCommandPort;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
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
@DisplayName("RefundPolicyCommandManager 단위 테스트")
class RefundPolicyCommandManagerTest {

    @InjectMocks private RefundPolicyCommandManager sut;

    @Mock private RefundPolicyCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 환불 정책 저장")
    class PersistTest {

        @Test
        @DisplayName("환불 정책을 저장하고 ID를 반환한다")
        void persist_SavesPolicy_ReturnsId() {
            // given
            RefundPolicy policy = RefundPolicyFixtures.newRefundPolicy();
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
    @DisplayName("persistAll() - 환불 정책 일괄 저장")
    class PersistAllTest {

        @Test
        @DisplayName("환불 정책 목록을 일괄 저장한다")
        void persistAll_SavesAllPolicies() {
            // given
            List<RefundPolicy> policies =
                    List.of(
                            RefundPolicyFixtures.newRefundPolicy(),
                            RefundPolicyFixtures.newRefundPolicy(7, 14));

            // when
            sut.persistAll(policies);

            // then
            then(commandPort).should().persistAll(policies);
        }
    }
}
