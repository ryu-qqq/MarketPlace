# APP-PKG-001 + APP-PORT-001 리팩토링 계획

> 전체 완료 (2026-03-20)

## 완료된 작업

### APP-PKG-001: DTO vs Internal 패키지 경계 (BLOCKER)
- [x] Exchange BatchResult → internal/
- [x] Cancel BatchResult → internal/
- [x] Refund BatchResult → internal/
- [x] Order ProductOrderListBundle/DetailBundle → internal/
- [x] Seller RegistrationBundle/UpdateBundle → internal/
- [x] Seller Composite → dto/response/ (composite 패키지 삭제)
- [x] Shipment ConfirmShipmentBundle → internal/

### APP-PORT-001: Port-Out Domain Aggregate 직접 전달 금지 (CRITICAL)
- [x] ProductGroupSyncData 신규 생성 (Application DTO만 포함)
- [x] SellerCsSyncResult 신규 생성
- [x] SalesChannelProductClient Port 시그니처 변경
- [x] Strategy에서 Bundle → SyncData 변환 후 Port 전달
- [x] setof/naver adapter Mapper 전면 DTO 기반 전환
- [x] Convention Hub APP-PORT-001 규칙 등록 + MERGED

### 보류/불필요 항목
- ProductGroupDetailBundle: application 내부에서는 그대로 유지 (ReadFacade/Assembler용)
- ProductGroupListBundle/ExcelBundle: application 내부 전용, Port 미사용
- ProductGroupExcelBaseBundle: Port에서 사용하지만 DTO 필드만 포함 (위반 아님)

## 컴파일 검증
- 모든 단계 `compileJava` + `compileTestJava` → BUILD SUCCESSFUL ✅
