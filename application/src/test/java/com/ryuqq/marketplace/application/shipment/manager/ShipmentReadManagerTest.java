package com.ryuqq.marketplace.application.shipment.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.shipment.port.out.query.ShipmentQueryPort;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.exception.ShipmentNotFoundException;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
@DisplayName("ShipmentReadManager 단위 테스트")
class ShipmentReadManagerTest {

    @InjectMocks private ShipmentReadManager sut;

    @Mock private ShipmentQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 Shipment 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Shipment를 반환한다")
        void getById_ExistingId_ReturnsShipment() {
            // given
            ShipmentId id = ShipmentFixtures.defaultShipmentId();
            Shipment expected = ShipmentFixtures.preparingShipment();
            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Shipment result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 ShipmentNotFoundException이 발생한다")
        void getById_NonExistingId_ThrowsShipmentNotFoundException() {
            // given
            ShipmentId id = ShipmentId.of("00000000-0000-7000-8000-000000000099");
            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(ShipmentNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByOrderItemId() - OrderItemId로 Shipment 조회")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("존재하는 OrderItemId로 Optional<Shipment>를 반환한다")
        void findByOrderItemId_ExistingOrderItemId_ReturnsOptionalShipment() {
            // given
            OrderItemId orderItemId = ShipmentFixtures.defaultOrderItemId();
            Shipment expected = ShipmentFixtures.preparingShipment();
            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.of(expected));

            // when
            Optional<Shipment> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 OrderItemId이면 빈 Optional을 반환한다")
        void findByOrderItemId_NonExisting_ReturnsEmpty() {
            // given
            OrderItemId orderItemId = OrderItemId.of("00000000-0000-7000-8000-000000000099");
            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<Shipment> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getByOrderItemId() - OrderItemId로 Shipment 조회 (예외 발생)")
    class GetByOrderItemIdTest {

        @Test
        @DisplayName("존재하는 OrderItemId로 Shipment를 반환한다")
        void getByOrderItemId_ExistingOrderItemId_ReturnsShipment() {
            // given
            OrderItemId orderItemId = ShipmentFixtures.defaultOrderItemId();
            Shipment expected = ShipmentFixtures.preparingShipment();
            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.of(expected));

            // when
            Shipment result = sut.getByOrderItemId(orderItemId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 OrderItemId이면 ShipmentNotFoundException이 발생한다")
        void getByOrderItemId_NonExisting_ThrowsShipmentNotFoundException() {
            // given
            OrderItemId orderItemId = OrderItemId.of("00000000-0000-7000-8000-000000000099");
            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getByOrderItemId(orderItemId))
                    .isInstanceOf(ShipmentNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByOrderItemIds() - OrderItemId 목록으로 Shipment 목록 조회")
    class FindByOrderItemIdsTest {

        @Test
        @DisplayName("OrderItemId 목록으로 Shipment 목록을 반환한다")
        void findByOrderItemIds_ValidIds_ReturnsShipmentList() {
            // given
            List<OrderItemId> ids =
                    List.of(
                            ShipmentFixtures.defaultOrderItemId(),
                            OrderItemId.of("01940001-0000-7000-8000-000000000002"));
            List<Shipment> expected = List.of(ShipmentFixtures.preparingShipment());
            given(queryPort.findByOrderItemIds(ids)).willReturn(expected);

            // when
            List<Shipment> result = sut.findByOrderItemIds(ids);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("빈 목록으로 조회 시 빈 목록을 반환한다")
        void findByOrderItemIds_EmptyIds_ReturnsEmptyList() {
            // given
            List<OrderItemId> emptyIds = List.of();
            given(queryPort.findByOrderItemIds(emptyIds)).willReturn(List.of());

            // when
            List<Shipment> result = sut.findByOrderItemIds(emptyIds);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByStatus() - 상태별 Shipment 카운트 조회")
    class CountByStatusTest {

        @Test
        @DisplayName("상태별 카운트 맵을 반환한다")
        void countByStatus_ReturnsStatusCountMap() {
            // given
            Map<ShipmentStatus, Long> expected =
                    Map.of(
                            ShipmentStatus.READY, 5L,
                            ShipmentStatus.SHIPPED, 10L);
            given(queryPort.countByStatus()).willReturn(expected);

            // when
            Map<ShipmentStatus, Long> result = sut.countByStatus();

            // then
            assertThat(result).isEqualTo(expected);
            assertThat(result.get(ShipmentStatus.READY)).isEqualTo(5L);
            assertThat(result.get(ShipmentStatus.SHIPPED)).isEqualTo(10L);
        }
    }
}
