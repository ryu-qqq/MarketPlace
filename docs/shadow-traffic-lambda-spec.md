# Shadow Traffic 검증 시스템 — Lambda Comparator 작업 명세

## 1. 현재 상태

| 항목 | 상태 |
|------|------|
| GET 요청 처리 | dry-run (로그만, 실제 HTTP 호출 안 함) |
| POST/PUT/PATCH | 명시적 스킵 (`process_message`에서 return) |
| Redis 연동 | 없음 |
| SqsMessage.body | 없음 |
| SqsMessage.correlationId | 없음 |

## 2. 목표

| 메서드 | 처리 방식 |
|--------|----------|
| GET | Lambda가 레거시 + Stage **직접 호출** → 응답 비교 |
| POST/PUT/PATCH | Redis에서 **스냅샷 조회** → DMS 복제 대기 → Stage **GET API 호출** → 비교 |

## 3. 전체 흐름

### GET (기존 dry-run → 실제 호출로 전환)

```
SQS 메시지 수신 (method=GET)
  ↓
레거시 + Stage 동시 호출 (fetch_pair)
  ↓
deepdiff 비교
  ↓
DynamoDB 저장 + CloudWatch 메트릭
```

### POST/PUT/PATCH (신규)

```
SQS 메시지 수신 (method=POST, correlationId=uuid-123)
  ↓
Redis에서 스냅샷 조회 (key: shadow:snapshot:uuid-123)
  ↓
DMS 복제 대기 (5~10초)
  ↓
Stage GET API 호출 (POST /product/group → GET /product/group/{id})
  ↓
스냅샷 응답 vs GET 응답 비교
  ↓
DynamoDB 저장 + CloudWatch 메트릭
```

---

## 4. 수정 대상 파일

### 4.1 `src/shadow/comparator/worker.py`

#### SqsMessage 모델 변경

```python
# 현재
class SqsMessage(BaseModel):
    traceId: str
    timestamp: str
    target: str
    method: str
    host: str
    path: str
    query: str | None = ""
    headers: dict[str, str] = {}

# 변경 후
class SqsMessage(BaseModel):
    traceId: str
    timestamp: str
    target: str
    method: str
    host: str
    path: str
    query: str | None = ""
    headers: dict[str, str] = {}
    correlationId: str | None = None    # POST/PUT/PATCH 시 UUID, GET은 null
```

> `body` 필드는 불필요. Gateway가 Stage를 직접 호출하므로 Lambda가 body를 알 필요 없음.

#### process_message 변경

```python
async def process_message(self, record: dict[str, Any]) -> None:
    body = json.loads(record["body"])
    message = SqsMessage.model_validate(body)

    structlog.contextvars.bind_contextvars(
        trace_id=message.traceId,
        target=message.target,
        path=message.path,
        method=message.method,
    )

    if message.method.upper() == "GET":
        await self._compare_get(message)
    elif message.method.upper() in ("POST", "PUT", "PATCH"):
        # auth 경로는 DB write가 없으므로 GET과 동일하게 양쪽 직접 호출
        if self._is_auth_path(message.path):
            await self._compare_get(message)
        else:
            await self._compare_write(message)
    else:
        logger.info("지원하지 않는 메서드 스킵", method=message.method)

@staticmethod
def _is_auth_path(path: str) -> bool:
    """인증 경로 여부 판별. DB write 없이 토큰만 발급하므로 GET과 동일하게 처리."""
    return path.startswith("/api/v1/auth/")
```

#### GET 비교 (기존 dry-run → 실제 호출)

```python
async def _compare_get(self, message: SqsMessage) -> None:
    """GET: 레거시 + Stage 직접 호출 → 비교."""
    legacy_url, new_url = self._resolve_urls(message.target)

    async with HttpClient(self._settings) as client:
        legacy_result, shadow_result = await client.fetch_pair(
            legacy_url=legacy_url,
            new_url=new_url,
            method="GET",
            path=message.path,
            legacy_headers=message.headers,
            new_headers=message.headers,
            timeout=self._settings.request_timeout_seconds,
        )

    comparison = compare_for_comparator(
        legacy_status=legacy_result.status_code,
        shadow_status=shadow_result.status_code,
        legacy_body=legacy_result.body,
        shadow_body=shadow_result.body,
        legacy_latency_ms=legacy_result.latency_ms,
        shadow_latency_ms=shadow_result.latency_ms,
    )

    self._save_and_publish(message, comparison, legacy_result, shadow_result)
```

#### POST/PUT/PATCH 비교 (신규)

```python
async def _compare_write(self, message: SqsMessage) -> None:
    """POST/PUT/PATCH: Redis 스냅샷 → DMS 대기 → GET API 비교."""
    correlation_id = message.correlationId
    if not correlation_id:
        logger.warning("correlationId 없음, 스킵", path=message.path)
        return

    # 1. Redis에서 스냅샷 조회
    snapshot = await self._redis.get_snapshot(correlation_id)
    if snapshot is None:
        logger.warning("스냅샷 없음 (TTL 만료?)", correlation_id=correlation_id)
        return

    # 2. DMS 복제 대기
    await asyncio.sleep(self._settings.dms_replication_wait_seconds)

    # 3. GET API 경로 생성
    get_path = self._resolve_get_path(message.path, snapshot)
    if get_path is None:
        logger.warning("GET 경로 매핑 불가", path=message.path)
        return

    # 4. Stage GET API 호출 (DMS 복제 데이터 조회)
    _, new_url = self._resolve_urls(message.target)
    async with HttpClient(self._settings) as client:
        get_result = await client.call_endpoint(
            http_client=client._client,
            base_url=new_url,
            method="GET",
            path=get_path,
            headers=message.headers,
            timeout=self._settings.request_timeout_seconds,
        )

    # 5. 스냅샷 응답 vs GET 응답 비교
    snapshot_body = json.loads(snapshot["responseBody"]) if isinstance(snapshot["responseBody"], str) else snapshot["responseBody"]

    comparison = compare_for_comparator(
        legacy_status=snapshot["statusCode"],
        shadow_status=get_result.status_code,
        legacy_body=snapshot_body,
        shadow_body=get_result.body,
        legacy_latency_ms=0,
        shadow_latency_ms=get_result.latency_ms,
    )

    # 6. 저장 + 메트릭
    domain = self._extract_domain(message.path)
    self._reporter.save_result(
        trace_id=message.traceId,
        target=message.target,
        path=message.path,
        query=message.query or "",
        timestamp=message.timestamp,
        comparison=comparison,
        legacy_body=snapshot_body,
        shadow_body=get_result.body,
    )

    self._metrics.publish_single(
        domain=domain,
        is_diff=comparison.result != ComparisonResultType.MATCH,
        legacy_latency_ms=0,
        shadow_latency_ms=get_result.latency_ms,
    )

    logger.info(
        "쓰기 검증 완료",
        correlation_id=correlation_id,
        result=comparison.result.value,
        get_path=get_path,
    )
```

#### GET 경로 매핑

```python
def _resolve_get_path(self, write_path: str, snapshot: dict) -> str | None:
    """쓰기 경로 → 조회 경로 변환.

    PUT /api/v1/legacy/product/group/5000/images → GET /api/v1/legacy/product/group/5000
    POST /api/v1/legacy/product/group → GET /api/v1/legacy/product/group/{id from snapshot}
    """
    import re

    # PUT/PATCH: 경로에서 productGroupId 추출
    match = re.search(r"/api/v1/legacy/product/group/(\d+)", write_path)
    if match:
        return f"/api/v1/legacy/product/group/{match.group(1)}"

    # POST /product/group: 스냅샷 응답에서 ID 추출
    if write_path == "/api/v1/legacy/product/group":
        try:
            body = json.loads(snapshot["responseBody"]) if isinstance(snapshot["responseBody"], str) else snapshot["responseBody"]
            product_group_id = body.get("data", {}).get("productGroupId")
            if product_group_id:
                return f"/api/v1/legacy/product/group/{product_group_id}"
        except Exception:
            pass

    # PUT /order: 스냅샷 응답에서 orderId 추출
    if write_path == "/api/v1/legacy/order":
        try:
            body = json.loads(snapshot["responseBody"]) if isinstance(snapshot["responseBody"], str) else snapshot["responseBody"]
            order_id = body.get("data", {}).get("orderId")
            if order_id:
                return f"/api/v1/legacy/order/{order_id}"
        except Exception:
            pass

    return None
```

---

### 4.2 Redis 클라이언트 추가

현재 Shadow 프로젝트에 Redis 연동이 없으므로 추가해야 합니다.

#### 새 파일: `src/shadow/shared/redis_client.py`

```python
"""Redis 클라이언트 — Shadow 스냅샷 조회."""
from __future__ import annotations

import json
from typing import Any

import redis.asyncio as aioredis
import structlog

from shadow.config.settings import Settings

logger = structlog.get_logger()

SNAPSHOT_KEY_PREFIX = "shadow:snapshot:"


class ShadowRedisClient:
    """MarketPlace Stage가 저장한 스냅샷을 조회한다."""

    def __init__(self, settings: Settings) -> None:
        self._redis = aioredis.from_url(
            settings.redis_url,
            decode_responses=True,
        )

    async def get_snapshot(self, correlation_id: str) -> dict[str, Any] | None:
        key = f"{SNAPSHOT_KEY_PREFIX}{correlation_id}"
        value = await self._redis.get(key)
        if value is None:
            logger.warning("스냅샷 없음", key=key)
            return None

        try:
            return json.loads(value)
        except json.JSONDecodeError:
            logger.error("스냅샷 파싱 실패", key=key)
            return None

    async def close(self) -> None:
        await self._redis.close()
```

#### 의존성 추가 (`pyproject.toml`)

```toml
[project]
dependencies = [
    # ... 기존 ...
    "redis[hiredis]>=5.0",    # 추가
]
```

---

### 4.3 `src/shadow/config/settings.py` 변경

```python
class Settings(BaseSettings):
    # ... 기존 ...

    # Redis (MarketPlace Stage Redis)
    redis_url: str = "redis://localhost:6379/0"    # 추가

    # DMS 복제 대기 시간 (초)
    dms_replication_wait_seconds: int = 7          # 추가
```

---

### 4.4 Terraform 변경 (Lambda → Redis 접근)

Lambda가 VPC 내에서 Stage Redis(ElastiCache)에 접근해야 합니다.

```hcl
# infra/prod/main.tf

# Lambda 보안 그룹에 Redis 포트 아웃바운드 추가
resource "aws_security_group_rule" "lambda_to_redis" {
  type                     = "egress"
  from_port                = 6379
  to_port                  = 6379
  protocol                 = "tcp"
  source_security_group_id = var.redis_security_group_id
  security_group_id        = aws_security_group.lambda.id
}

# Lambda 환경변수에 Redis URL 추가
environment {
  variables = {
    SHADOW_REDIS_URL = var.redis_url    # 추가
    SHADOW_DMS_REPLICATION_WAIT_SECONDS = "7"
  }
}
```

---

## 5. 스냅샷 Redis 키/값 구조

MarketPlace Stage의 `ShadowSnapshotRedisAdapter`가 저장하는 형식:

```
키: shadow:snapshot:{correlationId}
TTL: 10분

값 (JSON string):
{
  "correlationId": "550e8400-e29b-...",
  "timestamp": "2026-03-20T14:30:00+09:00",
  "httpMethod": "POST",
  "requestPath": "/api/v1/legacy/product/group",
  "statusCode": 200,
  "responseBody": "{\"data\":{\"productGroupId\":5000},\"response\":{\"status\":200,\"message\":\"success\"}}"
}
```

---

## 6. DynamoDB 저장 시 구분

POST/PUT/PATCH 비교 결과도 기존 DynamoDB 테이블(`shadow-traffic-diff`)에 저장합니다.

기존 GET과 구분하기 위해 `method` 속성을 추가하는 게 좋습니다:

```python
item = {
    "PK": {"S": f"{message.target}#{message.path}"},
    "SK": {"S": f"{message.timestamp}#{message.traceId}"},
    "method": {"S": message.method},             # 추가
    "correlationId": {"S": correlation_id or ""},  # 추가
    "result": {"S": comparison.result.value},
    # ... 기존 필드 ...
}
```

---

## 7. 작업 단계

| 단계 | 내용 | 의존성 |
|------|------|--------|
| **Step 1** | `SqsMessage`에 `correlationId` 필드 추가 | Gateway 배포 후 |
| **Step 2** | GET dry-run 해제 → 실제 호출 + 비교 | 없음 (즉시 가능) |
| **Step 3** | `redis_client.py` 추가 + `settings.py` 변경 | Redis 접근 권한 |
| **Step 4** | `process_message`에서 POST/PUT/PATCH 처리 로직 추가 | Step 1, 3 완료 |
| **Step 5** | Terraform — Lambda Redis 접근 보안 그룹 | Step 3과 병행 |
| **Step 6** | DynamoDB에 method/correlationId 속성 추가 | Step 4와 병행 |

---

## 8. 테스트 체크리스트

- [ ] GET: dry-run 해제 후 양쪽 호출 + 비교 정상 동작
- [ ] POST: correlationId로 Redis 스냅샷 조회 성공
- [ ] POST: DMS 대기 후 GET API 호출 → 데이터 조회 성공
- [ ] POST: 스냅샷 응답 vs GET 응답 비교 → MATCH/DIFF 판정
- [ ] PUT/PATCH: URL에서 productGroupId 추출 → GET 경로 매핑
- [ ] Redis 스냅샷 TTL 만료: 경고 로그 + 스킵
- [ ] Stage 다운: GET API 호출 실패 → 에러 로그 + DynamoDB에 ERROR 저장
- [ ] correlationId 없는 POST SQS 메시지: 경고 로그 + 스킵
