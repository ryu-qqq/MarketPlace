package com.ryuqq.marketplace.application.refundpolicy.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.refundpolicy.RefundPolicyCommandFixtures;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.ChangeRefundPolicyStatusCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.RegisterRefundPolicyCommand;
import com.ryuqq.marketplace.application.refundpolicy.dto.command.UpdateRefundPolicyCommand;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicyUpdateData;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
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
@DisplayName("RefundPolicyCommandFactory 단위 테스트")
class RefundPolicyCommandFactoryTest {

    @InjectMocks private RefundPolicyCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - RegisterRefundPolicyCommand → RefundPolicy 변환")
    class CreateTest {

        @Test
        @DisplayName("RegisterRefundPolicyCommand로부터 RefundPolicy를 생성한다")
        void create_CreatesRefundPolicy() {
            // given
            Long sellerId = 1L;
            Instant now = Instant.now();
            RegisterRefundPolicyCommand command =
                    RefundPolicyCommandFixtures.registerCommand(sellerId);

            given(timeProvider.now()).willReturn(now);

            // when
            RefundPolicy result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId().value()).isEqualTo(sellerId);
            assertThat(result.policyName().value()).isEqualTo(command.policyName());
            assertThat(result.isDefaultPolicy()).isTrue();
            assertThat(result.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("간편 환불 정책을 생성한다")
        void create_SimpleRefundPolicy_CreatesNonDefaultPolicy() {
            // given
            Long sellerId = 1L;
            Instant now = Instant.now();
            RegisterRefundPolicyCommand command =
                    RefundPolicyCommandFixtures.simpleRegisterCommand(sellerId);

            given(timeProvider.now()).willReturn(now);

            // when
            RefundPolicy result = sut.create(command);

            // then
            assertThat(result.isDefaultPolicy()).isFalse();
            assertThat(result.isPartialRefundEnabled()).isFalse();
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - UpdateRefundPolicyCommand → UpdateContext 변환")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateRefundPolicyCommand로부터 UpdateContext를 생성한다")
        void createUpdateContext_CreatesUpdateContext() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            Instant now = Instant.now();
            UpdateRefundPolicyCommand command =
                    RefundPolicyCommandFixtures.updateCommand(sellerId, policyId);

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<RefundPolicyId, RefundPolicyUpdateData> result =
                    sut.createUpdateContext(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id().value()).isEqualTo(policyId);
            assertThat(result.changedAt()).isEqualTo(now);
            assertThat(result.updateData()).isNotNull();
        }
    }

    @Nested
    @DisplayName(
            "createStatusChangeContexts() - ChangeRefundPolicyStatusCommand → StatusChangeContext"
                    + " 목록 변환")
    class CreateStatusChangeContextsTest {

        @Test
        @DisplayName("ChangeRefundPolicyStatusCommand로부터 StatusChangeContext 목록을 생성한다")
        void createStatusChangeContexts_CreatesContextList() {
            // given
            Long sellerId = 1L;
            Long policyId1 = 100L;
            Long policyId2 = 101L;
            Instant now = Instant.now();
            ChangeRefundPolicyStatusCommand command =
                    RefundPolicyCommandFixtures.activateCommand(sellerId, policyId1, policyId2);

            given(timeProvider.now()).willReturn(now);

            // when
            List<StatusChangeContext<RefundPolicyId>> result =
                    sut.createStatusChangeContexts(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id().value()).isEqualTo(policyId1);
            assertThat(result.get(1).id().value()).isEqualTo(policyId2);
            assertThat(result.get(0).changedAt()).isEqualTo(now);
        }
    }
}
