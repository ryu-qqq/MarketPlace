package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundProductQueryApiMapper 단위 테스트")
class InboundProductQueryApiMapperTest {

    private InboundProductQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundProductQueryApiMapper();
    }

    @Nested
    @DisplayName("toResponse(InboundProductDetailResult) - 상세 조회 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("InboundProductDetailResult를 InboundProductDetailApiResponse로 변환한다")
        void toResponse_ConvertsDetailResult_ReturnsApiResponse() {
            // given
            InboundProductDetailResult result = InboundProductApiFixtures.detailResult();

            // when
            InboundProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.status()).isEqualTo(InboundProductApiFixtures.DEFAULT_STATUS);
            assertThat(response.externalProductCode())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE);
            assertThat(response.internalProductGroupId())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID);
        }

        @Test
        @DisplayName("상품 목록이 올바르게 변환된다")
        void toResponse_ConvertsProducts_ReturnsProductItemResponses() {
            // given
            InboundProductDetailResult result = InboundProductApiFixtures.detailResult();

            // when
            InboundProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.products()).hasSize(2);
            assertThat(response.products().get(0).productId()).isEqualTo(1L);
            assertThat(response.products().get(0).skuCode()).isEqualTo("SKU-001");
            assertThat(response.products().get(0).regularPrice())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(response.products().get(0).currentPrice())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_CURRENT_PRICE);
            assertThat(response.products().get(0).stockQuantity()).isEqualTo(100);
            assertThat(response.products().get(0).sortOrder()).isZero();
        }

        @Test
        @DisplayName("옵션 목록이 올바르게 변환된다")
        void toResponse_ConvertsOptions_ReturnsOptionItemResponses() {
            // given
            InboundProductDetailResult result = InboundProductApiFixtures.detailResult();

            // when
            InboundProductDetailApiResponse response = mapper.toResponse(result);

            // then
            List<InboundProductDetailApiResponse.OptionItemApiResponse> options =
                    response.products().get(0).options();
            assertThat(options).hasSize(1);
            assertThat(options.get(0).optionGroupName()).isEqualTo("색상");
            assertThat(options.get(0).optionValueName()).isEqualTo("블랙");
        }

        @Test
        @DisplayName("미변환 상태(internalProductGroupId=null, products 빈 목록)도 올바르게 변환된다")
        void toResponse_NotConvertedResult_ReturnsApiResponseWithNullGroupId() {
            // given
            InboundProductDetailResult result =
                    InboundProductApiFixtures.detailResultNotConverted();

            // when
            InboundProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.status()).isEqualTo("PENDING_MAPPING");
            assertThat(response.externalProductCode())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE);
            assertThat(response.internalProductGroupId()).isNull();
            assertThat(response.products()).isEmpty();
        }

        @Test
        @DisplayName("상품 목록이 null이면 빈 목록으로 변환된다")
        void toResponse_NullProducts_ReturnsEmptyList() {
            // given
            InboundProductDetailResult result =
                    new InboundProductDetailResult(
                            "RECEIVED",
                            InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE,
                            null,
                            null);

            // when
            InboundProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.products()).isEmpty();
        }

        @Test
        @DisplayName("상품 내 옵션 목록이 null이면 빈 목록으로 변환된다")
        void toResponse_NullOptions_ReturnsEmptyOptionList() {
            // given
            List<InboundProductDetailResult.ProductItem> products =
                    List.of(
                            new InboundProductDetailResult.ProductItem(
                                    1L, "SKU-001", 30000, 25000, 100, 0, null));
            InboundProductDetailResult result =
                    new InboundProductDetailResult(
                            "CONVERTED",
                            InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE,
                            InboundProductApiFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                            products);

            // when
            InboundProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.products().get(0).options()).isEmpty();
        }
    }
}
