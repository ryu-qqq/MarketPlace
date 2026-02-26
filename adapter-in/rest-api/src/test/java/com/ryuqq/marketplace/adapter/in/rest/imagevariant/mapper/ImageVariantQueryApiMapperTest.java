package com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.imagevariant.ImageVariantApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response.ImageVariantApiResponse;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ImageVariantQueryApiMapper 단위 테스트")
class ImageVariantQueryApiMapperTest {

    private ImageVariantQueryApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ImageVariantQueryApiMapper();
    }

    @Nested
    @DisplayName("toApiResponses() - 목록 변환")
    class ToApiResponsesTest {

        @Test
        @DisplayName("ImageVariantResult 목록을 ImageVariantApiResponse 목록으로 변환한다")
        void toApiResponses_ConvertsList_ReturnsResponseList() {
            // given
            List<ImageVariantResult> results = ImageVariantApiFixtures.imageVariantResults();

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).hasSize(4);
            assertThat(responses.get(0).variantType()).isEqualTo("SMALL_WEBP");
            assertThat(responses.get(1).variantType()).isEqualTo("MEDIUM_WEBP");
            assertThat(responses.get(2).variantType()).isEqualTo("LARGE_WEBP");
            assertThat(responses.get(3).variantType()).isEqualTo("ORIGINAL_WEBP");
        }

        @Test
        @DisplayName("SMALL_WEBP Variant가 올바르게 변환된다")
        void toApiResponses_SmallWebp_ConvertsCorrectly() {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(ImageVariantType.SMALL_WEBP);

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).hasSize(1);
            ImageVariantApiResponse response = responses.get(0);
            assertThat(response.variantType()).isEqualTo("SMALL_WEBP");
            assertThat(response.variantUrl()).isEqualTo(ImageVariantApiFixtures.DEFAULT_SMALL_URL);
            assertThat(response.width()).isEqualTo(300);
            assertThat(response.height()).isEqualTo(300);
        }

        @Test
        @DisplayName("MEDIUM_WEBP Variant가 올바르게 변환된다")
        void toApiResponses_MediumWebp_ConvertsCorrectly() {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(ImageVariantType.MEDIUM_WEBP);

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).hasSize(1);
            ImageVariantApiResponse response = responses.get(0);
            assertThat(response.variantType()).isEqualTo("MEDIUM_WEBP");
            assertThat(response.variantUrl()).isEqualTo(ImageVariantApiFixtures.DEFAULT_MEDIUM_URL);
            assertThat(response.width()).isEqualTo(600);
            assertThat(response.height()).isEqualTo(600);
        }

        @Test
        @DisplayName("LARGE_WEBP Variant가 올바르게 변환된다")
        void toApiResponses_LargeWebp_ConvertsCorrectly() {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(ImageVariantType.LARGE_WEBP);

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).hasSize(1);
            ImageVariantApiResponse response = responses.get(0);
            assertThat(response.variantType()).isEqualTo("LARGE_WEBP");
            assertThat(response.variantUrl()).isEqualTo(ImageVariantApiFixtures.DEFAULT_LARGE_URL);
            assertThat(response.width()).isEqualTo(1200);
            assertThat(response.height()).isEqualTo(1200);
        }

        @Test
        @DisplayName("ORIGINAL_WEBP Variant는 width/height가 null로 변환된다")
        void toApiResponses_OriginalWebp_ConvertsWithNullDimensions() {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(ImageVariantType.ORIGINAL_WEBP);

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).hasSize(1);
            ImageVariantApiResponse response = responses.get(0);
            assertThat(response.variantType()).isEqualTo("ORIGINAL_WEBP");
            assertThat(response.variantUrl())
                    .isEqualTo(ImageVariantApiFixtures.DEFAULT_ORIGINAL_URL);
            assertThat(response.width()).isNull();
            assertThat(response.height()).isNull();
        }

        @Test
        @DisplayName("빈 목록이면 빈 응답 목록을 반환한다")
        void toApiResponses_EmptyList_ReturnsEmptyList() {
            // given
            List<ImageVariantResult> results = List.of();

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).isEmpty();
        }

        @Test
        @DisplayName("여러 Variant를 포함한 목록이 올바른 순서로 변환된다")
        void toApiResponses_MultipleVariants_PreservesOrder() {
            // given
            List<ImageVariantResult> results =
                    ImageVariantApiFixtures.imageVariantResults(
                            ImageVariantType.SMALL_WEBP, ImageVariantType.LARGE_WEBP);

            // when
            List<ImageVariantApiResponse> responses = mapper.toApiResponses(results);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.get(0).variantType()).isEqualTo("SMALL_WEBP");
            assertThat(responses.get(1).variantType()).isEqualTo("LARGE_WEBP");
        }
    }
}
