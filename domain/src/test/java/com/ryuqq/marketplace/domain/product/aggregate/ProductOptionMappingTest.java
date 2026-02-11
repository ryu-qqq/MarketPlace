package com.ryuqq.marketplace.domain.product.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.product.id.ProductOptionMappingId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductOptionMapping Entity 테스트")
class ProductOptionMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 옵션 매핑 생성")
    class ForNewTest {

        @Test
        @DisplayName("필수 정보로 신규 옵션 매핑을 생성한다")
        void createNewProductOptionMapping() {
            // given
            ProductId productId = ProductId.of(1L);
            SellerOptionValueId sellerOptionValueId = SellerOptionValueId.of(100L);

            // when
            ProductOptionMapping mapping = ProductOptionMapping.forNew(productId, sellerOptionValueId);

            // then
            assertThat(mapping.id().isNew()).isTrue();
            assertThat(mapping.productId()).isEqualTo(productId);
            assertThat(mapping.sellerOptionValueId()).isEqualTo(sellerOptionValueId);
        }
    }

    @Nested
    @DisplayName("reconstitute() - 영속성에서 복원")
    class ReconstituteTest {

        @Test
        @DisplayName("영속성에서 옵션 매핑을 복원한다")
        void reconstituteProductOptionMapping() {
            // given
            ProductOptionMappingId id = ProductOptionMappingId.of(1L);
            ProductId productId = ProductId.of(10L);
            SellerOptionValueId sellerOptionValueId = SellerOptionValueId.of(100L);

            // when
            ProductOptionMapping mapping = ProductOptionMapping.reconstitute(
                    id, productId, sellerOptionValueId);

            // then
            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.idValue()).isEqualTo(1L);
            assertThat(mapping.productId()).isEqualTo(productId);
            assertThat(mapping.productIdValue()).isEqualTo(10L);
            assertThat(mapping.sellerOptionValueId()).isEqualTo(sellerOptionValueId);
            assertThat(mapping.sellerOptionValueIdValue()).isEqualTo(100L);
        }
    }

    @Nested
    @DisplayName("Accessor 메서드 테스트")
    class AccessorTest {

        @Test
        @DisplayName("idValue()는 ID의 값을 반환한다")
        void idValueReturnsIdValue() {
            // given
            ProductOptionMapping mapping = ProductOptionMapping.reconstitute(
                    ProductOptionMappingId.of(123L),
                    ProductId.of(1L),
                    SellerOptionValueId.of(100L));

            // when & then
            assertThat(mapping.idValue()).isEqualTo(123L);
        }

        @Test
        @DisplayName("productIdValue()는 ProductId의 값을 반환한다")
        void productIdValueReturnsValue() {
            // given
            ProductOptionMapping mapping = ProductFixtures.defaultOptionMapping();

            // when & then
            assertThat(mapping.productIdValue()).isEqualTo(1L);
        }

        @Test
        @DisplayName("sellerOptionValueIdValue()는 SellerOptionValueId의 값을 반환한다")
        void sellerOptionValueIdValueReturnsValue() {
            // given
            ProductOptionMapping mapping = ProductFixtures.defaultOptionMapping();

            // when & then
            assertThat(mapping.sellerOptionValueIdValue()).isEqualTo(100L);
        }
    }
}
