package com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.SalesChannelCategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * SalesChannelCategoryJpaEntityMapperTest - SalesChannelCategory Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("SalesChannelCategoryJpaEntityMapper 단위 테스트")
class SalesChannelCategoryJpaEntityMapperTest {

    private SalesChannelCategoryJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelCategoryJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveSalesChannelCategory_ConvertsCorrectly() {
            // given
            SalesChannelCategory domain = SalesChannelCategoryFixtures.activeSalesChannelCategory();

            // when
            SalesChannelCategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getSalesChannelId()).isEqualTo(domain.salesChannelId());
            assertThat(entity.getExternalCategoryCode()).isEqualTo(domain.externalCategoryCode());
            assertThat(entity.getExternalCategoryName()).isEqualTo(domain.externalCategoryName());
            assertThat(entity.getParentId()).isEqualTo(domain.parentId());
            assertThat(entity.getDepth()).isEqualTo(domain.depth());
            assertThat(entity.getPath()).isEqualTo(domain.path());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrder());
            assertThat(entity.isLeaf()).isEqualTo(domain.isLeaf());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
            assertThat(entity.getDisplayPath()).isEqualTo(domain.displayPath());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveSalesChannelCategory_ConvertsCorrectly() {
            // given
            SalesChannelCategory domain = SalesChannelCategoryFixtures.inactiveSalesChannelCategory();

            // when
            SalesChannelCategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewSalesChannelCategory_ConvertsCorrectly() {
            // given
            SalesChannelCategory domain = SalesChannelCategoryFixtures.newSalesChannelCategory();

            // when
            SalesChannelCategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getExternalCategoryCode()).isEqualTo(domain.externalCategoryCode());
            assertThat(entity.getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("말단 카테고리 Domain을 Entity로 변환합니다")
        void toEntity_WithLeafCategory_ConvertsCorrectly() {
            // given
            SalesChannelCategory domain = SalesChannelCategoryFixtures.leafCategory();

            // when
            SalesChannelCategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isLeaf()).isTrue();
            assertThat(entity.getParentId()).isNotNull();
            assertThat(entity.getDepth()).isEqualTo(3);
        }

        @Test
        @DisplayName("하위 카테고리 Domain을 Entity로 변환합니다")
        void toEntity_WithChildCategory_ConvertsCorrectly() {
            // given
            SalesChannelCategory domain = SalesChannelCategoryFixtures.newChildCategory(100L);

            // when
            SalesChannelCategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getParentId()).isEqualTo(100L);
            assertThat(entity.getDepth()).isEqualTo(2);
            assertThat(entity.getPath()).contains("/CAT001/");
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
            SalesChannelCategoryJpaEntity entity =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(1L);

            // when
            SalesChannelCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.salesChannelId()).isEqualTo(entity.getSalesChannelId());
            assertThat(domain.externalCategoryCode()).isEqualTo(entity.getExternalCategoryCode());
            assertThat(domain.externalCategoryName()).isEqualTo(entity.getExternalCategoryName());
            assertThat(domain.parentId()).isEqualTo(entity.getParentId());
            assertThat(domain.depth()).isEqualTo(entity.getDepth());
            assertThat(domain.path()).isEqualTo(entity.getPath());
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
            assertThat(domain.isLeaf()).isEqualTo(entity.isLeaf());
            assertThat(domain.status().name()).isEqualTo(entity.getStatus());
            assertThat(domain.displayPath()).isEqualTo(entity.getDisplayPath());
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            SalesChannelCategoryJpaEntity entity =
                    SalesChannelCategoryJpaEntityFixtures.inactiveEntity();
            entity = SalesChannelCategoryJpaEntityFixtures.activeEntity(2L);
            entity.update("비활성 카테고리", 1, false, "INACTIVE", entity.getUpdatedAt());

            // when
            SalesChannelCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("말단 카테고리 Entity를 Domain으로 변환합니다")
        void toDomain_WithLeafEntity_ConvertsCorrectly() {
            // given
            SalesChannelCategoryJpaEntity entity =
                    SalesChannelCategoryJpaEntityFixtures.leafEntity();
            entity = SalesChannelCategoryJpaEntityFixtures.activeEntity(3L);
            entity.update("말단 카테고리", 1, true, "ACTIVE", entity.getUpdatedAt());

            // when
            SalesChannelCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isLeaf()).isTrue();
        }

        @Test
        @DisplayName("DisplayPath가 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutDisplayPath_ConvertsCorrectly() {
            // given
            SalesChannelCategoryJpaEntity entity =
                    SalesChannelCategoryJpaEntityFixtures.entityWithoutDisplayPath();
            entity = SalesChannelCategoryJpaEntityFixtures.customEntity(
                    4L,
                    1L,
                    "CAT004",
                    "테스트 카테고리",
                    null,
                    1,
                    "/CAT004",
                    1,
                    false,
                    "ACTIVE",
                    null);

            // when
            SalesChannelCategory domain = mapper.toDomain(entity);

            // then
            assertThat(domain.displayPath()).isNull();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환하면 예외가 발생합니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            SalesChannelCategoryJpaEntity entity =
                    SalesChannelCategoryJpaEntityFixtures.newEntity();

            // when & then
            assertThatThrownBy(() -> mapper.toDomain(entity))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("영속화된 엔티티의 ID는 null일 수 없습니다");
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
            SalesChannelCategory original =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            // when
            SalesChannelCategoryJpaEntity entity = mapper.toEntity(original);
            SalesChannelCategory converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.salesChannelId()).isEqualTo(original.salesChannelId());
            assertThat(converted.externalCategoryCode())
                    .isEqualTo(original.externalCategoryCode());
            assertThat(converted.externalCategoryName())
                    .isEqualTo(original.externalCategoryName());
            assertThat(converted.parentId()).isEqualTo(original.parentId());
            assertThat(converted.depth()).isEqualTo(original.depth());
            assertThat(converted.path()).isEqualTo(original.path());
            assertThat(converted.sortOrder()).isEqualTo(original.sortOrder());
            assertThat(converted.isLeaf()).isEqualTo(original.isLeaf());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.displayPath()).isEqualTo(original.displayPath());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            SalesChannelCategoryJpaEntity original =
                    SalesChannelCategoryJpaEntityFixtures.activeEntity(1L);

            // when
            SalesChannelCategory domain = mapper.toDomain(original);
            SalesChannelCategoryJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getSalesChannelId()).isEqualTo(original.getSalesChannelId());
            assertThat(converted.getExternalCategoryCode())
                    .isEqualTo(original.getExternalCategoryCode());
            assertThat(converted.getExternalCategoryName())
                    .isEqualTo(original.getExternalCategoryName());
            assertThat(converted.getParentId()).isEqualTo(original.getParentId());
            assertThat(converted.getDepth()).isEqualTo(original.getDepth());
            assertThat(converted.getPath()).isEqualTo(original.getPath());
            assertThat(converted.getSortOrder()).isEqualTo(original.getSortOrder());
            assertThat(converted.isLeaf()).isEqualTo(original.isLeaf());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getDisplayPath()).isEqualTo(original.getDisplayPath());
        }
    }
}
