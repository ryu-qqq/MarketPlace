package com.ryuqq.marketplace.adapter.out.persistence.brand.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.domain.brand.aggregate.Brand;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.brand.vo.BrandCode;
import com.ryuqq.marketplace.domain.brand.vo.BrandName;
import com.ryuqq.marketplace.domain.brand.vo.BrandStatus;
import com.ryuqq.marketplace.domain.brand.vo.LogoUrl;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * BrandJpaEntityMapperTest - 브랜드 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("BrandJpaEntityMapper 단위 테스트")
class BrandJpaEntityMapperTest {

    private BrandJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new BrandJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveBrand_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Brand domain =
                    Brand.reconstitute(
                            BrandId.of(1L),
                            BrandCode.of("BRAND001"),
                            BrandName.of("테스트 브랜드", "Test Brand", "테스트"),
                            BrandStatus.ACTIVE,
                            LogoUrl.of("https://example.com/logo.png"),
                            null,
                            now,
                            now);

            // when
            BrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
            assertThat(entity.getNameKo()).isEqualTo(domain.nameKo());
            assertThat(entity.getNameEn()).isEqualTo(domain.nameEn());
            assertThat(entity.getShortName()).isEqualTo(domain.shortName());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
            assertThat(entity.getLogoUrl()).isEqualTo(domain.logoUrlValue());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveBrand_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Brand domain =
                    Brand.reconstitute(
                            BrandId.of(2L),
                            BrandCode.of("BRAND002"),
                            BrandName.of("비활성 브랜드", "Inactive Brand", "비활성"),
                            BrandStatus.INACTIVE,
                            LogoUrl.of("https://example.com/logo2.png"),
                            null,
                            now,
                            now);

            // when
            BrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("삭제된 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedBrand_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(100);
            Brand domain =
                    Brand.reconstitute(
                            BrandId.of(3L),
                            BrandCode.of("BRAND003"),
                            BrandName.of("삭제된 브랜드", "Deleted Brand", "삭제"),
                            BrandStatus.ACTIVE,
                            LogoUrl.of("https://example.com/logo3.png"),
                            deletedAt,
                            now,
                            now);

            // when
            BrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewBrand_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Brand domain =
                    Brand.forNew(
                            BrandCode.of("BRAND004"),
                            BrandName.of("새 브랜드", "New Brand", "새"),
                            LogoUrl.of("https://example.com/logo4.png"),
                            now);

            // when
            BrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
        }

        @Test
        @DisplayName("로고 URL이 없는 Domain을 Entity로 변환합니다")
        void toEntity_WithoutLogoUrl_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Brand domain =
                    Brand.reconstitute(
                            BrandId.of(5L),
                            BrandCode.of("BRAND005"),
                            BrandName.of("로고없는 브랜드", "No Logo Brand", "로고없음"),
                            BrandStatus.ACTIVE,
                            null,
                            null,
                            now,
                            now);

            // when
            BrandJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getLogoUrl()).isNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithActiveEntity_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.activeEntity();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
            assertThat(domain.nameEn()).isEqualTo(entity.getNameEn());
            assertThat(domain.shortName()).isEqualTo(entity.getShortName());
            assertThat(domain.status()).isEqualTo(BrandStatus.ACTIVE);
            assertThat(domain.logoUrlValue()).isEqualTo(entity.getLogoUrl());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.inactiveEntity();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(BrandStatus.INACTIVE);
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.deletedEntity();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("로고 URL이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutLogoUrl_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.entityWithoutLogoUrl();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.logoUrlValue()).isNull();
        }

        @Test
        @DisplayName("영문명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutNameEn_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.entityWithoutNameEn();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nameEn()).isNull();
        }

        @Test
        @DisplayName("약칭이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutShortName_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.entityWithoutShortName();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.shortName()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            BrandJpaEntity entity = BrandJpaEntityFixtures.newEntity();

            // when
            Brand domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Domain -> Entity -> Domain 변환 시 데이터가 보존됩니다")
        void roundTrip_DomainToEntityToDomain_PreservesData() {
            // given
            Instant now = Instant.now();
            Brand original =
                    Brand.reconstitute(
                            BrandId.of(1L),
                            BrandCode.of("BRAND001"),
                            BrandName.of("테스트 브랜드", "Test Brand", "테스트"),
                            BrandStatus.ACTIVE,
                            LogoUrl.of("https://example.com/logo.png"),
                            null,
                            now,
                            now);

            // when
            BrandJpaEntity entity = mapper.toEntity(original);
            Brand converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.codeValue()).isEqualTo(original.codeValue());
            assertThat(converted.nameKo()).isEqualTo(original.nameKo());
            assertThat(converted.nameEn()).isEqualTo(original.nameEn());
            assertThat(converted.shortName()).isEqualTo(original.shortName());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.logoUrlValue()).isEqualTo(original.logoUrlValue());
            assertThat(converted.isActive()).isEqualTo(original.isActive());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            BrandJpaEntity original = BrandJpaEntityFixtures.activeEntity();

            // when
            Brand domain = mapper.toDomain(original);
            BrandJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getCode()).isEqualTo(original.getCode());
            assertThat(converted.getNameKo()).isEqualTo(original.getNameKo());
            assertThat(converted.getNameEn()).isEqualTo(original.getNameEn());
            assertThat(converted.getShortName()).isEqualTo(original.getShortName());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getLogoUrl()).isEqualTo(original.getLogoUrl());
        }
    }
}
