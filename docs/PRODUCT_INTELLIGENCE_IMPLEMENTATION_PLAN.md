# Product Intelligence Pipeline - 구현 계획

> **Status**: In Progress (Phase 0-1 완료, Step A 완료)
> **Created**: 2026-02-21
> **Last Updated**: 2026-02-21
> **Parent**: [PRODUCT_INTELLIGENCE_PIPELINE.md](./PRODUCT_INTELLIGENCE_PIPELINE.md)

---

## 0. 구현 진행 현황

### 전체 Phase 상태

| Phase | 설명 | 상태 | 비고 |
|-------|------|------|------|
| **Phase 0-1** | ProductProfile 도메인 + 영속성 | ✅ 완료 | Aggregate, Entity, Repository, Adapter, Migration 전부 구현 |
| **Phase 0-2** | ProductGroupStatus PENDING_REVIEW 추가 | ✅ 완료 | enum 값 + 상태 전이 메서드 추가됨 |
| **Phase 0-3** | Spring AI 의존성 + ChatClient Bean | ⚠️ 부분 | Anthropic 의존성 추가됨, 기존 stub Adapter 3개 존재. 신규 Client Port 미구현 |
| **Phase 0-4** | SQS 5개 큐 Terraform | ✅ 완료 | stage 환경에 `intelligence-*` 이름으로 5개 큐 + DLQ + CloudWatch Alarm + SSM 전부 구성 |
| **Phase 0-5** | SQS Consumer/Publisher 재구성 | ⚠️ 부분 | 아래 상세 참조 |
| **Phase 1-1** | Orchestrator Worker | ✅ 완료 | `OrchestrateAnalysisService` (데이터 수집은 미구현, 파이프라인 골격만) |
| **Phase 1-2** | Aggregator Worker | ✅ 완료 | `AggregateAnalysisService` (판정 로직은 stub) |
| **Phase 1-3** | Scheduler 수정 | ✅ 완료 | `IntelligenceOutbox` Outbox Relay 패턴 구현 완료 |
| **Phase 1-4** | 통합 테스트 | ❌ 미구현 | |
| **Step A** | 트리거 연결 (Option 2) | ✅ 완료 | 상품 등록/수정 → IntelligenceOutbox PENDING → Relay → Profile 생성 → 3큐 발행 |
| **Step B** | 3개 Analyzer SQS Consumer | ❌ 미구현 | |
| **Step C** | Aggregator 판정 로직 연결 | ❌ 미구현 | |
| **Step D** | Inspection ↔ Intelligence 전환 | ❌ 미구현 | |
| **Step E** | 통합 테스트 | ❌ 미구현 | |
| **Phase 2** | Option Analyzer | ❌ 미구현 | |
| **Phase 3** | Notice Analyzer | ❌ 미구현 | |
| **Phase 4** | Description Analyzer | ❌ 미구현 | |
| **Phase 5** | 피드백 루프 + 고도화 | ❌ 미구현 | |

### Phase 0-5 SQS 재구성 상세

**신규 구현 (Intelligence Pipeline)**:

| 구분 | 파일 | 상태 |
|------|------|------|
| SQS Consumer - Orchestration | `OrchestrationListener.java` | ✅ |
| SQS Consumer - Aggregation | `AggregationListener.java` | ✅ |
| SQS Consumer - Description/Option/Notice Analysis | 미생성 | ❌ |
| SQS Publisher - Description Analysis | `DescriptionAnalysisPublishAdapter.java` | ✅ |
| SQS Publisher - Option Analysis | `OptionAnalysisPublishAdapter.java` | ✅ |
| SQS Publisher - Notice Analysis | `NoticeAnalysisPublishAdapter.java` | ✅ |
| SQS Publisher - Aggregation | `AggregationPublishAdapter.java` | ✅ |

**기존 Inspection Pipeline (미제거, 공존 중)**:

| 구분 | 파일 | 상태 |
|------|------|------|
| SQS Consumer - Scoring | `ScoringListener.java` | 🔄 기존 유지 |
| SQS Consumer - Enhancement | `EnhancementListener.java` | 🔄 기존 유지 |
| SQS Consumer - Verification | `VerificationListener.java` | 🔄 기존 유지 |

### 추가 구현 사항 (문서 원안에 없던 것)

| 항목 | 설명 |
|------|------|
| **IntelligenceOutbox Aggregate** | Outbox Relay 패턴으로 SQS 발행 안정성 확보 (PENDING → SENT → COMPLETED/FAILED) |
| **IntelligenceOutbox 영속성** | Entity, JpaRepo, QueryDslRepo, Mapper, CommandAdapter, QueryAdapter, Migration(V73) |
| **IntelligenceOutbox 스케줄러** | `IntelligencePipelineScheduler` (ProcessPending 5분, RecoverTimeout 10분) |
| **IntelligenceRelayProcessor** | Orchestration (Profile 생성) + Outbox Relay → `IntelligencePublishManager.publishToAllAnalyzers()` |
| **Consumer 멱등성** | `AggregateAnalysisService`에 `hasExpectedStatus(ANALYZING)` CAS 가드 추가 |
| **트리거 연결 (Step A)** | 상품 등록/수정 시 `IntelligenceOutbox.forNew()` 생성. Relay에서 Profile 생성 + profileId 할당 + 3큐 발행 |

### 문서 원안과 실제 구현의 차이

| 항목 | 문서 원안 | 실제 구현 |
|------|----------|----------|
| **SQS 큐 네이밍** | `inspection-*` | `intelligence-*` |
| **Outbox 구조** | InspectionOutbox가 orchestration 큐로 relay | IntelligenceOutbox 별도 신설 (InspectionOutbox와 공존) |
| **트리거 경로** | 상품 등록 → InspectionOutbox → orchestration 큐 | 상품 등록/수정 → IntelligenceOutbox PENDING → Scheduler Relay → Profile 생성 → 3 Analyzer 큐 (Step A 완료) |
| **기존 Inspection** | 제거 예정 | 아직 공존 중 (Scoring/Enhancement/Verification 유지) |
| **Spring AI** | OpenAI (gpt-4o, gpt-4o-mini) | Anthropic (Claude) 의존성으로 변경 |

### 미해결 과제

1. ~~**트리거 연결 부재**~~: ✅ **해결 (Step A 완료)** — 상품 등록/수정 시 `IntelligenceOutbox.forNew()` 생성 → Scheduler Relay → Profile 생성 → 3큐 발행. `intelligence-orchestration` 큐는 우회 (Outbox Relay가 직접 Orchestration 수행).
2. **기존 Inspection ↔ Intelligence 전환 전략**: 두 시스템을 동시 운영할지, 완전 교체할지 결정 필요. (Step D)
3. **3개 Analyzer SQS Consumer 미구현**: Description/Option/Notice Analysis Listener가 아직 없음. (Step B)
4. **통합 테스트 부재**: 전체 파이프라인 흐름 검증 미완. (Step E)

---

## 1. 기술 선택

### LLM 모델 — 용도별 분리

| 용도 | 모델 | 근거 |
|------|------|------|
| Description 분석 (이미지+텍스트 멀티모달) | **gpt-4o** | 이미지 OCR 대체, 멀티모달 정확도 높음 |
| Option 매핑 (텍스트) | **gpt-4o-mini** | 비용 효율적, Few-shot 매핑에 충분 |
| Notice 보강 (텍스트) | **gpt-4o-mini** | 구조화 추출에 충분, 비용 절감 |

- Spring AI 1.0은 OpenAI + Anthropic 둘 다 지원
- 현재 `spring-ai-client`에 OpenAI 설정이 이미 있으므로 OpenAI 우선 구현
- Anthropic은 fallback 또는 A/B 테스트용으로 추후 추가 가능
- 모델별 ChatClient Bean을 분리하여 Analyzer별로 주입

### OCR 전략

- 별도 OCR API 없이 **GPT-4o 멀티모달**로 이미지 직접 분석
- Description 이미지 URL → GPT-4o에 이미지로 전달 → 텍스트 + 속성 동시 추출
- 장점: 구현 단순, 별도 API Key 불필요, OCR + 분석이 한 번에
- 단점: 비용이 전용 OCR보다 높음 → Description Analyzer만 gpt-4o 사용하는 이유

### SQS 큐 구성

기존 3개 제거, 5개 신규 생성:

| 큐 | Visibility Timeout | 용도 |
|---|---|---|
| `{env}-{project}-inspection-orchestration` | 60초 | 데이터 수집 + 분배 |
| `{env}-{project}-inspection-description-analysis` | 5분 | GPT-4o 멀티모달 분석 |
| `{env}-{project}-inspection-option-analysis` | 3분 | Rule + LLM 옵션 매핑 |
| `{env}-{project}-inspection-notice-analysis` | 3분 | Rule + LLM 고시 보강 |
| `{env}-{project}-inspection-aggregation` | 2분 | 집계 + 판정 |

각 큐에 DLQ 포함 (maxReceiveCount=3, DLQ 보존 14일).

---

## 2. 작업 순서 및 의존성

```
Phase 0: 기반 준비
├── [0-1] ProductProfile 도메인 설계 + 구현
├── [0-2] ProductGroupStatus에 PENDING_REVIEW 추가
├── [0-3] Spring AI 의존성 활성화 + ChatClient Bean 구성
├── [0-4] SQS 5개 큐 Terraform 구성
└── [0-5] SQS Consumer/Publisher 코드 재구성

Phase 1: Orchestrator + Aggregator (파이프라인 골격)
├── [1-1] Orchestrator Worker 구현
├── [1-2] Aggregator Worker 기본 구현
├── [1-3] Scheduler 수정 (새 큐로 relay)
└── [1-4] 통합 테스트 (전체 파이프라인 흐름)

Phase 2: Option Analyzer (가장 빠른 효과)
├── [2-1] Rule 기반 자동 매핑 (동의어 사전)
├── [2-2] LLM 보조 매핑
├── [2-3] Aggregator에 옵션 자동 적용 로직
└── [2-4] 테스트

Phase 3: Notice Analyzer
├── [3-1] 필수 필드 검증 (Rule 기반)
├── [3-2] LLM 보강 제안
├── [3-3] Aggregator에 고시정보 자동 적용 로직
└── [3-4] 테스트

Phase 4: Description Analyzer
├── [4-1] GPT-4o 멀티모달 분석 구현
├── [4-2] extractedAttributes 저장
├── [4-3] Aggregator 고도화 (Description 결과 활용)
└── [4-4] 테스트

Phase 5: 고도화
├── [5-1] 피드백 루프 (검수자 보정 → 동의어 사전 자동 확장)
├── [5-2] 셀러 품질 점수 (Seller Quality Index)
└── [5-3] 자동승인 임계값 동적 조정
```

---

## 3. Phase 0: 기반 준비

### [0-1] ProductProfile 도메인 설계 + 구현

**대상 모듈**: `domain`, `application`, `adapter-out/persistence-mysql`

#### Domain Layer

```
domain/src/main/java/com/ryuqq/marketplace/domain/productintelligence/
├── aggregate/
│   └── ProductProfile.java
├── id/
│   └── ProductProfileId.java
└── vo/
    ├── ExtractedAttribute.java
    ├── OptionMappingSuggestion.java
    ├── NoticeSuggestion.java
    ├── AnalysisStatus.java          // PENDING, ANALYZING, AGGREGATING, COMPLETED, FAILED
    ├── InspectionDecision.java
    ├── DecisionType.java            // AUTO_APPROVED, HUMAN_REVIEW, AUTO_REJECTED
    ├── ConfidenceScore.java
    └── AnalysisSource.java          // DESCRIPTION_TEXT, OCR_IMAGE, LLM_INFERENCE, RULE_ENGINE
```

#### Application Layer

```
application/src/main/java/com/ryuqq/marketplace/application/productintelligence/
├── port/
│   ├── in/command/
│   │   ├── OrchestrateInspectionUseCase.java
│   │   ├── AnalyzeDescriptionUseCase.java
│   │   ├── AnalyzeOptionUseCase.java
│   │   ├── AnalyzeNoticeUseCase.java
│   │   └── AggregateInspectionUseCase.java
│   └── out/
│       ├── command/ProductProfileCommandPort.java
│       └── query/ProductProfileQueryPort.java
├── service/command/
│   ├── OrchestrateInspectionService.java
│   ├── AnalyzeDescriptionService.java
│   ├── AnalyzeOptionService.java
│   ├── AnalyzeNoticeService.java
│   └── AggregateInspectionService.java
├── manager/
│   ├── ProductProfileCommandManager.java
│   └── ProductProfileReadManager.java
└── factory/
    └── ProductProfileFactory.java
```

#### Persistence Layer

```
adapter-out/persistence-mysql/
├── entity/ProductProfileJpaEntity.java
├── repository/
│   ├── ProductProfileJpaRepository.java
│   └── ProductProfileQueryDslRepository.java
├── mapper/ProductProfileJpaEntityMapper.java
└── adapter/
    ├── ProductProfileCommandAdapter.java
    └── ProductProfileQueryAdapter.java
```

#### DB 마이그레이션

```sql
-- V72__create_product_profiles_table.sql

CREATE TABLE product_profiles (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_group_id         BIGINT NOT NULL,
    version                  INT NOT NULL DEFAULT 1,
    status                   VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    expected_analysis_count  INT NOT NULL DEFAULT 3,
    completed_analysis_count INT NOT NULL DEFAULT 0,

    -- 분석 결과 (JSON)
    extracted_attributes     JSON NULL,
    option_suggestions       JSON NULL,
    notice_suggestions       JSON NULL,

    -- 판정
    decision_type            VARCHAR(20) NULL,
    overall_confidence       INT NULL,
    decision_reasons         JSON NULL,
    raw_analysis_json        JSON NULL,

    -- 타임스탬프
    analyzed_at              DATETIME(6) NULL,
    expired_at               DATETIME(6) NULL,
    created_at               DATETIME(6) NOT NULL,
    updated_at               DATETIME(6) NOT NULL,

    INDEX idx_product_profiles_product_group_id (product_group_id),
    INDEX idx_product_profiles_status (status),
    INDEX idx_product_profiles_product_group_version (product_group_id, version)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

**JSON 컬럼 선택 이유**:
- extractedAttributes, optionSuggestions, noticeSuggestions는 구조가 유동적
- 분석기별로 추출하는 속성 종류가 다를 수 있음
- JPA AttributeConverter로 도메인 VO ↔ JSON 변환

#### 테스트

- Domain: `ProductProfile` 상태 전이 단위 테스트
- Persistence: JpaEntityMapper 단위 테스트 + QueryDsl 통합 테스트
- testFixtures: `ProductProfileFixtures`, `ProductProfileJpaEntityFixtures`

---

### [0-2] ProductGroupStatus에 PENDING_REVIEW 추가

**대상 파일**: `domain/.../productgroup/vo/ProductGroupStatus.java`

변경:
- `PENDING_REVIEW` enum 값 추가
- `PROCESSING → PENDING_REVIEW` 전이 허용
- `PENDING_REVIEW → ACTIVE` 전이 허용 (검수자 승인)
- `PENDING_REVIEW → REJECTED` 전이 허용 (검수자 반려)

영향도:
- `ProductGroup.approve()` 메서드 추가 (PENDING_REVIEW → ACTIVE)
- JPA Entity의 Status enum에도 추가
- Mapper에 매핑 추가
- 기존 테스트에 새 상태 반영

---

### [0-3] Spring AI 의존성 활성화 + ChatClient Bean 구성

**대상 모듈**: `adapter-out/client/spring-ai-client`

1. `build.gradle`에서 Spring AI OpenAI 의존성 주석 해제:
   ```groovy
   implementation 'org.springframework.ai:spring-ai-openai-spring-boot-starter'
   ```

2. ChatClient Bean 분리 구성:
   ```java
   @Configuration
   public class AiClientConfig {

       @Bean("descriptionChatClient")
       public ChatClient descriptionChatClient(OpenAiChatModel chatModel) {
           // gpt-4o (멀티모달)
           return ChatClient.builder(chatModel)
               .defaultOptions(OpenAiChatOptions.builder()
                   .model("gpt-4o")
                   .temperature(0.2)
                   .build())
               .build();
       }

       @Bean("textChatClient")
       public ChatClient textChatClient(OpenAiChatModel chatModel) {
           // gpt-4o-mini (텍스트 전용)
           return ChatClient.builder(chatModel)
               .defaultOptions(OpenAiChatOptions.builder()
                   .model("gpt-4o-mini")
                   .temperature(0.3)
                   .build())
               .build();
       }
   }
   ```

3. Port 인터페이스 추가 (Application Layer):
   ```java
   // 기존 Client Port들을 새 구조에 맞게 재정의
   public interface DescriptionAnalysisClient {
       DescriptionAnalysisResult analyze(DescriptionAnalysisRequest request);
   }

   public interface OptionMappingClient {
       OptionMappingResult suggestMappings(OptionMappingRequest request);
   }

   public interface NoticeEnrichmentClient {
       NoticeEnrichmentResult suggest(NoticeEnrichmentRequest request);
   }
   ```

4. 기존 stub Adapter 3개 → 새 Adapter 3개로 교체

---

### [0-4] SQS 5개 큐 Terraform 구성

**대상**: `terraform/environments/{stage,prod}/sqs/`

기존 3개 큐 제거:
- `inspection-scoring`
- `inspection-enhancement`
- `inspection-verification`

신규 5개 큐 생성:

```hcl
module "inspection_orchestration_queue" {
  source = "git::https://github.com/ryu-qqq/Infrastructure.git//modules/sqs"

  queue_name               = "${var.environment}-${var.project_name}-inspection-orchestration"
  visibility_timeout       = 60       # 1분 (DB 조회 + SQS 분배)
  message_retention        = 345600   # 4일
  receive_wait_time        = 20       # Long Polling
  max_receive_count        = 3
  dlq_message_retention    = 1209600  # 14일

  kms_master_key_id     = module.sqs_kms_key.key_id
  environment           = var.environment
}

module "inspection_description_analysis_queue" {
  # visibility_timeout = 300 (5분, GPT-4o 멀티모달 호출)
}

module "inspection_option_analysis_queue" {
  # visibility_timeout = 180 (3분)
}

module "inspection_notice_analysis_queue" {
  # visibility_timeout = 180 (3분)
}

module "inspection_aggregation_queue" {
  # visibility_timeout = 120 (2분)
}
```

SSM Parameter Store에 큐 URL 저장 → ECS 환경변수로 주입.

---

### [0-5] SQS Consumer/Publisher 코드 재구성

**대상 모듈**: `adapter-in/sqs-consumer`, `adapter-out/client/sqs-client`

#### SQS Consumer (adapter-in/sqs-consumer)

기존 Listener 3개 제거:
- `ScoringListener`
- `EnhancementListener`
- `VerificationListener`

신규 Listener 5개:

```
productgroupinspection/
├── OrchestrationListener.java          // inspection-orchestration 큐
├── DescriptionAnalysisListener.java    // inspection-description-analysis 큐
├── OptionAnalysisListener.java         // inspection-option-analysis 큐
├── NoticeAnalysisListener.java         // inspection-notice-analysis 큐
└── AggregationListener.java            // inspection-aggregation 큐
```

각 Listener는 `@ConditionalOnProperty`로 개별 활성화/비활성화 가능.

#### SQS Publisher (adapter-out/client/sqs-client)

기존 Publisher 3개 제거, 신규 5개:

```
productgroupinspection/
├── OrchestrationPublishAdapter.java
├── DescriptionAnalysisPublishAdapter.java
├── OptionAnalysisPublishAdapter.java
├── NoticeAnalysisPublishAdapter.java
└── AggregationPublishAdapter.java
```

Application Layer Port:

```java
public interface OrchestrationPublishClient { String publish(String messageBody); }
public interface DescriptionAnalysisPublishClient { String publish(String messageBody); }
public interface OptionAnalysisPublishClient { String publish(String messageBody); }
public interface NoticeAnalysisPublishClient { String publish(String messageBody); }
public interface AggregationPublishClient { String publish(String messageBody); }
```

---

## 4. Phase 1: Orchestrator + Aggregator

### [1-1] Orchestrator Worker

**역할**: productGroupId를 받아 데이터 수집 → ProductProfile 생성 → 3개 Analysis 큐에 분배

```
Input: { "outboxId": 123, "productGroupId": 456 }

처리:
1. ProductGroup 조회 (상품명, 카테고리, 브랜드, 옵션)
2. ProductGroupDescription 조회 (HTML + 이미지 URL)
3. ProductNotice + NoticeCategory.NoticeFields 조회
4. CanonicalOption 전체 목록 조회 (매핑 후보군)
5. ProductProfile.forNew() 생성 (status: ANALYZING)
6. 3개 Analysis 큐에 메시지 발행:
   - description-analysis: { profileId, productGroupId, descriptionHtml, imageUrls, categoryInfo }
   - option-analysis: { profileId, productGroupId, sellerOptions, canonicalOptions }
   - notice-analysis: { profileId, productGroupId, currentEntries, requiredFields, productName }
```

**메시지 형식**: JSON (Jackson ObjectMapper 사용, 기존 InspectionMessageFactory 문자열 연산 대체)

### [1-2] Aggregator Worker

**역할**: 3개 Analyzer 결과가 모두 저장되면 집계 + 판정

```
트리거: completedAnalysisCount == expectedAnalysisCount (3)
       → Aggregation 큐에 메시지 발행

처리:
1. ProductProfile 조회 (모든 분석 결과 포함)
2. 자동 보강 적용 (confidence >= 0.95)
3. 종합 판정 (decisionType 결정)
4. ProductGroup 상태 전이
5. InspectionOutbox 완료 처리
```

### [1-3] Scheduler 수정

기존: PENDING → SQS scoring 큐로 발행
변경: PENDING → SQS **orchestration** 큐로 발행

`ProcessPendingInspectionService`에서 `scoringPublishManager` → `orchestrationPublishManager`로 교체.

### [1-4] 통합 테스트

이 시점에서 전체 파이프라인 흐름을 검증:

```
테스트 시나리오:
1. ProductGroup(PROCESSING) + InspectionOutbox(PENDING) 생성
2. Orchestrator 실행 → ProductProfile 생성 확인
3. 각 Analyzer stub 실행 → 결과 저장 확인
4. Aggregator 실행 → 판정 + ProductGroup 상태 전이 확인
```

Phase 1에서는 Analyzer를 stub으로 구현하여 파이프라인 흐름만 검증.

---

## 5. Phase 2: Option Analyzer

### [2-1] Rule 기반 자동 매핑

**새 컴포넌트**: `OptionMappingRuleEngine`

```
처리 순서:
1. 이미 매핑된 옵션 → skip
2. 완전 일치 (대소문자 무시, trim)
   "빨강" == CanonicalValue.nameKo "빨강" → confidence: 1.0
3. 정규화 일치 (동의어 사전 조회)
   "FREE SIZE" / "F" / "프리" → "FREE" → confidence: 1.0
4. 미매핑 잔여분 → LLM 단계로 전달
```

**동의어 사전**: 초기에는 DB 테이블로 관리

```sql
-- V73__create_option_synonym_dictionary.sql
CREATE TABLE option_synonym_dictionary (
    id                       BIGINT AUTO_INCREMENT PRIMARY KEY,
    canonical_option_value_id BIGINT NOT NULL,
    synonym                  VARCHAR(100) NOT NULL,
    source                   VARCHAR(20) NOT NULL,  -- MANUAL, LLM_APPROVED
    created_at               DATETIME(6) NOT NULL,

    UNIQUE INDEX idx_synonym_unique (synonym, canonical_option_value_id),
    INDEX idx_synonym_lookup (synonym)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
```

시드 데이터: 자주 쓰이는 매핑 (FREE SIZE/F/프리, XL/105, L/100 등)

### [2-2] LLM 보조 매핑

Rule로 해결 안 된 잔여분만 LLM에 전달:

```
Prompt:
당신은 이커머스 옵션 표준화 전문가입니다.
셀러가 입력한 옵션을 표준 옵션에 매핑하세요.

[표준 옵션 그룹 목록]
- COLOR: 빨강, 파랑, 검정, 흰색, 네이비, 베이지, ...
- SIZE: XS, S, M, L, XL, XXL, FREE, ...

[셀러 옵션]
- 그룹: "사이즈", 값: ["28(S)", "30(M)", "32(L)"]

다음 JSON 형식으로 매핑하세요:
[
  { "sellerValue": "28(S)", "canonicalGroup": "SIZE", "canonicalValue": "S", "confidence": 0.95 },
  ...
]
```

- 모델: **gpt-4o-mini** (텍스트 전용, 비용 효율)
- Few-shot 예시 10개 포함 (정확도 향상)

### [2-3] Aggregator에 옵션 자동 적용

```
confidence >= 0.95:
  → SellerOptionValue.canonicalOptionValueId 자동 업데이트
  → OptionMappingSuggestion.appliedAutomatically = true

confidence 0.80~0.95:
  → 자동 적용 + Human Review 마킹

confidence < 0.80:
  → 제안만 저장, 수동 확인 대기
```

### [2-4] 테스트

```
단위 테스트:
- OptionMappingRuleEngine: 완전 일치, 정규화 일치, 미매핑 케이스
- OptionAnalyzeService: Rule 결과 + LLM 결과 병합

통합 테스트:
- 실제 CanonicalOption 데이터 + 다양한 셀러 옵션 조합
- LLM은 stub (고정 응답 반환)
- confidence별 자동 적용/미적용 검증

LLM 테스트 (수동/CI 제외):
- 실제 gpt-4o-mini 호출하여 매핑 품질 확인
- @Tag("llm-integration")으로 분리, CI에서는 skip
```

---

## 6. Phase 3: Notice Analyzer

### [3-1] 필수 필드 검증 (Rule 기반)

```
처리:
1. NoticeCategory → NoticeFields (required=true인 것들) 조회
2. 현재 ProductNotice.entries와 대조
3. 빈 값, "상세페이지 참조", "해당없음" 등 무의미한 값 탐지
4. 누락 필수 필드 목록 생성
5. completenessScore 산출:
   - 필수 필드 충족률: (입력된 유의미 값 / 전체 필수 필드) * 100
```

무의미한 값 패턴 (정규식):

```
^(상세.?페이지.?참조|해당.?없음|없음|-|N/?A|미정|추후.?공지|확인.?중)$
```

### [3-2] LLM 보강 제안

누락된 필수 필드가 있을 때만 LLM 호출:

```
Prompt:
당신은 이커머스 상품 고시정보 전문가입니다.
아래 상품 정보에서 누락된 법정 고시 항목의 값을 추출하세요.

[상품명]: 린넨 블렌드 셔츠 원피스
[카테고리]: 여성의류 > 원피스
[상품 설명]: (Description HTML 텍스트)

[누락된 필수 항목]:
- 소재 (fieldId: 5)
- 제조국 (fieldId: 7)
- 세탁방법 (fieldId: 9)

다음 JSON 형식으로 추출하세요:
[
  { "fieldId": 5, "fieldName": "소재", "suggestedValue": "린넨 55%, 면 45%", "confidence": 0.88, "source": "description_text" },
  ...
]
추출할 수 없는 항목은 confidence: 0.0으로 표시하세요.
```

- 모델: **gpt-4o-mini**
- Description Analyzer 결과(extractedAttributes)가 이미 있으면 LLM 재호출 없이 매핑만으로 처리 가능 (비용 절감)

### [3-3~3-4] Aggregator 적용 + 테스트

옵션과 동일한 confidence 기반 자동/수동 분기.

---

## 7. Phase 4: Description Analyzer

### [4-1] GPT-4o 멀티모달 분석

```
처리:
1. DescriptionHtml → 순수 텍스트 추출 (Jsoup 사용)
2. DescriptionHtml.extractImageUrls() → 이미지 URL 목록
3. 대표 이미지(ProductGroupImages) URL 추가
4. GPT-4o 멀티모달 호출:
   - 텍스트 + 이미지 URL들을 함께 전달
   - JSON Schema 기반 구조화 출력 요청
```

**Prompt 구조**:

```
당신은 이커머스 상품 데이터 분석 전문가입니다.
아래 상품의 텍스트와 이미지를 분석하여 구조화된 속성을 추출하세요.

[카테고리]: {categoryName}
[상품명]: {productGroupName}
[상세 설명 텍스트]: {parsedText}
[이미지]: (첨부된 이미지들)

다음 JSON 형식으로 추출하세요. 확인할 수 없는 항목은 포함하지 마세요:
{
  "material": { "value": "...", "confidence": 0.0~1.0 },
  "origin": { "value": "...", "confidence": 0.0~1.0 },
  "manufacturer": { "value": "...", "confidence": 0.0~1.0 },
  "washCare": { "value": "...", "confidence": 0.0~1.0 },
  "sizeInfo": { "value": "...", "confidence": 0.0~1.0 },
  "season": { "value": "...", "confidence": 0.0~1.0 },
  "fit": { "value": "...", "confidence": 0.0~1.0 },
  "keywords": ["...", "..."],
  "qualityIssues": ["텍스트 200자 미만", ...]
}
```

**이미지 전달 제한**:
- 최대 5장 (비용 관리)
- 사이즈표/소재표 이미지 우선 선택 (이미지 파일명/alt 텍스트 기반 필터링)
- 이미지 리사이즈: 긴 변 기준 1024px (토큰 절감)

### [4-2~4-4] 저장 + Aggregator 활용 + 테스트

- extractedAttributes를 ProductProfile JSON 컬럼에 저장
- Notice Analyzer가 이 결과를 참조하여 LLM 재호출 회피 가능
- 테스트: 실제 이미지 URL 대신 테스트 이미지로 검증

---

## 8. 테스트 전략

### 레이어별 테스트

| 레이어 | 테스트 유형 | 도구 | LLM 호출 |
|--------|-----------|------|---------|
| Domain | 단위 테스트 | JUnit 5 | 없음 |
| Application (Service) | Mockito 기반 단위 | JUnit 5 + Mockito | Mock |
| Application (RuleEngine) | 단위 테스트 | JUnit 5 | 없음 |
| Persistence | 통합 테스트 | Testcontainers MySQL | 없음 |
| SQS Consumer | 단위 테스트 | Mockito | Mock |
| Spring AI Client | 단위 테스트 | Mockito (ChatClient Mock) | Mock |
| E2E | 통합 테스트 | REST Assured + H2 + Stub | Stub |

### LLM 통합 테스트 (별도 프로파일)

```java
@Tag("llm-integration")  // CI에서는 exclude
@ActiveProfiles("llm-test")
class OptionMappingLlmIntegrationTest {
    // 실제 gpt-4o-mini 호출
    // 결과 품질 검증 (confidence, 정확도)
    // 비용 모니터링
}
```

- `@Tag("llm-integration")`으로 분리
- CI에서는 skip, 수동으로만 실행
- 실제 API Key 필요 (환경변수)

### Stub 구성 (integration-test)

```java
// StubExternalClientConfig.java 확장
@Bean @Primary
public DescriptionAnalysisClient stubDescriptionAnalysisClient() {
    return request -> DescriptionAnalysisResult.of(
        List.of(
            ExtractedAttribute.of("material", "면 100%", 0.95, DESCRIPTION_TEXT),
            ExtractedAttribute.of("origin", "중국", 0.88, LLM_INFERENCE)
        ), 78);
}

@Bean @Primary
public OptionMappingClient stubOptionMappingClient() {
    return request -> OptionMappingResult.of(
        List.of(
            OptionMappingSuggestion.of("빨강", "COLOR", "RED", 0.97)
        ), true);
}

@Bean @Primary
public NoticeEnrichmentClient stubNoticeEnrichmentClient() {
    return request -> NoticeEnrichmentResult.of(
        List.of(
            NoticeSuggestion.of(5L, "소재", "", "면 100%", 0.92, DESCRIPTION_TEXT)
        ), 90);
}
```

---

## 9. 인프라 변경 체크리스트

### Terraform

- [x] ~~기존 SQS 3개 큐 제거 (scoring, enhancement, verification)~~ → 아직 제거 안 함 (공존 중)
- [x] 신규 SQS 5개 큐 생성 — `intelligence-*` 네이밍으로 stage 구성 완료
- [x] 각 큐 DLQ + CloudWatch Alarm 구성
- [x] SSM Parameter Store에 새 큐 URL 저장
- [ ] ECS Worker 환경변수 업데이트 (새 큐 URL 5개)
- [ ] ECS Worker 오토스케일링 설정 검토 (SQS 메시지 수 기반)
- [ ] prod 환경 Terraform 구성

### DB 마이그레이션

- [x] `V72__create_product_profiles_table.sql`
- [x] `V73__create_intelligence_outboxes_table.sql` (문서 원안의 option_synonym_dictionary 대신)
- [x] ProductGroupStatus에 PENDING_REVIEW 추가 (코드 레벨, DB enum 변경은 VARCHAR이므로 불필요)
- [ ] `V??__create_option_synonym_dictionary.sql` (Phase 2에서 필요)

### ECS

- [ ] Worker 컨테이너 메모리/CPU 조정 검토 (LLM 호출 시 응답 대기 스레드 증가)
- [ ] Worker 인스턴스 수 검토 (Description Analyzer가 병목)
- [ ] ANTHROPIC_API_KEY SSM Parameter 확인 (OpenAI → Anthropic 변경)

### 모니터링

- [x] DLQ 알람 (5개 큐 전부 CloudWatch Alarm 구성 완료)
- [ ] LLM API 호출 지연시간 메트릭 (CloudWatch Custom Metric)
- [ ] LLM 비용 추적 (일별 토큰 사용량)
- [ ] 자동승인율 / Human-Review율 대시보드

---

## 10. 비용 추정

### LLM API 비용 (상품 1건 기준)

| Analyzer | 모델 | 입력 토큰 (예상) | 출력 토큰 | 비용 (USD) |
|----------|------|-----------------|----------|-----------|
| Description | gpt-4o | ~2,000 (텍스트) + ~1,000 (이미지 5장) | ~500 | ~$0.015 |
| Option | gpt-4o-mini | ~500 | ~200 | ~$0.0001 |
| Notice | gpt-4o-mini | ~800 | ~300 | ~$0.0002 |
| **합계** | | | | **~$0.015/건** |

- 일 1,000건 검수 시: ~$15/일, ~$450/월
- Rule 기반 처리 비율이 높아질수록 LLM 호출 감소 → 비용 절감
- Description Analyzer가 전체 비용의 ~97% → 이미지 수 제한이 핵심

### SQS 비용

- 5개 큐 × 일 1,000건: 무시 가능 수준 ($0.40/백만 요청)

---

## 11. 리스크 및 대응

| 리스크 | 영향 | 대응 |
|--------|------|------|
| LLM 응답 지연 (>30초) | SQS visibility timeout 초과 → 중복 처리 | visibility timeout 여유 확보 + 멱등성 보장 |
| LLM API 장애 | 검수 파이프라인 중단 | 재시도 3회 → DLQ → 모니터링 알림, Fallback(NoOp) 구성 |
| GPT-4o 이미지 분석 비용 급증 | 월 비용 예상 초과 | 이미지 수 제한(5장), 리사이즈(1024px), 일별 예산 한도 설정 |
| 자동 보강 오류 | 잘못된 값이 자동 적용 | confidence 임계값 보수적 설정(0.95), 초기에는 Human-Review 비율 높게 |
| SQS 큐 재구성 시 기존 메시지 유실 | 처리 중인 검수 건 유실 | 배포 시 기존 큐 drain 후 전환, InspectionOutbox로 복구 가능 |

---

## 12. 마일스톤

| Phase | 예상 산출물 | 검증 기준 | 상태 |
|-------|-----------|----------|------|
| Phase 0 | 기반 도메인 + 인프라 + Spring AI 설정 | ProductProfile CRUD + SQS 5개 큐 동작 | ⚠️ 대부분 완료 (Spring AI Client Port 미정의) |
| Phase 1 | 파이프라인 골격 (Orchestrator + Aggregator) | 전체 흐름 통합 테스트 통과 (Analyzer는 stub) | ⚠️ 골격 완료 + Step A 완료 (Step B~E 남음) |
| Phase 2 | Option Analyzer | 캐노니컬 미매핑 상품 자동 처리 확인 | ❌ 미착수 |
| Phase 3 | Notice Analyzer | 빈 고시정보 자동 보강 확인 | ❌ 미착수 |
| Phase 4 | Description Analyzer | 이미지 OCR + 속성 추출 확인 | ❌ 미착수 |
| Phase 5 | 피드백 루프 + 고도화 | 자동승인율 > 50% 달성 | ❌ 미착수 |

---

## 13. 다음 작업 계획 (Phase 0~1 완성)

현재 Phase 0~1 골격은 구현되었으나 **파이프라인이 실제로 동작하지 않는 상태**. 아래 작업을 순서대로 완료하면 stub Analyzer 기반으로 전체 흐름이 돌아간다.

### Step A: 트리거 연결 — IntelligenceOutbox 기반 (Option 2) ✅ 완료

**목표**: 상품 등록/수정 시 Intelligence Pipeline이 자동으로 시작되도록 연결

**선택**: 옵션 2 — IntelligenceOutbox를 상품 등록/수정 시 직접 생성, Scheduler Relay에서 Orchestration 수행

**구현된 흐름**:
```
상품 등록/수정
  → IntelligenceOutbox.forNew(productGroupId) PENDING 생성 (profileId 없음)
  → Scheduler가 PENDING Outbox 수집
  → IntelligenceRelayProcessor.relay():
    1. OrchestrateAnalysisService.execute() → ProductProfile 생성 → profileId 반환
    2. outbox.assignProfile(profileId)
    3. outbox.markAsSent() + persist
    4. IntelligencePublishManager.publishToAllAnalyzers() → SQS 3큐 발행
    5. outbox.complete() + persist
```

**수정된 파일 (6개)**:

| 파일 | 변경 내용 |
|------|-----------|
| `OrchestrateAnalysisUseCase.java` | `void` → `Long` 반환 (profileId) |
| `OrchestrateAnalysisService.java` | IntelligenceOutbox 생성 제거, `@Transactional` 추가, profileId 반환 |
| `IntelligenceRelayProcessor.java` | Orchestration 단계 추가 (Profile 생성 → profileId 할당 → SQS 발행) |
| `FullProductGroupRegistrationCoordinator.java` | `IntelligenceOutbox.forNew()` 생성 추가 |
| `FullProductGroupUpdateCoordinator.java` | `IntelligenceOutbox.forNew()` 생성 추가 |
| `OrchestrationListener.java` | 인터페이스 반환 타입 변경 반영 |

**테스트 수정 (2개)**:

| 파일 | 변경 내용 |
|------|-----------|
| `FullProductGroupRegistrationCoordinatorTest.java` | `@Mock IntelligenceOutboxCommandManager` 추가 |
| `FullProductGroupUpdateCoordinatorTest.java` | `@Mock IntelligenceOutboxCommandManager` 추가 |

**참고**: `OrchestrationListener`는 유지하지만, 이제 `intelligence-orchestration` 큐에 메시지를 발행하는 Publisher가 없으므로 사실상 비활성 상태. 향후 Step D에서 정리 가능.

### Step B: 3개 Analyzer SQS Consumer (Stub)

**목표**: Description/Option/Notice Analysis 큐의 메시지를 수신하여 stub 처리

**구현 작업**:
1. `DescriptionAnalysisListener.java` — SQS 수신 → stub 결과 저장
2. `OptionAnalysisListener.java` — SQS 수신 → stub 결과 저장
3. `NoticeAnalysisListener.java` — SQS 수신 → stub 결과 저장
4. 각 Listener의 UseCase/Service/Command 생성 (Application Layer)
5. stub 구현: ProductProfile에 더미 분석 결과 저장 + completedAnalysisCount 증가
6. 마지막 Analyzer 완료 시 Aggregation 큐 발행

### Step C: Aggregator 판정 로직 연결

**목표**: 3개 Analyzer 결과를 종합하여 ProductGroup 상태 전이

**구현 작업**:
1. `AggregateAnalysisService`에서 ProductGroup 상태 전이 로직 추가
   - Auto-Approve → `productGroup.activate(now)` → `ProductGroupActivatedEvent`
   - Human-Review → `productGroup.pendingReview(now)` (ProductGroup에 메서드 추가)
   - Auto-Reject → `productGroup.reject(now)`
2. `AnalysisDecisionMaker` stub 판정: 초기에는 무조건 Auto-Approve
3. InspectionOutbox 완료 처리 연결 (또는 IntelligenceOutbox 완료로 대체)

### Step D: 기존 Inspection ↔ Intelligence 전환

**목표**: 기존 Inspection 파이프라인을 Intelligence Pipeline으로 교체

**결정 필요 사항**:
- 즉시 교체: 기존 3개 Listener/Publisher 제거, Intelligence로 완전 전환
- 점진 교체: Feature Flag로 상품별로 Intelligence Pipeline 적용, 안정화 후 전환
- 병렬 운영: 두 파이프라인 동시 실행 → 결과 비교 → 확신 후 전환

**권장**: Feature Flag 기반 점진 교체
- `scheduler.jobs.intelligence-pipeline.enabled=true` / `scheduler.jobs.inspection.enabled=false`
- 환경별 오버라이드: stage에서 먼저 Intelligence, prod는 기존 유지

### Step E: 통합 테스트

**목표**: 전체 파이프라인 E2E 검증

**테스트 시나리오**:
```
1. ProductGroup(PROCESSING) 등록 + IntelligenceOutbox(PENDING) 생성
2. 스케줄러 → IntelligenceOutbox Relay → OrchestrateAnalysisService
3. ProductProfile 생성 확인 (status: ANALYZING)
4. 3개 Analyzer stub 실행 → completedAnalysisCount == 3 확인
5. Aggregation 실행 → 판정 + ProductGroup 상태 전이 확인
6. Auto-Approve 시 ProductGroupActivatedEvent → ExternalProductSyncOutbox 생성 확인
```

### 작업 순서 요약

```
Step A: 트리거 연결 (✅ 완료)
  └─ 상품 등록/수정 → IntelligenceOutbox 생성 → Relay에서 Profile 생성 + 3큐 발행

Step B: 3개 Analyzer Stub (1~2일) ← 다음 작업
  └─ SQS Consumer + UseCase + Stub Service

Step C: Aggregator 판정 연결 (1일)
  └─ ProductGroup 상태 전이 + 이벤트 발행

Step D: 전환 전략 (1일)
  └─ Feature Flag + 기존 Inspection 비활성화

Step E: 통합 테스트 (1~2일)
  └─ E2E 파이프라인 검증

→ Phase 0~1 완전 완성 (Analyzer는 stub, 파이프라인 동작)
→ 이후 Phase 2~4에서 실제 Analyzer 구현 (Rule Engine + LLM)
```
