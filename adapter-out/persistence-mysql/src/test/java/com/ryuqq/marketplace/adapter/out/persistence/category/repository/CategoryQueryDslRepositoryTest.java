package com.ryuqq.marketplace.adapter.out.persistence.category.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.category.condition.CategoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * CategoryQueryDslRepositoryTest - 카테고리 QueryDslRepository 통합 테스트.
 *
 * <p>soft-delete(notDeleted) 필터 적용을 우선 검증합니다.
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
@DisplayName("CategoryQueryDslRepository 통합 테스트")
class CategoryQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CategoryQueryDslRepository repository() {
        return new CategoryQueryDslRepository(
                new JPAQueryFactory(entityManager), new CategoryConditionBuilder());
    }

    private CategoryJpaEntity persist(CategoryJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById / existsByCode")
    class FindByIdAndExistsTest {

        @Test
        @DisplayName("미삭제 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            CategoryJpaEntity saved =
                    persist(CategoryJpaEntityFixtures.activeEntityWithCode("CAT_FIND_01"));

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            CategoryJpaEntity deleted =
                    persist(CategoryJpaEntityFixtures.deletedEntityWithCode("CAT_DEL_01"));

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 Entity의 code는 existsByCode에서 false입니다")
        void existsByCode_WithDeleted_ReturnsFalse() {
            String code = "DELETED_CATEGORY_CODE";
            persist(CategoryJpaEntityFixtures.deletedEntityWithCode(code));

            boolean exists = repository().existsByCode(code);

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("활성 Entity의 code는 existsByCode에서 true입니다")
        void existsByCode_WithActive_ReturnsTrue() {
            String code = "ACTIVE_CATEGORY_CODE";
            persist(CategoryJpaEntityFixtures.activeEntityWithCode(code));

            boolean exists = repository().existsByCode(code);

            assertThat(exists).isTrue();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 조회하면 삭제되지 않은 모든 Entity를 반환합니다")
        void findByCriteria_WithDefaultCondition_ReturnsAllNotDeleted() {
            persist(CategoryJpaEntityFixtures.activeEntityWithCode("CAT_DEF_01"));
            persist(CategoryJpaEntityFixtures.inactiveEntityWithCode("CAT_DEF_02"));
            persist(CategoryJpaEntityFixtures.deletedEntityWithCode("CAT_DEF_03"));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria
                            .defaultCriteria();
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE만 조회합니다")
        void findByCriteria_WithActiveStatus_ReturnsOnlyActive() {
            persist(CategoryJpaEntityFixtures.activeEntityWithCode("CAT_ACT_01"));
            persist(CategoryJpaEntityFixtures.inactiveEntityWithCode("CAT_ACT_02"));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.activeOnly();
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("부모 ID 필터로 자식 카테고리만 조회합니다")
        void findByCriteria_WithParentId_ReturnsChildren() {
            Instant now = Instant.now();
            CategoryJpaEntity parent =
                    persist(
                            CategoryJpaEntity.create(
                                    null,
                                    "CAT_PARENT_01",
                                    "부모 카테고리",
                                    "Parent Category",
                                    null,
                                    1,
                                    "/1",
                                    1,
                                    false,
                                    "ACTIVE",
                                    "FASHION",
                                    "CLOTHING",
                                    null,
                                    now,
                                    now,
                                    null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_CHILD_01",
                            "자식 카테고리",
                            "Child Category",
                            parent.getId(),
                            2,
                            "/" + parent.getId() + "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_ROOT_02",
                            "루트2",
                            "Root2",
                            null,
                            1,
                            "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.byParent(
                            parent.getId());
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getParentId()).isEqualTo(parent.getId());
        }

        @Test
        @DisplayName("depth 필터로 특정 깊이의 카테고리만 조회합니다")
        void findByCriteria_WithDepth_ReturnsMatchingDepth() {
            Instant now = Instant.now();
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_DEPTH_01",
                            "깊이1",
                            "Depth1",
                            null,
                            1,
                            "/1",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            CategoryJpaEntity parent =
                    persist(
                            CategoryJpaEntity.create(
                                    null,
                                    "CAT_DEPTH_02",
                                    "부모",
                                    "Parent",
                                    null,
                                    1,
                                    "/2",
                                    1,
                                    false,
                                    "ACTIVE",
                                    "FASHION",
                                    "CLOTHING",
                                    null,
                                    now,
                                    now,
                                    null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_DEPTH_03",
                            "깊이2",
                            "Depth2",
                            parent.getId(),
                            2,
                            "/" + parent.getId() + "/3",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.of(
                            null,
                            2,
                            null,
                            java.util.List.of(
                                    com.ryuqq.marketplace.domain.category.vo.CategoryStatus.ACTIVE),
                            java.util.List.of(),
                            java.util.List.of(),
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.category.query.CategorySortKey
                                            .defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getDepth()).isEqualTo(2);
        }

        @Test
        @DisplayName("leaf 필터로 리프 노드만 조회합니다")
        void findByCriteria_WithLeaf_ReturnsOnlyLeafNodes() {
            Instant now = Instant.now();
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_LEAF_01",
                            "리프노드",
                            "Leaf",
                            null,
                            1,
                            "/1",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_NONLEAF_01",
                            "비리프노드",
                            "NonLeaf",
                            null,
                            1,
                            "/2",
                            1,
                            false,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.of(
                            null,
                            null,
                            true,
                            java.util.List.of(),
                            java.util.List.of(),
                            java.util.List.of(),
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.category.query.CategorySortKey
                                            .defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().isLeaf()).isTrue();
        }

        @Test
        @DisplayName("department 필터로 특정 부문의 카테고리만 조회합니다")
        void findByCriteria_WithDepartment_ReturnsMatchingDepartment() {
            Instant now = Instant.now();
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_DEPT_01",
                            "패션카테고리",
                            "Fashion Category",
                            null,
                            1,
                            "/1",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_DEPT_02",
                            "뷰티카테고리",
                            "Beauty Category",
                            null,
                            1,
                            "/2",
                            1,
                            true,
                            "ACTIVE",
                            "BEAUTY",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            java.util.List.of(),
                            java.util.List.of(
                                    com.ryuqq.marketplace.domain.category.vo.Department.FASHION),
                            java.util.List.of(),
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.category.query.CategorySortKey
                                            .defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getDepartment()).isEqualTo("FASHION");
        }

        @Test
        @DisplayName("categoryGroup 필터로 특정 그룹의 카테고리만 조회합니다")
        void findByCriteria_WithCategoryGroup_ReturnsMatchingGroup() {
            Instant now = Instant.now();
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_GRP_01",
                            "의류",
                            "Clothing",
                            null,
                            1,
                            "/1",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_GRP_02",
                            "신발",
                            "Shoes",
                            null,
                            1,
                            "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "SHOES",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria
                            .byCategoryGroup(
                                    com.ryuqq.marketplace.domain.category.vo.CategoryGroup
                                            .CLOTHING);
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCategoryGroup()).isEqualTo("CLOTHING");
        }

        @Test
        @DisplayName("검색어로 이름을 검색합니다")
        void findByCriteria_WithSearchWord_ReturnsMatchingEntities() {
            Instant now = Instant.now();
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_SEARCH_01",
                            "의류",
                            "Clothing",
                            null,
                            1,
                            "/1",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_SEARCH_02",
                            "신발",
                            "Shoes",
                            null,
                            1,
                            "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "SHOES",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            java.util.List.of(),
                            java.util.List.of(),
                            java.util.List.of(),
                            com.ryuqq.marketplace.domain.category.query.CategorySearchField.NAME_KO,
                            "의류",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.category.query.CategorySortKey
                                            .defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getNameKo()).contains("의류");
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            Instant now = Instant.now();
            for (int i = 0; i < 5; i++) {
                persist(
                        CategoryJpaEntity.create(
                                null,
                                "CAT_PAGE_" + i,
                                "카테고리" + i,
                                "Category" + i,
                                null,
                                1,
                                "/" + i,
                                1,
                                true,
                                "ACTIVE",
                                "FASHION",
                                "CLOTHING",
                                null,
                                now,
                                now,
                                null));
            }

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            java.util.List.of(),
                            java.util.List.of(),
                            java.util.List.of(),
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.of(
                                    com.ryuqq.marketplace.domain.category.query.CategorySortKey
                                            .defaultKey(),
                                    com.ryuqq.marketplace.domain.common.vo.SortDirection.ASC,
                                    com.ryuqq.marketplace.domain.common.vo.PageRequest.of(1, 2)));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 조회하면 삭제되지 않은 Entity 개수를 반환합니다")
        void countByCriteria_WithDefaultCondition_ReturnsCount() {
            persist(CategoryJpaEntityFixtures.activeEntityWithCode("CAT_CNT_01"));
            persist(CategoryJpaEntityFixtures.inactiveEntityWithCode("CAT_CNT_02"));
            persist(CategoryJpaEntityFixtures.deletedEntityWithCode("CAT_CNT_03"));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria
                            .defaultCriteria();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE 개수만 반환합니다")
        void countByCriteria_WithActiveStatus_ReturnsActiveCount() {
            persist(CategoryJpaEntityFixtures.activeEntityWithCode("CAT_CNT_ACT_01"));
            persist(CategoryJpaEntityFixtures.inactiveEntityWithCode("CAT_CNT_ACT_02"));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.activeOnly();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("부모 ID 필터로 자식 개수를 반환합니다")
        void countByCriteria_WithParentId_ReturnsChildrenCount() {
            Instant now = Instant.now();
            CategoryJpaEntity parent =
                    persist(
                            CategoryJpaEntity.create(
                                    null,
                                    "CAT_CNT_PAR_01",
                                    "부모",
                                    "Parent",
                                    null,
                                    1,
                                    "/1",
                                    1,
                                    false,
                                    "ACTIVE",
                                    "FASHION",
                                    "CLOTHING",
                                    null,
                                    now,
                                    now,
                                    null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_CNT_CHILD_01",
                            "자식",
                            "Child",
                            parent.getId(),
                            2,
                            "/" + parent.getId() + "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_CNT_ROOT_02",
                            "루트2",
                            "Root2",
                            null,
                            1,
                            "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.byParent(
                            parent.getId());
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("검색 조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithSearchWord_ReturnsMatchingCount() {
            Instant now = Instant.now();
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_CNT_SRCH_01",
                            "의류",
                            "Clothing",
                            null,
                            1,
                            "/1",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "CLOTHING",
                            null,
                            now,
                            now,
                            null));
            persist(
                    CategoryJpaEntity.create(
                            null,
                            "CAT_CNT_SRCH_02",
                            "신발",
                            "Shoes",
                            null,
                            1,
                            "/2",
                            1,
                            true,
                            "ACTIVE",
                            "FASHION",
                            "SHOES",
                            null,
                            now,
                            now,
                            null));

            var criteria =
                    com.ryuqq.marketplace.domain.category.query.CategorySearchCriteria.of(
                            null,
                            null,
                            null,
                            java.util.List.of(),
                            java.util.List.of(),
                            java.util.List.of(),
                            com.ryuqq.marketplace.domain.category.query.CategorySearchField.NAME_KO,
                            "의류",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.category.query.CategorySortKey
                                            .defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }
    }
}
