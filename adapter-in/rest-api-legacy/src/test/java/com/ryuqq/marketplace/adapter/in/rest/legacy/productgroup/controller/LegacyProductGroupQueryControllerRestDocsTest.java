package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyProductGroupQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
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
    @MockitoBean private LegacyProductGroupQueryApiMapper legacyProductGroupQueryApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private LegacyAccessChecker legacyAccessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

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
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("조회할 상품그룹 ID")),
                                    responseFields(
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
                                                    .type(JsonFieldType.STRING)
                                                    .description("관리유형"),
                                            fieldWithPath("data.productGroup.brand.brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("data.productGroup.brand.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.productGroup.price.regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
                                            fieldWithPath("data.productGroup.price.salePrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가"),
                                            fieldWithPath("data.productGroup.price.currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가"),
                                            fieldWithPath("data.productGroup.price.directDiscountPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("직접 할인 금액"),
                                            fieldWithPath("data.productGroup.price.directDiscountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("직접 할인율"),
                                            fieldWithPath("data.productGroup.price.discountRate")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인율"),
                                            fieldWithPath("data.productGroup.clothesDetailInfo.productCondition")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 상태"),
                                            fieldWithPath("data.productGroup.clothesDetailInfo.origin")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원산지"),
                                            fieldWithPath("data.productGroup.clothesDetailInfo.styleCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("스타일 코드"),
                                            fieldWithPath("data.productGroup.deliveryNotice.deliveryArea")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 가능 지역"),
                                            fieldWithPath("data.productGroup.deliveryNotice.deliveryFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송비"),
                                            fieldWithPath("data.productGroup.deliveryNotice.deliveryPeriodAverage")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("평균 배송 소요일"),
                                            fieldWithPath("data.productGroup.refundNotice.returnMethodDomestic")
                                                    .type(JsonFieldType.STRING)
                                                    .description("국내 반품 방법"),
                                            fieldWithPath("data.productGroup.refundNotice.returnCourierDomestic")
                                                    .type(JsonFieldType.STRING)
                                                    .description("국내 반품 택배사"),
                                            fieldWithPath("data.productGroup.refundNotice.returnChargeDomestic")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("국내 반품비"),
                                            fieldWithPath("data.productGroup.refundNotice.returnExchangeAreaDomestic")
                                                    .type(JsonFieldType.STRING)
                                                    .description("국내 반품/교환지"),
                                            fieldWithPath("data.productGroup.productGroupMainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("메인 이미지 URL"),
                                            fieldWithPath("data.productGroup.categoryFullName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("카테고리 전체 경로"),
                                            fieldWithPath("data.productGroup.productStatus.soldOutYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품절 여부"),
                                            fieldWithPath("data.productGroup.productStatus.displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("진열 여부"),
                                            fieldWithPath("data.productGroup.insertDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.productGroup.updateDate")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.productGroup.insertOperator")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록자"),
                                            fieldWithPath("data.productGroup.updateOperator")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정자"),
                                            fieldWithPath("data.products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("SKU 목록"),
                                            fieldWithPath("data.products[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data.products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고"),
                                            fieldWithPath("data.products[].productStatus.soldOutYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품절 여부"),
                                            fieldWithPath("data.products[].productStatus.displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("진열 여부"),
                                            fieldWithPath("data.products[].option")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 요약"),
                                            fieldWithPath("data.products[].options")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 상세 목록"),
                                            fieldWithPath("data.products[].options[].optionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 그룹 ID"),
                                            fieldWithPath("data.products[].options[].optionDetailId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("옵션 상세 ID"),
                                            fieldWithPath("data.products[].options[].optionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션명"),
                                            fieldWithPath("data.products[].options[].optionValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값"),
                                            fieldWithPath("data.products[].additionalPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("추가 금액"),
                                            fieldWithPath("data.productNotices.material")
                                                    .type(JsonFieldType.STRING)
                                                    .description("소재"),
                                            fieldWithPath("data.productNotices.color")
                                                    .type(JsonFieldType.STRING)
                                                    .description("색상"),
                                            fieldWithPath("data.productNotices.size")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사이즈"),
                                            fieldWithPath("data.productNotices.maker")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제조사"),
                                            fieldWithPath("data.productNotices.origin")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원산지"),
                                            fieldWithPath("data.productNotices.washingMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("세탁 방법"),
                                            fieldWithPath("data.productNotices.yearMonth")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제조 연월"),
                                            fieldWithPath("data.productNotices.assuranceStandard")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품질 보증 기준"),
                                            fieldWithPath("data.productNotices.asPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("AS 전화번호"),
                                            fieldWithPath("data.productGroupImages")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("data.productGroupImages[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 타입"),
                                            fieldWithPath("data.productGroupImages[].productImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 URL"),
                                            fieldWithPath("data.detailDescription")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세 설명 HTML"),
                                            fieldWithPath("data.categories")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("카테고리 목록"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }
}
