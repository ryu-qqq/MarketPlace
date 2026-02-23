package com.ryuqq.marketplace.adapter.in.rest.productgroup.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchChangeProductGroupStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchRegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupBasicInfoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupFullApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper.ProductGroupCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchItemResult;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchChangeProductGroupStatusUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.BatchRegisterProductGroupFullUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.RegisterProductGroupFullUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupBasicInfoUseCase;
import com.ryuqq.marketplace.application.productgroup.port.in.command.UpdateProductGroupFullUseCase;
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
@WebMvcTest(ProductGroupCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ProductGroupCommandController REST Docs 테스트")
class ProductGroupCommandControllerRestDocsTest {

    private static final String BASE_URL = ProductGroupAdminEndpoints.PRODUCT_GROUPS;
    private static final Long PRODUCT_GROUP_ID = 1L;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterProductGroupFullUseCase registerUseCase;
    @MockitoBean private BatchRegisterProductGroupFullUseCase batchRegisterUseCase;
    @MockitoBean private UpdateProductGroupFullUseCase updateUseCase;
    @MockitoBean private UpdateProductGroupBasicInfoUseCase updateBasicInfoUseCase;
    @MockitoBean private BatchChangeProductGroupStatusUseCase batchChangeStatusUseCase;
    @MockitoBean private ProductGroupCommandApiMapper mapper;
    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("상품 그룹 등록 API")
    class RegisterProductGroupTest {

        @Test
        @DisplayName("상품 그룹 등록 성공")
        void registerProductGroup_Success() throws Exception {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            given(mapper.toCommand(any(RegisterProductGroupApiRequest.class))).willReturn(null);
            given(registerUseCase.execute(any())).willReturn(PRODUCT_GROUP_ID);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(BASE_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productGroupId").value(PRODUCT_GROUP_ID))
                    .andDo(
                            document(
                                    "product-group/register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("shippingPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 정책 ID"),
                                            fieldWithPath("refundPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 정책 ID"),
                                            fieldWithPath("productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "옵션 유형 (NONE, SINGLE, COMBINATION)"),
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
                                                    .description(
                                                            "옵션 그룹 목록 (NONE=0개, SINGLE=1개,"
                                                                    + " COMBINATION=2개)"),
                                            fieldWithPath("optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath("optionGroups[].canonicalOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 그룹 ID")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].inputType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "입력 유형 (PREDEFINED: 사전 정의,"
                                                                    + " FREE_INPUT: OMS 자유 입력 매핑)")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description(
                                                            "옵션 값 목록 (모든 inputType에서 최소 1개 필수)"),
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
                                                    .description("상품(SKU) 목록"),
                                            fieldWithPath("products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath("products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
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
                                                    .description("이름 기반 옵션 선택 목록 (모든 그룹 포함)"),
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
                                                    .description("상세설명"),
                                            fieldWithPath("description.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세설명 내용 (HTML)")
                                                    .optional(),
                                            fieldWithPath("notice")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("고시정보"),
                                            fieldWithPath("notice.noticeCategoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 카테고리 ID"),
                                            fieldWithPath("notice.entries")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시 항목 목록"),
                                            fieldWithPath("notice.entries[].noticeFieldId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 필드 ID"),
                                            fieldWithPath("notice.entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("고시 필드 값")),
                                    responseFields(
                                            fieldWithPath("productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 상품 그룹 ID"))));
        }
    }

    @Nested
    @DisplayName("상품 그룹 배치 등록 API")
    class BatchRegisterProductGroupTest {

        @Test
        @DisplayName("상품 그룹 배치 등록 성공")
        void batchRegisterProductGroups_Success() throws Exception {
            // given
            BatchRegisterProductGroupApiRequest request =
                    ProductGroupApiFixtures.batchRegisterRequest();

            List<BatchItemResult<Long>> itemResults =
                    List.of(
                            new BatchItemResult<>(1L, true, null, null),
                            new BatchItemResult<>(2L, true, null, null));
            BatchProcessingResult<Long> batchResult =
                    new BatchProcessingResult<>(2, 2, 0, itemResults);

            given(mapper.toCommands(any(BatchRegisterProductGroupApiRequest.class)))
                    .willReturn(List.of());
            given(batchRegisterUseCase.execute(any())).willReturn(batchResult);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            BASE_URL + ProductGroupAdminEndpoints.BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalCount").value(2))
                    .andExpect(jsonPath("$.successCount").value(2))
                    .andExpect(jsonPath("$.failureCount").value(0))
                    .andDo(
                            document(
                                    "product-group/batch-register",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("등록할 상품 그룹 목록 (최대 100건)"),
                                            fieldWithPath("items[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("items[].brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("items[].categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("items[].shippingPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 정책 ID"),
                                            fieldWithPath("items[].refundPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 정책 ID"),
                                            fieldWithPath("items[].productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("items[].optionType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 유형"),
                                            fieldWithPath("items[].images")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록"),
                                            fieldWithPath("items[].images[].imageType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("이미지 유형"),
                                            fieldWithPath("items[].images[].originUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원본 이미지 URL"),
                                            fieldWithPath("items[].images[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("items[].optionGroups")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description(
                                                            "옵션 그룹 목록 (NONE=0개, SINGLE=1개,"
                                                                    + " COMBINATION=2개)"),
                                            fieldWithPath("items[].optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "items[].optionGroups[].canonicalOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 그룹 ID")
                                                    .optional(),
                                            fieldWithPath("items[].optionGroups[].inputType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "입력 유형 (PREDEFINED: 사전 정의,"
                                                                    + " FREE_INPUT: OMS 자유 입력 매핑)")
                                                    .optional(),
                                            fieldWithPath("items[].optionGroups[].optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description(
                                                            "옵션 값 목록 (모든 inputType에서 최소 1개 필수)"),
                                            fieldWithPath(
                                                            "items[].optionGroups[].optionValues[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath(
                                                            "items[].optionGroups[].optionValues[].canonicalOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 값 ID")
                                                    .optional(),
                                            fieldWithPath(
                                                            "items[].optionGroups[].optionValues[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("items[].products")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품(SKU) 목록"),
                                            fieldWithPath("items[].products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath("items[].products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
                                            fieldWithPath("items[].products[].currentPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매가"),
                                            fieldWithPath("items[].products[].stockQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재고 수량"),
                                            fieldWithPath("items[].products[].sortOrder")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정렬 순서"),
                                            fieldWithPath("items[].products[].selectedOptions")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이름 기반 옵션 선택 목록 (모든 그룹 포함)"),
                                            fieldWithPath(
                                                            "items[].products[].selectedOptions[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath(
                                                            "items[].products[].selectedOptions[].optionValueName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 값명"),
                                            fieldWithPath("items[].description")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("상세설명"),
                                            fieldWithPath("items[].description.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세설명 내용")
                                                    .optional(),
                                            fieldWithPath("items[].notice")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("고시정보"),
                                            fieldWithPath("items[].notice.noticeCategoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 카테고리 ID"),
                                            fieldWithPath("items[].notice.entries")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시 항목 목록"),
                                            fieldWithPath("items[].notice.entries[].noticeFieldId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 필드 ID"),
                                            fieldWithPath("items[].notice.entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("고시 필드 값")),
                                    responseFields(
                                            fieldWithPath("totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("항목별 결과"),
                                            fieldWithPath("results[].index")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("요청 인덱스 (0-based)"),
                                            fieldWithPath("results[].productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성된 상품 그룹 ID (실패 시 null)")
                                                    .optional(),
                                            fieldWithPath("results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("results[].errorCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("results[].errorMessage")
                                                    .type(JsonFieldType.STRING)
                                                    .description("에러 메시지 (성공 시 null)")
                                                    .optional())));
        }
    }

    @Nested
    @DisplayName("상품 그룹 전체 수정 API")
    class UpdateProductGroupFullTest {

        @Test
        @DisplayName("상품 그룹 전체 수정 성공")
        void updateProductGroupFull_Success() throws Exception {
            // given
            UpdateProductGroupFullApiRequest request = ProductGroupApiFixtures.updateFullRequest();

            given(mapper.toCommand(any(Long.class), any(UpdateProductGroupFullApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(
                                            BASE_URL + ProductGroupAdminEndpoints.ID,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product-group/update-full",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName(
                                                            ProductGroupAdminEndpoints
                                                                    .PATH_PRODUCT_GROUP_ID)
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("shippingPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 정책 ID"),
                                            fieldWithPath("refundPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 정책 ID"),
                                            fieldWithPath("images")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("이미지 목록 (최소 1개)"),
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
                                                    .description(
                                                            "옵션 그룹 목록 (NONE=0개, SINGLE=1개,"
                                                                    + " COMBINATION=2개)"),
                                            fieldWithPath("optionGroups[].sellerOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 옵션 그룹 ID (기존 그룹 수정 시)"),
                                            fieldWithPath("optionGroups[].optionGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("옵션 그룹명"),
                                            fieldWithPath("optionGroups[].canonicalOptionGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("표준 옵션 그룹 ID")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].inputType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "입력 유형 (PREDEFINED: 사전 정의,"
                                                                    + " FREE_INPUT: OMS 자유 입력 매핑)")
                                                    .optional(),
                                            fieldWithPath("optionGroups[].optionValues")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description(
                                                            "옵션 값 목록 (모든 inputType에서 최소 1개 필수)"),
                                            fieldWithPath(
                                                            "optionGroups[].optionValues[].sellerOptionValueId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 옵션 값 ID (기존 값 수정 시)")
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
                                                    .description("상품(SKU) 목록 (최소 1개)"),
                                            fieldWithPath("products[].productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID (기존 상품 수정 시)")
                                                    .optional(),
                                            fieldWithPath("products[].skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath("products[].regularPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("정상가"),
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
                                                    .description("이름 기반 옵션 선택 목록 (모든 그룹 포함)"),
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
                                                    .description("상세설명"),
                                            fieldWithPath("description.content")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세설명 내용 (HTML)"),
                                            fieldWithPath("notice")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("고시정보"),
                                            fieldWithPath("notice.noticeCategoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 카테고리 ID"),
                                            fieldWithPath("notice.entries")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("고시 항목 목록"),
                                            fieldWithPath("notice.entries[].noticeFieldId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("고시 필드 ID"),
                                            fieldWithPath("notice.entries[].fieldValue")
                                                    .type(JsonFieldType.STRING)
                                                    .description("고시 필드 값"))));
        }
    }

    @Nested
    @DisplayName("상품 그룹 기본정보 수정 API")
    class UpdateBasicInfoTest {

        @Test
        @DisplayName("상품 그룹 기본정보 수정 성공")
        void updateBasicInfo_Success() throws Exception {
            // given
            UpdateProductGroupBasicInfoApiRequest request =
                    ProductGroupApiFixtures.updateBasicInfoRequest();

            given(
                            mapper.toCommand(
                                    any(Long.class),
                                    any(UpdateProductGroupBasicInfoApiRequest.class)))
                    .willReturn(null);
            doNothing().when(updateBasicInfoUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL
                                                    + ProductGroupAdminEndpoints.ID
                                                    + ProductGroupAdminEndpoints.BASIC_INFO,
                                            PRODUCT_GROUP_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product-group/update-basic-info",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName(
                                                            ProductGroupAdminEndpoints
                                                                    .PATH_PRODUCT_GROUP_ID)
                                                    .description("상품 그룹 ID")),
                                    requestFields(
                                            fieldWithPath("productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품 그룹명"),
                                            fieldWithPath("brandId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("브랜드 ID"),
                                            fieldWithPath("categoryId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("카테고리 ID"),
                                            fieldWithPath("shippingPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 정책 ID"),
                                            fieldWithPath("refundPolicyId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 정책 ID"))));
        }
    }

    @Nested
    @DisplayName("상품 그룹 배치 상태 변경 API")
    class BatchChangeStatusTest {

        @Test
        @DisplayName("상품 그룹 배치 상태 변경 성공")
        void batchChangeStatus_Success() throws Exception {
            // given
            BatchChangeProductGroupStatusApiRequest request =
                    ProductGroupApiFixtures.batchChangeStatusRequest();

            given(accessChecker.resolveCurrentSellerId()).willReturn(1L);
            given(mapper.toCommand(anyLong(), any(BatchChangeProductGroupStatusApiRequest.class)))
                    .willReturn(null);
            doNothing().when(batchChangeStatusUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            BASE_URL + ProductGroupAdminEndpoints.STATUS)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent())
                    .andDo(
                            document(
                                    "product-group/batch-change-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("productGroupIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("상품 그룹 ID 목록"),
                                            fieldWithPath("targetStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "변경할 상태 (ACTIVE, INACTIVE, SOLDOUT,"
                                                                    + " DELETED)"))));
        }
    }
}
