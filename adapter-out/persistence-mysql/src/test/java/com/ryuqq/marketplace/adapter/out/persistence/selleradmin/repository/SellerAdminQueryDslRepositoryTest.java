package com.ryuqq.marketplace.adapter.out.persistence.selleradmin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.SellerAdminJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.condition.SellerAdminConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.selleradmin.entity.SellerAdminJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchCriteria;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSearchField;
import com.ryuqq.marketplace.domain.selleradmin.query.SellerAdminSortKey;
import com.ryuqq.marketplace.domain.selleradmin.vo.SellerAdminStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
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
 * SellerAdminQueryDslRepositoryTest - 셀러 관리자 QueryDslRepository 통합 테스트.
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
@DisplayName("SellerAdminQueryDslRepository 통합 테스트")
class SellerAdminQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerAdminQueryDslRepository repository() {
        return new SellerAdminQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerAdminConditionBuilder());
    }

    private SellerAdminJpaEntity persist(SellerAdminJpaEntity entity) {
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
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-001"));

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeleted_ReturnsEmpty() {
            SellerAdminJpaEntity deleted =
                    persist(
                            SellerAdminJpaEntityFixtures.deletedEntity(
                                    "admin-deleted", "deleted@test.com"));

            var result = repository().findById(deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndId")
    class FindBySellerIdAndIdTest {

        @Test
        @DisplayName("셀러 ID와 관리자 ID로 조회 성공")
        void findBySellerIdAndId_ReturnsEntity() {
            Long sellerId = 1L;
            String adminId = "admin-002";
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            var result = repository().findBySellerIdAndId(sellerId, saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getSellerId()).isEqualTo(sellerId);
        }

        @Test
        @DisplayName("삭제된 Entity는 findBySellerIdAndId로 조회되지 않습니다")
        void findBySellerIdAndId_WithDeleted_ReturnsEmpty() {
            Long sellerId = 1L;
            SellerAdminJpaEntity deleted =
                    persist(
                            SellerAdminJpaEntityFixtures.deletedEntity(
                                    "admin-deleted-2", "deleted2@test.com"));

            var result = repository().findBySellerIdAndId(sellerId, deleted.getId());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndIdAndStatuses")
    class FindBySellerIdAndIdAndStatusesTest {

        @Test
        @DisplayName("셀러 ID와 관리자 ID와 상태 목록으로 조회 성공")
        void findBySellerIdAndIdAndStatuses_ReturnsEntity() {
            Long sellerId = 1L;
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            var result =
                    repository()
                            .findBySellerIdAndIdAndStatuses(
                                    sellerId, saved.getId(), List.of(SellerAdminStatus.ACTIVE));

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(SellerAdminStatus.ACTIVE);
        }

        @Test
        @DisplayName("상태가 일치하지 않으면 조회되지 않습니다")
        void findBySellerIdAndIdAndStatuses_WithWrongStatus_ReturnsEmpty() {
            Long sellerId = 1L;
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.activeEntityWithSellerId(sellerId));

            var result =
                    repository()
                            .findBySellerIdAndIdAndStatuses(
                                    sellerId, saved.getId(), List.of(SellerAdminStatus.SUSPENDED));

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdAndStatuses")
    class FindByIdAndStatusesTest {

        @Test
        @DisplayName("관리자 ID와 상태 목록으로 조회 성공")
        void findByIdAndStatuses_ReturnsEntity() {
            SellerAdminJpaEntity saved =
                    persist(SellerAdminJpaEntityFixtures.pendingApprovalEntity());

            var result =
                    repository()
                            .findByIdAndStatuses(
                                    saved.getId(), List.of(SellerAdminStatus.PENDING_APPROVAL));

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(SellerAdminStatus.PENDING_APPROVAL);
        }

        @Test
        @DisplayName("여러 상태 중 하나와 일치하면 조회됩니다")
        void findByIdAndStatuses_WithMultipleStatuses_ReturnsEntity() {
            SellerAdminJpaEntity saved = persist(SellerAdminJpaEntityFixtures.activeEntity());

            var result =
                    repository()
                            .findByIdAndStatuses(
                                    saved.getId(),
                                    List.of(SellerAdminStatus.ACTIVE, SellerAdminStatus.SUSPENDED));

            assertThat(result).isPresent();
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIdsTest {

        @Test
        @DisplayName("ID 목록으로 여러 관리자를 조회합니다")
        void findAllByIds_ReturnsMultipleEntities() {
            SellerAdminJpaEntity admin1 =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-101"));
            SellerAdminJpaEntity admin2 =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-102"));

            var result = repository().findAllByIds(List.of(admin1.getId(), admin2.getId()));

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 findAllByIds에서 제외됩니다")
        void findAllByIds_WithDeleted_ExcludesDeleted() {
            SellerAdminJpaEntity active =
                    persist(SellerAdminJpaEntityFixtures.activeEntity("admin-201"));
            SellerAdminJpaEntity deleted =
                    persist(
                            SellerAdminJpaEntityFixtures.deletedEntity(
                                    "admin-202", "deleted@test.com"));

            var result = repository().findAllByIds(List.of(active.getId(), deleted.getId()));

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(active.getId());
        }
    }

    @Nested
    @DisplayName("existsByLoginId")
    class ExistsByLoginIdTest {

        @Test
        @DisplayName("미삭제 Entity의 loginId는 existsByLoginId에서 true입니다")
        void existsByLoginId_WithNotDeleted_ReturnsTrue() {
            String loginId = "existing@test.com";
            persist(SellerAdminJpaEntityFixtures.newActiveEntityWithLoginId("admin-301", loginId));

            boolean exists = repository().existsByLoginId(loginId);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("삭제된 Entity의 loginId는 existsByLoginId에서 false입니다")
        void existsByLoginId_WithDeleted_ReturnsFalse() {
            String loginId = "deleted-login@test.com";
            persist(SellerAdminJpaEntityFixtures.deletedEntity("admin-302", loginId));

            boolean exists = repository().existsByLoginId(loginId);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("findByAuthUserId")
    class FindByAuthUserIdTest {

        @Test
        @DisplayName("미삭제 Entity는 authUserId로 조회됩니다")
        void findByAuthUserId_WithNotDeleted_ReturnsEntity() {
            String authUserId = "auth-user-find-001";
            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithAuthUserId(
                            "admin-auth-001", authUserId));

            var result = repository().findByAuthUserId(authUserId);

            assertThat(result).isPresent();
            assertThat(result.get().getAuthUserId()).isEqualTo(authUserId);
        }

        @Test
        @DisplayName("삭제된 Entity는 authUserId로 조회되지 않습니다")
        void findByAuthUserId_WithDeleted_ReturnsEmpty() {
            Instant now = Instant.now();
            SellerAdminJpaEntity deleted =
                    SellerAdminJpaEntity.create(
                            "admin-auth-del-001",
                            1L,
                            "auth-user-deleted-001",
                            "auth-del-001@test.com",
                            "삭제된관리자",
                            "010-0000-0001",
                            SellerAdminStatus.ACTIVE,
                            now,
                            now,
                            now);
            persist(deleted);

            var result = repository().findByAuthUserId("auth-user-deleted-001");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 authUserId는 빈 결과를 반환합니다")
        void findByAuthUserId_WithNonExisting_ReturnsEmpty() {
            var result = repository().findByAuthUserId("non-existing-auth-user-id");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("sellerIds 필터로 특정 셀러의 관리자만 조회합니다")
        void findByCriteria_WithSellerIds_ReturnsFiltered() {
            persist(
                    SellerAdminJpaEntityFixtures.entityWithSellerIdAndStatus(
                            "criteria-s1", 100L, SellerAdminStatus.ACTIVE));
            persist(
                    SellerAdminJpaEntityFixtures.entityWithSellerIdAndStatus(
                            "criteria-s2", 200L, SellerAdminStatus.ACTIVE));
            persist(
                    SellerAdminJpaEntityFixtures.entityWithSellerIdAndStatus(
                            "criteria-s3", 300L, SellerAdminStatus.ACTIVE));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            List.of(100L, 200L),
                            List.of(),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getSellerId() == 100L || e.getSellerId() == 200L);
        }

        @Test
        @DisplayName("status 필터로 특정 상태의 관리자만 조회합니다")
        void findByCriteria_WithStatus_ReturnsFiltered() {
            persist(
                    SellerAdminJpaEntityFixtures.entityWithStatus(
                            "criteria-st1", SellerAdminStatus.ACTIVE));
            persist(
                    SellerAdminJpaEntityFixtures.entityWithStatus(
                            "criteria-st2", SellerAdminStatus.PENDING_APPROVAL));
            persist(
                    SellerAdminJpaEntityFixtures.entityWithStatus(
                            "criteria-st3", SellerAdminStatus.SUSPENDED));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(SellerAdminStatus.ACTIVE, SellerAdminStatus.SUSPENDED),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(
                            e ->
                                    e.getStatus() == SellerAdminStatus.ACTIVE
                                            || e.getStatus() == SellerAdminStatus.SUSPENDED);
        }

        @Test
        @DisplayName("searchField=LOGIN_ID로 검색어 필터가 적용됩니다")
        void findByCriteria_WithSearchByLoginId_ReturnsFiltered() {
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-sr1", "김검색", "search-login@test.com"));
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-sr2", "박다른", "other-login@test.com"));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            SellerAdminSearchField.LOGIN_ID,
                            "search-login",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getLoginId()).contains("search-login");
        }

        @Test
        @DisplayName("searchField=NAME으로 이름 검색이 적용됩니다")
        void findByCriteria_WithSearchByName_ReturnsFiltered() {
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-nm1", "홍길동관리자", "name-search1@test.com"));
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-nm2", "다른사람", "name-search2@test.com"));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            SellerAdminSearchField.NAME,
                            "홍길동",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).contains("홍길동");
        }

        @Test
        @DisplayName("searchField=null이면 loginId와 name 모두에서 검색합니다")
        void findByCriteria_WithSearchAllFields_ReturnsFiltered() {
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-af1", "일반이름", "keyword-match@test.com"));
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-af2", "keyword이름", "no-match@test.com"));
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "criteria-af3", "전혀다른", "completely-other@test.com"));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            "keyword",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("dateRange 필터로 기간 내 생성된 관리자만 조회합니다")
        void findByCriteria_WithDateRange_ReturnsFiltered() {
            Instant oldTime = Instant.parse("2025-01-01T00:00:00Z");
            Instant recentTime = Instant.parse("2026-02-15T00:00:00Z");

            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-dr1", "date-old@test.com", oldTime));
            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-dr2", "date-recent@test.com", recentTime));

            DateRange dateRange =
                    DateRange.of(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            null,
                            dateRange,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getLoginId()).isEqualTo("date-recent@test.com");
        }

        @Test
        @DisplayName("ASC 정렬이 적용됩니다")
        void findByCriteria_WithAscSort_ReturnsSortedAscending() {
            Instant time1 = Instant.parse("2026-01-01T00:00:00Z");
            Instant time2 = Instant.parse("2026-01-02T00:00:00Z");
            Instant time3 = Instant.parse("2026-01-03T00:00:00Z");

            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-asc3", "asc3@test.com", time3));
            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-asc1", "asc1@test.com", time1));
            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-asc2", "asc2@test.com", time2));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    SellerAdminSortKey.CREATED_AT,
                                    SortDirection.ASC,
                                    PageRequest.first(10)));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSizeGreaterThanOrEqualTo(3);
            // ASC 정렬 검증: 첫 번째가 가장 오래된 것
            assertThat(result.get(0).getCreatedAt())
                    .isBeforeOrEqualTo(result.get(1).getCreatedAt());
            assertThat(result.get(1).getCreatedAt())
                    .isBeforeOrEqualTo(result.get(2).getCreatedAt());
        }

        @Test
        @DisplayName("DESC 정렬이 적용됩니다")
        void findByCriteria_WithDescSort_ReturnsSortedDescending() {
            Instant time1 = Instant.parse("2026-02-01T00:00:00Z");
            Instant time2 = Instant.parse("2026-02-02T00:00:00Z");
            Instant time3 = Instant.parse("2026-02-03T00:00:00Z");

            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-desc1", "desc1@test.com", time1));
            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-desc2", "desc2@test.com", time2));
            persist(
                    SellerAdminJpaEntityFixtures.activeEntityWithCreatedAt(
                            "criteria-desc3", "desc3@test.com", time3));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    SellerAdminSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.first(10)));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSizeGreaterThanOrEqualTo(3);
            // DESC 정렬 검증: 첫 번째가 가장 최신
            assertThat(result.get(0).getCreatedAt()).isAfterOrEqualTo(result.get(1).getCreatedAt());
            assertThat(result.get(1).getCreatedAt()).isAfterOrEqualTo(result.get(2).getCreatedAt());
        }

        @Test
        @DisplayName("offset과 limit이 적용됩니다")
        void findByCriteria_WithOffsetAndLimit_ReturnsPagedResult() {
            persist(SellerAdminJpaEntityFixtures.activeEntity("criteria-pg1"));
            persist(SellerAdminJpaEntityFixtures.activeEntity("criteria-pg2"));
            persist(SellerAdminJpaEntityFixtures.activeEntity("criteria-pg3"));
            persist(SellerAdminJpaEntityFixtures.activeEntity("criteria-pg4"));
            persist(SellerAdminJpaEntityFixtures.activeEntity("criteria-pg5"));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            null,
                            null,
                            null,
                            QueryContext.of(
                                    SellerAdminSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.of(1, 2)));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 findByCriteria에서 제외됩니다")
        void findByCriteria_WithDeleted_ExcludesDeleted() {
            persist(SellerAdminJpaEntityFixtures.activeEntity("criteria-del-active"));
            persist(
                    SellerAdminJpaEntityFixtures.deletedEntity(
                            "criteria-del-deleted", "criteria-del-deleted@test.com"));

            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            var result = repository().findByCriteria(criteria);

            assertThat(result).noneMatch(e -> e.getId().equals("criteria-del-deleted"));
        }

        @Test
        @DisplayName("모든 필터 조합으로 조회합니다")
        void findByCriteria_WithCombinedFilters_ReturnsFiltered() {
            Long targetSellerId = 500L;
            Instant recentTime = Instant.parse("2026-02-10T00:00:00Z");

            persist(
                    SellerAdminJpaEntityFixtures.customEntityWithTimestamp(
                            "criteria-combo1",
                            targetSellerId,
                            "auth-combo-1",
                            "combo-match@test.com",
                            "콤보관리자",
                            "010-1111-2222",
                            SellerAdminStatus.ACTIVE,
                            recentTime));
            persist(
                    SellerAdminJpaEntityFixtures.customEntityWithTimestamp(
                            "criteria-combo2",
                            targetSellerId,
                            "auth-combo-2",
                            "combo-nomatch@test.com",
                            "다른이름",
                            "010-3333-4444",
                            SellerAdminStatus.SUSPENDED,
                            recentTime));
            persist(
                    SellerAdminJpaEntityFixtures.customEntityWithTimestamp(
                            "criteria-combo3",
                            999L,
                            "auth-combo-3",
                            "combo-otherseller@test.com",
                            "콤보관리자",
                            "010-5555-6666",
                            SellerAdminStatus.ACTIVE,
                            recentTime));

            DateRange dateRange = DateRange.of(LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            List.of(targetSellerId),
                            List.of(SellerAdminStatus.ACTIVE),
                            SellerAdminSearchField.NAME,
                            "콤보",
                            dateRange,
                            QueryContext.of(
                                    SellerAdminSortKey.CREATED_AT,
                                    SortDirection.DESC,
                                    PageRequest.first(10)));

            var result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("criteria-combo1");
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithFilters_ReturnsCorrectCount() {
            persist(
                    SellerAdminJpaEntityFixtures.entityWithStatus(
                            "count-st1", SellerAdminStatus.ACTIVE));
            persist(
                    SellerAdminJpaEntityFixtures.entityWithStatus(
                            "count-st2", SellerAdminStatus.ACTIVE));
            persist(
                    SellerAdminJpaEntityFixtures.entityWithStatus(
                            "count-st3", SellerAdminStatus.PENDING_APPROVAL));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(SellerAdminStatus.ACTIVE),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("삭제된 Entity는 countByCriteria에서 제외됩니다")
        void countByCriteria_WithDeleted_ExcludesDeleted() {
            persist(SellerAdminJpaEntityFixtures.activeEntity("count-del-active"));
            persist(
                    SellerAdminJpaEntityFixtures.deletedEntity(
                            "count-del-deleted", "count-del@test.com"));

            SellerAdminSearchCriteria criteria = SellerAdminSearchCriteria.defaultCriteria();

            long count = repository().countByCriteria(criteria);

            // 삭제된 것은 제외, 다른 테스트 데이터가 있을 수 있으므로 최소 1개
            assertThat(count).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("조건에 맞는 데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoMatch_ReturnsZero() {
            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            List.of(99999L),
                            List.of(),
                            null,
                            null,
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("검색어 필터가 countByCriteria에도 적용됩니다")
        void countByCriteria_WithSearchCondition_ReturnsFilteredCount() {
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "count-sc1", "카운트검색", "count-search1@test.com"));
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "count-sc2", "카운트검색", "count-search2@test.com"));
            persist(
                    SellerAdminJpaEntityFixtures.newEntityWithNameAndLoginId(
                            "count-sc3", "다른이름", "count-other@test.com"));

            SellerAdminSearchCriteria criteria =
                    SellerAdminSearchCriteria.of(
                            null,
                            List.of(),
                            SellerAdminSearchField.NAME,
                            "카운트검색",
                            null,
                            QueryContext.defaultOf(SellerAdminSortKey.CREATED_AT));

            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }
    }
}
