package com.ryuqq.marketplace.application.cancel.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.cancel.port.out.query.CancelOutboxQueryPort;
import com.ryuqq.marketplace.domain.cancel.outbox.aggregate.CancelOutbox;
import java.time.Instant;
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
@DisplayName("CancelOutboxReadManager 단위 테스트")
class CancelOutboxReadManagerTest {

    @InjectMocks private CancelOutboxReadManager sut;

    @Mock private CancelOutboxQueryPort queryPort;

    @Nested
    @DisplayName("findPendingOutboxes() - PENDING 아웃박스 조회")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("시간 기준 이전의 PENDING 아웃박스 목록을 반환한다")
        void findPendingOutboxes_ReturnsOutboxList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 100;
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);

            given(queryPort.findPendingOutboxes(beforeTime, batchSize)).willReturn(List.of(outbox));

            // when
            List<CancelOutbox> result = sut.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findPendingOutboxes(beforeTime, batchSize);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 빈 목록을 반환한다")
        void findPendingOutboxes_NoneFound_ReturnsEmptyList() {
            // given
            given(queryPort.findPendingOutboxes(any(), anyInt())).willReturn(List.of());

            // when
            List<CancelOutbox> result = sut.findPendingOutboxes(Instant.now(), 100);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - PROCESSING 타임아웃 아웃박스 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃 기준 이전 PROCESSING 아웃박스 목록을 반환한다")
        void findProcessingTimeoutOutboxes_ReturnsTimeoutOutboxList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 50;
            CancelOutbox outbox = org.mockito.Mockito.mock(CancelOutbox.class);

            given(queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of(outbox));

            // when
            List<CancelOutbox> result = sut.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findProcessingTimeoutOutboxes(timeoutBefore, batchSize);
        }
    }

    @Nested
    @DisplayName("getById() - ID로 아웃박스 조회")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 CancelOutbox를 반환한다")
        void getById_ExistingId_ReturnsCancelOutbox() {
            // given
            Long outboxId = 1L;
            CancelOutbox expected = org.mockito.Mockito.mock(CancelOutbox.class);

            given(queryPort.getById(outboxId)).willReturn(expected);

            // when
            CancelOutbox result = sut.getById(outboxId);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().getById(outboxId);
        }
    }
}
