package com.ryuqq.marketplace.application.cancel.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.cancel.CancelQueryFixtures;
import com.ryuqq.marketplace.application.cancel.dto.query.CancelSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
import com.ryuqq.marketplace.domain.cancel.query.CancelSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
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
@DisplayName("CancelQueryFactory 단위 테스트")
class CancelQueryFactoryTest {

    @InjectMocks private CancelQueryFactory sut;

    @Mock private CommonVoFactory commonVoFactory;

    @Nested
    @DisplayName("createCriteria() - 검색 조건 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("검색 파라미터를 CancelSearchCriteria로 변환한다")
        void createCriteria_ValidParams_ReturnsCancelSearchCriteria() {
            // given
            CancelSearchParams params = CancelQueryFixtures.searchParams();
            PageRequest pageRequest = PageRequest.of(0, 20);

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);

            // when
            CancelSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria).isNotNull();
            assertThat(criteria.queryContext().sortKey()).isEqualTo(CancelSortKey.CREATED_AT);
            assertThat(criteria.queryContext().sortDirection()).isEqualTo(SortDirection.DESC);
        }

        @Test
        @DisplayName("상태 필터가 있으면 조건에 포함된다")
        void createCriteria_WithStatusFilter_IncludesStatuses() {
            // given
            CancelSearchParams params = CancelQueryFixtures.searchParamsByStatus("REQUESTED");
            PageRequest pageRequest = PageRequest.of(0, 20);

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);

            // when
            CancelSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria).isNotNull();
            assertThat(criteria.statuses()).isNotEmpty();
        }

        @Test
        @DisplayName("날짜 범위가 있으면 DateRange를 생성한다")
        void createCriteria_WithDateRange_CreatesDateRange() {
            // given
            CancelSearchParams params =
                    CancelQueryFixtures.searchParamsByDateRange("2024-01-01", "2024-12-31");
            PageRequest pageRequest = PageRequest.of(0, 20);
            com.ryuqq.marketplace.domain.common.vo.DateRange dateRange =
                    com.ryuqq.marketplace.domain.common.vo.DateRange.of(
                            java.time.LocalDate.parse("2024-01-01"),
                            java.time.LocalDate.parse("2024-12-31"));

            given(commonVoFactory.createPageRequest(params.page(), params.size()))
                    .willReturn(pageRequest);
            given(
                            commonVoFactory.createDateRange(
                                    java.time.LocalDate.parse("2024-01-01"),
                                    java.time.LocalDate.parse("2024-12-31")))
                    .willReturn(dateRange);

            // when
            CancelSearchCriteria criteria = sut.createCriteria(params);

            // then
            assertThat(criteria).isNotNull();
            assertThat(criteria.dateRange()).isNotNull();
        }
    }
}
