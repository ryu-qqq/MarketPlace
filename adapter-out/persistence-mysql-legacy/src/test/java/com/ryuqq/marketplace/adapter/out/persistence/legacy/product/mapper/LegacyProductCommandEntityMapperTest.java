package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.entity.LegacyProductGroupEntity;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyProductGroup;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ManagementType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.Origin;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.ProductCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyProductCommandEntityMapperTest - 레거시 상품 Command Mapper 단위 테스트.
 *
 * <p>Domain → Entity 변환을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyProductCommandEntityMapper 단위 테스트")
class LegacyProductCommandEntityMapperTest {

    private LegacyProductCommandEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyProductCommandEntityMapper();
    }

    private LegacyProductGroup buildProductGroup(
            Long id,
            String name,
            long sellerId,
            long brandId,
            long categoryId,
            OptionType optionType,
            ManagementType managementType) {
        return LegacyProductGroup.reconstitute(
                id,
                name,
                sellerId,
                brandId,
                categoryId,
                optionType,
                managementType,
                50000L,
                45000L,
                "N",
                "Y",
                ProductCondition.NEW,
                Origin.KR,
                "STYLE001",
                null,
                null,
                null,
                null,
                null);
    }

    // ========================================================================
    // 1. toEntity(LegacyProductGroup) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyProductGroup) 변환 테스트")
    class ToEntityFromProductGroupTest {

        @Test
        @DisplayName("LegacyProductGroup을 LegacyProductGroupEntity로 변환합니다")
        void toEntity_WithValidProductGroup_ReturnsValidEntity() {
            // given
            LegacyProductGroup domain =
                    buildProductGroup(
                            100L,
                            "테스트 상품",
                            10L,
                            20L,
                            30L,
                            OptionType.SINGLE,
                            ManagementType.MENUAL);

            // when
            LegacyProductGroupEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getProductGroupName()).isEqualTo("테스트 상품");
            assertThat(entity.getSellerId()).isEqualTo(10L);
            assertThat(entity.getBrandId()).isEqualTo(20L);
            assertThat(entity.getCategoryId()).isEqualTo(30L);
            assertThat(entity.getOptionType()).isEqualTo(OptionType.SINGLE.name());
            assertThat(entity.getManagementType()).isEqualTo(ManagementType.MENUAL.name());
        }

        @Test
        @DisplayName("가격 정보가 올바르게 변환됩니다")
        void toEntity_WithPriceInfo_PreservesPrice() {
            // given
            LegacyProductGroup domain =
                    LegacyProductGroup.forNew(
                            "상품",
                            1L,
                            2L,
                            3L,
                            OptionType.SINGLE,
                            ManagementType.MENUAL,
                            100000L,
                            90000L,
                            "N",
                            "Y",
                            ProductCondition.NEW,
                            Origin.KR,
                            null,
                            null,
                            null,
                            null);

            // when
            LegacyProductGroupEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getRegularPrice()).isEqualTo(100000L);
            assertThat(entity.getCurrentPrice()).isEqualTo(90000L);
        }

        @Test
        @DisplayName("신규 상품 그룹도 변환됩니다")
        void toEntity_WithNewProductGroup_ReturnsEntityWithNullId() {
            // given
            LegacyProductGroup domain =
                    LegacyProductGroup.forNew(
                            "신규 상품",
                            10L,
                            20L,
                            30L,
                            OptionType.SINGLE,
                            ManagementType.MENUAL,
                            50000L,
                            45000L,
                            "N",
                            "Y",
                            ProductCondition.NEW,
                            Origin.KR,
                            "STYLE001",
                            null,
                            null,
                            null);

            // when
            LegacyProductGroupEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupName()).isEqualTo("신규 상품");
        }

        @Test
        @DisplayName("품절/전시 여부가 올바르게 변환됩니다")
        void toEntity_WithSoldOutAndDisplayFlags_PreservesFlags() {
            // given
            LegacyProductGroup domain =
                    buildProductGroup(
                            1L, "상품", 10L, 20L, 30L, OptionType.SINGLE, ManagementType.MENUAL);

            // when
            LegacyProductGroupEntity entity = mapper.toEntity(domain);

            // then - buildProductGroup 기본값은 soldOutYn="N", displayYn="Y"
            assertThat(entity.getSoldOutYn()).isEqualTo("N");
            assertThat(entity.getDisplayYn()).isEqualTo("Y");
        }
    }
}
