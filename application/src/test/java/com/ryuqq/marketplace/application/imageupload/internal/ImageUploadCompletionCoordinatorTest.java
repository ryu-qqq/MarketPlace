package com.ryuqq.marketplace.application.imageupload.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imageupload.manager.ImageUploadOutboxCommandManager;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
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
@DisplayName("ImageUploadCompletionCoordinator 단위 테스트")
class ImageUploadCompletionCoordinatorTest {

    @InjectMocks private ImageUploadCompletionCoordinator sut;

    @Mock private ImageUploadCompletionStrategyProvider strategyProvider;
    @Mock private ImageUploadOutboxCommandManager outboxCommandManager;
    @Mock private ImageUploadCompletionStrategy completionStrategy;

    @Nested
    @DisplayName("complete() - 이미지 업로드 완료 처리")
    class CompleteTest {

        @Test
        @DisplayName("sourceType에 맞는 전략을 호출하고 Outbox를 COMPLETED 상태로 저장한다")
        void complete_ValidOutbox_CallsStrategyAndPersistsOutbox() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            String newCdnUrl = "https://cdn.example.com/new-image.jpg";
            String fileAssetId = "asset-id-abc";
            Instant now = Instant.now();

            given(strategyProvider.getStrategy(outbox.sourceType())).willReturn(completionStrategy);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, newCdnUrl, fileAssetId, now);

            // then
            then(strategyProvider).should().getStrategy(outbox.sourceType());
            then(completionStrategy)
                    .should()
                    .complete(eq(outbox.sourceId()), any(ImageUrl.class), eq(fileAssetId));
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("완료 처리 후 Outbox는 COMPLETED 상태가 된다")
        void complete_AfterCompletion_OutboxIsCompleted() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            String newCdnUrl = "https://cdn.example.com/new-image.jpg";
            String fileAssetId = "asset-id-abc";
            Instant now = Instant.now();

            given(strategyProvider.getStrategy(outbox.sourceType())).willReturn(completionStrategy);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, newCdnUrl, fileAssetId, now);

            // then
            assertThat(outbox.isCompleted()).isTrue();
        }

        @Test
        @DisplayName("PRODUCT_GROUP_IMAGE 타입 Outbox 완료 처리 시 해당 전략이 호출된다")
        void complete_ProductGroupImageOutbox_CallsCorrectStrategy() {
            // given
            ImageUploadOutbox outbox =
                    ImageUploadFixtures.newPendingOutbox(ImageSourceType.PRODUCT_GROUP_IMAGE);
            String newCdnUrl = "https://cdn.example.com/product-image.jpg";
            String fileAssetId = "pg-asset-id";
            Instant now = Instant.now();

            given(strategyProvider.getStrategy(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(completionStrategy);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, newCdnUrl, fileAssetId, now);

            // then
            then(strategyProvider).should().getStrategy(ImageSourceType.PRODUCT_GROUP_IMAGE);
            then(completionStrategy)
                    .should()
                    .complete(eq(outbox.sourceId()), any(ImageUrl.class), eq(fileAssetId));
        }

        @Test
        @DisplayName("fileAssetId가 null이어도 완료 처리가 정상적으로 수행된다")
        void complete_NullFileAssetId_CompletesSuccessfully() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            String newCdnUrl = "https://cdn.example.com/new-image.jpg";
            String fileAssetId = null;
            Instant now = Instant.now();

            given(strategyProvider.getStrategy(outbox.sourceType())).willReturn(completionStrategy);
            given(outboxCommandManager.persist(outbox)).willReturn(1L);

            // when
            sut.complete(outbox, newCdnUrl, fileAssetId, now);

            // then
            then(completionStrategy)
                    .should()
                    .complete(eq(outbox.sourceId()), any(ImageUrl.class), eq(null));
            then(outboxCommandManager).should().persist(outbox);
        }
    }
}
