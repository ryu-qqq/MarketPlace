package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상품 JDBC Repository.
 *
 * <p>product + product_stock + product_option을 productGroupId 기준으로 일괄 soft delete.
 */
@Repository
public class LegacyProductJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LegacyProductJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void softDeleteByProductGroupId(long productGroupId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("productGroupId", productGroupId);

        jdbcTemplate.update(
                """
                UPDATE product_option po
                INNER JOIN product p ON po.PRODUCT_ID = p.product_id
                SET po.DELETE_YN = 'Y'
                WHERE p.PRODUCT_GROUP_ID = :productGroupId AND po.DELETE_YN = 'N'
                """,
                params);

        jdbcTemplate.update(
                """
                UPDATE product_stock ps
                INNER JOIN product p ON ps.product_id = p.product_id
                SET ps.delete_yn = 'Y'
                WHERE p.PRODUCT_GROUP_ID = :productGroupId AND ps.delete_yn = 'N'
                """,
                params);

        jdbcTemplate.update(
                """
                UPDATE product
                SET delete_yn = 'Y'
                WHERE PRODUCT_GROUP_ID = :productGroupId AND delete_yn = 'N'
                """,
                params);
    }

    public void updateStock(long productId, int stockQuantity) {
        jdbcTemplate.update(
                """
                UPDATE product_stock
                SET STOCK_QUANTITY = :stockQuantity
                WHERE product_id = :productId
                """,
                new MapSqlParameterSource()
                        .addValue("productId", productId)
                        .addValue("stockQuantity", stockQuantity));
    }
}
