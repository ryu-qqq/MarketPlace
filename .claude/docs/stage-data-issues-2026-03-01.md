# Stage DB 내부 상품 데이터 이슈 리포트

**작성일**: 2026-03-01
**대상 환경**: Stage (localhost:13308 → staging-shared-mysql)
**데이터 기준**: inbound_products 39건 CONVERTED → product_groups 39건 생성

---

## 전체 데이터 현황

| 테이블 | 건수 | 상태 |
|--------|------|------|
| inbound_products | 39 | 전부 CONVERTED |
| product_groups | 39 | **전부 DRAFT** (ACTIVE 전환 안 됨) |
| products (SKU) | 201 | 전부 ACTIVE |
| product_group_images | 259 (active) | THUMBNAIL 39 + DETAIL 220 |
| product_group_descriptions | 39 | **전부 PUBLISH_READY** (CDN 미퍼블리시) |
| description_images | **0** | 없음 |
| seller_option_groups | 39 | 전부 "사이즈" 단일옵션 |
| seller_option_values | 201 | 정상 |
| product_option_mappings | 201 | 1:1 매핑 |
| product_notices | 39 | 카테고리별 정상 |
| product_notice_entries | 298 | **전부 "상세설명 참고"** 기본값 |
| image_upload_outboxes | 259 | PENDING 257 + PROCESSING 2 (stuck) |
| image_transform_outboxes | 0 | 미생성 |
| image_variants | 0 | 미생성 |
| intelligence_outboxes | 77 | SENT 3 + PENDING 74 |
| product_profiles | 3 | 전부 ANALYZING (completed=0/3) |
| product_group_inspection_outboxes | 0 | 미생성 |

---

## 이슈 1: 이미지 업로드 파이프라인 멈춤 (Critical)

### 증상
- `image_upload_outboxes`: 259건 중 257건 PENDING, 2건 PROCESSING(stuck)
- `product_group_images.uploaded_url`: 259건 전부 **NULL**
- `image_transform_outboxes`: 0건 (업로드 완료 후 생성되어야 함)
- `image_variants`: 0건

### 파이프라인 흐름
```
① image_upload_outboxes (PENDING)
   ↓ 스케줄러: 3분마다, batch 30
   ↓ origin_url에서 이미지 다운로드 → S3 업로드
② product_group_images.uploaded_url 채워짐
   ↓
③ image_transform_outboxes 생성 (리사이징 요청)
   ↓ Fileflow API로 변환 요청
④ image_variants 생성 (CDN URL)
```

### 원인 분석
- PROCESSING 2건의 error_message: `"타임아웃으로 인한 복구"` → 외부 URL 접근 실패
- origin_url 도메인: `http://ccapsule1.negagea.kr/` → **이 서버가 Stage에서 접근 불가**하거나 응답이 느림
- THUMBNAIL은 `https://image.mustit.co.kr/` 도메인 → 이것도 업로드 안 됨
- 스케줄러 설정 (`scheduler-stage.yml`): `cron: "0 */3 * * * *"`, `batch-size: 30`, `delay-seconds: 20` → 설정 자체는 정상

### 확인 필요 사항
- [ ] Stage ECS에서 `ccapsule1.negagea.kr`, `image.mustit.co.kr` 외부 접근 가능한지 (Security Group / NAT)
- [ ] Fileflow (S3 업로드 서비스) 연결 상태 확인
- [ ] 스케줄러 ECS Task 로그 확인 (`ImageUploadOutbox-ProcessPending`)

### 영향
- 이미지 업로드 안 됨 → 이미지 변환 안 됨 → CDN URL 없음
- product_groups가 DRAFT에서 ACTIVE로 전환 불가 (이미지 필수)
- 상세설명 퍼블리시 시 이미지 URL 치환 불가

---

## 이슈 2: 상세설명 CDN 퍼블리시 안 됨 (High)

### 증상
- `product_group_descriptions`: 39건 전부 `PUBLISH_READY`, `cdn_path = NULL`
- `description_images`: 0건

### 파이프라인 흐름
```
① 상품 등록 시 description content(HTML) 저장 → publish_status = PUBLISH_READY
   ↓ DescriptionPublishScheduler: 5분마다, batch 30
② HTML 내 이미지 URL을 CDN URL로 치환 → CDN에 HTML 업로드
③ cdn_path 업데이트 → publish_status = PUBLISHED
```

### 원인 분석
- HTML content 내 이미지가 `http://ccapsule1.negagea.kr/` 외부 URL
- 이미지 업로드 파이프라인이 멈춰서 CDN URL이 없음 → 치환 불가
- `description_images` 테이블이 비어있음 → 상세설명 이미지 등록 자체가 안 된 것일 수 있음
- **이슈 1(이미지 업로드)이 선결 조건**

### ✅ 근본 원인 확인 및 코드 수정 완료 (2026-03-01)

**근본 원인**: `ProductGroupDescriptionCommandFactory.create()`에서 HTML content의 `<img>` 태그를 파싱하지 않는 코드 버그. `description_images` 레코드가 아예 생성되지 않았음.

**수정 내용** (4개 파일):
| 파일 | 변경 |
|------|------|
| `domain/.../ProductGroupDescription.java` | `forNew(pgId, content, images, now)` 4-param 오버로드 추가 |
| `application/.../ProductGroupDescriptionCommandFactory.java` | `create()` + `createOrUpdateDescription()` 신규 분기에 `content.extractImageUrls()` → `DescriptionImage.forNew()` 로직 추가 |
| `domain/src/test/.../ProductGroupDescriptionTest.java` | 이미지 포함 `forNew()` 테스트 추가 |
| `application/src/test/.../ProductGroupDescriptionCommandFactoryTest.java` | img 포함/미포함 HTML `create()` 테스트 2건 추가 |

**검증 결과**: domain 테스트 ✅, application Factory 테스트 ✅, Coordinator 테스트 ✅, spotlessCheck ✅

### 남은 작업: 기존 39건 데이터 소급 처리

**방식**: 기존 Update API 재호출 (동일 content로 PUT)
- `computeImageDiff`: 기존 images=0 vs HTML 추출 N개 → 전부 "added"
- `description_images` INSERT + `image_upload_outboxes` INSERT → 기존 파이프라인 자동 동작
- 인증 우회 필요하므로 `@ConditionalOnProperty` 기반 1회성 마이그레이션 컴포넌트 작성 예정

**엔드포인트**: `PUT /api/v1/market/product-groups/{productGroupId}/description`
- Body: `{ "content": "<기존 HTML 그대로>" }`
- UseCase → Factory.createUpdateData() → description.update() → Coordinator.update()

### 확인 필요 사항
- [x] description_images 생성 로직 확인 → **Factory.create()에서 이미지 추출 누락 버그 확인 및 수정 완료**
- [ ] 기존 39건 소급 처리 (Update API 재호출 또는 마이그레이션 컴포넌트)
- [ ] 소급 처리 후 이슈 1(이미지 업로드 파이프라인) 해결 필요 (S3 업로드 선결 조건)
- [ ] DescriptionPublishScheduler 로그 확인

---

## 이슈 3: 상품 인텔리전스 파이프라인 부분 작동 (High)

### 증상
- `intelligence_outboxes`: SENT 3건 + PENDING 74건
  - SENT 3건: product_group_id 149, 150, 151 (profile_id 있음)
  - PENDING 74건: profile_id 전부 **NULL**
- `product_profiles`: 3건 (149, 150, 151) - 전부 `ANALYZING`, `completed_analysis_count = 0/3`

### 파이프라인 흐름
```
① intelligence_outboxes (PENDING)
   ↓ IntelligencePipeline-ProcessPending 스케줄러
   ↓ SQS로 3개 분석 요청 발행 (속성추출/옵션제안/고시제안)
② intelligence_outboxes → SENT, product_profiles 생성 (ANALYZING)
   ↓ SQS Consumer(Worker)가 AI 분석 수행
③ 분석 결과 → product_profiles 업데이트
   ↓ 3개 분석 완료 시
④ Aggregation → product_profiles COMPLETED
```

### ✅ 코드 레벨 분석 완료 (2026-03-01)

**결론: 코드 버그 아님. 인프라/배포 이슈.**

#### 설정 검증 결과 (모두 정상)

| 항목 | 상태 | 근거 |
|------|------|------|
| `intelligence.pipeline.enabled: true` | ✅ | scheduler + worker `application.yml` |
| SQS 큐 URL 5개 | ✅ | `sqs-client-stage.yml` |
| SQS Consumer 5개 enabled | ✅ | `sqs-consumer-stage.yml` |
| Spring AI API 키 | ✅ | SSM Parameter Store 주입 |
| Outbox 생성 로직 | ✅ | `FullProductGroupRegistration/UpdateCoordinator` |
| 스케줄러 cron | ✅ | `scheduler.yml` 기본값 deep merge (process-pending 5분, recover-timeout 10분) |
| 도메인 상태 전이 | ✅ | PENDING→SENT→COMPLETED, retry→PENDING 복귀 |

#### 원인 분석 (상세)

**A. SENT 3건이 COMPLETED로 안 간 이유:**

`IntelligenceRelayProcessor.relay()` 흐름 추적:
```
1. createAndStartAnalyzing() → profileId 획득    ✅ (3건 프로파일 생성됨)
2. markAsSent() + persist() → SENT 커밋          ✅ (SENT로 DB 저장됨)
3. publishToAllAnalyzers() → SQS 3큐 발행        ❌ (실패 추정)
4. complete() + persist() → COMPLETED 커밋        ❌ (미도달)
```

- step 3 실패 시 catch block에서 `recordFailure()` → PENDING 복귀 + persist 해야 함
- **SENT로 남아있음** = catch block의 persist도 실패 (프로세스 비정상 종료 또는 DB 커넥션 끊김)
- `RecoverTimeoutIntelligenceService`가 SENT 좀비를 10분마다 PENDING으로 복구해야 하나, 스케줄러 자체가 작동하지 않으면 복구 불가

**B. PENDING 74건이 안 처리되는 이유:**

- `scheduler-stage.yml`에 `process-pending` 없음 → **scheduler.yml 기본값** deep merge 사용
- 기본값: `cron: "0 */5 * * * *"`, `batch-size: 20`, `delay-seconds: 60` → 설정상 동작해야 함
- **스케줄러가 최초 배치 실행 후 재실행 안 됨** → ECS Task 비정상 종료 또는 SQS 발행 실패로 인한 연쇄 장애

**C. product_profiles completed=0/3:**

- SQS 메시지가 발행되지 않았거나 (A에서 실패)
- Worker ECS Task가 메시지를 consume하지 못함
- AI API 호출 실패 (API 키 미주입 또는 네트워크 차단)

#### 가장 유력한 근본 원인

**SQS 큐 미생성 또는 IAM 권한 부족**
- `sqs-client-stage.yml`에 큐 URL이 설정되어 있어도 실제 AWS에 큐가 없으면 발행 실패
- ECS Task Role에 `sqs:SendMessage` 권한 없으면 발행 실패
- 발행 실패 → 프로세스 불안정 → SENT 고착 + PENDING 미처리

### 확인 필요 사항 (우선순위순)
- [ ] **Scheduler ECS Task 로그** - `Intelligence Outbox Relay 실패` 에러 메시지 확인
- [ ] **SQS 큐 존재 확인** - AWS Console에서 `stage-marketplace-intelligence-*` 5개 큐
- [ ] **ECS Task Role IAM** - `sqs:SendMessage`, `sqs:ReceiveMessage` 권한
- [ ] **Worker ECS Task** - `marketplace-worker` 서비스 Stage 배포 상태
- [ ] **AI API 키** - SSM `/marketplace/stage/OPENAI_API_KEY` 존재 여부
- [ ] ~~`scheduler-stage.yml`에 `process-pending` 설정 추가~~ → 기본값으로 동작하므로 불필요

---

## 이슈 4: product_option_mappings 고아 객체 가능성 (Medium)

### 증상
- `product_option_mappings` 테이블에 `deleted` / `deleted_at` 컬럼 없음
- 도메인 `ProductOptionMapping` 클래스에 `delete()` 메서드 없음 (불변 엔티티)

### 테이블 구조
```sql
product_option_mappings:
  id BIGINT PK
  product_id BIGINT
  seller_option_value_id BIGINT
  -- deleted 없음, deleted_at 없음
```

### 문제 시나리오
```
1. seller_option_values soft delete (deleted=1)
   → product_option_mappings는 삭제된 옵션값을 계속 참조
   → 고아 레코드 발생

2. products soft delete
   → product_option_mappings는 삭제된 상품을 계속 참조

3. 옵션 변경 (기존 옵션 삭제 + 새 옵션 추가)
   → 기존 product_option_mappings 정리 안 됨
   → 하나의 product_id에 여러 매핑 누적 가능
```

### 현재 영향
- 현재 39개 상품은 옵션 변경/삭제가 없어서 문제 없음
- **향후 옵션 수정 기능 사용 시 고아 데이터 누적 우려**

### ✅ 코드 수정 완료 (2026-03-01)

**수정 방식**: Soft Delete 적용 + Product 삭제 시 cascade. SellerOptionValue와 동일한 `DeletionStatus` VO 패턴 사용.

**수정 파일 (10개)**:

| 파일 | 변경 |
|------|------|
| `domain/.../ProductOptionMapping.java` | `DeletionStatus` 필드 + `delete(Instant)` / `isDeleted()` / `deletionStatus()` 추가. `reconstitute()` 시그니처에 `DeletionStatus` 파라미터 추가 |
| `domain/.../Product.java` | `delete()` 메서드에 `optionMappings.forEach(m -> m.delete(now))` cascade 추가 |
| `adapter-out/.../ProductOptionMappingJpaEntity.java` | `deleted` (TINYINT) + `deleted_at` (DATETIME) 컬럼 추가. `create()` 시그니처 변경 |
| `adapter-out/.../ProductJpaEntityMapper.java` | `toMappingEntity` 2개 오버로드에 `deleted`/`deletedAt` 전달. `toMappingDomain`에 `DeletionStatus.reconstitute()` 추가 |
| `adapter-out/.../ProductQueryDslRepository.java` | `findOptionMappingsByProductId`, `findOptionMappingsByProductIds`에 `optionMapping.deleted.isFalse()` 필터 추가 |
| `adapter-out/.../V88__add_soft_delete_to_product_option_mappings.sql` | DDL 마이그레이션 (신규) |
| `application/.../ProductCommandCoordinator.java` | `update()`에서 `diff.removed()` Product들의 soft-deleted 매핑을 `optionMappingCommandManager.persistAll()` 호출 |
| `application/.../BatchChangeProductStatusService.java` | `ProductOptionMappingCommandManager` 주입 추가. DELETED 상태 변경 시 매핑 persist |
| `domain/src/test/.../ProductOptionMappingTest.java` | `reconstitute()` 호출에 `DeletionStatus.active()` 파라미터 추가 |
| `adapter-out/.../ProductJpaEntityFixtures.java` | `create()` 호출에 `false, null` 파라미터 추가 |

**수정된 버그 경로 2개**:
1. `ProductCommandCoordinator.update()` - diff에서 removed Products의 옵션 매핑이 soft delete 후 persist됨
2. `BatchChangeProductStatusService.execute()` - DELETED 상태 변경 시 해당 Products의 옵션 매핑이 cascade로 soft delete 후 persist됨

**검증 결과**: domain 컴파일 ✅, adapter-out 컴파일 ✅, application 컴파일 ✅, ProductOptionMappingTest ✅, ProductJpaEntityMapperTest ✅, ProductCommandCoordinatorTest ✅, spotlessCheck ✅

### 권장 조치
- [x] `product_option_mappings`에 `deleted`, `deleted_at` 컬럼 추가
- [x] Product 삭제 시 관련 매핑을 cascade soft delete하는 로직 추가
- [x] 도메인 `ProductOptionMapping`에 `delete()` 메서드 추가
- [ ] Stage 배포 후 기존 201건 매핑 데이터 정상 동작 확인

---

## 이슈 5: DETAIL 이미지 URL 중복 (Low)

### 증상
```
origin_url                               | 중복 횟수
"http://ccapsule1.negagea.kr/1 front.jpg"  | 39
"http://ccapsule1.negagea.kr/2 front.jpg"  | 39
"http://ccapsule1.negagea.kr/3 back.jpg"   | 39
"http://ccapsule1.negagea.kr/4 back.jpg"   | 39
```

### 원인
- 크롤러가 공통 촬영 앵글 이미지 (front 2장 + back 2장)를 모든 상품에 동일하게 넣음
- 상품별 고유 이미지는 브랜드 이미지 1장 + 상품명 이미지 1장뿐
- 총 7장 중 4장이 모든 상품에서 동일 → **실질적으로 다른 이미지는 3장뿐**

### 영향
- 이미지 업로드 시 동일 URL을 39번 중복 업로드 (낭비)
- 사용자에게 동일한 이미지가 반복 노출

### 권장 조치
- [ ] 크롤러 측에서 상품별 고유 이미지만 전달하도록 수정
- [ ] 또는 image_upload_outboxes에서 동일 origin_url 중복 제거 로직 추가

---

## 이슈 6: notice_entries 전부 기본값 (Info)

### 증상
- `product_notice_entries` 298건 전부 `field_value = "상세설명 참고"`

### 원인
- 크롤러가 고시정보(notice)를 전달하지 않음
- `InboundProductRegistrationResolver.resolveNotice()`: 미제공 필드는 `"상세설명 참고"` 기본값 적용
- **설계대로 정상 동작** (기본값 전략)

### 영향
- 실제 서비스에서 고시정보가 모두 "상세설명 참고"로 노출됨
- 법적 요구사항 충족 여부 확인 필요 (전자상거래법 상품정보 제공 고시)

---

## 우선순위 및 해결 순서

```
[1] 이미지 업로드 파이프라인 (Critical) ← 모든 후속 파이프라인의 선결 조건
    ↓ 해결 시
[2] 상세설명 CDN 퍼블리시 (High) ← 이미지 URL 치환 필요
    ↓ 해결 시
[3] 인텔리전스 파이프라인 (High) ← 독립적이지만 Worker 확인 필요
[4] option_mappings 고아 객체 (Medium) ← 코드 수정 필요
[5] DETAIL 이미지 중복 (Low) ← 크롤러 측 수정
[6] notice 기본값 (Info) ← 비즈니스 결정
```

---

## 참고: Stage 스케줄러 설정 현황

| 스케줄러 | Stage cron | batch | 상태 |
|----------|-----------|-------|------|
| image-upload-outbox (process) | 3분마다 | 30 | 활성, 처리 실패 |
| image-upload-outbox (recover) | 5분마다 | 30 | 활성 |
| image-transform-outbox (process) | 3분마다 | 30 | 활성, 데이터 없음 |
| image-transform-outbox (poll) | 2분마다 | 30 | 활성, 데이터 없음 |
| description-publish | 5분마다 | 30 | 활성, 처리 실패 |
| intelligence (process-pending) | **설정 누락** | - | 기본값(5분) 사용 |
| intelligence (recover-timeout) | **설정 누락** | - | 기본값(10분) 사용 |
| intelligence (recover-stuck) | 5분마다 | 15 | 활성 |
| outbound-sync-outbox | 3분마다 | 50 | 활성 |
| legacy-conversion-seeder | 15분마다 | 50 | 활성 (max 200) |
