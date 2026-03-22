package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.willDoNothing;
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
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.PurchaseConfirmedWebhookRequest;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceivePurchaseConfirmedWebhookUseCase;
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
@WebMvcTest(PurchaseConfirmedWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("PurchaseConfirmedWebhookController REST Docs 테스트")
class PurchaseConfirmedWebhookControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean
    private ReceivePurchaseConfirmedWebhookUseCase receivePurchaseConfirmedWebhookUseCase;

    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("구매 확정 웹훅 수신 API")
    class HandlePurchaseConfirmedTest {

        @Test
        @DisplayName("구매 확정 웹훅 수신 성공")
        void handlePurchaseConfirmed_Success() throws Exception {
            // given
            PurchaseConfirmedWebhookRequest request =
                    InternalWebhookApiFixtures.purchaseConfirmedRequest();

            willDoNothing().given(receivePurchaseConfirmedWebhookUseCase).execute(anyLong(), any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.PURCHASE_CONFIRMED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").doesNotExist())
                    .andDo(
                            document(
                                    "internal/purchase-confirmed",
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
                                                    .description("구매 확정 아이템 목록"),
                                            fieldWithPath("items[].externalProductOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품주문 ID")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (없음)")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("externalOrderId가 blank이면 400을 반환한다")
        void handlePurchaseConfirmed_BlankOrderId_Returns400() throws Exception {
            // given
            PurchaseConfirmedWebhookRequest request =
                    new PurchaseConfirmedWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            "",
                            List.of(InternalWebhookApiFixtures.purchaseConfirmedItemRequest()));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.PURCHASE_CONFIRMED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("items가 빈 배열이면 400을 반환한다")
        void handlePurchaseConfirmed_EmptyItems_Returns400() throws Exception {
            // given
            PurchaseConfirmedWebhookRequest request =
                    new PurchaseConfirmedWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_ID,
                            List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.PURCHASE_CONFIRMED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
