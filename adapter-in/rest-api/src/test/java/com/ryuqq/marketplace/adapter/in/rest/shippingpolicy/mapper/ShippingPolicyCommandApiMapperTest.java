package com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.ShippingPolicyApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.ChangeShippingPolicyStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.RegisterShippingPolicyApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shippingpolicy.dto.command.UpdateShippingPolicyApiRequest;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.ChangeShippingPolicyStatusCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.RegisterShippingPolicyCommand;
import com.ryuqq.marketplace.application.shippingpolicy.dto.command.UpdateShippingPolicyCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShippingPolicyCommandApiMapper 단위 테스트")
class ShippingPolicyCommandApiMapperTest {

    private ShippingPolicyCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShippingPolicyCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, RegisterShippingPolicyApiRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterShippingPolicyApiRequest를 RegisterShippingPolicyCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            RegisterShippingPolicyApiRequest request = ShippingPolicyApiFixtures.registerRequest();

            // when
            RegisterShippingPolicyCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyName()).isEqualTo("기본 배송정책");
            assertThat(command.defaultPolicy()).isTrue();
            assertThat(command.shippingFeeType()).isEqualTo("CONDITIONAL_FREE");
            assertThat(command.baseFee()).isEqualTo(3000L);
            assertThat(command.freeThreshold()).isEqualTo(50000L);
            assertThat(command.jejuExtraFee()).isEqualTo(3000L);
            assertThat(command.islandExtraFee()).isEqualTo(5000L);
            assertThat(command.returnFee()).isEqualTo(3000L);
            assertThat(command.exchangeFee()).isEqualTo(6000L);
        }

        @Test
        @DisplayName("LeadTime 정보가 올바르게 변환된다")
        void toCommand_ConvertsLeadTime_ReturnsLeadTimeCommand() {
            // given
            Long sellerId = 1L;
            RegisterShippingPolicyApiRequest request = ShippingPolicyApiFixtures.registerRequest();

            // when
            RegisterShippingPolicyCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.leadTime()).isNotNull();
            assertThat(command.leadTime().minDays()).isEqualTo(1);
            assertThat(command.leadTime().maxDays()).isEqualTo(3);
            assertThat(command.leadTime().cutoffTime()).isEqualTo("14:00");
        }

        @Test
        @DisplayName("LeadTime이 null이면 null로 변환된다")
        void toCommand_NullLeadTime_ReturnsNullLeadTimeCommand() {
            // given
            Long sellerId = 1L;
            RegisterShippingPolicyApiRequest request =
                    ShippingPolicyApiFixtures.registerRequestWithoutLeadTime();

            // when
            RegisterShippingPolicyCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.leadTime()).isNull();
        }
    }

    @Nested
    @DisplayName("toCommand(Long, Long, UpdateShippingPolicyApiRequest) - 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("UpdateShippingPolicyApiRequest를 UpdateShippingPolicyCommand로 변환한다")
        void toCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            Long policyId = 10L;
            UpdateShippingPolicyApiRequest request = ShippingPolicyApiFixtures.updateRequest();

            // when
            UpdateShippingPolicyCommand command = mapper.toCommand(sellerId, policyId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyId()).isEqualTo(10L);
            assertThat(command.policyName()).isEqualTo("수정된 배송정책");
            assertThat(command.defaultPolicy()).isFalse();
            assertThat(command.shippingFeeType()).isEqualTo("PAID");
            assertThat(command.baseFee()).isEqualTo(3500L);
            assertThat(command.freeThreshold()).isNull();
            assertThat(command.jejuExtraFee()).isEqualTo(3500L);
            assertThat(command.islandExtraFee()).isEqualTo(5500L);
            assertThat(command.returnFee()).isEqualTo(3500L);
            assertThat(command.exchangeFee()).isEqualTo(7000L);
        }

        @Test
        @DisplayName("수정 요청의 LeadTime 정보가 올바르게 변환된다")
        void toCommand_ConvertsLeadTime_ReturnsUpdatedLeadTimeCommand() {
            // given
            Long sellerId = 1L;
            Long policyId = 10L;
            UpdateShippingPolicyApiRequest request = ShippingPolicyApiFixtures.updateRequest();

            // when
            UpdateShippingPolicyCommand command = mapper.toCommand(sellerId, policyId, request);

            // then
            assertThat(command.leadTime()).isNotNull();
            assertThat(command.leadTime().minDays()).isEqualTo(2);
            assertThat(command.leadTime().maxDays()).isEqualTo(5);
            assertThat(command.leadTime().cutoffTime()).isEqualTo("12:00");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, ChangeShippingPolicyStatusApiRequest) - 상태 변경 요청 변환")
    class ToChangeStatusCommandTest {

        @Test
        @DisplayName(
                "ChangeShippingPolicyStatusApiRequest를 ChangeShippingPolicyStatusCommand로 변환한다")
        void toCommand_ConvertsChangeStatusRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            ChangeShippingPolicyStatusApiRequest request =
                    ShippingPolicyApiFixtures.changeStatusRequest();

            // when
            ChangeShippingPolicyStatusCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyIds()).containsExactly(1L, 2L, 3L);
            assertThat(command.active()).isFalse();
        }

        @Test
        @DisplayName("활성화 상태 변경 요청을 올바르게 변환한다")
        void toCommand_ConvertsActivateRequest_ReturnsCommand() {
            // given
            Long sellerId = 1L;
            ChangeShippingPolicyStatusApiRequest request =
                    ShippingPolicyApiFixtures.activateRequest();

            // when
            ChangeShippingPolicyStatusCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(1L);
            assertThat(command.policyIds()).containsExactly(1L, 2L);
            assertThat(command.active()).isTrue();
        }
    }
}
