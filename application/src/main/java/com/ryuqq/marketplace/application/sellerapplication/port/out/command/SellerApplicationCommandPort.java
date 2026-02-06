package com.ryuqq.marketplace.application.sellerapplication.port.out.command;

import com.ryuqq.marketplace.domain.sellerapplication.aggregate.SellerApplication;
import java.util.List;

/**
 * SellerApplication Command Port.
 *
 * <p>입점 신청 저장/수정 포트입니다.
 *
 * @author ryu-qqq
 */
public interface SellerApplicationCommandPort {

    /**
     * 입점 신청을 저장합니다.
     *
     * @param sellerApplication 저장할 입점 신청
     * @return 저장된 신청 ID
     */
    Long persist(SellerApplication sellerApplication);

    /**
     * 다건의 입점 신청을 저장합니다.
     *
     * @param sellerApplications 저장할 입점 신청 목록
     */
    void persistAll(List<SellerApplication> sellerApplications);
}
