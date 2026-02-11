package com.ryuqq.marketplace.application.brand.assembler;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.brand.dto.response.BrandPageResult;
import com.ryuqq.marketplace.application.brand.dto.response.BrandResult;
import com.ryuqq.marketplace.domain.brand.BrandFixtures;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandAssembler 단위 테스트")
class BrandAssemblerTest {

    private BrandAssembler sut;

    @BeforeEach
    void setUp() {
        sut = new BrandAssembler();
    }

    @Nested
    @DisplayName("toResult() - 단일 Result 변환")
    class ToResultTest {

        @Test
        @DisplayName("브랜드를 Result로 변환한다")
        void toResult_ValidBrand_ReturnsResult() {
            // given
            Brand brand = BrandFixtures.activeBrand(1L);

            // when
            BrandResult result = sut.toResult(brand);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(brand.idValue());
            assertThat(result.code()).isEqualTo(brand.codeValue());
            assertThat(result.nameKo()).isEqualTo(brand.nameKo());
            assertThat(result.nameEn()).isEqualTo(brand.nameEn());
            assertThat(result.shortName()).isEqualTo(brand.shortName());
            assertThat(result.status()).isEqualTo(brand.status().name());
            assertThat(result.logoUrl()).isEqualTo(brand.logoUrlValue());
            assertThat(result.createdAt()).isEqualTo(brand.createdAt());
            assertThat(result.updatedAt()).isEqualTo(brand.updatedAt());
        }

        @Test
        @DisplayName("INACTIVE 상태의 브랜드를 Result로 변환한다")
        void toResult_InactiveBrand_ReturnsResult() {
            // given
            Brand brand = BrandFixtures.inactiveBrand(2L);

            // when
            BrandResult result = sut.toResult(brand);

            // then
            assertThat(result.status()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("toResults() - 리스트 변환")
    class ToResultsTest {

        @Test
        @DisplayName("브랜드 리스트를 Result 리스트로 변환한다")
        void toResults_ValidList_ReturnsResultList() {
            // given
            List<Brand> brands =
                    List.of(BrandFixtures.activeBrand(1L), BrandFixtures.activeBrand(2L));

            // when
            List<BrandResult> results = sut.toResults(brands);

            // then
            assertThat(results).hasSize(2);
            assertThat(results.get(0).id()).isEqualTo(1L);
            assertThat(results.get(1).id()).isEqualTo(2L);
        }

        @Test
        @DisplayName("빈 리스트를 빈 Result 리스트로 변환한다")
        void toResults_EmptyList_ReturnsEmptyList() {
            // given
            List<Brand> emptyBrands = List.of();

            // when
            List<BrandResult> results = sut.toResults(emptyBrands);

            // then
            assertThat(results).isEmpty();
        }
    }

    @Nested
    @DisplayName("toPageResult() - 페이지 Result 변환")
    class ToPageResultTest {

        @Test
        @DisplayName("브랜드 리스트를 PageResult로 변환한다")
        void toPageResult_ValidList_ReturnsPageResult() {
            // given
            List<Brand> brands =
                    List.of(BrandFixtures.activeBrand(1L), BrandFixtures.activeBrand(2L));
            int page = 0;
            int size = 20;
            long totalElements = 2L;

            // when
            BrandPageResult result = sut.toPageResult(brands, page, size, totalElements);

            // then
            assertThat(result).isNotNull();
            assertThat(result.results()).hasSize(2);
            assertThat(result.pageMeta().page()).isEqualTo(page);
            assertThat(result.pageMeta().size()).isEqualTo(size);
            assertThat(result.pageMeta().totalElements()).isEqualTo(totalElements);
        }

        @Test
        @DisplayName("빈 리스트로 빈 PageResult를 생성한다")
        void toPageResult_EmptyList_ReturnsEmptyPageResult() {
            // given
            List<Brand> emptyList = List.of();
            int page = 0;
            int size = 20;
            long totalElements = 0L;

            // when
            BrandPageResult result = sut.toPageResult(emptyList, page, size, totalElements);

            // then
            assertThat(result.results()).isEmpty();
            assertThat(result.isEmpty()).isTrue();
            assertThat(result.pageMeta().totalElements()).isZero();
        }
    }
}
