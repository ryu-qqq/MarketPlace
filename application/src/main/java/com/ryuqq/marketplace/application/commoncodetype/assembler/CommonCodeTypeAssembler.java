package com.ryuqq.marketplace.application.commoncodetype.assembler;

import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypePageResult;
import com.ryuqq.marketplace.application.commoncodetype.dto.response.CommonCodeTypeResult;
import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * CommonCodeTypeAssembler - 공통 코드 타입 Assembler.
 *
 * <p>Domain → Result 변환 및 PageResult 생성을 담당합니다.
 *
 * <p>APP-ASM-001: 도메인별 구체 Result 클래스 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class CommonCodeTypeAssembler {

    /**
     * Domain → Result 변환.
     *
     * @param domain CommonCodeType 도메인 객체
     * @return CommonCodeTypeResult
     */
    public CommonCodeTypeResult toResult(CommonCodeType domain) {
        return CommonCodeTypeResult.from(domain);
    }

    /**
     * Domain List → Result List 변환.
     *
     * @param domains CommonCodeType 도메인 객체 목록
     * @return CommonCodeTypeResult 목록
     */
    public List<CommonCodeTypeResult> toResults(List<CommonCodeType> domains) {
        return domains.stream().map(this::toResult).toList();
    }

    /**
     * Domain List → CommonCodeTypePageResult 생성.
     *
     * <p>Domain 객체를 Result로 변환하여 PageResult를 생성합니다.
     *
     * @param domains 도메인 객체 목록
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @param totalElements 전체 요소 수
     * @return CommonCodeTypePageResult
     */
    public CommonCodeTypePageResult toPageResult(
            List<CommonCodeType> domains, int page, int size, long totalElements) {
        List<CommonCodeTypeResult> results = toResults(domains);
        return CommonCodeTypePageResult.of(results, page, size, totalElements);
    }
}
