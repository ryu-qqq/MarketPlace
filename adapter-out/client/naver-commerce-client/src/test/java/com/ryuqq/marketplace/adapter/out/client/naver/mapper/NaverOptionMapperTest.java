package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo;
import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverProductRegistrationRequest.OptionInfo.OptionCombination;
import com.ryuqq.marketplace.application.product.dto.response.ProductOptionMappingResult;
import com.ryuqq.marketplace.application.product.dto.response.ProductResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionGroupResult;
import com.ryuqq.marketplace.application.productgroup.dto.response.SellerOptionValueResult;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("NaverOptionMapper 단위 테스트")
class NaverOptionMapperTest {

    // ── 헬퍼 메서드 ──

    private SellerOptionValueResult optionValue(Long id, Long groupId, String name) {
        return new SellerOptionValueResult(id, groupId, name, null, 1);
    }

    private SellerOptionGroupResult predefinedGroup(
            Long id, String name, List<SellerOptionValueResult> values) {
        return new SellerOptionGroupResult(id, name, null, "PREDEFINED", 1, values);
    }

    private SellerOptionGroupResult freeInputGroup(
            Long id, String name, List<SellerOptionValueResult> values) {
        return new SellerOptionGroupResult(id, name, null, "FREE_INPUT", 1, values);
    }

    private ProductResult product(
            Long id,
            String skuCode,
            int price,
            int stock,
            List<ProductOptionMappingResult> mappings) {
        return new ProductResult(
                id,
                1L,
                skuCode,
                price + 20000,
                price,
                price - 10000,
                10,
                stock,
                "ACTIVE",
                1,
                mappings,
                Instant.now(),
                Instant.now());
    }

    private ProductOptionMappingResult mapping(Long productId, Long optionValueId) {
        return ProductOptionMappingResult.withOptionNames(
                null, productId, optionValueId, null, null);
    }

    // ── 테스트 ──

    @Nested
    @DisplayName("mapOptionInfo (등록)")
    class MapOptionInfoTest {

        @Test
        @DisplayName("옵션 그룹이 비어있으면 null을 반환한다")
        void emptyOptionGroupsReturnsNull() {
            OptionInfo result = NaverOptionMapper.mapOptionInfo(List.of(), List.of(), false);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("PREDEFINED 옵션 그룹으로 combination을 생성한다")
        void predefinedGroupCreatesCombinations() {
            var colorValues = List.of(optionValue(1L, 10L, "빨강"), optionValue(2L, 10L, "파랑"));
            var colorGroup = predefinedGroup(10L, "색상", colorValues);

            var products =
                    List.of(
                            product(1L, "SKU-R", 50000, 10, List.of(mapping(1L, 1L))),
                            product(2L, "SKU-B", 60000, 5, List.of(mapping(2L, 2L))));

            OptionInfo result =
                    NaverOptionMapper.mapOptionInfo(List.of(colorGroup), products, false);

            assertThat(result).isNotNull();
            assertThat(result.optionCombinationGroupNames()).isNotNull();
            assertThat(result.optionCombinations()).hasSize(2);
            assertThat(result.optionCombinationSortType()).isEqualTo("CREATE");
        }

        @Test
        @DisplayName("재고가 soldOut이면 모든 옵션의 stockQuantity가 0이다")
        void soldOutSetsStockToZero() {
            var values = List.of(optionValue(1L, 10L, "빨강"));
            var group = predefinedGroup(10L, "색상", values);
            var products = List.of(product(1L, "SKU-R", 50000, 10, List.of(mapping(1L, 1L))));

            OptionInfo result = NaverOptionMapper.mapOptionInfo(List.of(group), products, true);

            assertThat(result.optionCombinations().get(0).stockQuantity()).isZero();
        }

        @Test
        @DisplayName("가격 차이가 대표가격 대비 차액으로 계산된다")
        void priceDiffIsCalculatedCorrectly() {
            var values = List.of(optionValue(1L, 10L, "S"), optionValue(2L, 10L, "L"));
            var group = predefinedGroup(10L, "사이즈", values);

            // S: 50000, L: 70000 → 대표가격(min) = 50000
            var products =
                    List.of(
                            product(1L, "SKU-S", 50000, 10, List.of(mapping(1L, 1L))),
                            product(2L, "SKU-L", 70000, 5, List.of(mapping(2L, 2L))));

            OptionInfo result = NaverOptionMapper.mapOptionInfo(List.of(group), products, false);

            List<OptionCombination> combinations = result.optionCombinations();
            // S: 50000 - 50000 = 0
            OptionCombination sComb =
                    combinations.stream()
                            .filter(c -> "S".equals(c.optionName1()))
                            .findFirst()
                            .orElseThrow();
            assertThat(sComb.price()).isZero();
            // L: 70000 - 50000 = 20000
            OptionCombination lComb =
                    combinations.stream()
                            .filter(c -> "L".equals(c.optionName1()))
                            .findFirst()
                            .orElseThrow();
            assertThat(lComb.price()).isEqualTo(20000);
        }

        @Test
        @DisplayName("FREE_INPUT 그룹이 optionCustom으로 매핑된다")
        void freeInputGroupMapsToOptionCustom() {
            var values = List.of(optionValue(1L, 10L, "메모"));
            var group = freeInputGroup(10L, "각인", values);

            OptionInfo result = NaverOptionMapper.mapOptionInfo(List.of(group), List.of(), false);

            assertThat(result).isNotNull();
            assertThat(result.optionCustom()).hasSize(1);
            assertThat(result.optionCustom().get(0).groupName()).isEqualTo("각인");
        }

        @Test
        @DisplayName("FREE_INPUT 옵션값이 2개 이상이면 combination과 custom 모두 생성된다")
        void freeInputWithMultipleValuesCreatesBoth() {
            var values = List.of(optionValue(1L, 10L, "A"), optionValue(2L, 10L, "B"));
            var group = freeInputGroup(10L, "각인", values);

            var products =
                    List.of(
                            product(1L, "SKU-A", 50000, 10, List.of(mapping(1L, 1L))),
                            product(2L, "SKU-B", 50000, 10, List.of(mapping(2L, 2L))));

            OptionInfo result = NaverOptionMapper.mapOptionInfo(List.of(group), products, false);

            // FREE_INPUT + 2개 이상 → combination과 custom 모두
            assertThat(result.optionCombinations()).isNotEmpty();
            assertThat(result.optionCustom()).hasSize(1);
            // custom groupName에 "(자유입력)" 접미사
            assertThat(result.optionCustom().get(0).groupName()).isEqualTo("각인(자유입력)");
        }
    }

    @Nested
    @DisplayName("mapOptionInfoForUpdate (수정)")
    class MapOptionInfoForUpdateTest {

        @Test
        @DisplayName("옵션 그룹이 비어있으면 null을 반환한다")
        void emptyOptionGroupsReturnsNull() {
            OptionInfo result =
                    NaverOptionMapper.mapOptionInfoForUpdate(List.of(), List.of(), null, false);
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("기존 상품 없으면 ID 없이 combination을 생성한다")
        void noExistingProductCreatesWithoutId() {
            var values = List.of(optionValue(1L, 10L, "빨강"));
            var group = predefinedGroup(10L, "색상", values);
            var products = List.of(product(1L, "SKU-R", 50000, 10, List.of(mapping(1L, 1L))));

            OptionInfo result =
                    NaverOptionMapper.mapOptionInfoForUpdate(List.of(group), products, null, false);

            assertThat(result).isNotNull();
            assertThat(result.optionCombinations()).hasSize(1);
            // 기존 상품 없으면 id는 null
            assertThat(result.optionCombinations().get(0).id()).isNull();
        }
    }
}
