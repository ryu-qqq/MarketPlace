package com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.RefundPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.condition.RefundPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.refundpolicy.entity.RefundPolicyJpaEntity;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * RefundPolicyQueryDslRepositoryTest - 반품 정책 QueryDslRepository 통합 테스트.
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
@DisplayName("RefundPolicyQueryDslRepository 통합 테스트")
class RefundPolicyQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private RefundPolicyQueryDslRepository repository() {
        return new RefundPolicyQueryDslRepository(
                new JPAQueryFactory(entityManager), new RefundPolicyConditionBuilder());
    }

    private RefundPolicyJpaEntity persist(RefundPolicyJpaEntity entity) {
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
            RefundPolicyJpaEntity saved =
                    persist(RefundPolicyJpaEntityFixtures.newActiveEntity(1L));

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            RefundPolicyJpaEntity deleted =
                    persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(1L));

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("ID 목록으로 여러 Entity를 조회합니다")
        void findByIds_WithMultipleIds_ReturnsEntities() {
            RefundPolicyJpaEntity saved1 =
                    persist(RefundPolicyJpaEntityFixtures.newActiveEntity(1L));
            RefundPolicyJpaEntity saved2 =
                    persist(RefundPolicyJpaEntityFixtures.newActiveEntity(1L));

            var result = repository().findByIds(List.of(saved1.getId(), saved2.getId()));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 ID 목록 조회에서 제외됩니다")
        void findByIds_WithDeletedEntity_ExcludesDeleted() {
            RefundPolicyJpaEntity saved =
                    persist(RefundPolicyJpaEntityFixtures.newActiveEntity(1L));
            RefundPolicyJpaEntity deleted =
                    persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(1L));

            var result = repository().findByIds(List.of(saved.getId(), deleted.getId()));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    @DisplayName("findDefaultBySellerId")
    class FindDefaultBySellerIdTest {

        @Test
        @DisplayName("셀러의 기본 정책을 조회합니다")
        void findDefaultBySellerId_WithDefaultPolicy_ReturnsPolicy() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newDefaultEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newActiveEntityWithName(sellerId, "추가 정책"));

            var result = repository().findDefaultBySellerId(sellerId);

            assertThat(result).isPresent();
            assertThat(result.get().isDefaultPolicy()).isTrue();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
        }

        @Test
        @DisplayName("기본 정책이 없으면 Empty를 반환합니다")
        void findDefaultBySellerId_WithoutDefaultPolicy_ReturnsEmpty() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntityWithName(sellerId, "일반 정책"));

            var result = repository().findDefaultBySellerId(sellerId);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 기본 정책은 조회되지 않습니다")
        void findDefaultBySellerId_WithDeletedDefaultPolicy_ReturnsEmpty() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(sellerId));

            var result = repository().findDefaultBySellerId(sellerId);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndId")
    class FindBySellerIdAndIdTest {

        @Test
        @DisplayName("셀러 ID와 정책 ID로 정책을 조회합니다")
        void findBySellerIdAndId_WithMatchingSeller_ReturnsPolicy() {
            Long sellerId = 1L;
            RefundPolicyJpaEntity saved =
                    persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            var result = repository().findBySellerIdAndId(sellerId, saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("다른 셀러의 정책은 조회되지 않습니다")
        void findBySellerIdAndId_WithDifferentSeller_ReturnsEmpty() {
            Long sellerId = 1L;
            RefundPolicyJpaEntity saved =
                    persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            var result = repository().findBySellerIdAndId(999L, saved.getId());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 정책은 조회되지 않습니다")
        void findBySellerIdAndId_WithDeletedPolicy_ReturnsEmpty() {
            Long sellerId = 1L;
            RefundPolicyJpaEntity deleted =
                    persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(sellerId));

            var result = repository().findBySellerIdAndId(sellerId, deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("특정 셀러의 모든 정책을 조회합니다")
        void findByCriteria_WithSellerId_ReturnsSellerPolicies() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(2L));

            var criteria =
                    com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria
                            .defaultCriteria(
                                    com.ryuqq.marketplace.domain.seller.id.SellerId.of(sellerId));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(p -> p.getSellerId().equals(sellerId));
        }

        @Test
        @DisplayName("삭제된 정책은 조회되지 않습니다")
        void findByCriteria_ExcludesDeletedPolicies() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(sellerId));

            var criteria =
                    com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria
                            .defaultCriteria(
                                    com.ryuqq.marketplace.domain.seller.id.SellerId.of(sellerId));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            Long sellerId = 1L;
            for (int i = 0; i < 5; i++) {
                persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            }

            var criteria =
                    com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria.of(
                            com.ryuqq.marketplace.domain.seller.id.SellerId.of(sellerId),
                            com.ryuqq.marketplace.domain.common.vo.QueryContext.of(
                                    com.ryuqq.marketplace.domain.refundpolicy.query
                                            .RefundPolicySortKey.defaultKey(),
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
        @DisplayName("특정 셀러의 정책 개수를 반환합니다")
        void countByCriteria_WithSellerId_ReturnsCount() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(2L));

            var criteria =
                    com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria
                            .defaultCriteria(
                                    com.ryuqq.marketplace.domain.seller.id.SellerId.of(sellerId));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("삭제된 정책은 개수에 포함되지 않습니다")
        void countByCriteria_ExcludesDeletedPolicies() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(sellerId));

            var criteria =
                    com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria
                            .defaultCriteria(
                                    com.ryuqq.marketplace.domain.seller.id.SellerId.of(sellerId));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("countActiveBySellerId")
    class CountActiveBySellerIdTest {

        @Test
        @DisplayName("특정 셀러의 활성 정책 개수를 반환합니다")
        void countActiveBySellerId_WithActivePolicies_ReturnsActiveCount() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newInactiveEntity(sellerId));

            long count = repository().countActiveBySellerId(sellerId);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("비활성 정책은 개수에 포함되지 않습니다")
        void countActiveBySellerId_ExcludesInactive() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newInactiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newInactiveEntity(sellerId));

            long count = repository().countActiveBySellerId(sellerId);

            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("삭제된 활성 정책은 개수에 포함되지 않습니다")
        void countActiveBySellerId_ExcludesDeleted() {
            Long sellerId = 1L;
            persist(RefundPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(RefundPolicyJpaEntityFixtures.newDeletedEntity(sellerId));

            long count = repository().countActiveBySellerId(sellerId);

            assertThat(count).isEqualTo(1);
        }
    }
}
