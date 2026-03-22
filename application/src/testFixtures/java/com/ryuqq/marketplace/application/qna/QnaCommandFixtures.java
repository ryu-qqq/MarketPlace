package com.ryuqq.marketplace.application.qna;

import com.ryuqq.marketplace.application.qna.dto.command.AnswerQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.CloseQnaCommand;
import com.ryuqq.marketplace.application.qna.dto.command.ExecuteQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.dto.command.ProcessPendingQnaOutboxCommand;
import com.ryuqq.marketplace.application.qna.dto.command.RecoverTimeoutQnaOutboxCommand;

/**
 * Qna Application Command 테스트 Fixtures.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public final class QnaCommandFixtures {

    private QnaCommandFixtures() {}

    // ===== 기본 상수 =====
    private static final long DEFAULT_QNA_ID = 1L;
    private static final long DEFAULT_OUTBOX_ID = 10L;
    private static final long DEFAULT_SALES_CHANNEL_ID = 1L;
    private static final String DEFAULT_EXTERNAL_QNA_ID = "EXT-QNA-001";
    private static final String DEFAULT_ANSWER_CONTENT = "해당 상품은 Free 사이즈입니다.";
    private static final String DEFAULT_AUTHOR_NAME = "판매자A";

    // ===== AnswerQnaCommand =====

    public static AnswerQnaCommand answerCommand() {
        return new AnswerQnaCommand(DEFAULT_QNA_ID, "", DEFAULT_ANSWER_CONTENT, DEFAULT_AUTHOR_NAME, null);
    }

    public static AnswerQnaCommand answerCommand(long qnaId) {
        return new AnswerQnaCommand(qnaId, "", DEFAULT_ANSWER_CONTENT, DEFAULT_AUTHOR_NAME, null);
    }

    public static AnswerQnaCommand answerCommandWithParent(long qnaId, long parentReplyId) {
        return new AnswerQnaCommand(qnaId, "", DEFAULT_ANSWER_CONTENT, DEFAULT_AUTHOR_NAME, parentReplyId);
    }

    // ===== CloseQnaCommand =====

    public static CloseQnaCommand closeCommand() {
        return new CloseQnaCommand(DEFAULT_QNA_ID);
    }

    public static CloseQnaCommand closeCommand(long qnaId) {
        return new CloseQnaCommand(qnaId);
    }

    // ===== ProcessPendingQnaOutboxCommand =====

    public static ProcessPendingQnaOutboxCommand processPendingOutboxCommand() {
        return new ProcessPendingQnaOutboxCommand(100, 5);
    }

    public static ProcessPendingQnaOutboxCommand processPendingOutboxCommand(int batchSize, int delaySeconds) {
        return new ProcessPendingQnaOutboxCommand(batchSize, delaySeconds);
    }

    // ===== RecoverTimeoutQnaOutboxCommand =====

    public static RecoverTimeoutQnaOutboxCommand recoverTimeoutOutboxCommand() {
        return new RecoverTimeoutQnaOutboxCommand(50, 300);
    }

    public static RecoverTimeoutQnaOutboxCommand recoverTimeoutOutboxCommand(int batchSize, int timeoutSeconds) {
        return new RecoverTimeoutQnaOutboxCommand(batchSize, timeoutSeconds);
    }

    // ===== ExecuteQnaOutboxCommand =====

    public static ExecuteQnaOutboxCommand executeOutboxCommand() {
        return new ExecuteQnaOutboxCommand(
                DEFAULT_OUTBOX_ID,
                DEFAULT_QNA_ID,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                "ANSWER");
    }

    public static ExecuteQnaOutboxCommand executeOutboxCommand(long outboxId, long qnaId) {
        return new ExecuteQnaOutboxCommand(
                outboxId,
                qnaId,
                DEFAULT_SALES_CHANNEL_ID,
                DEFAULT_EXTERNAL_QNA_ID,
                "ANSWER");
    }
}
