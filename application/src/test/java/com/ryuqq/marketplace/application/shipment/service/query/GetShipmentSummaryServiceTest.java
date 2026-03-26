package com.ryuqq.marketplace.application.shipment.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.shipment.ShipmentQueryFixtures;
import com.ryuqq.marketplace.application.shipment.assembler.ShipmentAssembler;
import com.ryuqq.marketplace.application.shipment.dto.response.ShipmentSummaryResult;
import com.ryuqq.marketplace.application.shipment.manager.ShipmentReadManager;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetShipmentSummaryService 단위 테스트")
class GetShipmentSummaryServiceTest {

    @InjectMocks private GetShipmentSummaryService sut;

    @Mock private ShipmentReadManager readManager;
    @Mock private ShipmentAssembler assembler;

    @Nested
    @DisplayName("execute() - 배송 상태별 요약 조회")
    class ExecuteTest {

        @Test
        @DisplayName("상태별 카운트를 조회하여 ShipmentSummaryResult를 반환한다")
        void execute_ReturnsShipmentSummaryResult() {
            // given
            Map<ShipmentStatus, Long> statusCounts =
                    Map.of(
                            ShipmentStatus.READY, 5L,
                            ShipmentStatus.PREPARING, 3L,
                            ShipmentStatus.SHIPPED, 10L,
                            ShipmentStatus.IN_TRANSIT, 7L,
                            ShipmentStatus.DELIVERED, 2L,
                            ShipmentStatus.FAILED, 1L,
                            ShipmentStatus.CANCELLED, 0L);
            ShipmentSummaryResult expected = ShipmentQueryFixtures.shipmentSummaryResult();

            given(readManager.countByStatus()).willReturn(statusCounts);
            given(assembler.toSummaryResult(statusCounts)).willReturn(expected);

            // when
            ShipmentSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(expected);
            then(readManager).should().countByStatus();
            then(assembler).should().toSummaryResult(statusCounts);
        }

        @Test
        @DisplayName("배송 데이터가 없으면 모든 카운트가 0인 결과를 반환한다")
        void execute_NoShipments_ReturnsZeroCounts() {
            // given
            Map<ShipmentStatus, Long> emptyMap = Map.of();
            ShipmentSummaryResult expected = ShipmentQueryFixtures.emptyShipmentSummaryResult();

            given(readManager.countByStatus()).willReturn(emptyMap);
            given(assembler.toSummaryResult(emptyMap)).willReturn(expected);

            // when
            ShipmentSummaryResult result = sut.execute();

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
