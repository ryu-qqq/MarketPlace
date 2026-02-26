package com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.PersistenceMysqlLegacyTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.LegacyCommonCodeEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.commoncode.entity.LegacyCommonCodeEntity;
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
 * LegacyCommonCodeQueryDslRepositoryTest - 레거시 공통 코드 QueryDslRepository 통합 테스트.
 *
 * <p>deleteYn = 'N' 필터 적용 여부를 우선 검증합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("integration")
@DataJpaTest
@ContextConfiguration(classes = PersistenceMysqlLegacyTestApplication.class)
@TestPropertySource(
        properties = {
            "spring.flyway.enabled=false",
            "spring.jpa.hibernate.ddl-auto=create-drop",
            "spring.jpa.show-sql=false",
            "persistence.legacy.enabled=false"
        })
@DisplayName("LegacyCommonCodeQueryDslRepository 통합 테스트")
class LegacyCommonCodeQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private LegacyCommonCodeQueryDslRepository repository() {
        return new LegacyCommonCodeQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private LegacyCommonCodeEntity persist(LegacyCommonCodeEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    // ========================================================================
    // 1. findByCodeGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCodeGroupId 메서드 테스트")
    class FindByCodeGroupIdTest {

        @Test
        @DisplayName("코드 그룹 ID로 미삭제 Entity 목록을 조회합니다")
        void findByCodeGroupId_WithActiveEntities_ReturnsEntities() {
            // given
            Long codeGroupId = 100L;
            persist(LegacyCommonCodeEntityFixtures.newEntityWithGroupId(codeGroupId));
            persist(LegacyCommonCodeEntityFixtures.newEntityWithGroupId(codeGroupId));

            // when
            List<LegacyCommonCodeEntity> result = repository().findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allMatch(e -> e.getCodeGroupId().equals(codeGroupId));
        }

        @Test
        @DisplayName("삭제된 Entity(DELETE_YN = 'Y')는 조회에서 제외됩니다")
        void findByCodeGroupId_WithDeletedEntity_ExcludesDeleted() {
            // given
            Long codeGroupId = 200L;
            persist(LegacyCommonCodeEntityFixtures.newEntityWithGroupId(codeGroupId));
            persist(LegacyCommonCodeEntityFixtures.newDeletedEntity());

            // when
            List<LegacyCommonCodeEntity> result = repository().findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getDeleteYn()).isEqualTo("N");
        }

        @Test
        @DisplayName("해당 그룹 ID의 Entity가 없으면 빈 목록을 반환합니다")
        void findByCodeGroupId_WithNonExistentGroupId_ReturnsEmptyList() {
            // given
            Long codeGroupId = 999L;

            // when
            List<LegacyCommonCodeEntity> result = repository().findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("다른 그룹 ID의 Entity는 조회에서 제외됩니다")
        void findByCodeGroupId_WithDifferentGroupId_ExcludesOtherGroup() {
            // given
            Long targetGroupId = 300L;
            Long otherGroupId = 301L;
            persist(LegacyCommonCodeEntityFixtures.newEntityWithGroupId(targetGroupId));
            persist(LegacyCommonCodeEntityFixtures.newEntityWithGroupId(otherGroupId));

            // when
            List<LegacyCommonCodeEntity> result = repository().findByCodeGroupId(targetGroupId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getCodeGroupId()).isEqualTo(targetGroupId);
        }

        @Test
        @DisplayName("표시 순서(DISPLAY_ORDER) 오름차순으로 정렬됩니다")
        void findByCodeGroupId_ReturnsSortedByDisplayOrderAsc() {
            // given
            Long codeGroupId = 400L;
            persist(LegacyCommonCodeEntity.create(null, codeGroupId, "CODE_B", "코드B", 3, "N"));
            persist(LegacyCommonCodeEntity.create(null, codeGroupId, "CODE_A", "코드A", 1, "N"));
            persist(LegacyCommonCodeEntity.create(null, codeGroupId, "CODE_C", "코드C", 2, "N"));

            // when
            List<LegacyCommonCodeEntity> result = repository().findByCodeGroupId(codeGroupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getDisplayOrder()).isEqualTo(1);
            assertThat(result.get(1).getDisplayOrder()).isEqualTo(2);
            assertThat(result.get(2).getDisplayOrder()).isEqualTo(3);
        }
    }
}
