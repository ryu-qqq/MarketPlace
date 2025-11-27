# /jira-done - PR 머지 후 Jira Done 처리

**목적**: PR 머지 확인 후 Jira Issue를 Done으로 완료 처리

**사용법**:
```bash
/jira-done <issue-key>
/jira-done MEMBER-001
```

---

## 📋 작업 순서

### 1. Issue Key 감지 및 검증

**Issue Key 추출 (현재 브랜치 또는 인자)**:
```bash
# 인자로 받은 경우
if [ -n "$1" ]; then
    issue_key=$1
else
    # 현재 브랜치에서 추출
    current_branch=$(git branch --show-current)
    issue_key=$(echo $current_branch | grep -oE '[A-Z]+-[0-9]+')
fi

if [ -z "$issue_key" ]; then
    echo "❌ Issue Key를 찾을 수 없습니다"
    echo "사용법: /jira-done MEMBER-001"
    exit 1
fi
```

### 2. Jira Issue 조회

**Jira API 호출**:
```bash
GET /rest/api/3/issue/${issueKey}
Authorization: Basic {base64(email:api_token)}
```

**확인 사항**:
- ✅ Issue 존재 여부
- ✅ 현재 상태 (`In Review` 또는 `In Progress`)
- ✅ PR URL 존재 여부 (Remote Link)

**에러 케이스**:
```
❌ Issue를 찾을 수 없음: MEMBER-001
❌ Issue가 이미 완료됨: MEMBER-001 (상태: Done)
❌ PR이 생성되지 않았습니다 (/jira-pr을 먼저 실행하세요)
```

### 3. PR 머지 확인

**GitHub PR 상태 조회**:
```bash
# Task 파일에서 PR URL 추출
pr_url=$(grep "PR URL:" docs/prd/tasks/${issue_key}.md | awk '{print $3}')

# PR 번호 추출
pr_number=$(echo $pr_url | grep -oE '[0-9]+$')

# GitHub CLI로 PR 상태 확인
pr_state=$(gh pr view $pr_number --json state -q .state)
pr_merged=$(gh pr view $pr_number --json merged -q .merged)

if [ "$pr_merged" != "true" ]; then
    echo "❌ PR이 아직 머지되지 않았습니다"
    echo "PR: $pr_url"
    echo "상태: $pr_state"
    echo ""
    echo "계속하시겠습니까? (y/N):"
    read -r answer.txt
    if [ "$answer" != "y" ]; then
        exit 1
    fi
fi
```

**Merge 정보 수집**:
```bash
merged_at=$(gh pr view $pr_number --json mergedAt -q .mergedAt)
merged_by=$(gh pr view $pr_number --json mergedBy -q .mergedBy.login)
merge_commit=$(gh pr view $pr_number --json mergeCommit -q .mergeCommit.oid)

echo "✅ PR 머지 확인"
echo "   - 머지 시각: $merged_at"
echo "   - 머지한 사람: $merged_by"
echo "   - Merge Commit: $merge_commit"
```

### 4. Jira 상태 업데이트 (In Review → Done)

**Jira Transition API 호출**:
```bash
POST /rest/api/3/issue/${issueKey}/transitions
Content-Type: application/json

{
  "transition": {
    "id": "41"  // In Review → Done (Transition ID)
  },
  "fields": {
    "resolution": {
      "name": "Done"
    }
  }
}
```

**Jira Comment 추가**:
```bash
POST /rest/api/3/issue/${issueKey}/comment
Content-Type: application/json

{
  "body": {
    "type": "doc",
    "version": 1,
    "content": [
      {
        "type": "paragraph",
        "content": [
          {"type": "text", "text": "✅ PR 머지 완료 및 Issue 종료"},
          {"type": "hardBreak"},
          {"type": "text", "text": "PR: ${pr_url}"},
          {"type": "hardBreak"},
          {"type": "text", "text": "Merged by: ${merged_by}"},
          {"type": "hardBreak"},
          {"type": "text", "text": "Merged at: ${merged_at}"}
        ]
      }
    ]
  }
}
```

### 5. 로컬 Task 파일 업데이트

**Task 파일에 완료 정보 추가**:
```markdown
# MEMBER-001: Domain Layer 구현

**Epic**: 회원 관리 시스템
**Layer**: Domain
**브랜치**: feature/MEMBER-001-domain
**Jira URL**: https://your-domain.atlassian.net/browse/MEMBER-001
**상태**: Done  ← 업데이트
**시작일**: 2025-11-14
**PR 생성일**: 2025-11-15
**완료일**: 2025-11-16  ← 추가
**PR URL**: https://github.com/org/repo/pull/123
**Merge Commit**: abc123def456  ← 추가
**담당자**: John Doe

---
```

### 6. Git 정리 (선택)

**브랜치 삭제 여부 확인**:
```bash
echo "🗑️ 브랜치를 삭제하시겠습니까?"
echo "   - 로컬: feature/${issue_key}-${layer}"
echo "   - 원격: origin/feature/${issue_key}-${layer}"
echo ""
echo "삭제하시겠습니까? (y/N):"
read -r answer.txt

if [ "$answer" = "y" ]; then
    # 로컬 브랜치 삭제
    git branch -D feature/${issue_key}-${layer}

    # 원격 브랜치 삭제
    git push origin --delete feature/${issue_key}-${layer}

    echo "✅ 브랜치 삭제 완료"
else
    echo "ℹ️ 브랜치 유지"
fi
```

**main 브랜치로 체크아웃 및 업데이트**:
```bash
echo "🔄 main 브랜치로 전환 중..."
git checkout main
git pull origin main

echo "✅ main 브랜치 최신화 완료"
```

### 7. 통계 수집 (LangFuse)

**TDD 메트릭 계산**:
```bash
# Plan 파일에서 사이클 수 추출
plan_file="docs/prd/plans/${issue_key}-${layer}-plan.md"
cycle_count=$(grep -c "### [0-9]️⃣" "$plan_file")

# Git log에서 커밋 수 계산
start_commit=$(git log --grep="chore: ${issue_key} 브랜치 시작" --format="%H" | head -1)
merge_commit=$(gh pr view $pr_number --json mergeCommit -q .mergeCommit.oid)
commit_count=$(git rev-list ${start_commit}..${merge_commit} --count)

# 소요 시간 계산
start_time=$(git log $start_commit --format="%ct" | head -1)
end_time=$(git log $merge_commit --format="%ct" | head -1)
duration=$((end_time - start_time))
duration_hours=$(echo "scale=2; $duration / 3600" | bc)

echo "📊 TDD 통계:"
echo "   - TDD 사이클: $cycle_count"
echo "   - 총 커밋 수: $commit_count"
echo "   - 소요 시간: ${duration_hours}시간"
```

**LangFuse 로깅**:
```bash
python3 .claude/scripts/log-to-langfuse.py \
    --event-type "task_completed" \
    --data "{
        \"project\": \"claude-spring-standards\",
        \"issue_key\": \"${issue_key}\",
        \"layer\": \"${layer}\",
        \"tdd_cycles\": $cycle_count,
        \"commit_count\": $commit_count,
        \"duration_hours\": $duration_hours,
        \"pr_url\": \"${pr_url}\",
        \"completed_at\": \"$(date -u +%Y-%m-%dT%H:%M:%SZ)\"
    }"
```

---

## 🚀 실행 예시

### 정상 실행

**입력**:
```bash
/jira-done MEMBER-001
```

**출력**:
```
🔍 Jira Issue 조회 중...
   ✅ MEMBER-001: Domain Layer 구현
   └─ 상태: In Review

📋 Task 파일 확인 중...
   ✅ docs/prd/tasks/MEMBER-001.md
   └─ PR URL: https://github.com/org/repo/pull/123

🔗 PR 머지 확인 중...
   ✅ PR #123 머지 완료
   └─ 머지 시각: 2025-11-16T03:45:23Z
   └─ 머지한 사람: john-doe
   └─ Merge Commit: abc123def456

📡 Jira 상태 업데이트 중...
   ✅ In Review → Done
   ✅ Resolution: Done
   ✅ Comment 추가 완료

📝 로컬 파일 업데이트 중...
   ✅ Task 파일에 완료 정보 추가

📊 TDD 통계:
   - TDD 사이클: 5
   - 총 커밋 수: 23
   - 소요 시간: 4.5시간

🗑️ 브랜치를 삭제하시겠습니까?
   - 로컬: feature/MEMBER-001-domain
   - 원격: origin/feature/MEMBER-001-domain

삭제하시겠습니까? (y/N): y

✅ 브랜치 삭제 완료

🔄 main 브랜치로 전환 중...
   ✅ main 브랜치 최신화 완료

✅ Issue 완료 처리 성공!

🔗 다음 단계:
   - 다음 Task 시작: /jira-start MEMBER-002
   - Epic 진행률 확인: Jira에서 MEMBER Epic 확인
```

### PR 미머지 경고

**입력**:
```bash
/jira-done MEMBER-001
```

**출력**:
```
🔍 Jira Issue 조회 중...
   ✅ MEMBER-001: Domain Layer 구현

🔗 PR 머지 확인 중...
   ❌ PR이 아직 머지되지 않았습니다
   └─ PR: https://github.com/org/repo/pull/123
   └─ 상태: OPEN

⚠️ PR을 먼저 머지하는 것을 권장합니다.

계속하시겠습니까? (y/N):
```

### Issue 이미 완료됨

**입력**:
```bash
/jira-done MEMBER-001
```

**출력**:
```
🔍 Jira Issue 조회 중...
   ✅ MEMBER-001: Domain Layer 구현
   └─ 상태: Done (이미 완료됨)

ℹ️ 이 Issue는 이미 완료 처리되었습니다.

브랜치 정리만 수행하시겠습니까? (y/N):
```

---

## 🔄 워크플로우 통합

### 전체 워크플로우 (시작 → 완료)

```bash
# Phase 1: 기획
/create-prd "회원 관리 시스템"
/breakdown-prd docs/prd/member-management.md
/sync-to-jira docs/prd/tasks/

# Phase 2: 개발 시작
/jira-start MEMBER-001
→ 브랜치: feature/MEMBER-001-domain
→ Plan: docs/prd/plans/MEMBER-001-domain-plan.md
→ Jira: To Do → In Progress

# Phase 3: TDD 수행
/kb/domain/go
→ Red → Green → Refactor → Tidy
→ Plan 체크박스 [x]
→ 반복...

# Phase 4: PR 생성
/jira-pr
→ PR #123 생성
→ Jira: In Progress → In Review

# Phase 5: 코드 리뷰 & 머지
→ GitHub에서 리뷰 및 승인
→ PR 머지

# Phase 6: 완료 처리
/jira-done MEMBER-001
→ Jira: In Review → Done
→ 브랜치 정리
→ main 체크아웃

# Phase 7: 다음 Task
/jira-start MEMBER-002
→ Application Layer 시작
```

---

## ⚙️ Jira Resolution 설정

### Resolution Types

**일반적인 Resolution**:
- `Done`: 정상 완료
- `Won't Do`: 작업 취소
- `Duplicate`: 중복 작업
- `Cannot Reproduce`: 버그 재현 불가

**기본값**:
```json
{
  "resolution": {
    "name": "Done"
  }
}
```

---

## ⚠️ 에러 처리

### GitHub 에러

**PR 없음**:
```
❌ PR을 찾을 수 없습니다: #123
   - Task 파일에 PR URL이 없습니다
   - /jira-pr을 먼저 실행하세요
```

**gh CLI 인증 실패**:
```
❌ GitHub 인증 실패
   - gh auth login으로 재인증하세요
```

### Jira API 에러

**Transition 불가능**:
```
❌ Done으로 전환할 수 없습니다
   - 현재 상태: To Do
   - 허용된 전환: In Progress만 가능
   - 워크플로우: To Do → In Progress → In Review → Done
```

**Resolution 필드 없음**:
```
❌ Resolution 필드를 설정할 수 없습니다
   - Jira 프로젝트 설정 확인 필요
   - 관리자에게 문의하세요
```

### Git 에러

**브랜치 삭제 실패**:
```
❌ 브랜치 삭제 실패
   - 원격 브랜치가 이미 삭제되었습니다
   - 또는 삭제 권한이 없습니다
```

---

## 🎯 옵션 플래그

### --keep-branch

브랜치 유지 (삭제하지 않음):
```bash
/jira-done MEMBER-001 --keep-branch
```

### --skip-jira

Jira 업데이트 없이 로컬만 정리:
```bash
/jira-done MEMBER-001 --skip-jira
```

### --force

PR 미머지 상태에서도 강제 완료:
```bash
/jira-done MEMBER-001 --force
```

---

## 📊 완료 후 리포트

### Epic 진행률 계산

**Jira JQL 쿼리**:
```
project = MEMBER AND type = Task AND "Epic Link" = MEMBER
```

**통계 계산**:
```bash
total_tasks=$(jira list --jql "..." --json | jq length)
done_tasks=$(jira list --jql "... AND status = Done" --json | jq length)
progress=$(echo "scale=2; $done_tasks / $total_tasks * 100" | bc)

echo "Epic 진행률: $progress% ($done_tasks/$total_tasks)"
```

### LangFuse 대시보드

**추적 메트릭**:
- Task별 TDD 사이클 수
- Task별 소요 시간
- Layer별 평균 커밋 수
- 전체 Epic 완료 시간

---

## 🎯 핵심 원칙

1. **PR 머지 확인**: Done 처리 전 PR 머지 필수
2. **자동 정리**: 브랜치 삭제 및 main 업데이트
3. **메트릭 수집**: TDD 통계 자동 수집
4. **추적성**: Jira와 Git 완료 상태 동기화
5. **다음 단계 안내**: Epic 진행률 및 다음 Task 제안

---

## 📚 관련 문서

- [Jira Transitions](https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issues/#api-rest-api-3-issue-issueidorkey-transitions-post)
- [GitHub PR API](https://docs.github.com/en/rest/pulls/pulls)
- [Git Branch Cleanup](https://git-scm.com/book/en/v2/Git-Branching-Branch-Management)
