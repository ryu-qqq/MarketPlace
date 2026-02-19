package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.ImageTransformOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * ImageTransformOutboxQueryDslRepositoryTest - 이미지 변환 Outbox QueryDslRepository 통합 테스트.
 *
 * <p>상태 기반 필터 (PENDING, PROCESSING) 적용을 검증합니다.
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("ImageTransformOutboxQueryDslRepository 통합 테스트")
class ImageTransformOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ImageTransformOutboxQueryDslRepository repository() {
        return new ImageTransformOutboxQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private ImageTransformOutboxJpaEntity persist(ImageTransformOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태이고 createdAt이 beforeTime 이전인 Entity를 반환합니다")
        void findPendingOutboxes_WithPendingBeforeTime_ReturnsEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus().isPending()).isTrue();
        }

        @Test
        @DisplayName("PROCESSING 상태 Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithProcessingEntity_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithCompletedEntity_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ImageTransformOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("최대 재시도 횟수에 도달한 PENDING Entity는 조회되지 않습니다")
        void findPendingOutboxes_WithMaxRetryReached_ExcludesIt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            // retryCount == maxRetry (3) 이므로 retryCount.lt(maxRetry) 조건 불만족
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntityWithRetry(3));

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit 제한이 적용됩니다")
        void findPendingOutboxes_WithLimit_RespectsLimit() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(60);
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findPendingOutboxes(beforeTime, 2);

            // then
            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 2. findProcessingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingOutboxes")
    class FindProcessingOutboxesTest {

        @Test
        @DisplayName("PROCESSING 상태 Entity를 반환합니다")
        void findProcessingOutboxes_WithProcessingEntity_ReturnsEntity() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result = repository().findProcessingOutboxes(10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus().isProcessing()).isTrue();
        }

        @Test
        @DisplayName("PENDING 상태 Entity는 조회되지 않습니다")
        void findProcessingOutboxes_WithPendingEntity_ExcludesIt() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result = repository().findProcessingOutboxes(10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity는 조회되지 않습니다")
        void findProcessingOutboxes_WithCompletedEntity_ExcludesIt() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result = repository().findProcessingOutboxes(10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit 제한이 적용됩니다")
        void findProcessingOutboxes_WithLimit_RespectsLimit() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<ImageTransformOutboxJpaEntity> result = repository().findProcessingOutboxes(2);

            // then
            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 3. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("updatedAt이 timeoutThreshold 이전인 PROCESSING Entity를 반환합니다")
        void findProcessingTimeoutOutboxes_WithTimedOutEntity_ReturnsEntity() {
            // given
            Instant oldTime = Instant.now().minusSeconds(600);
            persist(
                    ImageTransformOutboxJpaEntityFixtures.newProcessingEntityWithOldUpdatedAt(
                            oldTime));
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus().isProcessing()).isTrue();
        }

        @Test
        @DisplayName("updatedAt이 timeoutThreshold 이후인 PROCESSING Entity는 조회되지 않습니다")
        void findProcessingTimeoutOutboxes_WithRecentEntity_ExcludesIt() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태 Entity는 조회되지 않습니다")
        void findProcessingTimeoutOutboxes_WithPendingEntity_ExcludesIt() {
            // given
            Instant oldTime = Instant.now().minusSeconds(600);
            // PENDING 상태이므로 PROCESSING 조건 불만족 → 조회 안됨
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());
            Instant timeoutThreshold = Instant.now().minusSeconds(300);

            // when
            List<ImageTransformOutboxJpaEntity> result =
                    repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findActiveOutboxPairs 테스트
    // ========================================================================

    @Nested
    @DisplayName("findActiveOutboxPairs")
    class FindActiveOutboxPairsTest {

        @Test
        @DisplayName("PENDING 상태의 (sourceImageId, variantType) 쌍을 반환합니다")
        void findActiveOutboxPairs_WithPendingEntity_ReturnsTuple() {
            // given
            Long sourceImageId = 100L;
            ImageVariantType variantType = ImageVariantType.SMALL_WEBP;
            persist(
                    ImageTransformOutboxJpaEntityFixtures.newPendingEntity(
                            sourceImageId, variantType));

            // when
            List<Tuple> result =
                    repository()
                            .findActiveOutboxPairs(List.of(sourceImageId), List.of(variantType));

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("PROCESSING 상태의 (sourceImageId, variantType) 쌍을 반환합니다")
        void findActiveOutboxPairs_WithProcessingEntity_ReturnsTuple() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            List<Tuple> result =
                    repository()
                            .findActiveOutboxPairs(
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_SOURCE_IMAGE_ID),
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_VARIANT_TYPE));

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("COMPLETED 상태 Entity는 조회되지 않습니다")
        void findActiveOutboxPairs_WithCompletedEntity_ExcludesIt() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            List<Tuple> result =
                    repository()
                            .findActiveOutboxPairs(
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_SOURCE_IMAGE_ID),
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_VARIANT_TYPE));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("FAILED 상태 Entity는 조회되지 않습니다")
        void findActiveOutboxPairs_WithFailedEntity_ExcludesIt() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newFailedEntity());

            // when
            List<Tuple> result =
                    repository()
                            .findActiveOutboxPairs(
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_SOURCE_IMAGE_ID),
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_VARIANT_TYPE));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("요청한 sourceImageId에 해당하지 않는 Entity는 조회되지 않습니다")
        void findActiveOutboxPairs_WithDifferentSourceImageId_ExcludesIt() {
            // given
            persist(ImageTransformOutboxJpaEntityFixtures.newPendingEntity());

            // when
            List<Tuple> result =
                    repository()
                            .findActiveOutboxPairs(
                                    List.of(999L),
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_VARIANT_TYPE));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("요청한 variantType에 해당하지 않는 Entity는 조회되지 않습니다")
        void findActiveOutboxPairs_WithDifferentVariantType_ExcludesIt() {
            // given
            persist(
                    ImageTransformOutboxJpaEntityFixtures.newPendingEntity(
                            ImageTransformOutboxJpaEntityFixtures.DEFAULT_SOURCE_IMAGE_ID,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<Tuple> result =
                    repository()
                            .findActiveOutboxPairs(
                                    List.of(
                                            ImageTransformOutboxJpaEntityFixtures
                                                    .DEFAULT_SOURCE_IMAGE_ID),
                                    List.of(ImageVariantType.MEDIUM_WEBP));

            // then
            assertThat(result).isEmpty();
        }
    }
}
