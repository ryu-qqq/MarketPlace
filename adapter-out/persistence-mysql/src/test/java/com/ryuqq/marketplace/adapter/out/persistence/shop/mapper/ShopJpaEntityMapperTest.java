package com.ryuqq.marketplace.adapter.out.persistence.shop.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.shop.ShopJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ShopJpaEntityMapperTest - Shop Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ShopJpaEntityMapper 단위 테스트")
class ShopJpaEntityMapperTest {

    private ShopJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ShopJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveShop_ConvertsCorrectly() {
            // given
            Shop domain = ShopFixtures.activeShop();

            // when
            ShopJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getShopName()).isEqualTo(domain.shopName());
            assertThat(entity.getAccountId()).isEqualTo(domain.accountId());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveShop_ConvertsCorrectly() {
            // given
            Shop domain = ShopFixtures.inactiveShop();

            // when
            ShopJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("삭제된 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedShop_ConvertsCorrectly() {
            // given
            Shop domain = ShopFixtures.deletedShop();

            // when
            ShopJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewShop_ConvertsCorrectly() {
            // given
            Shop domain = ShopFixtures.newShop();

            // when
            ShopJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getShopName()).isEqualTo(domain.shopName());
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
            ShopJpaEntity entity = ShopJpaEntityFixtures.activeEntity();

            // when
            Shop domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.shopName()).isEqualTo(entity.getShopName());
            assertThat(domain.accountId()).isEqualTo(entity.getAccountId());
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            ShopJpaEntity entity = ShopJpaEntityFixtures.inactiveEntity();

            // when
            Shop domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            ShopJpaEntity entity = ShopJpaEntityFixtures.deletedEntity();

            // when
            Shop domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            ShopJpaEntity entity = ShopJpaEntityFixtures.newEntity();

            // when
            Shop domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.shopName()).isEqualTo(entity.getShopName());
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
            Shop original = ShopFixtures.activeShop();

            // when
            ShopJpaEntity entity = mapper.toEntity(original);
            Shop converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.shopName()).isEqualTo(original.shopName());
            assertThat(converted.accountId()).isEqualTo(original.accountId());
            assertThat(converted.isActive()).isEqualTo(original.isActive());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            ShopJpaEntity original = ShopJpaEntityFixtures.activeEntity();

            // when
            Shop domain = mapper.toDomain(original);
            ShopJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getShopName()).isEqualTo(original.getShopName());
            assertThat(converted.getAccountId()).isEqualTo(original.getAccountId());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
        }
    }
}
