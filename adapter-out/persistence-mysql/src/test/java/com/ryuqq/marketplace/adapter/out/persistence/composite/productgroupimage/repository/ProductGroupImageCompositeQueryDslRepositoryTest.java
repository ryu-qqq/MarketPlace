package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupimage.dto.ProductGroupImageCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.ProductGroupImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * ProductGroupImageCompositeQueryDslRepository 통합 테스트.
 *
 * <p>product_group_images + image_upload_outboxes 크로스 도메인 조회를 검증합니다.
 *
 * <p>삭제된 이미지(deleted=true)는 조회되지 않아야 합니다.
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
@DisplayName("ProductGroupImageCompositeQueryDslRepository 통합 테스트")
class ProductGroupImageCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupImageCompositeQueryDslRepository repository() {
        return new ProductGroupImageCompositeQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    /** 특정 sourceId와 sourceType으로 ImageUploadOutboxJpaEntity를 생성하는 헬퍼. */
    private ImageUploadOutboxJpaEntity outboxEntity(
            Long sourceId, ImageSourceType sourceType, ImageUploadOutboxStatus status) {
        Instant now = Instant.now();
        String key = "IUO:" + sourceType.name() + ":" + sourceId + ":" + now.toEpochMilli();
        return ImageUploadOutboxJpaEntity.create(
                null,
                sourceId,
                sourceType,
                "https://example.com/image.jpg",
                status,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                key,
                null);
    }

    // ========================================================================
    // 1. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("이미지와 아웃박스가 모두 있을 때 Composite DTO가 반환됩니다")
        void findByProductGroupId_WithImagesAndOutboxes_ReturnsCompositeDto() {
            // given
            Long productGroupId = 100L;
            ProductGroupImageJpaEntity image =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId);
            entityManager.persist(image);
            flushAndClear();

            Long imageId = image.getId();
            ImageUploadOutboxJpaEntity outbox =
                    outboxEntity(
                            imageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageUploadOutboxStatus.COMPLETED);
            entityManager.persist(outbox);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.images()).hasSize(1);
            assertThat(result.images().get(0).imageId()).isEqualTo(imageId);
            assertThat(result.outboxes()).hasSize(1);
            assertThat(result.outboxes().get(0).sourceId()).isEqualTo(imageId);
            assertThat(result.outboxes().get(0).status()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("이미지만 있고 아웃박스가 없을 때 아웃박스 목록이 비어있습니다")
        void findByProductGroupId_WithImagesOnly_ReturnsEmptyOutboxes() {
            // given
            Long productGroupId = 200L;
            ProductGroupImageJpaEntity image =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId);
            entityManager.persist(image);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.images()).hasSize(1);
            assertThat(result.outboxes()).isEmpty();
        }

        @Test
        @DisplayName("이미지가 없을 때 이미지와 아웃박스 목록이 모두 비어있습니다")
        void findByProductGroupId_WithNoImages_ReturnsBothEmpty() {
            // given
            Long productGroupId = 300L;

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.images()).isEmpty();
            assertThat(result.outboxes()).isEmpty();
        }

        @Test
        @DisplayName("삭제된 이미지(deleted=true)는 조회되지 않습니다")
        void findByProductGroupId_WithDeletedImage_ExcludesDeletedImages() {
            // given
            Long productGroupId = 400L;
            ProductGroupImageJpaEntity activeImage =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId);
            ProductGroupImageJpaEntity deletedImage =
                    ProductGroupImageJpaEntityFixtures.deletedEntity(productGroupId);

            entityManager.persist(activeImage);
            entityManager.persist(deletedImage);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.images()).hasSize(1);
            assertThat(result.images().get(0).imageId()).isEqualTo(activeImage.getId());
        }

        @Test
        @DisplayName("여러 이미지가 있을 때 모두 조회됩니다")
        void findByProductGroupId_WithMultipleImages_ReturnsAllImages() {
            // given
            Long productGroupId = 500L;
            ProductGroupImageJpaEntity thumb =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId);
            ProductGroupImageJpaEntity detail =
                    ProductGroupImageJpaEntityFixtures.detailEntity(productGroupId, 1);

            entityManager.persist(thumb);
            entityManager.persist(detail);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.images()).hasSize(2);
        }

        @Test
        @DisplayName("PRODUCT_GROUP_IMAGE 타입이 아닌 아웃박스는 조회되지 않습니다")
        void findByProductGroupId_WithDifferentSourceType_ExcludesNonMatchingOutboxes() {
            // given
            Long productGroupId = 600L;
            ProductGroupImageJpaEntity image =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId);
            entityManager.persist(image);
            flushAndClear();

            Long imageId = image.getId();
            ImageUploadOutboxJpaEntity wrongTypeOutbox =
                    outboxEntity(
                            imageId,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            ImageUploadOutboxStatus.COMPLETED);
            entityManager.persist(wrongTypeOutbox);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.images()).hasSize(1);
            assertThat(result.outboxes()).isEmpty();
        }

        @Test
        @DisplayName("다른 productGroupId의 이미지는 조회되지 않습니다")
        void findByProductGroupId_WithDifferentProductGroupId_ExcludesOtherGroups() {
            // given
            Long targetProductGroupId = 700L;
            Long otherProductGroupId = 701L;

            ProductGroupImageJpaEntity targetImage =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(targetProductGroupId);
            ProductGroupImageJpaEntity otherImage =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(otherProductGroupId);

            entityManager.persist(targetImage);
            entityManager.persist(otherImage);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(targetProductGroupId);

            // then
            assertThat(result.images()).hasSize(1);
            assertThat(result.images().get(0).imageId()).isEqualTo(targetImage.getId());
        }

        @Test
        @DisplayName("이미지 조회 결과에 imageType, originUrl, uploadedUrl이 올바르게 매핑됩니다")
        void findByProductGroupId_WithImage_MapsProjectionFieldsCorrectly() {
            // given
            Long productGroupId = 800L;
            ProductGroupImageJpaEntity image =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(productGroupId);
            entityManager.persist(image);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.images()).hasSize(1);
            var imageDto = result.images().get(0);
            assertThat(imageDto.imageType()).isEqualTo("THUMBNAIL");
            assertThat(imageDto.originUrl()).isNotBlank();
        }

        @Test
        @DisplayName("업로드 대기 중인 이미지(uploadedUrl null)도 조회됩니다")
        void findByProductGroupId_WithPendingUploadImage_ReturnsImageWithNullUploadedUrl() {
            // given
            Long productGroupId = 900L;
            ProductGroupImageJpaEntity pendingImage =
                    ProductGroupImageJpaEntityFixtures.pendingUploadEntity(productGroupId);
            entityManager.persist(pendingImage);
            flushAndClear();

            // when
            ProductGroupImageCompositeDto result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result.images()).hasSize(1);
            assertThat(result.images().get(0).uploadedUrl()).isNull();
        }
    }
}
