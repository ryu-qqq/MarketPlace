package com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.command.RequestImageTransformApiRequest;
import com.ryuqq.marketplace.application.imagetransform.dto.command.RequestImageTransformCommand;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariantCommandApiMapper 단위 테스트")
class ImageVariantCommandApiMapperTest {

    private ImageVariantCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ImageVariantCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, RequestImageTransformApiRequest) - 변환 요청 변환")
    class ToCommandTest {

        @Test
        @DisplayName("특정 Variant 타입 목록이 있으면 해당 타입으로 Command를 생성한다")
        void toCommand_WithVariantTypes_ReturnsCommandWithTypes() {
            // given
            Long productGroupId = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithVariantTypes();

            // when
            RequestImageTransformCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.variantTypes()).hasSize(2);
            assertThat(command.variantTypes()).contains(ImageVariantType.SMALL_WEBP);
            assertThat(command.variantTypes()).contains(ImageVariantType.MEDIUM_WEBP);
        }

        @Test
        @DisplayName("모든 Variant 타입이 있으면 4개 타입으로 Command를 생성한다")
        void toCommand_WithAllVariantTypes_ReturnsCommandWithAllTypes() {
            // given
            Long productGroupId = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithAllVariantTypes();

            // when
            RequestImageTransformCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.variantTypes()).hasSize(4);
            assertThat(command.variantTypes())
                    .containsExactlyInAnyOrder(
                            ImageVariantType.SMALL_WEBP,
                            ImageVariantType.MEDIUM_WEBP,
                            ImageVariantType.LARGE_WEBP,
                            ImageVariantType.ORIGINAL_WEBP);
        }

        @Test
        @DisplayName("request가 null이면 전체 Variant 대상 Command를 생성한다")
        void toCommand_WithNullRequest_ReturnsAllVariantsCommand() {
            // given
            Long productGroupId = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;

            // when
            RequestImageTransformCommand command = mapper.toCommand(productGroupId, null);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.variantTypes()).isNull();
        }

        @Test
        @DisplayName("variantTypes가 null이면 전체 Variant 대상 Command를 생성한다")
        void toCommand_WithNullVariantTypes_ReturnsAllVariantsCommand() {
            // given
            Long productGroupId = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithNullVariantTypes();

            // when
            RequestImageTransformCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.variantTypes()).isNull();
        }

        @Test
        @DisplayName("variantTypes가 빈 리스트이면 전체 Variant 대상 Command를 생성한다")
        void toCommand_WithEmptyVariantTypes_ReturnsAllVariantsCommand() {
            // given
            Long productGroupId = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithEmptyVariantTypes();

            // when
            RequestImageTransformCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.variantTypes()).isNull();
        }

        @Test
        @DisplayName("productGroupId가 올바르게 Command에 전달된다")
        void toCommand_ProductGroupId_IsCorrectlyMapped() {
            // given
            Long productGroupId = 999L;
            RequestImageTransformApiRequest request =
                    ImageVariantApiFixtures.requestWithVariantTypes();

            // when
            RequestImageTransformCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("잘못된 variantType 문자열이면 예외가 발생한다")
        void toCommand_InvalidVariantType_ThrowsException() {
            // given
            Long productGroupId = ImageVariantApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            RequestImageTransformApiRequest request =
                    new RequestImageTransformApiRequest(java.util.List.of("INVALID_TYPE"));

            // when & then
            assertThrows(
                    IllegalArgumentException.class,
                    () -> mapper.toCommand(productGroupId, request));
        }
    }
}
