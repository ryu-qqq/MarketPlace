package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionValueJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CanonicalOptionValueJpaEntityMapperTest - 캐노니컬 옵션 값 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CanonicalOptionValueJpaEntityMapper 단위 테스트")
class CanonicalOptionValueJpaEntityMapperTest {

    private CanonicalOptionValueJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CanonicalOptionValueJpaEntityMapper();
    }

    // ========================================================================
    // 1. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("기본 Entity를 Domain으로 변환합니다")
        void toDomain_WithDefaultEntity_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.defaultEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
            assertThat(domain.nameEn()).isEqualTo(entity.getNameEn());
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
        }

        @Test
        @DisplayName("영문명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutNameEn_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.entityWithoutNameEn();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nameEn()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 예외를 발생시킵니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }
    }

    // ========================================================================
    // 2. 옵션 값 타입별 테스트
    // ========================================================================

    @Nested
    @DisplayName("옵션 값 타입별 테스트")
    class OptionValueTypeTest {

        @Test
        @DisplayName("COLOR 옵션 값(RED)을 Domain으로 변환합니다")
        void toDomain_WithColorRed_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.colorRedEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeValue()).isEqualTo("RED");
            assertThat(domain.nameKo()).isEqualTo("빨강");
            assertThat(domain.nameEn()).isEqualTo("Red");
            assertThat(domain.sortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("COLOR 옵션 값(BLUE)을 Domain으로 변환합니다")
        void toDomain_WithColorBlue_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.colorBlueEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeValue()).isEqualTo("BLUE");
            assertThat(domain.nameKo()).isEqualTo("파랑");
            assertThat(domain.nameEn()).isEqualTo("Blue");
            assertThat(domain.sortOrder()).isEqualTo(2);
        }

        @Test
        @DisplayName("SIZE 옵션 값(SMALL)을 Domain으로 변환합니다")
        void toDomain_WithSizeSmall_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.sizeSmallEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeValue()).isEqualTo("SMALL");
            assertThat(domain.nameKo()).isEqualTo("소형");
            assertThat(domain.nameEn()).isEqualTo("Small");
        }

        @Test
        @DisplayName("SIZE 옵션 값(MEDIUM)을 Domain으로 변환합니다")
        void toDomain_WithSizeMedium_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.sizeMediumEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeValue()).isEqualTo("MEDIUM");
            assertThat(domain.sortOrder()).isEqualTo(2);
        }

        @Test
        @DisplayName("SIZE 옵션 값(LARGE)을 Domain으로 변환합니다")
        void toDomain_WithSizeLarge_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.sizeLargeEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.codeValue()).isEqualTo("LARGE");
            assertThat(domain.sortOrder()).isEqualTo(3);
        }
    }

    // ========================================================================
    // 3. 데이터 무결성 테스트
    // ========================================================================

    @Nested
    @DisplayName("데이터 무결성 테스트")
    class DataIntegrityTest {

        @Test
        @DisplayName("Entity -> Domain 변환 후 원본 데이터가 보존됩니다")
        void toDomain_PreservesOriginalData() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.defaultEntity();

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.id()).isNotNull();
            assertThat(domain.code()).isNotNull();
            assertThat(domain.name()).isNotNull();
            assertThat(domain.sortOrder()).isGreaterThanOrEqualTo(0);
        }

        @Test
        @DisplayName("sortOrder가 0인 경우도 올바르게 변환됩니다")
        void toDomain_WithZeroSortOrder_ConvertsCorrectly() {
            // given
            CanonicalOptionValueJpaEntity entity =
                    CanonicalOptionValueJpaEntityFixtures.entityWithCode(1L, "TEST", 0);

            // when
            CanonicalOptionValue domain = mapper.toDomain(entity);

            // then
            assertThat(domain.sortOrder()).isZero();
        }
    }
}
