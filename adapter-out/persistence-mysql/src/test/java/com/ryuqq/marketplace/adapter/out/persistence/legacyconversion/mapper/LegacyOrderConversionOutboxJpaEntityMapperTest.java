package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyOrderConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyOrderConversionOutboxJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyOrderConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderConversionOutboxId;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyOrderConversionOutboxJpaEntityMapperTest - 주문 Outbox Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyOrderConversionOutboxJpaEntityMapper 단위 테스트")
class LegacyOrderConversionOutboxJpaEntityMapperTest {

    private LegacyOrderConversionOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyOrderConversionOutboxJpaEntityMapper();
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
            Instant now = Instant.now();
            LegacyOrderConversionOutbox domain =
                    LegacyOrderConversionOutbox.reconstitute(
                            LegacyOrderConversionOutboxId.of(1L),
                            LegacyOrderConversionOutboxJpaEntityFixtures.DEFAULT_LEGACY_ORDER_ID,
                            LegacyOrderConversionOutboxJpaEntityFixtures.DEFAULT_LEGACY_PAYMENT_ID,
                            LegacyConversionOutboxStatus.PENDING,
                            LegacyOrderConversionOutboxJpaEntityFixtures.DEFAULT_RETRY_COUNT,
                            LegacyOrderConversionOutboxJpaEntityFixtures.DEFAULT_MAX_RETRY,
                            now,
                            now,
                            null,
                            null,
                            0L);

            // when
            LegacyOrderConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getLegacyOrderId()).isEqualTo(domain.legacyOrderId());
            assertThat(entity.getLegacyPaymentId()).isEqualTo(domain.legacyPaymentId());
            assertThat(entity.getStatus()).isEqualTo("PENDING");
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
            assertThat(entity.getProcessedAt()).isNull();
            assertThat(entity.getErrorMessage()).isNull();
            assertThat(entity.getVersion()).isEqualTo(domain.version());
        }

        @Test
        @DisplayName("PROCESSING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithProcessingOutbox_ConvertsStatus() {
            // given
            Instant now = Instant.now();
            LegacyOrderConversionOutbox domain =
                    LegacyOrderConversionOutbox.reconstitute(
                            LegacyOrderConversionOutboxId.of(1L),
                            10001L,
                            20001L,
                            LegacyConversionOutboxStatus.PROCESSING,
                            0,
                            3,
                            now,
                            now,
                            null,
                            null,
                            1L);

            // when
            LegacyOrderConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("PROCESSING");
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedOutbox_ConvertsStatus() {
            // given
            Instant now = Instant.now();
            LegacyOrderConversionOutbox domain =
                    LegacyOrderConversionOutbox.reconstitute(
                            LegacyOrderConversionOutboxId.of(1L),
                            10001L,
                            20001L,
                            LegacyConversionOutboxStatus.COMPLETED,
                            0,
                            3,
                            now,
                            now,
                            now,
                            null,
                            2L);

            // when
            LegacyOrderConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("COMPLETED");
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedOutbox_ConvertsStatus() {
            // given
            Instant now = Instant.now();
            LegacyOrderConversionOutbox domain =
                    LegacyOrderConversionOutbox.reconstitute(
                            LegacyOrderConversionOutboxId.of(1L),
                            10001L,
                            20001L,
                            LegacyConversionOutboxStatus.FAILED,
                            3,
                            3,
                            now,
                            now,
                            now,
                            "최대 재시도 횟수 초과",
                            3L);

            // when
            LegacyOrderConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("FAILED");
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("신규 Domain(ID 없음)을 Entity로 변환합니다")
        void toEntity_WithNewOutbox_ConvertsCorrectly() {
            // given
            LegacyOrderConversionOutbox domain =
                    LegacyOrderConversionOutbox.forNew(10001L, 20001L, Instant.now());

            // when
            LegacyOrderConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getLegacyOrderId()).isEqualTo(domain.legacyOrderId());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPendingEntity_ConvertsCorrectly() {
            // given
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.pendingEntity();

            // when
            LegacyOrderConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.legacyOrderId()).isEqualTo(entity.getLegacyOrderId());
            assertThat(domain.legacyPaymentId()).isEqualTo(entity.getLegacyPaymentId());
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
            assertThat(domain.processedAt()).isNull();
            assertThat(domain.errorMessage()).isNull();
            assertThat(domain.version()).isEqualTo(entity.getVersion());
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithProcessingEntity_ConvertsStatus() {
            // given
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.processingEntity();

            // when
            LegacyOrderConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsStatus() {
            // given
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.completedEntity();

            // when
            LegacyOrderConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsStatus() {
            // given
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.failedEntity();

            // when
            LegacyOrderConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환합니다")
        void toDomain_WithNullIdEntity_CreatesNewDomain() {
            // given
            LegacyOrderConversionOutboxJpaEntity entity =
                    LegacyOrderConversionOutboxJpaEntityFixtures.newPendingEntity();

            // when
            LegacyOrderConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            LegacyOrderConversionOutboxJpaEntity original =
                    LegacyOrderConversionOutboxJpaEntityFixtures.pendingEntity();

            // when
            LegacyOrderConversionOutbox domain = mapper.toDomain(original);
            LegacyOrderConversionOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getLegacyOrderId()).isEqualTo(original.getLegacyOrderId());
            assertThat(converted.getLegacyPaymentId()).isEqualTo(original.getLegacyPaymentId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
            assertThat(converted.getVersion()).isEqualTo(original.getVersion());
        }
    }
}
