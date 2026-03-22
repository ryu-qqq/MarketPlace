---
name: test-integration
description: Testcontainers 기반 E2E 통합 테스트 자동 생성. 엔드포인트 경로 분석 → 시나리오 설계 → 테스트 코드 생성을 한 번에 수행. MySQL, Redis, LocalStack 등 실제 인프라 컨테이너를 띄워 진짜 통합 테스트를 작성한다. /test-integration admin:seller 형태로 사용. 통합 테스트, E2E 테스트, 시나리오 테스트, integration test 관련 요청에 사용.
context: fork
agent: integration-test-generator
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-integration

엔드포인트 경로를 분석하고, 시나리오를 설계하고, Testcontainers 기반 E2E 통합 테스트 코드를 생성하는 올인원 스킬.

기존 `test-scenario` + `test-e2e`를 통합하고, H2 In-Memory DB 대신 Testcontainers MySQL/Redis/LocalStack을 사용하여 프로덕션에 가까운 환경에서 테스트한다.

## 사용법

```bash
/test-integration admin:seller              # admin 모듈 seller 패키지 전체
/test-integration web:product               # web 모듈 product 패키지 전체
/test-integration admin:order --query-only  # Query 테스트만
/test-integration admin:cancel --command-only # Command 테스트만
/test-integration web:product --no-run      # 생성만, 실행 안 함
/test-integration admin:seller --dry-run    # 미리보기 (코드 생성 안 함)
```

## 입력

- `$ARGUMENTS[0]`: `{모듈}:{패키지}` (예: `admin:seller`, `web:product`, `admin:cancel`)
- `$ARGUMENTS[1]`: (선택) `--query-only`, `--command-only`, `--no-run`, `--dry-run`

---

## 실행 흐름

이 스킬은 3단계를 순차적으로 수행한다. `--dry-run` 시 1~2단계만 수행 후 시나리오 문서를 출력한다.

### Phase 1: 엔드포인트 분석

대상 패키지의 Controller를 모두 찾아 엔드포인트를 파악한다.

1. **Controller 스캔**: 대상 모듈의 Controller 클래스를 Glob으로 검색
   - admin → `rest-api-admin/src/main/java/**/controller/{package}/**/*Controller.java`
   - web → `rest-api/src/main/java/**/controller/{package}/**/*Controller.java`

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
- 권한에 따른 데이터 범위 (SUPER_ADMIN vs 셀러)

**Command 시나리오**:
- 정상 생성/수정/삭제
- 유효성 검증 실패 (필수값 누락, 잘못된 형식)
- 상태 전이 검증 (유효한 전이 / 불가능한 전이)
- 권한 검증 (인증 없음, 권한 부족)
- 멱등성 검증 (해당 시)

**Flow 시나리오** (Command 간 연계):
- 생성 → 조회 → 수정 → 조회 → 삭제 플로우
- 상태 전이 전체 플로우 (REQUESTED → APPROVED → COMPLETED)

### Phase 3: 테스트 코드 생성

설계된 시나리오를 기반으로 실제 테스트 코드를 생성한다.

---

## Testcontainers 인프라 베이스 클래스

테스트 코드 생성 전, `IntegrationTestBase`가 존재하는지 확인한다.
없으면 기존 `E2ETestBase`를 확장하여 생성한다.

### IntegrationTestBase 설계 원칙

기존 `E2ETestBase`의 인증 헬퍼(givenSuperAdmin, givenSellerUser 등)는 그대로 유지하면서,
인프라를 H2에서 Testcontainers로 전환한다.

```java
@SpringBootTest(
    classes = MarketPlaceApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(StubExternalClientConfig.class)
@ActiveProfiles("integration")
public abstract class IntegrationTestBase {

    // === Testcontainers ===

    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("marketplace_test")
        .withUsername("test")
        .withPassword("test")
        .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci");

    static final GenericContainer<?> REDIS = new GenericContainer<>("redis:7-alpine")
        .withExposedPorts(6379);

    static final LocalStackContainer LOCALSTACK = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:3"))
        .withServices(LocalStackContainer.Service.S3, LocalStackContainer.Service.SQS);

    static {
        MYSQL.start();
        REDIS.start();
        LOCALSTACK.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // MySQL
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");

        // JPA - MySQL Dialect
        registry.add("spring.jpa.properties.hibernate.dialect",
            () -> "org.hibernate.dialect.MySQLDialect");

        // Redis
        registry.add("spring.data.redis.host", REDIS::getHost);
        registry.add("spring.data.redis.port", () -> REDIS.getMappedPort(6379));

        // LocalStack S3
        registry.add("aws.s3.endpoint",
            () -> LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.S3).toString());
        registry.add("aws.s3.region", LOCALSTACK::getRegion);
        registry.add("aws.s3.access-key", LOCALSTACK::getAccessKey);
        registry.add("aws.s3.secret-key", LOCALSTACK::getSecretKey);
    }

    // === REST Assured 설정 ===

    @LocalServerPort protected int port;
    private static final String BASE_PATH = "/api/v1/market";

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = BASE_PATH;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    // === 인증 컨텍스트 헬퍼 (기존 E2ETestBase와 동일) ===

    protected RequestSpecification givenSuperAdmin() { /* 기존과 동일 */ }
    protected RequestSpecification givenSellerUser(String orgId, String... perms) { /* 기존과 동일 */ }
    protected RequestSpecification givenAuthenticatedUser() { /* 기존과 동일 */ }
    protected RequestSpecification givenWithPermission(String... perms) { /* 기존과 동일 */ }
    protected RequestSpecification givenUnauthenticated() { /* 기존과 동일 */ }

    // === 데이터 정리 유틸 ===

    @Autowired protected JdbcTemplate jdbcTemplate;

    /**
     * 테스트 후 테이블 데이터 정리.
     * truncate 대신 delete 사용 (FK 제약 조건 회피).
     */
    protected void cleanTables(String... tableNames) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        for (String table : tableNames) {
            jdbcTemplate.execute("DELETE FROM " + table);
        }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    // === DB 검증 유틸 ===

    protected long countRows(String tableName) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + tableName, Long.class);
    }

    protected Map<String, Object> findById(String tableName, long id) {
        return jdbcTemplate.queryForMap(
            "SELECT * FROM " + tableName + " WHERE id = ?", id);
    }
}
```

### application-integration.yml 설정

Testcontainers 전환 시 필요한 설정 파일. 기존 application.yml에서 H2 관련 설정을 제거하고, Testcontainers가 동적으로 주입하는 프로퍼티를 사용한다.

```yaml
spring:
  application:
    name: marketplace-integration-test

  profiles:
    active: integration

  config:
    import:
      - optional:classpath:rest-api.yml

  # Testcontainers가 @DynamicPropertySource로 주입
  # datasource, redis 설정은 IntegrationTestBase에서 동적 설정

  jpa:
    open-in-view: false
    hibernate:
      ddl-auto: create-drop
    show-sql: false

  flyway:
    enabled: false

  autoconfigure:
    exclude:
      - com.ryuqq.authhub.sdk.autoconfigure.AuthHubAutoConfiguration
      - com.ryuqq.fileflow.sdk.autoconfigure.FileFlowAutoConfiguration
      # Redis AutoConfiguration은 제거하지 않음 (Testcontainers Redis 사용)

  jackson:
    date-format: yyyy-MM-dd'T'HH:mm:ss
    time-zone: Asia/Seoul
    serialization:
      WRITE_DATES_AS_TIMESTAMPS: false
      FAIL_ON_EMPTY_BEANS: false
    deserialization:
      FAIL_ON_UNKNOWN_PROPERTIES: false
    default-property-inclusion: non_null

authhub:
  base-url: http://localhost:19999
  service-token: test-service-token
  service-code: SVC_MARKETPLACE_TEST
  timeout:
    connect: 1s
    read: 1s

server:
  port: 0
  shutdown: graceful

logging:
  level:
    root: WARN
    com.ryuqq.marketplace: INFO
    org.testcontainers: INFO
    org.hibernate.SQL: DEBUG
```

---

## 테스트 코드 생성 규칙

### 파일 구조

```
integration-test/src/test/java/com/ryuqq/marketplace/integration/
├── IntegrationTestBase.java              # Testcontainers 베이스 (없으면 생성)
├── config/
│   └── StubExternalClientConfig.java     # 기존 유지
└── {domain}/
    ├── {Domain}QueryIntegrationTest.java     # Query 테스트
    ├── {Domain}CommandIntegrationTest.java   # Command 테스트
    └── {Domain}FlowIntegrationTest.java      # Flow 테스트 (선택)
```

### 코드 컨벤션

```java
@Tag("integration")
@Tag("{domain}")
@Tag("{query|command}")
class {Domain}{Query|Command}IntegrationTest extends IntegrationTestBase {

    // 의존성 주입 (데이터 시딩용)
    @Autowired private {Domain}Repository {domain}Repository;

    // 테스트 데이터 정리
    @AfterEach
    void tearDown() {
        cleanTables("{table_name}");
    }

    @Nested
    @DisplayName("{HTTP_METHOD} {path} - {설명}")
    class {TestGroupName} {

        @Test
        @Tag("P0")
        @DisplayName("[TC-{ID}] {시나리오 설명}")
        void {methodName}() {
            // given: 테스트 데이터 시딩 (testFixtures 또는 Repository 직접 사용)

            // when: REST Assured HTTP 요청

            // then: 응답 검증 + DB 상태 검증
        }
    }
}
```

### 테스트 데이터 시딩 전략

1. **testFixtures 우선**: 해당 도메인의 testFixtures가 있으면 활용
   ```java
   var seller = SellerFixtures.createDefault();
   sellerRepository.save(seller);
   ```

2. **Repository 직접 시딩**: testFixtures가 없으면 Repository로 직접 데이터 생성
   ```java
   jdbcTemplate.update(
       "INSERT INTO seller (name, status, ...) VALUES (?, ?, ...)",
       "테스트셀러", "ACTIVE", ...);
   ```

3. **API 기반 시딩**: Command → Query 플로우 테스트 시, 실제 API 호출로 데이터 생성
   ```java
   // Step 1: API로 셀러 생성
   var response = givenSuperAdmin()
       .body(createSellerRequest)
       .post("/admin/sellers")
       .then().statusCode(201)
       .extract();

   // Step 2: 생성된 셀러 조회
   givenSuperAdmin()
       .get("/admin/sellers/{id}", sellerId)
       .then().statusCode(200);
   ```

### 검증 패턴

**응답 검증** (REST Assured):
```java
givenSuperAdmin()
    .get("/admin/sellers")
    .then()
    .statusCode(200)
    .body("data.content.size()", greaterThan(0))
    .body("data.content[0].name", equalTo("테스트셀러"));
```

**DB 상태 검증** (JdbcTemplate):
```java
// DB에 실제로 저장되었는지 확인
var row = findById("seller", sellerId);
assertThat(row.get("name")).isEqualTo("테스트셀러");
assertThat(row.get("status")).isEqualTo("ACTIVE");
```

**Redis 검증** (필요 시):
```java
@Autowired private StringRedisTemplate redisTemplate;

// 캐시 적중 검증
String cached = redisTemplate.opsForValue().get("seller:" + sellerId);
assertThat(cached).isNotNull();
```

---

## 인프라 컨테이너 활성화 판단

모든 테스트에 MySQL/Redis/LocalStack을 다 띄울 필요는 없다.
Phase 1에서 분석한 UseCase 의존성을 기반으로 필요한 컨테이너만 활성화한다.

| 조건 | 활성화 컨테이너 |
|------|---------------|
| JPA Repository 사용 | MySQL (필수) |
| Redis 캐시/세션 사용 | Redis |
| S3 파일 업로드 | LocalStack S3 |
| SQS 메시지 발행 | LocalStack SQS |
| 외부 API 호출 | Stub 유지 (StubExternalClientConfig) |

UseCase가 Redis를 사용하지 않으면 Redis 컨테이너 없이 MySQL만으로 테스트한다.
IntegrationTestBase에서 `@ConditionalOnProperty` 또는 프로파일로 제어한다.

---

## 옵션별 동작

| 옵션 | Phase 1 | Phase 2 | Phase 3 |
|------|---------|---------|---------|
| (기본) | 실행 | 실행 | 실행 + 테스트 실행 |
| `--query-only` | Query만 | Query 시나리오만 | Query 테스트만 |
| `--command-only` | Command만 | Command 시나리오만 | Command 테스트만 |
| `--no-run` | 실행 | 실행 | 코드 생성만 |
| `--dry-run` | 실행 | 시나리오 출력 | 건너뜀 |

## 테스트 실행

```bash
# 특정 도메인 통합 테스트
./gradlew :integration-test:test --tests "*{Domain}*IntegrationTest"

# Query만
./gradlew :integration-test:test --tests "*{Domain}QueryIntegrationTest"

# Command만
./gradlew :integration-test:test --tests "*{Domain}CommandIntegrationTest"

# 전체 통합 테스트
./gradlew :integration-test:test -PincludeTags="integration"
```

## 출력

```
# 테스트 코드
integration-test/src/test/java/.../integration/{domain}/
├── {Domain}QueryIntegrationTest.java
├── {Domain}CommandIntegrationTest.java
└── {Domain}FlowIntegrationTest.java

# 인프라 베이스 (없으면 생성)
integration-test/src/test/java/.../integration/IntegrationTestBase.java

# 설정 파일 (없으면 생성)
integration-test/src/test/resources/application-integration.yml
```

## build.gradle 의존성 확인

테스트 코드 생성 전, integration-test/build.gradle에 Testcontainers 의존성이 있는지 확인한다.
없으면 추가를 제안한다.

```groovy
// 필요한 Testcontainers 의존성
testImplementation platform(libs.testcontainers.bom)
testImplementation libs.testcontainers.junit
testImplementation libs.testcontainers.mysql
testImplementation libs.testcontainers.redis       // Redis 사용 시
testImplementation libs.testcontainers.localstack   // AWS 서비스 사용 시
```

## 기존 E2E 테스트와의 공존

기존 `E2ETestBase` 기반 테스트(85개)는 그대로 유지한다.
새로 생성하는 통합 테스트는 `IntegrationTestBase`를 상속하고 `@Tag("integration")`으로 구분한다.

```bash
# 기존 E2E만 실행
./gradlew :integration-test:test -PincludeTags="e2e"

# 새 통합 테스트만 실행
./gradlew :integration-test:test -PincludeTags="integration"

# 전체
./gradlew :integration-test:test
```
