package com.ryuqq.marketplace.adapter.in.rest.shipment.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.PageApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.common.error.ErrorMapperRegistry;
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentEndpoints;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentDetailApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentListApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.ShipmentSummaryApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.shipment.mapper.ShipmentQueryApiMapper;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentDetailResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentPageResult;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentDetailUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentListUseCase;
import com.ryuqq.marketplace.application.shipment.port.in.query.GetShipmentSummaryUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@Tag("unit")
@WebMvcTest(ShipmentQueryController.class)
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
@DisplayName("ShipmentQueryController REST Docs 테스트")
class ShipmentQueryControllerRestDocsTest {

    private static final String BASE_URL = ShipmentEndpoints.SHIPMENTS;
    private static final String DEFAULT_SHIPMENT_ID = ShipmentApiFixtures.DEFAULT_SHIPMENT_ID;

    @Autowired private MockMvc mockMvc;

    @MockitoBean private GetShipmentSummaryUseCase getShipmentSummaryUseCase;
    @MockitoBean private GetShipmentListUseCase getShipmentListUseCase;
    @MockitoBean private GetShipmentDetailUseCase getShipmentDetailUseCase;
    @MockitoBean private ShipmentQueryApiMapper mapper;
    @MockitoBean private ErrorMapperRegistry errorMapperRegistry;

    @Nested
    @DisplayName("배송 상태별 요약 조회 API")
    class GetShipmentSummaryTest {

        @Test
        @DisplayName("배송 상태별 요약 조회 성공")
        void getSummary_Success() throws Exception {
            // given
            ShipmentSummaryResult result = ShipmentApiFixtures.summaryResult();
            ShipmentSummaryApiResponse response = ShipmentApiFixtures.summaryApiResponse();

            given(getShipmentSummaryUseCase.execute()).willReturn(result);
            given(mapper.toSummaryResponse(any(ShipmentSummaryResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + ShipmentEndpoints.SUMMARY))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.ready").value(10))
                    .andExpect(jsonPath("$.data.preparing").value(5))
                    .andExpect(jsonPath("$.data.shipped").value(30))
                    .andExpect(jsonPath("$.data.inTransit").value(15))
                    .andExpect(jsonPath("$.data.delivered").value(100))
                    .andExpect(jsonPath("$.data.failed").value(2))
                    .andExpect(jsonPath("$.data.cancelled").value(3))
                    .andDo(
                            document(
                                    "shipment/summary",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    responseFields(
                                            fieldWithPath("data.ready")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 준비 대기 건수"),
                                            fieldWithPath("data.preparing")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 준비 중 건수"),
                                            fieldWithPath("data.shipped")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("발송 완료 건수"),
                                            fieldWithPath("data.inTransit")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 중 건수"),
                                            fieldWithPath("data.delivered")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 완료 건수"),
                                            fieldWithPath("data.failed")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("배송 실패 건수"),
                                            fieldWithPath("data.cancelled")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("취소 건수"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }

    @Nested
    @DisplayName("배송 목록 검색 API")
    class SearchShipmentsTest {

        @Test
        @DisplayName("유효한 요청이면 200과 페이지 응답을 반환한다")
        void searchShipments_ValidRequest_Returns200WithPage() throws Exception {
            // given
            ShipmentPageResult pageResult = ShipmentApiFixtures.pageResult(3, 0, 20);
            PageApiResponse<ShipmentListApiResponse> pageResponse =
                    ShipmentApiFixtures.pageApiResponse(3);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getShipmentListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ShipmentPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray())
                    .andExpect(jsonPath("$.data.content.length()").value(3))
                    .andExpect(jsonPath("$.data.page").value(0))
                    .andExpect(jsonPath("$.data.size").value(20))
                    .andExpect(jsonPath("$.data.totalElements").value(3))
                    .andDo(
                            document(
                                    "shipment/search",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    queryParameters(
                                            parameterWithName("statuses")
                                                    .description(
                                                            "배송 상태 필터 (READY, PREPARING, SHIPPED,"
                                                                    + " IN_TRANSIT, DELIVERED,"
                                                                    + " FAILED, CANCELLED)")
                                                    .optional(),
                                            parameterWithName("searchField")
                                                    .description(
                                                            "검색 필드 (ORDER_ID, TRACKING_NUMBER,"
                                                                    + " CUSTOMER_NAME)")
                                                    .optional(),
                                            parameterWithName("searchWord")
                                                    .description("검색어")
                                                    .optional(),
                                            parameterWithName("dateField")
                                                    .description(
                                                            "날짜 검색 대상 필드 (PAYMENT,"
                                                                    + " ORDER_CONFIRMED, SHIPPED)")
                                                    .optional(),
                                            parameterWithName("sortKey")
                                                    .description(
                                                            "정렬 기준 (CREATED_AT, SHIPPED_AT,"
                                                                + " DELIVERED_AT). 기본값: CREATED_AT")
                                                    .optional(),
                                            parameterWithName("sortDirection")
                                                    .description("정렬 방향 (ASC, DESC). 기본값: DESC")
                                                    .optional(),
                                            parameterWithName("page")
                                                    .description("페이지 번호 (0부터). 기본값: 0")
                                                    .optional(),
                                            parameterWithName("size")
                                                    .description("페이지 크기. 기본값: 20")
                                                    .optional()),
                                    responseFields(
                                            fieldWithPath("data.content[]")
                                                    .type(JsonFieldType.ARRAY)
                                                    .description("배송 목록"),
                                            fieldWithPath("data.content[].shipment")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("배송 정보"),
                                            fieldWithPath("data.content[].shipment.shipmentId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 ID"),
                                            fieldWithPath("data.content[].shipment.shipmentNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송번호"),
                                            fieldWithPath("data.content[].shipment.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 상태"),
                                            fieldWithPath("data.content[].shipment.trackingNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("송장번호"),
                                            fieldWithPath("data.content[].shipment.courierCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드"),
                                            fieldWithPath("data.content[].shipment.courierName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사명"),
                                            fieldWithPath(
                                                            "data.content[].shipment.orderConfirmedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("발주확인일시"),
                                            fieldWithPath("data.content[].shipment.shippedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("발송일시"),
                                            fieldWithPath("data.content[].shipment.deliveredAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("배송완료일시")
                                                    .optional(),
                                            fieldWithPath("data.content[].shipment.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.content[].order")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("주문 정보"),
                                            fieldWithPath("data.content[].order.orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 ID"),
                                            fieldWithPath("data.content[].order.orderNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문번호"),
                                            fieldWithPath("data.content[].order.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 상태"),
                                            fieldWithPath("data.content[].order.salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.content[].order.shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("샵 ID"),
                                            fieldWithPath("data.content[].order.shopCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("샵 코드"),
                                            fieldWithPath("data.content[].order.shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("샵 이름"),
                                            fieldWithPath("data.content[].order.externalOrderNo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 주문번호"),
                                            fieldWithPath("data.content[].order.externalOrderedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 주문일시"),
                                            fieldWithPath("data.content[].order.buyerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자명"),
                                            fieldWithPath("data.content[].order.buyerEmail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 이메일"),
                                            fieldWithPath("data.content[].order.buyerPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 전화번호"),
                                            fieldWithPath("data.content[].order.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.content[].order.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.content[].productOrder")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("상품주문 정보"),
                                            fieldWithPath("data.content[].productOrder.orderItemId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문상품 ID"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.content[].productOrder.productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data.content[].productOrder.skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품그룹명"),
                                            fieldWithPath("data.content[].productOrder.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.content[].productOrder.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.mainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표 이미지"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.externalProductId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 ID"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.externalOptionId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션 ID"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.externalProductName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품명"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.externalOptionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션명"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.externalImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 이미지"),
                                            fieldWithPath("data.content[].productOrder.unitPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("단가"),
                                            fieldWithPath("data.content[].productOrder.quantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수량"),
                                            fieldWithPath("data.content[].productOrder.totalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 금액"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.discountAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인 금액"),
                                            fieldWithPath(
                                                            "data.content[].productOrder.paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실결제 금액"),
                                            fieldWithPath("data.content[].receiver")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("수령인 정보"),
                                            fieldWithPath("data.content[].receiver.receiverName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인명"),
                                            fieldWithPath("data.content[].receiver.receiverPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 전화번호"),
                                            fieldWithPath("data.content[].receiver.receiverZipcode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 우편번호"),
                                            fieldWithPath("data.content[].receiver.receiverAddress")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 주소"),
                                            fieldWithPath(
                                                            "data.content[].receiver.receiverAddressDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 상세주소"),
                                            fieldWithPath("data.content[].receiver.deliveryRequest")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 요청사항"),
                                            fieldWithPath("data.page")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("현재 페이지 번호"),
                                            fieldWithPath("data.size")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("페이지 크기"),
                                            fieldWithPath("data.totalElements")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 데이터 수"),
                                            fieldWithPath("data.totalPages")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("전체 페이지 수"),
                                            fieldWithPath("data.first")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("첫 페이지 여부"),
                                            fieldWithPath("data.last")
                                                    .type(JsonFieldType.BOOLEAN)
                                                    .description("마지막 페이지 여부"),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }

        @Test
        @DisplayName("배송 상태 필터를 사용하면 200을 반환한다")
        void searchShipments_WithStatusFilter_Returns200() throws Exception {
            // given
            ShipmentPageResult pageResult = ShipmentApiFixtures.pageResult(1, 0, 20);
            PageApiResponse<ShipmentListApiResponse> pageResponse =
                    ShipmentApiFixtures.pageApiResponse(1);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getShipmentListUseCase.execute(any())).willReturn(pageResult);
            given(mapper.toPageResponse(any(ShipmentPageResult.class))).willReturn(pageResponse);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(BASE_URL)
                                    .param("statuses", "READY", "PREPARING")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isArray());
        }

        @Test
        @DisplayName("빈 결과이면 200과 빈 페이지를 반환한다")
        void searchShipments_EmptyResult_Returns200WithEmptyPage() throws Exception {
            // given
            ShipmentPageResult emptyResult = ShipmentApiFixtures.emptyPageResult();
            PageApiResponse<ShipmentListApiResponse> emptyResponse =
                    PageApiResponse.of(java.util.List.of(), 0, 20, 0);

            given(mapper.toSearchParams(any())).willReturn(null);
            given(getShipmentListUseCase.execute(any())).willReturn(emptyResult);
            given(mapper.toPageResponse(any(ShipmentPageResult.class))).willReturn(emptyResponse);

            // when & then
            mockMvc.perform(RestDocumentationRequestBuilders.get(BASE_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.content").isEmpty())
                    .andExpect(jsonPath("$.data.totalElements").value(0));
        }
    }

    @Nested
    @DisplayName("배송 상세 조회 API")
    class GetShipmentDetailTest {

        @Test
        @DisplayName("배송 상세 조회 성공")
        void getShipment_Success() throws Exception {
            // given
            ShipmentDetailResult detailResult =
                    ShipmentApiFixtures.detailResult(DEFAULT_SHIPMENT_ID);
            ShipmentDetailApiResponse response =
                    ShipmentApiFixtures.detailApiResponse(DEFAULT_SHIPMENT_ID);

            given(getShipmentDetailUseCase.execute(DEFAULT_SHIPMENT_ID)).willReturn(detailResult);
            given(mapper.toDetailResponse(any(ShipmentDetailResult.class))).willReturn(response);

            // when & then
            mockMvc.perform(
                            RestDocumentationRequestBuilders.get(
                                    BASE_URL + ShipmentEndpoints.SHIPMENT_ID, DEFAULT_SHIPMENT_ID))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.shipment.shipmentId").value(DEFAULT_SHIPMENT_ID))
                    .andExpect(
                            jsonPath("$.data.shipment.shipmentNumber")
                                    .value(ShipmentApiFixtures.DEFAULT_SHIPMENT_NUMBER))
                    .andExpect(
                            jsonPath("$.data.productOrder.orderItemId")
                                    .value(ShipmentApiFixtures.DEFAULT_ORDER_ITEM_ID))
                    .andExpect(
                            jsonPath("$.data.shipment.status")
                                    .value(ShipmentApiFixtures.DEFAULT_STATUS))
                    .andExpect(jsonPath("$.data.payment").exists())
                    .andDo(
                            document(
                                    "shipment/detail",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    pathParameters(
                                            parameterWithName("shipmentId").description("배송 ID")),
                                    responseFields(
                                            fieldWithPath("data.shipment")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("배송 정보"),
                                            fieldWithPath("data.shipment.shipmentId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 ID"),
                                            fieldWithPath("data.shipment.shipmentNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송번호"),
                                            fieldWithPath("data.shipment.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 상태"),
                                            fieldWithPath("data.shipment.trackingNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("송장번호"),
                                            fieldWithPath("data.shipment.courierCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사 코드"),
                                            fieldWithPath("data.shipment.courierName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("택배사명"),
                                            fieldWithPath("data.shipment.orderConfirmedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("발주확인일시"),
                                            fieldWithPath("data.shipment.shippedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("발송일시"),
                                            fieldWithPath("data.shipment.deliveredAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("배송완료일시")
                                                    .optional(),
                                            fieldWithPath("data.shipment.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("등록일시"),
                                            fieldWithPath("data.order")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("주문 정보"),
                                            fieldWithPath("data.order.orderId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 ID"),
                                            fieldWithPath("data.order.orderNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문번호"),
                                            fieldWithPath("data.order.status")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문 상태"),
                                            fieldWithPath("data.order.salesChannelId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("판매채널 ID"),
                                            fieldWithPath("data.order.shopId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("샵 ID"),
                                            fieldWithPath("data.order.shopCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("샵 코드"),
                                            fieldWithPath("data.order.shopName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("샵 이름"),
                                            fieldWithPath("data.order.externalOrderNo")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 주문번호"),
                                            fieldWithPath("data.order.externalOrderedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 주문일시"),
                                            fieldWithPath("data.order.buyerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자명"),
                                            fieldWithPath("data.order.buyerEmail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 이메일"),
                                            fieldWithPath("data.order.buyerPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("구매자 전화번호"),
                                            fieldWithPath("data.order.createdAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("생성일시"),
                                            fieldWithPath("data.order.updatedAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수정일시"),
                                            fieldWithPath("data.productOrder")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("상품주문 정보"),
                                            fieldWithPath("data.productOrder.orderItemId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("주문상품 ID"),
                                            fieldWithPath("data.productOrder.productGroupId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품그룹 ID"),
                                            fieldWithPath("data.productOrder.productId")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("상품 ID"),
                                            fieldWithPath("data.productOrder.skuCode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("SKU 코드"),
                                            fieldWithPath("data.productOrder.productGroupName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("상품그룹명"),
                                            fieldWithPath("data.productOrder.brandName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("브랜드명"),
                                            fieldWithPath("data.productOrder.sellerName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("셀러명"),
                                            fieldWithPath("data.productOrder.mainImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("대표 이미지"),
                                            fieldWithPath("data.productOrder.externalProductId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품 ID"),
                                            fieldWithPath("data.productOrder.externalOptionId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션 ID"),
                                            fieldWithPath("data.productOrder.externalProductName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 상품명"),
                                            fieldWithPath("data.productOrder.externalOptionName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 옵션명"),
                                            fieldWithPath("data.productOrder.externalImageUrl")
                                                    .type(JsonFieldType.STRING)
                                                    .description("외부 이미지"),
                                            fieldWithPath("data.productOrder.unitPrice")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("단가"),
                                            fieldWithPath("data.productOrder.quantity")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("수량"),
                                            fieldWithPath("data.productOrder.totalAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("총 금액"),
                                            fieldWithPath("data.productOrder.discountAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("할인 금액"),
                                            fieldWithPath("data.productOrder.paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("실결제 금액"),
                                            fieldWithPath("data.receiver")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("수령인 정보"),
                                            fieldWithPath("data.receiver.receiverName")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인명"),
                                            fieldWithPath("data.receiver.receiverPhone")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 전화번호"),
                                            fieldWithPath("data.receiver.receiverZipcode")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 우편번호"),
                                            fieldWithPath("data.receiver.receiverAddress")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 주소"),
                                            fieldWithPath("data.receiver.receiverAddressDetail")
                                                    .type(JsonFieldType.STRING)
                                                    .description("수령인 상세주소"),
                                            fieldWithPath("data.receiver.deliveryRequest")
                                                    .type(JsonFieldType.STRING)
                                                    .description("배송 요청사항"),
                                            fieldWithPath("data.payment")
                                                    .type(JsonFieldType.OBJECT)
                                                    .description("결제 정보"),
                                            fieldWithPath("data.payment.paymentId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 ID"),
                                            fieldWithPath("data.payment.paymentNumber")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 번호"),
                                            fieldWithPath("data.payment.paymentStatus")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 상태"),
                                            fieldWithPath("data.payment.paymentMethod")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제 수단"),
                                            fieldWithPath("data.payment.paymentAgencyId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("PG사 결제 ID"),
                                            fieldWithPath("data.payment.paymentAmount")
                                                    .type(JsonFieldType.NUMBER)
                                                    .description("결제 금액"),
                                            fieldWithPath("data.payment.paidAt")
                                                    .type(JsonFieldType.STRING)
                                                    .description("결제일시"),
                                            fieldWithPath("data.payment.canceledAt")
                                                    .type(JsonFieldType.NULL)
                                                    .description("결제취소일시")
                                                    .optional(),
                                            fieldWithPath("timestamp")
                                                    .type(JsonFieldType.STRING)
                                                    .description("응답 시간"),
                                            fieldWithPath("requestId")
                                                    .type(JsonFieldType.STRING)
                                                    .description("요청 ID"))));
        }
    }
}
