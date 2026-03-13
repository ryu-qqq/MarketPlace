package com.ryuqq.marketplace.adapter.in.rest.outboundproduct.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
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

import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.OmsEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.command.SyncProductsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.RetrySyncApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.dto.response.SyncProductsApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.outboundproduct.mapper.OmsProductCommandApiMapper;
import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import com.ryuqq.marketplace.application.outboundproduct.port.in.command.ManualSyncProductsUseCase;
import com.ryuqq.marketplace.application.outboundproduct.port.in.command.RetryOutboundSyncUseCase;
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
@WebMvcTest(OmsProductCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OmsProductCommandController REST Docs 테스트")
class OmsProductCommandControllerRestDocsTest {

    private static final String RETRY_URL = OmsEndpoints.SYNC_HISTORY_RETRY;
    private static final String SYNC_URL = OmsEndpoints.SYNC;
    private static final long OUTBOX_ID = 202L;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private RetryOutboundSyncUseCase retryOutboundSyncUseCase;
    @MockitoBean private ManualSyncProductsUseCase manualSyncProductsUseCase;
    @MockitoBean private OmsProductCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("연동 재처리 API")
    class RetrySyncHistoryTest {

        @Test
        @DisplayName("유효한 outboxId로 재처리 요청 시 200과 ACCEPTED 응답을 반환한다")
        void retrySyncHistory_ValidOutboxId_Returns200WithAccepted() throws Exception {
            // given
            RetrySyncApiResponse response = RetrySyncApiResponse.of(OUTBOX_ID);

            willDoNothing().given(retryOutboundSyncUseCase).execute(OUTBOX_ID);
            given(mapper.toRetryResponse(OUTBOX_ID)).willReturn(response);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.post(RETRY_URL, OUTBOX_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.outboxId").value(OUTBOX_ID))
                    .andExpect(jsonPath("$.data.status").value("ACCEPTED"))
                    .andDo(
                            document(
                                    "oms-product/retry-sync-history",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("outboxId")
                                                    .description("재처리 대상 Outbox ID")),
                                    responseFields(
                                            fieldWithPath("data.outboxId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("재처리 대상 Outbox ID"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 상태 (항상 ACCEPTED)"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("UseCase에서 예외가 발생하면 500을 반환한다")
        void retrySyncHistory_UseCaseThrowsException_Returns500() throws Exception {
            // given
            willThrow(new RuntimeException("연동 재처리 중 오류 발생"))
                    .given(retryOutboundSyncUseCase)
                    .execute(anyLong());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.post(RETRY_URL, OUTBOX_ID))
                    .andExpect(status().isInternalServerError());
        }

        @Test
        @DisplayName("UseCase에서 IllegalArgumentException 발생 시 400을 반환한다")
        void retrySyncHistory_UseCaseThrowsIllegalArgumentException_Returns400() throws Exception {
            // given
            willThrow(new IllegalArgumentException("존재하지 않는 Outbox ID"))
                    .given(retryOutboundSyncUseCase)
                    .execute(anyLong());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.post(RETRY_URL, OUTBOX_ID))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("UseCase에서 IllegalStateException 발생 시 409를 반환한다")
        void retrySyncHistory_UseCaseThrowsIllegalStateException_Returns409() throws Exception {
            // given
            willThrow(new IllegalStateException("이미 처리 중인 Outbox"))
                    .given(retryOutboundSyncUseCase)
                    .execute(anyLong());

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.post(RETRY_URL, OUTBOX_ID))
                    .andExpect(status().isConflict());
        }
    }

    @Nested
    @DisplayName("상품 외부몰 전송 API")
    class SyncProductsTest {

        @Test
        @DisplayName("유효한 요청으로 상품 전송 시 200과 결과 응답을 반환한다")
        void syncProducts_ValidRequest_Returns200WithResult() throws Exception {
            // given
            SyncProductsApiResponse response = new SyncProductsApiResponse(3, 2, 1, "ACCEPTED");

            given(mapper.toCommand(any(SyncProductsApiRequest.class)))
                    .willReturn(
                            new ManualSyncProductsCommand(
                                    java.util.List.of(1L, 2L, 3L), java.util.List.of(10L, 20L)));
            given(manualSyncProductsUseCase.execute(any(ManualSyncProductsCommand.class)))
                    .willReturn(ManualSyncResult.of(3, 2, 1));
            given(mapper.toSyncResponse(any(ManualSyncResult.class))).willReturn(response);

            String requestBody =
                    """
                    {
                        "productIds": [1, 2, 3],
                        "shopId": 10
                    }
                    """;

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(SYNC_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.createCount").value(3))
                    .andExpect(jsonPath("$.data.updateCount").value(2))
                    .andExpect(jsonPath("$.data.skippedCount").value(1))
                    .andExpect(jsonPath("$.data.status").value("ACCEPTED"))
                    .andDo(
                            document(
                                    "oms-product/sync-products",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("productIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("전송 대상 상품그룹 ID 목록"),
                                            fieldWithPath("shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("쇼핑몰 ID")),
                                    responseFields(
                                            fieldWithPath("data.createCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("신규 생성 Outbox 수"),
                                            fieldWithPath("data.updateCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수정 Outbox 수"),
                                            fieldWithPath("data.skippedCount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("스킵된 수 (미연결/중복)"),
                                            fieldWithPath("data.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("처리 상태"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("UseCase에서 예외가 발생하면 500을 반환한다")
        void syncProducts_UseCaseThrowsException_Returns500() throws Exception {
            // given
            given(mapper.toCommand(any(SyncProductsApiRequest.class)))
                    .willReturn(
                            new ManualSyncProductsCommand(
                                    java.util.List.of(1L), java.util.List.of(10L)));
            willThrow(new RuntimeException("전송 처리 중 오류 발생"))
                    .given(manualSyncProductsUseCase)
                    .execute(any(ManualSyncProductsCommand.class));

            String requestBody =
                    """
                    {
                        "productIds": [1],
                        "shopId": 10
                    }
                    """;

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(SYNC_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isInternalServerError());
        }
    }
}
