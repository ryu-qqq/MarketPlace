package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.condition.ProductGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchCriteria;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSearchField;
import com.ryuqq.marketplace.domain.productgroup.query.ProductGroupSortKey;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * ProductGroupQueryDslRepositoryTest - 상품 그룹 QueryDslRepository 통합 테스트.
 *
 * <p>DELETED 상태 필터 및 조건 검색 적용을 검증합니다.
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
@DisplayName("ProductGroupQueryDslRepository 통합 테스트")
class ProductGroupQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupQueryDslRepository repository() {
        return new ProductGroupQueryDslRepository(
                new JPAQueryFactory(entityManager), new ProductGroupConditionBuilder());
    }

    private ProductGroupJpaEntity persist(ProductGroupJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private ProductGroupImageJpaEntity persistImage(ProductGroupImageJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SellerOptionGroupJpaEntity persistOptionGroup(SellerOptionGroupJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SellerOptionValueJpaEntity persistOptionValue(SellerOptionValueJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private ProductGroupSearchCriteria criteriaWith(
            List<ProductGroupStatus> statuses,
            List<Long> sellerIds,
            List<Long> brandIds,
            List<Long> categoryIds,
            List<Long> productGroupIds,
            ProductGroupSearchField searchField,
            String searchWord,
            ProductGroupSortKey sortKey,
            SortDirection direction,
            int page,
            int size) {
        return ProductGroupSearchCriteria.of(
                statuses,
                sellerIds,
                brandIds,
                categoryIds,
                productGroupIds,
                searchField,
                searchWord,
                null,
                QueryContext.of(sortKey, direction, PageRequest.of(page, size)));
    }

    private ProductGroupSearchCriteria defaultCriteriaWithSize(int size) {
        return criteriaWith(
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                null,
                null,
                ProductGroupSortKey.CREATED_AT,
                SortDirection.DESC,
                0,
                size);
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 상태 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            // given
            ProductGroupJpaEntity saved = persist(ProductGroupJpaEntityFixtures.newEntity());

            // when
            Optional<ProductGroupJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("DELETED 상태 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeletedStatus_ReturnsEmpty() {
            // given
            ProductGroupJpaEntity deleted = persist(ProductGroupJpaEntityFixtures.deletedEntity());

            // when
            Optional<ProductGroupJpaEntity> result = repository().findById(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductGroupJpaEntity> result = repository().findById(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByIdsAndSellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIdsAndSellerId")
    class FindByIdsAndSellerIdTest {

        @Test
        @DisplayName("ID 목록과 SellerId로 미삭제 상품 그룹을 조회합니다")
        void findByIdsAndSellerId_WithValidParams_ReturnsEntities() {
            // given
            Long sellerId = 1L;
            ProductGroupJpaEntity entity1 =
                    persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, sellerId));
            ProductGroupJpaEntity entity2 =
                    persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, sellerId));

            // when
            List<ProductGroupJpaEntity> result =
                    repository()
                            .findByIdsAndSellerId(
                                    List.of(entity1.getId(), entity2.getId()), sellerId);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("다른 SellerId의 상품 그룹은 조회되지 않습니다")
        void findByIdsAndSellerId_WithDifferentSellerId_ReturnsEmpty() {
            // given
            Long sellerId = 1L;
            Long otherSellerId = 2L;
            ProductGroupJpaEntity entity =
                    persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, sellerId));

            // when
            List<ProductGroupJpaEntity> result =
                    repository().findByIdsAndSellerId(List.of(entity.getId()), otherSellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("DELETED 상태 상품 그룹은 조회에서 제외됩니다")
        void findByIdsAndSellerId_WithDeletedEntity_ExcludesDeleted() {
            // given
            Long sellerId = 1L;
            ProductGroupJpaEntity active =
                    persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, sellerId));
            ProductGroupJpaEntity deleted = persist(ProductGroupJpaEntityFixtures.deletedEntity());

            // when
            List<ProductGroupJpaEntity> result =
                    repository()
                            .findByIdsAndSellerId(
                                    List.of(active.getId(), deleted.getId()), sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(active.getId());
        }
    }

    // ========================================================================
    // 3. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("필터 없이 전체 미삭제 상품 그룹을 조회합니다")
        void findByCriteria_WithNoFilter_ReturnsAllNonDeleted() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.draftEntity());
            persist(ProductGroupJpaEntityFixtures.deletedEntity());

            ProductGroupSearchCriteria criteria = defaultCriteriaWithSize(20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("sellerId로 필터링합니다")
        void findByCriteria_WithSellerIdFilter_ReturnsFiltered() {
            // given
            Long targetSellerId = 99L;
            persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, targetSellerId));
            persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, 88L));

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(targetSellerId),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSellerId()).isEqualTo(targetSellerId);
        }

        @Test
        @DisplayName("brandId로 필터링합니다")
        void findByCriteria_WithBrandIdFilter_ReturnsFiltered() {
            // given
            Long targetBrandId = ProductGroupJpaEntityFixtures.DEFAULT_BRAND_ID;
            persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(targetBrandId),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getBrandId()).isEqualTo(targetBrandId);
        }

        @Test
        @DisplayName("존재하지 않는 brandId로 필터링하면 빈 결과를 반환합니다")
        void findByCriteria_WithNonExistentBrandId_ReturnsEmpty() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(999999L),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("categoryId로 필터링합니다")
        void findByCriteria_WithCategoryIdFilter_ReturnsFiltered() {
            // given
            Long targetCategoryId = ProductGroupJpaEntityFixtures.DEFAULT_CATEGORY_ID;
            persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(targetCategoryId),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getCategoryId()).isEqualTo(targetCategoryId);
        }

        @Test
        @DisplayName("status로 필터링합니다")
        void findByCriteria_WithStatusFilter_ReturnsFiltered() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.draftEntity());
            persist(ProductGroupJpaEntityFixtures.inactiveEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(ProductGroupStatus.ACTIVE),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("DELETED 상태는 status 필터와 관계없이 항상 제외됩니다")
        void findByCriteria_WithDeletedEntities_AlwaysExcluded() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.deletedEntity());

            ProductGroupSearchCriteria criteria = defaultCriteriaWithSize(20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isNotEqualTo("DELETED");
        }

        @Test
        @DisplayName("offset/limit 페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResult() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            2);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("두 번째 페이지 조회 시 나머지 결과를 반환합니다")
        void findByCriteria_WithSecondPage_ReturnsRemainingResult() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            1,
                            2);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("CREATED_AT ASC 정렬이 적용됩니다")
        void findByCriteria_WithCreatedAtAsc_ReturnsSortedResult() {
            // given
            ProductGroupJpaEntity first = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity second = persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.ASC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(first.getId());
            assertThat(result.get(1).getId()).isEqualTo(second.getId());
        }

        @Test
        @DisplayName("CREATED_AT DESC 정렬이 적용됩니다")
        void findByCriteria_WithCreatedAtDesc_ReturnsSortedResult() {
            // given
            ProductGroupJpaEntity first = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity second = persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(second.getId());
            assertThat(result.get(1).getId()).isEqualTo(first.getId());
        }

        @Test
        @DisplayName("UPDATED_AT ASC 정렬이 적용됩니다")
        void findByCriteria_WithUpdatedAtAsc_ReturnsSortedResult() {
            // given
            ProductGroupJpaEntity first = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity second = persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.UPDATED_AT,
                            SortDirection.ASC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(first.getId());
        }

        @Test
        @DisplayName("UPDATED_AT DESC 정렬이 적용됩니다")
        void findByCriteria_WithUpdatedAtDesc_ReturnsSortedResult() {
            // given
            ProductGroupJpaEntity first = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity second = persist(ProductGroupJpaEntityFixtures.activeEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.UPDATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isEqualTo(second.getId());
        }

        @Test
        @DisplayName("NAME ASC 정렬이 적용됩니다")
        void findByCriteria_WithNameAsc_ReturnsSortedResult() {
            // given
            Instant now = Instant.now();
            ProductGroupJpaEntity entityB =
                    persist(
                            ProductGroupJpaEntity.create(
                                    null, 1L, 100L, 200L, 1L, 1L, "B상품", "NONE", "ACTIVE", now,
                                    now));
            ProductGroupJpaEntity entityA =
                    persist(
                            ProductGroupJpaEntity.create(
                                    null, 1L, 100L, 200L, 1L, 1L, "A상품", "NONE", "ACTIVE", now,
                                    now));

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.NAME,
                            SortDirection.ASC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductGroupName()).isEqualTo("A상품");
            assertThat(result.get(1).getProductGroupName()).isEqualTo("B상품");
        }

        @Test
        @DisplayName("NAME DESC 정렬이 적용됩니다")
        void findByCriteria_WithNameDesc_ReturnsSortedResult() {
            // given
            Instant now = Instant.now();
            ProductGroupJpaEntity entityA =
                    persist(
                            ProductGroupJpaEntity.create(
                                    null, 1L, 100L, 200L, 1L, 1L, "A상품", "NONE", "ACTIVE", now,
                                    now));
            ProductGroupJpaEntity entityB =
                    persist(
                            ProductGroupJpaEntity.create(
                                    null, 1L, 100L, 200L, 1L, 1L, "B상품", "NONE", "ACTIVE", now,
                                    now));

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.NAME,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductGroupName()).isEqualTo("B상품");
            assertThat(result.get(1).getProductGroupName()).isEqualTo("A상품");
        }

        @Test
        @DisplayName("searchWord로 상품명 검색이 적용됩니다")
        void findByCriteria_WithSearchWord_ReturnsMatchedResult() {
            // given
            Instant now = Instant.now();
            persist(
                    ProductGroupJpaEntity.create(
                            null, 1L, 100L, 200L, 1L, 1L, "나이키 신발", "NONE", "ACTIVE", now, now));
            persist(
                    ProductGroupJpaEntity.create(
                            null, 1L, 100L, 200L, 1L, 1L, "아디다스 운동화", "NONE", "ACTIVE", now, now));

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            "나이키",
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductGroupName()).contains("나이키");
        }

        @Test
        @DisplayName("NAME 검색 필드로 상품명을 검색합니다")
        void findByCriteria_WithNameSearchField_ReturnsMatchedResult() {
            // given
            Instant now = Instant.now();
            persist(
                    ProductGroupJpaEntity.create(
                            null, 1L, 100L, 200L, 1L, 1L, "프리미엄 자켓", "NONE", "ACTIVE", now, now));
            persist(
                    ProductGroupJpaEntity.create(
                            null, 1L, 100L, 200L, 1L, 1L, "기본 티셔츠", "NONE", "ACTIVE", now, now));

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            ProductGroupSearchField.NAME,
                            "자켓",
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductGroupName()).contains("자켓");
        }

        @Test
        @DisplayName("데이터가 없으면 빈 결과를 반환합니다")
        void findByCriteria_WithNoData_ReturnsEmpty() {
            // given
            ProductGroupSearchCriteria criteria = defaultCriteriaWithSize(20);

            // when
            List<ProductGroupJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("미삭제 상품 그룹의 전체 개수를 반환합니다")
        void countByCriteria_WithNoFilter_ReturnsTotalCount() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.draftEntity());
            persist(ProductGroupJpaEntityFixtures.deletedEntity());

            ProductGroupSearchCriteria criteria = defaultCriteriaWithSize(20);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("sellerId 필터 조건이 적용된 개수를 반환합니다")
        void countByCriteria_WithSellerIdFilter_ReturnsFilteredCount() {
            // given
            Long targetSellerId = 99L;
            persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, targetSellerId));
            persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, targetSellerId));
            persist(ProductGroupJpaEntityFixtures.activeEntityWithSeller(null, 88L));

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(),
                            List.of(targetSellerId),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("status 필터 조건이 적용된 개수를 반환합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.draftEntity());

            ProductGroupSearchCriteria criteria =
                    criteriaWith(
                            List.of(ProductGroupStatus.DRAFT),
                            List.of(),
                            List.of(),
                            List.of(),
                            List.of(),
                            null,
                            null,
                            ProductGroupSortKey.CREATED_AT,
                            SortDirection.DESC,
                            0,
                            20);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1L);
        }

        @Test
        @DisplayName("DELETED 상태는 카운트에서 항상 제외됩니다")
        void countByCriteria_WithDeletedEntities_AlwaysExcluded() {
            // given
            persist(ProductGroupJpaEntityFixtures.activeEntity());
            persist(ProductGroupJpaEntityFixtures.deletedEntity());
            persist(ProductGroupJpaEntityFixtures.deletedEntity());

            ProductGroupSearchCriteria criteria = defaultCriteriaWithSize(20);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1L);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            ProductGroupSearchCriteria criteria = defaultCriteriaWithSize(20);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(0L);
        }
    }

    // ========================================================================
    // 5. findImagesByProductGroupId / findImagesByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findImagesByProductGroupId")
    class FindImagesByProductGroupIdTest {

        @Test
        @DisplayName("상품 그룹 ID에 해당하는 이미지를 sortOrder ASC로 조회합니다")
        void findImagesByProductGroupId_ReturnsImagesSortedBySortOrder() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());
            Long pgId = productGroup.getId();

            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null, pgId, "http://img3.jpg", null, "MAIN", 3, false, null));
            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null, pgId, "http://img1.jpg", null, "MAIN", 1, false, null));
            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null, pgId, "http://img2.jpg", null, "SUB", 2, false, null));

            // when
            List<ProductGroupImageJpaEntity> result = repository().findImagesByProductGroupId(pgId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder()).isEqualTo(1);
            assertThat(result.get(1).getSortOrder()).isEqualTo(2);
            assertThat(result.get(2).getSortOrder()).isEqualTo(3);
        }

        @Test
        @DisplayName("해당 상품 그룹의 이미지가 없으면 빈 결과를 반환합니다")
        void findImagesByProductGroupId_WithNoImages_ReturnsEmpty() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findImagesByProductGroupId(productGroup.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 상품 그룹의 이미지는 조회되지 않습니다")
        void findImagesByProductGroupId_WithOtherGroupImages_ExcludesOtherGroups() {
            // given
            ProductGroupJpaEntity group1 = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity group2 = persist(ProductGroupJpaEntityFixtures.activeEntity());

            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null, group1.getId(), "http://img1.jpg", null, "MAIN", 1, false, null));
            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null, group2.getId(), "http://img2.jpg", null, "MAIN", 1, false, null));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findImagesByProductGroupId(group1.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductGroupId()).isEqualTo(group1.getId());
        }
    }

    @Nested
    @DisplayName("findImagesByProductGroupIds")
    class FindImagesByProductGroupIdsTest {

        @Test
        @DisplayName("여러 상품 그룹 ID에 해당하는 이미지를 sortOrder ASC로 조회합니다")
        void findImagesByProductGroupIds_ReturnsImagesSortedBySortOrder() {
            // given
            ProductGroupJpaEntity group1 = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity group2 = persist(ProductGroupJpaEntityFixtures.activeEntity());

            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null,
                            group1.getId(),
                            "http://g1-img2.jpg",
                            null,
                            "SUB",
                            2,
                            false,
                            null));
            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null,
                            group1.getId(),
                            "http://g1-img1.jpg",
                            null,
                            "MAIN",
                            1,
                            false,
                            null));
            persistImage(
                    ProductGroupImageJpaEntity.create(
                            null,
                            group2.getId(),
                            "http://g2-img1.jpg",
                            null,
                            "MAIN",
                            1,
                            false,
                            null));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository()
                            .findImagesByProductGroupIds(List.of(group1.getId(), group2.getId()));

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder())
                    .isLessThanOrEqualTo(result.get(1).getSortOrder());
        }

        @Test
        @DisplayName("빈 ID 목록에 대해 빈 결과를 반환합니다")
        void findImagesByProductGroupIds_WithEmptyIds_ReturnsEmpty() {
            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findImagesByProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 6. findOptionGroupsByProductGroupId / findOptionGroupsByProductGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findOptionGroupsByProductGroupId")
    class FindOptionGroupsByProductGroupIdTest {

        @Test
        @DisplayName("상품 그룹 ID에 해당하는 옵션 그룹을 sortOrder ASC로 조회합니다")
        void findOptionGroupsByProductGroupId_ReturnsSortedByOrder() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());
            Long pgId = productGroup.getId();

            persistOptionGroup(
                    SellerOptionGroupJpaEntity.create(
                            null, pgId, "사이즈", null, "PREDEFINED", 2, false, null));
            persistOptionGroup(
                    SellerOptionGroupJpaEntity.create(
                            null, pgId, "색상", null, "PREDEFINED", 1, false, null));

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findOptionGroupsByProductGroupId(pgId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSortOrder()).isEqualTo(1);
            assertThat(result.get(0).getOptionGroupName()).isEqualTo("색상");
            assertThat(result.get(1).getSortOrder()).isEqualTo(2);
            assertThat(result.get(1).getOptionGroupName()).isEqualTo("사이즈");
        }

        @Test
        @DisplayName("해당 상품 그룹의 옵션 그룹이 없으면 빈 결과를 반환합니다")
        void findOptionGroupsByProductGroupId_WithNoData_ReturnsEmpty() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findOptionGroupsByProductGroupId(productGroup.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Fixture로 생성한 옵션 그룹이 정상 조회됩니다")
        void findOptionGroupsByProductGroupId_WithFixture_ReturnsResult() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());
            Long pgId = productGroup.getId();

            persistOptionGroup(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(pgId));

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findOptionGroupsByProductGroupId(pgId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductGroupId()).isEqualTo(pgId);
            assertThat(result.get(0).isDeleted()).isFalse();
        }
    }

    @Nested
    @DisplayName("findOptionGroupsByProductGroupIds")
    class FindOptionGroupsByProductGroupIdsTest {

        @Test
        @DisplayName("여러 상품 그룹 ID에 해당하는 옵션 그룹을 sortOrder ASC로 조회합니다")
        void findOptionGroupsByProductGroupIds_ReturnsSortedByOrder() {
            // given
            ProductGroupJpaEntity group1 = persist(ProductGroupJpaEntityFixtures.activeEntity());
            ProductGroupJpaEntity group2 = persist(ProductGroupJpaEntityFixtures.activeEntity());

            persistOptionGroup(
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntity(group1.getId()));
            persistOptionGroup(
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntity(group2.getId()));

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository()
                            .findOptionGroupsByProductGroupIds(
                                    List.of(group1.getId(), group2.getId()));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getSortOrder())
                    .isLessThanOrEqualTo(result.get(1).getSortOrder());
        }

        @Test
        @DisplayName("빈 ID 목록에 대해 빈 결과를 반환합니다")
        void findOptionGroupsByProductGroupIds_WithEmptyIds_ReturnsEmpty() {
            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findOptionGroupsByProductGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 7. findOptionValuesByOptionGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findOptionValuesByOptionGroupIds")
    class FindOptionValuesByOptionGroupIdsTest {

        @Test
        @DisplayName("옵션 그룹 ID에 해당하는 옵션 값을 sortOrder ASC로 조회합니다")
        void findOptionValuesByOptionGroupIds_ReturnsSortedByOrder() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());
            SellerOptionGroupJpaEntity optionGroup =
                    persistOptionGroup(
                            ProductGroupJpaEntityFixtures.activeOptionGroupEntity(
                                    productGroup.getId()));

            Long groupId = optionGroup.getId();
            persistOptionValue(
                    SellerOptionValueJpaEntity.create(null, groupId, "XL", null, 3, false, null));
            persistOptionValue(
                    SellerOptionValueJpaEntity.create(null, groupId, "S", null, 1, false, null));
            persistOptionValue(
                    SellerOptionValueJpaEntity.create(null, groupId, "M", null, 2, false, null));

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findOptionValuesByOptionGroupIds(List.of(groupId));

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder()).isEqualTo(1);
            assertThat(result.get(0).getOptionValueName()).isEqualTo("S");
            assertThat(result.get(1).getSortOrder()).isEqualTo(2);
            assertThat(result.get(1).getOptionValueName()).isEqualTo("M");
            assertThat(result.get(2).getSortOrder()).isEqualTo(3);
            assertThat(result.get(2).getOptionValueName()).isEqualTo("XL");
        }

        @Test
        @DisplayName("null optionGroupIds는 빈 결과를 반환합니다")
        void findOptionValuesByOptionGroupIds_WithNull_ReturnsEmpty() {
            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findOptionValuesByOptionGroupIds(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 optionGroupIds는 빈 결과를 반환합니다")
        void findOptionValuesByOptionGroupIds_WithEmptyList_ReturnsEmpty() {
            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findOptionValuesByOptionGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Fixture로 생성한 옵션 값이 정상 조회됩니다")
        void findOptionValuesByOptionGroupIds_WithFixture_ReturnsResult() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());
            SellerOptionGroupJpaEntity optionGroup =
                    persistOptionGroup(
                            ProductGroupJpaEntityFixtures.activeOptionGroupEntity(
                                    productGroup.getId()));

            Long groupId = optionGroup.getId();
            persistOptionValue(ProductGroupJpaEntityFixtures.activeOptionValueEntity(groupId));

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findOptionValuesByOptionGroupIds(List.of(groupId));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSellerOptionGroupId()).isEqualTo(groupId);
            assertThat(result.get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("여러 옵션 그룹의 값을 한 번에 조회합니다")
        void findOptionValuesByOptionGroupIds_WithMultipleGroups_ReturnsAll() {
            // given
            ProductGroupJpaEntity productGroup =
                    persist(ProductGroupJpaEntityFixtures.activeEntity());
            SellerOptionGroupJpaEntity group1 =
                    persistOptionGroup(
                            ProductGroupJpaEntityFixtures.activeOptionGroupEntity(
                                    productGroup.getId()));
            SellerOptionGroupJpaEntity group2 =
                    persistOptionGroup(
                            ProductGroupJpaEntityFixtures.activeOptionGroupEntity(
                                    productGroup.getId()));

            persistOptionValue(
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(group1.getId()));
            persistOptionValue(
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(group2.getId()));
            persistOptionValue(
                    ProductGroupJpaEntityFixtures.activeOptionValueEntity(group2.getId()));

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository()
                            .findOptionValuesByOptionGroupIds(
                                    List.of(group1.getId(), group2.getId()));

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("존재하지 않는 옵션 그룹 ID는 빈 결과를 반환합니다")
        void findOptionValuesByOptionGroupIds_WithNonExistentIds_ReturnsEmpty() {
            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findOptionValuesByOptionGroupIds(List.of(999999L));

            // then
            assertThat(result).isEmpty();
        }
    }
}
