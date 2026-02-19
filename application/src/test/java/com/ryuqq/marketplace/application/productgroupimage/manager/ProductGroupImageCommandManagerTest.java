package com.ryuqq.marketplace.application.productgroupimage.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupimage.port.out.command.ProductGroupImageCommandPort;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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
@DisplayName("ProductGroupImageCommandManager 단위 테스트")
class ProductGroupImageCommandManagerTest {

    @InjectMocks private ProductGroupImageCommandManager sut;

    @Mock private ProductGroupImageCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 이미지 저장")
    class PersistTest {

        @Test
        @DisplayName("이미지를 저장하고 생성된 ID를 반환한다")
        void persist_ValidImage_ReturnsImageId() {
            // given
            ProductGroupImage image = ProductGroupFixtures.defaultProductGroupImage();
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
    @DisplayName("persistAll() - 다건 이미지 저장")
    class PersistAllTest {

        @Test
        @DisplayName("이미지 목록을 저장하고 생성된 ID 목록을 반환한다")
        void persistAll_MultipleImages_ReturnsImageIds() {
            // given
            ProductGroupImage image1 = ProductGroupFixtures.thumbnailImage();
            ProductGroupImage image2 = ProductGroupFixtures.detailImage(1);
            List<ProductGroupImage> images = List.of(image1, image2);

            given(commandPort.persist(image1)).willReturn(10L);
            given(commandPort.persist(image2)).willReturn(11L);

            // when
            List<Long> result = sut.persistAll(images);

            // then
            assertThat(result).containsExactly(10L, 11L);
            then(commandPort).should().persist(image1);
            then(commandPort).should().persist(image2);
        }

        @Test
        @DisplayName("빈 이미지 목록은 빈 ID 목록을 반환한다")
        void persistAll_EmptyList_ReturnsEmptyList() {
            // given
            List<ProductGroupImage> emptyImages = List.of();

            // when
            List<Long> result = sut.persistAll(emptyImages);

            // then
            assertThat(result).isEmpty();
            then(commandPort).shouldHaveNoInteractions();
        }
    }
}
