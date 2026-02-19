package com.ryuqq.marketplace.application.imageupload.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imageupload.port.out.command.ImageUploadOutboxCommandPort;
import com.ryuqq.marketplace.domain.imageupload.ImageUploadFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
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
@DisplayName("ImageUploadOutboxCommandManager 단위 테스트")
class ImageUploadOutboxCommandManagerTest {

    @InjectMocks private ImageUploadOutboxCommandManager sut;

    @Mock private ImageUploadOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단일 Outbox 저장")
    class PersistTest {

        @Test
        @DisplayName("Outbox를 저장하고 ID를 반환한다")
        void persist_ValidOutbox_ReturnsId() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            Long expectedId = 1L;

            given(commandPort.persist(outbox)).willReturn(expectedId);

            // when
            Long result = sut.persist(outbox);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(outbox);
        }
    }

    @Nested
    @DisplayName("persistAll() - Outbox 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("Outbox 목록을 순서대로 저장한다")
        void persistAll_ValidOutboxes_PersistsAll() {
            // given
            ImageUploadOutbox outbox1 = ImageUploadFixtures.pendingOutbox(1L);
            ImageUploadOutbox outbox2 = ImageUploadFixtures.pendingOutbox(2L);
            List<ImageUploadOutbox> outboxes = List.of(outbox1, outbox2);

            given(commandPort.persist(outbox1)).willReturn(1L);
            given(commandPort.persist(outbox2)).willReturn(2L);

            // when
            sut.persistAll(outboxes);

            // then
            then(commandPort).should().persist(outbox1);
            then(commandPort).should().persist(outbox2);
        }

        @Test
        @DisplayName("빈 목록을 전달하면 저장하지 않는다")
        void persistAll_EmptyList_DoesNotPersist() {
            // given
            List<ImageUploadOutbox> emptyOutboxes = List.of();

            // when
            sut.persistAll(emptyOutboxes);

            // then
            then(commandPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("단일 Outbox 목록을 전달하면 한 번만 저장한다")
        void persistAll_SingleOutbox_PersistsOnce() {
            // given
            ImageUploadOutbox outbox = ImageUploadFixtures.pendingOutbox();
            List<ImageUploadOutbox> outboxes = List.of(outbox);

            given(commandPort.persist(outbox)).willReturn(1L);

            // when
            sut.persistAll(outboxes);

            // then
            then(commandPort).should().persist(outbox);
            then(commandPort).shouldHaveNoMoreInteractions();
        }
    }
}
