package com.ryuqq.marketplace.application.productnotice.dto.command;

import java.util.List;

/**
 * RegisterProductNoticeCommand - 상품 그룹 고시정보 등록 Command.
 *
 * <p>APP-CMD-001: Command Record 패턴
 *
 * <p>APP-CMD-002: Primitive 타입 사용 (DTO 레이어)
 */
public record RegisterProductNoticeCommand(
        long productGroupId, long noticeCategoryId, List<NoticeEntryCommand> entries) {

    public record NoticeEntryCommand(long noticeFieldId, String fieldValue) {}
}
