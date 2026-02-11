package com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.SalesChannelBrandApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.query.SearchSalesChannelBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelbrand.dto.response.SalesChannelBrandApiResponse;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.query.SalesChannelBrandSearchParams;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandPageResult;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.SalesChannelBrandResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelBrandQueryApiMapper 단위 테스트")
class SalesChannelBrandQueryApiMapperTest {

    private SalesChannelBrandQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelBrandQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams 메서드 테스트")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchSalesChannelBrandsApiRequest를 SalesChannelBrandSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            List<Long> salesChannelIds = List.of(1L, 2L);
            SearchSalesChannelBrandsApiRequest request =
                    SalesChannelBrandApiFixtures.searchRequest(
                            List.of("ACTIVE"), "externalBrandCode", "BRD", 0, 20);

            // when
            SalesChannelBrandSearchParams params = mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(params.salesChannelIds()).containsExactly(1L, 2L);
            assertThat(params.statuses()).containsExactly("ACTIVE");
            assertThat(params.searchField()).isEqualTo("externalBrandCode");
            assertThat(params.searchWord()).isEqualTo("BRD");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
            assertThat(params.sortKey()).isEqualTo("createdAt");
            assertThat(params.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SearchSalesChannelBrandsApiRequest request =
                    new SearchSalesChannelBrandsApiRequest(
                            null, null, null, null, null, null, null);

            // when
            SalesChannelBrandSearchParams params = mapper.toSearchParams(salesChannelIds, request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
            assertThat(params.sortKey()).isEqualTo("createdAt");
            assertThat(params.sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            List<Long> salesChannelIds = List.of(1L);
            SearchSalesChannelBrandsApiRequest request = SalesChannelBrandApiFixtures.searchRequest();

            // when
            SalesChannelBrandSearchParams params = mapper.toSearchParams(salesChannelIds, request);

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
        @DisplayName("SalesChannelBrandResult를 SalesChannelBrandApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SalesChannelBrandResult result = SalesChannelBrandApiFixtures.brandResult(1L);

            // when
            SalesChannelBrandApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.salesChannelId()).isEqualTo(1L);
            assertThat(response.externalBrandCode()).isEqualTo("BRD001");
            assertThat(response.externalBrandName()).isEqualTo("나이키");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            SalesChannelBrandResult result = SalesChannelBrandApiFixtures.brandResult(1L);

            // when
            SalesChannelBrandApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).contains("T");
            assertThat(response.createdAt()).contains("+09:00");
            assertThat(response.updatedAt()).contains("T");
            assertThat(response.updatedAt()).contains("+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses 메서드 테스트")
    class ToResponsesTest {

        @Test
        @DisplayName("SalesChannelBrandResult 목록을 SalesChannelBrandApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<SalesChannelBrandResult> results = SalesChannelBrandApiFixtures.brandResults(3);

            // when
            List<SalesChannelBrandApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).externalBrandCode()).isEqualTo("BRD001");
            assertThat(responses.get(1).externalBrandCode()).isEqualTo("BRD002");
            assertThat(responses.get(2).externalBrandCode()).isEqualTo("BRD003");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<SalesChannelBrandResult> results = List.of();

            // when
            List<SalesChannelBrandApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse 메서드 테스트")
    class ToPageResponseTest {

        @Test
        @DisplayName("SalesChannelBrandPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SalesChannelBrandPageResult pageResult =
                    SalesChannelBrandApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<SalesChannelBrandApiResponse> response =
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
            SalesChannelBrandPageResult pageResult =
                    SalesChannelBrandApiFixtures.emptyPageResult();

            // when
            PageApiResponse<SalesChannelBrandApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
