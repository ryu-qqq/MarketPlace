package com.ryuqq.marketplace.adapter.out.persistence.imagevariant.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.ImageVariantJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.imagevariant.entity.ImageVariantJpaEntity;
import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
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
 * ImageVariantQueryDslRepositoryTest - 이미지 Variant QueryDslRepository 통합 테스트.
 *
 * <p>소스 이미지 ID, 소스 타입 기반 필터 적용을 검증합니다.
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
@DisplayName("ImageVariantQueryDslRepository 통합 테스트")
class ImageVariantQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ImageVariantQueryDslRepository repository() {
        return new ImageVariantQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private ImageVariantJpaEntity persist(ImageVariantJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findBySourceImageId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySourceImageId")
    class FindBySourceImageIdTest {

        @Test
        @DisplayName("소스 이미지 ID와 소스 타입이 일치하는 Entity를 반환합니다")
        void findBySourceImageId_WithMatchingSourceImageId_ReturnsEntity() {
            // given
            Long sourceImageId = 100L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageId(
                                    sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceImageId()).isEqualTo(sourceImageId);
            assertThat(result.get(0).getSourceType())
                    .isEqualTo(ImageSourceType.PRODUCT_GROUP_IMAGE);
        }

        @Test
        @DisplayName("소스 이미지 ID가 다른 Entity는 조회되지 않습니다")
        void findBySourceImageId_WithDifferentSourceImageId_ReturnsEmpty() {
            // given
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            100L,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository().findBySourceImageId(999L, ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("소스 타입이 다른 Entity는 조회되지 않습니다")
        void findBySourceImageId_WithDifferentSourceType_ReturnsEmpty() {
            // given
            Long sourceImageId = 100L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageId(sourceImageId, ImageSourceType.DESCRIPTION_IMAGE);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("동일 소스 이미지 ID에 여러 Variant가 있을 때 모두 반환합니다")
        void findBySourceImageId_WithMultipleVariants_ReturnsAll() {
            // given
            Long sourceImageId = 100L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.MEDIUM_WEBP));
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.LARGE_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageId(
                                    sourceImageId, ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("DESCRIPTION_IMAGE 소스 타입 Entity를 조회합니다")
        void findBySourceImageId_WithDescriptionImageType_ReturnsEntity() {
            // given
            Long sourceImageId = 200L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageId(sourceImageId, ImageSourceType.DESCRIPTION_IMAGE);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceType()).isEqualTo(ImageSourceType.DESCRIPTION_IMAGE);
        }
    }

    // ========================================================================
    // 2. findBySourceImageIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findBySourceImageIds")
    class FindBySourceImageIdsTest {

        @Test
        @DisplayName("소스 이미지 ID 목록으로 Variant 목록을 반환합니다")
        void findBySourceImageIds_WithValidIds_ReturnsEntities() {
            // given
            Long sourceImageId1 = 101L;
            Long sourceImageId2 = 102L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId1,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId2,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageIds(
                                    List.of(sourceImageId1, sourceImageId2),
                                    ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(ImageVariantJpaEntity::getSourceImageId)
                    .containsExactlyInAnyOrder(sourceImageId1, sourceImageId2);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findBySourceImageIds_WithEmptyList_ReturnsEmpty() {
            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageIds(List.of(), ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("null ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findBySourceImageIds_WithNullList_ReturnsEmpty() {
            // when
            List<ImageVariantJpaEntity> result =
                    repository().findBySourceImageIds(null, ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("소스 타입이 다른 Entity는 포함되지 않습니다")
        void findBySourceImageIds_WithMixedSourceTypes_FiltersCorrectly() {
            // given
            Long sourceImageId = 105L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId,
                            ImageSourceType.DESCRIPTION_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageIds(
                                    List.of(sourceImageId), ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getSourceType())
                    .isEqualTo(ImageSourceType.PRODUCT_GROUP_IMAGE);
        }

        @Test
        @DisplayName("결과는 sourceImageId 오름차순으로 정렬됩니다")
        void findBySourceImageIds_ReturnsInAscendingSourceImageIdOrder() {
            // given
            Long sourceImageId1 = 201L;
            Long sourceImageId2 = 202L;
            Long sourceImageId3 = 203L;
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId3,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId1,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));
            persist(
                    ImageVariantJpaEntityFixtures.newEntityWith(
                            sourceImageId2,
                            ImageSourceType.PRODUCT_GROUP_IMAGE,
                            ImageVariantType.SMALL_WEBP));

            // when
            List<ImageVariantJpaEntity> result =
                    repository()
                            .findBySourceImageIds(
                                    List.of(sourceImageId1, sourceImageId2, sourceImageId3),
                                    ImageSourceType.PRODUCT_GROUP_IMAGE);

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(ImageVariantJpaEntity::getSourceImageId)
                    .containsExactly(sourceImageId1, sourceImageId2, sourceImageId3);
        }
    }
}
