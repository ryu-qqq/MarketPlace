ALTER TABLE seller_option_groups
    ADD COLUMN input_type VARCHAR(20) NOT NULL DEFAULT 'PREDEFINED'
    COMMENT '입력 유형 (PREDEFINED: 사전 정의, FREE_INPUT: 자유 입력)'
    AFTER canonical_option_group_id;
