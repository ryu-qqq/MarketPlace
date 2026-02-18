package com.ryuqq.marketplace.adapter.in.rest.shipment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ConfirmShipmentBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipSingleApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.mapper.ShipmentCommandApiMapper;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.port.in.command.ConfirmShipmentBatchUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipBatchUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.command.ShipSingleUseCase;
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
@WebMvcTest(ShipmentCommandController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ShipmentCommandController REST Docs 테스트")
class ShipmentCommandControllerRestDocsTest {

    private static final String SHIPMENTS_URL = ShipmentEndpoints.SHIPMENTS;
    private static final String DEFAULT_ORDER_ID = ShipmentApiFixtures.DEFAULT_ORDER_ID;

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ConfirmShipmentBatchUseCase confirmShipmentBatchUseCase;
    @MockitoBean private ShipBatchUseCase shipBatchUseCase;
    @MockitoBean private ShipSingleUseCase shipSingleUseCase;
    @MockitoBean private ShipmentCommandApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("발주확인 일괄 처리 API")
    class ConfirmShipmentBatchTest {

        @Test
        @DisplayName("발주확인 일괄 처리 성공")
        void confirmBatch_Success() throws Exception {
            // given
            ConfirmShipmentBatchApiRequest request = ShipmentApiFixtures.confirmBatchRequest();
            BatchProcessingResult<String> batchResult =
                    ShipmentApiFixtures.batchSuccessResult(
                            List.of("SHIP-001", "SHIP-002", "SHIP-003"));
            BatchResultApiResponse response =
                    ShipmentApiFixtures.batchAllSuccessApiResponse(
                            List.of("SHIP-001", "SHIP-002", "SHIP-003"));

            given(mapper.toConfirmBatchCommand(any(ConfirmShipmentBatchApiRequest.class)))
                    .willReturn(null);
            given(confirmShipmentBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SHIPMENTS_URL + ShipmentEndpoints.CONFIRM_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(3))
                    .andExpect(jsonPath("$.data.successCount").value(3))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andExpect(jsonPath("$.data.results").isArray())
                    .andDo(
                            document(
                                    "shipment/confirm-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("shipmentIds")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("발주확인 대상 배송 ID 목록")),
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
                                                    .description("처리 대상 배송 ID"),
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
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }

        @Test
        @DisplayName("일부 실패가 있어도 200을 반환하고 전체 결과를 포함한다")
        void confirmBatch_PartialFailure_Returns200WithMixedResult() throws Exception {
            // given
            ConfirmShipmentBatchApiRequest request = ShipmentApiFixtures.confirmBatchRequest();
            BatchProcessingResult<String> batchResult = ShipmentApiFixtures.batchMixedResult();
            BatchResultApiResponse response = ShipmentApiFixtures.batchResultApiResponse();

            given(mapper.toConfirmBatchCommand(any(ConfirmShipmentBatchApiRequest.class)))
                    .willReturn(null);
            given(confirmShipmentBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SHIPMENTS_URL + ShipmentEndpoints.CONFIRM_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(3))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(1));
        }
    }

    @Nested
    @DisplayName("송장등록 일괄 처리 API")
    class ShipBatchTest {

        @Test
        @DisplayName("송장등록 일괄 처리 성공")
        void shipBatch_Success() throws Exception {
            // given
            ShipBatchApiRequest request = ShipmentApiFixtures.shipBatchRequest();
            BatchProcessingResult<String> batchResult =
                    ShipmentApiFixtures.batchSuccessResult(List.of("SHIP-001", "SHIP-002"));
            BatchResultApiResponse response =
                    ShipmentApiFixtures.batchAllSuccessApiResponse(List.of("SHIP-001", "SHIP-002"));

            given(mapper.toShipBatchCommand(any(ShipBatchApiRequest.class))).willReturn(null);
            given(shipBatchUseCase.execute(any())).willReturn(batchResult);
            given(mapper.toBatchResultResponse(any())).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SHIPMENTS_URL + ShipmentEndpoints.SHIP_BATCH)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.totalCount").value(2))
                    .andExpect(jsonPath("$.data.successCount").value(2))
                    .andExpect(jsonPath("$.data.failureCount").value(0))
                    .andDo(
                            document(
                                    "shipment/ship-batch",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    requestFields(
                                            fieldWithPath("items")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("송장등록 대상 목록"),
                                            fieldWithPath("items[].shipmentId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 ID"),
                                            fieldWithPath("items[].trackingNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("송장번호"),
                                            fieldWithPath("items[].courierCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드"),
                                            fieldWithPath("items[].courierName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사명"),
                                            fieldWithPath("items[].shipmentMethodType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 방법 유형")),
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
                                                    .description("처리 대상 배송 ID"),
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
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("단건 송장등록 API")
    class ShipSingleTest {

        @Test
        @DisplayName("단건 송장등록 성공")
        void shipSingle_Success() throws Exception {
            // given
            ShipSingleApiRequest request = ShipmentApiFixtures.shipSingleRequest();

            given(mapper.toShipSingleCommand(any(String.class), any(ShipSingleApiRequest.class)))
                    .willReturn(null);
            doNothing().when(shipSingleUseCase).execute(any());

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.post(
                                            SHIPMENTS_URL + ShipmentEndpoints.SHIP_SINGLE,
                                            DEFAULT_ORDER_ID)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(
                            document(
                                    "shipment/ship-single",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("orderId").description("주문 ID")),
                                    requestFields(
                                            fieldWithPath("trackingNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("송장번호"),
                                            fieldWithPath("courierCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드"),
                                            fieldWithPath("courierName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사명"),
                                            fieldWithPath("shipmentMethodType")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 방법 유형")),
                                    responseFields(
                                            fieldWithPath("data")
                                                    .type(JsonFieldType.NULL)
                                                    .description("응답 데이터 (단건 등록 시 null)")
                                                    .optional(),
                                            fieldWithPath("timestamp").description("응답 시간"),
                                            fieldWithPath("requestId").description("요청 ID"))));
        }
    }
}
