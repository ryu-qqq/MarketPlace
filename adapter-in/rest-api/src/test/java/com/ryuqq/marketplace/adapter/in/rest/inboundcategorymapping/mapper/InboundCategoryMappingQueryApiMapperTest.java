package com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.InboundCategoryMappingApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.query.SearchInboundCategoryMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundcategorymapping.dto.response.InboundCategoryMappingApiResponse;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.query.InboundCategoryMappingSearchParams;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingPageResult;
import com.ryuqq.marketplace.application.inboundcategorymapping.dto.response.InboundCategoryMappingResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundCategoryMappingQueryApiMapper 단위 테스트")
class InboundCategoryMappingQueryApiMapperTest {

    private InboundCategoryMappingQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundCategoryMappingQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 파라미터 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("검색 요청을 InboundCategoryMappingSearchParams로 변환한다")
        void toSearchParams_ConvertsRequestToSearchParams() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            SearchInboundCategoryMappingsApiRequest request =
                    new SearchInboundCategoryMappingsApiRequest(
                            "EXTERNAL_NAME", "남성의류", "CREATED_AT", "DESC", 0, 20);

            // when
            InboundCategoryMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.inboundSourceId()).isEqualTo(inboundSourceId);
            assertThat(params.searchField()).isEqualTo("EXTERNAL_NAME");
            assertThat(params.searchWord()).isEqualTo("남성의류");
            assertThat(params.page()).isEqualTo(0);
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("페이지 파라미터가 null이면 기본값을 사용한다")
        void toSearchParams_NullPageParams_UsesDefaults() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            SearchInboundCategoryMappingsApiRequest request =
                    new SearchInboundCategoryMappingsApiRequest(null, null, null, null, null, null);

            // when
            InboundCategoryMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.page()).isEqualTo(0);
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("inboundSourceId가 검색 파라미터에 올바르게 주입된다")
        void toSearchParams_InjectsInboundSourceId_Correctly() {
            // given
            Long inboundSourceId = 7L;
            SearchInboundCategoryMappingsApiRequest request =
                    InboundCategoryMappingApiFixtures.searchRequest();

            // when
            InboundCategoryMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.inboundSourceId()).isEqualTo(7L);
        }

        @Test
        @DisplayName("statuses는 null로 설정된다")
        void toSearchParams_StatusesAreNull() {
            // given
            Long inboundSourceId = InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            SearchInboundCategoryMappingsApiRequest request =
                    InboundCategoryMappingApiFixtures.searchRequest();

            // when
            InboundCategoryMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.statuses()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단건 응답 변환")
    class ToResponseTest {

        @Test
        @DisplayName("InboundCategoryMappingResult를 InboundCategoryMappingApiResponse로 변환한다")
        void toResponse_ConvertsResultToResponse() {
            // given
            InboundCategoryMappingResult result =
                    InboundCategoryMappingApiFixtures.mappingResult(1L);

            // when
            InboundCategoryMappingApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.inboundSourceId())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(response.externalCategoryCode())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_CATEGORY_CODE);
            assertThat(response.externalCategoryName())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_EXTERNAL_CATEGORY_NAME);
            assertThat(response.internalCategoryId())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_INTERNAL_CATEGORY_ID);
            assertThat(response.status())
                    .isEqualTo(InboundCategoryMappingApiFixtures.DEFAULT_STATUS);
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 포맷으로 변환된다")
        void toResponse_DateFieldsFormattedAsIso8601() {
            // given
            InboundCategoryMappingResult result =
                    InboundCategoryMappingApiFixtures.mappingResult(1L);

            // when
            InboundCategoryMappingApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 응답 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("InboundCategoryMappingResult 목록을 InboundCategoryMappingApiResponse 목록으로 변환한다")
        void toResponses_ConvertsResultsToResponses() {
            // given
            List<InboundCategoryMappingResult> results =
                    InboundCategoryMappingApiFixtures.mappingResults(3);

            // when
            List<InboundCategoryMappingApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
            assertThat(responses.get(2).id()).isEqualTo(3L);
        }

        @Test
        @DisplayName("빈 목록을 빈 응답 목록으로 변환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // when
            List<InboundCategoryMappingApiResponse> responses = mapper.toResponses(List.of());

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 응답 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("InboundCategoryMappingPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResultToPageResponse() {
            // given
            InboundCategoryMappingPageResult pageResult =
                    InboundCategoryMappingApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<InboundCategoryMappingApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("빈 결과를 빈 PageApiResponse로 변환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            InboundCategoryMappingPageResult emptyResult =
                    InboundCategoryMappingApiFixtures.emptyPageResult();

            // when
            PageApiResponse<InboundCategoryMappingApiResponse> response =
                    mapper.toPageResponse(emptyResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("첫 페이지이면 first가 true이다")
        void toPageResponse_FirstPage_FirstIsTrue() {
            // given
            InboundCategoryMappingPageResult pageResult =
                    InboundCategoryMappingApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<InboundCategoryMappingApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.first()).isTrue();
        }
    }
}
