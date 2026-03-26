package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementEntryJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper.SettlementEntryJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementEntryJpaRepository;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SettlementEntryCommandAdapter 단위 테스트.
 *
 * <p>PER-ADP-003: CommandAdapter는 JpaRepository만 사용.
 *
 * <p>PER-ADP-006: Mapper를 통해 Domain -> Entity 변환 후 저장.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SettlementEntryCommandAdapter 단위 테스트")
class SettlementEntryCommandAdapterTest {

    @Mock private SettlementEntryJpaRepository repository;
    @Mock private SettlementEntryJpaEntityMapper mapper;

    @InjectMocks private SettlementEntryCommandAdapter commandAdapter;

    // ========================================================================
    // persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("Mapper를 통해 Entity로 변환한 후 repository.save를 호출합니다")
        void persist_CallsMapperAndRepository() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity(entry.idValue());

            given(mapper.toEntity(entry)).willReturn(entity);

            // when
            commandAdapter.persist(entry);

            // then
            then(mapper).should().toEntity(entry);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("SALES Entry를 저장합니다")
        void persist_WithSalesEntry_SavesSuccessfully() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity(entry.idValue());

            given(mapper.toEntity(entry)).willReturn(entity);

            // when
            commandAdapter.persist(entry);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("CANCEL 역분개 Entry를 저장합니다")
        void persist_WithCancelReversalEntry_SavesSuccessfully() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.cancelReversalEntry();
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.cancelReversalEntity();

            given(mapper.toEntity(entry)).willReturn(entity);

            // when
            commandAdapter.persist(entry);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번만 호출됩니다")
        void persist_CallsMapperExactlyOnce() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity(entry.idValue());

            given(mapper.toEntity(entry)).willReturn(entity);

            // when
            commandAdapter.persist(entry);

            // then
            then(mapper).should(times(1)).toEntity(entry);
            then(mapper).shouldHaveNoMoreInteractions();
        }
    }

    // ========================================================================
    // persistAll 테스트
    // ========================================================================

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 Entry를 한 번의 saveAll로 저장합니다")
        void persistAll_WithMultipleEntries_SavesAllInOneCall() {
            // given
            SettlementEntry entry1 = SettlementEntryFixtures.salesEntry();
            SettlementEntry entry2 = SettlementEntryFixtures.cancelReversalEntry();
            List<SettlementEntry> entries = List.of(entry1, entry2);

            SettlementEntryJpaEntity entity1 =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity("entity-id-001");
            SettlementEntryJpaEntity entity2 =
                    SettlementEntryJpaEntityFixtures.cancelReversalEntity();

            given(mapper.toEntity(entry1)).willReturn(entity1);
            given(mapper.toEntity(entry2)).willReturn(entity2);

            // when
            commandAdapter.persistAll(entries);

            // then
            then(repository).should(times(1)).saveAll(anyList());
        }

        @Test
        @DisplayName("Entry 수만큼 Mapper가 호출됩니다")
        void persistAll_WithMultipleEntries_CallsMapperForEachEntry() {
            // given
            SettlementEntry entry1 = SettlementEntryFixtures.salesEntry();
            SettlementEntry entry2 = SettlementEntryFixtures.cancelReversalEntry();
            List<SettlementEntry> entries = List.of(entry1, entry2);

            given(mapper.toEntity(any(SettlementEntry.class)))
                    .willReturn(SettlementEntryJpaEntityFixtures.salesPendingEntity("id"));

            // when
            commandAdapter.persistAll(entries);

            // then
            then(mapper).should(times(2)).toEntity(any(SettlementEntry.class));
        }

        @Test
        @DisplayName("빈 리스트를 전달해도 repository.saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAllWithEmptyList() {
            // when
            commandAdapter.persistAll(List.of());

            // then
            then(repository).should().saveAll(List.of());
            then(mapper).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("단건 Entry도 persistAll로 저장할 수 있습니다")
        void persistAll_WithSingleEntry_SavesSuccessfully() {
            // given
            SettlementEntry entry = SettlementEntryFixtures.salesEntry();
            SettlementEntryJpaEntity entity =
                    SettlementEntryJpaEntityFixtures.salesPendingEntity(entry.idValue());

            given(mapper.toEntity(entry)).willReturn(entity);

            // when
            commandAdapter.persistAll(List.of(entry));

            // then
            then(repository).should().saveAll(List.of(entity));
        }
    }
}
