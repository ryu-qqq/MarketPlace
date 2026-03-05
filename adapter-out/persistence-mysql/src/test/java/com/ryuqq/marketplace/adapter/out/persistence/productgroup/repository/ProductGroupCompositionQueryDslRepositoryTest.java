package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition.CompositionProductConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition.ProductGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupEnrichmentResult;
import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupListCompositeResult;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * ProductGroupCompositionQueryDslRepositoryTest - 상품 그룹 Composition QueryDslRepository 통합 테스트.
 *
 * <p>크로스 도메인 JOIN (ProductGroup + Seller + Brand + Category) 기반 목록/상세 조회를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("ProductGroupCompositionQueryDslRepository 통합 테스트")
class ProductGroupCompositionQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupCompositionQueryDslRepository repository() {
        return new ProductGroupCompositionQueryDslRepository(
                new JPAQueryFactory(entityManager),
                new ProductGroupConditionBuilder(),
                new CompositionProductConditionBuilder());
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    /**
     * Seller, Brand, Category를 먼저 저장한 후 ProductGroup을 저장합니다. LEFT JOIN 기반 조회이므로 연관 엔티티가 없어도 결과에
     * 포함됩니다.
     */
    private ProductGroupJpaEntity persistProductGroupWithDependencies() {
        SellerJpaEntity seller = persist(SellerJpaEntityFixtures.activeEntity());
        BrandJpaEntity brand = persist(BrandJpaEntityFixtures.activeEntity(null));
        CategoryJpaEntity category = persist(CategoryJpaEntityFixtures.newEntity());

        return persist(
                ProductGroupJpaEntity.create(
                        null,
                        seller.getId(),
                        brand.getId(),
                        category.getId(),
                        1L,
                        1L,
                        "테스트 상품 그룹",
                        "NONE",
                        "ACTIVE",
                        java.time.Instant.now(),
                        java.time.Instant.now()));
    }

    private ProductGroupJpaEntity persistDeletedProductGroupWithDependencies() {
        SellerJpaEntity seller = persist(SellerJpaEntityFixtures.activeEntity());
        BrandJpaEntity brand = persist(BrandJpaEntityFixtures.activeEntity(null));
        CategoryJpaEntity category = persist(CategoryJpaEntityFixtures.newEntity());

        return persist(
                ProductGroupJpaEntity.create(
                        null,
                        seller.getId(),
                        brand.getId(),
                        category.getId(),
                        1L,
                        1L,
                        "삭제된 상품 그룹",
                        "NONE",
                        "DELETED",
                        java.time.Instant.now(),
                        java.time.Instant.now()));
    }

    // ========================================================================
    // 1. findCompositeById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findCompositeById")
    class FindCompositeByIdTest {

        @Test
        @DisplayName("미삭제 ProductGroup은 findCompositeById로 조회됩니다")
        void findCompositeById_WithActiveProductGroup_ReturnsResult() {
            // given
            ProductGroupJpaEntity saved = persistProductGroupWithDependencies();

            // when
            Optional<ProductGroupListCompositeResult> result =
                    repository().findCompositeById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("DELETED 상태 ProductGroup은 findCompositeById로 조회되지 않습니다")
        void findCompositeById_WithDeletedProductGroup_ReturnsEmpty() {
            // given
            ProductGroupJpaEntity deleted = persistDeletedProductGroupWithDependencies();

            // when
            Optional<ProductGroupListCompositeResult> result =
                    repository().findCompositeById(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 결과를 반환합니다")
        void findCompositeById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductGroupListCompositeResult> result =
                    repository().findCompositeById(999999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("조회 결과에 sellerId와 status 정보가 포함됩니다")
        void findCompositeById_ReturnsResultWithSellerAndStatus() {
            // given
            ProductGroupJpaEntity saved = persistProductGroupWithDependencies();

            // when
            Optional<ProductGroupListCompositeResult> result =
                    repository().findCompositeById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().sellerId()).isEqualTo(saved.getSellerId());
            assertThat(result.get().status()).isEqualTo("ACTIVE");
        }
    }

    // ========================================================================
    // 2. findCompositeByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findCompositeByCriteria")
    class FindCompositeByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 미삭제 ProductGroup 목록을 조회합니다")
        void findCompositeByCriteria_WithDefaultCriteria_ReturnsActiveGroups() {
            // given
            persistProductGroupWithDependencies();
            persistProductGroupWithDependencies();
            persistDeletedProductGroupWithDependencies(); // DELETED는 제외

            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            // when
            List<ProductGroupListCompositeResult> result =
                    repository().findCompositeByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(r -> !"DELETED".equals(r.status()));
        }

        @Test
        @DisplayName("결과가 없으면 빈 리스트를 반환합니다")
        void findCompositeByCriteria_WithNoMatchingGroups_ReturnsEmptyList() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            // when
            List<ProductGroupListCompositeResult> result =
                    repository().findCompositeByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("특정 ID 필터로 조회 시 해당 ProductGroup만 반환됩니다")
        void findCompositeByCriteria_WithProductGroupIdFilter_ReturnsOnlyMatchingGroups() {
            // given
            ProductGroupJpaEntity target = persistProductGroupWithDependencies();
            persistProductGroupWithDependencies(); // 다른 그룹

            ProductGroupSearchCriteria criteria =
                    ProductGroupSearchCriteria.of(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(target.getId()),
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.productgroup.query
                                            .ProductGroupSortKey.defaultKey()));

            // when
            List<ProductGroupListCompositeResult> result =
                    repository().findCompositeByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(target.getId());
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("미삭제 ProductGroup 개수를 반환합니다")
        void countByCriteria_WithActiveGroups_ReturnsCount() {
            // given
            persistProductGroupWithDependencies();
            persistProductGroupWithDependencies();
            persistDeletedProductGroupWithDependencies(); // DELETED는 카운트에서 제외

            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            ProductGroupSearchCriteria criteria = ProductGroupSearchCriteria.defaultCriteria();

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }

    // ========================================================================
    // 4. findEnrichmentsByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findEnrichmentsByProductGroupIds")
    class FindEnrichmentsByProductGroupIdsTest {

        @Test
        @DisplayName("빈 ID 목록으로 조회하면 빈 리스트를 반환합니다")
        void findEnrichmentsByProductGroupIds_WithEmptyIds_ReturnsEmpty() {
            // when
            List<ProductGroupEnrichmentResult> result =
                    repository().findEnrichmentsByProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 기본값 Enrichment를 반환합니다")
        void findEnrichmentsByProductGroupIds_WithNonExistentIds_ReturnsDefaultEnrichment() {
            // given
            List<Long> productGroupIds = List.of(999999L);

            // when
            List<ProductGroupEnrichmentResult> result =
                    repository().findEnrichmentsByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).productGroupId()).isEqualTo(999999L);
            assertThat(result.get(0).minPrice()).isZero();
            assertThat(result.get(0).maxPrice()).isZero();
            assertThat(result.get(0).optionGroups()).isEmpty();
        }

        @Test
        @DisplayName("여러 ID로 Enrichment 배치 조회 시 각 ID에 대한 결과를 반환합니다")
        void findEnrichmentsByProductGroupIds_WithMultipleIds_ReturnsResultsForEachId() {
            // given
            ProductGroupJpaEntity pg1 = persistProductGroupWithDependencies();
            ProductGroupJpaEntity pg2 = persistProductGroupWithDependencies();
            List<Long> productGroupIds = List.of(pg1.getId(), pg2.getId());

            // when
            List<ProductGroupEnrichmentResult> result =
                    repository().findEnrichmentsByProductGroupIds(productGroupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.stream().map(ProductGroupEnrichmentResult::productGroupId).toList())
                    .containsExactlyInAnyOrder(pg1.getId(), pg2.getId());
        }
    }

    // ========================================================================
    // 5. findDetailCompositeById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findDetailCompositeById")
    class FindDetailCompositeByIdTest {

        @Test
        @DisplayName("미삭제 ProductGroup은 findDetailCompositeById로 조회됩니다")
        void findDetailCompositeById_WithActiveProductGroup_ReturnsResult() {
            // given
            ProductGroupJpaEntity saved = persistProductGroupWithDependencies();

            // when
            Optional<
                            com.ryuqq.marketplace.application.productgroup.dto.composite
                                    .ProductGroupDetailCompositeQueryResult>
                    result = repository().findDetailCompositeById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().id()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("DELETED 상태 ProductGroup은 findDetailCompositeById로 조회되지 않습니다")
        void findDetailCompositeById_WithDeletedProductGroup_ReturnsEmpty() {
            // given
            ProductGroupJpaEntity deleted = persistDeletedProductGroupWithDependencies();

            // when
            Optional<
                            com.ryuqq.marketplace.application.productgroup.dto.composite
                                    .ProductGroupDetailCompositeQueryResult>
                    result = repository().findDetailCompositeById(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 결과를 반환합니다")
        void findDetailCompositeById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<
                            com.ryuqq.marketplace.application.productgroup.dto.composite
                                    .ProductGroupDetailCompositeQueryResult>
                    result = repository().findDetailCompositeById(999999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("조회 결과에 productGroupName과 optionType 정보가 포함됩니다")
        void findDetailCompositeById_ReturnsResultWithProductGroupInfo() {
            // given
            ProductGroupJpaEntity saved = persistProductGroupWithDependencies();

            // when
            Optional<
                            com.ryuqq.marketplace.application.productgroup.dto.composite
                                    .ProductGroupDetailCompositeQueryResult>
                    result = repository().findDetailCompositeById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().productGroupName()).isEqualTo(saved.getProductGroupName());
            assertThat(result.get().optionType()).isEqualTo(saved.getOptionType());
        }
    }
}
