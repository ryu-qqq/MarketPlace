package com.ryuqq.marketplace.application.claimhistory.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.claimhistory.assembler.ClaimHistoryAssembler;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryPageResult;
import com.ryuqq.marketplace.application.claimhistory.dto.response.ClaimHistoryResult;
import com.ryuqq.marketplace.application.claimhistory.manager.ClaimHistoryReadManager;
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
@DisplayName("GetOrderClaimHistoriesService 단위 테스트")
class GetOrderClaimHistoriesServiceTest {

    @InjectMocks private GetOrderClaimHistoriesService sut;

    @Mock private ClaimHistoryReadManager readManager;
    @Mock private ClaimHistoryAssembler assembler;

    @Nested
    @DisplayName("execute() - 주문 클레임 이력 페이지 조회")
    class ExecuteTest {

        @Test
        @DisplayName("조회 조건으로 이력 목록과 페이지 메타를 포함한 결과를 반환한다")
        void execute_ValidCriteria_ReturnsClaimHistoryPageResult() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf(ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID);
            List<ClaimHistory> histories =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.manualClaimHistory());
            List<ClaimHistoryResult> results =
                    histories.stream()
                            .map(
                                    h ->
                                            new ClaimHistoryResult(
                                                    h.idValue(),
                                                    h.historyType().name(),
                                                    h.title(),
                                                    h.message(),
                                                    h.actor().actorType().name(),
                                                    h.actor().actorId(),
                                                    h.actor().actorName(),
                                                    h.createdAt()))
                            .toList();
            long totalCount = 2L;

            given(readManager.findByCriteria(criteria)).willReturn(histories);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toResults(histories)).willReturn(results);

            // when
            ClaimHistoryPageResult result = sut.execute(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalCount);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
            then(assembler).should().toResults(histories);
        }

        @Test
        @DisplayName("이력이 없으면 빈 결과 목록과 totalCount 0을 반환한다")
        void execute_NoHistories_ReturnsEmptyPageResult() {
            // given
            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(999L);

            given(readManager.findByCriteria(criteria)).willReturn(List.of());
            given(readManager.countByCriteria(criteria)).willReturn(0L);
            given(assembler.toResults(List.of())).willReturn(List.of());

            // when
            ClaimHistoryPageResult result = sut.execute(criteria);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("클레임 타입 필터 조건으로 조회하면 해당 타입 이력만 반환한다")
        void execute_WithClaimTypeFilter_ReturnsFilteredPageResult() {
            // given
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID,
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));
            List<ClaimHistory> cancelHistories =
                    List.of(ClaimHistoryFixtures.cancelStatusChangeHistory());
            List<ClaimHistoryResult> cancelResults =
                    cancelHistories.stream()
                            .map(
                                    h ->
                                            new ClaimHistoryResult(
                                                    h.idValue(),
                                                    h.historyType().name(),
                                                    h.title(),
                                                    h.message(),
                                                    h.actor().actorType().name(),
                                                    h.actor().actorId(),
                                                    h.actor().actorName(),
                                                    h.createdAt()))
                            .toList();
            long totalCount = 1L;

            given(readManager.findByCriteria(criteria)).willReturn(cancelHistories);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toResults(cancelHistories)).willReturn(cancelResults);

            // when
            ClaimHistoryPageResult result = sut.execute(criteria);

            // then
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(1L);
            then(readManager).should().findByCriteria(criteria);
            then(readManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("페이지 메타에 올바른 page/size 정보가 포함된다")
        void execute_ValidCriteria_PageMetaContainsCorrectPageAndSize() {
            // given
            int page = 0;
            int size = 20;
            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.defaultOf(ClaimHistoryFixtures.DEFAULT_ORDER_ITEM_ID);
            long totalCount = 3L;
            List<ClaimHistory> histories =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.refundStatusChangeHistory(),
                            ClaimHistoryFixtures.exchangeStatusChangeHistory());

            given(readManager.findByCriteria(criteria)).willReturn(histories);
            given(readManager.countByCriteria(criteria)).willReturn(totalCount);
            given(assembler.toResults(histories)).willReturn(List.of());

            // when
            ClaimHistoryPageResult result = sut.execute(criteria);

            // then
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalCount);
        }
    }
}
