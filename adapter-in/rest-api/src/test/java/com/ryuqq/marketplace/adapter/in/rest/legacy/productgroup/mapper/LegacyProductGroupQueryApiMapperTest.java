package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacySearchProductGroupByOffsetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse.LegacyProductGroupDetailItem;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.query.LegacyProductGroupSearchParams;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import java.util.List;
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
    @DisplayName("toSearchParams - 레거시 Request → 내부 SearchParams 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("기본 요청이 올바르게 변환된다")
        void toSearchParams_BasicRequest_ReturnsCorrectParams() {
            // given
            LegacySearchProductGroupByOffsetApiRequest request =
                    LegacyProductGroupApiFixtures.searchRequest();

            // when
            LegacyProductGroupSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.sellerId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(params.brandId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
        }

        @Test
        @DisplayName("필터 조건이 있는 요청이 올바르게 변환된다")
        void toSearchParams_WithFilters_ReturnsCorrectParams() {
            // given
            LegacySearchProductGroupByOffsetApiRequest request =
                    LegacyProductGroupApiFixtures.searchRequestWithFilters();

            // when
            LegacyProductGroupSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.categoryId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_CATEGORY_ID);
            assertThat(params.soldOutYn()).isEqualTo("N");
            assertThat(params.displayYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("sellerId가 null이면 null로 변환된다")
        void toSearchParams_NullSellerId_ReturnsNull() {
            // given
            LegacySearchProductGroupByOffsetApiRequest request =
                    new LegacySearchProductGroupByOffsetApiRequest(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null);

            // when
            LegacyProductGroupSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.sellerId()).isNull();
            assertThat(params.brandId()).isNull();
            assertThat(params.categoryId()).isNull();
        }

        @Test
        @DisplayName("page/size가 null이면 기본값으로 변환된다")
        void toSearchParams_NullPageSize_ReturnsDefaults() {
            // given
            LegacySearchProductGroupByOffsetApiRequest request =
                    new LegacySearchProductGroupByOffsetApiRequest(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null, null, null, null);

            // when
            LegacyProductGroupSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.page()).isZero();
            assertThat(params.size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toListResponse - 내부 PageResult → 레거시 목록 응답 변환")
    class ToListResponseTest {

        @Test
        @DisplayName("PageResult를 LegacyProductGroupListApiResponse로 변환한다")
        void toListResponse_ConvertsResult_ReturnsResponse() {
            // given
            LegacyProductGroupDetailResult detailResult =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            LegacyProductGroupPageResult pageResult =
                    LegacyProductGroupPageResult.of(List.of(detailResult), 1, 0, 20);

            // when
            LegacyProductGroupListApiResponse response = mapper.toListResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(1);
            assertThat(response.totalElements()).isEqualTo(1);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.number()).isZero();
            assertThat(response.first()).isTrue();
        }

        @Test
        @DisplayName("레거시 nested 구조(productGroup + products)로 변환된다")
        void toListResponse_ConvertsToNestedStructure_ReturnsCorrectFields() {
            // given
            LegacyProductGroupDetailResult detailResult =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            LegacyProductGroupPageResult pageResult =
                    LegacyProductGroupPageResult.of(List.of(detailResult), 1, 0, 20);

            // when
            LegacyProductGroupListApiResponse response = mapper.toListResponse(pageResult);

            // then
            LegacyProductGroupDetailItem item = response.content().getFirst();
            assertThat(item.productGroup()).isNotNull();
            assertThat(item.products()).isNotNull();
            assertThat(item.products()).hasSize(2);
            assertThat(item.productGroup().productGroupId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID);
            assertThat(item.productGroup().productGroupName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(item.productGroup().sellerId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(item.productGroup().brand().id())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(item.productGroup().brand().brandName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_BRAND_NAME);
        }
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
