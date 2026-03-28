package com.ryuqq.marketplace.adapter.in.rest.order.controller;

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
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderAdminEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.order.OrderApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.order.mapper.OrderCommandApiMapper;
import com.ryuqq.marketplace.application.claimhistory.port.in.command.AddClaimHistoryMemoUseCase;
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
@WebMvcTest(OrderCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("OrderCommandController REST Docs 테스트")
class OrderCommandControllerRestDocsTest {

    private static final String ORDERS_URL = OrderAdminEndpoints.ORDERS;
    private static final String ORDER_ITEM_ID = OrderApiFixtures.DEFAULT_ORDER_ITEM_ID_STR;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AddClaimHistoryMemoUseCase addClaimHistoryMemoUseCase;
    @MockitoBean private OrderCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private MarketAccessChecker accessChecker;

    @Nested
    @DisplayName("주문 수기 메모 등록 API")
    class AddMemoTest {

        @Test
        @DisplayName("주문 수기 메모 등록 성공 - 201을 반환한다")
        void addMemo_Success() throws Exception {
            // given
            AddClaimHistoryMemoApiRequest request = OrderApiFixtures.addMemoRequest();

            given(accessChecker.resolveActorInfo())
                    .willReturn(new MarketAccessChecker.ActorInfo(100L, "seller01"));
            given(mapper.toAddMemoCommand(any(), any(), any())).willReturn(null);
            given(addClaimHistoryMemoUseCase.execute(any())).willReturn("HIST-ORDER-001");

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ORDERS_URL + OrderAdminEndpoints.HISTORIES,
                                            ORDER_ITEM_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.data.historyId").value("HIST-ORDER-001"))
                    .andDo(
                            document(
                                    "order/add-memo",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("orderItemId")
                                                    .description("상품주문 ID (UUIDv7)")),
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
                                            ORDERS_URL + OrderAdminEndpoints.HISTORIES,
                                            ORDER_ITEM_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("메모 내용이 null이면 400을 반환한다")
        void addMemo_NullMessage_Returns400() throws Exception {
            // given
            String requestBody = "{\"message\": null}";

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            ORDERS_URL + OrderAdminEndpoints.HISTORIES,
                                            ORDER_ITEM_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }
}
