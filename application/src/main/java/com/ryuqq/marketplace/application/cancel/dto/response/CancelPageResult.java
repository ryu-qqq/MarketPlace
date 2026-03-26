package com.ryuqq.marketplace.application.cancel.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 취소 목록 페이지 결과. */
public record CancelPageResult(List<CancelListResult> cancels, PageMeta pageMeta) {}
