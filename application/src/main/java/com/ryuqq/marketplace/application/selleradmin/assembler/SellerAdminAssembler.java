package com.ryuqq.marketplace.application.selleradmin.assembler;

import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationPageResult;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminApplicationResult;
import com.ryuqq.marketplace.application.selleradmin.dto.response.SellerAdminResult;
import com.ryuqq.marketplace.domain.selleradmin.aggregate.SellerAdmin;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * SellerAdmin Assembler.
 *
 * <p>Domain Entity를 Application DTO로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Component
public class SellerAdminAssembler {

    /**
     * SellerAdmin을 SellerAdminApplicationResult로 변환합니다.
     *
     * @param sellerAdmin 셀러 관리자 Entity
     * @return 변환된 Result DTO
     */
    public SellerAdminApplicationResult toResult(SellerAdmin sellerAdmin) {
        return new SellerAdminApplicationResult(
                sellerAdmin.idValue(),
                sellerAdmin.sellerIdValue(),
                sellerAdmin.loginIdValue(),
                sellerAdmin.nameValue(),
                sellerAdmin.phoneNumberValue(),
                sellerAdmin.status().name(),
                sellerAdmin.authUserId(),
                sellerAdmin.createdAt(),
                sellerAdmin.updatedAt());
    }

    /**
     * SellerAdmin 목록을 SellerAdminApplicationResult 목록으로 변환합니다.
     *
     * @param sellerAdmins 셀러 관리자 목록
     * @return 변환된 Result DTO 목록
     */
    public List<SellerAdminApplicationResult> toResults(List<SellerAdmin> sellerAdmins) {
        return sellerAdmins.stream().map(this::toResult).toList();
    }

    /**
     * 페이징 결과를 생성합니다.
     *
     * @param sellerAdmins 셀러 관리자 목록
     * @param page 현재 페이지
     * @param size 페이지 크기
     * @param totalElements 전체 개수
     * @return 페이징 Result DTO
     */
    public SellerAdminApplicationPageResult toPageResult(
            List<SellerAdmin> sellerAdmins, int page, int size, long totalElements) {
        List<SellerAdminApplicationResult> content = toResults(sellerAdmins);
        return SellerAdminApplicationPageResult.of(content, page, size, totalElements);
    }

    /**
     * SellerAdmin을 SellerAdminResult로 변환합니다.
     *
     * <p>승인된 관리자 정보 조회 시 사용합니다.
     *
     * @param sellerAdmin 셀러 관리자 Entity
     * @return 변환된 관리자 Result DTO
     */
    public SellerAdminResult toAdminResult(SellerAdmin sellerAdmin) {
        return new SellerAdminResult(
                sellerAdmin.idValue(),
                sellerAdmin.sellerIdValue(),
                sellerAdmin.loginIdValue(),
                sellerAdmin.nameValue(),
                sellerAdmin.phoneNumberValue(),
                sellerAdmin.status().name(),
                sellerAdmin.authUserId(),
                toLocalDateTime(sellerAdmin.createdAt()),
                toLocalDateTime(sellerAdmin.updatedAt()));
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.of("Asia/Seoul")) : null;
    }
}
