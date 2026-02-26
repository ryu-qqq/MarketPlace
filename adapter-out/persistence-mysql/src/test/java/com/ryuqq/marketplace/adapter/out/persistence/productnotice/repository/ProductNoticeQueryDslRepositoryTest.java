package com.ryuqq.marketplace.adapter.out.persistence.productnotice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.ProductNoticeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.condition.ProductNoticeConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
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
 * ProductNoticeQueryDslRepositoryTest - 상품 고시정보 QueryDslRepository 통합 테스트.
 *
 * <p>productGroupId 조건 검색 및 Entry 연관 조회를 검증합니다.
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
@DisplayName("ProductNoticeQueryDslRepository 통합 테스트")
class ProductNoticeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductNoticeQueryDslRepository repository() {
        return new ProductNoticeQueryDslRepository(
                new JPAQueryFactory(entityManager), new ProductNoticeConditionBuilder());
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("ProductGroupId로 ProductNotice를 조회합니다")
        void findByProductGroupId_WithExistingId_ReturnsEntity() {
            // given
            Long productGroupId = 1L;
            ProductNoticeJpaEntity saved =
                    persist(ProductNoticeJpaEntityFixtures.newEntity(productGroupId));

            // when
            Optional<ProductNoticeJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductGroupId()).isEqualTo(productGroupId);
        }

        @Test
        @DisplayName("존재하지 않는 ProductGroupId는 빈 결과를 반환합니다")
        void findByProductGroupId_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductNoticeJpaEntity> result = repository().findByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findEntriesByProductNoticeId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findEntriesByProductNoticeId")
    class FindEntriesByProductNoticeIdTest {

        @Test
        @DisplayName("ProductNoticeId로 연관된 Entry 목록을 조회합니다")
        void findEntriesByProductNoticeId_WithExistingId_ReturnsEntries() {
            // given
            ProductNoticeJpaEntity notice = persist(ProductNoticeJpaEntityFixtures.newEntity());
            Long noticeId = notice.getId();

            persist(ProductNoticeJpaEntityFixtures.entryEntity(noticeId, 100L, "제조국"));
            persist(ProductNoticeJpaEntityFixtures.entryEntity(noticeId, 101L, "제조사"));

            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository().findEntriesByProductNoticeId(noticeId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getProductNoticeId().equals(noticeId));
        }

        @Test
        @DisplayName("다른 ProductNoticeId의 Entry는 조회에서 제외됩니다")
        void findEntriesByProductNoticeId_WithDifferentNoticeId_ReturnsCorrectEntries() {
            // given
            ProductNoticeJpaEntity notice1 = persist(ProductNoticeJpaEntityFixtures.newEntity());
            ProductNoticeJpaEntity notice2 = persist(ProductNoticeJpaEntityFixtures.newEntity());

            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice1.getId(), 100L, "제조국"));
            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice2.getId(), 200L, "수입사"));

            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository().findEntriesByProductNoticeId(notice1.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductNoticeId()).isEqualTo(notice1.getId());
        }

        @Test
        @DisplayName("Entry가 없으면 빈 리스트를 반환합니다")
        void findEntriesByProductNoticeId_WithNoEntries_ReturnsEmpty() {
            // given
            ProductNoticeJpaEntity notice = persist(ProductNoticeJpaEntityFixtures.newEntity());

            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository().findEntriesByProductNoticeId(notice.getId());

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdIn")
    class FindByProductGroupIdInTest {

        @Test
        @DisplayName("여러 ProductGroupId로 Notice 목록을 배치 조회합니다")
        void findByProductGroupIdIn_WithMultipleGroupIds_ReturnsAllNotices() {
            // given
            Long productGroupId1 = 10L;
            Long productGroupId2 = 11L;
            persist(ProductNoticeJpaEntityFixtures.newEntity(productGroupId1));
            persist(ProductNoticeJpaEntityFixtures.newEntity(productGroupId2));

            // when
            List<ProductNoticeJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(productGroupId1, productGroupId2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ProductNoticeJpaEntity::getProductGroupId)
                    .containsExactlyInAnyOrder(productGroupId1, productGroupId2);
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithEmptyList_ReturnsEmpty() {
            // when
            List<ProductNoticeJpaEntity> result = repository().findByProductGroupIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 Notice가 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithNoMatchingNotices_ReturnsEmpty() {
            // when
            List<ProductNoticeJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(999999L, 1000000L));

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findEntriesByProductNoticeIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findEntriesByProductNoticeIds")
    class FindEntriesByProductNoticeIdsTest {

        @Test
        @DisplayName("여러 ProductNoticeId로 Entry 목록을 배치 조회합니다")
        void findEntriesByProductNoticeIds_WithMultipleNoticeIds_ReturnsAllEntries() {
            // given
            ProductNoticeJpaEntity notice1 = persist(ProductNoticeJpaEntityFixtures.newEntity());
            ProductNoticeJpaEntity notice2 = persist(ProductNoticeJpaEntityFixtures.newEntity());

            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice1.getId(), 100L, "제조국"));
            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice1.getId(), 101L, "제조사"));
            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice2.getId(), 200L, "원산지"));

            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository()
                            .findEntriesByProductNoticeIds(
                                    List.of(notice1.getId(), notice2.getId()));

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("특정 Notice의 Entry만 조회하고 다른 Notice Entry는 포함하지 않습니다")
        void findEntriesByProductNoticeIds_WithSubsetOfIds_ReturnsOnlyMatchingEntries() {
            // given
            ProductNoticeJpaEntity notice1 = persist(ProductNoticeJpaEntityFixtures.newEntity());
            ProductNoticeJpaEntity notice2 = persist(ProductNoticeJpaEntityFixtures.newEntity());

            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice1.getId(), 100L, "제조국"));
            persist(ProductNoticeJpaEntityFixtures.entryEntity(notice2.getId(), 200L, "원산지"));

            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository().findEntriesByProductNoticeIds(List.of(notice1.getId()));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductNoticeId()).isEqualTo(notice1.getId());
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 리스트를 반환합니다")
        void findEntriesByProductNoticeIds_WithEmptyList_ReturnsEmpty() {
            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository().findEntriesByProductNoticeIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 Entry가 없으면 빈 리스트를 반환합니다")
        void findEntriesByProductNoticeIds_WithNoEntries_ReturnsEmpty() {
            // given
            ProductNoticeJpaEntity notice = persist(ProductNoticeJpaEntityFixtures.newEntity());

            // when
            List<ProductNoticeEntryJpaEntity> result =
                    repository().findEntriesByProductNoticeIds(List.of(notice.getId()));

            // then
            assertThat(result).isEmpty();
        }
    }
}
