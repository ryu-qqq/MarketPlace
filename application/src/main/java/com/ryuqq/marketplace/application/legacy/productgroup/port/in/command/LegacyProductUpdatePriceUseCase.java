package com.ryuqq.marketplace.application.legacy.productgroup.port.in.command;

/** 레거시 상품 가격 수정 UseCase. */
public interface LegacyProductUpdatePriceUseCase {

    void execute(long productGroupId, long regularPrice, long currentPrice);
}
