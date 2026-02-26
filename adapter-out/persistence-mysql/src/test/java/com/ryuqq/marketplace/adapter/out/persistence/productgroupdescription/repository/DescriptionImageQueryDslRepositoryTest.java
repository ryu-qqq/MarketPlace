package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.ProductGroupDescriptionJpaEntity;
import jakarta.persistence.EntityManager;
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
 * DescriptionImageQueryDslRepositoryTest - 상세설명 이미지 QueryDslRepository 통합 테스트.
 *
 * <p>findById 조회를 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
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
@DisplayName("DescriptionImageQueryDslRepository 통합 테스트")
class DescriptionImageQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private DescriptionImageQueryDslRepository repository() {
        return new DescriptionImageQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("유효한 ID로 DescriptionImage를 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            ProductGroupDescriptionJpaEntity description =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(1L));
            DescriptionImageJpaEntity saved =
                    persist(
                            ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(
                                    description.getId()));

            // when
            Optional<DescriptionImageJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("삭제된 이미지도 findById로 조회됩니다 (deleted 필터 미적용)")
        void findById_WithDeletedEntity_ReturnsEntity() {
            // given
            ProductGroupDescriptionJpaEntity description =
                    persist(ProductGroupDescriptionJpaEntityFixtures.pendingEntity(2L));
            DescriptionImageJpaEntity deleted =
                    persist(
                            ProductGroupDescriptionJpaEntityFixtures.deletedImageEntity(
                                    description.getId()));

            // when
            Optional<DescriptionImageJpaEntity> result = repository().findById(deleted.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().isDeleted()).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<DescriptionImageJpaEntity> result = repository().findById(999999L);

            // then
            assertThat(result).isEmpty();
        }
    }
}
