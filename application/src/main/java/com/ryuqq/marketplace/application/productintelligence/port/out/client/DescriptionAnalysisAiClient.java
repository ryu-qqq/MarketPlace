package com.ryuqq.marketplace.application.productintelligence.port.out.client;

import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.util.List;

/**
 * Description AI 분석 클라이언트. 상세설명 텍스트/이미지에서 속성을 추출합니다.
 *
 * @param description 상품그룹 상세설명 도메인 객체
 * @param previousResults 이전 분석 결과 (컨텍스트로 전달, 최초 분석 시 빈 목록)
 */
public interface DescriptionAnalysisAiClient {

    List<ExtractedAttribute> analyze(
            ProductGroupDescription description, List<ExtractedAttribute> previousResults);
}
