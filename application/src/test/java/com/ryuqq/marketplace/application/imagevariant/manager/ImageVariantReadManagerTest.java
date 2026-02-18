package com.ryuqq.marketplace.application.imagevariant.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagevariant.port.out.query.ImageVariantQueryPort;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
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
@DisplayName("ImageVariantReadManager 단위 테스트")
class ImageVariantReadManagerTest {

    @InjectMocks private ImageVariantReadManager sut;

    @Mock private ImageVariantQueryPort queryPort;

    @Nested
    @DisplayName("findBySourceImageId() - 소스 이미지 ID로 Variant 목록 조회")
    class FindBySourceImageIdTest {

        @Test
        @DisplayName("소스 이미지 ID와 타입으로 Variant 목록을 반환한다")
        void findBySourceImageId_ValidParams_ReturnsVariants() {
            // given
            Long sourceImageId = ImageVariantFixtures.DEFAULT_SOURCE_IMAGE_ID;
            ImageSourceType sourceType = ImageVariantFixtures.DEFAULT_SOURCE_TYPE;
            List<ImageVariant> expectedVariants =
                    List.of(
                            ImageVariantFixtures.newSmallWebpVariant(),
                            ImageVariantFixtures.newMediumWebpVariant());

            given(queryPort.findBySourceImageId(sourceImageId, sourceType))
                    .willReturn(expectedVariants);

            // when
            List<ImageVariant> result = sut.findBySourceImageId(sourceImageId, sourceType);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedVariants);
            then(queryPort).should().findBySourceImageId(sourceImageId, sourceType);
        }

        @Test
        @DisplayName("Variant가 없으면 빈 목록을 반환한다")
        void findBySourceImageId_NoVariants_ReturnsEmptyList() {
            // given
            Long sourceImageId = 999L;
            ImageSourceType sourceType = ImageSourceType.PRODUCT_GROUP_IMAGE;

            given(queryPort.findBySourceImageId(sourceImageId, sourceType)).willReturn(List.of());

            // when
            List<ImageVariant> result = sut.findBySourceImageId(sourceImageId, sourceType);

            // then
            assertThat(result).isEmpty();
            then(queryPort).should().findBySourceImageId(sourceImageId, sourceType);
        }
    }
}
