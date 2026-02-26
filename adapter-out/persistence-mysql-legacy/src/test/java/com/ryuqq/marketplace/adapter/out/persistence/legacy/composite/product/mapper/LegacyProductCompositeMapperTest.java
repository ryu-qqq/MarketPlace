package com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.composite.product.dto.LegacyProductOptionQueryDto;
import com.ryuqq.marketplace.application.legacy.shared.dto.composite.LegacyProductCompositeResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyProductCompositeMapperTest - 레거시 상품 Composite Mapper 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyProductCompositeMapper 단위 테스트")
class LegacyProductCompositeMapperTest {

    private final LegacyProductCompositeMapper mapper = new LegacyProductCompositeMapper();

    @Nested
    @DisplayName("toCompositeResults 메서드 테스트")
    class ToCompositeResultsTest {

        @Test
        @DisplayName("null 입력 시 빈 목록을 반환합니다")
        void toCompositeResults_WithNullRows_ReturnsEmptyList() {
            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(null);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("빈 목록 입력 시 빈 목록을 반환합니다")
        void toCompositeResults_WithEmptyRows_ReturnsEmptyList() {
            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("단일 상품(옵션 없음) 행을 Composite 결과로 변환합니다")
        void toCompositeResults_WithSingleProductNoOption_ReturnsSingleResult() {
            // given
            LegacyProductOptionQueryDto row =
                    new LegacyProductOptionQueryDto(1L, 10L, "N", 5, null, null, null, null);

            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(List.of(row));

            // then
            assertThat(result).hasSize(1);
            LegacyProductCompositeResult composite = result.get(0);
            assertThat(composite.productId()).isEqualTo(1L);
            assertThat(composite.productGroupId()).isEqualTo(10L);
            assertThat(composite.stockQuantity()).isEqualTo(5);
            assertThat(composite.soldOut()).isFalse();
            assertThat(composite.optionMappings()).isEmpty();
        }

        @Test
        @DisplayName("품절된 상품 행을 변환 시 soldOut이 true입니다")
        void toCompositeResults_WithSoldOutProduct_ReturnsSoldOutTrue() {
            // given
            LegacyProductOptionQueryDto row =
                    new LegacyProductOptionQueryDto(1L, 10L, "Y", 0, null, null, null, null);

            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(List.of(row));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).soldOut()).isTrue();
        }

        @Test
        @DisplayName("단일 상품에 여러 옵션이 있는 경우 옵션 목록이 합쳐집니다")
        void toCompositeResults_WithMultipleOptionsForOneProduct_GroupsOptions() {
            // given
            LegacyProductOptionQueryDto row1 =
                    new LegacyProductOptionQueryDto(1L, 10L, "N", 3, 100L, 200L, "색상", "빨강");
            LegacyProductOptionQueryDto row2 =
                    new LegacyProductOptionQueryDto(1L, 10L, "N", 3, 101L, 201L, "사이즈", "M");
            List<LegacyProductOptionQueryDto> rows = List.of(row1, row2);

            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(rows);

            // then
            assertThat(result).hasSize(1);
            LegacyProductCompositeResult composite = result.get(0);
            assertThat(composite.optionMappings()).hasSize(2);
            assertThat(composite.optionMappings().get(0).optionGroupId()).isEqualTo(100L);
            assertThat(composite.optionMappings().get(0).optionDetailId()).isEqualTo(200L);
            assertThat(composite.optionMappings().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(composite.optionMappings().get(0).optionValue()).isEqualTo("빨강");
            assertThat(composite.optionMappings().get(1).optionGroupId()).isEqualTo(101L);
        }

        @Test
        @DisplayName("여러 상품 행이 있는 경우 각각 별도 Composite 결과로 변환됩니다")
        void toCompositeResults_WithMultipleProducts_ReturnsMultipleResults() {
            // given
            LegacyProductOptionQueryDto product1Row =
                    new LegacyProductOptionQueryDto(1L, 10L, "N", 3, 100L, 200L, "색상", "빨강");
            LegacyProductOptionQueryDto product2Row =
                    new LegacyProductOptionQueryDto(2L, 10L, "N", 5, 100L, 201L, "색상", "파랑");
            List<LegacyProductOptionQueryDto> rows = List.of(product1Row, product2Row);

            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(rows);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).productId()).isEqualTo(1L);
            assertThat(result.get(1).productId()).isEqualTo(2L);
        }

        @Test
        @DisplayName("옵션 그룹/상세 ID 중 하나라도 null인 경우 옵션으로 포함되지 않습니다")
        void toCompositeResults_WithPartialNullOptionIds_ExcludesFromOptions() {
            // given - optionGroupId가 있지만 optionDetailId가 null
            LegacyProductOptionQueryDto row =
                    new LegacyProductOptionQueryDto(1L, 10L, "N", 3, 100L, null, "색상", "빨강");

            // when
            List<LegacyProductCompositeResult> result = mapper.toCompositeResults(List.of(row));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).optionMappings()).isEmpty();
        }
    }
}
