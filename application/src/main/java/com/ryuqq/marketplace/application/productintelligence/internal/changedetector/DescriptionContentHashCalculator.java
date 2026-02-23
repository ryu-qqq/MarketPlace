package com.ryuqq.marketplace.application.productintelligence.internal.changedetector;

import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * Description Content Hash 계산기.
 *
 * <p>상세설명의 HTML 내용과 이미지 URL 목록을 기반으로 SHA-256 해시를 생성합니다. DescriptionChangeDetectorImpl과
 * DescriptionAnalysisProcessor에서 공통으로 사용합니다.
 */
@Component
public class DescriptionContentHashCalculator {

    public String compute(ProductGroupDescription description) {
        StringBuilder sb = new StringBuilder();

        String html = description.contentValue();
        if (html != null) {
            sb.append(html);
        }

        sb.append("|");

        List<String> imageUrls =
                description.images().stream()
                        .filter(img -> !img.isDeleted())
                        .map(DescriptionImage::originUrlValue)
                        .sorted()
                        .toList();
        sb.append(String.join(",", imageUrls));

        return sha256(sb.toString());
    }

    private String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다", e);
        }
    }
}
