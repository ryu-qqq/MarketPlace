# Shadow Traffic 검증 시스템 — Gateway 팀 작업 명세

## 1. 현재 상태

`ShadowMirrorFilter`가 모든 HTTP 메서드(GET, POST, PUT, PATCH)를 SQS에 발행 중.
단, POST-routing 필터(응답 후 실행)로 **메타데이터만** 발행하고, 라우팅은 변경하지 않음.

```
현재: 셀러 → Gateway → 레거시 (정상 라우팅) → ShadowMirrorFilter → SQS 발행
```

## 2. 변경이 필요한 이유

POST/PUT/PATCH 요청의 Shadow 검증을 위해, Gateway가 **레거시로 라우팅하기 전에 Stage MarketPlace에 먼저 전송**해야 합니다.
Stage에서 트랜잭션 롤백 + 스냅샷 저장 후, 정상적으로 레거시에 라우팅합니다.

## 3. 변경 사항

### 3.1 ShadowMirrorFilter 변경 또는 새 필터 추가

POST/PUT/PATCH 요청에 대해 **PRE-routing** 단계에서 Stage를 호출하는 로직이 필요합니다.

**흐름 변경:**

```
GET (변경 없음):
  셀러 → Gateway → 레거시 → ShadowMirrorFilter → SQS 발행

POST/PUT/PATCH (변경):
  셀러 → Gateway
           ├─ [PRE] ShadowPreMirrorFilter
           │    → Stage MarketPlace HTTP 호출 (비동기, fire-and-forget)
           │    → 헤더: X-Shadow-Mode: verify
           │    →       X-Shadow-Correlation-Id: {UUID 생성}
           │    →       X-Shadow-Timestamp: {now}
           │    →       Authorization: {원본 그대로}
           │    → body: 원본 request body 그대로 전달
           │    → 타임아웃: 5초 (실패 시 무시)
           │
           ├─ [ROUTING] 레거시 서버 (정상 처리)
           │
           └─ [POST] ShadowMirrorFilter
                → SQS 발행 (기존 메타데이터 + correlationId 추가)
```

### 3.2 Stage 호출 상세

**Stage 서버 정보:**

| 항목 | 값 |
|------|------|
| 호스트 | (배포 후 공유) |
| 포트 | 8081 |
| 프로토콜 | HTTP (내부망, VPC 내) |
| Health Check | `GET /actuator/health` |

**호출 규칙:**
- 대상: `ShadowMirrorProperties.targets`에 매칭되는 POST/PUT/PATCH 요청 중 **auth 경로 제외**
- auth 경로(`/api/v1/auth/authentication`)는 DB write가 없으므로 Stage 선행 호출 불필요. Lambda가 GET처럼 직접 양쪽 호출하여 비교
- 방식: 비동기 HTTP 호출 (WebClient). 응답은 무시 (fire-and-forget)
- 실패 처리: 타임아웃(5초) 또는 에러 시 로그만 남기고 정상 라우팅 진행
- request body: Spring Cloud Gateway의 `ServerHttpRequest` body를 캐싱하여 Stage에 전달

**추가할 헤더:**

```http
X-Shadow-Mode: verify
X-Shadow-Correlation-Id: {UUID}
X-Shadow-Timestamp: {ISO-8601}
```

`correlationId`는 필터에서 UUID를 생성하고, `ServerWebExchange.getAttributes()`에 저장하여 POST-routing의 ShadowMirrorFilter에서 SQS 메시지에 포함합니다.

### 3.3 ShadowTrafficMessage에 correlationId 추가

**현재** (`ShadowTrafficMessage.java`):
```java
public record ShadowTrafficMessage(
    String traceId,
    Instant timestamp,
    String target,
    String method,
    String host,
    String path,
    String query,
    Map<String, String> headers
)
```

**변경 후:**
```java
public record ShadowTrafficMessage(
    String traceId,
    Instant timestamp,
    String target,
    String method,
    String host,
    String path,
    String query,
    Map<String, String> headers,
    String correlationId    // ← 추가. POST/PUT/PATCH 시 UUID, GET은 null
)
```

### 3.4 ShadowMirrorFilter 변경

기존 ShadowMirrorFilter에서 `correlationId`를 Exchange attribute에서 꺼내 메시지에 포함:

```java
// ShadowMirrorFilter.java (POST-routing)
String correlationId = exchange.getAttribute("shadowCorrelationId"); // PRE 필터에서 저장한 값

ShadowTrafficMessage message = new ShadowTrafficMessage(
    traceId, Instant.now(), target, method, host, path, query, selectedHeaders,
    correlationId  // GET이면 null, POST/PUT/PATCH면 UUID
);
```

### 3.5 새 필터: ShadowPreMirrorFilter

```
위치: PRE-routing (GatewayFilterOrder.PERMISSION_FILTER 이후, 라우팅 전)
조건: gateway.shadow-mirror.enabled=true + POST/PUT/PATCH + target 매칭
```

**의사코드:**
```java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String method = request.getMethod().name();

    // GET이면 스킵 (기존 POST-routing 필터가 SQS 발행)
    if ("GET".equalsIgnoreCase(method) || "HEAD".equalsIgnoreCase(method)) {
        return chain.filter(exchange);
    }

    // auth 경로는 Stage 선행 호출 제외 (DB write 없음, Lambda가 직접 비교)
    String path = request.getURI().getPath();
    if (path.startsWith("/api/v1/auth/")) {
        return chain.filter(exchange);
    }

    String target = findMatchingTarget(request);
    if (target == null) {
        return chain.filter(exchange);
    }

    // correlationId 생성 → Exchange attribute에 저장
    String correlationId = UUID.randomUUID().toString();
    exchange.getAttributes().put("shadowCorrelationId", correlationId);

    // request body 캐싱 + Stage 비동기 호출
    return DataBufferUtils.join(request.getBody())
        .flatMap(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);

            // Stage 비동기 호출 (fire-and-forget)
            callStage(method, request.getURI().getPath(), bytes, request.getHeaders(), correlationId)
                .subscribe(); // 결과 무시

            // body를 다시 감싸서 downstream에 전달
            ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return Flux.just(exchange.getResponse().bufferFactory().wrap(bytes));
                }
            };

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        });
}

private Mono<Void> callStage(String method, String path, byte[] body,
                              HttpHeaders headers, String correlationId) {
    return webClient.method(HttpMethod.valueOf(method))
        .uri(stageBaseUrl + path)
        .headers(h -> {
            h.set("X-Shadow-Mode", "verify");
            h.set("X-Shadow-Correlation-Id", correlationId);
            h.set("X-Shadow-Timestamp", Instant.now().toString());
            // 인증 헤더 포워딩
            if (headers.containsKey("Authorization")) {
                h.set("Authorization", headers.getFirst("Authorization"));
            }
            if (headers.containsKey("API-KEY")) {
                h.set("API-KEY", headers.getFirst("API-KEY"));
            }
            h.setContentType(MediaType.APPLICATION_JSON);
        })
        .bodyValue(body)
        .retrieve()
        .toBodilessEntity()
        .timeout(Duration.ofSeconds(5))
        .onErrorResume(e -> {
            log.warn("[SHADOW-PRE] Stage 호출 실패: path={}, error={}", path, e.getMessage());
            return Mono.empty();
        })
        .then();
}
```

### 3.6 request body 캐싱 주의

Spring Cloud Gateway는 Reactive 기반이라 request body를 한 번만 읽을 수 있습니다.
Stage에 body를 보내면서 동시에 downstream(레거시)에도 body를 전달하려면 **body 캐싱**이 필요합니다.

방법:
- `ServerHttpRequestDecorator`로 body를 캐싱하여 재사용 (위 의사코드 참조)
- 또는 Spring Cloud Gateway의 `ReadBodyRoutePredicateFactory` 활용

---

## 4. 환경별 설정

### application-prod.yml (변경)

```yaml
gateway:
  shadow-mirror:
    enabled: true
    stage-base-url: http://marketplace-legacy-api-stage.connectly.local:8081  # 추가
    stage-timeout-seconds: 5  # 추가
    targets:
      admin:
        hosts:
          - admin.set-of.com
          - admin-server.set-of.net
        paths:
          - /api/v1/auth/authentication
          - /api/v1/seller
          - /api/v1/product/group
          - /api/v1/products/group
          - /api/v1/order
          - /api/v1/orders
          - /api/v1/qnas
          - /api/v1/shipment
          - /api/v1/image/presigned
```

### ShadowMirrorProperties 변경

```java
@ConfigurationProperties(prefix = "gateway.shadow-mirror")
public record ShadowMirrorProperties(
    boolean enabled,
    String stageBaseUrl,                    // 추가
    int stageTimeoutSeconds,                // 추가 (기본값 5)
    Map<String, TargetConfig> targets
) {
    public record TargetConfig(List<String> hosts, List<String> paths) {}
}
```

---

## 5. 경로별 처리 규칙

| 경로 | SQS 발행 | Stage 선행 호출 | 사유 |
|------|---------|----------------|------|
| `/api/v1/legacy/**` (GET) | O | X | Lambda가 직접 양쪽 호출 |
| `/api/v1/legacy/**` (POST/PUT/PATCH) | O (correlationId 포함) | O | 트랜잭션 롤백 + 스냅샷 |
| `/api/v1/auth/authentication` (POST) | O | **X** | DB write 없음. Lambda가 직접 양쪽 호출 |

---

## 6. 변경 파일 요약

| 파일 | 변경 내용 |
|------|----------|
| `ShadowMirrorProperties.java` | `stageBaseUrl`, `stageTimeoutSeconds` 추가 |
| `ShadowTrafficMessage.java` | `correlationId` 필드 추가 |
| `ShadowMirrorFilter.java` | Exchange attribute에서 correlationId 꺼내 메시지에 포함 |
| **신규** `ShadowPreMirrorFilter.java` | POST/PUT/PATCH PRE-routing Stage 호출 |
| `application-prod.yml` | `stage-base-url`, `stage-timeout-seconds` 추가 |
| `GatewayFilterOrder.java` | `SHADOW_PRE_MIRROR_FILTER` 순서 추가 |

---

## 7. 테스트 체크리스트

- [ ] GET 요청: 기존 동작 유지 (SQS 발행, Stage 호출 없음)
- [ ] POST 요청: Stage 호출 후 레거시 라우팅, SQS에 correlationId 포함
- [ ] PUT/PATCH 요청: 동일
- [ ] Stage 타임아웃: 5초 후 무시, 레거시 정상 라우팅
- [ ] Stage 500 에러: 무시, 레거시 정상 라우팅
- [ ] Stage 다운: 무시, 레거시 정상 라우팅
- [ ] request body 정상 전달: Stage와 레거시 모두 동일한 body 수신
- [ ] `/api/v1/auth/authentication` POST: Stage 선행 호출 안 함, SQS 발행만 (correlationId 없음)
- [ ] `/api/v1/auth/authentication` POST: Lambda가 양쪽 직접 호출하여 비교 가능
