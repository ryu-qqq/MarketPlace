package com.ryuqq.marketplace.application.legacy.productnotice.port.out.command;

import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;

/**
 * 레거시 상품 고시정보 저장 Port.
 *
 * <p>표준 커맨드 + NoticeCategory를 받아서 luxurydb에 저장합니다. entries의 fieldId → NoticeCategory.fields()로
 * fieldCode 역매핑하여 flat 컬럼에 저장하는 것은 adapter-out 구현체의 책임입니다.
 */
public interface LegacyProductNoticeCommandPort {

    void update(UpdateProductNoticeCommand command, NoticeCategory noticeCategory);
}
