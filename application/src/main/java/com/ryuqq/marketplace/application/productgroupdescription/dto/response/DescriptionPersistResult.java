package com.ryuqq.marketplace.application.productgroupdescription.dto.response;

import java.util.List;

/** Description + Image 저장 결과. */
public record DescriptionPersistResult(Long descriptionId, List<Long> imageIds) {}
