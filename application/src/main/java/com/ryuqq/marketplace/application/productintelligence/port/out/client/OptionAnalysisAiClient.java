package com.ryuqq.marketplace.application.productintelligence.port.out.client;

import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.util.List;

/**
 * Option AI 분석 클라이언트. 셀러 옵션을 캐노니컬 옵션에 매핑합니다.
 *
 * @param productGroup 상품그룹 도메인 객체
 * @param canonicalOptionGroups 매핑 대상 캐노니컬 옵션 그룹 목록
 * @param previousResults 이전 분석 결과 (컨텍스트로 전달, 최초 분석 시 빈 목록)
 */
public interface OptionAnalysisAiClient {

    List<OptionMappingSuggestion> analyze(
            ProductGroup productGroup,
            List<CanonicalOptionGroup> canonicalOptionGroups,
            List<OptionMappingSuggestion> previousResults);
}
