package com.ryuqq.marketplace.application.legacy.productcontext.port.in.query;

import com.ryuqq.marketplace.application.legacy.productcontext.dto.command.ResolveLegacyProductContextCommand;
import com.ryuqq.marketplace.application.legacy.productcontext.dto.result.LegacyProductContext;

/**
 * 레거시 상품 컨텍스트 리졸빙 UseCase.
 *
 * <p>레거시 PK + 배송/환불 데이터 → 표준 ID + 정책 ID + NoticeCategory를 조회합니다.
 */
public interface ResolveLegacyProductContextUseCase {

    LegacyProductContext resolve(ResolveLegacyProductContextCommand command);
}
