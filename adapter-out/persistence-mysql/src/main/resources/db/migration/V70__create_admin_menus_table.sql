CREATE TABLE admin_menus (
    id                  BIGINT AUTO_INCREMENT PRIMARY KEY,
    parent_id           BIGINT NULL,
    title               VARCHAR(50)  NOT NULL,
    url                 VARCHAR(200) NULL,
    icon_name           VARCHAR(50)  NOT NULL,
    display_order       INT          NOT NULL DEFAULT 0,
    required_role_level INT          NOT NULL DEFAULT 0,
    active              TINYINT(1)   NOT NULL DEFAULT 1,
    created_at          DATETIME(6)  NOT NULL,
    updated_at          DATETIME(6)  NOT NULL,
    INDEX idx_admin_menus_parent_order (parent_id, display_order),
    INDEX idx_admin_menus_active_role (active, required_role_level)
);
