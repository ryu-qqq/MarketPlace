package com.ryuqq.marketplace.adapter.out.persistence.brandpreset.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.BrandPresetJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.composite.BrandPresetDetailCompositeDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.condition.BrandPresetConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.brandpreset.entity.BrandPresetJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannelbrand.entity.SalesChannelBrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.shop.entity.ShopJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
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
 * BrandPresetQueryDslRepositoryTest - BrandPreset QueryDslRepository 통합 테스트.
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
@DisplayName("BrandPresetQueryDslRepository 통합 테스트")
class BrandPresetQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private BrandPresetQueryDslRepository repository() {
        return new BrandPresetQueryDslRepository(
                new JPAQueryFactory(entityManager), new BrandPresetConditionBuilder());
    }

    private BrandPresetJpaEntity persist(BrandPresetJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private <T> T persistEntity(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("활성 Entity는 findById로 조회됩니다")
        void findById_WithActiveEntity_ReturnsEntity() {
            // given
            BrandPresetJpaEntity saved = persist(BrandPresetJpaEntityFixtures.newEntity());

            // when
            var result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            var result = repository().findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAllByIds")
    class FindAllByIdsTest {

        @Test
        @DisplayName("활성 Entity 목록을 ID 목록으로 조회합니다")
        void findAllByIds_WithActiveEntities_ReturnsEntities() {
            // given
            BrandPresetJpaEntity entity1 = persist(BrandPresetJpaEntityFixtures.newEntity());
            BrandPresetJpaEntity entity2 = persist(BrandPresetJpaEntityFixtures.newEntity());
            BrandPresetJpaEntity entity3 = persist(BrandPresetJpaEntityFixtures.newEntity());

            // when
            var result =
                    repository()
                            .findAllByIds(
                                    List.of(entity1.getId(), entity2.getId(), entity3.getId()));

            // then
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(BrandPresetJpaEntity::getId)
                    .containsExactlyInAnyOrder(entity1.getId(), entity2.getId(), entity3.getId());
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findAllByIds_WithEmptyList_ReturnsEmptyList() {
            // when
            var result = repository().findAllByIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("존재하지 않는 ID 목록으로 조회 시 빈 목록을 반환합니다")
        void findAllByIds_WithNonExistentIds_ReturnsEmptyList() {
            // when
            var result = repository().findAllByIds(List.of(998L, 999L));

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findSalesChannelIdBySalesChannelBrandId")
    class FindSalesChannelIdBySalesChannelBrandIdTest {

        @Test
        @DisplayName("존재하지 않는 SalesChannelBrandId로 조회 시 빈 Optional을 반환합니다")
        void findSalesChannelIdBySalesChannelBrandId_WithNonExistentId_ReturnsEmpty() {
            // when
            var result = repository().findSalesChannelIdBySalesChannelBrandId(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findDetailCompositeById")
    class FindDetailCompositeByIdTest {

        @Test
        @DisplayName("존재하는 프리셋 ID로 조회 시 BrandPresetDetailCompositeDto를 반환한다")
        void findDetailCompositeById_WithExistingId_ReturnsDto() {
            // given
            Instant now = Instant.now();

            SalesChannelJpaEntity salesChannel =
                    persistEntity(SalesChannelJpaEntity.create(null, "테스트채널", "ACTIVE", now, now));

            SalesChannelBrandJpaEntity salesChannelBrand =
                    persistEntity(
                            SalesChannelBrandJpaEntity.create(
                                    null,
                                    salesChannel.getId(),
                                    "B123",
                                    "테스트브랜드",
                                    "ACTIVE",
                                    now,
                                    now));

            ShopJpaEntity shop =
                    persistEntity(
                            ShopJpaEntity.create(
                                    null,
                                    salesChannel.getId(),
                                    "테스트샵",
                                    "account123",
                                    "ACTIVE",
                                    now,
                                    now,
                                    null));

            BrandPresetJpaEntity preset =
                    persistEntity(
                            BrandPresetJpaEntity.create(
                                    null,
                                    shop.getId(),
                                    salesChannelBrand.getId(),
                                    "테스트프리셋",
                                    "ACTIVE",
                                    now,
                                    now));

            // when
            Optional<BrandPresetDetailCompositeDto> result =
                    repository().findDetailCompositeById(preset.getId());

            // then
            assertThat(result).isPresent();
            BrandPresetDetailCompositeDto dto = result.get();
            assertThat(dto.id()).isEqualTo(preset.getId());
            assertThat(dto.shopId()).isEqualTo(shop.getId());
            assertThat(dto.shopName()).isEqualTo("테스트샵");
            assertThat(dto.accountId()).isEqualTo("account123");
            assertThat(dto.salesChannelId()).isEqualTo(salesChannel.getId());
            assertThat(dto.salesChannelName()).isEqualTo("테스트채널");
            assertThat(dto.salesChannelBrandId()).isEqualTo(salesChannelBrand.getId());
            assertThat(dto.externalBrandCode()).isEqualTo("B123");
            assertThat(dto.externalBrandName()).isEqualTo("테스트브랜드");
            assertThat(dto.presetName()).isEqualTo("테스트프리셋");
            assertThat(dto.status()).isEqualTo("ACTIVE");
            assertThat(dto.createdAt()).isNotNull();
            assertThat(dto.updatedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 Optional.empty()를 반환한다")
        void findDetailCompositeById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<BrandPresetDetailCompositeDto> result =
                    repository().findDetailCompositeById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
