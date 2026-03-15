package com.ryuqq.marketplace.application.imagetransform.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.imagetransform.factory.ImageTransformCompletionBundle;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantCommandManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.time.Instant;
import java.util.Optional;
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
@DisplayName("ImageTransformCompletionCoordinator 단위 테스트")
class ImageTransformCompletionCoordinatorTest {

    @InjectMocks private ImageTransformCompletionCoordinator sut;

    @Mock private ImageVariantCommandManager variantCommandManager;
    @Mock private ImageTransformOutboxCommandManager outboxCommandManager;
    @Mock private ImageVariantSyncOutboxCommandManager syncOutboxCommandManager;

    @Nested
    @DisplayName("complete() - 이미지 변환 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("ImageVariant를 저장하고 Outbox를 COMPLETED로 변경한다")
        void shouldPersistVariantAndCompleteOutbox() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant now = Instant.now();
            ImageVariant variant = createTestVariant(now);
            ImageTransformCompletionBundle bundle =
                    ImageTransformCompletionBundle.completed(variant, Optional.empty());

            given(variantCommandManager.persist(variant)).willReturn(1L);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, bundle, now);

            // then
            then(variantCommandManager).should().persist(variant);
            then(outboxCommandManager).should().persist(outbox);
            org.assertj.core.api.Assertions.assertThat(outbox.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("sync 아웃박스가 번들에 포함되어 있으면 저장한다")
        void shouldPersistSyncOutboxWhenPresent() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant now = Instant.now();
            ImageVariant variant = createTestVariant(now);
            ImageVariantSyncOutbox syncOutbox =
                    ImageVariantSyncOutbox.forNew(1L, ImageSourceType.PRODUCT_GROUP_IMAGE, now);
            ImageTransformCompletionBundle bundle =
                    ImageTransformCompletionBundle.completed(variant, Optional.of(syncOutbox));

            given(variantCommandManager.persist(variant)).willReturn(1L);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);
            given(syncOutboxCommandManager.persist(syncOutbox)).willReturn(1L);

            // when
            sut.complete(outbox, bundle, now);

            // then
            then(syncOutboxCommandManager).should().persist(syncOutbox);
        }

        @Test
        @DisplayName("sync 아웃박스가 번들에 없으면 저장하지 않는다")
        void shouldNotPersistSyncOutboxWhenAbsent() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            Instant now = Instant.now();
            ImageVariant variant = createTestVariant(now);
            ImageTransformCompletionBundle bundle =
                    ImageTransformCompletionBundle.completed(variant, Optional.empty());

            given(variantCommandManager.persist(variant)).willReturn(1L);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, bundle, now);

            // then
            then(syncOutboxCommandManager).should(never()).persist(any());
        }

        private ImageVariant createTestVariant(Instant now) {
            return ImageVariant.forNew(
                    1L,
                    ImageSourceType.PRODUCT_GROUP_IMAGE,
                    ImageVariantType.SMALL_WEBP,
                    ResultAssetId.of("test-asset"),
                    ImageUrl.of("https://cdn.test/small.webp"),
                    ImageDimension.of(300, 300),
                    now);
        }
    }
}
