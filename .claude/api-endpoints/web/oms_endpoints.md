# OMS API Endpoints 분석

## 요약: Query 14개 + Command 22개 = 총 36개

## Query Endpoints (14개)

| # | Method | Path | Controller | UseCase |
|---|--------|------|------------|---------|
| Q01 | GET | /api/v1/market/orders | OrderQueryController | GetProductOrderListUseCase |
| Q02 | GET | /api/v1/market/orders/{orderItemId} | OrderQueryController | GetOrderDetailUseCase |
| Q03 | GET | /api/v1/market/shipments/summary | ShipmentQueryController | GetShipmentSummaryUseCase |
| Q04 | GET | /api/v1/market/shipments | ShipmentQueryController | GetShipmentListUseCase |
| Q05 | GET | /api/v1/market/shipments/{shipmentId} | ShipmentQueryController | GetShipmentDetailUseCase |
| Q06 | GET | /api/v1/market/exchanges/summary | ExchangeQueryController | GetExchangeSummaryUseCase |
| Q07 | GET | /api/v1/market/exchanges | ExchangeQueryController | GetExchangeListUseCase |
| Q08 | GET | /api/v1/market/exchanges/{exchangeClaimId} | ExchangeQueryController | GetExchangeDetailUseCase |
| Q09 | GET | /api/v1/market/refunds/summary | RefundQueryController | GetRefundSummaryUseCase |
| Q10 | GET | /api/v1/market/refunds | RefundQueryController | GetRefundListUseCase |
| Q11 | GET | /api/v1/market/refunds/{refundClaimId} | RefundQueryController | GetRefundDetailUseCase |
| Q12 | GET | /api/v1/market/cancels/summary | CancelQueryController | GetCancelSummaryUseCase |
| Q13 | GET | /api/v1/market/cancels | CancelQueryController | GetCancelListUseCase |
| Q14 | GET | /api/v1/market/cancels/{cancelId} | CancelQueryController | GetCancelDetailUseCase |

## Command Endpoints (22개)

| # | Method | Path | Controller | UseCase |
|---|--------|------|------------|---------|
| C01 | POST | /api/v1/market/shipments/confirm/batch | ShipmentCommandController | ConfirmShipmentBatchUseCase |
| C02 | POST | /api/v1/market/shipments/ship/batch | ShipmentCommandController | ShipBatchUseCase |
| C03 | POST | /api/v1/market/shipments/orders/{orderId}/ship | ShipmentCommandController | ShipSingleUseCase |
| C04 | POST | /api/v1/market/exchanges/request/batch | ExchangeCommandController | RequestExchangeBatchUseCase |
| C05 | POST | /api/v1/market/exchanges/approve/batch | ExchangeCommandController | ApproveExchangeBatchUseCase |
| C06 | POST | /api/v1/market/exchanges/collect/batch | ExchangeCommandController | CollectExchangeBatchUseCase |
| C07 | POST | /api/v1/market/exchanges/prepare/batch | ExchangeCommandController | PrepareExchangeBatchUseCase |
| C08 | POST | /api/v1/market/exchanges/reject/batch | ExchangeCommandController | RejectExchangeBatchUseCase |
| C09 | POST | /api/v1/market/exchanges/ship/batch | ExchangeCommandController | ShipExchangeBatchUseCase |
| C10 | POST | /api/v1/market/exchanges/complete/batch | ExchangeCommandController | CompleteExchangeBatchUseCase |
| C11 | POST | /api/v1/market/exchanges/convert-to-refund/batch | ExchangeCommandController | ConvertToRefundBatchUseCase |
| C12 | PATCH | /api/v1/market/exchanges/hold/batch | ExchangeCommandController | HoldExchangeBatchUseCase |
| C13 | POST | /api/v1/market/exchanges/{exchangeClaimId}/histories | ExchangeCommandController | AddClaimHistoryMemoUseCase |
| C14 | POST | /api/v1/market/refunds/request/batch | RefundCommandController | RequestRefundBatchUseCase |
| C15 | POST | /api/v1/market/refunds/approve/batch | RefundCommandController | ApproveRefundBatchUseCase |
| C16 | POST | /api/v1/market/refunds/reject/batch | RefundCommandController | RejectRefundBatchUseCase |
| C17 | PATCH | /api/v1/market/refunds/hold/batch | RefundCommandController | HoldRefundBatchUseCase |
| C18 | POST | /api/v1/market/refunds/{refundClaimId}/histories | RefundCommandController | AddClaimHistoryMemoUseCase |
| C19 | POST | /api/v1/market/cancels/seller-cancel/batch | CancelCommandController | SellerCancelBatchUseCase |
| C20 | POST | /api/v1/market/cancels/approve/batch | CancelCommandController | ApproveCancelBatchUseCase |
| C21 | POST | /api/v1/market/cancels/reject/batch | CancelCommandController | RejectCancelBatchUseCase |
| C22 | POST | /api/v1/market/cancels/{cancelId}/histories | CancelCommandController | AddClaimHistoryMemoUseCase |
