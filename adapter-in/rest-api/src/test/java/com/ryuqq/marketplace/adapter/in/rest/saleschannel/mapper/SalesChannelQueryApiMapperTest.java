package com.ryuqq.marketplace.adapter.in.rest.saleschannel.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.SalesChannelApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.query.SearchSalesChannelsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannel.dto.response.SalesChannelApiResponse;
import com.ryuqq.marketplace.application.saleschannel.dto.query.SalesChannelSearchParams;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelPageResult;
import com.ryuqq.marketplace.application.saleschannel.dto.response.SalesChannelResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelQueryApiMapper 단위 테스트")
class SalesChannelQueryApiMapperTest {

    private SalesChannelQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams 메서드 테스트")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchSalesChannelsApiRequest를 SalesChannelSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchSalesChannelsApiRequest request =
                    SalesChannelApiFixtures.searchRequest(
                            List.of("ACTIVE"), "CHANNEL_NAME", "쿠팡", 0, 20);

            // when
            SalesChannelSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.statuses()).containsExactly("ACTIVE");
            assertThat(params.searchField()).isEqualTo("CHANNEL_NAME");
            assertThat(params.searchWord()).isEqualTo("쿠팡");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
            assertThat(params.sortKey()).isEqualTo("createdAt");
            assertThat(params.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchSalesChannelsApiRequest request =
                    new SearchSalesChannelsApiRequest(null, null, null, null, null, null, null);

            // when
            SalesChannelSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
            assertThat(params.sortKey()).isEqualTo("createdAt");
            assertThat(params.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("sortKey가 null이면 기본값 createdAt을 사용한다")
        void toSearchParams_NullSortKey_UsesDefaultCreatedAt() {
            // given
            SearchSalesChannelsApiRequest request =
                    new SearchSalesChannelsApiRequest(null, null, null, null, null, 0, 20);

            // when
            SalesChannelSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.sortKey()).isEqualTo("createdAt");
        }

        @Test
        @DisplayName("sortDirection이 null이면 기본값 DESC를 사용한다")
        void toSearchParams_NullSortDirection_UsesDefaultDesc() {
            // given
            SearchSalesChannelsApiRequest request =
                    new SearchSalesChannelsApiRequest(null, null, null, null, null, 0, 20);

            // when
            SalesChannelSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            SearchSalesChannelsApiRequest request = SalesChannelApiFixtures.searchRequest();

            // when
            SalesChannelSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.statuses()).isNull();
            assertThat(params.searchField()).isNull();
            assertThat(params.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse 메서드 테스트")
    class ToResponseTest {

        @Test
        @DisplayName("SalesChannelResult를 SalesChannelApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SalesChannelResult result = SalesChannelApiFixtures.channelResult(1L);

            // when
            SalesChannelApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.channelName()).isEqualTo("쿠팡");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            SalesChannelResult result = SalesChannelApiFixtures.channelResult(1L);

            // when
            SalesChannelApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).contains("T");
            assertThat(response.createdAt()).contains("+09:00");
            assertThat(response.updatedAt()).contains("T");
            assertThat(response.updatedAt()).contains("+09:00");
        }

        @Test
        @DisplayName("INACTIVE 상태도 올바르게 변환된다")
        void toResponse_InactiveStatus_ReturnsApiResponse() {
            // given
            SalesChannelResult result =
                    SalesChannelApiFixtures.channelResult(2L, "G마켓", "INACTIVE");

            // when
            SalesChannelApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(2L);
            assertThat(response.channelName()).isEqualTo("G마켓");
            assertThat(response.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResponses 메서드 테스트")
    class ToResponsesTest {

        @Test
        @DisplayName("SalesChannelResult 목록을 SalesChannelApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<SalesChannelResult> results = SalesChannelApiFixtures.channelResults(3);

            // when
            List<SalesChannelApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).channelName()).isEqualTo("채널_1");
            assertThat(responses.get(1).channelName()).isEqualTo("채널_2");
            assertThat(responses.get(2).channelName()).isEqualTo("채널_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<SalesChannelResult> results = List.of();

            // when
            List<SalesChannelApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse 메서드 테스트")
    class ToPageResponseTest {

        @Test
        @DisplayName("SalesChannelPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SalesChannelPageResult pageResult = SalesChannelApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<SalesChannelApiResponse> response = mapper.toPageResponse(pageResult);

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
            SalesChannelPageResult pageResult = SalesChannelApiFixtures.emptyPageResult();

            // when
            PageApiResponse<SalesChannelApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
