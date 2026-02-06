package com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.CommonCodeTypeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.entity.CommonCodeTypeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.mapper.CommonCodeTypeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.commoncodetype.repository.CommonCodeTypeQueryDslRepository;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncodetype.CommonCodeTypeFixtures;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSortKey;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CommonCodeTypeQueryAdapterTest - 공통 코드 타입 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CommonCodeTypeQueryAdapter 단위 테스트")
class CommonCodeTypeQueryAdapterTest {

    @Mock private CommonCodeTypeQueryDslRepository queryDslRepository;

    @Mock private CommonCodeTypeJpaEntityMapper mapper;

    @InjectMocks private CommonCodeTypeQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 공통 코드 타입을 조회합니다")
        void findById_WithValidId_ReturnsCommonCodeType() {
            // given
            CommonCodeTypeId id = CommonCodeTypeId.of(1L);
            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.activeEntity();
            CommonCodeType domain = CommonCodeTypeFixtures.activeCommonCodeType();

            given(queryDslRepository.findById(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<CommonCodeType> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(queryDslRepository).should().findById(id.value());
            then(mapper).should().toDomain(entity);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 결과를 반환합니다")
        void findById_WithNonExistentId_ReturnsEmpty() {
            // given
            CommonCodeTypeId id = CommonCodeTypeId.of(999L);

            given(queryDslRepository.findById(id.value())).willReturn(Optional.empty());

            // when
            Optional<CommonCodeType> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByIds 메서드 테스트")
    class FindByIdsTest {

        @Test
        @DisplayName("ID 목록으로 공통 코드 타입 목록을 조회합니다")
        void findByIds_WithValidIds_ReturnsCommonCodeTypes() {
            // given
            List<CommonCodeTypeId> ids = List.of(CommonCodeTypeId.of(1L), CommonCodeTypeId.of(2L));
            List<Long> idValues = List.of(1L, 2L);

            CommonCodeTypeJpaEntity entity1 = CommonCodeTypeJpaEntityFixtures.activeEntity(1L);
            CommonCodeTypeJpaEntity entity2 = CommonCodeTypeJpaEntityFixtures.activeEntity(2L);
            List<CommonCodeTypeJpaEntity> entities = List.of(entity1, entity2);

            CommonCodeType domain1 = CommonCodeTypeFixtures.activeCommonCodeType(1L);
            CommonCodeType domain2 = CommonCodeTypeFixtures.activeCommonCodeType(2L);

            given(queryDslRepository.findByIds(idValues)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<CommonCodeType> result = queryAdapter.findByIds(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 결과를 반환합니다")
        void findByIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<CommonCodeTypeId> ids = Collections.emptyList();

            given(queryDslRepository.findByIds(Collections.emptyList()))
                    .willReturn(Collections.emptyList());

            // when
            List<CommonCodeType> result = queryAdapter.findByIds(ids);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. existsByCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByCode 메서드 테스트")
    class ExistsByCodeTest {

        @Test
        @DisplayName("코드 존재 여부를 확인합니다 - 존재하는 경우")
        void existsByCode_WhenExists_ReturnsTrue() {
            // given
            String code = "PAYMENT_METHOD";

            given(queryDslRepository.existsByCode(code)).willReturn(true);

            // when
            boolean result = queryAdapter.existsByCode(code);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("코드 존재 여부를 확인합니다 - 존재하지 않는 경우")
        void existsByCode_WhenNotExists_ReturnsFalse() {
            // given
            String code = "NON_EXISTENT";

            given(queryDslRepository.existsByCode(code)).willReturn(false);

            // when
            boolean result = queryAdapter.existsByCode(code);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 4. existsByDisplayOrderExcludingId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByDisplayOrderExcludingId 메서드 테스트")
    class ExistsByDisplayOrderExcludingIdTest {

        @Test
        @DisplayName("표시 순서 존재 여부를 확인합니다 (ID 제외) - 존재하는 경우")
        void existsByDisplayOrderExcludingId_WhenExists_ReturnsTrue() {
            // given
            int displayOrder = 1;
            Long excludeId = 2L;

            given(queryDslRepository.existsByDisplayOrderExcludingId(displayOrder, excludeId))
                    .willReturn(true);

            // when
            boolean result = queryAdapter.existsByDisplayOrderExcludingId(displayOrder, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("표시 순서 존재 여부를 확인합니다 (ID 제외) - 존재하지 않는 경우")
        void existsByDisplayOrderExcludingId_WhenNotExists_ReturnsFalse() {
            // given
            int displayOrder = 999;
            Long excludeId = 1L;

            given(queryDslRepository.existsByDisplayOrderExcludingId(displayOrder, excludeId))
                    .willReturn(false);

            // when
            boolean result = queryAdapter.existsByDisplayOrderExcludingId(displayOrder, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 5. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 공통 코드 타입 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsCommonCodeTypes() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(true, null, null, null, queryContext);

            CommonCodeTypeJpaEntity entity = CommonCodeTypeJpaEntityFixtures.activeEntity();
            CommonCodeType domain = CommonCodeTypeFixtures.activeCommonCodeType();

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<CommonCodeType> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(null, null, "NON_EXISTENT", null, queryContext);

            given(queryDslRepository.findByCriteria(criteria)).willReturn(Collections.emptyList());

            // when
            List<CommonCodeType> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 6. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 공통 코드 타입 개수를 조회합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(true, null, null, null, queryContext);

            given(queryDslRepository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            QueryContext<CommonCodeTypeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeTypeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeTypeSearchCriteria criteria =
                    CommonCodeTypeSearchCriteria.of(null, null, "NON_EXISTENT", null, queryContext);

            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
