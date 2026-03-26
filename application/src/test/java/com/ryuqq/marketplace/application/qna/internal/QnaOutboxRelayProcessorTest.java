package com.ryuqq.marketplace.application.qna.internal;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.ryuqq.marketplace.application.qna.manager.QnaOutboxCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaOutboxReadManager;
import com.ryuqq.marketplace.application.qna.port.out.client.QnaOutboxPublishClient;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
import com.ryuqq.marketplace.domain.qna.outbox.vo.QnaOutboxType;
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
@DisplayName("QnaOutboxRelayProcessor 단위 테스트")
class QnaOutboxRelayProcessorTest {

    @InjectMocks private QnaOutboxRelayProcessor sut;

    @Mock private QnaOutboxCommandManager commandManager;
    @Mock private QnaOutboxReadManager readManager;
    @Mock private QnaOutboxPublishClient publishClient;

    @Nested
    @DisplayName("relay() - QnA 아웃박스 SQS 발행")
    class RelayTest {

        @Test
        @DisplayName("SQS 발행 성공 시 Outbox를 PROCESSING으로 전환하고 메시지를 발행한다")
        void relay_Success_StartsProcessingAndPublishes() {
            // given
            // buildMessage()에서 idValue, qnaIdValue, salesChannelId, externalQnaId, outboxType 사용
            Long outboxId = 1L;
            Long qnaId = 10L;
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.qnaIdValue()).willReturn(qnaId);
            given(outbox.salesChannelId()).willReturn(1L);
            given(outbox.externalQnaId()).willReturn("EXT-QNA-001");
            given(outbox.outboxType()).willReturn(QnaOutboxType.ANSWER);

            // when
            sut.relay(outbox);

            // then
            then(outbox).should().startProcessing(Mockito.any(Instant.class));
            then(commandManager).should().persist(outbox);
            then(publishClient).should().publish(Mockito.anyString());
        }

        @Test
        @DisplayName("SQS 발행 성공 시 outboxId, qnaId, outboxType을 포함한 JSON 메시지를 발행한다")
        void relay_Success_PublishesMessageWithCorrectFields() {
            // given
            Long outboxId = 1L;
            Long qnaId = 10L;
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.qnaIdValue()).willReturn(qnaId);
            given(outbox.salesChannelId()).willReturn(1L);
            given(outbox.externalQnaId()).willReturn("EXT-QNA-001");
            given(outbox.outboxType()).willReturn(QnaOutboxType.ANSWER);

            // when
            sut.relay(outbox);

            // then
            then(publishClient).should().publish(
                    Mockito.argThat(msg ->
                            msg.contains("\"outboxId\":1")
                            && msg.contains("\"qnaId\":10")
                            && msg.contains("\"outboxType\":\"ANSWER\"")));
        }

        @Test
        @DisplayName("SQS 발행 실패 시 freshOutbox를 조회하여 실패를 기록하고 저장한다")
        void relay_SqsPublishFailure_RecordsFailureOnFreshOutbox() {
            // given
            // SQS 발행 실패 시 catch에서 readManager.getById(outbox.idValue()) 호출
            Long outboxId = 1L;
            Long qnaId = 10L;
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);

            given(outbox.idValue()).willReturn(outboxId);
            given(outbox.qnaIdValue()).willReturn(qnaId);
            given(outbox.salesChannelId()).willReturn(1L);
            given(outbox.externalQnaId()).willReturn("EXT-QNA-001");
            given(outbox.outboxType()).willReturn(QnaOutboxType.ANSWER);
            willThrow(new RuntimeException("SQS 연결 실패"))
                    .given(publishClient).publish(Mockito.anyString());
            given(readManager.getById(outboxId)).willReturn(freshOutbox);

            // when
            sut.relay(outbox);

            // then
            then(freshOutbox).should().recordFailure(
                    Mockito.eq(true),
                    Mockito.anyString(),
                    Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }

        @Test
        @DisplayName("Outbox 저장 실패 시 freshOutbox를 조회하여 실패를 기록한다")
        void relay_PersistFailure_RecordsFailureOnFreshOutbox() {
            // given
            // commandManager.persist(outbox) 실패 시 catch에서 readManager.getById(outbox.idValue()) 호출
            // startProcessing() 후 persist 실패이므로 buildMessage 관련 메서드는 호출되지 않음
            Long outboxId = 1L;
            QnaOutbox outbox = Mockito.mock(QnaOutbox.class);
            QnaOutbox freshOutbox = Mockito.mock(QnaOutbox.class);

            given(outbox.idValue()).willReturn(outboxId);
            willThrow(new RuntimeException("DB 저장 실패")).given(commandManager).persist(outbox);
            given(readManager.getById(outboxId)).willReturn(freshOutbox);

            // when
            sut.relay(outbox);

            // then
            then(freshOutbox).should().recordFailure(
                    Mockito.eq(true),
                    Mockito.anyString(),
                    Mockito.any(Instant.class));
            then(commandManager).should().persist(freshOutbox);
        }
    }
}
