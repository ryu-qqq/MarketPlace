package com.ryuqq.marketplace.application.imageupload.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ImageUploadCompletionStrategyProvider 단위 테스트")
class ImageUploadCompletionStrategyProviderTest {

    @Mock private ImageUploadCompletionStrategy productGroupImageStrategy;
    @Mock private ImageUploadCompletionStrategy descriptionImageStrategy;

    @Nested
    @DisplayName("getStrategy() - sourceType별 전략 반환")
    class GetStrategyTest {

        @Test
        @DisplayName("PRODUCT_GROUP_IMAGE 타입에 맞는 전략을 반환한다")
        void getStrategy_ProductGroupImageType_ReturnsCorrectStrategy() {
            // given
            given(productGroupImageStrategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(true);
            given(productGroupImageStrategy.supports(ImageSourceType.DESCRIPTION_IMAGE))
                    .willReturn(false);
            given(descriptionImageStrategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(false);
            given(descriptionImageStrategy.supports(ImageSourceType.DESCRIPTION_IMAGE))
                    .willReturn(true);

            ImageUploadCompletionStrategyProvider sut =
                    new ImageUploadCompletionStrategyProvider(
                            List.of(productGroupImageStrategy, descriptionImageStrategy));

            // when
            ImageUploadCompletionStrategy result =
                    sut.getStrategy(ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).isEqualTo(productGroupImageStrategy);
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 타입에 맞는 전략을 반환한다")
        void getStrategy_DescriptionImageType_ReturnsCorrectStrategy() {
            // given
            given(productGroupImageStrategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(true);
            given(productGroupImageStrategy.supports(ImageSourceType.DESCRIPTION_IMAGE))
                    .willReturn(false);
            given(descriptionImageStrategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(false);
            given(descriptionImageStrategy.supports(ImageSourceType.DESCRIPTION_IMAGE))
                    .willReturn(true);

            ImageUploadCompletionStrategyProvider sut =
                    new ImageUploadCompletionStrategyProvider(
                            List.of(productGroupImageStrategy, descriptionImageStrategy));

            // when
            ImageUploadCompletionStrategy result =
                    sut.getStrategy(ImageSourceType.DESCRIPTION_IMAGE);

            // then
            assertThat(result).isEqualTo(descriptionImageStrategy);
        }

        @Test
        @DisplayName("지원하지 않는 sourceType이면 IllegalArgumentException을 발생시킨다")
        void getStrategy_UnsupportedType_ThrowsIllegalArgumentException() {
            // given
            given(productGroupImageStrategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(false);
            given(productGroupImageStrategy.supports(ImageSourceType.DESCRIPTION_IMAGE))
                    .willReturn(false);

            ImageUploadCompletionStrategyProvider sut =
                    new ImageUploadCompletionStrategyProvider(List.of(productGroupImageStrategy));

            // when & then
            assertThatThrownBy(() -> sut.getStrategy(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("지원하지 않는 ImageSourceType");
        }

        @Test
        @DisplayName("전략 목록이 비어있으면 어떤 sourceType도 지원하지 않아 예외가 발생한다")
        void getStrategy_EmptyStrategyList_ThrowsIllegalArgumentException() {
            // given
            ImageUploadCompletionStrategyProvider sut =
                    new ImageUploadCompletionStrategyProvider(List.of());

            // when & then
            assertThatThrownBy(() -> sut.getStrategy(ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("ImageUploadCompletionStrategy 구현체 - supports() 메서드")
    class SupportsTest {

        @Test
        @DisplayName("ProductGroupImageCompletionStrategy는 PRODUCT_GROUP_IMAGE 타입을 지원한다")
        void productGroupImageStrategy_SupportsProductGroupImageType() {
            // given
            ImageUploadCompletionStrategy strategy =
                    new ImageUploadCompletionStrategy() {
                        @Override
                        public boolean supports(ImageSourceType sourceType) {
                            return sourceType == ImageSourceType.PRODUCT_GROUP_IMAGE;
                        }

                        @Override
                        public void complete(
                                Long sourceId, ImageUrl uploadedUrl, String fileAssetId) {}
                    };

            // when & then
            assertThat(strategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE)).isTrue();
            assertThat(strategy.supports(ImageSourceType.DESCRIPTION_IMAGE)).isFalse();
        }

        @Test
        @DisplayName("DescriptionImageCompletionStrategy는 DESCRIPTION_IMAGE 타입을 지원한다")
        void descriptionImageStrategy_SupportsDescriptionImageType() {
            // given
            ImageUploadCompletionStrategy strategy =
                    new ImageUploadCompletionStrategy() {
                        @Override
                        public boolean supports(ImageSourceType sourceType) {
                            return sourceType == ImageSourceType.DESCRIPTION_IMAGE;
                        }

                        @Override
                        public void complete(
                                Long sourceId, ImageUrl uploadedUrl, String fileAssetId) {}
                    };

            // when & then
            assertThat(strategy.supports(ImageSourceType.DESCRIPTION_IMAGE)).isTrue();
            assertThat(strategy.supports(ImageSourceType.PRODUCT_GROUP_IMAGE)).isFalse();
        }
    }
}
