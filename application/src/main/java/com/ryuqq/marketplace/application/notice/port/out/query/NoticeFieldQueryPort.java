package com.ryuqq.marketplace.application.notice.port.out.query;

import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.List;
import java.util.Map;

/** 고시정보 필드 Query Port. */
public interface NoticeFieldQueryPort {
    List<NoticeField> findByNoticeCategoryId(Long noticeCategoryId);

    Map<Long, List<NoticeField>> findGroupedByNoticeCategoryIds(List<Long> noticeCategoryIds);
}
