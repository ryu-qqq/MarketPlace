# Integration Test Module

E2E 통합 테스트 모듈입니다.

## 개요

- **테스트 프레임워크**: JUnit 5, RestAssured
- **데이터베이스**: H2 (In-Memory, MySQL 모드)
- **Spring Profile**: `test`
- **테스트 베이스**: `E2ETestBase`

## 테스트 구조

```
integration-test/
├── src/test/java/
│   └── com/ryuqq/marketplace/integration/
│       ├── common/
│       │   └── E2ETestBase.java           # 공통 E2E 테스트 Base 클래스
│       └── selleradmin/
│           ├── SellerAdminQueryE2ETest.java    # Query 엔드포인트 테스트
│           ├── SellerAdminCommandE2ETest.java  # Command 엔드포인트 테스트
│           └── SellerAdminFlowE2ETest.java     # 통합 플로우 테스트
└── src/test/resources/
    └── application-test.yml              # 테스트 설정
```

## 테스트 실행

### 전체 테스트 실행
```bash
./gradlew :integration-test:test
```

### 특정 도메인 테스트 실행
```bash
# SellerAdmin 도메인 테스트만 실행
./gradlew :integration-test:test --tests "*SellerAdmin*E2ETest"
```

### 태그별 테스트 실행
```bash
# P0 (필수) 테스트만 실행
./gradlew :integration-test:test -Ptag=p0

# P1 (중요) 테스트만 실행
./gradlew :integration-test:test -Ptag=p1

# 플로우 테스트만 실행
./gradlew :integration-test:test -Ptag=flow
```

## 테스트 태그

### 우선순위 태그
- `@Tag("p0")`: 필수 테스트 (CI/CD 필수)
- `@Tag("p1")`: 중요 테스트 (주간 빌드)
- `@Tag("p2")`: 선택 테스트 (릴리스 전)

### 도메인 태그
- `@Tag("selleradmin")`: 셀러 관리자 도메인
- `@Tag("seller")`: 셀러 도메인
- `@Tag("sellerapplication")`: 셀러 신청 도메인

### 테스트 타입 태그
- `@Tag("e2e")`: E2E 통합 테스트
- `@Tag("flow")`: 전체 플로우 테스트

## SellerAdmin E2E 테스트

### 1. SellerAdminQueryE2ETest (Query 엔드포인트)

**테스트 대상**:
- GET `/admin/seller-admin-applications` - 목록 조회
- GET `/admin/seller-admin-applications/{id}` - 상세 조회

**주요 시나리오**:
- 데이터 존재 시 정상 조회
- 빈 목록 반환
- sellerIds 필터 조회
- status 필터 조회
- 페이징 동작
- 이름 검색

### 2. SellerAdminCommandE2ETest (Command 엔드포인트)

**테스트 대상**:
- POST `/admin/seller-admin-applications` - 가입 신청
- POST `/admin/seller-admin-applications/{id}/approve` - 승인
- POST `/admin/seller-admin-applications/{id}/reject` - 거절
- POST `/admin/seller-admin-applications/bulk-approve` - 일괄 승인
- POST `/admin/seller-admin-applications/bulk-reject` - 일괄 거절

**주요 시나리오**:
- 유효한 요청으로 신청 성공
- 필수 필드 누락 시 400
- 승인 성공 및 상태 변경
- 거절 성공
- 일괄 승인/거절 (부분 성공 포함)

### 3. SellerAdminFlowE2ETest (통합 플로우)

**테스트 대상**:
- 전체 CRUD 플로우: 신청 → 승인 → 조회 → 비밀번호 변경
- 상태 전이 플로우: PENDING → ACTIVE → REJECTED
- 비밀번호 관리 플로우: 초기화 → 변경
- 일괄 처리 플로우: 여러 신청 → 일괄 승인/거절

## 테스트 Fixtures

### SellerAdminJpaEntityFixtures

```java
// PENDING_APPROVAL 상태
SellerAdminJpaEntityFixtures.pendingApprovalEntity()

// ACTIVE 상태
SellerAdminJpaEntityFixtures.activeEntity()

// REJECTED 상태
SellerAdminJpaEntityFixtures.rejectedEntity()

// 커스텀 Entity
SellerAdminJpaEntityFixtures.customEntity(id, sellerId, authUserId, loginId, ...)
```

### SellerJpaEntityFixtures

```java
// 활성 Seller
SellerJpaEntityFixtures.activeEntity(1L)
```

## 테스트 작성 가이드

### 1. 기본 구조

```java
@Tag("selleradmin")
@Tag("e2e")
@DisplayName("셀러 관리자 XX API E2E 테스트")
class SellerAdminXXE2ETest extends E2ETestBase {

    private static final String BASE_PATH = "/admin/seller-admin-applications";

    @Autowired
    private SellerAdminJpaRepository sellerAdminRepository;

    @BeforeEach
    void setUp() {
        sellerAdminRepository.deleteAll();
        // 사전 데이터 준비
    }

    @Nested
    @DisplayName("GET /xxx - 설명")
    class TestGroup {

        @Test
        @Tag("p0")
        @DisplayName("시나리오 설명")
        void testMethod() {
            // Given
            // When
            // Then
        }
    }
}
```

### 2. RestAssured 사용

```java
// Admin API 요청
RestAssured.given()
    .spec(givenAdmin())
    .queryParam("page", 0)
    .when()
    .get(BASE_PATH)
    .then()
    .statusCode(HttpStatus.OK.value())
    .body("data.content", hasSize(5));

// JSON Body 요청
Map<String, Object> request = Map.of("field", "value");
RestAssured.given()
    .spec(givenJson())
    .body(request)
    .when()
    .post(BASE_PATH);
```

### 3. DB 검증

```java
// Entity 조회 및 검증
SellerAdminJpaEntity saved = sellerAdminRepository.findById(id).orElseThrow();
assertThat(saved.status()).isEqualTo(SellerAdminStatus.ACTIVE);
```

## 주의사항

1. **데이터 격리**: `@BeforeEach`에서 반드시 `deleteAll()` 호출
2. **외부 연동**: 외부 API는 `@MockBean`으로 Mock 처리
3. **H2 호환성**: MySQL 전용 함수는 H2에서 실패할 수 있음
4. **트랜잭션**: 각 테스트는 독립적으로 실행됨
5. **비밀번호 테스트**: 외부 인증 서버 Mock 필요 (`@Disabled` 상태)

## 테스트 결과 확인

### 성공 케이스
```
✅ SellerAdminQueryE2ETest
  ✅ SearchTest
    ✅ SC-Q1-01: 데이터 존재 시 정상 조회
    ✅ SC-Q1-02: 데이터 없을 때 빈 목록 반환
```

### 실패 케이스
```
❌ SellerAdminCommandE2ETest
  ❌ ApplyTest
    ❌ SC-C1-01: 유효한 요청으로 가입 신청 성공
      Expected status code 201 but was 400
```

## 문제 해결

### H2 데이터베이스 초기화 실패
- Flyway 마이그레이션 스크립트 확인
- H2 호환 SQL 문법 확인

### 테스트 간 데이터 격리 실패
- `@BeforeEach`의 `deleteAll()` 확인
- 트랜잭션 롤백 설정 확인

### Mock 객체 동작 안 함
- `@MockBean` 선언 확인
- Mock 설정 (`when()`, `doNothing()`) 확인

## 참고 문서

- [Test Scenario 문서](.claude/docs/test-scenario/selleradmin.md)
- [API Endpoints 문서](.claude/docs/api-endpoints/selleradmin.md)
- [API Flow 문서](.claude/docs/api-flow/selleradmin.md)

## 버전 정보

- **작성일**: 2026-02-06
- **Spring Boot**: 3.5.1
- **JUnit**: 5.10.x
- **RestAssured**: 5.4.0
- **H2**: 2.2.x
