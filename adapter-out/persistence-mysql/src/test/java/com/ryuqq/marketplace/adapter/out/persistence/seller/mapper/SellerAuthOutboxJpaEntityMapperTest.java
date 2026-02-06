package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerAuthOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerAuthOutboxJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
import com.ryuqq.marketplace.domain.seller.vo.SellerAuthOutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAuthOutboxJpaEntityMapper 테스트")
class SellerAuthOutboxJpaEntityMapperTest {

    private final SellerAuthOutboxJpaEntityMapper sut = new SellerAuthOutboxJpaEntityMapper();

    @Nested
    @DisplayName("toEntity() - Domain → Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환한다")
        void toEntity_Pending() {
            // given
            SellerAuthOutbox domain = SellerFixtures.pendingSellerAuthOutbox();

            // when
            SellerAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getStatus()).isEqualTo(SellerAuthOutboxJpaEntity.Status.PENDING);
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
            SellerAuthOutbox domain = SellerFixtures.processingSellerAuthOutbox();

            // when
            SellerAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(SellerAuthOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환한다")
        void toEntity_Completed() {
            // given
            SellerAuthOutbox domain = SellerFixtures.completedSellerAuthOutbox();

            // when
            SellerAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(SellerAuthOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환한다")
        void toEntity_Failed() {
            // given
            SellerAuthOutbox domain = SellerFixtures.failedSellerAuthOutbox();

            // when
            SellerAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(SellerAuthOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("새 Domain(ID=null)을 Entity로 변환한다")
        void toEntity_NewDomain() {
            // given
            SellerAuthOutbox domain =
                    SellerFixtures.newSellerAuthOutbox(
                            com.ryuqq.marketplace.domain.common.CommonVoFixtures.defaultSellerId());

            // when
            SellerAuthOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
        }
    }

    @Nested
    @DisplayName("toDomain() - Entity → Domain 변환")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환한다")
        void toDomain_Pending() {
            // given
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
            assertThat(domain.status()).isEqualTo(SellerAuthOutboxStatus.PENDING);
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
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.processingEntity();

            // when
            SellerAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAuthOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Completed() {
            // given
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.completedEntity();

            // when
            SellerAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAuthOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Failed() {
            // given
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.failedEntity();

            // when
            SellerAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAuthOutboxStatus.FAILED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환한다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerAuthOutboxJpaEntity entity = SellerAuthOutboxJpaEntityFixtures.newPendingEntity();

            // when
            SellerAuthOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
        }
    }

    @Nested
    @DisplayName("양방향 변환 일관성 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 값이 일치한다")
        void domainToEntityToDomain_ShouldPreserveValues() {
            // given
            SellerAuthOutbox original = SellerFixtures.pendingSellerAuthOutboxWithId();

            // when
            SellerAuthOutboxJpaEntity entity = sut.toEntity(original);
            SellerAuthOutbox converted = sut.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
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
        @DisplayName("Entity → Domain → Entity 변환 시 값이 일치한다")
        void entityToDomainToEntity_ShouldPreserveValues() {
            // given
            SellerAuthOutboxJpaEntity original = SellerAuthOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAuthOutbox domain = sut.toDomain(original);
            SellerAuthOutboxJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
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
