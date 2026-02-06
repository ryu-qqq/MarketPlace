package com.ryuqq.marketplace.application.common.port.out;

import java.util.List;
import java.util.Map;

/**
 * Stock Counter Port
 *
 * <p>재고 카운터 작업을 위한 아웃바운드 포트입니다.
 *
 * <p><strong>구현체:</strong>
 *
 * <ul>
 *   <li>StockCounterAdapter - Redis 기반 재고 카운터
 * </ul>
 *
 * @author development-team
 * @since 1.0.0
 */
public interface StockCounterPort {

    /**
     * 재고를 차감합니다.
     *
     * @param productStockId 상품 재고 ID
     * @param quantity 차감할 수량
     * @return 차감 후 남은 재고 (키가 없으면 -1)
     */
    int decrement(Long productStockId, int quantity);

    /**
     * 재고를 증가합니다.
     *
     * @param productStockId 상품 재고 ID
     * @param quantity 증가할 수량
     * @return 증가 후 재고 (키가 없으면 -1)
     */
    int increment(Long productStockId, int quantity);

    /**
     * 현재 재고를 조회합니다.
     *
     * @param productStockId 상품 재고 ID
     * @return 현재 재고 (키가 없으면 -1)
     */
    int getStock(Long productStockId);

    /**
     * 여러 상품의 재고를 조회합니다.
     *
     * @param productStockIds 상품 재고 ID 목록
     * @return 상품 재고 ID별 현재 재고
     */
    Map<Long, Integer> getStocks(List<Long> productStockIds);

    /**
     * 재고가 충분한지 확인합니다.
     *
     * @param productStockId 상품 재고 ID
     * @param requiredQuantity 필요 수량
     * @return 충분 여부
     */
    boolean hasStock(Long productStockId, int requiredQuantity);

    /**
     * 여러 상품의 재고가 모두 충분한지 확인합니다.
     *
     * @param stockRequirements 상품 재고 ID별 필요 수량
     * @return 모두 충분 여부
     */
    boolean hasStocks(Map<Long, Integer> stockRequirements);

    /**
     * 재고를 초기화합니다.
     *
     * @param productStockId 상품 재고 ID
     * @param quantity 초기 재고
     */
    void initialize(Long productStockId, int quantity);

    /**
     * 여러 상품의 재고를 일괄 초기화합니다.
     *
     * @param stocks 상품 재고 ID별 초기 재고
     */
    void initializeAll(Map<Long, Integer> stocks);

    /**
     * 재고 키가 존재하는지 확인합니다.
     *
     * @param productStockId 상품 재고 ID
     * @return 존재 여부
     */
    boolean exists(Long productStockId);

    /**
     * 재고 키를 삭제합니다.
     *
     * @param productStockId 상품 재고 ID
     */
    void delete(Long productStockId);
}
