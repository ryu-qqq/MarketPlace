package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * CommonCodeTypeJpaEntityTest - 공통 코드 타입 JPA Entity 단위 테스트.
 *
 * <p>create() 정적 팩토리 및 getter가 Fixtures와 일치하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("CommonCodeTypeJpaEntity 단위 테스트")
class CommonCodeTypeJpaEntityTest {

    @Nested
    @DisplayName("create 메서드 테스트")
    class CreateTest {

        @Test
        @DisplayName("create로 생성한 Entity의 getter가 인자와 일치합니다")
        void create_WithAllArgs_ReturnsEntityWithMatchingGetters() {
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntity.create(
                            1L,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DISPLAY_ORDER,
                            true,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getCode()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE);
            assertThat(entity.getName()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME);
            assertThat(entity.getDescription())
                    .isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION);
            assertThat(entity.getDisplayOrder())
                    .isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_DISPLAY_ORDER);
            assertThat(entity.isActive()).isTrue();
            assertThat(entity.getCreatedAt()).isEqualTo(now);
            assertThat(entity.getUpdatedAt()).isEqualTo(now);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("id가 null인 새 Entity를 생성합니다")
        void create_WithNullId_ReturnsEntityWithNullId() {
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntity.create(
                            null,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DISPLAY_ORDER,
                            true,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE);
        }

        @Test
        @DisplayName("비활성 상태 Entity를 생성합니다")
        void create_WithInactive_ReturnsEntityWithActiveFalse() {
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntity.create(
                            1L,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DISPLAY_ORDER,
                            false,
                            now,
                            now,
                            null);

            assertThat(entity.isActive()).isFalse();
        }

        @Test
        @DisplayName("설명이 없는 Entity를 생성합니다")
        void create_WithoutDescription_ReturnsEntityWithNullDescription() {
            Instant now = Instant.now();
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntity.create(
                            1L,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME,
                            null,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DISPLAY_ORDER,
                            true,
                            now,
                            now,
                            null);

            assertThat(entity.getDescription()).isNull();
        }

        @Test
        @DisplayName("커스텀 표시 순서를 가진 Entity를 생성합니다")
        void create_WithCustomDisplayOrder_ReturnsEntityWithDisplayOrder() {
            Instant now = Instant.now();
            int customDisplayOrder = 10;
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntity.create(
                            1L,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION,
                            customDisplayOrder,
                            true,
                            now,
                            now,
                            null);

            assertThat(entity.getDisplayOrder()).isEqualTo(customDisplayOrder);
        }

        @Test
        @DisplayName("삭제 시점이 있으면 deletedAt이 설정됩니다")
        void create_WithDeletedAt_ReturnsEntityWithDeletedAt() {
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(60);
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntity.create(
                            1L,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DESCRIPTION,
                            CommonCodeTypeJpaEntityFixtures.DEFAULT_DISPLAY_ORDER,
                            true,
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
        @DisplayName("activeEntity Fixture가 create 인자와 일치합니다")
        void activeEntityFixture_MatchesCreateArgs() {
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.activeEntity();

            assertThat(entity.getId()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_ID);
            assertThat(entity.getCode()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE);
            assertThat(entity.getName()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_NAME);
            assertThat(entity.isActive()).isTrue();
        }

        @Test
        @DisplayName("newEntity Fixture는 id가 null입니다")
        void newEntityFixture_HasNullId() {
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.newEntity();

            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).isEqualTo(CommonCodeTypeJpaEntityFixtures.DEFAULT_CODE);
        }

        @Test
        @DisplayName("inactiveEntity Fixture는 active가 false입니다")
        void inactiveEntityFixture_HasActiveFalse() {
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.inactiveEntity();

            assertThat(entity.isActive()).isFalse();
        }

        @Test
        @DisplayName("entityWithoutDescription Fixture는 description이 null입니다")
        void entityWithoutDescriptionFixture_HasNullDescription() {
            CommonCodeTypeJpaEntity entity =
                    CommonCodeTypeJpaEntityFixtures.entityWithoutDescription();

            assertThat(entity.getDescription()).isNull();
        }
    }
}
