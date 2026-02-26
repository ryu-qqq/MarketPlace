package com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.InboundBrandMappingApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.query.SearchInboundBrandMappingsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundbrandmapping.dto.response.InboundBrandMappingApiResponse;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.query.InboundBrandMappingSearchParams;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingPageResult;
import com.ryuqq.marketplace.application.inboundbrandmapping.dto.response.InboundBrandMappingResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundBrandMappingQueryApiMapper 단위 테스트")
class InboundBrandMappingQueryApiMapperTest {

    private InboundBrandMappingQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundBrandMappingQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 파라미터 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("검색 요청을 InboundBrandMappingSearchParams로 변환한다")
        void toSearchParams_ConvertsRequestToSearchParams() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            SearchInboundBrandMappingsApiRequest request =
                    new SearchInboundBrandMappingsApiRequest(
                            "EXTERNAL_NAME", "나이키", "CREATED_AT", "DESC", 0, 20);

            // when
            InboundBrandMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.inboundSourceId()).isEqualTo(inboundSourceId);
            assertThat(params.searchField()).isEqualTo("EXTERNAL_NAME");
            assertThat(params.searchWord()).isEqualTo("나이키");
            assertThat(params.page()).isEqualTo(0);
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("페이지 파라미터가 null이면 기본값을 사용한다")
        void toSearchParams_NullPageParams_UsesDefaults() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            SearchInboundBrandMappingsApiRequest request =
                    new SearchInboundBrandMappingsApiRequest(null, null, null, null, null, null);

            // when
            InboundBrandMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.page()).isEqualTo(0);
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("inboundSourceId가 검색 파라미터에 올바르게 주입된다")
        void toSearchParams_InjectsInboundSourceId_Correctly() {
            // given
            Long inboundSourceId = 5L;
            SearchInboundBrandMappingsApiRequest request =
                    InboundBrandMappingApiFixtures.searchRequest();

            // when
            InboundBrandMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.inboundSourceId()).isEqualTo(5L);
        }

        @Test
        @DisplayName("statuses는 null로 설정된다")
        void toSearchParams_StatusesAreNull() {
            // given
            Long inboundSourceId = InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID;
            SearchInboundBrandMappingsApiRequest request =
                    InboundBrandMappingApiFixtures.searchRequest();

            // when
            InboundBrandMappingSearchParams params =
                    mapper.toSearchParams(inboundSourceId, request);

            // then
            assertThat(params.statuses()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단건 응답 변환")
    class ToResponseTest {

        @Test
        @DisplayName("InboundBrandMappingResult를 InboundBrandMappingApiResponse로 변환한다")
        void toResponse_ConvertsResultToResponse() {
            // given
            InboundBrandMappingResult result = InboundBrandMappingApiFixtures.mappingResult(1L);

            // when
            InboundBrandMappingApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.inboundSourceId())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_SOURCE_ID);
            assertThat(response.externalBrandCode())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_BRAND_CODE);
            assertThat(response.externalBrandName())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_EXTERNAL_BRAND_NAME);
            assertThat(response.internalBrandId())
                    .isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_INTERNAL_BRAND_ID);
            assertThat(response.status()).isEqualTo(InboundBrandMappingApiFixtures.DEFAULT_STATUS);
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 포맷으로 변환된다")
        void toResponse_DateFieldsFormattedAsIso8601() {
            // given
            InboundBrandMappingResult result = InboundBrandMappingApiFixtures.mappingResult(1L);

            // when
            InboundBrandMappingApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 응답 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("InboundBrandMappingResult 목록을 InboundBrandMappingApiResponse 목록으로 변환한다")
        void toResponses_ConvertsResultsToResponses() {
            // given
            List<InboundBrandMappingResult> results =
                    InboundBrandMappingApiFixtures.mappingResults(3);

            // when
            List<InboundBrandMappingApiResponse> responses = mapper.toResponses(results);

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
            List<InboundBrandMappingApiResponse> responses = mapper.toResponses(List.of());

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 응답 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("InboundBrandMappingPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResultToPageResponse() {
            // given
            InboundBrandMappingPageResult pageResult =
                    InboundBrandMappingApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<InboundBrandMappingApiResponse> response =
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
            InboundBrandMappingPageResult emptyResult =
                    InboundBrandMappingApiFixtures.emptyPageResult();

            // when
            PageApiResponse<InboundBrandMappingApiResponse> response =
                    mapper.toPageResponse(emptyResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("첫 페이지이면 first가 true이다")
        void toPageResponse_FirstPage_FirstIsTrue() {
            // given
            InboundBrandMappingPageResult pageResult =
                    InboundBrandMappingApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<InboundBrandMappingApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.first()).isTrue();
        }
    }
}
