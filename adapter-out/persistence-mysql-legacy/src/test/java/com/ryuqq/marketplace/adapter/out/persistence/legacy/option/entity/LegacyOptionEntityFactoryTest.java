package com.ryuqq.marketplace.adapter.out.persistence.legacy.option.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * LegacyOptionEntityFactoryTest - 레거시 옵션 엔티티 팩토리 메서드 단위 테스트.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("레거시 옵션 엔티티 팩토리 단위 테스트")
class LegacyOptionEntityFactoryTest {

    @Nested
    @DisplayName("LegacyOptionGroupEntity 테스트")
    class LegacyOptionGroupEntityTest {

        @Test
        @DisplayName("create 메서드로 옵션 그룹 엔티티를 생성합니다")
        void create_WithOptionName_CreatesEntity() {
            // when
            LegacyOptionGroupEntity entity = LegacyOptionGroupEntity.create(1L, "색상");

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getProductGroupId()).isEqualTo(1L);
            assertThat(entity.getOptionName()).isEqualTo("색상");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("다른 옵션 그룹명으로 엔티티를 생성합니다")
        void create_WithDifferentOptionName_CreatesEntity() {
            // when
            LegacyOptionGroupEntity entity = LegacyOptionGroupEntity.create(2L, "사이즈");

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(2L);
            assertThat(entity.getOptionName()).isEqualTo("사이즈");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }
    }

    @Nested
    @DisplayName("LegacyOptionDetailEntity 테스트")
    class LegacyOptionDetailEntityTest {

        @Test
        @DisplayName("create 메서드로 옵션 상세 엔티티를 생성합니다")
        void create_WithValidFields_CreatesEntity() {
            // when
            LegacyOptionDetailEntity entity = LegacyOptionDetailEntity.create(100L, "빨강");

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getOptionGroupId()).isEqualTo(100L);
            assertThat(entity.getOptionValue()).isEqualTo("빨강");
            assertThat(entity.getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("다른 옵션값으로 엔티티를 생성합니다")
        void create_WithDifferentOptionValue_CreatesEntity() {
            // when
            LegacyOptionDetailEntity entity = LegacyOptionDetailEntity.create(100L, "파랑");

            // then
            assertThat(entity.getOptionGroupId()).isEqualTo(100L);
            assertThat(entity.getOptionValue()).isEqualTo("파랑");
        }
    }
}
