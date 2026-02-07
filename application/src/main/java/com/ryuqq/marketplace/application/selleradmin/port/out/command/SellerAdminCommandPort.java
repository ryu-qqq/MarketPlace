package com.ryuqq.marketplace.application.selleradmin.port.out.command;

import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import java.util.List;

/**
 * SellerAdmin Command Port.
 *
 * <p>셀러 관리자 저장/수정을 위한 Port-Out 인터페이스입니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface SellerAdminCommandPort {

    /**
     * 셀러 관리자를 저장합니다.
     *
     * @param sellerAdmin 셀러 관리자 도메인 객체
     * @return 저장된 셀러 관리자 ID (UUIDv7 String)
     */
    String persist(SellerAdmin sellerAdmin);

    /**
     * 셀러 관리자를 일괄 저장합니다.
     *
     * @param sellerAdmins 셀러 관리자 도메인 객체 목록
     */
    default void persistAll(List<SellerAdmin> sellerAdmins) {
        sellerAdmins.forEach(this::persist);
    }
}
