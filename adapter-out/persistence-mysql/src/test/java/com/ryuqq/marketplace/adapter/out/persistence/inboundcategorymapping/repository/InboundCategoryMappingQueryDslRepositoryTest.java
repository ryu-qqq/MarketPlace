package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.PersistenceMysqlTestApplication;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.condition.InboundCategoryMappingConditionBuilder;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchField;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSortKey;
import com.ryuqq.marketplace.domain.inboundcategorymapping.vo.InboundCategoryMappingStatus;
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
 * InboundCategoryMappingQueryDslRepositoryTest - InboundCategoryMapping QueryDslRepository 통합 테스트.
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
@DisplayName("InboundCategoryMappingQueryDslRepository 통합 테스트")
class InboundCategoryMappingQueryDslRepositoryTest {

    @Autowired private EntityManager entityManager;

    private InboundCategoryMappingQueryDslRepository repository() {
        return new InboundCategoryMappingQueryDslRepository(
                new JPAQueryFactory(entityManager), new InboundCategoryMappingConditionBuilder());
    }

    private InboundCategoryMappingJpaEntity persist(
            Long inboundSourceId,
            String externalCategoryCode,
            String externalCategoryName,
            Long internalCategoryId,
            String status) {
        Instant now = Instant.now();
        InboundCategoryMappingJpaEntity entity =
                InboundCategoryMappingJpaEntity.create(
                        null,
                        inboundSourceId,
                        externalCategoryCode,
                        externalCategoryName,
                        internalCategoryId,
                        status,
                        now,
                        now);
        entityManager.persist(entity);
        entityManager.flush();
        entityManager.clear();
        return entity;
    }

    private InboundCategoryMappingSearchCriteria criteriaOf(
            Long inboundSourceId,
            List<InboundCategoryMappingStatus> statuses,
            InboundCategoryMappingSearchField searchField,
            String searchWord) {
        com.ryuqq.marketplace.domain.common.vo.QueryContext<InboundCategoryMappingSortKey>
                queryContext =
                        com.ryuqq.marketplace.domain.common.vo.QueryContext.defaultOf(
                                InboundCategoryMappingSortKey.CREATED_AT);
        return InboundCategoryMappingSearchCriteria.of(
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
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            Long id =
                    entityManager
                            .createQuery(
                                    "SELECT e.id FROM InboundCategoryMappingJpaEntity e WHERE"
                                            + " e.externalCategoryCode = :code",
                                    Long.class)
                            .setParameter("code", "CAT001")
                            .getSingleResult();

            // when
            Optional<InboundCategoryMappingJpaEntity> result = repository().findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getExternalCategoryCode()).isEqualTo("CAT001");
            assertThat(result.get().getInboundSourceId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회하면 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // when
            Optional<InboundCategoryMappingJpaEntity> result = repository().findById(999L);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByInboundSourceIdAndExternalCategoryCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalCategoryCode 테스트")
    class FindByInboundSourceIdAndExternalCategoryCodeTest {

        @Test
        @DisplayName("소스ID와 카테고리코드가 일치하는 Entity를 반환합니다")
        void findByInboundSourceIdAndExternalCategoryCode_WithExisting_ReturnsEntity() {
            // given
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "ACTIVE");
            persist(2L, "CAT001", "카테고리C", 300L, "ACTIVE");

            // when
            Optional<InboundCategoryMappingJpaEntity> result =
                    repository().findByInboundSourceIdAndExternalCategoryCode(1L, "CAT001");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getInboundSourceId()).isEqualTo(1L);
            assertThat(result.get().getExternalCategoryCode()).isEqualTo("CAT001");
        }

        @Test
        @DisplayName("소스ID가 다른 경우 조회되지 않습니다")
        void findByInboundSourceIdAndExternalCategoryCode_WithDifferentSourceId_ReturnsEmpty() {
            // given
            persist(2L, "CAT001", "카테고리A", 100L, "ACTIVE");

            // when
            Optional<InboundCategoryMappingJpaEntity> result =
                    repository().findByInboundSourceIdAndExternalCategoryCode(1L, "CAT001");

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByInboundSourceIdAndExternalCategoryCodes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalCategoryCodes 테스트")
    class FindByInboundSourceIdAndExternalCategoryCodesTest {

        @Test
        @DisplayName("소스ID와 여러 카테고리코드로 조회하면 해당 Entity 목록을 반환합니다")
        void findByInboundSourceIdAndExternalCategoryCodes_WithMatchingCodes_ReturnsEntities() {
            // given
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "ACTIVE");
            persist(1L, "CAT003", "카테고리C", 300L, "ACTIVE");

            // when
            List<InboundCategoryMappingJpaEntity> result =
                    repository()
                            .findByInboundSourceIdAndExternalCategoryCodes(
                                    1L, List.of("CAT001", "CAT002"));

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(InboundCategoryMappingJpaEntity::getExternalCategoryCode)
                    .containsExactlyInAnyOrder("CAT001", "CAT002");
        }

        @Test
        @DisplayName("소스ID가 다른 경우 조회되지 않습니다")
        void findByInboundSourceIdAndExternalCategoryCodes_WithDifferentSourceId_ReturnsEmpty() {
            // given
            persist(2L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(2L, "CAT002", "카테고리B", 200L, "ACTIVE");

            // when
            List<InboundCategoryMappingJpaEntity> result =
                    repository()
                            .findByInboundSourceIdAndExternalCategoryCodes(
                                    1L, List.of("CAT001", "CAT002"));

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
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "INACTIVE");
            persist(2L, "CAT003", "카테고리C", 300L, "ACTIVE");

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByInboundSourceId(1L);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getInboundSourceId()).isEqualTo(1L));
        }

        @Test
        @DisplayName("해당 소스ID의 매핑이 없으면 빈 목록을 반환합니다")
        void findByInboundSourceId_WithNoMappings_ReturnsEmptyList() {
            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByInboundSourceId(999L);

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
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "ACTIVE");
            persist(2L, "CAT003", "카테고리C", 300L, "ACTIVE");

            InboundCategoryMappingSearchCriteria criteria = criteriaOf(1L, null, null, null);

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getInboundSourceId()).isEqualTo(1L));
        }

        @Test
        @DisplayName("상태 필터로 ACTIVE 매핑만 조회합니다")
        void findByCriteria_WithActiveStatusFilter_ReturnsOnlyActive() {
            // given
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "ACTIVE");
            persist(1L, "CAT_INA", "비활성카테고리", 300L, "INACTIVE");

            InboundCategoryMappingSearchCriteria criteria =
                    criteriaOf(1L, List.of(InboundCategoryMappingStatus.ACTIVE), null, null);

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).allSatisfy(e -> assertThat(e.getStatus()).isEqualTo("ACTIVE"));
        }

        @Test
        @DisplayName("EXTERNAL_CODE 검색어로 조회합니다")
        void findByCriteria_WithExternalCodeSearch_ReturnsMatching() {
            // given
            persist(1L, "SHOES001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "BAGS001", "카테고리B", 200L, "ACTIVE");
            persist(1L, "SHOES_SPORTS", "카테고리C", 300L, "ACTIVE");

            InboundCategoryMappingSearchCriteria criteria =
                    criteriaOf(
                            null, null, InboundCategoryMappingSearchField.EXTERNAL_CODE, "SHOES");

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result)
                    .extracting(InboundCategoryMappingJpaEntity::getExternalCategoryCode)
                    .containsExactlyInAnyOrder("SHOES001", "SHOES_SPORTS");
        }

        @Test
        @DisplayName("EXTERNAL_NAME 검색어로 조회합니다")
        void findByCriteria_WithExternalNameSearch_ReturnsMatching() {
            // given
            persist(1L, "CAT001", "신발류", 100L, "ACTIVE");
            persist(1L, "CAT002", "가방류", 200L, "ACTIVE");
            persist(1L, "CAT003", "신발 스포츠", 300L, "ACTIVE");

            InboundCategoryMappingSearchCriteria criteria =
                    criteriaOf(null, null, InboundCategoryMappingSearchField.EXTERNAL_NAME, "신발");

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("검색 필드 없이 searchWord 입력 시 코드+이름 통합 검색을 합니다")
        void findByCriteria_WithSearchWordOnly_SearchesAllFields() {
            // given
            persist(1L, "SHOES001", "가방류", 100L, "ACTIVE");
            persist(1L, "CAT002", "신발류", 200L, "ACTIVE");
            persist(1L, "CAT003", "의류", 300L, "ACTIVE");

            InboundCategoryMappingSearchCriteria criteria = criteriaOf(null, null, null, "SHOES");

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getExternalCategoryCode()).isEqualTo("SHOES001");
        }

        @Test
        @DisplayName("모든 필터 조건 없이 조회하면 전체 목록을 반환합니다")
        void findByCriteria_WithNoFilters_ReturnsAll() {
            // given
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(2L, "CAT002", "카테고리B", 200L, "INACTIVE");
            persist(3L, "CAT003", "카테고리C", 300L, "ACTIVE");

            InboundCategoryMappingSearchCriteria criteria = criteriaOf(null, null, null, null);

            // when
            List<InboundCategoryMappingJpaEntity> result = repository().findByCriteria(criteria);

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
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "ACTIVE");
            persist(2L, "CAT003", "카테고리C", 300L, "ACTIVE");

            InboundCategoryMappingSearchCriteria criteria = criteriaOf(1L, null, null, null);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }

        @Test
        @DisplayName("데이터가 없으면 0을 반환합니다")
        void countByCriteria_WithNoData_ReturnsZero() {
            // given
            InboundCategoryMappingSearchCriteria criteria = criteriaOf(999L, null, null, null);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isZero();
        }

        @Test
        @DisplayName("상태 필터로 카운트합니다")
        void countByCriteria_WithStatusFilter_ReturnsFilteredCount() {
            // given
            persist(1L, "CAT001", "카테고리A", 100L, "ACTIVE");
            persist(1L, "CAT002", "카테고리B", 200L, "ACTIVE");
            persist(1L, "CAT_INA", "비활성카테고리", 300L, "INACTIVE");

            InboundCategoryMappingSearchCriteria criteria =
                    criteriaOf(null, List.of(InboundCategoryMappingStatus.ACTIVE), null, null);

            // when
            long count = repository().countByCriteria(criteria);

            // then
            assertThat(count).isEqualTo(2L);
        }
    }
}
