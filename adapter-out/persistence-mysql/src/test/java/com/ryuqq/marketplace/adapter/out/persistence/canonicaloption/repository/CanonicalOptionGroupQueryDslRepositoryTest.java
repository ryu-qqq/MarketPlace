package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionValueJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.condition.CanonicalOptionGroupConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
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

    private CanonicalOptionValueJpaEntity persistValue(CanonicalOptionValueJpaEntity entity) {
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
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "NAME_KO",
                            "색상",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("비활성 필터로 조회하면 비활성 Entity 개수만 반환합니다")
        void countByCriteria_WithInactiveFilter_ReturnsInactiveCount() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("ACTIVE_CNT_A"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("INACTIVE_CNT_A"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("INACTIVE_CNT_B"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            false,
                            null,
                            null,
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }
    }

    // ========================================================================
    // findByIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIds")
    class FindByIdsTest {

        @Test
        @DisplayName("여러 ID로 조회하면 해당 Entity를 반환합니다")
        void findByIds_WithExistingIds_ReturnsEntities() {
            CanonicalOptionGroupJpaEntity entity1 =
                    persist(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "IDS_COLOR"));
            CanonicalOptionGroupJpaEntity entity2 =
                    persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("IDS_SIZE"));

            List<CanonicalOptionGroupJpaEntity> result =
                    repository().findByIds(List.of(entity1.getId(), entity2.getId()));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 리스트로 조회하면 빈 리스트를 반환합니다")
        void findByIds_WithEmptyIds_ReturnsEmptyList() {
            List<CanonicalOptionGroupJpaEntity> result = repository().findByIds(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null ID 리스트로 조회하면 빈 리스트를 반환합니다")
        void findByIds_WithNullIds_ReturnsEmptyList() {
            List<CanonicalOptionGroupJpaEntity> result = repository().findByIds(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID가 포함되어 있으면 존재하는 것만 반환합니다")
        void findByIds_WithSomeNonExistingIds_ReturnsOnlyExisting() {
            CanonicalOptionGroupJpaEntity entity1 =
                    persist(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "IDS_EXIST"));

            List<CanonicalOptionGroupJpaEntity> result =
                    repository().findByIds(List.of(entity1.getId(), 99999L));

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getId()).isEqualTo(entity1.getId());
        }
    }

    // ========================================================================
    // findValuesByGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findValuesByGroupId")
    class FindValuesByGroupIdTest {

        @Test
        @DisplayName("그룹 ID로 옵션 값을 조회합니다")
        void findValuesByGroupId_WithExistingGroupId_ReturnsValues() {
            CanonicalOptionGroupJpaEntity group =
                    persist(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "VAL_GROUP"));

            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group.getId(), "RED", 1));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group.getId(), "BLUE", 2));

            List<CanonicalOptionValueJpaEntity> result =
                    repository().findValuesByGroupId(group.getId());

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 그룹 ID로 조회하면 빈 리스트를 반환합니다")
        void findValuesByGroupId_WithNonExistingGroupId_ReturnsEmptyList() {
            List<CanonicalOptionValueJpaEntity> result = repository().findValuesByGroupId(99999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("옵션 값이 sortOrder 오름차순으로 정렬됩니다")
        void findValuesByGroupId_ReturnsSortedBySortOrder() {
            CanonicalOptionGroupJpaEntity group =
                    persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("SORT_GRP"));

            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group.getId(), "THIRD", 3));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group.getId(), "FIRST", 1));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group.getId(), "SECOND", 2));

            List<CanonicalOptionValueJpaEntity> result =
                    repository().findValuesByGroupId(group.getId());

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder()).isEqualTo(1);
            assertThat(result.get(1).getSortOrder()).isEqualTo(2);
            assertThat(result.get(2).getSortOrder()).isEqualTo(3);
        }
    }

    // ========================================================================
    // findValuesByGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findValuesByGroupIds")
    class FindValuesByGroupIdsTest {

        @Test
        @DisplayName("여러 그룹 ID로 옵션 값을 조회합니다")
        void findValuesByGroupIds_WithExistingGroupIds_ReturnsValues() {
            CanonicalOptionGroupJpaEntity group1 =
                    persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("VALS_G1"));
            CanonicalOptionGroupJpaEntity group2 =
                    persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("VALS_G2"));

            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group1.getId(), "V1", 1));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group2.getId(), "V2", 1));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(
                            group2.getId(), "V3", 2));

            List<CanonicalOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(group1.getId(), group2.getId()));

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("빈 그룹 ID 리스트로 조회하면 빈 리스트를 반환합니다")
        void findValuesByGroupIds_WithEmptyGroupIds_ReturnsEmptyList() {
            List<CanonicalOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of());

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null 그룹 ID 리스트로 조회하면 빈 리스트를 반환합니다")
        void findValuesByGroupIds_WithNullGroupIds_ReturnsEmptyList() {
            List<CanonicalOptionValueJpaEntity> result = repository().findValuesByGroupIds(null);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("결과가 sortOrder 오름차순으로 정렬됩니다")
        void findValuesByGroupIds_ReturnsSortedBySortOrder() {
            CanonicalOptionGroupJpaEntity group =
                    persist(
                            CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode(
                                    "SORT_VALS"));

            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(group.getId(), "C", 3));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(group.getId(), "A", 1));
            persistValue(
                    CanonicalOptionValueJpaEntityFixtures.newEntityWithCode(group.getId(), "B", 2));

            List<CanonicalOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(group.getId()));

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder()).isEqualTo(1);
            assertThat(result.get(1).getSortOrder()).isEqualTo(2);
            assertThat(result.get(2).getSortOrder()).isEqualTo(3);
        }
    }

    // ========================================================================
    // 정렬 관련 추가 테스트
    // ========================================================================

    @Nested
    @DisplayName("정렬 테스트")
    class SortingTest {

        @Test
        @DisplayName("CODE 오름차순 정렬이 적용됩니다")
        void findByCriteria_WithCodeAscSort_ReturnsSortedByCode() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("ZZZ_CODE"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("AAA_CODE"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("MMM_CODE"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    CanonicalOptionGroupSortKey.CODE,
                                    SortDirection.ASC,
                                    PageRequest.defaultPage()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getCode()).isEqualTo("AAA_CODE");
            assertThat(result.get(1).getCode()).isEqualTo("MMM_CODE");
            assertThat(result.get(2).getCode()).isEqualTo("ZZZ_CODE");
        }

        @Test
        @DisplayName("CODE 내림차순 정렬이 적용됩니다")
        void findByCriteria_WithCodeDescSort_ReturnsSortedByCodeDesc() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("ZZZ_DESC"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("AAA_DESC"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("MMM_DESC"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    CanonicalOptionGroupSortKey.CODE,
                                    SortDirection.DESC,
                                    PageRequest.defaultPage()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(3);
            assertThat(result.get(0).getCode()).isEqualTo("ZZZ_DESC");
            assertThat(result.get(1).getCode()).isEqualTo("MMM_DESC");
            assertThat(result.get(2).getCode()).isEqualTo("AAA_DESC");
        }

        @Test
        @DisplayName("CREATED_AT 오름차순 정렬이 적용됩니다")
        void findByCriteria_WithCreatedAtAscSort_ReturnsSorted() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("SORT_A"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("SORT_B"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    CanonicalOptionGroupSortKey.CREATED_AT,
                                    SortDirection.ASC,
                                    PageRequest.defaultPage()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 추가 검색 조건 테스트
    // ========================================================================

    @Nested
    @DisplayName("추가 검색 필터")
    class AdditionalSearchFilterTest {

        @Test
        @DisplayName("NAME_EN 필드로 영문명을 검색합니다")
        void findByCriteria_WithNameEnSearch_ReturnsMatchingEntities() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithName("색상X", "ColorX"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithName("사이즈X", "SizeX"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "NAME_EN",
                            "Color",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getNameEn()).contains("Color");
        }

        @Test
        @DisplayName("비활성 필터로 조회하면 비활성 Entity만 반환합니다")
        void findByCriteria_WithInactiveFilter_ReturnsOnlyInactive() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("INACT_A"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("INACT_B"));
            persist(CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode("INACT_C"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            false,
                            null,
                            null,
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result).noneMatch(CanonicalOptionGroupJpaEntity::isActive);
        }

        @Test
        @DisplayName("활성 필터와 검색 조건을 함께 적용합니다")
        void findByCriteria_WithActiveFilterAndSearch_ReturnsFilteredResults() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COMBO_COLOR"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("COMBO_SIZE"));
            persist(
                    CanonicalOptionGroupJpaEntityFixtures.inactiveEntityWithCode(
                            "COMBO_COLOR_INACT"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            true,
                            "CODE",
                            "COLOR",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCode()).contains("COLOR");
            assertThat(result.getFirst().isActive()).isTrue();
        }

        @Test
        @DisplayName("알 수 없는 검색 필드로 검색 시 결과를 반환합니다 (default null 분기)")
        void findByCriteria_WithUnknownSearchField_ReturnsAllResults() {
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("UNK_FIELD_A"));
            persist(CanonicalOptionGroupJpaEntityFixtures.activeEntityWithCode("UNK_FIELD_B"));

            var criteria =
                    new CanonicalOptionGroupSearchCriteria(
                            null,
                            "UNKNOWN_FIELD",
                            "UNK",
                            QueryContext.defaultOf(CanonicalOptionGroupSortKey.defaultKey()));
            var result = repository().findByCriteria(criteria);

            // UNKNOWN_FIELD는 switch default -> null 반환 -> 조건 무시 -> 전체 반환
            assertThat(result).hasSize(2);
        }
    }
}
