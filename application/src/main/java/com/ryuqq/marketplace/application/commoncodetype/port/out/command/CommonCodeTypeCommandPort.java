package com.ryuqq.marketplace.application.commoncodetype.port.out.command;

import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.util.List;

/** 공통 코드 타입 Command Port. */
public interface CommonCodeTypeCommandPort {

    Long persist(CommonCodeType commonCodeType);

    void persistAll(List<CommonCodeType> commonCodeTypes);
}
