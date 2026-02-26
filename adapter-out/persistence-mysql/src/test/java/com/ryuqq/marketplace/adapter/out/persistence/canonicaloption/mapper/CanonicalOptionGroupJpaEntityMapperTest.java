package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionValueJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CanonicalOptionGroupJpaEntityMapperTest - 캐노니컬 옵션 그룹 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toDomain(Entity, List) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CanonicalOptionGroupJpaEntityMapper 단위 테스트")
class CanonicalOptionGroupJpaEntityMapperTest {

    private CanonicalOptionGroupJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CanonicalOptionGroupJpaEntityMapper(new CanonicalOptionValueJpaEntityMapper());
    }

    // ========================================================================
    // 1. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
            assertThat(domain.nameEn()).isEqualTo(entity.getNameEn());
            assertThat(domain.isActive()).isEqualTo(entity.isActive());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.inactiveEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("영문명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutNameEn_ConvertsCorrectly() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.entityWithoutNameEn();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.nameEn()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환 시 예외를 발생시킵니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity, List.of()))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
        }

        @Test
        @DisplayName("타임스탬프가 정확히 변환됩니다")
        void toDomain_PreservesTimestamps() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.createdAt()).isEqualTo(entity.getCreatedAt());
            assertThat(domain.updatedAt()).isEqualTo(entity.getUpdatedAt());
        }

        @Test
        @DisplayName("값 목록을 포함한 Domain을 생성합니다")
        void toDomain_WithValueEntities_ConvertsValues() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity();
            List<CanonicalOptionValueJpaEntity> values =
                    List.of(
                            CanonicalOptionValueJpaEntityFixtures.defaultEntity(),
                            CanonicalOptionValueJpaEntityFixtures.defaultEntity());

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, values);

            // then
            assertThat(domain.values()).hasSize(2);
        }
    }

    // ========================================================================
    // 2. 엣지 케이스 테스트
    // ========================================================================

    @Nested
    @DisplayName("엣지 케이스 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("SIZE 옵션 그룹을 Domain으로 변환합니다")
        void toDomain_WithSizeGroup_ConvertsCorrectly() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.sizeEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.codeValue()).isEqualTo("SIZE");
            assertThat(domain.nameKo()).isEqualTo("사이즈");
            assertThat(domain.nameEn()).isEqualTo("Size");
        }

        @Test
        @DisplayName("MATERIAL 옵션 그룹을 Domain으로 변환합니다")
        void toDomain_WithMaterialGroup_ConvertsCorrectly() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.materialEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.codeValue()).isEqualTo("MATERIAL");
            assertThat(domain.nameKo()).isEqualTo("소재");
            assertThat(domain.nameEn()).isEqualTo("Material");
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
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.id()).isNotNull();
            assertThat(domain.code()).isNotNull();
            assertThat(domain.name()).isNotNull();
            assertThat(domain.values()).isEmpty();
        }

        @Test
        @DisplayName("빈 값 목록으로 변환 시 values는 빈 리스트로 초기화됩니다")
        void toDomain_WithEmptyValues_InitializesEmptyValuesList() {
            // given
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity();

            // when
            CanonicalOptionGroup domain = mapper.toDomain(entity, List.of());

            // then
            assertThat(domain.values()).isNotNull();
            assertThat(domain.values()).isEmpty();
        }
    }
}
