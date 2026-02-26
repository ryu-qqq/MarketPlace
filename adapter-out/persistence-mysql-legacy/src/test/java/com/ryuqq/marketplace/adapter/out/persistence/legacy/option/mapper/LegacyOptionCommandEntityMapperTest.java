package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionDetailEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity.LegacyOptionGroupEntity;
import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;
import com.ryuqq.marketplace.domain.legacy.optiongroup.aggregate.LegacyOptionGroup;
import com.ryuqq.marketplace.domain.legacy.optiongroup.id.LegacyOptionGroupId;
import com.ryuqq.marketplace.domain.legacy.optiongroup.vo.LegacyOptionName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyOptionCommandEntityMapperTest - 레거시 옵션 커맨드 Mapper 단위 테스트.
 *
 * <p>Domain → Entity 변환을 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("LegacyOptionCommandEntityMapper 단위 테스트")
class LegacyOptionCommandEntityMapperTest {

    private LegacyOptionCommandEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyOptionCommandEntityMapper();
    }

    // ========================================================================
    // 1. toEntity(LegacyOptionGroup) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyOptionGroup) 변환 테스트")
    class ToEntityFromOptionGroupTest {

        @Test
        @DisplayName("LegacyOptionGroup을 LegacyOptionGroupEntity로 변환합니다")
        void toEntity_WithValidOptionGroup_ReturnsValidEntity() {
            // given
            LegacyOptionGroup optionGroup = LegacyOptionGroup.forNew(LegacyOptionName.COLOR);

            // when
            LegacyOptionGroupEntity entity = mapper.toEntity(optionGroup);

            // then
            assertThat(entity.getOptionName()).isEqualTo("COLOR");
        }

        @Test
        @DisplayName("다른 옵션 이름도 올바르게 변환됩니다")
        void toEntity_WithDifferentOptionName_PreservesName() {
            // given
            LegacyOptionGroup optionGroup = LegacyOptionGroup.forNew(LegacyOptionName.SIZE);

            // when
            LegacyOptionGroupEntity entity = mapper.toEntity(optionGroup);

            // then
            assertThat(entity.getOptionName()).isEqualTo("SIZE");
        }
    }

    // ========================================================================
    // 2. toEntity(LegacyOptionDetail) 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity(LegacyOptionDetail) 변환 테스트")
    class ToEntityFromOptionDetailTest {

        @Test
        @DisplayName("LegacyOptionDetail을 LegacyOptionDetailEntity로 변환합니다")
        void toEntity_WithValidOptionDetail_ReturnsValidEntity() {
            // given
            LegacyOptionDetail optionDetail =
                    LegacyOptionDetail.forNew(LegacyOptionGroupId.of(1L), "RED");

            // when
            LegacyOptionDetailEntity entity = mapper.toEntity(optionDetail);

            // then
            assertThat(entity.getOptionGroupId()).isEqualTo(1L);
            assertThat(entity.getOptionValue()).isEqualTo("RED");
        }

        @Test
        @DisplayName("다른 옵션 값도 올바르게 변환됩니다")
        void toEntity_WithDifferentOptionValue_PreservesValue() {
            // given
            LegacyOptionDetail optionDetail =
                    LegacyOptionDetail.forNew(LegacyOptionGroupId.of(2L), "BLUE");

            // when
            LegacyOptionDetailEntity entity = mapper.toEntity(optionDetail);

            // then
            assertThat(entity.getOptionGroupId()).isEqualTo(2L);
            assertThat(entity.getOptionValue()).isEqualTo("BLUE");
        }

        @Test
        @DisplayName("DB에서 복원된 옵션 상세도 변환됩니다")
        void toEntity_WithReconstituteOptionDetail_ConvertsCorrectly() {
            // given
            LegacyOptionDetail optionDetail = LegacyOptionDetail.reconstitute(10L, 5L, "XL");

            // when
            LegacyOptionDetailEntity entity = mapper.toEntity(optionDetail);

            // then
            assertThat(entity.getOptionGroupId()).isEqualTo(5L);
            assertThat(entity.getOptionValue()).isEqualTo("XL");
        }
    }
}
