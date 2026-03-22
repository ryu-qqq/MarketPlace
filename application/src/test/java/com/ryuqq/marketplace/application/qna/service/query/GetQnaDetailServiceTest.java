package com.ryuqq.marketplace.application.qna.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.qna.assembler.QnaAssembler;
import com.ryuqq.marketplace.application.qna.dto.result.QnaResult;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.QnaFixtures;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetQnaDetailService 단위 테스트")
class GetQnaDetailServiceTest {

    @InjectMocks private GetQnaDetailService sut;

    @Mock private QnaReadManager readManager;
    @Spy private QnaAssembler assembler;

    @Nested
    @DisplayName("execute() - QnA 상세 조회")
    class ExecuteTest {

        @Test
        @DisplayName("QnA ID로 상세 정보를 조회하고 QnaResult를 반환한다")
        void execute_ValidId_ReturnsQnaResult() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            given(readManager.getById(qna.idValue())).willReturn(qna);

            // when
            QnaResult result = sut.execute(qna.idValue());

            // then
            assertThat(result).isNotNull();
            assertThat(result.qnaId()).isEqualTo(qna.idValue());
            assertThat(result.status()).isEqualTo(QnaStatus.ANSWERED);
        }

        @Test
        @DisplayName("답변이 있는 QnA 조회 시 replies가 포함된 결과를 반환한다")
        void execute_AnsweredQna_ReturnsResultWithReplies() {
            // given
            Qna qna = QnaFixtures.answeredQna();
            given(readManager.getById(qna.idValue())).willReturn(qna);

            // when
            QnaResult result = sut.execute(qna.idValue());

            // then
            assertThat(result.replies()).hasSize(1);
        }

        @Test
        @DisplayName("조회 시 readManager.getById를 호출한다")
        void execute_ValidId_InvokesReadManager() {
            // given
            Qna qna = QnaFixtures.pendingQna();
            given(readManager.getById(qna.idValue())).willReturn(qna);

            // when
            sut.execute(qna.idValue());

            // then
            then(readManager).should().getById(qna.idValue());
        }
    }
}
