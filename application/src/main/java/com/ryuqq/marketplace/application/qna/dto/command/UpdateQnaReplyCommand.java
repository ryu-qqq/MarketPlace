package com.ryuqq.marketplace.application.qna.dto.command;

/** QnA 답변 수정 명령. */
public record UpdateQnaReplyCommand(long qnaId, long replyId, String content) {}
