package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.condition.CanonicalOptionGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
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
 * CanonicalOptionGroupQueryDslRepositoryTest - 캐노니컬 옵션 그룹 QueryDslRepository 통합 테스트.
 *
 * <p>기본 조회 및 검색 기능을 검증합니다.
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
@DisplayName("CanonicalOptionGroupQueryDslRepository 통합 테스트")
class CanonicalOptionGroupQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CanonicalOptionGroupQueryDslRepository repository() {
        return new CanonicalOptionGroupQueryDslRepository(
                new JPAQueryFactory(entityManager), new CanonicalOptionGroupConditionBuilder());
    }

    private CanonicalOptionGroupJpaEntity persist(CanonicalOptionGroupJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 Entity를 findById로 조회합니다")
        void findById_WithExisting_ReturnsEntity() {
            CanonicalOptionGroupJpaEntity saved =
                    persist(CanonicalOptionGroupJpaEntityFixtures.newEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExisting_ReturnsEmpty() {
            var result = repository().findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 조회하면 모든 Entity를 반환합니다")
        void findByCriteria_WithDefaultCondition_ReturnsAll() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COLOR"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("SIZE"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("MATERIAL"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("활성 필터로 조회하면 활성 Entity만 반환합니다")
        void findByCriteria_WithActiveFilter_ReturnsOnlyActive() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COLOR"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("SIZE"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("MATERIAL"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            true,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(CanonicalOptionGroupJpaEntity::isActive);
        }

        @Test
        @DisplayName("검색어로 코드를 검색합니다")
        void findByCriteria_WithCodeSearch_ReturnsMatchingEntities() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COLOR_RED"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("SIZE_LARGE"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            null,
                            "CODE",
                            "COLOR",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCode()).contains("COLOR");
        }

        @Test
        @DisplayName("검색어로 이름을 검색합니다")
        void findByCriteria_WithNameSearch_ReturnsMatchingEntities() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithName("색상", "Color"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithName("사이즈", "Size"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            null,
                            "NAME_KO",
                            "색상",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getNameKo()).contains("색상");
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            for (int i = 0; i < 5; i++) {
                persist(
                        CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                "PAGING_TEST_" + i));
            }

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.of(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey(),
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
        @DisplayName("기본 조건으로 조회하면 전체 Entity 개수를 반환합니다")
        void countByCriteria_WithDefaultCondition_ReturnsCount() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COUNT_TEST_1"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COUNT_TEST_2"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("COUNT_TEST_3"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("활성 필터로 조회하면 활성 Entity 개수만 반환합니다")
        void countByCriteria_WithActiveFilter_ReturnsActiveCount() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("ACTIVE_COUNT_1"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("ACTIVE_COUNT_2"));
            persist(
                    CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode(
                            "INACTIVE_COUNT_1"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            true,
                            null,
                            null,
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("검색 조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithSearchCondition_ReturnsMatchingCount() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithName("색상1", "Color1"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithName("사이즈1", "Size1"));

            var criteria =
                    new com.ryuqq.marketplace.domain.canonicaloption.query
                            .CanonicalOptionGroupSearchCriteria(
                            null,
                            "NAME_KO",
                            "색상",
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                    com.ryuqq.marketplace.domain.canonicaloption.query
                                            .CanonicalOptionGroupSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }
    }
}
