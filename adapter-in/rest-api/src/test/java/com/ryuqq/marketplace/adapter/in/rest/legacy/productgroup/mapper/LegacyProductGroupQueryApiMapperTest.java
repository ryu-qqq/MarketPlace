package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductGroupQueryApiMapper 단위 테스트")
class LegacyProductGroupQueryApiMapperTest {

    private LegacyProductGroupQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyProductGroupQueryApiMapper();
    }

    @Nested
    @DisplayName("toResponse - 상품그룹 상세 조회 결과 변환")
    class ToResponseTest {

        @Test
        @DisplayName("LegacyProductGroupDetailResult를 LegacyProductDetailApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response).isNotNull();
            assertThat(response.productGroup()).isNotNull();
            assertThat(response.products()).isNotNull();
            assertThat(response.productNotices()).isNotNull();
            assertThat(response.productGroupImages()).isNotNull();
        }

        @Test
        @DisplayName("상품그룹 기본정보가 올바르게 변환된다")
        void toResponse_ConvertsProductGroupInfo_ReturnsCorrectInfo() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().productGroupId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(response.productGroup().productGroupName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(response.productGroup().sellerId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(response.productGroup().sellerName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_NAME);
        }

        @Test
        @DisplayName("브랜드 정보가 올바르게 변환된다")
        void toResponse_ConvertsBrandInfo_ReturnsCorrectBrand() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().brand()).isNotNull();
            assertThat(response.productGroup().brand().brandId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(response.productGroup().brand().brandName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_BRAND_NAME);
        }

        @Test
        @DisplayName("가격 정보가 올바르게 변환된다")
        void toResponse_ConvertsPriceInfo_ReturnsCorrectPrice() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().price()).isNotNull();
            assertThat(response.productGroup().price().regularPrice().longValue())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(response.productGroup().price().currentPrice().longValue())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("의류 상세정보가 올바르게 변환된다")
        void toResponse_ConvertsClothesDetail_ReturnsCorrectDetail() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().clothesDetailInfo()).isNotNull();
            assertThat(response.productGroup().clothesDetailInfo().productCondition())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_CONDITION);
            assertThat(response.productGroup().clothesDetailInfo().origin())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_ORIGIN);
        }

        @Test
        @DisplayName("배송 정보가 올바르게 변환된다")
        void toResponse_ConvertsDeliveryNotice_ReturnsCorrectDelivery() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().deliveryNotice()).isNotNull();
            assertThat(response.productGroup().deliveryNotice().deliveryArea()).isEqualTo("전국");
            assertThat(response.productGroup().deliveryNotice().deliveryFee()).isEqualTo(3000L);
        }

        @Test
        @DisplayName("반품 정보가 올바르게 변환된다")
        void toResponse_ConvertsRefundNotice_ReturnsCorrectRefund() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().refundNotice()).isNotNull();
            assertThat(response.productGroup().refundNotice().returnMethodDomestic())
                    .isEqualTo("택배");
            assertThat(response.productGroup().refundNotice().returnCourierDomestic())
                    .isEqualTo("CJ대한통운");
        }

        @Test
        @DisplayName("고시정보가 올바르게 변환된다")
        void toResponse_ConvertsNotice_ReturnsCorrectNotice() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productNotices()).isNotNull();
            assertThat(response.productNotices().material()).isEqualTo("면 100%");
            assertThat(response.productNotices().color()).isEqualTo("블랙");
            assertThat(response.productNotices().origin()).isEqualTo("대한민국");
        }

        @Test
        @DisplayName("이미지 목록이 올바르게 변환된다")
        void toResponse_ConvertsImages_ReturnsCorrectImages() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroupImages()).hasSize(2);
            assertThat(response.productGroupImages().get(0).type()).isEqualTo("MAIN");
            assertThat(response.productGroupImages().get(0).productImageUrl())
                    .isEqualTo("https://cdn.example.com/main.jpg");
        }

        @Test
        @DisplayName("MAIN 이미지 URL이 productGroupMainImageUrl로 설정된다")
        void toResponse_ExtractsMainImageUrl_SetsCorrectUrl() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().productGroupMainImageUrl())
                    .isEqualTo("https://cdn.example.com/main.jpg");
        }

        @Test
        @DisplayName("상품 목록이 올바르게 변환된다")
        void toResponse_ConvertsProducts_ReturnsCorrectProducts() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.products()).hasSize(2);
        }

        @Test
        @DisplayName("delivery가 null이면 deliveryNotice와 refundNotice가 null이다")
        void toResponse_NullDelivery_DeliveryAndRefundAreNull() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResultWithoutDelivery(
                            LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID);

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().deliveryNotice()).isNull();
            assertThat(response.productGroup().refundNotice()).isNull();
        }

        @Test
        @DisplayName("detailDescription이 올바르게 매핑된다")
        void toResponse_MapsDetailDescription_Correctly() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.detailDescription()).isEqualTo("<p>상품 상세 설명</p>");
        }

        @Test
        @DisplayName("productStatus가 soldOut/displayed 값으로 올바르게 변환된다")
        void toResponse_ConvertsProductStatus_ReturnsCorrectStatus() {
            // given
            LegacyProductGroupDetailResult result =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            // when
            LegacyProductDetailApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.productGroup().productStatus()).isNotNull();
            assertThat(response.productGroup().productStatus().soldOutYn()).isEqualTo("N");
            assertThat(response.productGroup().productStatus().displayYn()).isEqualTo("Y");
        }
    }
}
