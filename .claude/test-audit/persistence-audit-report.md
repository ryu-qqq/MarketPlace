# Persistence 레이어 테스트 감사 리포트

## 전체 현황

| 항목 | 수치 |
|------|------|
| 소스 클래스 | 566개 (60개 패키지) |
| 테스트 클래스 | 284개 |
| 파일 기준 커버리지 | 50.2% |

## HIGH - 즉시 보완 필요 (11건)

| # | 패키지 | 소스 | 테스트 | Fixtures | 비고 |
|---|--------|------|--------|----------|------|
| 1 | settlement | 12 | 0 | X | 신규 도메인, 전체 미테스트 |
| 2 | canceloutbox | 7 | 0 | X | 전체 미테스트 |
| 3 | refundoutbox | 7 | 0 | X | 전체 미테스트 |
| 4 | exchangeoutbox | 7 | 0 | X | 전체 미테스트 |
| 5 | shipmentoutbox | 7 | 0 | X | 전체 미테스트 |
| 6 | claim | 5 | 0 | X | 전체 미테스트 |
| 7 | claimsync | 6 | 0 | X | 전체 미테스트 |
| 8 | inboundorder | 8 | 0 | O | 전체 미테스트 |
| 9 | ordermapping | 6 | 0 | X | 전체 미테스트 |
| 10 | composite/order | - | 0 | - | Adapter, Repository, ConditionBuilder 미테스트 |
| 11 | legacyconversion 신규 | 18 | 0 | - | Order 이관 관련 클래스 미테스트 |

## MEDIUM - 단기 보완 권장 (12건)

| # | 패키지 | 비고 |
|---|--------|------|
| 12 | outboundseller | 7개 클래스 전체 미테스트 + Fixtures 없음 |
| 13 | imagevariantsync | 6개 클래스 전체 미테스트 + Fixtures 없음 |
| 14 | adminmenu | ConditionBuilder, QueryDslRepository 미테스트 |
| 15 | brandmapping | QueryAdapter 미테스트 |
| 16 | categorymapping | QueryAdapter 미테스트 |
| 17 | order | Entity 4개 미테스트 |
| 18 | productgroup | CompositionProductConditionBuilder 미테스트 |
| 19 | outboundproductimage | QueryDslRepository 미테스트 |
| 20 | outboundsync | QueryDslRepository 미테스트 |
| 21 | cancel | E2E 있지만 단위 테스트 없음 |
| 22 | refund | E2E 있지만 단위 테스트 없음 |
