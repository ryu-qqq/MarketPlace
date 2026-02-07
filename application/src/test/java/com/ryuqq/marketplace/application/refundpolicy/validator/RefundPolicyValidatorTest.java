package com.ryuqq.marketplace.application.refundpolicy.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.LastActiveRefundPolicyCannotBeDeactivatedException;
import com.ryuqq.marketplace.domain.refundpolicy.exception.RefundPolicyException;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
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
@DisplayName("RefundPolicyValidator 단위 테스트")
class RefundPolicyValidatorTest {

    @InjectMocks private RefundPolicyValidator sut;

    @Mock private RefundPolicyReadManager refundPolicyReadManager;

    @Nested
    @DisplayName("findExistingOrThrow() - ID로 정책 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 정책을 반환한다")
        void findExistingOrThrow_ReturnsPolicy() {
            // given
            RefundPolicyId id = RefundPolicyId.of(1L);
            RefundPolicy expected = RefundPolicyFixtures.activeRefundPolicy();

            given(refundPolicyReadManager.getById(id)).willReturn(expected);

            // when
            RefundPolicy result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findExistingBySellerOrThrow() - 셀러 ID와 정책 ID로 조회")
    class FindExistingBySellerOrThrowTest {

        @Test
        @DisplayName("셀러가 소유한 정책을 반환한다")
        void findExistingBySellerOrThrow_ReturnsPolicy() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicyId policyId = RefundPolicyId.of(100L);
            RefundPolicy expected = RefundPolicyFixtures.activeRefundPolicy();

            given(refundPolicyReadManager.findBySellerIdAndId(sellerId, policyId))
                    .willReturn(Optional.of(expected));

            // when
            RefundPolicy result = sut.findExistingBySellerOrThrow(sellerId, policyId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("정책이 존재하지 않으면 예외를 발생시킨다")
        void findExistingBySellerOrThrow_NotFound_ThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicyId policyId = RefundPolicyId.of(999L);

            given(refundPolicyReadManager.findBySellerIdAndId(sellerId, policyId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.findExistingBySellerOrThrow(sellerId, policyId))
                    .isInstanceOf(RefundPolicyException.class);
        }
    }

    @Nested
    @DisplayName("findAllExistingOrThrow() - ID 목록으로 정책 목록 조회")
    class FindAllExistingOrThrowTest {

        @Test
        @DisplayName("모든 정책을 반환한다")
        void findAllExistingOrThrow_ReturnsAllPolicies() {
            // given
            List<RefundPolicyId> ids = List.of(RefundPolicyId.of(1L), RefundPolicyId.of(2L));
            List<RefundPolicy> policies =
                    List.of(
                            RefundPolicyFixtures.activeRefundPolicy(),
                            RefundPolicyFixtures.inactiveRefundPolicy());

            given(refundPolicyReadManager.getByIds(ids)).willReturn(policies);

            // when
            List<RefundPolicy> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 목록이면 빈 결과를 반환한다")
        void findAllExistingOrThrow_EmptyIds_ReturnsEmptyList() {
            // given
            List<RefundPolicyId> ids = Collections.emptyList();

            // when
            List<RefundPolicy> result = sut.findAllExistingOrThrow(ids);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID가 포함되면 예외를 발생시킨다")
        void findAllExistingOrThrow_NotFound_ThrowsException() {
            // given
            List<RefundPolicyId> ids = List.of(RefundPolicyId.of(1L), RefundPolicyId.of(999L));
            List<RefundPolicy> policies = List.of(RefundPolicyFixtures.activeRefundPolicy());

            given(refundPolicyReadManager.getByIds(ids)).willReturn(policies);

            // when & then
            assertThatThrownBy(() -> sut.findAllExistingOrThrow(ids))
                    .isInstanceOf(RefundPolicyException.class);
        }
    }

    @Nested
    @DisplayName("validateNotLastActivePolicy() - 마지막 활성 정책 검증")
    class ValidateNotLastActivePolicyTest {

        @Test
        @DisplayName("다른 활성 정책이 있으면 검증을 통과한다")
        void validateNotLastActivePolicy_OtherActivePoliciesExist_Passes() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            List<RefundPolicy> policiesToDeactivate =
                    List.of(RefundPolicyFixtures.activeRefundPolicy());

            given(refundPolicyReadManager.countActiveBySellerId(sellerId)).willReturn(3L);

            // when & then (no exception)
            sut.validateNotLastActivePolicy(sellerId, policiesToDeactivate);
        }

        @Test
        @DisplayName("마지막 활성 정책을 비활성화하려 하면 예외를 발생시킨다")
        void validateNotLastActivePolicy_LastActivePolicy_ThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            List<RefundPolicy> policiesToDeactivate =
                    List.of(RefundPolicyFixtures.activeRefundPolicy());

            given(refundPolicyReadManager.countActiveBySellerId(sellerId)).willReturn(1L);

            // when & then
            assertThatThrownBy(
                            () -> sut.validateNotLastActivePolicy(sellerId, policiesToDeactivate))
                    .isInstanceOf(LastActiveRefundPolicyCannotBeDeactivatedException.class);
        }

        @Test
        @DisplayName("빈 목록이면 검증을 통과한다")
        void validateNotLastActivePolicy_EmptyList_Passes() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            List<RefundPolicy> emptyList = Collections.emptyList();

            // when & then (no exception)
            sut.validateNotLastActivePolicy(sellerId, emptyList);
        }

        @Test
        @DisplayName("비활성화된 정책만 있으면 검증을 통과한다")
        void validateNotLastActivePolicy_OnlyInactivePolicies_Passes() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            List<RefundPolicy> inactivePolicies =
                    List.of(RefundPolicyFixtures.inactiveRefundPolicy());

            // when & then (no exception, no DB call needed)
            sut.validateNotLastActivePolicy(sellerId, inactivePolicies);
        }
    }
}
