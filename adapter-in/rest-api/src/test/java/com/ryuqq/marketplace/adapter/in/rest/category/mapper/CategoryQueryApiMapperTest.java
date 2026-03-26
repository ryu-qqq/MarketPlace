package com.ryuqq.marketplace.adapter.in.rest.category.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.category.CategoryApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.query.SearchCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.category.dto.response.CategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.category.dto.query.CategorySearchParams;
import com.ryuqq.marketplace.application.category.dto.response.CategoryPageResult;
import com.ryuqq.marketplace.application.category.dto.response.CategoryResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryQueryApiMapper 단위 테스트")
class CategoryQueryApiMapperTest {

    private CategoryQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchCategoriesApiRequest를 CategorySearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchCategoriesApiRequest request =
                    CategoryApiFixtures.searchRequest(
                            List.of("ACTIVE"), List.of("FASHION"), "nameKo", "테스트", 0, 20);

            // when
            CategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("ACTIVE");
            assertThat(result.departments()).containsExactly("FASHION");
            assertThat(result.searchField()).isEqualTo("nameKo");
            assertThat(result.searchWord()).isEqualTo("테스트");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchCategoriesApiRequest request =
                    new SearchCategoriesApiRequest(
                            null, null, null, null, null, null, null, null, null, null, null, null);

            // when
            CategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            SearchCategoriesApiRequest request = CategoryApiFixtures.searchRequest();

            // when
            CategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.parentId()).isNull();
            assertThat(result.depth()).isNull();
            assertThat(result.leaf()).isNull();
            assertThat(result.statuses()).isNull();
            assertThat(result.departments()).isNull();
            assertThat(result.categoryGroups()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }

        @Test
        @DisplayName("categoryGroups 필터를 포함하여 변환한다")
        void toSearchParams_WithCategoryGroups_ReturnsCategoryGroupFilter() {
            // given
            SearchCategoriesApiRequest request =
                    CategoryApiFixtures.searchRequestWithCategoryGroups(
                            List.of("CLOTHING", "SHOES"), 0, 20);

            // when
            CategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.categoryGroups()).containsExactly("CLOTHING", "SHOES");
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("CategoryResult를 CategoryApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            CategoryResult result = CategoryApiFixtures.categoryResult(1L);

            // when
            CategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("CAT001");
            assertThat(response.nameKo()).isEqualTo("테스트카테고리");
            assertThat(response.nameEn()).isEqualTo("TestCategory");
            assertThat(response.parentId()).isNull();
            assertThat(response.depth()).isEqualTo(1);
            assertThat(response.path()).isEqualTo("/1");
            assertThat(response.sortOrder()).isEqualTo(1);
            assertThat(response.leaf()).isFalse();
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.department()).isEqualTo("FASHION");
            assertThat(response.categoryGroup()).isEqualTo("CLOTHING");
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            CategoryResult result = CategoryApiFixtures.categoryResult(1L);

            // when
            CategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("CategoryResult 목록을 CategoryApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<CategoryResult> results = CategoryApiFixtures.categoryResults(3);

            // when
            List<CategoryApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).nameKo()).isEqualTo("카테고리_1");
            assertThat(responses.get(1).nameKo()).isEqualTo("카테고리_2");
            assertThat(responses.get(2).nameKo()).isEqualTo("카테고리_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<CategoryResult> results = List.of();

            // when
            List<CategoryApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("CategoryPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            CategoryPageResult pageResult = CategoryApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<CategoryApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            CategoryPageResult pageResult = CategoryApiFixtures.emptyPageResult();

            // when
            PageApiResponse<CategoryApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
