package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.IntelligenceOutboxJpaEntity;
import com.ryuqq.marketplace.domain.productintelligence.ProductIntelligenceFixtures;
import com.ryuqq.marketplace.domain.productintelligence.aggregate.IntelligenceOutbox;
import com.ryuqq.marketplace.domain.productintelligence.vo.IntelligenceOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * IntelligenceOutboxJpaEntityMapperTest - Intelligence Pipeline Outbox Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("IntelligenceOutboxJpaEntityMapper 단위 테스트")
class IntelligenceOutboxJpaEntityMapperTest {

    private IntelligenceOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new IntelligenceOutboxJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 IntelligenceOutbox의 모든 필드를 Entity로 변환합니다")
        void toEntity_WithPendingOutbox_ConvertsAllFieldsCorrectly() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.existingPendingOutbox();

            // when
            IntelligenceOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupId());
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getStatus()).isEqualTo(IntelligenceOutboxJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("신규 IntelligenceOutbox 변환 시 ID가 null입니다")
        void toEntity_WithNewOutbox_IdIsNull() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.newPendingOutbox();

            // when
            IntelligenceOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("idempotencyKey가 Entity에 설정됩니다")
        void toEntity_WithIdempotencyKey_SetsKeyCorrectly() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.existingPendingOutbox();

            // when
            IntelligenceOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
            assertThat(entity.getIdempotencyKey()).isNotBlank();
        }

        @Test
        @DisplayName("profileId가 null인 PENDING 상태 Entity를 변환합니다")
        void toEntity_WithNullProfileId_ProfileIdIsNull() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.newPendingOutbox();

            // when
            IntelligenceOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getProfileId()).isNull();
        }

        @Test
        @DisplayName("PENDING 상태를 Entity Status로 변환합니다")
        void toEntity_WithPendingStatus_ConvertsToPendingEntityStatus() {
            // given
            IntelligenceOutbox domain = ProductIntelligenceFixtures.existingPendingOutbox();

            // when
            IntelligenceOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(IntelligenceOutboxJpaEntity.Status.PENDING);
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
        void toDomain_WithPendingEntity_ConvertsAllFieldsCorrectly() {
            // given
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            1L, 100L, "PI:100:1740556800000");

            // when
            IntelligenceOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupId()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.status()).isEqualTo(IntelligenceOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 forNew ID가 생성됩니다")
        void toDomain_WithNullId_CreatesNewId() {
            // given
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity();

            // when
            IntelligenceOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
        }

        @Test
        @DisplayName("SENT 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithSentEntity_ConvertsStatus() {
            // given
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.sentOutboxEntity(
                            1L, 100L, "PI:100:1740556800001");

            // when
            IntelligenceOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(IntelligenceOutboxStatus.SENT);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsStatus() {
            // given
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.completedOutboxEntity(
                            1L, 100L, "PI:100:1740556800002");

            // when
            IntelligenceOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(IntelligenceOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsStatus() {
            // given
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.failedOutboxEntity(
                            1L, 100L, "PI:100:1740556800003");

            // when
            IntelligenceOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(IntelligenceOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isEqualTo("최대 재시도 초과");
        }

        @Test
        @DisplayName("idempotencyKey가 Domain에 설정됩니다")
        void toDomain_WithIdempotencyKey_SetsKeyCorrectly() {
            // given
            String idempotencyKey = "PI:100:1740557000000";
            IntelligenceOutboxJpaEntity entity =
                    ProductIntelligenceJpaEntityFixtures.pendingOutboxEntity(
                            1L, 100L, idempotencyKey);

            // when
            IntelligenceOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idempotencyKeyValue()).isEqualTo(idempotencyKey);
        }
    }
}
