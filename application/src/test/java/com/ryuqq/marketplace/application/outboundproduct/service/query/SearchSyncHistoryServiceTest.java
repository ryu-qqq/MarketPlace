package com.ryuqq.marketplace.application.outboundproduct.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.factory.OmsSyncHistoryQueryFactory;
import com.ryuqq.marketplace.application.outboundproduct.manager.OmsSyncHistoryCompositionReadManager;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySearchCriteria;
import com.ryuqq.marketplace.domain.outboundproduct.query.SyncHistorySortKey;
import java.util.Collections;
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
@DisplayName("SearchSyncHistoryService 단위 테스트")
class SearchSyncHistoryServiceTest {

    @InjectMocks private SearchSyncHistoryService sut;

    @Mock private OmsSyncHistoryCompositionReadManager compositionReadManager;
    @Mock private OmsSyncHistoryQueryFactory queryFactory;

    @Nested
    @DisplayName("execute() - 연동 이력 검색 (Offset 페이징)")
    class ExecuteTest {

        @Test
        @DisplayName("상품 그룹 ID로 연동 이력 목록을 페이징하여 반환한다")
        void execute_ValidProductGroupId_ReturnsSyncHistoryPage() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId);
            SyncHistorySearchCriteria criteria =
                    new SyncHistorySearchCriteria(
                            productGroupId,
                            null,
                            null,
                            QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));

            List<SyncHistoryListResult> results =
                    List.of(
                            OmsProductQueryFixtures.syncHistoryListResult(1L),
                            OmsProductQueryFixtures.syncHistoryListResult(2L));
            long totalElements = 2L;

            SyncHistoryPageResult expected =
                    SyncHistoryPageResult.of(
                            results, criteria.page(), criteria.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            SyncHistoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().totalElements()).isEqualTo(2L);
            then(queryFactory).should().createCriteria(params);
            then(compositionReadManager).should().findByCriteria(criteria);
            then(compositionReadManager).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("연동 이력이 없으면 빈 목록을 반환한다")
        void execute_NoHistory_ReturnsEmptyPage() {
            // given
            long productGroupId = 999L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId);
            SyncHistorySearchCriteria criteria =
                    new SyncHistorySearchCriteria(
                            productGroupId,
                            null,
                            null,
                            QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));

            List<SyncHistoryListResult> emptyResults = Collections.emptyList();
            long totalElements = 0L;

            SyncHistoryPageResult expected =
                    SyncHistoryPageResult.of(
                            emptyResults, criteria.page(), criteria.size(), totalElements);

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(emptyResults);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            SyncHistoryPageResult result = sut.execute(params);

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.results()).isEmpty();
            assertThat(result.pageMeta().totalElements()).isZero();
        }

        @Test
        @DisplayName("상태 필터가 적용된 연동 이력을 검색한다")
        void execute_WithStatusFilter_FiltersHistory() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, "FAILED");
            SyncHistorySearchCriteria criteria =
                    new SyncHistorySearchCriteria(
                            productGroupId,
                            null,
                            null,
                            QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));

            List<SyncHistoryListResult> results =
                    List.of(OmsProductQueryFixtures.syncHistoryListResult(1L, "FAILED"));
            long totalElements = 1L;

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            SyncHistoryPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            assertThat(result.results().get(0).status()).isEqualTo("FAILED");
            then(queryFactory).should().createCriteria(params);
        }

        @Test
        @DisplayName("page, size를 지정하여 특정 페이지를 조회한다")
        void execute_WithPagination_ReturnsCorrectPage() {
            // given
            long productGroupId = 100L;
            SyncHistorySearchParams params =
                    OmsProductQueryFixtures.syncHistorySearchParams(productGroupId, 1, 10);
            SyncHistorySearchCriteria criteria =
                    new SyncHistorySearchCriteria(
                            productGroupId,
                            null,
                            null,
                            QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));

            List<SyncHistoryListResult> results =
                    List.of(OmsProductQueryFixtures.syncHistoryListResult(11L));
            long totalElements = 11L;

            given(queryFactory.createCriteria(params)).willReturn(criteria);
            given(compositionReadManager.findByCriteria(criteria)).willReturn(results);
            given(compositionReadManager.countByCriteria(criteria)).willReturn(totalElements);

            // when
            SyncHistoryPageResult result = sut.execute(params);

            // then
            assertThat(result.results()).hasSize(1);
            assertThat(result.pageMeta().totalElements()).isEqualTo(11L);
        }
    }
}
