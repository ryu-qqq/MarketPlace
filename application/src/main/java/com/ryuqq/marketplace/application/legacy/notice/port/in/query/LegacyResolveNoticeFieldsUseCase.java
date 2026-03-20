package com.ryuqq.marketplace.application.legacy.notice.port.in.query;

import com.ryuqq.marketplace.domain.notice.aggregate.NoticeCategory;
import java.util.Optional;

/**
 * 레거시 상품그룹의 고시정보 카테고리 및 필드 해석 UseCase.
 *
 * <p>레거시 productGroupId → 레거시 카테고리 → 내부 카테고리 → NoticeCategory 해석.
 * Controller에서 고시정보 수정 시 표준 커맨드를 조립하기 위해 사용합니다.
 */
public interface LegacyResolveNoticeFieldsUseCase {

    /**
     * 레거시 상품그룹 ID로 고시정보 카테고리와 필드를 해석합니다.
     *
     * @param legacyProductGroupId 레거시 상품그룹 ID
     * @return 해석된 NoticeCategory (필드 포함), 매핑 실패 시 빈 Optional
     */
    Optional<NoticeCategory> execute(long legacyProductGroupId);
}
