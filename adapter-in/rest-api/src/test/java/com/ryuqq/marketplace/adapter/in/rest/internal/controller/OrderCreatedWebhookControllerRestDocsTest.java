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
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.request.OrderCreatedWebhookRequest;
import com.ryuqq.marketplace.adapter.in.rest.internal.dto.response.OrderCreatedWebhookResponse;
import com.ryuqq.marketplace.adapter.in.rest.internal.mapper.InternalWebhookApiMapper;
import com.ryuqq.marketplace.application.inboundorder.dto.external.ExternalOrderPayload;
import com.ryuqq.marketplace.application.inboundorder.dto.result.InboundOrderPollingResult;
import com.ryuqq.marketplace.application.inboundorder.port.in.command.ReceiveOrderCreatedWebhookUseCase;
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
@WebMvcTest(OrderCreatedWebhookController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OrderCreatedWebhookController REST Docs 테스트")
class OrderCreatedWebhookControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ReceiveOrderCreatedWebhookUseCase receiveOrderCreatedWebhookUseCase;
    @MockitoBean private InternalWebhookApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("주문 생성 웹훅 수신 API")
    class HandleOrderCreatedTest {

        @Test
        @DisplayName("주문 생성 웹훅 수신 성공")
        void handleOrderCreated_Success() throws Exception {
            // given
            OrderCreatedWebhookRequest request = InternalWebhookApiFixtures.orderCreatedRequest();
            InboundOrderPollingResult pollingResult = InternalWebhookApiFixtures.pollingResult();
            OrderCreatedWebhookResponse response =
                    InternalWebhookApiFixtures.orderCreatedWebhookResponse();

            given(mapper.toExternalOrderPayload(any(OrderCreatedWebhookRequest.class)))
                    .willReturn(
                            new ExternalOrderPayload(
                                    request.externalOrderNo(),
                                    request.orderedAt(),
                                    request.buyerName(),
                                    request.buyerEmail(),
                                    request.buyerPhone(),
                                    request.paymentMethod(),
                                    request.totalPaymentAmount(),
                                    request.paidAt(),
                                    java.util.List.of()));
            given(receiveOrderCreatedWebhookUseCase.execute(any(), anyLong(), anyLong()))
                    .willReturn(pollingResult);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(InternalWebhookEndpoints.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.total").value(1))
                    .andExpect(jsonPath("$.data.created").value(1))
                    .andExpect(jsonPath("$.data.pending").value(0))
                    .andExpect(jsonPath("$.data.duplicated").value(0))
                    .andExpect(jsonPath("$.data.failed").value(0))
                    .andDo(
                            document(
                                    "internal/order-created",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("샵 ID"),
                                            fieldWithPath("externalOrderNo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 주문번호"),
                                            fieldWithPath("orderedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문일시 (ISO 8601)")
                                                    .optional(),
                                            fieldWithPath("buyerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자명")
                                                    .optional(),
                                            fieldWithPath("buyerEmail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 이메일")
                                                    .optional(),
                                            fieldWithPath("buyerPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 전화번호")
                                                    .optional(),
                                            fieldWithPath("paymentMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제수단")
                                                    .optional(),
                                            fieldWithPath("totalPaymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 결제금액"),
                                            fieldWithPath("paidAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제일시 (ISO 8601)")
                                                    .optional(),
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("주문 아이템 목록"),
                                            fieldWithPath("items[].externalProductOrderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품주문 ID"),
                                            fieldWithPath("items[].externalProductId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 ID")
                                                    .optional(),
                                            fieldWithPath("items[].externalOptionId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션 ID")
                                                    .optional(),
                                            fieldWithPath("items[].externalProductName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품명")
                                                    .optional(),
                                            fieldWithPath("items[].externalOptionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션명")
                                                    .optional(),
                                            fieldWithPath("items[].externalImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 이미지 URL")
                                                    .optional(),
                                            fieldWithPath("items[].unitPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("개당 판매가"),
                                            fieldWithPath("items[].quantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수량"),
                                            fieldWithPath("items[].totalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("합계 금액"),
                                            fieldWithPath("items[].discountAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인 금액"),
                                            fieldWithPath("items[].paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실결제 금액"),
                                            fieldWithPath("items[].receiverName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인명")
                                                    .optional(),
                                            fieldWithPath("items[].receiverPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 전화번호")
                                                    .optional(),
                                            fieldWithPath("items[].receiverZipCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("우편번호")
                                                    .optional(),
                                            fieldWithPath("items[].receiverAddress")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주소")
                                                    .optional(),
                                            fieldWithPath("items[].receiverAddressDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상세주소")
                                                    .optional(),
                                            fieldWithPath("items[].deliveryRequest")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 요청사항")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.total")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 수신 건수"),
                                            fieldWithPath("data.created")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("변환 완료 건수"),
                                            fieldWithPath("data.pending")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("매핑 대기 건수"),
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
        @DisplayName("externalOrderNo가 blank이면 400을 반환한다")
        void handleOrderCreated_BlankOrderNo_Returns400() throws Exception {
            // given
            OrderCreatedWebhookRequest request =
                    new OrderCreatedWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            InternalWebhookApiFixtures.DEFAULT_SHOP_ID,
                            "",
                            null,
                            null,
                            null,
                            null,
                            null,
                            0,
                            null,
                            java.util.List.of(
                                    InternalWebhookApiFixtures.orderCreatedItemRequest()));

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(InternalWebhookEndpoints.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("items가 빈 배열이면 400을 반환한다")
        void handleOrderCreated_EmptyItems_Returns400() throws Exception {
            // given
            OrderCreatedWebhookRequest request =
                    new OrderCreatedWebhookRequest(
                            InternalWebhookApiFixtures.DEFAULT_SALES_CHANNEL_ID,
                            InternalWebhookApiFixtures.DEFAULT_SHOP_ID,
                            InternalWebhookApiFixtures.DEFAULT_EXTERNAL_ORDER_NO,
                            null,
                            null,
                            null,
                            null,
                            null,
                            0,
                            null,
                            java.util.List.of());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(InternalWebhookEndpoints.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }
}
