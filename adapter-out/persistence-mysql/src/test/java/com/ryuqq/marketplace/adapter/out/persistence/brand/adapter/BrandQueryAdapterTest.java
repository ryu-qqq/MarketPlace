package com.ryuqq.marketplace.adapter.out.persistence.brand.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brand.mapper.BrandJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.brand.repository.BrandQueryDslRepository;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria;
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

/**
 * BrandQueryAdapterTest - 브랜드 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("BrandQueryAdapter 단위 테스트")
class BrandQueryAdapterTest {

    @Mock private BrandQueryDslRepository queryDslRepository;

    @Mock private BrandJpaEntityMapper mapper;

    @Mock private BrandSearchCriteria criteria;

    @InjectMocks private BrandQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            BrandId brandId = BrandId.of(1L);
            BrandJpaEntity entity = BrandJpaEntityFixtures.activeEntity();
            Brand domain = createBrandDomain(1L, "BRAND001", "테스트 브랜드", "Test Brand", "테스트");

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Brand> result = queryAdapter.findById(brandId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            BrandId brandId = BrandId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<Brand> result = queryAdapter.findById(brandId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 브랜드 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            BrandJpaEntity entity1 = BrandJpaEntityFixtures.activeEntity(1L);
            BrandJpaEntity entity2 = BrandJpaEntityFixtures.activeEntity(2L);
            Brand domain1 = createBrandDomain(1L, "BRAND001", "테스트 브랜드1", "Test Brand1", "테스트1");
            Brand domain2 = createBrandDomain(2L, "BRAND002", "테스트 브랜드2", "Test Brand2", "테스트2");

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Brand> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<Brand> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 브랜드 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // 4. existsByCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByCode 메서드 테스트")
    class ExistsByCodeTest {

        @Test
        @DisplayName("존재하는 코드로 조회 시 true를 반환합니다")
        void existsByCode_WithExistingCode_ReturnsTrue() {
            // given
            String code = "BRAND001";
            given(queryDslRepository.existsByCode(code)).willReturn(true);

            // when
            boolean result = queryAdapter.existsByCode(code);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 코드로 조회 시 false를 반환합니다")
        void existsByCode_WithNonExistingCode_ReturnsFalse() {
            // given
            String code = "NONEXISTENT";
            given(queryDslRepository.existsByCode(code)).willReturn(false);

            // when
            boolean result = queryAdapter.existsByCode(code);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private Brand createBrandDomain(
            Long id, String code, String nameKo, String nameEn, String shortName) {
        java.time.Instant now = java.time.Instant.now();
        return Brand.reconstitute(
                BrandId.of(id),
                com.ryuqq.marketplace.domain.brand.vo.BrandCode.of(code),
                com.ryuqq.marketplace.domain.brand.vo.BrandName.of(nameKo, nameEn, shortName),
                com.ryuqq.marketplace.domain.brand.vo.BrandStatus.ACTIVE,
                com.ryuqq.marketplace.domain.brand.vo.LogoUrl.of("https://example.com/logo.png"),
                null,
                now,
                now);
    }
}
