package com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.ExchangeOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.exchangeoutbox.entity.ExchangeOutboxJpaEntity;
import com.ryuqq.marketplace.domain.exchange.outbox.ExchangeOutboxFixtures;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxStatus;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ExchangeOutboxJpaEntityMapperTest - 교환 아웃박스 Entity-Domain 매퍼 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ExchangeOutboxJpaEntityMapper 단위 테스트")
class ExchangeOutboxJpaEntityMapperTest {

    private ExchangeOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ExchangeOutboxJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("신규 Domain(isNew=true)을 Entity로 변환 시 id가 null입니다")
        void toEntity_WithNewDomain_ReturnsEntityWithNullId() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.newExchangeOutbox();

            // when
            ExchangeOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("기존 Domain(isNew=false)을 Entity로 변환 시 id가 설정됩니다")
        void toEntity_WithExistingDomain_ReturnsEntityWithId() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.pendingExchangeOutbox();

            // when
            ExchangeOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
        }

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingDomain_ConvertsStatusCorrectly() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.pendingExchangeOutbox();

            // when
            ExchangeOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ExchangeOutboxStatus.PENDING.name());
            assertThat(entity.getOrderItemId()).isEqualTo(domain.orderItemIdValue());
            assertThat(entity.getOutboxType()).isEqualTo(domain.outboxType().name());
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedDomain_ConvertsCorrectly() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.completedExchangeOutbox();

            // when
            ExchangeOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ExchangeOutboxStatus.COMPLETED.name());
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환 시 errorMessage가 설정됩니다")
        void toEntity_WithFailedDomain_SetsErrorMessage() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.failedExchangeOutbox();

            // when
            ExchangeOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ExchangeOutboxStatus.FAILED.name());
            assertThat(entity.getErrorMessage()).isNotNull();
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
        }

        @Test
        @DisplayName("payload, version 필드가 정확히 변환됩니다")
        void toEntity_WithDomain_ConvertsPayloadAndVersion() {
            // given
            ExchangeOutbox domain = ExchangeOutboxFixtures.completedExchangeOutbox();

            // when
            ExchangeOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getVersion()).isEqualTo(domain.version());
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
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();

            // when
            ExchangeOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.orderItemIdValue()).isEqualTo(entity.getOrderItemId());
            assertThat(domain.outboxType())
                    .isEqualTo(ExchangeOutboxType.valueOf(entity.getOutboxType()));
            assertThat(domain.status()).isEqualTo(ExchangeOutboxStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
        }

        @Test
        @DisplayName("COMPLETED Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.completedEntity();

            // when
            ExchangeOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ExchangeOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("FAILED Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.failedEntity();

            // when
            ExchangeOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ExchangeOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isNotNull();
            assertThat(domain.isFailed()).isTrue();
        }

        @Test
        @DisplayName("idempotencyKey 필드가 정확히 변환됩니다")
        void toDomain_WithEntity_ConvertsIdempotencyKey() {
            // given
            ExchangeOutboxJpaEntity entity = ExchangeOutboxJpaEntityFixtures.pendingEntity();

            // when
            ExchangeOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesCoreData() {
            // given
            ExchangeOutboxJpaEntity original = ExchangeOutboxJpaEntityFixtures.pendingEntity();

            // when
            ExchangeOutbox domain = mapper.toDomain(original);
            ExchangeOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getOrderItemId()).isEqualTo(original.getOrderItemId());
            assertThat(converted.getOutboxType()).isEqualTo(original.getOutboxType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }
    }
}
