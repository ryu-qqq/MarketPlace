package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupName Value Object 단위 테스트")
class ProductGroupNameTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("유효한 값으로 ProductGroupName을 생성한다")
        void createWithValidValue() {
            // when
            ProductGroupName name = ProductGroupName.of("테스트 상품");

            // then
            assertThat(name.value()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("앞뒤 공백은 제거된다")
        void trimWhitespace() {
            // when
            ProductGroupName name = ProductGroupName.of("  테스트 상품  ");

            // then
            assertThat(name.value()).isEqualTo("테스트 상품");
        }

        @Test
        @DisplayName("null 값으로 생성하면 예외가 발생한다")
        void createWithNullThrowsException() {
            // when & then
            assertThatThrownBy(() -> ProductGroupName.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 그룹명은 필수입니다");
        }

        @Test
        @DisplayName("빈 문자열로 생성하면 예외가 발생한다")
        void createWithBlankThrowsException() {
            // when & then
            assertThatThrownBy(() -> ProductGroupName.of(""))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 그룹명은 필수입니다");

            assertThatThrownBy(() -> ProductGroupName.of("   "))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("상품 그룹명은 필수입니다");
        }

        @Test
        @DisplayName("최대 길이를 초과하면 예외가 발생한다")
        void createWithExceedingMaxLengthThrowsException() {
            // given
            String tooLongName = "a".repeat(201);

            // when & then
            assertThatThrownBy(() -> ProductGroupName.of(tooLongName))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("200자 이내");
        }

        @Test
        @DisplayName("최대 길이 200자는 허용된다")
        void createWithMaxLength() {
            // given
            String maxLengthName = "a".repeat(200);

            // when
            ProductGroupName name = ProductGroupName.of(maxLengthName);

            // then
            assertThat(name.value()).hasSize(200);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값이면 동일하다")
        void sameValueAreEqual() {
            // given
            ProductGroupName name1 = ProductGroupName.of("테스트");
            ProductGroupName name2 = ProductGroupName.of("테스트");

            // then
            assertThat(name1).isEqualTo(name2);
            assertThat(name1.hashCode()).isEqualTo(name2.hashCode());
        }

        @Test
        @DisplayName("다른 값이면 동일하지 않다")
        void differentValueAreNotEqual() {
            // given
            ProductGroupName name1 = ProductGroupName.of("테스트1");
            ProductGroupName name2 = ProductGroupName.of("테스트2");

            // then
            assertThat(name1).isNotEqualTo(name2);
        }
    }
}
