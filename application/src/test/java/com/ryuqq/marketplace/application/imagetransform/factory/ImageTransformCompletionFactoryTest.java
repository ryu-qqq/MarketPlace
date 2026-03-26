package com.ryuqq.marketplace.application.imagetransform.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.imagetransform.dto.command.CompleteImageTransformCallbackCommand;
import com.ryuqq.marketplace.domain.imagetransform.ImageTransformFixtures;
import com.ryuqq.marketplace.domain.imagetransform.aggregate.ImageTransformOutbox;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageTransformCompletionFactory 단위 테스트")
class ImageTransformCompletionFactoryTest {

    private final ImageTransformCompletionFactory sut = new ImageTransformCompletionFactory();

    @Nested
    @DisplayName("create() - 콜백 커맨드 기반 번들 생성")
    class CreateTest {

        @Test
        @DisplayName("COMPLETED 상태이면 성공 번들을 생성한다")
        void shouldCreateCompletedBundle() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            CompleteImageTransformCallbackCommand command =
                    new CompleteImageTransformCallbackCommand(
                            "req-1", "COMPLETED", "asset-1", 300, 300, null);
            Instant now = Instant.now();

            // when
            ImageTransformCompletionBundle bundle =
                    sut.create(outbox, command, "https://cdn/result.webp", true, now);

            // then
            assertThat(bundle.completed()).isTrue();
            assertThat(bundle.variant()).isNotNull();
            assertThat(bundle.variant().sourceImageId()).isEqualTo(outbox.sourceImageId());
        }

        @Test
        @DisplayName("FAILED 상태이면 실패 번들을 생성한다")
        void shouldCreateFailedBundle() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            CompleteImageTransformCallbackCommand command =
                    new CompleteImageTransformCallbackCommand(
                            "req-1", "FAILED", null, null, null, "서버 타임아웃");
            Instant now = Instant.now();

            // when
            ImageTransformCompletionBundle bundle = sut.create(outbox, command, null, false, now);

            // then
            assertThat(bundle.completed()).isFalse();
            assertThat(bundle.variant()).isNull();
            assertThat(bundle.errorMessage()).isEqualTo("서버 타임아웃");
            assertThat(bundle.retryable()).isTrue();
        }

        @Test
        @DisplayName("4xx 에러 실패 시 retryable이 false이다")
        void shouldNotBeRetryableFor4xxError() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            CompleteImageTransformCallbackCommand command =
                    new CompleteImageTransformCallbackCommand(
                            "req-1", "FAILED", null, null, null, "400 Bad Request");
            Instant now = Instant.now();

            // when
            ImageTransformCompletionBundle bundle = sut.create(outbox, command, null, false, now);

            // then
            assertThat(bundle.retryable()).isFalse();
        }

        @Test
        @DisplayName("needsSyncOutbox가 true이면 sync 아웃박스를 포함한다")
        void shouldIncludeSyncOutboxWhenNeeded() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            CompleteImageTransformCallbackCommand command =
                    new CompleteImageTransformCallbackCommand(
                            "req-1", "COMPLETED", "asset-1", 300, 300, null);
            Instant now = Instant.now();

            // when
            ImageTransformCompletionBundle bundle =
                    sut.create(outbox, command, "https://cdn/result.webp", true, now);

            // then
            assertThat(bundle.syncOutbox()).isPresent();
            assertThat(bundle.syncOutbox().get().isPending()).isTrue();
        }

        @Test
        @DisplayName("needsSyncOutbox가 false이면 sync 아웃박스를 포함하지 않는다")
        void shouldNotIncludeSyncOutboxWhenNotNeeded() {
            // given
            ImageTransformOutbox outbox = ImageTransformFixtures.processingOutbox();
            CompleteImageTransformCallbackCommand command =
                    new CompleteImageTransformCallbackCommand(
                            "req-1", "COMPLETED", "asset-1", 300, 300, null);
            Instant now = Instant.now();

            // when
            ImageTransformCompletionBundle bundle =
                    sut.create(outbox, command, "https://cdn/result.webp", false, now);

            // then
            assertThat(bundle.syncOutbox()).isEmpty();
        }
    }
}
