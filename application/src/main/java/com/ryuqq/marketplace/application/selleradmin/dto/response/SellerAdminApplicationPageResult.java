package com.ryuqq.marketplace.application.selleradmin.dto.response;

import com.ryuqq.marketplace.domain.common.vo.PageMeta;
import java.util.List;

/**
 * 셀러 관리자 가입 신청 목록 Result (페이징).
 *
 * @param content 신청 목록
 * @param pageMeta 페이지 메타 정보
 * @author ryu-qqq
 * @since 1.1.0
 */
public record SellerAdminApplicationPageResult(
        List<SellerAdminApplicationResult> content, PageMeta pageMeta) {

    /**
     * 페이지 결과 생성 팩토리 메서드.
     *
     * @param content 컨텐츠 목록
     * @param page 현재 페이지
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return SellerAdminApplicationPageResult
     */
    public static SellerAdminApplicationPageResult of(
            List<SellerAdminApplicationResult> content, int page, int size, long totalElements) {
        return new SellerAdminApplicationPageResult(
                content, PageMeta.of(page, size, totalElements));
    }
}
