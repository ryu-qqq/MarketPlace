package com.ryuqq.marketplace.adapter.out.persistence.settlement.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.settlement.SettlementJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.entity.SettlementJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.mapper.SettlementJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.settlement.repository.SettlementJpaRepository;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SettlementCommandAdapter 단위 테스트.
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
@DisplayName("SettlementCommandAdapter 단위 테스트")
class SettlementCommandAdapterTest {

    @Mock private SettlementJpaRepository repository;
    @Mock private SettlementJpaEntityMapper mapper;

    @InjectMocks private SettlementCommandAdapter commandAdapter;

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
            Settlement settlement = SettlementFixtures.calculatingSettlement();
            SettlementJpaEntity entity =
                    SettlementJpaEntityFixtures.calculatingEntity(settlement.idValue());

            given(mapper.toEntity(settlement)).willReturn(entity);

            // when
            commandAdapter.persist(settlement);

            // then
            then(mapper).should().toEntity(settlement);
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("CALCULATING 상태 Settlement를 저장합니다")
        void persist_WithCalculatingSettlement_SavesSuccessfully() {
            // given
            Settlement settlement = SettlementFixtures.calculatingSettlement();
            SettlementJpaEntity entity =
                    SettlementJpaEntityFixtures.calculatingEntity(settlement.idValue());

            given(mapper.toEntity(settlement)).willReturn(entity);

            // when
            commandAdapter.persist(settlement);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("HOLD 상태 Settlement를 저장합니다")
        void persist_WithHeldSettlement_SavesSuccessfully() {
            // given
            Settlement settlement = SettlementFixtures.heldSettlement();
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.holdEntity();

            given(mapper.toEntity(settlement)).willReturn(entity);

            // when
            commandAdapter.persist(settlement);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("COMPLETED 상태 Settlement를 저장합니다")
        void persist_WithCompletedSettlement_SavesSuccessfully() {
            // given
            Settlement settlement = SettlementFixtures.completedSettlement();
            SettlementJpaEntity entity = SettlementJpaEntityFixtures.completedEntity();

            given(mapper.toEntity(settlement)).willReturn(entity);

            // when
            commandAdapter.persist(settlement);

            // then
            then(repository).should().save(entity);
        }

        @Test
        @DisplayName("Mapper가 정확히 한 번만 호출됩니다")
        void persist_CallsMapperExactlyOnce() {
            // given
            Settlement settlement = SettlementFixtures.calculatingSettlement();
            SettlementJpaEntity entity =
                    SettlementJpaEntityFixtures.calculatingEntity(settlement.idValue());

            given(mapper.toEntity(settlement)).willReturn(entity);

            // when
            commandAdapter.persist(settlement);

            // then
            then(mapper).should().toEntity(settlement);
            then(mapper).shouldHaveNoMoreInteractions();
        }
    }
}
