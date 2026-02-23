package com.ryuqq.marketplace.application.productintelligence.port.out.query;

import com.ryuqq.marketplace.domain.productintelligence.vo.ExtractedAttribute;
import java.util.List;

/**
 * Description 입력 데이터 변경 감지기.
 *
 * <p>현재 상품 설명 데이터와 이전 분석 시점의 데이터를 비교하여 재분석 필요 여부를 판단합니다. Approach B(Analyzer Self-Judgment)의 핵심
 * 인터페이스로, Processor가 AI 호출 전에 코드 레벨에서 변경 여부를 판단하는 데 사용합니다.
 *
 * <p>Phase 2에서 실제 비교 로직 구현 예정:
 *
 * <ul>
 *   <li>상품 상세설명 HTML 해시 비교
 *   <li>이미지 URL 목록 비교
 * </ul>
 */
public interface DescriptionChangeDetector {

    /**
     * 상품 설명 데이터가 이전 분석 시점 대비 변경되었는지 확인합니다.
     *
     * @param productGroupId 상품그룹 ID
     * @param previousResults 이전 프로파일의 분석 결과
     * @return 변경 감지 시 true, 변경 없으면 false
     */
    boolean hasChanged(Long productGroupId, List<ExtractedAttribute> previousResults);
}
