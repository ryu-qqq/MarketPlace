package com.ryuqq.marketplace.adapter.out.persistence.inboundsource.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.InboundSourceJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.entity.InboundSourceJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.mapper.InboundSourceJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundsource.repository.InboundSourceQueryDslRepository;
import com.ryuqq.marketplace.domain.inboundsource.aggregate.InboundSource;
import com.ryuqq.marketplace.domain.inboundsource.id.InboundSourceId;
import com.ryuqq.marketplace.domain.inboundsource.query.InboundSourceSearchCriteria;
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
 * InboundSourceQueryAdapterTest - InboundSource Query Adapter 단위 테스트.
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
@DisplayName("InboundSourceQueryAdapter 단위 테스트")
class InboundSourceQueryAdapterTest {

    @Mock private InboundSourceQueryDslRepository repository;

    @Mock private InboundSourceJpaEntityMapper mapper;

    @Mock private InboundSourceSearchCriteria criteria;

    @InjectMocks private InboundSourceQueryAdapter queryAdapter;

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
            InboundSourceId id = InboundSourceId.of(1L);
            InboundSourceJpaEntity entity = InboundSourceJpaEntityFixtures.activeEntity();
            InboundSource domain = createInboundSourceDomain(1L, "SETOF", "세토프 레거시");

            given(repository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundSource> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            InboundSourceId id = InboundSourceId.of(999L);
            given(repository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<InboundSource> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCode 메서드 테스트")
    class FindByCodeTest {

        @Test
        @DisplayName("존재하는 코드로 조회 시 Domain을 반환합니다")
        void findByCode_WithExistingCode_ReturnsDomain() {
            // given
            String code = "SETOF";
            InboundSourceJpaEntity entity = InboundSourceJpaEntityFixtures.activeEntity();
            InboundSource domain = createInboundSourceDomain(1L, code, "세토프 레거시");

            given(repository.findByCode(code)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundSource> result = queryAdapter.findByCode(code);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 코드로 조회 시 빈 Optional을 반환합니다")
        void findByCode_WithNonExistingCode_ReturnsEmpty() {
            // given
            String code = "NONEXISTENT";
            given(repository.findByCode(code)).willReturn(Optional.empty());

            // when
            Optional<InboundSource> result = queryAdapter.findByCode(code);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            InboundSourceJpaEntity entity1 = InboundSourceJpaEntityFixtures.activeEntity(1L);
            InboundSourceJpaEntity entity2 = InboundSourceJpaEntityFixtures.activeEntity(2L);
            InboundSource domain1 = createInboundSourceDomain(1L, "SETOF_1", "소스1");
            InboundSource domain2 = createInboundSourceDomain(2L, "SETOF_2", "소스2");

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundSource> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            then(repository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(repository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<InboundSource> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("검색 조건으로 개수를 반환합니다")
        void countByCriteria_WithValidCriteria_ReturnsCount() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(5L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(5L);
        }

        @Test
        @DisplayName("검색 결과가 없으면 0을 반환합니다")
        void countByCriteria_WithNoResults_ReturnsZero() {
            // given
            given(repository.countByCriteria(criteria)).willReturn(0L);

            // when
            long result = queryAdapter.countByCriteria(criteria);

            // then
            assertThat(result).isZero();
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private InboundSource createInboundSourceDomain(Long id, String code, String name) {
        java.time.Instant now = java.time.Instant.now();
        return InboundSource.reconstitute(
                InboundSourceId.of(id),
                com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceCode.of(code),
                name,
                com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceType.LEGACY,
                com.ryuqq.marketplace.domain.inboundsource.vo.InboundSourceStatus.ACTIVE,
                null,
                now,
                now);
    }
}
