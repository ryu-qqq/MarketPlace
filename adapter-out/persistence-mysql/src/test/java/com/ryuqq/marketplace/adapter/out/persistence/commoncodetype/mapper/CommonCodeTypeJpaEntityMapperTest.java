package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CommonCodeTypeJpaEntityMapperTest - 공통 코드 타입 Mapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CommonCodeTypeJpaEntityMapper 단위 테스트")
class CommonCodeTypeJpaEntityMapperTest {

    private CommonCodeTypeJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CommonCodeTypeJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 변환 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveDomain_ReturnsValidEntity() {
            // given
            CommonCodeType domain = CommonCodeTypeFixtures.activeCommonCodeType();

            // when
            CommonCodeTypeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getCode()).isEqualTo(domain.code());
            assertThat(entity.getName()).isEqualTo(domain.name());
            assertThat(entity.getDescription()).isEqualTo(domain.description());
            assertThat(entity.getDisplayOrder()).isEqualTo(domain.displayOrder());
            assertThat(entity.isActive()).isEqualTo(domain.isActive());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveDomain_ReturnsValidEntity() {
            // given
            CommonCodeType domain = CommonCodeTypeFixtures.inactiveCommonCodeType();

            // when
            CommonCodeTypeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedDomain_ReturnsEntityWithDeletedAt() {
            // given
            CommonCodeType domain = CommonCodeTypeFixtures.deletedCommonCodeType();

            // when
            CommonCodeTypeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("새로 생성된 Domain을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ReturnsValidEntity() {
            // given
            CommonCodeType domain = CommonCodeTypeFixtures.newCommonCodeType();

            // when
            CommonCodeTypeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(domain.code());
            assertThat(entity.getName()).isEqualTo(domain.name());
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 변환 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ReturnsValidDomain() {
            // given
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.activeEntity();

            // when
            CommonCodeType domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.code()).isEqualTo(entity.getCode());
            assertThat(domain.name()).isEqualTo(entity.getName());
            assertThat(domain.description()).isEqualTo(entity.getDescription());
            assertThat(domain.displayOrder()).isEqualTo(entity.getDisplayOrder());
            assertThat(domain.isActive()).isEqualTo(entity.isActive());
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ReturnsValidDomain() {
            // given
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.inactiveEntity();

            // when
            CommonCodeType domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ReturnsDomainWithDeletedAt() {
            // given
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.deletedEntity();

            // when
            CommonCodeType domain = mapper.toDomain(entity);

            // then
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("설명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutDescription_ReturnsValidDomain() {
            // given
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntityFixtures.entityWithoutDescription();

            // when
            CommonCodeType domain = mapper.toDomain(entity);

            // then
            assertThat(domain.description()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.newEntity();

            // when
            CommonCodeType domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.code()).isEqualTo(entity.getCode());
            assertThat(domain.name()).isEqualTo(entity.getName());
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain → Entity → Domain 변환 시 데이터가 보존됩니다")
        void bidirectionalConversion_PreservesData() {
            // given
            CommonCodeType originalDomain = CommonCodeTypeFixtures.activeCommonCodeType();

            // when
            CommonCodeTypeJpaEntity entity = mapper.toEntity(originalDomain);
            CommonCodeType convertedDomain = mapper.toDomain(entity);

            // then
            assertThat(convertedDomain.idValue()).isEqualTo(originalDomain.idValue());
            assertThat(convertedDomain.code()).isEqualTo(originalDomain.code());
            assertThat(convertedDomain.name()).isEqualTo(originalDomain.name());
            assertThat(convertedDomain.description()).isEqualTo(originalDomain.description());
            assertThat(convertedDomain.displayOrder()).isEqualTo(originalDomain.displayOrder());
            assertThat(convertedDomain.isActive()).isEqualTo(originalDomain.isActive());
        }
    }
}
