# QnA E2E 통합 테스트 시나리오

## 대상 엔드포인트

| Method | Path | 설명 | 응답 |
|--------|------|------|------|
| GET | /api/v1/market/qnas | QnA 목록 조회 | 200 |
| GET | /api/v1/market/qnas/{qnaId} | QnA 상세 조회 | 200 |
| POST | /api/v1/market/qnas/{qnaId}/answers | QnA 답변 등록 | 201 |
| POST | /api/v1/market/qnas/{qnaId}/close | QnA 종결 | 204 |

## 상태 머신

```
PENDING → ANSWERED (answer)
ANSWERED → CLOSED (close)
ANSWERED → PENDING (addFollowUp, 내부 로직)
```

## 테스트 파일 구성

### QnaQueryE2ETest (10개)

| ID | 시나리오 | 우선순위 |
|----|---------|---------|
| Q1 | 셀러별 QnA 목록 조회 - PENDING 필터 | P0 |
| Q2 | 셀러별 QnA 목록 조회 - ANSWERED 필터 | P0 |
| Q3 | 셀러별 QnA 목록 조회 - 상태 필터 없음 (전체) | P0 |
| Q4 | 셀러별 QnA 목록 조회 - 빈 결과 | P0 |
| Q5 | 셀러별 QnA 목록 조회 - 페이징 (page=0, size=1) | P1 |
| Q6 | QnA 상세 조회 - PENDING (답변 없음) | P0 |
| Q7 | QnA 상세 조회 - ANSWERED (답변 포함) | P0 |
| Q8 | QnA 상세 조회 - 존재하지 않는 ID → 404 | P0 |
| Q9 | QnA 상세 조회 - replies 대댓글 포함 | P1 |
| Q10 | 목록 조회 후 상세 조회 일관성 검증 | P2 |

### QnaCommandE2ETest (12개)

| ID | 시나리오 | 우선순위 |
|----|---------|---------|
| C1 | PENDING → 답변 등록 → ANSWERED + reply 생성 | P0 |
| C2 | 답변 등록 시 QnaOutbox 생성 확인 (status=PENDING, type=ANSWER) | P0 |
| C3 | 답변 등록 시 대댓글 (parentReplyId 지정) | P1 |
| C4 | ANSWERED 상태에서 답변 시도 → 400 | P0 |
| C5 | CLOSED 상태에서 답변 시도 → 400 | P0 |
| C6 | 존재하지 않는 QnA에 답변 → 404 | P0 |
| C7 | content 빈 문자열로 답변 → 400 (Validation) | P0 |
| C8 | ANSWERED → close → CLOSED | P0 |
| C9 | PENDING → close 시도 → 400 | P0 |
| C10 | CLOSED → close 시도 → 400 | P0 |
| C11 | 존재하지 않는 QnA close → 404 | P0 |
| C12 | 답변 등록 후 상세 조회로 replies 확인 | P0 |

### QnaFlowE2ETest (6개)

| ID | 시나리오 | 우선순위 |
|----|---------|---------|
| FLOW-1 | 전체 흐름: 생성 → 목록 조회 → 답변 → 상세 조회 → 종결 | P0 |
| FLOW-2 | 다건 QnA 생성 → 셀러별 필터 조회 → 각각 답변 → 목록 재조회 | P0 |
| FLOW-3 | 답변 → Outbox 확인 → 종결 → 재답변 불가 확인 | P0 |
| FLOW-4 | 답변 → 추가 질문(DB 직접) → PENDING 복귀 → 재답변 → ANSWERED | P1 |
| FLOW-5 | 여러 QnA 타입(PRODUCT, SHIPPING, ORDER) 생성 → 조회 | P1 |
| FLOW-6 | QnA 목록 페이징 + totalCount 정합성 | P2 |

## Fixture 설계

```java
// 필요 Repository
@Autowired QnaJpaRepository qnaRepository;
@Autowired QnaReplyJpaRepository qnaReplyRepository;
@Autowired QnaOutboxJpaRepository qnaOutboxRepository;

// 삭제 순서 (FK 제약)
qnaReplyRepository.deleteAll();
qnaOutboxRepository.deleteAll();
qnaRepository.deleteAll();
```

## 주의사항

- close 응답: 204 No Content (body 없음)
- FLOW-4의 addFollowUp은 REST API 없음 → DB 직접 조작으로 시뮬레이션
- idempotencyKey 중복 방지: 테스트별 고유 externalQnaId 사용
