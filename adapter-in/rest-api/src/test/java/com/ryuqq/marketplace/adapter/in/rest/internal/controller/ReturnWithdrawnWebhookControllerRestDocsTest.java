package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.ReturnWithdrawnWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.ClaimSyncWebhookResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.claimsync.dto.result.ClaimSyncResult;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceiveClaimWebhookUseCase;
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
@WebMvcTest(ReturnWithdrawnWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ReturnWithdrawnWebhookController REST Docs 테스트")
class ReturnWithdrawnWebhookControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ReceiveClaimWebhookUseCase receiveClaimWebhookUseCase;
    @MockitoBean private InternalWebhookApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("반품 철회 웹훅 수신 API")
    class HandleReturnWithdrawnTest {

        @Test
        @DisplayName("반품 철회 웹훅 수신 성공")
        void handleReturnWithdrawn_Success() throws Exception {
            // given
            ReturnWithdrawnWebhookRequest request =
                    InternalWebhookApiFixtures.returnWithdrawnRequest();
            ClaimSyncResult syncResult = InternalWebhookApiFixtures.returnSyncResult();
            ClaimSyncWebhookResponse response =
                    InternalWebhookApiFixtures.returnSyncWebhookResponse();

            given(mapper.toExternalClaimPayloads(any(ReturnWithdrawnWebhookRequest.class)))
                    .willReturn(List.of());
            given(receiveClaimWebhookUseCase.execute(any(), anyLong())).willReturn(syncResult);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.RETURN_WITHDRAWN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalProcessed").value(1))
                    .andExpect(jsonPath("$.data.cancelSynced").value(0))
                    .andExpect(jsonPath("$.data.refundSynced").value(1))
                    .andExpect(jsonPath("$.data.failed").value(0))
                    .andDo(
                            document(
                                    "internal/return-withdrawn",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("externalOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 주문번호"),
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("반품 철회 아이템 목록"),
                                            fieldWithPath("items[].externalProductOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품주문 ID")),
                                    responseFields(
                                            fieldWithPath("data.totalProcessed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 처리 건수"),
                                            fieldWithPath("data.cancelSynced")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 동기화 건수"),
                                            fieldWithPath("data.refundSynced")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("반품 동기화 건수"),
                                            fieldWithPath("data.exchangeSynced")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("교환 동기화 건수"),
                                            fieldWithPath("data.skipped")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("스킵 건수"),
                                            fieldWithPath("data.failed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실패 건수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("externalOrderId가 blank이면 400을 반환한다")
        void handleReturnWithdrawn_BlankOrderId_Returns400() throws Exception {
            // given
            ReturnWithdrawnWebhookRequest request =
                    new ReturnWithdrawnWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            "",
                            List.of(InternalWebhookApiFixtures.returnWithdrawnItemRequest()));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.RETURN_WITHDRAWN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("items가 빈 배열이면 400을 반환한다")
        void handleReturnWithdrawn_EmptyItems_Returns400() throws Exception {
            // given
            ReturnWithdrawnWebhookRequest request =
                    new ReturnWithdrawnWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_ID,
                            List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.RETURN_WITHDRAWN)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
