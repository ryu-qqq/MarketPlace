package com.ryuqq.marketplace.adapter.in.rest.inboundproduct;

/** InboundProduct Admin API 엔드포인트 상수. */
public final class InboundProductAdminEndpoints {

    private InboundProductAdminEndpoints() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static final String BASE = "/api/v1/market";

    public static final String INBOUND_PRODUCTS = BASE + "/inbound/products";

    public static final String PATH_EXTERNAL_SOURCE_ID = "inboundSourceId";
    public static final String PATH_EXTERNAL_PRODUCT_CODE = "externalProductCode";

    public static final String INBOUND_PRODUCT_ID =
            INBOUND_PRODUCTS
                    + "/{"
                    + PATH_EXTERNAL_SOURCE_ID
                    + "}/{"
                    + PATH_EXTERNAL_PRODUCT_CODE
                    + "}";

    public static final String PRICE = INBOUND_PRODUCT_ID + "/price";
    public static final String STOCK = INBOUND_PRODUCT_ID + "/stock";
    public static final String IMAGES = INBOUND_PRODUCT_ID + "/images";
    public static final String DESCRIPTION = INBOUND_PRODUCT_ID + "/description";
    public static final String PRODUCTS = INBOUND_PRODUCT_ID + "/products";
}
