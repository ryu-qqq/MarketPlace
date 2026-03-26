package com.ryuqq.marketplace.application.imagevariantsync.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.common.dto.result.SchedulerBatchProcessingResult;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantReadManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxCommandManager;
import com.ryuqq.marketplace.application.imagevariantsync.manager.ImageVariantSyncOutboxReadManager;
import com.ryuqq.marketplace.application.imagevariantsync.port.out.client.ImageVariantSyncClient;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageDimension;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ResultAssetId;
import com.ryuqq.marketplace.domain.imagevariantsync.aggregate.ImageVariantSyncOutbox;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
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
@DisplayName("ProcessPendingImageVariantSyncService 단위 테스트")
class ProcessPendingImageVariantSyncServiceTest {

    @InjectMocks private ProcessPendingImageVariantSyncService sut;

    @Mock private ImageVariantSyncOutboxReadManager outboxReadManager;
    @Mock private ImageVariantSyncOutboxCommandManager outboxCommandManager;
    @Mock private ImageVariantReadManager imageVariantReadManager;
    @Mock private ImageVariantSyncClient imageVariantSyncClient;

    @Nested
    @DisplayName("execute() - 배치 처리")
    class ExecuteTest {

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 처리 건수 0을 반환한다")
        void shouldReturnZeroWhenNoPendingOutboxes() {
            // given
            given(outboxReadManager.findPendingOutboxes(50)).willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(50);

            // then
            assertThat(result.total()).isZero();
            assertThat(result.success()).isZero();
        }

        @Test
        @DisplayName("variant 조회 후 syncVariants를 호출하고 COMPLETED 처리한다")
        void shouldSyncAndComplete() {
            // given
            ImageVariantSyncOutbox outbox = createPendingOutbox(1L);
            ImageVariant variant = createVariant(1L);

            given(outboxReadManager.findPendingOutboxes(50)).willReturn(List.of(outbox));
            given(
                            imageVariantReadManager.findBySourceImageId(
                                    1L, ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(List.of(variant));

            // when
            SchedulerBatchProcessingResult result = sut.execute(50);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.success()).isEqualTo(1);
            then(imageVariantSyncClient)
                    .should()
                    .syncVariants(eq(1L), eq("PRODUCT_GROUP_IMAGE"), anyList());
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("variant가 없으면 syncVariants를 호출하지 않고 실패 처리한다")
        void shouldFailWhenNoVariants() {
            // given
            ImageVariantSyncOutbox outbox = createPendingOutbox(1L);

            given(outboxReadManager.findPendingOutboxes(50)).willReturn(List.of(outbox));
            given(
                            imageVariantReadManager.findBySourceImageId(
                                    1L, ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(List.of());

            // when
            SchedulerBatchProcessingResult result = sut.execute(50);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(1);
            then(imageVariantSyncClient)
                    .should(never())
                    .syncVariants(anyLong(), anyString(), anyList());
            then(outboxCommandManager).should().persist(outbox);
        }

        @Test
        @DisplayName("syncVariants 예외 시 실패 처리한다")
        void shouldFailOnSyncException() {
            // given
            ImageVariantSyncOutbox outbox = createPendingOutbox(1L);
            ImageVariant variant = createVariant(1L);

            given(outboxReadManager.findPendingOutboxes(50)).willReturn(List.of(outbox));
            given(
                            imageVariantReadManager.findBySourceImageId(
                                    1L, ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(List.of(variant));
            org.mockito.Mockito.doThrow(new RuntimeException("세토프 서버 에러"))
                    .when(imageVariantSyncClient)
                    .syncVariants(anyLong(), anyString(), anyList());

            // when
            SchedulerBatchProcessingResult result = sut.execute(50);

            // then
            assertThat(result.total()).isEqualTo(1);
            assertThat(result.failed()).isEqualTo(1);
            then(outboxCommandManager).should().persist(outbox);
        }
    }

    private ImageVariantSyncOutbox createPendingOutbox(long sourceImageId) {
        return ImageVariantSyncOutbox.forNew(
                sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE, Instant.now());
    }

    private ImageVariant createVariant(long sourceImageId) {
        return ImageVariant.forNew(
                sourceImageId,
                ImageSourceType.PRODUCT_GROUP_IMAGE,
                ImageVariantType.SMALL_WEBP,
                ResultAssetId.of("test-asset"),
                ImageUrl.of("https://cdn.test/small.webp"),
                ImageDimension.of(300, 300),
                Instant.now());
    }
}
