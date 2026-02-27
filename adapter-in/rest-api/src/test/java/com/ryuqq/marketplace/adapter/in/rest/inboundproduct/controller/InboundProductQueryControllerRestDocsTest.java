package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper.InboundProductQueryApiMapper;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductDetailResult;
import com.ryuqq.marketplace.application.inboundproduct.port.in.query.GetInboundProductDetailUseCase;
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
@WebMvcTest(InboundProductQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("InboundProductQueryController REST Docs 테스트")
class InboundProductQueryControllerRestDocsTest {

    private static final String BASE_URL =
            "/api/v1/market/internal/inbound/products/{inboundSourceId}/{externalProductCode}";

    private static final long INBOUND_SOURCE_ID =
            InboundProductApiFixtures.DEFAULT_INBOUND_SOURCE_ID;
    private static final String EXTERNAL_PRODUCT_CODE =
            InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetInboundProductDetailUseCase getDetailUseCase;
    @MockitoBean private InboundProductQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("인바운드 상품 상세 조회 API")
    class GetInboundProductDetailTest {

        @Test
        @DisplayName("인바운드 상품 상세 조회 성공 - 변환 완료 상태")
        void getDetail_Success_ConvertedStatus() throws Exception {
            // given
            InboundProductDetailResult result = InboundProductApiFixtures.detailResult();
            InboundProductDetailApiResponse apiResponse =
                    InboundProductApiFixtures.detailApiResponse();

            given(getDetailUseCase.execute(anyLong(), anyString())).willReturn(result);
            given(mapper.toResponse(result)).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL, INBOUND_SOURCE_ID, EXTERNAL_PRODUCT_CODE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("CONVERTED"))
                    .andExpect(jsonPath("$.data.externalProductCode").value(EXTERNAL_PRODUCT_CODE))
                    .andExpect(
                            jsonPath("$.data.internalProductGroupId")
                                    .value(
                                            InboundProductApiFixtures
                                                    .DEFAULT_INTERNAL_PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.data.products").isArray())
                    .andExpect(jsonPath("$.data.products.length()").value(2))
                    .andDo(
                            document(
                                    "inbound-product/get-detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    responseFields(
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "인바운드 상품 상태 (RECEIVED, PENDING_MAPPING,"
                                                                    + " MAPPED, CONVERTED,"
                                                                    + " CONVERT_FAILED)"),
                                            fieldWithPath("data.externalProductCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 코드"),
                                            fieldWithPath("data.internalProductGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("내부 상품 그룹 ID (미변환 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("내부 상품 목록"),
                                            fieldWithPath("data.products[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data.products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드")
                                                    .optional(),
                                            fieldWithPath("data.products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("data.products[].currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가"),
                                            fieldWithPath("data.products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("data.products[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("data.products[].options")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 목록"),
                                            fieldWithPath(
                                                            "data.products[].options[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "data.products[].options[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("인바운드 상품 상세 조회 성공 - 미변환 상태")
        void getDetail_Success_PendingMappingStatus() throws Exception {
            // given
            InboundProductDetailResult result =
                    InboundProductApiFixtures.detailResultNotConverted();
            InboundProductDetailApiResponse apiResponse =
                    InboundProductApiFixtures.detailApiResponseNotConverted();

            given(getDetailUseCase.execute(anyLong(), anyString())).willReturn(result);
            given(mapper.toResponse(result)).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL, INBOUND_SOURCE_ID, EXTERNAL_PRODUCT_CODE))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.status").value("PENDING_MAPPING"))
                    .andExpect(jsonPath("$.data.products").isArray())
                    .andExpect(jsonPath("$.data.products.length()").value(0))
                    .andDo(
                            document(
                                    "inbound-product/get-detail-pending",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    responseFields(
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("인바운드 상품 상태"),
                                            fieldWithPath("data.externalProductCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 코드"),
                                            fieldWithPath("data.internalProductGroupId")
                                                    .type(JsonFieldType.NULL)
                                                    .description("내부 상품 그룹 ID (미변환 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("내부 상품 목록 (미변환 시 빈 목록)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
