-- ============================================
-- 캐노니컬 옵션 시드 데이터 - COLOR
-- ============================================
-- 셀러 자유입력 컬러를 정규화하는 시스템 표준 컬러 목록
-- ============================================

-- ============================================
-- 1. 캐노니컬 옵션 그룹: COLOR
-- ============================================
INSERT INTO canonical_option_group (id, code, name_ko, name_en, active) VALUES
(1, 'COLOR', '컬러', 'Color', TRUE);

-- ============================================
-- 2. 캐노니컬 옵션 값: COLOR (20개)
-- ============================================
INSERT INTO canonical_option_value (canonical_option_group_id, code, name_ko, name_en, sort_order) VALUES
(1, 'BLACK',        '블랙',     'Black',        1),
(1, 'WHITE',        '화이트',   'White',        2),
(1, 'GRAY',         '그레이',   'Gray',         3),
(1, 'NAVY',         '네이비',   'Navy',         4),
(1, 'BEIGE',        '베이지',   'Beige',        5),
(1, 'BROWN',        '브라운',   'Brown',        6),
(1, 'RED',          '레드',     'Red',          7),
(1, 'PINK',         '핑크',     'Pink',         8),
(1, 'ORANGE',       '오렌지',   'Orange',       9),
(1, 'YELLOW',       '옐로우',   'Yellow',       10),
(1, 'GREEN',        '그린',     'Green',        11),
(1, 'BLUE',         '블루',     'Blue',         12),
(1, 'PURPLE',       '퍼플',     'Purple',       13),
(1, 'WINE',         '와인',     'Wine',         14),
(1, 'KHAKI',        '카키',     'Khaki',        15),
(1, 'IVORY',        '아이보리', 'Ivory',        16),
(1, 'GOLD',         '골드',     'Gold',         17),
(1, 'SILVER',       '실버',     'Silver',       18),
(1, 'MULTI_COLOR',  '멀티컬러', 'Multi Color',  19),
(1, 'TRANSPARENT',  '투명',     'Transparent',  20);
