package com.ryuqq.marketplace.adapter.out.persistence.seller.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.condition.SellerConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
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
 * SellerQueryDslRepositoryTest - 셀러 QueryDslRepository 통합 테스트.
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
@DisplayName("SellerQueryDslRepository 통합 테스트")
class SellerQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerQueryDslRepository repository() {
        return new SellerQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerConditionBuilder());
    }

    private SellerJpaEntity persist(SellerJpaEntity entity) {
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
            SellerJpaEntity saved = persist(SellerJpaEntityFixtures.newEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            SellerJpaEntity deleted = persist(SellerJpaEntityFixtures.newDeletedEntity());

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("미삭제 Entity 목록을 ID 목록으로 조회합니다")
        void findByIds_WithNotDeletedEntities_ReturnsEntities() {
            // given
            SellerJpaEntity seller1 = persist(SellerJpaEntityFixtures.newEntity());
            SellerJpaEntity seller2 = persist(SellerJpaEntityFixtures.newEntity());
            persist(SellerJpaEntityFixtures.newDeletedEntity());

            // when
            var result =
                    repository().findByIds(java.util.List.of(seller1.getId(), seller2.getId()));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(SellerJpaEntity::getId)
                    .containsExactlyInAnyOrder(seller1.getId(), seller2.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 제외하고 조회합니다")
        void findByIds_WithDeletedEntity_ExcludesDeleted() {
            // given
            SellerJpaEntity active = persist(SellerJpaEntityFixtures.newEntity());
            SellerJpaEntity deleted = persist(SellerJpaEntityFixtures.newDeletedEntity());

            // when
            var result = repository().findByIds(java.util.List.of(active.getId(), deleted.getId()));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(active.getId());
        }
    }

    @Nested
    @DisplayName("existsById")
    class ExistsByIdTest {

        @Test
        @DisplayName("미삭제 Entity는 existsById로 존재 확인됩니다")
        void existsById_WithNotDeleted_ReturnsTrue() {
            // given
            SellerJpaEntity saved = persist(SellerJpaEntityFixtures.newEntity());

            // when
            boolean result = repository().existsById(saved.getId());

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity는 existsById로 false를 반환합니다")
        void existsById_WithDeleted_ReturnsFalse() {
            // given
            SellerJpaEntity deleted = persist(SellerJpaEntityFixtures.newDeletedEntity());

            // when
            boolean result = repository().existsById(deleted.getId());

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환합니다")
        void existsById_WithNonExistentId_ReturnsFalse() {
            // when
            boolean result = repository().existsById(999L);

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySellerName")
    class ExistsBySellerNameTest {

        @Test
        @DisplayName("미삭제 Entity의 셀러명은 존재 확인됩니다")
        void existsBySellerName_WithNotDeleted_ReturnsTrue() {
            // given
            String sellerName = "유니크셀러명";
            persist(SellerJpaEntityFixtures.activeEntityWithName(sellerName, "디스플레이명"));

            // when
            boolean result = repository().existsBySellerName(sellerName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity의 셀러명은 false를 반환합니다")
        void existsBySellerName_WithDeleted_ReturnsFalse() {
            // given
            String sellerName = "삭제된셀러";
            persist(SellerJpaEntityFixtures.deletedEntityWithName(sellerName, "디스플레이명"));

            // when
            boolean result = repository().existsBySellerName(sellerName);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 셀러명은 false를 반환합니다")
        void existsBySellerName_WithNonExistent_ReturnsFalse() {
            // when
            boolean result = repository().existsBySellerName("존재하지않는셀러");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("existsBySellerNameExcluding")
    class ExistsBySellerNameExcludingTest {

        @Test
        @DisplayName("제외 ID가 아닌 셀러명이 존재하면 true를 반환합니다")
        void existsBySellerNameExcluding_WithDifferentId_ReturnsTrue() {
            // given
            String sellerName = "중복셀러명";
            SellerJpaEntity existing =
                    persist(SellerJpaEntityFixtures.activeEntityWithName(sellerName, "디스플레이1"));

            // when
            boolean result = repository().existsBySellerNameExcluding(sellerName, 999L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("제외 ID의 셀러명은 false를 반환합니다")
        void existsBySellerNameExcluding_WithExcludedId_ReturnsFalse() {
            // given
            String sellerName = "셀러명";
            SellerJpaEntity existing =
                    persist(SellerJpaEntityFixtures.activeEntityWithName(sellerName, "디스플레이"));

            // when
            boolean result = repository().existsBySellerNameExcluding(sellerName, existing.getId());

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findIdByOrganizationId")
    class FindIdByOrganizationIdTest {

        @Test
        @DisplayName("미삭제 Entity의 organizationId로 셀러 ID를 조회합니다")
        void findIdByOrganizationId_WithNotDeleted_ReturnsSellerId() {
            // given
            String orgId = "org-test-123";
            SellerJpaEntity saved =
                    persist(SellerJpaEntityFixtures.activeEntityWithOrganization(orgId));

            // when
            var result = repository().findIdByOrganizationId(orgId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity의 organizationId는 조회되지 않습니다")
        void findIdByOrganizationId_WithDeleted_ReturnsEmpty() {
            // given
            String orgId = "org-deleted-456";
            SellerJpaEntity deleted = persist(SellerJpaEntityFixtures.newDeletedEntity());

            // when
            var result = repository().findIdByOrganizationId(orgId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 organizationId는 빈 결과를 반환합니다")
        void findIdByOrganizationId_WithNonExistent_ReturnsEmpty() {
            // when
            var result = repository().findIdByOrganizationId("non-existent-org");

            // then
            assertThat(result).isEmpty();
        }
    }
}
