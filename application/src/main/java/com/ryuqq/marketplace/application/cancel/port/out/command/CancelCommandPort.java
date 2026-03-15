package com.ryuqq.marketplace.application.cancel.port.out.command;

import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import java.util.List;

/** 취소 Command Port. */
public interface CancelCommandPort {

    /**
     * 취소를 저장합니다.
     *
     * @param cancel 저장할 취소 Aggregate
     */
    void persist(Cancel cancel);

    /**
     * 취소 목록을 일괄 저장합니다.
     *
     * @param cancels 저장할 취소 Aggregate 목록
     */
    void persistAll(List<Cancel> cancels);
}
