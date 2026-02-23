package com.ryuqq.marketplace.application.inboundproduct.port.in.command;

/** 인바운드 상품 가격 수정 UseCase. */
public interface UpdateInboundProductPriceUseCase {

    void execute(
            long inboundSourceId, String externalProductCode, int regularPrice, int currentPrice);
}
