package com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.ShipmentOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shipmentoutbox.entity.ShipmentOutboxJpaEntity;
import com.ryuqq.marketplace.domain.shipment.outbox.ShipmentOutboxFixtures;
import com.ryuqq.marketplace.domain.shipment.outbox.aggregate.ShipmentOutbox;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxStatus;
import com.ryuqq.marketplace.domain.shipment.outbox.vo.ShipmentOutboxType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ShipmentOutboxJpaEntityMapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만 포함.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ShipmentOutboxJpaEntityMapper 단위 테스트")
class ShipmentOutboxJpaEntityMapperTest {

    private ShipmentOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShipmentOutboxJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingOutbox_ConvertsCorrectly() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.pendingShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
            assertThat(entity.getOutboxType()).isEqualTo(ShipmentOutboxJpaEntity.OutboxType.SHIP);
            assertThat(entity.getStatus()).isEqualTo(ShipmentOutboxJpaEntity.Status.PENDING);
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
        }

        @Test
        @DisplayName("신규(isNew) Domain을 Entity로 변환하면 ID가 null입니다")
        void toEntity_WithNewOutbox_HasNullId() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.newShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("retryCount와 maxRetry가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithRetryFields_MapsCorrectly() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.pendingShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환하면 processedAt이 매핑됩니다")
        void toEntity_WithCompletedOutbox_MapsProcessedAt() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.completedShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ShipmentOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isEqualTo(domain.processedAt());
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환하면 errorMessage가 매핑됩니다")
        void toEntity_WithFailedOutbox_MapsErrorMessage() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.failedShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ShipmentOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getErrorMessage()).isEqualTo(domain.errorMessage());
        }

        @Test
        @DisplayName("idempotencyKey가 Entity에 올바르게 매핑됩니다")
        void toEntity_WithIdempotencyKey_MapsCorrectly() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.pendingShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("version이 Entity에 올바르게 매핑됩니다")
        void toEntity_WithVersion_MapsCorrectly() {
            // given
            ShipmentOutbox domain = ShipmentOutboxFixtures.completedShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getVersion()).isEqualTo(domain.version());
        }

        @Test
        @DisplayName("모든 OutboxType이 Entity 타입으로 올바르게 변환됩니다")
        void toEntity_WithAllOutboxTypes_ConvertsCorrectly() {
            // given
            ShipmentOutbox confirmDomain =
                    ShipmentOutboxFixtures.newShipmentOutbox(ShipmentOutboxType.CONFIRM);
            ShipmentOutbox shipDomain =
                    ShipmentOutboxFixtures.newShipmentOutbox(ShipmentOutboxType.SHIP);
            ShipmentOutbox deliverDomain =
                    ShipmentOutboxFixtures.newShipmentOutbox(ShipmentOutboxType.DELIVER);
            ShipmentOutbox cancelDomain =
                    ShipmentOutboxFixtures.newShipmentOutbox(ShipmentOutboxType.CANCEL);

            // when & then
            assertThat(mapper.toEntity(confirmDomain).getOutboxType())
                    .isEqualTo(ShipmentOutboxJpaEntity.OutboxType.CONFIRM);
            assertThat(mapper.toEntity(shipDomain).getOutboxType())
                    .isEqualTo(ShipmentOutboxJpaEntity.OutboxType.SHIP);
            assertThat(mapper.toEntity(deliverDomain).getOutboxType())
                    .isEqualTo(ShipmentOutboxJpaEntity.OutboxType.DELIVER);
            assertThat(mapper.toEntity(cancelDomain).getOutboxType())
                    .isEqualTo(ShipmentOutboxJpaEntity.OutboxType.CANCEL);
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.outboxType()).isEqualTo(ShipmentOutboxType.SHIP);
            assertThat(domain.status()).isEqualTo(ShipmentOutboxStatus.PENDING);
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
        }

        @Test
        @DisplayName("retryCount와 maxRetry가 Domain으로 올바르게 복원됩니다")
        void toDomain_WithRetryFields_RestoresCorrectly() {
            // given
            ShipmentOutboxJpaEntity entity =
                    ShipmentOutboxJpaEntityFixtures.pendingEntityWithRetry(2);

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
        }

        @Test
        @DisplayName("COMPLETED Entity를 Domain으로 변환하면 processedAt이 복원됩니다")
        void toDomain_WithCompletedEntity_RestoresProcessedAt() {
            // given
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.completedEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ShipmentOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isEqualTo(entity.getProcessedAt());
        }

        @Test
        @DisplayName("FAILED Entity를 Domain으로 변환하면 errorMessage가 복원됩니다")
        void toDomain_WithFailedEntity_RestoresErrorMessage() {
            // given
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.failedEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isEqualTo(entity.getErrorMessage());
        }

        @Test
        @DisplayName("idempotencyKey가 Domain으로 올바르게 복원됩니다")
        void toDomain_WithIdempotencyKey_RestoresCorrectly() {
            // given
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.pendingEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("version이 Domain으로 올바르게 복원됩니다")
        void toDomain_WithVersion_RestoresCorrectly() {
            // given
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.completedEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.version()).isEqualTo(entity.getVersion());
        }

        @Test
        @DisplayName("PROCESSING Entity를 Domain으로 변환합니다")
        void toDomain_WithProcessingEntity_ConvertsCorrectly() {
            // given
            ShipmentOutboxJpaEntity entity = ShipmentOutboxJpaEntityFixtures.processingEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ShipmentOutboxStatus.PROCESSING);
            assertThat(domain.processedAt()).isNull();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesCoreData() {
            // given
            ShipmentOutbox original = ShipmentOutboxFixtures.pendingShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(original);
            ShipmentOutbox converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.orderItemIdValue()).isEqualTo(original.orderItemIdValue());
            assertThat(converted.outboxType()).isEqualTo(original.outboxType());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.payload()).isEqualTo(original.payload());
            assertThat(converted.retryCount()).isEqualTo(original.retryCount());
            assertThat(converted.idempotencyKeyValue()).isEqualTo(original.idempotencyKeyValue());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            ShipmentOutboxJpaEntity original = ShipmentOutboxJpaEntityFixtures.pendingEntity();

            // when
            ShipmentOutbox domain = mapper.toDomain(original);
            ShipmentOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getOutboxType()).isEqualTo(original.getOutboxType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getPayload()).isEqualTo(original.getPayload());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }

        @Test
        @DisplayName("FAILED Domain 양방향 변환 시 errorMessage가 보존됩니다")
        void roundTrip_FailedOutbox_PreservesErrorMessage() {
            // given
            ShipmentOutbox original = ShipmentOutboxFixtures.failedShipmentOutbox();

            // when
            ShipmentOutboxJpaEntity entity = mapper.toEntity(original);
            ShipmentOutbox converted = mapper.toDomain(entity);

            // then
            assertThat(converted.errorMessage()).isEqualTo(original.errorMessage());
            assertThat(converted.status()).isEqualTo(ShipmentOutboxStatus.FAILED);
        }
    }
}
