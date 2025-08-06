package outpolic.systems.refund.mapper;

import org.apache.ibatis.annotations.Mapper;

import outpolic.systems.refund.dto.RefundDTO;

@Mapper
public interface RefundMapper {

	RefundDTO findSettlementForRefund(String stlmCd);
    int insertRefund(RefundDTO refundDto);
    int updateSettlementStatusToRefund(String stlmCd);
}
