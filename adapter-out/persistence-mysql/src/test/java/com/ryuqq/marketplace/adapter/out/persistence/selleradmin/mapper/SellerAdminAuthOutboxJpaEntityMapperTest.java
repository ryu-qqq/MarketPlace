package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminAuthOutboxJpaEntity;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminAuthOutbox;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminAuthOutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAdminAuthOutboxJpaEntityMapperTest - 셀러 관리자 인증 Outbox 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerAdminAuthOutboxJpaEntityMapper 테스트")
class SellerAdminAuthOutboxJpaEntityMapperTest {

    private final SellerAdminAuthOutboxJpaEntityMapper sut =
            new SellerAdminAuthOutboxJpaEntityMapper();

    @Nested
    @DisplayName("toEntity() - Domain -> Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환한다")
        void toEntity_Pending() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.pendingSellerAdminAuthOutbox();

            // when
            SellerAdminAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getSellerAdminId()).isEqualTo(domain.sellerAdminIdValue());
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getStatus()).isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.PENDING);
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getCreatedAt()).isEqualTo(domain.createdAt());
            assertThat(entity.getUpdatedAt()).isEqualTo(domain.updatedAt());
            assertThat(entity.getProcessedAt()).isNull();
            assertThat(entity.getErrorMessage()).isNull();
            assertThat(entity.getVersion()).isEqualTo(domain.version());
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("PROCESSING 상태 Domain을 Entity로 변환한다")
        void toEntity_Processing() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.processingSellerAdminAuthOutbox();

            // when
            SellerAdminAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환한다")
        void toEntity_Completed() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.completedSellerAdminAuthOutbox();

            // when
            SellerAdminAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환한다")
        void toEntity_Failed() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.failedSellerAdminAuthOutbox();

            // when
            SellerAdminAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("새 Domain(ID=null)을 Entity로 변환한다")
        void toEntity_NewDomain() {
            // given
            SellerAdminAuthOutbox domain = SellerAdminFixtures.newSellerAdminAuthOutbox();

            // when
            SellerAdminAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getStatus()).isEqualTo(SellerAdminAuthOutboxJpaEntity.Status.PENDING);
        }
    }

    @Nested
    @DisplayName("toDomain() - Entity -> Domain 변환")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환한다")
        void toDomain_Pending() {
            // given
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAdminAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerAdminIdValue()).isEqualTo(entity.getSellerAdminId());
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
            assertThat(domain.status()).isEqualTo(SellerAdminAuthOutboxStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
            assertThat(domain.processedAt()).isNull();
            assertThat(domain.errorMessage()).isNull();
            assertThat(domain.version()).isEqualTo(entity.getVersion());
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity를 Domain으로 변환한다")
        void toDomain_Processing() {
            // given
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.processingEntity();

            // when
            SellerAdminAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAdminAuthOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Completed() {
            // given
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.completedEntity();

            // when
            SellerAdminAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAdminAuthOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Failed() {
            // given
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntityFixtures.failedEntity();

            // when
            SellerAdminAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAdminAuthOutboxStatus.FAILED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("sellerAdminId가 null인 Entity를 Domain으로 변환한다")
        void toDomain_WithNullSellerAdminId() {
            // given
            SellerAdminAuthOutboxJpaEntity entity =
                    SellerAdminAuthOutboxJpaEntity.create(
                            1L,
                            null,
                            SellerAdminAuthOutboxJpaEntityFixtures.DEFAULT_PAYLOAD,
                            SellerAdminAuthOutboxJpaEntity.Status.PENDING,
                            0,
                            3,
                            java.time.Instant.now(),
                            java.time.Instant.now(),
                            null,
                            null,
                            0L,
                            "SAAO:unknown:123");

            // when
            SellerAdminAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.sellerAdminId()).isNull();
            assertThat(domain.sellerAdminIdValue()).isNull();
        }
    }

    @Nested
    @DisplayName("양방향 변환 일관성 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 값이 일치한다")
        void domainToEntityToDomain_ShouldPreserveValues() {
            // given
            SellerAdminAuthOutbox original =
                    SellerAdminFixtures.pendingSellerAdminAuthOutboxWithId();

            // when
            SellerAdminAuthOutboxJpaEntity entity = sut.toEntity(original);
            SellerAdminAuthOutbox converted = sut.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerAdminIdValue()).isEqualTo(original.sellerAdminIdValue());
            assertThat(converted.payload()).isEqualTo(original.payload());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.retryCount()).isEqualTo(original.retryCount());
            assertThat(converted.maxRetry()).isEqualTo(original.maxRetry());
            assertThat(converted.createdAt()).isEqualTo(original.createdAt());
            assertThat(converted.updatedAt()).isEqualTo(original.updatedAt());
            assertThat(converted.processedAt()).isEqualTo(original.processedAt());
            assertThat(converted.errorMessage()).isEqualTo(original.errorMessage());
            assertThat(converted.version()).isEqualTo(original.version());
            assertThat(converted.idempotencyKeyValue()).isEqualTo(original.idempotencyKeyValue());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 값이 일치한다")
        void entityToDomainToEntity_ShouldPreserveValues() {
            // given
            SellerAdminAuthOutboxJpaEntity original =
                    SellerAdminAuthOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAdminAuthOutbox domain = sut.toDomain(original);
            SellerAdminAuthOutboxJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerAdminId()).isEqualTo(original.getSellerAdminId());
            assertThat(converted.getPayload()).isEqualTo(original.getPayload());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
            assertThat(converted.getCreatedAt()).isEqualTo(original.getCreatedAt());
            assertThat(converted.getUpdatedAt()).isEqualTo(original.getUpdatedAt());
            assertThat(converted.getProcessedAt()).isEqualTo(original.getProcessedAt());
            assertThat(converted.getErrorMessage()).isEqualTo(original.getErrorMessage());
            assertThat(converted.getVersion()).isEqualTo(original.getVersion());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }
    }
}
