#!/usr/bin/env bash
# =============================================================================
# Legacy API 비교 테스트 스크립트
#
# MarketPlace legacy-api (9083) vs setof-legacy-web-api-admin (48083)
# 인증 → 상품 등록 → 조회 → 수정 순차 플로우로 양쪽 응답 비교
# =============================================================================
set -uo pipefail

MARKETPLACE="${MARKETPLACE_URL:-http://localhost:9083}"
SETOF_LEGACY="${SETOF_LEGACY_URL:-http://localhost:48083}"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

PASS_COUNT=0
FAIL_COUNT=0
SKIP_COUNT=0
DIFF_COUNT=0

pass()       { echo -e "  ${GREEN}[PASS]${NC} $1"; ((PASS_COUNT++)); }
fail()       { echo -e "  ${RED}[FAIL]${NC} $1"; ((FAIL_COUNT++)); }
skip()       { echo -e "  ${YELLOW}[SKIP]${NC} $1"; ((SKIP_COUNT++)); }
diff_found() { echo -e "  ${RED}[DIFF]${NC} $1"; ((DIFF_COUNT++)); }

echo "============================================================"
echo " Legacy API 비교 테스트 (인증 + 상품 전체 플로우)"
echo " MarketPlace: ${MARKETPLACE}"
echo " setof-legacy: ${SETOF_LEGACY}"
echo " 시작: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

# =============================================================================
# 서버 확인
# =============================================================================
echo ""
MP_OK=$(curl -s -o /dev/null -w "%{http_code}" "${MARKETPLACE}/actuator/health" 2>/dev/null)
SF_OK=$(curl -s -o /dev/null -w "%{http_code}" "${SETOF_LEGACY}/actuator/health" 2>/dev/null)

if [ "$MP_OK" != "200" ]; then echo -e "${RED}MarketPlace 미기동 (${MARKETPLACE})${NC}"; exit 1; fi
if [ "$SF_OK" != "200" ]; then echo -e "${RED}setof-legacy 미기동 (${SETOF_LEGACY})${NC}"; exit 1; fi
echo -e "서버 상태: MarketPlace ${GREEN}OK${NC} / setof-legacy ${GREEN}OK${NC}"

# =============================================================================
# GET 비교 함수 (배열 정렬 무시 옵션 포함)
# =============================================================================
compare_get() {
    local mp_path="$1"
    local sf_path="$2"
    local description="$3"
    local query="${4:-}"
    local sort_arrays="${5:-false}"  # true면 배열 요소를 정렬해서 비교

    local mp_url="${MARKETPLACE}${mp_path}${query:+?$query}"
    local sf_url="${SETOF_LEGACY}${sf_path}${query:+?$query}"

    local mp_resp=$(curl -s -w "\n%{http_code}" -H "API-KEY: ${MP_TOKEN}" "$mp_url" 2>/dev/null)
    local mp_code=$(echo "$mp_resp" | tail -1)
    local mp_body=$(echo "$mp_resp" | sed '$d')

    local sf_resp=$(curl -s -w "\n%{http_code}" -H "API-KEY: ${SF_TOKEN}" "$sf_url" 2>/dev/null)
    local sf_code=$(echo "$sf_resp" | tail -1)
    local sf_body=$(echo "$sf_resp" | sed '$d')

    if [ "$mp_code" != "$sf_code" ]; then
        fail "${description} — HTTP 코드 불일치: MP=${mp_code}, SF=${sf_code}"
        echo "    MP: $(echo "$mp_body" | jq -c '.' 2>/dev/null | head -c 200)"
        echo "    SF: $(echo "$sf_body" | jq -c '.' 2>/dev/null | head -c 200)"
        return 1
    fi

    local mp_sorted sf_sorted
    if [ "$sort_arrays" = "true" ]; then
        mp_sorted=$(echo "$mp_body" | jq -S 'walk(if type == "array" then sort_by(tostring) else . end)' 2>/dev/null || echo "$mp_body")
        sf_sorted=$(echo "$sf_body" | jq -S 'walk(if type == "array" then sort_by(tostring) else . end)' 2>/dev/null || echo "$sf_body")
    else
        mp_sorted=$(echo "$mp_body" | jq -S '.' 2>/dev/null || echo "$mp_body")
        sf_sorted=$(echo "$sf_body" | jq -S '.' 2>/dev/null || echo "$sf_body")
    fi

    if [ "$mp_sorted" = "$sf_sorted" ]; then
        pass "${description} — HTTP ${mp_code} 응답 일치"
        return 0
    else
        diff_found "${description} — HTTP ${mp_code} 응답 불일치"
        # 차이 상세 출력 (diff 첫 10줄)
        local diff_output=$(diff <(echo "$mp_sorted") <(echo "$sf_sorted") | head -20)
        echo "    $diff_output" | head -10
        return 1
    fi
}

# =============================================================================
# POST/PUT/PATCH 비교 함수
# =============================================================================
compare_write() {
    local method="$1"
    local mp_path="$2"
    local sf_path="$3"
    local body="$4"
    local description="$5"

    local mp_resp=$(curl -s -w "\n%{http_code}" -X "$method" "${MARKETPLACE}${mp_path}" \
        -H "API-KEY: ${MP_TOKEN}" -H "Content-Type: application/json" -d "$body" 2>/dev/null)
    local mp_code=$(echo "$mp_resp" | tail -1)
    local mp_body=$(echo "$mp_resp" | sed '$d')

    local sf_resp=$(curl -s -w "\n%{http_code}" -X "$method" "${SETOF_LEGACY}${sf_path}" \
        -H "API-KEY: ${SF_TOKEN}" -H "Content-Type: application/json" -d "$body" 2>/dev/null)
    local sf_code=$(echo "$sf_resp" | tail -1)
    local sf_body=$(echo "$sf_resp" | sed '$d')

    if [ "$mp_code" != "$sf_code" ]; then
        fail "${description} — HTTP 코드 불일치: MP=${mp_code}, SF=${sf_code}"
        echo "    MP: $(echo "$mp_body" | jq -c '.' 2>/dev/null | head -c 300)"
        echo "    SF: $(echo "$sf_body" | jq -c '.' 2>/dev/null | head -c 300)"
        return 1
    fi

    # 쓰기 요청은 HTTP 코드 일치 + 성공 여부만 판단
    if [[ "$mp_code" =~ ^2[0-9][0-9]$ ]]; then
        pass "${description} — HTTP ${mp_code} 양쪽 성공"
        # 응답 data 반환 (호출자가 사용)
        echo "$mp_body" | jq -r '.data' 2>/dev/null
        return 0
    else
        fail "${description} — 양쪽 모두 ${mp_code} 실패"
        echo "    MP: $(echo "$mp_body" | jq -c '.' 2>/dev/null | head -c 300)"
        echo "    SF: $(echo "$sf_body" | jq -c '.' 2>/dev/null | head -c 300)"
        return 1
    fi
}

# 한쪽만 호출 (동일 DB이므로 양쪽 다 하면 중복)
call_one_side() {
    local side="$1"  # MP or SF
    local method="$2"
    local path="$3"
    local body="$4"
    local description="$5"

    local base_url token
    if [ "$side" = "MP" ]; then
        base_url="$MARKETPLACE"
        token="$MP_TOKEN"
    else
        base_url="$SETOF_LEGACY"
        token="$SF_TOKEN"
    fi

    local resp=$(curl -s -w "\n%{http_code}" -X "$method" "${base_url}${path}" \
        -H "API-KEY: ${token}" -H "Content-Type: application/json" -d "$body" 2>/dev/null)
    local code=$(echo "$resp" | tail -1)
    local resp_body=$(echo "$resp" | sed '$d')

    if [[ "$code" =~ ^2[0-9][0-9]$ ]]; then
        pass "${description} — [${side}] HTTP ${code}"
        echo "$resp_body"
        return 0
    else
        fail "${description} — [${side}] HTTP ${code}"
        echo "    응답: $(echo "$resp_body" | jq -c '.' 2>/dev/null | head -c 300)"
        echo "$resp_body"
        return 1
    fi
}

# =============================================================================
# Phase 0: 인증
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 0: 인증 (POST /auth/authentication) ===${NC}"

# 테스트 셀러: e2e@test.com / test1234 (셀러ID=1, admin/트렉시)
AUTH_BODY='{"userId":"e2e@test.com","password":"test1234","roleType":"SELLER"}'

MP_AUTH=$(curl -s -X POST "${MARKETPLACE}/api/v1/legacy/auth/authentication" \
    -H "Content-Type: application/json" -d "$AUTH_BODY")
MP_TOKEN=$(echo "$MP_AUTH" | jq -r '.data.token // empty' 2>/dev/null)

SF_AUTH=$(curl -s -X POST "${SETOF_LEGACY}/api/v1/auth/authentication" \
    -H "Content-Type: application/json" -d "$AUTH_BODY")
SF_TOKEN=$(echo "$SF_AUTH" | jq -r '.data.token // empty' 2>/dev/null)

if [ -z "$MP_TOKEN" ]; then
    fail "MarketPlace 토큰 발급 실패"
    echo "    응답: $(echo "$MP_AUTH" | jq -c '.' 2>/dev/null)"
    echo -e "${RED}인증 실패로 테스트 중단${NC}"
    exit 1
else
    pass "MarketPlace 토큰 발급 성공"
fi

if [ -z "$SF_TOKEN" ]; then
    fail "setof-legacy 토큰 발급 실패"
    echo "    응답: $(echo "$SF_AUTH" | jq -c '.' 2>/dev/null)"
    echo -e "${RED}인증 실패로 테스트 중단${NC}"
    exit 1
else
    pass "setof-legacy 토큰 발급 성공"
fi

# 인증 응답 구조 비교 (토큰 값 제외, 필드 구조만)
MP_AUTH_KEYS=$(echo "$MP_AUTH" | jq -S 'del(.data.token, .data.refreshToken, .timestamp)' 2>/dev/null)
SF_AUTH_KEYS=$(echo "$SF_AUTH" | jq -S 'del(.data.token, .data.refreshToken, .timestamp)' 2>/dev/null)
if [ "$MP_AUTH_KEYS" = "$SF_AUTH_KEYS" ]; then
    pass "인증 응답 구조 일치 (토큰 값 제외)"
else
    diff_found "인증 응답 구조 불일치"
    echo "    MP: $(echo "$MP_AUTH_KEYS" | jq -c '.')"
    echo "    SF: $(echo "$SF_AUTH_KEYS" | jq -c '.')"
fi

# =============================================================================
# Phase 1: 셀러 정보 조회
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 1: 셀러 정보 조회 ===${NC}"
compare_get "/api/v1/legacy/seller" "/api/v1/seller" "GET /seller — 셀러 기본 정보"

# =============================================================================
# Phase 2: 상품 등록 (POST)
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 2: 상품 등록 (POST /product/group) ===${NC}"
echo "  (같은 DB 사용 → MarketPlace에서 등록 후 양쪽 조회 비교)"

CREATE_BODY=$(cat <<'ENDJSON'
{
  "productGroupName": "비교테스트 상품 - 자동생성",
  "sellerId": 1,
  "optionType": "OPTION_ONE",
  "managementType": "CONSIGNMENT",
  "categoryId": 1334,
  "brandId": 5465,
  "productStatus": {
    "soldOutYn": "N",
    "displayYn": "Y"
  },
  "price": {
    "regularPrice": 150000,
    "currentPrice": 120000
  },
  "productNotice": {
    "material": "면 100%",
    "color": "블랙",
    "size": "FREE",
    "maker": "테스트제조사",
    "origin": "대한민국",
    "washingMethod": "손세탁",
    "yearMonth": "2026-03",
    "assuranceStandard": "품질보증기준에 따름",
    "asPhone": "02-1234-5678"
  },
  "clothesDetailInfo": {
    "productCondition": "NEW",
    "origin": "DOMESTIC",
    "styleCode": "TST-001"
  },
  "deliveryNotice": {
    "deliveryArea": "전국",
    "deliveryFee": 3000,
    "deliveryPeriodAverage": 3
  },
  "refundNotice": {
    "returnMethodDomestic": "DELIVERY",
    "returnCourierDomestic": "CJ대한통운",
    "returnChargeDomestic": 5000,
    "returnExchangeAreaDomestic": "서울특별시 강남구"
  },
  "productImageList": [
    {
      "type": "MAIN",
      "productImageUrl": "https://cdn.set-of.com/test/main.jpg",
      "originUrl": "https://cdn.set-of.com/test/main_origin.jpg"
    },
    {
      "type": "SUB",
      "productImageUrl": "https://cdn.set-of.com/test/sub1.jpg",
      "originUrl": "https://cdn.set-of.com/test/sub1_origin.jpg"
    }
  ],
  "detailDescription": "<p>비교 테스트용 상품 상세 설명입니다.</p>",
  "productOptions": [
    {
      "stockQuantity": 10,
      "additionalPrice": 0,
      "options": [
        { "optionName": "사이즈", "optionValue": "S" }
      ]
    },
    {
      "stockQuantity": 20,
      "additionalPrice": 0,
      "options": [
        { "optionName": "사이즈", "optionValue": "M" }
      ]
    },
    {
      "stockQuantity": 5,
      "additionalPrice": 5000,
      "options": [
        { "optionName": "사이즈", "optionValue": "L" }
      ]
    }
  ]
}
ENDJSON
)

# MarketPlace에서 등록
MP_CREATE_RESP=$(call_one_side "MP" "POST" "/api/v1/legacy/product/group" "$CREATE_BODY" "상품그룹 등록")
MP_PG_ID=$(echo "$MP_CREATE_RESP" | jq -r '.data.productGroupId // empty' 2>/dev/null)
MP_PRODUCT_IDS=$(echo "$MP_CREATE_RESP" | jq -r '.data.productIds // empty' 2>/dev/null)

if [ -z "$MP_PG_ID" ]; then
    echo -e "  ${RED}MarketPlace 상품 등록 실패 — 이후 테스트 건너뜀${NC}"
    echo "    응답: $(echo "$MP_CREATE_RESP" | jq -c '.' 2>/dev/null | head -c 500)"
    MP_PG_ID=""
else
    echo -e "  ${GREEN}등록된 productGroupId: ${MP_PG_ID}${NC}"
    echo -e "  ${GREEN}등록된 productIds: ${MP_PRODUCT_IDS}${NC}"
fi

# setof-commerce에서도 등록 (같은 body)
SF_CREATE_RESP=$(call_one_side "SF" "POST" "/api/v1/product/group" "$CREATE_BODY" "상품그룹 등록")
SF_PG_ID=$(echo "$SF_CREATE_RESP" | jq -r '.data.productGroupId // empty' 2>/dev/null)
SF_PRODUCT_IDS=$(echo "$SF_CREATE_RESP" | jq -r '.data.productIds // empty' 2>/dev/null)

if [ -z "$SF_PG_ID" ]; then
    echo -e "  ${RED}setof-legacy 상품 등록 실패${NC}"
    echo "    응답: $(echo "$SF_CREATE_RESP" | jq -c '.' 2>/dev/null | head -c 500)"
    SF_PG_ID=""
else
    echo -e "  ${GREEN}등록된 productGroupId: ${SF_PG_ID}${NC}"
    echo -e "  ${GREEN}등록된 productIds: ${SF_PRODUCT_IDS}${NC}"
fi

# 등록 응답 구조 비교 (ID 값 제외)
if [ -n "$MP_PG_ID" ] && [ -n "$SF_PG_ID" ]; then
    MP_CREATE_STRUCT=$(echo "$MP_CREATE_RESP" | jq -S 'del(.data.productGroupId, .data.productIds, .data.sellerId, .timestamp)' 2>/dev/null)
    SF_CREATE_STRUCT=$(echo "$SF_CREATE_RESP" | jq -S 'del(.data.productGroupId, .data.productIds, .data.sellerId, .timestamp)' 2>/dev/null)
    if [ "$MP_CREATE_STRUCT" = "$SF_CREATE_STRUCT" ]; then
        pass "상품 등록 응답 구조 일치 (ID 제외)"
    else
        diff_found "상품 등록 응답 구조 불일치"
        echo "    MP: $(echo "$MP_CREATE_STRUCT" | jq -c '.')"
        echo "    SF: $(echo "$SF_CREATE_STRUCT" | jq -c '.')"
    fi
fi

# =============================================================================
# Phase 3: 상품 상세 조회 (GET)
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 3: 상품 상세 조회 (GET /product/group/{id}) ===${NC}"

if [ -n "$MP_PG_ID" ]; then
    # MP가 등록한 상품을 양쪽에서 조회
    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "MP 등록 상품(${MP_PG_ID}) 양쪽 조회 비교" "" "true"
fi

if [ -n "$SF_PG_ID" ]; then
    # SF가 등록한 상품을 양쪽에서 조회
    compare_get "/api/v1/legacy/product/group/${SF_PG_ID}" "/api/v1/product/group/${SF_PG_ID}" \
        "SF 등록 상품(${SF_PG_ID}) 양쪽 조회 비교" "" "true"
fi

# 기존 상품 조회 (이미 존재하는 데이터)
compare_get "/api/v1/legacy/product/group/479128" "/api/v1/product/group/479128" \
    "기존 상품(479128) 양쪽 조회 비교" "" "true"

# =============================================================================
# Phase 4: 상품 목록 조회 (GET)
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 4: 상품 목록 조회 (GET /products/group) ===${NC}"

compare_get "/api/v1/legacy/products/group" "/api/v1/products/group" \
    "상품 목록 (기본 page=0, size=5)" "page=0&size=5" "true"

compare_get "/api/v1/legacy/products/group" "/api/v1/products/group" \
    "상품 목록 (displayYn=Y 필터)" "page=0&size=5&displayYn=Y" "true"

compare_get "/api/v1/legacy/products/group" "/api/v1/products/group" \
    "상품 목록 (brandId=5465 필터)" "page=0&size=5&brandId=5465" "true"

compare_get "/api/v1/legacy/products/group" "/api/v1/products/group" \
    "상품 목록 (가격 범위 필터)" "page=0&size=5&minSalePrice=100000&maxSalePrice=200000" "true"

# =============================================================================
# Phase 5: 상품 수정 시나리오 (PUT/PATCH)
# 같은 DB → MP 등록 상품은 MP로, SF 등록 상품은 SF로 수정 후 양쪽 조회 비교
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 5: 상품 수정 (PUT/PATCH) ===${NC}"

# --- 5-1. 가격 수정 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-1] 가격 수정 (PATCH /product/group/{id}/price)${NC}"
    PRICE_BODY='{"regularPrice":200000,"currentPrice":180000}'

    call_one_side "MP" "PATCH" "/api/v1/legacy/product/group/${MP_PG_ID}/price" "$PRICE_BODY" \
        "MP 상품(${MP_PG_ID}) 가격 수정" > /dev/null
    call_one_side "SF" "PATCH" "/api/v1/product/group/${SF_PG_ID}/price" "$PRICE_BODY" \
        "SF 상품(${SF_PG_ID}) 가격 수정" > /dev/null

    # 수정 후 양쪽 조회 비교
    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "가격 수정 후 MP상품 조회 비교" "" "true"
fi

# --- 5-2. 진열 상태 변경 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-2] 진열 상태 변경 (PATCH /product/group/{id}/display-yn)${NC}"
    DISPLAY_BODY='{"displayYn":"N"}'

    call_one_side "MP" "PATCH" "/api/v1/legacy/product/group/${MP_PG_ID}/display-yn" "$DISPLAY_BODY" \
        "MP 상품(${MP_PG_ID}) 진열 OFF" > /dev/null
    call_one_side "SF" "PATCH" "/api/v1/product/group/${SF_PG_ID}/display-yn" "$DISPLAY_BODY" \
        "SF 상품(${SF_PG_ID}) 진열 OFF" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "진열 OFF 후 MP상품 조회 비교" "" "true"
fi

# --- 5-3. 품절 처리 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-3] 품절 처리 (PATCH /product/group/{id}/out-stock)${NC}"

    call_one_side "MP" "PATCH" "/api/v1/legacy/product/group/${MP_PG_ID}/out-stock" "{}" \
        "MP 상품(${MP_PG_ID}) 품절 처리" > /dev/null
    call_one_side "SF" "PATCH" "/api/v1/product/group/${SF_PG_ID}/out-stock" "{}" \
        "SF 상품(${SF_PG_ID}) 품절 처리" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "품절 처리 후 MP상품 조회 비교" "" "true"
fi

# --- 5-4. 이미지 수정 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-4] 이미지 수정 (PUT /product/group/{id}/images)${NC}"
    IMAGES_BODY='[{"type":"MAIN","productImageUrl":"https://cdn.set-of.com/test/updated_main.jpg","originUrl":"https://cdn.set-of.com/test/updated_main_origin.jpg"},{"type":"SUB","productImageUrl":"https://cdn.set-of.com/test/updated_sub.jpg","originUrl":"https://cdn.set-of.com/test/updated_sub_origin.jpg"},{"type":"SUB","productImageUrl":"https://cdn.set-of.com/test/updated_sub2.jpg","originUrl":"https://cdn.set-of.com/test/updated_sub2_origin.jpg"}]'

    call_one_side "MP" "PUT" "/api/v1/legacy/product/group/${MP_PG_ID}/images" "$IMAGES_BODY" \
        "MP 상품(${MP_PG_ID}) 이미지 수정" > /dev/null
    call_one_side "SF" "PUT" "/api/v1/product/group/${SF_PG_ID}/images" "$IMAGES_BODY" \
        "SF 상품(${SF_PG_ID}) 이미지 수정" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "이미지 수정 후 MP상품 조회 비교" "" "true"
fi

# --- 5-5. 상세설명 수정 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-5] 상세설명 수정 (PUT /product/group/{id}/detailDescription)${NC}"
    DESC_BODY='{"detailDescription":"<p>수정된 상세 설명입니다. 비교 테스트.</p>"}'

    call_one_side "MP" "PUT" "/api/v1/legacy/product/group/${MP_PG_ID}/detailDescription" "$DESC_BODY" \
        "MP 상품(${MP_PG_ID}) 상세설명 수정" > /dev/null
    call_one_side "SF" "PUT" "/api/v1/product/group/${SF_PG_ID}/detailDescription" "$DESC_BODY" \
        "SF 상품(${SF_PG_ID}) 상세설명 수정" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "상세설명 수정 후 MP상품 조회 비교" "" "true"
fi

# --- 5-6. 고시정보 수정 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-6] 고시정보 수정 (PUT /product/group/{id}/notice)${NC}"
    NOTICE_BODY='{"material":"폴리에스터 80% 면 20%","color":"네이비","size":"S/M/L","maker":"수정제조사","origin":"이탈리아","washingMethod":"드라이클리닝","yearMonth":"2026-04","assuranceStandard":"1년 품질보증","asPhone":"02-9876-5432"}'

    call_one_side "MP" "PUT" "/api/v1/legacy/product/group/${MP_PG_ID}/notice" "$NOTICE_BODY" \
        "MP 상품(${MP_PG_ID}) 고시정보 수정" > /dev/null
    call_one_side "SF" "PUT" "/api/v1/product/group/${SF_PG_ID}/notice" "$NOTICE_BODY" \
        "SF 상품(${SF_PG_ID}) 고시정보 수정" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "고시정보 수정 후 MP상품 조회 비교" "" "true"
fi

# --- 5-7. 옵션 수정 ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-7] 옵션 수정 (PUT /product/group/{id}/option)${NC}"

    # MP에서 등록한 상품의 productIds 추출
    MP_PID_1=$(echo "$MP_PRODUCT_IDS" | jq -r '.[0] // empty' 2>/dev/null)
    MP_PID_2=$(echo "$MP_PRODUCT_IDS" | jq -r '.[1] // empty' 2>/dev/null)
    SF_PID_1=$(echo "$SF_PRODUCT_IDS" | jq -r '.[0] // empty' 2>/dev/null)
    SF_PID_2=$(echo "$SF_PRODUCT_IDS" | jq -r '.[1] // empty' 2>/dev/null)

    # 기존 옵션 교체 (2개로 줄임)
    MP_OPTION_BODY="[{\"productId\":${MP_PID_1:-null},\"stockQuantity\":50,\"additionalPrice\":0,\"options\":[{\"optionName\":\"사이즈\",\"optionValue\":\"S\"}]},{\"productId\":${MP_PID_2:-null},\"stockQuantity\":30,\"additionalPrice\":3000,\"options\":[{\"optionName\":\"사이즈\",\"optionValue\":\"XL\"}]}]"
    SF_OPTION_BODY="[{\"productId\":${SF_PID_1:-null},\"stockQuantity\":50,\"additionalPrice\":0,\"options\":[{\"optionName\":\"사이즈\",\"optionValue\":\"S\"}]},{\"productId\":${SF_PID_2:-null},\"stockQuantity\":30,\"additionalPrice\":3000,\"options\":[{\"optionName\":\"사이즈\",\"optionValue\":\"XL\"}]}]"

    call_one_side "MP" "PUT" "/api/v1/legacy/product/group/${MP_PG_ID}/option" "$MP_OPTION_BODY" \
        "MP 상품(${MP_PG_ID}) 옵션 수정" > /dev/null
    call_one_side "SF" "PUT" "/api/v1/product/group/${SF_PG_ID}/option" "$SF_OPTION_BODY" \
        "SF 상품(${SF_PG_ID}) 옵션 수정" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "옵션 수정 후 MP상품 조회 비교" "" "true"
fi

# --- 5-8. 재고 수정 ---
if [ -n "$MP_PG_ID" ] && [ -n "$MP_PID_1" ]; then
    echo -e "\n  ${BOLD}[5-8] 재고 수정 (PATCH /product/group/{id}/stock)${NC}"
    MP_STOCK_BODY="[{\"productId\":${MP_PID_1},\"productStockQuantity\":999}]"
    SF_STOCK_BODY="[{\"productId\":${SF_PID_1},\"productStockQuantity\":999}]"

    call_one_side "MP" "PATCH" "/api/v1/legacy/product/group/${MP_PG_ID}/stock" "$MP_STOCK_BODY" \
        "MP 상품(${MP_PG_ID}) 재고 수정" > /dev/null
    call_one_side "SF" "PATCH" "/api/v1/product/group/${SF_PG_ID}/stock" "$SF_STOCK_BODY" \
        "SF 상품(${SF_PG_ID}) 재고 수정" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "재고 수정 후 MP상품 조회 비교" "" "true"
fi

# --- 5-9. 상품그룹 전체 수정 (PUT) ---
if [ -n "$MP_PG_ID" ]; then
    echo -e "\n  ${BOLD}[5-9] 상품그룹 전체 수정 (PUT /product/group/{id})${NC}"

    UPDATE_BODY=$(cat <<ENDJSON
{
  "productGroupDetails": {
    "productGroupName": "비교테스트 상품 - 수정됨",
    "optionType": "OPTION_ONE",
    "managementType": "CONSIGNMENT",
    "price": { "regularPrice": 250000, "currentPrice": 220000 },
    "productStatus": { "soldOutYn": "N", "displayYn": "Y" },
    "clothesDetailInfo": { "productCondition": "NEW", "origin": "DOMESTIC", "styleCode": "TST-002" },
    "sellerId": 1,
    "categoryId": 1334,
    "brandId": 5465
  },
  "deliveryNotice": { "deliveryArea": "전국 (제주도 포함)", "deliveryFee": 0, "deliveryPeriodAverage": 2 },
  "refundNotice": { "returnMethodDomestic": "DELIVERY", "returnCourierDomestic": "한진택배", "returnChargeDomestic": 6000, "returnExchangeAreaDomestic": "서울특별시 서초구" },
  "productNotice": { "material": "실크 100%", "color": "화이트", "size": "FREE", "maker": "수정제조사", "origin": "프랑스", "washingMethod": "드라이클리닝 전용", "yearMonth": "2026-04", "assuranceStandard": "2년 보증", "asPhone": "02-0000-0000" },
  "productImageList": [
    { "type": "MAIN", "productImageUrl": "https://cdn.set-of.com/test/final_main.jpg", "originUrl": "https://cdn.set-of.com/test/final_main_origin.jpg" }
  ],
  "detailDescription": { "detailDescription": "<p>최종 수정된 상세 설명</p>" },
  "productOptions": [
    { "productId": ${MP_PID_1:-null}, "stockQuantity": 100, "additionalPrice": 0, "options": [{ "optionName": "사이즈", "optionValue": "FREE" }] }
  ],
  "updateStatus": {
    "productStatus": true,
    "noticeStatus": true,
    "imageStatus": true,
    "descriptionStatus": true,
    "stockOptionStatus": true,
    "deliveryStatus": true,
    "refundStatus": true
  }
}
ENDJSON
)

    SF_UPDATE_BODY=$(echo "$UPDATE_BODY" | sed "s/${MP_PID_1:-NOOP}/${SF_PID_1:-null}/g")

    call_one_side "MP" "PUT" "/api/v1/legacy/product/group/${MP_PG_ID}" "$UPDATE_BODY" \
        "MP 상품(${MP_PG_ID}) 전체 수정" > /dev/null
    call_one_side "SF" "PUT" "/api/v1/product/group/${SF_PG_ID}" "$SF_UPDATE_BODY" \
        "SF 상품(${SF_PG_ID}) 전체 수정" > /dev/null

    compare_get "/api/v1/legacy/product/group/${MP_PG_ID}" "/api/v1/product/group/${MP_PG_ID}" \
        "전체 수정 후 MP상품 조회 비교" "" "true"
fi

# =============================================================================
# Phase 6: 기타 조회 엔드포인트
# =============================================================================
echo ""
echo -e "${CYAN}${BOLD}=== Phase 6: 기타 조회 엔드포인트 ===${NC}"

compare_get "/api/v1/legacy/shipment/company-codes" "/api/v1/shipment/company-codes" \
    "배송사 코드 조회" "" "true"

# =============================================================================
# 결과 요약
# =============================================================================
echo ""
echo "============================================================"
echo " Legacy API 비교 테스트 결과"
echo "============================================================"
TOTAL=$((PASS_COUNT + FAIL_COUNT + SKIP_COUNT + DIFF_COUNT))
echo -e "  전체: ${TOTAL}"
echo -e "  ${GREEN}일치(PASS): ${PASS_COUNT}${NC}"
echo -e "  ${RED}불일치(DIFF): ${DIFF_COUNT}${NC}"
echo -e "  ${RED}실패(FAIL): ${FAIL_COUNT}${NC}"
echo -e "  ${YELLOW}건너뜀(SKIP): ${SKIP_COUNT}${NC}"
echo ""
echo " 종료: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

echo ""
echo -e "${BOLD}커버리지 요약:${NC}"
echo "  인증:    POST /auth/authentication          — 검증 완료"
echo "  셀러:    GET  /seller                        — 검증 완료"
echo "  상품등록: POST /product/group                 — 검증 완료"
echo "  상품조회: GET  /product/group/{id}            — 검증 완료 (신규+기존)"
echo "  상품목록: GET  /products/group                — 검증 완료 (4개 필터 시나리오)"
echo "  가격수정: PATCH /product/group/{id}/price      — 검증 완료"
echo "  진열변경: PATCH /product/group/{id}/display-yn — 검증 완료"
echo "  품절처리: PATCH /product/group/{id}/out-stock   — 검증 완료"
echo "  이미지:   PUT  /product/group/{id}/images      — 검증 완료"
echo "  상세설명: PUT  /product/group/{id}/detailDesc   — 검증 완료"
echo "  고시정보: PUT  /product/group/{id}/notice       — 검증 완료"
echo "  옵션수정: PUT  /product/group/{id}/option       — 검증 완료"
echo "  재고수정: PATCH /product/group/{id}/stock       — 검증 완료"
echo "  전체수정: PUT  /product/group/{id}             — 검증 완료"
echo "  배송사:   GET  /shipment/company-codes         — 검증 완료"
echo ""
echo "  미포함 (이번 스크립트 범위 외):"
echo "  주문:    GET/PUT /order, /orders              — 별도 스크립트 필요"
echo "  QnA:     GET/POST/PUT /qna, /qnas             — 별도 스크립트 필요"
echo "  이미지:   POST /image/presigned               — 별도 스크립트 필요"

if [ $FAIL_COUNT -gt 0 ] || [ $DIFF_COUNT -gt 0 ]; then
    exit 1
fi
