package com.ryuqq.marketplace.adapter.in.rest.selleraddress.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.SellerAddressApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.query.SearchSellerAddressesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerAddressApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.response.SellerOperationMetadataApiResponse;
import com.ryuqq.marketplace.application.selleraddress.dto.query.SellerAddressSearchParams;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressPageResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerAddressResult;
import com.ryuqq.marketplace.application.selleraddress.dto.response.SellerOperationMetadataResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAddressQueryApiMapper 단위 테스트")
class SellerAddressQueryApiMapperTest {

    private SellerAddressQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerAddressQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 요청 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("request의 sellerIds를 SellerAddressSearchParams로 변환한다")
        void toSearchParams_ConvertsRequest_ReturnsSearchParams() {
            // given
            List<Long> sellerIds = List.of(1L, 2L);
            SearchSellerAddressesApiRequest request =
                    SellerAddressApiFixtures.searchRequest(
                            sellerIds,
                            List.of("SHIPPING", "RETURN"),
                            true,
                            "addressName",
                            "창고",
                            0,
                            20);

            // when
            SellerAddressSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.sellerIds()).containsExactly(1L, 2L);
            assertThat(result.addressTypes()).containsExactly("SHIPPING", "RETURN");
            assertThat(result.defaultAddress()).isTrue();
            assertThat(result.searchField()).isEqualTo("addressName");
            assertThat(result.searchWord()).isEqualTo("창고");
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("page/size가 null이면 기본값 0, 20을 적용한다")
        void toSearchParams_NullPageAndSize_AppliesDefaults() {
            // given
            SearchSellerAddressesApiRequest request =
                    SellerAddressApiFixtures.searchRequest(
                            List.of(1L), null, null, null, null, null, null);

            // when
            SellerAddressSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.sellerIds()).containsExactly(1L);
            assertThat(result.page()).isZero();
            assertThat(result.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("sellerIds가 null이면 빈 리스트로 변환한다")
        void toSearchParams_NullSellerIds_ReturnsEmptyList() {
            // given
            SearchSellerAddressesApiRequest request = SellerAddressApiFixtures.searchRequest();

            // when
            SellerAddressSearchParams result = mapper.toSearchParams(request);

            // then
            assertThat(result.sellerIds()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toResponse() - 단일 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("SellerAddressResult를 SellerAddressApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SellerAddressResult result = SellerAddressApiFixtures.sellerAddressResult(1L);

            // when
            SellerAddressApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.sellerId()).isEqualTo(1L);
            assertThat(response.addressType())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ADDRESS_TYPE);
            assertThat(response.addressName())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ADDRESS_NAME);
            assertThat(response.address().zipCode())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_ZIP_CODE);
            assertThat(response.address().line1())
                    .isEqualTo(SellerAddressApiFixtures.DEFAULT_LINE1);
            assertThat(response.defaultAddress()).isFalse();
            assertThat(response.createdAt()).isNotNull();
            assertThat(response.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("날짜를 ISO 8601 형식으로 변환한다")
        void toResponse_ConvertsDate_ReturnsIso8601Format() {
            // given
            SellerAddressResult result = SellerAddressApiFixtures.sellerAddressResult(1L);

            // when
            SellerAddressApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.createdAt()).contains("2025-01-23");
            assertThat(response.createdAt()).contains("+09:00");
        }
    }

    @Nested
    @DisplayName("toResponses() - 목록 변환")
    class ToResponsesTest {

        @Test
        @DisplayName("결과 목록을 응답 목록으로 변환한다")
        void toResponses_ConvertsResults_ReturnsApiResponses() {
            // given
            List<SellerAddressResult> results = SellerAddressApiFixtures.sellerAddressResults(3);

            // when
            List<SellerAddressApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).hasSize(3);
            assertThat(responses.get(0).id()).isEqualTo(1L);
            assertThat(responses.get(1).id()).isEqualTo(2L);
            assertThat(responses.get(2).id()).isEqualTo(3L);
        }

        @Test
        @DisplayName("빈 목록은 빈 응답 목록을 반환한다")
        void toResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<SellerAddressResult> results = List.of();

            // when
            List<SellerAddressApiResponse> responses = mapper.toResponses(results);

            // then
            assertThat(responses).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("PagedResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPagedResult_ReturnsPageApiResponse() {
            // given
            SellerAddressPageResult pagedResult = SellerAddressApiFixtures.pagedResult(5, 0, 20);

            // when
            PageApiResponse<SellerAddressApiResponse> response = mapper.toPageResponse(pagedResult);

            // then
            assertThat(response.content()).hasSize(5);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(5);
        }

        @Test
        @DisplayName("빈 페이지 결과를 변환한다")
        void toPageResponse_EmptyPagedResult_ReturnsEmptyPage() {
            // given
            SellerAddressPageResult pagedResult = SellerAddressApiFixtures.emptyPagedResult();

            // when
            PageApiResponse<SellerAddressApiResponse> response = mapper.toPageResponse(pagedResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }

    @Nested
    @DisplayName("toMetadataResponse() - 운영 메타데이터 변환")
    class ToMetadataResponseTest {

        @Test
        @DisplayName("SellerOperationMetadataResult를 SellerOperationMetadataApiResponse로 변환한다")
        void toMetadataResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            SellerOperationMetadataResult result =
                    new SellerOperationMetadataResult(5, 3, 2, true, false, 2, 1, true, true);

            // when
            SellerOperationMetadataApiResponse response = mapper.toMetadataResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(5);
            assertThat(response.shippingCount()).isEqualTo(3);
            assertThat(response.returnCount()).isEqualTo(2);
            assertThat(response.hasDefaultShipping()).isTrue();
            assertThat(response.hasDefaultReturn()).isFalse();
            assertThat(response.shippingPolicyCount()).isEqualTo(2);
            assertThat(response.refundPolicyCount()).isEqualTo(1);
            assertThat(response.hasDefaultShippingPolicy()).isTrue();
            assertThat(response.hasDefaultRefundPolicy()).isTrue();
        }
    }
}
