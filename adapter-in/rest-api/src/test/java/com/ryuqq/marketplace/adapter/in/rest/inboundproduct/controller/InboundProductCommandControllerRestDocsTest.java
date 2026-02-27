package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
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
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductDescriptionApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductConversionApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper.InboundProductCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.product.mapper.ProductCommandApiMapper;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductIdResolver;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.ReceiveInboundProductUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductDescriptionUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductImagesUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductPriceUseCase;
import com.ryuqq.marketplace.application.inboundproduct.port.in.command.UpdateInboundProductStockUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.List;
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
@WebMvcTest(InboundProductCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("InboundProductCommandController REST Docs 테스트")
class InboundProductCommandControllerRestDocsTest {

    private static final String INBOUND_PRODUCTS_URL = "/api/v1/market/internal/inbound/products";
    private static final String INBOUND_PRODUCT_ID_URL =
            INBOUND_PRODUCTS_URL + "/{inboundSourceId}/{externalProductCode}";

    private static final long INBOUND_SOURCE_ID =
            InboundProductApiFixtures.DEFAULT_INBOUND_SOURCE_ID;
    private static final String EXTERNAL_PRODUCT_CODE =
            InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ReceiveInboundProductUseCase receiveUseCase;
    @MockitoBean private UpdateInboundProductPriceUseCase updatePriceUseCase;
    @MockitoBean private UpdateInboundProductStockUseCase updateStockUseCase;
    @MockitoBean private UpdateInboundProductImagesUseCase updateImagesUseCase;
    @MockitoBean private UpdateInboundProductDescriptionUseCase updateDescriptionUseCase;
    @MockitoBean private UpdateProductsUseCase updateProductsUseCase;
    @MockitoBean private InboundProductIdResolver idResolver;
    @MockitoBean private InboundProductCommandApiMapper apiMapper;
    @MockitoBean private ProductCommandApiMapper productCommandApiMapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("인바운드 상품 수신 API")
    class ReceiveInboundProductTest {

        @Test
        @DisplayName("인바운드 상품 수신 성공")
        void receiveInboundProduct_Success() throws Exception {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();
            InboundProductConversionResult result = InboundProductApiFixtures.conversionResult();
            InboundProductConversionApiResponse apiResponse =
                    InboundProductApiFixtures.conversionApiResponse();

            given(apiMapper.toCommand(any(ReceiveInboundProductApiRequest.class))).willReturn(null);
            given(receiveUseCase.execute(any())).willReturn(result);
            given(apiMapper.toResponse(result)).willReturn(apiResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(INBOUND_PRODUCTS_URL)
                                    .header("X-Service-Token", "test-token")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(
                            jsonPath("$.data.inboundProductId")
                                    .value(InboundProductApiFixtures.DEFAULT_INBOUND_PRODUCT_ID))
                    .andExpect(
                            jsonPath("$.data.internalProductGroupId")
                                    .value(
                                            InboundProductApiFixtures
                                                    .DEFAULT_INTERNAL_PRODUCT_GROUP_ID))
                    .andExpect(jsonPath("$.data.status").value("CONVERTED"))
                    .andExpect(jsonPath("$.data.action").value("CREATED"))
                    .andDo(
                            document(
                                    "inbound-product/receive",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName("X-Service-Token")
                                                    .description("내부 서비스 인증 토큰")),
                                    requestFields(
                                            fieldWithPath("inboundSourceId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("인바운드 소스 ID"),
                                            fieldWithPath("externalProductCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 코드"),
                                            fieldWithPath("productName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품명"),
                                            fieldWithPath("externalBrandCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 브랜드 코드"),
                                            fieldWithPath("externalCategoryCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 카테고리 코드"),
                                            fieldWithPath("sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("대표 정가"),
                                            fieldWithPath("currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("대표 현재가"),
                                            fieldWithPath("optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "옵션 타입 (COMBINATION, SINGLE, NONE)"),
                                            fieldWithPath("images")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형 (THUMBNAIL, DETAIL)"),
                                            fieldWithPath("images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("optionGroups")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 그룹 목록"),
                                            fieldWithPath("optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath("optionGroups[].inputType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("입력 유형 (PREDEFINED, FREE_INPUT)")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
                                            fieldWithPath(
                                                            "optionGroups[].optionValues[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath("optionGroups[].optionValues[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품 목록"),
                                            fieldWithPath("products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드")
                                                    .optional(),
                                            fieldWithPath("products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("products[].currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재가"),
                                            fieldWithPath("products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("products[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("products[].selectedOptions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("선택된 옵션 목록"),
                                            fieldWithPath(
                                                            "products[].selectedOptions[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "products[].selectedOptions[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath("description")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("상세 설명"),
                                            fieldWithPath("description.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세 설명 내용 (HTML)"),
                                            fieldWithPath("notice")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("고시정보")
                                                    .optional(),
                                            fieldWithPath("notice.entries")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시정보 항목 목록")
                                                    .optional(),
                                            fieldWithPath("notice.entries[].fieldCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("고시정보 필드 코드")
                                                    .optional(),
                                            fieldWithPath("notice.entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("고시정보 필드 값")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.inboundProductId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("인바운드 상품 ID"),
                                            fieldWithPath("data.internalProductGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("내부 상품 그룹 ID (미매핑 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "처리 상태 (CONVERTED, PENDING_MAPPING,"
                                                                    + " PENDING_CONVERSION,"
                                                                    + " CONVERT_FAILED)"),
                                            fieldWithPath("data.action")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "처리 액션 (CREATED, UPDATED, NO_CHANGE,"
                                                                    + " PENDING_MAPPING,"
                                                                    + " PENDING_CONVERSION,"
                                                                    + " CONVERT_FAILED)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("인바운드 상품 가격 수정 API")
    class UpdatePriceTest {

        @Test
        @DisplayName("인바운드 상품 가격 수정 성공")
        void updatePrice_Success() throws Exception {
            // given
            UpdateInboundProductPriceApiRequest request =
                    InboundProductApiFixtures.updatePriceRequest();

            doNothing()
                    .when(updatePriceUseCase)
                    .execute(anyLong(), anyString(), any(Integer.class), any(Integer.class));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            INBOUND_PRODUCT_ID_URL + "/price",
                                            INBOUND_SOURCE_ID,
                                            EXTERNAL_PRODUCT_CODE)
                                    .header("X-Service-Token", "test-token")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "inbound-product/update-price",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName("X-Service-Token")
                                                    .description("내부 서비스 인증 토큰")),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    requestFields(
                                            fieldWithPath("regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가"))));
        }
    }

    @Nested
    @DisplayName("인바운드 상품 재고 수정 API")
    class UpdateStockTest {

        @Test
        @DisplayName("인바운드 상품 재고 수정 성공")
        void updateStock_Success() throws Exception {
            // given
            UpdateInboundProductStockApiRequest request =
                    InboundProductApiFixtures.updateStockRequest();

            given(apiMapper.toStockCommands(any(UpdateInboundProductStockApiRequest.class)))
                    .willReturn(List.of());
            doNothing().when(updateStockUseCase).execute(anyLong(), anyString(), any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            INBOUND_PRODUCT_ID_URL + "/stock",
                                            INBOUND_SOURCE_ID,
                                            EXTERNAL_PRODUCT_CODE)
                                    .header("X-Service-Token", "test-token")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "inbound-product/update-stock",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName("X-Service-Token")
                                                    .description("내부 서비스 인증 토큰")),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    requestFields(
                                            fieldWithPath("stocks")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("재고 수정 목록"),
                                            fieldWithPath("stocks[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("stocks[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"))));
        }
    }

    @Nested
    @DisplayName("인바운드 상품 이미지 수정 API")
    class UpdateImagesTest {

        @Test
        @DisplayName("인바운드 상품 이미지 수정 성공")
        void updateImages_Success() throws Exception {
            // given
            UpdateInboundProductImagesApiRequest request =
                    InboundProductApiFixtures.updateImagesRequest();

            given(apiMapper.toImagesCommand(any(UpdateInboundProductImagesApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateImagesUseCase).execute(anyLong(), anyString(), any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            INBOUND_PRODUCT_ID_URL + "/images",
                                            INBOUND_SOURCE_ID,
                                            EXTERNAL_PRODUCT_CODE)
                                    .header("X-Service-Token", "test-token")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "inbound-product/update-images",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName("X-Service-Token")
                                                    .description("내부 서비스 인증 토큰")),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    requestFields(
                                            fieldWithPath("images")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형 (THUMBNAIL, DETAIL)"),
                                            fieldWithPath("images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"))));
        }
    }

    @Nested
    @DisplayName("인바운드 상품 상세설명 수정 API")
    class UpdateDescriptionTest {

        @Test
        @DisplayName("인바운드 상품 상세설명 수정 성공")
        void updateDescription_Success() throws Exception {
            // given
            UpdateInboundProductDescriptionApiRequest request =
                    InboundProductApiFixtures.updateDescriptionRequest();

            doNothing().when(updateDescriptionUseCase).execute(anyLong(), anyString(), anyString());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            INBOUND_PRODUCT_ID_URL + "/description",
                                            INBOUND_SOURCE_ID,
                                            EXTERNAL_PRODUCT_CODE)
                                    .header("X-Service-Token", "test-token")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "inbound-product/update-description",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName("X-Service-Token")
                                                    .description("내부 서비스 인증 토큰")),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    requestFields(
                                            fieldWithPath("content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세 설명 HTML"))));
        }
    }

    @Nested
    @DisplayName("인바운드 상품 내부 상품 일괄 수정 API")
    class UpdateProductsTest {

        @Test
        @DisplayName("인바운드 상품 내부 상품 일괄 수정 성공")
        void updateProducts_Success() throws Exception {
            // given
            com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest
                    request =
                            new com.ryuqq.marketplace.adapter.in.rest.product.dto.command
                                    .UpdateProductsApiRequest(
                                    List.of(
                                            new com.ryuqq.marketplace.adapter.in.rest.product.dto
                                                    .command.UpdateProductsApiRequest
                                                    .OptionGroupApiRequest(
                                                    null,
                                                    "색상",
                                                    null,
                                                    "PREDEFINED",
                                                    List.of(
                                                            new com.ryuqq.marketplace.adapter.in
                                                                    .rest.product.dto.command
                                                                    .UpdateProductsApiRequest
                                                                    .OptionValueApiRequest(
                                                                    null, "블랙", null, 0)))),
                                    List.of(
                                            new com.ryuqq.marketplace.adapter.in.rest.product.dto
                                                    .command.UpdateProductsApiRequest
                                                    .ProductDataApiRequest(
                                                    1L,
                                                    "SKU-001",
                                                    30000,
                                                    25000,
                                                    100,
                                                    0,
                                                    List.of(
                                                            new com.ryuqq.marketplace.adapter.in
                                                                    .rest.product.dto.command
                                                                    .UpdateProductsApiRequest
                                                                    .SelectedOptionApiRequest(
                                                                    "색상", "블랙")))));

            given(idResolver.resolve(anyLong(), anyString())).willReturn(ProductGroupId.of(200L));
            given(productCommandApiMapper.toCommand(anyLong(), any())).willReturn(null);
            doNothing().when(updateProductsUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            INBOUND_PRODUCT_ID_URL + "/products",
                                            INBOUND_SOURCE_ID,
                                            EXTERNAL_PRODUCT_CODE)
                                    .header("X-Service-Token", "test-token")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "inbound-product/update-products",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestHeaders(
                                            headerWithName("X-Service-Token")
                                                    .description("내부 서비스 인증 토큰")),
                                    pathParameters(
                                            parameterWithName("inboundSourceId")
                                                    .description("인바운드 소스 ID"),
                                            parameterWithName("externalProductCode")
                                                    .description("외부 상품 코드")),
                                    requestFields(
                                            fieldWithPath("optionGroups")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 그룹 목록"),
                                            fieldWithPath("optionGroups[].sellerOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("기존 옵션 그룹 ID (신규이면 null)")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath("optionGroups[].canonicalOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 그룹 ID")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].inputType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("입력 유형 (PREDEFINED, FREE_INPUT)")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록")
                                                    .optional(),
                                            fieldWithPath(
                                                            "optionGroups[].optionValues[].sellerOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("기존 옵션 값 ID (신규이면 null)")
                                                    .optional(),
                                            fieldWithPath(
                                                            "optionGroups[].optionValues[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath(
                                                            "optionGroups[].optionValues[].canonicalOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 값 ID")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].optionValues[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("수정할 상품 목록"),
                                            fieldWithPath("products[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID (신규이면 null)")
                                                    .optional(),
                                            fieldWithPath("products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath("products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("products[].currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가"),
                                            fieldWithPath("products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("products[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("products[].selectedOptions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이름 기반 옵션 선택 목록"),
                                            fieldWithPath(
                                                            "products[].selectedOptions[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "products[].selectedOptions[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"))));
        }
    }
}
