package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchSyncHistoryApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncHistoryApiResponse;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsProductSearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.SyncHistorySearchParams;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsProductQueryApiMapper 단위 테스트")
class OmsProductQueryApiMapperTest {

    private OmsProductQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OmsProductQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 상품 목록 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchOmsProductsApiRequest를 OmsProductSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchOmsProductsApiRequest request =
                    OmsApiFixtures.searchProductsRequest(
                            "CREATED_AT",
                            "2026-01-01",
                            "2026-03-03",
                            List.of("ACTIVE"),
                            List.of("FAILED"),
                            "productName",
                            "나이키",
                            List.of(1L, 2L),
                            List.of(10L),
                            List.of("PG-1"),
                            0,
                            10);

            // when
            OmsProductSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.dateType()).isEqualTo("CREATED_AT");
            assertThat(params.statuses()).containsExactly("ACTIVE");
            assertThat(params.syncStatuses()).containsExactly("FAILED");
            assertThat(params.searchField()).isEqualTo("productName");
            assertThat(params.searchWord()).isEqualTo("나이키");
            assertThat(params.shopIds()).containsExactly(1L, 2L);
            assertThat(params.partnerIds()).containsExactly(10L);
            assertThat(params.productCodes()).containsExactly("PG-1");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 10)으로 변환한다")
        void toSearchParams_NullPageSize_UsesDefaults() {
            // given
            SearchOmsProductsApiRequest request =
                    new SearchOmsProductsApiRequest(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null);

            // when
            OmsProductSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("잘못된 날짜 형식은 null로 변환한다")
        void toSearchParams_InvalidDate_ReturnsNull() {
            // given
            SearchOmsProductsApiRequest request =
                    new SearchOmsProductsApiRequest(
                            null,
                            "invalid-date",
                            "also-invalid",
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            null,
                            0,
                            10);

            // when
            OmsProductSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("모든 필터가 null이면 전체 조회 파라미터로 변환한다")
        void toSearchParams_AllNulls_ReturnsAllSearchParams() {
            // given
            SearchOmsProductsApiRequest request = OmsApiFixtures.searchProductsRequest();

            // when
            OmsProductSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.statuses()).isNull();
            assertThat(params.syncStatuses()).isNull();
            assertThat(params.searchField()).isNull();
            assertThat(params.searchWord()).isNull();
            assertThat(params.shopIds()).isNull();
            assertThat(params.partnerIds()).isNull();
            assertThat(params.productCodes()).isNull();
        }
    }

    @Nested
    @DisplayName("toSyncHistoryParams() - 연동 이력 검색 요청 변환")
    class ToSyncHistoryParamsTest {

        @Test
        @DisplayName("productGroupId와 SearchSyncHistoryApiRequest를 SyncHistorySearchParams로 변환한다")
        void toSyncHistoryParams_ConvertsRequest_ReturnsSyncHistoryParams() {
            // given
            long productGroupId = 100L;
            SearchSyncHistoryApiRequest request =
                    OmsApiFixtures.searchSyncHistoryRequest("COMPLETED", 0, 10);

            // when
            SyncHistorySearchParams params = mapper.toSyncHistoryParams(productGroupId, request);

            // then
            assertThat(params.productGroupId()).isEqualTo(100L);
            assertThat(params.status()).isEqualTo("COMPLETED");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값(0, 10)으로 변환한다")
        void toSyncHistoryParams_NullPageSize_UsesDefaults() {
            // given
            long productGroupId = 100L;
            SearchSyncHistoryApiRequest request = new SearchSyncHistoryApiRequest(null, null, null);

            // when
            SyncHistorySearchParams params = mapper.toSyncHistoryParams(productGroupId, request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(10);
        }

        @Test
        @DisplayName("정렬은 createdAt DESC 고정으로 변환한다")
        void toSyncHistoryParams_AlwaysSortsByCreatedAtDesc() {
            // given
            long productGroupId = 100L;
            SearchSyncHistoryApiRequest request = OmsApiFixtures.searchSyncHistoryRequest();

            // when
            SyncHistorySearchParams params = mapper.toSyncHistoryParams(productGroupId, request);

            // then
            assertThat(params.commonSearchParams().sortKey()).isEqualTo("createdAt");
            assertThat(params.commonSearchParams().sortDirection()).isEqualTo("DESC");
        }
    }

    @Nested
    @DisplayName("toProductPageResponse() - 상품 목록 페이지 응답 변환")
    class ToProductPageResponseTest {

        @Test
        @DisplayName("OmsProductPageResult를 PageApiResponse로 변환한다")
        void toProductPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            OmsProductPageResult pageResult = OmsApiFixtures.productPageResult(3, 0, 10);

            // when
            PageApiResponse<OmsProductApiResponse> response =
                    mapper.toProductPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("상품 응답의 각 필드가 올바르게 변환된다")
        void toProductPageResponse_ConvertsFields_ReturnsCorrectFields() {
            // given
            OmsProductPageResult pageResult = OmsApiFixtures.productPageResult(1, 0, 10);

            // when
            PageApiResponse<OmsProductApiResponse> response =
                    mapper.toProductPageResponse(pageResult);

            // then
            OmsProductApiResponse first = response.content().get(0);
            assertThat(first.id()).isEqualTo(1L);
            assertThat(first.productCode()).isEqualTo("PG-1");
            assertThat(first.productName()).isEqualTo("테스트상품_1");
            assertThat(first.price()).isEqualTo(89000);
            assertThat(first.stock()).isEqualTo(150);
            assertThat(first.status()).isEqualTo("ACTIVE");
            assertThat(first.statusLabel()).isEqualTo("판매중");
            assertThat(first.partnerName()).isEqualTo("나이키코리아");
            assertThat(first.syncStatus()).isEqualTo("SUCCESS");
            assertThat(first.syncStatusLabel()).isEqualTo("연동완료");
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 형식으로 변환된다")
        void toProductPageResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            OmsProductPageResult pageResult = OmsApiFixtures.productPageResult(1, 0, 10);

            // when
            PageApiResponse<OmsProductApiResponse> response =
                    mapper.toProductPageResponse(pageResult);

            // then
            OmsProductApiResponse first = response.content().get(0);
            assertThat(first.createdAt()).contains("T");
            assertThat(first.createdAt()).contains("+09:00");
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toProductPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            OmsProductPageResult pageResult = OmsApiFixtures.emptyProductPageResult();

            // when
            PageApiResponse<OmsProductApiResponse> response =
                    mapper.toProductPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상품 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("OmsProductDetailResult를 OmsProductDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsResult_ReturnsDetailResponse() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(125694305L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.productGroup()).isNotNull();
            assertThat(response.productGroup().productGroupId()).isEqualTo(125694305L);
            assertThat(response.productGroup().productGroupName()).isEqualTo("나이키 에어포스 1 '07 화이트");
            assertThat(response.productGroup().sellerId()).isEqualTo(1001L);
            assertThat(response.productGroup().sellerName()).isEqualTo("나이키코리아");
        }

        @Test
        @DisplayName("브랜드 정보가 올바르게 변환된다")
        void toDetailResponse_ConvertsBrand_ReturnsBrandInfo() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(1L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.productGroup().brand()).isNotNull();
            assertThat(response.productGroup().brand().brandId()).isEqualTo(501L);
            assertThat(response.productGroup().brand().brandName()).isEqualTo("Nike");
        }

        @Test
        @DisplayName("상품(SKU) 목록이 올바르게 변환된다")
        void toDetailResponse_ConvertsProducts_ReturnsProductList() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(1L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.products()).isNotEmpty();
            assertThat(response.products().get(0).productId()).isEqualTo(1001L);
            assertThat(response.products().get(0).stockQuantity()).isEqualTo(10);
        }

        @Test
        @DisplayName("옵션 정보가 올바르게 변환된다")
        void toDetailResponse_ConvertsOptions_ReturnsOptionList() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(1L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            OmsProductDetailApiResponse.ProductResponse product = response.products().get(0);
            assertThat(product.options()).isNotEmpty();
            assertThat(product.options().get(0).optionName()).isEqualTo("SIZE");
            assertThat(product.options().get(0).optionValue()).isEqualTo("250");
            assertThat(product.option()).isEqualTo("250");
        }

        @Test
        @DisplayName("연동 통계(syncSummary)가 올바르게 변환된다")
        void toDetailResponse_ConvertsSyncSummary_ReturnsSyncSummary() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(1L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.syncSummary()).isNotNull();
            assertThat(response.syncSummary().totalSyncCount()).isEqualTo(5L);
            assertThat(response.syncSummary().successCount()).isEqualTo(3L);
            assertThat(response.syncSummary().failCount()).isEqualTo(1L);
            assertThat(response.syncSummary().pendingCount()).isEqualTo(1L);
            assertThat(response.syncSummary().lastSyncAt()).contains("+09:00");
        }

        @Test
        @DisplayName("추가 금액은 첫 번째 상품 가격을 기준으로 계산된다")
        void toDetailResponse_CalculatesAdditionalPrice_BasedOnFirstProduct() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(1L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            // 첫 번째 상품 현재가 129000, 두 번째 상품 현재가 139000 → 추가금액 10000
            assertThat(response.products().get(0).additionalPrice()).isZero();
            assertThat(response.products().get(1).additionalPrice()).isEqualTo(10000);
        }

        @Test
        @DisplayName("상품 상태 ACTIVE이면 noOut='N', display='Y'로 변환된다")
        void toDetailResponse_ActiveStatus_ReturnsDisplayYAndSoldOutN() {
            // given
            OmsProductDetailResult result = OmsApiFixtures.productDetailResult(1L);

            // when
            OmsProductDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.productGroup().productStatus().soldOutYn()).isEqualTo("N");
            assertThat(response.productGroup().productStatus().displayYn()).isEqualTo("Y");
        }
    }

    @Nested
    @DisplayName("toSyncHistoryPageResponse() - 연동 이력 페이지 응답 변환")
    class ToSyncHistoryPageResponseTest {

        @Test
        @DisplayName("SyncHistoryPageResult를 PageApiResponse로 변환한다")
        void toSyncHistoryPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SyncHistoryPageResult pageResult = OmsApiFixtures.syncHistoryPageResult(3, 0, 10);

            // when
            PageApiResponse<SyncHistoryApiResponse> response =
                    mapper.toSyncHistoryPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(10);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("연동 이력 응답의 각 필드가 올바르게 변환된다")
        void toSyncHistoryPageResponse_ConvertsFields_ReturnsCorrectFields() {
            // given
            SyncHistoryPageResult pageResult = OmsApiFixtures.syncHistoryPageResult(1, 0, 10);

            // when
            PageApiResponse<SyncHistoryApiResponse> response =
                    mapper.toSyncHistoryPageResponse(pageResult);

            // then
            SyncHistoryApiResponse first = response.content().get(0);
            assertThat(first.id()).isEqualTo(1L);
            assertThat(first.shopName()).isEqualTo("스마트스토어");
            assertThat(first.accountId()).isEqualTo("trexi001");
            assertThat(first.status()).isEqualTo("COMPLETED");
            assertThat(first.statusLabel()).isEqualTo("완료");
            assertThat(first.retryCount()).isZero();
            assertThat(first.externalProductId()).isEqualTo("NAVER-12345678");
        }

        @Test
        @DisplayName("jobId가 'SYNC-날짜-순번' 형식으로 생성된다")
        void toSyncHistoryPageResponse_GeneratesJobId_WithCorrectFormat() {
            // given
            SyncHistoryPageResult pageResult = OmsApiFixtures.syncHistoryPageResult(1, 0, 10);

            // when
            PageApiResponse<SyncHistoryApiResponse> response =
                    mapper.toSyncHistoryPageResponse(pageResult);

            // then
            SyncHistoryApiResponse first = response.content().get(0);
            assertThat(first.jobId()).startsWith("SYNC-");
            assertThat(first.jobId()).contains("-");
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toSyncHistoryPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            SyncHistoryPageResult pageResult = OmsApiFixtures.emptySyncHistoryPageResult();

            // when
            PageApiResponse<SyncHistoryApiResponse> response =
                    mapper.toSyncHistoryPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
