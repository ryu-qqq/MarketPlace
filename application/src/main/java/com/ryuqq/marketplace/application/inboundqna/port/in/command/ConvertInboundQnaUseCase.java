package com.ryuqq.marketplace.application.inboundqna.port.in.command;

/** InboundQna → Qna 단건 변환 UseCase. */
public interface ConvertInboundQnaUseCase {
    void execute(long inboundQnaId);
}
