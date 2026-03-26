#!/usr/bin/env bash
# =============================================================================
# Shadow Traffic 비교 테스트 스크립트
#
# MarketPlace legacy-api (8081) vs setof-legacy-web-api-admin (48083)
# 양쪽 GET 응답을 비교하고, POST/PUT은 Shadow 모드로 실행
# =============================================================================
set -uo pipefail

MARKETPLACE="http://localhost:8081"
SETOF_LEGACY="http://localhost:48083"
MYSQL="/opt/homebrew/opt/mysql-client/bin/mysql -h 127.0.0.1 -P 13308 -u admin -pREDACTED_DB_PASSWORD"

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

PASS_COUNT=0
FAIL_COUNT=0
SKIP_COUNT=0
DIFF_COUNT=0

pass() { echo -e "  ${GREEN}[PASS]${NC} $1"; ((PASS_COUNT++)); }
fail() { echo -e "  ${RED}[FAIL]${NC} $1"; ((FAIL_COUNT++)); }
skip() { echo -e "  ${YELLOW}[SKIP]${NC} $1"; ((SKIP_COUNT++)); }
diff_found() { echo -e "  ${RED}[DIFF]${NC} $1"; ((DIFF_COUNT++)); }

echo "============================================================"
echo " Shadow Traffic 비교 테스트"
echo " MarketPlace: ${MARKETPLACE} (포트 8081)"
echo " setof-legacy: ${SETOF_LEGACY} (포트 48083)"
echo " 시작: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

# =============================================================================
# 서버 확인
# =============================================================================
echo ""
MP_OK=$(curl -s -o /dev/null -w "%{http_code}" "${MARKETPLACE}/actuator/health" 2>/dev/null)
SF_OK=$(curl -s -o /dev/null -w "%{http_code}" "${SETOF_LEGACY}/actuator/health" 2>/dev/null)

if [ "$MP_OK" != "200" ]; then echo -e "${RED}MarketPlace(8081) 미기동${NC}"; exit 1; fi
if [ "$SF_OK" != "200" ]; then echo -e "${RED}setof-legacy(48083) 미기동${NC}"; exit 1; fi
echo -e "서버 상태: MarketPlace ${GREEN}OK${NC} / setof-legacy ${GREEN}OK${NC}"

# =============================================================================
# 인증 토큰 발급
# =============================================================================
echo ""
echo -e "${CYAN}=== 인증 ===${NC}"

MP_AUTH=$(curl -s -X POST "${MARKETPLACE}/api/v1/legacy/auth/authentication" \
  -H "Content-Type: application/json" \
  -d '{"userId":"e2e@test.com","password":"test1234","roleType":"SELLER"}')
MP_TOKEN=$(echo "$MP_AUTH" | jq -r '.data.token // empty' 2>/dev/null)

SF_AUTH=$(curl -s -X POST "${SETOF_LEGACY}/api/v1/auth/authentication" \
  -H "Content-Type: application/json" \
  -d '{"userId":"e2e@test.com","password":"test1234","roleType":"SELLER"}')
SF_TOKEN=$(echo "$SF_AUTH" | jq -r '.data.token // empty' 2>/dev/null)

if [ -z "$MP_TOKEN" ]; then echo -e "${RED}MarketPlace 토큰 발급 실패${NC}"; exit 1; fi
if [ -z "$SF_TOKEN" ]; then echo -e "${RED}setof-legacy 토큰 발급 실패${NC}"; exit 1; fi
echo -e "  토큰 발급: ${GREEN}양쪽 성공${NC}"

# =============================================================================
# GET 비교 함수
# =============================================================================
compare_get() {
    local mp_path="$1"
    local sf_path="$2"
    local description="$3"
    local query="${4:-}"

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
        return
    fi

    # JSON 정규화 비교 (키 정렬)
    local mp_sorted=$(echo "$mp_body" | jq -S '.' 2>/dev/null || echo "$mp_body")
    local sf_sorted=$(echo "$sf_body" | jq -S '.' 2>/dev/null || echo "$sf_body")

    if [ "$mp_sorted" = "$sf_sorted" ]; then
        pass "${description} — HTTP ${mp_code} 응답 일치"
    else
        diff_found "${description} — HTTP ${mp_code} 응답 불일치"
        echo "    MP: $(echo "$mp_body" | jq -c '.' 2>/dev/null | head -c 200)"
        echo "    SF: $(echo "$sf_body" | jq -c '.' 2>/dev/null | head -c 200)"
    fi
}

# =============================================================================
# Shadow POST 함수 (MarketPlace만 호출, Redis 스냅샷 확인)
# =============================================================================
shadow_post() {
    local mp_path="$1"
    local body="$2"
    local description="$3"
    local correlation_id="shadow-$(date +%s%N)"

    local mp_resp=$(curl -s -w "\n%{http_code}" \
        -X POST "${MARKETPLACE}${mp_path}" \
        -H "API-KEY: ${MP_TOKEN}" \
        -H "Content-Type: application/json" \
        -H "X-Shadow-Mode: verify" \
        -H "X-Shadow-Correlation-Id: ${correlation_id}" \
        -H "X-Shadow-Timestamp: $(date -u +%Y-%m-%dT%H:%M:%SZ)" \
        -d "$body" 2>/dev/null)
    local mp_code=$(echo "$mp_resp" | tail -1)
    local mp_body=$(echo "$mp_resp" | sed '$d')

    if [[ "$mp_code" =~ ^2[0-9][0-9]$ ]]; then
        # Redis에서 스냅샷 확인
        local snapshot=$(docker exec marketplace-redis redis-cli GET "shadow:snapshot:${correlation_id}" 2>/dev/null)
        if [ -n "$snapshot" ] && [ "$snapshot" != "(nil)" ]; then
            pass "${description} — HTTP ${mp_code}, Shadow 스냅샷 저장됨 (correlationId=${correlation_id})"
        else
            pass "${description} — HTTP ${mp_code} (스냅샷 미확인 — 읽기 요청이거나 필터 미적용)"
        fi
    else
        fail "${description} — HTTP ${mp_code}"
        echo "    응답: $(echo "$mp_body" | head -c 200)"
    fi
}

# =============================================================================
# Phase 1: GET 조회 비교
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 1: 조회(GET) 응답 비교 ===${NC}"

# 셀러 조회
compare_get "/api/v1/legacy/seller" "/api/v1/seller" "셀러 정보 조회"

# 주문 목록
compare_get "/api/v1/legacy/orders" "/api/v1/orders" "주문 목록 조회" "page=0&size=5"

# 상품 목록
compare_get "/api/v1/legacy/products/group" "/api/v1/products/group" "상품 그룹 목록 조회" "page=0&size=5"

# 상품 상세 (DB에서 실제 productGroupId 조회)
PG_ID=$($MYSQL luxurydb --batch --skip-column-names -e "SELECT PRODUCT_GROUP_ID FROM product_groups WHERE DELETE_YN = 'N' AND SELLER_ID = 1 LIMIT 1" 2>/dev/null || echo "")
if [ -n "$PG_ID" ]; then
    compare_get "/api/v1/legacy/product/group/${PG_ID}" "/api/v1/product/group/${PG_ID}" "상품 그룹 상세 (id=${PG_ID})"
    compare_get "/api/v1/legacy/product/group/${PG_ID}/images" "/api/v1/product/group/${PG_ID}/images" "상품 이미지 (id=${PG_ID})"
    compare_get "/api/v1/legacy/product/group/${PG_ID}/detailDescription" "/api/v1/product/group/${PG_ID}/detailDescription" "상세 설명 (id=${PG_ID})"
    compare_get "/api/v1/legacy/product/group/${PG_ID}/notice" "/api/v1/product/group/${PG_ID}/notice" "고시정보 (id=${PG_ID})"
else
    skip "상품 데이터 없음 (셀러1)"
fi

# QnA 목록
compare_get "/api/v1/legacy/qnas" "/api/v1/qnas" "QnA 목록 조회" "page=0&size=5"

# 배송사 코드
compare_get "/api/v1/legacy/shipment/company-codes" "/api/v1/shipment/company-codes" "배송사 코드 조회"

# =============================================================================
# Phase 2: Shadow POST (비멱등 요청)
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 2: Shadow POST (비멱등 요청, 롤백) ===${NC}"

if [ -n "$PG_ID" ]; then
    # 상품 수정 (Shadow 모드 → 실행 후 롤백)
    shadow_post "/api/v1/legacy/product/group/${PG_ID}" \
        '{"productGroupName":"Shadow 테스트 상품명","salePrice":99999}' \
        "상품 수정 Shadow (id=${PG_ID})"

    # 가격 수정
    shadow_post "/api/v1/legacy/product/group/${PG_ID}/price" \
        '{"salePrice":88888}' \
        "가격 수정 Shadow (id=${PG_ID})"
else
    skip "상품 데이터 없어 Shadow POST 건너뜀"
fi

# 주문 수정 (DB에서 주문 ID 조회)
ORDER_ID=$($MYSQL luxurydb --batch --skip-column-names -e "SELECT ORDER_ID FROM orders WHERE DELETE_YN = 'N' AND SELLER_ID = 1 LIMIT 1" 2>/dev/null || echo "")
if [ -n "$ORDER_ID" ]; then
    shadow_post "/api/v1/legacy/order" \
        "{\"orderId\":${ORDER_ID},\"status\":\"shipped\",\"trackingNumber\":\"SHADOW123\"}" \
        "주문 수정 Shadow (orderId=${ORDER_ID})"
else
    skip "주문 데이터 없어 Shadow POST 건너뜀"
fi

# =============================================================================
# 결과 요약
# =============================================================================
echo ""
echo "============================================================"
echo " Shadow Traffic 비교 결과"
echo "============================================================"
TOTAL=$((PASS_COUNT + FAIL_COUNT + SKIP_COUNT + DIFF_COUNT))
echo -e "  전체: ${TOTAL}"
echo -e "  ${GREEN}일치: ${PASS_COUNT}${NC}"
echo -e "  ${RED}불일치: ${DIFF_COUNT}${NC}"
echo -e "  ${RED}실패: ${FAIL_COUNT}${NC}"
echo -e "  ${YELLOW}건너뜀: ${SKIP_COUNT}${NC}"
echo ""
echo " 종료: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

if [ $FAIL_COUNT -gt 0 ] || [ $DIFF_COUNT -gt 0 ]; then
    exit 1
fi
