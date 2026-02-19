package com.ryuqq.marketplace.application.imagevariant.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.imagevariant.ImageVariantFixtures;
import com.ryuqq.marketplace.domain.imagevariant.aggregate.ImageVariant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariantAssembler 단위 테스트")
class ImageVariantAssemblerTest {

    private ImageVariantAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new ImageVariantAssembler();
    }

    @Nested
    @DisplayName("toResult() - ImageVariant를 Result로 변환")
    class ToResultTest {

        @Test
        @DisplayName("ImageVariant를 ImageVariantResult로 변환한다")
        void toResult_ValidVariant_ReturnsResult() {
            // given
            ImageVariant variant = ImageVariantFixtures.newSmallWebpVariant();

            // when
            ImageVariantResult result = sut.toResult(variant);

            // then
            assertThat(result).isNotNull();
            assertThat(result.variantType()).isEqualTo(variant.variantType());
            assertThat(result.variantUrl()).isEqualTo(variant.variantUrlValue());
            assertThat(result.width()).isEqualTo(variant.width());
            assertThat(result.height()).isEqualTo(variant.height());
        }

        @Test
        @DisplayName("SMALL_WEBP Variant를 변환하면 너비/높이가 300x300이다")
        void toResult_SmallWebpVariant_Returns300x300Dimensions() {
            // given
            ImageVariant variant = ImageVariantFixtures.newSmallWebpVariant();

            // when
            ImageVariantResult result = sut.toResult(variant);

            // then
            assertThat(result.width()).isEqualTo(300);
            assertThat(result.height()).isEqualTo(300);
        }

        @Test
        @DisplayName("ORIGINAL_WEBP Variant를 변환하면 너비/높이가 null이다")
        void toResult_OriginalWebpVariant_ReturnsNullDimensions() {
            // given
            ImageVariant variant = ImageVariantFixtures.newOriginalWebpVariant();

            // when
            ImageVariantResult result = sut.toResult(variant);

            // then
            assertThat(result.width()).isNull();
            assertThat(result.height()).isNull();
        }
    }

    @Nested
    @DisplayName("toResults() - ImageVariant 목록을 Result 목록으로 변환")
    class ToResultsTest {

        @Test
        @DisplayName("ImageVariant 목록을 ImageVariantResult 목록으로 변환한다")
        void toResults_ValidVariants_ReturnsResults() {
            // given
            List<ImageVariant> variants =
                    List.of(
                            ImageVariantFixtures.newSmallWebpVariant(),
                            ImageVariantFixtures.newMediumWebpVariant(),
                            ImageVariantFixtures.newOriginalWebpVariant());

            // when
            List<ImageVariantResult> results = sut.toResults(variants);

            // then
            assertThat(results).hasSize(3);
        }

        @Test
        @DisplayName("빈 목록을 전달하면 빈 목록을 반환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<ImageVariant> emptyVariants = List.of();

            // when
            List<ImageVariantResult> results = sut.toResults(emptyVariants);

            // then
            assertThat(results).isEmpty();
        }

        @Test
        @DisplayName("단일 Variant 목록을 변환하면 단일 Result 목록을 반환한다")
        void toResults_SingleVariant_ReturnsSingleResult() {
            // given
            List<ImageVariant> variants = List.of(ImageVariantFixtures.newSmallWebpVariant());

            // when
            List<ImageVariantResult> results = sut.toResults(variants);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.getFirst().variantType())
                    .isEqualTo(ImageVariantFixtures.newSmallWebpVariant().variantType());
        }
    }
}
