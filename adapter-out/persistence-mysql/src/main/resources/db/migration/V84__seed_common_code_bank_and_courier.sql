-- ============================================
-- 은행 코드 및 택배사 코드 공통 코드 시드
-- ============================================
-- BankCode: 금융결제원(KFTC) 3자리 기관코드 기반
-- CourierCode: Sweet Tracker 숫자 코드 기반

-- ============================================
-- 1. common_code_types (id: 34 ~ 35)
-- ============================================

INSERT INTO common_code_types (id, code, name, description, display_order, is_active) VALUES
(34, 'BANK_CODE',    '은행 코드',   '금융결제원 3자리 기관코드 기반', 34, 1),
(35, 'COURIER_CODE', '택배사 코드', 'Sweet Tracker 숫자 코드 기반',   35, 1);

-- ============================================
-- 2. common_codes - BANK_CODE
-- ============================================

-- 시중은행
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(34, 'KB_KOOKMIN', 'KB국민은행',       1, 1),
(34, 'SHINHAN',    '신한은행',         2, 1),
(34, 'WOORI',      '우리은행',         3, 1),
(34, 'HANA',       '하나은행',         4, 1),
(34, 'SC',         'SC제일은행',       5, 1),
(34, 'CITI',       '한국씨티은행',     6, 1);

-- 특수은행
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(34, 'IBK',        'IBK기업은행',      7, 1),
(34, 'NH',         'NH농협은행',       8, 1),
(34, 'NH_LOCAL',   '단위농협',         9, 1),
(34, 'SUHYUP',     'Sh수협은행',       10, 1),
(34, 'KDB',        '한국산업은행',     11, 1),
(34, 'EXIM',       '수출입은행',       12, 1);

-- 지방은행
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(34, 'IM_BANK',    'iM뱅크(대구)',     13, 1),
(34, 'BUSAN',      '부산은행',         14, 1),
(34, 'GWANGJU',    '광주은행',         15, 1),
(34, 'JEJU',       '제주은행',         16, 1),
(34, 'JEONBUK',    '전북은행',         17, 1),
(34, 'GYEONGNAM',  '경남은행',         18, 1);

-- 인터넷전문은행
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(34, 'K_BANK',      '케이뱅크',        19, 1),
(34, 'KAKAO_BANK',  '카카오뱅크',      20, 1),
(34, 'TOSS_BANK',   '토스뱅크',        21, 1);

-- 제2금융권
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(34, 'SAEMAUL',      '새마을금고',      22, 1),
(34, 'SHINHYUP',     '신협',            23, 1),
(34, 'SAVINGS_BANK', '상호저축은행',    24, 1),
(34, 'SANLIM',       '산림조합',        25, 1),
(34, 'POST_OFFICE',  '우체국',          26, 1);

-- ============================================
-- 3. common_codes - COURIER_CODE
-- ============================================

-- 주요 택배사
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(35, 'KOREA_POST',     '우체국택배',             1, 1),
(35, 'CJ_LOGISTICS',   'CJ대한통운',             2, 1),
(35, 'HANJIN',         '한진택배',               3, 1),
(35, 'LOGEN',          '로젠택배',               4, 1),
(35, 'LOTTE',          '롯데택배',               5, 1),
(35, 'ILYANG',         '일양로지스',             6, 1),
(35, 'HANJIN_LOVE',    '한의사랑택배',           7, 1),
(35, 'CHUNIL',         '천일택배',               8, 1),
(35, 'KUNYOUNG',       '건영택배',               9, 1),
(35, 'HANDEX',         '한덱스',                10, 1),
(35, 'DAESIN',         '대신택배',              11, 1),
(35, 'KYUNGDONG',      '경동택배',              12, 1),
(35, 'GS_POSTBOX',     'GS Postbox 택배',       13, 1),
(35, 'HAPDONG',        '합동택배',              14, 1);

-- 특수/물류 택배사
INSERT INTO common_codes (common_code_type_id, code, display_name, display_order, is_active) VALUES
(35, 'GOODTOLUCK',     '굿투럭',                15, 1),
(35, 'ANYTRACK',       '애니트랙',              16, 1),
(35, 'SLX',            'SLX택배',               17, 1),
(35, 'WOORI',          '우리택배',              18, 1),
(35, 'CU_POST',        'CU 편의점택배',         19, 1),
(35, 'WOORI_HANBANG',  '우리한방택배',          20, 1),
(35, 'NONGHYUP',       '농협택배',              21, 1),
(35, 'HOMEPICK',       '홈픽택배',              22, 1),
(35, 'IK_LOGISTICS',   'IK물류',                23, 1),
(35, 'SUNGHUN',        '성훈물류',              24, 1),
(35, 'YONGMA',         '용마로지스',            25, 1),
(35, 'WONDERS_QUICK',  '원더스퀵',              26, 1),
(35, 'LOGISVALLEY',    '로지스밸리택배',        27, 1),
(35, 'KURLY',          '컬리로지스',            28, 1),
(35, 'FULL_AT_HOME',   '풀앳홈',                29, 1),
(35, 'SAMSUNG',        '삼성전자물류',          30, 1),
(35, 'CURUN',          '큐런택배',              31, 1),
(35, 'DOOBAL_HERO',    '두발히어로',            32, 1),
(35, 'WINIADIMCHAE',   '위니아딤채',            33, 1),
(35, 'GENIEGO',        '지니고 당일배송',       34, 1),
(35, 'TODAYS_PICKUP',  '오늘의픽업',            35, 1),
(35, 'LOGISVALLEY_SAME','로지스밸리',           36, 1),
(35, 'HANSEM',         '한샘서비스원 택배',     37, 1),
(35, 'NDEX_KOREA',     'NDEX KOREA',            38, 1),
(35, 'DODOFLEX',       '도도플렉스',            39, 1),
(35, 'LG_PANTOS',      'LG전자(판토스)',        40, 1),
(35, 'VROONG',         '부릉',                  41, 1),
(35, 'HOME_1004',      '1004홈',                42, 1),
(35, 'THUNDER_HERO',   '썬더히어로',            43, 1),
(35, 'TEAMFRESH',      '팀프레시',              44, 1),
(35, 'LOTTE_CHILSUNG', '롯데칠성',              45, 1),
(35, 'PINGPONG',       '핑퐁',                  46, 1),
(35, 'VALLEX',         '발렉스 특수물류',       47, 1),
(35, 'NTLPS',          '엔티엘피스',            48, 1),
(35, 'GTS_LOGIS',      'GTS로지스',             49, 1),
(35, 'LOGISPOT',       '로지스팟',              50, 1),
(35, 'HOMEPICK_TODAY', '홈픽 오늘도착',         51, 1),
(35, 'UFO_LOGIS',      'UFO로지스',             52, 1),
(35, 'DELI_RABBIT',    '딜리래빗',              53, 1),
(35, 'GEOPI',          '지오피',                54, 1),
(35, 'HK_HOLDINGS',    '에이치케이홀딩스',      55, 1),
(35, 'HTNS',           'HTNS',                  56, 1),
(35, 'KJT',            '케이제이티',            57, 1),
(35, 'THE_BAO',        '더바오',                58, 1),
(35, 'LAST_MILE',      '라스트마일',            59, 1),
(35, 'ONEULHOE_RUSH',  '오늘회 러쉬',           60, 1),
(35, 'TANGO_AND_GO',   '탱고앤고',              61, 1),
(35, 'TODAY',          '투데이',                62, 1),
(35, 'MANUAL',         '수동처리(퀵, 방문수령 등)', 63, 1);
