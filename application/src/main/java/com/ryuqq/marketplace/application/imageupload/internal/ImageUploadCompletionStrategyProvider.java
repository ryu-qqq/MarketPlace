package com.ryuqq.marketplace.application.imageupload.internal;

import com.ryuqq.marketplace.domain.imageupload.vo.ImageSourceType;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * ImageSourceType별 전략 객체를 제공하는 Provider.
 *
 * <p>초기화 시 전략 목록을 Map으로 인덱싱하여 O(1) 조회를 지원합니다.
 */
@Component
public class ImageUploadCompletionStrategyProvider {

    private final Map<ImageSourceType, ImageUploadCompletionStrategy> strategyMap;

    public ImageUploadCompletionStrategyProvider(List<ImageUploadCompletionStrategy> strategies) {
        this.strategyMap = new EnumMap<>(ImageSourceType.class);
        for (ImageUploadCompletionStrategy strategy : strategies) {
            for (ImageSourceType sourceType : ImageSourceType.values()) {
                if (strategy.supports(sourceType)) {
                    strategyMap.put(sourceType, strategy);
                }
            }
        }
    }

    /**
     * sourceType에 해당하는 전략 객체를 반환한다.
     *
     * @param sourceType 이미지 소스 타입
     * @return 해당 전략 구현체
     * @throws IllegalArgumentException 지원하지 않는 sourceType인 경우
     */
    public ImageUploadCompletionStrategy getStrategy(ImageSourceType sourceType) {
        ImageUploadCompletionStrategy strategy = strategyMap.get(sourceType);
        if (strategy == null) {
            throw new IllegalArgumentException("지원하지 않는 ImageSourceType: " + sourceType);
        }
        return strategy;
    }
}
