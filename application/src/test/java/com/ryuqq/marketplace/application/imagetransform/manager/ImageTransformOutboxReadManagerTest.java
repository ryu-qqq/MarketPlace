package com.ryuqq.marketplace.application.imagetransform.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.port.out.query.ImageTransformOutboxQueryPort;
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

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageTransformOutboxReadManager 단위 테스트")
class ImageTransformOutboxReadManagerTest {

    @InjectMocks private ImageTransformOutboxReadManager sut;

    @Mock private ImageTransformOutboxQueryPort queryPort;

    @Nested
    @DisplayName("findPendingOutboxes() - PENDING 상태 Outbox 조회")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("PENDING 상태 Outbox 목록을 반환한다")
        void findPendingOutboxes_ValidParams_ReturnsPendingOutboxes() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            List<ImageTransformOutbox> expected =
                    List.of(
                            ImageTransformFixtures.pendingOutbox(1L),
                            ImageTransformFixtures.pendingOutbox(2L));

            given(queryPort.findPendingOutboxes(beforeTime, limit)).willReturn(expected);

            // when
            List<ImageTransformOutbox> result = sut.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findPendingOutboxes(beforeTime, limit);
        }

        @Test
        @DisplayName("PENDING 상태 Outbox가 없으면 빈 목록을 반환한다")
        void findPendingOutboxes_NoPendingOutboxes_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            given(queryPort.findPendingOutboxes(beforeTime, limit)).willReturn(List.of());

            // when
            List<ImageTransformOutbox> result = sut.findPendingOutboxes(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findPendingOutboxes(beforeTime, limit);
        }
    }

    @Nested
    @DisplayName("findProcessingOutboxes() - PROCESSING 상태 Outbox 조회")
    class FindProcessingOutboxesTest {

        @Test
        @DisplayName("PROCESSING 상태 Outbox 목록을 반환한다")
        void findProcessingOutboxes_ValidParams_ReturnsProcessingOutboxes() {
            // given
            int limit = 5;
            List<ImageTransformOutbox> expected =
                    List.of(ImageTransformFixtures.processingOutbox());

            given(queryPort.findProcessingOutboxes(limit)).willReturn(expected);

            // when
            List<ImageTransformOutbox> result = sut.findProcessingOutboxes(limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findProcessingOutboxes(limit);
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - 타임아웃 Outbox 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃된 PROCESSING Outbox 목록을 반환한다")
        void findProcessingTimeoutOutboxes_ValidParams_ReturnsTimeoutOutboxes() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;
            List<ImageTransformOutbox> expected =
                    List.of(ImageTransformFixtures.processingOutbox());

            given(queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(expected);

            // when
            List<ImageTransformOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }
    }

    @Nested
    @DisplayName("findActiveVariantTypesBySourceImageIds() - 활성 Variant 타입 조회")
    class FindActiveVariantTypesBySourceImageIdsTest {

        @Test
        @DisplayName("소스 이미지 ID 목록과 Variant 타입 목록으로 활성 타입 Map을 반환한다")
        void findActiveVariantTypesBySourceImageIds_ValidParams_ReturnsActiveTypesMap() {
            // given
            List<Long> sourceImageIds = List.of(1L, 2L);
            List<ImageVariantType> variantTypes =
                    List.of(ImageVariantType.SMALL_WEBP, ImageVariantType.MEDIUM_WEBP);
            Map<Long, Set<ImageVariantType>> expected =
                    Map.of(
                            1L, Set.of(ImageVariantType.SMALL_WEBP),
                            2L, Set.of(ImageVariantType.MEDIUM_WEBP));

            given(queryPort.findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes))
                    .willReturn(expected);

            // when
            Map<Long, Set<ImageVariantType>> result =
                    sut.findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L)).containsExactly(ImageVariantType.SMALL_WEBP);
            then(queryPort)
                    .should()
                    .findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes);
        }

        @Test
        @DisplayName("활성 Variant 타입이 없으면 빈 Map을 반환한다")
        void findActiveVariantTypesBySourceImageIds_NoActiveTypes_ReturnsEmptyMap() {
            // given
            List<Long> sourceImageIds = List.of(99L);
            List<ImageVariantType> variantTypes = List.of(ImageVariantType.SMALL_WEBP);

            given(queryPort.findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes))
                    .willReturn(Map.of());

            // when
            Map<Long, Set<ImageVariantType>> result =
                    sut.findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes);

            // then
            assertThat(result).isEmpty();
            then(queryPort)
                    .should()
                    .findActiveVariantTypesBySourceImageIds(sourceImageIds, variantTypes);
        }
    }
}
