package com.ryuqq.marketplace.application.exchange.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.exchange.factory.ExchangeCommandFactory;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxCommandManager;
import com.ryuqq.marketplace.application.exchange.manager.ExchangeOutboxReadManager;
import com.ryuqq.marketplace.application.exchange.port.out.client.ExchangeOutboxPublishClient;
import com.ryuqq.marketplace.domain.exchange.outbox.aggregate.ExchangeOutbox;
import com.ryuqq.marketplace.domain.exchange.outbox.vo.ExchangeOutboxType;
import java.time.Instant;
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
@DisplayName("ExchangeOutboxRelayProcessor 단위 테스트")
class ExchangeOutboxRelayProcessorTest {

    @InjectMocks private ExchangeOutboxRelayProcessor sut;

    @Mock private ExchangeOutboxCommandManager outboxCommandManager;
    @Mock private ExchangeOutboxReadManager outboxReadManager;
    @Mock private ExchangeOutboxPublishClient publishClient;
    @Mock private ObjectMapper objectMapper;
    @Mock private ExchangeCommandFactory commandFactory;

    @Nested
    @DisplayName("relay() - Outbox SQS 발행")
    class RelayTest {

        @Test
        @DisplayName("SQS 발행 성공 시 true를 반환한다")
        void relay_Success_ReturnsTrue() throws Exception {
            // given
            Long outboxId = 1L;
            Instant now = Instant.now();
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(outbox.outboxType()).willReturn(ExchangeOutboxType.COLLECT);
            given(objectMapper.writeValueAsString(Mockito.any())).willReturn("{\"outboxId\":1}");
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now));

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isTrue();
            then(outbox).should().startProcessing(now);
            then(outboxCommandManager).should().persist(outbox);
            then(publishClient).should().publish(Mockito.anyString());
        }

        @Test
        @DisplayName("SQS 발행 실패 시 Outbox에 실패를 기록하고 false를 반환한다")
        void relay_SqsPublishFailure_RecordsFailureAndReturnsFalse() throws Exception {
            // given
            Long outboxId = 1L;
            Instant now = Instant.now();
            Instant failNow = Instant.now();
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox freshOutbox = Mockito.mock(ExchangeOutbox.class);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(outbox.outboxType()).willReturn(ExchangeOutboxType.COLLECT);
            given(objectMapper.writeValueAsString(Mockito.any())).willReturn("{\"outboxId\":1}");
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now))
                    .willReturn(new StatusChangeContext<>(outboxId, failNow));
            willThrow(new RuntimeException("SQS 연결 실패"))
                    .given(publishClient)
                    .publish(Mockito.anyString());
            given(outboxReadManager.getById(outboxId)).willReturn(freshOutbox);

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isFalse();
            then(freshOutbox)
                    .should()
                    .recordFailure(
                            Mockito.eq(true), Mockito.contains("Relay 실패"), Mockito.eq(failNow));
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("Outbox 저장 실패 시 실패를 기록하고 false를 반환한다")
        void relay_PersistFailure_RecordsFailureAndReturnsFalse() {
            // given
            Long outboxId = 1L;
            Instant now = Instant.now();
            Instant failNow = Instant.now();
            ExchangeOutbox outbox = Mockito.mock(ExchangeOutbox.class);
            ExchangeOutbox freshOutbox = Mockito.mock(ExchangeOutbox.class);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(commandFactory.createOutboxChangeContext(outboxId))
                    .willReturn(new StatusChangeContext<>(outboxId, now))
                    .willReturn(new StatusChangeContext<>(outboxId, failNow));
            willThrow(new RuntimeException("DB 저장 실패")).given(outboxCommandManager).persist(outbox);
            given(outboxReadManager.getById(outboxId)).willReturn(freshOutbox);

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isFalse();
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.any(), Mockito.eq(failNow));
        }
    }
}
