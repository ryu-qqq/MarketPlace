package com.ryuqq.marketplace.application.claimhistory.manager;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;

import com.ryuqq.marketplace.application.claimhistory.port.out.command.ClaimHistoryCommandPort;
import com.ryuqq.marketplace.domain.claimhistory.ClaimHistoryFixtures;
import com.ryuqq.marketplace.domain.claimhistory.aggregate.ClaimHistory;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimHistoryCommandManager 단위 테스트")
class ClaimHistoryCommandManagerTest {

    @InjectMocks private ClaimHistoryCommandManager sut;

    @Mock private ClaimHistoryCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단일 이력 저장")
    class PersistTest {

        @Test
        @DisplayName("클레임 이력을 저장소에 저장한다")
        void persist_ValidClaimHistory_DelegatesToCommandPort() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.manualClaimHistory();
            doNothing().when(commandPort).persist(history);

            // when
            sut.persist(history);

            // then
            then(commandPort).should().persist(history);
        }

        @Test
        @DisplayName("상태 변경 이력을 저장소에 저장한다")
        void persist_StatusChangeHistory_DelegatesToCommandPort() {
            // given
            ClaimHistory history = ClaimHistoryFixtures.statusChangeClaimHistory();
            doNothing().when(commandPort).persist(history);

            // when
            sut.persist(history);

            // then
            then(commandPort).should().persist(history);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다중 이력 벌크 저장")
    class PersistAllTest {

        @Test
        @DisplayName("여러 클레임 이력을 벌크로 저장한다")
        void persistAll_ValidHistories_DelegatesToCommandPort() {
            // given
            List<ClaimHistory> histories =
                    List.of(
                            ClaimHistoryFixtures.cancelStatusChangeHistory(),
                            ClaimHistoryFixtures.refundStatusChangeHistory(),
                            ClaimHistoryFixtures.exchangeStatusChangeHistory());
            doNothing().when(commandPort).persistAll(histories);

            // when
            sut.persistAll(histories);

            // then
            then(commandPort).should().persistAll(histories);
        }

        @Test
        @DisplayName("빈 목록으로 호출하면 CommandPort에 빈 목록을 전달한다")
        void persistAll_EmptyList_DelegatesToCommandPort() {
            // given
            List<ClaimHistory> emptyList = List.of();
            doNothing().when(commandPort).persistAll(emptyList);

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).should().persistAll(emptyList);
        }
    }
}
