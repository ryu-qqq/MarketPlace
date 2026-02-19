package com.ryuqq.marketplace.adapter.out.persistence.shipment.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.ShipmentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import com.ryuqq.marketplace.domain.shipment.vo.ShipmentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ShipmentJpaEntityMapperTest - 배송 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ShipmentJpaEntityMapper 단위 테스트")
class ShipmentJpaEntityMapperTest {

    private ShipmentJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShipmentJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("READY 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithReadyShipment_ConvertsCorrectly() {
            // given
            Shipment domain = ShipmentFixtures.readyShipment();

            // when
            ShipmentJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getShipmentNumber()).isEqualTo(domain.shipmentNumberValue());
            assertThat(entity.getOrderId()).isEqualTo(domain.orderId());
            assertThat(entity.getOrderNumber()).isEqualTo(domain.orderNumber());
            assertThat(entity.getStatus()).isEqualTo(ShipmentStatus.READY.name());
        }

        @Test
        @DisplayName("배송 방법이 없는 Domain을 Entity로 변환합니다")
        void toEntity_WithoutShipmentMethod_ConvertsCorrectly() {
            // given
            Shipment domain = ShipmentFixtures.readyShipment();

            // when
            ShipmentJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getShipmentMethodType()).isNull();
            assertThat(entity.getCourierCode()).isNull();
            assertThat(entity.getCourierName()).isNull();
        }

        @Test
        @DisplayName("SHIPPED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithShippedShipment_ConvertsCorrectly() {
            // given
            Shipment domain = ShipmentFixtures.shippedShipment();

            // when
            ShipmentJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ShipmentStatus.SHIPPED.name());
            assertThat(entity.getShipmentMethodType()).isNotNull();
            assertThat(entity.getCourierCode()).isEqualTo("CJ");
            assertThat(entity.getCourierName()).isEqualTo("CJ대한통운");
            assertThat(entity.getTrackingNumber()).isEqualTo("1234567890");
        }

        @Test
        @DisplayName("DELIVERED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDeliveredShipment_ConvertsCorrectly() {
            // given
            Shipment domain = ShipmentFixtures.deliveredShipment();

            // when
            ShipmentJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ShipmentStatus.DELIVERED.name());
            assertThat(entity.getDeliveredAt()).isNotNull();
        }

        @Test
        @DisplayName("신규 배송 Domain을 Entity로 변환합니다")
        void toEntity_WithNewShipment_ConvertsCorrectly() {
            // given
            Shipment domain = ShipmentFixtures.newShipment();

            // when
            ShipmentJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getStatus()).isEqualTo(ShipmentStatus.READY.name());
            assertThat(entity.getDeletedAt()).isNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("READY Entity를 Domain으로 변환합니다")
        void toDomain_WithReadyEntity_ConvertsCorrectly() {
            // given
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();

            // when
            Shipment domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.shipmentNumberValue()).isEqualTo(entity.getShipmentNumber());
            assertThat(domain.orderId()).isEqualTo(entity.getOrderId());
            assertThat(domain.orderNumber()).isEqualTo(entity.getOrderNumber());
            assertThat(domain.status()).isEqualTo(ShipmentStatus.READY);
        }

        @Test
        @DisplayName("배송 방법이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutShipmentMethod_ConvertsCorrectly() {
            // given
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.entityWithoutShipmentMethod();

            // when
            Shipment domain = mapper.toDomain(entity);

            // then
            assertThat(domain.shipmentMethod()).isNull();
        }

        @Test
        @DisplayName("SHIPPED Entity를 Domain으로 변환합니다")
        void toDomain_WithShippedEntity_ConvertsCorrectly() {
            // given
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.shippedEntity();

            // when
            Shipment domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ShipmentStatus.SHIPPED);
            assertThat(domain.shipmentMethod()).isNotNull();
            assertThat(domain.trackingNumber())
                    .isEqualTo(ShipmentJpaEntityFixtures.DEFAULT_TRACKING_NUMBER);
        }

        @Test
        @DisplayName("DELIVERED Entity를 Domain으로 변환합니다")
        void toDomain_WithDeliveredEntity_ConvertsCorrectly() {
            // given
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.deliveredEntity();

            // when
            Shipment domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ShipmentStatus.DELIVERED);
            assertThat(domain.deliveredAt()).isNotNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            Shipment original = ShipmentFixtures.shippedShipment();

            // when
            ShipmentJpaEntity entity = mapper.toEntity(original);
            Shipment converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.shipmentNumberValue()).isEqualTo(original.shipmentNumberValue());
            assertThat(converted.orderId()).isEqualTo(original.orderId());
            assertThat(converted.orderNumber()).isEqualTo(original.orderNumber());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.trackingNumber()).isEqualTo(original.trackingNumber());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ShipmentJpaEntity original = ShipmentJpaEntityFixtures.shippedEntity();

            // when
            Shipment domain = mapper.toDomain(original);
            ShipmentJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getShipmentNumber()).isEqualTo(original.getShipmentNumber());
            assertThat(converted.getOrderId()).isEqualTo(original.getOrderId());
            assertThat(converted.getOrderNumber()).isEqualTo(original.getOrderNumber());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getTrackingNumber()).isEqualTo(original.getTrackingNumber());
        }
    }

    // ========================================================================
    // 4. ShipmentMethod 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("ShipmentMethod 변환 테스트")
    class ShipmentMethodConversionTest {

        @Test
        @DisplayName("methodType만 null인 Entity는 method를 null로 반환합니다")
        void toDomain_WithNullMethodType_ReturnsNullMethod() {
            // given - methodType은 null이지만 courierCode는 있는 엣지 케이스
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();

            // when
            Shipment domain = mapper.toDomain(entity);

            // then
            assertThat(domain.shipmentMethod()).isNull();
        }

        @Test
        @DisplayName("COURIER 타입 배송 방법이 올바르게 변환됩니다")
        void toDomain_WithCourierMethod_ConvertsCorrectly() {
            // given
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.shippedEntity();

            // when
            Shipment domain = mapper.toDomain(entity);

            // then
            assertThat(domain.shipmentMethod()).isNotNull();
            assertThat(domain.shipmentMethod().courierCode())
                    .isEqualTo(ShipmentJpaEntityFixtures.DEFAULT_COURIER_CODE);
            assertThat(domain.shipmentMethod().courierName())
                    .isEqualTo(ShipmentJpaEntityFixtures.DEFAULT_COURIER_NAME);
        }
    }
}
