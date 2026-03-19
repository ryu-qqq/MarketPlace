package com.ryuqq.marketplace.application.exchange.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeOutboxQueryPort;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ExchangeOutboxReadManager 단위 테스트")
class ExchangeOutboxReadManagerTest {

    @InjectMocks private ExchangeOutboxReadManager sut;

    @Mock private ExchangeOutboxQueryPort queryPort;

    @Nested
    @DisplayName("findPendingOutboxes() - PENDING 아웃박스 조회")
    class FindPendingOutboxesTest {

        @Test
        @DisplayName("기준 시간 이전의 PENDING 아웃박스 목록을 반환한다")
        void findPendingOutboxes_ReturnsOutboxList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 100;
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);

            given(queryPort.findPendingOutboxes(beforeTime, batchSize)).willReturn(List.of(outbox));

            // when
            List<ExchangeOutbox> result = sut.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(outbox);
        }

        @Test
        @DisplayName("PENDING 아웃박스가 없으면 빈 목록을 반환한다")
        void findPendingOutboxes_NoneFound_ReturnsEmptyList() {
            // given
            Instant beforeTime = Instant.now();
            int batchSize = 100;

            given(queryPort.findPendingOutboxes(beforeTime, batchSize)).willReturn(List.of());

            // when
            List<ExchangeOutbox> result = sut.findPendingOutboxes(beforeTime, batchSize);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findProcessingTimeoutOutboxes() - 타임아웃 아웃박스 조회")
    class FindProcessingTimeoutOutboxesTest {

        @Test
        @DisplayName("타임아웃 기준 시간 이전의 처리 중 아웃박스 목록을 반환한다")
        void findProcessingTimeoutOutboxes_ReturnsOutboxList() {
            // given
            Instant timeoutBefore = Instant.now().minusSeconds(300);
            int batchSize = 50;
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);

            given(queryPort.findProcessingTimeoutOutboxes(timeoutBefore, batchSize))
                    .willReturn(List.of(outbox));

            // when
            List<ExchangeOutbox> result =
                    sut.findProcessingTimeoutOutboxes(timeoutBefore, batchSize);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(outbox);
        }
    }

    @Nested
    @DisplayName("getById() - ID로 아웃박스 조회")
    class GetByIdTest {

        @Test
        @DisplayName("ID로 ExchangeOutbox를 반환한다")
        void getById_ExistingId_ReturnsExchangeOutbox() {
            // given
            Long outboxId = 1L;
            ExchangeOutbox expected = Mockito.mock(ExchangeOutbox.class);

            given(queryPort.getById(outboxId)).willReturn(expected);

            // when
            ExchangeOutbox result = sut.getById(outboxId);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }
}
