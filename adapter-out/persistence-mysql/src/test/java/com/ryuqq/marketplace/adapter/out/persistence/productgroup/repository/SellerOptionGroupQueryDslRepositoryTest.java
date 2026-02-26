package com.ryuqq.marketplace.adapter.out.persistence.productgroup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.ProductGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.ProductGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroup.entity.SellerOptionValueJpaEntity;
import jakarta.persistence.EntityManager;
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
 * SellerOptionGroupQueryDslRepositoryTest - 셀러 옵션 그룹 QueryDslRepository 통합 테스트.
 *
 * <p>deleted=true 소프트 삭제 필터 및 productGroupId 조건 검색 적용을 검증합니다.
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
@DisplayName("SellerOptionGroupQueryDslRepository 통합 테스트")
class SellerOptionGroupQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SellerOptionGroupQueryDslRepository repository() {
        return new SellerOptionGroupQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private ProductGroupJpaEntity persistProductGroup() {
        return persist(ProductGroupJpaEntityFixtures.newEntity());
    }

    // ========================================================================
    // 1. findByProductGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByProductGroupId")
    class FindByProductGroupIdTest {

        @Test
        @DisplayName("활성 SellerOptionGroup을 productGroupId로 조회합니다")
        void findByProductGroupId_WithActiveGroups_ReturnsEntities() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));
            persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getProductGroupId().equals(productGroupId));
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("소프트 삭제된 SellerOptionGroup은 조회에서 제외됩니다")
        void findByProductGroupId_WithDeletedGroups_ExcludesDeleted() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));
            persist(ProductGroupJpaEntityFixtures.deletedOptionGroupEntity(productGroupId));

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("해당 productGroupId에 옵션 그룹이 없으면 빈 리스트를 반환합니다")
        void findByProductGroupId_WithNoGroups_ReturnsEmpty() {
            // when
            List<SellerOptionGroupJpaEntity> result = repository().findByProductGroupId(999999L);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("sortOrder 오름차순으로 정렬하여 반환합니다")
        void findByProductGroupId_ReturnsSortedBySortOrder() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            // sortOrder를 다르게 설정하기 위해 직접 생성
            SellerOptionGroupJpaEntity group1 =
                    SellerOptionGroupJpaEntity.create(
                            null, productGroupId, "사이즈", null, "PREDEFINED", 2, false, null);
            SellerOptionGroupJpaEntity group2 =
                    SellerOptionGroupJpaEntity.create(
                            null, productGroupId, "색상", null, "PREDEFINED", 0, false, null);
            SellerOptionGroupJpaEntity group3 =
                    SellerOptionGroupJpaEntity.create(
                            null, productGroupId, "재질", null, "PREDEFINED", 1, false, null);

            persist(group1);
            persist(group2);
            persist(group3);

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findByProductGroupId(productGroupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder())
                    .isLessThanOrEqualTo(result.get(1).getSortOrder());
            assertThat(result.get(1).getSortOrder())
                    .isLessThanOrEqualTo(result.get(2).getSortOrder());
        }

        @Test
        @DisplayName("다른 productGroupId의 옵션 그룹은 조회되지 않습니다")
        void findByProductGroupId_WithDifferentProductGroupId_ReturnsOnlyMatchingGroups() {
            // given
            ProductGroupJpaEntity productGroup1 = persistProductGroup();
            ProductGroupJpaEntity productGroup2 = persistProductGroup();

            persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroup1.getId()));
            persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroup2.getId()));

            // when
            List<SellerOptionGroupJpaEntity> result =
                    repository().findByProductGroupId(productGroup1.getId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getProductGroupId()).isEqualTo(productGroup1.getId());
        }
    }

    // ========================================================================
    // 2. findValuesByGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findValuesByGroupIds")
    class FindValuesByGroupIdsTest {

        @Test
        @DisplayName("활성 SellerOptionValue를 groupIds로 배치 조회합니다")
        void findValuesByGroupIds_WithActiveValues_ReturnsEntities() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            SellerOptionGroupJpaEntity group =
                    persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));
            Long groupId = group.getId();

            persist(ProductGroupJpaEntityFixtures.activeOptionValueEntity(groupId));
            persist(ProductGroupJpaEntityFixtures.activeOptionValueEntity(groupId));

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(groupId));

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getSellerOptionGroupId().equals(groupId));
            assertThat(result).allMatch(e -> !e.isDeleted());
        }

        @Test
        @DisplayName("소프트 삭제된 SellerOptionValue는 조회에서 제외됩니다")
        void findValuesByGroupIds_WithDeletedValues_ExcludesDeleted() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            SellerOptionGroupJpaEntity group =
                    persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));
            Long groupId = group.getId();

            persist(ProductGroupJpaEntityFixtures.activeOptionValueEntity(groupId));
            persist(ProductGroupJpaEntityFixtures.deletedOptionValueEntity(groupId));

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(groupId));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).isDeleted()).isFalse();
        }

        @Test
        @DisplayName("빈 groupIds 목록으로 조회하면 빈 리스트를 반환합니다")
        void findValuesByGroupIds_WithEmptyGroupIds_ReturnsEmpty() {
            // when
            List<SellerOptionValueJpaEntity> result = repository().findValuesByGroupIds(List.of());

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("여러 groupId에 속한 SellerOptionValue를 배치 조회합니다")
        void findValuesByGroupIds_WithMultipleGroupIds_ReturnsAllValues() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            SellerOptionGroupJpaEntity group1 =
                    persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));
            SellerOptionGroupJpaEntity group2 =
                    persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));

            persist(ProductGroupJpaEntityFixtures.activeOptionValueEntity(group1.getId()));
            persist(ProductGroupJpaEntityFixtures.activeOptionValueEntity(group2.getId()));
            persist(ProductGroupJpaEntityFixtures.activeOptionValueEntity(group2.getId()));

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(group1.getId(), group2.getId()));

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("존재하지 않는 groupId로 조회하면 빈 리스트를 반환합니다")
        void findValuesByGroupIds_WithNonExistentGroupId_ReturnsEmpty() {
            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(999999L));

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("sortOrder 오름차순으로 정렬하여 반환합니다")
        void findValuesByGroupIds_ReturnsSortedBySortOrder() {
            // given
            ProductGroupJpaEntity productGroup = persistProductGroup();
            Long productGroupId = productGroup.getId();

            SellerOptionGroupJpaEntity group =
                    persist(ProductGroupJpaEntityFixtures.activeOptionGroupEntity(productGroupId));
            Long groupId = group.getId();

            SellerOptionValueJpaEntity value1 =
                    SellerOptionValueJpaEntity.create(null, groupId, "L", null, 2, false, null);
            SellerOptionValueJpaEntity value2 =
                    SellerOptionValueJpaEntity.create(null, groupId, "S", null, 0, false, null);
            SellerOptionValueJpaEntity value3 =
                    SellerOptionValueJpaEntity.create(null, groupId, "M", null, 1, false, null);

            persist(value1);
            persist(value2);
            persist(value3);

            // when
            List<SellerOptionValueJpaEntity> result =
                    repository().findValuesByGroupIds(List.of(groupId));

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getSortOrder())
                    .isLessThanOrEqualTo(result.get(1).getSortOrder());
            assertThat(result.get(1).getSortOrder())
                    .isLessThanOrEqualTo(result.get(2).getSortOrder());
        }
    }
}
