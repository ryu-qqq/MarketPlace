package com.ryuqq.marketplace.adapter.out.persistence.claimhistory.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.ClaimHistoryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.entity.ClaimHistoryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.mapper.ClaimHistoryPersistenceMapper;
import com.ryuqq.marketplace.adapter.out.persistence.claimhistory.repository.ClaimHistoryJpaRepository;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ClaimHistoryCommandAdapterTest - 클레임 이력 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-005: Domain -> Entity 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimHistoryCommandAdapter 단위 테스트")
class ClaimHistoryCommandAdapterTest {

    @Mock private ClaimHistoryJpaRepository claimHistoryRepository;

    @Mock private ClaimHistoryPersistenceMapper mapper;

    @InjectMocks private ClaimHistoryCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상태 변경 이력을 저장합니다")
        void persist_WithStatusChangeHistory_Saves() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.statusChangeClaimHistory();
            ClaimHistoryJpaEntity entity = ClaimHistoryJpaEntityFixtures.defaultEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(claimHistoryRepository).should().save(entity);
        }

        @Test
        @DisplayName("수기 메모 이력을 저장합니다")
        void persist_WithManualHistory_Saves() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.manualClaimHistory();
            ClaimHistoryJpaEntity entity =
                    ClaimHistoryJpaEntityFixtures.manualMemoEntity("refund-001");

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should().toEntity(domain);
            then(claimHistoryRepository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번 호출됩니다")
        void persist_CallsMapperOnce() {
            // given
            ClaimHistory domain = ClaimHistoryFixtures.cancelStatusChangeHistory();
            ClaimHistoryJpaEntity entity = ClaimHistoryJpaEntityFixtures.defaultEntity();

            given(mapper.toEntity(domain)).willReturn(entity);

            // when
            commandAdapter.persist(domain);

            // then
            then(mapper).should(times(1)).toEntity(domain);
        }
    }

    // ========================================================================
    // 2. persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 이력을 일괄 저장합니다")
        void persistAll_WithMultipleHistories_SavesAll() {
            // given
            ClaimHistory domain1 = ClaimHistoryFixtures.cancelStatusChangeHistory();
            ClaimHistory domain2 = ClaimHistoryFixtures.refundStatusChangeHistory();
            List<ClaimHistory> domains = List.of(domain1, domain2);

            ClaimHistoryJpaEntity entity1 =
                    ClaimHistoryJpaEntityFixtures.cancelStatusChangeEntity("cancel-001");
            ClaimHistoryJpaEntity entity2 =
                    ClaimHistoryJpaEntityFixtures.refundStatusChangeEntity("refund-001");

            given(mapper.toEntity(domain1)).willReturn(entity1);
            given(mapper.toEntity(domain2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(domains);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ClaimHistoryJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(claimHistoryRepository).should().saveAll(captor.capture());

            List<ClaimHistoryJpaEntity> savedEntities = captor.getValue();
            org.assertj.core.api.Assertions.assertThat(savedEntities).hasSize(2);
            org.assertj.core.api.Assertions.assertThat(savedEntities).contains(entity1, entity2);
        }

        @Test
        @DisplayName("빈 리스트를 저장해도 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            List<ClaimHistory> emptyList = List.of();

            // when
            commandAdapter.persistAll(emptyList);

            // then
            @SuppressWarnings("unchecked")
            ArgumentCaptor<List<ClaimHistoryJpaEntity>> captor =
                    ArgumentCaptor.forClass(List.class);
            then(claimHistoryRepository).should().saveAll(captor.capture());
            org.assertj.core.api.Assertions.assertThat(captor.getValue()).isEmpty();
        }

        @Test
        @DisplayName("각 Domain에 대해 Mapper가 호출됩니다")
        void persistAll_CallsMapperForEachDomain() {
            // given
            ClaimHistory domain1 = ClaimHistoryFixtures.cancelStatusChangeHistory();
            ClaimHistory domain2 = ClaimHistoryFixtures.refundStatusChangeHistory();
            ClaimHistory domain3 = ClaimHistoryFixtures.exchangeStatusChangeHistory();
            List<ClaimHistory> domains = List.of(domain1, domain2, domain3);

            ClaimHistoryJpaEntity entity = ClaimHistoryJpaEntityFixtures.defaultEntity();
            given(mapper.toEntity(domain1)).willReturn(entity);
            given(mapper.toEntity(domain2)).willReturn(entity);
            given(mapper.toEntity(domain3)).willReturn(entity);

            // when
            commandAdapter.persistAll(domains);

            // then
            then(mapper)
                    .should(times(3))
                    .toEntity(org.mockito.ArgumentMatchers.any(ClaimHistory.class));
        }
    }
}
