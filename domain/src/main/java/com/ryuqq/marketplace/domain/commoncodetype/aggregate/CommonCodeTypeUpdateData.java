package com.ryuqq.marketplace.domain.commoncodetype.aggregate;

import com.ryuqq.marketplace.domain.commoncodetype.vo.CommonCodeTypeDescription;
import com.ryuqq.marketplace.domain.commoncodetype.vo.CommonCodeTypeName;
import com.ryuqq.marketplace.domain.commoncodetype.vo.DisplayOrder;

/**
 * 공통 코드 타입 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 */
public record CommonCodeTypeUpdateData(
        CommonCodeTypeName name, CommonCodeTypeDescription description, DisplayOrder displayOrder) {

    public static CommonCodeTypeUpdateData of(
            CommonCodeTypeName name,
            CommonCodeTypeDescription description,
            DisplayOrder displayOrder) {
        return new CommonCodeTypeUpdateData(name, description, displayOrder);
    }

    public static CommonCodeTypeUpdateData of(String name, String description, int displayOrder) {
        return new CommonCodeTypeUpdateData(
                CommonCodeTypeName.of(name),
                CommonCodeTypeDescription.of(description),
                DisplayOrder.of(displayOrder));
    }
}
