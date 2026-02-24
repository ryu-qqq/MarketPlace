package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.ProductGroupImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
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
 * ProductGroupImageQueryDslRepositoryTest - 상품 그룹 이미지 QueryDslRepository 통합 테스트.
 *
 * <p>deleted=true 필터 및 productGroupId 조건 검색 적용을 검증합니다.
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
@DisplayName("ProductGroupImageQueryDslRepository 통합 테스트")
class ProductGroupImageQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupImageQueryDslRepository repository() {
        return new ProductGroupImageQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private ProductGroupImageJpaEntity persist(ProductGroupImageJpaEntity entity) {
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
        @DisplayName("미삭제 이미지는 findById로 조회됩니다")
        void findById_WithNotDeleted_ReturnsEntity() {
            // given
            ProductGroupImageJpaEntity saved =
                    persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(1L));

            // when
            Optional<ProductGroupImageJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductGroupImageJpaEntity> result = repository().findById(999999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("삭제된 이미지도 findById로 조회됩니다 (deleted 필터 미적용)")
        void findById_WithDeletedEntity_ReturnsEntity() {
            // given
            ProductGroupImageJpaEntity deleted =
                    persist(ProductGroupImageJpaEntityFixtures.deletedEntity(1L));

            // when
            Optional<ProductGroupImageJpaEntity> result = repository().findById(deleted.getId());

            // then
            assertThat(result).isPresent();
        }
    }

    // ========================================================================
    // 2. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("특정 ProductGroupId의 미삭제 이미지를 조회합니다")
        void findByProductGroupId_WithNotDeleted_ReturnsEntities() {
            // given
            Long productGroupId = 10L;
            persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId));
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId, 1));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getProductGroupId().equals(productGroupId));
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("삭제된 이미지는 findByProductGroupId에서 제외됩니다")
        void findByProductGroupId_WithDeletedEntity_ExcludesDeleted() {
            // given
            Long productGroupId = 20L;
            persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId));
            persist(ProductGroupImageJpaEntityFixtures.deletedEntity(productGroupId));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("이미지가 없는 ProductGroupId는 빈 리스트를 반환합니다")
        void findByProductGroupId_WithNoImages_ReturnsEmpty() {
            // when
            List<ProductGroupImageJpaEntity> result = repository().findByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("정렬 순서(sortOrder)가 오름차순으로 반환됩니다")
        void findByProductGroupId_ReturnsSortedBySortOrder() {
            // given
            Long productGroupId = 30L;
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId, 2));
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId, 0));
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId, 1));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder())
                    .isLessThanOrEqualTo(result.get(1).getSortOrder());
            assertThat(result.get(1).getSortOrder())
                    .isLessThanOrEqualTo(result.get(2).getSortOrder());
        }
    }

    // ========================================================================
    // 3. findByProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdIn")
    class FindByProductGroupIdInTest {

        @Test
        @DisplayName("여러 ProductGroupId에 속한 미삭제 이미지를 배치 조회합니다")
        void findByProductGroupIdIn_WithMultipleGroupIds_ReturnsAllNotDeletedImages() {
            // given
            Long productGroupId1 = 40L;
            Long productGroupId2 = 41L;
            persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId1));
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId1, 1));
            persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId2));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(productGroupId1, productGroupId2));

            // then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("삭제된 이미지는 배치 조회에서 제외됩니다")
        void findByProductGroupIdIn_WithDeletedImages_ExcludesDeleted() {
            // given
            Long productGroupId1 = 50L;
            Long productGroupId2 = 51L;
            persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId1));
            persist(ProductGroupImageJpaEntityFixtures.deletedEntity(productGroupId1));
            persist(ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId2));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(productGroupId1, productGroupId2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("productGroupId 오름차순, sortOrder 오름차순으로 정렬되어 반환됩니다")
        void findByProductGroupIdIn_ReturnsSortedByGroupIdAndSortOrder() {
            // given
            Long productGroupId1 = 60L;
            Long productGroupId2 = 61L;
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId2, 0));
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId1, 1));
            persist(ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId1, 0));

            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(productGroupId1, productGroupId2));

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getProductGroupId()).isEqualTo(productGroupId1);
            assertThat(result.get(1).getProductGroupId()).isEqualTo(productGroupId1);
            assertThat(result.get(2).getProductGroupId()).isEqualTo(productGroupId2);
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithEmptyList_ReturnsEmpty() {
            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 이미지가 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithNoMatchingImages_ReturnsEmpty() {
            // when
            List<ProductGroupImageJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(999999L, 1000000L));

            // then
            assertThat(result).isEmpty();
        }
    }
}
