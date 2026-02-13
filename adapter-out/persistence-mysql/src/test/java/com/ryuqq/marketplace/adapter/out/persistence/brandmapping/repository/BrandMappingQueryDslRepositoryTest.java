package com.ryuqq.marketplace.adapter.out.persistence.brandmapping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.brand.entity.BrandJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.composite.BrandMappingWithBrandDto;
import com.ryuqq.marketplace.adapter.out.persistence.brandmapping.entity.BrandMappingJpaEntity;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 * BrandMappingQueryDslRepositoryTest - BrandMapping QueryDslRepository 통합 테스트.
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
@DisplayName("BrandMappingQueryDslRepository 통합 테스트")
class BrandMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private BrandMappingQueryDslRepository repository() {
        return new BrandMappingQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findMappedBrandsByPresetId")
    class FindMappedBrandsByPresetIdTest {

        @Test
        @DisplayName("ACTIVE 매핑만 조회한다")
        void findMappedBrandsByPresetId_WithActiveMappings_ReturnsOnlyActive() {
            // given
            Instant now = Instant.now();
            long presetId = 1L;

            BrandJpaEntity brand1 =
                    persist(
                            BrandJpaEntity.create(
                                    null, "BRAND_A", "브랜드A", "BrandA", "BA", "ACTIVE", null, now,
                                    now, null));

            BrandJpaEntity brand2 =
                    persist(
                            BrandJpaEntity.create(
                                    null, "BRAND_B", "브랜드B", "BrandB", "BB", "ACTIVE", null, now,
                                    now, null));

            persist(
                    BrandMappingJpaEntity.create(
                            null, presetId, 100L, brand1.getId(), "ACTIVE", now, now));

            persist(
                    BrandMappingJpaEntity.create(
                            null, presetId, 100L, brand2.getId(), "ACTIVE", now, now));

            persist(
                    BrandMappingJpaEntity.create(
                            null, presetId, 100L, brand1.getId(), "INACTIVE", now, now));

            // when
            List<BrandMappingWithBrandDto> result =
                    repository().findMappedBrandsByPresetId(presetId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(BrandMappingWithBrandDto::internalBrandId)
                    .containsExactlyInAnyOrder(brand1.getId(), brand2.getId());
            assertThat(result)
                    .extracting(BrandMappingWithBrandDto::brandName)
                    .containsExactlyInAnyOrder("브랜드A", "브랜드B");
        }

        @Test
        @DisplayName("해당 presetId의 매핑이 없으면 빈 목록을 반환한다")
        void findMappedBrandsByPresetId_WithNoMappings_ReturnsEmptyList() {
            // when
            List<BrandMappingWithBrandDto> result = repository().findMappedBrandsByPresetId(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 presetId의 매핑은 조회되지 않는다")
        void findMappedBrandsByPresetId_WithDifferentPresetId_ReturnsEmpty() {
            // given
            Instant now = Instant.now();
            long presetId = 1L;
            long otherPresetId = 2L;

            BrandJpaEntity brand =
                    persist(
                            BrandJpaEntity.create(
                                    null, "BRAND_C", "브랜드C", "BrandC", "BC", "ACTIVE", null, now,
                                    now, null));

            persist(
                    BrandMappingJpaEntity.create(
                            null, otherPresetId, 100L, brand.getId(), "ACTIVE", now, now));

            // when
            List<BrandMappingWithBrandDto> result =
                    repository().findMappedBrandsByPresetId(presetId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
