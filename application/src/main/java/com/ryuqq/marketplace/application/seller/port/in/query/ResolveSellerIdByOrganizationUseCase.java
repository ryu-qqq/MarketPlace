package com.ryuqq.marketplace.application.seller.port.in.query;

import java.util.Optional;

/** organizationId로 sellerId를 조회하는 UseCase. */
public interface ResolveSellerIdByOrganizationUseCase {

    Optional<Long> execute(String organizationId);
}
