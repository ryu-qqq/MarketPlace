package com.ryuqq.marketplace.adapter.out.persistence.brand.entity;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.brand.BrandJpaEntityFixtures;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * BrandJpaEntityTest - 브랜드 JPA Entity 단위 테스트.
 *
 * <p>create() 정적 팩토리 및 getter가 Fixtures와 일치하는지 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("BrandJpaEntity 단위 테스트")
class BrandJpaEntityTest {

    @Nested
    @DisplayName("create 메서드 테스트")
    class CreateTest {

        @Test
        @DisplayName("create로 생성한 Entity의 getter가 인자와 일치합니다")
        void create_WithAllArgs_ReturnsEntityWithMatchingGetters() {
            Instant now = Instant.now();
            BrandJpaEntity entity =
                    BrandJpaEntity.create(
                            1L,
                            BrandJpaEntityFixtures.DEFAULT_CODE,
                            BrandJpaEntityFixtures.DEFAULT_NAME_KO,
                            BrandJpaEntityFixtures.DEFAULT_NAME_EN,
                            BrandJpaEntityFixtures.DEFAULT_SHORT_NAME,
                            BrandJpaEntityFixtures.DEFAULT_STATUS,
                            BrandJpaEntityFixtures.DEFAULT_LOGO_URL,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isEqualTo(1L);
            assertThat(entity.getCode()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_CODE);
            assertThat(entity.getNameKo()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_NAME_KO);
            assertThat(entity.getNameEn()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_NAME_EN);
            assertThat(entity.getShortName()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_SHORT_NAME);
            assertThat(entity.getStatus()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_STATUS);
            assertThat(entity.getLogoUrl()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_LOGO_URL);
            assertThat(entity.getCreatedAt()).isEqualTo(now);
            assertThat(entity.getUpdatedAt()).isEqualTo(now);
            assertThat(entity.getDeletedAt()).isNull();
        }

        @Test
        @DisplayName("id가 null인 새 Entity를 생성합니다")
        void create_WithNullId_ReturnsEntityWithNullId() {
            Instant now = Instant.now();
            BrandJpaEntity entity =
                    BrandJpaEntity.create(
                            null,
                            BrandJpaEntityFixtures.DEFAULT_CODE,
                            BrandJpaEntityFixtures.DEFAULT_NAME_KO,
                            BrandJpaEntityFixtures.DEFAULT_NAME_EN,
                            BrandJpaEntityFixtures.DEFAULT_SHORT_NAME,
                            BrandJpaEntityFixtures.DEFAULT_STATUS,
                            null,
                            now,
                            now,
                            null);

            assertThat(entity.getId()).isNull();
            assertThat(entity.getLogoUrl()).isNull();
        }

        @Test
        @DisplayName("삭제 시점이 있으면 deletedAt이 설정됩니다")
        void create_WithDeletedAt_ReturnsEntityWithDeletedAt() {
            Instant now = Instant.now();
            Instant deletedAt = now.plusSeconds(60);
            BrandJpaEntity entity =
                    BrandJpaEntity.create(
                            1L,
                            BrandJpaEntityFixtures.DEFAULT_CODE,
                            BrandJpaEntityFixtures.DEFAULT_NAME_KO,
                            BrandJpaEntityFixtures.DEFAULT_NAME_EN,
                            BrandJpaEntityFixtures.DEFAULT_SHORT_NAME,
                            BrandJpaEntityFixtures.DEFAULT_STATUS,
                            BrandJpaEntityFixtures.DEFAULT_LOGO_URL,
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
            BrandJpaEntity entity = BrandJpaEntityFixtures.activeEntity();

            assertThat(entity.getId()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_ID);
            assertThat(entity.getCode()).startsWith(BrandJpaEntityFixtures.DEFAULT_CODE);
            assertThat(entity.getStatus()).isEqualTo(BrandJpaEntityFixtures.DEFAULT_STATUS);
        }

        @Test
        @DisplayName("newEntity Fixture는 id가 null입니다")
        void newEntityFixture_HasNullId() {
            BrandJpaEntity entity = BrandJpaEntityFixtures.newEntity();

            assertThat(entity.getId()).isNull();
            assertThat(entity.getCode()).startsWith(BrandJpaEntityFixtures.DEFAULT_CODE);
        }
    }
}
