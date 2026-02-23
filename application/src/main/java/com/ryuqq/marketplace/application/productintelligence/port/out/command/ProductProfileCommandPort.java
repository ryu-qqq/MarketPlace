package com.ryuqq.marketplace.application.productintelligence.port.out.command;

import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;

/** 상품 프로파일 Command Port. */
public interface ProductProfileCommandPort {

    Long persist(ProductProfile profile);
}
