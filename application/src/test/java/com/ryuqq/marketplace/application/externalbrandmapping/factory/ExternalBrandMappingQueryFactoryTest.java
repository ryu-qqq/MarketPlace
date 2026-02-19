package com.ryuqq.marketplace.application.externalbrandmapping.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.externalbrandmapping.ExternalBrandMappingQueryFixtures;
import com.ryuqq.marketplace.application.externalbrandmapping.dto.query.ExternalBrandMappingSearchParams;
import com.ryuqq.marketplace.domain.externalbrandmapping.query.ExternalBrandMappingSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalBrandMappingQueryFactory 단위 테스트")
class ExternalBrandMappingQueryFactoryTest {

    private final ExternalBrandMappingQueryFactory sut =
            new ExternalBrandMappingQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createSearchCriteria() - SearchCriteria 생성")
    class CreateSearchCriteriaTest {

        @Test
        @DisplayName("기본 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_DefaultParams_ReturnsCriteria() {
            // given
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams();

            // when
            ExternalBrandMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.externalSourceId()).isNull();
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("외부 소스 ID 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithExternalSourceId_ReturnsCriteriaWithFilter() {
            // given
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams(1L);

            // when
            ExternalBrandMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.externalSourceId()).isEqualTo(1L);
            assertThat(result.hasExternalSourceIdFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithStatusFilter_ReturnsCriteriaWithStatuses() {
            // given
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParamsWithStatusFilter("ACTIVE");

            // when
            ExternalBrandMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasStatusesFilter()).isTrue();
            assertThat(result.statusNames()).contains("ACTIVE");
        }

        @Test
        @DisplayName("여러 상태 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithMultipleStatuses_ReturnsCriteriaWithAllStatuses() {
            // given
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams(
                            1L, List.of("ACTIVE", "INACTIVE"), null, null);

            // when
            ExternalBrandMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(2);
        }

        @Test
        @DisplayName("검색어가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithSearchWord_ReturnsCriteriaWithSearchWord() {
            // given
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams(
                            1L, List.of(), "EXTERNAL_NAME", "나이키");

            // when
            ExternalBrandMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("나이키");
            assertThat(result.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("페이징 정보가 QueryContext에 반영된다")
        void createSearchCriteria_WithPaging_ReturnsCorrectQueryContext() {
            // given
            int page = 2;
            int size = 10;
            ExternalBrandMappingSearchParams params =
                    ExternalBrandMappingQueryFixtures.searchParams(page, size);

            // when
            ExternalBrandMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }
    }
}
