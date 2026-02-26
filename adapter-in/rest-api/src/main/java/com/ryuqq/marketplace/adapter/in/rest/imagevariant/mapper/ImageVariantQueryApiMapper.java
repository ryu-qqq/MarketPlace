package com.ryuqq.marketplace.adapter.in.rest.imagevariant.mapper;

import com.ryuqq.marketplace.adapter.in.rest.imagevariant.dto.response.ImageVariantApiResponse;
import com.ryuqq.marketplace.application.imagevariant.dto.response.ImageVariantResult;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ImageVariant 조회 API 매퍼.
 *
 * <p>Application 레이어의 결과 DTO를 API 응답 DTO로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class ImageVariantQueryApiMapper {

    public List<ImageVariantApiResponse> toApiResponses(List<ImageVariantResult> results) {
        return results.stream().map(ImageVariantApiResponse::from).toList();
    }
}
