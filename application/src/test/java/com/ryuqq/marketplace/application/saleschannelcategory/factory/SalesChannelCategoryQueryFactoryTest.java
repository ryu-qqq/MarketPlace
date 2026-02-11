package com.ryuqq.marketplace.application.saleschannelcategory.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.saleschannelcategory.SalesChannelCategoryQueryFixtures;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategoryQueryFactory 단위 테스트")
class SalesChannelCategoryQueryFactoryTest {

    private final SalesChannelCategoryQueryFactory sut =
            new SalesChannelCategoryQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createCriteria() - SearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_FromParams_ReturnsCriteria() {
            // given
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams();

            // when
            SalesChannelCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("판매채널 ID 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithSalesChannelIdFilter_ReturnsCriteriaWithFilter() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(salesChannelIds);

            // when
            SalesChannelCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.salesChannelIds()).hasSize(2);
            assertThat(result.salesChannelIds()).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("상태 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithStatusFilter_ReturnsCriteriaWithStatusFilter() {
            // given
            List<String> statuses = List.of("ACTIVE");
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(null, statuses, null, null);

            // when
            SalesChannelCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.statuses()).hasSize(1);
        }

        @Test
        @DisplayName("검색 필드와 검색어가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithSearchFieldAndWord_ReturnsCriteriaWithSearch() {
            // given
            String searchField = "EXTERNAL_NAME";
            String searchWord = "테스트";
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(null, null, searchField, searchWord);

            // when
            SalesChannelCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isNotNull();
            assertThat(result.searchWord()).isEqualTo(searchWord);
        }

        @Test
        @DisplayName("페이징 정보가 QueryContext에 반영된다")
        void createCriteria_WithPaging_ReturnsCorrectQueryContext() {
            // given
            int page = 2;
            int size = 10;
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams(page, size);

            // when
            SalesChannelCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }

        @Test
        @DisplayName("정렬 키가 null이면 기본 정렬 키를 사용한다")
        void createCriteria_NullSortKey_UsesDefaultKey() {
            // given
            SalesChannelCategorySearchParams params =
                    SalesChannelCategoryQueryFixtures.searchParams();

            // when
            SalesChannelCategorySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext().sortKey()).isNotNull();
        }
    }
}
