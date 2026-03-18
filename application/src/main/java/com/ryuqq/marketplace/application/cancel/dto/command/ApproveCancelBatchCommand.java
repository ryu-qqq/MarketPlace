package com.ryuqq.marketplace.application.cancel.dto.command;

import java.util.List;

/** 취소 승인 일괄 처리 명령. sellerId가 null이면 슈퍼어드민 (전체 접근 가능). */
public record ApproveCancelBatchCommand(
        List<String> cancelIds, String processedBy, Long sellerId) {}
