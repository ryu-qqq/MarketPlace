package com.ryuqq.marketplace.adapter.in.rest.refund.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.refund.RefundApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.ApproveRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.HoldRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RejectRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.dto.request.RequestRefundBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.refund.mapper.RefundApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.refund.port.in.command.ApproveRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.HoldRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.RejectRefundBatchUseCase;
import com.ryuqq.marketplace.application.refund.port.in.command.RequestRefundBatchUseCase;
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
@WebMvcTest(RefundCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("RefundCommandController REST Docs 테스트")
class RefundCommandControllerRestDocsTest {

    private static final String REFUNDS_URL = RefundAdminEndpoints.REFUNDS;
    private static final String DEFAULT_REFUND_CLAIM_ID = RefundApiFixtures.DEFAULT_REFUND_CLAIM_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RequestRefundBatchUseCase requestRefundBatchUseCase;
    @MockitoBean private ApproveRefundBatchUseCase approveRefundBatchUseCase;
    @MockitoBean private RejectRefundBatchUseCase rejectRefundBatchUseCase;
    @MockitoBean private HoldRefundBatchUseCase holdRefundBatchUseCase;
    @MockitoBean private AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    @MockitoBean private RefundApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @MockitoBean
    private com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("환불 요청 일괄 처리 API")
    class RequestBatchTest {

        @Test
        @DisplayName("환불 요청 일괄 처리 성공")
        void requestBatch_Success() throws Exception {
            // given
            RequestRefundBatchApiRequest request = RefundApiFixtures.requestBatchRequest();
            BatchProcessingResult<String> batchResult =
                    RefundApiFixtures.batchSuccessResult(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));
            BatchResultApiResponse response =
                    RefundApiFixtures.batchAllSuccessApiResponse(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));

            given(accessChecker.resolveCurrentSellerId()).willReturn(1L);
            given(mapper.toRequestRefundBatchCommand(any(), any(), any(long.class)))
                    .willReturn(null);
            given(requestRefundBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            REFUNDS_URL + RefundAdminEndpoints.REQUEST_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andExpect(jsonPath("$.data.results").isArray())
                    .andDo(
                            document(
                                    "refund/request-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("환불 요청 대상 목록"),
                                            fieldWithPath("items[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 ID (프론트: orderId = 내부"
                                                                    + " orderItemId)"),
                                            fieldWithPath("items[].refundQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("환불 수량"),
                                            fieldWithPath("items[].reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 사유 유형"),
                                            fieldWithPath("items[].reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("환불 상세 사유")
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
                                                    .description("처리 대상 환불 클레임 ID"),
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
            RequestRefundBatchApiRequest request = RefundApiFixtures.requestBatchRequest();
            BatchProcessingResult<String> batchResult = RefundApiFixtures.batchMixedResult();
            BatchResultApiResponse response = RefundApiFixtures.batchResultApiResponse();

            given(accessChecker.resolveCurrentSellerId()).willReturn(1L);
            given(mapper.toRequestRefundBatchCommand(any(), any(), any(long.class)))
                    .willReturn(null);
            given(requestRefundBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            REFUNDS_URL + RefundAdminEndpoints.REQUEST_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(3))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(1));
        }
    }

    @Nested
    @DisplayName("환불 승인 일괄 처리 API")
    class ApproveBatchTest {

        @Test
        @DisplayName("환불 승인 일괄 처리 성공")
        void approveBatch_Success() throws Exception {
            // given
            ApproveRefundBatchApiRequest request = RefundApiFixtures.approveBatchRequest();
            BatchProcessingResult<String> batchResult =
                    RefundApiFixtures.batchSuccessResult(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));
            BatchResultApiResponse response =
                    RefundApiFixtures.batchAllSuccessApiResponse(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toApproveRefundBatchCommand(any(), any(), any())).willReturn(null);
            given(approveRefundBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            REFUNDS_URL + RefundAdminEndpoints.APPROVE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andDo(
                            document(
                                    "refund/approve-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("refundClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("환불 클레임 ID 목록")),
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
                                                    .description("처리 대상 환불 클레임 ID"),
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
    @DisplayName("환불 거절 일괄 처리 API")
    class RejectBatchTest {

        @Test
        @DisplayName("환불 거절 일괄 처리 성공")
        void rejectBatch_Success() throws Exception {
            // given
            RejectRefundBatchApiRequest request = RefundApiFixtures.rejectBatchRequest();
            BatchProcessingResult<String> batchResult =
                    RefundApiFixtures.batchSuccessResult(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));
            BatchResultApiResponse response =
                    RefundApiFixtures.batchAllSuccessApiResponse(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toRejectRefundBatchCommand(any(), any(), any())).willReturn(null);
            given(rejectRefundBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            REFUNDS_URL + RefundAdminEndpoints.REJECT_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andDo(
                            document(
                                    "refund/reject-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("refundClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("환불 클레임 ID 목록")),
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
                                                    .description("처리 대상 환불 클레임 ID"),
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
    @DisplayName("환불 보류/보류 해제 일괄 처리 API")
    class HoldBatchTest {

        @Test
        @DisplayName("환불 보류 일괄 처리 성공")
        void holdBatch_Hold_Success() throws Exception {
            // given
            HoldRefundBatchApiRequest request = RefundApiFixtures.holdBatchRequest();
            BatchProcessingResult<String> batchResult =
                    RefundApiFixtures.batchSuccessResult(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));
            BatchResultApiResponse response =
                    RefundApiFixtures.batchAllSuccessApiResponse(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toHoldCommand(any(), any(), any())).willReturn(null);
            given(holdRefundBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            REFUNDS_URL + RefundAdminEndpoints.HOLD_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andDo(
                            document(
                                    "refund/hold-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("refundClaimIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("환불 클레임 ID 목록"),
                                            fieldWithPath("isHold")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("보류 여부 (true: 보류, false: 보류 해제)"),
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
                                                    .description("처리 대상 환불 클레임 ID"),
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
        @DisplayName("환불 보류 해제 일괄 처리 성공")
        void holdBatch_Release_Success() throws Exception {
            // given
            HoldRefundBatchApiRequest request = RefundApiFixtures.releaseBatchRequest();
            BatchProcessingResult<String> batchResult =
                    RefundApiFixtures.batchSuccessResult(
                            List.of("01940001-0000-7000-8000-000000000001"));
            BatchResultApiResponse response =
                    RefundApiFixtures.batchAllSuccessApiResponse(
                            List.of("01940001-0000-7000-8000-000000000001"));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toHoldCommand(any(), any(), any())).willReturn(null);
            given(holdRefundBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.patch(
                                            REFUNDS_URL + RefundAdminEndpoints.HOLD_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(1))
                    .andExpect(jsonPath("$.data.successCount").value(1))
                    .andExpect(jsonPath("$.data.failureCount").value(0));
        }
    }

    @Nested
    @DisplayName("환불 수기 메모 등록 API")
    class AddMemoTest {

        @Test
        @DisplayName("환불 수기 메모 등록 성공")
        void addMemo_Success() throws Exception {
            // given
            AddClaimHistoryMemoApiRequest request = RefundApiFixtures.addMemoRequest();
            String historyId = "HIST-001";

            given(accessChecker.resolveCurrentSellerId()).willReturn(1L);
            given(mapper.toAddMemoCommand(any(), any(), any(long.class), any())).willReturn(null);
            given(addClaimHistoryMemoUseCase.execute(any())).willReturn(historyId);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            REFUNDS_URL + RefundAdminEndpoints.HISTORIES,
                                            DEFAULT_REFUND_CLAIM_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.historyId").value(historyId))
                    .andDo(
                            document(
                                    "refund/add-memo",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("refundClaimId")
                                                    .description("환불 클레임 ID")),
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
