package com.ryuqq.marketplace.application.outboundproductimage.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproductimage.port.out.command.OutboundProductImageCommandPort;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
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
@DisplayName("OutboundProductImageCommandManager 단위 테스트")
class OutboundProductImageCommandManagerTest {

    @InjectMocks private OutboundProductImageCommandManager sut;

    @Mock private OutboundProductImageCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단일 이미지 저장")
    class PersistTest {

        @Test
        @DisplayName("이미지를 저장하고 ID를 반환한다")
        void persist_ValidImage_ReturnsId() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();
            Long expectedId = 1L;

            given(commandPort.persist(image)).willReturn(expectedId);

            // when
            Long result = sut.persist(image);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(image);
        }

        @Test
        @DisplayName("commandPort.persist를 정확히 1회 호출한다")
        void persist_CallsCommandPortExactlyOnce() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.newThumbnailImage();

            given(commandPort.persist(image)).willReturn(1L);

            // when
            sut.persist(image);

            // then
            then(commandPort).should().persist(image);
            then(commandPort).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("persistAll() - 이미지 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("이미지 목록을 저장하고 ID 목록을 반환한다")
        void persistAll_ValidImages_ReturnsIds() {
            // given
            OutboundProductImage thumbnail = OutboundProductImageFixtures.activeThumbnailImage();
            OutboundProductImage detail = OutboundProductImageFixtures.activeDetailImage(2L, 1);
            List<OutboundProductImage> images = List.of(thumbnail, detail);
            List<Long> expectedIds = List.of(1L, 2L);

            given(commandPort.persistAll(images)).willReturn(expectedIds);

            // when
            List<Long> result = sut.persistAll(images);

            // then
            assertThat(result).isEqualTo(expectedIds);
            then(commandPort).should().persistAll(images);
        }

        @Test
        @DisplayName("빈 목록을 전달하면 commandPort를 호출하지 않고 빈 목록을 반환한다")
        void persistAll_EmptyList_DoesNotCallCommandPort() {
            // given
            List<OutboundProductImage> emptyImages = List.of();

            // when
            List<Long> result = sut.persistAll(emptyImages);

            // then
            assertThat(result).isEmpty();
            then(commandPort).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("단일 이미지 목록을 전달하면 commandPort.persistAll을 1회 호출한다")
        void persistAll_SingleImage_CallsCommandPortOnce() {
            // given
            OutboundProductImage image = OutboundProductImageFixtures.activeThumbnailImage();
            List<OutboundProductImage> images = List.of(image);

            given(commandPort.persistAll(images)).willReturn(List.of(1L));

            // when
            sut.persistAll(images);

            // then
            then(commandPort).should().persistAll(images);
            then(commandPort).shouldHaveNoMoreInteractions();
        }
    }
}
