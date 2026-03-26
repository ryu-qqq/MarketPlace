package com.ryuqq.marketplace.adapter.in.rest.legacy.order.controller;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.common.security.MarketAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto.LegacyApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.common.security.LegacyAccessChecker;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.LegacyOrderEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.request.LegacyUpdateOrderRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.dto.response.LegacyUpdateOrderResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper.LegacyOrderCommandApiMapper;
import com.ryuqq.marketplace.adapter.in.rest.legacy.order.mapper.LegacyOrderQueryApiMapper;
import com.ryuqq.marketplace.application.legacy.order.port.in.command.LegacyOrderUpdateUseCase;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderListQueryUseCase;
import com.ryuqq.marketplace.application.legacy.order.port.in.query.LegacyOrderQueryUseCase;
import java.util.List;
import org.junit.jupiter.api.Disabled;
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

/**
 * LegacyOrder Query/Command Controller REST Docs 테스트.
 *
 * <p>현재 컨트롤러 구현이 미완료(UnsupportedOperationException) 상태입니다. 테스트는 API 계약 설계 문서 역할을 합니다. 구현 완료
 * 후 @Disabled 제거 및 Mock 설정을 추가하세요.
 */
@Tag("unit")
@WebMvcTest({LegacyOrderQueryController.class, LegacyOrderCommandController.class})
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("LegacyOrderController REST Docs 테스트")
class LegacyOrderControllerRestDocsTest {

    private static final String ORDER_ID_URL = LegacyOrderEndpoints.ORDER_ID;
    private static final String ORDERS_URL = LegacyOrderEndpoints.ORDERS;
    private static final String ORDER_URL = LegacyOrderEndpoints.ORDER;
    private static final long ORDER_ID = LegacyOrderApiFixtures.DEFAULT_ORDER_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private MarketAccessChecker accessChecker;
    @MockitoBean private LegacyAccessChecker legacyAccessChecker;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;
    @MockitoBean private LegacyOrderQueryUseCase orderQueryUseCase;
    @MockitoBean private LegacyOrderListQueryUseCase orderListQueryUseCase;
    @MockitoBean private LegacyOrderQueryApiMapper queryApiMapper;
    @MockitoBean private LegacyOrderUpdateUseCase orderUpdateUseCase;
    @MockitoBean private LegacyOrderCommandApiMapper commandApiMapper;

    @Nested
    @DisplayName("주문 단건 조회 API")
    @Disabled("LegacyOrderController 미구현 상태 - 구현 완료 후 활성화")
    class FetchOrderTest {

        @Test
        @DisplayName("주문 ID로 주문 상세 조회 성공")
        void fetchOrder_Success() throws Exception {
            // given
            LegacyOrderResponse orderResponse = LegacyOrderApiFixtures.orderResponse();
            LegacyApiResponse<LegacyOrderResponse> apiResponse =
                    LegacyApiResponse.success(orderResponse);

            // NOTE: 구현 완료 후 UseCase/Mapper MockitoBean 및 given() 설정 추가

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(ORDER_ID_URL, ORDER_ID)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.orderId").value(ORDER_ID))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-order/fetch-order",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("orderId").description("조회할 주문 ID")),
                                    responseFields(
                                            fieldWithPath("data.orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID"),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("주문 목록 조회 API")
    @Disabled("LegacyOrderController 미구현 상태 - 구현 완료 후 활성화")
    class GetOrdersTest {

        @Test
        @DisplayName("주문 목록 페이징 조회 성공")
        void getOrders_Success() throws Exception {
            // given
            List<LegacyOrderResponse> content = LegacyOrderApiFixtures.orderListResponses(2);

            // NOTE: 구현 완료 후 UseCase/Mapper MockitoBean 및 given() 설정 추가

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(ORDERS_URL)
                                    .param("page", "0")
                                    .param("size", "20")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "legacy-order/get-orders",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터 시작)")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("주문 목록"),
                                            fieldWithPath("data.content[].orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 주문 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.number")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.numberOfElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 요소 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 번째 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("data.hasContent")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("컨텐츠 존재 여부"),
                                            fieldWithPath("data.lastDomainId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("마지막 도메인 ID (커서 기반 페이징용)")
                                                    .optional(),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }
    }

    @Nested
    @DisplayName("주문 상태 변경 API")
    @Disabled("LegacyOrderController 미구현 상태 - 구현 완료 후 활성화")
    class ModifyOrderStatusTest {

        @Test
        @DisplayName("주문 상태 변경 성공")
        void modifyOrderStatus_Success() throws Exception {
            // given
            LegacyUpdateOrderRequest request = LegacyOrderApiFixtures.updateOrderRequest();
            LegacyUpdateOrderResponse updateResponse = LegacyOrderApiFixtures.updateOrderResponse();
            LegacyApiResponse<LegacyUpdateOrderResponse> apiResponse =
                    LegacyApiResponse.success(updateResponse);

            // NOTE: 구현 완료 후 UseCase/Mapper MockitoBean 및 given() 설정 추가

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(ORDER_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.orderId").value(ORDER_ID))
                    .andExpect(
                            jsonPath("$.data.toBeOrderStatus")
                                    .value(LegacyOrderApiFixtures.DEFAULT_TO_BE_ORDER_STATUS))
                    .andExpect(jsonPath("$.response.status").value(200))
                    .andExpect(jsonPath("$.response.message").value("success"))
                    .andDo(
                            document(
                                    "legacy-order/modify-order-status",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("type")
                                                    .type(JsonFieldType.STRING)
                                                    .description(
                                                            "주문 변경 유형 (normalOrder, shipOrder,"
                                                                + " claimOrder,"
                                                                + " claimRejectedAndShipmentOrder)"),
                                            fieldWithPath("orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("변경할 주문 ID"),
                                            fieldWithPath("orderStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경할 주문 상태")
                                                    .optional(),
                                            fieldWithPath("byPass")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("상태 검증 우회 여부")
                                                    .optional(),
                                            fieldWithPath("changeReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경 사유")
                                                    .optional(),
                                            fieldWithPath("changeDetailReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경 상세 사유")
                                                    .optional(),
                                            fieldWithPath("shipmentInfo")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("배송 정보 (shipOrder 유형 시 필수)")
                                                    .optional(),
                                            fieldWithPath("shipmentInfo.invoiceNo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("송장 번호")
                                                    .optional(),
                                            fieldWithPath("shipmentInfo.shipmentType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 유형")
                                                    .optional(),
                                            fieldWithPath("shipmentInfo.companyCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.orderId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("주문 ID"),
                                            fieldWithPath("data.userId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("사용자 ID"),
                                            fieldWithPath("data.toBeOrderStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경 후 주문 상태"),
                                            fieldWithPath("data.asIsOrderStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경 전 주문 상태"),
                                            fieldWithPath("data.changeReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경 사유")
                                                    .optional(),
                                            fieldWithPath("data.changeDetailReason")
                                                    .type(JsonFieldType.STRING)
                                                    .description("변경 상세 사유")
                                                    .optional(),
                                            fieldWithPath("response.status")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("응답 상태 코드"),
                                            fieldWithPath("response.message")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 메시지"))));
        }

        @Test
        @DisplayName("배송 정보 없이 일반 주문 상태 변경 성공")
        void modifyOrderStatus_WithoutShipment_Success() throws Exception {
            // given
            LegacyUpdateOrderRequest request =
                    LegacyOrderApiFixtures.updateOrderRequestWithoutShipment();

            // NOTE: 구현 완료 후 UseCase/Mapper MockitoBean 및 given() 설정 추가

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.put(ORDER_URL)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }
    }
}
