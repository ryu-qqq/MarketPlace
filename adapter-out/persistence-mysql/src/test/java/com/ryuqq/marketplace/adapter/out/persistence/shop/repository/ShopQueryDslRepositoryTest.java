package com.ryuqq.marketplace.adapter.out.persistence.shop.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.condition.ShopConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
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
 * ShopQueryDslRepositoryTest - Shop QueryDslRepository 통합 테스트.
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
@DisplayName("ShopQueryDslRepository 통합 테스트")
class ShopQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ShopQueryDslRepository repository() {
        return new ShopQueryDslRepository(
                new JPAQueryFactory(entityManager), new ShopConditionBuilder());
    }

    private ShopJpaEntity persist(ShopJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            ShopJpaEntity saved = persist(ShopJpaEntityFixtures.newEntity());

            Optional<ShopJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            ShopJpaEntity deleted = persist(ShopJpaEntityFixtures.newDeletedEntity());

            Optional<ShopJpaEntity> result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            Optional<ShopJpaEntity> result = repository().findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("여러 ID로 미삭제 Entity 목록을 조회합니다")
        void findByIds_WithValidIds_ReturnsEntities() {
            ShopJpaEntity saved1 = persist(ShopJpaEntityFixtures.newEntity());
            ShopJpaEntity saved2 = persist(ShopJpaEntityFixtures.newEntity());

            List<ShopJpaEntity> result =
                    repository().findByIds(List.of(saved1.getId(), saved2.getId()));

            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ShopJpaEntity::getId)
                    .containsExactlyInAnyOrder(saved1.getId(), saved2.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findByIds 결과에서 제외됩니다")
        void findByIds_WithDeletedEntity_ExcludesDeleted() {
            ShopJpaEntity active = persist(ShopJpaEntityFixtures.newEntity());
            ShopJpaEntity deleted = persist(ShopJpaEntityFixtures.newDeletedEntity());

            List<ShopJpaEntity> result =
                    repository().findByIds(List.of(active.getId(), deleted.getId()));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(active.getId());
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 결과를 반환합니다")
        void findByIds_WithEmptyIds_ReturnsEmpty() {
            List<ShopJpaEntity> result = repository().findByIds(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null ID 목록으로 조회 시 빈 결과를 반환합니다")
        void findByIds_WithNullIds_ReturnsEmpty() {
            List<ShopJpaEntity> result = repository().findByIds(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 결과를 반환합니다")
        void findByIds_WithNonExistentIds_ReturnsEmpty() {
            List<ShopJpaEntity> result = repository().findByIds(List.of(999L, 998L));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndAccountId")
    class ExistsBySalesChannelIdAndAccountIdTest {

        @Test
        @DisplayName("미삭제 Entity의 salesChannelId와 accountId로 존재 확인됩니다")
        void existsBySalesChannelIdAndAccountId_WithNotDeleted_ReturnsTrue() {
            // given
            String accountId = "unique-account-test";
            ShopJpaEntity saved =
                    persist(ShopJpaEntityFixtures.activeEntityWithAccountId(accountId));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndAccountId(
                                    ShopJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID, accountId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity의 salesChannelId와 accountId는 false를 반환합니다")
        void existsBySalesChannelIdAndAccountId_WithDeleted_ReturnsFalse() {
            // given
            String accountId = "deleted-account";
            persist(ShopJpaEntityFixtures.deletedEntityWithAccountId(accountId));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndAccountId(
                                    ShopJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID, accountId);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 계정은 false를 반환합니다")
        void existsBySalesChannelIdAndAccountId_WithNonExistent_ReturnsFalse() {
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndAccountId(
                                    ShopJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    "non-existent-account");

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySalesChannelIdAndAccountIdExcluding")
    class ExistsBySalesChannelIdAndAccountIdExcludingTest {

        @Test
        @DisplayName("제외 ID가 아닌 계정이 존재하면 true를 반환합니다")
        void existsBySalesChannelIdAndAccountIdExcluding_WithDifferentId_ReturnsTrue() {
            // given
            String accountId = "unique-account-excluding";
            ShopJpaEntity existing =
                    persist(ShopJpaEntityFixtures.activeEntityWithAccountId(accountId));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndAccountIdExcluding(
                                    ShopJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    accountId,
                                    999L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("제외 ID의 계정은 false를 반환합니다")
        void existsBySalesChannelIdAndAccountIdExcluding_WithExcludedId_ReturnsFalse() {
            // given
            String accountId = "account-to-exclude";
            ShopJpaEntity existing =
                    persist(ShopJpaEntityFixtures.activeEntityWithAccountId(accountId));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndAccountIdExcluding(
                                    ShopJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    accountId,
                                    existing.getId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 제외 조회에서도 false를 반환합니다")
        void existsBySalesChannelIdAndAccountIdExcluding_WithDeletedEntity_ReturnsFalse() {
            // given
            String accountId = "deleted-account-excluding";
            persist(ShopJpaEntityFixtures.deletedEntityWithAccountId(accountId));

            // when
            boolean result =
                    repository()
                            .existsBySalesChannelIdAndAccountIdExcluding(
                                    ShopJpaEntityFixtures.DEFAULT_SALES_CHANNEL_ID,
                                    accountId,
                                    999L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("미삭제 Entity만 카운트에 포함됩니다")
        void countByCriteria_OnlyCountsNotDeleted() {
            // given
            persist(ShopJpaEntityFixtures.newEntity());
            persist(ShopJpaEntityFixtures.newEntity());
            persist(ShopJpaEntityFixtures.newDeletedEntity());

            var criteria = createDefaultCriteria();

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("미삭제 Entity 목록을 조회합니다")
        void findByCriteria_ReturnsNotDeletedEntities() {
            // given
            ShopJpaEntity active1 = persist(ShopJpaEntityFixtures.newEntity());
            ShopJpaEntity active2 = persist(ShopJpaEntityFixtures.newEntity());
            persist(ShopJpaEntityFixtures.newDeletedEntity());

            var criteria = createDefaultCriteria();

            // when
            List<ShopJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result)
                    .extracting(ShopJpaEntity::getId)
                    .contains(active1.getId(), active2.getId());
            assertThat(result).noneMatch(e -> e.getDeletedAt() != null);
        }
    }

    private com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria createDefaultCriteria() {
        return com.ryuqq.marketplace.domain.shop.query.ShopSearchCriteria.defaultCriteria();
    }
}
