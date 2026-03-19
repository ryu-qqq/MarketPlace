# OMS API 호출 흐름 분석

## 공통 패턴: Cancel/Refund/Exchange Command

```
Controller
  ApiMapper.toXxxBatchCommand(request, requestedBy, sellerId)
  XxxBatchService.execute(command)
    ① Validator: 대상 Aggregate 로드 + 권한 검증
    ② [for each item]:
       Factory.createXxx() → Domain Aggregate 생성/상태변경
       OrderItemReadManager.findById() → OrderItem Domain 상태변경
    ③ PersistenceFacade.persistXxx():
       - XxxCommandPort → XxxCommandAdapter → JpaRepository.saveAll()
       - XxxOutboxCommandPort (외부 동기화 필요 시)
       - ClaimHistoryCommandPort → 이력 저장
       - OrderItemCommandPort → OrderItemCommandAdapter
           → OrderItemJpaRepository: delivery_status UPDATE
           → OrderItemHistoryJpaRepository: history INSERT
  ApiMapper.toBatchResultResponse(result)
  → ApiResponse<BatchResultApiResponse>
```

## 주문 목록 조회 흐름

```
OrderQueryController.searchOrders(SearchOrdersApiRequest)
  OrderQueryApiMapper.toSearchParams()
  GetProductOrderListService.execute(params)
    OrderQueryFactory.createCriteria(params)
    OrderReadFacade.getProductOrderListBundle(criteria)
      1. OrderCompositeQueryDslRepository.searchProductOrders()
      2. countProductOrders()
      3. findOrdersByIds(orderIds)
      4. findCancelsByOrderItemIds(orderItemIds)
      5. findClaimsByOrderItemIds(orderItemIds)
    OrderAssembler.toProductOrderPageResult(bundle)
  OrderQueryApiMapper.toPageResponseV4(result)
```

## 아키텍처 특이사항

1. V4 간극: orderId = 내부 orderItemId, null→"", null 금액→0
2. N+1 방지: orderItems 기준 페이징 후 IN 쿼리로 일괄 조회
3. Outbox: Shipment CONFIRM/SHIP, Cancel, Refund 요청/승인에 적용
4. OrderItem 중심: 모든 클레임 처리 시 OrderItem.status 동시 업데이트 + 이력 저장
