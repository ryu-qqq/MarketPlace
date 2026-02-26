-- ================================================
-- V20250029: SalesChannel 시드 데이터
-- 주요 판매 채널 마스터 데이터
-- ================================================

INSERT INTO sales_channel (code, name, type, description, status) VALUES
-- 내부 채널
('INTERNAL', '자사몰', 'INTERNAL', '자사 직접 운영 쇼핑몰', 'ACTIVE'),

-- 국내 주요 마켓플레이스
('COUPANG', '쿠팡', 'EXTERNAL', '쿠팡 마켓플레이스', 'ACTIVE'),
('NAVER', '네이버 스마트스토어', 'EXTERNAL', '네이버 스마트스토어', 'ACTIVE'),
('GMARKET', 'G마켓', 'EXTERNAL', '이베이코리아 G마켓', 'ACTIVE'),
('AUCTION', '옥션', 'EXTERNAL', '이베이코리아 옥션', 'ACTIVE'),
('11ST', '11번가', 'EXTERNAL', 'SK플래닛 11번가', 'ACTIVE'),
('TMON', '티몬', 'EXTERNAL', '티켓몬스터', 'ACTIVE'),
('WEMAKEPRICE', '위메프', 'EXTERNAL', '위메프', 'ACTIVE'),
('SSG', 'SSG.COM', 'EXTERNAL', '신세계그룹 SSG.COM', 'ACTIVE'),
('LOTTE_ON', '롯데온', 'EXTERNAL', '롯데그룹 롯데온', 'ACTIVE'),
('KAKAO', '카카오쇼핑', 'EXTERNAL', '카카오 쇼핑하기/선물하기', 'ACTIVE'),

-- 럭셔리/패션 전문 플랫폼
('MUSINSA', '무신사', 'EXTERNAL', '무신사 스토어', 'ACTIVE'),
('W_CONCEPT', 'W컨셉', 'EXTERNAL', 'W컨셉', 'ACTIVE'),
('29CM', '29CM', 'EXTERNAL', '29CM', 'ACTIVE'),
('ABLY', '에이블리', 'EXTERNAL', '에이블리', 'ACTIVE'),
('ZIGZAG', '지그재그', 'EXTERNAL', '카카오스타일 지그재그', 'ACTIVE'),
('BALAAN', '발란', 'EXTERNAL', '명품 플랫폼 발란', 'ACTIVE'),
('TRENBE', '트렌비', 'EXTERNAL', '럭셔리 플랫폼 트렌비', 'ACTIVE'),
('MUSTIT', '머스트잇', 'EXTERNAL', '명품 플랫폼 머스트잇', 'ACTIVE'),

-- 해외 마켓플레이스
('FARFETCH', 'Farfetch', 'EXTERNAL', '글로벌 럭셔리 플랫폼', 'ACTIVE'),
('MYTHERESA', 'Mytheresa', 'EXTERNAL', '독일 럭셔리 플랫폼', 'ACTIVE'),
('MATCHESFASHION', 'Matches Fashion', 'EXTERNAL', '영국 럭셔리 플랫폼', 'ACTIVE'),
('NET_A_PORTER', 'Net-a-Porter', 'EXTERNAL', 'Richemont 럭셔리 플랫폼', 'ACTIVE'),
('SSENSE', 'SSENSE', 'EXTERNAL', '캐나다 럭셔리 플랫폼', 'ACTIVE'),
('LUISAVIAROMA', 'LUISAVIAROMA', 'EXTERNAL', '이탈리아 럭셔리 플랫폼', 'ACTIVE'),
('AMAZON', 'Amazon', 'EXTERNAL', '아마존', 'ACTIVE'),
('EBAY', 'eBay', 'EXTERNAL', '이베이', 'ACTIVE'),
('SHOPIFY', 'Shopify', 'EXTERNAL', '쇼피파이 기반 스토어', 'ACTIVE'),

-- 중국 마켓플레이스
('TMALL', 'Tmall', 'EXTERNAL', '알리바바 티몰', 'ACTIVE'),
('JD', 'JD.com', 'EXTERNAL', '징동닷컴', 'ACTIVE'),
('TAOBAO', 'Taobao', 'EXTERNAL', '알리바바 타오바오', 'ACTIVE'),

-- 동남아 마켓플레이스
('LAZADA', 'Lazada', 'EXTERNAL', '라자다 동남아', 'ACTIVE'),
('SHOPEE', 'Shopee', 'EXTERNAL', '쇼피 동남아', 'ACTIVE'),

-- 일본 마켓플레이스
('RAKUTEN', 'Rakuten', 'EXTERNAL', '라쿠텐', 'ACTIVE'),
('ZOZOTOWN', 'ZOZOTOWN', 'EXTERNAL', '조조타운', 'ACTIVE'),
('YAHOO_JP', 'Yahoo! Japan', 'EXTERNAL', '야후 재팬 쇼핑', 'ACTIVE');
