---
name: test-integration
description: Testcontainers 기반 E2E 통합 테스트 자동 생성. 엔드포인트 경로 분석 → 시나리오 설계 → 테스트 코드 생성을 한 번에 수행. MySQL, Redis 실제 인프라 컨테이너를 띄워 진짜 통합 테스트를 작성한다. /test-integration admin:seller 형태로 사용. 통합 테스트, E2E 테스트, 시나리오 테스트, integration test 관련 요청에 사용.
context: fork
agent: integration-test-generator
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-integration

엔드포인트 경로를 분석하고, 시나리오를 설계하고, Testcontainers 기반 E2E 통합 테스트 코드를 생성하는 올인원 스킬.

## 사용법

```bash
/test-integration admin:seller              # admin 모듈 seller 패키지 전체
/test-integration api:product               # api(web) 모듈 product 패키지 전체
/test-integration admin:order --query-only  # Query 테스트만
/test-integration admin:cancel --command-only # Command 테스트만
/test-integration api:product --no-run      # 생성만, 실행 안 함
/test-integration admin:seller --dry-run    # 미리보기 (코드 생성 안 함)
```

## 입력

- `$ARGUMENTS[0]`: `{모듈}:{패키지}` (예: `admin:seller`, `api:product`, `admin:cancel`)
  - `admin` → rest-api-admin 모듈
  - `api` → rest-api 모듈
- `$ARGUMENTS[1]`: (선택) `--query-only`, `--command-only`, `--no-run`, `--dry-run`

---

## 실행 흐름

이 스킬은 3단계를 순차적으로 수행한다. `--dry-run` 시 1~2단계만 수행 후 시나리오 문서를 출력한다.

### Phase 1: 엔드포인트 분석

대상 패키지의 Controller를 모두 찾아 엔드포인트를 파악한다.

1. **Controller 스캔**: 대상 모듈의 Controller 클래스를 Glob으로 검색
   - admin → `adapter-in/rest-api-admin/src/main/java/**/controller/{package}/**/*Controller.java`
   - api → `adapter-in/rest-api/src/main/java/**/controller/{package}/**/*Controller.java`

2. **엔드포인트 추출**: 각 Controller에서 `@GetMapping`, `@PostMapping`, `@PutMapping`, `@PatchMapping`, `@DeleteMapping` 어노테이션 파싱
   - HTTP 메서드, 경로, 핸들러 메서드명, 파라미터 타입 추출

3. **CQRS 분류**:
   - **Query**: GET 메서드 → 조회 테스트
   - **Command**: POST/PUT/PATCH/DELETE → 명령 테스트

4. **UseCase 추적**: Controller → UseCase(Port) → Service 흐름을 추적하여 비즈니스 로직 파악
   - 어떤 Repository가 관여하는지
   - 어떤 외부 서비스(Redis, S3 등)를 호출하는지
   - 상태 전이가 있는 도메인인지

### Phase 2: 시나리오 설계

분석된 엔드포인트와 비즈니스 로직을 기반으로 테스트 시나리오를 설계한다.

#### 시나리오 우선순위 체계

| 우선순위 | 기준 | 예시 |
|---------|------|------|
| **P0** (필수) | 핵심 비즈니스 플로우, CRUD 정상 동작 | 셀러 등록 성공, 주문 생성 |
| **P1** (중요) | 권한 검증, 유효성 검사, 에러 케이스 | 권한 없는 사용자 접근 거부, 잘못된 입력 |
| **P2** (보완) | 엣지 케이스, 경계값, 동시성 | 빈 목록 조회, 페이지네이션 경계 |

#### 시나리오 유형별 설계 원칙

**Query 시나리오**:
- 데이터 있을 때 / 없을 때 조회
- 페이지네이션 (첫 페이지, 마지막 페이지, 빈 페이지)
- 필터/검색 조건별 조회
- 권한에 따른 데이터 범위

**Command 시나리오**:
- 정상 생성/수정/삭제
- 유효성 검증 실패 (필수값 누락, 잘못된 형식)
- 상태 전이 검증 (유효한 전이 / 불가능한 전이)
- 권한 검증 (인증 없음, 권한 부족)
- 멱등성 검증 (해당 시)

**Flow 시나리오** (Command 간 연계):
- 생성 → 조회 → 수정 → 조회 → 삭제 플로우
- 상태 전이 전체 플로우

### Phase 3: 테스트 코드 생성

설계된 시나리오를 기반으로 실제 테스트 코드를 생성한다.

---

## 핵심 인프라: TestContainersConfig (반드시 사용)

> **중요**: 컨테이너를 직접 생성하지 않는다. 반드시 `TestContainersConfig`를 사용한다.

이 프로젝트는 싱글톤 Testcontainers 인프라를 사용한다.

### 컨테이너 설정 (공유 싱글톤)

**파일**: `integration-test/src/test/java/com/ryuqq/marketplace/integration/common/container/TestContainersConfig.java`

```java
public final class TestContainersConfig {
    public static final MySQLContainer<?> MYSQL = ...;  // mysql:8.0, withReuse(true)
    public static final RedisContainer REDIS = ...;     // redis:7-alpine, withReuse(true)

    // 모든 Testcontainers 기반 테스트는 이 메서드만 호출
    public static void overrideProperties(DynamicPropertyRegistry registry) { ... }
}
```

**이 클래스가 등록하는 속성**:
- `spring.datasource.*` (MySQL Testcontainer)
- `spring.jpa.hibernate.ddl-auto=create`
- `spring.jpa.properties.hibernate.dialect=MySQLDialect`
- `spring.flyway.enabled=false`
- `spring.data.redis.host/port` (Redis Testcontainer)
- `redis.enabled=true`

### 테스트 베이스 클래스

| 베이스 클래스 | 용도 | 인프라 | 태그 |
|-------------|------|--------|------|
| `ContainerE2ETestBase` | Testcontainers E2E | MySQL + Redis 실제 컨테이너 | `@Tag("e2e")`, `@Tag("container")` |
| `E2ETestBase` | 기존 E2E (H2+Mock) | H2 + Redis Mock | `@Tag("e2e")` |

**모듈에 따른 베이스 선택**:
- Testcontainers 테스트 → `ContainerE2ETestBase` 상속
- 모든 Testcontainers 테스트는 `@Tag("container")` 포함

### ContainerE2ETestBase 구조

**파일**: `integration-test/src/test/java/com/ryuqq/marketplace/integration/common/base/ContainerE2ETestBase.java`

```java
@Tag(TestTags.E2E)
@Tag(TestTags.CONTAINER)
@SpringBootTest(
    classes = TestContainersWebApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestSecurityConfig.class, TestContainersExternalMockConfig.class})
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class ContainerE2ETestBase {

    @DynamicPropertySource
    static void configureContainers(DynamicPropertyRegistry registry) {
        TestContainersConfig.overrideProperties(registry);  // 반드시 이것만 호출
    }

    // givenJson(), givenAuthenticated() 등 헬퍼 제공
}
```

### Application 클래스 구분

| 클래스 | Redis 스캔 | 용도 |
|--------|-----------|------|
| `TestWebApplication` | ❌ 제외 | 기존 E2E (H2+Mock) |
| `TestContainersWebApplication` | ✅ 포함 | Container E2E (MySQL+Redis 실제) |

### Mock Config 구분

| 클래스 | Redis Mock | 용도 |
|--------|-----------|------|
| `TestPaymentConfig` (기존) | ✅ Redis, Redisson 전부 Mock | 기존 E2E (H2 환경) |
| `TestContainersExternalMockConfig` | ❌ Redis Mock 없음 | Container E2E (외부 API만 Mock) |

**TestContainersExternalMockConfig가 Mock하는 것** (외부 HTTP API만):
- `SalesChannelProductClient` (외부몰 상품 등록)
- `SalesChannelOrderClient` (외부몰 주문 폴링)
- `ShipmentSyncStrategy` (외부몰 배송 동기화)
- `OutboxPublishClient` 계열 (SQS 발행)
- 기타 외부 API Port

**Mock하지 않는 것** (실제 컨테이너 사용):
- `RedisConnectionFactory`, `RedisTemplate`, `RedissonClient`
- JPA Repository (MySQL)

---

## 테스트 코드 생성 규칙

### 금지 사항 (Zero-Tolerance)

| # | 금지 | 이유 | 올바른 방법 |
|---|------|------|-----------|
| 1 | 테스트 클래스에서 컨테이너 직접 생성 | 컨테이너 중복, 자원 낭비, 포트 충돌 | `TestContainersConfig` 사용 |
| 2 | `@DynamicPropertySource`에서 인프라 속성 직접 등록 | 설정 분산, DDL 전략 불일치 | `TestContainersConfig.overrideProperties()` 호출 |
| 3 | Testcontainers 테스트에서 `TestPaymentConfig` Import | `@Primary` Redis Mock이 실제 Redis를 덮어씀 | `TestContainersExternalMockConfig` 사용 |
| 4 | `ddl-auto=create-drop` 사용 (싱글톤 컨테이너) | 다른 테스트 클래스의 테이블이 drop됨 | `ddl-auto=create` 사용 |
| 5 | 테스트 간 데이터 공유 의존 | 실행 순서에 따라 결과가 달라짐 | `@BeforeEach`에서 데이터 생성+정리 |

### 파일 구조

```
integration-test/src/test/java/.../integration/
├── common/
│   ├── base/
│   │   ├── E2ETestBase.java                     # 기존 H2+Mock (유지)
│   │   └── ContainerE2ETestBase.java            # Testcontainers E2E
│   ├── config/
│   │   ├── TestWebApplication.java              # H2+Mock용
│   │   ├── TestContainersWebApplication.java    # Testcontainers용
│   │   ├── StubExternalClientConfig.java        # H2+Mock용 스텁
│   │   ├── TestContainersExternalMockConfig.java # Testcontainers용 (외부 API만 Mock)
│   │   └── TestSecurityConfig.java              # 공유
│   ├── container/
│   │   └── TestContainersConfig.java            # 싱글톤 컨테이너 (핵심)
│   └── tag/
│       └── TestTags.java                        # 태그 상수
└── e2e/
    └── container/                               # Container E2E 테스트 저장 위치
        ├── ContainerSmokeTest.java              # 인프라 검증
        └── {domain}/
            ├── {Domain}QueryContainerE2ETest.java
            ├── {Domain}CommandContainerE2ETest.java
            └── {Domain}FlowContainerE2ETest.java
```

### 코드 컨벤션

```java
@Tag(TestTags.CONTAINER)
@Tag(TestTags.{DOMAIN})
class {Domain}{Query|Command}ContainerE2ETest extends ContainerE2ETestBase {

    @Autowired private {Domain}JpaRepository {domain}JpaRepository;

    @BeforeEach
    void setUp() {
        // FK 역순으로 테이블 클리어
        {domain}JpaRepository.deleteAll();
    }

    @Nested
    @DisplayName("{HTTP_METHOD} {path} - {설명}")
    class {TestGroupName} {

        @Test
        @DisplayName("[P0] {시나리오 설명}")
        void {methodName}() {
            // given
            // when
            // then
        }
    }
}
```

---

## 데이터 격리

### MySQL

```java
@BeforeEach
void setUp() {
    // FK 역순으로 삭제
    orderItemJpaRepository.deleteAll();
    orderJpaRepository.deleteAll();
}
```

또는 JdbcTemplate:

```java
protected void cleanTables(String... tableNames) {
    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
    for (String table : tableNames) {
        jdbcTemplate.execute("TRUNCATE TABLE " + table);
    }
    jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
}
```

### Redis

```java
@AfterEach
void cleanUp() {
    redisTemplate.execute(connection -> {
        connection.serverCommands().flushDb();
        return null;
    }, true);
}
```

---

## 테스트 실행

```bash
# Container E2E 테스트 전체
./gradlew :integration-test:containerE2ETest

# 특정 도메인
./gradlew :integration-test:containerE2ETest --tests "*{Domain}*ContainerE2ETest"

# 기존 E2E (H2+Mock, 변경 없음)
./gradlew :integration-test:e2eTest

# 전체
./gradlew :integration-test:test
```

## 기존 테스트와의 공존

- 기존 `E2ETestBase` 기반 테스트는 그대로 유지 (H2+Mock)
- 새로 생성하는 Testcontainers 테스트는 `ContainerE2ETestBase`를 상속하고 `@Tag("container")`로 구분
- `containerE2ETest` Gradle 태스크로 Testcontainers 테스트만 선택 실행

## 옵션별 동작

| 옵션 | Phase 1 | Phase 2 | Phase 3 |
|------|---------|---------|---------|
| (기본) | 실행 | 실행 | 실행 + 테스트 실행 |
| `--query-only` | Query만 | Query 시나리오만 | Query 테스트만 |
| `--command-only` | Command만 | Command 시나리오만 | Command 테스트만 |
| `--no-run` | 실행 | 실행 | 코드 생성만 |
| `--dry-run` | 실행 | 시나리오 출력 | 건너뜀 |
