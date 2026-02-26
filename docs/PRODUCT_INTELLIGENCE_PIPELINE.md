# Product Intelligence Pipeline - 상품 검수 자동화 시스템 설계

> **Status**: In Progress (기반 골격 구현 완료, Analyzer 미구현)
> **Created**: 2026-02-21
> **Last Updated**: 2026-02-21
> **Domain**: `productgroupinspection` → `productintelligence` (확장)
> **구현 상세**: [PRODUCT_INTELLIGENCE_IMPLEMENTATION_PLAN.md](./PRODUCT_INTELLIGENCE_IMPLEMENTATION_PLAN.md)

---

## 0. 현재 구현 상태 요약

### 완료된 것

- **`productintelligence` 도메인**: ProductProfile Aggregate + VO 전체 구현
- **IntelligenceOutbox Outbox Relay**: PENDING → SENT → COMPLETED/FAILED 상태 관리 + 스케줄러
- **파이프라인 골격**: Orchestrator(`OrchestrateAnalysisService`) + Aggregator(`AggregateAnalysisService`)
- **SQS 인프라**: Terraform으로 5개 큐 + DLQ + CloudWatch Alarm 구성 완료 (stage)
- **SQS Publisher**: 3개 Analyzer 큐 + Aggregation 큐 Publisher 구현
- **Consumer 멱등성**: `hasExpectedStatus()` CAS 패턴 적용
- **ProductGroupStatus**: `PENDING_REVIEW` 상태 추가 완료

### 진행 중 / 미완료

- ~~**트리거 연결**~~: ✅ **해결** — 상품 등록/수정 시 `IntelligenceOutbox.forNew()` 생성 → Scheduler Relay → Profile 생성 → 3큐 발행 (Option 2 방식, SQS orchestration 큐 우회)
- **3개 Analyzer Consumer**: Description/Option/Notice Analysis SQS Listener 미구현 (Step B)
- **3개 Analyzer Service**: 실제 분석 로직 (Rule Engine + LLM 호출) 미구현
- **Spring AI Client**: 기존 stub Adapter만 존재, 신규 Client Port 미정의
- **기존 Inspection 전환**: Scoring/Enhancement/Verification 파이프라인 아직 공존 (Step D)
- **통합 테스트**: 전체 파이프라인 E2E 검증 미완 (Step E)

---

## 1. 배경 및 목표

### 현재 상황

- 상품 등록 시 검수(Inspection)를 거쳐 ACTIVE 상태로 전환
- 검수 통과 후 외부 판매채널(네이버커머스, 세토프, 바이마, LF몰)에 연동
- 현재 검수 파이프라인: **Scoring → Enhancement → Verification** (3단계 순차)
- Spring AI Client Adapter 3개가 stub 상태 (실제 AI 호출 미구현)

### 현재 구조의 한계

| 한계 | 설명 |
|------|------|
| 순차 파이프라인 비효율 | Scoring → Enhancement → Verification 루프, LLM 호출 시 최소 2~3회 |
| Enhancement 모놀리식 | Notice/Option/Image 보강이 하나의 큐에서 처리 |
| 분석 결과 휘발 | 점수(int)만 저장, AI가 추출한 정보가 버려짐 |
| 이진 판정 | 통과/실패만 존재, 중간 단계(사람 검수 대기) 없음 |
| 데이터 재활용 없음 | 검수 결과가 검색/추천/채널연동에 활용 불가 |

### 목표

1. **검수 자동화 극대화**: Description/이미지에서 정보 추출 → 고시정보 보완, 캐노니컬 옵션 매핑 자동화
2. **상품 지능화**: 검수를 단순 통과/실패 판정이 아닌, 상품 데이터 분석 + 보강 + 축적 프로세스로 확장
3. **고가용성**: Worker 기반 병렬 처리, 독립적 스케일링
4. **장기 데이터 가치**: 축적된 ProductProfile이 검색, 추천, 채널연동, 셀러 품질 관리에 재활용

---

## 2. 아키텍처 개요

### 핵심 발상 전환

```
[기존] "채점 → 보충 → 재채점" 순차 루프
[제안] "수집 → 병렬분석+보강 → 집계+판정" 파이프라인

- LLM은 분석하면서 동시에 보강 제안을 생성할 수 있음
- 분석 유형별 Worker를 분리하여 독립적으로 스케일링
- 모든 분석 결과를 구조화하여 저장 (ProductProfile)
```

### 전체 파이프라인

```
[상품 등록/수정]
    │
    ▼
[InspectionOutbox] ──Scheduler──→ [SQS: inspection-orchestration]
                                        │
                                        ▼
                                ┌───────────────┐
                                │  Orchestrator  │  (경량 Worker)
                                │    Worker      │  LLM 호출 없음
                                └───────┬───────┘
                                        │ 데이터 수집 + 전처리 + 분배
                    ┌───────────────────┼───────────────────┐
                    ▼                   ▼                   ▼
            [SQS: description]  [SQS: option]       [SQS: notice]
            ┌────────────────┐  ┌──────────────┐    ┌──────────────┐
            │  Description   │  │   Option     │    │   Notice     │
            │   Analyzer     │  │  Analyzer    │    │  Analyzer    │
            │  (OCR + LLM)   │  │ (Rule + LLM) │    │ (Rule + LLM) │
            └───────┬────────┘  └──────┬───────┘    └──────┬───────┘
                    │                  │                    │
                    │    결과를 DB에 저장 (ProductProfile)    │
                    └──────────────────┼────────────────────┘
                                       ▼
                               [SQS: inspection-aggregation]
                               ┌──────────────────┐
                               │    Aggregator     │
                               │     Worker        │
                               └────────┬─────────┘
                                        │ 집계 + 자동보강 + 판정
                       ┌────────────────┼────────────────┐
                       ▼                ▼                ▼
                 Auto-Approve     Human-Review      Auto-Reject
                  (ACTIVE)       (PENDING_REVIEW)   (REJECTED)
```

---

## 3. Phase별 상세 설계

### Phase 1: Orchestrator Worker

**역할**: 데이터 수집 + 전처리 + 각 Analyzer에 분배 (LLM 호출 없음, 경량)

**Input**: `productGroupId` (SQS 메시지)

**수집 대상**:

| 데이터 | 도메인 | 용도 |
|--------|--------|------|
| 상품 기본 정보 | `ProductGroup` | 상품명, 카테고리, 브랜드, 옵션 타입 |
| 셀러 옵션 | `ProductGroup.sellerOptionGroups` | 현재 옵션 + 캐노니컬 매핑 상태 |
| 상세 설명 | `ProductGroupDescription` | HTML 원문 + 이미지 URL 목록 |
| 상품 이미지 | `ProductGroupImages` | 썸네일 + 상세이미지 URL |
| 현재 고시정보 | `ProductNotice` | 입력된 entries |
| 고시 스키마 | `NoticeCategory → NoticeFields` | 해당 카테고리 필수/선택 필드 목록 |
| 캐노니컬 옵션 | `CanonicalOptionGroup/Value` | 매핑 후보군 |

**처리**:

```
1. ProductProfile 레코드 생성 (status: ANALYZING)
2. 각 Analyzer에 필요한 데이터를 조합하여 SQS 메시지 발행:
   - description-analysis 큐: HTML + 이미지URLs + 카테고리정보
   - option-analysis 큐: 셀러옵션 + CanonicalOption 후보군
   - notice-analysis 큐: 현재 Notice + 필수 스키마 + 상품기본정보
3. ProductProfile에 expectedAnalysisCount = 3 저장
```

### Phase 2-A: Description Analyzer Worker

**역할**: 상세설명 텍스트/이미지에서 상품 속성 정보 추출

**처리 단계**:

```
Step 1 — 전처리 (LLM 불필요)
├── DescriptionHtml → 순수 텍스트 추출 (HTML 태그 제거)
├── DescriptionHtml.extractImageUrls() → 이미지 URL 목록
└── 이미지 URL → OCR API 호출 (CLOVA OCR / Amazon Textract)
    → 사이즈표, 소재표, 세탁 라벨 등의 텍스트 추출

Step 2 — LLM 멀티모달 분석
├── Input: 텍스트 + OCR 결과 + 카테고리 + 상품명
├── Prompt: 상품 속성 구조화 추출 (JSON Schema 기반)
└── Output: ExtractedAttributes (key-value-confidence-source)

Step 3 — 품질 평가
├── 텍스트 길이, 이미지 수, 정보 밀도 평가
└── qualityScore 산출
```

**LLM 프롬프트 구조** (예시):

```
당신은 이커머스 상품 정보 분석 전문가입니다.
아래 상품 설명 텍스트와 이미지 OCR 결과에서 구조화된 속성을 추출하세요.

[카테고리]: 여성의류 > 원피스
[상품명]: 린넨 블렌드 셔츠 원피스
[설명 텍스트]: ...
[OCR 결과]: ...

다음 JSON 형식으로 추출하세요:
{
  "material": { "value": "...", "confidence": 0.0~1.0 },
  "origin": { "value": "...", "confidence": 0.0~1.0 },
  "manufacturer": { "value": "...", "confidence": 0.0~1.0 },
  "washCare": { "value": "...", "confidence": 0.0~1.0 },
  "sizeInfo": { "value": "...", "confidence": 0.0~1.0 },
  "season": { "value": "...", "confidence": 0.0~1.0 },
  "fit": { "value": "...", "confidence": 0.0~1.0 },
  "additionalFeatures": ["...", "..."]
}
```

**Output 저장**: `ProductProfile.extractedAttributes`에 기록

### Phase 2-B: Option Analyzer Worker

**역할**: 셀러 옵션 → CanonicalOption 자동 매핑

**처리 단계**:

```
Step 1 — Rule 기반 자동 매핑 (LLM 불필요, 빠름)
├── 이미 매핑된 옵션 → skip
├── 완전 일치: "빨강" == CanonicalValue.nameKo "빨강" → 자동 매핑
├── 정규화 일치: trim + lowercase + 동의어 사전
│   "FREE SIZE" / "F" / "프리사이즈" / "free" → "FREE"
│   "네이비" / "남색" / "NAVY" → "NAVY"
└── 매핑 성공 시 confidence: 1.0

Step 2 — LLM 보조 매핑 (미매핑 잔여분만)
├── Input: 미매핑 셀러 옵션 + CanonicalOption 전체 목록
├── Few-shot prompt: 유사한 매핑 사례 10개 포함
└── Output: 매핑 제안 + confidence
```

**Rule 기반 동의어 사전 관리**:

```
향후 LLM 매핑 결과 중 confidence >= 0.95이고
검수자가 승인한 매핑을 자동으로 동의어 사전에 추가
→ Rule 기반 처리 비율이 시간이 지날수록 증가
→ LLM 호출 비용 점진적 감소
```

**Output 저장**: `ProductProfile.optionMappingSuggestions`에 기록

### Phase 2-C: Notice Analyzer Worker

**역할**: 고시정보 완성도 검증 + 누락 필드 자동 보강 제안

**처리 단계**:

```
Step 1 — Rule 기반 검증 (LLM 불필요)
├── NoticeCategory.fields에서 required=true인 필드 추출
├── 현재 ProductNotice.entries와 대조
├── 빈 필드, "상세페이지 참조", "해당없음" 등 무의미한 값 탐지
└── completenessScore 산출 (필수 필드 기준)

Step 2 — LLM 보강 제안 (빈 필수 필드가 있을 때만)
├── Input: 빈 필수 필드 목록 + Description 텍스트 + 상품명 + 카테고리
├── Prompt: "상품 설명에서 다음 고시정보 필드의 값을 추출하세요"
└── Output: 필드별 보강 제안 + confidence + 근거(source)

※ Description Analyzer의 extractedAttributes를 활용할 수 있으면
   LLM 재호출 없이 매핑만으로 처리 가능 (비용 절감)
```

**Output 저장**: `ProductProfile.noticeSuggestions`에 기록

### Phase 3: Aggregator Worker

**역할**: 전체 분석 결과 집계 + 자동 보강 적용 + 최종 판정

**트리거**: 모든 Analyzer 결과가 저장된 후 실행

```
completedAnalysisCount == expectedAnalysisCount → Aggregation SQS 발행
```

**처리 단계**:

```
Step 1 — 결과 집계
├── Description 분석 결과 (추출 속성, 품질 점수)
├── Option 분석 결과 (매핑 제안, 완전매핑 여부)
└── Notice 분석 결과 (보강 제안, 완성도 점수)

Step 2 — 자동 보강 (confidence 임계값 기반)
├── confidence >= 0.95: 자동 적용
│   ├── CanonicalOption 매핑 자동 반영 → SellerOptionValue 업데이트
│   └── Notice 빈 필드 자동 채움 → ProductNoticeEntry 생성
│
├── confidence 0.80~0.95: 적용 + Human 확인 마킹
│   └── 적용하되 ReviewQueue에 해당 항목 등록
│
└── confidence < 0.80: 제안만 저장
    └── 검수자가 수동으로 확인/적용/거부

Step 3 — 종합 판정
```

**판정 로직**:

```
[필수 조건 체크]
├── 썸네일 이미지 존재 여부
├── 필수 고시정보 필드 충족 (직접 입력 + 자동 보강 합산)
├── 캐노니컬 옵션 매핑 완료 (직접 매핑 + 자동 매핑 합산)
└── Description 존재 + 이미지 업로드 완료

[종합 판정]
├── 필수 조건 모두 충족 + 전체 confidence 평균 >= 0.90
│   → Auto-Approve (ACTIVE)
│
├── 필수 조건 충족 + 자동보강으로 해결됨 + confidence 0.80~0.90
│   → Auto-Approve + Review 마킹 (ACTIVE, 사후 검수 대상)
│
├── 일부 조건 미충족이나 보강 제안 존재 + confidence 중간
│   → Human-Review 큐 (PENDING_REVIEW)
│
└── 필수 조건 미충족 + 보강 불가
    → Auto-Reject (REJECTED + 반려 사유 목록)
```

---

## 4. 새로운 도메인 설계: `productintelligence`

### 4.1 도메인 구조

```
domain/src/main/java/com/ryuqq/marketplace/domain/productintelligence/
├── aggregate/
│   └── ProductProfile.java               ← Aggregate Root
├── id/
│   └── ProductProfileId.java
└── vo/
    ├── ExtractedAttribute.java            ← AI가 추출한 속성
    ├── OptionMappingSuggestion.java        ← 옵션 매핑 제안
    ├── NoticeSuggestion.java              ← 고시정보 보강 제안
    ├── AnalysisStatus.java                ← 분석 진행 상태
    ├── InspectionDecision.java            ← 최종 판정
    ├── DecisionType.java                  ← AUTO_APPROVED / HUMAN_REVIEW / AUTO_REJECTED
    ├── ConfidenceScore.java               ← 신뢰도 점수 VO
    └── AnalysisSource.java                ← 정보 출처 (DESCRIPTION_TEXT / OCR_IMAGE / LLM_INFERENCE)
```

### 4.2 ProductProfile Aggregate

```java
ProductProfile
├── ProductProfileId id
├── ProductGroupId productGroupId
├── int version                             // 상품 수정 시마다 +1
│
├── // 분석 상태 관리
├── AnalysisStatus status                   // PENDING → ANALYZING → AGGREGATING → COMPLETED
├── int expectedAnalysisCount               // 3 (Description + Option + Notice)
├── int completedAnalysisCount              // 0 → 1 → 2 → 3
│
├── // 분석 결과
├── List<ExtractedAttribute> extractedAttributes
│   └── { key, value, confidence, source, analyzedAt }
│   └── 예: { "material", "면 95%", 0.95, OCR_IMAGE, ... }
│
├── List<OptionMappingSuggestion> optionSuggestions
│   └── { sellerOptionGroupId, sellerOptionValueId,
│         suggestedCanonicalGroupId, suggestedCanonicalValueId,
│         confidence, appliedAutomatically }
│
├── List<NoticeSuggestion> noticeSuggestions
│   └── { noticeFieldId, fieldName, currentValue, suggestedValue,
│         confidence, source, appliedAutomatically }
│
├── // 판정
├── InspectionDecision decision
│   └── { decisionType, overallConfidence, reasons[], decidedAt }
│
├── // 메타데이터
├── String rawAnalysisJson                  // LLM 원본 응답 (디버깅/학습용)
├── Instant analyzedAt
└── Instant expiredAt                       // 상품 수정 시 이전 프로파일 만료
```

### 4.3 ExtractedAttribute VO

```java
ExtractedAttribute
├── String key                // "material", "origin", "washCare", "sizeInfo", ...
├── String value              // "면 95%, 폴리에스터 5%"
├── ConfidenceScore confidence // 0.0 ~ 1.0
├── AnalysisSource source     // DESCRIPTION_TEXT / OCR_IMAGE / LLM_INFERENCE
├── String sourceDetail       // "ocr_image_3" / "description_paragraph_2"
└── Instant extractedAt
```

### 4.4 상태 전이

```
ProductProfile 상태:
PENDING → ANALYZING → AGGREGATING → COMPLETED

ProductGroupStatus 확장:
DRAFT → PROCESSING → ACTIVE / REJECTED / PENDING_REVIEW (신규)
                                            │
                                            ├─ 검수자 승인 → ACTIVE
                                            └─ 검수자 반려 → REJECTED
```

---

## 5. SQS 큐 구성

### 큐 설계

| 큐 이름 | 역할 | Worker 수 | Visibility Timeout |
|---------|------|-----------|-------------------|
| `inspection-orchestration` | 데이터 수집 + 분배 | 1~2 | 60초 |
| `inspection-description-analysis` | Description OCR + LLM 분석 | 2~4 | 5분 |
| `inspection-option-analysis` | 옵션 Rule + LLM 매핑 | 1~2 | 3분 |
| `inspection-notice-analysis` | 고시정보 Rule + LLM 보강 | 1~2 | 3분 |
| `inspection-aggregation` | 집계 + 자동보강 + 판정 | 1~2 | 2분 |

### DLQ 설계

| DLQ | 조건 | 후속 처리 |
|-----|------|----------|
| `inspection-*-dlq` | 각 큐별, maxReceiveCount=3 | 모니터링 알림 + 수동 재처리 |

### 스케일링 전략

```
[Description Analyzer] — 병목 지점 (OCR + LLM)
  → 가장 많은 Worker 인스턴스 할당
  → SQS ApproximateNumberOfMessages 기반 Auto Scaling

[Option/Notice Analyzer] — Rule 우선 처리로 LLM 부하 낮음
  → 기본 인스턴스로 충분

[Orchestrator/Aggregator] — 경량, DB I/O 위주
  → 최소 인스턴스
```

---

## 6. 장기 데이터 활용 전략

### 6.1 즉시 활용 (검수 완료 직후)

| 활용처 | 데이터 | 효과 |
|--------|--------|------|
| **외부 채널 연동** | extractedAttributes + optionSuggestions | 채널별 필수 속성 자동 매핑 |
| **검수 자동승인** | confidence 기반 판정 | 검수 처리 시간 단축 |
| **셀러 피드백** | 반려 사유 자동 생성 | "소재 정보 누락, 제조국 미기재" 구체적 안내 |

### 6.2 중기 활용 (데이터 축적 후)

| 활용처 | 데이터 | 효과 |
|--------|--------|------|
| **검색 개선** | extractedAttributes | "면 소재" 검색 시 Description에 없어도 OCR 추출 속성으로 매칭 |
| **셀러 품질 점수** | 셀러별 confidence 평균 | 우수 셀러 자동승인 임계값 완화 |
| **동의어 사전 자동 확장** | 승인된 optionMappingSuggestion | Rule 기반 매핑 비율 점진적 증가 → LLM 비용 감소 |
| **카테고리 보정** | 추출 속성 vs 등록 카테고리 | 잘못된 카테고리 자동 탐지 |

### 6.3 장기 활용 (시스템 고도화)

| 활용처 | 데이터 | 효과 |
|--------|--------|------|
| **상품 추천** | extractedAttributes 벡터 유사도 | 소재/스타일/가격대 기반 유사 상품 |
| **중복 상품 탐지** | 상품명 + 브랜드 + 추출 속성 | 다른 셀러의 동일 상품 식별 |
| **가격 비교** | 중복 탐지 결과 + 가격 정보 | 동일 상품 가격 경쟁력 분석 |
| **트렌드 분석** | 시계열 extractedAttributes 집계 | "이번 시즌 린넨 소재 급증" 등 |
| **Few-shot 자동 갱신** | rawAnalysisJson + 검수자 보정 이력 | 프롬프트 개선 → confidence 향상 피드백 루프 |

### 6.4 피드백 루프 설계

```
[검수자 보정 이력 축적]
    │
    ├── LLM이 제안한 값 vs 검수자가 최종 확정한 값
    │   → 오차율 측정 → 프롬프트 개선 포인트 식별
    │
    ├── 승인된 매핑 → 동의어 사전 자동 추가
    │   → Rule 기반 처리 비율 증가 → LLM 비용 감소
    │
    └── 셀러별 보정 패턴 → 셀러 교육 자료 자동 생성
        → "귀사 상품의 80%에서 소재 정보가 누락됩니다" 등
```

---

## 7. ProductGroupStatus 확장

### 현재 상태

```
DRAFT → PROCESSING → ACTIVE / REJECTED
```

### 제안 상태

```
DRAFT → PROCESSING → ACTIVE / REJECTED / PENDING_REVIEW (신규)
                                            │
                                            ├─ 검수자 승인 → ACTIVE
                                            └─ 검수자 반려 → REJECTED
```

**PENDING_REVIEW 도입 이유**:
- confidence가 중간인 상품을 즉시 반려하지 않고 사람이 확인
- 자동 보강이 적용되었지만 검수자 확인이 필요한 케이스
- 어드민에서 PENDING_REVIEW 상품 목록을 조회하여 일괄 처리

---

## 8. 기존 구조 대비 변경 영향도

### 유지하는 것

| 항목 | 설명 |
|------|------|
| `InspectionOutbox` | Outbox 패턴 유지, Scheduler relay 역할 유지 |
| SQS 기반 비동기 처리 | 큐만 재구성, 처리 패턴은 동일 |
| `ProductGroupActivatedEvent` | 검수 통과 → 외부 연동 트리거 유지 |

### 변경/추가하는 것

| 항목 | 변경 내용 |
|------|----------|
| SQS 큐 | 3개 → 5개 (orchestration, description, option, notice, aggregation) |
| Worker (SQS Consumer) | 3개 → 5개 (유형별 분리) |
| 판정 로직 | 점수 기반 이진 → confidence 기반 3분기 |
| 도메인 추가 | `productintelligence` (ProductProfile Aggregate) |
| 상태 추가 | `PENDING_REVIEW` |

### 제거하는 것

| 항목 | 사유 |
|------|------|
| `InspectionScorer` 인터페이스 | Analyzer Worker가 대체 |
| Scoring → Enhancement → Verification 순차 파이프라인 | 병렬 Analyzer + Aggregator로 대체 |
| `InspectionResult.PASSING_SCORE` (70점 하드코딩) | confidence 기반 동적 임계값으로 대체 |

---

## 9. 단계별 구현 로드맵

### 1단계: 기반 구축

```
- ProductProfile 도메인 설계 및 구현
- ProductGroupStatus에 PENDING_REVIEW 추가
- SQS 큐 5개 구성 (Terraform)
- Orchestrator Worker 구현
- Aggregator Worker 기본 로직 구현
```

### 2단계: Option Analyzer

```
- Rule 기반 자동 매핑 구현 (동의어 사전)
- LLM 보조 매핑 추가
- 자동 매핑 → SellerOptionValue 업데이트 로직
→ 즉시 효과: 캐노니컬 미매핑 상품 자동 처리
```

### 3단계: Notice Analyzer

```
- 필수 필드 검증 로직 (Rule 기반)
- Description 텍스트 파싱 + LLM 추출
- 고시정보 자동 보강 → ProductNoticeEntry 생성
→ 셀러 부담 감소, 법적 리스크 감소
```

### 4단계: Description Analyzer (OCR 포함)

```
- OCR API 연동 (CLOVA OCR / Amazon Textract)
- 멀티모달 LLM 분석 프롬프트 설계
- extractedAttributes 구조화 저장
→ ProductProfile 데이터 품질 향상
```

### 5단계: 피드백 루프 + 고도화

```
- Human-Review 결과 → Few-shot 자동 갱신
- 동의어 사전 자동 확장
- 셀러 품질 점수(Seller Quality Index) 도입
- 자동승인 임계값 동적 조정
```

---

## 10. 레퍼런스

### 국내 커머스

| 회사 | 사례 | 참고 포인트 |
|------|------|------------|
| 무신사 | SageMaker 이미지 자동 검수 | 비동기 파이프라인 + 자동 분류 |
| 카카오스타일 | Bedrock + Claude 3 멀티모달 | 텍스트+이미지 동시 분석, Human-in-the-loop |
| 네이버쇼핑 | TensorFlow 카테고리 자동 분류 | 상품명+이미지 멀티모달 분류 |

### 해외 커머스

| 회사 | 사례 | 참고 포인트 |
|------|------|------------|
| Amazon AutoKnow | 상품 지식그래프 자동 구축 | Data Imputation = 고시정보 보완, Data Cleaning = 옵션 정규화 |
| Amazon PAM | 이미지 기반 속성 추출 | OCR + 시각 객체 감지 + 텍스트 Transformer 융합 |
| Amazon Catalog | GenAI 카탈로그 보강 | 멀티 소스 퓨전, QA 파이프라인, 판매자 80% 수용률 |
| Shopify | AI Agent 택소노미 자동화 | 동등성 탐지 에이전트 = 옵션 매핑, 85% 수용률 |

### 논문

| 제목 | 핵심 |
|------|------|
| Using LLMs for Extraction and Normalization of Product Attribute Values (2024) | GPT-4 Few-shot 속성 추출, 정규화 98% 정확도 |
| PAE: LLM-based Product Attribute Extraction for Fashion (2024) | 패션 도메인 특화 속성 추출 |
| PRAISE: Enhancing Product Descriptions with LLM-Driven Insights (2025) | 리뷰-설명 불일치 탐지 |
