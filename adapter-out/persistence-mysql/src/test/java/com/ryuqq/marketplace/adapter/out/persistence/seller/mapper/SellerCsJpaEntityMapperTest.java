package com.ryuqq.marketplace.adapter.out.persistence.seller.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.seller.SellerCsJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.seller.entity.SellerCsJpaEntity;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SellerCsJpaEntityMapperTest - 셀러 CS 정보 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SellerCsJpaEntityMapper 단위 테스트")
class SellerCsJpaEntityMapperTest {

    private SellerCsJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SellerCsJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveCs_ConvertsCorrectly() {
            // given
            SellerCs domain = SellerFixtures.activeSellerCs();

            // when
            SellerCsJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSellerId()).isEqualTo(domain.sellerIdValue());
            assertThat(entity.getCsPhone()).isEqualTo(domain.csPhone());
            assertThat(entity.getCsMobile()).isEqualTo(domain.csMobile());
            assertThat(entity.getCsEmail()).isEqualTo(domain.csEmail());
            assertThat(entity.getOperatingDays()).isEqualTo(domain.operatingDays());
            assertThat(entity.getKakaoChannelUrl()).isEqualTo(domain.kakaoChannelUrl());
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewCs_ConvertsCorrectly() {
            // given
            SellerCs domain = SellerFixtures.newSellerCs();

            // when
            SellerCsJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCsPhone()).isEqualTo(domain.csPhone());
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
            SellerCsJpaEntity entity = SellerCsJpaEntityFixtures.activeEntity();

            // when
            SellerCs domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.sellerIdValue()).isEqualTo(entity.getSellerId());
            assertThat(domain.csPhone()).isEqualTo(entity.getCsPhone());
            assertThat(domain.csMobile()).isEqualTo(entity.getCsMobile());
            assertThat(domain.csEmail()).isEqualTo(entity.getCsEmail());
            assertThat(domain.operatingDays()).isEqualTo(entity.getOperatingDays());
            assertThat(domain.kakaoChannelUrl()).isEqualTo(entity.getKakaoChannelUrl());
            assertThat(domain.operatingHours()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            SellerCsJpaEntity entity = SellerCsJpaEntityFixtures.newEntity();

            // when
            SellerCs domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.isNew()).isTrue();
            assertThat(domain.csPhone()).isEqualTo(entity.getCsPhone());
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
            SellerCs original = SellerFixtures.activeSellerCs();

            // when
            SellerCsJpaEntity entity = mapper.toEntity(original);
            SellerCs converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.sellerIdValue()).isEqualTo(original.sellerIdValue());
            assertThat(converted.csPhone()).isEqualTo(original.csPhone());
            assertThat(converted.csMobile()).isEqualTo(original.csMobile());
            assertThat(converted.csEmail()).isEqualTo(original.csEmail());
            assertThat(converted.operatingDays()).isEqualTo(original.operatingDays());
            assertThat(converted.kakaoChannelUrl()).isEqualTo(original.kakaoChannelUrl());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SellerCsJpaEntity original = SellerCsJpaEntityFixtures.activeEntity();

            // when
            SellerCs domain = mapper.toDomain(original);
            SellerCsJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSellerId()).isEqualTo(original.getSellerId());
            assertThat(converted.getCsPhone()).isEqualTo(original.getCsPhone());
            assertThat(converted.getCsMobile()).isEqualTo(original.getCsMobile());
            assertThat(converted.getCsEmail()).isEqualTo(original.getCsEmail());
            assertThat(converted.getOperatingDays()).isEqualTo(original.getOperatingDays());
            assertThat(converted.getKakaoChannelUrl()).isEqualTo(original.getKakaoChannelUrl());
        }
    }
}
