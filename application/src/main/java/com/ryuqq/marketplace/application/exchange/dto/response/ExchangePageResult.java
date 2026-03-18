package com.ryuqq.marketplace.application.exchange.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/** 교환 목록 페이지 결과. */
public record ExchangePageResult(List<ExchangeListResult> exchanges, PageMeta pageMeta) {}
