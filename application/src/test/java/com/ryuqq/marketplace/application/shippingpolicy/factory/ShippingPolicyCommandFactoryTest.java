package com.ryuqq.marketplace.application.shippingpolicy.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.dto.command.UpdateContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.shippingpolicy.ShippingPolicyCommandFixtures;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicyUpdateData;
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
@DisplayName("ShippingPolicyCommandFactory 단위 테스트")
class ShippingPolicyCommandFactoryTest {

    @InjectMocks private ShippingPolicyCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - RegisterShippingPolicyCommand → ShippingPolicy 변환")
    class CreateTest {

        @Test
        @DisplayName("RegisterShippingPolicyCommand로부터 ShippingPolicy를 생성한다")
        void create_CreatesShippingPolicy() {
            // given
            Long sellerId = 1L;
            Instant now = Instant.now();
            RegisterShippingPolicyCommand command =
                    ShippingPolicyCommandFixtures.registerCommand(sellerId);

            given(timeProvider.now()).willReturn(now);

            // when
            ShippingPolicy result = sut.create(command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId().value()).isEqualTo(sellerId);
            assertThat(result.policyName().value()).isEqualTo(command.policyName());
            assertThat(result.isDefaultPolicy()).isTrue();
            assertThat(result.createdAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("무료 배송 정책을 생성한다")
        void create_FreeShippingPolicy_CreatesWithZeroBaseFee() {
            // given
            Long sellerId = 1L;
            Instant now = Instant.now();
            RegisterShippingPolicyCommand command =
                    ShippingPolicyCommandFixtures.freeShippingRegisterCommand(sellerId);

            given(timeProvider.now()).willReturn(now);

            // when
            ShippingPolicy result = sut.create(command);

            // then
            assertThat(result.shippingFeeType().name()).isEqualTo("FREE");
            assertThat(result.isDefaultPolicy()).isFalse();
        }
    }

    @Nested
    @DisplayName("createUpdateContext() - UpdateShippingPolicyCommand → UpdateContext 변환")
    class CreateUpdateContextTest {

        @Test
        @DisplayName("UpdateShippingPolicyCommand로부터 UpdateContext를 생성한다")
        void createUpdateContext_CreatesUpdateContext() {
            // given
            Long sellerId = 1L;
            Long policyId = 100L;
            Instant now = Instant.now();
            UpdateShippingPolicyCommand command =
                    ShippingPolicyCommandFixtures.updateCommand(sellerId, policyId);

            given(timeProvider.now()).willReturn(now);

            // when
            UpdateContext<ShippingPolicyId, ShippingPolicyUpdateData> result =
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
            "createStatusChangeContexts() - ChangeShippingPolicyStatusCommand → StatusChangeContext"
                    + " 목록 변환")
    class CreateStatusChangeContextsTest {

        @Test
        @DisplayName("ChangeShippingPolicyStatusCommand로부터 StatusChangeContext 목록을 생성한다")
        void createStatusChangeContexts_CreatesContextList() {
            // given
            Long sellerId = 1L;
            Long policyId1 = 100L;
            Long policyId2 = 101L;
            Instant now = Instant.now();
            ChangeShippingPolicyStatusCommand command =
                    ShippingPolicyCommandFixtures.activateCommand(sellerId, policyId1, policyId2);

            given(timeProvider.now()).willReturn(now);

            // when
            List<StatusChangeContext<ShippingPolicyId>> result =
                    sut.createStatusChangeContexts(command);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).id().value()).isEqualTo(policyId1);
            assertThat(result.get(1).id().value()).isEqualTo(policyId2);
            assertThat(result.get(0).changedAt()).isEqualTo(now);
        }
    }
}
