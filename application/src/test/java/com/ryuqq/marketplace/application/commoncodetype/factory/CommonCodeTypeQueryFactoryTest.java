package com.ryuqq.marketplace.application.commoncodetype.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.commoncodetype.CommonCodeTypeQueryFixtures;
import com.ryuqq.marketplace.application.commoncodetype.dto.query.CommonCodeTypeSearchParams;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchField;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CommonCodeTypeQueryFactory 단위 테스트")
class CommonCodeTypeQueryFactoryTest {

    private final CommonCodeTypeQueryFactory sut =
            new CommonCodeTypeQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createCriteria() - SearchCriteria 생성")
    class CreateCriteriaTest {

        @Test
        @DisplayName("SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_FromParams_ReturnsCriteria() {
            // given
            CommonCodeTypeSearchParams params = CommonCodeTypeQueryFixtures.searchParams();

            // when
            CommonCodeTypeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("활성화 필터가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithActiveFilter_ReturnsCriteriaWithActiveFilter() {
            // given
            CommonCodeTypeSearchParams params = CommonCodeTypeQueryFixtures.searchParams(true);

            // when
            CommonCodeTypeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.active()).isTrue();
        }

        @Test
        @DisplayName("검색 필드/검색어가 있는 SearchParams로 SearchCriteria를 생성한다")
        void createCriteria_WithSearchFieldAndWord_ReturnsCriteriaWithSearchWord() {
            // given
            CommonCodeTypeSearchParams params =
                    CommonCodeTypeQueryFixtures.searchParams("code", "결제");

            // when
            CommonCodeTypeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.searchField()).isEqualTo(CommonCodeTypeSearchField.CODE);
            assertThat(result.searchWord()).isEqualTo("결제");
        }

        @Test
        @DisplayName("페이징 정보가 QueryContext에 반영된다")
        void createCriteria_WithPaging_ReturnsCorrectQueryContext() {
            // given
            int page = 2;
            int size = 10;
            CommonCodeTypeSearchParams params =
                    CommonCodeTypeQueryFixtures.searchParams(page, size);

            // when
            CommonCodeTypeSearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext()).isNotNull();
            assertThat(result.queryContext().pageRequest().page()).isEqualTo(page);
            assertThat(result.queryContext().pageRequest().size()).isEqualTo(size);
        }
    }
}
