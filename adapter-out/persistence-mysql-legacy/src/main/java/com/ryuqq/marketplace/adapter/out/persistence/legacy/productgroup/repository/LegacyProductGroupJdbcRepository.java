package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroup.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * 레거시 상품그룹 JDBC Repository.
 *
 * <p>조회 없이 바로 UPDATE 칠 때 사용합니다.
 */
@Repository
public class LegacyProductGroupJdbcRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LegacyProductGroupJdbcRepository(
            @Qualifier("legacyNamedParameterJdbcTemplate") NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void update(
            long productGroupId,
            String productGroupName,
            long brandId,
            long categoryId,
            String optionType,
            long regularPrice,
            long currentPrice) {
        String sql =
                """
                UPDATE product_group
                SET PRODUCT_GROUP_NAME = :productGroupName,
                    BRAND_ID = :brandId,
                    CATEGORY_ID = :categoryId,
                    OPTION_TYPE = :optionType,
                    REGULAR_PRICE = :regularPrice,
                    CURRENT_PRICE = :currentPrice,
                    SALE_PRICE = :currentPrice
                WHERE product_group_id = :productGroupId
                """;

        MapSqlParameterSource params =
                new MapSqlParameterSource()
                        .addValue("productGroupId", productGroupId)
                        .addValue("productGroupName", productGroupName)
                        .addValue("brandId", brandId)
                        .addValue("categoryId", categoryId)
                        .addValue("optionType", optionType)
                        .addValue("regularPrice", regularPrice)
                        .addValue("currentPrice", currentPrice);

        jdbcTemplate.update(sql, params);
    }

    public void updateDisplayYn(long productGroupId, String displayYn) {
        jdbcTemplate.update(
                """
                UPDATE product_group
                SET DISPLAY_YN = :displayYn
                WHERE product_group_id = :productGroupId
                """,
                new MapSqlParameterSource()
                        .addValue("productGroupId", productGroupId)
                        .addValue("displayYn", displayYn));
    }

    public void markSoldOut(long productGroupId) {
        jdbcTemplate.update(
                """
                UPDATE product_group
                SET SOLD_OUT_YN = 'Y'
                WHERE product_group_id = :productGroupId
                """,
                new MapSqlParameterSource().addValue("productGroupId", productGroupId));
    }

    public void updatePrice(long productGroupId, long regularPrice, long currentPrice) {
        jdbcTemplate.update(
                """
                UPDATE product_group
                SET REGULAR_PRICE = :regularPrice,
                    CURRENT_PRICE = :currentPrice,
                    SALE_PRICE = :currentPrice
                WHERE product_group_id = :productGroupId
                """,
                new MapSqlParameterSource()
                        .addValue("productGroupId", productGroupId)
                        .addValue("regularPrice", regularPrice)
                        .addValue("currentPrice", currentPrice));
    }
}
