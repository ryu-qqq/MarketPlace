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
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupExcelApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.response.ProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper.ProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupExcelCompositeResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupPageResult;
import com.ryuqq.marketplace.application.productgroup.port.in.query.GetProductGroupUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.query.SearchProductGroupForExcelUseCase;
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
import org.springframework.restdocs.payload.JsonFieldType;
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
    @MockitoBean private SearchProductGroupForExcelUseCase searchProductGroupForExcelUseCase;
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
                                                            "상태 필터 (DRAFT, ACTIVE, INACTIVE,"
                                                                    + " SOLDOUT, DELETED)")
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
                                                    .description(
                                                            "검색 필드 (NAME, CATEGORY_NAME,"
                                                                    + " BRAND_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("조회 시작일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("조회 종료일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT, UPDATED_AT,"
                                                                    + " NAME). 기본값: CREATED_AT")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC). 기본값: DESC")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품 그룹 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data.content[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.content[].brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.content[].categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.content[].categoryName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리명"),
                                            fieldWithPath("data.content[].categoryDisplayPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath("data.content[].categoryIdPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 ID 경로 (예: 1/5/23)"),
                                            fieldWithPath("data.content[].categoryDepth")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 깊이"),
                                            fieldWithPath("data.content[].department")
                                                    .type(JsonFieldType.STRING)
                                                    .description("부서 (MEN, WOMEN 등)"),
                                            fieldWithPath("data.content[].categoryGroup")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 그룹"),
                                            fieldWithPath("data.content[].productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("data.content[].optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "옵션 유형 (NONE, SINGLE, COMBINATION)"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "상태 (DRAFT, ACTIVE, INACTIVE,"
                                                                    + " SOLDOUT, DELETED)"),
                                            fieldWithPath("data.content[].thumbnailUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("썸네일 이미지 URL"),
                                            fieldWithPath("data.content[].productCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 수량"),
                                            fieldWithPath("data.content[].minPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최저 판매가"),
                                            fieldWithPath("data.content[].maxPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최고 판매가"),
                                            fieldWithPath("data.content[].maxDiscountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최대 할인율"),
                                            fieldWithPath("data.content[].optionGroups[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 그룹 요약 목록"),
                                            fieldWithPath(
                                                            "data.content[].optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.content[].optionGroups[].optionValueNames[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.content[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
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
                                            fieldWithPath("data.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.categoryName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리명"),
                                            fieldWithPath("data.categoryDisplayPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath("data.categoryIdPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 ID 경로 (예: 1/5/23)"),
                                            fieldWithPath("data.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("data.optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "옵션 유형 (NONE, SINGLE, COMBINATION)"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "상태 (DRAFT, ACTIVE, INACTIVE,"
                                                                    + " SOLDOUT, DELETED)"),
                                            fieldWithPath("data.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.images[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("data.images[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("이미지 ID"),
                                            fieldWithPath("data.images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("data.images[].uploadedUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드된 이미지 URL"),
                                            fieldWithPath("data.images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형 (THUMBNAIL, DETAIL)"),
                                            fieldWithPath("data.images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.optionProductMatrix")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("옵션-상품 매트릭스"),
                                            fieldWithPath("data.optionProductMatrix.optionGroups[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 그룹 목록"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].canonicalOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].inputType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "입력 유형 (PREDEFINED: 사전 정의,"
                                                                    + " FREE_INPUT: 자유 입력)"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 값 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].sellerOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].canonicalOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 값 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.optionGroups[].optionValues[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.optionProductMatrix.products[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품(SKU) 목록"),
                                            fieldWithPath("data.optionProductMatrix.products[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 판매가"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].discountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인율"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 상태"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품 옵션 목록"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].sellerOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].sellerOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 옵션 값 ID"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].options[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 등록일시"),
                                            fieldWithPath(
                                                            "data.optionProductMatrix.products[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 수정일시"),
                                            fieldWithPath("data.shippingPolicy")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("배송 정책")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.policyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 정책 ID")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.policyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정책명")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.defaultPolicy")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 정책 여부")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성 상태")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.shippingFeeType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송비 유형 코드")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.shippingPolicy.shippingFeeTypeDisplayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송비 유형명")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.baseFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("기본 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.freeThreshold")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("무료 배송 기준 금액")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.jejuExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("제주 추가 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.islandExtraFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("도서 산간 추가 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.returnFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.exchangeFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 배송비")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.leadTimeMinDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최소 배송 소요일")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.leadTimeMaxDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최대 배송 소요일")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.leadTimeCutoffTime")
                                                    .type(JsonFieldType.STRING)
                                                    .description("마감 시간 (HH:mm)")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시")
                                                    .optional(),
                                            fieldWithPath("data.shippingPolicy.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("환불 정책")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.policyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 정책 ID")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.policyName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("정책명")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.defaultPolicy")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("기본 정책 여부")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.active")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("활성 상태")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.returnPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 가능 일수")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.exchangePeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 가능 일수")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.refundPolicy.nonReturnableConditions[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("반품 불가 조건 목록")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.refundPolicy.nonReturnableConditions[].code")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조건 코드")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.refundPolicy.nonReturnableConditions[].displayName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("조건명")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.partialRefundEnabled")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("부분 환불 가능 여부")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.inspectionRequired")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("검수 필요 여부")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.inspectionPeriodDays")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("검수 기간 (일)")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.additionalInfo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("추가 정보")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시")
                                                    .optional(),
                                            fieldWithPath("data.refundPolicy.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시")
                                                    .optional(),
                                            fieldWithPath("data.description")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("상품 상세설명")
                                                    .optional(),
                                            fieldWithPath("data.description.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상세설명 ID")
                                                    .optional(),
                                            fieldWithPath("data.description.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세설명 내용")
                                                    .optional(),
                                            fieldWithPath("data.description.cdnPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("CDN 경로")
                                                    .optional(),
                                            fieldWithPath("data.description.images[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상세설명 이미지 목록")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("이미지 ID")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 URL")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].uploadedUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드 URL")
                                                    .optional(),
                                            fieldWithPath("data.description.images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서")
                                                    .optional(),
                                            fieldWithPath("data.productNotice")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("상품 고시정보")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시정보 ID")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.noticeCategoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 카테고리 ID")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.entries[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시 항목 목록")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.entries[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("항목 ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data.productNotice.entries[].noticeFieldId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 필드 ID")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("필드 값")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시")
                                                    .optional(),
                                            fieldWithPath("data.productNotice.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("상품 그룹 엑셀 다운로드 조회 API")
    class SearchProductGroupsForExcelTest {

        @Test
        @DisplayName("유효한 요청이면 200과 엑셀 데이터를 반환한다")
        void searchForExcel_ValidRequest_Returns200WithExcelData() throws Exception {
            // given
            List<ProductGroupExcelCompositeResult> excelResults =
                    ProductGroupApiFixtures.productGroupExcelResults(2);
            List<ProductGroupExcelApiResponse> excelResponses =
                    List.of(
                            ProductGroupApiFixtures.productGroupExcelApiResponse(1L),
                            ProductGroupApiFixtures.productGroupExcelApiResponse(2L));

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchProductGroupForExcelUseCase.execute(any())).willReturn(excelResults);
            given(mapper.toExcelResponses(any())).willReturn(excelResponses);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            BASE_URL + ProductGroupAdminEndpoints.EXCEL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2))
                    .andExpect(jsonPath("$.data[0].images").isArray())
                    .andExpect(jsonPath("$.data[0].products").isArray())
                    .andDo(
                            document(
                                    "product-group/search-for-excel",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description(
                                                            "상태 필터 (DRAFT, ACTIVE, INACTIVE,"
                                                                    + " SOLDOUT, DELETED)")
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
                                                    .description(
                                                            "검색 필드 (NAME, CATEGORY_NAME,"
                                                                    + " BRAND_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("조회 시작일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("조회 종료일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description("정렬 키")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("엑셀 다운로드용 상품 그룹 목록"),
                                            fieldWithPath("data[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data[].sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data[].brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data[].brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data[].categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data[].categoryName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리명"),
                                            fieldWithPath("data[].categoryDisplayPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath("data[].categoryIdPath")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 ID 경로"),
                                            fieldWithPath("data[].categoryDepth")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 깊이"),
                                            fieldWithPath("data[].department")
                                                    .type(JsonFieldType.STRING)
                                                    .description("부서"),
                                            fieldWithPath("data[].categoryGroup")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 그룹"),
                                            fieldWithPath("data[].productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("data[].optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 유형"),
                                            fieldWithPath("data[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태"),
                                            fieldWithPath("data[].thumbnailUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("썸네일 URL"),
                                            fieldWithPath("data[].productCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 수"),
                                            fieldWithPath("data[].minPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최저가"),
                                            fieldWithPath("data[].maxPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최고가"),
                                            fieldWithPath("data[].maxDiscountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("최대 할인율"),
                                            fieldWithPath("data[].optionGroups[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 그룹 요약"),
                                            fieldWithPath("data[].optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data[].optionGroups[].optionValueNames[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
                                            fieldWithPath("data[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data[].images[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("data[].images[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("이미지 ID"),
                                            fieldWithPath("data[].images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 URL"),
                                            fieldWithPath("data[].images[].uploadedUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("업로드 URL"),
                                            fieldWithPath("data[].images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형"),
                                            fieldWithPath("data[].images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data[].products[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품(SKU) 목록"),
                                            fieldWithPath("data[].products[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data[].products[].productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 그룹 ID"),
                                            fieldWithPath("data[].products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath("data[].products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("data[].products[].currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가"),
                                            fieldWithPath("data[].products[].salePrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인가"),
                                            fieldWithPath("data[].products[].discountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인율"),
                                            fieldWithPath("data[].products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("data[].products[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태"),
                                            fieldWithPath("data[].products[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data[].products[].optionMappings[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 매핑 목록"),
                                            fieldWithPath("data[].products[].optionMappings[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("매핑 ID"),
                                            fieldWithPath(
                                                            "data[].products[].optionMappings[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath(
                                                            "data[].products[].optionMappings[].sellerOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 옵션값 ID"),
                                            fieldWithPath(
                                                            "data[].products[].optionMappings[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명")
                                                    .optional(),
                                            fieldWithPath(
                                                            "data[].products[].optionMappings[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명")
                                                    .optional(),
                                            fieldWithPath("data[].products[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 등록일시"),
                                            fieldWithPath("data[].products[].updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 수정일시"),
                                            fieldWithPath("data[].descriptionCdnUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세설명 CDN URL"),
                                            fieldWithPath("data[].notice")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("고시정보"),
                                            fieldWithPath("data[].notice.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시정보 ID"),
                                            fieldWithPath("data[].notice.noticeCategoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 카테고리 ID"),
                                            fieldWithPath("data[].notice.entries[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시 항목 목록"),
                                            fieldWithPath("data[].notice.entries[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("항목 ID"),
                                            fieldWithPath("data[].notice.entries[].noticeFieldId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 필드 ID"),
                                            fieldWithPath("data[].notice.entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("필드 값"),
                                            fieldWithPath("data[].notice.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data[].notice.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 목록을 반환한다")
        void searchForExcel_EmptyResult_Returns200WithEmptyList() throws Exception {
            // given
            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchProductGroupForExcelUseCase.execute(any())).willReturn(List.of());
            given(mapper.toExcelResponses(any())).willReturn(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + ProductGroupAdminEndpoints.EXCEL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isEmpty());
        }
    }
}
