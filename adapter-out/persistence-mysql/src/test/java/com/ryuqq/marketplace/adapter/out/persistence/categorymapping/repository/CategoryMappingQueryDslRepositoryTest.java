package com.ryuqq.marketplace.adapter.out.persistence.categorymapping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.category.entity.CategoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.composite.CategoryMappingWithCategoryDto;
import com.ryuqq.marketplace.adapter.out.persistence.categorymapping.entity.CategoryMappingJpaEntity;
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
 * CategoryMappingQueryDslRepositoryTest - CategoryMapping QueryDslRepository 통합 테스트.
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
@DisplayName("CategoryMappingQueryDslRepository 통합 테스트")
class CategoryMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private CategoryMappingQueryDslRepository repository() {
        return new CategoryMappingQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findMappedCategoriesByPresetId")
    class FindMappedCategoriesByPresetIdTest {

        @Test
        @DisplayName("ACTIVE 매핑만 조회한다")
        void findMappedCategoriesByPresetId_WithActiveMappings_ReturnsOnlyActive() {
            // given
            Instant now = Instant.now();
            long presetId = 1L;

            CategoryJpaEntity category1 =
                    persist(
                            CategoryJpaEntity.create(
                                    null,
                                    "CAT_INT_A",
                                    "내부카테고리A",
                                    "CategoryA",
                                    null,
                                    1,
                                    "1",
                                    0,
                                    true,
                                    "ACTIVE",
                                    "FASHION",
                                    "TOPS",
                                    "의류 > 상의",
                                    now,
                                    now,
                                    null));

            CategoryJpaEntity category2 =
                    persist(
                            CategoryJpaEntity.create(
                                    null,
                                    "CAT_INT_B",
                                    "내부카테고리B",
                                    "CategoryB",
                                    null,
                                    1,
                                    "2",
                                    0,
                                    true,
                                    "ACTIVE",
                                    "FASHION",
                                    "BOTTOMS",
                                    "의류 > 하의",
                                    now,
                                    now,
                                    null));

            persist(
                    CategoryMappingJpaEntity.create(
                            null, presetId, 100L, category1.getId(), "ACTIVE", now, now));

            persist(
                    CategoryMappingJpaEntity.create(
                            null, presetId, 100L, category2.getId(), "INACTIVE", now, now));

            // when
            List<CategoryMappingWithCategoryDto> result =
                    repository().findMappedCategoriesByPresetId(presetId);

            // then
            assertThat(result).hasSize(1);
            CategoryMappingWithCategoryDto dto = result.get(0);
            assertThat(dto.internalCategoryId()).isEqualTo(category1.getId());
            assertThat(dto.categoryName()).isEqualTo("내부카테고리A");
            assertThat(dto.displayPath()).isEqualTo("의류 > 상의");
            assertThat(dto.code()).isEqualTo("CAT_INT_A");
        }

        @Test
        @DisplayName("해당 presetId의 매핑이 없으면 빈 목록을 반환한다")
        void findMappedCategoriesByPresetId_WithNoMappings_ReturnsEmptyList() {
            // when
            List<CategoryMappingWithCategoryDto> result =
                    repository().findMappedCategoriesByPresetId(999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 presetId의 매핑은 조회되지 않는다")
        void findMappedCategoriesByPresetId_WithDifferentPresetId_ReturnsEmpty() {
            // given
            Instant now = Instant.now();
            long presetId = 1L;
            long otherPresetId = 2L;

            CategoryJpaEntity category =
                    persist(
                            CategoryJpaEntity.create(
                                    null,
                                    "CAT_INT_C",
                                    "내부카테고리C",
                                    "CategoryC",
                                    null,
                                    1,
                                    "3",
                                    0,
                                    true,
                                    "ACTIVE",
                                    "FASHION",
                                    "OUTER",
                                    "의류 > 아우터",
                                    now,
                                    now,
                                    null));

            persist(
                    CategoryMappingJpaEntity.create(
                            null, otherPresetId, 100L, category.getId(), "ACTIVE", now, now));

            // when
            List<CategoryMappingWithCategoryDto> result =
                    repository().findMappedCategoriesByPresetId(presetId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
