package com.ryuqq.marketplace.adapter.out.persistence.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.product.ProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.product.condition.ProductConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
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
 * ProductQueryDslRepositoryTest - 상품 QueryDslRepository 통합 테스트.
 *
 * <p>DELETED 상태 필터 적용을 우선 검증합니다.
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
@DisplayName("ProductQueryDslRepository 통합 테스트")
class ProductQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductQueryDslRepository repository() {
        return new ProductQueryDslRepository(
                new JPAQueryFactory(entityManager), new ProductConditionBuilder());
    }

    private ProductJpaEntity persist(ProductJpaEntity entity) {
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
        @DisplayName("미삭제 상태 Entity는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            // given
            ProductJpaEntity saved = persist(ProductJpaEntityFixtures.newEntity());

            // when
            Optional<ProductJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("DELETED 상태 Entity는 findById로 조회되지 않습니다")
        void findById_WithDeletedStatus_ReturnsEmpty() {
            // given
            ProductJpaEntity deleted = persist(ProductJpaEntityFixtures.deletedEntity());

            // when
            Optional<ProductJpaEntity> result = repository().findById(deleted.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductJpaEntity> result = repository().findById(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("동일한 ProductGroupId를 가진 미삭제 상품들을 조회합니다")
        void findByProductGroupId_WithActiveProducts_ReturnsAll() {
            // given
            Long productGroupId = 100L;
            ProductJpaEntity entity1 =
                    persist(ProductJpaEntityFixtures.activeEntity(null, productGroupId));
            ProductJpaEntity entity2 =
                    persist(ProductJpaEntityFixtures.activeEntity(null, productGroupId));

            // when
            List<ProductJpaEntity> result = repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ProductJpaEntity::getProductGroupId)
                    .containsOnly(productGroupId);
        }

        @Test
        @DisplayName("DELETED 상태 상품은 조회에서 제외됩니다")
        void findByProductGroupId_WithDeletedProduct_ExcludesDeleted() {
            // given
            Long productGroupId = 200L;
            persist(ProductJpaEntityFixtures.activeEntity(null, productGroupId));
            persist(ProductJpaEntityFixtures.deletedEntity());

            // when
            List<ProductJpaEntity> result = repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("해당 그룹에 상품이 없으면 빈 리스트를 반환합니다")
        void findByProductGroupId_WithNoProducts_ReturnsEmpty() {
            // when
            List<ProductJpaEntity> result = repository().findByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByProductGroupIdAndIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdAndIdIn")
    class FindByProductGroupIdAndIdInTest {

        @Test
        @DisplayName("그룹 ID와 ID 목록으로 미삭제 상품을 조회합니다")
        void findByProductGroupIdAndIdIn_WithValidParams_ReturnsMatchingEntities() {
            // given
            Long productGroupId = 300L;
            ProductJpaEntity entity1 =
                    persist(ProductJpaEntityFixtures.activeEntity(null, productGroupId));
            ProductJpaEntity entity2 =
                    persist(ProductJpaEntityFixtures.activeEntity(null, productGroupId));

            // when
            List<ProductJpaEntity> result =
                    repository()
                            .findByProductGroupIdAndIdIn(
                                    productGroupId, List.of(entity1.getId(), entity2.getId()));

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("DELETED 상태 상품은 그룹+ID 조회에서도 제외됩니다")
        void findByProductGroupIdAndIdIn_WithDeletedProduct_ExcludesDeleted() {
            // given
            Long productGroupId = 400L;
            ProductJpaEntity active =
                    persist(ProductJpaEntityFixtures.activeEntity(null, productGroupId));
            ProductJpaEntity deleted = persist(ProductJpaEntityFixtures.deletedEntity());

            // when
            List<ProductJpaEntity> result =
                    repository()
                            .findByProductGroupIdAndIdIn(productGroupId, List.of(active.getId()));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo(active.getId());
        }
    }
}
