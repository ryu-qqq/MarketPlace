package com.ryuqq.marketplace.adapter.in.rest.exchange.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
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
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.exchange.ExchangeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ApproveExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CollectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.CompleteExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ConvertToRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.HoldExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.PrepareExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RejectExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.RequestExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.dto.request.ShipExchangeBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.exchange.mapper.ExchangeApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.exchange.port.in.command.ApproveExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.CollectExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.CompleteExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.ConvertToRefundBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.HoldExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.PrepareExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.RejectExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.RequestExchangeBatchUseCase;
import com.ryuqq.marketplace.application.exchange.port.in.command.ShipExchangeBatchUseCase;
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
@WebMvcTest(ExchangeCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ExchangeCommandController REST Docs 테스트")
class ExchangeCommandControllerRestDocsTest {

    private static final String EXCHANGES_URL = ExchangeAdminEndpoints.EXCHANGES;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RequestExchangeBatchUseCase requestExchangeBatchUseCase;
    @MockitoBean private ApproveExchangeBatchUseCase approveExchangeBatchUseCase;
    @MockitoBean private CollectExchangeBatchUseCase collectExchangeBatchUseCase;
    @MockitoBean private PrepareExchangeBatchUseCase prepareExchangeBatchUseCase;
    @MockitoBean private RejectExchangeBatchUseCase rejectExchangeBatchUseCase;
    @MockitoBean private ShipExchangeBatchUseCase shipExchangeBatchUseCase;
    @MockitoBean private CompleteExchangeBatchUseCase completeExchangeBatchUseCase;
    @MockitoBean private ConvertToRefundBatchUseCase convertToRefundBatchUseCase;
    @MockitoBean private HoldExchangeBatchUseCase holdExchangeBatchUseCase;
    @MockitoBean private AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    @MockitoBean private ExchangeApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("교환 요청 일괄 처리 API")
    class RequestBatchTest {

        @Test
        @DisplayName("교환 요청 일괄 처리 성공")
        void requestBatch_Success() throws Exception {
            // given
            RequestExchangeBatchApiRequest request = ExchangeApiFixtures.requestBatchRequest();
            BatchProcessingResult<String> batchResult =
                    ExchangeApiFixtures.batchSuccessResult(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveCurrentSellerId())
                    .willReturn(ExchangeApiFixtures.DEFAULT_SELLER_ID);
            given(mapper.toRequestExchangeBatchCommand(any(), any(), any(long.class)))
                    .willReturn(null);
            given(requestExchangeBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.REQUEST_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.successCount").value(1))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andExpect(jsonPath("$.data.results").isArray())
                    .andDo(
                            document(
                                    "exchange/request-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("교환 요청 대상 목록"),
                                            fieldWithPath("items[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (프론트: orderId = 내부"
                                                                    + " orderItemId)"),
                                            fieldWithPath("items[].exchangeQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 수량"),
                                            fieldWithPath("items[].reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "교환 사유 유형 (SIZE_CHANGE, COLOR_CHANGE,"
                                                                + " OPTION_CHANGE,"
                                                                + " WRONG_OPTION_SENT, DEFECTIVE,"
                                                                + " OTHER)"),
                                            fieldWithPath("items[].reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 상세 사유")
                                                    .optional(),
                                            fieldWithPath("items[].originalProductId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("원 상품 ID"),
                                            fieldWithPath("items[].originalSkuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("원 SKU 코드"),
                                            fieldWithPath("items[].targetProductGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 상품 그룹 ID"),
                                            fieldWithPath("items[].targetProductId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 상품 ID"),
                                            fieldWithPath("items[].targetSkuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 대상 SKU 코드"),
                                            fieldWithPath("items[].targetQuantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 대상 수량")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("일부 실패가 있어도 200을 반환하고 전체 결과를 포함한다")
        void requestBatch_PartialFailure_Returns200WithMixedResult() throws Exception {
            // given
            RequestExchangeBatchApiRequest request = ExchangeApiFixtures.requestBatchRequest();
            BatchProcessingResult<String> batchResult = ExchangeApiFixtures.batchMixedResult();
            BatchResultApiResponse response = ExchangeApiFixtures.batchResultApiResponse();

            given(accessChecker.resolveCurrentSellerId())
                    .willReturn(ExchangeApiFixtures.DEFAULT_SELLER_ID);
            given(mapper.toRequestExchangeBatchCommand(any(), any(), any(long.class)))
                    .willReturn(null);
            given(requestExchangeBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.REQUEST_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(3))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(1));
        }
    }

    @Nested
    @DisplayName("교환 승인 일괄 처리 API")
    class ApproveBatchTest {

        @Test
        @DisplayName("교환 승인 일괄 처리 성공")
        void approveBatch_Success() throws Exception {
            // given
            ApproveExchangeBatchApiRequest request = ExchangeApiFixtures.approveBatchRequest();
            BatchProcessingResult<String> batchResult =
                    ExchangeApiFixtures.batchSuccessResult(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toApproveExchangeBatchCommand(any(), any(), any())).willReturn(null);
            given(approveExchangeBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.APPROVE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.successCount").value(1))
                    .andDo(
                            document(
                                    "exchange/approve-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("교환 승인 대상 클레임 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 수거 완료 일괄 처리 API")
    class CollectBatchTest {

        @Test
        @DisplayName("교환 수거 완료 일괄 처리 성공")
        void collectBatch_Success() throws Exception {
            // given
            CollectExchangeBatchApiRequest request = ExchangeApiFixtures.collectBatchRequest();
            BatchProcessingResult<String> batchResult =
                    ExchangeApiFixtures.batchSuccessResult(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toCollectExchangeBatchCommand(any(), any(), any())).willReturn(null);
            given(collectExchangeBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.COLLECT_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/collect-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("수거 완료 대상 교환 클레임 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 준비 완료 일괄 처리 API")
    class PrepareBatchTest {

        @Test
        @DisplayName("교환 준비 완료 일괄 처리 성공")
        void prepareBatch_Success() throws Exception {
            // given
            PrepareExchangeBatchApiRequest request = ExchangeApiFixtures.prepareBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toPrepareExchangeBatchCommand(any(), any(), any())).willReturn(null);
            given(prepareExchangeBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.PREPARE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/prepare-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("준비 완료 대상 교환 클레임 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 거절 일괄 처리 API")
    class RejectBatchTest {

        @Test
        @DisplayName("교환 거절 일괄 처리 성공")
        void rejectBatch_Success() throws Exception {
            // given
            RejectExchangeBatchApiRequest request = ExchangeApiFixtures.rejectBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toRejectExchangeBatchCommand(any(), any(), any())).willReturn(null);
            given(rejectExchangeBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.REJECT_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/reject-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("교환 거절 대상 클레임 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 재배송 일괄 처리 API")
    class ShipBatchTest {

        @Test
        @DisplayName("교환 재배송 일괄 처리 성공")
        void shipBatch_Success() throws Exception {
            // given
            ShipExchangeBatchApiRequest request = ExchangeApiFixtures.shipBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toShipCommand(any(), any(), any())).willReturn(null);
            given(shipExchangeBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.SHIP_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/ship-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("재배송 처리 대상 목록"),
                                            fieldWithPath("items[].exchangeClaimId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("교환 클레임 ID"),
                                            fieldWithPath("items[].linkedOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("연결 주문 ID"),
                                            fieldWithPath("items[].deliveryCompany")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송사명"),
                                            fieldWithPath("items[].trackingNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("운송장 번호")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 완료 일괄 처리 API")
    class CompleteBatchTest {

        @Test
        @DisplayName("교환 완료 일괄 처리 성공")
        void completeBatch_Success() throws Exception {
            // given
            CompleteExchangeBatchApiRequest request = ExchangeApiFixtures.completeBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toCompleteCommand(any(), any(), any())).willReturn(null);
            given(completeExchangeBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.COMPLETE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/complete-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("교환 완료 대상 클레임 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 건 환불 전환 일괄 처리 API")
    class ConvertToRefundBatchTest {

        @Test
        @DisplayName("교환 건 환불 전환 일괄 처리 성공")
        void convertToRefundBatch_Success() throws Exception {
            // given
            ConvertToRefundBatchApiRequest request =
                    ExchangeApiFixtures.convertToRefundBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toConvertToRefundCommand(any(), any(), any())).willReturn(null);
            given(convertToRefundBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL
                                                    + ExchangeAdminEndpoints
                                                            .CONVERT_TO_REFUND_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/convert-to-refund-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("환불 전환 대상 교환 클레임 ID 목록")),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
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
    @DisplayName("교환 보류/보류 해제 일괄 처리 API")
    class HoldBatchTest {

        @Test
        @DisplayName("교환 보류 일괄 처리 성공")
        void holdBatch_Hold_Success() throws Exception {
            // given
            HoldExchangeBatchApiRequest request = ExchangeApiFixtures.holdBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toHoldCommand(any(), any(), any())).willReturn(null);
            given(holdExchangeBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.HOLD_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andDo(
                            document(
                                    "exchange/hold-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("exchangeClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("보류/보류 해제 대상 교환 클레임 ID 목록"),
                                            fieldWithPath("isHold")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("true: 보류, false: 보류 해제"),
                                            fieldWithPath("memo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("보류 사유 메모")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.totalCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 처리 건수"),
                                            fieldWithPath("data.successCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("성공 건수"),
                                            fieldWithPath("data.failureCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("data.results")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("개별 항목 결과 목록"),
                                            fieldWithPath("data.results[].id")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 대상 교환 클레임 ID"),
                                            fieldWithPath("data.results[].success")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("성공 여부"),
                                            fieldWithPath("data.results[].errorCode")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 코드 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("data.results[].errorMessage")
                                                    .type(JsonFieldType.NULL)
                                                    .description("에러 메시지 (성공 시 null)")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("교환 보류 해제 일괄 처리 성공")
        void holdBatch_Release_Success() throws Exception {
            // given
            HoldExchangeBatchApiRequest request = ExchangeApiFixtures.releaseBatchRequest();
            BatchResultApiResponse response =
                    ExchangeApiFixtures.batchAllSuccessApiResponse(
                            List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toHoldCommand(any(), any(), any())).willReturn(null);
            given(holdExchangeBatchUseCase.execute(any()))
                    .willReturn(
                            ExchangeApiFixtures.batchSuccessResult(
                                    List.of(ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID)));
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            EXCHANGES_URL + ExchangeAdminEndpoints.HOLD_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.successCount").value(1));
        }
    }

    @Nested
    @DisplayName("교환 수기 메모 등록 API")
    class AddMemoTest {

        @Test
        @DisplayName("교환 수기 메모 등록 성공")
        void addMemo_Success() throws Exception {
            // given
            String exchangeClaimId = ExchangeApiFixtures.DEFAULT_EXCHANGE_CLAIM_ID;
            com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest
                    request = ExchangeApiFixtures.addMemoRequest();
            String historyId = ExchangeApiFixtures.DEFAULT_HISTORY_ID;

            given(accessChecker.resolveCurrentSellerId())
                    .willReturn(ExchangeApiFixtures.DEFAULT_SELLER_ID);
            given(mapper.toAddMemoCommand(any(), any(), any(long.class), any())).willReturn(null);
            given(addClaimHistoryMemoUseCase.execute(any())).willReturn(historyId);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            EXCHANGES_URL + "/{exchangeClaimId}/histories",
                                            exchangeClaimId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.historyId").value(historyId))
                    .andDo(
                            document(
                                    "exchange/add-memo",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("exchangeClaimId")
                                                    .description("교환 클레임 ID")),
                                    requestFields(
                                            fieldWithPath("message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("메모 내용")),
                                    responseFields(
                                            fieldWithPath("data.historyId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성된 이력 ID"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
