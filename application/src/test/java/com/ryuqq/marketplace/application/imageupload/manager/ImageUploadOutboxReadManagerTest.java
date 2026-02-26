package com.ryuqq.marketplace.application.imageupload.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imageupload.port.out.query.ImageUploadOutboxQueryPort;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageUploadOutboxReadManager 단위 테스트")
class ImageUploadOutboxReadManagerTest {

    @InjectMocks private ImageUploadOutboxReadManager sut;

    @Mock private ImageUploadOutboxQueryPort queryPort;

    @Nested
    @DisplayName("findPendingOutboxesForRetry() - 재처리 대기 Outbox 조회")
    class FindPendingOutboxesForRetryTest {

        @Test
        @DisplayName("beforeTime과 limit으로 대기 중인 Outbox 목록을 반환한다")
        void findPendingOutboxesForRetry_ReturnsOutboxList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;
            ImageUploadOutbox outbox1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox outbox2 = ImageUploadFixtures.pendingOutbox(2L);
            List<ImageUploadOutbox> expected = List.of(outbox1, outbox2);

            given(queryPort.findPendingOutboxesForRetry(beforeTime, limit)).willReturn(expected);

            // when
            List<ImageUploadOutbox> result = sut.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findPendingOutboxesForRetry(beforeTime, limit);
        }

        @Test
        @DisplayName("대기 중인 Outbox가 없으면 빈 목록을 반환한다")
        void findPendingOutboxesForRetry_NoOutboxes_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int limit = 10;

            given(queryPort.findPendingOutboxesForRetry(beforeTime, limit)).willReturn(List.of());

            // when
            List<ImageUploadOutbox> result = sut.findPendingOutboxesForRetry(beforeTime, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - 타임아웃 Outbox 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("timeoutThreshold와 limit으로 타임아웃된 Outbox 목록을 반환한다")
        void findProcessingTimeoutOutboxes_ReturnsOutboxList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;
            ImageUploadOutbox outbox = ImageUploadFixtures.processingOutbox();
            List<ImageUploadOutbox> expected = List.of(outbox);

            given(queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(expected);

            // when
            List<ImageUploadOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).hasSize(1);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findProcessingTimeoutOutboxes(timeoutThreshold, limit);
        }

        @Test
        @DisplayName("타임아웃된 Outbox가 없으면 빈 목록을 반환한다")
        void findProcessingTimeoutOutboxes_NoOutboxes_ReturnsEmptyList() {
            // given
            Instant timeoutThreshold = Instant.now().minusSeconds(300);
            int limit = 10;

            given(queryPort.findProcessingTimeoutOutboxes(timeoutThreshold, limit))
                    .willReturn(List.of());

            // when
            List<ImageUploadOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutThreshold, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySourceIdsAndSourceType() - sourceId 목록으로 Outbox 조회")
    class FindBySourceIdsAndSourceTypeTest {

        @Test
        @DisplayName("sourceId 목록과 sourceType으로 Outbox 목록을 반환한다")
        void findBySourceIdsAndSourceType_ValidIds_ReturnsOutboxList() {
            // given
            List<Long> sourceIds = List.of(1L, 2L, 3L);
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;
            ImageUploadOutbox outbox1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox outbox2 = ImageUploadFixtures.pendingOutbox(2L);
            List<ImageUploadOutbox> expected = List.of(outbox1, outbox2);

            given(queryPort.findBySourceIdsAndSourceType(sourceIds, sourceType))
                    .willReturn(expected);

            // when
            List<ImageUploadOutbox> result =
                    sut.findBySourceIdsAndSourceType(sourceIds, sourceType);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findBySourceIdsAndSourceType(sourceIds, sourceType);
        }

        @Test
        @DisplayName("sourceId 목록이 null이면 빈 목록을 반환한다")
        void findBySourceIdsAndSourceType_NullIds_ReturnsEmptyList() {
            // given
            List<Long> nullIds = null;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            // when
            List<ImageUploadOutbox> result = sut.findBySourceIdsAndSourceType(nullIds, sourceType);

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("sourceId 목록이 비어있으면 빈 목록을 반환한다")
        void findBySourceIdsAndSourceType_EmptyIds_ReturnsEmptyList() {
            // given
            List<Long> emptyIds = List.of();
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            // when
            List<ImageUploadOutbox> result = sut.findBySourceIdsAndSourceType(emptyIds, sourceType);

            // then
            assertThat(result).isEmpty();
            then(queryPort).shouldHaveNoInteractions();
        }
    }
}
