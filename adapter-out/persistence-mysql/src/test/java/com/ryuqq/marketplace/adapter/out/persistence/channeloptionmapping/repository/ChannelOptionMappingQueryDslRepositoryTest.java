package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSortKey;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
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
 * ChannelOptionMappingQueryDslRepositoryTest - ChannelOptionMapping QueryDslRepository 통합 테스트.
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
@DisplayName("ChannelOptionMappingQueryDslRepository 통합 테스트")
class ChannelOptionMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private ChannelOptionMappingQueryDslRepository repository() {
        return new ChannelOptionMappingQueryDslRepository(new JPAQueryFactory(entityManager));
    }

    private <T> T persist(T entity) {
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private ChannelOptionMappingJpaEntity persistMapping(
            Long salesChannelId, Long canonicalOptionValueId, String externalOptionCode) {
        Instant now = Instant.now();
        return persist(
                ChannelOptionMappingJpaEntity.create(
                        null,
                        salesChannelId,
                        canonicalOptionValueId,
                        externalOptionCode,
                        now,
                        now));
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Entity를 반환합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            ChannelOptionMappingJpaEntity saved = persistMapping(1L, 100L, "EXT-OPTION-001");

            // when
            Optional<ChannelOptionMappingJpaEntity> result = repository().findById(saved.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getSalesChannelId()).isEqualTo(1L);
            assertThat(result.get().getCanonicalOptionValueId()).isEqualTo(100L);
            assertThat(result.get().getExternalOptionCode()).isEqualTo("EXT-OPTION-001");
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // when
            Optional<ChannelOptionMappingJpaEntity> result = repository().findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("salesChannelId 필터로 해당 채널의 매핑만 조회합니다")
        void findByCriteria_WithSalesChannelFilter_ReturnsFilteredResult() {
            // given
            persistMapping(1L, 100L, "EXT-001");
            persistMapping(1L, 200L, "EXT-002");
            persistMapping(2L, 100L, "EXT-003");

            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.of(
                            1L,
                            null,
                            QueryContext.defaultOf(ChannelOptionMappingSortKey.defaultKey()));

            // when
            List<ChannelOptionMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getSalesChannelId()).isEqualTo(1L));
        }

        @Test
        @DisplayName("필터 없이 조회 시 전체 매핑을 반환합니다")
        void findByCriteria_WithNoFilter_ReturnsAll() {
            // given
            persistMapping(1L, 100L, "EXT-001");
            persistMapping(2L, 200L, "EXT-002");
            persistMapping(3L, 300L, "EXT-003");

            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();

            // when
            List<ChannelOptionMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(3);
        }

        @Test
        @DisplayName("존재하지 않는 salesChannelId로 조회 시 빈 목록을 반환합니다")
        void findByCriteria_WithNonExistentSalesChannelId_ReturnsEmpty() {
            // given
            persistMapping(1L, 100L, "EXT-001");

            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.of(
                            999L,
                            null,
                            QueryContext.defaultOf(ChannelOptionMappingSortKey.defaultKey()));

            // when
            List<ChannelOptionMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("페이징 크기 제한이 적용됩니다")
        void findByCriteria_WithSizeLimit_ReturnsLimitedResult() {
            // given
            persistMapping(1L, 101L, "EXT-001");
            persistMapping(1L, 102L, "EXT-002");
            persistMapping(1L, 103L, "EXT-003");

            // page=0, size=2 -> 첫 페이지에서 2건 반환
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.of(
                            1L,
                            null,
                            QueryContext.of(
                                    ChannelOptionMappingSortKey.defaultKey(),
                                    SortDirection.defaultDirection(),
                                    PageRequest.of(0, 2)));

            // when
            List<ChannelOptionMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("salesChannelId 필터로 개수를 집계합니다")
        void countByCriteria_WithSalesChannelFilter_ReturnsCount() {
            // given
            persistMapping(1L, 100L, "EXT-001");
            persistMapping(1L, 200L, "EXT-002");
            persistMapping(2L, 100L, "EXT-003");

            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.of(
                            1L,
                            null,
                            QueryContext.defaultOf(ChannelOptionMappingSortKey.defaultKey()));

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("필터 없이 전체 개수를 반환합니다")
        void countByCriteria_WithNoFilter_ReturnsTotalCount() {
            // given
            persistMapping(1L, 100L, "EXT-001");
            persistMapping(2L, 200L, "EXT-002");

            ChannelOptionMappingSearchCriteria criteria =
                    ChannelOptionMappingSearchCriteria.defaultCriteria();

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }
    }

    // ========================================================================
    // 4. existsBySalesChannelIdAndCanonicalOptionValueId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsBySalesChannelIdAndCanonicalOptionValueId 메서드 테스트")
    class ExistsBySalesChannelIdAndCanonicalOptionValueIdTest {

        @Test
        @DisplayName("salesChannelId와 canonicalOptionValueId 조합이 존재하면 true를 반환합니다")
        void exists_WhenCombinationExists_ReturnsTrue() {
            // given
            persistMapping(1L, 100L, "EXT-001");

            // when
            boolean result = repository().existsBySalesChannelIdAndCanonicalOptionValueId(1L, 100L);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("salesChannelId와 canonicalOptionValueId 조합이 없으면 false를 반환합니다")
        void exists_WhenCombinationNotExists_ReturnsFalse() {
            // given
            persistMapping(1L, 100L, "EXT-001");

            // when
            boolean result = repository().existsBySalesChannelIdAndCanonicalOptionValueId(1L, 999L);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("salesChannelId가 다르면 false를 반환합니다")
        void exists_WhenSalesChannelIdDiffers_ReturnsFalse() {
            // given
            persistMapping(1L, 100L, "EXT-001");

            // when
            boolean result = repository().existsBySalesChannelIdAndCanonicalOptionValueId(2L, 100L);

            // then
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("데이터가 없으면 false를 반환합니다")
        void exists_WithNoData_ReturnsFalse() {
            // when
            boolean result = repository().existsBySalesChannelIdAndCanonicalOptionValueId(1L, 100L);

            // then
            assertThat(result).isFalse();
        }
    }
}
