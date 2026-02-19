package com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.ImageUploadOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
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

/** ImageUploadOutboxQueryDslRepositoryTest - 이미지 업로드 Outbox QueryDslRepository 통합 테스트. */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("ImageUploadOutboxQueryDslRepository 통합 테스트")
class ImageUploadOutboxQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ImageUploadOutboxQueryDslRepository repository() {
        return new ImageUploadOutboxQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private ImageUploadOutboxJpaEntity persist(ImageUploadOutboxJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findPendingOutboxesForRetry")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("재시도 대상 PENDING Outbox 목록을 조회합니다")
        void findPendingOutboxesForRetry_WithValidConditions_ReturnsOutboxes() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(100L));
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(101L));

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == ImageUploadOutboxStatus.PENDING);
            assertThat(result).allMatch(e -> e.getRetryCount() < e.getMaxRetry());
        }

        @Test
        @DisplayName("최대 재시도 횟수에 도달한 Outbox는 제외됩니다")
        void findPendingOutboxesForRetry_WithMaxRetryReached_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(200L));
            persist(ImageUploadOutboxJpaEntityFixtures.retriedPendingEntity(3)); // max_retry = 3

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceId()).isEqualTo(200L);
        }

        @Test
        @DisplayName("PROCESSING 상태는 제외됩니다")
        void findPendingOutboxesForRetry_WithProcessingStatus_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(300L));
            persist(ImageUploadOutboxJpaEntityFixtures.newProcessingEntity());

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceId()).isEqualTo(300L);
        }

        @Test
        @DisplayName("COMPLETED 상태는 제외됩니다")
        void findPendingOutboxesForRetry_WithCompletedStatus_ExcludesEntity() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(400L));
            persist(ImageUploadOutboxJpaEntityFixtures.newCompletedEntity());

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceId()).isEqualTo(400L);
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findPendingOutboxesForRetry_WithLimit_ReturnsLimitedResults() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(500L));
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(501L));
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(502L));

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 2);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("createdAt 기준 오름차순으로 정렬됩니다")
        void findPendingOutboxesForRetry_WithMultipleEntities_ReturnsSortedByCreatedAt() {
            // given
            Instant beforeTime = Instant.now().plusSeconds(10);
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(600L));
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(601L));

            // when
            var result = repository().findPendingOutboxesForRetry(beforeTime, 10);

            // then
            assertThat(result).hasSize(2);
            for (int i = 0; i < result.size() - 1; i++) {
                assertThat(result.get(i).getCreatedAt())
                        .isBeforeOrEqualTo(result.get(i + 1).getCreatedAt());
            }
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 조회합니다")
        void findProcessingTimeoutOutboxes_WithTimeoutEntities_ReturnsOutboxes() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(300)); // 5분 전

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(ImageUploadOutboxStatus.PROCESSING);
        }

        @Test
        @DisplayName("최신 PROCESSING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithRecentProcessing_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(60);
            persist(ImageUploadOutboxJpaEntityFixtures.newProcessingEntity()); // 방금 생성

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태는 제외됩니다")
        void findProcessingTimeoutOutboxes_WithPendingStatus_ExcludesEntity() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(700L));

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit 개수만큼만 조회됩니다")
        void findProcessingTimeoutOutboxes_WithLimit_ReturnsLimitedResults() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(300));
            persist(ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(400));
            persist(ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(500));

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 2);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("updatedAt 기준 오름차순으로 정렬됩니다")
        void findProcessingTimeoutOutboxes_WithMultipleEntities_ReturnsSortedByUpdatedAt() {
            // given
            Instant timeoutThreshold = Instant.now();
            persist(ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(500)); // 더 오래된
            persist(ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(300)); // 상대적으로 최근

            // when
            var result = repository().findProcessingTimeoutOutboxes(timeoutThreshold, 10);

            // then
            assertThat(result).hasSize(2);
            for (int i = 0; i < result.size() - 1; i++) {
                assertThat(result.get(i).getUpdatedAt())
                        .isBeforeOrEqualTo(result.get(i + 1).getUpdatedAt());
            }
        }
    }

    @Nested
    @DisplayName("findBySourceIdsAndSourceType")
    class FindBySourceIdsAndSourceTypeTest {

        @Test
        @DisplayName("sourceId 목록과 sourceType으로 Outbox 목록을 조회합니다")
        void findBySourceIdsAndSourceType_WithValidConditions_ReturnsOutboxes() {
            // given
            List<Long> sourceIds = List.of(1000L, 1001L);
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            1000L, sourceType));
            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            1001L, sourceType));

            // when
            var result = repository().findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getSourceType() == sourceType);
        }

        @Test
        @DisplayName("다른 sourceType은 제외됩니다")
        void findBySourceIdsAndSourceType_WithDifferentSourceType_ExcludesEntity() {
            // given
            List<Long> sourceIds = List.of(2000L);
            ImageSourceType targetType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            2000L, ImageSourceType.DESCRIPTION_IMAGE)); // 다른 타입

            // when
            var result = repository().findBySourceIdsAndSourceType(sourceIds, targetType);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("sourceIds에 포함되지 않은 sourceId는 제외됩니다")
        void findBySourceIdsAndSourceType_WithUnrelatedSourceId_ExcludesEntity() {
            // given
            List<Long> sourceIds = List.of(3000L);
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            9999L, sourceType)); // 포함되지 않은 ID

            // when
            var result = repository().findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 sourceIds를 입력하면 빈 리스트를 반환합니다")
        void findBySourceIdsAndSourceType_WithEmptySourceIds_ReturnsEmptyList() {
            // given
            List<Long> sourceIds = List.of();
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            persist(ImageUploadOutboxJpaEntityFixtures.newPendingEntity());

            // when
            var result = repository().findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 타입으로 조회합니다")
        void findBySourceIdsAndSourceType_WithDescriptionImageType_ReturnsOutboxes() {
            // given
            List<Long> sourceIds = List.of(4000L);
            ImageSourceType sourceType = ImageSourceType.DESCRIPTION_IMAGE;

            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            4000L, sourceType));

            // when
            var result = repository().findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceType()).isEqualTo(ImageSourceType.DESCRIPTION_IMAGE);
        }

        @Test
        @DisplayName("sourceId 오름차순, createdAt 내림차순으로 정렬됩니다")
        void findBySourceIdsAndSourceType_WithMultipleEntities_ReturnsSortedResults() {
            // given
            List<Long> sourceIds = List.of(5000L, 5001L);
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            5001L, sourceType));
            persist(
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceIdAndType(
                            5000L, sourceType));

            // when
            var result = repository().findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSourceId()).isEqualTo(5000L);
            assertThat(result.get(1).getSourceId()).isEqualTo(5001L);
        }
    }
}
