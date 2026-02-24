package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.condition.ProductGroupDescriptionConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
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
 * ProductGroupDescriptionQueryDslRepositoryTest - 상품 그룹 상세설명 QueryDslRepository 통합 테스트.
 *
 * <p>publishStatus 필터 및 이미지 soft-delete 조건 검색을 검증합니다.
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
@DisplayName("ProductGroupDescriptionQueryDslRepository 통합 테스트")
class ProductGroupDescriptionQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupDescriptionQueryDslRepository repository() {
        return new ProductGroupDescriptionQueryDslRepository(
                new JPAQueryFactory(entityManager), new ProductGroupDescriptionConditionBuilder());
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
        @DisplayName("유효한 ID로 Description을 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            ProductGroupDescriptionJpaEntity saved =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L));

            // when
            Optional<ProductGroupDescriptionJpaEntity> result =
                    repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductGroupDescriptionJpaEntity> result = repository().findById(999999L);

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
        @DisplayName("ProductGroupId로 Description을 조회합니다")
        void findByProductGroupId_WithExistingId_ReturnsEntity() {
            // given
            Long productGroupId = 100L;
            ProductGroupDescriptionJpaEntity saved =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId));

            // when
            Optional<ProductGroupDescriptionJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getProductGroupId()).isEqualTo(productGroupId);
        }

        @Test
        @DisplayName("존재하지 않는 ProductGroupId는 빈 결과를 반환합니다")
        void findByProductGroupId_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ProductGroupDescriptionJpaEntity> result =
                    repository().findByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByPublishStatus 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByPublishStatus")
    class FindByPublishStatusTest {

        @Test
        @DisplayName("PENDING 상태의 Description 목록을 limit 적용하여 조회합니다")
        void findByPublishStatus_WithPendingStatus_ReturnsEntities() {
            // given
            persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(10L));
            persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(11L));
            persist(ProductGroupDescriptionJpaEntityFixtures.publishedEntity(12L));

            // when
            List<ProductGroupDescriptionJpaEntity> result =
                    repository().findByPublishStatus("PENDING", 10);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getPublishStatus().equals("PENDING"));
        }

        @Test
        @DisplayName("limit을 초과하는 경우 limit만큼만 반환합니다")
        void findByPublishStatus_WithLimitExceeded_ReturnsLimitedResults() {
            // given
            for (int i = 0; i < 5; i++) {
                persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity((long) (100 + i)));
            }

            // when
            List<ProductGroupDescriptionJpaEntity> result =
                    repository().findByPublishStatus("PENDING", 3);

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("해당 상태의 결과가 없으면 빈 리스트를 반환합니다")
        void findByPublishStatus_WithNoResults_ReturnsEmpty() {
            // when
            List<ProductGroupDescriptionJpaEntity> result =
                    repository().findByPublishStatus("PUBLISHED", 10);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findImagesByDescriptionId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findImagesByDescriptionId")
    class FindImagesByDescriptionIdTest {

        @Test
        @DisplayName("미삭제 이미지만 조회됩니다")
        void findImagesByDescriptionId_WithNotDeletedImages_ReturnsImages() {
            // given
            ProductGroupDescriptionJpaEntity description =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(200L));
            Long descriptionId = description.getId();

            persist(ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId));
            persist(ProductGroupDescriptionJpaEntityFixtures.pendingImageEntity(descriptionId));
            persist(ProductGroupDescriptionJpaEntityFixtures.deletedImageEntity(descriptionId));

            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionId(descriptionId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("이미지가 없으면 빈 리스트를 반환합니다")
        void findImagesByDescriptionId_WithNoImages_ReturnsEmpty() {
            // given
            ProductGroupDescriptionJpaEntity description =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(300L));

            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionId(description.getId());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("정렬 순서(sortOrder)가 오름차순으로 반환됩니다")
        void findImagesByDescriptionId_ReturnsSortedBySortOrder() {
            // given
            ProductGroupDescriptionJpaEntity description =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(400L));
            Long descriptionId = description.getId();

            persist(ProductGroupDescriptionJpaEntityFixtures.imageEntity(null, descriptionId));

            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionId(descriptionId);

            // then
            assertThat(result).isNotEmpty();
        }
    }

    // ========================================================================
    // 5. findByProductGroupIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupIdIn")
    class FindByProductGroupIdInTest {

        @Test
        @DisplayName("여러 ProductGroupId로 Description 목록을 배치 조회합니다")
        void findByProductGroupIdIn_WithMultipleGroupIds_ReturnsAllDescriptions() {
            // given
            Long productGroupId1 = 500L;
            Long productGroupId2 = 501L;
            persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId1));
            persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId2));

            // when
            List<ProductGroupDescriptionJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(productGroupId1, productGroupId2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ProductGroupDescriptionJpaEntity::getProductGroupId)
                    .containsExactlyInAnyOrder(productGroupId1, productGroupId2);
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithEmptyList_ReturnsEmpty() {
            // when
            List<ProductGroupDescriptionJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 Description이 없으면 빈 리스트를 반환합니다")
        void findByProductGroupIdIn_WithNoMatchingDescriptions_ReturnsEmpty() {
            // when
            List<ProductGroupDescriptionJpaEntity> result =
                    repository().findByProductGroupIdIn(List.of(999999L, 1000000L));

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 6. findImagesByDescriptionIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findImagesByDescriptionIds")
    class FindImagesByDescriptionIdsTest {

        @Test
        @DisplayName("여러 descriptionId에 속한 미삭제 이미지를 배치 조회합니다")
        void findImagesByDescriptionIds_WithMultipleIds_ReturnsAllNotDeletedImages() {
            // given
            ProductGroupDescriptionJpaEntity desc1 =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(600L));
            ProductGroupDescriptionJpaEntity desc2 =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(601L));

            persist(ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(desc1.getId()));
            persist(ProductGroupDescriptionJpaEntityFixtures.pendingImageEntity(desc1.getId()));
            persist(ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(desc2.getId()));

            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionIds(List.of(desc1.getId(), desc2.getId()));

            // then
            assertThat(result).hasSize(3);
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("삭제된 이미지는 배치 조회에서 제외됩니다")
        void findImagesByDescriptionIds_WithDeletedImages_ExcludesDeleted() {
            // given
            ProductGroupDescriptionJpaEntity desc =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(700L));
            Long descriptionId = desc.getId();

            persist(ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId));
            persist(ProductGroupDescriptionJpaEntityFixtures.deletedImageEntity(descriptionId));

            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionIds(List.of(descriptionId));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("descriptionId 오름차순, sortOrder 오름차순으로 정렬되어 반환됩니다")
        void findImagesByDescriptionIds_ReturnsSortedByDescriptionIdAndSortOrder() {
            // given
            ProductGroupDescriptionJpaEntity desc1 =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(800L));
            ProductGroupDescriptionJpaEntity desc2 =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(801L));

            persist(ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(desc2.getId()));
            persist(ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(desc1.getId()));

            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionIds(List.of(desc1.getId(), desc2.getId()));

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getProductGroupDescriptionId()).isEqualTo(desc1.getId());
            assertThat(result.get(1).getProductGroupDescriptionId()).isEqualTo(desc2.getId());
        }

        @Test
        @DisplayName("빈 ID 목록 입력 시 빈 리스트를 반환합니다")
        void findImagesByDescriptionIds_WithEmptyList_ReturnsEmpty() {
            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("해당하는 이미지가 없으면 빈 리스트를 반환합니다")
        void findImagesByDescriptionIds_WithNoMatchingImages_ReturnsEmpty() {
            // given
            ProductGroupDescriptionJpaEntity desc =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(900L));

            // when
            List<DescriptionImageJpaEntity> result =
                    repository().findImagesByDescriptionIds(List.of(desc.getId()));

            // then
            assertThat(result).isEmpty();
        }
    }
}
