package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.ShipmentOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.mapper.ShipmentOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxJpaRepository;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.repository.ShipmentOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import java.time.Instant;
import java.util.List;
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
 * ShipmentOutboxQueryAdapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 JpaRepository + QueryDslRepository를 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentOutboxQueryAdapter 단위 테스트")
class ShipmentOutboxQueryAdapterTest {

    @Mock private ShipmentOutboxJpaRepository jpaRepository;
    @Mock private ShipmentOutboxQueryDslRepository queryDslRepository;
    @Mock private ShipmentOutboxJpaEntityMapper mapper;

    @InjectMocks private ShipmentOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 아웃박스 목록을 조회하여 Domain으로 변환합니다")
        void findPendingOutboxes_WithPendingEntities_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 10;
            ShipmentOutboxJpaEntity entity1 = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);
            ShipmentOutboxJpaEntity entity2 = ShipmentOutboxJpaEntityFixtures.pendingEntity(2L);
            ShipmentOutbox domain1 = ShipmentOutboxFixtures.pendingShipmentOutbox();
            ShipmentOutbox domain2 = ShipmentOutboxFixtures.pendingShipmentOutbox();

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ShipmentOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, batchSize);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoEntities_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 10;

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of());

            // when
            List<ShipmentOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("batchSize만큼 조회를 요청합니다")
        void findPendingOutboxes_PassesBatchSizeToRepository() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 50;

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of());

            // when
            queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, batchSize);
        }

        @Test
        @DisplayName("각 Entity에 대해 Mapper가 호출됩니다")
        void findPendingOutboxes_CallsMapperForEachEntity() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 10;
            ShipmentOutboxJpaEntity entity1 = ShipmentOutboxJpaEntityFixtures.pendingEntity(1L);
            ShipmentOutboxJpaEntity entity2 = ShipmentOutboxJpaEntityFixtures.pendingEntity(2L);
            ShipmentOutboxJpaEntity entity3 = ShipmentOutboxJpaEntityFixtures.pendingEntity(3L);

            given(queryDslRepository.findPendingOutboxes(beforeTime, batchSize))
                    .willReturn(List.of(entity1, entity2, entity3));
            given(mapper.toDomain(entity1))
                    .willReturn(ShipmentOutboxFixtures.pendingShipmentOutbox());
            given(mapper.toDomain(entity2))
                    .willReturn(ShipmentOutboxFixtures.pendingShipmentOutbox());
            given(mapper.toDomain(entity3))
                    .willReturn(ShipmentOutboxFixtures.pendingShipmentOutbox());

            // when
            List<ShipmentOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(3);
            then(mapper).should().toDomain(entity1);
            then(mapper).should().toDomain(entity2);
            then(mapper).should().toDomain(entity3);
        }
    }

    // ========================================================================
    // 2. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃 PROCESSING 아웃박스 목록을 조회하여 Domain으로 변환합니다")
        void findProcessingTimeoutOutboxes_WithTimeoutEntities_ReturnsDomainList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 10;
            ShipmentOutboxJpaEntity entity1 = ShipmentOutboxJpaEntityFixtures.processingEntity();
            ShipmentOutbox domain1 = ShipmentOutboxFixtures.processingShipmentOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of(entity1));
            given(mapper.toDomain(entity1)).willReturn(domain1);

            // when
            List<ShipmentOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
        }

        @Test
        @DisplayName("타임아웃 아웃박스가 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoEntities_ReturnsEmptyList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of());

            // when
            List<ShipmentOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("timeoutBefore와 batchSize가 repository에 그대로 전달됩니다")
        void findProcessingTimeoutOutboxes_DelegatesToRepositoryWithCorrectParams() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(600);
            int batchSize = 30;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of());

            // when
            queryAdapter.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
        }
    }

    // ========================================================================
    // 3. getById 테스트
    // ========================================================================

    @Nested
    @DisplayName("getById 메서드 테스트")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void getById_WithExistingId_ReturnsDomain() {
            // given
            Long outboxId = ShipmentOutboxJpaEntityFixtures.DEFAULT_ID;
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutbox domain = ShipmentOutboxFixtures.pendingShipmentOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            ShipmentOutbox result = queryAdapter.getById(outboxId);

            // then
            assertThat(result).isEqualTo(domain);
            then(jpaRepository).should().findById(outboxId);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 IllegalStateException을 던집니다")
        void getById_WithNonExistingId_ThrowsIllegalStateException() {
            // given
            Long outboxId = 9999L;

            given(jpaRepository.findById(outboxId)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> queryAdapter.getById(outboxId))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ShipmentOutbox를 찾을 수 없습니다")
                    .hasMessageContaining(String.valueOf(outboxId));
        }

        @Test
        @DisplayName("getById 호출 시 jpaRepository에 ID가 전달됩니다")
        void getById_DelegatesToJpaRepositoryWithId() {
            // given
            Long outboxId = ShipmentOutboxJpaEntityFixtures.DEFAULT_ID;
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();
            ShipmentOutbox domain = ShipmentOutboxFixtures.pendingShipmentOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.getById(outboxId);

            // then
            then(jpaRepository).should().findById(outboxId);
        }

        @Test
        @DisplayName("getById 성공 시 Mapper가 호출됩니다")
        void getById_OnSuccess_CallsMapper() {
            // given
            Long outboxId = ShipmentOutboxJpaEntityFixtures.DEFAULT_ID;
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.completedEntity();
            ShipmentOutbox domain = ShipmentOutboxFixtures.completedShipmentOutbox();

            given(jpaRepository.findById(outboxId)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            queryAdapter.getById(outboxId);

            // then
            then(mapper).should().toDomain(entity);
        }
    }
}
