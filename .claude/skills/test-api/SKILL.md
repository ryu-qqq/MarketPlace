---
name: test-api
description: rest-api / rest-api-admin 모듈 테스트 자동 생성. testFixtures + ApiMapper 단위 테스트 + RestDocs 문서화 테스트. API 테스트, 컨트롤러 테스트, RestDocs 테스트 요청 시 사용.
context: fork
agent: api-tester
allowed-tools: Read, Write, Edit, Glob, Grep, Bash
---

# /test-api

rest-api / rest-api-admin 모듈의 테스트를 자동 생성한다.
ApiMapper 단위 테스트 + Controller RestDocs 문서화 테스트.

## 사용법

```bash
/test-api rest-api-admin selleradmin
/test-api rest-api-admin v2/seller
/test-api rest-api brand
/test-api rest-api-admin auth --mapper-only
/test-api rest-api-admin category --restdocs-only
/test-api rest-api seller --query-only
/test-api rest-api-admin order --command-only
/test-api rest-api-admin seller --no-run
```

## 입력

- `$ARGUMENTS[0]`: 모듈명 (`rest-api-admin` 또는 `rest-api`)
- `$ARGUMENTS[1]`: 패키지명 (예: selleradmin, brand, seller)
- `$ARGUMENTS[2]`: (선택) `--mapper-only`, `--restdocs-only`, `--query-only`, `--command-only`, `--no-run`

---

## 실행 흐름

### Step 1: 소스 분석

대상 모듈의 Controller, Mapper, Request/Response DTO를 스캔한다.

```
adapter-in/{module}/src/main/java/com/ryuqq/marketplace/adapter/in/{rest-type}/{package}/
├── controller/
│   ├── {Domain}QueryController.java
│   └── {Domain}CommandController.java
├── mapper/
│   ├── {Domain}QueryApiMapper.java
│   └── {Domain}CommandApiMapper.java
└── dto/
    ├── request/
    │   └── {Action}{Domain}ApiRequest.java
    └── response/
        └── {Domain}ApiResponse.java
```

각 Controller에서:
- 엔드포인트 목록 (HTTP 메서드 + 경로)
- 주입된 UseCase 인터페이스
- 요청/응답 DTO 타입 파악

### Step 2: testFixtures 생성

API 레이어 전용 Fixtures를 생성한다.

```java
package com.ryuqq.marketplace.adapter.in.{rest-type}.{package};

public class {Domain}ApiFixtures {

    // Request Fixtures
    public static Register{Domain}ApiRequest registerRequest() {
        return new Register{Domain}ApiRequest(
            "테스트", /* ... 기본 파라미터 */
        );
    }

    public static Update{Domain}ApiRequest updateRequest() {
        return new Update{Domain}ApiRequest(/* ... */);
    }

    // Response Fixtures (Mapper 테스트용)
    public static {Domain}ApiResponse apiResponse() {
        return new {Domain}ApiResponse(1L, "테스트", /* ... */);
    }
}
```

### Step 3: ApiMapper 테스트

Mapper는 DTO ↔ Command/Query 변환만 담당한다. Mock 없이 순수 단위 테스트.

#### Command Mapper 테스트

```java
@Tag("unit")
@DisplayName("{Domain}CommandApiMapper 테스트")
class {Domain}CommandApiMapperTest {

    private {Domain}CommandApiMapper sut;

    @BeforeEach
    void setUp() {
        sut = new {Domain}CommandApiMapper();
    }

    @Nested
    @DisplayName("toRegisterCommand")
    class ToRegisterCommand {
        @Test
        @DisplayName("ApiRequest → RegisterCommand 변환 성공")
        void convertToCommand() {
            // given
            var request = {Domain}ApiFixtures.registerRequest();

            // when
            var command = sut.toRegisterCommand(request);

            // then
            assertThat(command.name()).isEqualTo(request.name());
            // 모든 필드 매핑 검증
        }
    }
}
```

#### Query Mapper 테스트

```java
@Tag("unit")
@DisplayName("{Domain}QueryApiMapper 테스트")
class {Domain}QueryApiMapperTest {

    private {Domain}QueryApiMapper sut;

    @BeforeEach
    void setUp() {
        sut = new {Domain}QueryApiMapper();
    }

    @Nested
    @DisplayName("toApiResponse")
    class ToApiResponse {
        @Test
        @DisplayName("QueryResponse → ApiResponse 변환 성공")
        void convertToApiResponse() {
            // given
            var queryResponse = {Domain}QueryFixtures.queryResponse();

            // when
            var apiResponse = sut.toApiResponse(queryResponse);

            // then
            assertThat(apiResponse.id()).isEqualTo(queryResponse.id());
        }
    }
}
```

### Step 4: RestDocs 컨트롤러 테스트

Spring REST Docs를 활용한 API 문서화 + 테스트.

```java
@Tag("unit")
@WebMvcTest({Domain}CommandController.class)
@AutoConfigureRestDocs
@DisplayName("{Domain}CommandController RestDocs 테스트")
class {Domain}CommandControllerRestDocsTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private {Domain}CommandUseCase {domain}CommandUseCase;
    @MockitoBean private {Domain}CommandApiMapper {domain}CommandApiMapper;

    @Nested
    @DisplayName("POST /admin/{domain}s - 등록")
    class Register{Domain} {

        @Test
        @DisplayName("유효한 요청으로 등록 성공 (201)")
        void registerSuccess() throws Exception {
            // given
            var request = {Domain}ApiFixtures.registerRequest();
            var command = {Domain}CommandFixtures.registerCommand();

            given({domain}CommandApiMapper.toRegisterCommand(any())).willReturn(command);
            given({domain}CommandUseCase.register(command)).willReturn(1L);

            // when & then
            mockMvc.perform(post("/admin/{domain}s")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andDo(document("register-{domain}",
                    requestFields(
                        fieldWithPath("name").description("이름"),
                        // ... 모든 요청 필드
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("응답 메시지"),
                        fieldWithPath("data").description("생성된 ID")
                    )
                ));
        }
    }
}
```

**RestDocs 테스트 핵심 패턴**:
- `@WebMvcTest` + `@AutoConfigureRestDocs`
- UseCase와 ApiMapper를 `@MockitoBean`으로 주입
- `document()` 호출로 REST Docs 스니펫 생성
- `requestFields`, `responseFields`, `pathParameters` 문서화
- 모든 필드에 `.description()` 필수

### Step 5: 테스트 실행

```bash
# rest-api-admin 모듈
./gradlew :adapter-in:rest-api-admin:test --tests "*{패턴}*"

# rest-api 모듈
./gradlew :adapter-in:rest-api:test --tests "*{패턴}*"
```

---

## 생성 파일 구조

```
adapter-in/{module}/src/testFixtures/java/.../{package}/
└── {Domain}ApiFixtures.java

adapter-in/{module}/src/test/java/.../{package}/
├── mapper/
│   ├── {Domain}QueryApiMapperTest.java
│   └── {Domain}CommandApiMapperTest.java
└── controller/
    ├── {Domain}QueryControllerRestDocsTest.java
    └── {Domain}CommandControllerRestDocsTest.java
```

## 코드 품질 체크리스트

- [ ] `@Tag("unit")` 태그 포함
- [ ] Mapper 테스트: 모든 필드 매핑 검증 (누락된 필드 없음)
- [ ] RestDocs 테스트: `@WebMvcTest` + `@AutoConfigureRestDocs`
- [ ] RestDocs: 모든 요청/응답 필드에 `.description()` 포함
- [ ] RestDocs: pathParameters, queryParameters 문서화 (해당 시)
- [ ] UseCase/Mapper → `@MockitoBean`
- [ ] `@Nested` + `@DisplayName` 구조화
- [ ] testFixtures의 ApiFixtures 활용
- [ ] 성공/실패/유효성 검증 실패 시나리오 포함
