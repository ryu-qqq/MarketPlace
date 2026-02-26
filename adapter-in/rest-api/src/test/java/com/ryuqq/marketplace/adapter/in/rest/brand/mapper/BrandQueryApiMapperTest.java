package com.ryuqq.marketplace.adapter.in.rest.brand.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.brand.BrandApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.query.SearchBrandsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brand.dto.response.BrandApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.application.brand.dto.query.BrandSearchParams;
import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandQueryApiMapper 단위 테스트")
class BrandQueryApiMapperTest {

    private BrandQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BrandQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchBrandsApiRequest를 BrandSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchBrandsApiRequest request =
                    BrandApiFixtures.searchRequest(List.of("ACTIVE"), "nameKo", "테스트", 0, 20);

            // when
            BrandSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.statuses()).containsExactly("ACTIVE");
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
            SearchBrandsApiRequest request =
                    new SearchBrandsApiRequest(null, null, null, null, null, null, null);

            // when
            BrandSearchParams result = mapper.toSearchParams(request);

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
            SearchBrandsApiRequest request = BrandApiFixtures.searchRequest();

            // when
            BrandSearchParams result = mapper.toSearchParams(request);

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
        @DisplayName("BrandResult를 BrandApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            BrandResult result = BrandApiFixtures.brandResult(1L);

            // when
            BrandApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.code()).isEqualTo("BR001");
            assertThat(response.nameKo()).isEqualTo("테스트브랜드");
            assertThat(response.nameEn()).isEqualTo("TestBrand");
            assertThat(response.shortName()).isEqualTo("테브");
            assertThat(response.status()).isEqualTo("ACTIVE");
            assertThat(response.logoUrl()).isEqualTo("https://example.com/brand-logo.png");
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            BrandResult result = BrandApiFixtures.brandResult(1L);

            // when
            BrandApiResponse response = mapper.toResponse(result);

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
        @DisplayName("BrandResult 목록을 BrandApiResponse 목록으로 변환한다")
        void toResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<BrandResult> results = BrandApiFixtures.brandResults(3);

            // when
            List<BrandApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).nameKo()).isEqualTo("브랜드_1");
            assertThat(responses.get(1).nameKo()).isEqualTo("브랜드_2");
            assertThat(responses.get(2).nameKo()).isEqualTo("브랜드_3");
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<BrandResult> results = List.of();

            // when
            List<BrandApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 결과 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("BrandPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            BrandPageResult pageResult = BrandApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<BrandApiResponse> response = mapper.toPageResponse(pageResult);

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
            BrandPageResult pageResult = BrandApiFixtures.emptyPageResult();

            // when
            PageApiResponse<BrandApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
