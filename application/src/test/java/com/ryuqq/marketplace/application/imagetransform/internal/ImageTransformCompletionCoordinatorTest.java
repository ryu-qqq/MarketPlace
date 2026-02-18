package com.ryuqq.marketplace.application.imagetransform.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.ImageTransformResponseFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.manager.ImageTransformOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantCommandManager;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.time.Instant;
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

    @Nested
    @DisplayName("complete() - 이미지 변환 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("변환 완료 시 ImageVariant를 생성하고 Outbox 상태를 COMPLETED로 변경한다")
        void complete_SuccessfulTransform_PersistsVariantAndUpdatesOutbox() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            ImageTransformResponse response = ImageTransformResponseFixtures.completedResponse();
            Instant now = Instant.now();

            given(variantCommandManager.persist(any(ImageVariant.class))).willReturn(1L);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, response, now);

            // then
            then(variantCommandManager).should().persist(any(ImageVariant.class));
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("완료 처리 후 Outbox는 COMPLETED 상태가 된다")
        void complete_AfterCompletion_OutboxIsCompleted() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            ImageTransformResponse response = ImageTransformResponseFixtures.completedResponse();
            Instant now = Instant.now();

            given(variantCommandManager.persist(any(ImageVariant.class))).willReturn(1L);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, response, now);

            // then
            then(outboxCommandManager).should().persist(outbox);
            org.assertj.core.api.Assertions.assertThat(outbox.isCompleted()).isTrue();
        }
    }
}
