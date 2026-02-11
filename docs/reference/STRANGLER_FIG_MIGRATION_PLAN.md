# Strangler Fig 마이그레이션 계획

## 개요

레거시 시스템에서 새 시스템으로 점진적 마이그레이션을 위한 Strangler Fig 패턴 적용 계획.
API 레벨 어댑터를 사용하여 스키마 차이를 해결하면서 점진적으로 전환.

---

## 현재 상황

```
┌─────────────────────────────────────────────────────────────┐
│                      현재 아키텍처                           │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  레거시 서버                    새 서버                      │
│  (bootstrap-legacy-web-api)    (rest-api, rest-api-admin)   │
│         │                              │                    │
│         ▼                              ▼                    │
│    레거시 DB                        새 DB                   │
│   (구 스키마)                    (개선된 스키마)             │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

### 문제점
- 레거시 스키마와 신규 스키마가 다름
- 한 번에 전환 시 리스크 높음
- 롤백 어려움

---

## 목표 아키텍처

```
┌─────────────────────────────────────────────────────────────┐
│                        Gateway                               │
│  ┌─────────────────────────────────────────────────────────┐│
│  │  /api/v2/products/** → 새 서버                          ││
│  │  /api/v2/orders/**   → 새 서버                          ││
│  │  /api/v1/**          → 레거시 (점진적 축소)              ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘
                              │
               ┌──────────────┴──────────────┐
               ▼                             ▼
          새 서버                       레거시 서버
     ┌─────────────────┐           ┌─────────────────┐
     │ Application     │           │                 │
     │ Layer           │           │                 │
     ├─────────────────┤           │                 │
     │ Adapter Layer   │           │                 │
     │ ┌─────┐ ┌─────┐ │           │                 │
     │ │Leg  │ │New  │ │           │                 │
     │ │Repo │ │Repo │ │           │                 │
     │ └──┬──┘ └──┬──┘ │           │                 │
     └────┼───────┼────┘           └────────┬────────┘
          │       │                         │
          ▼       ▼                         ▼
     레거시 DB  새 DB                   레거시 DB
```

---

## 마이그레이션 전략: API 레벨 어댑터

### 핵심 개념

새 서버의 Application Layer는 새 스키마 기준으로 작성하고,
Adapter Layer에서 레거시 DB 접근 시 스키마 변환을 처리.

```java
// Application Layer - 새 스키마 기준
public class ProductService {
    private final ProductRepository productRepository;

    public Product getProduct(Long id) {
        return productRepository.findById(id);
    }
}

// Adapter Layer - 레거시 DB용 구현
@Repository
public class LegacyProductRepositoryAdapter implements ProductRepository {

    private final LegacyProductJpaRepository legacyRepo;

    @Override
    public Product findById(Long id) {
        LegacyProductEntity legacy = legacyRepo.findById(id);
        return convertToNewSchema(legacy);  // 스키마 변환!
    }

    private Product convertToNewSchema(LegacyProductEntity legacy) {
        // 구린 스키마 → 새 스키마 변환 로직
        return Product.builder()
            .id(legacy.getProductId())
            .name(legacy.getProductNm())  // 컬럼명 다름
            .price(Money.of(legacy.getPrc(), legacy.getCurrCd()))  // 분리된 컬럼
            .build();
    }
}
```

---

## 마이그레이션 Phase

### Phase 1: 새 서버 + 레거시 DB (현재 목표)

```
┌─────────────────────────────────────────┐
│              새 서버                     │
│  ┌─────────────────────────────────┐   │
│  │ Application (새 스키마 기준)     │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────┴──────────────────┐   │
│  │ LegacyRepositoryAdapter         │   │
│  │ (레거시 스키마 → 새 스키마 변환) │   │
│  └──────────────┬──────────────────┘   │
└─────────────────┼───────────────────────┘
                  ▼
             레거시 DB
```

**작업 내용**:
- [ ] 새 서버에서 레거시 DB 연결 설정
- [ ] 도메인별 LegacyRepositoryAdapter 구현
- [ ] 스키마 변환 로직 작성
- [ ] Gateway에서 특정 API 라우팅 변경

**장점**:
- 데이터 마이그레이션 불필요
- 롤백 쉬움 (Gateway 라우팅만 변경)
- 점진적 적용 가능

---

### Phase 2: 이중 쓰기 (Dual Write)

```
┌─────────────────────────────────────────┐
│              새 서버                     │
│  ┌─────────────────────────────────┐   │
│  │ Application (새 스키마 기준)     │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────┴──────────────────┐   │
│  │ DualWriteRepositoryAdapter      │   │
│  │ Read: 레거시 DB                  │   │
│  │ Write: 레거시 DB + 새 DB         │   │
│  └───────┬─────────────┬───────────┘   │
└──────────┼─────────────┼────────────────┘
           ▼             ▼
       레거시 DB       새 DB
```

**작업 내용**:
- [ ] 새 DB 스키마 생성
- [ ] DualWriteRepositoryAdapter 구현
- [ ] Write 시 양쪽 DB에 동시 저장
- [ ] Read는 여전히 레거시 DB

---

### Phase 3: 새 DB로 Read 전환

```
┌─────────────────────────────────────────┐
│              새 서버                     │
│  ┌─────────────────────────────────┐   │
│  │ Application (새 스키마 기준)     │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────┴──────────────────┐   │
│  │ NewPrimaryRepositoryAdapter     │   │
│  │ Read: 새 DB (Primary)            │   │
│  │ Write: 새 DB + 레거시 DB (Sync)  │   │
│  └───────┬─────────────┬───────────┘   │
└──────────┼─────────────┼────────────────┘
           ▼             ▼
        새 DB        레거시 DB
       (Primary)     (Secondary)
```

**작업 내용**:
- [ ] 데이터 정합성 검증
- [ ] Read를 새 DB로 전환
- [ ] 레거시 DB는 동기화용으로만 유지

---

### Phase 4: 레거시 DB 제거

```
┌─────────────────────────────────────────┐
│              새 서버                     │
│  ┌─────────────────────────────────┐   │
│  │ Application (새 스키마 기준)     │   │
│  └──────────────┬──────────────────┘   │
│                 │                       │
│  ┌──────────────┴──────────────────┐   │
│  │ NewRepository (최종)             │   │
│  └──────────────┬──────────────────┘   │
└─────────────────┼───────────────────────┘
                  ▼
               새 DB
```

**작업 내용**:
- [ ] 레거시 DB 동기화 제거
- [ ] 레거시 서버 제거
- [ ] 레거시 DB 아카이브/삭제

---

## Gateway 라우팅 전략

### 라우팅 우선순위

```yaml
gateway:
  routing:
    services:
      # 1️⃣ 마이그레이션 완료된 API (구체적 경로 먼저)
      - id: commerce-new
        uri: http://commerce-web-api-prod.connectly.local:8080
        paths:
          - /api/v2/**           # 새 버전 API
          - /api/v1/products/**  # 마이그레이션 완료된 v1 API
        hosts:
          - stage.set-of.com
          - set-of.com

      # 2️⃣ 레거시로 폴백 (catch-all)
      - id: legacy-web
        uri: http://setof-commerce-legacy-api-prod.connectly.local:8080
        paths:
          - /**
        hosts:
          - stage.set-of.com
          - set-of.com
```

### 마이그레이션 진행에 따른 라우팅 변경

| 단계 | 새 서버로 라우팅 | 레거시로 라우팅 |
|------|-----------------|----------------|
| Week 1 | `/api/v1/products/**` | 나머지 전부 |
| Week 2 | + `/api/v1/categories/**` | 나머지 전부 |
| Week 3 | + `/api/v1/brands/**` | 나머지 전부 |
| ... | 점점 추가 | 점점 축소 |
| Final | 전부 | 없음 |

---

## 롤백 전략

### 즉시 롤백 (Gateway 라우팅)

문제 발생 시 Gateway 설정에서 해당 경로를 레거시로 복귀:

```yaml
# 롤백 전
- /api/v1/products/** → 새 서버

# 롤백 후
- /api/v1/products/** → (제거, catch-all로 레거시 사용)
```

### Circuit Breaker (자동 폴백)

```java
@Bean
public RouteLocator routesWithCircuitBreaker(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("products-with-fallback", r -> r
            .path("/api/v1/products/**")
            .filters(f -> f
                .circuitBreaker(config -> config
                    .setName("productsCircuitBreaker")
                    .setFallbackUri("forward:/fallback/legacy")))
            .uri("http://new-server"))
        .build();
}
```

---

## 도메인별 마이그레이션 우선순위 (예시)

| 우선순위 | 도메인 | 이유 | 예상 난이도 |
|---------|--------|------|------------|
| 1 | Products | 조회 위주, 리스크 낮음 | 낮음 |
| 2 | Categories | 단순 구조 | 낮음 |
| 3 | Brands | 단순 구조 | 낮음 |
| 4 | Users | 인증 연동 필요 | 중간 |
| 5 | Orders | 복잡한 상태 관리 | 높음 |
| 6 | Payments | 외부 연동, 리스크 높음 | 높음 |

---

## 체크리스트

### Phase 1 시작 전
- [ ] 새 서버에서 레거시 DB 접속 가능 확인
- [ ] 스키마 차이점 문서화
- [ ] 어댑터 인터페이스 설계
- [ ] 첫 번째 마이그레이션 도메인 선정

### 각 도메인 마이그레이션 시
- [ ] LegacyRepositoryAdapter 구현
- [ ] 단위 테스트 작성
- [ ] 통합 테스트 작성
- [ ] Gateway 라우팅 변경
- [ ] 모니터링 설정
- [ ] 롤백 계획 확인

---

## 모니터링

### 주요 메트릭

- **응답 시간**: 새 서버 vs 레거시 서버 비교
- **에러율**: 새 서버 에러율 모니터링
- **트래픽 비율**: 새 서버로 가는 트래픽 %

### 알림 설정

- 새 서버 에러율 > 1% → 알림
- 응답 시간 > 500ms → 알림
- Circuit Breaker OPEN → 즉시 알림

---

## 프로덕션 운영 가이드

### 대기업 성공 사례

| 회사 | 마이그레이션 내용 | 기간 | 특징 |
|------|-----------------|------|------|
| **Netflix** | Reloaded → Cosmos (미디어 플랫폼) | 7년 | 다운타임 없이 마이크로서비스 전환 |
| **Amazon** | 모놀리식 리테일 → 마이크로서비스 | 수년 | 지속적 기능 배포 유지 |
| **Shopify** | 코어 서비스 마이그레이션 | - | 24/7 업타임 유지하며 전환 |

> **통계**: 스트랭글러 패턴을 적용한 조직은 "빅뱅" 또는 병렬 구현 대비 **67% 적은 프로덕션 장애**를 경험

---

### Canary 배포 전략

#### 트래픽 라우팅 단계 (권장)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  권장 트래픽 증가 패턴 (로그 스케일)                                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Stage 1:  1% ──▶ 새 서비스    (1시간 모니터링)                          │
│           99% ──▶ 레거시                                                 │
│                                                                          │
│  Stage 2:  5% ──▶ 새 서비스    (4시간 모니터링)                          │
│           95% ──▶ 레거시                                                 │
│                                                                          │
│  Stage 3: 25% ──▶ 새 서비스    (24시간 모니터링)                         │
│           75% ──▶ 레거시                                                 │
│                                                                          │
│  Stage 4: 50% ──▶ 새 서비스    (48시간 모니터링)                         │
│           50% ──▶ 레거시                                                 │
│                                                                          │
│  Stage 5: 100% ──▶ 새 서비스   (레거시 라우트 제거)                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### Read-First 전략 (리스크 최소화)

```
┌─────────────────────────────────────────────────────────────────────────┐
│  Phase 1: READ 엔드포인트 먼저 (리스크 낮음)                              │
│           GET /api/v1/products/**                                        │
│           GET /api/v1/categories/**                                      │
├─────────────────────────────────────────────────────────────────────────┤
│  Phase 2: WRITE 엔드포인트 (Read 안정화 후)                              │
│           POST /api/v1/products                                          │
│           PUT /api/v1/products/{id}                                      │
│           DELETE /api/v1/products/{id}                                   │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 모니터링 아키텍처

#### Observability Stack

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    Observability Stack                                   │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐                   │
│  │   Metrics    │  │    Logs      │  │   Traces     │                   │
│  │  (Prometheus)│  │ (CloudWatch) │  │   (X-Ray)    │                   │
│  └──────┬───────┘  └──────┬───────┘  └──────┬───────┘                   │
│         │                 │                 │                            │
│         └─────────────────┼─────────────────┘                            │
│                           ▼                                              │
│                   ┌───────────────┐                                      │
│                   │   Dashboard   │                                      │
│                   │   (Grafana)   │                                      │
│                   └───────────────┘                                      │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

#### 핵심 비교 메트릭 (마이그레이션 전 베이스라인 수집 필수!)

| Metric | Legacy (Baseline) | New Service | Threshold |
|--------|-------------------|-------------|-----------|
| Response Time (p99) | 200ms | ??? | < 250ms |
| Error Rate | 0.1% | ??? | < 1% |
| Throughput | 1000 RPS | ??? | > 800 RPS |
| CPU Usage | 40% | ??? | < 70% |
| Memory Usage | 60% | ??? | < 80% |

#### 알림 설정

```yaml
# 자동 롤백 트리거 조건
alerts:
  critical:
    - name: "High Error Rate"
      condition: error_rate > 1%
      duration: 5m
      action: AUTO_ROLLBACK

    - name: "Latency Spike"
      condition: p99_latency > 500ms
      duration: 5m
      action: AUTO_ROLLBACK

  warning:
    - name: "Elevated Error Rate"
      condition: error_rate > 0.5%
      duration: 10m
      action: ALERT_ONCALL

    - name: "Latency Increase"
      condition: p99_latency > 300ms
      duration: 10m
      action: ALERT_ONCALL
```

---

### 롤백 전략 3단계

#### Level 1: 즉시 롤백 - Gateway 라우팅 (초 단위)

```yaml
# 문제 감지 → Gateway 설정에서 해당 경로 제거 → 자동으로 레거시 폴백

# 롤백: 단순히 경로 제거 또는 주석처리
- id: new-admin-service
  paths:
    - /api/v1/users/**
    # - /api/v1/products/**  ← 이 줄만 주석처리하면 레거시로 폴백

# ⏱️ 소요 시간: 수 초 (설정 배포)
```

#### Level 2: Feature Flag 롤백 (밀리초 단위)

```java
// Feature Flag 서비스 연동 (LaunchDarkly, Unleash, AWS AppConfig 등)
@Component
public class MigrationFeatureFlags {

    private final FeatureFlagClient featureFlags;

    // 런타임에 즉시 on/off 가능 - 재배포 불필요
    public boolean isProductsV2Enabled() {
        return featureFlags.isEnabled("products-v2-migration");
    }
}

// Gateway Filter에서 Feature Flag 체크
@Component
public class FeatureFlagRoutingFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        if (path.startsWith("/api/v1/products") &&
            !featureFlags.isProductsV2Enabled()) {
            // Feature Flag OFF → 레거시로 강제 라우팅
            return routeToLegacy(exchange, chain);
        }

        return chain.filter(exchange);
    }
}
```

#### Level 3: Circuit Breaker 자동 롤백

```java
// Spring Cloud Gateway + Resilience4j
@Bean
public RouteLocator routesWithCircuitBreaker(RouteLocatorBuilder builder) {
    return builder.routes()
        .route("products-with-fallback", r -> r
            .path("/api/v1/products/**")
            .filters(f -> f
                .circuitBreaker(config -> config
                    .setName("productsCircuitBreaker")
                    .setFallbackUri("forward:/fallback/legacy-products")))
            .uri("http://new-service"))
        .build();
}
```

```yaml
# Circuit Breaker 설정
resilience4j:
  circuitbreaker:
    instances:
      productsCircuitBreaker:
        slidingWindowSize: 100
        failureRateThreshold: 50      # 50% 실패 시 OPEN
        waitDurationInOpenState: 60s   # 60초 후 HALF_OPEN
        permittedNumberOfCallsInHalfOpenState: 10
```

---

### Shadow Testing (권장)

프로덕션 트래픽으로 새 서비스를 검증하되 실제 응답은 레거시에서 반환:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Shadow Testing Flow                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  Client Request                                                          │
│       │                                                                  │
│       ▼                                                                  │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                      API Gateway                                  │   │
│  │  1. 레거시에서 실제 응답 반환 ✅                                   │   │
│  │  2. 새 서비스에도 동일 요청 전달 (비동기) 📤                      │   │
│  │  3. 두 응답 비교 → 불일치 시 알림 🔔                             │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  장점:                                                                   │
│  - 실제 프로덕션 트래픽으로 검증                                         │
│  - 사용자에게 영향 없음 (리스크 제로)                                    │
│  - 성능/정확도 비교 가능                                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 프로덕션 아키텍처

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        Production Architecture                           │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────────┐                                                        │
│  │   Client    │                                                        │
│  └──────┬──────┘                                                        │
│         │                                                                │
│         ▼                                                                │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │                    API Gateway                                    │   │
│  │  ┌──────────────────────────────────────────────────────────┐   │   │
│  │  │  Feature Flag Check → Circuit Breaker → Traffic Split    │   │   │
│  │  └──────────────────────────────────────────────────────────┘   │   │
│  └───────────────────────────┬─────────────────────────────────────┘   │
│                              │                                          │
│         ┌────────────────────┼────────────────────┐                    │
│         │                    │                    │                    │
│         ▼ (1%)               ▼ (99%)              ▼                    │
│  ┌─────────────┐      ┌─────────────┐      ┌─────────────┐            │
│  │ New Service │      │   Legacy    │      │   Shadow    │            │
│  │  (Canary)   │      │   Service   │      │   Service   │            │
│  └──────┬──────┘      └──────┬──────┘      └──────┬──────┘            │
│         │                    │                    │                    │
│         ▼                    ▼                    ▼                    │
│  ┌─────────────┐      ┌─────────────┐      (결과 비교만)              │
│  │   New DB    │      │  Legacy DB  │                                  │
│  │  (Phase 2+) │      │             │                                  │
│  └─────────────┘      └─────────────┘                                  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### Gateway 설정 예시 (Canary + Feature Flag)

```yaml
# application-prod.yml
gateway:
  routing:
    services:
      # ===================================
      # 1️⃣ 마이그레이션된 엔드포인트 (Canary)
      # ===================================
      - id: new-admin-service
        uri: http://new-admin-api.connectly.local:8080
        paths:
          - /api/v1/users/**        # 새 서버로 라우팅
          - /api/v1/products/**     # 새 서버로 라우팅
        hosts:
          - admin.set-of.com
        public-paths: []            # JWT 인증 필요 (게이트웨이에서 처리)
        # traffic-weight: 5         # (구현 시) 5% 트래픽만 새 서비스로
        # fallback-service: legacy-admin

      # ===================================
      # 2️⃣ 레거시 catch-all
      # ===================================
      - id: legacy-admin
        uri: http://legacy-admin-alb.internal:8080
        paths:
          - /**                     # 나머지 전부
        hosts:
          - admin.set-of.com
        public-paths:
          - /**                     # 레거시는 자체 인증
```

---

### 운영 체크리스트

#### 마이그레이션 전
- [ ] 레거시 서비스 베이스라인 메트릭 수집 (최소 2주)
- [ ] 모니터링 대시보드 구축 (Grafana/CloudWatch)
- [ ] 알림 설정 (Slack, PagerDuty 연동)
- [ ] 롤백 절차 문서화 및 팀 훈련
- [ ] Shadow Testing 환경 구축 (선택)

#### 각 엔드포인트 마이그레이션 시
- [ ] Shadow Testing 통과 (선택)
- [ ] 1% → 5% → 25% → 50% → 100% 단계적 전환
- [ ] 각 단계별 최소 모니터링 기간 준수
- [ ] 에러율 < 1% 확인
- [ ] Latency < baseline × 1.2 확인
- [ ] 롤백 테스트 완료 (실제 롤백 1회 이상 수행)

#### 마이그레이션 완료 후
- [ ] 레거시 라우트 제거
- [ ] 레거시 서비스 sunset 일정 확정
- [ ] 문서 업데이트
- [ ] 회고 진행

---

## 구체적 실행 계획: Phase 1 (Brand, Category, Seller, CommonCode)

### 개요

Phase 1의 첫 번째 파일럿으로 **조회 위주의 단순한 도메인**부터 마이그레이션합니다.

**대상 도메인:**
- Brand (브랜드)
- Category (카테고리)
- Seller (셀러)
- CommonCode (공통코드)

**전략:**
- 새 서버가 Read/Write 모두 처리
- 저장소만 레거시 DB 사용 (LegacyRepositoryAdapter)
- Gateway에서 도메인 단위로 전체 API 라우팅 전환

---

### 모듈 구조

```
setof-commerce/
├── adapter-out/
│   ├── persistence/              # 새 DB용 (기존)
│   └── persistence-legacy/       # 레거시 DB용 (신규) ⭐
│       ├── build.gradle.kts
│       └── src/main/java/
│           └── com/ryuqq/setof/storage/legacy/
│               ├── config/
│               │   └── LegacyDataSourceConfig.java
│               ├── brand/
│               │   ├── entity/LegacyBrandEntity.java
│               │   ├── repository/LegacyBrandJpaRepository.java
│               │   └── adapter/LegacyBrandQueryAdapter.java
│               ├── category/
│               │   ├── entity/LegacyCategoryEntity.java
│               │   ├── repository/LegacyCategoryJpaRepository.java
│               │   └── adapter/LegacyCategoryQueryAdapter.java
│               ├── seller/
│               │   └── ...
│               └── commoncode/
│                   └── ...
```

---

### 스키마 매핑

#### Brand
| Legacy Column | New Domain |
|---------------|------------|
| `brand_id` | `BrandId` |
| `BRAND_NAME` | `BrandName` |
| `BRAND_ICON_IMAGE_URL` | `BrandIconImageUrl` |
| `DISPLAY_ENGLISH_NAME` / `DISPLAY_KOREAN_NAME` | `DisplayName` (mainDisplayType에 따라 선택) |
| `DISPLAY_ORDER` | `DisplayOrder` |
| `DISPLAY_YN` (Y/N) | `displayed` (boolean) |

#### Category
| Legacy Column | New Domain |
|---------------|------------|
| `category_id` | `CategoryId` |
| `CATEGORY_NAME` | `CategoryName` |
| `CATEGORY_DEPTH` | `CategoryDepth` |
| `PARENT_CATEGORY_ID` | `parentCategoryId` |
| `DISPLAY_NAME` | `CategoryDisplayName` |
| `DISPLAY_YN` (Y/N) | `displayed` (boolean) |
| `TARGET_GROUP` | `TargetGroup` |
| `CATEGORY_TYPE` | `CategoryType` |
| `PATH` | `CategoryPath` |

#### Seller
| Legacy Column | New Domain |
|---------------|------------|
| `seller_id` | `SellerId` |
| `sellerName` | `SellerName` |
| `sellerLogoUrl` | `SellerLogoUrl` |
| `commissionRate` | `CommissionRate` |
| `approvalStatus` | `ApprovalStatus` |

#### CommonCode
| Legacy Column | New Domain |
|---------------|------------|
| `code_id` | `CommonCodeId` |
| `codeGroupId` | `CommonCodeTypeId` |
| `codeDetail` | `Code` |
| `codeDetailDisplayName` | `DisplayName` |
| `displayOrder` | `DisplayOrder` |

---

### 단계별 실행 계획

#### Step 1: persistence-legacy 모듈 구현

**목표:** 레거시 DB 연결 및 Adapter 구현

**작업 내용:**
- [ ] `adapter-out/persistence-legacy` 모듈 생성
- [ ] `build.gradle.kts` 설정 (JPA, QueryDSL)
- [ ] `LegacyDataSourceConfig` 구현 (레거시 DB 연결)
- [ ] Brand: Entity → Repository → QueryAdapter
- [ ] Category: Entity → Repository → QueryAdapter
- [ ] Seller: Entity → Repository → QueryAdapter
- [ ] CommonCode: Entity → Repository → QueryAdapter

**완료 기준:**
- 모든 Adapter 구현 완료
- 컴파일 성공
- 로컬에서 레거시 DB 연결 확인

---

#### Step 2: 통합 테스트 작성 (로컬/CI 검증)

**목표:** Adapter 동작 검증 및 스키마 매핑 정확성 확인

**작업 내용:**
- [ ] TestContainers로 레거시 DB 스키마 로드
- [ ] `BrandQueryAdapter` 통합 테스트
- [ ] `CategoryQueryAdapter` 통합 테스트
- [ ] `SellerQueryAdapter` 통합 테스트
- [ ] `CommonCodeQueryAdapter` 통합 테스트
- [ ] CI 파이프라인에 통합 테스트 추가

**테스트 항목:**
```java
@SpringBootTest
class LegacyBrandQueryAdapterTest {

    @Test
    void findById_레거시DB에서_조회_후_도메인변환_성공() {
        // Given: 레거시 DB에 테스트 데이터 존재

        // When: Adapter로 조회
        Brand brand = adapter.findById(BrandId.of(1L));

        // Then: 도메인 객체로 정상 변환
        assertThat(brand.brandNameValue()).isEqualTo("테스트브랜드");
        assertThat(brand.isDisplayed()).isTrue();
    }

    @Test
    void findByCriteria_검색조건_정상동작() { ... }

    @Test
    void findAllDisplayed_표시여부_필터링_정상() { ... }
}
```

**완료 기준:**
- 모든 통합 테스트 100% 통과
- CI에서 자동 실행 확인

---

#### Step 3: Stage 배포 및 API 비교 테스트

**목표:** 실제 환경에서 API 응답 동일성 검증

**작업 내용:**
- [ ] `bootstrap-web-api`에 `persistence-legacy` 의존성 추가
- [ ] Stage 환경 배포 (레거시 DB 연결 설정)
- [ ] API 비교 테스트 구현 (Lambda + EventBridge)
- [ ] 비교 결과 알림 설정 (CloudWatch + Slack)

**API 비교 아키텍처:**

```
┌─────────────────────────────────────────────────────────────────────┐
│  EventBridge Rule (매 1시간)                                        │
│         │                                                            │
│         ▼                                                            │
│  Lambda Function (api-comparator)                                   │
│         │                                                            │
│         ├──▶ Legacy API: http://legacy.internal/api/v1/brands      │
│         │                                                            │
│         ├──▶ New API: http://new.internal/api/v1/brands            │
│         │                                                            │
│         ├──▶ JSON 비교 (DeepDiff)                                   │
│         │                                                            │
│         └──▶ 결과 리포트                                            │
│               ├── CloudWatch Metrics (일치율)                       │
│               ├── CloudWatch Logs (상세 diff)                       │
│               └── SNS → Slack (불일치 시)                           │
└─────────────────────────────────────────────────────────────────────┘
```

**비교 대상 API:**
| API | Legacy Endpoint | New Endpoint |
|-----|-----------------|--------------|
| 브랜드 목록 | `GET /api/v1/brands` | `GET /api/v1/brands` |
| 브랜드 상세 | `GET /api/v1/brands/{id}` | `GET /api/v1/brands/{id}` |
| 카테고리 목록 | `GET /api/v1/categories` | `GET /api/v1/categories` |
| 카테고리 트리 | `GET /api/v1/categories/tree` | `GET /api/v1/categories/tree` |
| 셀러 목록 | `GET /api/v1/sellers` | `GET /api/v1/sellers` |
| 공통코드 조회 | `GET /api/v1/common-codes` | `GET /api/v1/common-codes` |

**완료 기준:**
- API 비교 일치율 99% 이상
- 24시간 이상 모니터링
- 불일치 항목 원인 분석 및 수정 완료

---

#### Step 4: Gateway 라우팅 전환 (Stage)

**목표:** Stage에서 실제 트래픽을 새 서버로 전환

**작업 내용:**
- [ ] Gateway 라우팅 규칙 추가
- [ ] Brand API 전환 → 48시간 모니터링
- [ ] Category API 전환 → 48시간 모니터링
- [ ] Seller API 전환 → 48시간 모니터링
- [ ] CommonCode API 전환 → 48시간 모니터링

**Gateway 설정 예시:**
```yaml
gateway:
  routing:
    services:
      # 마이그레이션된 API
      - id: new-commerce-api
        uri: http://commerce-web-api-stage.connectly.local:8080
        paths:
          - /api/v1/brands/**
          - /api/v1/categories/**
          - /api/v1/sellers/**
          - /api/v1/common-codes/**
        hosts:
          - stage.set-of.com

      # 레거시 catch-all
      - id: legacy-commerce-api
        uri: http://legacy-api-stage.connectly.local:8080
        paths:
          - /**
        hosts:
          - stage.set-of.com
```

**모니터링 지표:**
| 지표 | 임계값 | 알림 |
|------|--------|------|
| 에러율 | > 0.5% | Slack Warning |
| 에러율 | > 1% | Slack Critical + 자동 롤백 고려 |
| P99 응답시간 | > 레거시 × 1.5 | Slack Warning |
| P99 응답시간 | > 500ms | Slack Critical |

**완료 기준:**
- 에러율 < 0.1%
- 응답시간 ≤ 레거시
- 48시간 안정 운영

---

#### Step 5: Production 점진적 전환

**목표:** 프로덕션 트래픽 점진적 마이그레이션

**트래픽 전환 단계:**
```
Stage 1:  1% ──▶ 새 서버 (1시간 모니터링)
Stage 2:  5% ──▶ 새 서버 (4시간 모니터링)
Stage 3: 25% ──▶ 새 서버 (24시간 모니터링)
Stage 4: 50% ──▶ 새 서버 (48시간 모니터링)
Stage 5: 100% ──▶ 새 서버 (레거시 라우트 제거)
```

**각 단계 체크리스트:**
- [ ] 에러율 < 0.1% 확인
- [ ] 응답시간 정상 범위 확인
- [ ] 비즈니스 메트릭 이상 없음 확인
- [ ] 롤백 테스트 완료

**완료 기준:**
- 100% 트래픽 전환 완료
- 7일간 안정 운영
- 레거시 서버에서 해당 API 제거

---

### 성공 기준 (Gate)

| 단계 | 다음 단계로 넘어가는 기준 |
|------|--------------------------|
| Step 1 완료 | 모든 Adapter 구현 + 컴파일 성공 |
| Step 2 완료 | 통합 테스트 100% 통과 |
| Step 3 완료 | API 비교 일치율 99% 이상 (24시간) |
| Step 4 완료 | Stage 에러율 < 0.1%, 응답시간 ≤ 레거시 (48시간) |
| Step 5 완료 | Prod 에러율 < 0.1%, 7일간 안정 |

---

### 롤백 전략

#### Level 1: Gateway 라우팅 롤백 (즉시, 수 초)

```yaml
# 롤백: 새 서버 라우팅 제거 → 자동으로 레거시 catch-all 사용
- id: new-commerce-api
  paths:
    # - /api/v1/brands/**  ← 주석 처리
```

#### Level 2: ECS 서비스 롤백 (수 분)

```bash
# 이전 Task Definition으로 롤백
aws ecs update-service \
  --cluster commerce-cluster-stage \
  --service commerce-web-api-stage \
  --task-definition commerce-web-api-stage:이전버전
```

#### Level 3: 코드 롤백 (10분+)

```bash
git revert HEAD
git push origin stage
# CI/CD 자동 배포
```

---

### 타임라인

| 주차 | 작업 내용 |
|------|----------|
| Week 1 | persistence-legacy 모듈 구현 |
| Week 2 | 통합 테스트 + Stage 배포 |
| Week 3 | API 비교 테스트 (Lambda + EventBridge) |
| Week 4 | Stage Gateway 전환 + 모니터링 |
| Week 5-6 | Production 점진적 전환 |

---

### 리스크 및 대응

| 리스크 | 영향 | 대응 방안 |
|--------|------|----------|
| 스키마 매핑 오류 | 데이터 불일치 | 통합 테스트로 사전 검증 |
| 레거시 DB 연결 문제 | 서비스 장애 | DataSource 이중화, Circuit Breaker |
| 응답 포맷 불일치 | API 호환성 깨짐 | API 비교 테스트로 사전 검증 |
| 성능 저하 | 사용자 경험 악화 | 캐시 적용, 쿼리 최적화 |
| 롤백 실패 | 장애 장기화 | 롤백 테스트 사전 수행 |

---

## 참고 자료

- [Strangler Fig Pattern - Martin Fowler](https://martinfowler.com/bliki/StranglerFigApplication.html)
- [Branch by Abstraction](https://martinfowler.com/bliki/BranchByAbstraction.html)
- [AWS Prescriptive Guidance - Strangler Fig Pattern](https://docs.aws.amazon.com/prescriptive-guidance/latest/cloud-design-patterns/strangler-fig.html)
- [Shopify Engineering - Refactoring with Strangler Fig](https://shopify.engineering/refactoring-legacy-code-strangler-fig-pattern)
- [CircleCI - Strangler Pattern Implementation](https://circleci.com/blog/strangler-pattern-implementation-for-safe-microservices-transition/)
- [Microsoft Azure - Strangler Fig Pattern](https://learn.microsoft.com/en-us/azure/architecture/patterns/strangler-fig)
- [Harness - Canary Releases and Feature Flags](https://www.harness.io/blog/canary-release-feature-flags)
- [Thoughtworks - Embracing Strangler Fig](https://www.thoughtworks.com/en-us/insights/articles/embracing-strangler-fig-pattern-legacy-modernization-part-one)
