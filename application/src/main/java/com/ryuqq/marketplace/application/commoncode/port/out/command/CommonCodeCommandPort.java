package com.ryuqq.marketplace.application.commoncode.port.out.command;

import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;

/** 공통 코드 Command Port. */
public interface CommonCodeCommandPort {

    Long persist(CommonCode commonCode);

    void persistAll(List<CommonCode> commonCodes);
}
