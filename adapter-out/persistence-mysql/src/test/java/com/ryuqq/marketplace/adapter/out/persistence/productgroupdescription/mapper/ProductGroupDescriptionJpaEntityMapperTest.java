package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductGroupDescriptionJpaEntityMapperTest - 상품 그룹 상세설명 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) + toImageEntity + toImageDomain 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductGroupDescriptionJpaEntityMapper 단위 테스트")
class ProductGroupDescriptionJpaEntityMapperTest {

    private ProductGroupDescriptionJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupDescriptionJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("PENDING 상태 ProductGroupDescription을 Entity로 변환합니다")
        void toEntity_WithPendingDescription_ConvertsCorrectly() {
            // given
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();

            // when
            ProductGroupDescriptionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getContent()).isEqualTo(domain.contentValue());
            assertThat(entity.getPublishStatus()).isEqualTo(domain.publishStatus().name());
        }

        @Test
        @DisplayName("새 ProductGroupDescription을 Entity로 변환 시 ID가 null입니다")
        void toEntity_WithNewDescription_IdIsNull() {
            // given
            ProductGroupDescription domain = ProductGroupFixtures.defaultProductGroupDescription();

            // when
            ProductGroupDescriptionJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }
    }

    // ========================================================================
    // 2. toImageEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageEntity 메서드 테스트")
    class ToImageEntityTest {

        @Test
        @DisplayName("DescriptionImage를 Entity로 변환합니다")
        void toImageEntity_WithValidImage_ConvertsCorrectly() {
            // given
            DescriptionImage domain = ProductGroupFixtures.defaultDescriptionImage();

            // when
            DescriptionImageJpaEntity entity = mapper.toImageEntity(domain);

            // then
            assertThat(entity.getOriginUrl()).isEqualTo(domain.originUrlValue());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrder());
            assertThat(entity.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("업로드 완료된 DescriptionImage를 Entity로 변환합니다")
        void toImageEntity_WithUploadedImage_SetsUploadedUrl() {
            // given
            DescriptionImage domain = ProductGroupFixtures.uploadedDescriptionImage();

            // when
            DescriptionImageJpaEntity entity = mapper.toImageEntity(domain);

            // then
            assertThat(entity.getUploadedUrl()).isNotNull();
        }
    }

    // ========================================================================
    // 3. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("Entity와 이미지 목록으로 ProductGroupDescription Domain을 생성합니다")
        void toDomain_WithValidEntities_ConvertsCorrectly() {
            // given
            ProductGroupDescriptionJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L, 1L);
            List<DescriptionImageJpaEntity> images =
                    ProductGroupDescriptionJpaEntityFixtures.emptyImageEntities();

            // when
            ProductGroupDescription domain = mapper.toDomain(entity, images);

            // then
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.contentValue()).isEqualTo(entity.getContent());
            assertThat(domain.publishStatus()).isEqualTo(DescriptionPublishStatus.PENDING);
        }

        @Test
        @DisplayName("PUBLISHED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithPublishedEntity_ConvertsStatus() {
            // given
            ProductGroupDescriptionJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.publishedEntity(1L, 1L);
            List<DescriptionImageJpaEntity> images =
                    ProductGroupDescriptionJpaEntityFixtures.emptyImageEntities();

            // when
            ProductGroupDescription domain = mapper.toDomain(entity, images);

            // then
            assertThat(domain.publishStatus()).isEqualTo(DescriptionPublishStatus.PUBLISHED);
            assertThat(domain.cdnPathValue()).isNotNull();
        }

        @Test
        @DisplayName("이미지 목록을 포함한 Domain을 생성합니다")
        void toDomain_WithImages_ConvertsImages() {
            // given
            ProductGroupDescriptionJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L, 1L);
            List<DescriptionImageJpaEntity> images =
                    ProductGroupDescriptionJpaEntityFixtures.defaultImageEntities(1L);

            // when
            ProductGroupDescription domain = mapper.toDomain(entity, images);

            // then
            assertThat(domain.images()).hasSize(images.size());
        }
    }

    // ========================================================================
    // 4. toImageDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageDomain 메서드 테스트")
    class ToImageDomainTest {

        @Test
        @DisplayName("DescriptionImageEntity를 Domain으로 변환합니다")
        void toImageDomain_WithValidEntity_ConvertsCorrectly() {
            // given
            DescriptionImageJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.imageEntity(1L, 10L);

            // when
            DescriptionImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.originUrlValue()).isEqualTo(entity.getOriginUrl());
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
        }

        @Test
        @DisplayName("삭제된 DescriptionImageEntity를 Domain으로 변환합니다")
        void toImageDomain_WithDeletedEntity_ConvertsDeletedStatus() {
            // given
            DescriptionImageJpaEntity entity =
                    ProductGroupDescriptionJpaEntityFixtures.deletedImageEntity(10L);

            // when
            DescriptionImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
        }
    }
}
