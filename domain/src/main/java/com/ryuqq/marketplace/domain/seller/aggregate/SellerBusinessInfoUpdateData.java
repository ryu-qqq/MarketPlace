package com.ryuqq.marketplace.domain.seller.aggregate;

import com.ryuqq.marketplace.domain.common.vo.Address;
import com.ryuqq.marketplace.domain.seller.vo.CompanyName;
import com.ryuqq.marketplace.domain.seller.vo.RegistrationNumber;
import com.ryuqq.marketplace.domain.seller.vo.Representative;
import com.ryuqq.marketplace.domain.seller.vo.SaleReportNumber;

/**
 * 셀러 사업자 정보 수정 데이터 Value Object.
 *
 * <p>Aggregate의 update() 메서드에 전달할 수정 데이터를 묶는 역할을 합니다.
 *
 * <p>VO 객체들은 이미 null 검증이 완료된 객체이므로, UpdateData에서는 추가 검증을 하지 않습니다.
 */
public record SellerBusinessInfoUpdateData(
        RegistrationNumber registrationNumber,
        CompanyName companyName,
        Representative representative,
        SaleReportNumber saleReportNumber,
        Address businessAddress) {

    public static SellerBusinessInfoUpdateData of(
            RegistrationNumber registrationNumber,
            CompanyName companyName,
            Representative representative,
            SaleReportNumber saleReportNumber,
            Address businessAddress) {
        return new SellerBusinessInfoUpdateData(
                registrationNumber, companyName, representative, saleReportNumber, businessAddress);
    }
}
