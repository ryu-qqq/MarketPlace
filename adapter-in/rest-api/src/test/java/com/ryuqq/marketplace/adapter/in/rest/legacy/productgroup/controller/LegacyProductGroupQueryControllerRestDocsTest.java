package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.controller;

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
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductGroupListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.response.LegacyProductGroupPageResult;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacySearchProductGroupByOffsetUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
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
@WebMvcTest(LegacyProductGroupQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyProductGroupQueryController REST Docs 테스트")
class LegacyProductGroupQueryControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/legacy/product/group/{productGroupId}";
    private static final long PRODUCT_GROUP_ID =
            LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LegacyProductQueryUseCase legacyProductQueryUseCase;

    @MockitoBean
    private LegacySearchProductGroupByOffsetUseCase legacySearchProductGroupByOffsetUseCase;

    @MockitoBean private LegacyProductGroupQueryApiMapper legacyProductGroupQueryApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 상품그룹 목록 조회 API")
    class SearchProductGroupsTest {

        private static final String LIST_URL = "/api/v1/legacy/products/group";

        @Test
        @DisplayName("상품그룹 목록 조회 성공")
        void searchProductGroups_Success() throws Exception {
            // given
            LegacyProductGroupDetailResult detailResult =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            LegacyProductGroupPageResult pageResult =
                    LegacyProductGroupPageResult.of(List.of(detailResult), 1, 0, 20);
            LegacyProductGroupListApiResponse listResponse =
                    LegacyProductGroupApiFixtures.productGroupListApiResponse();

            given(legacyProductGroupQueryApiMapper.toSearchParams(any()))
                    .willReturn(LegacyProductGroupApiFixtures.legacySearchParams());
            given(legacySearchProductGroupByOffsetUseCase.execute(any())).willReturn(pageResult);
            given(legacyProductGroupQueryApiMapper.toListResponse(any())).willReturn(listResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(LIST_URL)
                                    .param(
                                            "sellerId",
                                            String.valueOf(
                                                    LegacyProductGroupApiFixtures
                                                            .DEFAULT_SELLER_ID))
                                    .param(
                                            "brandId",
                                            String.valueOf(
                                                    LegacyProductGroupApiFixtures.DEFAULT_BRAND_ID))
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(
                            jsonPath("$.data.content[0].productGroup.productGroupId")
                                    .value(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.data.totalElements").value(1))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/search-list",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("sellerId")
                                                    .description("판매자 ID")
                                                    .optional(),
                                            parameterWithName("brandId")
                                                    .description("브랜드 ID")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품그룹 목록"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품그룹명"),
                                            fieldWithPath("data.content[].productGroup.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.content[].productGroup.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.content[].productGroup.categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("data.content[].productGroup.optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 타입"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.managementType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("관리유형"),
                                            fieldWithPath("data.content[].productGroup.brand.id")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.brand.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.price.regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.price.salePrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.price.discountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인율"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.productGroupMainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("메인 이미지 URL"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.categoryFullName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.productStatus.soldOutYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품절 여부"),
                                            fieldWithPath(
                                                            "data.content[].productGroup.productStatus.displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("진열 여부"),
                                            fieldWithPath("data.content[].productGroup.insertDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.content[].productGroup.updateDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.content[].products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("SKU 목록"),
                                            fieldWithPath("data.content[].products[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data.content[].products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고"),
                                            fieldWithPath("data.content[].products[].option")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 요약"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 건수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.number")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("data.hasNext")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("다음 페이지 존재"),
                                            fieldWithPath("data.hasPrevious")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("이전 페이지 존재"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("레거시 상품그룹 상세 조회 API")
    class FetchProductGroupTest {

        @Test
        @DisplayName("상품그룹 상세 조회 성공")
        void fetchProductGroup_Success() throws Exception {
            // given
            LegacyProductGroupDetailResult detailResult =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();
            LegacyProductDetailApiResponse response =
                    LegacyProductGroupApiFixtures.productDetailApiResponse();

            given(legacyProductQueryUseCase.execute(anyLong())).willReturn(detailResult);
            given(legacyProductGroupQueryApiMapper.toResponse(detailResult)).willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL, PRODUCT_GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.productGroup.productGroupId").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.response.status").value(200));
        }
    }
}
