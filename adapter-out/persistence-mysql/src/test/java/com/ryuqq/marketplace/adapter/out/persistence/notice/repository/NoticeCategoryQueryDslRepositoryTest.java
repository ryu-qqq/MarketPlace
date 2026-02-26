package com.ryuqq.marketplace.adapter.out.persistence.notice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.NoticeFieldJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.notice.condition.NoticeCategoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.notice.entity.NoticeFieldJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySearchCriteria;
import com.ryuqq.marketplace.domain.notice.query.NoticeCategorySortKey;
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
 * NoticeCategoryQueryDslRepositoryTest - NoticeCategory QueryDslRepository 통합 테스트.
 *
 * <p>NoticeCategory는 soft-delete를 적용하지 않으며 active 필드로 활성 여부를 관리합니다. findByTargetCategoryGroup,
 * findFieldsByCategoryId 등 고유 메서드를 검증합니다.
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
@DisplayName("NoticeCategoryQueryDslRepository 통합 테스트")
class NoticeCategoryQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private NoticeCategoryQueryDslRepository repository() {
        return new NoticeCategoryQueryDslRepository(
                new JPAQueryFactory(entityManager), new NoticeCategoryConditionBuilder());
    }

    private NoticeCategoryJpaEntity persist(NoticeCategoryJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private NoticeFieldJpaEntity persistField(NoticeFieldJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private NoticeCategorySearchCriteria defaultCriteria() {
        return new NoticeCategorySearchCriteria(
                null, null, null, QueryContext.defaultOf(NoticeCategorySortKey.defaultKey()));
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 Entity를 ID로 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            NoticeCategoryJpaEntity saved = persist(NoticeCategoryJpaEntityFixtures.newEntity());

            Optional<NoticeCategoryJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            Optional<NoticeCategoryJpaEntity> result = repository().findById(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("비활성 Entity도 ID로 조회됩니다")
        void findById_WithInactiveEntity_ReturnsEntity() {
            NoticeCategoryJpaEntity inactive =
                    persist(NoticeCategoryJpaEntityFixtures.newInactiveEntity());

            Optional<NoticeCategoryJpaEntity> result = repository().findById(inactive.getId());

            assertThat(result).isPresent();
            assertThat(result.get().isActive()).isFalse();
        }
    }

    @Nested
    @DisplayName("findByTargetCategoryGroup")
    class FindByTargetCategoryGroupTest {

        @Test
        @DisplayName("존재하는 targetCategoryGroup으로 Entity를 조회합니다")
        void findByTargetCategoryGroup_WithExistingGroup_ReturnsEntity() {
            // given
            String uniqueTargetGroup = "UNIQUE_GROUP_REPO_TEST";
            NoticeCategoryJpaEntity entity =
                    persist(
                            NoticeCategoryJpaEntity.create(
                                    null,
                                    "CODE_REPO_TEST",
                                    "테스트",
                                    "Test",
                                    uniqueTargetGroup,
                                    true,
                                    java.time.Instant.now(),
                                    java.time.Instant.now()));

            // when
            Optional<NoticeCategoryJpaEntity> result =
                    repository().findByTargetCategoryGroup(uniqueTargetGroup);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getTargetCategoryGroup()).isEqualTo(uniqueTargetGroup);
        }

        @Test
        @DisplayName("존재하지 않는 targetCategoryGroup은 빈 결과를 반환합니다")
        void findByTargetCategoryGroup_WithNonExistentGroup_ReturnsEmpty() {
            Optional<NoticeCategoryJpaEntity> result =
                    repository().findByTargetCategoryGroup("NON_EXISTENT_GROUP");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findFieldsByCategoryId")
    class FindFieldsByCategoryIdTest {

        @Test
        @DisplayName("카테고리 ID에 해당하는 필드 목록을 sortOrder 오름차순으로 조회합니다")
        void findFieldsByCategoryId_WithExistingCategoryId_ReturnsFieldsInOrder() {
            // given
            NoticeCategoryJpaEntity category = persist(NoticeCategoryJpaEntityFixtures.newEntity());
            Long categoryId = category.getId();

            NoticeFieldJpaEntity field1 =
                    persistField(
                            NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId));
            NoticeFieldJpaEntity field2 =
                    persistField(
                            NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(categoryId));

            // when
            List<NoticeFieldJpaEntity> result = repository().findFieldsByCategoryId(categoryId);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(f -> f.getNoticeCategoryId().equals(categoryId));
        }

        @Test
        @DisplayName("존재하지 않는 카테고리 ID는 빈 목록을 반환합니다")
        void findFieldsByCategoryId_WithNonExistentCategoryId_ReturnsEmpty() {
            List<NoticeFieldJpaEntity> result = repository().findFieldsByCategoryId(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findFieldsByCategoryIds")
    class FindFieldsByCategoryIdsTest {

        @Test
        @DisplayName("여러 카테고리 ID에 해당하는 필드 목록을 조회합니다")
        void findFieldsByCategoryIds_WithMultipleCategoryIds_ReturnsFields() {
            // given
            NoticeCategoryJpaEntity cat1 = persist(NoticeCategoryJpaEntityFixtures.newEntity());
            NoticeCategoryJpaEntity cat2 = persist(NoticeCategoryJpaEntityFixtures.newEntity());

            persistField(NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(cat1.getId()));
            persistField(NoticeFieldJpaEntityFixtures.fieldEntityWithCategoryId(cat2.getId()));

            // when
            List<NoticeFieldJpaEntity> result =
                    repository().findFieldsByCategoryIds(List.of(cat1.getId(), cat2.getId()));

            // then
            assertThat(result).isNotEmpty();
            assertThat(result)
                    .allMatch(
                            f ->
                                    f.getNoticeCategoryId().equals(cat1.getId())
                                            || f.getNoticeCategoryId().equals(cat2.getId()));
        }

        @Test
        @DisplayName("빈 카테고리 ID 목록은 빈 결과를 반환합니다")
        void findFieldsByCategoryIds_WithEmptyList_ReturnsEmpty() {
            List<NoticeFieldJpaEntity> result = repository().findFieldsByCategoryIds(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 카테고리 ID 목록은 빈 결과를 반환합니다")
        void findFieldsByCategoryIds_WithNullList_ReturnsEmpty() {
            List<NoticeFieldJpaEntity> result = repository().findFieldsByCategoryIds(null);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 전체 Entity 목록을 조회합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsAllEntities() {
            // given
            NoticeCategoryJpaEntity cat1 = persist(NoticeCategoryJpaEntityFixtures.newEntity());
            NoticeCategoryJpaEntity cat2 = persist(NoticeCategoryJpaEntityFixtures.newEntity());

            // when
            List<NoticeCategoryJpaEntity> result = repository().findByCriteria(defaultCriteria());

            // then
            assertThat(result)
                    .extracting(NoticeCategoryJpaEntity::getId)
                    .contains(cat1.getId(), cat2.getId());
        }

        @Test
        @DisplayName("active=true 필터로 활성 Entity만 조회합니다")
        void findByCriteria_WithActiveFilter_ReturnsOnlyActiveEntities() {
            // given
            persist(NoticeCategoryJpaEntityFixtures.newEntity());
            persist(NoticeCategoryJpaEntityFixtures.newInactiveEntity());

            NoticeCategorySearchCriteria criteria =
                    new NoticeCategorySearchCriteria(
                            true,
                            null,
                            null,
                            QueryContext.defaultOf(NoticeCategorySortKey.defaultKey()));

            // when
            List<NoticeCategoryJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(NoticeCategoryJpaEntity::isActive);
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("전체 Entity 개수를 반환합니다")
        void countByCriteria_ReturnsCorrectCount() {
            // given
            persist(NoticeCategoryJpaEntityFixtures.newEntity());
            persist(NoticeCategoryJpaEntityFixtures.newEntity());

            // when
            long count = repository().countByCriteria(defaultCriteria());

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("Entity가 없으면 0을 반환합니다")
        void countByCriteria_WithNoEntities_ReturnsZero() {
            long count = repository().countByCriteria(defaultCriteria());

            assertThat(count).isZero();
        }
    }
}
