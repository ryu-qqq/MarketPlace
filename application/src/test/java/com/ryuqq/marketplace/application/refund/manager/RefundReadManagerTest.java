package com.ryuqq.marketplace.application.refund.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.refund.port.out.query.RefundQueryPort;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
import com.ryuqq.marketplace.domain.refund.exception.RefundNotFoundException;
import com.ryuqq.marketplace.domain.refund.id.RefundClaimId;
import com.ryuqq.marketplace.domain.refund.query.RefundSearchCriteria;
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
@DisplayName("RefundReadManager 단위 테스트")
class RefundReadManagerTest {

    @InjectMocks private RefundReadManager sut;

    @Mock private RefundQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 RefundClaim 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 RefundClaim을 반환한다")
        void getById_ExistingId_ReturnsRefundClaim() {
            // given
            RefundClaimId id = RefundFixtures.defaultRefundClaimId();
            RefundClaim expected = RefundFixtures.requestedRefundClaim();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            RefundClaim result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 RefundNotFoundException이 발생한다")
        void getById_NonExistingId_ThrowsRefundNotFoundException() {
            // given
            RefundClaimId id = RefundClaimId.of("00000000-0000-7000-8000-000000000099");

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(RefundNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 조건으로 RefundClaim 목록 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 RefundClaim 목록을 반환한다")
        void findByCriteria_ValidCriteria_ReturnsRefundClaimList() {
            // given
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();
            List<RefundClaim> expected = List.of(RefundFixtures.requestedRefundClaim());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<RefundClaim> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 조건으로 RefundClaim 건수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 총 건수를 반환한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            RefundSearchCriteria criteria = RefundSearchCriteria.defaultCriteria();
            long expectedCount = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }

    @Nested
    @DisplayName("findByIdIn() - ID 목록으로 RefundClaim 조회")
    class FindByIdInTest {

        @Test
        @DisplayName("refundClaimId 목록과 sellerId로 RefundClaim 목록을 반환한다")
        void findByIdIn_ValidIds_ReturnsRefundClaimList() {
            // given
            List<String> refundClaimIds = List.of("01900000-0000-7000-8000-000000000010");
            Long sellerId = 10L;
            List<RefundClaim> expected = List.of(RefundFixtures.requestedRefundClaim());

            given(queryPort.findByIdIn(refundClaimIds, sellerId)).willReturn(expected);

            // when
            List<RefundClaim> result = sut.findByIdIn(refundClaimIds, sellerId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("sellerId가 null이면 슈퍼어드민으로 전체 조회한다")
        void findByIdIn_NullSellerId_ReturnsAllMatchingClaims() {
            // given
            List<String> refundClaimIds = List.of("01900000-0000-7000-8000-000000000010");
            List<RefundClaim> expected = List.of(RefundFixtures.requestedRefundClaim());

            given(queryPort.findByIdIn(refundClaimIds, null)).willReturn(expected);

            // when
            List<RefundClaim> result = sut.findByIdIn(refundClaimIds, null);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findByOrderItemId() - OrderItemId로 RefundClaim 조회")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("OrderItemId로 RefundClaim을 반환한다")
        void findByOrderItemId_ExistingOrderItemId_ReturnsRefundClaim() {
            // given
            String orderItemId = "01940001-0000-7000-8000-000000000001";
            RefundClaim expected = RefundFixtures.requestedRefundClaim();

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.of(expected));

            // when
            Optional<RefundClaim> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("OrderItemId에 해당하는 RefundClaim이 없으면 빈 Optional을 반환한다")
        void findByOrderItemId_NonExistingOrderItemId_ReturnsEmpty() {
            // given
            String orderItemId = "00000000-0000-7000-8000-000000000099";

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<RefundClaim> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
