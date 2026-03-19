# Domain 레이어 테스트 감사 리포트

## 전체 통계

| 항목 | 수치 |
|------|------|
| 총 소스 클래스 | 794개 |
| 총 테스트 파일 | 282개 |
| 총 testFixtures 파일 | 48개 |
| 전체 커버리지 | 약 35% |
| 테스트 완전 부재 패키지 | 14개 |
| testFixtures 부재 패키지 | 8개 |

## 패키지별 커버리지 현황

| 패키지 | 소스 | 테스트 | Fixtures | 커버리지 | 상태 |
|--------|------|--------|----------|----------|------|
| common | 24 | 0 | O | 0% | MISSING_TEST (HIGH) |
| inboundproduct | 13 | 0 | O | 0% | MISSING_TEST (HIGH) |
| inboundorder | 8 | 0 | O | 0% | MISSING_TEST (HIGH) |
| inboundbrandmapping | 11 | 0 | O | 0% | MISSING_TEST (HIGH) |
| inboundcategorymapping | 11 | 0 | O | 0% | MISSING_TEST (HIGH) |
| inboundsource | 14 | 0 | O | 0% | MISSING_TEST (HIGH) |
| outboundproduct | 10 | 0 | O | 0% | MISSING_TEST (HIGH) |
| productintelligence | 22 | 0 | O | 0% | MISSING_TEST (HIGH) |
| legacy | 43 | 1 | X | 2% | 사실상 미테스트 |
| selleradmin | 23 | 1 | O | 4% | 사실상 미테스트 (HIGH) |
| category | 18 | 2 | X | 11% | 대부분 미테스트 |
| outboundsync | 7 | 1 | O | 14% | |
| cancel | 27 | 7 | O | 26% | outbox/event/query 전체 누락 |
| shipment | 21 | 6 | O | 29% | outbox/event/query 전체 누락 |
| settlement | 30 | 9 | O | 30% | query/event 전체 누락 |
| refund | 28 | 9 | O | 32% | outbox/event/query 전체 누락 |
| exchange | 30 | 13 | O | 43% | outbox 서브패키지 미테스트 |
| order | 30 | 24 | O | 80% | 일부 enum/exception 누락 |

## 테스트 전혀 없는 패키지 (14개)

`auth`, `claimsync`, `common`, `inboundbrandmapping`, `inboundcategorymapping`, `inboundorder`, `inboundproduct`, `inboundsource`, `legacyconversion`, `ordermapping`, `outboundproduct`, `outboundseller`, `productintelligence`, `sellersaleschannel`

## testFixtures 없는 패키지 (8개)

`auth`, `category`, `claimsync`, `imagevariantsync`, `legacy`, `ordermapping`, `outboundseller`, `productgroupimage`

## OMS 도메인 공통 패턴 갭

- cancel/refund/exchange/shipment 4개 도메인 모두 **outbox 서브패키지** 테스트 완전 부재
- 동일 4개 도메인의 **Domain Event 클래스** 전부 미테스트
- **OwnershipMismatchException** (Cancel/Refund/Exchange) 시나리오 없음

## 잘 된 테스트 (참조 패턴)

`CancelTest`, `ShipmentTest`, `SettlementTest`:
- `@Tag("unit")` + `@Nested` + 한글 `@DisplayName`
- forNew/reconstitute 양방향 검증
- 정상 상태전이 + 비허용 전이 예외
- 이벤트 발행 및 pollEvents 후 비워짐 검증
- 전체 흐름 통합 시나리오
