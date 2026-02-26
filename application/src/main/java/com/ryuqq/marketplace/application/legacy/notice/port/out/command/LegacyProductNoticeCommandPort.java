package com.ryuqq.marketplace.application.legacy.notice.port.out.command;

import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productgroup.vo.LegacyProductNotice;

/** 세토프 DB product_notice 테이블 커맨드 Port. */
public interface LegacyProductNoticeCommandPort {

    void persist(LegacyProductGroupId productGroupId, LegacyProductNotice notice);
}
