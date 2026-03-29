package com.ryuqq.marketplace.application.legacy.productcontext.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.resolver.LegacyProductIdResolver;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductIdResolveFactory 단위 테스트")
class LegacyProductIdResolveFactoryTest {

    @InjectMocks private LegacyProductIdResolveFactory sut;

    @Mock private LegacyProductIdResolver productIdResolver;
    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("resolve() - 레거시 PK resolve")
    class ResolveTest {

        @Test
        @DisplayName("레거시 productGroupId로 ResolvedLegacyProductIds를 생성한다")
        void resolve_ValidLegacyGroupId_ReturnsResolvedIds() {
            // given
            long legacyGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            Map<Long, Long> rawMap =
                    Map.of(
                            LegacyProductContextFixtures.LEGACY_PRODUCT_ID_1,
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_1,
                            LegacyProductContextFixtures.LEGACY_PRODUCT_ID_2,
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_2);

            given(productIdResolver.resolveProductGroupId(legacyGroupId))
                    .willReturn(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            given(productIdResolver.resolveProductIdsByLegacyGroupId(legacyGroupId))
                    .willReturn(rawMap);

            // when
            ResolvedLegacyProductIds result = sut.resolve(legacyGroupId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.resolvedProductGroupId().value())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            assertThat(result.productIdMap()).hasSize(2);
            then(productIdResolver).should().resolveProductGroupId(legacyGroupId);
            then(productIdResolver).should().resolveProductIdsByLegacyGroupId(legacyGroupId);
        }

        @Test
        @DisplayName("SKU 매핑이 없는 경우 빈 productIdMap을 포함한 결과를 반환한다")
        void resolve_NoProductMappings_ReturnsEmptyMap() {
            // given
            long legacyGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;

            given(productIdResolver.resolveProductGroupId(legacyGroupId))
                    .willReturn(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            given(productIdResolver.resolveProductIdsByLegacyGroupId(legacyGroupId))
                    .willReturn(Map.of());

            // when
            ResolvedLegacyProductIds result = sut.resolve(legacyGroupId);

            // then
            assertThat(result.resolvedProductGroupId().value())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            assertThat(result.productIdMap()).isEmpty();
        }
    }

    @Nested
    @DisplayName("resolveUpdateProductsCommand() - UpdateProductsCommand PK 변환")
    class ResolveUpdateProductsCommandTest {

        @Test
        @DisplayName("Command의 productGroupId/productId를 market PK로 변환한다")
        void resolveUpdateProductsCommand_ValidCommand_ReturnsResolvedCommand() {
            // given
            UpdateProductsCommand command = LegacyProductContextFixtures.updateProductsCommand();
            Map<Long, Long> rawMap =
                    Map.of(
                            LegacyProductContextFixtures.LEGACY_PRODUCT_ID_1,
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_1,
                            LegacyProductContextFixtures.LEGACY_PRODUCT_ID_2,
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_2);

            given(productIdResolver.resolveProductGroupId(command.productGroupId()))
                    .willReturn(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            given(productIdResolver.resolveProductIdsByLegacyGroupId(command.productGroupId()))
                    .willReturn(rawMap);

            // when
            UpdateProductsCommand resolved = sut.resolveUpdateProductsCommand(command);

            // then
            assertThat(resolved.productGroupId())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            assertThat(resolved.products()).hasSize(2);
            assertThat(resolved.products().get(0).productId())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_1);
            assertThat(resolved.products().get(1).productId())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_2);
        }

        @Test
        @DisplayName("productId가 null인 경우 null을 그대로 유지한다")
        void resolveUpdateProductsCommand_NullProductId_KeepsNull() {
            // given
            UpdateProductsCommand command =
                    LegacyProductContextFixtures.updateProductsCommandWithNullProductId();

            given(productIdResolver.resolveProductGroupId(command.productGroupId()))
                    .willReturn(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            given(productIdResolver.resolveProductIdsByLegacyGroupId(command.productGroupId()))
                    .willReturn(Map.of());

            // when
            UpdateProductsCommand resolved = sut.resolveUpdateProductsCommand(command);

            // then
            assertThat(resolved.products().get(0).productId()).isNull();
        }

        @Test
        @DisplayName("productId가 0 이하인 경우 그대로 반환한다")
        void resolveUpdateProductsCommand_ZeroProductId_KeepsZero() {
            // given
            UpdateProductsCommand command =
                    LegacyProductContextFixtures.updateProductsCommandWithZeroProductId();

            given(productIdResolver.resolveProductGroupId(command.productGroupId()))
                    .willReturn(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            given(productIdResolver.resolveProductIdsByLegacyGroupId(command.productGroupId()))
                    .willReturn(Map.of());

            // when
            UpdateProductsCommand resolved = sut.resolveUpdateProductsCommand(command);

            // then
            assertThat(resolved.products().get(0).productId()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("resolveUpdateFullCommand() - UpdateProductGroupFullCommand PK 변환")
    class ResolveUpdateFullCommandTest {

        @Test
        @DisplayName("Command의 productGroupId/productId를 market PK로 변환한다")
        void resolveUpdateFullCommand_ValidCommand_ReturnsResolvedCommand() {
            // given
            UpdateProductGroupFullCommand command =
                    LegacyProductContextFixtures.updateProductGroupFullCommand();
            Map<Long, Long> rawMap =
                    Map.of(
                            LegacyProductContextFixtures.LEGACY_PRODUCT_ID_1,
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_1);

            given(productIdResolver.resolveProductGroupId(command.productGroupId()))
                    .willReturn(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            given(productIdResolver.resolveProductIdsByLegacyGroupId(command.productGroupId()))
                    .willReturn(rawMap);

            // when
            UpdateProductGroupFullCommand resolved = sut.resolveUpdateFullCommand(command);

            // then
            assertThat(resolved.productGroupId())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID);
            assertThat(resolved.productGroupName()).isEqualTo(command.productGroupName());
            assertThat(resolved.products().get(0).productId())
                    .isEqualTo(LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_1);
            assertThat(resolved.products().get(1).productId()).isNull();
        }
    }

    @Nested
    @DisplayName("now() - 현재 시각 반환")
    class NowTest {

        @Test
        @DisplayName("TimeProvider로부터 현재 시각을 반환한다")
        void now_ReturnsCurrentInstant() {
            // given
            Instant expected = Instant.now();
            given(timeProvider.now()).willReturn(expected);

            // when
            Instant result = sut.now();

            // then
            assertThat(result).isEqualTo(expected);
            then(timeProvider).should().now();
        }
    }
}
