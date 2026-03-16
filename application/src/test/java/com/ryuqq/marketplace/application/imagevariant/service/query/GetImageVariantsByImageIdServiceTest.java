package com.ryuqq.marketplace.application.imagevariant.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagevariant.assembler.ImageVariantAssembler;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.application.imagevariant.manager.ImageVariantReadManager;
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
@DisplayName("GetImageVariantsByImageIdService 단위 테스트")
class GetImageVariantsByImageIdServiceTest {

    @InjectMocks private GetImageVariantsByImageIdService sut;

    @Mock private ImageVariantReadManager variantReadManager;
    @Mock private ImageVariantAssembler assembler;

    @Nested
    @DisplayName("execute() - 소스 이미지 ID로 Variant 목록 조회")
    class ExecuteTest {

        @Test
        @DisplayName("소스 이미지 ID로 Variant 목록을 조회하고 Result 목록을 반환한다")
        void execute_ValidSourceImageId_ReturnsVariantResults() {
            // given
            Long sourceImageId = ImageVariantFixtures.DEFAULT_SOURCE_IMAGE_ID;
            List<ImageVariant> variants =
                    List.of(
                            ImageVariantFixtures.newSmallWebpVariant(),
                            ImageVariantFixtures.newMediumWebpVariant());
            List<ImageVariantResult> expectedResults =
                    List.of(
                            new ImageVariantResult(
                                    com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType
                                            .SMALL_WEBP,
                                    ImageVariantFixtures.DEFAULT_RESULT_ASSET_ID,
                                    ImageVariantFixtures.DEFAULT_VARIANT_URL,
                                    300,
                                    300),
                            new ImageVariantResult(
                                    com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType
                                            .MEDIUM_WEBP,
                                    ImageVariantFixtures.DEFAULT_RESULT_ASSET_ID,
                                    "https://cdn.example.com/images/variant_600x600.webp",
                                    600,
                                    600));

            given(
                            variantReadManager.findBySourceImageId(
                                    sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(variants);
            given(assembler.toResults(variants)).willReturn(expectedResults);

            // when
            List<ImageVariantResult> result = sut.execute(sourceImageId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expectedResults);
            then(variantReadManager)
                    .should()
                    .findBySourceImageId(sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE);
            then(assembler).should().toResults(variants);
        }

        @Test
        @DisplayName("Variant가 없으면 빈 Result 목록을 반환한다")
        void execute_NoVariants_ReturnsEmptyList() {
            // given
            Long sourceImageId = 999L;
            List<ImageVariant> emptyVariants = List.of();
            List<ImageVariantResult> emptyResults = List.of();

            given(
                            variantReadManager.findBySourceImageId(
                                    sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(emptyVariants);
            given(assembler.toResults(emptyVariants)).willReturn(emptyResults);

            // when
            List<ImageVariantResult> result = sut.execute(sourceImageId);

            // then
            assertThat(result).isEmpty();
            then(variantReadManager)
                    .should()
                    .findBySourceImageId(sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE);
            then(assembler).should().toResults(emptyVariants);
        }

        @Test
        @DisplayName("항상 PRODUCT_GROUP_IMAGE 소스 타입으로 조회한다")
        void execute_AlwaysUseProductGroupImageSourceType() {
            // given
            Long sourceImageId = 1L;
            given(
                            variantReadManager.findBySourceImageId(
                                    sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE))
                    .willReturn(List.of());
            given(assembler.toResults(List.of())).willReturn(List.of());

            // when
            sut.execute(sourceImageId);

            // then
            then(variantReadManager)
                    .should()
                    .findBySourceImageId(sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE);
            then(variantReadManager).shouldHaveNoMoreInteractions();
        }
    }
}
