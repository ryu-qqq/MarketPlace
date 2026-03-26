package com.ryuqq.marketplace.application.outboundproduct.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproduct.OmsProductQueryFixtures;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryListResult;
import com.ryuqq.marketplace.application.outboundproduct.port.out.query.OmsSyncHistoryCompositionQueryPort;
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
@DisplayName("OmsSyncHistoryCompositionReadManager 단위 테스트")
class OmsSyncHistoryCompositionReadManagerTest {

    @InjectMocks private OmsSyncHistoryCompositionReadManager sut;

    @Mock private OmsSyncHistoryCompositionQueryPort compositionQueryPort;

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 연동 이력 목록 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 연동 이력 목록을 반환한다")
        void findByCriteria_ReturnsSyncHistories() {
            // given
            SyncHistorySearchCriteria criteria = createDefaultCriteria(100L);
            List<SyncHistoryListResult> expected =
                    List.of(
                            OmsProductQueryFixtures.syncHistoryListResult(1L),
                            OmsProductQueryFixtures.syncHistoryListResult(2L));

            given(compositionQueryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SyncHistoryListResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("연동 이력이 없으면 빈 목록을 반환한다")
        void findByCriteria_NoHistory_ReturnsEmptyList() {
            // given
            SyncHistorySearchCriteria criteria = createDefaultCriteria(999L);

            given(compositionQueryPort.findByCriteria(criteria))
                    .willReturn(Collections.emptyList());

            // when
            List<SyncHistoryListResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
            then(compositionQueryPort).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("상태 필터가 포함된 조건으로 조회할 수 있다")
        void findByCriteria_WithStatusFilter_ReturnsFilteredHistory() {
            // given
            SyncHistorySearchCriteria criteria =
                    new SyncHistorySearchCriteria(
                            100L,
                            null,
                            com.ryuqq.marketplace.domain.outboundsync.vo.SyncStatus.FAILED,
                            QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));
            List<SyncHistoryListResult> expected =
                    List.of(OmsProductQueryFixtures.syncHistoryListResult(1L, "FAILED"));

            given(compositionQueryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<SyncHistoryListResult> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).status()).isEqualTo("FAILED");
        }

        @Test
        @DisplayName("Port에 조회를 위임하고 결과를 그대로 반환한다")
        void findByCriteria_DelegatesToPort() {
            // given
            SyncHistorySearchCriteria criteria = createDefaultCriteria(100L);
            List<SyncHistoryListResult> expected =
                    List.of(OmsProductQueryFixtures.syncHistoryListResult(1L));

            given(compositionQueryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            sut.findByCriteria(criteria);

            // then
            then(compositionQueryPort).should().findByCriteria(criteria);
            then(compositionQueryPort).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 연동 이력 개수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 연동 이력 개수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            SyncHistorySearchCriteria criteria = createDefaultCriteria(100L);
            long expected = 15L;

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("연동 이력이 없으면 0을 반환한다")
        void countByCriteria_NoHistory_ReturnsZero() {
            // given
            SyncHistorySearchCriteria criteria = createDefaultCriteria(999L);

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
            then(compositionQueryPort).should().countByCriteria(criteria);
        }

        @Test
        @DisplayName("Port에 카운트 조회를 위임하고 결과를 그대로 반환한다")
        void countByCriteria_DelegatesToPort() {
            // given
            SyncHistorySearchCriteria criteria = createDefaultCriteria(100L);
            long expected = 5L;

            given(compositionQueryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            sut.countByCriteria(criteria);

            // then
            then(compositionQueryPort).should().countByCriteria(criteria);
            then(compositionQueryPort).shouldHaveNoMoreInteractions();
        }
    }

    private SyncHistorySearchCriteria createDefaultCriteria(long productGroupId) {
        return new SyncHistorySearchCriteria(
                productGroupId,
                null,
                null,
                QueryContext.defaultOf(SyncHistorySortKey.defaultKey()));
    }
}
