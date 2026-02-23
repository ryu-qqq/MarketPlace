package com.ryuqq.marketplace.application.productintelligence.port.out.client;

import com.ryuqq.marketplace.domain.productintelligence.vo.NoticeSuggestion;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import java.util.List;

/**
 * Notice AI 분석 클라이언트. 누락된 고시정보 필드를 보강합니다.
 *
 * @param productNotice 상품 고시정보 도메인 객체
 * @param previousResults 이전 분석 결과 (컨텍스트로 전달, 최초 분석 시 빈 목록)
 */
public interface NoticeAnalysisAiClient {

    List<NoticeSuggestion> analyze(
            ProductNotice productNotice, List<NoticeSuggestion> previousResults);
}
