package com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.ProductGroupInspectionOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupinspection.entity.ProductGroupInspectionOutboxJpaEntity;
import com.ryuqq.marketplace.domain.productgroupinspection.ProductGroupInspectionFixtures;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
import com.ryuqq.marketplace.domain.productgroupinspection.vo.InspectionOutboxStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductGroupInspectionOutboxJpaEntityMapperTest - 상품 그룹 검수 Outbox Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("ProductGroupInspectionOutboxJpaEntityMapper 단위 테스트")
class ProductGroupInspectionOutboxJpaEntityMapperTest {

    private ProductGroupInspectionOutboxJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupInspectionOutboxJpaEntityMapper();
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
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.pendingOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupId());
            assertThat(entity.getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.PENDING);
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
            assertThat(entity.getMaxRetry()).isEqualTo(domain.maxRetry());
            assertThat(entity.getIdempotencyKey()).isEqualTo(domain.idempotencyKeyValue());
            assertThat(entity.getInspectionResultJson()).isNull();
            assertThat(entity.getTotalScore()).isNull();
            assertThat(entity.getPassed()).isNull();
        }

        @Test
        @DisplayName("SENT 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithSentDomain_ConvertsCorrectly() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.processingOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.SENT);
        }

        @Test
        @DisplayName("COMPLETED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithCompletedDomain_ConvertsCorrectly() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.completedOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.COMPLETED);
            assertThat(entity.getInspectionResultJson()).isNotNull();
            assertThat(entity.getTotalScore()).isNotNull();
            assertThat(entity.getPassed()).isNotNull();
            assertThat(entity.getProcessedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithFailedDomain_ConvertsCorrectly() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.failedOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.FAILED);
            assertThat(entity.getErrorMessage()).isNotNull();
            assertThat(entity.getRetryCount()).isEqualTo(domain.retryCount());
        }

        @Test
        @DisplayName("신규 Domain (ID null)을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ConvertsCorrectly() {
            // given
            ProductGroupInspectionOutbox domain = ProductGroupInspectionFixtures.newPendingOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getStatus())
                    .isEqualTo(ProductGroupInspectionOutboxJpaEntity.Status.PENDING);
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
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);

            // when
            ProductGroupInspectionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupId()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.status()).isEqualTo(InspectionOutboxStatus.PENDING);
            assertThat(domain.retryCount()).isEqualTo(entity.getRetryCount());
            assertThat(domain.maxRetry()).isEqualTo(entity.getMaxRetry());
            assertThat(domain.idempotencyKeyValue()).isEqualTo(entity.getIdempotencyKey());
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity를 Domain으로 변환하면 PENDING으로 매핑됩니다")
        void toDomain_WithProcessingEntity_MapsToPending() {
            // given
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newProcessingEntity();

            // when
            ProductGroupInspectionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(InspectionOutboxStatus.PENDING);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithCompletedEntity_ConvertsCorrectly() {
            // given
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newCompletedEntity();

            // when
            ProductGroupInspectionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(InspectionOutboxStatus.COMPLETED);
            assertThat(domain.inspectionResultJson()).isNotNull();
            assertThat(domain.totalScore()).isNotNull();
            assertThat(domain.passed()).isNotNull();
            assertThat(domain.processedAt()).isNotNull();
        }

        @Test
        @DisplayName("FAILED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithFailedEntity_ConvertsCorrectly() {
            // given
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newFailedEntity();

            // when
            ProductGroupInspectionOutbox domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(InspectionOutboxStatus.FAILED);
            assertThat(domain.errorMessage()).isNotNull();
            assertThat(domain.isFailed()).isTrue();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 새 ID로 처리합니다")
        void toDomain_WithNullIdEntity_CreatesNewId() {
            // given
            ProductGroupInspectionOutboxJpaEntity entity =
                    ProductGroupInspectionOutboxJpaEntityFixtures.newPendingEntity();

            // when
            ProductGroupInspectionOutbox domain = mapper.toDomain(entity);

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
            ProductGroupInspectionOutbox original = ProductGroupInspectionFixtures.pendingOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(original);
            ProductGroupInspectionOutbox converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.productGroupId()).isEqualTo(original.productGroupId());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.retryCount()).isEqualTo(original.retryCount());
            assertThat(converted.maxRetry()).isEqualTo(original.maxRetry());
            assertThat(converted.idempotencyKeyValue()).isEqualTo(original.idempotencyKeyValue());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ProductGroupInspectionOutboxJpaEntity original =
                    ProductGroupInspectionOutboxJpaEntityFixtures.pendingEntity(1L);

            // when
            ProductGroupInspectionOutbox domain = mapper.toDomain(original);
            ProductGroupInspectionOutboxJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getProductGroupId()).isEqualTo(original.getProductGroupId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getRetryCount()).isEqualTo(original.getRetryCount());
            assertThat(converted.getMaxRetry()).isEqualTo(original.getMaxRetry());
            assertThat(converted.getIdempotencyKey()).isEqualTo(original.getIdempotencyKey());
        }

        @Test
        @DisplayName("COMPLETED Domain 양방향 변환 시 결과 데이터가 보존됩니다")
        void roundTrip_CompletedDomain_PreservesResultData() {
            // given
            ProductGroupInspectionOutbox original =
                    ProductGroupInspectionFixtures.completedOutbox();

            // when
            ProductGroupInspectionOutboxJpaEntity entity = mapper.toEntity(original);
            ProductGroupInspectionOutbox converted = mapper.toDomain(entity);

            // then
            assertThat(converted.inspectionResultJson()).isEqualTo(original.inspectionResultJson());
            assertThat(converted.totalScore()).isEqualTo(original.totalScore());
            assertThat(converted.passed()).isEqualTo(original.passed());
        }
    }
}
