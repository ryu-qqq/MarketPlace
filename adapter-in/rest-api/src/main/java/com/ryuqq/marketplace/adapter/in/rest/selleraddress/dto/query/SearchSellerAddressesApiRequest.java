package com.ryuqq.marketplace.adapter.in.rest.selleraddress.dto.query;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/** 셀러 주소 복합 조회 요청. (슈퍼 관리자: sellerIds 다건, 셀러: 서버에서 sellerId 고정) */
@Schema(description = "셀러 주소 복합 조회 요청")
public record SearchSellerAddressesApiRequest(
        @Schema(description = "셀러 ID 목록 (미입력 시 path sellerId 1건 사용, 슈퍼관리자 복수 가능)")
                List<Long> sellerIds,
        @Schema(description = "주소 유형 필터 (SHIPPING, RETURN) 복수 선택") List<String> addressTypes,
        @Schema(description = "기본 주소 필터") Boolean defaultAddress,
        @Schema(description = "검색 필드 (addressName, address 등)") String searchField,
        @Schema(description = "검색어") String searchWord,
        @Schema(description = "페이지 번호 (0-based)", example = "0") Integer page,
        @Schema(description = "페이지 크기", example = "20") Integer size) {}
