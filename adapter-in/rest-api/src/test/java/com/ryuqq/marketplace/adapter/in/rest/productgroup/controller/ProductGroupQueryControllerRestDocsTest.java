package com.ryuqq.marketplace.adapter.in.rest.productgroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper.ProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupByOffsetUseCase;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(ProductGroupQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductGroupQueryController REST Docs 테스트")
class ProductGroupQueryControllerRestDocsTest {

    private static final String BASE_URL = ProductGroupAdminEndpoints.PRODUCT_GROUPS;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchProductGroupByOffsetUseCase searchProductGroupByOffsetUseCase;
    @MockitoBean private GetProductGroupUseCase getProductGroupUseCase;
    @MockitoBean private ProductGroupQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 목록 검색 API")
    class SearchProductGroupsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchProductGroups_ValidRequest_Returns200WithPage() throws Exception {
            // given
            ProductGroupPageResult pageResult = ProductGroupApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<ProductGroupListApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(
                                    ProductGroupApiFixtures.productGroupListApiResponse(1L),
                                    ProductGroupApiFixtures.productGroupListApiResponse(2L),
                                    ProductGroupApiFixtures.productGroupListApiResponse(3L)),
                            0,
                            20,
                            3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchProductGroupByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ProductGroupPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "product-group/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description(
                                                            "상태 필터 (PENDING, ACTIVE, INACTIVE,"
                                                                    + " DELETED)")
                                                    .optional(),
                                            parameterWithName("sellerIds")
                                                    .description("셀러 ID 목록")
                                                    .optional(),
                                            parameterWithName("brandIds")
                                                    .description("브랜드 ID 목록")
                                                    .optional(),
                                            parameterWithName("categoryIds")
                                                    .description("카테고리 ID 목록")
                                                    .optional(),
                                            parameterWithName("productGroupIds")
                                                    .description("상품 그룹 ID 목록")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description("검색 필드 (NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 키")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]").description("상품 그룹 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data.content[].sellerId")
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].sellerName")
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].brandId")
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.content[].brandName")
                                                    .description("브랜드명"),
                                            fieldWithPath("data.content[].categoryId")
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.content[].categoryName")
                                                    .description("카테고리명"),
                                            fieldWithPath("data.content[].categoryDisplayPath")
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath("data.content[].categoryDepth")
                                                    .description("카테고리 깊이"),
                                            fieldWithPath("data.content[].department")
                                                    .description("부서 (MEN, WOMEN 등)"),
                                            fieldWithPath("data.content[].categoryGroup")
                                                    .description("카테고리 그룹"),
                                            fieldWithPath("data.content[].productGroupName")
                                                    .description("상품 그룹명"),
                                            fieldWithPath("data.content[].optionType")
                                                    .description("옵션 유형 (SINGLE, COMBINATION)"),
                                            fieldWithPath("data.content[].status")
                                                    .description(
                                                            "상태 (PENDING, ACTIVE, INACTIVE,"
                                                                    + " DELETED)"),
                                            fieldWithPath("data.content[].thumbnailUrl")
                                                    .description("썸네일 이미지 URL"),
                                            fieldWithPath("data.content[].productCount")
                                                    .description("상품 수량"),
                                            fieldWithPath("data.content[].minPrice")
                                                    .description("최저 판매가"),
                                            fieldWithPath("data.content[].maxPrice")
                                                    .description("최고 판매가"),
                                            fieldWithPath("data.content[].maxDiscountRate")
                                                    .description("최대 할인율"),
                                            fieldWithPath("data.content[].optionGroups[]")
                                                    .description("옵션 그룹 요약 목록"),
                                            fieldWithPath(
                                                            "data.content[].optionGroups[].optionGroupName")
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.content[].optionGroups[].optionValueNames[]")
                                                    .description("옵션 값 목록"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .description("등록일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .description("수정일시"),
                                            fieldWithPath("data.page").description("현재 페이지 번호"),
                                            fieldWithPath("data.size").description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first").description("첫 페이지 여부"),
                                            fieldWithPath("data.last").description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchProductGroups_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ProductGroupPageResult emptyResult = ProductGroupApiFixtures.emptyPageResult();
            PageApiResponse<ProductGroupListApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchProductGroupByOffsetUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(ProductGroupPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("상품 그룹 상세 조회 API")
    class GetProductGroupDetailTest {

        @Test
        @DisplayName("유효한 ID로 조회하면 200과 상세 응답을 반환한다")
        void getProductGroup_ValidId_Returns200() throws Exception {
            // given
            Long productGroupId = 1L;
            ProductGroupDetailCompositeResult detailResult =
                    ProductGroupApiFixtures.productGroupDetailResult(productGroupId);
            ProductGroupDetailApiResponse detailResponse =
                    ProductGroupApiFixtures.productGroupDetailApiResponse(productGroupId);

            given(getProductGroupUseCase.execute(productGroupId)).willReturn(detailResult);
            given(mapper.toDetailResponse(any(ProductGroupDetailCompositeResult.class)))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + "/{productGroupId}", productGroupId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.id").value(productGroupId))
                    .andExpect(jsonPath("$.data.images").isArray())
                    .andExpect(jsonPath("$.data.optionProductMatrix").exists())
                    .andDo(
                            document(
                                    "product-group/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    responseFields(
                                            fieldWithPath("data.id").description("상품 그룹 ID"),
                                            fieldWithPath("data.sellerId").description("셀러 ID"),
                                            fieldWithPath("data.sellerName").description("셀러명"),
                                            fieldWithPath("data.brandId").description("브랜드 ID"),
                                            fieldWithPath("data.brandName").description("브랜드명"),
                                            fieldWithPath("data.categoryId").description("카테고리 ID"),
                                            fieldWithPath("data.categoryName").description("카테고리명"),
                                            fieldWithPath("data.categoryDisplayPath")
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath("data.productGroupName")
                                                    .description("상품 그룹명"),
                                            fieldWithPath("data.optionType")
                                                    .description("옵션 유형 (SINGLE, COMBINATION)"),
                                            fieldWithPath("data.status")
                                                    .description(
                                                            "상태 (PENDING, ACTIVE, INACTIVE,"
                                                                    + " DELETED)"),
                                            fieldWithPath("data.createdAt").description("등록일시"),
                                            fieldWithPath("data.updatedAt").description("수정일시"),
                                            fieldWithPath("data.images[]").description("이미지 목록"),
                                            fieldWithPath("data.images[].id").description("이미지 ID"),
                                            fieldWithPath("data.images[].originUrl")
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("data.images[].uploadedUrl")
                                                    .description("업로드된 이미지 URL"),
                                            fieldWithPath("data.images[].imageType")
                                                    .description("이미지 유형 (MAIN, DETAIL 등)"),
                                            fieldWithPath("data.images[].sortOrder")
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.optionProductMatrix")
                                                    .description("옵션-상품 매트릭스"),
                                            fieldWithPath("data.optionProductMatrix.optionGroups[]")
                                                    .description("옵션 그룹 목록"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].id")
                                                    .description("옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionGroupName")
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].canonicalOptionGroupId")
                                                    .description("표준 옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].sortOrder")
                                                    .description("정렬 순서"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[]")
                                                    .description("옵션 값 목록"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].id")
                                                    .description("옵션 값 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].sellerOptionGroupId")
                                                    .description("셀러 옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].optionValueName")
                                                    .description("옵션 값명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].canonicalOptionValueId")
                                                    .description("표준 옵션 값 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].sortOrder")
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.optionProductMatrix.products[]")
                                                    .description("상품(SKU) 목록"),
                                            fieldWithPath("data.optionProductMatrix.products[].id")
                                                    .description("상품 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].skuCode")
                                                    .description("SKU 코드"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].regularPrice")
                                                    .description("정상가"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].currentPrice")
                                                    .description("현재 판매가"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].salePrice")
                                                    .description("할인가"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].discountRate")
                                                    .description("할인율"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].stockQuantity")
                                                    .description("재고 수량"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].status")
                                                    .description("상품 상태"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].sortOrder")
                                                    .description("정렬 순서"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[]")
                                                    .description("상품 옵션 목록"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].sellerOptionGroupId")
                                                    .description("셀러 옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].optionGroupName")
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].sellerOptionValueId")
                                                    .description("셀러 옵션 값 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].optionValueName")
                                                    .description("옵션 값명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].createdAt")
                                                    .description("상품 등록일시"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].updatedAt")
                                                    .description("상품 수정일시"),
                                            fieldWithPath("data.shippingPolicy")
                                                    .description("배송 정책")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.policyId")
                                                    .description("배송 정책 ID")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.sellerId")
                                                    .description("셀러 ID")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.policyName")
                                                    .description("정책명")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.defaultPolicy")
                                                    .description("기본 정책 여부")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.active")
                                                    .description("활성 상태")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.shippingFeeType")
                                                    .description("배송비 유형 코드")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.shippingPolicy.shippingFeeTypeDisplayName")
                                                    .description("배송비 유형명")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.baseFee")
                                                    .description("기본 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.freeThreshold")
                                                    .description("무료 배송 기준 금액")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.jejuExtraFee")
                                                    .description("제주 추가 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.islandExtraFee")
                                                    .description("도서 산간 추가 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.returnFee")
                                                    .description("반품 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.exchangeFee")
                                                    .description("교환 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.leadTimeMinDays")
                                                    .description("최소 배송 소요일")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.leadTimeMaxDays")
                                                    .description("최대 배송 소요일")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.leadTimeCutoffTime")
                                                    .description("마감 시간 (HH:mm)")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.createdAt")
                                                    .description("등록일시")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.updatedAt")
                                                    .description("수정일시")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy")
                                                    .description("환불 정책")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.policyId")
                                                    .description("환불 정책 ID")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.sellerId")
                                                    .description("셀러 ID")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.policyName")
                                                    .description("정책명")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.defaultPolicy")
                                                    .description("기본 정책 여부")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.active")
                                                    .description("활성 상태")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.returnPeriodDays")
                                                    .description("반품 가능 일수")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.exchangePeriodDays")
                                                    .description("교환 가능 일수")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.refundPolicy.nonReturnableConditions[]")
                                                    .description("반품 불가 조건 목록")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.refundPolicy.nonReturnableConditions[].code")
                                                    .description("조건 코드")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.refundPolicy.nonReturnableConditions[].displayName")
                                                    .description("조건명")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.partialRefundEnabled")
                                                    .description("부분 환불 가능 여부")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.inspectionRequired")
                                                    .description("검수 필요 여부")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.inspectionPeriodDays")
                                                    .description("검수 기간 (일)")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.additionalInfo")
                                                    .description("추가 정보")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.createdAt")
                                                    .description("등록일시")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.updatedAt")
                                                    .description("수정일시")
                                                    .optional(),
                                            fieldWithPath("data.description")
                                                    .description("상품 상세설명")
                                                    .optional(),
                                            fieldWithPath("data.description.id")
                                                    .description("상세설명 ID")
                                                    .optional(),
                                            fieldWithPath("data.description.content")
                                                    .description("상세설명 내용")
                                                    .optional(),
                                            fieldWithPath("data.description.cdnPath")
                                                    .description("CDN 경로")
                                                    .optional(),
                                            fieldWithPath("data.description.images[]")
                                                    .description("상세설명 이미지 목록")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].id")
                                                    .description("이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].originUrl")
                                                    .description("원본 URL")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].uploadedUrl")
                                                    .description("업로드 URL")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].sortOrder")
                                                    .description("정렬 순서")
                                                    .optional(),
                                            fieldWithPath("data.productNotice")
                                                    .description("상품 고시정보")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.id")
                                                    .description("고시정보 ID")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.noticeCategoryId")
                                                    .description("고시 카테고리 ID")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.entries[]")
                                                    .description("고시 항목 목록")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.entries[].id")
                                                    .description("항목 ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.productNotice.entries[].noticeFieldId")
                                                    .description("고시 필드 ID")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.entries[].fieldValue")
                                                    .description("필드 값")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.createdAt")
                                                    .description("등록일시")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.updatedAt")
                                                    .description("수정일시")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
