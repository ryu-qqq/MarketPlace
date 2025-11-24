# EXTMALL-001 TDD Plan

**Task**: 외부몰 등록 (Register - Foundation)
**Feature**: Vertical Slice (Domain + Application + Persistence + REST API + Integration)
**브랜치**: feature/EXTMALL-001-register
**예상 소요 시간**: 약 25 사이클 × 15분 = 6-7시간 (실제 개발은 2-3일)

---

## 📋 TDD 사이클 체크리스트

---

## 🔵 Phase 1: Domain Layer (Foundation)

### 1️⃣ MallCode Enum 구현 (Cycle 1)

#### 🔴 Red: 테스트 작성
- [ ] `domain/src/test/java/com/ryuqq/marketplace/domain/externalmall/MallCodeTest.java` 생성
- [ ] `shouldValidateMallCode()` 작성
- [ ] `shouldThrowExceptionForInvalidMallCode()` 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: MallCode Enum 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `domain/src/main/java/com/ryuqq/marketplace/domain/externalmall/MallCode.java` 생성
- [ ] `OCO, SELLIC, LF, BUYMA` Enum 상수 추가
- [ ] `isValid()` 메서드 구현
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: MallCode Enum 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Enum 메서드 최적화
- [ ] Javadoc 추가
- [ ] ArchUnit 테스트 통과 확인
- [ ] 커밋: `struct: MallCode Enum 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `domain/src/test/java/com/ryuqq/marketplace/domain/externalmall/MallCodeFixture.java` 생성
- [ ] `MallCodeFixture.oco()`, `sellic()`, `lf()`, `buyma()` 메서드 작성
- [ ] `MallCodeTest` → Fixture 사용으로 리팩토링
- [ ] 커밋: `test: MallCodeFixture 정리 (Tidy)`

---

### 2️⃣ ExternalMallStatus Enum 구현 (Cycle 2)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallStatusTest.java` 생성
- [ ] `shouldAllowValidTransition()` 작성 (PENDING → ACTIVE)
- [ ] `shouldThrowExceptionForInvalidTransition()` 작성 (PENDING → INACTIVE)
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: ExternalMallStatus Enum 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallStatus.java` 생성
- [ ] `PENDING, ACTIVE, INACTIVE, ERROR` Enum 상수 추가
- [ ] `canTransitionTo(ExternalMallStatus next)` 메서드 구현
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: ExternalMallStatus Enum 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 상태 전환 로직 명확화
- [ ] Javadoc 추가
- [ ] 커밋: `struct: ExternalMallStatus Enum 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ExternalMallStatusFixture.java` 생성
- [ ] `pending()`, `active()`, `inactive()`, `error()` 메서드 작성
- [ ] `ExternalMallStatusTest` → Fixture 사용
- [ ] 커밋: `test: ExternalMallStatusFixture 정리 (Tidy)`

---

### 3️⃣ AuthConfig Value Object 구현 (Cycle 3) ✅

#### 🔴 Red: 테스트 작성
- [x] `AuthConfigTest.java` 생성
- [x] `OcoAuthConfigTest.java` 생성
- [x] `shouldCreateOcoAuthConfig()` 작성
- [x] `shouldThrowExceptionWhenInvalid()` 작성
- [x] 커밋: `test: AuthConfig VO 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [x] `AuthConfig.java` (sealed interface) 생성
- [x] `OcoAuthConfig.java` (Record) 생성
- [x] `SellicAuthConfig.java`, `LfAuthConfig.java`, `BuymaAuthConfig.java` 생성
- [x] `validate()` 메서드 구현
- [x] 테스트 실행 → 통과 확인
- [x] 커밋: `feat: AuthConfig VO 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [x] Sealed interface 패턴 최적화
- [x] 검증 로직 개선 (필드별 메서드 분리)
- [x] VO ArchUnit 테스트 통과 확인
- [x] 커밋: `struct: AuthConfig VO 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [x] `AuthConfigFixture.java` 생성
- [x] `ocoAuthConfig()`, `sellicAuthConfig()` 등 메서드 작성
- [x] `AuthConfigTest` → Fixture 사용
- [x] 커밋: `test: AuthConfigFixture 정리 (Tidy)`

---

### 4️⃣ ExternalMall Aggregate 구현 (Cycle 4)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallTest.java` 생성
- [ ] `shouldCreateExternalMallWithValidData()` 작성
- [ ] `shouldGenerateUUIDAutomatically()` 작성
- [ ] `shouldSetInitialStatusToPending()` 작성
- [ ] 테스트 실행 → 컴파일 에러 확인
- [ ] 커밋: `test: ExternalMall Aggregate 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMall.java` 생성 (Plain Java, Lombok 금지)
- [ ] 생성자 + Getter 작성
- [ ] UUID 자동 생성 로직
- [ ] 초기 status = PENDING
- [ ] 테스트 실행 → 통과 확인
- [ ] 커밋: `feat: ExternalMall Aggregate 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 불변성 보장 (final 필드)
- [ ] Law of Demeter 준수 확인
- [ ] Tell Don't Ask 패턴 적용
- [ ] Aggregate ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMall Aggregate 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ExternalMallFixture.java` 생성 (Object Mother 패턴)
- [ ] `anExternalMall()`, `ocoExternalMall()` 메서드 작성
- [ ] `ExternalMallTest` → Fixture 사용으로 리팩토링
- [ ] 커밋: `test: ExternalMallFixture 정리 (Tidy)`

---

### 5️⃣ ExternalMall 비즈니스 규칙 구현 (Cycle 5)

#### 🔴 Red: 테스트 작성
- [ ] `shouldThrowExceptionWhenMallNameIsBlank()` 작성
- [ ] `shouldThrowExceptionWhenBaseUrlIsInvalid()` 작성
- [ ] `shouldThrowExceptionWhenAuthConfigIsNull()` 작성
- [ ] 커밋: `test: ExternalMall 비즈니스 규칙 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] 생성자에 검증 로직 추가
- [ ] `mallName` 빈 문자열 검증
- [ ] `baseUrl` URL 형식 검증
- [ ] `authConfig` null 검증
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: ExternalMall 비즈니스 규칙 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 검증 로직 메서드 추출
- [ ] 예외 메시지 명확화
- [ ] 커밋: `struct: ExternalMall 비즈니스 규칙 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Fixture에 잘못된 데이터 생성 메서드 추가
- [ ] `invalidMallName()`, `invalidBaseUrl()` 등
- [ ] 커밋: `test: ExternalMallFixture 업데이트 (Tidy)`

---

### 6️⃣ Domain 예외 정의 (Cycle 6)

#### 🔴 Red: 테스트 작성
- [ ] `DuplicateMallCodeExceptionTest.java` 생성
- [ ] `InvalidMallCodeExceptionTest.java` 생성
- [ ] `InvalidAuthConfigExceptionTest.java` 생성
- [ ] 예외 메시지 검증 테스트 작성
- [ ] 커밋: `test: Domain 예외 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `DuplicateMallCodeException.java` 생성
- [ ] `InvalidMallCodeException.java` 생성
- [ ] `InvalidStatusTransitionException.java` 생성
- [ ] `InvalidAuthConfigException.java` 생성
- [ ] `ExternalMallNotFoundException.java` 생성
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: Domain 예외 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 예외 계층 구조 최적화
- [ ] Exception ArchUnit 테스트 통과
- [ ] 커밋: `struct: Domain 예외 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] 예외 테스트용 Fixture 정리
- [ ] 커밋: `test: Domain 예외 Fixture 정리 (Tidy)`

---

## 🟢 Phase 2: Application Layer

### 7️⃣ RegisterExternalMallCommand DTO 구현 (Cycle 7)

#### 🔴 Red: 테스트 작성
- [ ] `application/src/test/java/com/ryuqq/marketplace/application/externalmall/RegisterExternalMallCommandTest.java` 생성
- [ ] `shouldCreateCommandWithValidData()` 작성
- [ ] 커밋: `test: RegisterExternalMallCommand 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `application/src/main/java/com/ryuqq/marketplace/application/externalmall/dto/command/RegisterExternalMallCommand.java` 생성 (Record)
- [ ] `mallCode`, `mallName`, `baseUrl`, `authConfig` 필드
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: RegisterExternalMallCommand 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] DTO ArchUnit 테스트 통과 확인
- [ ] 커밋: `struct: RegisterExternalMallCommand 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `RegisterExternalMallCommandFixture.java` 생성
- [ ] `aRegisterCommand()` 메서드 작성
- [ ] 커밋: `test: RegisterExternalMallCommandFixture 정리 (Tidy)`

---

### 8️⃣ ExternalMallResponse DTO 구현 (Cycle 8)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallResponseTest.java` 생성
- [ ] `shouldNotIncludeAuthConfigAndBaseUrl()` 작성 (보안 검증)
- [ ] 커밋: `test: ExternalMallResponse 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallResponse.java` 생성 (Record)
- [ ] `externalMallId`, `mallCode`, `mallName`, `status`, `createdAt` 필드만 포함
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: ExternalMallResponse 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] DTO ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMallResponse 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ExternalMallResponseFixture.java` 생성
- [ ] 커밋: `test: ExternalMallResponseFixture 정리 (Tidy)`

---

### 9️⃣ Port 인터페이스 정의 (Cycle 9)

#### 🔴 Red: 테스트 작성
- [ ] Port 인터페이스는 테스트 없이 정의 (인터페이스만)
- [ ] 커밋: `test: Port 인터페이스 테스트 스킵 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallCommandPort.java` 생성
  - `save(ExternalMall): ExternalMall`
  - `existsByMallCode(MallCode): boolean`
- [ ] `EncryptionPort.java` 생성
  - `encrypt(AuthConfig): String`
  - `decrypt(String, MallCode): AuthConfig`
- [ ] 커밋: `feat: Port 인터페이스 정의 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Port ArchUnit 테스트 통과 확인
- [ ] 커밋: `struct: Port 인터페이스 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Mock Port Fixture 생성 (테스트용)
- [ ] 커밋: `test: Port Mock Fixture 정리 (Tidy)`

---

### 🔟 RegisterExternalMallUseCase 구현 (Cycle 10)

#### 🔴 Red: 테스트 작성
- [ ] `RegisterExternalMallUseCaseTest.java` 생성
- [ ] Mock Port 준비 (Mockito)
- [ ] `shouldRegisterExternalMallSuccessfully()` 작성
- [ ] `shouldThrowExceptionWhenDuplicateMallCode()` 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: RegisterExternalMallUseCase 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `RegisterExternalMallUseCase.java` 생성
- [ ] `@Transactional` 추가
- [ ] 중복 체크 → Aggregate 생성 → 암호화 → 저장 로직
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: RegisterExternalMallUseCase 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Transaction 경계 검증
- [ ] CQRS 패턴 준수 확인
- [ ] UseCase ArchUnit 테스트 통과
- [ ] 커밋: `struct: RegisterExternalMallUseCase 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] UseCase 테스트용 Fixture 정리
- [ ] 커밋: `test: RegisterExternalMallUseCase 테스트 정리 (Tidy)`

---

## 🟡 Phase 3: Persistence Layer (Foundation)

### 1️⃣1️⃣ Flyway Migration 작성 (Cycle 11)

#### 🔴 Red: 테스트 작성
- [ ] `FlywayMigrationTest.java` 생성
- [ ] `shouldApplyV1Migration()` 작성
- [ ] 테스트 실행 → 실패 확인
- [ ] 커밋: `test: Flyway Migration 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `persistence/src/main/resources/db/migration/V1__create_external_malls_table.sql` 생성
- [ ] `external_malls` 테이블 생성 DDL 작성
- [ ] 유니크 제약 (`external_mall_id`, `mall_code`)
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: Flyway V1 Migration 작성 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] DDL 최적화
- [ ] 인덱스 전략 검토
- [ ] 커밋: `struct: Flyway Migration 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Migration 테스트 정리
- [ ] 커밋: `test: Flyway Migration 테스트 정리 (Tidy)`

---

### 1️⃣2️⃣ ExternalMallJpaEntity 구현 (Cycle 12)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallJpaEntityTest.java` 생성
- [ ] `shouldMapToExternalMall()` 작성
- [ ] 커밋: `test: ExternalMallJpaEntity 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallJpaEntity.java` 생성 (Lombok 금지)
- [ ] Long FK 전략 (관계 어노테이션 금지)
- [ ] BaseAuditEntity 상속
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: ExternalMallJpaEntity 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] JPA Entity ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMallJpaEntity 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `ExternalMallJpaEntityFixture.java` 생성
- [ ] 커밋: `test: ExternalMallJpaEntityFixture 정리 (Tidy)`

---

### 1️⃣3️⃣ EncryptionAdapter 구현 (Cycle 13)

#### 🔴 Red: 테스트 작성
- [ ] `EncryptionAdapterTest.java` 생성 (@SpringBootTest)
- [ ] `shouldEncryptAndDecryptAuthConfig()` 작성
- [ ] `shouldDecryptToCorrectAuthConfigType()` 작성 (사이트별)
- [ ] 커밋: `test: EncryptionAdapter 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `EncryptionAdapter.java` 생성 (EncryptionPort 구현)
- [ ] AES-256-GCM 암호화 구현 (Spring Security Crypto)
- [ ] AuthConfig → JSON → 암호화
- [ ] 복호화 → JSON → 사이트별 AuthConfig 역직렬화
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: EncryptionAdapter 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 암호화 키 환경 변수 관리 (`EXTERNAL_MALL_ENCRYPTION_KEY`)
- [ ] Adapter ArchUnit 테스트 통과
- [ ] 커밋: `struct: EncryptionAdapter 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] 암호화 테스트용 Fixture 정리
- [ ] 커밋: `test: EncryptionAdapter 테스트 정리 (Tidy)`

---

### 1️⃣4️⃣ ExternalMallMapper 구현 (Cycle 14)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallMapperTest.java` 생성
- [ ] `shouldMapToEntity()` 작성
- [ ] `shouldMapToDomain()` 작성
- [ ] 커밋: `test: ExternalMallMapper 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallMapper.java` 생성
- [ ] `toEntity(ExternalMall): ExternalMallJpaEntity`
- [ ] `toDomain(ExternalMallJpaEntity): ExternalMall`
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: ExternalMallMapper 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Mapper ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMallMapper 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Mapper 테스트용 Fixture 정리
- [ ] 커밋: `test: ExternalMallMapper 테스트 정리 (Tidy)`

---

### 1️⃣5️⃣ ExternalMallJpaRepository 구현 (Cycle 15)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallJpaRepositoryTest.java` 생성 (@DataJpaTest)
- [ ] `shouldFindByExternalMallId()` 작성
- [ ] `shouldExistsByMallCode()` 작성
- [ ] 커밋: `test: ExternalMallJpaRepository 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallJpaRepository.java` 생성 (extends JpaRepository)
- [ ] `findByExternalMallId()` 메서드 정의
- [ ] `findByMallCode()` 메서드 정의
- [ ] `existsByMallCode()` 메서드 정의
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: ExternalMallJpaRepository 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Repository ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMallJpaRepository 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Repository 테스트용 Fixture 정리
- [ ] 커밋: `test: ExternalMallJpaRepository 테스트 정리 (Tidy)`

---

### 1️⃣6️⃣ ExternalMallCommandAdapter 구현 (Cycle 16)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallCommandAdapterTest.java` 생성 (@DataJpaTest)
- [ ] `shouldSaveExternalMall()` 작성
- [ ] `shouldCheckExistsByMallCode()` 작성
- [ ] 커밋: `test: ExternalMallCommandAdapter 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallCommandAdapter.java` 생성 (ExternalMallCommandPort 구현)
- [ ] `save()` 메서드: Domain → JPA Entity → 저장 → Domain
- [ ] `existsByMallCode()` 메서드
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: ExternalMallCommandAdapter 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] Adapter ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMallCommandAdapter 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Adapter 테스트용 Fixture 정리
- [ ] 커밋: `test: ExternalMallCommandAdapter 테스트 정리 (Tidy)`

---

## 🟠 Phase 4: REST API Layer

### 1️⃣7️⃣ RegisterExternalMallRequest DTO 구현 (Cycle 17)

#### 🔴 Red: 테스트 작성
- [ ] `adapter-in/rest-api/src/test/java/com/ryuqq/marketplace/adapter/in/rest/externalmall/RegisterExternalMallRequestTest.java` 생성
- [ ] `shouldValidateRequest()` 작성
- [ ] 커밋: `test: RegisterExternalMallRequest 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `RegisterExternalMallRequest.java` 생성 (Record)
- [ ] `@NotNull`, `@NotBlank`, `@Size`, `@URL` 어노테이션 추가
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: RegisterExternalMallRequest 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] DTO ArchUnit 테스트 통과
- [ ] 커밋: `struct: RegisterExternalMallRequest 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] `RegisterExternalMallRequestFixture.java` 생성
- [ ] 커밋: `test: RegisterExternalMallRequestFixture 정리 (Tidy)`

---

### 1️⃣8️⃣ ExternalMallController 구현 (Cycle 18)

#### 🔴 Red: 테스트 작성
- [ ] `ExternalMallControllerTest.java` 생성
- [ ] MockMvc 금지, TestRestTemplate 사용
- [ ] `shouldRegisterExternalMallSuccessfully()` 작성
- [ ] 커밋: `test: POST /api/v1/external-malls 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `ExternalMallController.java` 생성
- [ ] POST /api/v1/external-malls 엔드포인트 구현
- [ ] Request DTO → Command 변환
- [ ] UseCase 호출 → Response 반환 (201 Created)
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: POST /api/v1/external-malls 구현 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] RESTful 설계 검증
- [ ] Controller ArchUnit 테스트 통과
- [ ] 커밋: `struct: ExternalMallController 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Controller 테스트용 Fixture 정리
- [ ] RestDocs 문서화 추가
- [ ] 커밋: `test: ExternalMallController 테스트 정리 (Tidy)`

---

### 1️⃣9️⃣ GlobalExceptionHandler 확장 (Cycle 19)

#### 🔴 Red: 테스트 작성
- [ ] `GlobalExceptionHandlerTest.java` 확장
- [ ] `shouldHandleDuplicateMallCodeException()` 작성
- [ ] `shouldHandleInvalidAuthConfigException()` 작성
- [ ] 커밋: `test: GlobalExceptionHandler 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `GlobalExceptionHandler.java` 확장
- [ ] `DuplicateMallCodeException` → 409 Conflict
- [ ] `InvalidAuthConfigException` → 400 Bad Request
- [ ] ApiResponse 패턴 적용
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: GlobalExceptionHandler 확장 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 에러 응답 구조 최적화
- [ ] 커밋: `struct: GlobalExceptionHandler 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] 예외 처리 테스트용 Fixture 정리
- [ ] 커밋: `test: GlobalExceptionHandler 테스트 정리 (Tidy)`

---

## 🔴 Phase 5: Integration Test (E2E)

### 2️⃣0️⃣ TestContainers MySQL 설정 (Cycle 20)

#### 🔴 Red: 테스트 작성
- [ ] `application/src/test/java/com/ryuqq/marketplace/ExternalMallIntegrationTest.java` 생성
- [ ] @SpringBootTest + TestRestTemplate
- [ ] TestContainers MySQL 설정
- [ ] 커밋: `test: TestContainers 설정 (Red)`

#### 🟢 Green: 최소 구현
- [ ] `testcontainers` 의존성 추가 (build.gradle)
- [ ] `@Container` 설정
- [ ] MySQL 컨테이너 구성
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: TestContainers MySQL 설정 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 컨테이너 성능 최적화
- [ ] 커밋: `struct: TestContainers 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Integration Test Fixture 정리
- [ ] 커밋: `test: Integration Test Fixture 정리 (Tidy)`

---

### 2️⃣1️⃣ 시나리오 1: 정상 등록 E2E (Cycle 21)

#### 🔴 Red: 테스트 작성
- [ ] `shouldRegisterExternalMallSuccessfully()` 작성
- [ ] POST → 201 Created 확인
- [ ] DB 조회 → auth_config 암호화 확인
- [ ] 커밋: `test: 정상 등록 E2E 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] 전체 스택 통합 확인
- [ ] Flyway Migration 자동 실행 확인
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: 정상 등록 E2E 통과 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 테스트 격리 확인
- [ ] 커밋: `struct: 정상 등록 E2E 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] E2E Fixture 정리
- [ ] 커밋: `test: 정상 등록 E2E Fixture 정리 (Tidy)`

---

### 2️⃣2️⃣ 시나리오 2: 중복 외부몰 코드 E2E (Cycle 22)

#### 🔴 Red: 테스트 작성
- [ ] `shouldThrowExceptionWhenDuplicateMallCode()` 작성
- [ ] POST (OCO) → POST (OCO 중복) → 409 Conflict
- [ ] 커밋: `test: 중복 외부몰 코드 E2E 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] 중복 체크 로직 통합 확인
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: 중복 외부몰 코드 E2E 통과 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 테스트 정리
- [ ] 커밋: `struct: 중복 외부몰 코드 E2E 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] E2E Fixture 업데이트
- [ ] 커밋: `test: 중복 외부몰 코드 E2E Fixture 정리 (Tidy)`

---

### 2️⃣3️⃣ 시나리오 3: Validation 검증 E2E (Cycle 23)

#### 🔴 Red: 테스트 작성
- [ ] `shouldReturnBadRequestWhenMallNameIsBlank()` 작성
- [ ] `shouldReturnBadRequestWhenBaseUrlIsInvalid()` 작성
- [ ] `shouldReturnBadRequestWhenAuthConfigIsNull()` 작성
- [ ] 커밋: `test: Validation 검증 E2E 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] Bean Validation 통합 확인
- [ ] 테스트 통과 확인
- [ ] 커밋: `feat: Validation 검증 E2E 통과 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 테스트 정리
- [ ] 커밋: `struct: Validation 검증 E2E 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] Validation 테스트용 Fixture 정리
- [ ] 커밋: `test: Validation 검증 E2E Fixture 정리 (Tidy)`

---

## 🎯 Final Phase: ArchUnit 통합 검증

### 2️⃣4️⃣ ArchUnit 전체 검증 (Cycle 24)

#### 🔴 Red: 테스트 작성
- [ ] `ArchitectureTest.java` 생성
- [ ] Layer 의존성 규칙 테스트
- [ ] Lombok 사용 금지 검증
- [ ] JPA 관계 어노테이션 금지 검증
- [ ] 커밋: `test: ArchUnit 전체 검증 테스트 추가 (Red)`

#### 🟢 Green: 최소 구현
- [ ] 모든 ArchUnit 규칙 통과 확인
- [ ] 커밋: `feat: ArchUnit 전체 검증 통과 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] ArchUnit 규칙 정리
- [ ] 커밋: `struct: ArchUnit 전체 검증 개선 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] ArchUnit 테스트 정리
- [ ] 커밋: `test: ArchUnit 전체 검증 정리 (Tidy)`

---

### 2️⃣5️⃣ 최종 통합 검증 및 문서화 (Cycle 25)

#### 🔴 Red: 테스트 작성
- [ ] 전체 테스트 스위트 실행
- [ ] 커버리지 80% 이상 확인
- [ ] 커밋: `test: 최종 통합 검증 (Red)`

#### 🟢 Green: 최소 구현
- [ ] 모든 테스트 통과 확인
- [ ] 커밋: `feat: 최종 통합 검증 통과 (Green)`

#### ♻️ Refactor: 리팩토링
- [ ] 불필요한 코드 제거
- [ ] Javadoc 최종 정리
- [ ] 커밋: `struct: 최종 코드 정리 (Refactor)`

#### 🧹 Tidy: TestFixture 정리
- [ ] 모든 Fixture 최종 정리
- [ ] README 업데이트
- [ ] 커밋: `test: 최종 TestFixture 정리 (Tidy)`

---

## ✅ 완료 조건

- [ ] 25개 모든 TDD 사이클 완료 (체크박스 모두 ✅)
- [ ] 100개 커밋 (각 사이클 × 4단계 = 100 커밋)
- [ ] 모든 테스트 통과 (Domain, Application, Persistence, REST API, Integration)
- [ ] ArchUnit 테스트 통과
- [ ] 테스트 커버리지 > 80%
- [ ] Zero-Tolerance 규칙 준수
- [ ] TestFixture 모두 정리 (Object Mother 패턴)
- [ ] PR 준비 완료

---

## 🔗 관련 문서

- **Task**: docs/prd/tasks/EXTMALL-001.md
- **PRD**: docs/prd/external-mall-management.md
- **Coding Rules**:
  - Domain: docs/coding_convention/02-domain-layer/
  - Application: docs/coding_convention/03-application-layer/
  - Persistence: docs/coding_convention/04-persistence-layer/
  - REST API: docs/coding_convention/01-adapter-in-layer/rest-api/

---

## 🚀 다음 단계

1. `/kb-domain` - Domain Layer TDD 시작 (Cycle 1~6)
2. `/kb-application` - Application Layer TDD (Cycle 7~10)
3. `/kb-persistence` - Persistence Layer TDD (Cycle 11~16)
4. `/kb-rest-api` - REST API Layer TDD (Cycle 17~19)
5. `/kb-integration` - Integration Test (Cycle 20~23)
6. 최종 검증 (Cycle 24~25)

---

## 📊 사이클 요약

| Phase | Cycles | 예상 시간 | 레이어 |
|-------|--------|----------|--------|
| Domain Layer | 1-6 | 1.5시간 | Foundation |
| Application Layer | 7-10 | 1시간 | UseCase |
| Persistence Layer | 11-16 | 1.5시간 | Foundation |
| REST API Layer | 17-19 | 45분 | Endpoint |
| Integration Test | 20-23 | 1시간 | E2E |
| ArchUnit 검증 | 24-25 | 30분 | Quality |
| **총계** | **25 사이클** | **6-7시간** | **Vertical Slice** |

---

## 💡 TDD 원칙 준수

- ✅ **Red → Green → Refactor → Tidy** 4단계 필수
- ✅ 각 단계마다 커밋 (총 100 커밋)
- ✅ TestFixture 패턴 (Object Mother)
- ✅ Zero-Tolerance 규칙 자동 검증 (ArchUnit)
- ✅ 작은 단위 (5-15분 내 완료)
