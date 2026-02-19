package com.ryuqq.marketplace.application.externalcategorymapping.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.externalcategorymapping.ExternalCategoryMappingQueryFixtures;
import com.ryuqq.marketplace.application.externalcategorymapping.dto.query.ExternalCategoryMappingSearchParams;
import com.ryuqq.marketplace.domain.externalcategorymapping.query.ExternalCategoryMappingSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalCategoryMappingQueryFactory 단위 테스트")
class ExternalCategoryMappingQueryFactoryTest {

    private final ExternalCategoryMappingQueryFactory sut =
            new ExternalCategoryMappingQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createSearchCriteria() - SearchCriteria 생성")
    class CreateSearchCriteriaTest {

        @Test
        @DisplayName("기본 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_DefaultParams_ReturnsCriteria() {
            // given
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParams();

            // when
            ExternalCategoryMappingSearchCriteria result = sut.createSearchCriteria(params);

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
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParams(1L);

            // when
            ExternalCategoryMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.externalSourceId()).isEqualTo(1L);
            assertThat(result.hasExternalSourceIdFilter()).isTrue();
        }

        @Test
        @DisplayName("상태 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithStatusFilter_ReturnsCriteriaWithStatuses() {
            // given
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParamsWithStatusFilter("ACTIVE");

            // when
            ExternalCategoryMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.hasStatusesFilter()).isTrue();
            assertThat(result.statusNames()).contains("ACTIVE");
        }

        @Test
        @DisplayName("검색어가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithSearchWord_ReturnsCriteriaWithSearchWord() {
            // given
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParams(
                            1L, List.of(), "EXTERNAL_NAME", "신발");

            // when
            ExternalCategoryMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("신발");
            assertThat(result.hasSearchCondition()).isTrue();
        }

        @Test
        @DisplayName("페이징 정보가 QueryContext에 반영된다")
        void createSearchCriteria_WithPaging_ReturnsCorrectQueryContext() {
            // given
            int page = 1;
            int size = 10;
            ExternalCategoryMappingSearchParams params =
                    ExternalCategoryMappingQueryFixtures.searchParams(page, size);

            // when
            ExternalCategoryMappingSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }
    }
}
