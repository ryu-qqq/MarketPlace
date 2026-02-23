ALTER TABLE product_profiles
    ADD COLUMN previous_profile_id BIGINT NULL AFTER product_group_id;

CREATE INDEX idx_pp_previous_profile ON product_profiles (previous_profile_id);
