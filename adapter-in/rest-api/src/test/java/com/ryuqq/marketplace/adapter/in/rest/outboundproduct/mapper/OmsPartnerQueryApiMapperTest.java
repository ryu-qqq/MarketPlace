package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.query.SearchOmsPartnersApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsPartnerApiResponse;
import com.ryuqq.marketplace.application.outboundproduct.dto.query.OmsPartnerSearchParams;
import com.ryuqq.marketplace.application.seller.dto.response.SellerPageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("OmsPartnerQueryApiMapper 단위 테스트")
class OmsPartnerQueryApiMapperTest {

    private OmsPartnerQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OmsPartnerQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 파트너 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("SearchOmsPartnersApiRequest를 OmsPartnerSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            SearchOmsPartnersApiRequest request =
                    OmsApiFixtures.searchPartnersRequest("나이키", 0, 100);

            // when
            OmsPartnerSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.keyword()).isEqualTo("나이키");
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(100);
            assertThat(params.sortKey()).isEqualTo("CREATED_AT");
            assertThat(params.sortDirection()).isEqualTo("ASC");
        }

        @Test
        @DisplayName("keyword가 null이면 null로 변환한다")
        void toSearchParams_NullKeyword_ReturnsNullKeyword() {
            // given
            SearchOmsPartnersApiRequest request = OmsApiFixtures.searchPartnersRequest();

            // when
            OmsPartnerSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.keyword()).isNull();
        }

        @Test
        @DisplayName("page와 size가 올바르게 전달된다")
        void toSearchParams_PageAndSize_AreCorrect() {
            // given
            SearchOmsPartnersApiRequest request = OmsApiFixtures.searchPartnersRequest(null, 2, 50);

            // when
            OmsPartnerSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isEqualTo(2);
            assertThat(params.size()).isEqualTo(50);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 파트너 페이지 응답 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("SellerPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResult_ReturnsPageResponse() {
            // given
            SellerPageResult pageResult = OmsApiFixtures.sellerPageResult(3, 0, 100);

            // when
            PageApiResponse<OmsPartnerApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(100);
            assertThat(response.totalElements()).isEqualTo(3);
        }

        @Test
        @DisplayName("파트너 응답의 각 필드가 올바르게 변환된다")
        void toPageResponse_ConvertsFields_ReturnsCorrectFields() {
            // given
            SellerPageResult pageResult = OmsApiFixtures.sellerPageResult(1, 0, 100);

            // when
            PageApiResponse<OmsPartnerApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            OmsPartnerApiResponse first = response.content().get(0);
            assertThat(first.id()).isEqualTo(1L);
            assertThat(first.partnerName()).isEqualTo("Nike Korea 1");
            assertThat(first.partnerCode()).isEqualTo("나이키코리아_1");
            assertThat(first.status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("활성화된 셀러는 status가 ACTIVE로 변환된다")
        void toPageResponse_ActiveSeller_ReturnsActiveStatus() {
            // given
            SellerPageResult pageResult = OmsApiFixtures.sellerPageResult(1, 0, 100);

            // when
            PageApiResponse<OmsPartnerApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).status()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("비활성화된 셀러는 status가 INACTIVE로 변환된다")
        void toPageResponse_InactiveSeller_ReturnsInactiveStatus() {
            // given
            com.ryuqq.marketplace.application.seller.dto.response.SellerResult inactiveSeller =
                    new com.ryuqq.marketplace.application.seller.dto.response.SellerResult(
                            99L,
                            "비활성파트너",
                            "Inactive Partner",
                            null,
                            null,
                            false,
                            OmsApiFixtures.DEFAULT_INSTANT,
                            OmsApiFixtures.DEFAULT_INSTANT);
            SellerPageResult pageResult =
                    SellerPageResult.of(java.util.List.of(inactiveSeller), 1, 0, 100);

            // when
            PageApiResponse<OmsPartnerApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).status()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("빈 결과이면 빈 페이지 응답을 반환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            SellerPageResult pageResult = OmsApiFixtures.emptySellerPageResult();

            // when
            PageApiResponse<OmsPartnerApiResponse> response = mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
