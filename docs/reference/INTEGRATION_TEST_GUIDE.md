# Integration Test Guide

> **목적**: E2E 통합 테스트와 Repository 통합 테스트를 태그로 구분하여 관리

---

## 1. 테스트 분류 체계

### 1.1 테스트 유형

| 유형 | 태그 | 범위 | 속도 | 실행 시점 |
|------|------|------|------|----------|
| **E2E** | `@Tag("e2e")` | 전체 스택 (API→DB) | 느림 (5-30초) | PR 머지, 배포 전 |
| **Repository** | `@Tag("repository")` | DB 레이어만 | 빠름 (1-5초) | 매 커밋, PR |

### 1.2 아키텍처 범위

```
┌─────────────────────────────────────────────────────────────────┐
│  E2E Test (@Tag("e2e"))                                         │
│  ───────────────────────────────────────────────────────────    │
│  REST Client → Controller → UseCase → Domain → Repository → DB  │
│               (adapter-in)  (application) (domain) (adapter-out) │
│                                                                  │
│  @SpringBootTest(webEnvironment = RANDOM_PORT)                  │
│  Bootstrap 애플리케이션 전체 컨텍스트 로드                         │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│  Repository Test (@Tag("repository"))                           │
│  ───────────────────────────────────────────────────────────    │
│  Repository → JPA Entity → DB                                   │
│                                                                  │
│  @DataJpaTest 또는 @SpringBootTest + JPA Slice                  │
│  JPA 관련 컨텍스트만 로드 (가볍고 빠름)                            │
└─────────────────────────────────────────────────────────────────┘
```

---

## 2. 디렉토리 구조

```
integration-test/
└── src/test/java/com/ryuqq/setof/
    │
    ├── common/                            # 공통 테스트 인프라
    │   ├── tag/
    │   │   └── TestTags.java              # 태그 상수 정의
    │   ├── base/
    │   │   ├── E2ETestBase.java           # E2E 테스트 베이스
    │   │   ├── AdminE2ETestBase.java      # Admin E2E 테스트 베이스
    │   │   └── RepositoryTestBase.java    # Repository 테스트 베이스
    │   ├── config/
    │   │   ├── E2ETestConfig.java         # E2E 설정
    │   │   └── RepositoryTestConfig.java  # Repository 설정
    │   └── fixture/
    │       └── CommonTestFixture.java     # 공통 픽스처
    │
    ├── e2e/                               # @Tag("e2e") 테스트
    │   ├── api/                           # web-api (일반 사용자)
    │   │   ├── auth/
    │   │   │   └── AuthE2ETest.java
    │   │   ├── member/
    │   │   │   └── MemberE2ETest.java
    │   │   ├── product/
    │   │   │   └── ProductE2ETest.java
    │   │   └── order/
    │   │       └── OrderE2ETest.java
    │   │
    │   └── admin/                         # web-api-admin (관리자)
    │       ├── seller/
    │       │   └── SellerAdminE2ETest.java
    │       ├── product/
    │       │   └── ProductAdminE2ETest.java
    │       └── policy/
    │           └── PolicyAdminE2ETest.java
    │
    └── repository/                        # @Tag("repository") 테스트
        ├── seller/
        │   └── SellerRepositoryTest.java
        ├── product/
        │   └── ProductRepositoryTest.java
        ├── order/
        │   └── OrderRepositoryTest.java
        └── policy/
            └── PolicyRepositoryTest.java
```

---

## 3. 핵심 컴포넌트

### 3.1 TestTags (태그 상수)

```java
// 위치: common/tag/TestTags.java
public final class TestTags {

    private TestTags() {}

    // 주요 태그
    public static final String E2E = "e2e";
    public static final String REPOSITORY = "repository";

    // 세부 태그
    public static final String API = "api";
    public static final String ADMIN = "admin";
    public static final String SLOW = "slow";
}
```

### 3.2 E2ETestBase (일반 API)

```java
// 위치: common/base/E2ETestBase.java
@Tag(TestTags.E2E)
@Tag(TestTags.API)
@SpringBootTest(
    classes = WebApiApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class E2ETestBase {

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }
}
```

### 3.3 AdminE2ETestBase (관리자 API)

```java
// 위치: common/base/AdminE2ETestBase.java
@Tag(TestTags.E2E)
@Tag(TestTags.ADMIN)
@SpringBootTest(
    classes = WebApiAdminApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AdminE2ETestBase {

    @LocalServerPort
    protected int port;

    @BeforeEach
    void setUpRestAssured() {
        RestAssured.port = port;
        RestAssured.basePath = "/admin/api";
    }
}
```

### 3.4 RepositoryTestBase

```java
// 위치: common/base/RepositoryTestBase.java
@Tag(TestTags.REPOSITORY)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(RepositoryTestConfig.class)
public abstract class RepositoryTestBase {

    @Autowired
    protected TestEntityManager entityManager;

    protected void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    protected <T> T persist(T entity) {
        return entityManager.persist(entity);
    }
}
```

---

## 4. Gradle 태스크

### 4.1 태스크 정의 (build.gradle)

```groovy
// integration-test/build.gradle

// 전체 통합 테스트
tasks.test {
    useJUnitPlatform()
    systemProperty 'spring.profiles.active', 'test'
    maxParallelForks = 1
}

// E2E 테스트만 실행
tasks.register('e2eTest', Test) {
    description = 'Full Stack E2E 테스트 실행'
    group = 'verification'

    useJUnitPlatform {
        includeTags 'e2e'
    }

    systemProperty 'spring.profiles.active', 'test'
    maxParallelForks = 1  // 순차 실행 (포트 충돌 방지)
}

// Repository 테스트만 실행
tasks.register('repositoryTest', Test) {
    description = 'Repository 통합 테스트 실행'
    group = 'verification'

    useJUnitPlatform {
        includeTags 'repository'
    }

    systemProperty 'spring.profiles.active', 'test'
    maxParallelForks = 4  // 병렬 가능
}

// CI용 빠른 테스트 (E2E 제외)
tasks.register('fastTest', Test) {
    description = 'E2E 제외 빠른 테스트'
    group = 'verification'

    useJUnitPlatform {
        excludeTags 'e2e', 'slow'
    }

    systemProperty 'spring.profiles.active', 'test'
    maxParallelForks = 4
}

// Admin E2E만 실행
tasks.register('adminE2ETest', Test) {
    description = 'Admin E2E 테스트만 실행'
    group = 'verification'

    useJUnitPlatform {
        includeTags 'e2e & admin'
    }

    systemProperty 'spring.profiles.active', 'test'
    maxParallelForks = 1
}

// API E2E만 실행
tasks.register('apiE2ETest', Test) {
    description = 'API E2E 테스트만 실행'
    group = 'verification'

    useJUnitPlatform {
        includeTags 'e2e & api'
    }

    systemProperty 'spring.profiles.active', 'test'
    maxParallelForks = 1
}
```

### 4.2 실행 명령어

```bash
# 전체 통합 테스트
./gradlew :integration-test:test

# E2E만 실행 (느림, PR 머지 전)
./gradlew :integration-test:e2eTest

# Repository만 실행 (빠름, 매 커밋)
./gradlew :integration-test:repositoryTest

# E2E 제외 빠른 테스트
./gradlew :integration-test:fastTest

# Admin E2E만
./gradlew :integration-test:adminE2ETest

# API E2E만
./gradlew :integration-test:apiE2ETest
```

---

## 5. 테스트 작성 예시

### 5.1 E2E 테스트 예시

```java
// e2e/admin/seller/SellerAdminE2ETest.java
class SellerAdminE2ETest extends AdminE2ETestBase {

    @Nested
    @DisplayName("판매자 등록 API")
    class RegisterSellerTest {

        @Test
        @DisplayName("정상 등록 성공")
        void shouldRegisterSeller() {
            // given
            var request = Map.of(
                "shopName", "테스트샵",
                "businessNumber", "123-45-67890"
            );

            // when & then
            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/v2/sellers")
            .then()
                .statusCode(201)
                .body("data.shopName", equalTo("테스트샵"));
        }

        @Test
        @DisplayName("필수값 누락시 400 에러")
        void shouldReturn400WhenMissingRequired() {
            // given
            var request = Map.of("shopName", "");

            // when & then
            given()
                .contentType(ContentType.JSON)
                .body(request)
            .when()
                .post("/v2/sellers")
            .then()
                .statusCode(400);
        }
    }

    @Nested
    @DisplayName("판매자 조회 API")
    class GetSellerTest {
        // ...
    }
}
```

### 5.2 Repository 테스트 예시

```java
// repository/seller/SellerRepositoryTest.java
class SellerRepositoryTest extends RepositoryTestBase {

    @Autowired
    private SellerJpaRepository sellerRepository;

    @Nested
    @DisplayName("판매자 저장")
    class SaveTest {

        @Test
        @DisplayName("판매자 엔티티 저장 성공")
        void shouldSaveSeller() {
            // given
            var entity = SellerJpaEntityFixtures.createDefault();

            // when
            var saved = sellerRepository.save(entity);
            flushAndClear();

            // then
            var found = sellerRepository.findById(saved.getId());
            assertThat(found).isPresent();
            assertThat(found.get().getShopName()).isEqualTo(entity.getShopName());
        }
    }

    @Nested
    @DisplayName("판매자 조회")
    class FindTest {

        @Test
        @DisplayName("ID로 조회 성공")
        void shouldFindById() {
            // given
            var entity = persist(SellerJpaEntityFixtures.createDefault());
            flushAndClear();

            // when
            var found = sellerRepository.findById(entity.getId());

            // then
            assertThat(found).isPresent();
        }

        @Test
        @DisplayName("존재하지 않는 ID 조회시 empty")
        void shouldReturnEmptyWhenNotFound() {
            // when
            var found = sellerRepository.findById(999L);

            // then
            assertThat(found).isEmpty();
        }
    }
}
```

---

## 6. CI/CD 통합

### 6.1 GitHub Actions 예시

```yaml
# .github/workflows/test.yml
name: Test

on: [push, pull_request]

jobs:
  fast-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      # 매 커밋: Repository 테스트만 (빠름)
      - name: Repository Tests
        run: ./gradlew :integration-test:repositoryTest

  e2e-test:
    runs-on: ubuntu-latest
    if: github.event_name == 'pull_request'
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      # PR 머지 전: E2E 테스트 (느림)
      - name: E2E Tests
        run: ./gradlew :integration-test:e2eTest
```

---

## 7. 체크리스트

### 7.1 새 E2E 테스트 작성 시

- [ ] 적절한 베이스 클래스 상속 (`E2ETestBase` 또는 `AdminE2ETestBase`)
- [ ] `@Tag(TestTags.E2E)` 자동 적용됨 (베이스에서)
- [ ] `@Nested` 클래스로 시나리오 그룹화
- [ ] `@DisplayName`으로 테스트 의도 명시
- [ ] REST Assured 사용하여 API 호출
- [ ] 테스트 데이터 정리 (cleanup)

### 7.2 새 Repository 테스트 작성 시

- [ ] `RepositoryTestBase` 상속
- [ ] `@Tag(TestTags.REPOSITORY)` 자동 적용됨 (베이스에서)
- [ ] `flushAndClear()` 호출하여 영속성 컨텍스트 초기화
- [ ] TestFixtures 활용하여 엔티티 생성
- [ ] 단일 책임: Repository 메서드 하나씩 테스트

---

## 8. 트러블슈팅

### 8.1 포트 충돌

```
E2E 테스트에서 포트 충돌 발생 시:
- maxParallelForks = 1 확인
- @SpringBootTest(webEnvironment = RANDOM_PORT) 확인
```

### 8.2 컨텍스트 캐싱

```
테스트 간 데이터 오염 발생 시:
- @DirtiesContext 추가 고려
- @Transactional + @Rollback 확인
- cleanup.sql 실행 확인
```

### 8.3 느린 테스트

```
테스트가 너무 느릴 때:
- @Tag("slow") 추가하여 fastTest에서 제외
- Repository 테스트로 분리 가능한지 검토
- TestContainers 대신 H2 사용 고려
```

---

## 9. 관련 파일

| 파일 | 설명 |
|------|------|
| `integration-test/build.gradle` | Gradle 태스크 정의 |
| `integration-test/src/test/resources/application-test.yml` | 테스트 프로필 설정 |
| `integration-test/src/test/resources/sql/cleanup.sql` | 데이터 초기화 SQL |
| `common/tag/TestTags.java` | 태그 상수 정의 |
| `common/base/E2ETestBase.java` | E2E 베이스 클래스 |
| `common/base/AdminE2ETestBase.java` | Admin E2E 베이스 클래스 |
| `common/base/RepositoryTestBase.java` | Repository 베이스 클래스 |

---

## 변경 이력

| 날짜 | 버전 | 내용 |
|------|------|------|
| 2026-01-29 | 1.0 | 초기 문서 작성 |
