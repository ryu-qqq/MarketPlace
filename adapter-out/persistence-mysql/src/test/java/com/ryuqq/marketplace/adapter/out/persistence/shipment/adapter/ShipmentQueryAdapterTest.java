package com.ryuqq.marketplace.adapter.out.persistence.shipment.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.ShipmentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.mapper.ShipmentJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentQueryDslRepository;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.id.ShipmentId;
import com.ryuqq.marketplace.domain.shipment.query.ShipmentSearchCriteria;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import java.util.HashMap;
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

/**
 * ShipmentQueryAdapterTest - 배송 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentQueryAdapter 단위 테스트")
class ShipmentQueryAdapterTest {

    @Mock private ShipmentQueryDslRepository queryDslRepository;

    @Mock private ShipmentJpaEntityMapper mapper;

    @Mock private ShipmentSearchCriteria criteria;

    @InjectMocks private ShipmentQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            ShipmentId shipmentId = ShipmentFixtures.defaultShipmentId();
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();
            Shipment domain = ShipmentFixtures.readyShipment();

            given(queryDslRepository.findById(shipmentId.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Shipment> result = queryAdapter.findById(shipmentId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            ShipmentId shipmentId = ShipmentFixtures.shipmentId("non-existent-id");
            given(queryDslRepository.findById("non-existent-id")).willReturn(Optional.empty());

            // when
            Optional<Shipment> result = queryAdapter.findById(shipmentId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("findById 호출 시 repository에 ID 값이 전달됩니다")
        void findById_DelegatesToRepositoryWithIdValue() {
            // given
            ShipmentId shipmentId = ShipmentFixtures.defaultShipmentId();
            given(queryDslRepository.findById(shipmentId.value())).willReturn(Optional.empty());

            // when
            queryAdapter.findById(shipmentId);

            // then
            then(queryDslRepository).should().findById(shipmentId.value());
        }
    }

    // ========================================================================
    // 2. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId 메서드 테스트")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("존재하는 orderItemId로 조회 시 Domain을 반환합니다")
        void findByOrderItemId_WithExistingOrderItemId_ReturnsDomain() {
            // given
            String orderItemId = "01940001-0000-7000-8000-000000000001";
            ShipmentJpaEntity entity =
                    ShipmentJpaEntityFixtures.readyEntityWithOrderItemId(
                            ShipmentJpaEntityFixtures.DEFAULT_ID, orderItemId);
            Shipment domain = ShipmentFixtures.readyShipment();

            given(queryDslRepository.findByOrderItemId(orderItemId))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Shipment> result = queryAdapter.findByOrderItemId(OrderItemId.of(orderItemId));

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 orderItemId로 조회 시 빈 Optional을 반환합니다")
        void findByOrderItemId_WithNonExistingOrderItemId_ReturnsEmpty() {
            // given
            String orderItemId = "01940001-0000-7000-8000-000000000999";
            given(queryDslRepository.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<Shipment> result = queryAdapter.findByOrderItemId(OrderItemId.of(orderItemId));

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 배송 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            ShipmentJpaEntity entity1 = ShipmentJpaEntityFixtures.readyEntity("id-1");
            ShipmentJpaEntity entity2 = ShipmentJpaEntityFixtures.readyEntity("id-2");
            Shipment domain1 = ShipmentFixtures.readyShipment();
            Shipment domain2 = ShipmentFixtures.preparingShipment();

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Shipment> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<Shipment> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 배송 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 5. countByStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByStatus 메서드 테스트")
    class CountByStatusTest {

        @Test
        @DisplayName("상태별 배송 개수를 반환합니다")
        void countByStatus_ReturnsStatusCountMap() {
            // given
            Map<String, Long> rawCounts = new HashMap<>();
            rawCounts.put("READY", 10L);
            rawCounts.put("SHIPPED", 5L);
            rawCounts.put("DELIVERED", 3L);

            given(queryDslRepository.countByStatus()).willReturn(rawCounts);

            // when
            Map<ShipmentStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(ShipmentStatus.READY)).isEqualTo(10L);
            assertThat(result.get(ShipmentStatus.SHIPPED)).isEqualTo(5L);
            assertThat(result.get(ShipmentStatus.DELIVERED)).isEqualTo(3L);
        }

        @Test
        @DisplayName("배송이 없으면 빈 맵을 반환합니다")
        void countByStatus_WithNoShipments_ReturnsEmptyMap() {
            // given
            given(queryDslRepository.countByStatus()).willReturn(new HashMap<>());

            // when
            Map<ShipmentStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("repository의 String 키를 ShipmentStatus enum으로 변환합니다")
        void countByStatus_ConvertsStringKeysToEnums() {
            // given
            Map<String, Long> rawCounts = new HashMap<>();
            rawCounts.put("PREPARING", 7L);
            rawCounts.put("IN_TRANSIT", 2L);

            given(queryDslRepository.countByStatus()).willReturn(rawCounts);

            // when
            Map<ShipmentStatus, Long> result = queryAdapter.countByStatus();

            // then
            assertThat(result).containsKey(ShipmentStatus.PREPARING);
            assertThat(result).containsKey(ShipmentStatus.IN_TRANSIT);
            assertThat(result.get(ShipmentStatus.PREPARING)).isEqualTo(7L);
        }
    }
}
