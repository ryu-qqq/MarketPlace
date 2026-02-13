package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.composite.BrandMappingWithBrandDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository.BrandMappingQueryDslRepository;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository.BrandPresetQueryDslRepository;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
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
@DisplayName("BrandPresetCompositionQueryAdapter 단위 테스트")
class BrandPresetCompositionQueryAdapterTest {

    @Mock private BrandPresetQueryDslRepository presetRepository;

    @Mock private BrandMappingQueryDslRepository brandMappingRepository;

    @InjectMocks private BrandPresetCompositionQueryAdapter adapter;

    @Nested
    @DisplayName("findDetailById 메서드 테스트")
    class FindDetailByIdTest {

        @Test
        @DisplayName("프리셋이 존재하고 매핑 브랜드가 있으면 완전한 BrandPresetDetailResult를 반환한다")
        void findDetailById_WithExistingPresetAndMappings_ReturnsCompleteResult() {
            // given
            Long id = 1L;
            BrandPresetDetailCompositeDto composite = createCompositeDto(id);
            List<BrandMappingWithBrandDto> mappings =
                    List.of(
                            new BrandMappingWithBrandDto(10L, 100L, "내부 브랜드 A"),
                            new BrandMappingWithBrandDto(20L, 200L, "내부 브랜드 B"));

            given(presetRepository.findDetailCompositeById(id)).willReturn(Optional.of(composite));
            given(brandMappingRepository.findMappedBrandsByPresetId(id)).willReturn(mappings);

            // when
            Optional<BrandPresetDetailResult> result = adapter.findDetailById(id);

            // then
            assertThat(result).isPresent();
            BrandPresetDetailResult detail = result.get();
            assertThat(detail.id()).isEqualTo(id);
            assertThat(detail.mappingBrand().brandCode()).isEqualTo("B123");
            assertThat(detail.mappingBrand().brandName()).isEqualTo("테스트브랜드");
            assertThat(detail.internalBrands()).hasSize(2);
            assertThat(detail.internalBrands().get(0).id()).isEqualTo(100L);
            then(presetRepository).should().findDetailCompositeById(id);
            then(brandMappingRepository).should().findMappedBrandsByPresetId(id);
        }

        @Test
        @DisplayName("프리셋이 존재하지 않으면 Optional.empty()를 반환한다")
        void findDetailById_WithNonExistentId_ReturnsEmpty() {
            // given
            Long id = 999L;
            given(presetRepository.findDetailCompositeById(id)).willReturn(Optional.empty());

            // when
            Optional<BrandPresetDetailResult> result = adapter.findDetailById(id);

            // then
            assertThat(result).isEmpty();
            then(presetRepository).should().findDetailCompositeById(id);
            then(brandMappingRepository).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("프리셋은 존재하지만 내부 브랜드 매핑이 없으면 빈 리스트로 반환한다")
        void findDetailById_WithNoMappings_ReturnsEmptyInternalBrands() {
            // given
            Long id = 1L;
            BrandPresetDetailCompositeDto composite = createCompositeDto(id);

            given(presetRepository.findDetailCompositeById(id)).willReturn(Optional.of(composite));
            given(brandMappingRepository.findMappedBrandsByPresetId(id)).willReturn(List.of());

            // when
            Optional<BrandPresetDetailResult> result = adapter.findDetailById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().internalBrands()).isEmpty();
        }
    }

    private BrandPresetDetailCompositeDto createCompositeDto(Long id) {
        return new BrandPresetDetailCompositeDto(
                id,
                100L,
                "테스트샵",
                "account123",
                1L,
                "테스트채널",
                200L,
                "B123",
                "테스트브랜드",
                "테스트프리셋",
                "ACTIVE",
                Instant.now(),
                Instant.now());
    }
}
