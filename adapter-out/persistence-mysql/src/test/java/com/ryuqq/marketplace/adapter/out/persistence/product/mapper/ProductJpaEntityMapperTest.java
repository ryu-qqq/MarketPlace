package com.ryuqq.marketplace.adapter.out.persistence.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.product.ProductJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.product.entity.ProductOptionMappingJpaEntity;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
import com.ryuqq.marketplace.domain.product.aggregate.ProductOptionMapping;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductJpaEntityMapperTest - 상품 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductJpaEntityMapper 단위 테스트")
class ProductJpaEntityMapperTest {

    private ProductJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("ACTIVE 상태 Product를 Entity로 변환합니다")
        void toEntity_WithActiveProduct_ConvertsCorrectly() {
            // given
            Product domain = ProductFixtures.activeProduct();

            // when
            ProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getSkuCode()).isEqualTo(domain.skuCodeValue());
            assertThat(entity.getRegularPrice()).isEqualTo(domain.regularPriceValue());
            assertThat(entity.getCurrentPrice()).isEqualTo(domain.currentPriceValue());
            assertThat(entity.getStatus()).isEqualTo(ProductStatus.ACTIVE.name());
        }

        @Test
        @DisplayName("세일 가격이 있는 Product를 Entity로 변환합니다")
        void toEntity_WithSalePrice_ConvertsSalePrice() {
            // given
            Product domain = ProductFixtures.activeProduct();

            // when
            ProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSalePrice()).isNotNull();
            assertThat(entity.getDiscountRate()).isEqualTo(domain.discountRate());
        }

        @Test
        @DisplayName("세일 가격이 없는 Product를 Entity로 변환합니다")
        void toEntity_WithoutSalePrice_ConvertsNullSalePrice() {
            // given
            Product domain = ProductFixtures.productWithoutSale();

            // when
            ProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getSalePrice()).isNull();
            assertThat(entity.getDiscountRate()).isZero();
        }

        @Test
        @DisplayName("DELETED 상태 Product를 Entity로 변환합니다")
        void toEntity_WithDeletedProduct_ConvertsStatus() {
            // given
            Product domain = ProductFixtures.deletedProduct();

            // when
            ProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo(ProductStatus.DELETED.name());
        }

        @Test
        @DisplayName("새 Product를 Entity로 변환 시 ID가 null입니다")
        void toEntity_WithNewProduct_IdIsNull() {
            // given
            Product domain = ProductFixtures.newProduct();

            // when
            ProductJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("ACTIVE 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            ProductJpaEntity entity = ProductJpaEntityFixtures.activeEntity(1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();

            // when
            Product domain = mapper.toDomain(entity, mappings);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.skuCodeValue()).isEqualTo(entity.getSkuCode());
            assertThat(domain.regularPriceValue()).isEqualTo(entity.getRegularPrice());
            assertThat(domain.currentPriceValue()).isEqualTo(entity.getCurrentPrice());
            assertThat(domain.status()).isEqualTo(ProductStatus.ACTIVE);
        }

        @Test
        @DisplayName("세일 가격이 있는 Entity를 Domain으로 변환합니다")
        void toDomain_WithSalePrice_ConvertsSalePrice() {
            // given
            ProductJpaEntity entity = ProductJpaEntityFixtures.activeEntity(1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();

            // when
            Product domain = mapper.toDomain(entity, mappings);

            // then
            assertThat(domain.salePriceValue()).isNotNull();
        }

        @Test
        @DisplayName("세일 가격이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutSalePrice_ConvertsNullSalePrice() {
            // given
            ProductJpaEntity entity = ProductJpaEntityFixtures.entityWithoutSalePrice(2L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();

            // when
            Product domain = mapper.toDomain(entity, mappings);

            // then
            assertThat(domain.salePriceValue()).isNull();
        }

        @Test
        @DisplayName("옵션 매핑이 있는 Entity를 Domain으로 변환합니다")
        void toDomain_WithOptionMappings_ConvertsMappings() {
            // given
            ProductJpaEntity entity = ProductJpaEntityFixtures.activeEntity(1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.savedSingleOptionMappings(10L, 1L);

            // when
            Product domain = mapper.toDomain(entity, mappings);

            // then
            assertThat(domain.optionMappings()).hasSize(1);
        }

        @Test
        @DisplayName("빈 옵션 매핑으로 Entity를 Domain으로 변환합니다")
        void toDomain_WithEmptyOptionMappings_ReturnsEmptyMappings() {
            // given
            ProductJpaEntity entity = ProductJpaEntityFixtures.activeEntity(1L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();

            // when
            Product domain = mapper.toDomain(entity, mappings);

            // then
            assertThat(domain.optionMappings()).isEmpty();
        }

        @Test
        @DisplayName("SOLDOUT 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithSoldOutEntity_ConvertsStatus() {
            // given
            ProductJpaEntity entity = ProductJpaEntityFixtures.soldOutEntity(3L);
            List<ProductOptionMappingJpaEntity> mappings =
                    ProductJpaEntityFixtures.emptyOptionMappings();

            // when
            Product domain = mapper.toDomain(entity, mappings);

            // then
            assertThat(domain.status()).isEqualTo(ProductStatus.SOLDOUT);
        }
    }

    // ========================================================================
    // 3. toMappingEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toMappingEntity 메서드 테스트")
    class ToMappingEntityTest {

        @Test
        @DisplayName("ProductOptionMapping을 Entity로 변환합니다")
        void toMappingEntity_WithValidMapping_ConvertsCorrectly() {
            // given
            ProductOptionMapping mapping = ProductFixtures.defaultOptionMapping();

            // when
            ProductOptionMappingJpaEntity entity = mapper.toMappingEntity(mapping);

            // then
            assertThat(entity.getProductId()).isEqualTo(mapping.productIdValue());
            assertThat(entity.getSellerOptionValueId())
                    .isEqualTo(mapping.sellerOptionValueIdValue());
        }

        @Test
        @DisplayName("productId를 오버라이드하여 ProductOptionMapping을 Entity로 변환합니다")
        void toMappingEntity_WithProductIdOverride_ConvertsWithOverriddenId() {
            // given
            ProductOptionMapping mapping = ProductFixtures.defaultOptionMapping();
            Long overrideProductId = 999L;

            // when
            ProductOptionMappingJpaEntity entity =
                    mapper.toMappingEntity(mapping, overrideProductId);

            // then
            assertThat(entity.getProductId()).isEqualTo(overrideProductId);
            assertThat(entity.getSellerOptionValueId())
                    .isEqualTo(mapping.sellerOptionValueIdValue());
        }
    }
}
