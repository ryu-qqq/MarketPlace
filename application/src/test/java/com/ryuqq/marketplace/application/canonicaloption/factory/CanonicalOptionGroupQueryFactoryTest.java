package com.ryuqq.marketplace.application.canonicaloption.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.canonicaloption.CanonicalOptionQueryFixtures;
import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CanonicalOptionGroupQueryFactory 단위 테스트")
class CanonicalOptionGroupQueryFactoryTest {

    private final CanonicalOptionGroupQueryFactory sut =
            new CanonicalOptionGroupQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createCriteria() - SearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_FromParams_ReturnsCriteria() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams();

            // when
            CanonicalOptionGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("활성화 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithActiveFilter_ReturnsCriteriaWithActiveFilter() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams(true);

            // when
            CanonicalOptionGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.active()).isTrue();
            assertThat(result.hasActiveFilter()).isTrue();
        }

        @Test
        @DisplayName("검색 필드/검색어가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithSearchFieldAndWord_ReturnsCriteriaWithSearchWord() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams("code", "COLOR");

            // when
            CanonicalOptionGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo("code");
            assertThat(result.searchWord()).isEqualTo("COLOR");
            assertThat(result.hasSearchFilter()).isTrue();
        }

        @Test
        @DisplayName("페이징 정보가 QueryContext에 반영된다")
        void createCriteria_WithPaging_ReturnsCorrectQueryContext() {
            // given
            int page = 2;
            int size = 10;
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams(page, size);

            // when
            CanonicalOptionGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }

        @Test
        @DisplayName("모든 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithAllFilters_ReturnsFullCriteria() {
            // given
            CanonicalOptionGroupSearchParams params =
                    CanonicalOptionQueryFixtures.searchParams(true, "nameKo", "색상");

            // when
            CanonicalOptionGroupSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.active()).isTrue();
            assertThat(result.searchField()).isEqualTo("nameKo");
            assertThat(result.searchWord()).isEqualTo("색상");
            assertThat(result.hasActiveFilter()).isTrue();
            assertThat(result.hasSearchFilter()).isTrue();
        }
    }
}
