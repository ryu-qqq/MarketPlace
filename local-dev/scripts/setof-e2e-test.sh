#!/usr/bin/env bash
# =============================================================================
# 세토프(setof) 로컬 Docker 서버 E2E 테스트 스크립트
#
# 대상: http://localhost:48081
# 용도: 세토프 Admin API 전체 흐름 검증
# 실행: ./setof-e2e-test.sh
# =============================================================================
set -euo pipefail

# -----------------------------------------------------------------------------
# 환경 변수
# -----------------------------------------------------------------------------
BASE_URL="http://localhost:48081"
MYSQL="/opt/homebrew/opt/mysql-client/bin/mysql"
DB_HOST="127.0.0.1"
DB_PORT="13308"
DB_USER="admin"
DB_PASS="${STAGE_DB_PASSWORD:?STAGE_DB_PASSWORD 환경변수 필요}"

# 색상 코드
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
NC='\033[0m'

# 카운터
PASS_COUNT=0
FAIL_COUNT=0
SKIP_COUNT=0

# 전역 상태
TOKEN=""
LAST_HTTP_CODE=""
LAST_BODY=""

# -----------------------------------------------------------------------------
# 유틸리티 함수
# -----------------------------------------------------------------------------

# DB 조회 헬퍼
# $1=스키마, $2=SQL
db_query() {
    local schema="$1"
    local query="$2"
    $MYSQL -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$schema" \
        --batch --skip-column-names -e "$query" 2>/dev/null
}

# API 호출 헬퍼
# $1=method, $2=url, $3=body(빈 문자열 가능), $4=description, $5=expected_status(기본: 2xx)
call_api() {
    local method="$1"
    local url="$2"
    local body="${3:-}"
    local desc="${4:-}"
    local expected="${5:-}"

    local full_url="${BASE_URL}${url}"

    local curl_args=(-s -w "\n%{http_code}" -X "$method" "$full_url"
        -H "Content-Type: application/json")

    # 인증이 필요한 경우 토큰 헤더 추가
    if [ -n "$TOKEN" ]; then
        curl_args+=(-H "X-Seller-Token: ${TOKEN}")
    fi

    if [ -n "$body" ]; then
        curl_args+=(-d "$body")
    fi

    local response
    response=$(curl "${curl_args[@]}" 2>/dev/null || echo -e "\n000")

    local http_code
    http_code=$(echo "$response" | tail -1)
    local response_body
    response_body=$(echo "$response" | sed '$d')

    LAST_HTTP_CODE="$http_code"
    LAST_BODY="$response_body"

    # 성공/실패 판단
    local is_success=false
    if [ -n "$expected" ]; then
        [ "$http_code" = "$expected" ] && is_success=true
    else
        [[ "$http_code" =~ ^2[0-9][0-9]$ ]] && is_success=true
    fi

    if $is_success; then
        echo -e "  ${GREEN}[PASS]${NC} ${method} ${url} -> ${http_code} (${desc})"
        PASS_COUNT=$((PASS_COUNT + 1))
    elif [ "$http_code" = "000" ]; then
        echo -e "  ${RED}[FAIL]${NC} ${method} ${url} -> 연결 실패 (${desc})"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    else
        echo -e "  ${RED}[FAIL]${NC} ${method} ${url} -> ${http_code} (${desc})"
        if [ -n "$response_body" ]; then
            echo "         응답: $(echo "$response_body" | head -c 300)"
        fi
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi
}

skip() {
    local desc="$1"
    echo -e "  ${YELLOW}[SKIP]${NC} ${desc}"
    SKIP_COUNT=$((SKIP_COUNT + 1))
}

# -----------------------------------------------------------------------------
# 시작
# -----------------------------------------------------------------------------
echo ""
echo "============================================================"
echo " 세토프(setof) 로컬 E2E 테스트"
echo " 대상: ${BASE_URL}"
echo " 시작: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"
echo ""

# 서버 연결 확인
echo -n "서버 연결 확인... "
if curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/actuator/health" 2>/dev/null | grep -q "200"; then
    echo -e "${GREEN}OK${NC}"
elif curl -s -o /dev/null --max-time 3 "${BASE_URL}/" 2>/dev/null; then
    echo -e "${YELLOW}응답 있음 (health 엔드포인트 없을 수 있음)${NC}"
else
    echo -e "${RED}연결 실패 - 서버가 실행 중인지 확인하세요${NC}"
    exit 1
fi

# =============================================================================
# 데이터 준비
# =============================================================================
echo ""
echo -e "${CYAN}=== 데이터 준비 ===${NC}"

# 1. market DB에서 api_key + api_secret 조회
echo -n "  API Key/Secret 조회 (market DB)... "
API_CREDS=$(db_query "market" "SELECT api_key, api_secret FROM seller_sales_channels WHERE seller_id = 1 LIMIT 1" 2>/dev/null || echo "")
if [ -n "$API_CREDS" ]; then
    API_KEY=$(echo "$API_CREDS" | cut -f1)
    API_SECRET=$(echo "$API_CREDS" | cut -f2)
    echo -e "${GREEN}OK${NC}"
else
    echo -e "${YELLOW}DB 조회 실패, 환경변수에서 조회${NC}"
    API_KEY="${E2E_API_KEY:?E2E_API_KEY 환경변수 필요}"
    API_SECRET="${E2E_API_SECRET:?E2E_API_SECRET 환경변수 필요}"
fi

# 2. 세토프 DB에서 카테고리 ID 조회
echo -n "  카테고리 ID 조회 (setof DB)... "
CATEGORY_ID=$(db_query "setof" "SELECT id FROM categories LIMIT 1" 2>/dev/null || echo "")
if [ -z "$CATEGORY_ID" ]; then
    CATEGORY_ID=1
    echo -e "${YELLOW}DB 조회 실패, 기본값 사용: ${CATEGORY_ID}${NC}"
else
    echo -e "${GREEN}${CATEGORY_ID}${NC}"
fi

# 3. 세토프 DB에서 브랜드 ID 조회
echo -n "  브랜드 ID 조회 (setof DB)... "
BRAND_ID=$(db_query "setof" "SELECT id FROM brands LIMIT 1" 2>/dev/null || echo "")
if [ -z "$BRAND_ID" ]; then
    BRAND_ID=1
    echo -e "${YELLOW}DB 조회 실패, 기본값 사용: ${BRAND_ID}${NC}"
else
    echo -e "${GREEN}${BRAND_ID}${NC}"
fi

# =============================================================================
# Phase 1: 인증
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 1: 인증 ===${NC}"

call_api "POST" "/api/admin/v1/auth/seller-token" \
    "{\"apiKey\":\"${API_KEY}\",\"apiSecret\":\"${API_SECRET}\"}" \
    "셀러 토큰 발급"

if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    TOKEN=$(echo "$LAST_BODY" | jq -r '.accessToken // empty' 2>/dev/null || echo "")
    if [ -n "$TOKEN" ]; then
        echo -e "  -> 토큰 확보: ${TOKEN:0:40}..."
    else
        echo -e "  ${RED}-> 토큰 추출 실패. 응답: $(echo "$LAST_BODY" | head -c 200)${NC}"
        echo -e "  ${YELLOW}-> 토큰 없이 계속 진행합니다${NC}"
    fi
else
    echo -e "  ${YELLOW}-> 인증 실패, 토큰 없이 진행${NC}"
fi

# =============================================================================
# Phase 2: 배송정책
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 2: 배송정책 ===${NC}"

# 배송정책 등록
call_api "POST" "/api/v2/shipping-policies" \
    '{
        "policyName": "E2E 테스트 배송정책",
        "defaultPolicy": false,
        "shippingFeeType": "CONDITIONAL_FREE",
        "baseFee": 3000,
        "freeThreshold": 50000,
        "jejuExtraFee": 3000,
        "islandExtraFee": 5000,
        "returnFee": 3000,
        "exchangeFee": 6000,
        "leadTime": {
            "minDays": 1,
            "maxDays": 3,
            "cutoffTime": "14:00"
        }
    }' \
    "배송정책 등록" \
    "201"

SHIPPING_POLICY_ID=""
if [ "$LAST_HTTP_CODE" = "201" ]; then
    SHIPPING_POLICY_ID=$(echo "$LAST_BODY" | jq -r '.data.policyId // empty' 2>/dev/null || echo "")
    if [ -n "$SHIPPING_POLICY_ID" ]; then
        echo -e "  -> 배송정책 ID: ${SHIPPING_POLICY_ID}"
    fi
fi

# 배송정책 ID 없으면 DB에서 조회
if [ -z "$SHIPPING_POLICY_ID" ]; then
    SHIPPING_POLICY_ID=$(db_query "setof" "SELECT id FROM shipping_policies WHERE deleted = 0 ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "1")
    echo -e "  -> DB에서 배송정책 ID 조회: ${SHIPPING_POLICY_ID}"
fi

# 배송정책 수정
call_api "PUT" "/api/v2/shipping-policies/${SHIPPING_POLICY_ID}" \
    '{
        "policyName": "E2E 수정 배송정책",
        "defaultPolicy": false,
        "shippingFeeType": "CONDITIONAL_FREE",
        "baseFee": 2500,
        "freeThreshold": 30000,
        "jejuExtraFee": 3000,
        "islandExtraFee": 5000,
        "returnFee": 2500,
        "exchangeFee": 5000,
        "leadTime": {
            "minDays": 1,
            "maxDays": 2,
            "cutoffTime": "15:00"
        }
    }' \
    "배송정책 수정 (policyId=${SHIPPING_POLICY_ID})" \
    "204"

# =============================================================================
# Phase 3: 환불정책
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 3: 환불정책 ===${NC}"

# 환불정책 등록
call_api "POST" "/api/v2/refund-policies" \
    '{
        "policyName": "E2E 테스트 환불정책",
        "defaultPolicy": false,
        "returnPeriodDays": 7,
        "exchangePeriodDays": 7,
        "nonReturnableConditions": ["OPENED_PACKAGING", "USED_PRODUCT"],
        "partialRefundEnabled": true,
        "inspectionRequired": true,
        "inspectionPeriodDays": 3,
        "additionalInfo": "E2E 테스트 환불 정책입니다"
    }' \
    "환불정책 등록" \
    "201"

REFUND_POLICY_ID=""
if [ "$LAST_HTTP_CODE" = "201" ]; then
    REFUND_POLICY_ID=$(echo "$LAST_BODY" | jq -r '.data.policyId // empty' 2>/dev/null || echo "")
    if [ -n "$REFUND_POLICY_ID" ]; then
        echo -e "  -> 환불정책 ID: ${REFUND_POLICY_ID}"
    fi
fi

# 환불정책 ID 없으면 DB에서 조회
if [ -z "$REFUND_POLICY_ID" ]; then
    REFUND_POLICY_ID=$(db_query "setof" "SELECT id FROM refund_policies WHERE deleted = 0 ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "1")
    echo -e "  -> DB에서 환불정책 ID 조회: ${REFUND_POLICY_ID}"
fi

# 환불정책 수정
call_api "PUT" "/api/v2/refund-policies/${REFUND_POLICY_ID}" \
    '{
        "policyName": "E2E 수정 환불정책",
        "defaultPolicy": false,
        "returnPeriodDays": 14,
        "exchangePeriodDays": 14,
        "nonReturnableConditions": ["OPENED_PACKAGING", "USED_PRODUCT", "MISSING_TAG"],
        "partialRefundEnabled": true,
        "inspectionRequired": false,
        "inspectionPeriodDays": 0,
        "additionalInfo": "수정된 E2E 테스트 환불 정책입니다"
    }' \
    "환불정책 수정 (policyId=${REFUND_POLICY_ID})" \
    "204"

# =============================================================================
# Phase 4: 상품 등록/조회/수정
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 4: 상품 등록/조회/수정 ===${NC}"

# 상품 등록
call_api "POST" "/api/v2/admin/product-groups" \
    "{
        \"brandId\": ${BRAND_ID},
        \"categoryId\": ${CATEGORY_ID},
        \"shippingPolicyId\": ${SHIPPING_POLICY_ID},
        \"refundPolicyId\": ${REFUND_POLICY_ID},
        \"productGroupName\": \"E2E 테스트 상품\",
        \"optionType\": \"COMBINATION\",
        \"regularPrice\": 50000,
        \"currentPrice\": 39900,
        \"images\": [
            {\"imageType\": \"THUMBNAIL\", \"imageUrl\": \"https://example.com/test.jpg\", \"sortOrder\": 0}
        ],
        \"optionGroups\": [
            {
                \"optionGroupName\": \"사이즈\",
                \"sortOrder\": 0,
                \"optionValues\": [
                    {\"optionValueName\": \"M\", \"sortOrder\": 0},
                    {\"optionValueName\": \"L\", \"sortOrder\": 1}
                ]
            }
        ],
        \"products\": [
            {
                \"skuCode\": \"TEST-M\",
                \"regularPrice\": 50000,
                \"currentPrice\": 39900,
                \"stockQuantity\": 100,
                \"sortOrder\": 0,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"M\"}]
            },
            {
                \"skuCode\": \"TEST-L\",
                \"regularPrice\": 50000,
                \"currentPrice\": 39900,
                \"stockQuantity\": 50,
                \"sortOrder\": 1,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"L\"}]
            }
        ],
        \"description\": {
            \"content\": \"<p>E2E 테스트 상품 설명</p>\",
            \"descriptionImages\": []
        },
        \"notice\": {
            \"entries\": [
                {\"noticeFieldId\": 1, \"fieldName\": \"소재\", \"fieldValue\": \"면 100%\"}
            ]
        }
    }" \
    "상품 등록" \
    "201"

PRODUCT_GROUP_ID=""
if [ "$LAST_HTTP_CODE" = "201" ]; then
    PRODUCT_GROUP_ID=$(echo "$LAST_BODY" | jq -r '.data.productGroupId // empty' 2>/dev/null || echo "")
    if [ -n "$PRODUCT_GROUP_ID" ]; then
        echo -e "  -> 상품그룹 ID: ${PRODUCT_GROUP_ID}"
    fi
fi

# 상품그룹 ID 없으면 DB에서 조회
if [ -z "$PRODUCT_GROUP_ID" ]; then
    PRODUCT_GROUP_ID=$(db_query "setof" "SELECT id FROM product_groups WHERE deleted = 0 ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "")
    if [ -n "$PRODUCT_GROUP_ID" ]; then
        echo -e "  -> DB에서 상품그룹 ID 조회: ${PRODUCT_GROUP_ID}"
    else
        PRODUCT_GROUP_ID="1"
        echo -e "  -> 기본값 사용: ${PRODUCT_GROUP_ID}"
    fi
fi

# 상품 조회
call_api "GET" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}" \
    "" \
    "상품 조회 (productGroupId=${PRODUCT_GROUP_ID})"

# 조회 응답에서 products[0].id 추출 (Phase 6에서 사용)
FIRST_PRODUCT_ID=""
if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    FIRST_PRODUCT_ID=$(echo "$LAST_BODY" | jq -r '.data.optionProductMatrix.products[0].id // empty' 2>/dev/null || echo "")
    if [ -n "$FIRST_PRODUCT_ID" ]; then
        echo -e "  -> 첫 번째 상품 ID (조회에서 추출): ${FIRST_PRODUCT_ID}"
    fi
fi

# 상품 전체 수정 (PUT)
# products에 productId 추가
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}" \
    "{
        \"brandId\": ${BRAND_ID},
        \"categoryId\": ${CATEGORY_ID},
        \"shippingPolicyId\": ${SHIPPING_POLICY_ID},
        \"refundPolicyId\": ${REFUND_POLICY_ID},
        \"productGroupName\": \"E2E 테스트 상품 (전체수정)\",
        \"optionType\": \"COMBINATION\",
        \"regularPrice\": 55000,
        \"currentPrice\": 42900,
        \"images\": [
            {\"imageType\": \"THUMBNAIL\", \"imageUrl\": \"https://example.com/test-updated.jpg\", \"sortOrder\": 0}
        ],
        \"optionGroups\": [
            {
                \"optionGroupName\": \"사이즈\",
                \"sortOrder\": 0,
                \"optionValues\": [
                    {\"optionValueName\": \"M\", \"sortOrder\": 0},
                    {\"optionValueName\": \"L\", \"sortOrder\": 1}
                ]
            }
        ],
        \"products\": [
            {
                \"skuCode\": \"TEST-M\",
                \"regularPrice\": 55000,
                \"currentPrice\": 42900,
                \"stockQuantity\": 150,
                \"sortOrder\": 0,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"M\"}]
            },
            {
                \"skuCode\": \"TEST-L\",
                \"regularPrice\": 55000,
                \"currentPrice\": 42900,
                \"stockQuantity\": 80,
                \"sortOrder\": 1,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"L\"}]
            }
        ],
        \"description\": {
            \"content\": \"<p>전체 수정된 E2E 테스트 상품 설명</p>\",
            \"descriptionImages\": []
        },
        \"notice\": {
            \"entries\": [
                {\"noticeFieldId\": 1, \"fieldName\": \"소재\", \"fieldValue\": \"폴리에스터 100%\"}
            ]
        }
    }" \
    "상품 전체 수정 (productGroupId=${PRODUCT_GROUP_ID})" \
    "204"

# =============================================================================
# Phase 5: 부분 수정
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 5: 부분 수정 ===${NC}"

# 기본 정보 수정
call_api "PATCH" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/basic-info" \
    "{
        \"productGroupName\": \"E2E 수정 상품\",
        \"brandId\": ${BRAND_ID},
        \"categoryId\": ${CATEGORY_ID},
        \"shippingPolicyId\": ${SHIPPING_POLICY_ID},
        \"refundPolicyId\": ${REFUND_POLICY_ID}
    }" \
    "기본 정보 부분 수정 (productGroupId=${PRODUCT_GROUP_ID})" \
    "204"

# 옵션/상품 부분 수정
call_api "PATCH" "/api/v2/admin/products/product-groups/${PRODUCT_GROUP_ID}" \
    '{
        "optionGroups": [
            {
                "optionGroupName": "사이즈",
                "sortOrder": 0,
                "optionValues": [
                    {"optionValueName": "M", "sortOrder": 0},
                    {"optionValueName": "L", "sortOrder": 1},
                    {"optionValueName": "XL", "sortOrder": 2}
                ]
            }
        ],
        "products": [
            {
                "skuCode": "TEST-M",
                "regularPrice": 55000,
                "currentPrice": 42900,
                "stockQuantity": 150,
                "sortOrder": 0,
                "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "M"}]
            },
            {
                "skuCode": "TEST-L",
                "regularPrice": 55000,
                "currentPrice": 42900,
                "stockQuantity": 80,
                "sortOrder": 1,
                "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "L"}]
            },
            {
                "skuCode": "TEST-XL",
                "regularPrice": 55000,
                "currentPrice": 42900,
                "stockQuantity": 30,
                "sortOrder": 2,
                "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "XL"}]
            }
        ]
    }' \
    "옵션/상품 부분 수정 (productGroupId=${PRODUCT_GROUP_ID})" \
    "204"

# 이미지 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/images" \
    '{
        "images": [
            {"imageType": "THUMBNAIL", "imageUrl": "https://example.com/updated.jpg", "sortOrder": 0}
        ]
    }' \
    "이미지 수정 (productGroupId=${PRODUCT_GROUP_ID})" \
    "204"

# 설명 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/description" \
    '{
        "content": "<p>수정된 설명</p>",
        "descriptionImages": []
    }' \
    "설명 수정 (productGroupId=${PRODUCT_GROUP_ID})" \
    "204"

# 고시정보 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/notice" \
    '{
        "entries": [
            {"noticeFieldId": 1, "fieldName": "소재", "fieldValue": "폴리에스터 100%"}
        ]
    }' \
    "고시정보 수정 (productGroupId=${PRODUCT_GROUP_ID})" \
    "204"

# =============================================================================
# Phase 6: 개별 상품 수정
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 6: 개별 상품 수정 ===${NC}"

# 전체수정 후 상품 ID가 바뀌므로 재조회해서 최신 ID 추출
echo -e "  -> 전체수정 후 재조회로 최신 상품 ID 추출..."
call_api "GET" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}" "" \
    "상품 재조회 (productGroupId=${PRODUCT_GROUP_ID})"
FIRST_PRODUCT_ID=""
if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    FIRST_PRODUCT_ID=$(echo "$LAST_BODY" | jq -r '.data.optionProductMatrix.products[0].id // empty' 2>/dev/null || echo "")
fi

if [ -n "$FIRST_PRODUCT_ID" ]; then
    echo -e "  -> 사용할 상품 ID: ${FIRST_PRODUCT_ID}"

    # 가격 수정
    call_api "PATCH" "/api/v2/admin/products/${FIRST_PRODUCT_ID}/price" \
        '{
            "regularPrice": 45000,
            "currentPrice": 35900
        }' \
        "가격 수정 (productId=${FIRST_PRODUCT_ID})" \
        "204"

    # 재고 수정
    call_api "PATCH" "/api/v2/admin/products/${FIRST_PRODUCT_ID}/stock" \
        '{
            "stockQuantity": 200
        }' \
        "재고 수정 (productId=${FIRST_PRODUCT_ID})" \
        "204"
else
    skip "개별 상품 ID를 찾을 수 없어 가격/재고 수정 건너뜀"
    skip "개별 상품 ID를 찾을 수 없어 가격/재고 수정 건너뜀"
fi

# =============================================================================
# Phase 7: 주문/클레임
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 7: 주문/클레임 ===${NC}"

# --- 주문 확인 (PENDING → CONFIRMED) ---
PENDING_ITEM=$(db_query "setof" "SELECT id FROM order_items WHERE order_item_status = 'PENDING' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$PENDING_ITEM" ]; then
    echo -e "  -> PENDING 주문 아이템 ID: ${PENDING_ITEM}"
    call_api "POST" "/api/v2/orders/${PENDING_ITEM}/confirm" "" \
        "주문 확인 PENDING→CONFIRMED (orderItemId=${PENDING_ITEM})"
else
    skip "PENDING 상태 주문 아이템 없음"
fi

# --- 발송 준비 (CONFIRMED → SHIPPING_READY) ---
CONFIRMED_ITEM=$(db_query "setof" "SELECT id FROM order_items WHERE order_item_status = 'CONFIRMED' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$CONFIRMED_ITEM" ]; then
    echo -e "  -> CONFIRMED 주문 아이템 ID: ${CONFIRMED_ITEM}"
    call_api "POST" "/api/v2/orders/${CONFIRMED_ITEM}/ready-to-ship" "" \
        "발송 준비 CONFIRMED→SHIPPING_READY (orderItemId=${CONFIRMED_ITEM})"
else
    skip "CONFIRMED 상태 주문 아이템 없음"
fi

# --- 운송장 등록 (SHIPPING_READY → SHIPPED) ---
READY_ITEM=$(db_query "setof" "SELECT id FROM order_items WHERE order_item_status = 'SHIPPING_READY' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$READY_ITEM" ]; then
    echo -e "  -> SHIPPING_READY 주문 아이템 ID: ${READY_ITEM}"
    call_api "POST" "/api/v2/shipments" \
        "{\"orderItemId\":${READY_ITEM},\"shipmentCompanyCode\":\"CJ\",\"invoiceNo\":\"E2E$(date +%s)\",\"senderName\":\"E2E발송자\"}" \
        "운송장 등록 (orderItemId=${READY_ITEM})"
else
    skip "SHIPPING_READY 상태 주문 아이템 없음"
fi

# --- 취소 승인 (REQUESTED → APPROVED) ---
CANCEL_ID=$(db_query "setof" "SELECT id FROM cancel WHERE cancel_status = 'REQUESTED' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$CANCEL_ID" ]; then
    echo -e "  -> 취소 ID (승인용): ${CANCEL_ID}"
    call_api "POST" "/api/v2/cancels/${CANCEL_ID}/approve" "" \
        "취소 승인 (cancelId=${CANCEL_ID})"
else
    skip "REQUESTED 상태 취소 데이터 없음"
fi

# --- 취소 거부 (REQUESTED → REJECTED) ---
CANCEL_REJECT_ID=$(db_query "setof" "SELECT id FROM cancel WHERE cancel_status = 'REQUESTED' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$CANCEL_REJECT_ID" ]; then
    echo -e "  -> 취소 ID (거부용): ${CANCEL_REJECT_ID}"
    call_api "POST" "/api/v2/cancels/${CANCEL_REJECT_ID}/reject" \
        '{"rejectReason":"E2E 테스트 거부"}' \
        "취소 거부 (cancelId=${CANCEL_REJECT_ID})"
else
    skip "REQUESTED 상태 취소 데이터 없음 (거부용)"
fi

# --- 환불 완료 (COLLECTED → COMPLETED) ---
REFUND_COMPLETE_ID=$(db_query "setof" "SELECT id FROM refund_claim WHERE refund_status = 'COLLECTED' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$REFUND_COMPLETE_ID" ]; then
    echo -e "  -> 환불 ID (완료용): ${REFUND_COMPLETE_ID}"
    call_api "POST" "/api/v2/refunds/${REFUND_COMPLETE_ID}/complete" "" \
        "환불 완료 COLLECTED→COMPLETED (refundId=${REFUND_COMPLETE_ID})"
else
    skip "COLLECTED 상태 환불 데이터 없음 (REQUESTED→COMPLETED 직접 전이 불가)"
fi

# --- 환불 거부 (REQUESTED → REJECTED) ---
REFUND_REJECT_ID=$(db_query "setof" "SELECT id FROM refund_claim WHERE refund_status = 'REQUESTED' LIMIT 1" 2>/dev/null || echo "")
if [ -n "$REFUND_REJECT_ID" ]; then
    echo -e "  -> 환불 ID (거부용): ${REFUND_REJECT_ID}"
    call_api "POST" "/api/v2/refunds/${REFUND_REJECT_ID}/reject" \
        '{"rejectReason":"E2E 환불 거부 테스트"}' \
        "환불 거부 (refundId=${REFUND_REJECT_ID})"
else
    skip "REQUESTED 상태 환불 데이터 없음"
fi

# =============================================================================
# Phase 8: 이미지 변형
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 8: 이미지 변형 ===${NC}"

# DB에서 이미지 ID 조회
SOURCE_IMAGE_ID=$(db_query "setof" "SELECT id FROM product_group_images WHERE deleted = 0 ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "")
if [ -z "$SOURCE_IMAGE_ID" ]; then
    SOURCE_IMAGE_ID="1"
fi
echo -e "  -> 소스 이미지 ID: ${SOURCE_IMAGE_ID}"

call_api "PUT" "/api/v2/admin/image-variants/sync" \
    "{
        \"sourceImageId\": ${SOURCE_IMAGE_ID},
        \"sourceType\": \"PRODUCT_GROUP_IMAGE\",
        \"variants\": [
            {
                \"variantType\": \"SMALL_WEBP\",
                \"resultAssetId\": \"test-asset-001\",
                \"variantUrl\": \"https://cdn.example.com/test.webp\",
                \"width\": 300,
                \"height\": 300
            }
        ]
    }" \
    "이미지 변형 동기화"

# =============================================================================
# Phase 9: 셀러 등록/수정
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 9: 셀러 등록/수정 ===${NC}"

call_api "POST" "/api/v2/admin/sellers" \
    '{
        "sellerInfo": {
            "sellerName": "E2E 테스트 셀러 '$(date +%s)'",
            "displayName": "E2E셀러",
            "logoUrl": "https://example.com/logo.png",
            "description": "E2E 테스트용 셀러입니다"
        },
        "businessInfo": {
            "registrationNumber": "'$(printf "%03d-%02d-%05d" $((RANDOM%999)) $((RANDOM%99)) $((RANDOM%99999)))'",
            "companyName": "E2E테스트주식회사",
            "representative": "홍길동",
            "saleReportNumber": "2026-E2E-'$(date +%s)'",
            "businessAddress": {
                "zipCode": "06234",
                "line1": "서울시 강남구 테헤란로 1",
                "line2": "10층"
            },
            "csContact": {
                "phone": "02-1234-5678",
                "email": "cs@e2etest.com",
                "mobile": "010-1234-5678"
            }
        }
    }' \
    "셀러 등록" "201"

CREATED_SELLER_ID=$(echo "$LAST_BODY" | jq -r '.data.sellerId // empty' 2>/dev/null || echo "")
if [ -z "$CREATED_SELLER_ID" ] || [ "$CREATED_SELLER_ID" = "null" ]; then
    echo -e "  -> 셀러 ID 추출 실패, DB에서 조회"
    CREATED_SELLER_ID=$(db_query "setof" "SELECT id FROM sellers ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "1")
fi
echo -e "  -> 셀러 ID: ${CREATED_SELLER_ID}"

call_api "PUT" "/api/v2/admin/sellers/${CREATED_SELLER_ID}" \
    '{
        "sellerName": "E2E 수정된 셀러",
        "displayName": "E2E수정",
        "logoUrl": "https://example.com/logo-updated.png",
        "description": "수정된 셀러 설명",
        "csInfo": {
            "phone": "02-9876-5432",
            "email": "updated@e2etest.com",
            "mobile": "010-9876-5432"
        },
        "businessInfo": {
            "registrationNumber": "'$(printf "%03d-%02d-%05d" $((RANDOM%999)) $((RANDOM%99)) $((RANDOM%99999)))'",
            "companyName": "수정된주식회사",
            "representative": "김수정",
            "saleReportNumber": "2026-수정-'$(date +%s)'",
            "businessAddress": {
                "zipCode": "06235",
                "line1": "서울시 강남구 역삼로 100",
                "line2": "20층"
            }
        }
    }' \
    "셀러 수정 (sellerId=${CREATED_SELLER_ID})" "204"

# =============================================================================
# Phase 10: 셀러 주소 CRUD
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 10: 셀러 주소 CRUD ===${NC}"

# JWT에서 sellerId=1 사용 (토큰 발급한 셀러)
ADDR_SELLER_ID="1"

call_api "POST" "/api/v2/admin/seller-addresses/sellers/${ADDR_SELLER_ID}" \
    '{
        "addressType": "SHIPPING",
        "addressName": "E2E 배송 창고",
        "zipCode": "12345",
        "roadAddress": "경기도 성남시 분당구 판교로 1",
        "detailAddress": "B동 1층",
        "defaultAddress": false
    }' \
    "셀러 주소 등록 (sellerId=${ADDR_SELLER_ID})" "201"

ADDR_ID=$(echo "$LAST_BODY" | jq -r '.data // empty' 2>/dev/null || echo "")
if [ -z "$ADDR_ID" ] || [ "$ADDR_ID" = "null" ]; then
    ADDR_ID=$(db_query "setof" "SELECT id FROM seller_addresses WHERE seller_id=${ADDR_SELLER_ID} ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "")
fi
echo -e "  -> 주소 ID: ${ADDR_ID}"

if [ -n "$ADDR_ID" ] && [ "$ADDR_ID" != "null" ]; then
    call_api "PUT" "/api/v2/admin/seller-addresses/sellers/${ADDR_SELLER_ID}/${ADDR_ID}" \
        '{
            "addressType": "RETURN",
            "addressName": "E2E 반품 창고 (수정)",
            "zipCode": "54321",
            "roadAddress": "경기도 성남시 분당구 판교로 999",
            "detailAddress": "A동 2층",
            "defaultAddress": true
        }' \
        "셀러 주소 수정 (addressId=${ADDR_ID})" "204"

    call_api "DELETE" "/api/v2/admin/seller-addresses/sellers/${ADDR_SELLER_ID}/${ADDR_ID}" \
        "" \
        "셀러 주소 삭제 (addressId=${ADDR_ID})" "204"
else
    skip "주소 ID를 찾을 수 없어 수정/삭제 건너뜀"
    skip "주소 ID를 찾을 수 없어 수정/삭제 건너뜀"
fi

# =============================================================================
# 결과 요약
# =============================================================================
echo ""
echo "============================================================"
echo " 테스트 결과 요약"
echo "============================================================"
TOTAL=$((PASS_COUNT + FAIL_COUNT + SKIP_COUNT))
echo -e "  전체: ${TOTAL}"
echo -e "  ${GREEN}성공: ${PASS_COUNT}${NC}"
echo -e "  ${RED}실패: ${FAIL_COUNT}${NC}"
echo -e "  ${YELLOW}건너뜀: ${SKIP_COUNT}${NC}"
echo ""
echo " 종료: $(date '+%Y-%m-%d %H:%M:%S')"
echo "============================================================"

# 실패가 있으면 exit code 1
if [ "$FAIL_COUNT" -gt 0 ]; then
    exit 1
fi
exit 0
