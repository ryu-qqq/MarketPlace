package com.ryuqq.marketplace.adapter.in.rest.shop.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shop.ShopApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.query.SearchShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shop.dto.response.ShopApiResponse;
import com.ryuqq.marketplace.application.shop.dto.query.ShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import com.ryuqq.marketplace.application.shop.dto.response.ShopResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShopQueryApiMapper 단위 테스트")
class ShopQueryApiMapperTest {

    private ShopQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShopQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchShopsApiRequest를 ShopSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchShopsApiRequest request =
                    ShopApiFixtures.searchRequest(List.of("ACTIVE"), "SHOP_NAME", "테스트", 0, 20);

            // when
            ShopSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("ACTIVE");
            assertThat(result.searchField()).isEqualTo("SHOP_NAME");
            assertThat(result.searchWord()).isEqualTo("테스트");
            assertThat(result.searchParams().page()).isZero();
            assertThat(result.searchParams().size()).isEqualTo(20);
            assertThat(result.searchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.searchParams().sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchShopsApiRequest request =
                    new SearchShopsApiRequest(null, null, null, null, null, null, null, null);

            // when
            ShopSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.searchParams().page()).isZero();
            assertThat(result.searchParams().size()).isEqualTo(20);
            assertThat(result.searchParams().sortKey()).isEqualTo("createdAt");
            assertThat(result.searchParams().sortDirection()).isEqualTo("DESC");
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            SearchShopsApiRequest request = ShopApiFixtures.searchRequest();

            // when
            ShopSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("ShopResult를 ShopApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            ShopResult result = ShopApiFixtures.shopResult(1L);

            // when
            ShopApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.shopName()).isEqualTo("테스트몰");
            assertThat(response.accountId()).isEqualTo("test_account_01");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            ShopResult result = ShopApiFixtures.shopResult(1L);

            // when
            ShopApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("ShopResult 목록을 ShopApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<ShopResult> results = ShopApiFixtures.shopResults(3);

            // when
            List<ShopApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).shopName()).isEqualTo("테스트몰_1");
            assertThat(responses.get(1).shopName()).isEqualTo("테스트몰_2");
            assertThat(responses.get(2).shopName()).isEqualTo("테스트몰_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<ShopResult> results = List.of();

            // when
            List<ShopApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("ShopPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            ShopPageResult pageResult = ShopApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<ShopApiResponse> response = mapper.toPageResponse(pageResult);

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
            ShopPageResult pageResult = ShopApiFixtures.emptyPageResult();

            // when
            PageApiResponse<ShopApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
