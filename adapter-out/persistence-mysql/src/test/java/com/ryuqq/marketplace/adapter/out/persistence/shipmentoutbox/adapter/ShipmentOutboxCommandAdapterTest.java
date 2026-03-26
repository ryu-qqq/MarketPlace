package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.ShipmentOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.mapper.ShipmentOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.util.List;
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
 * ShipmentOutboxCommandAdapter 단위 테스트.
 *
 * <p>PER-ADP-003: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-006: Mapper를 통해 Domain -> Entity 변환 후 저장.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentOutboxCommandAdapter 단위 테스트")
class ShipmentOutboxCommandAdapterTest {

    @Mock private ShipmentOutboxJpaRepository repository;
    @Mock private ShipmentOutboxJpaEntityMapper mapper;

    @InjectMocks private ShipmentOutboxCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Mapper를 통해 Entity로 변환한 후 repository.save를 호출합니다")
        void persist_CallsMapperAndRepository() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutboxJpaEntity savedEntity = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(outbox);

            // then
            then(mapper).should().toEntity(outbox);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("저장 후 ID를 반환합니다")
        void persist_WithNewOutbox_ReturnsId() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutboxJpaEntity savedEntity = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(outbox);

            // then
            assertThat(result).isEqualTo(savedEntity.getId());
        }

        @Test
        @DisplayName("저장 후 Domain의 version이 갱신됩니다")
        void persist_AfterSave_RefreshesVersion() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutboxJpaEntity savedEntity = ShipmentOutboxJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(outbox);

            // then
            assertThat(outbox.version()).isEqualTo(savedEntity.getVersion());
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번만 호출됩니다")
        void persist_CallsMapperExactlyOnce() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutboxJpaEntity savedEntity = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(outbox);

            // then
            then(mapper).should(times(1)).toEntity(outbox);
            then(mapper).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("PENDING 상태 아웃박스를 저장합니다")
        void persist_WithPendingOutbox_SavesSuccessfully() {
            // given
            ShipmentOutbox outbox = ShipmentOutboxFixtures.pendingShipmentOutbox();
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutboxJpaEntity savedEntity = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);

            given(mapper.toEntity(outbox)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            commandAdapter.persist(outbox);

            // then
            then(repository).should().save(entity);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 아웃박스를 일괄 저장합니다")
        void persistAll_WithMultipleOutboxes_SavesAll() {
            // given
            ShipmentOutbox outbox1 = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutbox outbox2 = ShipmentOutboxFixtures.newShipmentOutbox();
            List<ShipmentOutbox> outboxes = List.of(outbox1, outbox2);

            ShipmentOutboxJpaEntity entity1 = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);
            ShipmentOutboxJpaEntity entity2 = ShipmentOutboxJpaEntityFixtures.pendingEntity(2L);

            given(mapper.toEntity(outbox1)).willReturn(entity1);
            given(mapper.toEntity(outbox2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(outboxes);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ShipmentOutboxJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());

            List<ShipmentOutboxJpaEntity> savedEntities = captor.getValue();
            assertThat(savedEntities).hasSize(2);
            assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<ShipmentOutbox> emptyList = List.of();
            given(repository.saveAll(List.of())).willReturn(List.of());

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ShipmentOutboxJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(repository).should().saveAll(captor.capture());
            assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachOutbox() {
            // given
            ShipmentOutbox outbox1 = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutbox outbox2 = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutbox outbox3 = ShipmentOutboxFixtures.newShipmentOutbox();
            List<ShipmentOutbox> outboxes = List.of(outbox1, outbox2, outbox3);

            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            given(mapper.toEntity(outbox1)).willReturn(entity);
            given(mapper.toEntity(outbox2)).willReturn(entity);
            given(mapper.toEntity(outbox3)).willReturn(entity);
            given(repository.saveAll(org.mockito.ArgumentMatchers.anyList()))
                    .willReturn(List.of(entity, entity, entity));

            // when
            commandAdapter.persistAll(outboxes);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(ShipmentOutbox.class));
        }

        @Test
        @DisplayName("저장 후 각 Domain의 version이 갱신됩니다")
        void persistAll_AfterSave_RefreshesVersionForEachOutbox() {
            // given
            ShipmentOutbox outbox1 = ShipmentOutboxFixtures.newShipmentOutbox();
            ShipmentOutbox outbox2 = ShipmentOutboxFixtures.newShipmentOutbox();
            List<ShipmentOutbox> outboxes = List.of(outbox1, outbox2);

            ShipmentOutboxJpaEntity entity1 = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);
            ShipmentOutboxJpaEntity entity2 = ShipmentOutboxJpaEntityFixtures.pendingEntity(2L);
            ShipmentOutboxJpaEntity savedEntity1 =
                    ShipmentOutboxJpaEntityFixtures.completedEntity();
            ShipmentOutboxJpaEntity savedEntity2 =
                    ShipmentOutboxJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(outbox1)).willReturn(entity1);
            given(mapper.toEntity(outbox2)).willReturn(entity2);
            given(repository.saveAll(List.of(entity1, entity2)))
                    .willReturn(List.of(savedEntity1, savedEntity2));

            // when
            commandAdapter.persistAll(outboxes);

            // then
            assertThat(outbox1.version()).isEqualTo(savedEntity1.getVersion());
            assertThat(outbox2.version()).isEqualTo(savedEntity2.getVersion());
        }
    }
}
