package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper.LegacyProductCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.response.LegacyCreateProductGroupResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyInboundApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper.LegacyProductGroupCommandApiMapper;
import com.ryuqq.marketplace.application.legacy.productcontext.port.in.query.ResolveLegacyProductContextUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullRegisterUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductGroupFullUpdateUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductMarkOutOfStockUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdateDisplayStatusUseCase;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.command.LegacyProductUpdatePriceUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(LegacyProductGroupCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyProductGroupCommandController REST Docs 테스트")
class LegacyProductGroupCommandControllerRestDocsTest {

    private static final String BASE_URL = "/api/v1/product/group";
    private static final String ID_URL = BASE_URL + "/{productGroupId}";
    private static final String DISPLAY_YN_URL = ID_URL + "/display-yn";
    private static final String OUT_STOCK_URL = ID_URL + "/out-stock";
    private static final String PRICE_URL = ID_URL + "/price";
    private static final long PRODUCT_GROUP_ID =
            LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ResolveLegacyProductContextUseCase resolveLegacyProductContextUseCase;

    @MockitoBean
    private LegacyProductGroupFullRegisterUseCase legacyProductGroupFullRegisterUseCase;

    @MockitoBean private LegacyProductGroupFullUpdateUseCase legacyProductGroupFullUpdateUseCase;

    @MockitoBean
    private LegacyProductUpdateDisplayStatusUseCase legacyProductUpdateDisplayStatusUseCase;

    @MockitoBean private LegacyProductUpdatePriceUseCase legacyProductUpdatePriceUseCase;
    @MockitoBean private LegacyProductMarkOutOfStockUseCase legacyProductMarkOutOfStockUseCase;
    @MockitoBean private LegacyInboundApiMapper legacyInboundApiMapper;
    @MockitoBean private LegacyProductGroupCommandApiMapper legacyProductGroupCommandApiMapper;
    @MockitoBean private LegacyProductCommandApiMapper legacyProductCommandApiMapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private LegacyAccessChecker legacyAccessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("레거시 상품그룹 등록 API")
    class RegisterProductGroupFullTest {

        @Test
        @DisplayName("상품그룹 등록 성공")
        void registerProductGroupFull_Success() throws Exception {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();
            LegacyCreateProductGroupResponse response =
                    LegacyProductGroupApiFixtures.createResponse();

            given(resolveLegacyProductContextUseCase.resolve(any())).willReturn(null);
            given(legacyProductGroupCommandApiMapper.toRegisterCommand(any(), any()))
                    .willReturn(null);
            given(legacyProductGroupFullRegisterUseCase.execute(any()))
                    .willReturn(LegacyProductGroupApiFixtures.registrationResult());
            given(legacyProductGroupCommandApiMapper.toCreateResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.productGroupId").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명 (최대 200자)"),
                                            fieldWithPath("sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 타입 (SINGLE, OPTION_ONE 등)"),
                                            fieldWithPath("managementType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("관리 타입 (SETOF)"),
                                            fieldWithPath("categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("productStatus.soldOutYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품절 여부 (Y/N)"),
                                            fieldWithPath("productStatus.displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("진열 여부 (Y/N)"),
                                            fieldWithPath("price.regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
                                            fieldWithPath("price.currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가"),
                                            fieldWithPath("productNotice.material")
                                                    .type(JsonFieldType.STRING)
                                                    .description("소재")
                                                    .optional(),
                                            fieldWithPath("productNotice.color")
                                                    .type(JsonFieldType.STRING)
                                                    .description("색상")
                                                    .optional(),
                                            fieldWithPath("productNotice.size")
                                                    .type(JsonFieldType.STRING)
                                                    .description("사이즈")
                                                    .optional(),
                                            fieldWithPath("productNotice.maker")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제조사")
                                                    .optional(),
                                            fieldWithPath("productNotice.origin")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원산지")
                                                    .optional(),
                                            fieldWithPath("productNotice.washingMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("세탁 방법")
                                                    .optional(),
                                            fieldWithPath("productNotice.yearMonth")
                                                    .type(JsonFieldType.STRING)
                                                    .description("제조 연월")
                                                    .optional(),
                                            fieldWithPath("productNotice.assuranceStandard")
                                                    .type(JsonFieldType.STRING)
                                                    .description("품질 보증 기준")
                                                    .optional(),
                                            fieldWithPath("productNotice.asPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("AS 전화번호")
                                                    .optional(),
                                            fieldWithPath("clothesDetailInfo.productCondition")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 상태"),
                                            fieldWithPath("clothesDetailInfo.origin")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원산지"),
                                            fieldWithPath("clothesDetailInfo.styleCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("스타일 코드"),
                                            fieldWithPath("deliveryNotice.deliveryArea")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 가능 지역"),
                                            fieldWithPath("deliveryNotice.deliveryFee")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송비"),
                                            fieldWithPath("deliveryNotice.deliveryPeriodAverage")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("평균 배송 소요일"),
                                            fieldWithPath("refundNotice.returnMethodDomestic")
                                                    .type(JsonFieldType.STRING)
                                                    .description("국내 반품 방법"),
                                            fieldWithPath("refundNotice.returnCourierDomestic")
                                                    .type(JsonFieldType.STRING)
                                                    .description("국내 반품 택배사"),
                                            fieldWithPath("refundNotice.returnChargeDomestic")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("국내 반품비"),
                                            fieldWithPath("refundNotice.returnExchangeAreaDomestic")
                                                    .type(JsonFieldType.STRING)
                                                    .description("국내 반품/교환지"),
                                            fieldWithPath("productImageList[].type")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 타입 (MAIN, DETAIL 등)"),
                                            fieldWithPath("productImageList[].productImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 이미지 URL"),
                                            fieldWithPath("productImageList[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("detailDescription")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세 설명 HTML"),
                                            fieldWithPath("productOptions[].productId")
                                                    .type(JsonFieldType.NULL)
                                                    .description("상품 ID (등록 시 null)")
                                                    .optional(),
                                            fieldWithPath("productOptions[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("productOptions[].additionalPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("추가 금액"),
                                            fieldWithPath(
                                                            "productOptions[].options[].optionGroupId")
                                                    .type(JsonFieldType.NULL)
                                                    .description("옵션 그룹 ID (등록 시 null)")
                                                    .optional(),
                                            fieldWithPath(
                                                            "productOptions[].options[].optionDetailId")
                                                    .type(JsonFieldType.NULL)
                                                    .description("옵션 상세 ID (등록 시 null)")
                                                    .optional(),
                                            fieldWithPath("productOptions[].options[].optionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션명"),
                                            fieldWithPath("productOptions[].options[].optionValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값")),
                                    responseFields(
                                            fieldWithPath("data.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("등록된 상품그룹 ID"),
                                            fieldWithPath("data.sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("data.productIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("등록된 상품 ID 목록"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("레거시 상품그룹 수정 API")
    class UpdateProductGroupFullTest {

        @Test
        @DisplayName("상품그룹 수정 성공")
        void updateProductGroupFull_Success() throws Exception {
            // given
            LegacyUpdateProductGroupRequest request = LegacyProductGroupApiFixtures.updateRequest();

            given(resolveLegacyProductContextUseCase.resolve(any())).willReturn(null);
            given(legacyProductGroupCommandApiMapper.toUpdateFullCommand(anyLong(), any(), any()))
                    .willReturn(null);
            doNothing().when(legacyProductGroupFullUpdateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(ID_URL, PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("수정할 상품그룹 ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 상품그룹 ID"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("레거시 가격 수정 API")
    class UpdatePriceTest {

        @Test
        @DisplayName("가격 수정 성공")
        void updatePrice_Success() throws Exception {
            // given
            LegacyCreatePriceRequest request =
                    LegacyProductGroupApiFixtures.price(
                            LegacyProductGroupApiFixtures.DEFAULT_REGULAR_PRICE,
                            LegacyProductGroupApiFixtures.DEFAULT_CURRENT_PRICE);

            doNothing()
                    .when(legacyProductUpdatePriceUseCase)
                    .execute(anyLong(), anyLong(), anyLong());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(PRICE_URL, PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/update-price",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품그룹 ID")),
                                    requestFields(
                                            fieldWithPath("regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
                                            fieldWithPath("currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 상품그룹 ID"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("레거시 진열 상태 변경 API")
    class UpdateGroupDisplayYnTest {

        @Test
        @DisplayName("진열 상태 변경 성공")
        void updateGroupDisplayYn_Success() throws Exception {
            // given
            LegacyUpdateDisplayYnRequest request = LegacyProductGroupApiFixtures.displayOnRequest();

            doNothing().when(legacyProductUpdateDisplayStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(DISPLAY_YN_URL, PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").value(PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/update-display",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품그룹 ID")),
                                    requestFields(
                                            fieldWithPath("displayYn")
                                                    .type(JsonFieldType.STRING)
                                                    .description("진열 여부 (Y/N)")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정된 상품그룹 ID"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("레거시 품절 처리 API")
    class OutOfStockTest {

        @Test
        @DisplayName("품절 처리 성공")
        void outOfStock_Success() throws Exception {
            // given
            LegacyProductGroupDetailResult detailResult =
                    LegacyProductGroupApiFixtures.productGroupDetailResult();

            given(legacyProductGroupCommandApiMapper.toLegacyMarkOutOfStockCommand(any(Long.class)))
                    .willReturn(null);
            given(legacyProductMarkOutOfStockUseCase.execute(any())).willReturn(detailResult);
            given(legacyProductCommandApiMapper.toProductFetchResponses(any(), any(Map.class)))
                    .willReturn(Set.of());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.patch(OUT_STOCK_URL, PRODUCT_GROUP_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andDo(
                            document(
                                    "legacy-product-group/out-of-stock",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("품절 처리할 상품그룹 ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("품절 처리된 상품 목록"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }
}
