package com.ryuqq.marketplace.application.productgroupdescription.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupdescription.port.out.command.DescriptionImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
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
@DisplayName("DescriptionImageCommandManager 단위 테스트")
class DescriptionImageCommandManagerTest {

    @InjectMocks private DescriptionImageCommandManager sut;

    @Mock private DescriptionImageCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 이미지 저장")
    class PersistTest {

        @Test
        @DisplayName("DescriptionImage를 저장하고 ID를 반환한다")
        void persist_ValidImage_ReturnsId() {
            // given
            DescriptionImage image = ProductGroupFixtures.defaultDescriptionImage();
            Long expectedId = 1L;
            given(commandPort.persist(image)).willReturn(expectedId);

            // when
            Long result = sut.persist(image);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(image);
        }
    }

    @Nested
    @DisplayName("persistAll() - 복수 이미지 저장")
    class PersistAllTest {

        @Test
        @DisplayName("복수의 DescriptionImage를 저장하고 ID 목록을 반환한다")
        void persistAll_MultipleImages_ReturnsIds() {
            // given
            DescriptionImage image1 = ProductGroupFixtures.defaultDescriptionImage();
            DescriptionImage image2 = ProductGroupFixtures.uploadedDescriptionImage();
            List<DescriptionImage> images = List.of(image1, image2);

            given(commandPort.persist(image1)).willReturn(1L);
            given(commandPort.persist(image2)).willReturn(2L);

            // when
            List<Long> result = sut.persistAll(images);

            // then
            assertThat(result).containsExactly(1L, 2L);
        }

        @Test
        @DisplayName("빈 이미지 목록으로 저장하면 빈 ID 목록을 반환한다")
        void persistAll_EmptyImages_ReturnsEmptyIds() {
            // given
            List<DescriptionImage> images = List.of();

            // when
            List<Long> result = sut.persistAll(images);

            // then
            assertThat(result).isEmpty();
            then(commandPort).shouldHaveNoInteractions();
        }
    }
}
