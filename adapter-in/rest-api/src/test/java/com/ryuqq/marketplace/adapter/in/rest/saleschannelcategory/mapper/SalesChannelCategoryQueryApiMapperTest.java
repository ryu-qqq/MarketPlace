package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.query.SearchSalesChannelCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.response.SalesChannelCategoryApiResponse;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.query.SalesChannelCategorySearchParams;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryPageResult;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.SalesChannelCategoryResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategoryQueryApiMapper 단위 테스트")
class SalesChannelCategoryQueryApiMapperTest {

    private SalesChannelCategoryQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelCategoryQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchSalesChannelCategoriesApiRequest를 SalesChannelCategorySearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SearchSalesChannelCategoriesApiRequest request =
                    SalesChannelCategoryApiFixtures.searchRequest(
                            List.of("ACTIVE"), "externalCategoryCode", "CAT001", 0, 20);

            // when
            SalesChannelCategorySearchParams result =
                    mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(result.salesChannelIds()).containsExactly(1L);
            assertThat(result.statuses()).containsExactly("ACTIVE");
            assertThat(result.searchField()).isEqualTo("externalCategoryCode");
            assertThat(result.searchWord()).isEqualTo("CAT001");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("sortKey/sortDirection이 null이면 기본값으로 변환한다")
        void toSearchParams_NullSort_UsesDefaults() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SearchSalesChannelCategoriesApiRequest request =
                    new SearchSalesChannelCategoriesApiRequest(
                            null, null, null, null, null, null, null, null, null, null);

            // when
            SalesChannelCategorySearchParams result =
                    mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(result.sortKey()).isEqualTo("createdAt");
            assertThat(result.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SearchSalesChannelCategoriesApiRequest request =
                    SalesChannelCategoryApiFixtures.searchRequest();

            // when
            SalesChannelCategorySearchParams result =
                    mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SearchSalesChannelCategoriesApiRequest request =
                    SalesChannelCategoryApiFixtures.searchRequest();

            // when
            SalesChannelCategorySearchParams result =
                    mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(result.statuses()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }

        @Test
        @DisplayName("복수 salesChannelIds가 올바르게 설정된다")
        void toSearchParams_MultipleSalesChannelIds_ReturnsParamsWithIds() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L, 3L);
            SearchSalesChannelCategoriesApiRequest request =
                    SalesChannelCategoryApiFixtures.searchRequest();

            // when
            SalesChannelCategorySearchParams result =
                    mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(result.salesChannelIds()).containsExactly(1L, 2L, 3L);
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("SalesChannelCategoryResult를 SalesChannelCategoryApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SalesChannelCategoryResult result = SalesChannelCategoryApiFixtures.categoryResult(1L);

            // when
            SalesChannelCategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.salesChannelId()).isEqualTo(1L);
            assertThat(response.externalCategoryCode()).isEqualTo("CAT001");
            assertThat(response.externalCategoryName()).isEqualTo("의류");
            assertThat(response.parentId()).isEqualTo(0L);
            assertThat(response.depth()).isZero();
            assertThat(response.path()).isEqualTo("1");
            assertThat(response.sortOrder()).isEqualTo(1);
            assertThat(response.leaf()).isFalse();
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            SalesChannelCategoryResult result = SalesChannelCategoryApiFixtures.categoryResult(1L);

            // when
            SalesChannelCategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).contains("T");
            assertThat(response.createdAt()).contains("+09:00");
            assertThat(response.updatedAt()).contains("T");
            assertThat(response.updatedAt()).contains("+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("SalesChannelCategoryResult 목록을 SalesChannelCategoryApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<SalesChannelCategoryResult> results =
                    SalesChannelCategoryApiFixtures.categoryResults(3);

            // when
            List<SalesChannelCategoryApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).externalCategoryCode()).isEqualTo("CAT001");
            assertThat(responses.get(1).externalCategoryCode()).isEqualTo("CAT002");
            assertThat(responses.get(2).externalCategoryCode()).isEqualTo("CAT003");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<SalesChannelCategoryResult> results = List.of();

            // when
            List<SalesChannelCategoryApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("SalesChannelCategoryPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SalesChannelCategoryPageResult pageResult =
                    SalesChannelCategoryApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<SalesChannelCategoryApiResponse> response =
                    mapper.toPageResponse(pageResult);

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
            SalesChannelCategoryPageResult pageResult =
                    SalesChannelCategoryApiFixtures.emptyPageResult();

            // when
            PageApiResponse<SalesChannelCategoryApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
