package com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.ProductGroupImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductGroupJpaEntityMapperTest - 상품 그룹 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductGroupJpaEntityMapper 단위 테스트")
class ProductGroupJpaEntityMapperTest {

    private ProductGroupJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("ACTIVE 상태 ProductGroup의 모든 필드를 Entity로 변환합니다")
        void toEntity_WithActiveProductGroup_ConvertsAllFieldsCorrectly() {
            // given
            ProductGroup domain = ProductGroupFixtures.activeProductGroup();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getBrandId()).isEqualTo(domain.brandIdValue());
            assertThat(entity.getCategoryId()).isEqualTo(domain.categoryIdValue());
            assertThat(entity.getShippingPolicyId()).isEqualTo(domain.shippingPolicyIdValue());
            assertThat(entity.getRefundPolicyId()).isEqualTo(domain.refundPolicyIdValue());
            assertThat(entity.getProductGroupName()).isEqualTo(domain.productGroupNameValue());
            assertThat(entity.getOptionType()).isEqualTo(domain.optionType().name());
            assertThat(entity.getStatus()).isEqualTo(ProductGroupStatus.ACTIVE.name());
        }

        @Test
        @DisplayName("DRAFT 상태 ProductGroup을 Entity로 변환합니다")
        void toEntity_WithDraftProductGroup_ConvertsStatus() {
            // given
            ProductGroup domain = ProductGroupFixtures.draftProductGroup(1L);

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductGroupStatus.DRAFT.name());
        }

        @Test
        @DisplayName("새 ProductGroup을 Entity로 변환 시 ID가 null입니다")
        void toEntity_WithNewProductGroup_IdIsNull() {
            // given
            ProductGroup domain = ProductGroupFixtures.newProductGroup();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("SINGLE 옵션 타입의 ProductGroup을 Entity로 변환합니다")
        void toEntity_WithSingleOptionType_ConvertsOptionType() {
            // given
            ProductGroup domain = ProductGroupFixtures.newProductGroupWithSingleOption();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOptionType()).isEqualTo(OptionType.SINGLE.name());
        }

        @Test
        @DisplayName("COMBINATION 옵션 타입의 ProductGroup을 Entity로 변환합니다")
        void toEntity_WithCombinationOptionType_ConvertsOptionType() {
            // given
            ProductGroup domain = ProductGroupFixtures.newProductGroupWithCombinationOption();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getOptionType()).isEqualTo(OptionType.COMBINATION.name());
        }

        @Test
        @DisplayName("INACTIVE 상태 ProductGroup을 Entity로 변환합니다")
        void toEntity_WithInactiveProductGroup_ConvertsStatus() {
            // given
            ProductGroup domain = ProductGroupFixtures.inactiveProductGroup();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductGroupStatus.INACTIVE.name());
        }

        @Test
        @DisplayName("SOLDOUT 상태 ProductGroup을 Entity로 변환합니다")
        void toEntity_WithSoldoutProductGroup_ConvertsStatus() {
            // given
            ProductGroup domain = ProductGroupFixtures.soldoutProductGroup();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductGroupStatus.SOLDOUT.name());
        }

        @Test
        @DisplayName("DELETED 상태 ProductGroup을 Entity로 변환합니다")
        void toEntity_WithDeletedProductGroup_ConvertsStatus() {
            // given
            ProductGroup domain = ProductGroupFixtures.deletedProductGroup();

            // when
            ProductGroupJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductGroupStatus.DELETED.name());
        }
    }

    // ========================================================================
    // 2. toImageEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageEntity 메서드 테스트")
    class ToImageEntityTest {

        @Test
        @DisplayName("THUMBNAIL 이미지를 Entity로 변환합니다")
        void toImageEntity_WithThumbnailImage_ConvertsAllFields() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.thumbnailImage();

            // when
            ProductGroupImageJpaEntity entity = mapper.toImageEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getOriginUrl()).isEqualTo(domain.originUrlValue());
            assertThat(entity.getImageType()).isEqualTo(ImageType.THUMBNAIL.name());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrder());
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("업로드 URL이 없는 이미지를 Entity로 변환합니다")
        void toImageEntity_WithNoUploadedUrl_ConvertsWithNullUploadedUrl() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.thumbnailImage();

            // when
            ProductGroupImageJpaEntity entity = mapper.toImageEntity(domain);

            // then
            assertThat(entity.getUploadedUrl()).isNull();
        }

        @Test
        @DisplayName("업로드 완료된 이미지를 Entity로 변환합니다")
        void toImageEntity_WithUploadedImage_ConvertsUploadedUrl() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.uploadedImage();

            // when
            ProductGroupImageJpaEntity entity = mapper.toImageEntity(domain);

            // then
            assertThat(entity.getUploadedUrl()).isEqualTo(domain.uploadedUrlValue());
            assertThat(entity.getUploadedUrl()).isNotNull();
        }

        @Test
        @DisplayName("DETAIL 이미지를 Entity로 변환합니다")
        void toImageEntity_WithDetailImage_ConvertsImageType() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.detailImage(2);

            // when
            ProductGroupImageJpaEntity entity = mapper.toImageEntity(domain);

            // then
            assertThat(entity.getImageType()).isEqualTo(ImageType.DETAIL.name());
            assertThat(entity.getSortOrder()).isEqualTo(2);
        }
    }

    // ========================================================================
    // 3. toOptionGroupEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toOptionGroupEntity 메서드 테스트")
    class ToOptionGroupEntityTest {

        @Test
        @DisplayName("SellerOptionGroup의 모든 필드를 Entity로 변환합니다")
        void toOptionGroupEntity_WithValidGroup_ConvertsAllFields() {
            // given
            SellerOptionGroup domain = ProductGroupFixtures.defaultSellerOptionGroup();

            // when
            SellerOptionGroupJpaEntity entity = mapper.toOptionGroupEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getOptionGroupName()).isEqualTo(domain.optionGroupNameValue());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrder());
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("캐노니컬 매핑이 없는 SellerOptionGroup의 canonicalOptionGroupId는 null입니다")
        void toOptionGroupEntity_WithNoCanonicalMapping_CanonicalIdIsNull() {
            // given
            SellerOptionGroup domain = ProductGroupFixtures.defaultSellerOptionGroup();

            // when
            SellerOptionGroupJpaEntity entity = mapper.toOptionGroupEntity(domain);

            // then
            assertThat(entity.getCanonicalOptionGroupId()).isNull();
        }

        @Test
        @DisplayName("캐노니컬 매핑된 SellerOptionGroup을 Entity로 변환합니다")
        void toOptionGroupEntity_WithMappedGroup_ConvertsCanonicalId() {
            // given
            SellerOptionGroup domain = ProductGroupFixtures.mappedSellerOptionGroup();

            // when
            SellerOptionGroupJpaEntity entity = mapper.toOptionGroupEntity(domain);

            // then
            assertThat(entity.getCanonicalOptionGroupId()).isNotNull();
            assertThat(entity.getCanonicalOptionGroupId())
                    .isEqualTo(domain.canonicalOptionGroupId().value());
        }
    }

    // ========================================================================
    // 4. toOptionValueEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toOptionValueEntity 메서드 테스트")
    class ToOptionValueEntityTest {

        @Test
        @DisplayName("SellerOptionValue의 모든 필드를 Entity로 변환합니다")
        void toOptionValueEntity_WithValidValue_ConvertsAllFields() {
            // given
            SellerOptionValue domain = ProductGroupFixtures.defaultSellerOptionValue();

            // when
            SellerOptionValueJpaEntity entity = mapper.toOptionValueEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerOptionGroupId())
                    .isEqualTo(domain.sellerOptionGroupIdValue());
            assertThat(entity.getOptionValueName()).isEqualTo(domain.optionValueNameValue());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrder());
            assertThat(entity.isDeleted()).isFalse();
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("캐노니컬 매핑이 없는 SellerOptionValue의 canonicalOptionValueId는 null입니다")
        void toOptionValueEntity_WithNoCanonicalMapping_CanonicalIdIsNull() {
            // given
            SellerOptionValue domain = ProductGroupFixtures.defaultSellerOptionValue();

            // when
            SellerOptionValueJpaEntity entity = mapper.toOptionValueEntity(domain);

            // then
            assertThat(entity.getCanonicalOptionValueId()).isNull();
        }

        @Test
        @DisplayName("캐노니컬 매핑된 SellerOptionValue를 Entity로 변환합니다")
        void toOptionValueEntity_WithMappedValue_ConvertsCanonicalId() {
            // given
            SellerOptionValue domain = ProductGroupFixtures.mappedSellerOptionValue();

            // when
            SellerOptionValueJpaEntity entity = mapper.toOptionValueEntity(domain);

            // then
            assertThat(entity.getCanonicalOptionValueId()).isNotNull();
            assertThat(entity.getCanonicalOptionValueId())
                    .isEqualTo(domain.canonicalOptionValueId().value());
        }
    }

    // ========================================================================
    // 5. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("Entity와 빈 관련 데이터로 ProductGroup Domain을 생성합니다")
        void toDomain_WithEmptyRelatedEntities_ConvertsBasicFields() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.activeEntity(1L);
            List<ProductGroupImageJpaEntity> images = List.of();
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.brandIdValue()).isEqualTo(entity.getBrandId());
            assertThat(domain.categoryIdValue()).isEqualTo(entity.getCategoryId());
            assertThat(domain.shippingPolicyIdValue()).isEqualTo(entity.getShippingPolicyId());
            assertThat(domain.refundPolicyIdValue()).isEqualTo(entity.getRefundPolicyId());
            assertThat(domain.productGroupNameValue()).isEqualTo(entity.getProductGroupName());
            assertThat(domain.optionType()).isEqualTo(OptionType.valueOf(entity.getOptionType()));
            assertThat(domain.status()).isEqualTo(ProductGroupStatus.ACTIVE);
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 forNew ID가 생성됩니다")
        void toDomain_WithNullId_CreatesNewId() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.newEntity();
            List<ProductGroupImageJpaEntity> images = List.of();
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.idValue()).isNull();
        }

        @Test
        @DisplayName("DELETED 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsStatus() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.deletedEntity();
            List<ProductGroupImageJpaEntity> images = List.of();
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.status()).isEqualTo(ProductGroupStatus.DELETED);
        }

        @Test
        @DisplayName("DRAFT 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDraftEntity_ConvertsStatus() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.draftEntity();
            List<ProductGroupImageJpaEntity> images = List.of();
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.status()).isEqualTo(ProductGroupStatus.DRAFT);
        }

        @Test
        @DisplayName("INACTIVE 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsStatus() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.inactiveEntity();
            List<ProductGroupImageJpaEntity> images = List.of();
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.status()).isEqualTo(ProductGroupStatus.INACTIVE);
        }

        @Test
        @DisplayName("이미지 Entity 목록을 포함하여 Domain으로 변환합니다")
        void toDomain_WithImageEntities_ConvertsImages() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.activeEntity(1L);
            List<ProductGroupImageJpaEntity> images =
                    List.of(
                            ProductGroupImageJpaEntityFixtures.thumbnailEntity(1L, 1L),
                            ProductGroupImageJpaEntityFixtures.thumbnailEntity(2L, 1L));
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.images()).hasSize(2);
        }

        @Test
        @DisplayName("옵션 그룹과 옵션 값을 포함하여 Domain으로 변환합니다")
        void toDomain_WithOptionGroupsAndValues_ConvertsOptions() {
            // given
            Long productGroupId = 1L;
            ProductGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeEntity(productGroupId);
            SellerOptionGroupJpaEntity groupEntity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(
                            10L, productGroupId);
            SellerOptionValueJpaEntity valueEntity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(100L, 10L);
            List<ProductGroupImageJpaEntity> images = List.of();

            // when
            ProductGroup domain =
                    mapper.toDomain(entity, images, List.of(groupEntity), List.of(valueEntity));

            // then
            assertThat(domain.sellerOptionGroups()).hasSize(1);
            assertThat(domain.sellerOptionGroups().get(0).optionValues()).hasSize(1);
        }

        @Test
        @DisplayName("여러 옵션 그룹에 옵션 값이 올바르게 분배됩니다")
        void toDomain_WithMultipleOptionGroups_DistributesValuesCorrectly() {
            // given
            Long productGroupId = 1L;
            ProductGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeEntity(productGroupId);

            SellerOptionGroupJpaEntity colorGroup =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(
                            10L, productGroupId);
            SellerOptionGroupJpaEntity sizeGroup =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(
                            20L, productGroupId);

            SellerOptionValueJpaEntity colorValue1 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(100L, 10L);
            SellerOptionValueJpaEntity colorValue2 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(101L, 10L);
            SellerOptionValueJpaEntity sizeValue1 =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(200L, 20L);

            List<ProductGroupImageJpaEntity> images = List.of();

            // when
            ProductGroup domain =
                    mapper.toDomain(
                            entity,
                            images,
                            List.of(colorGroup, sizeGroup),
                            List.of(colorValue1, colorValue2, sizeValue1));

            // then
            assertThat(domain.sellerOptionGroups()).hasSize(2);
            SellerOptionGroup firstGroup = domain.sellerOptionGroups().get(0);
            SellerOptionGroup secondGroup = domain.sellerOptionGroups().get(1);

            // 10L 그룹에 2개, 20L 그룹에 1개
            assertThat(firstGroup.optionValues()).hasSize(2);
            assertThat(secondGroup.optionValues()).hasSize(1);
        }

        @Test
        @DisplayName("옵션 그룹에 매칭되는 옵션 값이 없으면 빈 리스트로 생성됩니다")
        void toDomain_WithOptionGroupButNoValues_CreatesGroupWithEmptyValues() {
            // given
            Long productGroupId = 1L;
            ProductGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeEntity(productGroupId);
            SellerOptionGroupJpaEntity groupEntity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(
                            10L, productGroupId);
            List<ProductGroupImageJpaEntity> images = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, List.of(groupEntity), List.of());

            // then
            assertThat(domain.sellerOptionGroups()).hasSize(1);
            assertThat(domain.sellerOptionGroups().get(0).optionValues()).isEmpty();
        }

        @Test
        @DisplayName("SINGLE 옵션 타입 Entity를 Domain으로 변환합니다")
        void toDomain_WithSingleOptionType_ConvertsOptionType() {
            // given
            ProductGroupJpaEntity entity = ProductGroupJpaEntityFixtures.entityWithSingleOption();
            List<ProductGroupImageJpaEntity> images = List.of();
            List<SellerOptionGroupJpaEntity> groups = List.of();
            List<SellerOptionValueJpaEntity> values = List.of();

            // when
            ProductGroup domain = mapper.toDomain(entity, images, groups, values);

            // then
            assertThat(domain.optionType()).isEqualTo(OptionType.SINGLE);
        }
    }

    // ========================================================================
    // 6. toImageDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageDomain 메서드 테스트")
    class ToImageDomainTest {

        @Test
        @DisplayName("업로드 완료된 이미지 Entity의 모든 필드를 Domain으로 변환합니다")
        void toImageDomain_WithUploadedUrl_ConvertsAllFields() {
            // given
            ProductGroupImageJpaEntity entity =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(1L, 1L);

            // when
            ProductGroupImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.originUrlValue()).isEqualTo(entity.getOriginUrl());
            assertThat(domain.uploadedUrlValue()).isEqualTo(entity.getUploadedUrl());
            assertThat(domain.imageType()).isEqualTo(ImageType.valueOf(entity.getImageType()));
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
            assertThat(domain.isDeleted()).isEqualTo(entity.isDeleted());
        }

        @Test
        @DisplayName("업로드 URL이 없는 이미지 Entity를 Domain으로 변환합니다")
        void toImageDomain_WithNullUploadedUrl_ConvertsWithNullUploadedUrl() {
            // given - ID가 있고 uploadedUrl이 null인 Entity 직접 생성
            ProductGroupImageJpaEntity entity =
                    ProductGroupImageJpaEntity.create(
                            50L,
                            1L,
                            "https://example.com/pending.jpg",
                            null,
                            "THUMBNAIL",
                            0,
                            false,
                            null);

            // when
            ProductGroupImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.uploadedUrl()).isNull();
            assertThat(domain.uploadedUrlValue()).isNull();
            assertThat(domain.isUploaded()).isFalse();
        }

        @Test
        @DisplayName("DETAIL 타입 이미지 Entity를 Domain으로 변환합니다")
        void toImageDomain_WithDetailType_ConvertsImageType() {
            // given - ID가 있는 DETAIL Entity 직접 생성
            ProductGroupImageJpaEntity entity =
                    ProductGroupImageJpaEntity.create(
                            51L,
                            1L,
                            "https://example.com/detail.jpg",
                            "https://s3.example.com/detail.jpg",
                            "DETAIL",
                            3,
                            false,
                            null);

            // when
            ProductGroupImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.imageType()).isEqualTo(ImageType.DETAIL);
            assertThat(domain.sortOrder()).isEqualTo(3);
        }

        @Test
        @DisplayName("삭제된 이미지 Entity를 Domain으로 변환합니다")
        void toImageDomain_WithDeletedEntity_ConvertsDeletionStatus() {
            // given - ID가 있는 삭제된 Entity 직접 생성
            Instant deletedAt = Instant.now();
            ProductGroupImageJpaEntity entity =
                    ProductGroupImageJpaEntity.create(
                            52L,
                            1L,
                            "https://example.com/deleted.jpg",
                            "https://s3.example.com/deleted.jpg",
                            "THUMBNAIL",
                            0,
                            true,
                            deletedAt);

            // when
            ProductGroupImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletionStatus().deletedAt()).isEqualTo(deletedAt);
        }
    }

    // ========================================================================
    // 7. toOptionGroupDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toOptionGroupDomain 메서드 테스트")
    class ToOptionGroupDomainTest {

        @Test
        @DisplayName("캐노니컬 매핑이 없는 옵션 그룹 Entity를 Domain으로 변환합니다")
        void toOptionGroupDomain_WithNoCanonicalMapping_ConvertsWithNullCanonicalId() {
            // given
            SellerOptionGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(10L, 1L);
            List<SellerOptionValue> values = List.of();

            // when
            SellerOptionGroup domain = mapper.toOptionGroupDomain(entity, values);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.optionGroupNameValue()).isEqualTo(entity.getOptionGroupName());
            assertThat(domain.canonicalOptionGroupId()).isNull();
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.optionValues()).isEmpty();
        }

        @Test
        @DisplayName("캐노니컬 매핑된 옵션 그룹 Entity를 Domain으로 변환합니다")
        void toOptionGroupDomain_WithCanonicalMapping_ConvertsCanonicalId() {
            // given
            SellerOptionGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.mappedOptionGroupEntityWithId(10L, 1L, 100L);
            List<SellerOptionValue> values = List.of();

            // when
            SellerOptionGroup domain = mapper.toOptionGroupDomain(entity, values);

            // then
            assertThat(domain.canonicalOptionGroupId()).isNotNull();
            assertThat(domain.canonicalOptionGroupId().value())
                    .isEqualTo(entity.getCanonicalOptionGroupId());
            assertThat(domain.isMappedToCanonical()).isTrue();
        }

        @Test
        @DisplayName("옵션 값 목록을 포함한 옵션 그룹 Entity를 Domain으로 변환합니다")
        void toOptionGroupDomain_WithValues_ConvertsWithValues() {
            // given
            SellerOptionGroupJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionGroupEntityWithId(10L, 1L);
            List<SellerOptionValue> values =
                    List.of(
                            ProductGroupFixtures.defaultSellerOptionValue(),
                            ProductGroupFixtures.mappedSellerOptionValue());

            // when
            SellerOptionGroup domain = mapper.toOptionGroupDomain(entity, values);

            // then
            assertThat(domain.optionValues()).hasSize(2);
        }

        @Test
        @DisplayName("삭제된 옵션 그룹 Entity를 Domain으로 변환합니다")
        void toOptionGroupDomain_WithDeletedEntity_ConvertsDeletionStatus() {
            // given - ID가 있는 삭제된 Entity 직접 생성
            Instant deletedAt = Instant.now();
            SellerOptionGroupJpaEntity entity =
                    SellerOptionGroupJpaEntity.create(
                            30L, 1L, "삭제된 색상", null, "PREDEFINED", 0, true, deletedAt);
            List<SellerOptionValue> values = List.of();

            // when
            SellerOptionGroup domain = mapper.toOptionGroupDomain(entity, values);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletionStatus().deletedAt()).isEqualTo(deletedAt);
        }
    }

    // ========================================================================
    // 8. toOptionValueDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toOptionValueDomain 메서드 테스트")
    class ToOptionValueDomainTest {

        @Test
        @DisplayName("캐노니컬 매핑이 없는 옵션 값 Entity를 Domain으로 변환합니다")
        void toOptionValueDomain_WithNoCanonicalMapping_ConvertsWithNullCanonicalId() {
            // given
            SellerOptionValueJpaEntity entity =
                    ProductGroupJpaEntityFixtures.activeOptionValueEntityWithId(100L, 10L);

            // when
            SellerOptionValue domain = mapper.toOptionValueDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerOptionGroupIdValue())
                    .isEqualTo(entity.getSellerOptionGroupId());
            assertThat(domain.optionValueNameValue()).isEqualTo(entity.getOptionValueName());
            assertThat(domain.canonicalOptionValueId()).isNull();
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
            assertThat(domain.isDeleted()).isFalse();
            assertThat(domain.isMappedToCanonical()).isFalse();
        }

        @Test
        @DisplayName("캐노니컬 매핑된 옵션 값 Entity를 Domain으로 변환합니다")
        void toOptionValueDomain_WithCanonicalMapping_ConvertsCanonicalId() {
            // given
            SellerOptionValueJpaEntity entity =
                    ProductGroupJpaEntityFixtures.mappedOptionValueEntityWithId(100L, 10L, 200L);

            // when
            SellerOptionValue domain = mapper.toOptionValueDomain(entity);

            // then
            assertThat(domain.canonicalOptionValueId()).isNotNull();
            assertThat(domain.canonicalOptionValueId().value())
                    .isEqualTo(entity.getCanonicalOptionValueId());
            assertThat(domain.isMappedToCanonical()).isTrue();
        }

        @Test
        @DisplayName("삭제된 옵션 값 Entity를 Domain으로 변환합니다")
        void toOptionValueDomain_WithDeletedEntity_ConvertsDeletionStatus() {
            // given - ID가 있는 삭제된 Entity 직접 생성
            Instant deletedAt = Instant.now();
            SellerOptionValueJpaEntity entity =
                    SellerOptionValueJpaEntity.create(
                            300L, 10L, "삭제된 검정", null, 0, true, deletedAt);

            // when
            SellerOptionValue domain = mapper.toOptionValueDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletionStatus().deletedAt()).isEqualTo(deletedAt);
        }
    }
}
