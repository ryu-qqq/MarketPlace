package com.ryuqq.marketplace.adapter.in.rest.cancel.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.cancel.CancelApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.ApproveCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.RejectCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.dto.request.SellerCancelBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.cancel.mapper.CancelApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.dto.request.AddClaimHistoryMemoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.cancel.dto.response.CancelDetailResult;
import com.ryuqq.marketplace.application.cancel.port.in.command.ApproveCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.RejectCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.command.SellerCancelBatchUseCase;
import com.ryuqq.marketplace.application.cancel.port.in.query.GetCancelDetailUseCase;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
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
@WebMvcTest(CancelCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("CancelCommandController REST Docs 테스트")
class CancelCommandControllerRestDocsTest {

    private static final String CANCELS_URL = CancelAdminEndpoints.CANCELS;
    private static final String DEFAULT_CANCEL_ID = CancelApiFixtures.DEFAULT_CANCEL_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private SellerCancelBatchUseCase sellerCancelBatchUseCase;
    @MockitoBean private ApproveCancelBatchUseCase approveCancelBatchUseCase;
    @MockitoBean private RejectCancelBatchUseCase rejectCancelBatchUseCase;
    @MockitoBean private AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    @MockitoBean private GetCancelDetailUseCase getCancelDetailUseCase;
    @MockitoBean private CancelApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("판매자 취소 일괄 처리 API")
    class SellerCancelBatchTest {

        @Test
        @DisplayName("판매자 취소 일괄 처리 성공")
        void sellerCancelBatch_Success() throws Exception {
            // given
            SellerCancelBatchApiRequest request = CancelApiFixtures.sellerCancelBatchRequest();
            BatchProcessingResult<String> batchResult =
                    CancelApiFixtures.batchSuccessResult(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));
            BatchResultApiResponse response =
                    CancelApiFixtures.batchAllSuccessApiResponse(
                            List.of(
                                    "01940001-0000-7000-8000-000000000001",
                                    "01940001-0000-7000-8000-000000000002"));

            given(accessChecker.resolveSellerIdOrNull()).willReturn(100L);
            given(mapper.toSellerCancelBatchCommand(any(), anyString(), anyLong()))
                    .willReturn(null);
            given(sellerCancelBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.SELLER_CANCEL_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andExpect(jsonPath("$.data.results").isArray())
                    .andDo(
                            document(
                                    "cancel/seller-cancel-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("취소 대상 목록"),
                                            fieldWithPath("items[].orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 상품 ID (V4: orderId = 내부"
                                                                    + " orderItemId)"),
                                            fieldWithPath("items[].cancelQty")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 수량"),
                                            fieldWithPath("items[].reasonType")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "취소 사유 유형 (OUT_OF_STOCK, CHANGE_OF_MIND"
                                                                    + " 등)"),
                                            fieldWithPath("items[].reasonDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("취소 상세 사유")
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
                                                    .description("처리 대상 ID"),
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
        @DisplayName("items가 빈 배열이면 400을 반환한다")
        void sellerCancelBatch_EmptyItems_Returns400() throws Exception {
            // given
            SellerCancelBatchApiRequest request = new SellerCancelBatchApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.SELLER_CANCEL_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("일부 항목 실패 시 혼합 결과를 반환한다")
        void sellerCancelBatch_PartialFailure_ReturnsMixedResult() throws Exception {
            // given
            SellerCancelBatchApiRequest request = CancelApiFixtures.sellerCancelBatchRequest();
            BatchProcessingResult<String> batchResult = CancelApiFixtures.batchMixedResult();
            BatchResultApiResponse response = CancelApiFixtures.batchResultApiResponse();

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toSellerCancelBatchCommand(any(), anyString(), anyLong()))
                    .willReturn(null);
            given(sellerCancelBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.SELLER_CANCEL_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(3))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(1));
        }
    }

    @Nested
    @DisplayName("취소 승인 일괄 처리 API")
    class ApproveCancelBatchTest {

        @Test
        @DisplayName("취소 승인 일괄 처리 성공")
        void approveBatch_Success() throws Exception {
            // given
            ApproveCancelBatchApiRequest request = CancelApiFixtures.approveBatchRequest();
            BatchProcessingResult<String> batchResult =
                    CancelApiFixtures.batchSuccessResult(request.cancelIds());
            BatchResultApiResponse response =
                    CancelApiFixtures.batchAllSuccessApiResponse(request.cancelIds());

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toApproveCancelBatchCommand(any(), anyString(), any())).willReturn(null);
            given(approveCancelBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.APPROVE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andDo(
                            document(
                                    "cancel/approve-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("cancelIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("승인 대상 취소 ID 목록")),
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
                                                    .description("처리 대상 취소 ID"),
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
        @DisplayName("cancelIds가 빈 배열이면 400을 반환한다")
        void approveBatch_EmptyCancelIds_Returns400() throws Exception {
            // given
            ApproveCancelBatchApiRequest request = new ApproveCancelBatchApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.APPROVE_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("취소 거절 일괄 처리 API")
    class RejectCancelBatchTest {

        @Test
        @DisplayName("취소 거절 일괄 처리 성공")
        void rejectBatch_Success() throws Exception {
            // given
            RejectCancelBatchApiRequest request = CancelApiFixtures.rejectBatchRequest();
            BatchProcessingResult<String> batchResult =
                    CancelApiFixtures.batchSuccessResult(request.cancelIds());
            BatchResultApiResponse response =
                    CancelApiFixtures.batchAllSuccessApiResponse(request.cancelIds());

            given(accessChecker.resolveSellerIdOrNull()).willReturn(null);
            given(mapper.toRejectCancelBatchCommand(any(), anyString(), any())).willReturn(null);
            given(rejectCancelBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.REJECT_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andDo(
                            document(
                                    "cancel/reject-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("cancelIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("거절 대상 취소 ID 목록")),
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
                                                    .description("처리 대상 취소 ID"),
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
        @DisplayName("cancelIds가 빈 배열이면 400을 반환한다")
        void rejectBatch_EmptyCancelIds_Returns400() throws Exception {
            // given
            RejectCancelBatchApiRequest request = new RejectCancelBatchApiRequest(List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.REJECT_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("취소 수기 메모 등록 API")
    class AddMemoTest {

        @Test
        @DisplayName("취소 수기 메모 등록 성공 - 201을 반환한다")
        void addMemo_Success() throws Exception {
            // given
            AddClaimHistoryMemoApiRequest request = CancelApiFixtures.addMemoRequest();
            CancelDetailResult detailResult = CancelApiFixtures.detailResult(DEFAULT_CANCEL_ID);

            given(getCancelDetailUseCase.execute(DEFAULT_CANCEL_ID)).willReturn(detailResult);
            given(accessChecker.resolveSellerIdOrNull()).willReturn(100L);
            given(mapper.toAddMemoCommand(anyString(), any(), anyLong(), anyString()))
                    .willReturn(null);
            given(addClaimHistoryMemoUseCase.execute(any())).willReturn("HIST-001");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.HISTORIES,
                                            DEFAULT_CANCEL_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.historyId").value("HIST-001"))
                    .andDo(
                            document(
                                    "cancel/add-memo",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("cancelId")
                                                    .description("취소 ID (UUIDv7)")),
                                    requestFields(
                                            fieldWithPath("message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수기 메모 내용")),
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

        @Test
        @DisplayName("메모 내용이 빈 문자열이면 400을 반환한다")
        void addMemo_BlankMessage_Returns400() throws Exception {
            // given
            AddClaimHistoryMemoApiRequest request = new AddClaimHistoryMemoApiRequest("");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            CANCELS_URL + CancelAdminEndpoints.HISTORIES,
                                            DEFAULT_CANCEL_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
