package com.ryuqq.marketplace.application.exchange.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeOwnershipMismatchException;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
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
@DisplayName("ExchangeBatchValidator 단위 테스트")
class ExchangeBatchValidatorTest {

    @InjectMocks private ExchangeBatchValidator sut;

    @Mock private ExchangeReadManager exchangeReadManager;
    @Mock private RefundReadManager refundReadManager;

    @Nested
    @DisplayName("validateAndGet() - 소유권 검증 후 클레임 반환")
    class ValidateAndGetTest {

        @Test
        @DisplayName("요청한 클레임 ID 수와 조회된 수가 일치하면 클레임 목록을 반환한다")
        void validateAndGet_AllFound_ReturnsClaims() {
            // given
            String claimId = "01900000-0000-7000-0000-000000000001";
            List<String> claimIds = List.of(claimId);
            Long sellerId = 100L;
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            given(exchangeReadManager.findByIdIn(claimIds, sellerId)).willReturn(List.of(claim));

            // when
            List<ExchangeClaim> result = sut.validateAndGet(claimIds, sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(claim);
        }

        @Test
        @DisplayName("요청한 클레임 ID 수보다 조회된 수가 적으면 ExchangeOwnershipMismatchException이 발생한다")
        void validateAndGet_SomeMissing_ThrowsException() {
            // given
            List<String> claimIds =
                    List.of(
                            "01900000-0000-7000-0000-000000000001",
                            "01900000-0000-7000-0000-000000000002");
            Long sellerId = 100L;
            ExchangeClaim claim = ExchangeFixtures.requestedExchangeClaim();

            given(exchangeReadManager.findByIdIn(claimIds, sellerId)).willReturn(List.of(claim));

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(claimIds, sellerId))
                    .isInstanceOf(ExchangeOwnershipMismatchException.class);
        }

        @Test
        @DisplayName("빈 클레임 ID 목록이면 빈 목록을 반환한다")
        void validateAndGet_EmptyIds_ReturnsEmptyList() {
            // given
            List<String> claimIds = List.of();
            Long sellerId = 100L;

            given(exchangeReadManager.findByIdIn(claimIds, sellerId)).willReturn(List.of());

            // when
            List<ExchangeClaim> result = sut.validateAndGet(claimIds, sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("hasActiveClaim() - 진행 중인 클레임 존재 여부 확인")
    class HasActiveClaimTest {

        @Test
        @DisplayName("진행 중인 Refund가 있으면 true를 반환한다")
        void hasActiveClaim_HasActiveRefund_ReturnsTrue() {
            // given
            Long orderItemId = 1001L;
            RefundClaim activeRefund = RefundFixtures.requestedRefundClaim();

            given(refundReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(activeRefund));

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("진행 중인 Exchange가 있으면 true를 반환한다")
        void hasActiveClaim_HasActiveExchange_ReturnsTrue() {
            // given
            Long orderItemId = 1001L;
            ExchangeClaim activeExchange = ExchangeFixtures.requestedExchangeClaim();
            OrderItemId orderItemIdVo = OrderItemId.of(1001L);

            given(refundReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());
            given(exchangeReadManager.findByOrderItemId(orderItemIdVo))
                    .willReturn(Optional.of(activeExchange));

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("진행 중인 클레임이 없으면 false를 반환한다")
        void hasActiveClaim_NoActiveClaim_ReturnsFalse() {
            // given
            Long orderItemId = 1001L;
            ExchangeClaim completedExchange = ExchangeFixtures.completedExchangeClaim();
            OrderItemId orderItemIdVo = OrderItemId.of(1001L);

            given(refundReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());
            given(exchangeReadManager.findByOrderItemId(orderItemIdVo))
                    .willReturn(Optional.of(completedExchange));

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Refund와 Exchange 모두 없으면 false를 반환한다")
        void hasActiveClaim_NoClaims_ReturnsFalse() {
            // given
            Long orderItemId = 1001L;
            OrderItemId orderItemIdVo = OrderItemId.of(1001L);

            given(refundReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());
            given(exchangeReadManager.findByOrderItemId(orderItemIdVo))
                    .willReturn(Optional.empty());

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isFalse();
        }
    }
}
