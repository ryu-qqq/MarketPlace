package com.ryuqq.marketplace.domain.legacyconversion.vo;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.product.id.ProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ResolvedLegacyProductIds Value Object 테스트")
class ResolvedLegacyProductIdsTest {

    @Nested
    @DisplayName("생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("resolvedProductGroupId와 productIdMap으로 생성한다")
        void createWithValidFields() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Map<Long, ProductId> productIdMap = Map.of(100L, ProductId.of(200L));

            // when
            ResolvedLegacyProductIds resolved =
                    new ResolvedLegacyProductIds(productGroupId, productIdMap);

            // then
            assertThat(resolved.resolvedProductGroupId()).isEqualTo(productGroupId);
            assertThat(resolved.productIdMap()).containsEntry(100L, ProductId.of(200L));
        }

        @Test
        @DisplayName("생성 시 productIdMap을 불변 복사본으로 저장한다")
        void productIdMapIsImmutableCopy() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Map<Long, ProductId> mutableMap = new HashMap<>();
            mutableMap.put(100L, ProductId.of(200L));

            // when
            ResolvedLegacyProductIds resolved =
                    new ResolvedLegacyProductIds(productGroupId, mutableMap);

            // 원본 수정
            mutableMap.put(999L, ProductId.of(888L));

            // then: VO 내부 맵은 영향을 받지 않는다
            assertThat(resolved.productIdMap()).doesNotContainKey(999L);
        }
    }

    @Nested
    @DisplayName("resolveProductId() - 레거시 ID를 내부 ID로 변환")
    class ResolveProductIdTest {

        @Test
        @DisplayName("매핑이 존재하면 내부 ProductId를 반환한다")
        void resolveProductId_WhenMappingExists() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Map<Long, ProductId> productIdMap = Map.of(100L, ProductId.of(200L));
            ResolvedLegacyProductIds resolved =
                    new ResolvedLegacyProductIds(productGroupId, productIdMap);

            // when
            ProductId result = resolved.resolveProductId(100L);

            // then
            assertThat(result).isEqualTo(ProductId.of(200L));
        }

        @Test
        @DisplayName("매핑이 없으면 레거시 ID를 그대로 ProductId로 반환한다")
        void resolveProductId_WhenNoMappingExists_ReturnsFallback() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Map<Long, ProductId> productIdMap = Map.of(100L, ProductId.of(200L));
            ResolvedLegacyProductIds resolved =
                    new ResolvedLegacyProductIds(productGroupId, productIdMap);

            // when: 매핑되지 않은 레거시 ID 999L 사용
            ProductId result = resolved.resolveProductId(999L);

            // then: 999L 그대로 반환
            assertThat(result).isEqualTo(ProductId.of(999L));
        }
    }

    @Nested
    @DisplayName("reverseProductIdMap() - 역매핑 생성")
    class ReverseProductIdMapTest {

        @Test
        @DisplayName("market ProductId를 키로, 레거시 productId를 값으로 역매핑을 생성한다")
        void reverseProductIdMap() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Map<Long, ProductId> productIdMap =
                    Map.of(
                            100L, ProductId.of(200L),
                            101L, ProductId.of(201L));
            ResolvedLegacyProductIds resolved =
                    new ResolvedLegacyProductIds(productGroupId, productIdMap);

            // when
            Map<Long, Long> reversed = resolved.reverseProductIdMap();

            // then
            assertThat(reversed).containsEntry(200L, 100L);
            assertThat(reversed).containsEntry(201L, 101L);
        }

        @Test
        @DisplayName("빈 매핑에서 역매핑은 빈 맵을 반환한다")
        void reverseProductIdMap_EmptyMap() {
            // given
            ResolvedLegacyProductIds resolved =
                    new ResolvedLegacyProductIds(ProductGroupId.of(1L), Map.of());

            // when
            Map<Long, Long> reversed = resolved.reverseProductIdMap();

            // then
            assertThat(reversed).isEmpty();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("같은 값을 가진 VO는 동등하다")
        void sameValuesAreEqual() {
            // given
            ProductGroupId productGroupId = ProductGroupId.of(1L);
            Map<Long, ProductId> productIdMap = Map.of(100L, ProductId.of(200L));

            ResolvedLegacyProductIds vo1 =
                    new ResolvedLegacyProductIds(productGroupId, productIdMap);
            ResolvedLegacyProductIds vo2 =
                    new ResolvedLegacyProductIds(productGroupId, productIdMap);

            // then
            assertThat(vo1).isEqualTo(vo2);
            assertThat(vo1.hashCode()).isEqualTo(vo2.hashCode());
        }

        @Test
        @DisplayName("다른 productGroupId를 가진 VO는 동등하지 않다")
        void differentProductGroupIdNotEqual() {
            // given
            Map<Long, ProductId> productIdMap = Map.of(100L, ProductId.of(200L));

            ResolvedLegacyProductIds vo1 =
                    new ResolvedLegacyProductIds(ProductGroupId.of(1L), productIdMap);
            ResolvedLegacyProductIds vo2 =
                    new ResolvedLegacyProductIds(ProductGroupId.of(2L), productIdMap);

            // then
            assertThat(vo1).isNotEqualTo(vo2);
        }
    }
}
