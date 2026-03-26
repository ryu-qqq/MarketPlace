package com.ryuqq.marketplace.application.qna.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.port.out.command.QnaOutboxCommandPort;
import com.ryuqq.marketplace.domain.qna.QnaOutboxFixtures;
import com.ryuqq.marketplace.domain.qna.outbox.aggregate.QnaOutbox;
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
@DisplayName("QnaOutboxCommandManager 단위 테스트")
class QnaOutboxCommandManagerTest {

    @InjectMocks private QnaOutboxCommandManager sut;

    @Mock private QnaOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - QnaOutbox 저장")
    class PersistTest {

        @Test
        @DisplayName("QnaOutbox를 QnaOutboxCommandPort에 위임하여 저장한다")
        void persist_PendingOutbox_DelegatesToCommandPort() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.pendingQnaOutbox();

            // when
            sut.persist(outbox);

            // then
            then(commandPort).should().persist(outbox);
        }

        @Test
        @DisplayName("COMPLETED 상태 QnaOutbox도 QnaOutboxCommandPort에 위임하여 저장한다")
        void persist_CompletedOutbox_DelegatesToCommandPort() {
            // given
            QnaOutbox outbox = QnaOutboxFixtures.completedQnaOutbox();

            // when
            sut.persist(outbox);

            // then
            then(commandPort).should().persist(outbox);
        }
    }
}
