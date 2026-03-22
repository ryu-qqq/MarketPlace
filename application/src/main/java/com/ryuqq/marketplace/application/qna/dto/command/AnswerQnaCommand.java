package com.ryuqq.marketplace.application.qna.dto.command;

/** QnA 답변 등록 명령. */
public record AnswerQnaCommand(
        long qnaId, String title, String content, String authorName, Long parentReplyId) {}
