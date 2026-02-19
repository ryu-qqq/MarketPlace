package com.ryuqq.marketplace.adapter.out.persistence.imagetransform.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.querydsl.core.Tuple;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.ImageTransformOutboxJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.ImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.entity.QImageTransformOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.mapper.ImageTransformOutboxJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.imagetransform.repository.ImageTransformOutboxQueryDslRepository;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ImageTransformOutboxQueryAdapterTest - 이미지 변환 Outbox Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageTransformOutboxQueryAdapter 단위 테스트")
class ImageTransformOutboxQueryAdapterTest {

    @Mock private ImageTransformOutboxQueryDslRepository queryDslRepository;

    @Mock private ImageTransformOutboxJpaEntityMapper mapper;

    @InjectMocks private ImageTransformOutboxQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findPendingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findPendingOutboxes 메서드 테스트")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태 Outbox 목록을 Domain으로 변환하여 반환합니다")
        void findPendingOutboxes_WithResults_ReturnsDomainList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            ImageTransformOutboxJpaEntity entity1 =
                    ImageTransformOutboxJpaEntityFixtures.pendingEntity(1L);
            ImageTransformOutboxJpaEntity entity2 =
                    ImageTransformOutboxJpaEntityFixtures.pendingEntity(2L);
            ImageTransformOutbox domain1 = ImageTransformFixtures.pendingOutbox(1L);
            ImageTransformOutbox domain2 = ImageTransformFixtures.pendingOutbox(2L);

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<ImageTransformOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findPendingOutboxes(beforeTime, limit);
        }

        @Test
        @DisplayName("PENDING Outbox가 없으면 빈 리스트를 반환합니다")
        void findPendingOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            given(queryDslRepository.findPendingOutboxes(beforeTime, limit)).willReturn(List.of());

            // when
            List<ImageTransformOutbox> result = queryAdapter.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findProcessingOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingOutboxes 메서드 테스트")
    class FindProcessingOutboxesTest {

        @Test
        @DisplayName("PROCESSING 상태 Outbox 목록을 Domain으로 변환하여 반환합니다")
        void findProcessingOutboxes_WithResults_ReturnsDomainList() {
            // given
            int limit = 5;

            ImageTransformOutboxJpaEntity entity1 =
                    ImageTransformOutboxJpaEntityFixtures.newProcessingEntity();
            ImageTransformOutbox domain1 = ImageTransformFixtures.processingOutbox();

            given(queryDslRepository.findProcessingOutboxes(limit)).willReturn(List.of(entity1));
            given(mapper.toDomain(entity1)).willReturn(domain1);

            // when
            List<ImageTransformOutbox> result = queryAdapter.findProcessingOutboxes(limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain1);
            then(queryDslRepository).should().findProcessingOutboxes(limit);
        }

        @Test
        @DisplayName("PROCESSING Outbox가 없으면 빈 리스트를 반환합니다")
        void findProcessingOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            int limit = 5;

            given(queryDslRepository.findProcessingOutboxes(limit)).willReturn(List.of());

            // when
            List<ImageTransformOutbox> result = queryAdapter.findProcessingOutboxes(limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findProcessingTimeoutOutboxes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes 메서드 테스트")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 Domain으로 변환하여 반환합니다")
        void findProcessingTimeoutOutboxes_WithResults_ReturnsDomainList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;

            ImageTransformOutboxJpaEntity entity =
                    ImageTransformOutboxJpaEntityFixtures.newProcessingEntity();
            ImageTransformOutbox domain = ImageTransformFixtures.processingOutbox();

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<ImageTransformOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(1);
            then(queryDslRepository)
                    .should()
                    .findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃 Outbox가 없으면 빈 리스트를 반환합니다")
        void findProcessingTimeoutOutboxes_WithNoResults_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryDslRepository.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<ImageTransformOutbox> result =
                    queryAdapter.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findActiveVariantTypesBySourceImageIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findActiveVariantTypesBySourceImageIds 메서드 테스트")
    class FindActiveVariantTypesBySourceImageIdsTest {

        @Test
        @DisplayName("Tuple 목록을 Map으로 변환하여 반환합니다")
        void findActiveVariantTypesBySourceImageIds_WithResults_ReturnsMap() {
            // given
            List<Long> sourceImageIds = List.of(100L, 200L);
            List<ImageVariantType> variantTypes =
                    List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP);

            Tuple tuple1 = mockTuple(100L, ImageVariantType.SMALL_WEBP);
            Tuple tuple2 = mockTuple(100L, ImageVariantType.MEDIUM_WEBP);
            Tuple tuple3 = mockTuple(200L, ImageVariantType.SMALL_WEBP);

            given(queryDslRepository.findActiveOutboxPairs(sourceImageIds, variantTypes))
                    .willReturn(List.of(tuple1, tuple2, tuple3));

            // when
            Map<Long, Set<ImageVariantType>> result =
                    queryAdapter.findActiveVariantTypesBySourceImageIds(
                            sourceImageIds, variantTypes);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(100L))
                    .containsExactlyInAnyOrder(
                            ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP);
            assertThat(result.get(200L)).containsExactly(ImageVariantType.SMALL_WEBP);
        }

        @Test
        @DisplayName("활성 Outbox가 없으면 빈 Map을 반환합니다")
        void findActiveVariantTypesBySourceImageIds_WithNoResults_ReturnsEmptyMap() {
            // given
            List<Long> sourceImageIds = List.of(100L);
            List<ImageVariantType> variantTypes = List.of(ImageVariantType.SMALL_WEBP);

            given(queryDslRepository.findActiveOutboxPairs(sourceImageIds, variantTypes))
                    .willReturn(List.of());

            // when
            Map<Long, Set<ImageVariantType>> result =
                    queryAdapter.findActiveVariantTypesBySourceImageIds(
                            sourceImageIds, variantTypes);

            // then
            assertThat(result).isEmpty();
        }

        private Tuple mockTuple(Long sourceImageId, ImageVariantType variantType) {
            Tuple tuple = org.mockito.Mockito.mock(Tuple.class);
            given(
                            tuple.get(
                                    QImageTransformOutboxJpaEntity.imageTransformOutboxJpaEntity
                                            .sourceImageId))
                    .willReturn(sourceImageId);
            given(
                            tuple.get(
                                    QImageTransformOutboxJpaEntity.imageTransformOutboxJpaEntity
                                            .variantType))
                    .willReturn(variantType);
            return tuple;
        }
    }
}
