package com.ryuqq.marketplace.adapter.out.persistence.commoncode.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CommonCodeJpaEntityMapperTest - 공통 코드 Mapper 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CommonCodeJpaEntityMapper 단위 테스트")
class CommonCodeJpaEntityMapperTest {

    private CommonCodeJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CommonCodeJpaEntityMapper();
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
            CommonCode domain = CommonCodeFixtures.activeCommonCode();

            // when
            CommonCodeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getCommonCodeTypeId()).isEqualTo(domain.commonCodeTypeIdValue());
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
            assertThat(entity.getDisplayName()).isEqualTo(domain.displayNameValue());
            assertThat(entity.getDisplayOrder()).isEqualTo(domain.displayOrderValue());
            assertThat(entity.isActive()).isEqualTo(domain.isActive());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveDomain_ReturnsValidEntity() {
            // given
            CommonCode domain = CommonCodeFixtures.inactiveCommonCode();

            // when
            CommonCodeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedDomain_ReturnsEntityWithDeletedAt() {
            // given
            CommonCode domain = CommonCodeFixtures.deletedCommonCode();

            // when
            CommonCodeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("새로 생성된 Domain을 Entity로 변환합니다")
        void toEntity_WithNewDomain_ReturnsValidEntity() {
            // given
            CommonCode domain = CommonCodeFixtures.newCommonCode();

            // when
            CommonCodeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
            assertThat(entity.getDisplayName()).isEqualTo(domain.displayNameValue());
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
            CommonCodeJpaEntity entity = CommonCodeJpaEntityFixtures.activeEntity();

            // when
            CommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.commonCodeTypeIdValue()).isEqualTo(entity.getCommonCodeTypeId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.displayNameValue()).isEqualTo(entity.getDisplayName());
            assertThat(domain.displayOrderValue()).isEqualTo(entity.getDisplayOrder());
            assertThat(domain.isActive()).isEqualTo(entity.isActive());
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ReturnsValidDomain() {
            // given
            CommonCodeJpaEntity entity = CommonCodeJpaEntityFixtures.inactiveEntity();

            // when
            CommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ReturnsDomainWithDeletedAt() {
            // given
            CommonCodeJpaEntity entity = CommonCodeJpaEntityFixtures.deletedEntity();

            // when
            CommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            CommonCodeJpaEntity entity = CommonCodeJpaEntityFixtures.newEntity();

            // when
            CommonCode domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.displayNameValue()).isEqualTo(entity.getDisplayName());
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
            CommonCode originalDomain = CommonCodeFixtures.activeCommonCode();

            // when
            CommonCodeJpaEntity entity = mapper.toEntity(originalDomain);
            CommonCode convertedDomain = mapper.toDomain(entity);

            // then
            assertThat(convertedDomain.idValue()).isEqualTo(originalDomain.idValue());
            assertThat(convertedDomain.commonCodeTypeIdValue())
                    .isEqualTo(originalDomain.commonCodeTypeIdValue());
            assertThat(convertedDomain.codeValue()).isEqualTo(originalDomain.codeValue());
            assertThat(convertedDomain.displayNameValue())
                    .isEqualTo(originalDomain.displayNameValue());
            assertThat(convertedDomain.displayOrderValue())
                    .isEqualTo(originalDomain.displayOrderValue());
            assertThat(convertedDomain.isActive()).isEqualTo(originalDomain.isActive());
        }
    }
}
