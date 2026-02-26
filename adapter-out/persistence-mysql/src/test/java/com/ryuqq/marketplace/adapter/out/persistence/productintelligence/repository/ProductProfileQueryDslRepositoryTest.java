package com.ryuqq.marketplace.adapter.out.persistence.productintelligence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.ProductIntelligenceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productintelligence.entity.ProductProfileJpaEntity;
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
 * ProductProfileQueryDslRepositoryTest - 상품 프로파일 QueryDslRepository 통합 테스트.
 *
 * <p>조건 검색 및 상태 필터 동작을 검증합니다.
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
@DisplayName("ProductProfileQueryDslRepository 통합 테스트")
class ProductProfileQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductProfileQueryDslRepository repository() {
        return new ProductProfileQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
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
        @DisplayName("ID로 ProductProfile을 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            ProductProfileJpaEntity saved =
                    persist(ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(null, 100L));

            // when
            Optional<ProductProfileJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductGroupId()).isEqualTo(100L);
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductProfileJpaEntity> result = repository().findById(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findLatestByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findLatestByProductGroupId")
    class FindLatestByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 최신 버전의 프로파일을 조회합니다")
        void findLatestByProductGroupId_WithMultipleVersions_ReturnsLatestVersion() {
            // given
            Long productGroupId = 200L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntityWithVersion(
                            null, productGroupId, 1));
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntityWithVersion(
                            null, productGroupId, 2));
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntityWithVersion(
                            null, productGroupId, 3));

            // when
            Optional<ProductProfileJpaEntity> result =
                    repository().findLatestByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProfileVersion()).isEqualTo(3);
        }

        @Test
        @DisplayName("프로파일이 없으면 빈 결과를 반환합니다")
        void findLatestByProductGroupId_WithNoProfiles_ReturnsEmpty() {
            // when
            Optional<ProductProfileJpaEntity> result =
                    repository().findLatestByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findLatestActiveByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findLatestActiveByProductGroupId")
    class FindLatestActiveByProductGroupIdTest {

        @Test
        @DisplayName("만료되지 않은(expiredAt IS NULL) 최신 프로파일을 반환합니다")
        void findLatestActiveByProductGroupId_WithActiveAndExpired_ReturnsActiveOnly() {
            // given
            Long productGroupId = 300L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.expiredProfileEntity(
                            null, productGroupId));
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(
                            null, productGroupId));

            // when
            Optional<ProductProfileJpaEntity> result =
                    repository().findLatestActiveByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getExpiredAt()).isNull();
        }

        @Test
        @DisplayName("모든 프로파일이 만료되면 빈 결과를 반환합니다")
        void findLatestActiveByProductGroupId_WithAllExpired_ReturnsEmpty() {
            // given
            Long productGroupId = 301L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.expiredProfileEntity(
                            null, productGroupId));

            // when
            Optional<ProductProfileJpaEntity> result =
                    repository().findLatestActiveByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findLatestCompletedByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findLatestCompletedByProductGroupId")
    class FindLatestCompletedByProductGroupIdTest {

        @Test
        @DisplayName("COMPLETED 상태의 최신 프로파일을 반환합니다")
        void findLatestCompletedByProductGroupId_WithCompletedProfile_ReturnsCompleted() {
            // given
            Long productGroupId = 400L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(
                            null, productGroupId));
            persist(
                    ProductIntelligenceJpaEntityFixtures.completedProfileEntity(
                            null, productGroupId));

            // when
            Optional<ProductProfileJpaEntity> result =
                    repository().findLatestCompletedByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getStatus())
                    .isEqualTo(ProductProfileJpaEntity.Status.COMPLETED);
        }

        @Test
        @DisplayName("COMPLETED 프로파일이 없으면 빈 결과를 반환합니다")
        void findLatestCompletedByProductGroupId_WithNoPendingProfiles_ReturnsEmpty() {
            // given
            Long productGroupId = 401L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntity(
                            null, productGroupId));

            // when
            Optional<ProductProfileJpaEntity> result =
                    repository().findLatestCompletedByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findAllByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findAllByProductGroupId")
    class FindAllByProductGroupIdTest {

        @Test
        @DisplayName("상품그룹 ID로 모든 프로파일 이력을 최신 버전 순으로 조회합니다")
        void findAllByProductGroupId_WithMultipleProfiles_ReturnsSortedByVersionDesc() {
            // given
            Long productGroupId = 500L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntityWithVersion(
                            null, productGroupId, 1));
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntityWithVersion(
                            null, productGroupId, 3));
            persist(
                    ProductIntelligenceJpaEntityFixtures.pendingProfileEntityWithVersion(
                            null, productGroupId, 2));

            // when
            List<ProductProfileJpaEntity> result =
                    repository().findAllByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getProfileVersion()).isEqualTo(3);
            assertThat(result.get(1).getProfileVersion()).isEqualTo(2);
            assertThat(result.get(2).getProfileVersion()).isEqualTo(1);
        }

        @Test
        @DisplayName("프로파일이 없으면 빈 리스트를 반환합니다")
        void findAllByProductGroupId_WithNoProfiles_ReturnsEmptyList() {
            // when
            List<ProductProfileJpaEntity> result = repository().findAllByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 6. findStuckAnalyzingProfiles 테스트
    // ========================================================================

    @Nested
    @DisplayName("findStuckAnalyzingProfiles")
    class FindStuckAnalyzingProfilesTest {

        @Test
        @DisplayName("ANALYZING 상태에서 분석이 완료되었지만 오래된 프로파일을 조회합니다")
        void findStuckAnalyzingProfiles_WithStuckProfiles_ReturnsStuckProfiles() {
            // given
            Long productGroupId = 600L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.stuckAnalyzingProfileEntity(
                            null, productGroupId));
            Instant threshold = Instant.now().minusSeconds(3600);

            // when
            List<ProductProfileJpaEntity> result =
                    repository().findStuckAnalyzingProfiles(threshold, 10);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus())
                    .isEqualTo(ProductProfileJpaEntity.Status.ANALYZING);
        }

        @Test
        @DisplayName("오래되지 않은 ANALYZING 프로파일은 반환하지 않습니다")
        void findStuckAnalyzingProfiles_WithRecentAnalyzingProfile_ReturnsEmpty() {
            // given
            Long productGroupId = 601L;
            persist(
                    ProductIntelligenceJpaEntityFixtures.analyzingProfileEntity(
                            null, productGroupId));
            Instant threshold = Instant.now().minusSeconds(7200);

            // when
            List<ProductProfileJpaEntity> result =
                    repository().findStuckAnalyzingProfiles(threshold, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("limit을 초과하지 않는 수의 프로파일을 반환합니다")
        void findStuckAnalyzingProfiles_WithLimitApplied_ReturnsLimitedResults() {
            // given
            int limit = 2;
            for (int i = 0; i < 5; i++) {
                persist(
                        ProductIntelligenceJpaEntityFixtures.stuckAnalyzingProfileEntity(
                                null, 700L + i));
            }
            Instant threshold = Instant.now().minusSeconds(3600);

            // when
            List<ProductProfileJpaEntity> result =
                    repository().findStuckAnalyzingProfiles(threshold, limit);

            // then
            assertThat(result).hasSizeLessThanOrEqualTo(limit);
        }
    }
}
