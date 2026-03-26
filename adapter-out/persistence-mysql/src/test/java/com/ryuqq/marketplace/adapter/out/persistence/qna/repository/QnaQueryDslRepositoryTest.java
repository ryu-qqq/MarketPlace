package com.ryuqq.marketplace.adapter.out.persistence.qna.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.qna.QnaJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.qna.entity.QnaJpaEntity;
import com.ryuqq.marketplace.application.qna.dto.query.QnaSearchCondition;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * QnaQueryDslRepositoryTest - Qna QueryDslRepository 통합 테스트.
 *
 * <p>QueryDSL 기반 Qna 조회 메서드의 동작을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
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
@DisplayName("QnaQueryDslRepository 통합 테스트")
class QnaQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private QnaQueryDslRepository repository() {
        return new QnaQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private QnaJpaEntity persist(QnaJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Qna Entity를 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            QnaJpaEntity saved = persist(QnaJpaEntityFixtures.pendingEntity());

            // when
            Optional<QnaJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
            assertThat(result.get().getSellerId()).isEqualTo(QnaJpaEntityFixtures.DEFAULT_SELLER_ID);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<QnaJpaEntity> result = repository().findById(9999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("PENDING 상태의 Qna를 ID로 조회합니다")
        void findById_WithPendingEntity_ReturnsCorrectStatus() {
            // given
            QnaJpaEntity saved = persist(QnaJpaEntityFixtures.pendingEntity());

            // when
            Optional<QnaJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(QnaJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("ANSWERED 상태의 Qna를 ID로 조회합니다")
        void findById_WithAnsweredEntity_ReturnsCorrectStatus() {
            // given
            QnaJpaEntity saved = persist(QnaJpaEntityFixtures.answeredEntity());

            // when
            Optional<QnaJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo(QnaJpaEntity.Status.ANSWERED);
        }
    }

    // ========================================================================
    // 2. findBySellerIdAndStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySellerIdAndStatus")
    class FindBySellerIdAndStatusTest {

        @Test
        @DisplayName("sellerId로 Qna 목록을 조회합니다")
        void findBySellerIdAndStatus_WithSellerId_ReturnsEntities() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            // when
            List<QnaJpaEntity> result = repository().findBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, null, 0, 10);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .allMatch(e -> e.getSellerId() == QnaJpaEntityFixtures.DEFAULT_SELLER_ID);
        }

        @Test
        @DisplayName("status 필터로 PENDING 상태만 조회합니다")
        void findBySellerIdAndStatus_WithPendingStatus_ReturnsPendingOnly() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            // when
            List<QnaJpaEntity> result = repository().findBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, QnaJpaEntity.Status.PENDING, 0, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(QnaJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("status 필터로 ANSWERED 상태만 조회합니다")
        void findBySellerIdAndStatus_WithAnsweredStatus_ReturnsAnsweredOnly() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            // when
            List<QnaJpaEntity> result = repository().findBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, QnaJpaEntity.Status.ANSWERED, 0, 10);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getStatus() == QnaJpaEntity.Status.ANSWERED);
        }

        @Test
        @DisplayName("offset과 limit이 올바르게 적용됩니다")
        void findBySellerIdAndStatus_WithOffsetAndLimit_AppliesPagination() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());

            // when
            List<QnaJpaEntity> result = repository().findBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, null, 0, 2);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("존재하지 않는 sellerId로 조회 시 빈 목록을 반환합니다")
        void findBySellerIdAndStatus_WithNonExistentSellerId_ReturnsEmpty() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());

            // when
            List<QnaJpaEntity> result = repository().findBySellerIdAndStatus(9999L, null, 0, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("결과를 ID 내림차순으로 반환합니다")
        void findBySellerIdAndStatus_ReturnsResultsOrderedByIdDesc() {
            // given
            QnaJpaEntity first = persist(QnaJpaEntityFixtures.pendingEntity());
            QnaJpaEntity second = persist(QnaJpaEntityFixtures.pendingEntity());
            QnaJpaEntity third = persist(QnaJpaEntityFixtures.pendingEntity());

            // when
            List<QnaJpaEntity> result = repository().findBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, null, 0, 10);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
            assertThat(result.get(1).getId()).isGreaterThan(result.get(2).getId());
        }
    }

    // ========================================================================
    // 3. countBySellerIdAndStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("countBySellerIdAndStatus")
    class CountBySellerIdAndStatusTest {

        @Test
        @DisplayName("sellerId로 전체 개수를 조회합니다")
        void countBySellerIdAndStatus_WithSellerId_ReturnsTotal() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());
            persist(QnaJpaEntityFixtures.closedEntity(null));

            // when
            long count = repository().countBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, null);

            // then
            assertThat(count).isEqualTo(3L);
        }

        @Test
        @DisplayName("status 필터로 개수를 조회합니다")
        void countBySellerIdAndStatus_WithStatus_ReturnsFilteredCount() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            // when
            long count = repository().countBySellerIdAndStatus(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID, QnaJpaEntity.Status.PENDING);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없을 때 0을 반환합니다")
        void countBySellerIdAndStatus_WhenNoData_ReturnsZero() {
            // when
            long count = repository().countBySellerIdAndStatus(9999L, null);

            // then
            assertThat(count).isZero();
        }
    }

    // ========================================================================
    // 4. search 테스트
    // ========================================================================

    @Nested
    @DisplayName("search")
    class SearchTest {

        @Test
        @DisplayName("sellerId 조건으로 Qna를 검색합니다")
        void search_WithSellerId_ReturnsMatchingEntities() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("status 조건으로 Qna를 검색합니다")
        void search_WithStatus_ReturnsFilteredEntities() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    QnaStatus.PENDING, null, null, null, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo(QnaJpaEntity.Status.PENDING);
        }

        @Test
        @DisplayName("qnaType 조건으로 Qna를 검색합니다")
        void search_WithQnaType_ReturnsFilteredEntities() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, QnaType.PRODUCT, null, null, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getQnaType()).isEqualTo(QnaType.PRODUCT.name());
        }

        @Test
        @DisplayName("keyword로 questionContent 또는 questionTitle에서 검색합니다")
        void search_WithKeyword_ReturnsMatchingEntities() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    null, null, null,
                    QnaJpaEntityFixtures.DEFAULT_QUESTION_CONTENT.substring(0, 5),
                    null, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("cursorId보다 작은 ID의 Qna만 조회합니다 (커서 페이지네이션)")
        void search_WithCursorId_ReturnsEntitiesBeforeCursor() {
            // given
            QnaJpaEntity first = persist(QnaJpaEntityFixtures.pendingEntity());
            QnaJpaEntity second = persist(QnaJpaEntityFixtures.pendingEntity());
            QnaJpaEntity third = persist(QnaJpaEntityFixtures.pendingEntity());

            Long cursorId = third.getId();

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, cursorId, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getId() < cursorId);
        }

        @Test
        @DisplayName("fromDate 조건으로 생성일 이후의 Qna를 검색합니다")
        void search_WithFromDate_ReturnsEntitiesAfterDate() {
            // given
            persist(QnaJpaEntityFixtures.answeredEntity());

            Instant fromDate = Instant.now().minusSeconds(7200 * 2);

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, fromDate, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("size 제한이 올바르게 적용됩니다")
        void search_WithSizeLimit_ReturnsLimitedResults() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, null, 2);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("조건에 맞는 데이터가 없으면 빈 목록을 반환합니다")
        void search_WhenNoMatch_ReturnsEmptyList() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    9999L, null, null, null, null, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("검색 결과를 ID 내림차순으로 반환합니다")
        void search_ReturnsResultsOrderedByIdDesc() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, null, 10);

            // when
            List<QnaJpaEntity> result = repository().search(condition);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getId()).isGreaterThan(result.get(1).getId());
        }
    }

    // ========================================================================
    // 5. countByCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCondition")
    class CountByConditionTest {

        @Test
        @DisplayName("sellerId 조건으로 총 개수를 조회합니다")
        void countByCondition_WithSellerId_ReturnsCount() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, null, 10);

            // when
            long count = repository().countByCondition(condition);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("cursorId는 카운트에서 제외됩니다")
        void countByCondition_CursorIdIsExcludedFromCount() {
            // given
            QnaJpaEntity first = persist(QnaJpaEntityFixtures.pendingEntity());
            QnaJpaEntity second = persist(QnaJpaEntityFixtures.pendingEntity());
            QnaJpaEntity third = persist(QnaJpaEntityFixtures.pendingEntity());

            QnaSearchCondition conditionWithCursor = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, third.getId(), 10);

            QnaSearchCondition conditionWithoutCursor = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    null, null, null, null, null, null, 10);

            // when
            long countWithCursor = repository().countByCondition(conditionWithCursor);
            long countWithoutCursor = repository().countByCondition(conditionWithoutCursor);

            // then
            assertThat(countWithCursor).isEqualTo(countWithoutCursor);
        }

        @Test
        @DisplayName("status 조건으로 개수를 조회합니다")
        void countByCondition_WithStatus_ReturnsFilteredCount() {
            // given
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.pendingEntity());
            persist(QnaJpaEntityFixtures.answeredEntity());

            QnaSearchCondition condition = new QnaSearchCondition(
                    QnaJpaEntityFixtures.DEFAULT_SELLER_ID,
                    QnaStatus.PENDING, null, null, null, null, null, 10);

            // when
            long count = repository().countByCondition(condition);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없을 때 0을 반환합니다")
        void countByCondition_WhenNoData_ReturnsZero() {
            // given
            QnaSearchCondition condition = new QnaSearchCondition(
                    9999L, null, null, null, null, null, null, 10);

            // when
            long count = repository().countByCondition(condition);

            // then
            assertThat(count).isZero();
        }
    }
}
