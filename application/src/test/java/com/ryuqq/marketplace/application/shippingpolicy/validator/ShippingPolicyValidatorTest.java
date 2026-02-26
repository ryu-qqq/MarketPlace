package com.ryuqq.marketplace.application.shippingpolicy.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.LastActiveShippingPolicyCannotBeDeactivatedException;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.ShippingPolicyException;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
@DisplayName("ShippingPolicyValidator 단위 테스트")
class ShippingPolicyValidatorTest {

    @InjectMocks private ShippingPolicyValidator sut;

    @Mock private ShippingPolicyReadManager shippingPolicyReadManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 배송 정책 존재 검증")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 배송 정책을 반환한다")
        void findExistingOrThrow_ExistingPolicy_ReturnsPolicy() {
            // given
            ShippingPolicyId id = ShippingPolicyId.of(1L);
            ShippingPolicy expected = ShippingPolicyFixtures.activeShippingPolicy();

            given(shippingPolicyReadManager.getById(id)).willReturn(expected);

            // when
            ShippingPolicy result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(shippingPolicyReadManager).should().getById(id);
        }
    }

    @Nested
    @DisplayName("findExistingBySellerOrThrow() - 셀러의 배송 정책 존재 검증")
    class FindExistingBySellerOrThrowTest {

        @Test
        @DisplayName("셀러가 소유한 배송 정책을 반환한다")
        void findExistingBySellerOrThrow_ExistingPolicy_ReturnsPolicy() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicyId policyId = ShippingPolicyId.of(1L);
            ShippingPolicy expected = ShippingPolicyFixtures.activeShippingPolicy();

            given(shippingPolicyReadManager.findBySellerIdAndId(sellerId, policyId))
                    .willReturn(Optional.of(expected));

            // when
            ShippingPolicy result = sut.findExistingBySellerOrThrow(sellerId, policyId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 정책 조회 시 예외가 발생한다")
        void findExistingBySellerOrThrow_NonExistingPolicy_ThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicyId policyId = ShippingPolicyId.of(999L);

            given(shippingPolicyReadManager.findBySellerIdAndId(sellerId, policyId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingBySellerOrThrow(sellerId, policyId))
                    .isInstanceOf(ShippingPolicyException.class);
        }
    }

    @Nested
    @DisplayName("findAllExistingOrThrow() - 배송 정책 목록 존재 검증")
    class FindAllExistingOrThrowTest {

        @Test
        @DisplayName("빈 ID 목록이면 빈 목록을 반환한다")
        void findAllExistingOrThrow_EmptyIds_ReturnsEmptyList() {
            // given
            List<ShippingPolicyId> ids = Collections.emptyList();

            // when
            List<ShippingPolicy> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("모든 ID에 해당하는 정책이 존재하면 목록을 반환한다")
        void findAllExistingOrThrow_AllExist_ReturnsPolicies() {
            // given
            ShippingPolicyId id1 = ShippingPolicyId.of(1L);
            ShippingPolicyId id2 = ShippingPolicyId.of(2L);
            List<ShippingPolicyId> ids = List.of(id1, id2);

            ShippingPolicy policy1 =
                    ShippingPolicyFixtures.activeShippingPolicy(
                            1L, CommonVoFixtures.defaultSellerId());
            ShippingPolicy policy2 =
                    ShippingPolicyFixtures.activeShippingPolicy(
                            2L, CommonVoFixtures.defaultSellerId());

            given(shippingPolicyReadManager.getByIds(ids)).willReturn(List.of(policy1, policy2));

            // when
            List<ShippingPolicy> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("일부 ID가 존재하지 않으면 예외가 발생한다")
        void findAllExistingOrThrow_SomeMissing_ThrowsException() {
            // given
            ShippingPolicyId id1 = ShippingPolicyId.of(1L);
            ShippingPolicyId id2 = ShippingPolicyId.of(2L);
            List<ShippingPolicyId> ids = List.of(id1, id2);

            ShippingPolicy policy1 =
                    ShippingPolicyFixtures.activeShippingPolicy(
                            1L, CommonVoFixtures.defaultSellerId());
            // id2는 존재하지 않음
            given(shippingPolicyReadManager.getByIds(ids)).willReturn(List.of(policy1));

            // when & then
            assertThatThrownBy(() -> sut.findAllExistingOrThrow(ids))
                    .isInstanceOf(ShippingPolicyException.class);
        }
    }

    @Nested
    @DisplayName("validateNotLastActivePolicy() - 마지막 활성 정책 비활성화 검증")
    class ValidateNotLastActivePolicyTest {

        @Test
        @DisplayName("비활성화할 정책이 없으면 검증을 통과한다")
        void validateNotLastActivePolicy_EmptyList_Passes() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            List<ShippingPolicy> policies = Collections.emptyList();

            // when & then - 예외 없이 통과
            sut.validateNotLastActivePolicy(sellerId, policies);
        }

        @Test
        @DisplayName("비활성 정책만 있으면 검증을 통과한다")
        void validateNotLastActivePolicy_OnlyInactivePolicies_Passes() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy inactivePolicy = ShippingPolicyFixtures.inactiveShippingPolicy();
            List<ShippingPolicy> policies = List.of(inactivePolicy);

            // when & then - 예외 없이 통과
            sut.validateNotLastActivePolicy(sellerId, policies);
        }

        @Test
        @DisplayName("마지막 활성 정책 비활성화 시도 시 예외가 발생한다")
        void validateNotLastActivePolicy_LastActivePolicy_ThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy activePolicy = ShippingPolicyFixtures.activeShippingPolicy();
            List<ShippingPolicy> policies = List.of(activePolicy);

            given(shippingPolicyReadManager.countActiveBySellerId(sellerId)).willReturn(1L);

            // when & then
            assertThatThrownBy(() -> sut.validateNotLastActivePolicy(sellerId, policies))
                    .isInstanceOf(LastActiveShippingPolicyCannotBeDeactivatedException.class);
        }

        @Test
        @DisplayName("다른 활성 정책이 있으면 검증을 통과한다")
        void validateNotLastActivePolicy_OtherActivePoliciesExist_Passes() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy activePolicy = ShippingPolicyFixtures.activeShippingPolicy();
            List<ShippingPolicy> policies = List.of(activePolicy);

            given(shippingPolicyReadManager.countActiveBySellerId(sellerId)).willReturn(3L);

            // when & then - 예외 없이 통과
            sut.validateNotLastActivePolicy(sellerId, policies);
        }
    }
}
