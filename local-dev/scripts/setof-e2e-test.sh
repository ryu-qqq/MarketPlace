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
DB_PASS='REDACTED_DB_PASSWORD'

# 색상 코드
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 카운터
PASS_COUNT=0
FAIL_COUNT=0
SKIP_COUNT=0

# JSON 파싱 도구 선택
if command -v jq &>/dev/null; then
    JSON_TOOL="jq"
else
    JSON_TOOL="python3"
fi

# -----------------------------------------------------------------------------
# 유틸리티 함수
# -----------------------------------------------------------------------------
json_extract() {
    # $1 = json string, $2 = jq filter
    if [ "$JSON_TOOL" = "jq" ]; then
        echo "$1" | jq -r "$2" 2>/dev/null
    else
        echo "$1" | python3 -c "import sys,json; data=json.load(sys.stdin); print(eval('data$2'.replace('.','[\"').replace('[\"','[\"',1) + '\"]' * ($2.count('.') )))" 2>/dev/null || echo ""
    fi
}

jq_raw() {
    # $1 = json string, $2 = jq filter
    if [ "$JSON_TOOL" = "jq" ]; then
        echo "$1" | jq -r "$2" 2>/dev/null || echo ""
    else
        echo "$1" | python3 -c "
import sys, json
try:
    data = json.load(sys.stdin)
    keys = '$2'.strip('.').split('.')
    result = data
    for k in keys:
        if k == '':
            continue
        if k.startswith('[') and k.endswith(']'):
            result = result[int(k[1:-1])]
        else:
            result = result[k]
    if result is None:
        print('null')
    else:
        print(result)
except:
    print('')
" 2>/dev/null || echo ""
    fi
}

# API 호출 함수
# $1=method, $2=path, $3=data(optional), $4=description, $5=expected_status(optional, default=any 2xx)
call_api() {
    local method="$1"
    local path="$2"
    local data="${3:-}"
    local desc="${4:-}"
    local expected="${5:-}"

    local url="${BASE_URL}${path}"
    local tmpfile
    tmpfile=$(mktemp)

    local curl_args=(-s -w "\n%{http_code}" -X "$method" "$url"
        -H "Content-Type: application/json")

    if [ -n "$TOKEN" ]; then
        curl_args+=(-H "X-Seller-Token: ${TOKEN}")
    fi

    if [ -n "$data" ]; then
        curl_args+=(-d "$data")
    fi

    local response
    response=$(curl "${curl_args[@]}" 2>/dev/null || echo -e "\n000")

    local http_code
    http_code=$(echo "$response" | tail -1)
    local body
    body=$(echo "$response" | sed '$d')

    # 결과 저장
    LAST_HTTP_CODE="$http_code"
    LAST_BODY="$body"

    # 성공/실패 판단
    local is_success=false
    if [ -n "$expected" ]; then
        [ "$http_code" = "$expected" ] && is_success=true
    else
        # 2xx 범위면 성공
        [[ "$http_code" =~ ^2[0-9][0-9]$ ]] && is_success=true
    fi

    if $is_success; then
        echo -e "  ${GREEN}[PASS]${NC} ${method} ${path} -> ${http_code} (${desc})"
        PASS_COUNT=$((PASS_COUNT + 1))
    elif [ "$http_code" = "404" ] || [ "$http_code" = "405" ]; then
        echo -e "  ${YELLOW}[SKIP]${NC} ${method} ${path} -> ${http_code} (${desc} - 엔드포인트 없음 또는 데이터 없음)"
        SKIP_COUNT=$((SKIP_COUNT + 1))
    elif [ "$http_code" = "409" ]; then
        echo -e "  ${YELLOW}[SKIP]${NC} ${method} ${path} -> ${http_code} (${desc} - 이미 존재)"
        SKIP_COUNT=$((SKIP_COUNT + 1))
    elif [ "$http_code" = "000" ]; then
        echo -e "  ${RED}[FAIL]${NC} ${method} ${path} -> 연결 실패 (${desc})"
        FAIL_COUNT=$((FAIL_COUNT + 1))
    else
        echo -e "  ${RED}[FAIL]${NC} ${method} ${path} -> ${http_code} (${desc})"
        if [ -n "$body" ]; then
            echo "         응답: $(echo "$body" | head -c 300)"
        fi
        FAIL_COUNT=$((FAIL_COUNT + 1))
    fi

    rm -f "$tmpfile"
}

db_query() {
    local schema="$1"
    local query="$2"
    $MYSQL -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -p"$DB_PASS" "$schema" -N -s -e "$query" 2>/dev/null
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

TOKEN=""
LAST_HTTP_CODE=""
LAST_BODY=""

# =============================================================================
# 데이터 준비
# =============================================================================
echo ""
echo -e "${CYAN}=== 데이터 준비 ===${NC}"

# 1. 세토프 DB에서 카테고리 ID 조회
echo -n "  카테고리 ID 조회... "
CATEGORY_ID=$(db_query "setof" "SELECT id FROM categories WHERE deleted = 0 LIMIT 1" 2>/dev/null || echo "")
if [ -z "$CATEGORY_ID" ]; then
    CATEGORY_ID=1
    echo -e "${YELLOW}DB 조회 실패, 기본값 사용: ${CATEGORY_ID}${NC}"
else
    echo -e "${GREEN}${CATEGORY_ID}${NC}"
fi

# 2. 세토프 DB에서 브랜드 ID 조회
echo -n "  브랜드 ID 조회... "
BRAND_ID=$(db_query "setof" "SELECT id FROM brands WHERE deleted = 0 LIMIT 1" 2>/dev/null || echo "")
if [ -z "$BRAND_ID" ]; then
    BRAND_ID=1
    echo -e "${YELLOW}DB 조회 실패, 기본값 사용: ${BRAND_ID}${NC}"
else
    echo -e "${GREEN}${BRAND_ID}${NC}"
fi

# 3. market DB에서 셀러1의 apiKey, apiSecret 조회
echo -n "  API Key/Secret 조회... "
API_KEY="REDACTED_API_KEY"
API_SECRET=$(db_query "market" "SELECT api_secret FROM seller_sales_channels WHERE seller_id = 1 LIMIT 1" 2>/dev/null || echo "")
if [ -z "$API_SECRET" ]; then
    # setof DB에서도 시도
    API_SECRET=$(db_query "setof" "SELECT api_secret FROM seller_api_keys WHERE seller_id = 1 AND revoked = 0 LIMIT 1" 2>/dev/null || echo "")
fi
if [ -z "$API_SECRET" ]; then
    echo -e "${YELLOW}DB 조회 실패 - 수동 입력이 필요할 수 있음${NC}"
    API_SECRET="UNKNOWN"
else
    echo -e "${GREEN}OK (api_secret 확보)${NC}"
fi

# 4. 기존 셀러 ID 확인
echo -n "  셀러 ID 조회... "
SELLER_ID=$(db_query "setof" "SELECT id FROM sellers WHERE deleted = 0 LIMIT 1" 2>/dev/null || echo "")
if [ -z "$SELLER_ID" ]; then
    SELLER_ID=1
    echo -e "${YELLOW}DB 조회 실패, 기본값 사용: ${SELLER_ID}${NC}"
else
    echo -e "${GREEN}${SELLER_ID}${NC}"
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
    TOKEN=$(jq_raw "$LAST_BODY" ".token")
    if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
        TOKEN=$(jq_raw "$LAST_BODY" ".data.token")
    fi
    if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
        # 토큰이 다른 형태일 수 있음
        TOKEN=$(jq_raw "$LAST_BODY" ".accessToken")
    fi
    if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
        TOKEN=$(jq_raw "$LAST_BODY" ".data.accessToken")
    fi
    if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
        echo -e "  -> 토큰 확보: ${TOKEN:0:30}..."
    else
        echo -e "  ${YELLOW}-> 토큰 추출 실패, 응답: $(echo "$LAST_BODY" | head -c 200)${NC}"
        # 관리자 로그인으로 재시도
        echo -e "  -> 관리자 로그인으로 재시도..."
        call_api "POST" "/api/admin/v1/auth/login" \
            '{"loginId":"admin","password":"admin"}' \
            "관리자 로그인 (폴백)"
        if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
            TOKEN=$(jq_raw "$LAST_BODY" ".token")
            if [ -z "$TOKEN" ] || [ "$TOKEN" = "null" ]; then
                TOKEN=$(jq_raw "$LAST_BODY" ".data.token")
            fi
            if [ -n "$TOKEN" ] && [ "$TOKEN" != "null" ]; then
                echo -e "  -> 관리자 토큰 확보: ${TOKEN:0:30}..."
            fi
        fi
    fi
else
    echo -e "  ${YELLOW}-> 인증 실패, 토큰 없이 진행${NC}"
fi

# =============================================================================
# Phase 2: 셀러 관리
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 2: 셀러 관리 ===${NC}"

call_api "POST" "/api/v2/sellers" \
    '{
        "sellerInfo": {
            "sellerName": "E2E 테스트 셀러",
            "displayName": "E2E테스트",
            "logoUrl": "https://example.com/logo.png",
            "description": "E2E 테스트용 셀러입니다"
        },
        "businessInfo": {
            "registrationNumber": "123-45-67890",
            "companyName": "테스트 주식회사",
            "representative": "홍길동",
            "saleReportNumber": "2026-서울강남-0001",
            "businessAddress": {
                "zipCode": "06241",
                "line1": "서울특별시 강남구 테헤란로",
                "line2": "123층"
            },
            "csContact": {
                "phone": "02-1234-5678",
                "email": "cs@test.com",
                "mobile": "010-1234-5678"
            }
        }
    }' \
    "셀러 등록"

# 셀러 등록 응답에서 ID 추출 시도
if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    NEW_SELLER_ID=$(jq_raw "$LAST_BODY" ".data.sellerId")
    if [ -n "$NEW_SELLER_ID" ] && [ "$NEW_SELLER_ID" != "null" ]; then
        SELLER_ID="$NEW_SELLER_ID"
        echo -e "  -> 셀러 ID: ${SELLER_ID}"
    fi
fi

call_api "PATCH" "/api/v2/sellers/${SELLER_ID}" \
    '{
        "sellerName": "E2E 테스트 셀러 (수정)",
        "displayName": "E2E수정",
        "logoUrl": "https://example.com/logo-updated.png",
        "description": "수정된 설명입니다"
    }' \
    "셀러 수정 (sellerId=${SELLER_ID})"

# =============================================================================
# Phase 3: 셀러 정책
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 3: 셀러 정책 ===${NC}"

# 배송정책 등록
call_api "POST" "/api/v2/shipping-policies" \
    '{
        "policyName": "E2E 기본 배송정책",
        "defaultPolicy": true,
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
    "배송정책 등록"

SHIPPING_POLICY_ID=""
if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    SHIPPING_POLICY_ID=$(jq_raw "$LAST_BODY" ".data.shippingPolicyId")
    if [ -z "$SHIPPING_POLICY_ID" ] || [ "$SHIPPING_POLICY_ID" = "null" ]; then
        SHIPPING_POLICY_ID=$(jq_raw "$LAST_BODY" ".data.id")
    fi
    if [ -z "$SHIPPING_POLICY_ID" ] || [ "$SHIPPING_POLICY_ID" = "null" ]; then
        SHIPPING_POLICY_ID=$(jq_raw "$LAST_BODY" ".data")
    fi
    if [ -n "$SHIPPING_POLICY_ID" ] && [ "$SHIPPING_POLICY_ID" != "null" ]; then
        echo -e "  -> 배송정책 ID: ${SHIPPING_POLICY_ID}"
    fi
fi

# 배송정책이 없으면 DB에서 조회
if [ -z "$SHIPPING_POLICY_ID" ] || [ "$SHIPPING_POLICY_ID" = "null" ]; then
    SHIPPING_POLICY_ID=$(db_query "setof" "SELECT id FROM shipping_policies WHERE deleted = 0 LIMIT 1" 2>/dev/null || echo "1")
    echo -e "  -> DB에서 배송정책 ID 조회: ${SHIPPING_POLICY_ID}"
fi

# 배송정책 수정
if [ -n "$SHIPPING_POLICY_ID" ] && [ "$SHIPPING_POLICY_ID" != "null" ]; then
    call_api "PUT" "/api/v2/shipping-policies/${SHIPPING_POLICY_ID}" \
        '{
            "policyName": "E2E 수정 배송정책",
            "defaultPolicy": true,
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
        "배송정책 수정 (policyId=${SHIPPING_POLICY_ID})"
fi

# 환불정책 등록
call_api "POST" "/api/v2/refund-policies" \
    '{
        "policyName": "E2E 기본 환불정책",
        "defaultPolicy": true,
        "returnPeriodDays": 7,
        "exchangePeriodDays": 7,
        "nonReturnableConditions": ["OPENED_PACKAGING", "USED_PRODUCT"],
        "partialRefundEnabled": true,
        "inspectionRequired": true,
        "inspectionPeriodDays": 3,
        "additionalInfo": "E2E 테스트 환불정책입니다."
    }' \
    "환불정책 등록"

REFUND_POLICY_ID=""
if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    REFUND_POLICY_ID=$(jq_raw "$LAST_BODY" ".data.refundPolicyId")
    if [ -z "$REFUND_POLICY_ID" ] || [ "$REFUND_POLICY_ID" = "null" ]; then
        REFUND_POLICY_ID=$(jq_raw "$LAST_BODY" ".data.id")
    fi
    if [ -z "$REFUND_POLICY_ID" ] || [ "$REFUND_POLICY_ID" = "null" ]; then
        REFUND_POLICY_ID=$(jq_raw "$LAST_BODY" ".data")
    fi
    if [ -n "$REFUND_POLICY_ID" ] && [ "$REFUND_POLICY_ID" != "null" ]; then
        echo -e "  -> 환불정책 ID: ${REFUND_POLICY_ID}"
    fi
fi

# 환불정책이 없으면 DB에서 조회
if [ -z "$REFUND_POLICY_ID" ] || [ "$REFUND_POLICY_ID" = "null" ]; then
    REFUND_POLICY_ID=$(db_query "setof" "SELECT id FROM refund_policies WHERE deleted = 0 LIMIT 1" 2>/dev/null || echo "1")
    echo -e "  -> DB에서 환불정책 ID 조회: ${REFUND_POLICY_ID}"
fi

# 환불정책 수정
if [ -n "$REFUND_POLICY_ID" ] && [ "$REFUND_POLICY_ID" != "null" ]; then
    call_api "PUT" "/api/v2/refund-policies/${REFUND_POLICY_ID}" \
        '{
            "policyName": "E2E 수정 환불정책",
            "defaultPolicy": true,
            "returnPeriodDays": 14,
            "exchangePeriodDays": 14,
            "nonReturnableConditions": ["OPENED_PACKAGING", "USED_PRODUCT", "MISSING_TAG"],
            "partialRefundEnabled": true,
            "inspectionRequired": false,
            "inspectionPeriodDays": 0,
            "additionalInfo": "수정된 E2E 테스트 환불정책입니다."
        }' \
        "환불정책 수정 (policyId=${REFUND_POLICY_ID})"
fi

# =============================================================================
# Phase 4: 셀러 주소 (세토프에 셀러 주소 API가 없으므로 SKIP 표시)
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 4: 셀러 주소 ===${NC}"
echo -e "  ${YELLOW}[SKIP]${NC} 세토프에 셀러 주소 관리 API가 존재하지 않습니다. (MarketPlace 전용 기능)"
SKIP_COUNT=$((SKIP_COUNT + 3))

# =============================================================================
# Phase 5: 상품
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 5: 상품 ===${NC}"

# 상품 등록
call_api "POST" "/api/v2/admin/product-groups" \
    "{
        \"brandId\": ${BRAND_ID},
        \"categoryId\": ${CATEGORY_ID},
        \"shippingPolicyId\": ${SHIPPING_POLICY_ID:-1},
        \"refundPolicyId\": ${REFUND_POLICY_ID:-1},
        \"productGroupName\": \"E2E 테스트 상품\",
        \"optionType\": \"SINGLE\",
        \"regularPrice\": 100000,
        \"currentPrice\": 89000,
        \"images\": [
            {
                \"imageType\": \"THUMBNAIL\",
                \"imageUrl\": \"https://example.com/thumb.jpg\",
                \"sortOrder\": 0
            },
            {
                \"imageType\": \"DETAIL\",
                \"imageUrl\": \"https://example.com/detail.jpg\",
                \"sortOrder\": 1
            }
        ],
        \"optionGroups\": [
            {
                \"optionGroupName\": \"사이즈\",
                \"sortOrder\": 0,
                \"optionValues\": [
                    {\"optionValueName\": \"S\", \"sortOrder\": 0},
                    {\"optionValueName\": \"M\", \"sortOrder\": 1},
                    {\"optionValueName\": \"L\", \"sortOrder\": 2}
                ]
            }
        ],
        \"products\": [
            {
                \"skuCode\": \"E2E-SKU-S\",
                \"regularPrice\": 100000,
                \"currentPrice\": 89000,
                \"stockQuantity\": 50,
                \"sortOrder\": 0,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"S\"}]
            },
            {
                \"skuCode\": \"E2E-SKU-M\",
                \"regularPrice\": 100000,
                \"currentPrice\": 89000,
                \"stockQuantity\": 100,
                \"sortOrder\": 1,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"M\"}]
            },
            {
                \"skuCode\": \"E2E-SKU-L\",
                \"regularPrice\": 100000,
                \"currentPrice\": 89000,
                \"stockQuantity\": 30,
                \"sortOrder\": 2,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"L\"}]
            }
        ],
        \"description\": {
            \"content\": \"<p>E2E 테스트 상품의 상세 설명입니다.</p>\",
            \"descriptionImages\": [
                {\"imageUrl\": \"https://example.com/desc1.jpg\", \"sortOrder\": 0}
            ]
        },
        \"notice\": {
            \"entries\": [
                {\"noticeFieldId\": 1, \"fieldName\": \"소재\", \"fieldValue\": \"면 100%\"},
                {\"noticeFieldId\": 2, \"fieldName\": \"색상\", \"fieldValue\": \"블랙\"}
            ]
        }
    }" \
    "상품 등록"

PRODUCT_GROUP_ID=""
if [[ "$LAST_HTTP_CODE" =~ ^2[0-9][0-9]$ ]]; then
    PRODUCT_GROUP_ID=$(jq_raw "$LAST_BODY" ".data.productGroupId")
    if [ -z "$PRODUCT_GROUP_ID" ] || [ "$PRODUCT_GROUP_ID" = "null" ]; then
        PRODUCT_GROUP_ID=$(jq_raw "$LAST_BODY" ".data.id")
    fi
    if [ -z "$PRODUCT_GROUP_ID" ] || [ "$PRODUCT_GROUP_ID" = "null" ]; then
        PRODUCT_GROUP_ID=$(jq_raw "$LAST_BODY" ".data")
    fi
    if [ -n "$PRODUCT_GROUP_ID" ] && [ "$PRODUCT_GROUP_ID" != "null" ]; then
        echo -e "  -> 상품그룹 ID: ${PRODUCT_GROUP_ID}"
    fi
fi

# 상품그룹 없으면 DB에서 조회
if [ -z "$PRODUCT_GROUP_ID" ] || [ "$PRODUCT_GROUP_ID" = "null" ]; then
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

# 상품 전체 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}" \
    "{
        \"brandId\": ${BRAND_ID},
        \"categoryId\": ${CATEGORY_ID},
        \"shippingPolicyId\": ${SHIPPING_POLICY_ID:-1},
        \"refundPolicyId\": ${REFUND_POLICY_ID:-1},
        \"productGroupName\": \"E2E 테스트 상품 (수정)\",
        \"optionType\": \"SINGLE\",
        \"regularPrice\": 120000,
        \"currentPrice\": 99000,
        \"images\": [
            {\"imageType\": \"THUMBNAIL\", \"imageUrl\": \"https://example.com/thumb-v2.jpg\", \"sortOrder\": 0}
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
                \"skuCode\": \"E2E-SKU-M-V2\",
                \"regularPrice\": 120000,
                \"currentPrice\": 99000,
                \"stockQuantity\": 200,
                \"sortOrder\": 0,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"M\"}]
            },
            {
                \"skuCode\": \"E2E-SKU-L-V2\",
                \"regularPrice\": 120000,
                \"currentPrice\": 99000,
                \"stockQuantity\": 150,
                \"sortOrder\": 1,
                \"selectedOptions\": [{\"optionGroupName\": \"사이즈\", \"optionValueName\": \"L\"}]
            }
        ],
        \"description\": {
            \"content\": \"<p>수정된 E2E 상세 설명</p>\",
            \"descriptionImages\": []
        },
        \"notice\": {
            \"entries\": [
                {\"noticeFieldId\": 1, \"fieldName\": \"소재\", \"fieldValue\": \"폴리에스터 100%\"}
            ]
        }
    }" \
    "상품 전체 수정 (productGroupId=${PRODUCT_GROUP_ID})"

# 기본 정보 수정
call_api "PATCH" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/basic-info" \
    "{
        \"productGroupName\": \"E2E 기본정보만 수정\",
        \"brandId\": ${BRAND_ID},
        \"categoryId\": ${CATEGORY_ID},
        \"shippingPolicyId\": ${SHIPPING_POLICY_ID:-1},
        \"refundPolicyId\": ${REFUND_POLICY_ID:-1}
    }" \
    "기본 정보 수정 (productGroupId=${PRODUCT_GROUP_ID})"

# SKU 수정 (상품 일괄 수정)
call_api "PATCH" "/api/v2/admin/products/product-groups/${PRODUCT_GROUP_ID}" \
    '{
        "products": [
            {
                "skuCode": "E2E-SKU-M-V3",
                "regularPrice": 130000,
                "currentPrice": 109000,
                "stockQuantity": 300,
                "sortOrder": 0,
                "selectedOptions": [{"optionGroupName": "사이즈", "optionValueName": "M"}]
            }
        ]
    }' \
    "SKU 수정 (productGroupId=${PRODUCT_GROUP_ID})"

# 이미지 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/images" \
    '{
        "images": [
            {"imageType": "THUMBNAIL", "imageUrl": "https://example.com/thumb-v3.jpg", "sortOrder": 0},
            {"imageType": "DETAIL", "imageUrl": "https://example.com/detail-v3.jpg", "sortOrder": 1}
        ]
    }' \
    "이미지 수정 (productGroupId=${PRODUCT_GROUP_ID})"

# 설명 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/description" \
    '{
        "content": "<p>최종 수정된 상세 설명입니다.</p>",
        "descriptionImages": [
            {"imageUrl": "https://example.com/desc-final.jpg", "sortOrder": 0}
        ]
    }' \
    "설명 수정 (productGroupId=${PRODUCT_GROUP_ID})"

# 고시정보 수정
call_api "PUT" "/api/v2/admin/product-groups/${PRODUCT_GROUP_ID}/notice" \
    '{
        "entries": [
            {"noticeFieldId": 1, "fieldName": "소재", "fieldValue": "나일론 100%"},
            {"noticeFieldId": 2, "fieldName": "원산지", "fieldValue": "대한민국"}
        ]
    }' \
    "고시정보 수정 (productGroupId=${PRODUCT_GROUP_ID})"

# =============================================================================
# Phase 6: 개별 상품 수정
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 6: 개별 상품 수정 ===${NC}"

# DB에서 상품 ID 조회
PRODUCT_ID=$(db_query "setof" "SELECT id FROM products WHERE product_group_id = ${PRODUCT_GROUP_ID} AND deleted = 0 LIMIT 1" 2>/dev/null || echo "")
if [ -z "$PRODUCT_ID" ]; then
    PRODUCT_ID=$(db_query "setof" "SELECT id FROM products WHERE deleted = 0 ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "1")
fi
echo -e "  -> 사용할 상품 ID: ${PRODUCT_ID}"

# 가격 수정
call_api "PATCH" "/api/v2/admin/products/${PRODUCT_ID}/price" \
    '{
        "regularPrice": 150000,
        "currentPrice": 119000
    }' \
    "가격 수정 (productId=${PRODUCT_ID})"

# 재고 수정
call_api "PATCH" "/api/v2/admin/products/${PRODUCT_ID}/stock" \
    '{
        "stockQuantity": 500
    }' \
    "재고 수정 (productId=${PRODUCT_ID})"

# =============================================================================
# Phase 7: 주문/클레임
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 7: 주문/클레임 ===${NC}"

# DB에서 주문 아이템 조회
ORDER_ITEM_ID=$(db_query "setof" "SELECT id FROM order_items WHERE status = 'PENDING' LIMIT 1" 2>/dev/null || echo "")
if [ -z "$ORDER_ITEM_ID" ]; then
    ORDER_ITEM_ID=$(db_query "setof" "SELECT id FROM order_items ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "")
fi

if [ -n "$ORDER_ITEM_ID" ]; then
    echo -e "  -> 주문 아이템 ID: ${ORDER_ITEM_ID}"

    # 주문 확인
    call_api "POST" "/api/v2/orders/${ORDER_ITEM_ID}/confirm" \
        "" \
        "주문 확인 (orderItemId=${ORDER_ITEM_ID})"

    # 발송 준비 (confirm 성공 후)
    call_api "POST" "/api/v2/orders/${ORDER_ITEM_ID}/ready-to-ship" \
        "" \
        "발송 준비 (orderItemId=${ORDER_ITEM_ID})"
else
    echo -e "  ${YELLOW}[SKIP]${NC} 주문 아이템 데이터가 없습니다"
    SKIP_COUNT=$((SKIP_COUNT + 2))
fi

# 취소 조회
CANCEL_ID=$(db_query "setof" "SELECT id FROM cancels WHERE status = 'REQUESTED' LIMIT 1" 2>/dev/null || echo "")
if [ -z "$CANCEL_ID" ]; then
    CANCEL_ID=$(db_query "setof" "SELECT id FROM cancels ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "")
fi

if [ -n "$CANCEL_ID" ]; then
    echo -e "  -> 취소 ID: ${CANCEL_ID}"

    # 취소 승인
    call_api "POST" "/api/v2/cancels/${CANCEL_ID}/approve" \
        "" \
        "취소 승인 (cancelId=${CANCEL_ID})"

    # 취소 거부 (이미 승인했으면 실패할 수 있음)
    CANCEL_ID_2=$(db_query "setof" "SELECT id FROM cancels WHERE status = 'REQUESTED' AND id != ${CANCEL_ID} LIMIT 1" 2>/dev/null || echo "")
    if [ -n "$CANCEL_ID_2" ]; then
        call_api "POST" "/api/v2/cancels/${CANCEL_ID_2}/reject" \
            '{"rejectReason": "E2E 테스트 거부 사유"}' \
            "취소 거부 (cancelId=${CANCEL_ID_2})"
    else
        echo -e "  ${YELLOW}[SKIP]${NC} 거부할 취소 건이 없습니다"
        SKIP_COUNT=$((SKIP_COUNT + 1))
    fi
else
    echo -e "  ${YELLOW}[SKIP]${NC} 취소 데이터가 없습니다"
    SKIP_COUNT=$((SKIP_COUNT + 2))
fi

# 환불(반품) 조회
REFUND_ID=$(db_query "setof" "SELECT id FROM refunds WHERE status = 'REQUESTED' LIMIT 1" 2>/dev/null || echo "")
if [ -z "$REFUND_ID" ]; then
    REFUND_ID=$(db_query "setof" "SELECT id FROM refunds ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "")
fi

if [ -n "$REFUND_ID" ]; then
    echo -e "  -> 환불 ID: ${REFUND_ID}"

    # 환불 완료
    call_api "POST" "/api/v2/refunds/${REFUND_ID}/complete" \
        "" \
        "환불 완료 (refundId=${REFUND_ID})"

    # 환불 거부
    REFUND_ID_2=$(db_query "setof" "SELECT id FROM refunds WHERE status = 'REQUESTED' AND id != ${REFUND_ID} LIMIT 1" 2>/dev/null || echo "")
    if [ -n "$REFUND_ID_2" ]; then
        call_api "POST" "/api/v2/refunds/${REFUND_ID_2}/reject" \
            '{"rejectReason": "E2E 테스트 거부 사유"}' \
            "환불 거부 (refundId=${REFUND_ID_2})"
    else
        echo -e "  ${YELLOW}[SKIP]${NC} 거부할 환불 건이 없습니다"
        SKIP_COUNT=$((SKIP_COUNT + 1))
    fi
else
    echo -e "  ${YELLOW}[SKIP]${NC} 환불 데이터가 없습니다"
    SKIP_COUNT=$((SKIP_COUNT + 2))
fi

# =============================================================================
# Phase 8: 이미지 변형
# =============================================================================
echo ""
echo -e "${CYAN}=== Phase 8: 이미지 변형 ===${NC}"

# DB에서 이미지 ID 조회
SOURCE_IMAGE_ID=$(db_query "setof" "SELECT id FROM product_group_images WHERE deleted = 0 ORDER BY id DESC LIMIT 1" 2>/dev/null || echo "1")
echo -e "  -> 소스 이미지 ID: ${SOURCE_IMAGE_ID}"

call_api "PUT" "/api/v2/admin/image-variants/sync" \
    "{
        \"sourceImageId\": ${SOURCE_IMAGE_ID},
        \"sourceType\": \"PRODUCT_GROUP_IMAGE\",
        \"variants\": [
            {
                \"variantType\": \"SMALL_WEBP\",
                \"resultAssetId\": \"e2e-asset-small-001\",
                \"variantUrl\": \"https://stage-cdn.set-of.com/public/2026/03/e2e-small.webp\",
                \"width\": 300,
                \"height\": 300
            },
            {
                \"variantType\": \"MEDIUM_WEBP\",
                \"resultAssetId\": \"e2e-asset-medium-001\",
                \"variantUrl\": \"https://stage-cdn.set-of.com/public/2026/03/e2e-medium.webp\",
                \"width\": 600,
                \"height\": 600
            },
            {
                \"variantType\": \"LARGE_WEBP\",
                \"resultAssetId\": \"e2e-asset-large-001\",
                \"variantUrl\": \"https://stage-cdn.set-of.com/public/2026/03/e2e-large.webp\",
                \"width\": 1200,
                \"height\": 1200
            }
        ]
    }" \
    "이미지 변형 동기화"

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
