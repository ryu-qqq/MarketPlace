---
name: shadow-test
description: legacy-flow 분석 결과 기반 Shadow Traffic 테스트 케이스(YAML) 자동 생성. 모든 시나리오 커버.
context: fork
agent: shadow-test-generator
allowed-tools: Read, Write, Glob, Grep
---

# /shadow-test

`/legacy-flow` 분석 결과를 기반으로 **Shadow Traffic 테스트 케이스 YAML**을 자동 생성합니다.

## 사용법

```bash
/shadow-test web:UserController.fetchAddressBook
/shadow-test web:UserController                    # Controller 전체 엔드포인트
/shadow-test shipping-address                      # 도메인명으로 직접 지정
```

## 입력 형식

| 형식 | 설명 |
|------|------|
| `{Controller}.{method}` | 단일 엔드포인트 |
| `{Controller}` | Controller 전체 엔드포인트 |
| `{domain}` | 도메인명 직접 지정 |

## 전제조건

`/legacy-flow` 분석 문서가 존재해야 함:
- `claudedocs/legacy-flows/{web|admin}/{Controller}_{method}.md`

## 출력

```
tools/shadow-traffic/test-cases/{domain}.yml
```

## 생성 시나리오

| 카테고리 | 시나리오 |
|----------|----------|
| 정상 조회 | 존재하는 ID, 목록 조회, 검색어 조회 |
| 에러 | 존재하지 않는 ID (404), 잘못된 파라미터 (400) |
| 인증 | 인증 필요 엔드포인트의 auth 필드 설정 |
| 경계값 | 빈 목록, 최대 개수, 특수문자 검색 |
| Command | POST/PUT/DELETE의 structure_only/status_only 비교 |

## 다음 단계

테스트 케이스 생성 후:
```bash
/shadow-verify   # 로컬 Docker 환경에서 검증 실행
```
