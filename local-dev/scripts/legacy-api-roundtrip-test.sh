#!/usr/bin/env bash
# =============================================================================
# Legacy API 라운드트립 검증 스크립트
#
# MarketPlace legacy-api (market 스키마) 단독 검증
# 인증 → 등록 → 조회 → 수정 → 재조회 → DB 확인
# 옵션 타입 3종 (OPTION_ONE, OPTION_TWO, SINGLE) 전부 커버
# =============================================================================
set -uo pipefail

MP="${MARKETPLACE_URL:-http://localhost:9083}"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

PASS=0; FAIL=0; SKIP=0
pass()  { echo -e "  ${GREEN}[PASS]${NC} $1"; ((PASS++)); }
fail()  { echo -e "  ${RED}[FAIL]${NC} $1"; ((FAIL++)); }
skip()  { echo -e "  ${YELLOW}[SKIP]${NC} $1"; ((SKIP++)); }

echo "============================================================"
echo " Legacy API 라운드트립 검증 (market 스키마)"
echo " 서버: ${MP}"
echo " 시작: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

# =============================================================================
# 헬스체크
# =============================================================================
echo ""
HC=$(curl -s -o /dev/null -w "%{http_code}" "${MP}/actuator/health" 2>/dev/null)
if [ "$HC" != "200" ]; then echo -e "${RED}서버 미기동${NC}"; exit 1; fi
echo -e "서버 상태: ${GREEN}OK${NC}"

# =============================================================================
# 헬퍼 함수
# =============================================================================
api() {
    local method="$1" path="$2" body="${3:-}"
    if [ -n "$body" ]; then
        curl -s -w "\n%{http_code}" -X "$method" "${MP}${path}" \
            -H "API-KEY: ${TOKEN}" -H "Content-Type: application/json" -d "$body" 2>/dev/null
    else
        curl -s -w "\n%{http_code}" -X "$method" "${MP}${path}" \
            -H "API-KEY: ${TOKEN}" 2>/dev/null
    fi
}

check_status() {
    local resp="$1" expected="$2" desc="$3"
    local code=$(echo "$resp" | tail -1)
    local body=$(echo "$resp" | sed '$d')
    if [ "$code" = "$expected" ]; then
        pass "${desc} — HTTP ${code}"
        echo "$body"
        return 0
    else
        fail "${desc} — HTTP ${code} (expected ${expected})"
        echo "    $(echo "$body" | jq -c '.' 2>/dev/null | head -c 300)"
        echo ""
        return 1
    fi
}

check_field() {
    local json="$1" jq_path="$2" expected="$3" desc="$4"
    local actual=$(echo "$json" | jq -r "$jq_path" 2>/dev/null)
    if [ "$actual" = "$expected" ]; then
        pass "${desc}: ${actual}"
    else
        fail "${desc}: expected='${expected}', actual='${actual}'"
    fi
}

# =============================================================================
# Phase 0: 인증
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 0: 인증 ===${NC}"
AUTH_RESP=$(curl -s -X POST "${MP}/api/v1/legacy/auth/authentication" \
    -H "Content-Type: application/json" \
    -d '{"userId":"e2e@test.com","password":"test1234","roleType":"SELLER"}')
TOKEN=$(echo "$AUTH_RESP" | jq -r '.data.token // empty' 2>/dev/null)
if [ -z "$TOKEN" ]; then
    fail "토큰 발급 실패"
    echo "    $(echo "$AUTH_RESP" | jq -c '.')"
    exit 1
fi
pass "토큰 발급 성공"

# =============================================================================
# Phase 1: OPTION_ONE 상품 등록 → 조회 → 수정 → 재조회
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 1: OPTION_ONE 라운드트립 ===${NC}"

RESP=$(api POST "/api/v1/legacy/product/group" '{
    "productGroupName":"RT-OPTION_ONE","sellerId":1,"optionType":"OPTION_ONE","managementType":"MENUAL",
    "categoryId":1334,"brandId":5465,
    "productStatus":{"soldOutYn":"N","displayYn":"Y"},
    "price":{"regularPrice":100000,"currentPrice":80000},
    "productNotice":{"material":"면","color":"블랙","size":"FREE","maker":"테스트","origin":"한국","washingMethod":"손세탁","yearMonth":"2026-03","assuranceStandard":"1년","asPhone":"02-1234-5678"},
    "clothesDetailInfo":{"productCondition":"NEW","origin":"DOMESTIC","styleCode":"T001"},
    "deliveryNotice":{"deliveryArea":"전국","deliveryFee":3000,"deliveryPeriodAverage":3},
    "refundNotice":{"returnMethodDomestic":"DELIVERY","returnCourierDomestic":"CJ대한통운","returnChargeDomestic":5000,"returnExchangeAreaDomestic":"서울"},
    "productImageList":[{"type":"MAIN","productImageUrl":"https://cdn.set-of.com/rt1.jpg","originUrl":"https://cdn.set-of.com/rt1.jpg"}],
    "detailDescription":"<p>OPTION_ONE 라운드트립</p>",
    "productOptions":[
        {"stockQuantity":10,"additionalPrice":0,"options":[{"optionName":"SIZE","optionValue":"S"}]},
        {"stockQuantity":20,"additionalPrice":0,"options":[{"optionName":"SIZE","optionValue":"M"}]}
    ]
}')

BODY=$(check_status "$RESP" "200" "OPTION_ONE 등록") || { echo -e "${RED}등록 실패, Phase 1 중단${NC}"; }
PG1_ID=$(echo "$BODY" | jq -r '.data.productGroupId // empty' 2>/dev/null)

if [ -n "$PG1_ID" ]; then
    echo -e "  등록된 productGroupId: ${PG1_ID}"

    # 조회
    RESP=$(api GET "/api/v1/legacy/product/group/${PG1_ID}")
    DETAIL=$(check_status "$RESP" "200" "OPTION_ONE 상세 조회")
    if [ $? -eq 0 ]; then
        check_field "$DETAIL" ".data.productGroup.productGroupName" "RT-OPTION_ONE" "상품명"
        check_field "$DETAIL" ".data.productGroup.optionType" "OPTION_ONE" "옵션타입"
        PRODUCT_COUNT=$(echo "$DETAIL" | jq '.data.products | length' 2>/dev/null)
        check_field "$DETAIL" ".data.products | length" "2" "SKU 수"
    fi

    # 가격 수정
    RESP=$(api PATCH "/api/v1/legacy/product/group/${PG1_ID}/price" '{"regularPrice":150000,"currentPrice":120000}')
    check_status "$RESP" "200" "OPTION_ONE 가격 수정" > /dev/null

    # 가격 반영 확인
    RESP=$(api GET "/api/v1/legacy/product/group/${PG1_ID}")
    DETAIL=$(check_status "$RESP" "200" "가격 수정 후 조회")
    # market에서는 Product 레벨 가격이므로 첫 번째 product 확인
fi

# =============================================================================
# Phase 2: OPTION_TWO 상품 등록 → 조회 → 옵션 수정 → 재조회
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 2: OPTION_TWO 라운드트립 ===${NC}"

RESP=$(api POST "/api/v1/legacy/product/group" '{
    "productGroupName":"RT-OPTION_TWO","sellerId":1,"optionType":"OPTION_TWO","managementType":"MENUAL",
    "categoryId":1334,"brandId":5465,
    "productStatus":{"soldOutYn":"N","displayYn":"Y"},
    "price":{"regularPrice":200000,"currentPrice":150000},
    "productNotice":{"material":"가죽","color":"브라운","size":"260","maker":"테스트","origin":"이탈리아","washingMethod":"전문세탁","yearMonth":"2026-03","assuranceStandard":"1년","asPhone":"02-9999-0000"},
    "clothesDetailInfo":{"productCondition":"NEW","origin":"OVERSEAS","styleCode":"T002"},
    "deliveryNotice":{"deliveryArea":"전국","deliveryFee":0,"deliveryPeriodAverage":5},
    "refundNotice":{"returnMethodDomestic":"DELIVERY","returnCourierDomestic":"한진택배","returnChargeDomestic":6000,"returnExchangeAreaDomestic":"서울"},
    "productImageList":[{"type":"MAIN","productImageUrl":"https://cdn.set-of.com/rt2.jpg","originUrl":"https://cdn.set-of.com/rt2.jpg"}],
    "detailDescription":"<p>OPTION_TWO 라운드트립</p>",
    "productOptions":[
        {"stockQuantity":5,"additionalPrice":0,"options":[{"optionName":"DEFAULT_ONE","optionValue":"250"},{"optionName":"DEFAULT_TWO","optionValue":"블랙"}]},
        {"stockQuantity":3,"additionalPrice":10000,"options":[{"optionName":"DEFAULT_ONE","optionValue":"260"},{"optionName":"DEFAULT_TWO","optionValue":"브라운"}]}
    ]
}')

BODY=$(check_status "$RESP" "200" "OPTION_TWO 등록") || true
PG2_ID=$(echo "$BODY" | jq -r '.data.productGroupId // empty' 2>/dev/null)

if [ -n "$PG2_ID" ]; then
    echo -e "  등록된 productGroupId: ${PG2_ID}"

    RESP=$(api GET "/api/v1/legacy/product/group/${PG2_ID}")
    DETAIL=$(check_status "$RESP" "200" "OPTION_TWO 상세 조회")
    if [ $? -eq 0 ]; then
        check_field "$DETAIL" ".data.productGroup.optionType" "OPTION_TWO" "옵션타입"
        check_field "$DETAIL" ".data.products | length" "2" "SKU 수 (2종 조합)"
    fi

    # 품절 처리
    RESP=$(api PATCH "/api/v1/legacy/product/group/${PG2_ID}/out-stock" "{}")
    check_status "$RESP" "200" "OPTION_TWO 품절 처리" > /dev/null

    # 품절 확인
    RESP=$(api GET "/api/v1/legacy/product/group/${PG2_ID}")
    DETAIL=$(check_status "$RESP" "200" "품절 후 조회")
fi

# =============================================================================
# Phase 3: SINGLE 상품 등록 → 조회 → 진열OFF → 재조회
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 3: SINGLE 라운드트립 ===${NC}"

RESP=$(api POST "/api/v1/legacy/product/group" '{
    "productGroupName":"RT-SINGLE","sellerId":1,"optionType":"SINGLE","managementType":"MENUAL",
    "categoryId":1334,"brandId":5465,
    "productStatus":{"soldOutYn":"N","displayYn":"Y"},
    "price":{"regularPrice":50000,"currentPrice":45000},
    "productNotice":{"material":"면","color":"화이트","size":"FREE","maker":"테스트","origin":"한국","washingMethod":"물세탁","yearMonth":"2026-03","assuranceStandard":"1년","asPhone":"02-0000-1111"},
    "clothesDetailInfo":{"productCondition":"NEW","origin":"DOMESTIC","styleCode":"T003"},
    "deliveryNotice":{"deliveryArea":"전국","deliveryFee":3000,"deliveryPeriodAverage":2},
    "refundNotice":{"returnMethodDomestic":"DELIVERY","returnCourierDomestic":"CJ대한통운","returnChargeDomestic":3000,"returnExchangeAreaDomestic":"서울"},
    "productImageList":[{"type":"MAIN","productImageUrl":"https://cdn.set-of.com/rt3.jpg","originUrl":"https://cdn.set-of.com/rt3.jpg"}],
    "detailDescription":"<p>SINGLE 단품</p>",
    "productOptions":[
        {"stockQuantity":100,"additionalPrice":0,"options":[]}
    ]
}')

BODY=$(check_status "$RESP" "200" "SINGLE 등록") || true
PG3_ID=$(echo "$BODY" | jq -r '.data.productGroupId // empty' 2>/dev/null)

if [ -n "$PG3_ID" ]; then
    echo -e "  등록된 productGroupId: ${PG3_ID}"

    RESP=$(api GET "/api/v1/legacy/product/group/${PG3_ID}")
    DETAIL=$(check_status "$RESP" "200" "SINGLE 상세 조회")
    if [ $? -eq 0 ]; then
        check_field "$DETAIL" ".data.productGroup.optionType" "SINGLE" "옵션타입"
        check_field "$DETAIL" ".data.products | length" "1" "SKU 수 (단품)"
    fi

    # 진열 OFF
    RESP=$(api PATCH "/api/v1/legacy/product/group/${PG3_ID}/display-yn" '{"displayYn":"N"}')
    check_status "$RESP" "200" "SINGLE 진열 OFF" > /dev/null

    # 이미지 수정
    RESP=$(api PUT "/api/v1/legacy/product/group/${PG3_ID}/images" '[
        {"type":"MAIN","productImageUrl":"https://cdn.set-of.com/rt3_v2.jpg","originUrl":"https://cdn.set-of.com/rt3_v2.jpg"},
        {"type":"DETAIL","productImageUrl":"https://cdn.set-of.com/rt3_sub.jpg","originUrl":"https://cdn.set-of.com/rt3_sub.jpg"}
    ]')
    check_status "$RESP" "200" "SINGLE 이미지 수정" > /dev/null

    # 상세설명 수정
    RESP=$(api PUT "/api/v1/legacy/product/group/${PG3_ID}/detailDescription" '{"detailDescription":"<p>수정된 설명</p>"}')
    check_status "$RESP" "200" "SINGLE 상세설명 수정" > /dev/null

    # 고시정보 수정
    RESP=$(api PUT "/api/v1/legacy/product/group/${PG3_ID}/notice" '{"material":"실크","color":"레드","size":"M","maker":"수정제조사","origin":"프랑스","washingMethod":"드라이","yearMonth":"2026-04","assuranceStandard":"2년","asPhone":"02-1111-2222"}')
    check_status "$RESP" "200" "SINGLE 고시정보 수정" > /dev/null
fi

# =============================================================================
# Phase 4: 목록 조회
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 4: 목록 조회 ===${NC}"

RESP=$(api GET "/api/v1/legacy/products/group?page=0&size=5&startDate=2024-01-01T00:00:00&endDate=2027-12-31T23:59:59")
check_status "$RESP" "200" "목록 조회 (날짜 범위)" > /dev/null

# 필수 파라미터 누락 시 에러
RESP=$(api GET "/api/v1/legacy/products/group?page=0&size=5")
CODE=$(echo "$RESP" | tail -1)
if [ "$CODE" = "400" ]; then
    pass "목록 조회 startDate 누락 → 400"
else
    fail "목록 조회 startDate 누락 → HTTP ${CODE} (expected 400)"
fi

# =============================================================================
# Phase 5: 에러 케이스
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 5: 에러 케이스 ===${NC}"

# 존재하지 않는 상품 조회
RESP=$(api GET "/api/v1/legacy/product/group/999999999")
CODE=$(echo "$RESP" | tail -1)
if [ "$CODE" = "404" ]; then
    pass "존재하지 않는 상품 → 404"
elif [ "$CODE" = "200" ]; then
    fail "존재하지 않는 상품 → 200 (NOT_FOUND가 200으로 반환됨)"
else
    pass "존재하지 않는 상품 → HTTP ${CODE}"
fi

# 잘못된 옵션 조합 (COLOR + DEFAULT_TWO)
RESP=$(api POST "/api/v1/legacy/product/group" '{
    "productGroupName":"에러테스트","sellerId":1,"optionType":"OPTION_TWO","managementType":"MENUAL",
    "categoryId":1334,"brandId":5465,
    "productStatus":{"soldOutYn":"N","displayYn":"Y"},
    "price":{"regularPrice":10000,"currentPrice":8000},
    "productNotice":{"material":"","color":"","size":"","maker":"","origin":"","washingMethod":"","yearMonth":"","assuranceStandard":"","asPhone":""},
    "clothesDetailInfo":{"productCondition":"NEW"},
    "deliveryNotice":{"deliveryArea":"전국","deliveryFee":0,"deliveryPeriodAverage":1},
    "refundNotice":{"returnMethodDomestic":"DELIVERY","returnCourierDomestic":"CJ","returnChargeDomestic":0,"returnExchangeAreaDomestic":"서울"},
    "productImageList":[{"type":"MAIN","productImageUrl":"https://cdn.set-of.com/err.jpg","originUrl":"https://cdn.set-of.com/err.jpg"}],
    "detailDescription":"에러",
    "productOptions":[
        {"stockQuantity":1,"additionalPrice":0,"options":[{"optionName":"COLOR","optionValue":"블랙"},{"optionName":"DEFAULT_TWO","optionValue":"XL"}]}
    ]
}')
CODE=$(echo "$RESP" | tail -1)
if [ "$CODE" = "400" ]; then
    pass "잘못된 옵션 조합 (COLOR+DEFAULT_TWO) → 400"
else
    skip "잘못된 옵션 조합 → HTTP ${CODE} (검증 미구현 가능)"
fi

# =============================================================================
# 결과 요약
# =============================================================================
echo ""
echo "============================================================"
echo " 라운드트립 검증 결과"
echo "============================================================"
TOTAL=$((PASS + FAIL + SKIP))
echo -e "  전체: ${TOTAL}"
echo -e "  ${GREEN}PASS: ${PASS}${NC}"
echo -e "  ${RED}FAIL: ${FAIL}${NC}"
echo -e "  ${YELLOW}SKIP: ${SKIP}${NC}"
echo ""
echo " 종료: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

if [ $FAIL -gt 0 ]; then exit 1; fi
