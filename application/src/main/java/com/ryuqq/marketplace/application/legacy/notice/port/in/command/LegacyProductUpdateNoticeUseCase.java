package com.ryuqq.marketplace.application.legacy.notice.port.in.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;

/**
 * 레거시 상품 고시정보 수정 UseCase.
 *
 * <p>표준 커맨드를 받되, luxurydb에 저장합니다.
 * NoticeCategory를 함께 받아 entries → flat 필드 역매핑에 사용합니다.
 */
public interface LegacyProductUpdateNoticeUseCase {

    void execute(UpdateProductNoticeCommand command, NoticeCategory noticeCategory);
}
