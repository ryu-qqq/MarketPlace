package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsShopsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsShopApiResponse;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsShopSearchParams;
import com.ryuqq.marketplace.application.shop.dto.response.ShopPageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsShopQueryApiMapper 단위 테스트")
class OmsShopQueryApiMapperTest {

    private OmsShopQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OmsShopQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 쇼핑몰 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchOmsShopsApiRequest를 OmsShopSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchOmsShopsApiRequest request = OmsApiFixtures.searchShopsRequest("스마트스토어", 0, 100);

            // when
            OmsShopSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.keyword()).isEqualTo("스마트스토어");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(100);
            assertThat(params.sortKey()).isEqualTo("CREATED_AT");
            assertThat(params.sortDirection()).isEqualTo("ASC");
        }

        @Test
        @DisplayName("keyword가 null이면 null로 변환한다")
        void toSearchParams_NullKeyword_ReturnsNullKeyword() {
            // given
            SearchOmsShopsApiRequest request = OmsApiFixtures.searchShopsRequest();

            // when
            OmsShopSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.keyword()).isNull();
        }

        @Test
        @DisplayName("page와 size가 올바르게 전달된다")
        void toSearchParams_PageAndSize_AreCorrect() {
            // given
            SearchOmsShopsApiRequest request = OmsApiFixtures.searchShopsRequest(null, 1, 50);

            // when
            OmsShopSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isEqualTo(1);
            assertThat(params.size()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 쇼핑몰 페이지 응답 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("ShopPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            ShopPageResult pageResult = OmsApiFixtures.shopPageResult(3, 0, 100);

            // when
            PageApiResponse<OmsShopApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(100);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("쇼핑몰 응답의 각 필드가 올바르게 변환된다")
        void toPageResponse_ConvertsFields_ReturnsCorrectFields() {
            // given
            ShopPageResult pageResult = OmsApiFixtures.shopPageResult(1, 0, 100);

            // when
            PageApiResponse<OmsShopApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            OmsShopApiResponse first = response.content().get(0);
            assertThat(first.id()).isEqualTo(1L);
            assertThat(first.shopName()).isEqualTo("스마트스토어_1");
            assertThat(first.salesChannelId()).isEqualTo(1L);
            assertThat(first.accountId()).isEqualTo("account_1");
            assertThat(first.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            ShopPageResult pageResult = OmsApiFixtures.emptyShopPageResult();

            // when
            PageApiResponse<OmsShopApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }

        @Test
        @DisplayName("여러 쇼핑몰이 순서대로 변환된다")
        void toPageResponse_MultipleShops_PreservesOrder() {
            // given
            ShopPageResult pageResult = OmsApiFixtures.shopPageResult(3, 0, 100);

            // when
            PageApiResponse<OmsShopApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).shopName()).isEqualTo("스마트스토어_1");
            assertThat(response.content().get(1).shopName()).isEqualTo("스마트스토어_2");
            assertThat(response.content().get(2).shopName()).isEqualTo("스마트스토어_3");
        }
    }
}
