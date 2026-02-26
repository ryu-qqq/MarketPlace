package com.ryuqq.marketplace.application.commoncode.assembler;

import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodePageResult;
import com.ryuqq.marketplace.application.commoncode.dto.response.CommonCodeResult;
import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeAssembler - 공통 코드 Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 *
 * <p>APP-ASM-001: 도메인별 구체 Result 클래스 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeAssembler {

    /**
     * Domain → Result 변환.
     *
     * @param domain CommonCode 도메인 객체
     * @return CommonCodeResult
     */
    public CommonCodeResult toResult(CommonCode domain) {
        return CommonCodeResult.from(domain);
    }

    /**
     * Domain List → Result List 변환.
     *
     * @param domains CommonCode 도메인 객체 목록
     * @return CommonCodeResult 목록
     */
    public List<CommonCodeResult> toResults(List<CommonCode> domains) {
        return domains.stream().map(this::toResult).toList();
    }

    /**
     * Domain List → CommonCodePageResult 생성.
     *
     * <p>Domain 객체를 Result로 변환하여 PageResult를 생성합니다.
     *
     * @param domains 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return CommonCodePageResult
     */
    public CommonCodePageResult toPageResult(
            List<CommonCode> domains, int page, int size, long totalElements) {
        List<CommonCodeResult> results = toResults(domains);
        return CommonCodePageResult.of(results, page, size, totalElements);
    }
}
