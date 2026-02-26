package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminEmailOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminEmailOutboxJpaEntity;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.selleradmin.SellerAdminFixtures;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdminEmailOutbox;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminEmailOutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerAdminEmailOutboxJpaEntityMapper 테스트")
class SellerAdminEmailOutboxJpaEntityMapperTest {

    private final SellerAdminEmailOutboxJpaEntityMapper sut =
            new SellerAdminEmailOutboxJpaEntityMapper();

    @Nested
    @DisplayName("toEntity() - Domain → Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환한다")
        void toEntity_Pending() {
            // given
            SellerAdminEmailOutbox domain = SellerAdminFixtures.pendingSellerAdminEmailOutbox();

            // when
            SellerAdminEmailOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getPayload()).isEqualTo(domain.payload());
            assertThat(entity.getStatus())
                    .isEqualTo(SellerAdminEmailOutboxJpaEntity.Status.PENDING);
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
            SellerAdminEmailOutbox domain = SellerAdminFixtures.processingSellerAdminEmailOutbox();

            // when
            SellerAdminEmailOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(SellerAdminEmailOutboxJpaEntity.Status.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환한다")
        void toEntity_Completed() {
            // given
            SellerAdminEmailOutbox domain = SellerAdminFixtures.completedSellerAdminEmailOutbox();

            // when
            SellerAdminEmailOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(SellerAdminEmailOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환한다")
        void toEntity_Failed() {
            // given
            SellerAdminEmailOutbox domain = SellerAdminFixtures.failedSellerAdminEmailOutbox();

            // when
            SellerAdminEmailOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(SellerAdminEmailOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("새 Domain(ID=null)을 Entity로 변환한다")
        void toEntity_NewDomain() {
            // given
            SellerAdminEmailOutbox domain =
                    SellerAdminFixtures.newSellerAdminEmailOutbox(
                            CommonVoFixtures.defaultSellerId());

            // when
            SellerAdminEmailOutboxJpaEntity entity = sut.toEntity(domain);

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
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAdminEmailOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.payload()).isEqualTo(entity.getPayload());
            assertThat(domain.status()).isEqualTo(SellerAdminEmailOutboxStatus.PENDING);
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
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.processingEntity();

            // when
            SellerAdminEmailOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAdminEmailOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Completed() {
            // given
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.completedEntity();

            // when
            SellerAdminEmailOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAdminEmailOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Failed() {
            // given
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.failedEntity();

            // when
            SellerAdminEmailOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(SellerAdminEmailOutboxStatus.FAILED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ID가 있는 Entity를 Domain으로 변환한다")
        void toDomain_WithEntity_ConvertsCorrectly() {
            // given
            SellerAdminEmailOutboxJpaEntity entity =
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAdminEmailOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
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
            SellerAdminEmailOutbox original =
                    SellerAdminFixtures.pendingSellerAdminEmailOutboxWithId();

            // when
            SellerAdminEmailOutboxJpaEntity entity = sut.toEntity(original);
            SellerAdminEmailOutbox converted = sut.toDomain(entity);

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
            SellerAdminEmailOutboxJpaEntity original =
                    SellerAdminEmailOutboxJpaEntityFixtures.pendingEntity();

            // when
            SellerAdminEmailOutbox domain = sut.toDomain(original);
            SellerAdminEmailOutboxJpaEntity converted = sut.toEntity(domain);

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
