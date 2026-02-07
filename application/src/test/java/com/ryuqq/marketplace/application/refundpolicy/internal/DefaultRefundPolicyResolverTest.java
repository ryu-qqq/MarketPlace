package com.ryuqq.marketplace.application.refundpolicy.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyCommandManager;
import com.ryuqq.marketplace.application.refundpolicy.manager.RefundPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.RefundPolicyFixtures;
import com.ryuqq.marketplace.domain.refundpolicy.aggregate.RefundPolicy;
import com.ryuqq.marketplace.domain.refundpolicy.exception.CannotUnmarkOnlyDefaultRefundPolicyException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
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
@DisplayName("DefaultRefundPolicyResolver 단위 테스트")
class DefaultRefundPolicyResolverTest {

    @InjectMocks private DefaultRefundPolicyResolver sut;

    @Mock private RefundPolicyReadManager readManager;
    @Mock private RefundPolicyCommandManager commandManager;

    @Nested
    @DisplayName("resolveForRegistration() - 등록 시 기본 정책 해결")
    class ResolveForRegistrationTest {

        @Test
        @DisplayName("새 정책이 기본 정책이고 기존 기본 정책이 있으면 기존 것을 해제한다")
        void resolveForRegistration_NewDefaultWithExisting_UnmarksExisting() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy newPolicy = RefundPolicyFixtures.newRefundPolicy();
            RefundPolicy existingDefault = RefundPolicyFixtures.activeRefundPolicy();
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId))
                    .willReturn(Optional.of(existingDefault));

            // when
            sut.resolveForRegistration(sellerId, newPolicy, timestamp);

            // then
            assertThat(existingDefault.isDefaultPolicy()).isFalse();
            then(commandManager).should().persist(existingDefault);
        }

        @Test
        @DisplayName("셀러의 첫 번째 정책이면 자동으로 기본 정책으로 설정한다")
        void resolveForRegistration_FirstPolicy_MarksAsDefault() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy newPolicy = RefundPolicyFixtures.newRefundPolicy(7, 14);
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId)).willReturn(Optional.empty());

            // when
            sut.resolveForRegistration(sellerId, newPolicy, timestamp);

            // then
            assertThat(newPolicy.isDefaultPolicy()).isTrue();
            then(commandManager).should(never()).persist(newPolicy);
        }

        @Test
        @DisplayName("새 정책이 기본 정책이 아니고 기존 기본 정책이 있으면 아무 작업도 하지 않는다")
        void resolveForRegistration_NonDefaultWithExisting_NoAction() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy newPolicy = RefundPolicyFixtures.newRefundPolicy(7, 14);
            RefundPolicy existingDefault = RefundPolicyFixtures.activeRefundPolicy();
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId))
                    .willReturn(Optional.of(existingDefault));

            // when
            sut.resolveForRegistration(sellerId, newPolicy, timestamp);

            // then
            assertThat(existingDefault.isDefaultPolicy()).isTrue();
            then(commandManager).should(never()).persist(existingDefault);
        }
    }

    @Nested
    @DisplayName("resolveForUpdate() - 수정 시 기본 정책 해결")
    class ResolveForUpdateTest {

        @Test
        @DisplayName("newDefaultPolicy가 null이면 아무 작업도 하지 않는다")
        void resolveForUpdate_NullDefaultPolicy_NoAction() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant timestamp = Instant.now();

            // when
            sut.resolveForUpdate(sellerId, policy, null, timestamp);

            // then
            then(readManager).should(never()).findDefaultBySellerId(sellerId);
        }

        @Test
        @DisplayName("기본 정책 상태가 변경되지 않으면 아무 작업도 하지 않는다")
        void resolveForUpdate_SameDefaultPolicy_NoAction() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant timestamp = Instant.now();

            // when
            sut.resolveForUpdate(sellerId, policy, true, timestamp);

            // then
            then(readManager).should(never()).findDefaultBySellerId(sellerId);
        }

        @Test
        @DisplayName("기본 정책으로 변경하면 기존 기본 정책을 해제한다")
        void resolveForUpdate_MarkAsDefault_UnmarksExisting() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy policy = RefundPolicyFixtures.inactiveRefundPolicy();
            RefundPolicy existingDefault = RefundPolicyFixtures.activeRefundPolicy();
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId))
                    .willReturn(Optional.of(existingDefault));

            // when
            sut.resolveForUpdate(sellerId, policy, true, timestamp);

            // then
            assertThat(policy.isDefaultPolicy()).isTrue();
            assertThat(existingDefault.isDefaultPolicy()).isFalse();
            then(commandManager).should().persist(existingDefault);
        }

        @Test
        @DisplayName("유일한 기본 정책을 해제하려 하면 예외를 발생시킨다")
        void resolveForUpdate_UnmarkOnlyDefault_ThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            RefundPolicy policy = RefundPolicyFixtures.activeRefundPolicy();
            Instant timestamp = Instant.now();

            // when & then
            assertThatThrownBy(() -> sut.resolveForUpdate(sellerId, policy, false, timestamp))
                    .isInstanceOf(CannotUnmarkOnlyDefaultRefundPolicyException.class);
        }
    }
}
