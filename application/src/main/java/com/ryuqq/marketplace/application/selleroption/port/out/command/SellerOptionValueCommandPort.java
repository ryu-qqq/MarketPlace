package com.ryuqq.marketplace.application.selleroption.port.out.command;

import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import java.util.List;

/** SellerOptionValue Command Port. */
public interface SellerOptionValueCommandPort {

    Long persist(SellerOptionValue value);

    List<Long> persistAll(List<SellerOptionValue> values);

    /**
     * 지정된 sellerOptionGroupId로 옵션 값 목록을 저장합니다.
     *
     * <p>신규 등록 시 OptionGroup persist 후 생성된 ID를 OptionValue에 전달할 때 사용합니다.
     *
     * @param sellerOptionGroupId 부모 옵션 그룹의 생성된 ID
     * @param values 저장할 옵션 값 목록
     * @return 생성된 ID 목록
     */
    List<Long> persistAllForGroup(Long sellerOptionGroupId, List<SellerOptionValue> values);
}
