-- Admin 메뉴 시드 데이터
-- required_role_level: VIEWER=0, EDITOR=1, ADMIN=2, SUPER_ADMIN=3

-- ========================================
-- 그룹 메뉴 (parent_id = NULL)
-- ========================================

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (1, NULL, '판매자 관리', NULL, 'Users', 1, 2, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (2, NULL, '주문 관리', NULL, 'LineChart', 2, 0, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (3, NULL, '고객 관리', NULL, 'MessageCircle', 3, 1, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (4, NULL, '상품 관리', NULL, 'Box', 4, 1, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (5, NULL, '외부연동 관리', NULL, 'ExternalLink', 5, 2, 1, NOW(6), NOW(6));

-- ========================================
-- 판매자 관리 하위 메뉴 (parent_id = 1)
-- ========================================

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (6, 1, '판매자 입점 관리', '/seller/management', 'UserCheck', 1, 2, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (7, 1, '판매자 정보 관리', '/seller/information', 'Users', 2, 2, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (8, 1, '판매자 유저 관리', '/seller/user-management', 'UserPlus', 3, 2, 1, NOW(6), NOW(6));

-- ========================================
-- 주문 관리 하위 메뉴 (parent_id = 2)
-- ========================================

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (9, 2, '통합주문 관리', '/order/management', 'ShoppingCart', 1, 0, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (10, 2, '발송 / 배송 관리', '/order/shipment', 'Truck', 2, 0, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (11, 2, '반품 관리', '/claim/refund', 'RotateCcw', 3, 1, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (12, 2, '교환 관리', '/claim/exchange', 'Repeat2', 4, 1, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (13, 2, '취소 관리', '/claim/cancel', 'X', 5, 1, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (14, 2, '정산 관리', '/order/settlement', 'BadgeDollarSign', 6, 3, 1, NOW(6), NOW(6));

-- ========================================
-- 고객 관리 하위 메뉴 (parent_id = 3)
-- ========================================

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (15, 3, '상품 문의 관리', '/qna/product', 'ShoppingBasket', 1, 1, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (16, 3, '주문 문의 관리', '/qna/order', 'ClipboardCheck', 2, 1, 1, NOW(6), NOW(6));

-- ========================================
-- 상품 관리 하위 메뉴 (parent_id = 4)
-- ========================================

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (17, 4, '상품 관리', '/product/management', 'Box', 1, 1, 1, NOW(6), NOW(6));

-- ========================================
-- 외부연동 관리 하위 메뉴 (parent_id = 5)
-- ========================================

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (18, 5, '카테고리 프리셋 관리', '/external/category-preset', 'ListChecks', 1, 2, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (19, 5, '브랜드 프리셋 관리', '/external/brand-preset', 'Cherry', 2, 2, 1, NOW(6), NOW(6));

INSERT INTO admin_menus (id, parent_id, title, url, icon_name, display_order, required_role_level, active, created_at, updated_at)
VALUES (20, 5, '외부연동 상품 관리', '/external/batch-product', 'Box', 3, 2, 1, NOW(6), NOW(6));
