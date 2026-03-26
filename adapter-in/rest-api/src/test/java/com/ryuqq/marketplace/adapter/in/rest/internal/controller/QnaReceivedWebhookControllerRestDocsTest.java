package com.ryuqq.marketplace.adapter.in.rest.internal.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
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
import com.ryuqq.marketplace.adapter.in.rest.internal.InternalWebhookEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.QnaReceivedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.QnaReceivedWebhookRequest.QnaItemRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.inboundqna.dto.external.ExternalQnaPayload;
import com.ryuqq.marketplace.application.inboundqna.dto.result.QnaWebhookResult;
import com.ryuqq.marketplace.application.inboundqna.port.in.command.ReceiveQnaWebhookUseCase;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import java.time.Instant;
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
@WebMvcTest(QnaReceivedWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("QnaReceivedWebhookController REST Docs 테스트")
class QnaReceivedWebhookControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ReceiveQnaWebhookUseCase receiveQnaWebhookUseCase;
    @MockitoBean private InternalWebhookApiMapper mapper;
    @MockitoBean private ShopReadManager shopReadManager;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("QnA 수신 웹훅 API")
    class HandleQnaReceivedTest {

        @Test
        @DisplayName("QnA 수신 성공")
        void handleQnaReceived_Success() throws Exception {
            // given
            QnaItemRequest qnaItem =
                    new QnaItemRequest(
                            1001L,
                            null,
                            "PRODUCT",
                            "사이즈 문의",
                            "사이즈가 어떻게 되나요?",
                            "구매자A",
                            100L,
                            200L,
                            null,
                            Instant.parse("2026-03-26T01:00:00Z"));

            QnaReceivedWebhookRequest request =
                    new QnaReceivedWebhookRequest(1L, List.of(qnaItem));

            Shop shop = mock(Shop.class);
            given(shop.idValue()).willReturn(10L);

            given(shopReadManager.getBySalesChannelIdAndAccountId(anyLong(), anyString()))
                    .willReturn(shop);
            given(mapper.toExternalQnaPayloads(any(QnaReceivedWebhookRequest.class)))
                    .willReturn(
                            List.of(
                                    new ExternalQnaPayload(
                                            "1001",
                                            null,
                                            "PRODUCT",
                                            "사이즈 문의",
                                            "사이즈가 어떻게 되나요?",
                                            "구매자A",
                                            "200",
                                            null,
                                            "{}")));
            given(receiveQnaWebhookUseCase.execute(any(), anyLong(), anyLong()))
                    .willReturn(QnaWebhookResult.of(1, 1, 0, 0));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.QNA_RECEIVED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1))
                    .andExpect(jsonPath("$.data.created").value(1))
                    .andExpect(jsonPath("$.data.duplicated").value(0))
                    .andExpect(jsonPath("$.data.failed").value(0))
                    .andDo(
                            document(
                                    "internal/qna-received",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("qnas")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("QnA 목록"),
                                            fieldWithPath("qnas[].externalQnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("외부 QnA ID"),
                                            fieldWithPath("qnas[].parentExternalQnaId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("부모 QnA ID (대댓글일 때)")
                                                    .optional(),
                                            fieldWithPath("qnas[].qnaType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("문의 유형"),
                                            fieldWithPath("qnas[].questionTitle")
                                                    .type(JsonFieldType.STRING)
                                                    .description("문의 제목")
                                                    .optional(),
                                            fieldWithPath("qnas[].questionContent")
                                                    .type(JsonFieldType.STRING)
                                                    .description("문의 내용"),
                                            fieldWithPath("qnas[].questionAuthor")
                                                    .type(JsonFieldType.STRING)
                                                    .description("작성자명")
                                                    .optional(),
                                            fieldWithPath("qnas[].sellerId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("셀러 ID"),
                                            fieldWithPath("qnas[].externalProductId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("외부 상품 ID")
                                                    .optional(),
                                            fieldWithPath("qnas[].externalOrderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("외부 주문 ID")
                                                    .optional(),
                                            fieldWithPath("qnas[].questionedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("문의 작성 시각 (ISO 8601)")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.total")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 수신 건수"),
                                            fieldWithPath("data.created")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("생성 완료 건수"),
                                            fieldWithPath("data.duplicated")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("중복 건수"),
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
        @DisplayName("빈 qnas 배열이면 400을 반환한다")
        void handleQnaReceived_EmptyQnas_Returns400() throws Exception {
            // given
            QnaReceivedWebhookRequest request =
                    new QnaReceivedWebhookRequest(1L, List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            InternalWebhookEndpoints.QNA_RECEIVED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
