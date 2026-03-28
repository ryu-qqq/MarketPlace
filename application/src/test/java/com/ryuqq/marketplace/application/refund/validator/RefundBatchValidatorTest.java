package com.ryuqq.marketplace.application.refund.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.exchange.manager.ExchangeReadManager;
import com.ryuqq.marketplace.application.refund.manager.RefundReadManager;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.exception.RefundOwnershipMismatchException;
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
@DisplayName("RefundBatchValidator 단위 테스트")
class RefundBatchValidatorTest {

    @InjectMocks private RefundBatchValidator sut;

    @Mock private RefundReadManager refundReadManager;
    @Mock private ExchangeReadManager exchangeReadManager;

    @Nested
    @DisplayName("validateAndGet() - 환불 배치 검증 및 조회")
    class ValidateAndGetTest {

        @Test
        @DisplayName("요청한 refundClaimIds가 모두 조회되면 RefundClaim 목록을 반환한다")
        void validateAndGet_AllIdsFound_ReturnsRefundClaimList() {
            // given
            String refundClaimId = "01900000-0000-7000-8000-000000000010";
            List<String> refundClaimIds = List.of(refundClaimId);
            Long sellerId = 10L;
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            given(refundReadManager.findByIdIn(refundClaimIds, sellerId))
                    .willReturn(List.of(claim));

            // when
            List<RefundClaim> result = sut.validateAndGet(refundClaimIds, sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(claim);
        }

        @Test
        @DisplayName("sellerId가 null이면 슈퍼어드민으로 전체 조회가 가능하다")
        void validateAndGet_NullSellerId_AllowsAllAccess() {
            // given
            String refundClaimId = "01900000-0000-7000-8000-000000000010";
            List<String> refundClaimIds = List.of(refundClaimId);
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            given(refundReadManager.findByIdIn(refundClaimIds, null)).willReturn(List.of(claim));

            // when
            List<RefundClaim> result = sut.validateAndGet(refundClaimIds, null);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("조회된 건수가 요청 건수보다 적으면 RefundOwnershipMismatchException이 발생한다")
        void validateAndGet_SomeIdsNotFound_ThrowsRefundOwnershipMismatchException() {
            // given
            List<String> refundClaimIds =
                    List.of(
                            "01900000-0000-7000-8000-000000000010",
                            "01900000-0000-7000-8000-000000000011");
            Long sellerId = 10L;
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            given(refundReadManager.findByIdIn(refundClaimIds, sellerId))
                    .willReturn(List.of(claim)); // 1건만 반환 (2건 요청)

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(refundClaimIds, sellerId))
                    .isInstanceOf(RefundOwnershipMismatchException.class);
        }

        @Test
        @DisplayName("다른 셀러의 RefundClaim을 요청하면 RefundOwnershipMismatchException이 발생한다")
        void validateAndGet_WrongSellerId_ThrowsRefundOwnershipMismatchException() {
            // given
            String refundClaimId = "01900000-0000-7000-8000-000000000010";
            List<String> refundClaimIds = List.of(refundClaimId);
            Long wrongSellerId = 999L;

            given(refundReadManager.findByIdIn(refundClaimIds, wrongSellerId))
                    .willReturn(List.of()); // 다른 셀러 소유이므로 빈 결과

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(refundClaimIds, wrongSellerId))
                    .isInstanceOf(RefundOwnershipMismatchException.class);
        }
    }

    @Nested
    @DisplayName("hasActiveClaim() - 활성 클레임 존재 여부 확인")
    class HasActiveClaimTest {

        @Test
        @DisplayName("활성 환불 클레임이 있으면 true를 반환한다")
        void hasActiveClaim_ActiveRefundExists_ReturnsTrue() {
            // given
            Long orderItemId = 1001L;
            RefundClaim activeClaim = RefundFixtures.requestedRefundClaim();

            given(refundReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(activeClaim));

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("활성 환불 클레임이 없고 활성 교환 클레임도 없으면 false를 반환한다")
        void hasActiveClaim_NoActiveClaims_ReturnsFalse() {
            // given
            Long orderItemId = 1001L;

            given(refundReadManager.findByOrderItemId(orderItemId)).willReturn(Optional.empty());
            given(exchangeReadManager.findByOrderItemId(OrderItemId.of(orderItemId)))
                    .willReturn(Optional.empty());

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("완료된 환불 클레임만 있으면 false를 반환한다")
        void hasActiveClaim_CompletedRefundOnly_ReturnsFalse() {
            // given
            Long orderItemId = 1001L;
            RefundClaim completedClaim = RefundFixtures.completedRefundClaim();

            given(refundReadManager.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(completedClaim));

            // when
            boolean result = sut.hasActiveClaim(orderItemId);

            // then
            assertThat(result).isFalse();
        }
    }
}
