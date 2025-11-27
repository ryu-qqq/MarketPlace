package com.ryuqq.marketplace.domain.category.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Category VO 단위 테스트
 *
 * <p><strong>테스트 대상 (12개 VO)</strong>:</p>
 * <ul>
 *   <li>CategoryId</li>
 *   <li>CategoryCode</li>
 *   <li>CategoryName</li>
 *   <li>CategoryDepth</li>
 *   <li>CategoryPath</li>
 *   <li>SortOrder</li>
 *   <li>CategoryStatus</li>
 *   <li>CategoryVisibility</li>
 *   <li>ProductGroup</li>
 *   <li>CategoryMeta</li>
 *   <li>AgeGroup</li>
 *   <li>GenderScope</li>
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
@DisplayName("Category VO 단위 테스트")
@Tag("unit")
@Tag("domain")
@Tag("category")
@Tag("vo")
class CategoryVoTest {

    // ==================== CategoryId 테스트 ====================

    @Nested
    @DisplayName("CategoryId 테스트")
    class CategoryIdTest {

        @Test
        @DisplayName("[성공] forNew()로 신규 ID 생성")
        void forNew_ShouldCreateNewId() {
            // When
            CategoryId id = CategoryId.forNew();

            // Then
            assertThat(id.value()).isNull();
            assertThat(id.isNew()).isTrue();
        }

        @Test
        @DisplayName("[성공] of()로 값 기반 생성")
        void of_WithValidValue_ShouldCreate() {
            // When
            CategoryId id = CategoryId.of(1L);

            // Then
            assertThat(id.value()).isEqualTo(1L);
            assertThat(id.isNew()).isFalse();
        }

        @Test
        @DisplayName("[성공] 큰 ID 값 허용")
        void of_WithLargeValue_ShouldCreate() {
            // When
            CategoryId id = CategoryId.of(Long.MAX_VALUE);

            // Then
            assertThat(id.value()).isEqualTo(Long.MAX_VALUE);
        }

        @Test
        @DisplayName("[실패] of()에 null 전달 시 예외")
        void of_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> CategoryId.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] 0 이하 값 예외")
        void of_WithZeroOrNegative_ShouldThrow() {
            assertThatThrownBy(() -> CategoryId.of(0L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("양수");

            assertThatThrownBy(() -> CategoryId.of(-1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("양수");
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryId id1 = CategoryId.of(1L);
            CategoryId id2 = CategoryId.of(1L);
            CategoryId id3 = CategoryId.of(2L);

            assertThat(id1).isEqualTo(id2);
            assertThat(id1).isNotEqualTo(id3);
            assertThat(id1.hashCode()).isEqualTo(id2.hashCode());
        }
    }

    // ==================== CategoryCode 테스트 ====================

    @Nested
    @DisplayName("CategoryCode 테스트")
    class CategoryCodeTest {

        @Test
        @DisplayName("[성공] 유효한 코드 생성")
        void of_WithValidCode_ShouldCreate() {
            // When
            CategoryCode code = CategoryCode.of("FASHION");

            // Then
            assertThat(code.value()).isEqualTo("FASHION");
        }

        @ParameterizedTest
        @ValueSource(strings = {"A", "FASHION", "MEN_CLOTHING", "A1", "CATEGORY_123_TEST"})
        @DisplayName("[성공] 다양한 유효 패턴 테스트")
        void of_WithValidPatterns_ShouldCreate(String value) {
            // When
            CategoryCode code = CategoryCode.of(value);

            // Then
            assertThat(code.value()).isEqualTo(value);
        }

        @Test
        @DisplayName("[성공] 최대 길이 100자 코드")
        void of_WithMaxLength_ShouldCreate() {
            // Given
            String maxLengthCode = "A" + "B".repeat(99);  // 100자

            // When
            CategoryCode code = CategoryCode.of(maxLengthCode);

            // Then
            assertThat(code.value()).hasSize(100);
        }

        @Test
        @DisplayName("[실패] null 코드 예외")
        void of_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> CategoryCode.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] 빈 문자열 예외")
        void of_WithEmpty_ShouldThrow() {
            assertThatThrownBy(() -> CategoryCode.of(""))
                .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> CategoryCode.of("   "))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @ValueSource(strings = {"fashion", "Fashion", "1FASHION", "_FASHION", "123", "FASHION-TEST"})
        @DisplayName("[실패] 잘못된 패턴 예외")
        void of_WithInvalidPatterns_ShouldThrow(String value) {
            assertThatThrownBy(() -> CategoryCode.of(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("대문자로 시작");
        }

        @Test
        @DisplayName("[실패] 101자 초과 예외")
        void of_WithExceedMaxLength_ShouldThrow() {
            String tooLongCode = "A" + "B".repeat(100);  // 101자

            assertThatThrownBy(() -> CategoryCode.of(tooLongCode))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryCode code1 = CategoryCode.of("FASHION");
            CategoryCode code2 = CategoryCode.of("FASHION");
            CategoryCode code3 = CategoryCode.of("BEAUTY");

            assertThat(code1).isEqualTo(code2);
            assertThat(code1).isNotEqualTo(code3);
        }
    }

    // ==================== CategoryName 테스트 ====================

    @Nested
    @DisplayName("CategoryName 테스트")
    class CategoryNameTest {

        @Test
        @DisplayName("[성공] 한국어와 영어 둘 다 있는 경우")
        void of_WithBothNames_ShouldCreate() {
            // When
            CategoryName name = CategoryName.of("패션", "Fashion");

            // Then
            assertThat(name.ko()).isEqualTo("패션");
            assertThat(name.en()).isEqualTo("Fashion");
            assertThat(name.displayName()).isEqualTo("패션");  // 한국어 우선
        }

        @Test
        @DisplayName("[성공] 한국어만 있는 경우")
        void of_WithKoreanOnly_ShouldCreate() {
            // When
            CategoryName name = CategoryName.of("패션", null);

            // Then
            assertThat(name.ko()).isEqualTo("패션");
            assertThat(name.en()).isNull();
            assertThat(name.displayName()).isEqualTo("패션");
        }

        @Test
        @DisplayName("[성공] 영어만 있는 경우")
        void of_WithEnglishOnly_ShouldCreate() {
            // When
            CategoryName name = CategoryName.of(null, "Fashion");

            // Then
            assertThat(name.ko()).isNull();
            assertThat(name.en()).isEqualTo("Fashion");
            assertThat(name.displayName()).isEqualTo("Fashion");
        }

        @Test
        @DisplayName("[성공] displayName은 한국어 우선")
        void displayName_ShouldPreferKorean() {
            CategoryName name = CategoryName.of("패션", "Fashion");

            assertThat(name.displayName()).isEqualTo("패션");
        }

        @Test
        @DisplayName("[성공] 한국어가 빈 문자열이면 영어 반환")
        void displayName_WithEmptyKorean_ShouldReturnEnglish() {
            CategoryName name = CategoryName.of("", "Fashion");

            assertThat(name.displayName()).isEqualTo("Fashion");
        }

        @Test
        @DisplayName("[실패] 둘 다 null이면 예외")
        void of_WithBothNull_ShouldThrow() {
            assertThatThrownBy(() -> CategoryName.of(null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("[실패] 둘 다 빈 문자열이면 예외")
        void of_WithBothEmpty_ShouldThrow() {
            assertThatThrownBy(() -> CategoryName.of("", ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("필수");

            assertThatThrownBy(() -> CategoryName.of("   ", "   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("필수");
        }

        @Test
        @DisplayName("[실패] 한국어 이름 255자 초과 예외")
        void of_WithKoreanExceedMaxLength_ShouldThrow() {
            String tooLong = "가".repeat(256);

            assertThatThrownBy(() -> CategoryName.of(tooLong, "Fashion"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
        }

        @Test
        @DisplayName("[실패] 영어 이름 255자 초과 예외")
        void of_WithEnglishExceedMaxLength_ShouldThrow() {
            String tooLong = "A".repeat(256);

            assertThatThrownBy(() -> CategoryName.of("패션", tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryName name1 = CategoryName.of("패션", "Fashion");
            CategoryName name2 = CategoryName.of("패션", "Fashion");
            CategoryName name3 = CategoryName.of("뷰티", "Beauty");

            assertThat(name1).isEqualTo(name2);
            assertThat(name1).isNotEqualTo(name3);
        }
    }

    // ==================== CategoryDepth 테스트 ====================

    @Nested
    @DisplayName("CategoryDepth 테스트")
    class CategoryDepthTest {

        @Test
        @DisplayName("[성공] 루트 깊이 (0) 생성")
        void of_WithZero_ShouldCreate() {
            // When
            CategoryDepth depth = CategoryDepth.of(0);

            // Then
            assertThat(depth.value()).isEqualTo(0);
            assertThat(depth.isRoot()).isTrue();
        }

        @Test
        @DisplayName("[성공] 중간 깊이 생성")
        void of_WithMiddleValue_ShouldCreate() {
            // When
            CategoryDepth depth = CategoryDepth.of(5);

            // Then
            assertThat(depth.value()).isEqualTo(5);
            assertThat(depth.isRoot()).isFalse();
        }

        @Test
        @DisplayName("[성공] 최대 깊이 (10) 생성")
        void of_WithMaxDepth_ShouldCreate() {
            // When
            CategoryDepth depth = CategoryDepth.of(10);

            // Then
            assertThat(depth.value()).isEqualTo(10);
        }

        @Test
        @DisplayName("[성공] increment() 깊이 증가")
        void increment_ShouldIncreaseDepth() {
            // Given
            CategoryDepth depth = CategoryDepth.of(5);

            // When
            CategoryDepth incremented = depth.increment();

            // Then
            assertThat(incremented.value()).isEqualTo(6);
            assertThat(depth.value()).isEqualTo(5);  // 원본 불변
        }

        @Test
        @DisplayName("[성공] isRoot() 루트 확인")
        void isRoot_ShouldReturnTrue_WhenDepthIsZero() {
            assertThat(CategoryDepth.of(0).isRoot()).isTrue();
            assertThat(CategoryDepth.of(1).isRoot()).isFalse();
        }

        @Test
        @DisplayName("[실패] 음수 깊이 예외")
        void of_WithNegative_ShouldThrow() {
            assertThatThrownBy(() -> CategoryDepth.of(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("음수");
        }

        @Test
        @DisplayName("[실패] 최대 깊이 초과 예외")
        void of_WithExceedMax_ShouldThrow() {
            assertThatThrownBy(() -> CategoryDepth.of(11))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10");
        }

        @Test
        @DisplayName("[실패] increment 후 최대 깊이 초과 예외")
        void increment_WhenAtMax_ShouldThrow() {
            CategoryDepth maxDepth = CategoryDepth.of(10);

            assertThatThrownBy(maxDepth::increment)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("10");
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryDepth depth1 = CategoryDepth.of(5);
            CategoryDepth depth2 = CategoryDepth.of(5);
            CategoryDepth depth3 = CategoryDepth.of(6);

            assertThat(depth1).isEqualTo(depth2);
            assertThat(depth1).isNotEqualTo(depth3);
        }
    }

    // ==================== CategoryPath 테스트 ====================

    @Nested
    @DisplayName("CategoryPath 테스트")
    class CategoryPathTest {

        @Test
        @DisplayName("[성공] root() 루트 경로 생성")
        void root_ShouldCreateRootPath() {
            // When
            CategoryPath path = CategoryPath.root(1L);

            // Then
            assertThat(path.value()).isEqualTo("1");
            assertThat(path.depth()).isEqualTo(0);
        }

        @Test
        @DisplayName("[성공] of() 문자열로 생성")
        void of_WithValidPath_ShouldCreate() {
            // When
            CategoryPath path = CategoryPath.of("1/10/100");

            // Then
            assertThat(path.value()).isEqualTo("1/10/100");
        }

        @Test
        @DisplayName("[성공] appendChild() 자식 추가")
        void appendChild_ShouldAddChild() {
            // Given
            CategoryPath path = CategoryPath.root(1L);

            // When
            CategoryPath childPath = path.appendChild(10L);

            // Then
            assertThat(childPath.value()).isEqualTo("1/10");
            assertThat(path.value()).isEqualTo("1");  // 원본 불변
        }

        @Test
        @DisplayName("[성공] 연속 appendChild() 호출")
        void appendChild_ChainedCalls_ShouldWork() {
            // Given
            CategoryPath path = CategoryPath.root(1L);

            // When
            CategoryPath fullPath = path.appendChild(10L).appendChild(100L);

            // Then
            assertThat(fullPath.value()).isEqualTo("1/10/100");
        }

        @Test
        @DisplayName("[성공] toIdList() ID 리스트 변환")
        void toIdList_ShouldReturnIdList() {
            // Given
            CategoryPath path = CategoryPath.of("1/10/100");

            // When
            List<Long> idList = path.toIdList();

            // Then
            assertThat(idList).containsExactly(1L, 10L, 100L);
        }

        @Test
        @DisplayName("[성공] depth() 깊이 계산")
        void depth_ShouldCalculateCorrectly() {
            assertThat(CategoryPath.of("1").depth()).isEqualTo(0);
            assertThat(CategoryPath.of("1/10").depth()).isEqualTo(1);
            assertThat(CategoryPath.of("1/10/100").depth()).isEqualTo(2);
            assertThat(CategoryPath.of("1/10/100/1000").depth()).isEqualTo(3);
        }

        @Test
        @DisplayName("[성공] isDescendantOf() 하위 경로 확인")
        void isDescendantOf_ShouldWork() {
            CategoryPath parent = CategoryPath.of("1");
            CategoryPath child = CategoryPath.of("1/10");
            CategoryPath grandChild = CategoryPath.of("1/10/100");
            CategoryPath other = CategoryPath.of("2/20");

            assertThat(child.isDescendantOf(parent)).isTrue();
            assertThat(grandChild.isDescendantOf(parent)).isTrue();
            assertThat(grandChild.isDescendantOf(child)).isTrue();
            assertThat(parent.isDescendantOf(child)).isFalse();
            assertThat(other.isDescendantOf(parent)).isFalse();
        }

        @Test
        @DisplayName("[실패] null 경로 예외")
        void of_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> CategoryPath.of(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] 빈 경로 예외")
        void of_WithEmpty_ShouldThrow() {
            assertThatThrownBy(() -> CategoryPath.of(""))
                .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> CategoryPath.of("   "))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[실패] 1000자 초과 예외")
        void of_WithExceedMaxLength_ShouldThrow() {
            String tooLong = "1/" + "0".repeat(1000);

            assertThatThrownBy(() -> CategoryPath.of(tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1000");
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryPath path1 = CategoryPath.of("1/10/100");
            CategoryPath path2 = CategoryPath.of("1/10/100");
            CategoryPath path3 = CategoryPath.of("1/10/200");

            assertThat(path1).isEqualTo(path2);
            assertThat(path1).isNotEqualTo(path3);
        }
    }

    // ==================== SortOrder 테스트 ====================

    @Nested
    @DisplayName("SortOrder 테스트")
    class SortOrderTest {

        @Test
        @DisplayName("[성공] of() 값 기반 생성")
        void of_WithValidValue_ShouldCreate() {
            // When
            SortOrder order = SortOrder.of(5);

            // Then
            assertThat(order.value()).isEqualTo(5);
        }

        @Test
        @DisplayName("[성공] defaultOrder() 기본 정렬 순서")
        void defaultOrder_ShouldReturnZero() {
            // When
            SortOrder order = SortOrder.defaultOrder();

            // Then
            assertThat(order.value()).isEqualTo(0);
        }

        @Test
        @DisplayName("[성공] 0 허용")
        void of_WithZero_ShouldCreate() {
            // When
            SortOrder order = SortOrder.of(0);

            // Then
            assertThat(order.value()).isEqualTo(0);
        }

        @Test
        @DisplayName("[성공] 큰 값 허용")
        void of_WithLargeValue_ShouldCreate() {
            // When
            SortOrder order = SortOrder.of(Integer.MAX_VALUE);

            // Then
            assertThat(order.value()).isEqualTo(Integer.MAX_VALUE);
        }

        @Test
        @DisplayName("[실패] 음수 값 예외")
        void of_WithNegative_ShouldThrow() {
            assertThatThrownBy(() -> SortOrder.of(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("음수");
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            SortOrder order1 = SortOrder.of(5);
            SortOrder order2 = SortOrder.of(5);
            SortOrder order3 = SortOrder.of(10);

            assertThat(order1).isEqualTo(order2);
            assertThat(order1).isNotEqualTo(order3);
        }
    }

    // ==================== CategoryStatus 테스트 ====================

    @Nested
    @DisplayName("CategoryStatus 테스트")
    class CategoryStatusTest {

        @Test
        @DisplayName("[성공] ACTIVE 상태 속성")
        void active_ShouldHaveCorrectProperties() {
            CategoryStatus status = CategoryStatus.ACTIVE;

            assertThat(status.displayName()).isEqualTo("활성");
            assertThat(status.isUsable()).isTrue();
            assertThat(status.isVisible()).isTrue();
        }

        @Test
        @DisplayName("[성공] INACTIVE 상태 속성")
        void inactive_ShouldHaveCorrectProperties() {
            CategoryStatus status = CategoryStatus.INACTIVE;

            assertThat(status.displayName()).isEqualTo("비활성");
            assertThat(status.isUsable()).isFalse();
            assertThat(status.isVisible()).isFalse();
        }

        @Test
        @DisplayName("[성공] DEPRECATED 상태 속성")
        void deprecated_ShouldHaveCorrectProperties() {
            CategoryStatus status = CategoryStatus.DEPRECATED;

            assertThat(status.displayName()).isEqualTo("폐기");
            assertThat(status.isUsable()).isFalse();
            assertThat(status.isVisible()).isFalse();
        }

        @Test
        @DisplayName("[성공] fromString() 문자열 변환")
        void fromString_WithValidValues_ShouldConvert() {
            assertThat(CategoryStatus.fromString("ACTIVE")).isEqualTo(CategoryStatus.ACTIVE);
            assertThat(CategoryStatus.fromString("active")).isEqualTo(CategoryStatus.ACTIVE);
            assertThat(CategoryStatus.fromString("Active")).isEqualTo(CategoryStatus.ACTIVE);
            assertThat(CategoryStatus.fromString("INACTIVE")).isEqualTo(CategoryStatus.INACTIVE);
            assertThat(CategoryStatus.fromString("DEPRECATED")).isEqualTo(CategoryStatus.DEPRECATED);
        }

        @Test
        @DisplayName("[실패] fromString() null 예외")
        void fromString_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> CategoryStatus.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] fromString() 빈 문자열 예외")
        void fromString_WithEmpty_ShouldThrow() {
            assertThatThrownBy(() -> CategoryStatus.fromString(""))
                .isInstanceOf(IllegalArgumentException.class);

            assertThatThrownBy(() -> CategoryStatus.fromString("   "))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[실패] fromString() 잘못된 값 예외")
        void fromString_WithInvalidValue_ShouldThrow() {
            assertThatThrownBy(() -> CategoryStatus.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid");
        }

        @Test
        @DisplayName("[성공] 모든 상태 값 확인")
        void allValues_ShouldExist() {
            assertThat(CategoryStatus.values()).hasSize(3);
        }
    }

    // ==================== CategoryVisibility 테스트 ====================

    @Nested
    @DisplayName("CategoryVisibility 테스트")
    class CategoryVisibilityTest {

        @Test
        @DisplayName("[성공] visible() 표시 가능 생성")
        void visible_ShouldCreateVisibleVisibility() {
            // When
            CategoryVisibility visibility = CategoryVisibility.visible();

            // Then
            assertThat(visibility.isVisible()).isTrue();
            assertThat(visibility.isListable()).isTrue();
            assertThat(visibility.canDisplay()).isTrue();
            assertThat(visibility.canListProducts()).isTrue();
        }

        @Test
        @DisplayName("[성공] hidden() 숨김 생성")
        void hidden_ShouldCreateHiddenVisibility() {
            // When
            CategoryVisibility visibility = CategoryVisibility.hidden();

            // Then
            assertThat(visibility.isVisible()).isFalse();
            assertThat(visibility.isListable()).isFalse();
            assertThat(visibility.canDisplay()).isFalse();
            assertThat(visibility.canListProducts()).isFalse();
        }

        @Test
        @DisplayName("[성공] of() 커스텀 생성")
        void of_ShouldCreateCustomVisibility() {
            // When - 표시는 하지만 상품 등록은 불가
            CategoryVisibility visibility = CategoryVisibility.of(true, false);

            // Then
            assertThat(visibility.isVisible()).isTrue();
            assertThat(visibility.isListable()).isFalse();
            assertThat(visibility.canDisplay()).isTrue();
            assertThat(visibility.canListProducts()).isFalse();
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryVisibility v1 = CategoryVisibility.visible();
            CategoryVisibility v2 = CategoryVisibility.of(true, true);
            CategoryVisibility v3 = CategoryVisibility.hidden();

            assertThat(v1).isEqualTo(v2);
            assertThat(v1).isNotEqualTo(v3);
        }
    }

    // ==================== ProductGroup 테스트 ====================

    @Nested
    @DisplayName("ProductGroup 테스트")
    class ProductGroupTest {

        @Test
        @DisplayName("[성공] 모든 ProductGroup 값 확인")
        void allValues_ShouldExist() {
            assertThat(ProductGroup.values()).hasSize(9);
        }

        @Test
        @DisplayName("[성공] displayName() 한글 표시명")
        void displayName_ShouldReturnKorean() {
            assertThat(ProductGroup.CLOTHING.displayName()).isEqualTo("의류");
            assertThat(ProductGroup.SHOES.displayName()).isEqualTo("신발");
            assertThat(ProductGroup.BAGS.displayName()).isEqualTo("가방");
            assertThat(ProductGroup.ACCESSORIES.displayName()).isEqualTo("액세서리");
            assertThat(ProductGroup.JEWELRY.displayName()).isEqualTo("주얼리");
            assertThat(ProductGroup.BEAUTY.displayName()).isEqualTo("뷰티");
            assertThat(ProductGroup.HOME.displayName()).isEqualTo("홈/리빙");
            assertThat(ProductGroup.ELECTRONICS.displayName()).isEqualTo("전자기기");
            assertThat(ProductGroup.ETC.displayName()).isEqualTo("기타");
        }

        @Test
        @DisplayName("[성공] fromString() 문자열 변환")
        void fromString_WithValidValues_ShouldConvert() {
            assertThat(ProductGroup.fromString("CLOTHING")).isEqualTo(ProductGroup.CLOTHING);
            assertThat(ProductGroup.fromString("clothing")).isEqualTo(ProductGroup.CLOTHING);
            assertThat(ProductGroup.fromString("Clothing")).isEqualTo(ProductGroup.CLOTHING);
            assertThat(ProductGroup.fromString("  SHOES  ")).isEqualTo(ProductGroup.SHOES);
        }

        @Test
        @DisplayName("[실패] fromString() null 예외")
        void fromString_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> ProductGroup.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] fromString() 빈 문자열 예외")
        void fromString_WithEmpty_ShouldThrow() {
            assertThatThrownBy(() -> ProductGroup.fromString(""))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[실패] fromString() 잘못된 값 예외")
        void fromString_WithInvalidValue_ShouldThrow() {
            assertThatThrownBy(() -> ProductGroup.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은");
        }
    }

    // ==================== CategoryMeta 테스트 ====================

    @Nested
    @DisplayName("CategoryMeta 테스트")
    class CategoryMetaTest {

        @Test
        @DisplayName("[성공] of() 값 기반 생성")
        void of_WithValidValues_ShouldCreate() {
            // When
            CategoryMeta meta = CategoryMeta.of("패션 카테고리", "fashion", "https://cdn.example.com/icon.png");

            // Then
            assertThat(meta.displayName()).isEqualTo("패션 카테고리");
            assertThat(meta.seoSlug()).isEqualTo("fashion");
            assertThat(meta.iconUrl()).isEqualTo("https://cdn.example.com/icon.png");
        }

        @Test
        @DisplayName("[성공] empty() 빈 메타데이터 생성")
        void empty_ShouldCreateEmptyMeta() {
            // When
            CategoryMeta meta = CategoryMeta.empty();

            // Then
            assertThat(meta.displayName()).isNull();
            assertThat(meta.seoSlug()).isNull();
            assertThat(meta.iconUrl()).isNull();
        }

        @Test
        @DisplayName("[성공] 일부 필드만 있는 경우")
        void of_WithPartialValues_ShouldCreate() {
            // When
            CategoryMeta meta = CategoryMeta.of("패션", null, null);

            // Then
            assertThat(meta.displayName()).isEqualTo("패션");
            assertThat(meta.seoSlug()).isNull();
            assertThat(meta.iconUrl()).isNull();
        }

        @Test
        @DisplayName("[실패] displayName 255자 초과 예외")
        void of_WithDisplayNameExceedMax_ShouldThrow() {
            String tooLong = "A".repeat(256);

            assertThatThrownBy(() -> CategoryMeta.of(tooLong, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
        }

        @Test
        @DisplayName("[실패] seoSlug 255자 초과 예외")
        void of_WithSeoSlugExceedMax_ShouldThrow() {
            String tooLong = "a".repeat(256);

            assertThatThrownBy(() -> CategoryMeta.of(null, tooLong, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("255");
        }

        @Test
        @DisplayName("[실패] iconUrl 500자 초과 예외")
        void of_WithIconUrlExceedMax_ShouldThrow() {
            String tooLong = "https://example.com/" + "a".repeat(500);

            assertThatThrownBy(() -> CategoryMeta.of(null, null, tooLong))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("500");
        }

        @Test
        @DisplayName("[성공] 동등성 테스트")
        void equality_ShouldWork() {
            CategoryMeta meta1 = CategoryMeta.of("패션", "fashion", "https://icon.png");
            CategoryMeta meta2 = CategoryMeta.of("패션", "fashion", "https://icon.png");
            CategoryMeta meta3 = CategoryMeta.of("뷰티", "beauty", "https://icon.png");

            assertThat(meta1).isEqualTo(meta2);
            assertThat(meta1).isNotEqualTo(meta3);
        }
    }

    // ==================== AgeGroup 테스트 ====================

    @Nested
    @DisplayName("AgeGroup 테스트")
    class AgeGroupTest {

        @Test
        @DisplayName("[성공] 모든 AgeGroup 값 확인")
        void allValues_ShouldExist() {
            assertThat(AgeGroup.values()).hasSize(6);
        }

        @Test
        @DisplayName("[성공] displayName() 한글 표시명")
        void displayName_ShouldReturnKorean() {
            assertThat(AgeGroup.INFANT.displayName()).isEqualTo("유아");
            assertThat(AgeGroup.KIDS.displayName()).isEqualTo("어린이");
            assertThat(AgeGroup.TEEN.displayName()).isEqualTo("청소년");
            assertThat(AgeGroup.ADULT.displayName()).isEqualTo("성인");
            assertThat(AgeGroup.SENIOR.displayName()).isEqualTo("노년");
            assertThat(AgeGroup.NONE.displayName()).isEqualTo("해당 없음");
        }

        @Test
        @DisplayName("[성공] fromString() 문자열 변환")
        void fromString_WithValidValues_ShouldConvert() {
            assertThat(AgeGroup.fromString("ADULT")).isEqualTo(AgeGroup.ADULT);
            assertThat(AgeGroup.fromString("adult")).isEqualTo(AgeGroup.ADULT);
            assertThat(AgeGroup.fromString("Adult")).isEqualTo(AgeGroup.ADULT);
            assertThat(AgeGroup.fromString("  KIDS  ")).isEqualTo(AgeGroup.KIDS);
        }

        @Test
        @DisplayName("[실패] fromString() null 예외")
        void fromString_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> AgeGroup.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] fromString() 빈 문자열 예외")
        void fromString_WithEmpty_ShouldThrow() {
            assertThatThrownBy(() -> AgeGroup.fromString(""))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[실패] fromString() 잘못된 값 예외")
        void fromString_WithInvalidValue_ShouldThrow() {
            assertThatThrownBy(() -> AgeGroup.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은");
        }
    }

    // ==================== GenderScope 테스트 ====================

    @Nested
    @DisplayName("GenderScope 테스트")
    class GenderScopeTest {

        @Test
        @DisplayName("[성공] 모든 GenderScope 값 확인")
        void allValues_ShouldExist() {
            assertThat(GenderScope.values()).hasSize(5);
        }

        @Test
        @DisplayName("[성공] displayName() 한글 표시명")
        void displayName_ShouldReturnKorean() {
            assertThat(GenderScope.MEN.displayName()).isEqualTo("남성");
            assertThat(GenderScope.WOMEN.displayName()).isEqualTo("여성");
            assertThat(GenderScope.UNISEX.displayName()).isEqualTo("남녀공용");
            assertThat(GenderScope.KIDS.displayName()).isEqualTo("아동");
            assertThat(GenderScope.NONE.displayName()).isEqualTo("해당 없음");
        }

        @Test
        @DisplayName("[성공] fromString() 문자열 변환")
        void fromString_WithValidValues_ShouldConvert() {
            assertThat(GenderScope.fromString("MEN")).isEqualTo(GenderScope.MEN);
            assertThat(GenderScope.fromString("men")).isEqualTo(GenderScope.MEN);
            assertThat(GenderScope.fromString("Men")).isEqualTo(GenderScope.MEN);
            assertThat(GenderScope.fromString("  WOMEN  ")).isEqualTo(GenderScope.WOMEN);
        }

        @Test
        @DisplayName("[실패] fromString() null 예외")
        void fromString_WithNull_ShouldThrow() {
            assertThatThrownBy(() -> GenderScope.fromString(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @Test
        @DisplayName("[실패] fromString() 빈 문자열 예외")
        void fromString_WithEmpty_ShouldThrow() {
            assertThatThrownBy(() -> GenderScope.fromString(""))
                .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("[실패] fromString() 잘못된 값 예외")
        void fromString_WithInvalidValue_ShouldThrow() {
            assertThatThrownBy(() -> GenderScope.fromString("INVALID"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("유효하지 않은");
        }
    }
}
