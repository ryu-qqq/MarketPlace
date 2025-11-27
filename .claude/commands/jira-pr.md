# /jira-pr - PR 생성 + Jira In Review 전환

**목적**: TDD 완료 후 PR 생성 및 Jira 상태를 In Review로 전환

**사용법**:
```bash
/jira-pr
/jira-pr MEMBER-001  (Issue Key 명시)
```

---

## 📋 작업 순서

### 1. 현재 브랜치 및 Issue 감지

**Git 브랜치에서 Issue Key 추출**:
```bash
current_branch=$(git branch --show-current)
# 예: feature/MEMBER-001-domain

# Issue Key 추출 (정규표현식)
issue_key=$(echo $current_branch | grep -oE '[A-Z]+-[0-9]+')
# 결과: MEMBER-001

# Layer 추출
layer=$(echo $current_branch | sed 's/.*-//')
# 결과: domain
```

**검증**:
- ✅ feature 브랜치인지 확인
- ✅ Issue Key 패턴 일치 (`[A-Z]+-[0-9]+`)
- ✅ 브랜치가 원격에 존재하는지 확인

**에러 케이스**:
```
❌ feature 브랜치가 아닙니다: main
❌ Issue Key를 찾을 수 없습니다: my-custom-branch
❌ 원격 브랜치가 없습니다 (git push 먼저 실행하세요)
```

### 2. TDD Plan 완료 확인

**Plan 파일 읽기**:
```bash
plan_file="docs/prd/plans/${issue_key}-${layer}-plan.md"

if [ ! -f "$plan_file" ]; then
    echo "❌ Plan 파일을 찾을 수 없습니다: $plan_file"
    exit 1
fi
```

**체크박스 완료 검증**:
```bash
# 전체 체크박스 개수
total_checkboxes=$(grep -c '- \[ \]' "$plan_file" || echo 0)
unchecked=$(grep -c '- \[ \]' "$plan_file" || echo 0)
checked=$(grep -c '- \[x\]' "$plan_file" || echo 0)

completion_rate=$(echo "scale=2; $checked / ($checked + $unchecked) * 100" | bc)

echo "완료율: $completion_rate% ($checked/$total)"

if [ "$unchecked" -gt 0 ]; then
    echo "⚠️ 미완료 항목이 ${unchecked}개 남아있습니다"
    echo "계속하시겠습니까? (y/N):"
    read -r answer.txt
    if [ "$answer" != "y" ]; then
        exit 1
    fi
fi
```

### 3. 테스트 실행 및 검증

**Gradle 테스트 실행**:
```bash
echo "🧪 테스트 실행 중..."
./gradlew test

if [ $? -ne 0 ]; then
    echo "❌ 테스트 실패"
    echo "PR 생성을 중단합니다"
    exit 1
fi
```

**ArchUnit 테스트 검증**:
```bash
# Layer별 ArchUnit 테스트 패키지
case $layer in
    "domain")
        test_package="domain/src/test/java/.../architecture/"
        ;;
    "application")
        test_package="application/src/test/java/.../architecture/"
        ;;
    "persistence")
        test_package="adapter-out/persistence-mysql/src/test/java/.../architecture/"
        ;;
    "rest-api")
        test_package="adapter-in/rest-api/src/test/java/.../architecture/"
        ;;
esac

# ArchUnit 테스트 실행
./gradlew test --tests "*ArchTest"
```

### 4. Git Push

**원격 브랜치 최신화**:
```bash
echo "📤 원격 브랜치 push 중..."
git push origin $current_branch

if [ $? -ne 0 ]; then
    echo "❌ Push 실패"
    echo "Conflict를 해결하거나 git push --force-with-lease를 시도하세요"
    exit 1
fi
```

### 5. PR 생성 (GitHub CLI)

**PR 제목 및 본문 생성**:
```bash
# PR 제목
pr_title="[${issue_key}] ${task_title}"
# 예: [MEMBER-001] Domain Layer 구현

# PR 본문 (Plan 파일 내용 포함)
pr_body=$(cat <<EOF
## 📋 Summary

${task_purpose}

## ✅ TDD Plan 완료

\`\`\`markdown
$(cat $plan_file | grep -A 100 "## TDD 사이클 체크리스트")
\`\`\`

## 🧪 테스트 결과

- Unit Tests: ✅ 통과
- ArchUnit Tests: ✅ 통과
- 커버리지: ${coverage}%

## 🔗 Related

- Jira: ${jira_url}
- Task: docs/prd/tasks/${issue_key}.md
- Plan: docs/prd/plans/${issue_key}-${layer}-plan.md

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
EOF
)
```

**GitHub PR 생성**:
```bash
gh pr create \
  --base main \
  --head $current_branch \
  --title "$pr_title" \
  --body "$pr_body" \
  --label "${layer},tdd,hexagonal" \
  --assignee "@me"

# PR URL 추출
pr_url=$(gh pr view $current_branch --json url -q .url)
echo "✅ PR 생성 완료: $pr_url"
```

### 6. Jira 상태 업데이트

**Jira Transition: In Progress → In Review**:
```bash
POST /rest/api/3/issue/${issueKey}/transitions
Content-Type: application/json

{
  "transition": {
    "id": "31"  // In Progress → In Review
  }
}
```

**Jira Issue에 PR URL 추가**:
```bash
POST /rest/api/3/issue/${issueKey}/remotelink
Content-Type: application/json

{
  "object": {
    "url": "${pr_url}",
    "title": "Pull Request #${pr_number}",
    "icon": {
      "url16x16": "https://github.com/favicon.ico"
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
          {"type": "text", "text": "✅ TDD 완료 및 PR 생성"},
          {"type": "hardBreak"},
          {"type": "text", "text": "PR: ${pr_url}"},
          {"type": "hardBreak"},
          {"type": "text", "text": "완료율: ${completion_rate}%"}
        ]
      }
    ]
  }
}
```

### 7. 로컬 Task 파일 업데이트

**Task 파일에 PR 정보 추가**:
```markdown
# MEMBER-001: Domain Layer 구현

**Epic**: 회원 관리 시스템
**Layer**: Domain
**브랜치**: feature/MEMBER-001-domain
**Jira URL**: https://your-domain.atlassian.net/browse/MEMBER-001
**상태**: In Review  ← 업데이트
**시작일**: 2025-11-14
**PR 생성일**: 2025-11-15  ← 추가
**PR URL**: https://github.com/org/repo/pull/123  ← 추가
**담당자**: John Doe

---
```

---

## 🚀 실행 예시

### 정상 실행

**입력**:
```bash
/jira-pr
```

**출력**:
```
🔍 현재 브랜치 확인 중...
   ✅ feature/MEMBER-001-domain
   └─ Issue Key: MEMBER-001
   └─ Layer: domain

📋 TDD Plan 완료 확인 중...
   ✅ docs/prd/plans/MEMBER-001-domain-plan.md
   └─ 완료율: 100% (20/20)

🧪 테스트 실행 중...
   ✅ Unit Tests: 15 passed
   ✅ ArchUnit Tests: 5 passed
   └─ 총 소요 시간: 12.3초

📤 원격 브랜치 push 중...
   ✅ feature/MEMBER-001-domain → origin

📝 PR 생성 중...
   ✅ PR #123 생성 완료
   └─ https://github.com/org/repo/pull/123

📡 Jira 상태 업데이트 중...
   ✅ In Progress → In Review
   ✅ PR URL 추가 완료
   ✅ Comment 추가 완료

📝 로컬 파일 업데이트 중...
   ✅ Task 파일에 PR 정보 추가

✅ PR 생성 완료!

🔗 다음 단계:
   1. 코드 리뷰 요청
   2. CI/CD 통과 확인
   3. PR 머지 후 /jira-done MEMBER-001 실행
```

### Plan 미완료

**입력**:
```bash
/jira-pr
```

**출력**:
```
🔍 현재 브랜치 확인 중...
   ✅ feature/MEMBER-001-domain

📋 TDD Plan 완료 확인 중...
   ⚠️ 완료율: 75% (15/20)
   └─ 미완료 항목: 5개

미완료 항목:
   - [ ] shouldValidateEmailFormat() 테스트 작성
   - [ ] EmailFixture 정리
   - [ ] shouldThrowExceptionWhenInvalidPassword() 테스트
   - [ ] PasswordFixture 정리
   - [ ] Member ArchUnit 테스트

계속하시겠습니까? (y/N):
```

### 테스트 실패

**입력**:
```bash
/jira-pr
```

**출력**:
```
🔍 현재 브랜치 확인 중...
   ✅ feature/MEMBER-001-domain

📋 TDD Plan 완료 확인 중...
   ✅ 완료율: 100%

🧪 테스트 실행 중...
   ❌ 테스트 실패

실패한 테스트:
   - MemberTest.shouldCreateMemberWithValidData()
   - EmailTest.shouldValidateEmailFormat()

에러 메시지:
   Expected: <valid@email.com>
   Actual: <null>

PR 생성을 중단합니다.
먼저 테스트를 수정하세요.
```

---

## ⚙️ GitHub PR 템플릿 설정

**`.github/pull_request_template.md`**:
```markdown
## 📋 Summary

<!-- Task 목적 -->

## ✅ TDD Plan

<!-- Plan 파일 체크리스트 -->

## 🧪 테스트 결과

- [ ] Unit Tests 통과
- [ ] ArchUnit Tests 통과
- [ ] 커버리지 > 80%

## 🔗 Related

- Jira:
- Task:
- Plan:

## 📸 Screenshots (if applicable)

<!-- 스크린샷 첨부 -->

---

🤖 Generated with [Claude Code](https://claude.com/claude-code)

Co-Authored-By: Claude <noreply@anthropic.com>
```

---

## 🔄 워크플로우 통합

### TDD 완료 → PR 생성 전체 흐름

```bash
# 1. TDD 완료 확인
/kb/domain/go
→ Plan 파일 모든 체크박스 [x]

# 2. PR 생성
/jira-pr
→ 테스트 실행
→ PR 생성
→ Jira In Review

# 3. 코드 리뷰
→ GitHub에서 리뷰어 assign
→ 리뷰어가 코멘트/승인

# 4. PR 머지
→ Squash and Merge 또는 Merge Commit

# 5. 완료 처리
/jira-done MEMBER-001
→ Jira Done
```

---

## ⚠️ 에러 처리

### Git 에러

**Uncommitted Changes**:
```
❌ 커밋되지 않은 변경사항이 있습니다
   - git status로 확인하세요
   - 변경사항을 커밋하세요
```

**Merge Conflict**:
```
❌ Push 실패: Conflict
   - main 브랜치를 merge하세요:
     git fetch origin
     git merge origin/main
   - Conflict 해결 후 다시 시도하세요
```

### GitHub CLI 에러

**gh 미설치**:
```
❌ GitHub CLI(gh)가 설치되지 않았습니다
   - 설치: brew install gh
   - 인증: gh auth login
```

**권한 부족**:
```
❌ Repository에 대한 권한이 없습니다
   - gh auth status로 인증 확인
   - Repository owner에게 권한 요청
```

### Jira API 에러

**Transition 불가능**:
```
❌ Jira 상태 전환 실패
   - 현재 상태: In Progress
   - 허용된 전환: Done만 가능
   - Jira에서 수동으로 전환하거나 Workflow 확인 필요
```

---

## 🎯 옵션 플래그

### --skip-test

테스트 건너뛰기 (비권장):
```bash
/jira-pr --skip-test
```

### --skip-jira

Jira 업데이트 없이 PR만 생성:
```bash
/jira-pr --skip-jira
```

### --draft

Draft PR 생성:
```bash
/jira-pr --draft
```

---

## 🎯 핵심 원칙

1. **품질 보증**: 테스트 통과 필수 (ArchUnit 포함)
2. **완료 검증**: Plan 파일 완료율 확인
3. **자동 문서화**: PR 본문에 Plan 내용 자동 포함
4. **추적성**: Jira와 GitHub 양방향 링크
5. **안전성**: 테스트 실패 시 PR 생성 차단

---

## 📚 관련 문서

- [GitHub CLI PR 생성](https://cli.github.com/manual/gh_pr_create)
- [Jira Remote Links](https://developer.atlassian.com/cloud/jira/platform/rest/v3/api-group-issue-remote-links/)
- [Git Branch Protection](https://docs.github.com/en/repositories/configuring-branches-and-merges-in-your-repository/managing-protected-branches)
