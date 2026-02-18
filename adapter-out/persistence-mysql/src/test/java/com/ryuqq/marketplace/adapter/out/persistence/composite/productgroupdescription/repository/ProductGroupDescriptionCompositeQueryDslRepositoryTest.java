package com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.composite.productgroupdescription.dto.DescriptionCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.imageupload.entity.ImageUploadOutboxJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageUploadOutboxStatus;
import jakarta.persistence.EntityManager;
import java.time.Instant;
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
 * ProductGroupDescriptionCompositeQueryDslRepository 통합 테스트.
 *
 * <p>product_group_descriptions + description_images + image_upload_outboxes 크로스 도메인 조회를 검증합니다.
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
@DisplayName("ProductGroupDescriptionCompositeQueryDslRepository 통합 테스트")
class ProductGroupDescriptionCompositeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ProductGroupDescriptionCompositeQueryDslRepository repository() {
        return new ProductGroupDescriptionCompositeQueryDslRepository(
                new JPAQueryFactory(entityManager));
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
                "https://example.com/desc-image.jpg",
                status,
                0,
                3,
                now,
                now,
                null,
                null,
                0L,
                key);
    }

    // ========================================================================
    // 1. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId 테스트")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("상세설명이 없을 때 Optional.empty가 반환됩니다")
        void findByProductGroupId_WithNoDescription_ReturnsEmpty() {
            // given
            Long productGroupId = 100L;

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("상세설명만 있고 이미지가 없을 때 빈 이미지 목록을 가진 Composite DTO가 반환됩니다")
        void findByProductGroupId_WithDescriptionOnly_ReturnsCompositeDtoWithEmptyImages() {
            // given
            Long productGroupId = 200L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            DescriptionCompositeDto dto = result.get();
            assertThat(dto.productGroupId()).isEqualTo(productGroupId);
            assertThat(dto.description().descriptionId()).isEqualTo(description.getId());
            assertThat(dto.description().publishStatus()).isEqualTo("PENDING");
            assertThat(dto.description().cdnPath()).isNull();
            assertThat(dto.images()).isEmpty();
            assertThat(dto.outboxes()).isEmpty();
        }

        @Test
        @DisplayName("상세설명과 이미지가 있을 때 이미지 목록이 포함된 Composite DTO가 반환됩니다")
        void findByProductGroupId_WithDescriptionAndImages_ReturnsCompositeDtoWithImages() {
            // given
            Long productGroupId = 300L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            Long descriptionId = description.getId();
            DescriptionImageJpaEntity image1 =
                    ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId);
            DescriptionImageJpaEntity image2 =
                    ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId);
            entityManager.persist(image1);
            entityManager.persist(image2);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().images()).hasSize(2);
        }

        @Test
        @DisplayName("이미지와 아웃박스가 모두 있을 때 Composite DTO에 아웃박스 목록이 포함됩니다")
        void findByProductGroupId_WithImagesAndOutboxes_ReturnsCompositeDtoWithOutboxes() {
            // given
            Long productGroupId = 400L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            Long descriptionId = description.getId();
            DescriptionImageJpaEntity image =
                    ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId);
            entityManager.persist(image);
            flushAndClear();

            Long imageId = image.getId();
            ImageUploadOutboxJpaEntity outbox =
                    outboxEntity(
                            imageId,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            ImageUploadOutboxStatus.COMPLETED);
            entityManager.persist(outbox);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().images()).hasSize(1);
            assertThat(result.get().outboxes()).hasSize(1);
            assertThat(result.get().outboxes().get(0).sourceId()).isEqualTo(imageId);
            assertThat(result.get().outboxes().get(0).status()).isEqualTo("COMPLETED");
        }

        @Test
        @DisplayName("삭제된 이미지(deleted=true)는 조회되지 않습니다")
        void findByProductGroupId_WithDeletedImage_ExcludesDeletedImages() {
            // given
            Long productGroupId = 500L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            Long descriptionId = description.getId();
            DescriptionImageJpaEntity activeImage =
                    ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId);
            DescriptionImageJpaEntity deletedImage =
                    ProductGroupDescriptionJpaEntityFixtures.deletedImageEntity(descriptionId);
            entityManager.persist(activeImage);
            entityManager.persist(deletedImage);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().images()).hasSize(1);
            assertThat(result.get().images().get(0).imageId()).isEqualTo(activeImage.getId());
        }

        @Test
        @DisplayName("PUBLISHED 상태 상세설명을 조회할 때 publishStatus와 cdnPath가 올바르게 매핑됩니다")
        void findByProductGroupId_WithPublishedDescription_ReturnsPublishStatusAndCdnPath() {
            // given
            Long productGroupId = 600L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.publishedEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().description().publishStatus()).isEqualTo("PUBLISHED");
            assertThat(result.get().description().cdnPath())
                    .isEqualTo(ProductGroupDescriptionJpaEntityFixtures.DEFAULT_CDN_PATH);
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 타입이 아닌 아웃박스는 조회되지 않습니다")
        void findByProductGroupId_WithDifferentOutboxSourceType_ExcludesNonMatchingOutboxes() {
            // given
            Long productGroupId = 700L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            Long descriptionId = description.getId();
            DescriptionImageJpaEntity image =
                    ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(descriptionId);
            entityManager.persist(image);
            flushAndClear();

            Long imageId = image.getId();
            ImageUploadOutboxJpaEntity wrongTypeOutbox =
                    outboxEntity(
                            imageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageUploadOutboxStatus.COMPLETED);
            entityManager.persist(wrongTypeOutbox);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().images()).hasSize(1);
            assertThat(result.get().outboxes()).isEmpty();
        }

        @Test
        @DisplayName("다른 productGroupId의 상세설명은 조회되지 않습니다")
        void findByProductGroupId_WithDifferentProductGroupId_ReturnsEmpty() {
            // given
            Long targetProductGroupId = 800L;
            Long otherProductGroupId = 801L;

            ProductGroupDescriptionJpaEntity otherDescription =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(otherProductGroupId);
            entityManager.persist(otherDescription);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(targetProductGroupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("업로드 대기 중인 이미지(uploadedUrl null)도 조회됩니다")
        void findByProductGroupId_WithPendingUploadImage_ReturnsImageWithNullUploadedUrl() {
            // given
            Long productGroupId = 900L;
            ProductGroupDescriptionJpaEntity description =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(productGroupId);
            entityManager.persist(description);
            flushAndClear();

            Long descriptionId = description.getId();
            DescriptionImageJpaEntity pendingImage =
                    ProductGroupDescriptionJpaEntityFixtures.pendingImageEntity(descriptionId);
            entityManager.persist(pendingImage);
            flushAndClear();

            // when
            Optional<DescriptionCompositeDto> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().images()).hasSize(1);
            assertThat(result.get().images().get(0).uploadedUrl()).isNull();
        }
    }
}
