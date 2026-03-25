package com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.query.SearchProductGroupsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupExcelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupListApiResponse;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.query.ProductGroupSearchParams;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupExcelPageResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupQueryApiMapper 단위 테스트")
class ProductGroupQueryApiMapperTest {

    private ProductGroupQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupQueryApiMapper();
    }

    @Nested
    @DisplayName("toSearchParams() - 검색 파라미터 변환")
    class ToSearchParamsTest {

        @Test
        @DisplayName("검색 요청을 ProductGroupSearchParams로 변환한다")
        void toSearchParams_ConvertsRequestToSearchParams() {
            // given
            List<String> statuses = List.of("ACTIVE", "PENDING");
            List<Long> sellerIds = List.of(1L, 2L);
            List<Long> brandIds = List.of(100L);
            List<Long> categoryIds = List.of(1000L);
            SearchProductGroupsApiRequest request =
                    new SearchProductGroupsApiRequest(
                            statuses,
                            sellerIds,
                            brandIds,
                            categoryIds,
                            null,
                            "NAME",
                            "테스트",
                            null,
                            null,
                            "createdAt",
                            "DESC",
                            0,
                            20);

            // when
            ProductGroupSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.statuses()).isEqualTo(statuses);
            assertThat(params.sellerIds()).isEqualTo(sellerIds);
            assertThat(params.brandIds()).isEqualTo(brandIds);
            assertThat(params.categoryIds()).isEqualTo(categoryIds);
            assertThat(params.searchField()).isEqualTo("NAME");
            assertThat(params.searchWord()).isEqualTo("테스트");
            assertThat(params.searchParams().page()).isEqualTo(0);
            assertThat(params.searchParams().size()).isEqualTo(20);
        }

        @Test
        @DisplayName("페이지 파라미터가 null이면 기본값을 사용한다")
        void toSearchParams_NullPageParams_UsesDefaults() {
            // given
            SearchProductGroupsApiRequest request =
                    new SearchProductGroupsApiRequest(
                            null, null, null, null, null, null, null, null, null, null, null, null,
                            null);

            // when
            ProductGroupSearchParams params = mapper.toSearchParams(request);

            // then
            assertThat(params.searchParams().page()).isEqualTo(0);
            assertThat(params.searchParams().size()).isEqualTo(20);
        }
    }

    @Nested
    @DisplayName("toPageResponse() - 페이지 응답 변환")
    class ToPageResponseTest {

        @Test
        @DisplayName("ProductGroupPageResult를 PageApiResponse로 변환한다")
        void toPageResponse_ConvertsPageResultToPageResponse() {
            // given
            ProductGroupPageResult pageResult = ProductGroupApiFixtures.pageResult(3, 0, 20);

            // when
            PageApiResponse<ProductGroupListApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(3);
            assertThat(response.page()).isEqualTo(0);
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(3);
            assertThat(response.first()).isTrue();
            assertThat(response.last()).isTrue();
        }

        @Test
        @DisplayName("빈 결과를 빈 PageApiResponse로 변환한다")
        void toPageResponse_EmptyResult_ReturnsEmptyPage() {
            // given
            ProductGroupPageResult emptyResult = ProductGroupApiFixtures.emptyPageResult();

            // when
            PageApiResponse<ProductGroupListApiResponse> response =
                    mapper.toPageResponse(emptyResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("목록 응답의 날짜 필드가 ISO 8601 포맷으로 변환된다")
        void toPageResponse_DateFieldsFormattedAsIso8601() {
            // given
            ProductGroupPageResult pageResult = ProductGroupApiFixtures.pageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupListApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            ProductGroupListApiResponse first = response.content().get(0);
            assertThat(first.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
            assertThat(first.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
        }

        @Test
        @DisplayName("옵션 그룹 요약이 정확히 변환된다")
        void toPageResponse_OptionGroupSummaryConverted() {
            // given
            ProductGroupPageResult pageResult = ProductGroupApiFixtures.pageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupListApiResponse> response =
                    mapper.toPageResponse(pageResult);

            // then
            ProductGroupListApiResponse first = response.content().get(0);
            assertThat(first.optionGroups()).isNotEmpty();
            assertThat(first.optionGroups().get(0).optionGroupName()).isNotBlank();
            assertThat(first.optionGroups().get(0).optionValueNames()).isNotEmpty();
        }
    }

    @Nested
    @DisplayName("toDetailResponse() - 상세 응답 변환")
    class ToDetailResponseTest {

        @Test
        @DisplayName("ProductGroupDetailCompositeResult를 ProductGroupDetailApiResponse로 변환한다")
        void toDetailResponse_ConvertsDetailResultToDetailResponse() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResult(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.id()).isEqualTo(result.id());
            assertThat(response.sellerId()).isEqualTo(result.sellerId());
            assertThat(response.sellerName()).isEqualTo(result.sellerName());
            assertThat(response.brandId()).isEqualTo(result.brandId());
            assertThat(response.brandName()).isEqualTo(result.brandName());
            assertThat(response.categoryId()).isEqualTo(result.categoryId());
            assertThat(response.categoryName()).isEqualTo(result.categoryName());
            assertThat(response.productGroupName()).isEqualTo(result.productGroupName());
            assertThat(response.optionType()).isEqualTo(result.optionType());
            assertThat(response.status()).isEqualTo(result.status());
        }

        @Test
        @DisplayName("상세 응답의 날짜 필드가 ISO 8601 포맷으로 변환된다")
        void toDetailResponse_DateFieldsFormattedAsIso8601() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResult(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.createdAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
            assertThat(response.updatedAt()).matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
        }

        @Test
        @DisplayName("이미지 목록이 정확히 변환된다")
        void toDetailResponse_ImagesConverted() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResult(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.images()).hasSize(result.images().size());
            assertThat(response.images().get(0).id()).isEqualTo(result.images().get(0).id());
            assertThat(response.images().get(0).imageType())
                    .isEqualTo(result.images().get(0).imageType());
        }

        @Test
        @DisplayName("옵션-상품 매트릭스가 정확히 변환된다")
        void toDetailResponse_OptionMatrixConverted() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResult(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.optionProductMatrix()).isNotNull();
            assertThat(response.optionProductMatrix().optionGroups()).isNotEmpty();
            assertThat(response.optionProductMatrix().products()).isNotEmpty();
        }

        @Test
        @DisplayName("배송 정책이 null이면 null로 변환된다")
        void toDetailResponse_NullShippingPolicy_ReturnsNull() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResultMinimal(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.shippingPolicy()).isNull();
        }

        @Test
        @DisplayName("환불 정책이 null이면 null로 변환된다")
        void toDetailResponse_NullRefundPolicy_ReturnsNull() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResultMinimal(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.refundPolicy()).isNull();
        }

        @Test
        @DisplayName("상품 설명이 null이면 null로 변환된다")
        void toDetailResponse_NullDescription_ReturnsNull() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResultMinimal(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.description()).isNull();
        }

        @Test
        @DisplayName("상품 고시정보가 null이면 null로 변환된다")
        void toDetailResponse_NullProductNotice_ReturnsNull() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResultMinimal(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.productNotice()).isNull();
        }

        @Test
        @DisplayName("배송 정책의 시간 필드가 HH:mm 포맷으로 변환된다")
        void toDetailResponse_ShippingPolicyTimeFormatted() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResult(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.shippingPolicy()).isNotNull();
            assertThat(response.shippingPolicy().leadTimeCutoffTime()).matches("\\d{2}:\\d{2}");
        }

        @Test
        @DisplayName("상품 상세의 날짜가 ISO 8601 포맷으로 변환된다")
        void toDetailResponse_ProductDetailDatesFormatted() {
            // given
            ProductGroupDetailCompositeResult result =
                    ProductGroupApiFixtures.productGroupDetailResult(1L);

            // when
            ProductGroupDetailApiResponse response = mapper.toDetailResponse(result);

            // then
            assertThat(response.optionProductMatrix().products()).isNotEmpty();
            assertThat(response.optionProductMatrix().products().get(0).createdAt())
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
        }
    }

    @Nested
    @DisplayName("toExcelPageResponse() - 엑셀 다운로드 페이지 응답 변환")
    class ToExcelPageResponseTest {

        @Test
        @DisplayName("ProductGroupExcelPageResult를 PageApiResponse로 변환한다")
        void toExcelPageResponse_ConvertsPageResultToPageResponse() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(2, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            assertThat(response.content()).hasSize(2);
            assertThat(response.content().get(0).id()).isEqualTo(1L);
            assertThat(response.content().get(1).id()).isEqualTo(2L);
            assertThat(response.page()).isZero();
            assertThat(response.size()).isEqualTo(20);
            assertThat(response.totalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("기본 목록 필드가 정확히 변환된다")
        void toExcelPageResponse_BaseFieldsConverted() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            ProductGroupExcelApiResponse excelResponse = response.content().get(0);
            assertThat(excelResponse.sellerId())
                    .isEqualTo(ProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(excelResponse.sellerName())
                    .isEqualTo(ProductGroupApiFixtures.DEFAULT_SELLER_NAME);
            assertThat(excelResponse.brandId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(excelResponse.brandName())
                    .isEqualTo(ProductGroupApiFixtures.DEFAULT_BRAND_NAME);
            assertThat(excelResponse.categoryId())
                    .isEqualTo(ProductGroupApiFixtures.DEFAULT_CATEGORY_ID);
            assertThat(excelResponse.optionType())
                    .isEqualTo(ProductGroupApiFixtures.DEFAULT_OPTION_TYPE);
            assertThat(excelResponse.status()).isEqualTo(ProductGroupApiFixtures.DEFAULT_STATUS);
        }

        @Test
        @DisplayName("이미지 목록이 정확히 변환된다")
        void toExcelPageResponse_ImagesConverted() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).images()).hasSize(2);
            assertThat(response.content().get(0).images().get(0).imageType())
                    .isEqualTo("THUMBNAIL");
        }

        @Test
        @DisplayName("상품(SKU) 목록이 정확히 변환된다")
        void toExcelPageResponse_ProductsConverted() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).products()).hasSize(2);
            assertThat(response.content().get(0).products().get(0).skuCode()).isEqualTo("SKU-001");
            assertThat(response.content().get(0).products().get(0).optionMappings()).hasSize(1);
        }

        @Test
        @DisplayName("상세설명 CDN URL이 변환된다")
        void toExcelPageResponse_DescriptionCdnUrlConverted() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).descriptionCdnUrl())
                    .isEqualTo("https://cdn.example.com/description/");
        }

        @Test
        @DisplayName("고시정보가 정확히 변환된다")
        void toExcelPageResponse_NoticeConverted() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            assertThat(response.content().get(0).notice()).isNotNull();
            assertThat(response.content().get(0).notice().entries()).hasSize(2);
        }

        @Test
        @DisplayName("날짜 필드가 ISO 8601 포맷으로 변환된다")
        void toExcelPageResponse_DateFieldsFormattedAsIso8601() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupApiFixtures.excelPageResult(1, 0, 20);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            ProductGroupExcelApiResponse excelResponse = response.content().get(0);
            assertThat(excelResponse.createdAt())
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
            assertThat(excelResponse.updatedAt())
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
            assertThat(excelResponse.products().get(0).createdAt())
                    .matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.*");
        }

        @Test
        @DisplayName("이미지/상품/고시정보가 null이면 빈 목록 또는 null로 변환된다")
        void toExcelPageResponse_MinimalResult_EmptyOrNullFields() {
            // given
            ProductGroupExcelPageResult pageResult =
                    ProductGroupExcelPageResult.of(
                            List.of(ProductGroupApiFixtures.productGroupExcelResultMinimal(1L)),
                            0,
                            20,
                            1L);

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            ProductGroupExcelApiResponse excelResponse = response.content().get(0);
            assertThat(excelResponse.images()).isEmpty();
            assertThat(excelResponse.products()).isEmpty();
            assertThat(excelResponse.descriptionCdnUrl()).isNull();
            assertThat(excelResponse.notice()).isNull();
        }

        @Test
        @DisplayName("빈 결과이면 빈 content와 페이지 정보를 반환한다")
        void toExcelPageResponse_EmptyResults_ReturnsEmptyPage() {
            // given
            ProductGroupExcelPageResult pageResult = ProductGroupApiFixtures.emptyExcelPageResult();

            // when
            PageApiResponse<ProductGroupExcelApiResponse> response =
                    mapper.toExcelPageResponse(pageResult);

            // then
            assertThat(response.content()).isEmpty();
            assertThat(response.totalElements()).isZero();
        }
    }
}
