package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("unit")
@DisplayName("CategoryDepth Value Object 단위 테스트")
class CategoryDepthTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("0으로 루트 깊이를 생성한다")
        void createRootDepth() {
            CategoryDepth depth = CategoryDepth.of(0);

            assertThat(depth.value()).isZero();
            assertThat(depth.isRoot()).isTrue();
        }

        @ParameterizedTest
        @ValueSource(ints = {0, 1, 5, 10})
        @DisplayName("유효한 범위(0~10)의 깊이로 생성한다")
        void createWithValidRange(int value) {
            CategoryDepth depth = CategoryDepth.of(value);

            assertThat(depth.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("음수이면 예외가 발생한다")
        void createWithNegative_ThrowsException() {
            assertThatThrownBy(() -> CategoryDepth.of(-1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("범위");
        }

        @Test
        @DisplayName("10 초과이면 예외가 발생한다")
        void createWithMoreThan10_ThrowsException() {
            assertThatThrownBy(() -> CategoryDepth.of(11))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("범위");
        }
    }

    @Nested
    @DisplayName("root() 팩토리 메서드")
    class RootTest {

        @Test
        @DisplayName("root()는 깊이 0을 반환한다")
        void rootReturnsDepthZero() {
            CategoryDepth root = CategoryDepth.root();

            assertThat(root.value()).isZero();
            assertThat(root.isRoot()).isTrue();
        }
    }

    @Nested
    @DisplayName("child() - 자식 깊이 생성")
    class ChildTest {

        @Test
        @DisplayName("루트 깊이에서 자식 깊이는 1이다")
        void childDepthFromRootIsOne() {
            CategoryDepth root = CategoryDepth.root();

            CategoryDepth child = root.child();

            assertThat(child.value()).isEqualTo(1);
        }

        @Test
        @DisplayName("깊이 2에서 자식 깊이는 3이다")
        void childDepthIncrementsByOne() {
            CategoryDepth depth2 = CategoryDepth.of(2);

            CategoryDepth child = depth2.child();

            assertThat(child.value()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("isRoot() 테스트")
    class IsRootTest {

        @Test
        @DisplayName("깊이 0은 루트다")
        void depthZeroIsRoot() {
            assertThat(CategoryDepth.of(0).isRoot()).isTrue();
        }

        @Test
        @DisplayName("깊이 1 이상은 루트가 아니다")
        void depthAboveZeroIsNotRoot() {
            assertThat(CategoryDepth.of(1).isRoot()).isFalse();
            assertThat(CategoryDepth.of(5).isRoot()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 깊이는 동일하다")
        void sameDepthAreEqual() {
            CategoryDepth depth1 = CategoryDepth.of(3);
            CategoryDepth depth2 = CategoryDepth.of(3);

            assertThat(depth1).isEqualTo(depth2);
            assertThat(depth1.hashCode()).isEqualTo(depth2.hashCode());
        }

        @Test
        @DisplayName("다른 깊이는 동일하지 않다")
        void differentDepthAreNotEqual() {
            CategoryDepth depth1 = CategoryDepth.of(1);
            CategoryDepth depth2 = CategoryDepth.of(2);

            assertThat(depth1).isNotEqualTo(depth2);
        }
    }
}
