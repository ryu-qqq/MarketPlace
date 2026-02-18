package com.ryuqq.marketplace.adapter.out.persistence.imageupload.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.imageupload.ImageUploadOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.mapper.ImageUploadOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.repository.ImageUploadOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ImageUploadOutboxQueryAdapterTest - 이미지 업로드 Outbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageUploadOutboxQueryAdapter 단위 테스트")
class ImageUploadOutboxQueryAdapterTest {

    @Mock private ImageUploadOutboxQueryDslRepository queryDslRepository;

    @Mock private ImageUploadOutboxJpaEntityMapper mapper;

    @InjectMocks private ImageUploadOutboxQueryAdapter queryAdapter;

    @Nested
    @DisplayName("findPendingOutboxesForRetry 메서드 테스트")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("재시도 대상 PENDING Outbox 목록을 조회하여 Domain 리스트로 변환합니다")
        void findPendingOutboxesForRetry_WithValidParams_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);
            int limit = 10;

            ImageUploadOutboxJpaEntity entity1 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(100L);
            ImageUploadOutboxJpaEntity entity2 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(101L);
            List<ImageUploadOutboxJpaEntity> entities = List.of(entity1, entity2);

            ImageUploadOutbox domain1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox domain2 = ImageUploadFixtures.pendingOutbox(2L);

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(queryDslRepository).should().findPendingOutboxesForRetry(beforeTime, limit);
        }

        @Test
        @DisplayName("재시도 대상이 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxesForRetry_WithNoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(List.of());

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findPendingOutboxesForRetry(beforeTime, limit);
        }

        @Test
        @DisplayName("Mapper가 각 Entity에 대해 호출됩니다")
        void findPendingOutboxesForRetry_CallsMapperForEachEntity() {
            // given
            Instant beforeTime = Instant.now().minusSeconds(300);
            int limit = 10;

            ImageUploadOutboxJpaEntity entity1 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(200L);
            ImageUploadOutboxJpaEntity entity2 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(201L);
            ImageUploadOutboxJpaEntity entity3 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(202L);
            List<ImageUploadOutboxJpaEntity> entities = List.of(entity1, entity2, entity3);

            ImageUploadOutbox domain = ImageUploadFixtures.pendingOutbox();
            given(queryDslRepository.findPendingOutboxesForRetry(beforeTime, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain);
            given(mapper.toDomain(entity2)).willReturn(domain);
            given(mapper.toDomain(entity3)).willReturn(domain);

            // when
            queryAdapter.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            then(mapper)
                    .should(times(3))
                    .toDomain(ArgumentMatchers.any(ImageUploadOutboxJpaEntity.class));
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 조회하여 Domain 리스트로 변환합니다")
        void findProcessingTimeoutOutboxes_WithValidParams_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(600);
            int limit = 10;

            ImageUploadOutboxJpaEntity entity1 =
                    ImageUploadOutboxJpaEntityFixtures.processingEntity();
            ImageUploadOutboxJpaEntity entity2 =
                    ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(700L);
            List<ImageUploadOutboxJpaEntity> entities = List.of(entity1, entity2);

            ImageUploadOutbox domain1 = ImageUploadFixtures.processingOutbox();
            ImageUploadOutbox domain2 = ImageUploadFixtures.processingOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃된 PROCESSING이 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(600);
            int limit = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("Mapper가 각 Entity에 대해 호출됩니다")
        void findProcessingTimeoutOutboxes_CallsMapperForEachEntity() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(600);
            int limit = 10;

            ImageUploadOutboxJpaEntity entity1 =
                    ImageUploadOutboxJpaEntityFixtures.processingEntity();
            ImageUploadOutboxJpaEntity entity2 =
                    ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(700L);
            ImageUploadOutboxJpaEntity entity3 =
                    ImageUploadOutboxJpaEntityFixtures.processingTimeoutEntity(800L);
            List<ImageUploadOutboxJpaEntity> entities = List.of(entity1, entity2, entity3);

            ImageUploadOutbox domain = ImageUploadFixtures.processingOutbox();
            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain);
            given(mapper.toDomain(entity2)).willReturn(domain);
            given(mapper.toDomain(entity3)).willReturn(domain);

            // when
            queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            then(mapper)
                    .should(times(3))
                    .toDomain(ArgumentMatchers.any(ImageUploadOutboxJpaEntity.class));
        }
    }

    @Nested
    @DisplayName("findBySourceIdsAndSourceType 메서드 테스트")
    class FindBySourceIdsAndSourceTypeTest {

        @Test
        @DisplayName("sourceId 목록과 sourceType으로 Outbox 목록을 조회하여 Domain 리스트로 변환합니다")
        void findBySourceIdsAndSourceType_WithValidParams_ReturnsDomainList() {
            // given
            List<Long> sourceIds = List.of(100L, 101L);
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            ImageUploadOutboxJpaEntity entity1 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(100L);
            ImageUploadOutboxJpaEntity entity2 =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceId(101L);
            List<ImageUploadOutboxJpaEntity> entities = List.of(entity1, entity2);

            ImageUploadOutbox domain1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox domain2 = ImageUploadFixtures.pendingOutbox(2L);

            given(queryDslRepository.findBySourceIdsAndSourceType(sourceIds, sourceType))
                    .willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).contains(domain1, domain2);
            then(queryDslRepository).should().findBySourceIdsAndSourceType(sourceIds, sourceType);
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findBySourceIdsAndSourceType_WithNoResults_ReturnsEmptyList() {
            // given
            List<Long> sourceIds = List.of(999L);
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            given(queryDslRepository.findBySourceIdsAndSourceType(sourceIds, sourceType))
                    .willReturn(List.of());

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findBySourceIdsAndSourceType(sourceIds, sourceType);
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 타입으로 조회합니다")
        void findBySourceIdsAndSourceType_WithDescriptionImageType_ReturnsDomainList() {
            // given
            List<Long> sourceIds = List.of(200L);
            ImageSourceType sourceType = ImageSourceType.DESCRIPTION_IMAGE;

            ImageUploadOutboxJpaEntity entity =
                    ImageUploadOutboxJpaEntityFixtures.newPendingEntityWithSourceType(
                            ImageSourceType.DESCRIPTION_IMAGE);
            ImageUploadOutbox domain = ImageUploadFixtures.newPendingOutbox(sourceType);

            given(queryDslRepository.findBySourceIdsAndSourceType(sourceIds, sourceType))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ImageUploadOutbox> result =
                    queryAdapter.findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository).should().findBySourceIdsAndSourceType(sourceIds, sourceType);
        }
    }
}
