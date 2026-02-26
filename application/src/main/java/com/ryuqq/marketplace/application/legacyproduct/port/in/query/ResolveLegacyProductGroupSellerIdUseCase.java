package com.ryuqq.marketplace.application.legacyproduct.port.in.query;

import java.util.Optional;

/** 레거시 productGroupId로 sellerId를 조회하는 UseCase. */
public interface ResolveLegacyProductGroupSellerIdUseCase {

    Optional<Long> execute(long productGroupId);
}
