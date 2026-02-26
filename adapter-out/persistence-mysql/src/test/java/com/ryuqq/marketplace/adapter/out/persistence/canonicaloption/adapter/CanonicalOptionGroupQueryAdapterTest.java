package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionGroupJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionValueJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper.CanonicalOptionGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository.CanonicalOptionGroupQueryDslRepository;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionGroupName;
import java.time.Instant;
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
 * CanonicalOptionGroupQueryAdapterTest - 캐노니컬 옵션 그룹 Query Adapter 단위 테스트.
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
@DisplayName("CanonicalOptionGroupQueryAdapter 단위 테스트")
class CanonicalOptionGroupQueryAdapterTest {

    @Mock private CanonicalOptionGroupQueryDslRepository queryDslRepository;

    @Mock private CanonicalOptionGroupJpaEntityMapper mapper;

    @Mock private CanonicalOptionGroupSearchCriteria criteria;

    @InjectMocks private CanonicalOptionGroupQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findById 테스트
    // ========================================================================

    @Nested
    @DisplayName("findById 메서드 테스트")
    class FindByIdTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 Domain을 반환합니다")
        void findById_WithExistingId_ReturnsDomain() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(1L);
            CanonicalOptionGroupJpaEntity entity =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity();
            List<CanonicalOptionValueJpaEntity> values = List.of();
            CanonicalOptionGroup domain = createDomain(1L, "COLOR", "색상", "Color", true);

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(queryDslRepository.findValuesByGroupId(entity.getId())).willReturn(values);
            given(mapper.toDomain(entity, values)).willReturn(domain);

            // when
            Optional<CanonicalOptionGroup> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            CanonicalOptionGroupId id = CanonicalOptionGroupId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<CanonicalOptionGroup> result = queryAdapter.findById(id);

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
        @DisplayName("여러 ID로 조회 시 Domain 리스트를 반환합니다")
        void findByIds_WithExistingIds_ReturnsDomainList() {
            // given
            CanonicalOptionGroupId id1 = CanonicalOptionGroupId.of(1L);
            CanonicalOptionGroupId id2 = CanonicalOptionGroupId.of(2L);
            CanonicalOptionGroupJpaEntity entity1 =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity(1L);
            CanonicalOptionGroupJpaEntity entity2 =
                    CanonicalOptionGroupJpaEntityFixtures.sizeEntity();
            CanonicalOptionGroup domain1 = createDomain(1L, "COLOR", "색상", "Color", true);
            CanonicalOptionGroup domain2 = createDomain(2L, "SIZE", "사이즈", "Size", true);

            CanonicalOptionValueJpaEntity value1 =
                    CanonicalOptionValueJpaEntityFixtures.colorRedEntity();
            CanonicalOptionValueJpaEntity value2 =
                    CanonicalOptionValueJpaEntityFixtures.sizeSmallEntity();

            given(queryDslRepository.findByIds(List.of(1L, 2L)))
                    .willReturn(List.of(entity1, entity2));
            given(
                            queryDslRepository.findValuesByGroupIds(
                                    List.of(entity1.getId(), entity2.getId())))
                    .willReturn(List.of(value1, value2));
            given(mapper.toDomain(entity1, List.of(value1))).willReturn(domain1);
            given(mapper.toDomain(entity2, List.of(value2))).willReturn(domain2);

            // when
            List<CanonicalOptionGroup> result = queryAdapter.findByIds(List.of(id1, id2));

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("빈 ID 리스트로 조회 시 빈 리스트를 반환합니다 (groups가 빈 경우)")
        void findByIds_WithEmptyIds_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByIds(List.of())).willReturn(List.of());

            // when
            List<CanonicalOptionGroup> result = queryAdapter.findByIds(List.of());

            // then
            assertThat(result).isEmpty();
            then(queryDslRepository).should().findByIds(List.of());
            then(queryDslRepository).shouldHaveNoMoreInteractions();
        }

        @Test
        @DisplayName("values가 없는 그룹도 정상적으로 반환합니다")
        void findByIds_WithNoValues_ReturnsDomainWithEmptyValues() {
            // given
            CanonicalOptionGroupId id1 = CanonicalOptionGroupId.of(1L);
            CanonicalOptionGroupJpaEntity entity1 =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity(1L);
            CanonicalOptionGroup domain1 = createDomain(1L, "COLOR", "색상", "Color", true);

            given(queryDslRepository.findByIds(List.of(1L))).willReturn(List.of(entity1));
            given(queryDslRepository.findValuesByGroupIds(List.of(entity1.getId())))
                    .willReturn(List.of());
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);

            // when
            List<CanonicalOptionGroup> result = queryAdapter.findByIds(List.of(id1));

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst()).isEqualTo(domain1);
        }
    }

    // ========================================================================
    // 3. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 옵션 그룹 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            CanonicalOptionGroupJpaEntity entity1 =
                    CanonicalOptionGroupJpaEntityFixtures.activeEntity(1L);
            CanonicalOptionGroupJpaEntity entity2 =
                    CanonicalOptionGroupJpaEntityFixtures.sizeEntity();
            CanonicalOptionGroup domain1 = createDomain(1L, "COLOR", "색상", "Color", true);
            CanonicalOptionGroup domain2 = createDomain(2L, "SIZE", "사이즈", "Size", true);

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(
                            queryDslRepository.findValuesByGroupIds(
                                    List.of(entity1.getId(), entity2.getId())))
                    .willReturn(List.of());
            given(mapper.toDomain(entity1, List.of())).willReturn(domain1);
            given(mapper.toDomain(entity2, List.of())).willReturn(domain2);

            // when
            List<CanonicalOptionGroup> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<CanonicalOptionGroup> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 옵션 그룹 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
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
            given(queryDslRepository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private CanonicalOptionGroup createDomain(
            Long id, String code, String nameKo, String nameEn, boolean active) {
        Instant now = Instant.now();
        return CanonicalOptionGroup.reconstitute(
                CanonicalOptionGroupId.of(id),
                CanonicalOptionGroupCode.of(code),
                CanonicalOptionGroupName.of(nameKo, nameEn),
                active,
                List.of(),
                now,
                now);
    }
}
