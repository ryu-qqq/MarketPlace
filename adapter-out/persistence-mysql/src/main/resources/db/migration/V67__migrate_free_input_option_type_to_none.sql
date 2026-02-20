-- OptionType.FREE_INPUT 제거: 기존 FREE_INPUT → NONE 일괄 변환
UPDATE product_group SET option_type = 'NONE' WHERE option_type = 'FREE_INPUT';
