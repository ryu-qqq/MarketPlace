package com.ryuqq.marketplace.adapter.out.persistence.category.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.mapper.CategoryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.category.repository.CategoryQueryDslRepository;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria;
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
 * CategoryQueryAdapterTest - 카테고리 Query Adapter 단위 테스트.
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
@DisplayName("CategoryQueryAdapter 단위 테스트")
class CategoryQueryAdapterTest {

    @Mock private CategoryQueryDslRepository queryDslRepository;

    @Mock private CategoryJpaEntityMapper mapper;

    @Mock private CategorySearchCriteria criteria;

    @InjectMocks private CategoryQueryAdapter queryAdapter;

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
            CategoryId categoryId = CategoryId.of(1L);
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.activeRootEntity();
            Category domain = createCategoryDomain(1L, "CAT001", "테스트 카테고리", "Test Category");

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<Category> result = queryAdapter.findById(categoryId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            CategoryId categoryId = CategoryId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<Category> result = queryAdapter.findById(categoryId);

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
        @DisplayName("검색 조건으로 카테고리 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            CategoryJpaEntity entity1 = CategoryJpaEntityFixtures.activeRootEntity(1L);
            CategoryJpaEntity entity2 = CategoryJpaEntityFixtures.activeRootEntity(2L);
            Category domain1 = createCategoryDomain(1L, "CAT001", "테스트 카테고리1", "Test Category1");
            Category domain2 = createCategoryDomain(2L, "CAT002", "테스트 카테고리2", "Test Category2");

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<Category> result = queryAdapter.findByCriteria(criteria);

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
            List<Category> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("검색 조건으로 카테고리 개수를 반환합니다")
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
            String code = "CAT001";
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

    private Category createCategoryDomain(Long id, String code, String nameKo, String nameEn) {
        java.time.Instant now = java.time.Instant.now();
        return Category.reconstitute(
                CategoryId.of(id),
                com.ryuqq.marketplace.domain.category.vo.CategoryCode.of(code),
                com.ryuqq.marketplace.domain.category.vo.CategoryName.of(nameKo, nameEn),
                null,
                com.ryuqq.marketplace.domain.category.vo.CategoryDepth.of(1),
                com.ryuqq.marketplace.domain.category.vo.CategoryPath.of("/" + id),
                com.ryuqq.marketplace.domain.category.vo.SortOrder.of(1),
                true,
                com.ryuqq.marketplace.domain.category.vo.CategoryStatus.ACTIVE,
                com.ryuqq.marketplace.domain.category.vo.Department.FASHION,
                com.ryuqq.marketplace.domain.category.vo.CategoryGroup.CLOTHING,
                null,
                now,
                now);
    }
}
