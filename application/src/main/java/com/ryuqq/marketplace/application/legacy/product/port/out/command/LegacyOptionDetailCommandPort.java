package com.ryuqq.marketplace.application.legacy.product.port.out.command;

import com.ryuqq.marketplace.domain.legacy.optiondetail.aggregate.LegacyOptionDetail;

/** 세토프 DB option_detail 테이블 커맨드 Port. */
public interface LegacyOptionDetailCommandPort {

    Long persist(LegacyOptionDetail optionDetail);
}
