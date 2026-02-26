package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.condition.InboundBrandMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchField;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSortKey;
import com.ryuqq.marketplace.domain.inboundbrandmapping.vo.InboundBrandMappingStatus;
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
 * InboundBrandMappingQueryDslRepositoryTest - InboundBrandMapping QueryDslRepository 통합 테스트.
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
@DisplayName("InboundBrandMappingQueryDslRepository 통합 테스트")
class InboundBrandMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private InboundBrandMappingQueryDslRepository repository() {
        return new InboundBrandMappingQueryDslRepository(
                new JPAQueryFactory(entityManager), new InboundBrandMappingConditionBuilder());
    }

    private InboundBrandMappingJpaEntity persist(
            Long inboundSourceId,
            String externalBrandCode,
            String externalBrandName,
            Long internalBrandId,
            String status) {
        Instant now = Instant.now();
        InboundBrandMappingJpaEntity entity =
                InboundBrandMappingJpaEntity.create(
                        null,
                        inboundSourceId,
                        externalBrandCode,
                        externalBrandName,
                        internalBrandId,
                        status,
                        now,
                        now);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private InboundBrandMappingSearchCriteria criteriaOf(
            Long inboundSourceId,
            List<InboundBrandMappingStatus> statuses,
            InboundBrandMappingSearchField searchField,
            String searchWord) {
        com.ryuqq.marketplace.domain.common.vo.QueryContext<InboundBrandMappingSortKey>
                queryContext =
                        com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                InboundBrandMappingSortKey.CREATED_AT);
        return InboundBrandMappingSearchCriteria.of(
                inboundSourceId, statuses, searchField, searchWord, queryContext);
    }

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회하면 Entity를 반환합니다")
        void findById_WithExistingId_ReturnsEntity() {
            // given
            InboundBrandMappingJpaEntity saved = persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            Long id =
                    entityManager
                            .createQuery(
                                    "SELECT e.id FROM InboundBrandMappingJpaEntity e WHERE"
                                            + " e.externalBrandCode = :code",
                                    Long.class)
                            .setParameter("code", "BR001")
                            .getSingleResult();

            // when
            Optional<InboundBrandMappingJpaEntity> result = repository().findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getExternalBrandCode()).isEqualTo("BR001");
            assertThat(result.get().getInboundSourceId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<InboundBrandMappingJpaEntity> result = repository().findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByInboundSourceIdAndExternalBrandCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalBrandCode 테스트")
    class FindByInboundSourceIdAndExternalBrandCodeTest {

        @Test
        @DisplayName("소스ID와 브랜드코드가 일치하는 Entity를 반환합니다")
        void findByInboundSourceIdAndExternalBrandCode_WithExisting_ReturnsEntity() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "ACTIVE");
            persist(2L, "BR001", "브랜드C", 300L, "ACTIVE");

            // when
            Optional<InboundBrandMappingJpaEntity> result =
                    repository().findByInboundSourceIdAndExternalBrandCode(1L, "BR001");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getInboundSourceId()).isEqualTo(1L);
            assertThat(result.get().getExternalBrandCode()).isEqualTo("BR001");
        }

        @Test
        @DisplayName("소스ID가 다른 경우 조회되지 않습니다")
        void findByInboundSourceIdAndExternalBrandCode_WithDifferentSourceId_ReturnsEmpty() {
            // given
            persist(2L, "BR001", "브랜드A", 100L, "ACTIVE");

            // when
            Optional<InboundBrandMappingJpaEntity> result =
                    repository().findByInboundSourceIdAndExternalBrandCode(1L, "BR001");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByInboundSourceIdAndExternalBrandCodes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalBrandCodes 테스트")
    class FindByInboundSourceIdAndExternalBrandCodesTest {

        @Test
        @DisplayName("소스ID와 여러 브랜드코드로 조회하면 해당 Entity 목록을 반환합니다")
        void findByInboundSourceIdAndExternalBrandCodes_WithMatchingCodes_ReturnsEntities() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "ACTIVE");
            persist(1L, "BR003", "브랜드C", 300L, "ACTIVE");

            // when
            List<InboundBrandMappingJpaEntity> result =
                    repository()
                            .findByInboundSourceIdAndExternalBrandCodes(
                                    1L, List.of("BR001", "BR002"));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(InboundBrandMappingJpaEntity::getExternalBrandCode)
                    .containsExactlyInAnyOrder("BR001", "BR002");
        }

        @Test
        @DisplayName("소스ID가 다른 경우 조회되지 않습니다")
        void findByInboundSourceIdAndExternalBrandCodes_WithDifferentSourceId_ReturnsEmpty() {
            // given
            persist(2L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(2L, "BR002", "브랜드B", 200L, "ACTIVE");

            // when
            List<InboundBrandMappingJpaEntity> result =
                    repository()
                            .findByInboundSourceIdAndExternalBrandCodes(
                                    1L, List.of("BR001", "BR002"));

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findByInboundSourceId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceId 테스트")
    class FindByInboundSourceIdTest {

        @Test
        @DisplayName("소스ID로 조회하면 해당 소스의 모든 매핑을 반환합니다")
        void findByInboundSourceId_WithExistingSourceId_ReturnsAllMappings() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "INACTIVE");
            persist(2L, "BR003", "브랜드C", 300L, "ACTIVE");

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByInboundSourceId(1L);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getInboundSourceId()).isEqualTo(1L));
        }

        @Test
        @DisplayName("해당 소스ID의 매핑이 없으면 빈 목록을 반환합니다")
        void findByInboundSourceId_WithNoMappings_ReturnsEmptyList() {
            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByInboundSourceId(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("inboundSourceId 필터로 조회합니다")
        void findByCriteria_WithSourceIdFilter_ReturnsFiltered() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "ACTIVE");
            persist(2L, "BR003", "브랜드C", 300L, "ACTIVE");

            InboundBrandMappingSearchCriteria criteria = criteriaOf(1L, null, null, null);

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getInboundSourceId()).isEqualTo(1L));
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE 매핑만 조회합니다")
        void findByCriteria_WithActiveStatusFilter_ReturnsOnlyActive() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "ACTIVE");
            persist(1L, "BR_INA", "비활성브랜드", 300L, "INACTIVE");

            InboundBrandMappingSearchCriteria criteria =
                    criteriaOf(1L, List.of(InboundBrandMappingStatus.ACTIVE), null, null);

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getStatus()).isEqualTo("ACTIVE"));
        }

        @Test
        @DisplayName("EXTERNAL_CODE 검색어로 조회합니다")
        void findByCriteria_WithExternalCodeSearch_ReturnsMatching() {
            // given
            persist(1L, "NIKE001", "나이키", 100L, "ACTIVE");
            persist(1L, "ADIDAS001", "아디다스", 200L, "ACTIVE");
            persist(1L, "NIKELAB", "나이키랩", 300L, "ACTIVE");

            InboundBrandMappingSearchCriteria criteria =
                    criteriaOf(null, null, InboundBrandMappingSearchField.EXTERNAL_CODE, "NIKE");

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(InboundBrandMappingJpaEntity::getExternalBrandCode)
                    .containsExactlyInAnyOrder("NIKE001", "NIKELAB");
        }

        @Test
        @DisplayName("EXTERNAL_NAME 검색어로 조회합니다")
        void findByCriteria_WithExternalNameSearch_ReturnsMatching() {
            // given
            persist(1L, "BR001", "나이키", 100L, "ACTIVE");
            persist(1L, "BR002", "아디다스", 200L, "ACTIVE");
            persist(1L, "BR003", "나이키랩", 300L, "ACTIVE");

            InboundBrandMappingSearchCriteria criteria =
                    criteriaOf(null, null, InboundBrandMappingSearchField.EXTERNAL_NAME, "나이키");

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("검색 필드 없이 searchWord 입력 시 코드+이름 통합 검색을 합니다")
        void findByCriteria_WithSearchWordOnly_SearchesAllFields() {
            // given
            persist(1L, "NIKE001", "아디다스", 100L, "ACTIVE");
            persist(1L, "BR002", "나이키", 200L, "ACTIVE");
            persist(1L, "BR003", "퓨마", 300L, "ACTIVE");

            InboundBrandMappingSearchCriteria criteria = criteriaOf(null, null, null, "NIKE");

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getExternalBrandCode()).isEqualTo("NIKE001");
        }

        @Test
        @DisplayName("모든 필터 조건 없이 조회하면 전체 목록을 반환합니다")
        void findByCriteria_WithNoFilters_ReturnsAll() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(2L, "BR002", "브랜드B", 200L, "INACTIVE");
            persist(3L, "BR003", "브랜드C", 300L, "ACTIVE");

            InboundBrandMappingSearchCriteria criteria = criteriaOf(null, null, null, null);

            // when
            List<InboundBrandMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(3);
        }
    }

    // ========================================================================
    // 6. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("필터 조건에 맞는 개수를 반환합니다")
        void countByCriteria_WithSourceIdFilter_ReturnsCount() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "ACTIVE");
            persist(2L, "BR003", "브랜드C", 300L, "ACTIVE");

            InboundBrandMappingSearchCriteria criteria = criteriaOf(1L, null, null, null);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            InboundBrandMappingSearchCriteria criteria = criteriaOf(999L, null, null, null);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("상태 필터로 카운트합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            // given
            persist(1L, "BR001", "브랜드A", 100L, "ACTIVE");
            persist(1L, "BR002", "브랜드B", 200L, "ACTIVE");
            persist(1L, "BR_INA", "비활성브랜드", 300L, "INACTIVE");

            InboundBrandMappingSearchCriteria criteria =
                    criteriaOf(null, List.of(InboundBrandMappingStatus.ACTIVE), null, null);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }
    }
}
