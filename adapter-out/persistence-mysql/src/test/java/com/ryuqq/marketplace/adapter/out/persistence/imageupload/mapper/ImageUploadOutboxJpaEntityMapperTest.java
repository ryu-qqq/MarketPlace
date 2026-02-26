package com.ryuqq.marketplace.adapter.out.persistence.imageupload.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.ImageUploadOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageUploadOutboxJpaEntityMapper 테스트")
class ImageUploadOutboxJpaEntityMapperTest {

    private final ImageUploadOutboxJpaEntityMapper sut = new ImageUploadOutboxJpaEntityMapper();

    @Nested
    @DisplayName("toEntity() - Domain -> Entity 변환")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환한다")
        void toEntity_Pending() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.pendingOutbox();

            // when
            ImageUploadOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSourceId()).isEqualTo(domain.sourceId());
            assertThat(entity.getSourceType()).isEqualTo(domain.sourceType());
            assertThat(entity.getOriginUrl()).isEqualTo(domain.originUrlValue());
            assertThat(entity.getStatus()).isEqualTo(ImageUploadOutboxStatus.PENDING);
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
            ImageUploadOutbox domain = ImageUploadFixtures.processingOutbox();

            // when
            ImageUploadOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ImageUploadOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환한다")
        void toEntity_Completed() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.completedOutbox();

            // when
            ImageUploadOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ImageUploadOutboxStatus.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환한다")
        void toEntity_Failed() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.failedOutbox();

            // when
            ImageUploadOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ImageUploadOutboxStatus.FAILED);
            assertThat(entity.getProcessedAt()).isNotNull();
            assertThat(entity.getErrorMessage()).isNotNull();
        }

        @Test
        @DisplayName("새 Domain(ID=null)을 Entity로 변환한다")
        void toEntity_NewDomain() {
            // given
            ImageUploadOutbox domain = ImageUploadFixtures.newPendingOutbox();

            // when
            ImageUploadOutboxJpaEntity entity = sut.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getSourceId()).isEqualTo(domain.sourceId());
            assertThat(entity.getOriginUrl()).isEqualTo(domain.originUrlValue());
        }
    }

    @Nested
    @DisplayName("toDomain() - Entity -> Domain 변환")
    class ToDomainTest {

        @Test
        @DisplayName("PENDING 상태 Entity를 Domain으로 변환한다")
        void toDomain_Pending() {
            // given
            ImageUploadOutboxJpaEntity entity = ImageUploadOutboxJpaEntityFixtures.pendingEntity();

            // when
            ImageUploadOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sourceId()).isEqualTo(entity.getSourceId());
            assertThat(domain.sourceType()).isEqualTo(entity.getSourceType());
            assertThat(domain.originUrlValue()).isEqualTo(entity.getOriginUrl());
            assertThat(domain.status()).isEqualTo(ImageUploadOutboxStatus.PENDING);
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
            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.processingEntity();

            // when
            ImageUploadOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ImageUploadOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Completed() {
            // given
            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.completedEntity();

            // when
            ImageUploadOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ImageUploadOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환한다")
        void toDomain_Failed() {
            // given
            ImageUploadOutboxJpaEntity entity = ImageUploadOutboxJpaEntityFixtures.failedEntity();

            // when
            ImageUploadOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ImageUploadOutboxStatus.FAILED);
            assertThat(domain.processedAt()).isNotNull();
            assertThat(domain.errorMessage()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환한다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntity();

            // when
            ImageUploadOutbox domain = sut.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.sourceId()).isEqualTo(entity.getSourceId());
        }
    }

    @Nested
    @DisplayName("양방향 변환 일관성 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 값이 일치한다")
        void domainToEntityToDomain_ShouldPreserveValues() {
            // given
            ImageUploadOutbox original = ImageUploadFixtures.pendingOutbox();

            // when
            ImageUploadOutboxJpaEntity entity = sut.toEntity(original);
            ImageUploadOutbox converted = sut.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sourceId()).isEqualTo(original.sourceId());
            assertThat(converted.sourceType()).isEqualTo(original.sourceType());
            assertThat(converted.originUrlValue()).isEqualTo(original.originUrlValue());
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
            ImageUploadOutboxJpaEntity original =
                    ImageUploadOutboxJpaEntityFixtures.pendingEntity();

            // when
            ImageUploadOutbox domain = sut.toDomain(original);
            ImageUploadOutboxJpaEntity converted = sut.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSourceId()).isEqualTo(original.getSourceId());
            assertThat(converted.getSourceType()).isEqualTo(original.getSourceType());
            assertThat(converted.getOriginUrl()).isEqualTo(original.getOriginUrl());
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
