package com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.InboundCategoryMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.entity.InboundCategoryMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.mapper.InboundCategoryMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundcategorymapping.repository.InboundCategoryMappingQueryDslRepository;
import com.ryuqq.marketplace.domain.inboundcategorymapping.InboundCategoryMappingFixtures;
import com.ryuqq.marketplace.domain.inboundcategorymapping.aggregate.InboundCategoryMapping;
import com.ryuqq.marketplace.domain.inboundcategorymapping.id.InboundCategoryMappingId;
import com.ryuqq.marketplace.domain.inboundcategorymapping.query.InboundCategoryMappingSearchCriteria;
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
 * InboundCategoryMappingQueryAdapterTest - InboundCategoryMapping Query Adapter 단위 테스트.
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
@DisplayName("InboundCategoryMappingQueryAdapter 단위 테스트")
class InboundCategoryMappingQueryAdapterTest {

    @Mock private InboundCategoryMappingQueryDslRepository repository;

    @Mock private InboundCategoryMappingJpaEntityMapper mapper;

    @Mock private InboundCategoryMappingSearchCriteria criteria;

    @InjectMocks private InboundCategoryMappingQueryAdapter queryAdapter;

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
            InboundCategoryMappingId id = InboundCategoryMappingId.of(1L);
            InboundCategoryMappingJpaEntity entity =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(1L);
            InboundCategoryMapping domain = InboundCategoryMappingFixtures.activeMapping(1L);

            given(repository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundCategoryMapping> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(repository).should().findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            InboundCategoryMappingId id = InboundCategoryMappingId.of(999L);
            given(repository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<InboundCategoryMapping> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByInboundSourceIdAndExternalCategoryCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalCategoryCode 메서드 테스트")
    class FindByInboundSourceIdAndExternalCategoryCodeTest {

        @Test
        @DisplayName("존재하는 소스ID와 카테고리코드로 조회 시 Domain을 반환합니다")
        void findByInboundSourceIdAndExternalCategoryCode_WithExisting_ReturnsDomain() {
            // given
            Long inboundSourceId = 1L;
            String externalCategoryCode = "CAT001";
            InboundCategoryMappingJpaEntity entity =
                    InboundCategoryMappingJpaEntityFixtures.activeEntityWithCode(
                            inboundSourceId, externalCategoryCode);
            InboundCategoryMapping domain = InboundCategoryMappingFixtures.activeMapping();

            given(
                            repository.findByInboundSourceIdAndExternalCategoryCode(
                                    inboundSourceId, externalCategoryCode))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundCategoryMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalCategoryCode(
                            inboundSourceId, externalCategoryCode);

            // then
            assertThat(result).isPresent();
            then(repository)
                    .should()
                    .findByInboundSourceIdAndExternalCategoryCode(
                            inboundSourceId, externalCategoryCode);
        }

        @Test
        @DisplayName("존재하지 않는 소스ID와 카테고리코드로 조회 시 빈 Optional을 반환합니다")
        void findByInboundSourceIdAndExternalCategoryCode_WithNonExisting_ReturnsEmpty() {
            // given
            Long inboundSourceId = 999L;
            String externalCategoryCode = "NONEXISTENT";
            given(
                            repository.findByInboundSourceIdAndExternalCategoryCode(
                                    inboundSourceId, externalCategoryCode))
                    .willReturn(Optional.empty());

            // when
            Optional<InboundCategoryMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalCategoryCode(
                            inboundSourceId, externalCategoryCode);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByInboundSourceIdAndExternalCategoryCodes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalCategoryCodes 메서드 테스트")
    class FindByInboundSourceIdAndExternalCategoryCodesTest {

        @Test
        @DisplayName("여러 카테고리코드로 조회 시 Domain 목록을 반환합니다")
        void findByInboundSourceIdAndExternalCategoryCodes_WithMultipleCodes_ReturnsDomainList() {
            // given
            Long inboundSourceId = 1L;
            List<String> codes = List.of("CAT001", "CAT002");
            InboundCategoryMappingJpaEntity entity1 =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(1L);
            InboundCategoryMappingJpaEntity entity2 =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(2L);
            InboundCategoryMapping domain1 = InboundCategoryMappingFixtures.activeMapping(1L);
            InboundCategoryMapping domain2 = InboundCategoryMappingFixtures.activeMapping(2L);

            given(repository.findByInboundSourceIdAndExternalCategoryCodes(inboundSourceId, codes))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundCategoryMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalCategoryCodes(
                            inboundSourceId, codes);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("코드 목록이 비어있으면 빈 리스트를 반환합니다")
        void findByInboundSourceIdAndExternalCategoryCodes_WithEmptyCodes_ReturnsEmptyList() {
            // given
            Long inboundSourceId = 1L;
            List<String> emptyCodes = List.of();
            given(
                            repository.findByInboundSourceIdAndExternalCategoryCodes(
                                    inboundSourceId, emptyCodes))
                    .willReturn(List.of());

            // when
            List<InboundCategoryMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalCategoryCodes(
                            inboundSourceId, emptyCodes);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 4. findByInboundSourceId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceId 메서드 테스트")
    class FindByInboundSourceIdTest {

        @Test
        @DisplayName("소스ID로 조회 시 해당 Domain 목록을 반환합니다")
        void findByInboundSourceId_WithExistingSourceId_ReturnsDomainList() {
            // given
            Long inboundSourceId = 1L;
            InboundCategoryMappingJpaEntity entity1 =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(1L);
            InboundCategoryMappingJpaEntity entity2 =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(2L);
            InboundCategoryMapping domain1 = InboundCategoryMappingFixtures.activeMapping(1L);
            InboundCategoryMapping domain2 = InboundCategoryMappingFixtures.activeMapping(2L);

            given(repository.findByInboundSourceId(inboundSourceId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundCategoryMapping> result =
                    queryAdapter.findByInboundSourceId(inboundSourceId);

            // then
            assertThat(result).hasSize(2);
            then(repository).should().findByInboundSourceId(inboundSourceId);
        }

        @Test
        @DisplayName("소스ID에 해당하는 매핑이 없으면 빈 리스트를 반환합니다")
        void findByInboundSourceId_WithNoMappings_ReturnsEmptyList() {
            // given
            Long inboundSourceId = 999L;
            given(repository.findByInboundSourceId(inboundSourceId)).willReturn(List.of());

            // when
            List<InboundCategoryMapping> result =
                    queryAdapter.findByInboundSourceId(inboundSourceId);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 5. findByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCriteria 메서드 테스트")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 매핑 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            InboundCategoryMappingJpaEntity entity1 =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(1L);
            InboundCategoryMappingJpaEntity entity2 =
                    InboundCategoryMappingJpaEntityFixtures.activeEntity(2L);
            InboundCategoryMapping domain1 = InboundCategoryMappingFixtures.activeMapping(1L);
            InboundCategoryMapping domain2 = InboundCategoryMappingFixtures.activeMapping(2L);

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundCategoryMapping> result = queryAdapter.findByCriteria(criteria);

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
            List<InboundCategoryMapping> result = queryAdapter.findByCriteria(criteria);

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
        @DisplayName("검색 조건으로 매핑 개수를 반환합니다")
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
}
