package com.ryuqq.marketplace.domain.commoncode.aggregate;

import com.ryuqq.marketplace.domain.commoncode.vo.CommonCodeDisplayName;
import com.ryuqq.marketplace.domain.commoncode.vo.DisplayOrder;

/**
 * CommonCode 업데이트 데이터 번들.
 *
 * <p>공통 코드 수정 시 필요한 데이터를 묶어서 전달합니다.
 *
 * @param displayName 표시명 (필수)
 * @param displayOrder 표시 순서 (null이면 기본값)
 */
public record CommonCodeUpdateData(CommonCodeDisplayName displayName, DisplayOrder displayOrder) {

    public static CommonCodeUpdateData of(String displayName, int displayOrder) {
        return new CommonCodeUpdateData(
                CommonCodeDisplayName.of(displayName), DisplayOrder.of(displayOrder));
    }
}
