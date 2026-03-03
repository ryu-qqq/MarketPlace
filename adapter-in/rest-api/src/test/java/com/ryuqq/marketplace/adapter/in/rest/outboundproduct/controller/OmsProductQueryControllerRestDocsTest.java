package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.OmsProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncHistoryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsProductQueryApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductDetailResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.OmsProductPageResult;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.SyncHistoryPageResult;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.GetOmsProductDetailUseCase;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchOmsProductUseCase;
import com.ryuqq.marketplace.application.outboundproduct.port.in.query.SearchSyncHistoryUseCase;
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
@WebMvcTest(OmsProductQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OmsProductQueryController REST Docs 테스트")
class OmsProductQueryControllerRestDocsTest {

    private static final String PRODUCTS_URL = OmsEndpoints.PRODUCTS;
    private static final long PRODUCT_GROUP_ID = 125694305L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private SearchOmsProductUseCase searchOmsProductUseCase;
    @MockitoBean private GetOmsProductDetailUseCase getOmsProductDetailUseCase;
    @MockitoBean private SearchSyncHistoryUseCase searchSyncHistoryUseCase;
    @MockitoBean private OmsProductQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("OMS 상품 목록 조회 API")
    class SearchProductsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchProducts_ValidRequest_Returns200WithPage() throws Exception {
            // given
            OmsProductPageResult pageResult = OmsApiFixtures.productPageResult(3, 0, 10);
            PageApiResponse<OmsProductApiResponse> pageResponse =
                    PageApiResponse.of(OmsApiFixtures.productApiResponses(3), 0, 10, 3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsProductUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toProductPageResponse(any(OmsProductPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(PRODUCTS_URL)
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(10))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "oms-product/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("dateType")
                                                    .description("날짜 필터 대상 (CREATED_AT/UPDATED_AT)")
                                                    .optional(),
                                            parameterWithName("startDate")
                                                    .description("조회 시작일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("endDate")
                                                    .description("조회 종료일 (yyyy-MM-dd)")
                                                    .optional(),
                                            parameterWithName("statuses")
                                                    .description(
                                                            "상품 상태 필터 (ACTIVE/INACTIVE/SOLDOUT)")
                                                    .optional(),
                                            parameterWithName("syncStatuses")
                                                    .description(
                                                            "연동 상태 필터 (SUCCESS/FAILED/PENDING)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드"
                                                                + " (productCode/productName/partnerName)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("shopIds")
                                                    .description("쇼핑몰 ID 목록")
                                                    .optional(),
                                            parameterWithName("partnerIds")
                                                    .description("파트너(셀러) ID 목록")
                                                    .optional(),
                                            parameterWithName("productCodes")
                                                    .description("상품 코드 목록")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 키 (CREATED_AT/UPDATED_AT/PRODUCT_GROUP_NAME)")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC/DESC)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.content[].productCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 코드"),
                                            fieldWithPath("data.content[].productName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            fieldWithPath("data.content[].imageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표 이미지 URL"),
                                            fieldWithPath("data.content[].price")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("대표 가격"),
                                            fieldWithPath("data.content[].stock")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 재고"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 상태"),
                                            fieldWithPath("data.content[].statusLabel")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 상태 라벨"),
                                            fieldWithPath("data.content[].partnerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("파트너(셀러)명"),
                                            fieldWithPath("data.content[].createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일"),
                                            fieldWithPath("data.content[].syncStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("연동 상태"),
                                            fieldWithPath("data.content[].syncStatusLabel")
                                                    .type(JsonFieldType.STRING)
                                                    .description("연동 상태 라벨"),
                                            fieldWithPath("data.content[].lastSyncAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("마지막 연동일"),
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
        @DisplayName("상태 필터와 연동 상태 필터를 함께 사용할 수 있다")
        void searchProducts_WithFilters_Returns200() throws Exception {
            // given
            OmsProductPageResult pageResult = OmsApiFixtures.productPageResult(1, 0, 10);
            PageApiResponse<OmsProductApiResponse> pageResponse =
                    PageApiResponse.of(List.of(OmsApiFixtures.productApiResponse(1L)), 0, 10, 1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsProductUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toProductPageResponse(any(OmsProductPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(PRODUCTS_URL)
                                    .param("statuses", "ACTIVE")
                                    .param("syncStatuses", "FAILED")
                                    .param("searchField", "productName")
                                    .param("searchWord", "나이키")
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchProducts_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            OmsProductPageResult emptyResult = OmsApiFixtures.emptyProductPageResult();
            PageApiResponse<OmsProductApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 10, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(searchOmsProductUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toProductPageResponse(any(OmsProductPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(PRODUCTS_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("OMS 상품 상세 조회 API")
    class GetProductDetailTest {

        @Test
        @DisplayName("상품그룹 ID로 상세 조회 시 200과 상세 응답을 반환한다")
        void getProductDetail_ValidId_Returns200WithDetail() throws Exception {
            // given
            OmsProductDetailResult detailResult =
                    OmsApiFixtures.productDetailResult(PRODUCT_GROUP_ID);
            OmsProductDetailApiResponse detailResponse =
                    OmsApiFixtures.productDetailApiResponse(PRODUCT_GROUP_ID);

            given(getOmsProductDetailUseCase.execute(PRODUCT_GROUP_ID)).willReturn(detailResult);
            given(mapper.toDetailResponse(any(OmsProductDetailResult.class)))
                    .willReturn(detailResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    PRODUCTS_URL + OmsEndpoints.PRODUCT_GROUP_ID, PRODUCT_GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.productGroup.productGroupId").value(PRODUCT_GROUP_ID))
                    .andExpect(
                            jsonPath("$.data.productGroup.productGroupName")
                                    .value("나이키 에어포스 1 '07 화이트"))
                    .andExpect(jsonPath("$.data.products").isArray())
                    .andExpect(jsonPath("$.data.syncSummary").exists())
                    .andDo(
                            document(
                                    "oms-product/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품그룹 ID")),
                                    responseFields(
                                            // ProductGroup
                                            fieldWithPath("data.productGroup.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.productGroup.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품그룹명"),
                                            fieldWithPath("data.productGroup.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.productGroup.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.productGroup.categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.productGroup.optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 타입"),
                                            fieldWithPath("data.productGroup.managementType")
                                                    .type(JsonFieldType.NULL)
                                                    .description("재고 관리 타입 (미지원)"),
                                            fieldWithPath("data.productGroup.brand.brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.productGroup.brand.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.productGroup.brand.brandNameKo")
                                                    .type(JsonFieldType.NULL)
                                                    .description("브랜드 한글명"),
                                            fieldWithPath("data.productGroup.price.regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("data.productGroup.price.currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가"),
                                            fieldWithPath("data.productGroup.price.salePrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가"),
                                            fieldWithPath(
                                                            "data.productGroup.price.directDiscountPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("즉시할인 금액"),
                                            fieldWithPath(
                                                            "data.productGroup.price.directDiscountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("즉시할인율"),
                                            fieldWithPath("data.productGroup.price.discountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인율"),
                                            fieldWithPath(
                                                            "data.productGroup.productGroupMainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표 이미지 URL"),
                                            fieldWithPath("data.productGroup.categoryFullName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 전체명"),
                                            fieldWithPath(
                                                            "data.productGroup.productStatus.soldOutYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품절 여부 (Y/N)"),
                                            fieldWithPath(
                                                            "data.productGroup.productStatus.displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("노출 여부 (Y/N)"),
                                            fieldWithPath("data.productGroup.insertDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일"),
                                            fieldWithPath("data.productGroup.updateDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일"),
                                            fieldWithPath("data.productGroup.insertOperator")
                                                    .type(JsonFieldType.NULL)
                                                    .description("등록자"),
                                            fieldWithPath("data.productGroup.updateOperator")
                                                    .type(JsonFieldType.NULL)
                                                    .description("수정자"),
                                            // Products
                                            fieldWithPath("data.products[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품(SKU) 목록"),
                                            fieldWithPath("data.products[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data.products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("data.products[].productStatus.soldOutYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품절 여부 (Y/N)"),
                                            fieldWithPath("data.products[].productStatus.displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("노출 여부 (Y/N)"),
                                            fieldWithPath("data.products[].option")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 요약"),
                                            fieldWithPath("data.products[].options[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 목록"),
                                            fieldWithPath("data.products[].options[].optionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 그룹 ID"),
                                            fieldWithPath(
                                                            "data.products[].options[].optionDetailId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 상세 ID"),
                                            fieldWithPath("data.products[].options[].optionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션명"),
                                            fieldWithPath("data.products[].options[].optionValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션값"),
                                            fieldWithPath("data.products[].additionalPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("추가 금액"),
                                            // SyncSummary
                                            fieldWithPath("data.syncSummary.totalSyncCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 연동 횟수"),
                                            fieldWithPath("data.syncSummary.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 횟수"),
                                            fieldWithPath("data.syncSummary.failCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 횟수"),
                                            fieldWithPath("data.syncSummary.pendingCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("대기 횟수"),
                                            fieldWithPath("data.syncSummary.lastSyncAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("마지막 연동일"),
                                            // Common
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("상품별 연동 이력 조회 API")
    class SearchSyncHistoryTest {

        @Test
        @DisplayName("유효한 요청이면 200과 연동 이력 페이지 응답을 반환한다")
        void searchSyncHistory_ValidRequest_Returns200WithPage() throws Exception {
            // given
            SyncHistoryPageResult pageResult = OmsApiFixtures.syncHistoryPageResult(3, 0, 10);
            PageApiResponse<SyncHistoryApiResponse> pageResponse =
                    PageApiResponse.of(OmsApiFixtures.syncHistoryApiResponses(3), 0, 10, 3);

            given(mapper.toSyncHistoryParams(anyLong(), any())).willReturn(null);
            given(searchSyncHistoryUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toSyncHistoryPageResponse(any(SyncHistoryPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            OmsEndpoints.SYNC_HISTORY, PRODUCT_GROUP_ID)
                                    .param("page", "0")
                                    .param("size", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "oms-product/search-sync-history",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품그룹 ID")),
                                    queryParameters(
                                            parameterWithName("status")
                                                    .description(
                                                            "상태 필터"
                                                                + " (PENDING/PROCESSING/COMPLETED/FAILED)")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("연동 이력 목록"),
                                            fieldWithPath("data.content[].id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("이력 ID"),
                                            fieldWithPath("data.content[].jobId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("작업 ID (SYNC-날짜-순번)"),
                                            fieldWithPath("data.content[].shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("쇼핑몰명"),
                                            fieldWithPath("data.content[].accountId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("계정 ID"),
                                            fieldWithPath("data.content[].presetName")
                                                    .type(JsonFieldType.NULL)
                                                    .description("프리셋명 (미지원)"),
                                            fieldWithPath("data.content[].status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태"),
                                            fieldWithPath("data.content[].statusLabel")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상태 라벨"),
                                            fieldWithPath("data.content[].requestedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청일시"),
                                            fieldWithPath("data.content[].completedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("완료일시"),
                                            fieldWithPath("data.content[].externalProductId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 ID"),
                                            fieldWithPath("data.content[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지"),
                                            fieldWithPath("data.content[].retryCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재시도 횟수"),
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
        @DisplayName("상태 필터를 사용할 수 있다")
        void searchSyncHistory_WithStatusFilter_Returns200() throws Exception {
            // given
            SyncHistoryPageResult pageResult = OmsApiFixtures.syncHistoryPageResult(1, 0, 10);
            PageApiResponse<SyncHistoryApiResponse> pageResponse =
                    PageApiResponse.of(
                            List.of(OmsApiFixtures.syncHistoryApiResponse(1L)), 0, 10, 1);

            given(mapper.toSyncHistoryParams(anyLong(), any())).willReturn(null);
            given(searchSyncHistoryUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toSyncHistoryPageResponse(any(SyncHistoryPageResult.class)))
                    .willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                            OmsEndpoints.SYNC_HISTORY, PRODUCT_GROUP_ID)
                                    .param("status", "COMPLETED"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchSyncHistory_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            SyncHistoryPageResult emptyResult = OmsApiFixtures.emptySyncHistoryPageResult();
            PageApiResponse<SyncHistoryApiResponse> emptyResponse =
                    PageApiResponse.of(List.of(), 0, 10, 0);

            given(mapper.toSyncHistoryParams(anyLong(), any())).willReturn(null);
            given(searchSyncHistoryUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toSyncHistoryPageResponse(any(SyncHistoryPageResult.class)))
                    .willReturn(emptyResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    OmsEndpoints.SYNC_HISTORY, PRODUCT_GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }
}
