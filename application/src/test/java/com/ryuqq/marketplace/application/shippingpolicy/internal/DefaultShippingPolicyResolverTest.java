package com.ryuqq.marketplace.application.shippingpolicy.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyCommandManager;
import com.ryuqq.marketplace.application.shippingpolicy.manager.ShippingPolicyReadManager;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.ShippingPolicyFixtures;
import com.ryuqq.marketplace.domain.shippingpolicy.aggregate.ShippingPolicy;
import com.ryuqq.marketplace.domain.shippingpolicy.exception.CannotUnmarkOnlyDefaultShippingPolicyException;
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
@DisplayName("DefaultShippingPolicyResolver 단위 테스트")
class DefaultShippingPolicyResolverTest {

    @InjectMocks private DefaultShippingPolicyResolver sut;

    @Mock private ShippingPolicyReadManager readManager;
    @Mock private ShippingPolicyCommandManager commandManager;

    @Nested
    @DisplayName("resolveForRegistration() - 등록 시 기본 정책 해결")
    class ResolveForRegistrationTest {

        @Test
        @DisplayName("첫 번째 정책이면 자동으로 기본 정책으로 설정한다")
        void resolveForRegistration_FirstPolicy_MarksAsDefault() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy newPolicy = ShippingPolicyFixtures.newPaidShippingPolicy();
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId)).willReturn(Optional.empty());

            // when
            sut.resolveForRegistration(sellerId, newPolicy, timestamp);

            // then - newPolicy가 기본 정책으로 설정됨 (markAsDefault 호출)
            then(readManager).should().findDefaultBySellerId(sellerId);
            then(commandManager).should(never()).persist(newPolicy);
        }

        @Test
        @DisplayName("새 정책이 기본 정책이고 기존 기본 정책이 있으면 기존 것을 해제한다")
        void resolveForRegistration_NewDefault_UnmarksExisting() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy newDefaultPolicy = ShippingPolicyFixtures.newFreeShippingPolicy();
            ShippingPolicy existingDefault = ShippingPolicyFixtures.activeShippingPolicy();
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId))
                    .willReturn(Optional.of(existingDefault));

            // when
            sut.resolveForRegistration(sellerId, newDefaultPolicy, timestamp);

            // then
            then(commandManager).should().persist(existingDefault);
        }
    }

    @Nested
    @DisplayName("resolveForUpdate() - 수정 시 기본 정책 해결")
    class ResolveForUpdateTest {

        @Test
        @DisplayName("newDefaultPolicy가 null이면 아무 작업도 하지 않는다")
        void resolveForUpdate_NullNewDefaultPolicy_DoesNothing() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy policy = ShippingPolicyFixtures.activeShippingPolicy();
            Instant timestamp = Instant.now();

            // when
            sut.resolveForUpdate(sellerId, policy, null, timestamp);

            // then
            then(readManager).should(never()).findDefaultBySellerId(sellerId);
            then(commandManager).should(never()).persist(policy);
        }

        @Test
        @DisplayName("기본 정책 상태가 변경되지 않으면 아무 작업도 하지 않는다")
        void resolveForUpdate_SameDefaultStatus_DoesNothing() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy defaultPolicy = ShippingPolicyFixtures.activeShippingPolicy();
            Instant timestamp = Instant.now();

            // when - 이미 기본 정책인데 true로 설정
            sut.resolveForUpdate(sellerId, defaultPolicy, true, timestamp);

            // then
            then(readManager).should(never()).findDefaultBySellerId(sellerId);
        }

        @Test
        @DisplayName("비기본 정책을 기본 정책으로 변경하면 기존 기본 정책을 해제한다")
        void resolveForUpdate_MarkAsDefault_UnmarksExisting() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy nonDefaultPolicy = ShippingPolicyFixtures.inactiveShippingPolicy();
            ShippingPolicy existingDefault = ShippingPolicyFixtures.activeShippingPolicy();
            Instant timestamp = Instant.now();

            given(readManager.findDefaultBySellerId(sellerId))
                    .willReturn(Optional.of(existingDefault));

            // when
            sut.resolveForUpdate(sellerId, nonDefaultPolicy, true, timestamp);

            // then
            then(commandManager).should().persist(existingDefault);
        }

        @Test
        @DisplayName("유일한 기본 정책을 해제하려 하면 예외가 발생한다")
        void resolveForUpdate_UnmarkOnlyDefault_ThrowsException() {
            // given
            SellerId sellerId = CommonVoFixtures.defaultSellerId();
            ShippingPolicy defaultPolicy = ShippingPolicyFixtures.activeShippingPolicy();
            Instant timestamp = Instant.now();

            // when & then
            assertThatThrownBy(
                            () -> sut.resolveForUpdate(sellerId, defaultPolicy, false, timestamp))
                    .isInstanceOf(CannotUnmarkOnlyDefaultShippingPolicyException.class);
        }
    }
}
