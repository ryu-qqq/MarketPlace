package com.ryuqq.marketplace.application.inboundqna.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.inboundqna.dto.external.QnaUpdatePayload;
import com.ryuqq.marketplace.application.qna.manager.QnaCommandManager;
import com.ryuqq.marketplace.application.qna.manager.QnaReadManager;
import com.ryuqq.marketplace.domain.qna.aggregate.Qna;
import com.ryuqq.marketplace.domain.qna.vo.QnaSource;
import com.ryuqq.marketplace.domain.qna.vo.QnaType;
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
@DisplayName("UpdateQnaWebhookService 단위 테스트")
class UpdateQnaWebhookServiceTest {

    @Mock private QnaReadManager qnaReadManager;
    @Mock private QnaCommandManager qnaCommandManager;

    @InjectMocks private UpdateQnaWebhookService sut;

    private static final long SALES_CHANNEL_ID = 1L;

    @Nested
    @DisplayName("execute() - QnA 수정 웹훅 처리")
    class ExecuteTest {

        @Test
        @DisplayName("정상 수정 시 Qna.updateQuestion() 호출 후 persist()를 수행한다")
        void execute_ValidUpdate_UpdatesAndPersists() {
            // given
            QnaUpdatePayload payload =
                    new QnaUpdatePayload("EXT-QNA-001", "수정된 제목", "수정된 내용");

            Qna qna =
                    Qna.forNew(
                            100L,
                            200L,
                            null,
                            QnaType.PRODUCT,
                            new QnaSource(SALES_CHANNEL_ID, "EXT-QNA-001"),
                            "원본 제목",
                            "원본 내용",
                            "구매자A",
                            Instant.now());

            given(qnaReadManager.getBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-001"))
                    .willReturn(qna);

            // when
            int updated = sut.execute(List.of(payload), SALES_CHANNEL_ID);

            // then
            assertThat(updated).isEqualTo(1);
            assertThat(qna.questionTitle()).isEqualTo("수정된 제목");
            assertThat(qna.questionContent()).isEqualTo("수정된 내용");
            then(qnaCommandManager).should().persist(qna);
        }

        @Test
        @DisplayName("QnA를 찾을 수 없으면 예외가 발생하고 updated=0을 반환한다")
        void execute_QnaNotFound_ReturnsZero() {
            // given
            QnaUpdatePayload payload =
                    new QnaUpdatePayload("EXT-QNA-NOT-FOUND", "수정된 제목", "수정된 내용");

            given(qnaReadManager.getBySalesChannelIdAndExternalQnaId(
                            SALES_CHANNEL_ID, "EXT-QNA-NOT-FOUND"))
                    .willThrow(new RuntimeException("QnA를 찾을 수 없습니다"));

            // when
            int updated = sut.execute(List.of(payload), SALES_CHANNEL_ID);

            // then
            assertThat(updated).isZero();
            then(qnaCommandManager).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 건 수정 시 각각 처리하여 성공 건수를 반환한다")
        void execute_MultiplePayloads_ProcessesEach() {
            // given
            QnaUpdatePayload payload1 =
                    new QnaUpdatePayload("EXT-QNA-001", "수정 제목 1", "수정 내용 1");
            QnaUpdatePayload payload2 =
                    new QnaUpdatePayload("EXT-QNA-002", "수정 제목 2", "수정 내용 2");

            Qna qna1 =
                    Qna.forNew(
                            100L,
                            200L,
                            null,
                            QnaType.PRODUCT,
                            new QnaSource(SALES_CHANNEL_ID, "EXT-QNA-001"),
                            "원본 제목 1",
                            "원본 내용 1",
                            "구매자A",
                            Instant.now());

            Qna qna2 =
                    Qna.forNew(
                            100L,
                            300L,
                            null,
                            QnaType.SHIPPING,
                            new QnaSource(SALES_CHANNEL_ID, "EXT-QNA-002"),
                            "원본 제목 2",
                            "원본 내용 2",
                            "구매자B",
                            Instant.now());

            given(qnaReadManager.getBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-001"))
                    .willReturn(qna1);
            given(qnaReadManager.getBySalesChannelIdAndExternalQnaId(SALES_CHANNEL_ID, "EXT-QNA-002"))
                    .willReturn(qna2);

            // when
            int updated = sut.execute(List.of(payload1, payload2), SALES_CHANNEL_ID);

            // then
            assertThat(updated).isEqualTo(2);
            assertThat(qna1.questionTitle()).isEqualTo("수정 제목 1");
            assertThat(qna2.questionTitle()).isEqualTo("수정 제목 2");
            then(qnaCommandManager).should().persist(qna1);
            then(qnaCommandManager).should().persist(qna2);
        }
    }
}
