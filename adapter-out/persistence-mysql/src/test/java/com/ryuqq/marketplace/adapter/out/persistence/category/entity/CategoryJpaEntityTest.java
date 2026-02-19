package com.ryuqq.marketplace.adapter.out.persistence.category.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.category.CategoryJpaEntityFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CategoryJpaEntityTest - 카테고리 JPA Entity 단위 테스트.
 *
 * <p>create() 정적 팩토리 및 getter가 Fixtures와 일치하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CategoryJpaEntity 단위 테스트")
class CategoryJpaEntityTest {

    @Nested
    @DisplayName("create 메서드 테스트")
    class CreateTest {

        @Test
        @DisplayName("create로 생성한 Entity의 getter가 인자와 일치합니다")
        void create_WithAllArgs_ReturnsEntityWithMatchingGetters() {
            Instant now = Instant.now();
            CategoryJpaEntity entity =
                    CategoryJpaEntity.create(
                            1L,
                            CategoryJpaEntityFixtures.DEFAULT_CODE,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_KO,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_EN,
                            CategoryJpaEntityFixtures.DEFAULT_PARENT_ID,
                            CategoryJpaEntityFixtures.DEFAULT_DEPTH,
                            CategoryJpaEntityFixtures.DEFAULT_PATH,
                            CategoryJpaEntityFixtures.DEFAULT_SORT_ORDER,
                            CategoryJpaEntityFixtures.DEFAULT_LEAF,
                            CategoryJpaEntityFixtures.DEFAULT_STATUS,
                            CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT,
                            CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getCode()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_CODE);
            assertThat(entity.getNameKo()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_NAME_KO);
            assertThat(entity.getNameEn()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_NAME_EN);
            assertThat(entity.getParentId()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_PARENT_ID);
            assertThat(entity.getDepth()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_DEPTH);
            assertThat(entity.getPath()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_PATH);
            assertThat(entity.getSortOrder())
                    .isEqualTo(CategoryJpaEntityFixtures.DEFAULT_SORT_ORDER);
            assertThat(entity.isLeaf()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_LEAF);
            assertThat(entity.getStatus()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_STATUS);
            assertThat(entity.getDepartment())
                    .isEqualTo(CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT);
            assertThat(entity.getCategoryGroup())
                    .isEqualTo(CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP);
            assertThat(entity.getCreatedAt()).isEqualTo(now);
            assertThat(entity.getUpdatedAt()).isEqualTo(now);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("id가 null인 새 Entity를 생성합니다")
        void create_WithNullId_ReturnsEntityWithNullId() {
            Instant now = Instant.now();
            CategoryJpaEntity entity =
                    CategoryJpaEntity.create(
                            null,
                            CategoryJpaEntityFixtures.DEFAULT_CODE,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_KO,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_EN,
                            CategoryJpaEntityFixtures.DEFAULT_PARENT_ID,
                            CategoryJpaEntityFixtures.DEFAULT_DEPTH,
                            CategoryJpaEntityFixtures.DEFAULT_PATH,
                            CategoryJpaEntityFixtures.DEFAULT_SORT_ORDER,
                            CategoryJpaEntityFixtures.DEFAULT_LEAF,
                            CategoryJpaEntityFixtures.DEFAULT_STATUS,
                            CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT,
                            CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_CODE);
        }

        @Test
        @DisplayName("자식 카테고리 Entity를 생성합니다")
        void create_WithParentId_ReturnsEntityWithParentId() {
            Instant now = Instant.now();
            Long parentId = 1L;
            CategoryJpaEntity entity =
                    CategoryJpaEntity.create(
                            2L,
                            CategoryJpaEntityFixtures.DEFAULT_CODE,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_KO,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_EN,
                            parentId,
                            2,
                            "/1/2",
                            CategoryJpaEntityFixtures.DEFAULT_SORT_ORDER,
                            CategoryJpaEntityFixtures.DEFAULT_LEAF,
                            CategoryJpaEntityFixtures.DEFAULT_STATUS,
                            CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT,
                            CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.getParentId()).isEqualTo(parentId);
            assertThat(entity.getDepth()).isEqualTo(2);
            assertThat(entity.getPath()).isEqualTo("/1/2");
        }

        @Test
        @DisplayName("리프가 아닌 Entity를 생성합니다")
        void create_WithNonLeaf_ReturnsEntityWithLeafFalse() {
            Instant now = Instant.now();
            CategoryJpaEntity entity =
                    CategoryJpaEntity.create(
                            1L,
                            CategoryJpaEntityFixtures.DEFAULT_CODE,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_KO,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_EN,
                            CategoryJpaEntityFixtures.DEFAULT_PARENT_ID,
                            CategoryJpaEntityFixtures.DEFAULT_DEPTH,
                            CategoryJpaEntityFixtures.DEFAULT_PATH,
                            CategoryJpaEntityFixtures.DEFAULT_SORT_ORDER,
                            false,
                            CategoryJpaEntityFixtures.DEFAULT_STATUS,
                            CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT,
                            CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.isLeaf()).isFalse();
        }

        @Test
        @DisplayName("삭제 시점이 있으면 deletedAt이 설정됩니다")
        void create_WithDeletedAt_ReturnsEntityWithDeletedAt() {
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(60);
            CategoryJpaEntity entity =
                    CategoryJpaEntity.create(
                            1L,
                            CategoryJpaEntityFixtures.DEFAULT_CODE,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_KO,
                            CategoryJpaEntityFixtures.DEFAULT_NAME_EN,
                            CategoryJpaEntityFixtures.DEFAULT_PARENT_ID,
                            CategoryJpaEntityFixtures.DEFAULT_DEPTH,
                            CategoryJpaEntityFixtures.DEFAULT_PATH,
                            CategoryJpaEntityFixtures.DEFAULT_SORT_ORDER,
                            CategoryJpaEntityFixtures.DEFAULT_LEAF,
                            CategoryJpaEntityFixtures.DEFAULT_STATUS,
                            CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT,
                            CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP,
                            null,
                            now,
                            now,
                            deletedAt);

            assertThat(entity.getDeletedAt()).isEqualTo(deletedAt);
        }
    }

    @Nested
    @DisplayName("Fixtures 일관성 테스트")
    class FixturesConsistencyTest {

        @Test
        @DisplayName("activeRootEntity Fixture가 create 인자와 일치합니다")
        void activeRootEntityFixture_MatchesCreateArgs() {
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.activeRootEntity();

            assertThat(entity.getId()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_ID);
            assertThat(entity.getCode()).startsWith(CategoryJpaEntityFixtures.DEFAULT_CODE);
            assertThat(entity.getStatus()).isEqualTo(CategoryJpaEntityFixtures.DEFAULT_STATUS);
            assertThat(entity.getDepartment())
                    .isEqualTo(CategoryJpaEntityFixtures.DEFAULT_DEPARTMENT);
            assertThat(entity.getCategoryGroup())
                    .isEqualTo(CategoryJpaEntityFixtures.DEFAULT_CATEGORY_GROUP);
        }

        @Test
        @DisplayName("newEntity Fixture는 id가 null입니다")
        void newEntityFixture_HasNullId() {
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.newEntity();

            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).startsWith(CategoryJpaEntityFixtures.DEFAULT_CODE);
        }

        @Test
        @DisplayName("activeChildEntity Fixture는 parentId가 설정됩니다")
        void activeChildEntityFixture_HasParentId() {
            Long parentId = 1L;
            CategoryJpaEntity entity = CategoryJpaEntityFixtures.activeChildEntity(parentId);

            assertThat(entity.getParentId()).isEqualTo(parentId);
            assertThat(entity.getDepth()).isEqualTo(2);
        }
    }
}
