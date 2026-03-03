package com.ryuqq.marketplace.application.uploadsession.vo;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 허용된 업로드 디렉토리.
 *
 * <p>클라이언트가 요청할 수 있는 디렉토리를 화이트리스트로 관리합니다. S3 key 경로 구성에 사용되므로 임의 값을 허용하지 않습니다.
 */
public enum UploadDirectory {
    PRODUCT_IMAGES("product-images"),
    DESCRIPTION("description"),
    CONTENTS("contents"),
    QNAS("qnas"),
    SELLER_LOGOS("seller-logos"),
    SELLER_DOCUMENTS("seller-documents"),
    ;

    private static final Map<String, UploadDirectory> BY_VALUE =
            Arrays.stream(values())
                    .collect(Collectors.toMap(UploadDirectory::value, Function.identity()));

    private final String value;

    UploadDirectory(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    /**
     * 디렉토리 문자열이 허용된 값인지 검증합니다.
     *
     * @param directory 디렉토리 문자열
     * @return 허용 여부
     */
    public static boolean isAllowed(String directory) {
        return directory != null && BY_VALUE.containsKey(directory);
    }

    /**
     * 디렉토리 문자열을 enum으로 변환합니다.
     *
     * @param directory 디렉토리 문자열
     * @return UploadDirectory
     * @throws IllegalArgumentException 허용되지 않은 디렉토리인 경우
     */
    public static UploadDirectory from(String directory) {
        UploadDirectory result = BY_VALUE.get(directory);
        if (result == null) {
            throw new IllegalArgumentException(
                    "허용되지 않은 업로드 디렉토리입니다: " + directory + ". 허용 목록: " + BY_VALUE.keySet());
        }
        return result;
    }
}
