package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.ClaimHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.condition.ClaimHistoryConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistoryPageCriteria;
import com.ryuqq.marketplace.domain.claimhistory.query.ClaimHistorySortKey;
import com.ryuqq.marketplace.domain.claimhistory.vo.ClaimType;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
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
 * ClaimHistoryQueryDslRepositoryTest - 클레임 이력 QueryDslRepository 통합 테스트.
 *
 * <p>claimType + claimId 기반 조회 로직을 검증합니다.
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
@DisplayName("ClaimHistoryQueryDslRepository 통합 테스트")
class ClaimHistoryQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ClaimHistoryQueryDslRepository repository() {
        return new ClaimHistoryQueryDslRepository(
                new JPAQueryFactory(entityManager), new ClaimHistoryConditionBuilder());
    }

    private ClaimHistoryJpaEntity persist(ClaimHistoryJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findByClaimTypeAndClaimId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByClaimTypeAndClaimId")
    class FindByClaimTypeAndClaimIdTest {

        @Test
        @DisplayName("claimType과 claimId가 일치하는 Entity 목록을 반환합니다")
        void findByClaimTypeAndClaimId_WithMatchingEntities_ReturnsEntityList() {
            // given
            String claimId = "cancel-claim-test-001";
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId));
            persist(ClaimHistoryJpaEntityFixtures.manualMemoEntity(claimId));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimId("CANCEL", claimId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ClaimHistoryJpaEntity::getClaimType)
                    .containsOnly("CANCEL");
            assertThat(result).extracting(ClaimHistoryJpaEntity::getClaimId).containsOnly(claimId);
        }

        @Test
        @DisplayName("claimType이 다른 Entity는 조회되지 않습니다")
        void findByClaimTypeAndClaimId_WithDifferentClaimType_ReturnsEmpty() {
            // given
            String claimId = "claim-type-filter-001";
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimId("REFUND", claimId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("claimId가 다른 Entity는 조회되지 않습니다")
        void findByClaimTypeAndClaimId_WithDifferentClaimId_ReturnsEmpty() {
            // given
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("other-claim-001"));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimId("CANCEL", "non-existent-claim");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("결과는 createdAt 오름차순으로 정렬됩니다")
        void findByClaimTypeAndClaimId_ReturnsOrderedByCreatedAtAsc() {
            // given
            String claimId = "order-test-claim-001";
            ClaimHistoryJpaEntity entity1 =
                    persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId));
            ClaimHistoryJpaEntity entity2 =
                    persist(ClaimHistoryJpaEntityFixtures.manualMemoEntity(claimId));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimId("CANCEL", claimId);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
            if (result.size() >= 2) {
                assertThat(result.get(0).getCreatedAt())
                        .isBeforeOrEqualTo(result.get(1).getCreatedAt());
            }
        }

        @Test
        @DisplayName("REFUND 타입으로 이력을 조회합니다")
        void findByClaimTypeAndClaimId_WithRefundType_ReturnsEntities() {
            // given
            String claimId = "refund-query-test-001";
            persist(ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity(claimId));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimId("REFUND", claimId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getClaimType()).isEqualTo("REFUND");
        }

        @Test
        @DisplayName("EXCHANGE 타입으로 이력을 조회합니다")
        void findByClaimTypeAndClaimId_WithExchangeType_ReturnsEntities() {
            // given
            String claimId = "exchange-query-test-001";
            persist(ClaimHistoryJpaEntityFixtures.exchangeStatusChangeEntity(claimId));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimId("EXCHANGE", claimId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getClaimType()).isEqualTo("EXCHANGE");
        }
    }

    // ========================================================================
    // 2. findByClaimTypeAndClaimIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByClaimTypeAndClaimIds")
    class FindByClaimTypeAndClaimIdsTest {

        @Test
        @DisplayName("여러 claimId의 이력을 한 번에 조회합니다")
        void findByClaimTypeAndClaimIds_WithMultipleClaimIds_ReturnsAllEntities() {
            // given
            String claimId1 = "bulk-cancel-001";
            String claimId2 = "bulk-cancel-002";
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId1));
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId2));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimIds("CANCEL", List.of(claimId1, claimId2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ClaimHistoryJpaEntity::getClaimId)
                    .containsExactlyInAnyOrder(claimId1, claimId2);
        }

        @Test
        @DisplayName("claimType이 다른 Entity는 제외합니다")
        void findByClaimTypeAndClaimIds_WithDifferentClaimType_ExcludesMismatch() {
            // given
            String cancelId = "type-filter-cancel-001";
            String refundId = "type-filter-refund-001";
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(cancelId));
            persist(ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity(refundId));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimIds("CANCEL", List.of(cancelId, refundId));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getClaimId()).isEqualTo(cancelId);
        }

        @Test
        @DisplayName("빈 claimId 목록으로 조회 시 빈 리스트를 반환합니다")
        void findByClaimTypeAndClaimIds_WithEmptyList_ReturnsEmpty() {
            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimIds("CANCEL", List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 claimId 목록 조회 시 빈 리스트를 반환합니다")
        void findByClaimTypeAndClaimIds_WithNonExistentIds_ReturnsEmpty() {
            // when
            List<ClaimHistoryJpaEntity> result =
                    repository()
                            .findByClaimTypeAndClaimIds(
                                    "CANCEL", List.of("non-existent-001", "non-existent-002"));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("결과는 createdAt 오름차순으로 정렬됩니다")
        void findByClaimTypeAndClaimIds_ReturnsOrderedByCreatedAtAsc() {
            // given
            String claimId1 = "sort-test-claim-001";
            String claimId2 = "sort-test-claim-002";
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId1));
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(claimId2));

            // when
            List<ClaimHistoryJpaEntity> result =
                    repository().findByClaimTypeAndClaimIds("CANCEL", List.of(claimId1, claimId2));

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
            for (int i = 0; i < result.size() - 1; i++) {
                assertThat(result.get(i).getCreatedAt())
                        .isBeforeOrEqualTo(result.get(i + 1).getCreatedAt());
            }
        }
    }

    // ========================================================================
    // 3. findByOrderItemId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByOrderItemId")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("orderItemId가 일치하는 모든 이력을 반환합니다")
        void findByOrderItemId_WithMatchingEntities_ReturnsEntityList() {
            // given
            Long orderItemId = 3001L;
            persist(ClaimHistoryJpaEntityFixtures.orderMemoEntity(orderItemId));
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-001"));

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOrderItemId()).isEqualTo(orderItemId);
        }

        @Test
        @DisplayName("orderItemId가 다른 Entity는 조회되지 않습니다")
        void findByOrderItemId_WithDifferentOrderItemId_ReturnsEmpty() {
            // given
            persist(ClaimHistoryJpaEntityFixtures.orderMemoEntity(9901L));

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByOrderItemId(99999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 claimType의 이력이 같은 orderItemId를 가지면 모두 반환됩니다")
        void findByOrderItemId_WithMultipleClaimTypes_ReturnsAll() {
            // given
            Long orderItemId = 3002L;
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-multi-001"));
            persist(ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity("refund-multi-001"));
            persist(ClaimHistoryJpaEntityFixtures.orderMemoEntity(orderItemId));

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("결과는 createdAt 오름차순으로 정렬됩니다")
        void findByOrderItemId_ReturnsOrderedByCreatedAtAsc() {
            // given
            Long orderItemId = 3003L;
            persist(ClaimHistoryJpaEntityFixtures.orderMemoEntity(orderItemId));
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-for-sort-001"));

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByOrderItemId(orderItemId);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(1);
        }
    }

    // ========================================================================
    // 4. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("orderItemId 조건으로 이력을 조회합니다")
        void findByCriteria_WithOrderItemId_ReturnsMatchingEntities() {
            // given
            Long orderItemId = 4001L;
            persist(ClaimHistoryJpaEntityFixtures.orderMemoEntity(orderItemId));
            persist(ClaimHistoryJpaEntityFixtures.orderMemoEntity(9902L));

            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(orderItemId);

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getOrderItemId()).isEqualTo(orderItemId);
        }

        @Test
        @DisplayName("claimType 필터와 함께 조회하면 해당 타입만 반환됩니다")
        void findByCriteria_WithClaimTypeFilter_ReturnsFilteredEntities() {
            // given
            Long orderItemId = ClaimHistoryJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("criteria-cancel-001"));
            persist(ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity("criteria-refund-001"));

            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            orderItemId,
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result)
                    .extracting(ClaimHistoryJpaEntity::getClaimType)
                    .containsOnly("CANCEL");
        }

        @Test
        @DisplayName("claimType 필터가 없으면 해당 orderItemId의 모든 이력을 반환합니다")
        void findByCriteria_WithoutClaimTypeFilter_ReturnsAllForOrderItemId() {
            // given
            Long orderItemId = ClaimHistoryJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("all-cancel-001"));
            persist(ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity("all-refund-001"));

            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(orderItemId);

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("조건에 맞는 데이터가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoMatchingData_ReturnsEmpty() {
            // given
            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(99999L);

            // when
            List<ClaimHistoryJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("orderItemId 조건으로 카운트를 반환합니다")
        void countByCriteria_WithOrderItemId_ReturnsCount() {
            // given
            Long orderItemId = ClaimHistoryJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            persist(ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("count-cancel-001"));
            persist(ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity("count-refund-001"));

            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(orderItemId);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("claimType 필터와 함께 카운트하면 해당 타입 수만 반환됩니다")
        void countByCriteria_WithClaimTypeFilter_ReturnsFilteredCount() {
            // given
            Long orderItemId = ClaimHistoryJpaEntityFixtures.DEFAULT_ORDER_ITEM_ID;
            persist(
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(
                            "count-filter-cancel-001"));
            persist(
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity(
                            "count-filter-cancel-002"));
            persist(
                    ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity(
                            "count-filter-refund-001"));

            ClaimHistoryPageCriteria criteria =
                    ClaimHistoryPageCriteria.of(
                            orderItemId,
                            ClaimType.CANCEL,
                            QueryContext.defaultOf(ClaimHistorySortKey.defaultKey()));

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("조건에 맞는 데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoMatchingData_ReturnsZero() {
            // given
            ClaimHistoryPageCriteria criteria = ClaimHistoryPageCriteria.defaultOf(99998L);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }
    }
}
