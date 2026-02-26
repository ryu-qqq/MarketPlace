package com.ryuqq.marketplace.application.productintelligence.port.out.query;

import com.ryuqq.marketplace.domain.productintelligence.aggregate.ProductProfile;
import java.util.List;
import java.util.Optional;

/** 상품 프로파일 Query Port. */
public interface ProductProfileQueryPort {

    /**
     * ID로 프로파일 단건 조회.
     *
     * @param profileId 프로파일 ID
     * @return 프로파일
     */
    Optional<ProductProfile> findById(Long profileId);

    /**
     * 상품그룹 ID로 최신 프로파일 단건 조회 (버전 기준).
     *
     * @param productGroupId 상품그룹 ID
     * @return 최신 프로파일
     */
    Optional<ProductProfile> findLatestByProductGroupId(Long productGroupId);

    /**
     * 상품그룹 ID로 활성(만료되지 않은) 최신 프로파일 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return 최신 활성 프로파일
     */
    Optional<ProductProfile> findLatestActiveByProductGroupId(Long productGroupId);

    /**
     * 상품그룹 ID로 최신 완료 프로파일 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return 최신 COMPLETED 프로파일
     */
    Optional<ProductProfile> findLatestCompletedByProductGroupId(Long productGroupId);

    /**
     * 상품그룹 ID로 모든 프로파일 이력 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return 프로파일 이력 목록
     */
    List<ProductProfile> findAllByProductGroupId(Long productGroupId);

    /**
     * ANALYZING 상태에서 모든 분석 완료 후 Aggregation 발행이 누락된 프로파일 조회.
     *
     * @param stuckThreshold 이 시각 이전에 업데이트된 건만 조회
     * @param limit 최대 조회 개수
     * @return stuck 프로파일 목록
     */
    List<ProductProfile> findStuckAnalyzingProfiles(java.time.Instant stuckThreshold, int limit);
}
