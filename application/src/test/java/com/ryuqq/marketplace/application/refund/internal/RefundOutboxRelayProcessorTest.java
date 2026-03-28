package com.ryuqq.marketplace.application.refund.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.refund.factory.RefundCommandFactory;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxCommandManager;
import com.ryuqq.marketplace.application.refund.manager.RefundOutboxReadManager;
import com.ryuqq.marketplace.application.refund.port.out.client.RefundOutboxPublishClient;
import com.ryuqq.marketplace.domain.refund.outbox.aggregate.RefundOutbox;
import com.ryuqq.marketplace.domain.refund.outbox.vo.RefundOutboxType;
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
@DisplayName("RefundOutboxRelayProcessor 단위 테스트")
class RefundOutboxRelayProcessorTest {

    @InjectMocks private RefundOutboxRelayProcessor sut;

    @Mock private RefundOutboxCommandManager outboxCommandManager;
    @Mock private RefundOutboxReadManager outboxReadManager;
    @Mock private RefundOutboxPublishClient publishClient;
    @Mock private ObjectMapper objectMapper;
    @Mock private RefundCommandFactory commandFactory;

    @Nested
    @DisplayName("relay() - Outbox SQS 발행")
    class RelayTest {

        @Test
        @DisplayName("SQS 발행 성공 시 true를 반환한다")
        void relay_Success_ReturnsTrue() throws Exception {
            // given
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            Instant now = Instant.now();
            given(outbox.idValue()).willReturn(1L);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(outbox.outboxType()).willReturn(RefundOutboxType.REQUEST);
            given(commandFactory.createOutboxChangeContext(1L))
                    .willReturn(new StatusChangeContext<>(1L, now));
            given(objectMapper.writeValueAsString(Mockito.any())).willReturn("{\"outboxId\":1}");

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isTrue();
            then(outbox).should().startProcessing(Mockito.any());
            then(outboxCommandManager).should().persist(outbox);
            then(publishClient).should().publish(Mockito.anyString());
        }

        @Test
        @DisplayName("SQS 발행 실패 시 Outbox에 실패를 기록하고 false를 반환한다")
        void relay_SqsPublishFailure_RecordsFailureAndReturnsFalse() throws Exception {
            // given
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            RefundOutbox freshOutbox = Mockito.mock(RefundOutbox.class);
            Instant now = Instant.now();
            given(outbox.idValue()).willReturn(1L);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(outbox.outboxType()).willReturn(RefundOutboxType.REQUEST);
            given(commandFactory.createOutboxChangeContext(1L))
                    .willReturn(new StatusChangeContext<>(1L, now));
            given(objectMapper.writeValueAsString(Mockito.any())).willReturn("{\"outboxId\":1}");
            willThrow(new RuntimeException("SQS 연결 실패"))
                    .given(publishClient)
                    .publish(Mockito.anyString());
            given(outboxReadManager.getById(1L)).willReturn(freshOutbox);

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isFalse();
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.contains("Relay 실패"), Mockito.any());
            then(outboxCommandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("Outbox 저장 실패 시 실패를 기록하고 false를 반환한다")
        void relay_PersistFailure_RecordsFailureAndReturnsFalse() {
            // given
            RefundOutbox outbox = Mockito.mock(RefundOutbox.class);
            RefundOutbox freshOutbox = Mockito.mock(RefundOutbox.class);
            Instant now = Instant.now();
            given(outbox.idValue()).willReturn(1L);
            given(outbox.orderItemIdValue()).willReturn(1001L);
            given(commandFactory.createOutboxChangeContext(1L))
                    .willReturn(new StatusChangeContext<>(1L, now));
            willThrow(new RuntimeException("DB 저장 실패")).given(outboxCommandManager).persist(outbox);
            given(outboxReadManager.getById(1L)).willReturn(freshOutbox);

            // when
            boolean result = sut.relay(outbox);

            // then
            assertThat(result).isFalse();
            then(freshOutbox)
                    .should()
                    .recordFailure(Mockito.eq(true), Mockito.any(), Mockito.any());
        }
    }
}
