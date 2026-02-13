package com.ryuqq.marketplace.adapter.out.persistence.categorypreset.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.composite.CategoryPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.condition.CategoryPresetConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.categorypreset.entity.CategoryPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelcategory.entity.SalesChannelCategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * CategoryPresetQueryDslRepositoryTest - CategoryPreset QueryDslRepository 통합 테스트.
 *
 * <p>실제 데이터베이스 연동을 통한 조회 기능 검증.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false"
        })
@DisplayName("CategoryPresetQueryDslRepository 통합 테스트")
class CategoryPresetQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CategoryPresetQueryDslRepository repository() {
        return new CategoryPresetQueryDslRepository(
                new JPAQueryFactory(entityManager), new CategoryPresetConditionBuilder());
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findDetailCompositeById")
    class FindDetailCompositeByIdTest {

        @Test
        @DisplayName("존재하는 프리셋 ID로 조회 시 CategoryPresetDetailCompositeDto를 반환한다")
        void findDetailCompositeById_WithExistingId_ReturnsDto() {
            // given
            Instant now = Instant.now();

            SalesChannelJpaEntity salesChannel =
                    persist(SalesChannelJpaEntity.create(null, "테스트채널", "ACTIVE", now, now));

            SalesChannelCategoryJpaEntity salesChannelCategory =
                    persist(
                            SalesChannelCategoryJpaEntity.create(
                                    null,
                                    salesChannel.getId(),
                                    "CAT001",
                                    "테스트카테고리",
                                    null,
                                    1,
                                    "1",
                                    0,
                                    true,
                                    "ACTIVE",
                                    "의류 > 상의",
                                    now,
                                    now));

            ShopJpaEntity shop =
                    persist(
                            ShopJpaEntity.create(
                                    null,
                                    salesChannel.getId(),
                                    "테스트샵",
                                    "account123",
                                    "ACTIVE",
                                    now,
                                    now,
                                    null));

            CategoryPresetJpaEntity preset =
                    persist(
                            CategoryPresetJpaEntity.create(
                                    null,
                                    shop.getId(),
                                    salesChannelCategory.getId(),
                                    "테스트프리셋",
                                    "ACTIVE",
                                    now,
                                    now));

            // when
            Optional<CategoryPresetDetailCompositeDto> result =
                    repository().findDetailCompositeById(preset.getId());

            // then
            assertThat(result).isPresent();
            CategoryPresetDetailCompositeDto dto = result.get();
            assertThat(dto.id()).isEqualTo(preset.getId());
            assertThat(dto.shopId()).isEqualTo(shop.getId());
            assertThat(dto.shopName()).isEqualTo("테스트샵");
            assertThat(dto.accountId()).isEqualTo("account123");
            assertThat(dto.salesChannelId()).isEqualTo(salesChannel.getId());
            assertThat(dto.salesChannelName()).isEqualTo("테스트채널");
            assertThat(dto.salesChannelCategoryId()).isEqualTo(salesChannelCategory.getId());
            assertThat(dto.externalCategoryCode()).isEqualTo("CAT001");
            assertThat(dto.categoryDisplayPath()).isEqualTo("의류 > 상의");
            assertThat(dto.presetName()).isEqualTo("테스트프리셋");
            assertThat(dto.status()).isEqualTo("ACTIVE");
            assertThat(dto.createdAt()).isNotNull();
            assertThat(dto.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 Optional.empty()를 반환한다")
        void findDetailCompositeById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<CategoryPresetDetailCompositeDto> result =
                    repository().findDetailCompositeById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
