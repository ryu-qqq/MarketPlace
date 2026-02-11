package com.ryuqq.marketplace.application.notice.manager;

import com.ryuqq.marketplace.application.notice.port.out.query.NoticeFieldQueryPort;
import com.ryuqq.marketplace.domain.notice.aggregate.NoticeField;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 고시정보 필드 Read Manager. */
@Component
public class NoticeFieldReadManager {

    private final NoticeFieldQueryPort queryPort;

    public NoticeFieldReadManager(NoticeFieldQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    @Transactional(readOnly = true)
    public List<NoticeField> getByNoticeCategoryId(Long noticeCategoryId) {
        return queryPort.findByNoticeCategoryId(noticeCategoryId);
    }

    @Transactional(readOnly = true)
    public Map<Long, List<NoticeField>> getGroupedByNoticeCategoryIds(
            List<Long> noticeCategoryIds) {
        if (noticeCategoryIds.isEmpty()) {
            return Map.of();
        }
        return queryPort.findGroupedByNoticeCategoryIds(noticeCategoryIds);
    }
}
