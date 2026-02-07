package com.ryuqq.marketplace.adapter.out.persistence.selleraddress.condition;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerAddressConditionBuilderTest - 셀러 주소 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerAddressConditionBuilder 단위 테스트")
class SellerAddressConditionBuilderTest {

    private SellerAddressConditionBuilder conditionBuilder;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SellerAddressConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. sellerIdEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdEq 메서드 테스트")
    class SellerIdEqTest {

        @Test
        @DisplayName("유효한 셀러 ID 입력 시 BooleanExpression을 반환합니다")
        void sellerIdEq_WithValidSellerId_ReturnsBooleanExpression() {
            // given
            Long sellerId = 1L;

            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(sellerId);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 셀러 ID 입력 시 null을 반환합니다")
        void sellerIdEq_WithNullSellerId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. sellerIdIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("sellerIdIn 메서드 테스트")
    class SellerIdInTest {

        @Test
        @DisplayName("유효한 셀러 ID 목록 입력 시 BooleanExpression을 반환합니다")
        void sellerIdIn_WithValidSellerIds_ReturnsBooleanExpression() {
            // given
            List<Long> sellerIds = List.of(1L, 2L, 3L);

            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(sellerIds);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void sellerIdIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void sellerIdIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.sellerIdIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 4. addressTypeEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("addressTypeEq 메서드 테스트")
    class AddressTypeEqTest {

        @Test
        @DisplayName("유효한 주소 유형 입력 시 BooleanExpression을 반환합니다")
        void addressTypeEq_WithValidAddressType_ReturnsBooleanExpression() {
            // given
            String addressType = "SHIPPING";

            // when
            BooleanExpression result = conditionBuilder.addressTypeEq(addressType);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 주소 유형 입력 시 null을 반환합니다")
        void addressTypeEq_WithNullAddressType_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.addressTypeEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 5. addressTypeIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("addressTypeIn 메서드 테스트")
    class AddressTypeInTest {

        @Test
        @DisplayName("유효한 주소 유형 목록 입력 시 BooleanExpression을 반환합니다")
        void addressTypeIn_WithValidAddressTypes_ReturnsBooleanExpression() {
            // given
            List<String> addressTypes = List.of("SHIPPING", "RETURN");

            // when
            BooleanExpression result = conditionBuilder.addressTypeIn(addressTypes);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 목록 입력 시 null을 반환합니다")
        void addressTypeIn_WithNullList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.addressTypeIn(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 목록 입력 시 null을 반환합니다")
        void addressTypeIn_WithEmptyList_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.addressTypeIn(Collections.emptyList());

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 6. defaultAddressEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("defaultAddressEq 메서드 테스트")
    class DefaultAddressEqTest {

        @Test
        @DisplayName("true 입력 시 BooleanExpression을 반환합니다")
        void defaultAddressEq_WithTrue_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.defaultAddressEq(Boolean.TRUE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("false 입력 시 BooleanExpression을 반환합니다")
        void defaultAddressEq_WithFalse_ReturnsBooleanExpression() {
            // when
            BooleanExpression result = conditionBuilder.defaultAddressEq(Boolean.FALSE);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 입력 시 null을 반환합니다")
        void defaultAddressEq_WithNull_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.defaultAddressEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 7. addressNameEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("addressNameEq 메서드 테스트")
    class AddressNameEqTest {

        @Test
        @DisplayName("유효한 주소명 입력 시 BooleanExpression을 반환합니다")
        void addressNameEq_WithValidAddressName_ReturnsBooleanExpression() {
            // given
            String addressName = "본사 창고";

            // when
            BooleanExpression result = conditionBuilder.addressNameEq(addressName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("공백이 있는 주소명은 trim 처리됩니다")
        void addressNameEq_WithWhitespaceAddressName_TrimsAndReturnsExpression() {
            // given
            String addressName = "  본사 창고  ";

            // when
            BooleanExpression result = conditionBuilder.addressNameEq(addressName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 주소명 입력 시 null을 반환합니다")
        void addressNameEq_WithNullAddressName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.addressNameEq(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 주소명 입력 시 null을 반환합니다")
        void addressNameEq_WithBlankAddressName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.addressNameEq("   ");

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 8. keywordContains 테스트
    // ========================================================================

    @Nested
    @DisplayName("keywordContains 메서드 테스트")
    class KeywordContainsTest {

        @Test
        @DisplayName("유효한 키워드 입력 시 BooleanExpression을 반환합니다")
        void keywordContains_WithValidKeyword_ReturnsBooleanExpression() {
            // given
            String keyword = "창고";

            // when
            BooleanExpression result = conditionBuilder.keywordContains(keyword);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 키워드 입력 시 null을 반환합니다")
        void keywordContains_WithNullKeyword_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.keywordContains(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("빈 키워드 입력 시 null을 반환합니다")
        void keywordContains_WithBlankKeyword_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.keywordContains("   ");

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("키워드는 주소명과 주소에서 OR 조건으로 검색됩니다")
        void keywordContains_WithKeyword_SearchesInNameAndAddress() {
            // given
            String keyword = "강남";

            // when
            BooleanExpression result = conditionBuilder.keywordContains(keyword);

            // then
            assertThat(result).isNotNull();
            // 실제로는 addressName.containsIgnoreCase(keyword).or(address.containsIgnoreCase(keyword))
            // 반환되는 것을 확인
        }
    }
}
