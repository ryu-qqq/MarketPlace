package com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.SellerApplicationJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.condition.SellerApplicationConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.sellerapplication.entity.SellerApplicationJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchCriteria;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSearchField;
import com.ryuqq.marketplace.domain.sellerapplication.query.SellerApplicationSortKey;
import com.ryuqq.marketplace.domain.sellerapplication.vo.ApplicationStatus;
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
 * SellerApplicationQueryDslRepositoryTest - 입점 신청 QueryDslRepository 통합 테스트.
 *
 * <p>SellerApplication은 soft-delete 대상이 아니지만, 조회 쿼리 검증을 수행합니다.
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
@DisplayName("SellerApplicationQueryDslRepository 통합 테스트")
class SellerApplicationQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerApplicationQueryDslRepository repository() {
        return new SellerApplicationQueryDslRepository(
                new JPAQueryFactory(entityManager), new SellerApplicationConditionBuilder());
    }

    private SellerApplicationJpaEntity persist(SellerApplicationJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 입점 신청을 조회합니다")
        void findById_ReturnsEntity() {
            SellerApplicationJpaEntity saved =
                    persist(SellerApplicationJpaEntityFixtures.pendingEntity());

            var result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 Optional을 반환합니다")
        void findById_WithNonExistent_ReturnsEmpty() {
            var result = repository().findById(999L);

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("existsById")
    class ExistsByIdTest {

        @Test
        @DisplayName("존재하는 ID는 true를 반환합니다")
        void existsById_WithExisting_ReturnsTrue() {
            SellerApplicationJpaEntity saved =
                    persist(SellerApplicationJpaEntityFixtures.pendingEntity());

            boolean exists = repository().existsById(saved.getId());

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 false를 반환합니다")
        void existsById_WithNonExistent_ReturnsFalse() {
            boolean exists = repository().existsById(999L);

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("existsPendingByRegistrationNumber")
    class ExistsPendingByRegistrationNumberTest {

        @Test
        @DisplayName("PENDING 상태의 사업자등록번호가 존재하면 true를 반환합니다")
        void existsPendingByRegistrationNumber_WithPending_ReturnsTrue() {
            String regNumber = "123-45-67890";
            persist(SellerApplicationJpaEntityFixtures.pendingEntity(null, regNumber));

            boolean exists = repository().existsPendingByRegistrationNumber(regNumber);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("PENDING이 아닌 상태의 사업자등록번호는 false를 반환합니다")
        void existsPendingByRegistrationNumber_WithApproved_ReturnsFalse() {
            // given
            // APPROVED 상태 신청 생성 (고유 사업자등록번호 사용)
            SellerApplicationJpaEntity approved =
                    persist(SellerApplicationJpaEntityFixtures.approvedEntity(1L));

            // when
            // APPROVED 상태의 사업자등록번호로 PENDING 존재 여부 확인
            boolean exists =
                    repository()
                            .existsPendingByRegistrationNumber(approved.getRegistrationNumber());

            // then
            // APPROVED 상태이므로 PENDING 검색에서는 false 반환
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 사업자등록번호는 false를 반환합니다")
        void existsPendingByRegistrationNumber_WithNonExistent_ReturnsFalse() {
            boolean exists = repository().existsPendingByRegistrationNumber("000-00-00000");

            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("Integration - Status Filter")
    class StatusFilterTest {

        @Test
        @DisplayName("PENDING 상태만 조회됩니다")
        void existsPendingByRegistrationNumber_OnlyPending() {
            String regNumber = "111-22-33333";
            persist(SellerApplicationJpaEntityFixtures.pendingEntity(null, regNumber));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(2L)); // 다른 번호

            boolean exists = repository().existsPendingByRegistrationNumber(regNumber);

            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("REJECTED 상태는 PENDING 검색에서 제외됩니다")
        void existsPendingByRegistrationNumber_ExcludesRejected() {
            String regNumber = "222-33-44444";
            SellerApplicationJpaEntity rejected =
                    persist(SellerApplicationJpaEntityFixtures.rejectedEntity());
            // rejectedEntity는 DEFAULT_REGISTRATION_NUMBER 사용

            // 다른 번호로 테스트
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            regNumber));

            boolean existsForRejected =
                    repository()
                            .existsPendingByRegistrationNumber(
                                    SellerApplicationJpaEntityFixtures.DEFAULT_REGISTRATION_NUMBER);
            boolean existsForPending = repository().existsPendingByRegistrationNumber(regNumber);

            assertThat(existsForRejected).isFalse(); // REJECTED는 제외
            assertThat(existsForPending).isTrue(); // PENDING만 조회
        }
    }

    // ========================================================================
    // findByApprovedSellerId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByApprovedSellerId")
    class FindByApprovedSellerIdTest {

        @Test
        @DisplayName("승인된 셀러 ID로 입점 신청을 조회합니다")
        void findByApprovedSellerId_WithExistingId_ReturnsEntity() {
            Long sellerId = 100L;
            SellerApplicationJpaEntity saved =
                    persist(SellerApplicationJpaEntityFixtures.approvedEntity(sellerId));

            var result = repository().findByApprovedSellerId(sellerId);

            assertThat(result).isPresent();
            assertThat(result.get().getApprovedSellerId()).isEqualTo(sellerId);
            assertThat(result.get().getStatus()).isEqualTo(ApplicationStatus.APPROVED);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 ID는 빈 Optional을 반환합니다")
        void findByApprovedSellerId_WithNonExistingId_ReturnsEmpty() {
            var result = repository().findByApprovedSellerId(99999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 셀러 ID로 승인된 신청은 조회되지 않습니다")
        void findByApprovedSellerId_WithDifferentSellerId_ReturnsEmpty() {
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(200L));

            var result = repository().findByApprovedSellerId(999L);

            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 조회하면 전체 Entity를 반환합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsAll() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "AAA-11-11111"));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(10L));
            persist(SellerApplicationJpaEntityFixtures.rejectedEntity());

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.defaultCriteria();
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("PENDING 상태 필터로 조회하면 PENDING만 반환합니다")
        void findByCriteria_WithPendingStatusFilter_ReturnsPendingOnly() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "BBB-22-22222"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "CCC-33-33333"));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(20L));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.pendingOnly();
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == ApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("여러 상태 필터로 조회합니다")
        void findByCriteria_WithMultipleStatusFilter_ReturnsMatching() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "DDD-44-44444"));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(30L));
            persist(SellerApplicationJpaEntityFixtures.rejectedEntity("서류 미비"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(ApplicationStatus.PENDING, ApplicationStatus.APPROVED),
                            null,
                            null,
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(
                            e ->
                                    e.getStatus() == ApplicationStatus.PENDING
                                            || e.getStatus() == ApplicationStatus.APPROVED);
        }

        @Test
        @DisplayName("회사명으로 검색합니다")
        void findByCriteria_WithCompanyNameSearch_ReturnsMatching() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "알파컴퍼니", "김대표", "EEE-55-55555"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "베타주식회사", "이대표", "FFF-66-66666"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            SellerApplicationSearchField.COMPANY_NAME,
                            "알파",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCompanyName()).contains("알파");
        }

        @Test
        @DisplayName("대표자명으로 검색합니다")
        void findByCriteria_WithRepresentativeSearch_ReturnsMatching() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "감마회사", "박철수", "GGG-77-77777"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "델타회사", "최영희", "HHH-88-88888"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            SellerApplicationSearchField.REPRESENTATIVE_NAME,
                            "박철수",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getRepresentative()).contains("박철수");
        }

        @Test
        @DisplayName("검색 필드 없이 검색어만 입력하면 회사명 또는 대표자명으로 전체 검색합니다")
        void findByCriteria_WithSearchWordOnly_SearchesBothFields() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "유니크컴퍼니", "김가나", "III-99-99999"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "일반회사", "유니크대표", "JJJ-10-10101"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "별도회사", "별도대표", "KKK-11-11112"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            null,
                            "유니크",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("상태 필터와 검색 조건을 함께 적용합니다")
        void findByCriteria_WithStatusAndSearch_ReturnsFilteredResults() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "타겟회사", "김타겟", "LLL-12-12121"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "다른회사", "이다른", "MMM-13-13131"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(ApplicationStatus.PENDING),
                            SellerApplicationSearchField.COMPANY_NAME,
                            "타겟",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCompanyName()).contains("타겟");
            assertThat(result.getFirst().getStatus()).isEqualTo(ApplicationStatus.PENDING);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoMatchingResults_ReturnsEmpty() {
            persist(SellerApplicationJpaEntityFixtures.pendingEntity());

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            SellerApplicationSearchField.COMPANY_NAME,
                            "존재하지않는회사명",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("페이징이 적용됩니다")
        void findByCriteria_WithPaging_ReturnsPagedResults() {
            for (int i = 0; i < 5; i++) {
                persist(
                        SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                                "PAG-"
                                        + String.format("%02d", i)
                                        + "-"
                                        + String.format("%05d", i)));
            }

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            null,
                            null,
                            QueryContext.of(
                                    SellerApplicationSortKey.defaultKey(),
                                    SortDirection.DESC,
                                    PageRequest.of(0, 3)));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("두 번째 페이지를 조회합니다")
        void findByCriteria_WithSecondPage_ReturnsRemainingResults() {
            for (int i = 0; i < 5; i++) {
                persist(
                        SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                                "PG2-"
                                        + String.format("%02d", i)
                                        + "-"
                                        + String.format("%05d", i)));
            }

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            null,
                            null,
                            QueryContext.of(
                                    SellerApplicationSortKey.defaultKey(),
                                    SortDirection.DESC,
                                    PageRequest.of(1, 3)));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("오름차순 정렬이 적용됩니다")
        void findByCriteria_WithAscSort_ReturnsSortedResults() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "ASC-00-00001"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "ASC-00-00002"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            null,
                            null,
                            QueryContext.of(
                                    SellerApplicationSortKey.APPLIED_AT,
                                    SortDirection.ASC,
                                    PageRequest.defaultPage()));
            List<SellerApplicationJpaEntity> result = repository().findByCriteria(criteria);

            assertThat(result).hasSize(2);
            // ASC 정렬이므로 첫 번째 항목의 appliedAt이 두 번째보다 이전이어야 합니다
            assertThat(result.get(0).getAppliedAt())
                    .isBeforeOrEqualTo(result.get(1).getAppliedAt());
        }
    }

    // ========================================================================
    // countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 전체 개수를 반환합니다")
        void countByCriteria_WithDefaultCriteria_ReturnsTotalCount() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "CNT-01-00001"));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(40L));
            persist(SellerApplicationJpaEntityFixtures.rejectedEntity("사유1"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.defaultCriteria();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("상태 필터로 해당 상태의 개수만 반환합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "CNT-02-00001"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithRegistrationNumber(
                            "CNT-02-00002"));
            persist(SellerApplicationJpaEntityFixtures.approvedEntity(50L));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.pendingOnly();
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("검색 조건으로 일치하는 개수를 반환합니다")
        void countByCriteria_WithSearchCondition_ReturnsMatchingCount() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "카운트대상회사", "김카운트", "CNT-03-00001"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "다른이름회사", "이다름", "CNT-03-00002"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            SellerApplicationSearchField.COMPANY_NAME,
                            "카운트대상",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(),
                            SellerApplicationSearchField.COMPANY_NAME,
                            "없는회사명",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isZero();
        }

        @Test
        @DisplayName("상태 필터와 검색 조건을 함께 적용한 개수를 반환합니다")
        void countByCriteria_WithStatusAndSearch_ReturnsFilteredCount() {
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "복합테스트회사", "김복합", "CNT-04-00001"));
            persist(
                    SellerApplicationJpaEntityFixtures.pendingEntityWithFullInfo(
                            "복합다른회사", "이다름", "CNT-04-00002"));

            SellerApplicationSearchCriteria criteria =
                    SellerApplicationSearchCriteria.of(
                            List.of(ApplicationStatus.PENDING),
                            SellerApplicationSearchField.COMPANY_NAME,
                            "복합테스트",
                            QueryContext.defaultOf(SellerApplicationSortKey.defaultKey()));
            long count = repository().countByCriteria(criteria);

            assertThat(count).isEqualTo(1);
        }
    }
}
