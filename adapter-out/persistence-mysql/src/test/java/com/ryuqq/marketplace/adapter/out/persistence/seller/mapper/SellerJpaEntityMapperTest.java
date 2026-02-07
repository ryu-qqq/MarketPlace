package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerJpaEntityMapperTest - 셀러 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerJpaEntityMapper 단위 테스트")
class SellerJpaEntityMapperTest {

    private SellerJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveSeller_ConvertsCorrectly() {
            // given
            Seller domain = SellerFixtures.activeSeller();

            // when
            SellerJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerName()).isEqualTo(domain.sellerNameValue());
            assertThat(entity.getDisplayName()).isEqualTo(domain.displayNameValue());
            assertThat(entity.getLogoUrl()).isEqualTo(domain.logoUrlValue());
            assertThat(entity.getDescription()).isEqualTo(domain.descriptionValue());
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveSeller_ConvertsCorrectly() {
            // given
            Seller domain = SellerFixtures.inactiveSeller();

            // when
            SellerJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedSeller_ConvertsCorrectly() {
            // given
            Seller domain = SellerFixtures.deletedSeller();

            // when
            SellerJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewSeller_ConvertsCorrectly() {
            // given
            Seller domain = SellerFixtures.newSeller();

            // when
            SellerJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getSellerName()).isEqualTo(domain.sellerNameValue());
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
            SellerJpaEntity entity = SellerJpaEntityFixtures.activeEntity();

            // when
            Seller domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerNameValue()).isEqualTo(entity.getSellerName());
            assertThat(domain.displayNameValue()).isEqualTo(entity.getDisplayName());
            assertThat(domain.logoUrlValue()).isEqualTo(entity.getLogoUrl());
            assertThat(domain.descriptionValue()).isEqualTo(entity.getDescription());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            SellerJpaEntity entity = SellerJpaEntityFixtures.inactiveEntity();

            // when
            Seller domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            SellerJpaEntity entity = SellerJpaEntityFixtures.deletedEntity();

            // when
            Seller domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("로고 URL이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutLogoUrl_ConvertsCorrectly() {
            // given
            SellerJpaEntity entity = SellerJpaEntityFixtures.entityWithoutLogoUrl();

            // when
            Seller domain = mapper.toDomain(entity);

            // then
            assertThat(domain.logoUrlValue()).isNull();
        }

        @Test
        @DisplayName("설명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutDescription_ConvertsCorrectly() {
            // given
            SellerJpaEntity entity = SellerJpaEntityFixtures.entityWithoutDescription();

            // when
            Seller domain = mapper.toDomain(entity);

            // then
            assertThat(domain.descriptionValue()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerJpaEntity entity = SellerJpaEntityFixtures.newEntity();

            // when
            Seller domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.sellerNameValue()).isEqualTo(entity.getSellerName());
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
            Seller original = SellerFixtures.activeSeller();

            // when
            SellerJpaEntity entity = mapper.toEntity(original);
            Seller converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerNameValue()).isEqualTo(original.sellerNameValue());
            assertThat(converted.displayNameValue()).isEqualTo(original.displayNameValue());
            assertThat(converted.logoUrlValue()).isEqualTo(original.logoUrlValue());
            assertThat(converted.descriptionValue()).isEqualTo(original.descriptionValue());
            assertThat(converted.isActive()).isEqualTo(original.isActive());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerJpaEntity original = SellerJpaEntityFixtures.activeEntity();

            // when
            Seller domain = mapper.toDomain(original);
            SellerJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerName()).isEqualTo(original.getSellerName());
            assertThat(converted.getDisplayName()).isEqualTo(original.getDisplayName());
            assertThat(converted.getLogoUrl()).isEqualTo(original.getLogoUrl());
            assertThat(converted.getDescription()).isEqualTo(original.getDescription());
            assertThat(converted.isActive()).isEqualTo(original.isActive());
        }
    }
}
