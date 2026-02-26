package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.InboundSourceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.condition.InboundSourceConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchField;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSortKey;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus;
import com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType;
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
 * InboundSourceQueryDslRepositoryTest - InboundSource QueryDslRepository 통합 테스트.
 *
 * <p>QueryDSL 기반 동적 쿼리 동작을 검증합니다.
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
@DisplayName("InboundSourceQueryDslRepository 통합 테스트")
class InboundSourceQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private InboundSourceQueryDslRepository repository() {
        return new InboundSourceQueryDslRepository(
                new JPAQueryFactory(entityManager), new InboundSourceConditionBuilder());
    }

    private InboundSourceJpaEntity persist(InboundSourceJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findById / findByCode / existsByCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById / findByCode / existsByCode")
    class FindByIdAndCodeTest {

        @Test
        @DisplayName("저장된 Entity를 findById로 조회합니다")
        void findById_WithExistingEntity_ReturnsEntity() {
            // given
            InboundSourceJpaEntity saved = persist(InboundSourceJpaEntityFixtures.newEntity());

            // when
            Optional<InboundSourceJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<InboundSourceJpaEntity> result = repository().findById(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("저장된 Entity를 findByCode로 조회합니다")
        void findByCode_WithExistingCode_ReturnsEntity() {
            // given
            String code = "FIND_BY_CODE_TEST";
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode(code));

            // when
            Optional<InboundSourceJpaEntity> result = repository().findByCode(code);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getCode()).isEqualTo(code);
        }

        @Test
        @DisplayName("존재하지 않는 코드로 조회 시 빈 Optional을 반환합니다")
        void findByCode_WithNonExistingCode_ReturnsEmpty() {
            // when
            Optional<InboundSourceJpaEntity> result = repository().findByCode("NONEXISTENT_CODE");

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하는 코드의 existsByCode는 true를 반환합니다")
        void existsByCode_WithExistingCode_ReturnsTrue() {
            // given
            String code = "EXISTS_CODE_TEST";
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode(code));

            // when
            boolean exists = repository().existsByCode(code);

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 코드의 existsByCode는 false를 반환합니다")
        void existsByCode_WithNonExistingCode_ReturnsFalse() {
            // when
            boolean exists = repository().existsByCode("NONEXISTENT_CODE_CHECK");

            // then
            assertThat(exists).isFalse();
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 조회하면 모든 Entity를 반환합니다")
        void findByCriteria_WithDefaultCondition_ReturnsAll() {
            // given
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode("CRITERIA_ACTIVE_1"));
            persist(InboundSourceJpaEntityFixtures.inactiveEntityWithCode("CRITERIA_INACTIVE_1"));

            InboundSourceSearchCriteria criteria = buildDefaultCriteria();

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSizeGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("타입 필터로 LEGACY만 조회합니다")
        void findByCriteria_WithTypeFilter_ReturnsMatchingEntities() {
            // given
            persist(
                    InboundSourceJpaEntityFixtures.activeEntityWithCodeAndType(
                            "TYPE_LEGACY_1", "LEGACY"));
            persist(
                    InboundSourceJpaEntityFixtures.activeEntityWithCodeAndType(
                            "TYPE_CRAWLING_1", "CRAWLING"));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            List.of(InboundSourceType.LEGACY),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> "LEGACY".equals(e.getType()));
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE만 조회합니다")
        void findByCriteria_WithStatusFilter_ReturnsOnlyActive() {
            // given
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode("STATUS_ACTIVE_1"));
            persist(InboundSourceJpaEntityFixtures.inactiveEntityWithCode("STATUS_INACTIVE_1"));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            List.of(InboundSourceStatus.ACTIVE),
                            null,
                            null,
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> "ACTIVE".equals(e.getStatus()));
        }

        @Test
        @DisplayName("검색어로 이름을 검색합니다")
        void findByCriteria_WithSearchWord_ReturnsMatchingEntities() {
            // given
            persist(InboundSourceJpaEntityFixtures.activeEntityWithName("세토프 레거시 상품"));
            persist(InboundSourceJpaEntityFixtures.activeEntityWithName("쿠팡 크롤링 소스"));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            null,
                            InboundSourceSearchField.NAME,
                            "세토프",
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getName().contains("세토프"));
        }

        @Test
        @DisplayName("코드 검색어로 조회합니다")
        void findByCriteria_WithCodeSearchField_ReturnsMatchingEntities() {
            // given
            String code = "SEARCH_CODE_UNIQUE";
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode(code));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            null,
                            InboundSourceSearchField.CODE,
                            "SEARCH_CODE",
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
            assertThat(result).allMatch(e -> e.getCode().contains("SEARCH_CODE"));
        }

        @Test
        @DisplayName("검색 필드 없이 검색어만 있으면 이름과 코드 통합 검색을 합니다")
        void findByCriteria_WithSearchWordNoField_ReturnsMatchingEntities() {
            // given
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode("UNIFIED_SEARCH_CODE"));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            null,
                            null,
                            "UNIFIED_SEARCH",
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isNotEmpty();
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            // given
            for (int i = 0; i < 5; i++) {
                persist(InboundSourceJpaEntityFixtures.activeEntityWithCode("PAGING_SRC_" + i));
            }

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    InboundSourceSortKey.defaultKey(),
                                    com.ryuqq.marketplace.domain.common.vo.SortDirection.ASC,
                                    com.ryuqq.marketplace.domain.common.vo.PageRequest.of(0, 2)));

            // when
            List<InboundSourceJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 조회하면 전체 개수를 반환합니다")
        void countByCriteria_WithDefaultCondition_ReturnsCount() {
            // given
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode("COUNT_ACTIVE_1"));
            persist(InboundSourceJpaEntityFixtures.inactiveEntityWithCode("COUNT_INACTIVE_1"));

            InboundSourceSearchCriteria criteria = buildDefaultCriteria();

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE 개수만 반환합니다")
        void countByCriteria_WithActiveStatusFilter_ReturnsActiveCount() {
            // given
            persist(InboundSourceJpaEntityFixtures.activeEntityWithCode("COUNT_ACT_ONLY_1"));
            persist(InboundSourceJpaEntityFixtures.inactiveEntityWithCode("COUNT_INACT_ONLY_1"));

            InboundSourceSearchCriteria activeCriteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            List.of(InboundSourceStatus.ACTIVE),
                            null,
                            null,
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            InboundSourceSearchCriteria inactiveCriteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            List.of(InboundSourceStatus.INACTIVE),
                            null,
                            null,
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            long activeCount = repository().countByCriteria(activeCriteria);
            long inactiveCount = repository().countByCriteria(inactiveCriteria);

            // then
            assertThat(activeCount).isGreaterThanOrEqualTo(1);
            assertThat(inactiveCount).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("검색 조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithSearchWord_ReturnsMatchingCount() {
            // given
            persist(
                    InboundSourceJpaEntityFixtures.activeEntityWithCode(
                            "COUNT_SEARCH_UNIQUE_CODE"));

            InboundSourceSearchCriteria criteria =
                    InboundSourceSearchCriteria.of(
                            null,
                            null,
                            InboundSourceSearchField.CODE,
                            "COUNT_SEARCH_UNIQUE",
                            QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(1);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private InboundSourceSearchCriteria buildDefaultCriteria() {
        return InboundSourceSearchCriteria.of(
                null, null, null, null, QueryContext.defaultOf(InboundSourceSortKey.defaultKey()));
    }
}
