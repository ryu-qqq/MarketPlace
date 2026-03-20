# APP-PKG-001 전체 리팩토링 계획

> Convention Hub 규칙 APP-PKG-001 기반
> dto/ = UseCase 시그니처에 등장하는 타입만 / internal/ = 나머지 전부

## 완료된 작업

### Exchange 도메인 (이전 세션)
- [x] ExchangeBatchResult: `dto/` → `internal/` 이동 완료
- [x] ExchangePersistenceBundle: `internal/` 신규 생성 완료
- [x] ExchangePersistenceFacade: 단일 `persistAll()` 리팩토링 완료

### Cancel 도메인
- [x] CancelBatchResult: `dto/` → `internal/` 이동 완료

### Refund 도메인
- [x] RefundBatchResult: `dto/` → `internal/` 이동 완료

### Order 도메인
- [x] ProductOrderListBundle: `dto/composite/` → `internal/` 이동 완료
- [x] ProductOrderDetailBundle: `dto/composite/` → `internal/` 이동 완료
- [x] ProductOrderDetailData: Port에서 사용하므로 `dto/composite/` 유지 (정상)

### Seller 도메인
- [x] SellerRegistrationBundle: `dto/bundle/` → `internal/` 이동 완료
- [x] SellerUpdateBundle: `dto/bundle/` → `internal/` 이동 완료
- [x] SellerComposite 4개: Port/UseCase에서 사용하므로 `dto/composite/` 유지 (정상)

### Shipment 도메인
- [x] ConfirmShipmentBundle: `dto/command/` → `internal/` 이동 완료

---

## 보류/불필요 항목

### ProductGroup Bundle/Composite
- ProductGroupDetailBundle: Port(`SalesChannelProductClient`) + adapter-out 클라이언트에서 광범위하게 사용 → UseCase 경계 타입, dto 유지
- ProductGroupListBundle, ProductGroupExcelBundle: ReadFacade/Assembler 내부용이지만 변경 영향범위가 넓음 → 별도 정리 시 처리
- ProductGroupExcelBaseBundle: Port에서 직접 사용 → dto 유지

### Seller Composite → dto/response/ 이동
- 4개 CompositeResult가 `dto/composite/`에 있지만, dto/ 내부이므로 규칙 위반 아님
- `dto/response/`로 이동하면 정리는 되지만 adapter-in/adapter-out 포함 40+곳 import 변경 필요
- 우선순위 낮음, 별도 정리 시 처리

### Legacy Bundle
- LegacyProductGroupDetailBundle: legacy 패키지 내부로 별도 관리

---

## 컴파일 검증
- `./gradlew :application:compileJava :application:compileTestJava` → BUILD SUCCESSFUL ✅
