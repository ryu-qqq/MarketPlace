package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.condition.CommonCodeTypeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * CommonCodeTypeQueryDslRepositoryTest - 공통 코드 타입 QueryDslRepository 통합 테스트.
 *
 * <p>JPA + QueryDSL 기반 쿼리 동작과 notDeleted(soft-delete) 필터 적용을 검증합니다.
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
@DisplayName("CommonCodeTypeQueryDslRepository 통합 테스트")
class CommonCodeTypeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CommonCodeTypeQueryDslRepository repository() {
        return new CommonCodeTypeQueryDslRepository(
                new JPAQueryFactory(entityManager), new CommonCodeTypeConditionBuilder());
    }

    private CommonCodeTypeJpaEntity persist(CommonCodeTypeJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("미삭제 Entity는 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            // given
            CommonCodeTypeJpaEntity saved = persist(CommonCodeTypeJpaEntityFixtures.newEntity());
            Long id = saved.getId();

            // when
            var result = repository().findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(id);
        }

        @Test
        @DisplayName("삭제된 Entity는 조회되지 않습니다(notDeleted 필터)")
        void findById_WithDeletedEntity_ReturnsEmpty() {
            // given
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity deletedEntity =
                    CommonCodeTypeJpaEntity.create(
                            null,
                            "DELETED_TYPE",
                            "삭제 타입",
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION,
                            999,
                            true,
                            now,
                            now,
                            now);
            persist(deletedEntity);

            // when
            var result = repository().findById(deletedEntity.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("ID 목록으로 여러 Entity를 조회합니다")
        void findByIds_WithMultipleIds_ReturnsEntities() {
            CommonCodeTypeJpaEntity saved1 =
                    persist(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "CODE_TYPE_01", "코드타입1"));
            CommonCodeTypeJpaEntity saved2 =
                    persist(
                            CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                    "CODE_TYPE_02", "코드타입2"));

            var result = repository().findByIds(List.of(saved1.getId(), saved2.getId()));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 ID 목록 조회에서 제외됩니다")
        void findByIds_WithDeletedEntity_ExcludesDeleted() {
            CommonCodeTypeJpaEntity saved = persist(CommonCodeTypeJpaEntityFixtures.newEntity());
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity deleted =
                    CommonCodeTypeJpaEntity.create(
                            null, "DELETED_TYPE", "삭제 타입", "설명", 999, true, now, now, now);
            persist(deleted);

            var result = repository().findByIds(List.of(saved.getId(), deleted.getId()));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(saved.getId());
        }
    }

    @Nested
    @DisplayName("existsByCode")
    class ExistsByCodeTest {

        @Test
        @DisplayName("코드가 존재하면 true를 반환합니다")
        void existsByCode_WithExistingCode_ReturnsTrue() {
            String code = "PAYMENT_METHOD";
            persist(CommonCodeTypeJpaEntityFixtures.newEntityWithCode(code, "결제수단"));

            boolean exists = repository().existsByCode(code);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 코드면 false를 반환합니다")
        void existsByCode_WithNonExistingCode_ReturnsFalse() {
            boolean exists = repository().existsByCode("NON_EXISTING_CODE");

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity의 코드는 존재하지 않는 것으로 처리됩니다")
        void existsByCode_WithDeletedEntity_ReturnsFalse() {
            String code = "DELETED_CODE";
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity deleted =
                    CommonCodeTypeJpaEntity.create(
                            null, code, "삭제 타입", "설명", 999, true, now, now, now);
            persist(deleted);

            boolean exists = repository().existsByCode(code);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsByDisplayOrderExcludingId")
    class ExistsByDisplayOrderExcludingIdTest {

        @Test
        @DisplayName("같은 표시 순서를 가진 다른 Entity가 존재하면 true를 반환합니다")
        void existsByDisplayOrderExcludingId_WithExistingOrder_ReturnsTrue() {
            int displayOrder = 10;
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity entity1 =
                    persist(
                            CommonCodeTypeJpaEntity.create(
                                    null,
                                    "TYPE_A",
                                    "타입 A",
                                    "설명",
                                    displayOrder,
                                    true,
                                    now,
                                    now,
                                    null));
            persist(
                    CommonCodeTypeJpaEntity.create(
                            null, "TYPE_B", "타입 B", "설명", 20, true, now, now, null));

            boolean exists = repository().existsByDisplayOrderExcludingId(displayOrder, 999L);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("자기 자신의 ID는 제외하고 검사합니다")
        void existsByDisplayOrderExcludingId_ExcludesSelfId_ReturnsFalse() {
            int displayOrder = 10;
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity entity =
                    persist(
                            CommonCodeTypeJpaEntity.create(
                                    null,
                                    "TYPE_C",
                                    "타입 C",
                                    "설명",
                                    displayOrder,
                                    true,
                                    now,
                                    now,
                                    null));

            boolean exists =
                    repository().existsByDisplayOrderExcludingId(displayOrder, entity.getId());

            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity는 표시 순서 검증에서 제외됩니다")
        void existsByDisplayOrderExcludingId_WithDeletedEntity_ReturnsFalse() {
            int displayOrder = 10;
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity deleted =
                    CommonCodeTypeJpaEntity.create(
                            null, "DELETED", "삭제", "설명", displayOrder, true, now, now, now);
            persist(deleted);

            boolean exists = repository().existsByDisplayOrderExcludingId(displayOrder, 999L);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 삭제되지 않은 모든 Entity를 조회합니다")
        void findByCriteria_WithDefaultCondition_ReturnsAllNotDeleted() {
            persist(CommonCodeTypeJpaEntityFixtures.newEntity());
            persist(CommonCodeTypeJpaEntityFixtures.newInactiveEntity());
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity deleted =
                    CommonCodeTypeJpaEntity.create(
                            null, "DELETED", "삭제", "설명", 999, true, now, now, now);
            persist(deleted);

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .defaultCriteria();
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("활성 필터로 활성 타입만 조회합니다")
        void findByCriteria_WithActiveFilter_ReturnsOnlyActive() {
            persist(CommonCodeTypeJpaEntityFixtures.newEntity());
            persist(CommonCodeTypeJpaEntityFixtures.newInactiveEntity());

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .activeOnly();
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().isActive()).isTrue();
        }

        @Test
        @DisplayName("검색어로 이름을 검색합니다")
        void findByCriteria_WithSearchWord_ReturnsMatchingEntities() {
            persist(CommonCodeTypeJpaEntityFixtures.newEntityWithCode("PAYMENT_METHOD", "결제수단"));
            persist(CommonCodeTypeJpaEntityFixtures.newEntityWithCode("REFUND_METHOD", "환불수단"));

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .of(
                                    null,
                                    com.ryuqq.marketplace.domain.commoncodetype.query
                                            .CommonCodeTypeSearchField.NAME,
                                    "결제",
                                    null,
                                    com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                            com.ryuqq.marketplace.domain.commoncodetype.query
                                                    .CommonCodeTypeSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).contains("결제");
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            for (int i = 0; i < 5; i++) {
                persist(
                        CommonCodeTypeJpaEntityFixtures.newEntityWithCode(
                                "PAGE_TYPE_" + i, "페이징타입" + i));
            }

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .of(
                                    null,
                                    null,
                                    null,
                                    null,
                                    com.ryuqq.marketplace.domain.common.vo.QueryContext.of(
                                            com.ryuqq.marketplace.domain.commoncodetype.query
                                                    .CommonCodeTypeSortKey.defaultKey(),
                                            com.ryuqq.marketplace.domain.common.vo.SortDirection
                                                    .ASC,
                                            com.ryuqq.marketplace.domain.common.vo.PageRequest.of(
                                                    1, 2)));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 삭제되지 않은 Entity 개수를 반환합니다")
        void countByCriteria_WithDefaultCondition_ReturnsCount() {
            persist(CommonCodeTypeJpaEntityFixtures.newEntity());
            persist(CommonCodeTypeJpaEntityFixtures.newInactiveEntity());
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity deleted =
                    CommonCodeTypeJpaEntity.create(
                            null, "DELETED", "삭제", "설명", 999, true, now, now, now);
            persist(deleted);

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .defaultCriteria();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("활성 필터로 활성 타입 개수만 반환합니다")
        void countByCriteria_WithActiveFilter_ReturnsActiveCount() {
            persist(CommonCodeTypeJpaEntityFixtures.newEntity());
            persist(CommonCodeTypeJpaEntityFixtures.newInactiveEntity());

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .activeOnly();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("검색 조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithSearchWord_ReturnsMatchingCount() {
            persist(CommonCodeTypeJpaEntityFixtures.newEntityWithCode("PAYMENT_METHOD", "결제수단"));
            persist(CommonCodeTypeJpaEntityFixtures.newEntityWithCode("REFUND_METHOD", "환불수단"));

            var criteria =
                    com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria
                            .of(
                                    null,
                                    com.ryuqq.marketplace.domain.commoncodetype.query
                                            .CommonCodeTypeSearchField.NAME,
                                    "결제",
                                    null,
                                    com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                            com.ryuqq.marketplace.domain.commoncodetype.query
                                                    .CommonCodeTypeSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }
    }
}
