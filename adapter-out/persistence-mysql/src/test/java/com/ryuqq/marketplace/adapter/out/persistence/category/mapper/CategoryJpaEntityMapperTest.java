package com.ryuqq.marketplace.adapter.out.persistence.category.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.domain.category.aggregate.Category;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.category.vo.CategoryCode;
import com.ryuqq.marketplace.domain.category.vo.CategoryDepth;
import com.ryuqq.marketplace.domain.category.vo.CategoryGroup;
import com.ryuqq.marketplace.domain.category.vo.CategoryName;
import com.ryuqq.marketplace.domain.category.vo.CategoryPath;
import com.ryuqq.marketplace.domain.category.vo.CategoryStatus;
import com.ryuqq.marketplace.domain.category.vo.Department;
import com.ryuqq.marketplace.domain.category.vo.SortOrder;
import java.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CategoryJpaEntityMapperTest - 카테고리 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CategoryJpaEntityMapper 단위 테스트")
class CategoryJpaEntityMapperTest {

    private CategoryJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new CategoryJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithActiveCategory_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Category domain =
                    Category.reconstitute(
                            CategoryId.of(1L),
                            CategoryCode.of("CAT001"),
                            CategoryName.of("테스트 카테고리", "Test Category"),
                            null,
                            CategoryDepth.of(1),
                            CategoryPath.of("/1"),
                            SortOrder.of(1),
                            true,
                            CategoryStatus.ACTIVE,
                            Department.FASHION,
                            CategoryGroup.CLOTHING,
                            null,
                            now,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
            assertThat(entity.getNameKo()).isEqualTo(domain.nameKo());
            assertThat(entity.getNameEn()).isEqualTo(domain.nameEn());
            assertThat(entity.getParentId()).isEqualTo(domain.parentId());
            assertThat(entity.getDepth()).isEqualTo(domain.depthValue());
            assertThat(entity.getPath()).isEqualTo(domain.pathValue());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrderValue());
            assertThat(entity.isLeaf()).isEqualTo(domain.isLeaf());
            assertThat(entity.getStatus()).isEqualTo(domain.status().name());
            assertThat(entity.getDepartment()).isEqualTo(domain.department().name());
            assertThat(entity.getCategoryGroup()).isEqualTo(domain.categoryGroup().name());
        }

        @Test
        @DisplayName("비활성 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithInactiveCategory_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Category domain =
                    Category.reconstitute(
                            CategoryId.of(2L),
                            CategoryCode.of("CAT002"),
                            CategoryName.of("비활성 카테고리", "Inactive Category"),
                            null,
                            CategoryDepth.of(1),
                            CategoryPath.of("/2"),
                            SortOrder.of(1),
                            true,
                            CategoryStatus.INACTIVE,
                            Department.BEAUTY,
                            CategoryGroup.COSMETICS,
                            null,
                            now,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getStatus()).isEqualTo("INACTIVE");
        }

        @Test
        @DisplayName("삭제된 상태 Domain을 Entity로 변환합니다")
        void toEntity_WithDeletedCategory_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(100);
            Category domain =
                    Category.reconstitute(
                            CategoryId.of(3L),
                            CategoryCode.of("CAT003"),
                            CategoryName.of("삭제된 카테고리", "Deleted Category"),
                            null,
                            CategoryDepth.of(1),
                            CategoryPath.of("/3"),
                            SortOrder.of(1),
                            true,
                            CategoryStatus.ACTIVE,
                            Department.LIVING,
                            CategoryGroup.FURNITURE,
                            deletedAt,
                            now,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }

        @Test
        @DisplayName("새로운 Domain을 Entity로 변환합니다")
        void toEntity_WithNewCategory_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Category domain =
                    Category.forNew(
                            CategoryCode.of("CAT004"),
                            CategoryName.of("새 카테고리", "New Category"),
                            null,
                            CategoryDepth.root(),
                            CategoryPath.of("/"),
                            SortOrder.of(1),
                            Department.FOOD,
                            CategoryGroup.ETC,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(domain.codeValue());
        }

        @Test
        @DisplayName("자식 카테고리 Domain을 Entity로 변환합니다")
        void toEntity_WithChildCategory_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Category domain =
                    Category.reconstitute(
                            CategoryId.of(5L),
                            CategoryCode.of("CAT005"),
                            CategoryName.of("자식 카테고리", "Child Category"),
                            1L,
                            CategoryDepth.of(2),
                            CategoryPath.of("/1/5"),
                            SortOrder.of(1),
                            true,
                            CategoryStatus.ACTIVE,
                            Department.DIGITAL,
                            CategoryGroup.DIGITAL,
                            null,
                            now,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getParentId()).isEqualTo(1L);
            assertThat(entity.getDepth()).isEqualTo(2);
            assertThat(entity.getPath()).isEqualTo("/1/5");
        }

        @Test
        @DisplayName("리프가 아닌 Domain을 Entity로 변환합니다")
        void toEntity_WithNonLeafCategory_ConvertsCorrectly() {
            // given
            Instant now = Instant.now();
            Category domain =
                    Category.reconstitute(
                            CategoryId.of(6L),
                            CategoryCode.of("CAT006"),
                            CategoryName.of("부모 카테고리", "Parent Category"),
                            null,
                            CategoryDepth.of(1),
                            CategoryPath.of("/6"),
                            SortOrder.of(1),
                            false,
                            CategoryStatus.ACTIVE,
                            Department.SPORTS,
                            CategoryGroup.SPORTS,
                            null,
                            now,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isLeaf()).isFalse();
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
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.activeRootEntity();

            // when
            Category domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.codeValue()).isEqualTo(entity.getCode());
            assertThat(domain.nameKo()).isEqualTo(entity.getNameKo());
            assertThat(domain.nameEn()).isEqualTo(entity.getNameEn());
            assertThat(domain.parentId()).isEqualTo(entity.getParentId());
            assertThat(domain.depthValue()).isEqualTo(entity.getDepth());
            assertThat(domain.pathValue()).isEqualTo(entity.getPath());
            assertThat(domain.sortOrderValue()).isEqualTo(entity.getSortOrder());
            assertThat(domain.isLeaf()).isEqualTo(entity.isLeaf());
            assertThat(domain.status()).isEqualTo(CategoryStatus.ACTIVE);
            assertThat(domain.department()).isEqualTo(Department.FASHION);
            assertThat(domain.categoryGroup()).isEqualTo(CategoryGroup.CLOTHING);
            assertThat(domain.isActive()).isTrue();
        }

        @Test
        @DisplayName("비활성 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithInactiveEntity_ConvertsCorrectly() {
            // given
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.inactiveEntity();

            // when
            Category domain = mapper.toDomain(entity);

            // then
            assertThat(domain.status()).isEqualTo(CategoryStatus.INACTIVE);
            assertThat(domain.isActive()).isFalse();
        }

        @Test
        @DisplayName("삭제된 상태 Entity를 Domain으로 변환합니다")
        void toDomain_WithDeletedEntity_ConvertsCorrectly() {
            // given
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.deletedEntity();

            // when
            Category domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isDeleted()).isTrue();
            assertThat(domain.deletedAt()).isNotNull();
        }

        @Test
        @DisplayName("영문명이 없는 Entity를 Domain으로 변환합니다")
        void toDomain_WithoutNameEn_ConvertsCorrectly() {
            // given
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.entityWithoutNameEn();

            // when
            Category domain = mapper.toDomain(entity);

            // then
            assertThat(domain.nameEn()).isNull();
        }

        @Test
        @DisplayName("리프가 아닌 Entity를 Domain으로 변환합니다")
        void toDomain_WithNonLeafEntity_ConvertsCorrectly() {
            // given
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.nonLeafEntity();

            // when
            Category domain = mapper.toDomain(entity);

            // then
            assertThat(domain.isLeaf()).isFalse();
        }

        @Test
        @DisplayName("자식 카테고리 Entity를 Domain으로 변환합니다")
        void toDomain_WithChildEntity_ConvertsCorrectly() {
            // given
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.activeChildEntity(1L);

            // when
            Category domain = mapper.toDomain(entity);

            // then
            assertThat(domain.parentId()).isEqualTo(1L);
            assertThat(domain.depthValue()).isEqualTo(2);
        }

        @Test
        @DisplayName("ID가 null인 새 Entity를 Domain으로 변환합니다")
        void toDomain_WithNewEntity_ConvertsCorrectly() {
            // given
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.newEntity();

            // when
            Category domain = mapper.toDomain(entity);

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
            Category original =
                    Category.reconstitute(
                            CategoryId.of(1L),
                            CategoryCode.of("CAT001"),
                            CategoryName.of("테스트 카테고리", "Test Category"),
                            null,
                            CategoryDepth.of(1),
                            CategoryPath.of("/1"),
                            SortOrder.of(1),
                            true,
                            CategoryStatus.ACTIVE,
                            Department.FASHION,
                            CategoryGroup.CLOTHING,
                            null,
                            now,
                            now);

            // when
            CategoryJpaEntity entity = mapper.toEntity(original);
            Category converted = mapper.toDomain(entity);

            // then
            assertThat(converted.idValue()).isEqualTo(original.idValue());
            assertThat(converted.codeValue()).isEqualTo(original.codeValue());
            assertThat(converted.nameKo()).isEqualTo(original.nameKo());
            assertThat(converted.nameEn()).isEqualTo(original.nameEn());
            assertThat(converted.parentId()).isEqualTo(original.parentId());
            assertThat(converted.depthValue()).isEqualTo(original.depthValue());
            assertThat(converted.pathValue()).isEqualTo(original.pathValue());
            assertThat(converted.sortOrderValue()).isEqualTo(original.sortOrderValue());
            assertThat(converted.isLeaf()).isEqualTo(original.isLeaf());
            assertThat(converted.status()).isEqualTo(original.status());
            assertThat(converted.department()).isEqualTo(original.department());
            assertThat(converted.categoryGroup()).isEqualTo(original.categoryGroup());
            assertThat(converted.isActive()).isEqualTo(original.isActive());
        }

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            CategoryJpaEntity original = CategoryJpaEntityFixtures.activeRootEntity();

            // when
            Category domain = mapper.toDomain(original);
            CategoryJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getCode()).isEqualTo(original.getCode());
            assertThat(converted.getNameKo()).isEqualTo(original.getNameKo());
            assertThat(converted.getNameEn()).isEqualTo(original.getNameEn());
            assertThat(converted.getParentId()).isEqualTo(original.getParentId());
            assertThat(converted.getDepth()).isEqualTo(original.getDepth());
            assertThat(converted.getPath()).isEqualTo(original.getPath());
            assertThat(converted.getSortOrder()).isEqualTo(original.getSortOrder());
            assertThat(converted.isLeaf()).isEqualTo(original.isLeaf());
            assertThat(converted.getStatus()).isEqualTo(original.getStatus());
            assertThat(converted.getDepartment()).isEqualTo(original.getDepartment());
            assertThat(converted.getCategoryGroup()).isEqualTo(original.getCategoryGroup());
        }
    }
}
