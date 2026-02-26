package com.ryuqq.marketplace.adapter.in.rest.notice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.NoticeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.query.SearchNoticeCategoriesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeCategoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.notice.dto.response.NoticeFieldApiResponse;
import com.ryuqq.marketplace.application.notice.dto.query.NoticeCategorySearchParams;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryPageResult;
import com.ryuqq.marketplace.application.notice.dto.response.NoticeCategoryResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NoticeCategoryQueryApiMapper 단위 테스트")
class NoticeCategoryQueryApiMapperTest {

    private NoticeCategoryQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new NoticeCategoryQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchNoticeCategoriesApiRequest를 NoticeCategorySearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchNoticeCategoriesApiRequest request =
                    NoticeApiFixtures.searchRequest(true, "CODE", "CLOTHING", 0, 20);

            // when
            NoticeCategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isTrue();
            assertThat(result.searchField()).isEqualTo("CODE");
            assertThat(result.searchWord()).isEqualTo("CLOTHING");
            assertThat(result.commonSearchParams().page()).isZero();
            assertThat(result.commonSearchParams().size()).isEqualTo(20);
            assertThat(result.commonSearchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.commonSearchParams().sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchNoticeCategoriesApiRequest request =
                    new SearchNoticeCategoriesApiRequest(null, null, null, null, null, null, null);

            // when
            NoticeCategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.commonSearchParams().page()).isZero();
            assertThat(result.commonSearchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            SearchNoticeCategoriesApiRequest request = NoticeApiFixtures.searchRequest();

            // when
            NoticeCategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }

        @Test
        @DisplayName("sortKey와 sortDirection이 null이면 기본값으로 변환한다")
        void toSearchParams_NullSort_UsesDefaults() {
            // given
            SearchNoticeCategoriesApiRequest request =
                    new SearchNoticeCategoriesApiRequest(null, null, null, null, null, 0, 20);

            // when
            NoticeCategorySearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.commonSearchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.commonSearchParams().sortDirection()).isEqualTo("DESC");
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("NoticeCategoryResult를 NoticeCategoryApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResult(1L);

            // when
            NoticeCategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("CLOTHING");
            assertThat(response.nameKo()).isEqualTo("의류");
            assertThat(response.nameEn()).isEqualTo("Clothing");
            assertThat(response.targetCategoryGroup()).isEqualTo("CLOTHING");
            assertThat(response.active()).isTrue();
            assertThat(response.fields()).isEmpty();
            assertThat(response.createdAt()).isNotNull();
        }

        @Test
        @DisplayName("필드 목록이 포함된 결과를 변환한다")
        void toResponse_WithFields_ConvertsFields() {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResultWithFields(1L);

            // when
            NoticeCategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.fields()).hasSize(2);
            assertThat(response.fields().get(0).fieldCode()).isEqualTo("MATERIAL");
            assertThat(response.fields().get(0).fieldName()).isEqualTo("소재");
            assertThat(response.fields().get(0).required()).isTrue();
            assertThat(response.fields().get(1).fieldCode()).isEqualTo("ORIGIN");
            assertThat(response.fields().get(1).fieldName()).isEqualTo("원산지");
            assertThat(response.fields().get(1).required()).isTrue();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResult(1L);

            // when
            NoticeCategoryApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).contains("T");
            assertThat(response.createdAt()).contains("+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("NoticeCategoryResult 목록을 NoticeCategoryApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<NoticeCategoryResult> results = NoticeApiFixtures.noticeCategoryResults(3);

            // when
            List<NoticeCategoryApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).code()).isEqualTo("CODE_1");
            assertThat(responses.get(1).code()).isEqualTo("CODE_2");
            assertThat(responses.get(2).code()).isEqualTo("CODE_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<NoticeCategoryResult> results = List.of();

            // when
            List<NoticeCategoryApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("NoticeCategoryPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            NoticeCategoryPageResult pageResult = NoticeApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<NoticeCategoryApiResponse> response = mapper.toPageResponse(pageResult);

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
            NoticeCategoryPageResult pageResult = NoticeApiFixtures.emptyPageResult();

            // when
            PageApiResponse<NoticeCategoryApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("페이지 메타 정보가 정확히 변환된다")
        void toPageResponse_ConvertsPageMeta_Correctly() {
            // given
            NoticeCategoryPageResult pageResult = NoticeApiFixtures.pageResult(5, 1, 10);

            // when
            PageApiResponse<NoticeCategoryApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.page()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("toFieldResponse() - 필드 응답 변환")
    class ToFieldResponseTest {

        @Test
        @DisplayName("필수 필드가 올바르게 변환된다")
        void toFieldResponse_RequiredField_ConvertsCorrectly() {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResultWithFields(1L);

            // when
            NoticeCategoryApiResponse response = mapper.toResponse(result);

            // then
            NoticeFieldApiResponse fieldResponse = response.fields().get(0);
            assertThat(fieldResponse.id()).isEqualTo(1L);
            assertThat(fieldResponse.fieldCode()).isEqualTo("MATERIAL");
            assertThat(fieldResponse.fieldName()).isEqualTo("소재");
            assertThat(fieldResponse.required()).isTrue();
            assertThat(fieldResponse.sortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("선택 필드가 올바르게 변환된다")
        void toFieldResponse_OptionalField_ConvertsCorrectly() {
            // given
            NoticeCategoryResult result = NoticeApiFixtures.noticeCategoryResultWithFields(1L);

            // when
            NoticeCategoryApiResponse response = mapper.toResponse(result);

            // then
            NoticeFieldApiResponse fieldResponse = response.fields().get(1);
            assertThat(fieldResponse.id()).isEqualTo(2L);
            assertThat(fieldResponse.fieldCode()).isEqualTo("ORIGIN");
            assertThat(fieldResponse.fieldName()).isEqualTo("원산지");
            assertThat(fieldResponse.required()).isTrue();
            assertThat(fieldResponse.sortOrder()).isEqualTo(2);
        }
    }
}
