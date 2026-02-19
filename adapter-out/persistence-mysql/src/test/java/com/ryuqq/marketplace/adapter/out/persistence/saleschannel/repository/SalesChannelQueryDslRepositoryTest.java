package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.condition.SalesChannelConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import jakarta.persistence.EntityManager;
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
 * SalesChannelQueryDslRepositoryTest - SalesChannel QueryDslRepository 통합 테스트.
 *
 * <p>SalesChannel은 soft-delete를 적용하지 않으므로 status 필터 동작을 검증합니다.
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
@DisplayName("SalesChannelQueryDslRepository 통합 테스트")
class SalesChannelQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private SalesChannelQueryDslRepository repository() {
        return new SalesChannelQueryDslRepository(
                new JPAQueryFactory(entityManager), new SalesChannelConditionBuilder());
    }

    private SalesChannelJpaEntity persist(SalesChannelJpaEntity entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 Entity를 ID로 조회합니다")
        void findById_WithExistingId_ReturnsEntity() {
            SalesChannelJpaEntity saved = persist(SalesChannelJpaEntityFixtures.newEntity());

            Optional<SalesChannelJpaEntity> result = repository().findById(saved.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(saved.getId());
        }

        @Test
        @DisplayName("존재하지 않는 ID는 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            Optional<SalesChannelJpaEntity> result = repository().findById(999L);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("비활성 Entity도 ID로 조회됩니다")
        void findById_WithInactiveEntity_ReturnsEntity() {
            SalesChannelJpaEntity inactive =
                    persist(SalesChannelJpaEntityFixtures.inactiveEntity());

            Optional<SalesChannelJpaEntity> result = repository().findById(inactive.getId());

            assertThat(result).isPresent();
            assertThat(result.get().getStatus()).isEqualTo("INACTIVE");
        }
    }

    @Nested
    @DisplayName("existsByChannelName")
    class ExistsByChannelNameTest {

        @Test
        @DisplayName("존재하는 채널명은 true를 반환합니다")
        void existsByChannelName_WithExistingName_ReturnsTrue() {
            // given
            String channelName = "유니크채널명테스트";
            persist(SalesChannelJpaEntityFixtures.activeEntityWithName(channelName));

            // when
            boolean result = repository().existsByChannelName(channelName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 채널명은 false를 반환합니다")
        void existsByChannelName_WithNonExistentName_ReturnsFalse() {
            boolean result = repository().existsByChannelName("존재하지않는채널");

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("비활성 채널명도 존재 확인됩니다")
        void existsByChannelName_WithInactiveChannel_ReturnsTrue() {
            // given
            String channelName = "비활성채널명";
            persist(SalesChannelJpaEntityFixtures.inactiveEntityWithName(channelName));

            // when
            boolean result = repository().existsByChannelName(channelName);

            // then
            assertThat(result).isTrue();
        }
    }

    @Nested
    @DisplayName("existsByChannelNameExcluding")
    class ExistsByChannelNameExcludingTest {

        @Test
        @DisplayName("제외 ID가 아닌 채널명이 존재하면 true를 반환합니다")
        void existsByChannelNameExcluding_WithDifferentId_ReturnsTrue() {
            // given
            String channelName = "중복채널명테스트";
            persist(SalesChannelJpaEntityFixtures.activeEntityWithName(channelName));

            // when
            boolean result = repository().existsByChannelNameExcluding(channelName, 999L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("제외 ID의 채널명은 false를 반환합니다")
        void existsByChannelNameExcluding_WithExcludedId_ReturnsFalse() {
            // given
            String channelName = "제외채널명";
            SalesChannelJpaEntity existing =
                    persist(SalesChannelJpaEntityFixtures.activeEntityWithName(channelName));

            // when
            boolean result =
                    repository().existsByChannelNameExcluding(channelName, existing.getId());

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("findByCriteria")
    class FindByCriteriaTest {

        @Test
        @DisplayName("기본 조건으로 Entity 목록을 조회합니다")
        void findByCriteria_WithDefaultCriteria_ReturnsEntities() {
            // given
            SalesChannelJpaEntity channel1 = persist(SalesChannelJpaEntityFixtures.newEntity());
            SalesChannelJpaEntity channel2 = persist(SalesChannelJpaEntityFixtures.newEntity());

            // when
            List<SalesChannelJpaEntity> result =
                    repository().findByCriteria(SalesChannelSearchCriteria.defaultCriteria());

            // then
            assertThat(result)
                    .extracting(SalesChannelJpaEntity::getId)
                    .contains(channel1.getId(), channel2.getId());
        }

        @Test
        @DisplayName("빈 목록인 경우 0건을 반환합니다")
        void findByCriteria_WithNoEntities_ReturnsEmpty() {
            List<SalesChannelJpaEntity> result =
                    repository().findByCriteria(SalesChannelSearchCriteria.defaultCriteria());

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("countByCriteria")
    class CountByCriteriaTest {

        @Test
        @DisplayName("전체 Entity 개수를 반환합니다")
        void countByCriteria_ReturnsCorrectCount() {
            // given
            persist(SalesChannelJpaEntityFixtures.newEntity());
            persist(SalesChannelJpaEntityFixtures.newEntity());

            // when
            long count = repository().countByCriteria(SalesChannelSearchCriteria.defaultCriteria());

            // then
            assertThat(count).isGreaterThanOrEqualTo(2);
        }

        @Test
        @DisplayName("Entity가 없으면 0을 반환합니다")
        void countByCriteria_WithNoEntities_ReturnsZero() {
            long count = repository().countByCriteria(SalesChannelSearchCriteria.defaultCriteria());

            assertThat(count).isZero();
        }
    }
}
