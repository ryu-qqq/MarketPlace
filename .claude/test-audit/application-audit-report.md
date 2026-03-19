# Application 레이어 테스트 감사 리포트

## 전체 통계

| 항목 | 수치 |
|------|------|
| 총 소스 구현 클래스 | 712개 |
| 테스트 클래스 | 379개 |
| 전체 커버리지 | 53% |
| 테스트 0% 패키지 | 12개 |
| testFixtures 미존재 패키지 | 19개 |
| HIGH 갭 | 68개 |
| MEDIUM 갭 | 107개 |

## 커버리지 0% 패키지 (전체 미테스트)

| 패키지 | 소스 수 | 우선순위 |
|--------|--------|---------|
| settlement | 15 | HIGH - 신규 도입, 비즈니스 크리티컬 |
| inboundorder | 10 | HIGH - 외부 주문 수신 핵심 |
| inboundproduct | 18 | HIGH - 외부 상품 수신 핵심 |
| legacyconversion | 29 | HIGH - 레거시 이관 배치 |
| productintelligence | 26 | HIGH - AI 분석 파이프라인 |
| outboundseller | 6 | MEDIUM |
| channeloptionmapping | 9 | MEDIUM |
| category | 4 | MEDIUM |
| legacyseller | 1 | LOW |
| legacycommoncode | 1 | LOW |
| legacyshipment | 2 | LOW |
| sellersaleschannel | 1 | LOW |

## 커버리지 10% 미만 패키지

| 패키지 | 소스 | 테스트 | 커버리지 |
|--------|------|--------|---------|
| selleradmin | 31 | 2 | 6% |
| sellerapplication | 13 | 1 | 8% |
| legacy | 52 | 8 | 15% |
| outboundsync | 21 | 3 | 14% |
| shipment | 20 | 4 | 20% |
| claimsync | 11 | 3 | 27% |

## 커버리지 100% 패키지 (18개)

adminmenu, brand, canonicaloption, claimhistory, commoncodetype, imagevariant, inboundbrandmapping, inboundcategorymapping, notice, outboundproductimage, productgroupimage, productnotice, saleschannel, saleschannelbrand, saleschannelcategory, selleroption, shop, uploadsession
