ALTER TABLE inbound_products
    ADD COLUMN resolved_shipping_policy_id BIGINT NULL AFTER raw_payload,
    ADD COLUMN resolved_refund_policy_id   BIGINT NULL AFTER resolved_shipping_policy_id,
    ADD COLUMN resolved_notice_category_id BIGINT NULL AFTER resolved_refund_policy_id;
