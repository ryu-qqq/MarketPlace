package com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.LegacyConversionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacyconversion.entity.LegacyConversionOutboxJpaEntity;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyConversionOutbox;
import com.ryuqq.marketplace.domain.legacyconversion.vo.LegacyConversionOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyConversionOutboxJpaEntityMapperTest - Outbox Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyConversionOutboxJpaEntityMapper 단위 테스트")
class LegacyConversionOutboxJpaEntityMapperTest {

    private LegacyConversionOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyConversionOutboxJpaEntityMapper();
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
            LegacyConversionOutbox domain = LegacyConversionFixtures.pendingOutbox();

            // when
            LegacyConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getLegacyProductGroupId()).isEqualTo(domain.legacyProductGroupId());
            assertThat(entity.getStatus())
                    .isEqualTo(LegacyConversionOutboxJpaEntity.Status.PENDING);
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
            LegacyConversionOutbox domain = LegacyConversionFixtures.processingOutbox();

            // when
            LegacyConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(LegacyConversionOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedOutbox_ConvertsStatus() {
            // given
            LegacyConversionOutbox domain = LegacyConversionFixtures.completedOutbox();

            // when
            LegacyConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(LegacyConversionOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedOutbox_ConvertsStatus() {
            // given
            LegacyConversionOutbox domain = LegacyConversionFixtures.failedOutbox();

            // when
            LegacyConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(LegacyConversionOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("신규 Domain(ID 없음)을 Entity로 변환합니다")
        void toEntity_WithNewOutbox_ConvertsCorrectly() {
            // given
            LegacyConversionOutbox domain = LegacyConversionFixtures.newPendingOutbox();

            // when
            LegacyConversionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getLegacyProductGroupId()).isEqualTo(domain.legacyProductGroupId());
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
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();

            // when
            LegacyConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.legacyProductGroupId()).isEqualTo(entity.getLegacyProductGroupId());
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
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.processingEntity();

            // when
            LegacyConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsStatus() {
            // given
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.completedEntity();

            // when
            LegacyConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsStatus() {
            // given
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.failedEntity();

            // when
            LegacyConversionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(LegacyConversionOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환합니다")
        void toDomain_WithNullIdEntity_CreatesNewDomain() {
            // given
            LegacyConversionOutboxJpaEntity entity =
                    LegacyConversionOutboxJpaEntityFixtures.newPendingEntity();

            // when
            LegacyConversionOutbox domain = mapper.toDomain(entity);

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
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            LegacyConversionOutbox original = LegacyConversionFixtures.pendingOutbox();

            // when
            LegacyConversionOutboxJpaEntity entity = mapper.toEntity(original);
            LegacyConversionOutbox converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.legacyProductGroupId()).isEqualTo(original.legacyProductGroupId());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.retryCount()).isEqualTo(original.retryCount());
            assertThat(converted.maxRetry()).isEqualTo(original.maxRetry());
            assertThat(converted.version()).isEqualTo(original.version());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            LegacyConversionOutboxJpaEntity original =
                    LegacyConversionOutboxJpaEntityFixtures.pendingEntity();

            // when
            LegacyConversionOutbox domain = mapper.toDomain(original);
            LegacyConversionOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getLegacyProductGroupId())
                    .isEqualTo(original.getLegacyProductGroupId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
            assertThat(converted.getVersion()).isEqualTo(original.getVersion());
        }
    }
}
