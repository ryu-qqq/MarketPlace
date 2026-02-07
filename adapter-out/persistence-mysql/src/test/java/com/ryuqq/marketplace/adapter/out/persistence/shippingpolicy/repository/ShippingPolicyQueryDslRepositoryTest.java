package com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.repository;

import static com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.ShippingPolicyJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.condition.ShippingPolicyConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.shippingpolicy.entity.ShippingPolicyJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySearchCriteria;
import com.ryuqq.marketplace.domain.shippingpolicy.query.ShippingPolicySortKey;
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
 * ShippingPolicyQueryDslRepositoryTest - 배송 정책 QueryDslRepository 통합 테스트.
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
@DisplayName("ShippingPolicyQueryDslRepository 통합 테스트")
class ShippingPolicyQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ShippingPolicyQueryDslRepository repository() {
        return new ShippingPolicyQueryDslRepository(
                new JPAQueryFactory(entityManager), new ShippingPolicyConditionBuilder());
    }

    private ShippingPolicyJpaEntity persist(ShippingPolicyJpaEntity entity) {
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
            // given
            ShippingPolicyJpaEntity saved = persist(ShippingPolicyJpaEntityFixtures.newEntity());

            // when
            var result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다 (soft-delete 필터)")
        void findById_WithDeleted_ReturnsEmpty() {
            // given
            ShippingPolicyJpaEntity deleted =
                    persist(ShippingPolicyJpaEntityFixtures.newDeletedEntity(DEFAULT_SELLER_ID));

            // when
            var result = repository().findById(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional 반환")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // when
            var result = repository().findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("여러 ID로 조회 시 미삭제 Entity만 반환됩니다")
        void findByIds_WithMultipleIds_ReturnsNotDeletedEntities() {
            // given
            ShippingPolicyJpaEntity entity1 =
                    persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(1L));
            ShippingPolicyJpaEntity entity2 =
                    persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(1L));
            ShippingPolicyJpaEntity deleted =
                    persist(ShippingPolicyJpaEntityFixtures.newDeletedEntity(1L));

            // when
            var result =
                    repository()
                            .findByIds(List.of(entity1.getId(), entity2.getId(), deleted.getId()));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting("id")
                    .containsExactlyInAnyOrder(entity1.getId(), entity2.getId());
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 결과 반환")
        void findByIds_WithEmptyList_ReturnsEmpty() {
            // when
            var result = repository().findByIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID들로 조회 시 빈 결과 반환")
        void findByIds_WithNonExistingIds_ReturnsEmpty() {
            // when
            var result = repository().findByIds(List.of(998L, 999L));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDefaultBySellerId")
    class FindDefaultBySellerIdTest {

        @Test
        @DisplayName("셀러의 기본 정책 조회 성공")
        void findDefaultBySellerId_WithDefaultPolicy_ReturnsEntity() {
            // given
            Long sellerId = 1L;
            ShippingPolicyJpaEntity defaultPolicy =
                    persist(ShippingPolicyJpaEntityFixtures.newDefaultEntity(sellerId));
            persist(
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            sellerId, "비기본 정책")); // 비기본 정책

            // when
            var result = repository().findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(defaultPolicy.getId());
            assertThat(result.get().isDefaultPolicy()).isTrue();
        }

        @Test
        @DisplayName("기본 정책이 없을 때 빈 Optional 반환")
        void findDefaultBySellerId_WithNoDefaultPolicy_ReturnsEmpty() {
            // given
            Long sellerId = 1L;
            persist(
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            sellerId, "비기본 정책")); // 비기본 정책

            // when
            var result = repository().findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 기본 정책은 조회되지 않습니다 (soft-delete 필터)")
        void findDefaultBySellerId_WithDeletedDefaultPolicy_ReturnsEmpty() {
            // given
            Long sellerId = 1L;
            persist(
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(
                            sellerId)); // deletedAt이 설정된 Entity

            // when
            var result = repository().findDefaultBySellerId(sellerId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndId")
    class FindBySellerIdAndIdTest {

        @Test
        @DisplayName("셀러 ID와 정책 ID로 정상 조회")
        void findBySellerIdAndId_WithValidIds_ReturnsEntity() {
            // given
            Long sellerId = 1L;
            ShippingPolicyJpaEntity saved =
                    persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            // when
            var result = repository().findBySellerIdAndId(sellerId, saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
        }

        @Test
        @DisplayName("다른 셀러의 정책 조회 시 빈 결과 반환 (권한 분리)")
        void findBySellerIdAndId_WithDifferentSeller_ReturnsEmpty() {
            // given
            Long sellerId1 = 1L;
            Long sellerId2 = 2L;
            ShippingPolicyJpaEntity seller1Policy =
                    persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId1));

            // when
            var result = repository().findBySellerIdAndId(sellerId2, seller1Policy.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 정책은 조회되지 않습니다 (soft-delete 필터)")
        void findBySellerIdAndId_WithDeletedEntity_ReturnsEmpty() {
            // given
            Long sellerId = 1L;
            ShippingPolicyJpaEntity deleted =
                    persist(ShippingPolicyJpaEntityFixtures.newDeletedEntity(sellerId));

            // when
            var result = repository().findBySellerIdAndId(sellerId, deleted.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 검색 조건으로 페이징 조회 성공")
        void findByCriteria_WithDefaultCriteria_ReturnsPaginatedResult() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            SellerId.of(sellerId),
                            QueryContext.of(
                                    ShippingPolicySortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 2)));

            // when
            var result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("페이징 경계값 테스트 (offset=0, size=1)")
        void findByCriteria_WithBoundaryPaging_ReturnsCorrectResult() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            SellerId.of(sellerId),
                            QueryContext.of(
                                    ShippingPolicySortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 1)));

            // when
            var result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("CREATED_AT 정렬 키로 조회 성공")
        void findByCriteria_WithCreatedAtSort_ReturnsCorrectOrder() {
            // given
            Long sellerId = 1L;
            ShippingPolicyJpaEntity entity1 =
                    persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            ShippingPolicyJpaEntity entity2 =
                    persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            SellerId.of(sellerId),
                            QueryContext.of(
                                    ShippingPolicySortKey.CREATED_AT,
                                    SortDirection.ASC,
                                    PageRequest.of(0, 10)));

            // when
            var result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting("id").containsExactly(entity1.getId(), entity2.getId());
        }

        @Test
        @DisplayName("POLICY_NAME 정렬 키로 조회 성공")
        void findByCriteria_WithPolicyNameSort_ReturnsCorrectOrder() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(sellerId, "A정책"));
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(sellerId, "B정책"));
            entityManager.flush();
            entityManager.clear();

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            SellerId.of(sellerId),
                            QueryContext.of(
                                    ShippingPolicySortKey.POLICY_NAME,
                                    SortDirection.ASC,
                                    PageRequest.of(0, 10)));

            // when
            var result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).extracting("policyName").containsExactly("A정책", "B정책");
        }

        @Test
        @DisplayName("BASE_FEE 정렬 키로 조회 성공")
        void findByCriteria_WithBaseFeeSort_ReturnsCorrectOrder() {
            // given
            Long sellerId = 1L;
            // freeShippingEntity는 baseFee=0인데, newEntity는 baseFee=3000 (DEFAULT_BASE_FEE)
            persist(ShippingPolicyJpaEntityFixtures.newEntity()); // baseFee=3000, sellerId=1L
            persist(
                    ShippingPolicyJpaEntityFixtures.newActiveEntityWithName(
                            sellerId, "무료배송")); // baseFee=3000
            entityManager.flush();
            entityManager.clear();

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            SellerId.of(sellerId),
                            QueryContext.of(
                                    ShippingPolicySortKey.BASE_FEE,
                                    SortDirection.ASC,
                                    PageRequest.of(0, 10)));

            // when
            var result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting("baseFee").allMatch(fee -> fee.equals(DEFAULT_BASE_FEE));
        }

        @Test
        @DisplayName("삭제된 정책은 조회되지 않습니다 (soft-delete 필터)")
        void findByCriteria_WithDeletedEntities_ReturnsOnlyNotDeleted() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(
                            sellerId)); // deletedAt이 설정된 Entity

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.of(
                            SellerId.of(sellerId),
                            QueryContext.of(
                                    ShippingPolicySortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(0, 10)));

            // when
            var result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getDeletedAt()).isNull();
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 정책 개수 조회 성공")
        void countByCriteria_WithCriteria_ReturnsCount() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(SellerId.of(sellerId));

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("조건에 맞는 정책이 없을 때 0 반환")
        void countByCriteria_WithNoMatching_ReturnsZero() {
            // given
            Long sellerId = 999L;
            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(SellerId.of(sellerId));

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(0L);
        }

        @Test
        @DisplayName("삭제된 정책은 카운트에서 제외됩니다 (soft-delete 필터)")
        void countByCriteria_WithDeletedEntities_ReturnsOnlyNotDeletedCount() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId));
            persist(
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(
                            sellerId)); // deletedAt이 설정된 Entity

            ShippingPolicySearchCriteria criteria =
                    ShippingPolicySearchCriteria.defaultCriteria(SellerId.of(sellerId));

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("countActiveBySellerId")
    class CountActiveBySellerIdTest {

        @Test
        @DisplayName("활성 정책 개수 조회 성공")
        void countActiveBySellerId_WithActivePolicies_ReturnsCount() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId)); // active=true
            persist(ShippingPolicyJpaEntityFixtures.newActiveEntity(sellerId)); // active=true
            persist(ShippingPolicyJpaEntityFixtures.newInactiveEntity(sellerId)); // active=false

            // when
            long count = repository().countActiveBySellerId(sellerId);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("활성 정책이 없을 때 0 반환")
        void countActiveBySellerId_WithNoActivePolicies_ReturnsZero() {
            // given
            Long sellerId = 1L;
            persist(ShippingPolicyJpaEntityFixtures.newInactiveEntity(sellerId)); // active=false

            // when
            long count = repository().countActiveBySellerId(sellerId);

            // then
            assertThat(count).isEqualTo(0L);
        }

        @Test
        @DisplayName("삭제된 활성 정책은 카운트에서 제외됩니다 (soft-delete 필터)")
        void countActiveBySellerId_WithDeletedActivePolicies_ReturnsOnlyNotDeletedCount() {
            // given
            Long sellerId = 1L;
            persist(
                    ShippingPolicyJpaEntityFixtures.newActiveEntity(
                            sellerId)); // active=true, deletedAt=null
            persist(
                    ShippingPolicyJpaEntityFixtures.newDeletedEntity(
                            sellerId)); // deletedAt이 설정된 Entity

            // when
            long count = repository().countActiveBySellerId(sellerId);

            // then
            assertThat(count).isEqualTo(1L);
        }
    }
}
