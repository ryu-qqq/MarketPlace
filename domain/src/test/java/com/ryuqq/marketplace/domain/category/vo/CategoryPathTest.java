package com.ryuqq.marketplace.domain.category.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPath Value Object 단위 테스트")
class CategoryPathTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("루트 경로로 생성한다")
        void createRootPath() {
            CategoryPath path = CategoryPath.of("1");

            assertThat(path.value()).isEqualTo("1");
            assertThat(path.isRoot()).isTrue();
        }

        @Test
        @DisplayName("계층 경로로 생성한다")
        void createHierarchyPath() {
            CategoryPath path = CategoryPath.of("1/2/3");

            assertThat(path.value()).isEqualTo("1/2/3");
            assertThat(path.isRoot()).isFalse();
        }

        @Test
        @DisplayName("앞뒤 공백은 trim된다")
        void createWithWhitespaceTrimmed() {
            CategoryPath path = CategoryPath.of("  1/2  ");

            assertThat(path.value()).isEqualTo("1/2");
        }

        @Test
        @DisplayName("null이면 예외가 발생한다")
        void createWithNull_ThrowsException() {
            assertThatThrownBy(() -> CategoryPath.of(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("빈 문자열이면 예외가 발생한다")
        void createWithBlank_ThrowsException() {
            assertThatThrownBy(() -> CategoryPath.of(""))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("1000자 초과이면 예외가 발생한다")
        void createWithTooLong_ThrowsException() {
            String longPath = "1/" + "2/".repeat(500);
            assertThatThrownBy(() -> CategoryPath.of(longPath))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("1000자");
        }
    }

    @Nested
    @DisplayName("appendChild() - 자식 경로 생성")
    class AppendChildTest {

        @Test
        @DisplayName("루트 경로에 자식 ID를 추가한다")
        void appendChildToRootPath() {
            CategoryPath root = CategoryPath.of("1");

            CategoryPath child = root.appendChild(2L);

            assertThat(child.value()).isEqualTo("1/2");
            assertThat(child.isRoot()).isFalse();
        }

        @Test
        @DisplayName("계층 경로에 자식 ID를 추가한다")
        void appendChildToHierarchyPath() {
            CategoryPath parent = CategoryPath.of("1/2");

            CategoryPath child = parent.appendChild(3L);

            assertThat(child.value()).isEqualTo("1/2/3");
        }
    }

    @Nested
    @DisplayName("isRoot() 테스트")
    class IsRootTest {

        @Test
        @DisplayName("슬래시가 없는 경로는 루트다")
        void pathWithoutSlashIsRoot() {
            assertThat(CategoryPath.of("1").isRoot()).isTrue();
        }

        @Test
        @DisplayName("슬래시가 있는 경로는 루트가 아니다")
        void pathWithSlashIsNotRoot() {
            assertThat(CategoryPath.of("1/2").isRoot()).isFalse();
        }
    }

    @Nested
    @DisplayName("depth() - 포함된 카테고리 수")
    class DepthTest {

        @Test
        @DisplayName("루트 경로의 슬래시 수는 0이다")
        void rootPathHasZeroSlashes() {
            CategoryPath path = CategoryPath.of("1");

            assertThat(path.depth()).isZero();
        }

        @Test
        @DisplayName("슬래시 수가 경로 깊이를 나타낸다")
        void slashCountRepresentsDepth() {
            CategoryPath path = CategoryPath.of("1/2/3");

            assertThat(path.depth()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 경로는 동일하다")
        void samePathAreEqual() {
            CategoryPath path1 = CategoryPath.of("1/2");
            CategoryPath path2 = CategoryPath.of("1/2");

            assertThat(path1).isEqualTo(path2);
            assertThat(path1.hashCode()).isEqualTo(path2.hashCode());
        }

        @Test
        @DisplayName("다른 경로는 동일하지 않다")
        void differentPathAreNotEqual() {
            CategoryPath path1 = CategoryPath.of("1/2");
            CategoryPath path2 = CategoryPath.of("1/3");

            assertThat(path1).isNotEqualTo(path2);
        }
    }
}
