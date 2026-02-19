package com.ryuqq.marketplace.application.externalsource.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.externalsource.ExternalSourceQueryFixtures;
import com.ryuqq.marketplace.application.externalsource.dto.query.ExternalSourceSearchParams;
import com.ryuqq.marketplace.domain.externalsource.query.ExternalSourceSearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ExternalSourceQueryFactory 단위 테스트")
class ExternalSourceQueryFactoryTest {

    private final ExternalSourceQueryFactory sut =
            new ExternalSourceQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createSearchCriteria() - SearchCriteria 생성")
    class CreateSearchCriteriaTest {

        @Test
        @DisplayName("기본 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_DefaultParams_ReturnsCriteria() {
            // given
            ExternalSourceSearchParams params = ExternalSourceQueryFixtures.searchParams();

            // when
            ExternalSourceSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.types()).isEmpty();
            assertThat(result.statuses()).isEmpty();
        }

        @Test
        @DisplayName("유형 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithTypeFilter_ReturnsCriteriaWithTypes() {
            // given
            ExternalSourceSearchParams params =
                    ExternalSourceQueryFixtures.searchParamsWithTypeFilter("LEGACY");

            // when
            ExternalSourceSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.types()).hasSize(1);
            assertThat(result.types().get(0).name()).isEqualTo("LEGACY");
        }

        @Test
        @DisplayName("여러 유형 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithMultipleTypes_ReturnsCriteriaWithAllTypes() {
            // given
            ExternalSourceSearchParams params =
                    ExternalSourceQueryFixtures.searchParams(
                            List.of("LEGACY", "CRAWLING"), List.of(), null, null);

            // when
            ExternalSourceSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.types()).hasSize(2);
        }

        @Test
        @DisplayName("상태 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithStatusFilter_ReturnsCriteriaWithStatuses() {
            // given
            ExternalSourceSearchParams params =
                    ExternalSourceQueryFixtures.searchParamsWithStatusFilter("ACTIVE");

            // when
            ExternalSourceSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(1);
            assertThat(result.statuses().get(0).name()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("검색어가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createSearchCriteria_WithSearchWord_ReturnsCriteriaWithSearchWord() {
            // given
            ExternalSourceSearchParams params =
                    ExternalSourceQueryFixtures.searchParams(List.of(), List.of(), "NAME", "세토프");

            // when
            ExternalSourceSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchWord()).isEqualTo("세토프");
        }

        @Test
        @DisplayName("페이징 정보가 QueryContext에 반영된다")
        void createSearchCriteria_WithPaging_ReturnsCorrectQueryContext() {
            // given
            int page = 1;
            int size = 10;
            ExternalSourceSearchParams params =
                    ExternalSourceQueryFixtures.searchParams(page, size);

            // when
            ExternalSourceSearchCriteria result = sut.createSearchCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }
    }
}
