package com.ryuqq.marketplace.adapter.in.rest.shipment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.shipment.ShipmentApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ConfirmShipmentBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipBatchApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.request.ShipSingleApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.shipment.dto.response.BatchResultApiResponse;
import com.ryuqq.marketplace.application.common.dto.result.BatchProcessingResult;
import com.ryuqq.marketplace.application.shipment.dto.command.ConfirmShipmentBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipBatchCommand;
import com.ryuqq.marketplace.application.shipment.dto.command.ShipSingleCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ShipmentCommandApiMapper 단위 테스트")
class ShipmentCommandApiMapperTest {

    private ShipmentCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShipmentCommandApiMapper();
    }

    @Nested
    @DisplayName("toConfirmBatchCommand() - 발주확인 일괄 요청 변환")
    class ToConfirmBatchCommandTest {

        @Test
        @DisplayName("ConfirmShipmentBatchApiRequest를 ConfirmShipmentBatchCommand로 변환한다")
        void toConfirmBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ConfirmShipmentBatchApiRequest request = ShipmentApiFixtures.confirmBatchRequest();

            // when
            ConfirmShipmentBatchCommand command = mapper.toConfirmBatchCommand(request, 1L);

            // then
            assertThat(command.orderItemIds()).containsExactly(1001L, 1002L, 1003L);
        }

        @Test
        @DisplayName("단일 shipmentId를 가진 요청도 변환된다")
        void toConfirmBatchCommand_SingleId_ReturnsCommand() {
            // given
            ConfirmShipmentBatchApiRequest request =
                    ShipmentApiFixtures.confirmBatchRequest(List.of(1001L));

            // when
            ConfirmShipmentBatchCommand command = mapper.toConfirmBatchCommand(request, 1L);

            // then
            assertThat(command.orderItemIds()).hasSize(1);
            assertThat(command.orderItemIds()).containsExactly(1001L);
        }
    }

    @Nested
    @DisplayName("toShipBatchCommand() - 송장등록 일괄 요청 변환")
    class ToShipBatchCommandTest {

        @Test
        @DisplayName("ShipBatchApiRequest를 ShipBatchCommand로 변환한다")
        void toShipBatchCommand_ConvertsRequest_ReturnsCommand() {
            // given
            ShipBatchApiRequest request = ShipmentApiFixtures.shipBatchRequest();

            // when
            ShipBatchCommand command = mapper.toShipBatchCommand(request);

            // then
            assertThat(command.items()).hasSize(2);
        }

        @Test
        @DisplayName("ShipBatchItem 각 항목이 올바르게 변환된다")
        void toShipBatchCommand_ConvertsEachItem_ReturnsCorrectItems() {
            // given
            ShipBatchApiRequest request = ShipmentApiFixtures.shipBatchRequest();

            // when
            ShipBatchCommand command = mapper.toShipBatchCommand(request);

            // then
            ShipBatchCommand.ShipBatchItem firstItem = command.items().get(0);
            assertThat(firstItem.orderItemId()).isEqualTo(1001L);
            assertThat(firstItem.trackingNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_TRACKING_NUMBER);
            assertThat(firstItem.courierCode()).isEqualTo(ShipmentApiFixtures.DEFAULT_COURIER_CODE);
            assertThat(firstItem.courierName()).isEqualTo(ShipmentApiFixtures.DEFAULT_COURIER_NAME);
            assertThat(firstItem.shipmentMethodType())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_SHIPMENT_METHOD_TYPE);
        }

        @Test
        @DisplayName("두 번째 항목도 올바르게 변환된다")
        void toShipBatchCommand_ConvertsSecondItem_ReturnsCorrectItem() {
            // given
            ShipBatchApiRequest request = ShipmentApiFixtures.shipBatchRequest();

            // when
            ShipBatchCommand command = mapper.toShipBatchCommand(request);

            // then
            ShipBatchCommand.ShipBatchItem secondItem = command.items().get(1);
            assertThat(secondItem.orderItemId()).isEqualTo(1002L);
        }
    }

    @Nested
    @DisplayName("toShipSingleCommand() - 단건 송장등록 요청 변환")
    class ToShipSingleCommandTest {

        @Test
        @DisplayName("orderId와 ShipSingleApiRequest를 ShipSingleCommand로 변환한다")
        void toShipSingleCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long orderItemId = ShipmentApiFixtures.DEFAULT_ORDER_ITEM_ID;
            ShipSingleApiRequest request = ShipmentApiFixtures.shipSingleRequest();

            // when
            ShipSingleCommand command = mapper.toShipSingleCommand(orderItemId, request);

            // then
            assertThat(command.orderItemId()).isEqualTo(ShipmentApiFixtures.DEFAULT_ORDER_ITEM_ID);
            assertThat(command.trackingNumber())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_TRACKING_NUMBER);
            assertThat(command.courierCode()).isEqualTo(ShipmentApiFixtures.DEFAULT_COURIER_CODE);
            assertThat(command.courierName()).isEqualTo(ShipmentApiFixtures.DEFAULT_COURIER_NAME);
            assertThat(command.shipmentMethodType())
                    .isEqualTo(ShipmentApiFixtures.DEFAULT_SHIPMENT_METHOD_TYPE);
        }

        @Test
        @DisplayName("경로변수 orderId가 Command에 올바르게 매핑된다")
        void toShipSingleCommand_OrderIdFromPath_MappedCorrectly() {
            // given
            long orderItemId = 9999L;
            ShipSingleApiRequest request = ShipmentApiFixtures.shipSingleRequest();

            // when
            ShipSingleCommand command = mapper.toShipSingleCommand(orderItemId, request);

            // then
            assertThat(command.orderItemId()).isEqualTo(9999L);
        }
    }

    @Nested
    @DisplayName("toBatchResultResponse() - 일괄 처리 결과 응답 변환")
    class ToBatchResultResponseTest {

        @Test
        @DisplayName("BatchProcessingResult를 BatchResultApiResponse로 변환한다")
        void toBatchResultResponse_ConvertsMixedResult_ReturnsApiResponse() {
            // given
            BatchProcessingResult<String> result = ShipmentApiFixtures.batchMixedResult();

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(3);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isEqualTo(1);
            assertThat(response.results()).hasSize(3);
        }

        @Test
        @DisplayName("성공 항목의 결과가 올바르게 변환된다")
        void toBatchResultResponse_SuccessItem_ReturnsCorrectResponse() {
            // given
            BatchProcessingResult<String> result =
                    ShipmentApiFixtures.batchSuccessResult(List.of("SHIP-001", "SHIP-002"));

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            assertThat(response.totalCount()).isEqualTo(2);
            assertThat(response.successCount()).isEqualTo(2);
            assertThat(response.failureCount()).isZero();

            BatchResultApiResponse.BatchResultItemApiResponse firstItem = response.results().get(0);
            assertThat(firstItem.id()).isEqualTo("SHIP-001");
            assertThat(firstItem.success()).isTrue();
            assertThat(firstItem.errorCode()).isNull();
            assertThat(firstItem.errorMessage()).isNull();
        }

        @Test
        @DisplayName("실패 항목의 결과가 올바르게 변환된다")
        void toBatchResultResponse_FailureItem_ReturnsCorrectResponse() {
            // given
            BatchProcessingResult<String> result = ShipmentApiFixtures.batchMixedResult();

            // when
            BatchResultApiResponse response = mapper.toBatchResultResponse(result);

            // then
            BatchResultApiResponse.BatchResultItemApiResponse failedItem =
                    response.results().get(1);
            assertThat(failedItem.id()).isEqualTo("1002");
            assertThat(failedItem.success()).isFalse();
            assertThat(failedItem.errorCode()).isEqualTo("ALREADY_CONFIRMED");
            assertThat(failedItem.errorMessage()).isEqualTo("이미 발주 확인된 배송입니다.");
        }
    }
}
