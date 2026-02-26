package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.SalesChannelJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.entity.SalesChannelJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.mapper.SalesChannelJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.saleschannel.repository.SalesChannelQueryDslRepository;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
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
 * SalesChannelQueryAdapterTest - 판매 채널 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelQueryAdapter 단위 테스트")
class SalesChannelQueryAdapterTest {

    @Mock private SalesChannelQueryDslRepository queryDslRepository;

    @Mock private SalesChannelJpaEntityMapper mapper;

    @Mock private SalesChannelSearchCriteria criteria;

    @InjectMocks private SalesChannelQueryAdapter queryAdapter;

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
            SalesChannelId salesChannelId = SalesChannelId.of(1L);
            SalesChannelJpaEntity entity = SalesChannelJpaEntityFixtures.activeEntity(1L);
            SalesChannel domain = SalesChannelFixtures.activeSalesChannel();

            given(queryDslRepository.findById(1L)).willReturn(Optional.of(entity));
            given(mapper.toDomain(entity)).willReturn(domain);

            // when
            Optional<SalesChannel> result = queryAdapter.findById(salesChannelId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(domain);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 빈 Optional을 반환합니다")
        void findById_WithNonExistingId_ReturnsEmpty() {
            // given
            SalesChannelId salesChannelId = SalesChannelId.of(999L);
            given(queryDslRepository.findById(999L)).willReturn(Optional.empty());

            // when
            Optional<SalesChannel> result = queryAdapter.findById(salesChannelId);

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
        @DisplayName("검색 조건으로 판매 채널 목록을 조회합니다")
        void findByCriteria_WithValidCriteria_ReturnsDomainList() {
            // given
            SalesChannelJpaEntity entity1 = SalesChannelJpaEntityFixtures.activeEntity(1L);
            SalesChannelJpaEntity entity2 = SalesChannelJpaEntityFixtures.activeEntity(2L);
            SalesChannel domain1 = SalesChannelFixtures.activeSalesChannel(1L);
            SalesChannel domain2 = SalesChannelFixtures.activeSalesChannel(2L);

            given(queryDslRepository.findByCriteria(criteria))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<SalesChannel> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(2);
            then(queryDslRepository).should().findByCriteria(criteria);
        }

        @Test
        @DisplayName("검색 결과가 없으면 빈 리스트를 반환합니다")
        void findByCriteria_WithNoResults_ReturnsEmptyList() {
            // given
            given(queryDslRepository.findByCriteria(criteria)).willReturn(List.of());

            // when
            List<SalesChannel> result = queryAdapter.findByCriteria(criteria);

            // then
            assertThat(result).isEmpty();
        }
    }

    // ========================================================================
    // 3. countByCriteria 테스트
    // ========================================================================

    @Nested
    @DisplayName("countByCriteria 메서드 테스트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 판매 채널 개수를 반환합니다")
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
    // 4. existsByChannelName 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByChannelName 메서드 테스트")
    class ExistsByChannelNameTest {

        @Test
        @DisplayName("존재하는 채널명으로 조회 시 true를 반환합니다")
        void existsByChannelName_WithExistingName_ReturnsTrue() {
            // given
            String channelName = "테스트 채널";
            given(queryDslRepository.existsByChannelName(channelName)).willReturn(true);

            // when
            boolean result = queryAdapter.existsByChannelName(channelName);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("존재하지 않는 채널명으로 조회 시 false를 반환합니다")
        void existsByChannelName_WithNonExistingName_ReturnsFalse() {
            // given
            String channelName = "존재하지 않는 채널";
            given(queryDslRepository.existsByChannelName(channelName)).willReturn(false);

            // when
            boolean result = queryAdapter.existsByChannelName(channelName);

            // then
            assertThat(result).isFalse();
        }
    }

    // ========================================================================
    // 5. existsByChannelNameExcluding 테스트
    // ========================================================================

    @Nested
    @DisplayName("existsByChannelNameExcluding 메서드 테스트")
    class ExistsByChannelNameExcludingTest {

        @Test
        @DisplayName("특정 ID 제외하고 채널명 존재 확인 시 true를 반환합니다")
        void existsByChannelNameExcluding_WithExistingName_ReturnsTrue() {
            // given
            String channelName = "테스트 채널";
            SalesChannelId excludeId = SalesChannelId.of(1L);
            given(queryDslRepository.existsByChannelNameExcluding(channelName, 1L))
                    .willReturn(true);

            // when
            boolean result = queryAdapter.existsByChannelNameExcluding(channelName, excludeId);

            // then
            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("특정 ID 제외하고 채널명이 없으면 false를 반환합니다")
        void existsByChannelNameExcluding_WithNonExistingName_ReturnsFalse() {
            // given
            String channelName = "고유한 채널명";
            SalesChannelId excludeId = SalesChannelId.of(1L);
            given(queryDslRepository.existsByChannelNameExcluding(channelName, 1L))
                    .willReturn(false);

            // when
            boolean result = queryAdapter.existsByChannelNameExcluding(channelName, excludeId);

            // then
            assertThat(result).isFalse();
        }
    }
}
