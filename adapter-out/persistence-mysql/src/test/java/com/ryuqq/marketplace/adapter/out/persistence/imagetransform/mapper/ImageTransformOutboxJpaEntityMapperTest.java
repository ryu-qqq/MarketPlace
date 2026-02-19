package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.ImageTransformOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagetransform.vo.ImageTransformOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ImageTransformOutboxJpaEntityMapperTest - 이미지 변환 Outbox Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ImageTransformOutboxJpaEntityMapper 단위 테스트")
class ImageTransformOutboxJpaEntityMapperTest {

    private ImageTransformOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ImageTransformOutboxJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithPendingDomain_ConvertsCorrectly() {
            // given
            ImageTransformOutbox domain = ImageTransformFixtures.pendingOutbox();

            // when
            ImageTransformOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSourceImageId()).isEqualTo(domain.sourceImageId());
            assertThat(entity.getSourceType()).isEqualTo(domain.sourceType());
            assertThat(entity.getUploadedUrl()).isEqualTo(domain.uploadedUrlValue());
            assertThat(entity.getVariantType()).isEqualTo(domain.variantType());
            assertThat(entity.getStatus()).isEqualTo(ImageTransformOutboxStatus.PENDING);
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
        }

        @Test
        @DisplayName("PROCESSING 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithProcessingDomain_ConvertsCorrectly() {
            // given
            ImageTransformOutbox domain = ImageTransformFixtures.processingOutbox();

            // when
            ImageTransformOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ImageTransformOutboxStatus.PROCESSING);
            assertThat(entity.getTransformRequestId()).isEqualTo(domain.transformRequestId());
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedDomain_ConvertsCorrectly() {
            // given
            ImageTransformOutbox domain = ImageTransformFixtures.completedOutbox();

            // when
            ImageTransformOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ImageTransformOutboxStatus.COMPLETED);
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedDomain_ConvertsCorrectly() {
            // given
            ImageTransformOutbox domain = ImageTransformFixtures.failedOutbox();

            // when
            ImageTransformOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ImageTransformOutboxStatus.FAILED);
            assertThat(entity.getErrorMessage()).isNotNull();
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
        }

        @Test
        @DisplayName("신규 Domain (ID null)을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ConvertsCorrectly() {
            // given
            ImageTransformOutbox domain = ImageTransformFixtures.newPendingOutbox();

            // when
            ImageTransformOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getStatus()).isEqualTo(ImageTransformOutboxStatus.PENDING);
            assertThat(entity.getRetryCount()).isZero();
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
            ImageTransformOutboxJpaEntity entity =
                    ImageTransformOutboxJpaEntityFixtures.pendingEntity(1L);

            // when
            ImageTransformOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sourceImageId()).isEqualTo(entity.getSourceImageId());
            assertThat(domain.sourceType()).isEqualTo(entity.getSourceType());
            assertThat(domain.uploadedUrlValue()).isEqualTo(entity.getUploadedUrl());
            assertThat(domain.variantType()).isEqualTo(entity.getVariantType());
            assertThat(domain.status()).isEqualTo(ImageTransformOutboxStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithProcessingEntity_ConvertsCorrectly() {
            // given
            ImageTransformOutboxJpaEntity entity =
                    ImageTransformOutboxJpaEntityFixtures.newProcessingEntity();

            // when
            ImageTransformOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ImageTransformOutboxStatus.PROCESSING);
            assertThat(domain.transformRequestId()).isEqualTo(entity.getTransformRequestId());
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            ImageTransformOutboxJpaEntity entity =
                    ImageTransformOutboxJpaEntityFixtures.newCompletedEntity();

            // when
            ImageTransformOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ImageTransformOutboxStatus.COMPLETED);
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            ImageTransformOutboxJpaEntity entity =
                    ImageTransformOutboxJpaEntityFixtures.newFailedEntity();

            // when
            ImageTransformOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(ImageTransformOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isNotNull();
            assertThat(domain.isFailed()).isTrue();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 새 ID로 처리합니다")
        void toDomain_WithNullIdEntity_CreatesNewId() {
            // given
            ImageTransformOutboxJpaEntity entity =
                    ImageTransformOutboxJpaEntityFixtures.newPendingEntity();

            // when
            ImageTransformOutbox domain = mapper.toDomain(entity);

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
            ImageTransformOutbox original = ImageTransformFixtures.pendingOutbox();

            // when
            ImageTransformOutboxJpaEntity entity = mapper.toEntity(original);
            ImageTransformOutbox converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sourceImageId()).isEqualTo(original.sourceImageId());
            assertThat(converted.sourceType()).isEqualTo(original.sourceType());
            assertThat(converted.uploadedUrlValue()).isEqualTo(original.uploadedUrlValue());
            assertThat(converted.variantType()).isEqualTo(original.variantType());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.retryCount()).isEqualTo(original.retryCount());
            assertThat(converted.maxRetry()).isEqualTo(original.maxRetry());
            assertThat(converted.idempotencyKeyValue()).isEqualTo(original.idempotencyKeyValue());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ImageTransformOutboxJpaEntity original =
                    ImageTransformOutboxJpaEntityFixtures.pendingEntity(1L);

            // when
            ImageTransformOutbox domain = mapper.toDomain(original);
            ImageTransformOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSourceImageId()).isEqualTo(original.getSourceImageId());
            assertThat(converted.getSourceType()).isEqualTo(original.getSourceType());
            assertThat(converted.getUploadedUrl()).isEqualTo(original.getUploadedUrl());
            assertThat(converted.getVariantType()).isEqualTo(original.getVariantType());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
        }
    }
}
