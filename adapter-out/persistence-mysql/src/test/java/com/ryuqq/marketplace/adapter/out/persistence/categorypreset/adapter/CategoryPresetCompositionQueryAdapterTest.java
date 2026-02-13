package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.composite.CategoryMappingWithCategoryDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository.CategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository.CategoryPresetQueryDslRepository;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
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
@DisplayName("CategoryPresetCompositionQueryAdapter 단위 테스트")
class CategoryPresetCompositionQueryAdapterTest {

    @Mock private CategoryPresetQueryDslRepository presetRepository;

    @Mock private CategoryMappingQueryDslRepository categoryMappingRepository;

    @InjectMocks private CategoryPresetCompositionQueryAdapter adapter;

    @Nested
    @DisplayName("findDetailById 메서드 테스트")
    class FindDetailByIdTest {

        @Test
        @DisplayName("프리셋이 존재하고 매핑 카테고리가 있으면 완전한 CategoryPresetDetailResult를 반환한다")
        void findDetailById_WithExistingPresetAndMappings_ReturnsCompleteResult() {
            // given
            Long id = 1L;
            CategoryPresetDetailCompositeDto composite = createCompositeDto(id);
            List<CategoryMappingWithCategoryDto> mappings =
                    List.of(
                            new CategoryMappingWithCategoryDto(
                                    10L, 100L, "내부 카테고리 A", "내부 카테고리 A 경로", "CAT_A"),
                            new CategoryMappingWithCategoryDto(
                                    20L, 200L, "내부 카테고리 B", "내부 카테고리 B 경로", "CAT_B"));

            given(presetRepository.findDetailCompositeById(id)).willReturn(Optional.of(composite));
            given(categoryMappingRepository.findMappedCategoriesByPresetId(id))
                    .willReturn(mappings);

            // when
            Optional<CategoryPresetDetailResult> result = adapter.findDetailById(id);

            // then
            assertThat(result).isPresent();
            CategoryPresetDetailResult detail = result.get();
            assertThat(detail.id()).isEqualTo(id);
            assertThat(detail.mappingCategory().categoryCode()).isEqualTo("C123");
            assertThat(detail.mappingCategory().categoryPath()).isEqualTo("카테고리 > 경로");
            assertThat(detail.internalCategories()).hasSize(2);
            assertThat(detail.internalCategories().get(0).id()).isEqualTo(100L);
            then(presetRepository).should().findDetailCompositeById(id);
            then(categoryMappingRepository).should().findMappedCategoriesByPresetId(id);
        }

        @Test
        @DisplayName("프리셋이 존재하지 않으면 Optional.empty()를 반환한다")
        void findDetailById_WithNonExistentId_ReturnsEmpty() {
            // given
            Long id = 999L;
            given(presetRepository.findDetailCompositeById(id)).willReturn(Optional.empty());

            // when
            Optional<CategoryPresetDetailResult> result = adapter.findDetailById(id);

            // then
            assertThat(result).isEmpty();
            then(presetRepository).should().findDetailCompositeById(id);
            then(categoryMappingRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("프리셋은 존재하지만 내부 카테고리 매핑이 없으면 빈 리스트로 반환한다")
        void findDetailById_WithNoMappings_ReturnsEmptyInternalCategories() {
            // given
            Long id = 1L;
            CategoryPresetDetailCompositeDto composite = createCompositeDto(id);

            given(presetRepository.findDetailCompositeById(id)).willReturn(Optional.of(composite));
            given(categoryMappingRepository.findMappedCategoriesByPresetId(id))
                    .willReturn(List.of());

            // when
            Optional<CategoryPresetDetailResult> result = adapter.findDetailById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().internalCategories()).isEmpty();
        }
    }

    private CategoryPresetDetailCompositeDto createCompositeDto(Long id) {
        return new CategoryPresetDetailCompositeDto(
                id,
                100L,
                "테스트샵",
                "account123",
                1L,
                "테스트채널",
                300L,
                "C123",
                "카테고리 > 경로",
                "테스트프리셋",
                "ACTIVE",
                Instant.now(),
                Instant.now());
    }
}
