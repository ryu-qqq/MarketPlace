package com.ryuqq.marketplace.application.cancel.dto.command;

import java.util.List;

/** 취소 거절 일괄 처리 명령. sellerId가 null이면 슈퍼어드민 (전체 접근 가능). */
public record RejectCancelBatchCommand(List<String> cancelIds, String processedBy, Long sellerId) {}
