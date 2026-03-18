package com.ryuqq.marketplace.application.refund.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 환불 목록 페이지 결과. */
public record RefundPageResult(List<RefundListResult> refunds, PageMeta pageMeta) {}
