package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

/** 인바운드 상품 상세설명 수정 UseCase. */
public interface UpdateInboundProductDescriptionUseCase {

    void execute(long inboundSourceId, String externalProductCode, String content);
}
