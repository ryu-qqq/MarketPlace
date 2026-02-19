package com.ryuqq.marketplace.adapter.in.rest.legacy.product.controller;

import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.CATEGORY;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.DELETE_GROUPS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.DELIVERY_NOTICE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.DETAIL_DESCRIPTION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.GROUP_DISPLAY_YN;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.GROUP_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.IMAGES;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.NOTICE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.OPTION;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.OUT_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRICE;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRICE_BULK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_DISPLAY_YN;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUP;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUPS;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUP_ID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_GROUP_UUID;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.PRODUCT_STOCK;
import static com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductEndpoints.REFUND_NOTICE;

import com.ryuqq.marketplace.adapter.in.rest.common.dto.ApiResponse;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateDeliveryNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreatePriceRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateRefundNoticeRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyDeleteProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateCategoryRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateDisplayYnRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdatePriceContextRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyUpdateProductStockRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.response.LegacyCreateProductGroupResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/** 세토프 레거시 상품 API 호환 컨트롤러. */
@SuppressWarnings("PMD.TooManyMethods")
@RestController
public class LegacyProductController {

    // ===== 조회 =====

    @GetMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<ApiResponse<Object>> fetchProductGroup(
            @PathVariable long productGroupId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(PRODUCT_GROUP_UUID)
    public ResponseEntity<ApiResponse<Object>> fetchProductGroupByUuid(
            @PathVariable String externalProductUuId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @GetMapping(PRODUCT_GROUPS)
    public ResponseEntity<ApiResponse<Object>> fetchProductGroups() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ===== 등록 =====

    @PostMapping(PRODUCT_GROUP)
    public ResponseEntity<ApiResponse<LegacyCreateProductGroupResponse>> registerProductGroup(
            @RequestBody LegacyCreateProductGroupRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PostMapping(PRODUCT_GROUPS)
    public ResponseEntity<ApiResponse<List<Long>>> registerProductGroups(
            @RequestBody List<LegacyCreateProductGroupRequest> requests) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ===== 수정 =====

    @PutMapping(NOTICE)
    public ResponseEntity<ApiResponse<Long>> updateProductNotice(
            @PathVariable long productGroupId,
            @RequestBody LegacyCreateProductNoticeRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(DELIVERY_NOTICE)
    public ResponseEntity<ApiResponse<Long>> updateDeliveryNotice(
            @PathVariable long productGroupId,
            @RequestBody LegacyCreateDeliveryNoticeRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(REFUND_NOTICE)
    public ResponseEntity<ApiResponse<Long>> updateRefundNotice(
            @PathVariable long productGroupId,
            @RequestBody LegacyCreateRefundNoticeRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(IMAGES)
    public ResponseEntity<ApiResponse<Long>> updateProductImages(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyCreateProductImageRequest> request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(DETAIL_DESCRIPTION)
    public ResponseEntity<ApiResponse<Long>> updateDetailDescription(
            @PathVariable long productGroupId,
            @RequestBody LegacyUpdateProductDescriptionRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(CATEGORY)
    public ResponseEntity<ApiResponse<Long>> updateCategory(
            @PathVariable long productGroupId, @RequestBody LegacyUpdateCategoryRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(PRICE)
    public ResponseEntity<ApiResponse<Long>> updatePrice(
            @PathVariable long productGroupId, @RequestBody LegacyCreatePriceRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(PRICE_BULK)
    public ResponseEntity<ApiResponse<Integer>> updatePriceBulk(
            @RequestBody LegacyUpdatePriceContextRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(GROUP_DISPLAY_YN)
    public ResponseEntity<ApiResponse<Long>> updateGroupDisplayYn(
            @PathVariable long productGroupId, @RequestBody LegacyUpdateDisplayYnRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(PRODUCT_GROUP_ID)
    public ResponseEntity<ApiResponse<Long>> updateProductGroup(
            @PathVariable long productGroupId,
            @RequestBody LegacyUpdateProductGroupRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(PRODUCT_DISPLAY_YN)
    public ResponseEntity<ApiResponse<Long>> updateProductDisplayYn(
            @PathVariable long productId, @RequestBody LegacyUpdateDisplayYnRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PatchMapping(OUT_STOCK)
    public ResponseEntity<ApiResponse<Object>> outOfStock(@PathVariable long productGroupId) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @PutMapping(OPTION)
    public ResponseEntity<ApiResponse<Object>> updateProductOption(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyCreateOptionRequest> request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    // ===== Deprecated =====

    @Deprecated
    @DeleteMapping(DELETE_GROUPS)
    public ResponseEntity<ApiResponse<List<Long>>> deleteProductGroups(
            @RequestBody LegacyDeleteProductGroupRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Deprecated
    @PatchMapping(PRODUCT_STOCK)
    public ResponseEntity<ApiResponse<Object>> updateProductStock(
            @PathVariable long productId, @RequestBody LegacyUpdateProductStockRequest request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Deprecated
    @PatchMapping(GROUP_STOCK)
    public ResponseEntity<ApiResponse<Object>> updateGroupStock(
            @PathVariable long productGroupId,
            @RequestBody List<LegacyUpdateProductStockRequest> request) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
