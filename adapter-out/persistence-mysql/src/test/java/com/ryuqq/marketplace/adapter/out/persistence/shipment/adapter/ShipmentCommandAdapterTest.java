package com.ryuqq.marketplace.adapter.out.persistence.shipment.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.shipment.ShipmentJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.entity.ShipmentJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.mapper.ShipmentJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipment.repository.ShipmentJpaRepository;
import com.ryuqq.marketplace.domain.shipment.ShipmentFixtures;
import com.ryuqq.marketplace.domain.shipment.aggregate.Shipment;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ShipmentCommandAdapterTest - 배송 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentCommandAdapter 단위 테스트")
class ShipmentCommandAdapterTest {

    @Mock private ShipmentJpaRepository jpaRepository;

    @Mock private ShipmentJpaEntityMapper mapper;

    @InjectMocks private ShipmentCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Domain을 Entity로 변환 후 저장합니다")
        void persist_WithValidDomain_SavesEntity() {
            // given
            Shipment domain = ShipmentFixtures.newShipment();
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(jpaRepository).should().save(entity);
        }

        @Test
        @DisplayName("READY 상태 배송을 저장합니다")
        void persist_WithReadyShipment_Saves() {
            // given
            Shipment domain = ShipmentFixtures.readyShipment();
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(jpaRepository).should().save(entity);
        }

        @Test
        @DisplayName("SHIPPED 상태 배송을 저장합니다")
        void persist_WithShippedShipment_Saves() {
            // given
            Shipment domain = ShipmentFixtures.shippedShipment();
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.shippedEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(jpaRepository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            Shipment domain = ShipmentFixtures.newShipment();
            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Domain을 Entity로 변환 후 일괄 저장합니다")
        void persistAll_WithMultipleDomains_SavesAll() {
            // given
            Shipment domain1 = ShipmentFixtures.readyShipment();
            Shipment domain2 = ShipmentFixtures.shippedShipment();
            List<Shipment> domains = List.of(domain1, domain2);

            ShipmentJpaEntity entity1 = ShipmentJpaEntityFixtures.readyEntity();
            ShipmentJpaEntity entity2 = ShipmentJpaEntityFixtures.shippedEntity();

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ShipmentJpaEntity>> captor = ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());

            List<ShipmentJpaEntity> savedEntities = captor.getValue();
            Assertions.assertThat(savedEntities).hasSize(2);
            Assertions.assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<Shipment> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ShipmentJpaEntity>> captor = ArgumentCaptor.forClass(List.class);
            then(jpaRepository).should().saveAll(captor.capture());
            Assertions.assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            Shipment domain1 = ShipmentFixtures.readyShipment();
            Shipment domain2 = ShipmentFixtures.shippedShipment();
            Shipment domain3 = ShipmentFixtures.deliveredShipment();
            List<Shipment> domains = List.of(domain1, domain2, domain3);

            ShipmentJpaEntity entity = ShipmentJpaEntityFixtures.readyEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(Shipment.class));
        }
    }
}
