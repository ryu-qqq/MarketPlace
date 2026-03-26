---
name: shadow-verify
description: 로컬 New 서버(Docker) + Stage Legacy 서버(배포됨)로 Shadow Traffic 응답 비교 검증. 배포 전 안전망.
allowed-tools: Bash, Read, Glob
---

# /shadow-verify

로컬에서 **New 서버만 Docker로 실행**하고, **Stage에 배포된 Legacy 서버**에 요청을 보내
두 서버의 응답이 동일한지 Shadow Traffic Runner로 검증합니다.

## 사용법

```bash
/shadow-verify                          # 전체 도메인 검증
/shadow-verify brand,category           # 특정 도메인만
/shadow-verify shipping-address --skip-build  # Docker 빌드 스킵
```

## 입력

- `$ARGUMENTS[0]`: (선택) 도메인 필터 (쉼표 구분). 생략 시 전체
- `$ARGUMENTS[1]`: (선택) `--skip-build` Docker 이미지 빌드 스킵

---

## 환경 정보

| 항목 | 값 |
|------|-----|
| Stage RDS Port | 13308 (SSM 포트포워딩) |
| New Server | localhost:48080 (docker: setof-web-api) |
| Legacy Server | https://stage.set-of.com (Stage 배포) |
| Docker Compose | local-dev/docker-compose.aws.yml |
| Shadow Traffic | tools/shadow-traffic/ |
| Test Cases | tools/shadow-traffic/test-cases/*.yml |

---

## 실행 순서

### Step 1: SSM 포트포워딩 확인

```bash
# Stage RDS 포트포워딩 확인
lsof -i :13308 2>/dev/null
```

포트가 열려있지 않으면:
```bash
cd /Users/sangwon-ryu/setof-commerce
bash local-dev/scripts/aws-port-forward-stage.sh &
sleep 15
# 접속 테스트
mysql -h 127.0.0.1 -P 13308 -u admin -p'REDACTED_DB_PASSWORD' -e "SELECT 1" 2>/dev/null
```

### Step 2: Docker 빌드 + New 서버만 실행

```bash
cd /Users/sangwon-ryu/setof-commerce/local-dev

# .env 파일에 필수 환경변수 확인
# DB_PASSWORD, DB_NAME=luxurydb, JWT_SECRET, KAKAO_CLIENT_ID

# New 서버만 실행 (Legacy는 Stage 배포 서버 사용)
docker-compose -f docker-compose.aws.yml --profile web-api up -d --build
```

`--skip-build` 옵션이 있으면:
```bash
docker-compose -f docker-compose.aws.yml --profile web-api up -d
```

필수 환경변수 (.env 또는 export):
```bash
DB_PASSWORD=REDACTED_DB_PASSWORD
DB_NAME=luxurydb
JWT_SECRET=<AWS Secrets Manager: authhub/stage/jwt/secret>
KAKAO_CLIENT_ID=dummy
KAKAO_CLIENT_SECRET=dummy
```

### Step 3: 서버 헬스체크 대기

```bash
# New Server (48080) 헬스체크
for i in $(seq 1 30); do
  if curl -sf http://localhost:48080/actuator/health > /dev/null 2>&1; then
    echo "New server is ready"
    break
  fi
  echo "Waiting for new server... ($i/30)"
  sleep 5
done

# Stage Legacy Server 헬스체크
curl -sf https://stage.set-of.com/actuator/health > /dev/null 2>&1 && echo "Legacy server is ready" || echo "WARNING: Legacy server not responding"
```

New 서버가 안 뜨면 에러 로그 확인:
```bash
docker logs setof-web-api --tail 50
```

### Step 4: Shadow Traffic 실행

```bash
cd /Users/sangwon-ryu/setof-commerce/tools/shadow-traffic

# 가상환경 확인/생성
if [ ! -d ".venv" ]; then
  python3 -m venv .venv
  .venv/bin/pip install -r requirements.txt
fi

# 인증 정보: tools/shadow-traffic/.env.local에서 자동 로드
# .env.local이 없으면 생성:
# SHADOW_TEST_PHONE=01036817687
# SHADOW_TEST_PASSWORD=Test1234@Ffgqwer

# 도메인 필터 적용
DOMAIN_ARGS=""
if [ -n "$DOMAINS" ]; then
  DOMAIN_ARGS="--domains $DOMAINS"
fi

# Shadow Traffic 실행
# Legacy: Stage 배포 서버 / New: 로컬 Docker
# 인증 정보는 .env.local에서 자동으로 읽힘 (env var 미설정 시)
PYTHONPATH=src .venv/bin/python src/main.py \
  --legacy-url https://stage.set-of.com \
  --new-url http://localhost:48080 \
  --dry-run \
  $DOMAIN_ARGS
```

### Step 5: 결과 분석 및 리포트

실행 결과를 파싱하여 리포트 출력:

```
=== Shadow Verify Report ===

Domain: brand
  PASS  brand_list_all (legacy=45ms, new=32ms)
  PASS  brand_detail_id_1 (legacy=12ms, new=8ms)
  PASS  brand_detail_not_found (legacy=3ms, new=2ms)
  Result: 3/3 passed

Domain: shipping-address
  PASS  shipping_address_list (legacy=23ms, new=15ms)
  FAIL  shipping_address_detail_id_1
        Status: legacy=200 new=500
        Error: NullPointerException at ShippingAddressService:45
  Result: 1/2 passed

=== TOTAL: 4/5 passed, 1 FAILED ===
```

### Step 6: 실패 시 상세 분석

FAIL이 있으면:
1. diff 상세 내역 출력 (어떤 필드가 다른지)
2. New Server 로그에서 에러 추출: `docker logs setof-web-api --tail 100`
3. 원인 분석 및 수정 가이드 제시

### Step 7: (선택) 정리

검증 완료 후 사용자에게 확인:
```
검증이 완료되었습니다. Docker 컨테이너를 종료할까요? [Y/n]
```

종료 시:
```bash
cd /Users/sangwon-ryu/setof-commerce/local-dev
docker-compose -f docker-compose.aws.yml down
```

---

## 전제조건

1. AWS SSM 접근 권한 (포트포워딩용 - New 서버가 Stage DB 접근)
2. Docker Desktop 실행 중
3. `tools/shadow-traffic/test-cases/` 에 테스트 케이스 YAML 존재
4. Python 3.9+ (shadow traffic runner)
5. Stage Legacy 서버가 정상 배포되어 있어야 함

## 주의사항

- New 서버는 Stage DB에 직접 연결하므로 **Command(POST/PUT/DELETE) 테스트는 Stage 데이터를 변경**할 수 있음
- Command 테스트가 포함된 경우 사용자에게 경고 출력
- 인증이 필요한 엔드포인트는 auth_headers 설정 필요 (현재 미지원 -> GET 위주로 먼저 검증)
- Legacy 서버는 Stage 배포 서버를 사용하므로 로컬 Docker 메모리 부담 없음
