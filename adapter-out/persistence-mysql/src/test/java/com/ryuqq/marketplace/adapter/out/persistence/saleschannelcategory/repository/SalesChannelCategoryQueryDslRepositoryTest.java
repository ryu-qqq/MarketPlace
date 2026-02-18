package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.condition.SalesChannelCategoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySearchCriteria;
import com.ryuqq.marketplace.domain.saleschannelcategory.query.SalesChannelCategorySortKey;
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
 * SalesChannelCategoryQueryDslRepositoryTest - SalesChannelCategory QueryDslRepository 통합 테스트.
 *
 * <p>SalesChannelCategory는 soft-delete를 적용하지 않으므로 조회, 존재 여부, findAllByIds 동작을 검증합니다.
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
@DisplayName("SalesChannelCategoryQueryDslRepository 통합 테스트")
class SalesChannelCategoryQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SalesChannelCategoryQueryDslRepository repository() {
        return new SalesChannelCategoryQueryDslRepository(
                new JPAQueryFactory(entityManager), new SalesChannelCategoryConditionBuilder());
    }

    private SalesChannelCategoryJpaEntity persist(SalesChannelCategoryJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private SalesChannelCategorySearchCriteria defaultCriteria() {
        return SalesChannelCategorySearchCriteria.of(
                List.of(),
                List.of(),
                null,
                null,
                QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 Entity를 ID로 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            SalesChannelCategoryJpaEntity saved =
                    persist(SalesChannelCategoryJpaEntityFixtures.newEntity());

            Optional<SalesChannelCategoryJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            Optional<SalesChannelCategoryJpaEntity> result = repository().findById(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("비활성 Entity도 ID로 조회됩니다")
        void findById_WithInactiveEntity_ReturnsEntity() {
            SalesChannelCategoryJpaEntity inactive =
                    persist(SalesChannelCategoryJpaEntityFixtures.inactiveEntity());

            Optional<SalesChannelCategoryJpaEntity> result =
                    repository().findById(inactive.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIdsTest {

        @Test
        @DisplayName("여러 ID로 Entity 목록을 조회합니다")
        void findAllByIds_WithMultipleIds_ReturnsEntities() {
            // given
            SalesChannelCategoryJpaEntity cat1 =
                    persist(SalesChannelCategoryJpaEntityFixtures.newEntity());
            SalesChannelCategoryJpaEntity cat2 =
                    persist(SalesChannelCategoryJpaEntityFixtures.newEntity());

            // when
            List<SalesChannelCategoryJpaEntity> result =
                    repository().findAllByIds(List.of(cat1.getId(), cat2.getId()));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(SalesChannelCategoryJpaEntity::getId)
                    .containsExactlyInAnyOrder(cat1.getId(), cat2.getId());
        }

        @Test
        @DisplayName("빈 ID 목록은 빈 결과를 반환합니다")
        void findAllByIds_WithEmptyList_ReturnsEmpty() {
            List<SalesChannelCategoryJpaEntity> result = repository().findAllByIds(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 결과에 포함되지 않습니다")
        void findAllByIds_WithNonExistentId_ReturnsOnlyExisting() {
            // given
            SalesChannelCategoryJpaEntity cat =
                    persist(SalesChannelCategoryJpaEntityFixtures.newEntity());

            // when
            List<SalesChannelCategoryJpaEntity> result =
                    repository().findAllByIds(List.of(cat.getId(), 999L));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(cat.getId());
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndExternalCode")
    class ExistsBySalesChannelIdAndExternalCodeTest {

        @Test
        @DisplayName("존재하는 salesChannelId와 externalCategoryCode 조합은 true를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithExisting_ReturnsTrue() {
            // given
            Long salesChannelId = SalesChannelCategoryJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID;
            String externalCode = "UNIQUE-CAT-CODE";
            persist(
                    SalesChannelCategoryJpaEntityFixtures.entityWithExternalCode(
                            salesChannelId, externalCode));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndExternalCode(salesChannelId, externalCode);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 조합은 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithNonExistent_ReturnsFalse() {
            boolean result =
                    repository().existsBySalesChannelIdAndExternalCode(999L, "NON-EXISTENT");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("다른 salesChannelId의 코드는 false를 반환합니다")
        void existsBySalesChannelIdAndExternalCode_WithDifferentSalesChannelId_ReturnsFalse() {
            // given
            String externalCode = "CAT-CODE-CHANNEL";
            persist(
                    SalesChannelCategoryJpaEntityFixtures.entityWithExternalCode(
                            SalesChannelCategoryJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                            externalCode));

            // when
            boolean result = repository().existsBySalesChannelIdAndExternalCode(999L, externalCode);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 Entity 목록을 조회합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsEntities() {
            // given
            SalesChannelCategoryJpaEntity cat1 =
                    persist(SalesChannelCategoryJpaEntityFixtures.newEntity());
            SalesChannelCategoryJpaEntity cat2 =
                    persist(SalesChannelCategoryJpaEntityFixtures.newEntity());

            // when
            List<SalesChannelCategoryJpaEntity> result =
                    repository().findByCriteria(defaultCriteria());

            // then
            assertThat(result)
                    .extracting(SalesChannelCategoryJpaEntity::getId)
                    .contains(cat1.getId(), cat2.getId());
        }

        @Test
        @DisplayName("특정 salesChannelId로 필터링합니다")
        void findByCriteria_WithSalesChannelIdFilter_ReturnsFilteredEntities() {
            // given
            Long targetChannelId = 10L;
            SalesChannelCategoryJpaEntity targetCat =
                    persist(
                            SalesChannelCategoryJpaEntityFixtures.entityWithSalesChannel(
                                    targetChannelId));
            persist(SalesChannelCategoryJpaEntityFixtures.entityWithSalesChannel(20L));

            SalesChannelCategorySearchCriteria criteria =
                    SalesChannelCategorySearchCriteria.of(
                            List.of(targetChannelId),
                            List.of(),
                            null,
                            null,
                            QueryContext.defaultOf(SalesChannelCategorySortKey.defaultKey()));

            // when
            List<SalesChannelCategoryJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result)
                    .extracting(SalesChannelCategoryJpaEntity::getId)
                    .contains(targetCat.getId());
            assertThat(result).allMatch(e -> e.getSalesChannelId().equals(targetChannelId));
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("전체 Entity 개수를 반환합니다")
        void countByCriteria_ReturnsCorrectCount() {
            // given
            persist(SalesChannelCategoryJpaEntityFixtures.newEntity());
            persist(SalesChannelCategoryJpaEntityFixtures.newEntity());

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
