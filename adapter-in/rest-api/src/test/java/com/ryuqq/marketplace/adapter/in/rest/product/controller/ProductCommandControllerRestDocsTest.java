package com.ryuqq.marketplace.adapter.in.rest.product.controller;

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
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.product.ProductAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.product.ProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.BatchChangeProductStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductPriceApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.dto.command.UpdateProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.product.mapper.ProductCommandApiMapper;
import com.ryuqq.marketplace.application.product.port.in.command.BatchChangeProductStatusUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductPriceUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductStockUseCase;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
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
@WebMvcTest(ProductCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductCommandController REST Docs 테스트")
class ProductCommandControllerRestDocsTest {

    private static final String BASE_URL = ProductAdminEndpoints.PRODUCTS;
    private static final Long PRODUCT_ID = 1L;
    private static final Long PRODUCT_GROUP_ID = 10L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private UpdateProductPriceUseCase updatePriceUseCase;
    @MockitoBean private UpdateProductStockUseCase updateStockUseCase;
    @MockitoBean private BatchChangeProductStatusUseCase batchChangeStatusUseCase;
    @MockitoBean private UpdateProductsUseCase updateProductsUseCase;
    @MockitoBean private ProductCommandApiMapper mapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 가격 수정 API")
    class UpdatePriceTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updatePrice_ValidRequest_Returns204() throws Exception {
            // given
            UpdateProductPriceApiRequest request = ProductApiFixtures.updatePriceRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateProductPriceApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updatePriceUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + ProductAdminEndpoints.ID
                                                    + ProductAdminEndpoints.PRICE,
                                            PRODUCT_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product/update-price",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productId").description("상품 ID")),
                                    requestFields(
                                            fieldWithPath("regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정가"),
                                            fieldWithPath("currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가 (정가 이하)"))));
        }
    }

    @Nested
    @DisplayName("상품 재고 수정 API")
    class UpdateStockTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateStock_ValidRequest_Returns204() throws Exception {
            // given
            UpdateProductStockApiRequest request = ProductApiFixtures.updateStockRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateProductStockApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateStockUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + ProductAdminEndpoints.ID
                                                    + ProductAdminEndpoints.STOCK,
                                            PRODUCT_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product/update-stock",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productId").description("상품 ID")),
                                    requestFields(
                                            fieldWithPath("stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량 (0 이상)"))));
        }
    }

    @Nested
    @DisplayName("상품 배치 상태 변경 API")
    class BatchChangeStatusTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void batchChangeStatus_ValidRequest_Returns204() throws Exception {
            // given
            BatchChangeProductStatusApiRequest request =
                    ProductApiFixtures.batchChangeStatusRequest();

            given(accessChecker.resolveCurrentSellerId())
                    .willReturn(ProductApiFixtures.DEFAULT_SELLER_ID);
            given(
                            mapper.toCommand(
                                    anyLong(),
                                    any(Long.class),
                                    any(BatchChangeProductStatusApiRequest.class)))
                    .willReturn(null);
            doNothing().when(batchChangeStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + ProductAdminEndpoints.PRODUCT_GROUP
                                                    + ProductAdminEndpoints.STATUS,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product/batch-change-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("productIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상태를 변경할 상품 ID 목록 (숫자 배열)"),
                                            fieldWithPath("targetStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "변경할 상태 (ACTIVE, INACTIVE,"
                                                                    + " SOLDOUT)"))));
        }
    }

    @Nested
    @DisplayName("상품 일괄 수정 API")
    class UpdateProductsTest {

        @Test
        @DisplayName("유효한 요청이면 204를 반환한다")
        void updateProducts_ValidRequest_Returns204() throws Exception {
            // given
            UpdateProductsApiRequest request = ProductApiFixtures.updateProductsRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateProductsApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateProductsUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + ProductAdminEndpoints.PRODUCT_GROUP,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product/update-products",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("productGroupId")
                                                    .description("상품 그룹 ID")),
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
                                            fieldWithPath("optionGroups[].optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("옵션 값 목록"),
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
                                                    .description("판매가 (정가 이하)"),
                                            fieldWithPath("products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량 (0 이상)"),
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
