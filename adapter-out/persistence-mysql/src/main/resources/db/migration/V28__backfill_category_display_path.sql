-- depth 0: 루트 카테고리는 자기 이름이 곧 display_path
UPDATE category SET display_path = name_ko WHERE depth = 0;

-- depth 1: 부모(depth 0) display_path + ' > ' + 자신 name_ko
UPDATE category c
JOIN category p ON c.parent_id = p.id
SET c.display_path = CONCAT(p.display_path, ' > ', c.name_ko)
WHERE c.depth = 1;

-- depth 2
UPDATE category c
JOIN category p ON c.parent_id = p.id
SET c.display_path = CONCAT(p.display_path, ' > ', c.name_ko)
WHERE c.depth = 2;

-- depth 3
UPDATE category c
JOIN category p ON c.parent_id = p.id
SET c.display_path = CONCAT(p.display_path, ' > ', c.name_ko)
WHERE c.depth = 3;

-- depth 4
UPDATE category c
JOIN category p ON c.parent_id = p.id
SET c.display_path = CONCAT(p.display_path, ' > ', c.name_ko)
WHERE c.depth = 4;
