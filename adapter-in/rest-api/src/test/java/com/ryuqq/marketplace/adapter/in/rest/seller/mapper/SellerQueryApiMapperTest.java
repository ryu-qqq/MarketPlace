package com.ryuqq.marketplace.adapter.in.rest.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.SellerApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.query.SearchSellersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.seller.dto.response.SellerPublicProfileApiResponse;
import com.ryuqq.marketplace.application.seller.dto.query.SellerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerFullCompositeResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPublicProfileResult;
import com.ryuqq.marketplace.application.seller.dto.response.SellerResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerQueryApiMapper 단위 테스트")
class SellerQueryApiMapperTest {

    private SellerQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchSellersApiRequest를 SellerSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchSellersApiRequest request =
                    SellerApiFixtures.searchRequest(true, "sellerName", "테스트", 0, 20);

            // when
            SellerSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isTrue();
            assertThat(result.searchField()).isEqualTo("sellerName");
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
            SearchSellersApiRequest request =
                    new SearchSellersApiRequest(null, null, null, null, null, null, null);

            // when
            SellerSearchParams result = mapper.toSearchParams(request);

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
            SearchSellersApiRequest request = SellerApiFixtures.searchRequest();

            // when
            SellerSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.active()).isNull();
            assertThat(result.searchField()).isNull();
            assertThat(result.searchWord()).isNull();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("SellerResult를 SellerApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SellerResult result = SellerApiFixtures.sellerResult(1L);

            // when
            SellerApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.sellerName()).isEqualTo("테스트셀러");
            assertThat(response.displayName()).isEqualTo("테스트 브랜드");
            assertThat(response.logoUrl()).isEqualTo("https://example.com/logo.png");
            assertThat(response.description()).isEqualTo("테스트 셀러 설명입니다.");
            assertThat(response.active()).isTrue();
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            SellerResult result = SellerApiFixtures.sellerResult(1L);

            // when
            SellerApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("SellerResult 목록을 SellerApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<SellerResult> results = SellerApiFixtures.sellerResults(3);

            // when
            List<SellerApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).sellerName()).isEqualTo("셀러_1");
            assertThat(responses.get(1).sellerName()).isEqualTo("셀러_2");
            assertThat(responses.get(2).sellerName()).isEqualTo("셀러_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<SellerResult> results = List.of();

            // when
            List<SellerApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("SellerPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SellerPageResult pageResult = SellerApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<SellerApiResponse> response = mapper.toPageResponse(pageResult);

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
            SellerPageResult pageResult = SellerApiFixtures.emptyPageResult();

            // when
            PageApiResponse<SellerApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("SellerFullCompositeResult를 SellerDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsFullResult_ReturnsDetailResponse() {
            // given
            SellerFullCompositeResult result = SellerApiFixtures.fullCompositeResult(1L);

            // when
            SellerDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.seller()).isNotNull();
            assertThat(response.seller().id()).isEqualTo(1L);
            assertThat(response.seller().sellerName()).isEqualTo("테스트셀러");

            assertThat(response.businessInfo()).isNotNull();
            assertThat(response.businessInfo().id()).isEqualTo(1L);

            assertThat(response.csInfo()).isNotNull();
            assertThat(response.csInfo().id()).isEqualTo(1L);

            assertThat(response.contractInfo()).isNotNull();
            assertThat(response.contractInfo().id()).isEqualTo(1L);

            assertThat(response.settlementInfo()).isNotNull();
            assertThat(response.settlementInfo().id()).isEqualTo(1L);
        }

        @Test
        @DisplayName("SellerInfo의 날짜 필드가 ISO 8601 형식으로 변환된다")
        void toDetailResponse_ConvertsSellerDate_ReturnsIso8601Format() {
            // given
            SellerFullCompositeResult result = SellerApiFixtures.fullCompositeResult(1L);

            // when
            SellerDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.seller().createdAt())
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
            assertThat(response.seller().updatedAt())
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}");
        }
    }

    @Nested
    @DisplayName("toPublicProfileResponse() - 공개 프로필 응답 변환")
    class ToPublicProfileResponseTest {

        @Test
        @DisplayName("SellerPublicProfileResult를 SellerPublicProfileApiResponse로 변환한다")
        void toPublicProfileResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SellerPublicProfileResult result = SellerApiFixtures.publicProfileResult();

            // when
            SellerPublicProfileApiResponse response = mapper.toPublicProfileResponse(result);

            // then
            assertThat(response.sellerName()).isEqualTo("테스트셀러");
            assertThat(response.displayName()).isEqualTo("테스트 브랜드");
            assertThat(response.companyName()).isEqualTo("테스트컴퍼니");
            assertThat(response.representative()).isEqualTo("홍길동");
        }
    }
}
