package com.ryuqq.marketplace.application.claimhistory.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimhistory.port.out.query.ClaimHistoryQueryPort;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistorySortKey;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
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
@DisplayName("ClaimHistoryReadManager 단위 테스트")
class ClaimHistoryReadManagerTest {

    @InjectMocks private ClaimHistoryReadManager sut;

    @Mock private ClaimHistoryQueryPort queryPort;

    @Nested
    @DisplayName("findByClaimId() - 클레임 ID로 이력 조회")
    class FindByClaimIdTest {

        @Test
        @DisplayName("클레임 타입과 ID로 해당 클레임의 이력 목록을 반환한다")
        void findByClaimId_ValidParams_ReturnsHistories() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            String claimId = "cancel-claim-001";
            List<ClaimHistory> expected =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.manualClaimHistory());

            given(queryPort.findByClaimTypeAndClaimId(claimType, claimId)).willReturn(expected);

            // when
            List<ClaimHistory> result = sut.findByClaimId(claimType, claimId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(2);
            then(queryPort).should().findByClaimTypeAndClaimId(claimType, claimId);
        }

        @Test
        @DisplayName("이력이 없는 클레임 조회 시 빈 목록을 반환한다")
        void findByClaimId_NoHistories_ReturnsEmptyList() {
            // given
            ClaimType claimType = ClaimType.REFUND;
            String claimId = "refund-claim-999";

            given(queryPort.findByClaimTypeAndClaimId(claimType, claimId)).willReturn(List.of());

            // when
            List<ClaimHistory> result = sut.findByClaimId(claimType, claimId);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByClaimTypeAndClaimId(claimType, claimId);
        }

        @Test
        @DisplayName("교환 클레임 이력을 조회한다")
        void findByClaimId_ExchangeClaimType_ReturnsHistories() {
            // given
            ClaimType claimType = ClaimType.EXCHANGE;
            String claimId = "exchange-claim-001";
            List<ClaimHistory> expected =
                    List.of(ClaimHistoryFixtures.exchangeStatusChangeHistory());

            given(queryPort.findByClaimTypeAndClaimId(claimType, claimId)).willReturn(expected);

            // when
            List<ClaimHistory> result = sut.findByClaimId(claimType, claimId);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByClaimTypeAndClaimId(claimType, claimId);
        }
    }

    @Nested
    @DisplayName("findByClaimIds() - 여러 클레임 ID로 이력 조회")
    class FindByClaimIdsTest {

        @Test
        @DisplayName("여러 클레임 ID로 이력 목록을 반환한다")
        void findByClaimIds_ValidParams_ReturnsHistories() {
            // given
            ClaimType claimType = ClaimType.CANCEL;
            List<String> claimIds =
                    List.of("cancel-claim-001", "cancel-claim-002", "cancel-claim-003");
            List<ClaimHistory> expected =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.cancelStatusChangeHistory());

            given(queryPort.findByClaimTypeAndClaimIds(claimType, claimIds)).willReturn(expected);

            // when
            List<ClaimHistory> result = sut.findByClaimIds(claimType, claimIds);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(3);
            then(queryPort).should().findByClaimTypeAndClaimIds(claimType, claimIds);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 목록을 반환한다")
        void findByClaimIds_NoHistories_ReturnsEmptyList() {
            // given
            ClaimType claimType = ClaimType.REFUND;
            List<String> claimIds = List.of("refund-claim-999");

            given(queryPort.findByClaimTypeAndClaimIds(claimType, claimIds)).willReturn(List.of());

            // when
            List<ClaimHistory> result = sut.findByClaimIds(claimType, claimIds);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByClaimTypeAndClaimIds(claimType, claimIds);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회하면 빈 목록을 반환한다")
        void findByClaimIds_EmptyClaimIds_ReturnsEmptyList() {
            // given
            ClaimType claimType = ClaimType.EXCHANGE;
            List<String> emptyClaimIds = List.of();

            given(queryPort.findByClaimTypeAndClaimIds(claimType, emptyClaimIds))
                    .willReturn(List.of());

            // when
            List<ClaimHistory> result = sut.findByClaimIds(claimType, emptyClaimIds);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByClaimTypeAndClaimIds(claimType, emptyClaimIds);
        }
    }

    @Nested
    @DisplayName("findByOrderItemId() - 주문 아이템 ID로 이력 조회")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("주문 아이템 ID로 해당 이력 목록을 반환한다")
        void findByOrderItemId_ValidOrderItemId_ReturnsHistories() {
            // given
            String orderItemId = ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID;
            List<ClaimHistory> expected =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.refundStatusChangeHistory());

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(expected);

            // when
            List<ClaimHistory> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(2);
            then(queryPort).should().findByOrderItemId(orderItemId);
        }

        @Test
        @DisplayName("이력이 없는 주문 아이템 조회 시 빈 목록을 반환한다")
        void findByOrderItemId_NoHistories_ReturnsEmptyList() {
            // given
            String orderItemId = "order-item-999";

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            List<ClaimHistory> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByOrderItemId(orderItemId);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 페이지 조건으로 이력 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("조회 조건으로 클레임 이력 목록을 반환한다")
        void findByCriteria_ValidCriteria_ReturnsHistories() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf(ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID);
            List<ClaimHistory> expected =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.manualClaimHistory());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ClaimHistory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result).hasSize(2);
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("조회 결과가 없으면 빈 목록을 반환한다")
        void findByCriteria_NoResults_ReturnsEmptyList() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("order-item-999");

            given(queryPort.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<ClaimHistory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("클레임 타입 필터가 있는 조건으로 이력을 조회한다")
        void findByCriteria_WithClaimTypeFilter_ReturnsFilteredHistories() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID,
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));
            List<ClaimHistory> expected = List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ClaimHistory> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 페이지 조건으로 이력 수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조회 조건으로 클레임 이력 수를 반환한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf(ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID);
            long expected = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("이력이 없으면 0을 반환한다")
        void countByCriteria_NoResults_ReturnsZero() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf("order-item-999");

            given(queryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
