package com.ryuqq.marketplace.domain.order.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.order.OrderFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InternalProductReference Value Object 테스트")
class InternalProductReferenceTest {

    @Nested
    @DisplayName("of() - 내부 상품 참조 생성")
    class OfTest {

        @Test
        @DisplayName("모든 필드로 내부 상품 참조를 생성한다")
        void createWithAllFields() {
            // when
            InternalProductReference ref =
                    InternalProductReference.of(
                            100L,
                            200L,
                            1L,
                            5L,
                            "SKU-001",
                            "상품그룹명",
                            "브랜드명",
                            "셀러명",
                            "https://img.example.com/main.jpg");

            // then
            assertThat(ref.productGroupId()).isEqualTo(100L);
            assertThat(ref.productId()).isEqualTo(200L);
            assertThat(ref.sellerId()).isEqualTo(1L);
            assertThat(ref.brandId()).isEqualTo(5L);
            assertThat(ref.skuCode()).isEqualTo("SKU-001");
            assertThat(ref.productGroupName()).isEqualTo("상품그룹명");
            assertThat(ref.brandName()).isEqualTo("브랜드명");
            assertThat(ref.sellerName()).isEqualTo("셀러명");
            assertThat(ref.mainImageUrl()).isEqualTo("https://img.example.com/main.jpg");
        }

        @Test
        @DisplayName("매핑되지 않은 경우 null 허용 필드는 null로 생성한다")
        void createWithNullableFields() {
            // when
            InternalProductReference ref =
                    InternalProductReference.of(
                            null, null, null, null, null, null, null, null, null);

            // then
            assertThat(ref.productGroupId()).isNull();
            assertThat(ref.productId()).isNull();
            assertThat(ref.sellerId()).isNull();
            assertThat(ref.brandId()).isNull();
            assertThat(ref.skuCode()).isNull();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 InternalProductReference는 동일하다")
        void sameValuesAreEqual() {
            // given
            InternalProductReference ref1 = OrderFixtures.defaultInternalProductReference();
            InternalProductReference ref2 = OrderFixtures.defaultInternalProductReference();

            // then
            assertThat(ref1).isEqualTo(ref2);
            assertThat(ref1.hashCode()).isEqualTo(ref2.hashCode());
        }

        @Test
        @DisplayName("다른 sellerId를 가진 InternalProductReference는 동일하지 않다")
        void differentSellerIdNotEqual() {
            // given
            InternalProductReference ref1 =
                    InternalProductReference.of(
                            100L, 200L, 1L, 5L, "SKU-001", null, null, null, null);
            InternalProductReference ref2 =
                    InternalProductReference.of(
                            100L, 200L, 2L, 5L, "SKU-001", null, null, null, null);

            // then
            assertThat(ref1).isNotEqualTo(ref2);
        }
    }
}
