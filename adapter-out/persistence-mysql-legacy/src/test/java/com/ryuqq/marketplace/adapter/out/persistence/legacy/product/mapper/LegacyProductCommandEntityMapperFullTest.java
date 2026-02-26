package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductDeliveryEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductNoticeEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductOptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductStockEntity;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.legacy.optiondetail.id.LegacyOptionDetailId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.product.aggregate.LegacyProduct;
import com.ryuqq.marketplace.domain.legacy.product.id.LegacyProductId;
import com.ryuqq.marketplace.domain.legacy.product.vo.LegacyProductOption;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyDescriptionImage;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDelivery;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ReturnMethod;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ShipmentCompanyCode;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacy.productimage.vo.ProductGroupImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionPublishStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyProductCommandEntityMapperFullTest - 레거시 상품 Command Mapper 전체 메서드 단위 테스트.
 *
 * <p>Product, Stock, Option, Notice, Delivery, Image, Description 변환을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyProductCommandEntityMapper 전체 메서드 테스트")
class LegacyProductCommandEntityMapperFullTest {

    private LegacyProductCommandEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyProductCommandEntityMapper();
    }

    // ========================================================================
    // toEntity(LegacyProduct) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProduct) 변환 테스트")
    class ToEntityFromProductTest {

        @Test
        @DisplayName("LegacyProduct를 LegacyProductEntity로 변환합니다")
        void toEntity_WithValidProduct_ReturnsValidEntity() {
            // given
            LegacyProduct product =
                    LegacyProduct.forNew(LegacyProductGroupId.of(1L), "N", "Y", 10, List.of());

            // when
            LegacyProductEntity entity = mapper.toEntity(product);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getSoldOutYn()).isEqualTo("N");
            assertThat(entity.getDisplayYn()).isEqualTo("Y");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }
    }

    // ========================================================================
    // toStockEntity(LegacyProduct) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toStockEntity(LegacyProduct) 변환 테스트")
    class ToStockEntityFromProductTest {

        @Test
        @DisplayName("LegacyProduct를 LegacyProductStockEntity로 변환합니다")
        void toStockEntity_WithValidProduct_ReturnsValidEntity() {
            // given
            LegacyProduct product =
                    LegacyProduct.reconstitute(
                            10L, 1L, "N", "Y", 5, List.of(), DeletionStatus.active());

            // when
            LegacyProductStockEntity entity = mapper.toStockEntity(product);

            // then
            assertThat(entity.getProductId()).isEqualTo(10L);
            assertThat(entity.getStockQuantity()).isEqualTo(5);
        }
    }

    // ========================================================================
    // toEntity(LegacyProductId, int) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductId, stockQuantity) 변환 테스트")
    class ToEntityFromProductIdTest {

        @Test
        @DisplayName("상품 ID와 재고 수량으로 재고 엔티티를 생성합니다")
        void toEntity_WithProductIdAndQuantity_ReturnsStockEntity() {
            // given
            LegacyProductId productId = LegacyProductId.of(1L);
            int stockQuantity = 100;

            // when
            LegacyProductStockEntity entity = mapper.toEntity(productId, stockQuantity);

            // then
            assertThat(entity.getProductId()).isEqualTo(1L);
            assertThat(entity.getStockQuantity()).isEqualTo(100);
        }
    }

    // ========================================================================
    // toEntity(LegacyProductOption) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductOption) 변환 테스트")
    class ToEntityFromProductOptionTest {

        @Test
        @DisplayName("LegacyProductOption을 LegacyProductOptionEntity로 변환합니다")
        void toEntity_WithValidOption_ReturnsValidEntity() {
            // given
            LegacyProductOption option =
                    LegacyProductOption.forNew(
                            LegacyProductId.of(1L),
                            LegacyOptionGroupId.of(10L),
                            LegacyOptionDetailId.of(100L),
                            500L);

            // when
            LegacyProductOptionEntity entity = mapper.toEntity(option);

            // then
            assertThat(entity.getProductId()).isEqualTo(1L);
            assertThat(entity.getOptionGroupId()).isEqualTo(10L);
            assertThat(entity.getOptionDetailId()).isEqualTo(100L);
            assertThat(entity.getAdditionalPrice()).isEqualTo(500L);
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }
    }

    // ========================================================================
    // toProductDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toProductDomain 변환 테스트")
    class ToProductDomainTest {

        @Test
        @DisplayName("LegacyProductEntity로 LegacyProduct 도메인을 복원합니다")
        void toProductDomain_WithValidEntity_ReturnsValidDomain() {
            // given
            LegacyProductEntity entity = LegacyProductEntity.create(10L, 1L, "N", "Y", "N");
            int stockQuantity = 5;
            List<LegacyProductOption> options = List.of();

            // when
            LegacyProduct domain = mapper.toProductDomain(entity, stockQuantity, options);

            // then
            assertThat(domain.idValue()).isEqualTo(10L);
            assertThat(domain.productGroupIdValue()).isEqualTo(1L);
            assertThat(domain.stockQuantity()).isEqualTo(5);
            assertThat(domain.soldOutYn()).isEqualTo("N");
            assertThat(domain.displayYn()).isEqualTo("Y");
        }
    }

    // ========================================================================
    // toOptionDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toOptionDomain 변환 테스트")
    class ToOptionDomainTest {

        @Test
        @DisplayName("LegacyProductOptionEntity로 LegacyProductOption 도메인을 복원합니다")
        void toOptionDomain_WithValidEntity_ReturnsValidDomain() {
            // given
            LegacyProductOptionEntity entity =
                    LegacyProductOptionEntity.create(1L, 10L, 100L, 1000L, 500L, "N");

            // when
            LegacyProductOption domain = mapper.toOptionDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(1L);
            assertThat(domain.productId().value()).isEqualTo(10L);
            assertThat(domain.optionGroupId().value()).isEqualTo(100L);
            assertThat(domain.optionDetailId().value()).isEqualTo(1000L);
            assertThat(domain.additionalPrice()).isEqualTo(500L);
        }
    }

    // ========================================================================
    // toEntity(LegacyProductGroupId, LegacyProductNotice) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductGroupId, LegacyProductNotice) 변환 테스트")
    class ToEntityFromNoticeTest {

        @Test
        @DisplayName("고시정보를 LegacyProductNoticeEntity로 변환합니다")
        void toEntity_WithValidNotice_ReturnsValidEntity() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductNotice notice =
                    new LegacyProductNotice(
                            "면 100%",
                            "블랙", "M", "삼성", "한국", "물세탁", "2024-01-01", "KC인증", "1588-1234");

            // when
            LegacyProductNoticeEntity entity = mapper.toEntity(productGroupId, notice);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getMaterial()).isEqualTo("면 100%");
            assertThat(entity.getColor()).isEqualTo("블랙");
            assertThat(entity.getSize()).isEqualTo("M");
        }
    }

    // ========================================================================
    // toEntity(LegacyProductGroupId, LegacyProductDelivery) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductGroupId, LegacyProductDelivery) 변환 테스트")
    class ToEntityFromDeliveryTest {

        @Test
        @DisplayName("배송정보를 LegacyProductDeliveryEntity로 변환합니다")
        void toEntity_WithValidDelivery_ReturnsValidEntity() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductDelivery delivery =
                    new LegacyProductDelivery(
                            "전국",
                            3000L,
                            3,
                            ReturnMethod.RETURN_CONSUMER,
                            ShipmentCompanyCode.SHIP04,
                            5000,
                            "서울시 강남구");

            // when
            LegacyProductDeliveryEntity entity = mapper.toEntity(productGroupId, delivery);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getDeliveryArea()).isEqualTo("전국");
            assertThat(entity.getDeliveryFee()).isEqualTo(3000L);
            assertThat(entity.getDeliveryPeriodAverage()).isEqualTo(3);
        }
    }

    // ========================================================================
    // toEntity(LegacyProductImage) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductImage) 변환 테스트")
    class ToEntityFromImageTest {

        @Test
        @DisplayName("LegacyProductImage를 LegacyProductGroupImageEntity로 변환합니다")
        void toEntity_WithValidImage_ReturnsValidEntity() {
            // given
            LegacyProductImage image =
                    LegacyProductImage.forNew(
                            LegacyProductGroupId.of(1L),
                            ProductGroupImageType.MAIN,
                            "https://cdn.example.com/main.jpg",
                            "https://origin.example.com/main.jpg",
                            1);

            // when
            LegacyProductGroupImageEntity entity = mapper.toEntity(image);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getProductGroupImageType()).isEqualTo("MAIN");
            assertThat(entity.getImageUrl()).isEqualTo("https://cdn.example.com/main.jpg");
            assertThat(entity.getOriginUrl()).isEqualTo("https://origin.example.com/main.jpg");
            assertThat(entity.getDisplayOrder()).isEqualTo(1L);
        }
    }

    // ========================================================================
    // toImageDomain(LegacyProductGroupImageEntity) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageDomain(LegacyProductGroupImageEntity) 변환 테스트")
    class ToImageDomainTest {

        @Test
        @DisplayName("LegacyProductGroupImageEntity로 LegacyProductImage 도메인을 복원합니다")
        void toImageDomain_WithValidEntity_ReturnsValidDomain() {
            // given
            LegacyProductGroupImageEntity entity =
                    LegacyProductGroupImageEntity.create(
                            1L,
                            10L,
                            "MAIN",
                            "https://cdn.example.com/main.jpg",
                            "https://origin.example.com/main.jpg",
                            1L,
                            "N");

            // when
            LegacyProductImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.productGroupIdValue()).isEqualTo(10L);
            assertThat(domain.imageType()).isEqualTo(ProductGroupImageType.MAIN);
            assertThat(domain.imageUrl()).isEqualTo("https://cdn.example.com/main.jpg");
            assertThat(domain.displayOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("displayOrder가 null인 경우 0으로 변환됩니다")
        void toImageDomain_WithNullDisplayOrder_Returns0() {
            // given
            LegacyProductGroupImageEntity entity =
                    LegacyProductGroupImageEntity.create(
                            1L,
                            10L,
                            "MAIN",
                            "https://cdn.example.com/main.jpg",
                            "https://origin.example.com/main.jpg",
                            0L,
                            "N");

            // when
            LegacyProductImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.displayOrder()).isEqualTo(0);
        }
    }

    // ========================================================================
    // toEntity(LegacyProductGroupId, LegacyProductDescription) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductGroupId, LegacyProductDescription) 변환 테스트")
    class ToEntityFromDescriptionTest {

        @Test
        @DisplayName("상세설명을 LegacyProductGroupDetailDescriptionEntity로 변환합니다")
        void toEntity_WithValidDescription_ReturnsValidEntity() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductDescription description = new LegacyProductDescription("<p>상세 설명 내용</p>");

            // when
            LegacyProductGroupDetailDescriptionEntity entity =
                    mapper.toEntity(productGroupId, description);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getImageUrl()).isEqualTo("<p>상세 설명 내용</p>");
        }
    }

    // ========================================================================
    // toDescriptionEntity(LegacyProductGroupDescription) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDescriptionEntity(LegacyProductGroupDescription) 변환 테스트")
    class ToDescriptionEntityTest {

        @Test
        @DisplayName("LegacyProductGroupDescription을 전체 필드 포함 엔티티로 변환합니다")
        void toDescriptionEntity_WithValidDescription_ReturnsFullEntity() {
            // given
            LegacyProductGroupDescription description =
                    LegacyProductGroupDescription.forNew(1L, "<p>콘텐츠</p>");

            // when
            LegacyProductGroupDetailDescriptionEntity entity =
                    mapper.toDescriptionEntity(description);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getContent()).isEqualTo("<p>콘텐츠</p>");
            assertThat(entity.getPublishStatus()).isEqualTo("PENDING");
        }
    }

    // ========================================================================
    // toImageEntity(LegacyDescriptionImage) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageEntity(LegacyDescriptionImage) 변환 테스트")
    class ToImageEntityTest {

        @Test
        @DisplayName("LegacyDescriptionImage를 LegacyDescriptionImageEntity로 변환합니다")
        void toImageEntity_WithValidImage_ReturnsValidEntity() {
            // given
            LegacyDescriptionImage image =
                    LegacyDescriptionImage.forNew(1L, "https://origin.example.com/img.jpg", 1);

            // when
            LegacyDescriptionImageEntity entity = mapper.toImageEntity(image);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getOriginUrl()).isEqualTo("https://origin.example.com/img.jpg");
            assertThat(entity.getSortOrder()).isEqualTo(1);
            assertThat(entity.isDeleted()).isFalse();
        }
    }

    // ========================================================================
    // toImageDomain(LegacyDescriptionImageEntity) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toImageDomain(LegacyDescriptionImageEntity) 변환 테스트")
    class ToDescriptionImageDomainTest {

        @Test
        @DisplayName("LegacyDescriptionImageEntity로 LegacyDescriptionImage 도메인을 복원합니다")
        void toImageDomain_WithValidEntity_ReturnsValidDomain() {
            // given
            LegacyDescriptionImageEntity entity =
                    LegacyDescriptionImageEntity.create(
                            1L,
                            10L,
                            "https://origin.example.com/img.jpg",
                            "https://cdn.example.com/img.jpg",
                            2,
                            false,
                            null);

            // when
            LegacyDescriptionImage domain = mapper.toImageDomain(entity);

            // then
            assertThat(domain.id()).isEqualTo(1L);
            assertThat(domain.productGroupId()).isEqualTo(10L);
            assertThat(domain.originUrl()).isEqualTo("https://origin.example.com/img.jpg");
            assertThat(domain.uploadedUrl()).isEqualTo("https://cdn.example.com/img.jpg");
            assertThat(domain.sortOrder()).isEqualTo(2);
            assertThat(domain.isDeleted()).isFalse();
        }
    }

    // ========================================================================
    // toDescriptionDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDescriptionDomain 변환 테스트")
    class ToDescriptionDomainTest {

        @Test
        @DisplayName("content 필드로 LegacyProductGroupDescription 도메인을 복원합니다")
        void toDescriptionDomain_WithContentField_ReturnsValidDomain() {
            // given
            LegacyProductGroupDetailDescriptionEntity descEntity =
                    LegacyProductGroupDetailDescriptionEntity.createFull(
                            1L, "<p>콘텐츠</p>", "/cdn/path", "PUBLISHED");
            List<LegacyDescriptionImageEntity> imageEntities = List.of();

            // when
            LegacyProductGroupDescription domain =
                    mapper.toDescriptionDomain(descEntity, imageEntities);

            // then
            assertThat(domain.productGroupId()).isEqualTo(1L);
            assertThat(domain.content()).isEqualTo("<p>콘텐츠</p>");
            assertThat(domain.cdnPath()).isEqualTo("/cdn/path");
            assertThat(domain.publishStatus()).isEqualTo(DescriptionPublishStatus.PUBLISHED);
            assertThat(domain.images()).isEmpty();
        }

        @Test
        @DisplayName("content가 null인 경우 imageUrl을 사용합니다")
        void toDescriptionDomain_WithNullContent_UsesImageUrl() {
            // given
            LegacyProductGroupDetailDescriptionEntity descEntity =
                    LegacyProductGroupDetailDescriptionEntity.create(1L, "<p>레거시 이미지 URL</p>");
            List<LegacyDescriptionImageEntity> imageEntities = List.of();

            // when
            LegacyProductGroupDescription domain =
                    mapper.toDescriptionDomain(descEntity, imageEntities);

            // then
            assertThat(domain.content()).isEqualTo("<p>레거시 이미지 URL</p>");
            assertThat(domain.publishStatus()).isEqualTo(DescriptionPublishStatus.PENDING);
        }
    }

    // ========================================================================
    // toDomain(LegacyProductGroupEntity) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain(LegacyProductGroupEntity) 변환 테스트")
    class ToDomainFromEntityTest {

        @Test
        @DisplayName("LegacyProductGroupEntity로 LegacyProductGroup 도메인을 복원합니다")
        void toDomain_WithValidEntity_ReturnsValidDomain() {
            // given
            com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity
                            .LegacyProductGroupEntity
                    entity =
                            com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity
                                    .LegacyProductGroupEntity.create(
                                    1L,
                                    "테스트 상품",
                                    10L,
                                    20L,
                                    30L,
                                    "SINGLE",
                                    "MENUAL",
                                    50000L,
                                    45000L,
                                    "N",
                                    "Y",
                                    "NEW",
                                    "KR",
                                    "STYLE001");

            // when
            LegacyProductGroup domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.productGroupName()).isEqualTo("테스트 상품");
            assertThat(domain.sellerId()).isEqualTo(10L);
            assertThat(domain.brandId()).isEqualTo(20L);
            assertThat(domain.categoryId()).isEqualTo(30L);
            assertThat(domain.optionType()).isEqualTo(OptionType.SINGLE);
            assertThat(domain.managementType()).isEqualTo(ManagementType.MENUAL);
            assertThat(domain.regularPrice()).isEqualTo(50000L);
            assertThat(domain.currentPrice()).isEqualTo(45000L);
            assertThat(domain.origin()).isEqualTo(Origin.KR);
            assertThat(domain.productCondition()).isEqualTo(ProductCondition.NEW);
        }
    }
}
