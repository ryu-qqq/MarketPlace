package com.ryuqq.marketplace.application.qna.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.port.out.command.QnaCommandPort;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
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
@DisplayName("QnaCommandManager 단위 테스트")
class QnaCommandManagerTest {

    @InjectMocks private QnaCommandManager sut;

    @Mock private QnaCommandPort commandPort;

    @Nested
    @DisplayName("persist() - Qna 저장")
    class PersistTest {

        @Test
        @DisplayName("Qna를 QnaCommandPort에 위임하여 저장한다")
        void persist_ValidQna_DelegatesToCommandPort() {
            // given
            Qna qna = QnaFixtures.pendingQna();

            // when
            sut.persist(qna);

            // then
            then(commandPort).should().persist(qna);
        }

        @Test
        @DisplayName("ANSWERED 상태 Qna도 QnaCommandPort에 위임하여 저장한다")
        void persist_AnsweredQna_DelegatesToCommandPort() {
            // given
            Qna qna = QnaFixtures.answeredQna();

            // when
            sut.persist(qna);

            // then
            then(commandPort).should().persist(qna);
        }
    }
}
