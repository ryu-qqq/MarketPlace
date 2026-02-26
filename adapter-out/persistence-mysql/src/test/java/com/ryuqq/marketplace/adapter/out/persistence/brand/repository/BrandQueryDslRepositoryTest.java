package com.ryuqq.marketplace.adapter.out.persistence.brand.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brand.condition.BrandConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * BrandQueryDslRepositoryTest - 브랜드 QueryDslRepository 통합 테스트.
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
@DisplayName("BrandQueryDslRepository 통합 테스트")
class BrandQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private BrandQueryDslRepository repository() {
        return new BrandQueryDslRepository(
                new JPAQueryFactory(entityManager), new BrandConditionBuilder());
    }

    private BrandJpaEntity persist(BrandJpaEntity entity) {
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
            BrandJpaEntity saved = persist(BrandJpaEntityFixtures.newEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            BrandJpaEntity deleted = persist(BrandJpaEntityFixtures.newDeletedEntity());

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 Entity의 code는 existsByCode에서 false입니다")
        void existsByCode_WithDeleted_ReturnsFalse() {
            String code = "DELETED_BRAND_CODE";
            persist(BrandJpaEntityFixtures.deletedEntityWithCode(code));

            boolean exists = repository().existsByCode(code);

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("활성 Entity의 code는 existsByCode에서 true입니다")
        void existsByCode_WithActive_ReturnsTrue() {
            String code = "ACTIVE_BRAND_CODE";
            persist(BrandJpaEntityFixtures.activeEntityWithCode(code));

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
            persist(BrandJpaEntityFixtures.activeEntityWithCode("ACTIVE_BRAND_1"));
            persist(BrandJpaEntityFixtures.inactiveEntityWithCode("INACTIVE_BRAND_1"));
            persist(BrandJpaEntityFixtures.deletedEntityWithCode("DELETED_BRAND_1"));

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.defaultCriteria();
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE만 조회합니다")
        void findByCriteria_WithActiveStatus_ReturnsOnlyActive() {
            persist(BrandJpaEntityFixtures.activeEntityWithCode("ACTIVE_BRAND_2"));
            persist(BrandJpaEntityFixtures.inactiveEntityWithCode("INACTIVE_BRAND_2"));

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.activeOnly();
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("검색어로 이름을 검색합니다")
        void findByCriteria_WithSearchWord_ReturnsMatchingEntities() {
            persist(BrandJpaEntityFixtures.activeEntityWithName("나이키코리아", "Nike Korea", "나이키"));
            persist(BrandJpaEntityFixtures.activeEntityWithName("아디다스코리아", "Adidas Korea", "아디다스"));

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.of(
                            java.util.List.of(),
                            com.ryuqq.marketplace.domain.brand.query.BrandSearchField.NAME_KO,
                            "나이키",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.brand.query.BrandSortKey
                                            .defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getNameKo()).contains("나이키");
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            for (int i = 0; i < 5; i++) {
                persist(BrandJpaEntityFixtures.activeEntityWithCode("PAGING_BRAND_" + i));
            }

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.of(
                            java.util.List.of(),
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.of(
                                    com.ryuqq.marketplace.domain.brand.query.BrandSortKey
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
            persist(BrandJpaEntityFixtures.activeEntityWithCode("COUNT_ACTIVE_1"));
            persist(BrandJpaEntityFixtures.inactiveEntityWithCode("COUNT_INACTIVE_1"));
            persist(BrandJpaEntityFixtures.deletedEntityWithCode("COUNT_DELETED_1"));

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.defaultCriteria();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE 개수만 반환합니다")
        void countByCriteria_WithActiveStatus_ReturnsActiveCount() {
            persist(BrandJpaEntityFixtures.activeEntityWithCode("COUNT_ACTIVE_2"));
            persist(BrandJpaEntityFixtures.inactiveEntityWithCode("COUNT_INACTIVE_2"));

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.activeOnly();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("검색 조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithSearchWord_ReturnsMatchingCount() {
            persist(BrandJpaEntityFixtures.activeEntityWithName("나이키제품", "Nike Product", "나이키"));
            persist(
                    BrandJpaEntityFixtures.activeEntityWithName(
                            "아디다스제품", "Adidas Product", "아디다스"));

            var criteria =
                    com.ryuqq.marketplace.domain.brand.query.BrandSearchCriteria.of(
                            java.util.List.of(),
                            com.ryuqq.marketplace.domain.brand.query.BrandSearchField.NAME_KO,
                            "나이키",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.brand.query.BrandSortKey
                                            .defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }
    }
}
