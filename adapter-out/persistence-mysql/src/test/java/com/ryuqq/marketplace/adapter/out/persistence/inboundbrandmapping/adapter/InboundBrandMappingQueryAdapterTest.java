package com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.InboundBrandMappingJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.entity.InboundBrandMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.mapper.InboundBrandMappingJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.inboundbrandmapping.repository.InboundBrandMappingQueryDslRepository;
import com.ryuqq.marketplace.domain.inboundbrandmapping.InboundBrandMappingFixtures;
import com.ryuqq.marketplace.domain.inboundbrandmapping.aggregate.InboundBrandMapping;
import com.ryuqq.marketplace.domain.inboundbrandmapping.id.InboundBrandMappingId;
import com.ryuqq.marketplace.domain.inboundbrandmapping.query.InboundBrandMappingSearchCriteria;
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
 * InboundBrandMappingQueryAdapterTest - InboundBrandMapping Query Adapter 단위 테스트.
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
@DisplayName("InboundBrandMappingQueryAdapter 단위 테스트")
class InboundBrandMappingQueryAdapterTest {

    @Mock private InboundBrandMappingQueryDslRepository repository;

    @Mock private InboundBrandMappingJpaEntityMapper mapper;

    @Mock private InboundBrandMappingSearchCriteria criteria;

    @InjectMocks private InboundBrandMappingQueryAdapter queryAdapter;

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
            InboundBrandMappingId id = InboundBrandMappingId.of(1L);
            InboundBrandMappingJpaEntity entity =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);
            InboundBrandMapping domain = InboundBrandMappingFixtures.activeMapping(1L);

            given(repository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundBrandMapping> result = queryAdapter.findById(id);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
            then(repository).should().findById(1L);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            InboundBrandMappingId id = InboundBrandMappingId.of(999L);
            given(repository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<InboundBrandMapping> result = queryAdapter.findById(id);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 2. findByInboundSourceIdAndExternalBrandCode 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalBrandCode 메서드 테스트")
    class FindByInboundSourceIdAndExternalBrandCodeTest {

        @Test
        @DisplayName("존재하는 소스ID와 브랜드코드로 조회 시 Domain을 반환합니다")
        void findByInboundSourceIdAndExternalBrandCode_WithExisting_ReturnsDomain() {
            // given
            Long inboundSourceId = 1L;
            String externalBrandCode = "BR001";
            InboundBrandMappingJpaEntity entity =
                    InboundBrandMappingJpaEntityFixtures.activeEntityWithCode(
                            inboundSourceId, externalBrandCode);
            InboundBrandMapping domain = InboundBrandMappingFixtures.activeMapping();

            given(
                            repository.findByInboundSourceIdAndExternalBrandCode(
                                    inboundSourceId, externalBrandCode))
                    .willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<InboundBrandMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalBrandCode(
                            inboundSourceId, externalBrandCode);

            // then
            assertThat(result).isPresent();
            then(repository)
                    .should()
                    .findByInboundSourceIdAndExternalBrandCode(inboundSourceId, externalBrandCode);
        }

        @Test
        @DisplayName("존재하지 않는 소스ID와 브랜드코드로 조회 시 빈 Optional을 반환합니다")
        void findByInboundSourceIdAndExternalBrandCode_WithNonExisting_ReturnsEmpty() {
            // given
            Long inboundSourceId = 999L;
            String externalBrandCode = "NONEXISTENT";
            given(
                            repository.findByInboundSourceIdAndExternalBrandCode(
                                    inboundSourceId, externalBrandCode))
                    .willReturn(Optional.empty());

            // when
            Optional<InboundBrandMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalBrandCode(
                            inboundSourceId, externalBrandCode);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. findByInboundSourceIdAndExternalBrandCodes 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByInboundSourceIdAndExternalBrandCodes 메서드 테스트")
    class FindByInboundSourceIdAndExternalBrandCodesTest {

        @Test
        @DisplayName("여러 브랜드코드로 조회 시 Domain 목록을 반환합니다")
        void findByInboundSourceIdAndExternalBrandCodes_WithMultipleCodes_ReturnsDomainList() {
            // given
            Long inboundSourceId = 1L;
            List<String> codes = List.of("BR001", "BR002");
            InboundBrandMappingJpaEntity entity1 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);
            InboundBrandMappingJpaEntity entity2 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(2L);
            InboundBrandMapping domain1 = InboundBrandMappingFixtures.activeMapping(1L);
            InboundBrandMapping domain2 = InboundBrandMappingFixtures.activeMapping(2L);

            given(repository.findByInboundSourceIdAndExternalBrandCodes(inboundSourceId, codes))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundBrandMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalBrandCodes(inboundSourceId, codes);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
        }

        @Test
        @DisplayName("코드 목록이 비어있으면 빈 리스트를 반환합니다")
        void findByInboundSourceIdAndExternalBrandCodes_WithEmptyCodes_ReturnsEmptyList() {
            // given
            Long inboundSourceId = 1L;
            List<String> emptyCodes = List.of();
            given(
                            repository.findByInboundSourceIdAndExternalBrandCodes(
                                    inboundSourceId, emptyCodes))
                    .willReturn(List.of());

            // when
            List<InboundBrandMapping> result =
                    queryAdapter.findByInboundSourceIdAndExternalBrandCodes(
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
            InboundBrandMappingJpaEntity entity1 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);
            InboundBrandMappingJpaEntity entity2 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(2L);
            InboundBrandMapping domain1 = InboundBrandMappingFixtures.activeMapping(1L);
            InboundBrandMapping domain2 = InboundBrandMappingFixtures.activeMapping(2L);

            given(repository.findByInboundSourceId(inboundSourceId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundBrandMapping> result = queryAdapter.findByInboundSourceId(inboundSourceId);

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
            List<InboundBrandMapping> result = queryAdapter.findByInboundSourceId(inboundSourceId);

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
            InboundBrandMappingJpaEntity entity1 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(1L);
            InboundBrandMappingJpaEntity entity2 =
                    InboundBrandMappingJpaEntityFixtures.activeEntity(2L);
            InboundBrandMapping domain1 = InboundBrandMappingFixtures.activeMapping(1L);
            InboundBrandMapping domain2 = InboundBrandMappingFixtures.activeMapping(2L);

            given(repository.findByCriteria(criteria)).willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<InboundBrandMapping> result = queryAdapter.findByCriteria(criteria);

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
            List<InboundBrandMapping> result = queryAdapter.findByCriteria(criteria);

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
