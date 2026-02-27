package com.ryuqq.marketplace.adapter.out.persistence.commoncode.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.commoncode.CommonCodeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.entity.CommonCodeJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.mapper.CommonCodeJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.commoncode.repository.CommonCodeQueryDslRepository;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.commoncode.CommonCodeFixtures;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSortKey;
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
 * CommonCodeQueryAdapterTest - 공통 코드 Query Adapter 단위 테스트.
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
@DisplayName("CommonCodeQueryAdapter 단위 테스트")
class CommonCodeQueryAdapterTest {

    @Mock private CommonCodeQueryDslRepository queryDslRepository;

    @Mock private CommonCodeJpaEntityMapper mapper;

    @InjectMocks private CommonCodeQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("ID로 공통 코드를 조회합니다")
        void findById_WithValidId_ReturnsCommonCode() {
            // given
            CommonCodeId id = CommonCodeId.of(1L);
            CommonCodeJpaEntity entity = CommonCodeJpaEntityFixtures.activeEntity();
            CommonCode domain = CommonCodeFixtures.activeCommonCode();

            given(queryDslRepository.findById(id.value())).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<CommonCode> result = queryAdapter.findById(id);

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
            CommonCodeId id = CommonCodeId.of(999L);

            given(queryDslRepository.findById(id.value())).willReturn(Optional.empty());

            // when
            Optional<CommonCode> result = queryAdapter.findById(id);

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
        @DisplayName("ID 목록으로 공통 코드 목록을 조회합니다")
        void findByIds_WithValidIds_ReturnsCommonCodes() {
            // given
            List<CommonCodeId> ids = List.of(CommonCodeId.of(1L), CommonCodeId.of(2L));
            List<Long> idValues = List.of(1L, 2L);

            CommonCodeJpaEntity entity1 = CommonCodeJpaEntityFixtures.activeEntity(1L);
            CommonCodeJpaEntity entity2 = CommonCodeJpaEntityFixtures.activeEntity(2L);
            List<CommonCodeJpaEntity> entities = List.of(entity1, entity2);

            CommonCode domain1 = CommonCodeFixtures.activeCommonCode(1L);
            CommonCode domain2 = CommonCodeFixtures.activeCommonCode(2L);

            given(queryDslRepository.findByIds(idValues)).willReturn(entities);
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<CommonCode> result = queryAdapter.findByIds(ids);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("빈 ID 목록으로 조회 시 빈 결과를 반환합니다")
        void findByIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            List<CommonCodeId> ids = Collections.emptyList();

            given(queryDslRepository.findByIds(Collections.emptyList()))
                    .willReturn(Collections.emptyList());

            // when
            List<CommonCode> result = queryAdapter.findByIds(ids);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. existsByCommonCodeTypeIdAndCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByCommonCodeTypeIdAndCode 메서드 테스트")
    class ExistsByCommonCodeTypeIdAndCodeTest {

        @Test
        @DisplayName("타입 ID와 코드로 존재 여부를 확인합니다 - 존재하는 경우")
        void existsByCommonCodeTypeIdAndCode_WhenExists_ReturnsTrue() {
            // given
            Long commonCodeTypeId = 1L;
            String code = "CREDIT_CARD";

            given(queryDslRepository.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code))
                    .willReturn(true);

            // when
            boolean result = queryAdapter.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("타입 ID와 코드로 존재 여부를 확인합니다 - 존재하지 않는 경우")
        void existsByCommonCodeTypeIdAndCode_WhenNotExists_ReturnsFalse() {
            // given
            Long commonCodeTypeId = 1L;
            String code = "NON_EXISTENT";

            given(queryDslRepository.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code))
                    .willReturn(false);

            // when
            boolean result = queryAdapter.existsByCommonCodeTypeIdAndCode(commonCodeTypeId, code);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 4. existsActiveByCommonCodeTypeId 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsActiveByCommonCodeTypeId 메서드 테스트")
    class ExistsActiveByCommonCodeTypeIdTest {

        @Test
        @DisplayName("타입 ID로 활성 공통 코드 존재 여부를 확인합니다 - 존재하는 경우")
        void existsActiveByCommonCodeTypeId_WhenExists_ReturnsTrue() {
            // given
            Long commonCodeTypeId = 1L;

            given(queryDslRepository.existsActiveByCommonCodeTypeId(commonCodeTypeId))
                    .willReturn(true);

            // when
            boolean result = queryAdapter.existsActiveByCommonCodeTypeId(commonCodeTypeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("타입 ID로 활성 공통 코드 존재 여부를 확인합니다 - 존재하지 않는 경우")
        void existsActiveByCommonCodeTypeId_WhenNotExists_ReturnsFalse() {
            // given
            Long commonCodeTypeId = 999L;

            given(queryDslRepository.existsActiveByCommonCodeTypeId(commonCodeTypeId))
                    .willReturn(false);

            // when
            boolean result = queryAdapter.existsActiveByCommonCodeTypeId(commonCodeTypeId);

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
        @DisplayName("검색 조건으로 공통 코드 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsCommonCodes() {
            // given
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of("PAYMENT_METHOD", true, queryContext);

            CommonCodeJpaEntity entity = CommonCodeJpaEntityFixtures.activeEntity();
            CommonCode domain = CommonCodeFixtures.activeCommonCode();

            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            List<CommonCode> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(domain);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 목록을 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of("NON_EXISTING_TYPE", null, queryContext);

            given(queryDslRepository.findByCriteria(criteria)).willReturn(Collections.emptyList());

            // when
            List<CommonCode> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("검색 조건으로 공통 코드 개수를 조회합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of("PAYMENT_METHOD", true, queryContext);

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
            QueryContext<CommonCodeSortKey> queryContext =
                    QueryContext.of(
                            CommonCodeSortKey.DISPLAY_ORDER,
                            SortDirection.ASC,
                            PageRequest.of(0, 20));
            CommonCodeSearchCriteria criteria =
                    CommonCodeSearchCriteria.of("NON_EXISTING_TYPE", null, queryContext);

            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }
}
